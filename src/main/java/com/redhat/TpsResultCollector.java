package com.redhat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.redhat.ConfigurationModel.ClientConfiguration.Suite;
import com.redhat.ConfigurationModel.ClientConfiguration.Suite.Step;

import io.quarkus.logging.Log;
import io.vertx.core.Vertx;

public class TpsResultCollector implements ResultCollector{

    AtomicInteger lastTPS =new AtomicInteger(0);
    AtomicInteger currentBucketTPS = new AtomicInteger(0);
    AtomicInteger size =new AtomicInteger(0);

    AtomicInteger requestCounter = new AtomicInteger(0);

    Vertx vertx;
    Writer writer;

    public void setVertx(Vertx v) {
        this.vertx = v;
    }

    @Override
    public void beforeStep(Step step, Map<String,Object> ctx){
        int requestId = requestCounter.getAndIncrement();
        Log.debug("Request " + requestId);
    }

    @Override
    public void afterStep(Step step, Map<String,Object> ctx){
        size.incrementAndGet();
        currentBucketTPS.incrementAndGet();
    }

    @Override
    public void afterSuite(Suite suite, Map<String, Object> ctx) {
        
    }

    @Override
    public void init(File resultFile, ConfigurationModel model) {
        Log.debug("Initializing TpsResultCollector.");
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

        try {
            if (resultFile == null) {
                resultFile = File.createTempFile("results", ".tps");
            } else {
                if (!resultFile.createNewFile()) {
                    Log.warnf("File %s already exists.", resultFile);
                }
            }
        } catch (IOException e) {
            Log.error("Failed to create result File", e);
        }
        //Prepare the result output
        try  {
            this.writer = new FileWriter(resultFile);
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
}
