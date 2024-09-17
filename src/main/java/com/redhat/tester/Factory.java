package com.redhat.tester;

import com.redhat.tester.results.CsvResultCollector;
import com.redhat.tester.results.JsonResultCollector;
import com.redhat.tester.results.ResultCollector;
import com.redhat.tester.results.TpsResultCollector;

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
        return resultCollector;
    }

    public ResultCollector registerResultCollector(ConfigurationModel model) {
        this.format = model.results.format;
        if (resultCollector == null || !resultCollector.getFormat().equals(format)) {
            if (format == null || ResultCollector.FORMAT_JSON.equals(format)) {
                resultCollector = new JsonResultCollector(renderer);
            } else if (ResultCollector.FORMAT_CSV.equals(format)) {
                resultCollector = new CsvResultCollector();
            } else {
                resultCollector = new TpsResultCollector(vertx);
            }
        }
       
        // send the init signal
        resultCollector.init(model);
        
        return resultCollector;
    }
}
