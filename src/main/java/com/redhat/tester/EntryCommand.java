package com.redhat.tester;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.redhat.tester.ConfigurationModel.ClientConfiguration;
import com.redhat.tester.ConfigurationModel.ClientConfiguration.Endpoint;
import com.redhat.tester.ConfigurationModel.ClientConfiguration.Suite;
import com.redhat.tester.ConfigurationModel.ClientConfiguration.Suite.Step;
import com.redhat.tester.api.TesterApi;
import com.redhat.tester.api.UIServer;
import com.redhat.tester.ConfigurationModel.ServerConfiguration;
import com.redhat.tester.results.ResultCollector;
import io.quarkus.logging.Log;
import io.quarkus.runtime.Quarkus;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import jakarta.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParseResult;
import picocli.CommandLine.Spec;

@CommandLine.Command(name = "tester", mixinStandardHelpOptions = true, description = "Starts the HTTP client.")
class EntryCommand implements Runnable {

    @Option(names = "--help", usageHelp = true, description = "display this help and exit")
    boolean help;

    @Option(names = "--endpoint-list", description = "List endpoints and exits.")
    boolean endpointLists;

    @Option(names = OVERRIDE_ENDPOINT_LIST, description = "Override an endpoint with a uri. Example: --override-endpoint database=http://abc:123/test")
    Map<String, URL> overrideEndpoints;
    static final String OVERRIDE_ENDPOINT_LIST = "--override-endpoint";

    // File mode
    @Option(names = { "-f",
            "--file" }, description = "Files to load from. If a directory is specified, all the yaml files in it will be loaded.", defaultValue = "${TESTER_FILE}")
    File[] files;

    @Option(names = { "-t",
            "--result" }, description = "The file name where to save the results in the format specified by -o .")
    String resultFile;

    @Option(names = { 
            "--result-folder" }, description = "The folder where result files are created.",defaultValue = "${TESTER_RESULT_FOLDER:-results}")
    String resultFolder;

    @Option(names = { "-o",
            "--format" }, description = "The format of the result collector. Can be CSV, TPS, or JSON.", defaultValue = ResultCollector.FORMAT_CSV)
    String format = ResultCollector.FORMAT_CSV;

    @Option(names = { "-v",
            "--verbose" }, description = "Verbose mode. Helpful for troubleshooting. Multiple -v options increase the verbosity.")
    private boolean[] verbose;

    // Client mode

    @Parameters(index = "0", description = "URL of the resource", defaultValue = "http://localhost:8080")
    URL url;

    @Option(names = { "-P", "--parallel" }, description = "The number of parallel calls.")
    Optional<Integer> parallel;

    @Option(names = { "-R", "--repeat" }, description = "The number of times to repeat the calls for each thread.")
    Optional<Integer> repeat;

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

    @Option(names = {"--ui"}, description= "Starts the User Interface",defaultValue = "${TESTER_SERVER_UI:-false}")
    Boolean ui = false;

    // Other fields

    @Inject
    Factory factory;

    @Inject
    Vertx vertx;

    @Spec
    CommandSpec spec;

    @Inject
    TesterApi api;

    private ConfigurationModel model = null;
    private static final String SERVER_MODE_MESSAGE="Running in Server mode, Press CTRL-C to stop.";

    public EntryCommand() throws IOException {

    }

