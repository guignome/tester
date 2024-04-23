package com.redhat;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import io.quarkus.logging.Log;
import io.vertx.core.Vertx;

@ApplicationScoped
public class Factory {
    @Inject
    Vertx vertx;

    @Inject
    TemplateRenderer renderer;

    @Inject
    Endpoints endpoints;

    String format = ResultCollector.FORMAT_CSV;

    public void setFormat(String format) {
        this.format = format;
    }

    private ResultCollector resultCollector;

    public ClientRunner createClientRunner() {
        Log.debug("Creating ClientRunner.");
        ClientRunner client = new ClientRunner(vertx,endpoints);
        client.setResultCollector(resultCollector);
        client.setRenderer(renderer);
        return client;
    }

    public ServerRunner createServerRunner() {
        Log.debug("Creating ServerRunner.");
        ServerRunner server = new ServerRunner(vertx);
        server.setRenderer(renderer);
        return server;
    }

    public ResultCollector getResultCollector() {
        if (resultCollector == null || !resultCollector.getFormat().equals(format)) {
            if (format == null || ResultCollector.FORMAT_CSV.equals(format)) {
                resultCollector = new CsvResultCollector();
            } else if (ResultCollector.FORMAT_JSON.equals(format)) {
                resultCollector = new JsonResultCollector(renderer);
            } else {
                TpsResultCollector tps = new TpsResultCollector();
                tps.setVertx(vertx);
                resultCollector = tps;
            }
        }
        return resultCollector;
    }
}
