package com.redhat;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.context.ManagedExecutor;

import io.quarkus.logging.Log;
import io.vertx.core.Vertx;

@ApplicationScoped
public class Factory {
    @Inject
    Vertx vertx;

    @Inject
    TemplateRenderer renderer;

    @Inject
    ManagedExecutor executor;

    String format = ResultCollector.FORMAT_CSV;
    
    public void setFormat(String format) {
        this.format = format;
    }


    private ResultCollector resultCollector;

    public ClientRunner createClientRunner() {
        Log.debug("Creating ClientRunner.");
        ClientRunner client = new ClientRunner();
        client.setVertx(vertx);
        client.setResultCollector(resultCollector);
        client.setRenderer(renderer);
        return client;
    }

    public ServerRunner createServerRunner() {
        Log.debug("Creating ServerRunner.");
        ServerRunner server = new ServerRunner();
        server.setVertx(vertx);
        server.setRenderer(renderer);
        return server;
    }


    public ResultCollector getResultCollector() {
        if (resultCollector == null || !resultCollector.getFormat().equals(format)) {
            if (format == null || ResultCollector.FORMAT_CSV.equals(format)) {
                resultCollector = new CsvResultCollector();
            } else {
                TpsResultCollector tps = new TpsResultCollector();
                tps.setVertx(vertx);
                resultCollector = tps;
            }
        }
        return resultCollector;
    }
}
