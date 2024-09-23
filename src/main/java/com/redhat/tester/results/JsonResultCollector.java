package com.redhat.tester.results;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.redhat.tester.ClientRunner;
import com.redhat.tester.ConfigurationModel;
import com.redhat.tester.ConfigurationModel.ClientConfiguration.Suite;
import com.redhat.tester.ConfigurationModel.ClientConfiguration.Suite.Assertion;
import com.redhat.tester.ConfigurationModel.ClientConfiguration.Suite.Step;
import com.redhat.tester.TemplateRenderer;

import io.quarkus.logging.Log;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.vertx.ext.web.client.HttpResponse;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RegisterForReflection
public class JsonResultCollector implements ResultCollector {
    BufferedWriter w;
    ObjectMapper mapper;
    JsonGenerator jsonGenerator;
    TemplateRenderer renderer;
    ConfigurationModel model;
    String fileName;
    ResultSummary summary;

    Map<String,StepResult> inflight = new HashMap<>();

    public JsonResultCollector(TemplateRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public String getFormat() {
        return FORMAT_JSON;
    }
    @Override
    public String getResultFileName() {
        return fileName;
    }

    @Override
    public void init(ConfigurationModel model) {
        Log.debug("Initializing JSONResultCollector.");
        //initialize the summary
        this.summary = new ResultSummary();
        this.summary.startTime = ZonedDateTime.now();
        this.summary.statusCodesCount = new HashMap<>();

        this.model = model;
        this.fileName = model.results.filename;
        Path result = createResultFile(fileName);

        try {

            w = Files.newBufferedWriter(result);

            // Create and configure an ObjectMapper instance
            mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            //mapper.enable(SerializationFeature.INDENT_OUTPUT);

            jsonGenerator = mapper.getFactory().createGenerator(w);

            // Write the start of the object
            jsonGenerator.writeStartObject();
            jsonGenerator.writeObjectField("name", this.fileName);
            jsonGenerator.writeObjectField("creationTime", ZonedDateTime.now());
            jsonGenerator.writeObjectField("model", model);
            jsonGenerator.writeFieldName("results");
            jsonGenerator.writeStartArray();

        } catch (IOException e) {
            Log.error("Error initializing JSON Factory", e);
        }
    }

    @Override
    public int size() {
        return summary.size;
    }

    @Override
    public void close() {
        Log.debug("Closing JsonResultCollector.");
        summary.endTime = ZonedDateTime.now();
        try {
            jsonGenerator.writeEndArray();
            jsonGenerator.writeObjectField("summary", summary);
            jsonGenerator.writeEndObject();
            jsonGenerator.close();
            w.close();
        } catch (IOException e) {
            Log.error(e);
        }
    }

     // Events

     @Override
     public void beforeStep(Step step, Map<String,Object> ctx){
        StepResult stepResult = new StepResult();
        stepResult.stepName = step.name;
        stepResult.startTime = ZonedDateTime.now();
        stepResult.assertions = new ArrayList<>();
        
        String clientId =(String) ctx.get(ClientRunner.CLIENT_ID_VAR);
        stepResult.clientId = clientId;
        inflight.put(clientId, stepResult);
     }

    @Override
    public void afterStep(Step step, Map<String,Object> ctx){
        String clientId =(String) ctx.get(ClientRunner.CLIENT_ID_VAR);

        StepResult stepResult = inflight.remove(clientId);
        stepResult.endTime = ZonedDateTime.now();

        Object response = ctx.get(ClientRunner.RESULT_VAR);
        if(response instanceof HttpResponse) {
            stepResult.statusCode =((HttpResponse<?>) response).statusCode();
        } else {
            stepResult.statusCode=0;
        }
        
        for(Assertion assertion: step.assertions) {
            AssertionResult assertionResult = new AssertionResult();
            assertionResult.name = assertion.name;
            assertionResult.passed = renderer.evaluateAssertion(assertion, ctx);
            stepResult.assertions.add(assertionResult);
        }
        try {
            mapper.writeValue(jsonGenerator, stepResult);
        } catch (IOException e) {
            Log.error("Couldn't write to json file.", e);
        }
        
        //update the summary
        
        long duration = ChronoUnit.MILLIS.between(stepResult.startTime,stepResult.endTime);
        if(duration>summary.maxDuration) {
            summary.maxDuration = duration;
        }
        if(duration<summary.minDuration) {
            summary.minDuration = duration;
        }
        summary.averageDuration = (summary.size * summary.averageDuration + duration) / (summary.size + 1);
        summary.size++;
        // Increment the count in the map ( 400: i++, )
        summary.statusCodesCount.put(stepResult.statusCode, summary.statusCodesCount.getOrDefault(stepResult.statusCode, 0) + 1);

    }

    @Override
    public void afterSuite(Suite suite, Map<String,Object> ctx){}

    @Override
    public String renderSummary() {
        //Open the Json file, calculate statusCodesCount and duration stats


        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s Requests sent. Duration (ms): min=%d, max=%d, avg=%.3f",
                summary.size,summary.minDuration, summary.maxDuration,
                summary.averageDuration))
                .append("\n")
                .append("HTTP Return Codes: { ");
        for (int code : summary.statusCodesCount.keySet()) {
            sb.append(code).append(":").append(summary.statusCodesCount.get(code))
                    .append(' ');
        }
        sb.append('}');

        return sb.toString();
    }

    //JSON Object

    @RegisterForReflection
    public static class TestExecution {
        public ZonedDateTime creationTime;
        public String name;
        public ConfigurationModel model;
        public List<StepResult> results;
    }

    @RegisterForReflection
    public static class StepResult {
        public ZonedDateTime startTime;
        public ZonedDateTime endTime;
        public String clientId;

        public String stepName;
        public int statusCode;
        public List<AssertionResult> assertions;    
    }
    @RegisterForReflection
    public static class AssertionResult {
        public String name;
        public boolean passed;
    }

    // Result Summary

    

    @Override
    public ResultSummary getCurrentResultSummary() {
        return summary;
    }
}
