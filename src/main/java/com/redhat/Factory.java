package com.redhat;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import io.quarkus.logging.Log;
import io.vertx.core.Vertx;

@Dependent
public class Factory {
    @Inject
    Vertx vertx;

    @Inject
    ResultCollector resultCollector;

    public ClientRunner createClientRunner() {
        Log.debug("Creating ClientRunner.");
        ClientRunner client = new ClientRunner();
        client.setVertx(vertx);
        client.setResultCollector(resultCollector);
        return client;
    }
    
    public ServerRunner createServerRunner() {
        Log.debug("Creating ServerRunner.");
        ServerRunner server = new ServerRunner();
        server.setVertx(vertx);
        return server;
    }
}
