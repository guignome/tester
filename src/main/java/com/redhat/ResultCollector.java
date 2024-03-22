package com.redhat;

import java.io.IOException;
import java.io.Writer;

import jakarta.enterprise.context.ApplicationScoped;

import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;

public interface ResultCollector {

    public static String FORMAT_CSV = "csv";
    public static String FORMAT_TPS = "tps";

    public String getFormat();

    public int onRequestSent(HttpRequest<?> request);

    public void onResponseReceived(int requestId, HttpResponse<?> response);

    public void onFailureReceived(int requestId, Throwable t);

    public void init();

    public int size();

    void render(Writer w) throws IOException;

    String renderSummary();

    @ApplicationScoped
    public static class ResultCollectorFactory {

        private String format;
        private ResultCollector instance = null;

        public void setFormat(String f) {
            this.format = f;
        }

        public ResultCollector getInstance() {
            if (instance == null || !format.equals(instance.getFormat())) {
                if (FORMAT_CSV.equals(format)) {
                    instance = new CsvResultCollector();
                } else {
                    instance = new TpsResultCollector();
                }
            }
            return instance;
        }
    }
}