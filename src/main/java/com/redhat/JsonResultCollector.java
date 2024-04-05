package com.redhat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.redhat.ConfigurationModel.Variable;
import com.redhat.ConfigurationModel.ClientConfiguration.Suite;
import com.redhat.ConfigurationModel.ClientConfiguration.Suite.Assertion;
import com.redhat.ConfigurationModel.ClientConfiguration.Suite.Step;

import io.quarkus.logging.Log;

public class JsonResultCollector implements ResultCollector {
    FileOutputStream fos;
    ObjectMapper mapper;
    JsonGenerator jsonGenerator;

    Map<String,StepResult> inflight = new HashMap<>();

    @Override
    public String getFormat() {
        return FORMAT_JSON;
    }

    @Override
    public void init(File result) {
        try {
            if (result == null) {
                result = File.createTempFile("results", ".json");
            } else {
                if (!result.createNewFile()) {
                    Log.warnf("File %s already exists.", result);
                }
            }

            fos = new FileOutputStream(result);

            // Create and configure an ObjectMapper instance
            mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            //mapper.enable(SerializationFeature.INDENT_OUTPUT);

            jsonGenerator = mapper.getFactory().createGenerator(fos);
            // Write the start array token
            jsonGenerator.writeStartArray();

        } catch (IOException e) {
            Log.error("Error initializing JSON Factory", e);
        }
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException("Unimplemented method 'size'");
    }

    @Override
    public String renderSummary() {
        return "See output file.";
    }

    @Override
    public void close() {
        Log.debug("Closing JsonResultCollector.");
        try {
            jsonGenerator.writeEndArray();
            jsonGenerator.close();
            fos.close();
        } catch (IOException e) {
            Log.error(e);
        }
    }

    // @Override
    // public void afterStep(Step step, Map<String,Object> ctx){
    //     try {
    //         jsonGenerator.writeStartObject();
    //         jsonGenerator.writeStringField("stepName", step.name);
    //         jsonGenerator.writeArrayFieldStart("assertions");
    //         for(Assertion assertion: step.assertions) {
    //             jsonGenerator.writeStartObject();
    //             jsonGenerator.writeStringField("name", assertion.name);
    //             jsonGenerator.writeBooleanField("passed", assertion.evaluate(ctx));
    //             jsonGenerator.writeEndObject();
    //         }
    //         jsonGenerator.writeEndArray();
    //         jsonGenerator.writeEndObject();
    //     } catch (IOException e) {
    //         Log.error("Couldn't write to json file.", e);
    //     }
    // }

     // Events

     @Override
     public void beforeStep(Step step, Map<String,Object> ctx){
        StepResult stepResult = new StepResult();
        stepResult.stepName = step.name;
        stepResult.startTime = LocalDateTime.now();
        stepResult.assertions = new ArrayList<>();
        
        String clientId =(String) ctx.get(ClientRunner.CLIENT_ID_VAR);
        stepResult.clientId = clientId;
        inflight.put(clientId, stepResult);
     }

    @Override
    public void afterStep(Step step, Map<String,Object> ctx){
        String clientId =(String) ctx.get(ClientRunner.CLIENT_ID_VAR);

        StepResult stepResult = inflight.remove(clientId);
        stepResult.endTime = LocalDateTime.now();
        
        for(Assertion assertion: step.assertions) {
            AssertionResult assertionResult = new AssertionResult();
            assertionResult.name = assertion.name;
            assertionResult.passed = assertion.evaluate(ctx);
            stepResult.assertions.add(assertionResult);
        }
        try {
            mapper.writeValue(jsonGenerator, stepResult);
        } catch (IOException e) {
            Log.error("Couldn't write to json file.", e);
        }
    }

    @Override
    public void afterSuite(Suite suite, Map<String,Object> ctx){

    }

    //JSON Object
    public static class StepResult {
        public LocalDateTime startTime;
        public LocalDateTime endTime;
        public String clientId;

        public String stepName;
        public List<AssertionResult> assertions;    
    }
    public static class AssertionResult {
        public String name;
        public boolean passed;
    }
}
