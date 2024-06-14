package com.redhat.tester.results;

import com.redhat.tester.ConfigurationModel;
import com.redhat.tester.ConfigurationModel.ClientConfiguration.Suite;
import com.redhat.tester.ConfigurationModel.ClientConfiguration.Suite.Step;
import io.quarkus.logging.Log;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

public interface ResultCollector {

    static String FORMAT_CSV = "csv";
    static String FORMAT_TPS = "tps";
    static String FORMAT_JSON = "json";

    String getFormat();

    void beforeStep(Step step, Map<String, Object> ctx);

    void afterStep(Step step, Map<String, Object> ctx);

    void afterSuite(Suite suite, Map<String, Object> ctx);

    void init(String file, ConfigurationModel model);

    void close();

    int size();

    String renderSummary();

    ResultSummary getCurrentResultSummary();

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

    public static class ResultSummary {
        public LocalDateTime startTime;
        public LocalDateTime endTime;
        public int numberOfTestsPassed;
        public int numberOfTestsFailed;
        public int numberOfSteps;
    }
}