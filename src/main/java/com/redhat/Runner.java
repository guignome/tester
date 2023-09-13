package com.redhat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.quarkus.logging.Log;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
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
    ServerRunner server = null;

    public void setResultCollector(ResultCollector resultCollector) {
        this.resultCollector = resultCollector;
    }

    public void setCsvFile(File csvFile) {
        this.csvFile = csvFile;
    }

    public void setModel(ConfigurationModel model) {
        this.model = model;
    }
    
    public Future run() {
        resultCollector.init();
        
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
            serverFuture.onComplete(h -> {
                Log.debug("Server startup Completed.");
                clientFutures.addAll(startClients());
            });
        } else {
            clientFutures.addAll(startClients());
        }

        return CompositeFuture.join(clientFutures);
    }

    private List<Future> startClients() {
        List<Future> clientFutures = new ArrayList<>();
        for (ClientRunner client : clients) {
            clientFutures.add(client.run());
        }
        
        return clientFutures;
    }

}
