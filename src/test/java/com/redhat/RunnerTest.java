package com.redhat;

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
    public void testScenarios() throws Exception {
        for (int n = 1; n < 5; n++) {
            Log.info("\n Running testScenario " + n + "\n");
            ConfigurationModel model = ConfigurationModel
                    .loadFromFile(new File("src/test/resources/example" + n + ".yaml"));
            runner.setModel(model);
            Future future = runner.run();
            final int i = n;

            synchronized (obj) {
                future.onComplete(h -> {
                    Log.debug("testScenario" + i + " complete.");
                    synchronized (obj) {
                        obj.notify();
                    }
                });
                obj.wait();
            }
            // Thread.sleep(10000);

        }
    }
}
