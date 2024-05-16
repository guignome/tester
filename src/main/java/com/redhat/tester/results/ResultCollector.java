package com.redhat.tester.results;

import com.redhat.tester.ConfigurationModel;
import com.redhat.tester.ConfigurationModel.ClientConfiguration.Suite;
import com.redhat.tester.ConfigurationModel.ClientConfiguration.Suite.Step;
import io.quarkus.logging.Log;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public interface ResultCollector {

    public static String FORMAT_CSV = "csv";
    public static String FORMAT_TPS = "tps";
    public static String FORMAT_JSON = "json";

    public String getFormat();

    void beforeStep(Step step, Map<String, Object> ctx);

    void afterStep(Step step, Map<String, Object> ctx);

    void afterSuite(Suite suite, Map<String, Object> ctx);

    public void init(String file, ConfigurationModel model);

    public void close();

    public int size();

    String renderSummary();

    default File createResultFile(String fileName) {
        File result = null;
        if (fileName == null || fileName.isEmpty()) {
            try {
                result = File.createTempFile("results", "." + getFormat());
            } catch (IOException e) {
                Log.error("Can't create temp file.", e);
            }
        } else {
            result = new File(fileName);
        }
        return result;
    }
}