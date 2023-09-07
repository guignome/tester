package com.redhat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.redhat.ConfigurationModel.ClientConfiguration;
import com.redhat.ConfigurationModel.ClientConfiguration.Endpoint;
import com.redhat.ConfigurationModel.ClientConfiguration.Scenario;
import com.redhat.ConfigurationModel.ClientConfiguration.Scenario.Step;
import com.redhat.ConfigurationModel.ClientConfiguration.Topology.Local;

import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import io.quarkus.logging.Log;
import io.quarkus.runtime.Quarkus;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;

@CommandLine.Command(name = "tester", mixinStandardHelpOptions = true, description = "Starts the HTTP client.")
class EntryCommand implements Runnable {
    @Parameters(index = "0", description = "URL of the resource", defaultValue = "http://localhost:8080")
    URL url;

    @Option(names = { "-p", "--port" }, description = "The port number")
    int port;

    @Option(names = { "-h", "--host" }, description = "The host name")
    String host;

    @Option(names = { "-f", "--file" }, description = "The file name")
    File file;

    @Option(names = { "-v",
            "--verbose" }, description = "Verbose mode. Helpful for troubleshooting. Multiple -v options increase the verbosity.")
    private boolean[] verbose;

    @Inject
    Factory factory;

    @Override
    public void run() {
        ConfigurationModel model = null;
        try {
            model = createModelFromOptions();
        } catch (Exception e) {
            Log.error("Couldn't initialize model: ", e);
        }

        // Create clients
        List<ClientRunner> clients = new ArrayList<>();
        if (model.client != null) {
            ClientRunner currentClient;
            for (int i = 0; i < model.client.topology.local.parallel; i++) {
                currentClient = factory.createClientRunner();
                currentClient.setModel(model);
                clients.add(currentClient);
            }
        }

        // Create server
        ServerRunner server = null;
        Future<HttpServer> serverFuture = null;
        if (model.server != null) {
            server = factory.createServerRunner();
            server.setModel(model);

            // Run instances.
            serverFuture = server.run();
        }
        // Wait for the server to be started
        List<Future> clientFutures = new ArrayList<>();
        if (serverFuture != null) {
            serverFuture.andThen((e) -> {
                for (ClientRunner client : clients) {
                    clientFutures.add(client.run());
                }
            });
        } else {
            for (ClientRunner client : clients) {
                clientFutures.add(client.run());
            }
        }
        if (clientFutures.size() > 0) {
            CompositeFuture allClientsFuture = CompositeFuture.all(clientFutures);
            allClientsFuture.onSuccess(h -> {
                Log.debug("All clients succeeded, exiting.");
                Quarkus.asyncExit(0);
            }).onFailure(h -> {
                Log.debug("All clients failed, exiting.");
                Quarkus.asyncExit(1);
            });
        }

        Log.debug("Waiting For Exit.");
        Quarkus.waitForExit();

    }

    private ConfigurationModel createModelFromOptions() throws StreamReadException, DatabindException, IOException {
        if (file != null) {
            return ConfigurationModel.loadFromFile(file);
        }
        ConfigurationModel model = new ConfigurationModel();
        model.client = new ClientConfiguration();
        model.client.endpoint = new Endpoint();
        model.client.endpoint.host = url.getHost();
        model.client.endpoint.port = url.getPort() == -1 ? 80 : url.getPort();

        Scenario scenario = new Scenario();
        Step step = new Step();
        step.path = url.getPath();

        model.client.scenarios.add(scenario);
        scenario.steps.add(step);

        return model;
    }
}