    @Override
    public void run() {
        serverMode = serverMode || ui;
        try {
            loadModelFromOptions();
        } catch (Exception e) {
            Log.error("Couldn't initialize model: ", e);
        }
        api.registerCommandLineModel(model);

        factory.registerResultCollector(model);

        // Endpoint list
        if (endpointLists) {
            Log.info("List of Endpoints: \n\n");
            Log.info("┌────────────────────────────────────────────────────────────────────────────────────────────┐");
            Log.infof("│%-15s |%-5s │%-30s │%-6s │%-20s │%-5s │", "Name", "Proto", "Host", "Port", "Prefix",
                    "Default");
            Log.info("├────────────────────────────────────────────────────────────────────────────────────────────┤");

            if (model.client == null || model.client.endpoints == null) {
                Quarkus.asyncExit(0);
                return;
            }
            for (Endpoint ep : model.client.endpoints) {
                Log.infof("│%-15s │%-5s │%-30s │%-6s │%-20s │%-5s │\n", ep.name, ep.protocol, ep.host, ep.port,
                        ep.prefix, ep.isdefault);
            }
            Log.info("└────────────────────────────────────────────────────────────────────────────────────────────┘");
            Log.info("");
            Quarkus.asyncExit(0);
            return;
        }
        //UI
        if(ui) {
            UIServer uiserver = new UIServer(vertx, api);
            uiserver.init();
        }

        Future<?> appFuture = api.executeClientAndServer(model);
        appFuture.onSuccess(h -> {
            Log.debug("All clients succeeded.");
            factory.getResultCollector().close();
            if(!serverMode) {
                Quarkus.asyncExit(0);
            } else {
                Log.info(SERVER_MODE_MESSAGE);
            }
        }).onFailure(h -> {
            Log.debug("All clients failed.");
            factory.getResultCollector().close();
            if(!serverMode) {
                Quarkus.asyncExit(1);
            } else {
                Log.info(SERVER_MODE_MESSAGE);
            }
        });

        Log.debug("Waiting For Exit.");
        Quarkus.waitForExit();
        Log.info(factory.getResultCollector().renderSummary());
        Log.debug("Exiting now.");
    }

    void loadModelFromOptions() throws StreamReadException, DatabindException, IOException {
        ParseResult pr = spec.commandLine().getParseResult();

        if (files != null) {
            // load files first
            this.model = ConfigurationModel.loadFromFile(files);
            // Then override with extra options
            // override url
            if (pr.hasMatchedPositional(0)) {
                // override all the endpoints with the url.
                this.model.client.endpoints = new ArrayList<>();
                this.model.client.endpoints.add(urlToEndpoint(url, null));
            }

            // override Endpoints
            if (pr.hasMatchedOption(OVERRIDE_ENDPOINT_LIST)) {
                for (String overrideEndpointName : overrideEndpoints.keySet()) {
                    // removed endpoints with the same name
                    model.client.endpoints.removeIf(e -> {
                        return e.name.equals(overrideEndpointName);
                    });
                    // add the new endpoint
                    model.client.endpoints
                            .add(urlToEndpoint(overrideEndpoints.get(overrideEndpointName), overrideEndpointName));
                }
            }

            // override repeat and parallel
            if (pr.hasMatchedOption("--repeat")) {
                this.model.client.topology.local.repeat = repeat.get();
            }
            if (pr.hasMatchedOption("--parallel")) {
                this.model.client.topology.local.parallel = parallel.get();
            }

            // override result format and file
            
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

            modelFromOptions.client.endpoints.add(urlToEndpoint(url, null));

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

            modelFromOptions.client.topology.local.parallel = parallel.orElse(1);
            modelFromOptions.client.topology.local.repeat = repeat.orElse(1);
        }

        this.model = modelFromOptions;
    }

    public static final String FROM_URL_ENDPOINT_NAME  = "From url";
    private static Endpoint urlToEndpoint(URL url, String endpointName) {
        Endpoint endpoint = new Endpoint();
        if (endpointName != null) {
            endpoint.name = endpointName;
        } else {
            endpoint.name = FROM_URL_ENDPOINT_NAME;
        }
        endpoint.isdefault = true;
        endpoint.host = url.getHost();
        endpoint.port = url.getPort() == -1 ? url.getDefaultPort() : url.getPort();
        if (url.getProtocol() != null) {
            endpoint.protocol = url.getProtocol();
        }
        return endpoint;
    }

    public ConfigurationModel getModel() {
        return this.model;
    }
}
