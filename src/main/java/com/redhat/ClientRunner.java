package com.redhat;

import java.util.ArrayList;
import java.util.List;

import com.redhat.ConfigurationModel.ClientConfiguration.Suite;
import com.redhat.ConfigurationModel.ClientConfiguration.Suite.Step;

import io.quarkus.logging.Log;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;

public class ClientRunner {
    private ConfigurationModel model;
    private Vertx vertx;
    private ResultCollector resultCollector;
    WebClient client;

    public Vertx getVertx() {
        return vertx;
    }

    public void setVertx(Vertx vertx) {
        this.vertx = vertx;
        client = WebClient.create(vertx);
    }

    public ConfigurationModel getModel() {
        return model;
    }

    public void setModel(ConfigurationModel model) {
        this.model = model;
    }

    public void setResultCollector(ResultCollector resultCollector) {
        this.resultCollector = resultCollector;
    }

    public Future run() {

        if (model.client == null) {
            return Future.succeededFuture();
        }
        List<Future> futures = new ArrayList<>();
        Future<HttpResponse<Buffer>> currentFuture = Future.succeededFuture();
        for (int repeat = 0; repeat < model.client.topology.local.repeat; repeat++) {
            for (Suite suite : model.client.suites) {
                for (Step step : suite.steps) {
                    Log.debug("Step: " + step.method + " " + step.path);
                    HttpRequest<Buffer> request = client.request(HttpMethod.valueOf(step.method),
                            model.client.endpoint.port, model.client.endpoint.host, step.path);
                    currentFuture = currentFuture.compose(ar -> {
                        int requestId = resultCollector.onRequestSent(request);
                        return request.send()
                                .onComplete(r -> resultCollector.onResponseReceived(requestId, r.result()));
                    });
                    futures.add(currentFuture);
                }
            }
        }
        Log.debug("ClientRunner started.");
        return CompositeFuture.join(futures);
    }
}
