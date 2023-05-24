package com.redhat;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import io.vertx.core.Vertx;

@Dependent
public class Factory {
    @Inject
    Vertx vertx;

    public ClientRunner createClientRunner() {
        ClientRunner client = new ClientRunner();
        client.setVertx(vertx);
        return client;
    }
    
    public ServerRunner createServerRunner() {
        ServerRunner server = new ServerRunner();
        server.setVertx(vertx);
        return server;
    }
}
