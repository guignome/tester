package com.redhat.tester.api;

import java.util.ArrayList;
import java.util.List;
import com.redhat.tester.ClientRunner;
import com.redhat.tester.ConfigurationModel;
import com.redhat.tester.Factory;
import com.redhat.tester.ServerRunner;
import com.redhat.tester.ConfigurationModel.ClientConfiguration;
import com.redhat.tester.ConfigurationModel.ServerConfiguration;
import com.redhat.tester.ConfigurationModel.Variable;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import io.quarkus.logging.Log;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TesterApiImpl implements TesterApi {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    public static final String STATUS_PROP = "status";
    public static final String STATUS_RUNNING = "running";
    public static final String STATUS_STOPPED = "stopped";
    private String status = STATUS_STOPPED;

    @Inject
    Factory factory;

    public TesterApiImpl() {

    }

    @Override
    public Future<?> executeClient(ClientConfiguration config, List<Variable> variables) {
        setStatus(STATUS_RUNNING);

        List<ClientRunner> clients = new ArrayList<>();
        List<Future<?>> clientFutures = new ArrayList<>();
        Promise<?> promise = Promise.promise();
        ClientRunner currentClient;
        for (int i = 0; i < config.topology.local.parallel; i++) {
            currentClient = factory.createClientRunner();
            currentClient.init(variables,config);
            clients.add(currentClient);
        }

        clientFutures.addAll(startClients(clients, config));
        Future.join(clientFutures).onComplete(v -> promise.complete());
        promise.future().onComplete(h-> {setStatus(STATUS_STOPPED);});
        return promise.future();
    }

    @Override
    public Future<?> executeServer(ServerConfiguration config, List<Variable> variables) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'executeServer'");
    }

    @Override
    public Future<?> executeClientAndServer(ConfigurationModel model) {
        setStatus(STATUS_RUNNING);
        var clients = new ArrayList<ClientRunner>();
        var servers = new ArrayList<ServerRunner>();
        var promise = Promise.promise();

        // Create clients
        if (model.client != null) {
            ClientRunner currentClient;
            for (int i = 0; i < model.client.topology.local.parallel; i++) {
                currentClient = factory.createClientRunner();
                currentClient.init(model.variables,model.client);
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
                serverFutures.add(currentServer.run(model.variables,serverConfiguration));
            }
        }
        // Wait for the server to be started
        List<Future<?>> clientFutures = new ArrayList<>();
        if (serverFutures.size() > 0) {
            Future<?> allServersFuture = Future.join(serverFutures);
            allServersFuture.onComplete(h -> {
                Log.debug("Server startup Completed.");
                //start clients.
                clientFutures.addAll(startClients(clients,model.client));
                if (clientFutures.size() > 0) {
                    Future.join(clientFutures).onComplete(v -> promise.complete());
                } else {
                    System.out.println("Running in Server mode, Press CTRL-C to stop.");
                }
            });
        } else {
            clientFutures.addAll(startClients(clients,model.client));
            if (clientFutures.size() > 0) {
                Future.join(clientFutures).onComplete(v -> promise.complete());
            } else {
                System.out.println("Running in Server mode, Press CTRL-C to stop.");
            }
        }
        return promise.future();
    }


    private List<Future<?>> startClients(List<ClientRunner> clients, ClientConfiguration config) {
        List<Future<?>> clientFutures = new ArrayList<>();
        for (ClientRunner client : clients) {
            clientFutures.add(client.execute(config.suites, config.topology.local.repeat));
        }

        return clientFutures;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    @Override
    public String getStatus() {
        return status;

    }

    private void setStatus(String status) {
        String oldValue = this.status;
        this.status = status;
        this.pcs.firePropertyChange("value", oldValue, status);
    }


}
