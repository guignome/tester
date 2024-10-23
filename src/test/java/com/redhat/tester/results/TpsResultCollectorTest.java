package com.redhat.tester.results;

import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.Vertx;
import jakarta.inject.Inject;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.redhat.tester.ClientRunner;
import com.redhat.tester.ConfigurationModel;
import com.redhat.tester.ConfigurationModel.ClientConfiguration;
import com.redhat.tester.ConfigurationModel.ClientConfiguration.Suite.Step;
import com.redhat.tester.ConfigurationModel.Results;
import com.redhat.tester.Factory;
import com.redhat.tester.TemplateRenderer;
import com.redhat.tester.results.ResultCollector.ResultSummary;

@QuarkusTest
public class TpsResultCollectorTest {

    @Inject
    Vertx vertx;

    @Inject
    Factory factory;

    @Inject TemplateRenderer renderer;

    @Test
    public void testTpsResultCollector() throws Exception {
        Log.debug("Starting testTPS");
        //factory.registerResultCollector(ResultCollector.FORMAT_TPS, "/tmp/test.tps", null);

        JsonResultCollector tpsResultCollector = new JsonResultCollector(renderer,vertx);
        ConfigurationModel model = new ConfigurationModel();
        model.client = new ClientConfiguration();
        model.results = new Results();
        model.results.filename = "testTps.json";
        tpsResultCollector.init(model);
        Log.debug("TPSResultCollector created. ");
        ResultSummary summary = tpsResultCollector.getCurrentResultSummary();
        printStats(summary);
        assertEquals(0, summary.size);
        assertEquals(0, summary.lastTPS);
        assertEquals(0, summary.currentBucketTPS);
        Step step = new Step();
        Map<String,Object> ctx = new HashMap<>();
        ctx.put(ClientRunner.CLIENT_ID_VAR, "0");

        tpsResultCollector.beforeStep(step, ctx);
        tpsResultCollector.afterStep(step, ctx);
        Log.debug("Called AfterStep on TPSResultcollector");
        printStats(summary);

        assertEquals(1, summary.size);
        assertEquals(0, summary.lastTPS);
        assertEquals(1, summary.currentBucketTPS);

        Thread.sleep(1100);
        Log.debug("TPSResultCollector done sleeping");
        printStats(summary);

        assertEquals(1, summary.size);
        assertEquals(1, summary.lastTPS);
        assertEquals(0, summary.currentBucketTPS);
    }

    private void printStats(ResultSummary summary) {
        Log.debug("TPSTest stats: " + summary.lastTPS + " " + summary.currentBucketTPS + " " + summary.size + " " + summary.requestCounter);
    }
}
