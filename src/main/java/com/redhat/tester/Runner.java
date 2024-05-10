package com.redhat.tester;

import com.redhat.tester.ConfigurationModel.ServerConfiguration;
import io.quarkus.logging.Log;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class Runner {
    ConfigurationModel model;

    @Inject
    Factory factory;

    @Inject
    Endpoints endpoints;

    List<ClientRunner> clients = new ArrayList<>();
    List<ServerRunner> servers = new ArrayList<>();

    public void setModel(ConfigurationModel model) {
        this.model = model;
        if(model.client != null) {
            endpoints.init(model.client.endpoints);
        }
    }

    @SuppressWarnings("rawtypes")
    public Future run() {

        clients = new ArrayList<>();
        Promise promise = Promise.promise();

        // Create clients
        if (model.client != null) {
            ClientRunner currentClient;
            for (int i = 0; i < model.client.topology.local.parallel; i++) {
                currentClient = factory.createClientRunner();
                currentClient.init(model.variables);
                clients.add(currentClient);
            }
        }

        // Create server
        List<Future<?>> serverFutures = new ArrayList<>();
        if (model.servers != null) {
            ServerRunner currentServer;
            for (ServerConfiguration serverConfiguration : model.servers) {

                currentServer = factory.createServerRunner();
                currentServer.setModel(model);
                currentServer.setServer(serverConfiguration);
                servers.add(currentServer);
                // Run instances.
                serverFutures.add(currentServer.run());
            }

        }
        // Wait for the server to be started
        List<Future<?>> clientFutures = new ArrayList<>();
        if (serverFutures.size() > 0) {
            Future<?> allServersFuture = Future.join(serverFutures);
            allServersFuture.onComplete(h -> {
                Log.debug("Server startup Completed.");
                clientFutures.addAll(startClients());
                if (clientFutures.size() > 0) {
                    Future.join(clientFutures).onComplete(v -> promise.complete());
                } else {
                    System.out.println("Running in Server mode, Press CTRL-C to stop.");
                }
            });
        } else {
            clientFutures.addAll(startClients());
            if (clientFutures.size() > 0) {
                Future.join(clientFutures).onComplete(v -> promise.complete());
            } else {
                System.out.println("Running in Server mode, Press CTRL-C to stop.");
            }
        }

        return promise.future();
    }

    private List<Future<?>> startClients() {
        List<Future<?>> clientFutures = new ArrayList<>();
        for (ClientRunner client : clients) {
            clientFutures.add(client.execute(model.client.suites, model.client.topology.local.repeat));
        }

        return clientFutures;
    }

}
