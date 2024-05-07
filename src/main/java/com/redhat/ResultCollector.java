package com.redhat;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import com.redhat.ConfigurationModel.ClientConfiguration.Suite;
import com.redhat.ConfigurationModel.ClientConfiguration.Suite.Step;

import io.quarkus.logging.Log;

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

    public static record BeforeStepPayload(Step step, Map<String, Object> ctx) {
    };

    public static record AfterStepPayload(Step step, Map<String, Object> ctx)  {
    };

    public static record AfterSuitePayload(Suite suite, Map<String, Object> ctx)  {
    };

    public static record InitPayload(String file, ConfigurationModel model) {
    };

    public static final String BEFORE_STEP_ADDRESS = "/bus/results/beforeStep";
    public static final String AFTER_STEP_ADDRESS = "/bus/results/afterStep";
    public static final String AFTER_SUITE_ADDRESS = "/bus/results/afterSuite";
    public static final String INIT_ADDRESS = "/bus/results/init";
    public static final String CLOSE_ADDRESS = "/bus/results/close";
    public static final String SUMMARY_ADDRESS = "/bus/results/summary";

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