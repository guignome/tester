package com.redhat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;

import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.Future;

@QuarkusTest
public class RunnerTest {

    @Inject
    Runner runner;

    @Inject
    ResultCollector resultCollector;

    public final static Object obj = new Object();

    @Test
    public void testLoadYaml() throws StreamReadException, DatabindException, IOException {
        Log.info("Running testLoadYaml test.\n");
        ConfigurationModel model1 = ConfigurationModel.loadFromFile(new File("src/test/resources/example1.yaml"));
        assertNotNull(model1);
        ConfigurationModel model2 = ConfigurationModel.loadFromFile(new File("src/test/resources/example2.yaml"));
        assertNotNull(model2);
        ConfigurationModel model3 = ConfigurationModel.loadFromFile(new File("src/test/resources/example3.yaml"));
        assertNotNull(model3);
    }

    @Test
    public void testCLI() {
        // java -jar target/quarkus-app/quarkus-run.jar -P 2 -R 3 -m GET
        // https://api.publicapis.org/random
    }

    @Test
    public void testScenarios() throws Exception {
        testScenario(1, 12);
        //testScenario(2, 0);
        testScenario(3, 12);
        testScenario(4, 80);
        testScenario(5, 6);
    }

    private void testScenario(final int scenarioNumber, int expectedResultSize) throws Exception {
        Log.info("\n Running testScenario " + scenarioNumber + "\n");
        resultCollector.init();
        ConfigurationModel model = ConfigurationModel
                .loadFromFile(new File("src/test/resources/example" + scenarioNumber + ".yaml"));
        runner.setModel(model);
        Future future = runner.run();

        synchronized (obj) {
            future.onComplete(h -> {
                Log.debug("testScenario" + scenarioNumber + " complete.");
                assertEquals(expectedResultSize, resultCollector.size(), "Wrong size of results.");
                synchronized (obj) {
                    obj.notify();
                }
            });
            obj.wait();
        }
    }
}
