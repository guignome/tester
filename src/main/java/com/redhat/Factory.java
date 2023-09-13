package com.redhat;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import io.vertx.core.Vertx;

@Dependent
public class Factory {
    @Inject
    Vertx vertx;

    @Inject
    ResultCollector resultCollector;

    public ClientRunner createClientRunner() {
        ClientRunner client = new ClientRunner();
        client.setVertx(vertx);
        client.setResultCollector(resultCollector);
        return client;
    }
    
    public ServerRunner createServerRunner() {
        ServerRunner server = new ServerRunner();
        server.setVertx(vertx);
        return server;
    }
}
