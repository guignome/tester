package com.redhat;

import java.io.File;
import java.util.Map;

import com.redhat.ConfigurationModel.ClientConfiguration.Suite;
import com.redhat.ConfigurationModel.ClientConfiguration.Suite.Step;

import jakarta.enterprise.context.ApplicationScoped;

public interface ResultCollector {

    public static String FORMAT_CSV = "csv";
    public static String FORMAT_TPS = "tps";
    public static String FORMAT_JSON = "json";


    public String getFormat();

    //public int onRequestSent(HttpRequest<?> request);

    //public void onResponseReceived(int requestId, HttpResponse<?> response);

    //public void onFailureReceived(int requestId, Throwable t);

    void beforeStep(Step step, Map<String,Object> ctx);

    void afterStep(Step step, Map<String,Object> ctx);
    void afterSuite(Suite suite, Map<String,Object> ctx);

    public void init(File f);
    public void close();


    public int size();

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