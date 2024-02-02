package com.redhat;

import java.util.ArrayList;
import java.util.List;

import com.redhat.ConfigurationModel.Header;
import com.redhat.ConfigurationModel.ClientConfiguration.Endpoint;
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

    public Future<?> run() {
        Log.debug("Client Runner Running.");
        if (model.client == null) {
            return Future.succeededFuture();
        }
        List<Future> futures = new ArrayList<>();
        Future<HttpResponse<Buffer>> currentFuture = Future.succeededFuture();
        for (int repeat = 0; repeat < model.client.topology.local.repeat; repeat++) {
            Log.debugf("Repeat %s",repeat);
            for (Suite suite : model.client.suites) {
                Log.debugf("Suite %s", suite.name);
                for (Step step : suite.steps) {
                    Log.debugf("Repeat: %s, Suite: %s, Step: %s ",repeat,suite.name, step.method + " " + step.path);
                    Endpoint targetEndpoint = model.client.getEndpoint(step.endpoint);
                    if(targetEndpoint == null) {
                        Log.error("Refering to non-existent endpoint: " + step.endpoint);
                    }
                    String absoluteUri = new StringBuilder()
                       .append(targetEndpoint.protocol)
                       .append("://")
                       .append(targetEndpoint.host)
                       .append(":")
                       .append(targetEndpoint.port)
                       .append(targetEndpoint.prefix)
                       .append(step.path)
                       .toString();
                    HttpRequest<Buffer> request = client.requestAbs(HttpMethod.valueOf(step.method),
                            absoluteUri);
                            for(Header header: step.headers) {
                                request.putHeader(header.name, header.value);
                            }
                    currentFuture = currentFuture.compose(ar -> {
                        int requestId = resultCollector.onRequestSent(request);
                        Buffer body = Buffer.buffer(step.body); 
                        return request.sendBuffer(body)
                                .onSuccess(r -> resultCollector.onResponseReceived(requestId, r))
                                .onFailure(t-> resultCollector.onFailureReceived(requestId, t));
                    });
                    futures.add(currentFuture);
                }
            }
        }
        Log.debug("ClientRunner started.");
        return CompositeFuture.join(futures);
    }
}
