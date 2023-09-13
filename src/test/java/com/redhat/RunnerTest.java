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

    
    @ParameterizedTest
    @ValueSource(ints = {1, 2,3,4,5}) 
    public void testScenarios(int n) throws Exception {
        Log.info("\n Running testScenario " + n + "\n");
        ConfigurationModel model = ConfigurationModel.loadFromFile(new File("src/test/resources/example" + n + ".yaml"));
        runner.setModel(model);
        Future future = runner.run();
        future.onComplete(h -> {
            Log.debug("testScenario" + n + " complete.");
        });
        Thread.sleep(5000);
    }
}
