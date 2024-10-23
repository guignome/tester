package com.redhat.tester.results;

import com.redhat.tester.ConfigurationModel;
import com.redhat.tester.ConfigurationModel.ClientConfiguration.Suite;
import com.redhat.tester.ConfigurationModel.ClientConfiguration.Suite.Step;
import io.quarkus.logging.Log;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.Map;

public interface ResultCollector {

    static String FORMAT_CSV = "csv";
    static String FORMAT_TPS = "tps";
    static String FORMAT_JSON = "json";

    static Path DEFAULT_RESULT_REPOSITORY_FOLDER=Paths.get("results");

    String getFormat();

    void beforeStep(Step step, Map<String, Object> ctx);

    void afterStep(Step step, Map<String, Object> ctx);

    void afterSuite(Suite suite, Map<String, Object> ctx);

    void init(ConfigurationModel model);

    void close();

    int size();

    String renderSummary();

    ResultSummary getCurrentResultSummary();

    default Path createResultFile(String fileName) {
        //Create the result folder if it doesn't exist.
        if(!Files.exists(DEFAULT_RESULT_REPOSITORY_FOLDER)) {
            try {
                Files.createDirectories(DEFAULT_RESULT_REPOSITORY_FOLDER);
            } catch (IOException e) {
                Log.error("Unable to create Result file " + fileName, e);
            }
        }
        Path result = null;
        if (fileName == null || fileName.isEmpty()) {
            try {
                result = Files.createTempFile(DEFAULT_RESULT_REPOSITORY_FOLDER,"results", "." + getFormat());
            } catch (IOException e) {
                Log.error("Can't create temp file.", e);
            }
        } else {
            result = DEFAULT_RESULT_REPOSITORY_FOLDER.resolve(fileName);
        }
        return result;
    }
    String getResultFileName();

    @RegisterForReflection
    public static class ResultSummary {
        public Map<Integer, Integer> statusCodesCount;
        public int size =0;

        public ZonedDateTime startTime;
        public ZonedDateTime endTime;
        public long minDuration = Long.MAX_VALUE;
        public long maxDuration = 0;
        public float averageDuration =0;

        //For TPS
        public  int lastTPS = 0;
        public int currentBucketTPS = 0;

        public int requestCounter = 0;
    }
}