package com.redhat.tester.results;

import com.redhat.tester.ConfigurationModel;
import com.redhat.tester.ConfigurationModel.ClientConfiguration.Suite;
import com.redhat.tester.ConfigurationModel.ClientConfiguration.Suite.Step;
import io.quarkus.logging.Log;
import io.vertx.core.Vertx;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class TpsResultCollector implements ResultCollector{

    AtomicInteger lastTPS =new AtomicInteger(0);
    AtomicInteger currentBucketTPS = new AtomicInteger(0);
    AtomicInteger size =new AtomicInteger(0);
    private String fileName;

    AtomicInteger requestCounter = new AtomicInteger(0);

    Vertx vertx;
    Writer writer;

    public TpsResultCollector(Vertx v) {
        this.vertx = v;
    }

    @Override
    public String getResultFileName() {
        return fileName;
    }

    @Override
    public void beforeStep(Step step, Map<String,Object> ctx){
        int requestId = requestCounter.getAndIncrement();
        Log.debug("Request " + requestId);
    }

    @Override
    public void afterStep(Step step, Map<String,Object> ctx){
        Log.debugf("Size: %s",size());
        size.incrementAndGet();
        currentBucketTPS.incrementAndGet();
    }

    @Override
    public void afterSuite(Suite suite, Map<String, Object> ctx) {
        
    }

    @Override
    public void init(ConfigurationModel model) {
        Log.debug("Initializing TpsResultCollector.");
        this.fileName = model.results.filename;
        requestCounter = new AtomicInteger(0);
        lastTPS.set(0);
        currentBucketTPS.set(0);
        size.set(0);
        vertx.setPeriodic(1000,1000,(id)-> {
            Log.debug("Moving to next bucket.");
            System.out.println(renderSummary());
            lastTPS.set(currentBucketTPS.get());
            currentBucketTPS.set(0);
        });

        Path result = createResultFile(fileName);
        //Prepare the result output
        try  {
            this.writer = Files.newBufferedWriter(result);
        } catch (IOException e) {
            Log.error("Not able to create Output result file.", e);
        }
    }

    @Override
    public int size() {
        return size.get();
    }

    private void render(Writer w) throws IOException {
        w.write(renderSummary());
    }

    @Override
    public String renderSummary() {
        return String.format("%s Requests. Last TPS: %s, Current TPS: %s", size,lastTPS,currentBucketTPS);

    }

    @Override
    public String getFormat() {
        return FORMAT_TPS;
    }

    @Override
    public void close() {
        try {
            render(writer);
            writer.close();
        } catch (IOException e) {
            Log.error(e);
        }
    }

    @Override
    public ResultSummary getCurrentResultSummary() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCurrentResultSummary'");
    }
}
