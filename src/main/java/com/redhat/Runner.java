package com.redhat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.redhat.ConfigurationModel.ServerConfiguration;

import io.quarkus.logging.Log;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;

@ApplicationScoped
public class Runner {
    ConfigurationModel model;
    File csvFile;

    @Inject
    Factory factory;

    @Inject
    ResultCollector resultCollector;

    List<ClientRunner> clients = new ArrayList<>();
    List<ServerRunner> servers = new ArrayList<>();

    public void setResultCollector(ResultCollector resultCollector) {
        this.resultCollector = resultCollector;
    }

    public void setCsvFile(File csvFile) {
        this.csvFile = csvFile;
    }

    public void setModel(ConfigurationModel model) {
        this.model = model;
    }

    @SuppressWarnings("rawtypes")
    public Future run() {
        resultCollector.init();
        clients = new ArrayList<>();
        Promise promise = Promise.promise();

        // Create clients
        if (model.client != null) {
            ClientRunner currentClient;
            for (int i = 0; i < model.client.topology.local.parallel; i++) {
                currentClient = factory.createClientRunner();
                currentClient.setModel(model);
                clients.add(currentClient);
            }
        }

        // Create server
        List<Future> serverFutures = new ArrayList<>();
        if (model.servers != null) {
            ServerRunner currentServer;
            for (ServerConfiguration serverConfiguration:model.servers) {

                currentServer = factory.createServerRunner();
                currentServer.setModel(model);
                currentServer.setServer(serverConfiguration);
                servers.add(currentServer);
                // Run instances.
                serverFutures.add(currentServer.run());
            }

        }
        // Wait for the server to be started
        List<Future> clientFutures = new ArrayList<>();
        if (serverFutures.size() > 0) {
            Future allServersFuture = CompositeFuture.join(serverFutures);
            allServersFuture.onComplete(h -> {
                Log.debug("Server startup Completed.");
                clientFutures.addAll(startClients());
                if (clientFutures.size() > 0) {
                    CompositeFuture.join(clientFutures).onComplete(v -> promise.complete());
                } else {
                    System.out.println("Running in Server mode, Press CTRL-C to stop.");
                }
            });
        } else {
            clientFutures.addAll(startClients());
            if (clientFutures.size() > 0) {
                CompositeFuture.join(clientFutures).onComplete(v -> promise.complete());
            } else {
               System.out.println("Running in Server mode, Press CTRL-C to stop.");
            }
        }

        return promise.future();
    }

    private List<Future> startClients() {
        List<Future> clientFutures = new ArrayList<>();
        for (ClientRunner client : clients) {
            clientFutures.add(client.run());
        }

        return clientFutures;
    }

}
