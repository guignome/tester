package com.redhat.tester.results;

import com.redhat.tester.ConfigurationModel;
import com.redhat.tester.ConfigurationModel.ClientConfiguration.Suite;
import com.redhat.tester.ConfigurationModel.ClientConfiguration.Suite.Step;
import io.quarkus.logging.Log;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Map;

public interface ResultCollector {

    static String FORMAT_CSV = "csv";
    static String FORMAT_TPS = "tps";
    static String FORMAT_JSON = "json";

    static File DEFAULT_RESULT_REPOSITORY_FOLDER=new File("results");

    String getFormat();

    void beforeStep(Step step, Map<String, Object> ctx);

    void afterStep(Step step, Map<String, Object> ctx);

    void afterSuite(Suite suite, Map<String, Object> ctx);

    void init(ConfigurationModel model);

    void close();

    int size();

    String renderSummary();

    ResultSummary getCurrentResultSummary();

    default File createResultFile(String fileName) {
        //Create the result folder if it doesn't exist.
        if(!DEFAULT_RESULT_REPOSITORY_FOLDER.exists()) {
            DEFAULT_RESULT_REPOSITORY_FOLDER.mkdirs();
        }
        File result = null;
        if (fileName == null || fileName.isEmpty()) {
            try {
                result = File.createTempFile("results", "." + getFormat(),DEFAULT_RESULT_REPOSITORY_FOLDER);
            } catch (IOException e) {
                Log.error("Can't create temp file.", e);
            }
        } else {
            result = new File(DEFAULT_RESULT_REPOSITORY_FOLDER,fileName);
        }
        return result;
    }
    String getResultFileName();

    @RegisterForReflection
    public static class ResultSummary {
        public Map<Integer, Integer> statusCodesCount;
        public int size = 0;
        public ZonedDateTime startTime;
        public ZonedDateTime endTime;
        long minDuration = Long.MAX_VALUE;
        long maxDuration = 0;
        float averageDuration =0;
    }
}