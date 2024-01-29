package com.redhat;

import java.io.IOException;
import java.io.Writer;

import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;

public interface ResultCollector {

    public int onRequestSent(HttpRequest request);

    public void onResponseReceived(int requestId, HttpResponse response);

    public void onFailureReceived(int requestId, Throwable t);

    public double averageDuration();

    public long minDuration();

    public long maxDuration();

    public int size();

    public void init();

    void render(Writer w) throws IOException;

}