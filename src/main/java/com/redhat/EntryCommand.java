package com.redhat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.redhat.ConfigurationModel.ClientConfiguration;
import com.redhat.ConfigurationModel.ClientConfiguration.Endpoint;
import com.redhat.ConfigurationModel.ClientConfiguration.Suite;
import com.redhat.ConfigurationModel.ClientConfiguration.Suite.Step;
import com.redhat.ConfigurationModel.ServerConfiguration;

import io.quarkus.logging.Log;
import io.quarkus.runtime.Quarkus;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@CommandLine.Command(name = "tester", mixinStandardHelpOptions = true, description = "Starts the HTTP client.")
class EntryCommand implements Runnable {

    // File mode
    @Option(names = { "-f",
            "--file" }, 
            description = "Files to load from. If a directory is specified, all the yaml files in it will be loaded.",
            defaultValue = "${TESTER_FILE}")
    File[] files;

    @Option(names = { "-c", "--csv" }, description = "The file name where to save the results in csv format.")
    File csvFile;

    @Option(names = { "-o", "--format" }, description = "The format of the result collector. Either csv or tps", 
        defaultValue = ResultCollector.FORMAT_CSV)
    String format= ResultCollector.FORMAT_CSV;

    @Option(names = { "-v",
            "--verbose" }, description = "Verbose mode. Helpful for troubleshooting. Multiple -v options increase the verbosity.")
    private boolean[] verbose;

    // Client mode

    @Parameters(index = "0", description = "URL of the resource", defaultValue = "http://localhost:8080")
    URL url;

    @Option(names = { "-P", "--parallel" }, description = "The number of parallel calls.")
    int parallel = 1;

    @Option(names = { "-R", "--repeat" }, description = "The number of times to repeat the calls for each thread.")
    int repeat = 1;

    @Option(names = { "-m",
            "--method" }, description = "The HTTP Method to use.", defaultValue = "${TESTER_METHOD:-GET}")
    String method;

    @Option(names = { "-H", "--header" })
    Map<String, String> headers;

    // Server mode
    @Option(names = { "-s",
            "--server" }, description = "Run in Server Mode.", defaultValue = "${TESTER_SERVER_MODE:-false}")
    boolean serverMode;

    @Option(names = { "-p", "--port" }, description = "The port number.", defaultValue = "${TESTER_SERVER_PORT:-8080}")
    int port;

    @Option(names = { "-h",
            "--host" }, description = "The interface to listen on", defaultValue = "${TESTER_SERVER_HOST:-localhost}")
    String host;

    @Option(names = { "-d",
            "--delay" }, description = "Delay to respond to requests.", defaultValue = "${TESTER_SERVER_DELAY:-0}")
    int delay;

    @Option(names = { "-r",
            "--response" }, description = "Response body of requests.", defaultValue = "${TESTER_SERVER_RESPONSE:-Hi}")
    String response;

    // Other fields

    @Inject
    Factory factory;

    @Inject
    Vertx vertx;

    @Inject
    Runner runner;

    List<ClientRunner> clients = new ArrayList<>();
    ServerRunner server = null;

    private ConfigurationModel model = null;

    public EntryCommand() throws IOException {
        csvFile = File.createTempFile("results", ".csv");
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void run() {
        factory.setFormat(format);
        try {
            loadModelFromOptions();
        } catch (Exception e) {
            Log.error("Couldn't initialize model: ", e);
        }
        runner.setModel(model);

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
        System.out.println(factory.getResultCollector().renderSummary());
        Log.debug("Exiting now.");
    }

    private void printResultsToFile() {
        try (FileWriter writer = new FileWriter(csvFile)) {
            factory.getResultCollector().render(writer);
            writer.close();
            System.out.printf("Creating result file: %s\n", csvFile.getAbsolutePath());
        } catch (IOException e) {
            Log.error("Not able to create CSV result file.", e);
        }
    }

    void loadModelFromOptions() throws StreamReadException, DatabindException, IOException {
        if (files != null) {
            this.model = ConfigurationModel.loadFromFile(files);
            return;
        }
        ConfigurationModel modelFromOptions = new ConfigurationModel();
        if (serverMode) {
            // server mode
            modelFromOptions.servers.add(new ServerConfiguration());
            modelFromOptions.servers.get(0).port = port;
            modelFromOptions.servers.get(0).host = host;
            ServerConfiguration.Handler handler = new ServerConfiguration.Handler();
            handler.delay = delay;
            handler.response = response;
            handler.method = "GET";
            handler.path = "/*";
            modelFromOptions.servers.get(0).handlers.add(handler);

        } else {
            // client mode
            modelFromOptions.client = new ClientConfiguration();
            Endpoint endpoint = new Endpoint();
            endpoint.host = url.getHost();
            endpoint.port = url.getPort() == -1 ? url.getDefaultPort() : url.getPort();
            if (url.getProtocol() != null) {
                endpoint.protocol = url.getProtocol();
            }

            modelFromOptions.client.endpoints.add(endpoint);

            Suite suite = new Suite();
            Step step = new Step();
            step.path = url.getPath();
            step.method = method;
            // load headers
            if (headers != null) {
                headers.forEach((k, v) -> {
                    step.headers.add(new ConfigurationModel.Header(k, v));
                });
            }

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
