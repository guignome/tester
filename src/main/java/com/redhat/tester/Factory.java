package com.redhat.tester;

import com.redhat.tester.results.JsonResultCollector;
import com.redhat.tester.results.ResultCollector;
import io.quarkus.logging.Log;
import io.vertx.core.Vertx;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class Factory {
    @Inject
    Vertx vertx;

    @Inject
    TemplateRenderer renderer;

    String format = ResultCollector.FORMAT_JSON;

    private ResultCollector resultCollector;

    public ClientRunner createClientRunner() {
        Log.debug("Creating ClientRunner.");
        ClientRunner client = new ClientRunner(vertx,resultCollector,renderer);
        return client;
    }

    public ServerRunner createServerRunner() {
        Log.debug("Creating ServerRunner.");
        ServerRunner server = new ServerRunner(vertx,renderer);
        return server;
    }

    public ResultCollector getResultCollector() {
        if(resultCollector == null) {
            resultCollector = new JsonResultCollector(renderer, vertx);
        }
        return resultCollector;
    }
}
