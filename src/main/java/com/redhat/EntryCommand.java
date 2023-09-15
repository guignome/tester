package com.redhat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

import javax.inject.Inject;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.redhat.ConfigurationModel.ClientConfiguration;
import com.redhat.ConfigurationModel.ServerConfiguration;
import com.redhat.ConfigurationModel.ClientConfiguration.Endpoint;
import com.redhat.ConfigurationModel.ClientConfiguration.Suite;
import com.redhat.ConfigurationModel.ClientConfiguration.Suite.Step;

import io.quarkus.logging.Log;
import io.quarkus.runtime.Quarkus;
import io.vertx.core.Future;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@CommandLine.Command(name = "tester", mixinStandardHelpOptions = true, description = "Starts the HTTP client.")
class EntryCommand implements Runnable {

    //File mode
    @Option(names = { "-f", "--file" }, description = "The file name.")
    File file;

    @Option(names = { "-c", "--csv" }, description = "The file name where to save the results in csv format.")
    File csvFile;

    @Option(names = { "-v",
            "--verbose" }, description = "Verbose mode. Helpful for troubleshooting. Multiple -v options increase the verbosity.")
    private boolean[] verbose;

    //Client mode

    @Parameters(index = "0", description = "URL of the resource", defaultValue = "http://localhost:8080")
    URL url;

    @Option(names = { "-P", "--parallel" }, description = "The number of parallel calls.")
    int parallel = 1;

    @Option(names = { "-R", "--repeat" }, description = "The number of times to repeat the calls for each thread.")
    int repeat = 1;

    @Option(names = { "-m", "--method" }, description = "The HTTP Method to use.", defaultValue = "${TESTER_METHOD:-GET}")
    String method;

    //Server mode
    @Option(names = { "-s", "--server" }, description = "Run in Server Mode.", defaultValue = "false" )
    boolean serverMode ;

    @Option(names = { "-p", "--port" }, description = "The port number.", defaultValue = "${TESTER_PORT:-8080}")
    int port ;

    @Option(names = { "-d", "--delay" }, description = "Delay to respond to requests.", defaultValue = "${TESTER_DELAY:-0}")
    int delay ;

    @Option(names = { "-r", "--response" }, description = "Response body of requests.", defaultValue = "Hi")
    String response ;

    //Other fields

    @Inject
    Factory factory;

    @Inject
    ResultCollector resultCollector;

    @Inject
    Runner runner;

    List<ClientRunner> clients = new ArrayList<>();
    ServerRunner server = null;

    private ConfigurationModel model = null;

    public EntryCommand() throws IOException{
        csvFile= File.createTempFile("results", ".csv");
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void run() {
        
        try {
            loadModelFromOptions();
        } catch (Exception e) {
            Log.error("Couldn't initialize model: ", e);
        }
        runner.setModel(model);
        runner.setCsvFile(csvFile);

        Future appFuture = runner.run();
        appFuture.onSuccess(h -> {
            Log.debug("All clients succeeded, exiting.");
            printResultsToFile();
            Quarkus.asyncExit(0);
        }).onFailure(h -> {
            Log.debug("All clients failed, exiting.");
            printResultsToFile();
            Quarkus.asyncExit(1);
        });

        Log.debug("Waiting For Exit.");
        Quarkus.waitForExit();
        System.out.println(String.format("%s Requests sent. Duration (ms): min=%d, max=%d, avg=%.3f",
             resultCollector.size(),resultCollector.minDuration(),resultCollector.maxDuration()
             ,resultCollector.averageDuration()));
        Log.debug("Exiting now.");
    }

    private void printResultsToFile() {
        Log.info("Creating result file: " + csvFile.getAbsolutePath());
        try (FileWriter writer = new FileWriter(csvFile)) {
            writer.write(resultCollector.renderCSV());
            writer.close();
        } catch (IOException e) {
            Log.error("Not able to create CSV result file.", e);
        }
    }

    void loadModelFromOptions() throws StreamReadException, DatabindException, IOException {
        if (file != null) {
            this.model = ConfigurationModel.loadFromFile(file);
            return; 
        }
        ConfigurationModel modelFromOptions = new ConfigurationModel();
        if(serverMode) {
            //server mode
            modelFromOptions.server = new ServerConfiguration();
            modelFromOptions.server.endpoint = new ServerConfiguration.Endpoint();
            modelFromOptions.server.endpoint.port = port;
            ServerConfiguration.Handler handler = new ServerConfiguration.Handler();
            handler.delay = delay;
            handler.response = response;
            handler.method = "GET";
            handler.path = "/*";
            modelFromOptions.server.handlers.add(handler);

        } else {
            // client mode
            modelFromOptions.client = new ClientConfiguration();
            modelFromOptions.client.endpoint = new Endpoint();
            modelFromOptions.client.endpoint.host = url.getHost();
            modelFromOptions.client.endpoint.port = url.getPort() == -1 ? 80 : url.getPort();

            Suite suite = new Suite();
            Step step = new Step();
            step.path = url.getPath();
            step.method = method;

            modelFromOptions.client.suites.add(suite);
            suite.steps.add(step);

            modelFromOptions.client.topology.local.parallel = parallel;
            modelFromOptions.client.topology.local.repeat = repeat;
        }

        this.model = modelFromOptions;
    }

    public ConfigurationModel getModel() {
        return this.model;
    }
}
