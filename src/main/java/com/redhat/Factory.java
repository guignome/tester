package com.redhat;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import io.quarkus.logging.Log;
import io.vertx.core.Vertx;

@ApplicationScoped
public class Factory {
    @Inject
    Vertx vertx;

    
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

    @Produces
    public ResultCollector getResultCollector() {
        if(resultCollector == null) {
            resultCollector = new CsvResultCollector();
        }
        return resultCollector;
    }
}
