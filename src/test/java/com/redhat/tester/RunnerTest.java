package com.redhat.tester;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.redhat.tester.results.ResultCollector;

import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.Future;
import jakarta.inject.Inject;
import java.io.File;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

@QuarkusTest
public class RunnerTest {

    @Inject
    Runner runner;

    @Inject
    Factory factory;

  

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
    public void testCLIclient() throws Exception {
        // java -jar target/quarkus-app/quarkus-run.jar -P 2 -R 3 -m GET
        // https://api.publicapis.org/random
        EntryCommand app = new EntryCommand();
        new CommandLine(app).parseArgs("-P", "2", "-R","3", "-m", "GET","https://api.publicapis.org/random");
        app.loadModelFromOptions();
        assertEquals(2, app.getModel().client.topology.local.parallel);
        assertEquals(3, app.getModel().client.topology.local.repeat);
        assertEquals("GET", app.getModel().client.suites.get(0).steps.get(0).method);
        
        String endpointName = app.getModel().client.suites.get(0).steps.get(0).endpoint;
        assertEquals("api.publicapis.org", app.getModel().client.getEndpoint(endpointName).host);
        assertEquals("https", app.getModel().client.getEndpoint(endpointName).protocol);
        assertEquals(443,app.getModel().client.getEndpoint(endpointName).port);
    }

    @Test
    public void testCLIserver() throws Exception {
        // java -jar target/quarkus-app/quarkus-run.jar -s
        EntryCommand app = new EntryCommand();
        new CommandLine(app).parseArgs("-s");
        app.loadModelFromOptions();
        assertNotNull(app.getModel());
        //assertEquals(2, app.getModel().client.endpoint.);


    }

    @Test
    public void testScenarios() throws Exception {
        testScenario(1, 18);
        //testScenario(2, 0);
        testScenario(3, 12);
        testScenario(4, 80);
        testScenario(5, 6);
        testScenario(6,6);
        testScenario(7, 2);
    }

    private void testScenario(final int scenarioNumber, int expectedResultSize) throws Exception {
        Log.info("\n Running testScenario " + scenarioNumber + "\n");
        ConfigurationModel model = ConfigurationModel
                .loadFromFile(new File("src/test/resources/example" + scenarioNumber + ".yaml"));
        runner.setModel(model);
        factory.registerResultCollector(model);
        final ResultCollector resultCollector = factory.getResultCollector();

        Future<?> future = runner.run();

        synchronized (obj) {
            future.onComplete(h -> {
                Log.debug("testScenario" + scenarioNumber + " complete.");
                resultCollector.close();
                assertEquals(expectedResultSize, resultCollector.size(), "Wrong size of results for scenario " + scenarioNumber + ".");
                synchronized (obj) {
                    obj.notify();
                }
            });
            obj.wait();
        }
    }

    @Test
    public void testOverride() throws Exception {
        EntryCommand app = new EntryCommand();
        new CommandLine(app).parseArgs("-f", "src/test/resources/example1.yaml");
        app.loadModelFromOptions();
        assertEquals(2, app.getModel().client.topology.local.parallel);
        assertEquals(3, app.getModel().client.topology.local.repeat);
        assertEquals(2, app.getModel().client.endpoints.size());

        EntryCommand app2 = new EntryCommand();
        new CommandLine(app2).parseArgs("-f", "src/test/resources/example1.yaml", "--repeat","5","--parallel","7",
                                        "--override-endpoint","database=http://abc:123/test",
                                        "--override-endpoint","db2=http://abc:123/test2",
                                        "--override-endpoint","db3=http://abc:123/test3");
        app2.loadModelFromOptions();
        assertEquals(7, app2.getModel().client.topology.local.parallel);
        assertEquals(5, app2.getModel().client.topology.local.repeat);
        assertEquals(4, app2.getModel().client.endpoints.size());

    }
}
