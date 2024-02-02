package com.redhat;

import java.io.IOException;
import java.io.Writer;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

import io.quarkus.logging.Log;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;

public class TpsResultCollector implements ResultCollector{

    AtomicInteger lastTPS =new AtomicInteger(0);
    AtomicInteger currentBucketTPS = new AtomicInteger(0);
    AtomicInteger size =new AtomicInteger(0);

    AtomicInteger requestCounter = new AtomicInteger(0);

    Vertx vertx;

    public void setVertx(Vertx v) {
        this.vertx = v;
    }


    @Override
    public int onRequestSent(HttpRequest request) {
        int requestId = requestCounter.getAndIncrement();
        Log.debug("Request " + requestId);
        return requestId;
    }

    @Override
    public void onResponseReceived(int requestId, HttpResponse response) {
        Log.debug("Response " + requestId);
        size.incrementAndGet();
        currentBucketTPS.incrementAndGet();
    }

    @Override
    public void onFailureReceived(int requestId, Throwable t) {
        size.incrementAndGet();
        currentBucketTPS.incrementAndGet();
    }

    @Override
    public void init() {
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
    }

    @Override
    public int size() {
        return size.get();
    }

    @Override
    public void render(Writer w) throws IOException {
        w.write(renderSummary());
    }

    public String renderSummary() {
        return String.format("%s Requests. Last TPS: %s, Current TPS: %s", size,lastTPS,currentBucketTPS);

    }

    @Override
    public String getFormat() {
        return FORMAT_TPS;
    }
    
}
