package com.redhat.tester.api;

import java.util.ArrayList;
import java.util.List;
import com.redhat.tester.ClientRunner;
import com.redhat.tester.ConfigurationModel;
import com.redhat.tester.Factory;
import com.redhat.tester.RunningBase;
import com.redhat.tester.ServerRunner;
import com.redhat.tester.ConfigurationModel.ClientConfiguration;
import com.redhat.tester.ConfigurationModel.ServerConfiguration;

import io.quarkus.logging.Log;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TesterApiImpl extends RunningBase implements TesterApi {
    

    private ArrayList<ClientRunner> clients = new ArrayList<>();
    private ArrayList<ServerRunner> servers = new ArrayList<>();

    @Inject
    Factory factory;
    private ConfigurationModel commandLineModel;

    public TesterApiImpl() {

    }

    @Override
    public Future<?> executeClientAndServer(ConfigurationModel model) {
        //create resultcollector
        factory.registerResultCollector(model);
        
        setRunning(true);
        clients = new ArrayList<ClientRunner>();
        servers = new ArrayList<ServerRunner>();
        var promise = Promise.promise();
        promise.future().onComplete(
            h->{factory.getResultCollector().close();
                setRunning(false);
            });

        // Create clients
        if (model.client != null) {
            ClientRunner currentClient;
            for (int i = 0; i < model.client.topology.local.parallel; i++) {
                currentClient = factory.createClientRunner();
                currentClient.init(model.variables, model.client);
                clients.add(currentClient);
            }
        }

        // Create server
        List<Future<?>> serverFutures = new ArrayList<>();
        if (model.servers != null) {
            ServerRunner currentServer;
            for (ServerConfiguration serverConfiguration : model.servers) {

                currentServer = factory.createServerRunner();
                servers.add(currentServer);
                // Run instances.
                serverFutures.add(currentServer.run(model.variables, serverConfiguration));
            }
        }
        // Wait for the server to be started
        List<Future<?>> clientFutures = new ArrayList<>();
        if (serverFutures.size() > 0) {
            Future<?> allServersFuture = Future.join(serverFutures);
            allServersFuture.onComplete(h -> {
                Log.debug("Server startup Completed.");
                // start clients.
                clientFutures.addAll(startClients(clients, model.client));
                    Future.join(clientFutures).onComplete(v -> {
                        promise.complete();});
            });
        } else {
            clientFutures.addAll(startClients(clients, model.client));
                Future.join(clientFutures).onComplete(v -> {
                    promise.complete();});
        }
        
        return promise.future();
    }

    @Override
    public Future<?> stop() {
        clients.forEach(c -> c.stop());
        servers.forEach(s -> s.stop());
        setRunning(false);
        return null;
    }

    @Override
    public void registerCommandLineModel(ConfigurationModel model) {
        this.commandLineModel = model;
    }

    @Override
    public ConfigurationModel getCommandLineModel() {
        return commandLineModel;
    }

    private List<Future<?>> startClients(List<ClientRunner> clients, ClientConfiguration config) {
        List<Future<?>> clientFutures = new ArrayList<>();
        for (ClientRunner client : clients) {
            clientFutures.add(client.execute(config.suites, config.topology.local.repeat));
        }

        return clientFutures;
    }

    @Override
    public String getResultFileName() {
        return factory.getResultCollector().getResultFileName();
    }

}
