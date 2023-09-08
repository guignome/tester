package com.redhat;

import java.util.ArrayList;
import java.util.List;

import com.redhat.ConfigurationModel.ClientConfiguration.Suite;
import com.redhat.ConfigurationModel.ClientConfiguration.Suite.Step;

import io.quarkus.logging.Log;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;

public class ClientRunner {
    private ConfigurationModel model;
    private Vertx vertx;

    public Vertx getVertx() {
        return vertx;
    }

    public void setVertx(Vertx vertx) {
        this.vertx = vertx;
    }

    public ConfigurationModel getModel() {
        return model;
    }

    public void setModel(ConfigurationModel model) {
        this.model = model;
    }

    public Future<HttpResponse<Buffer>> run() {
        if (model.client == null) {
            return Future.succeededFuture();
        }
        WebClient client = WebClient.create(vertx);
        Future<HttpResponse<Buffer>> future = null;

        Handler<AsyncResult<HttpResponse<Buffer>>> handler = new Handler<AsyncResult<HttpResponse<Buffer>>>(){

            @Override
            public void handle(AsyncResult<HttpResponse<Buffer>> ar) {
                if(ar.result() == null) {
                    Log.debug("Client response received: null");
                } else {
                    Log.debug("Client response received: \n" + renderResponse(ar.result()));
                }
                
            }
        };

        for (int i = 0; i < model.client.topology.local.repeat; i++) {
            for (Suite scenario : model.client.suites) {
                for (Step step : scenario.steps) {
                    HttpRequest<Buffer> request = client.request(
                            HttpMethod.valueOf(step.method),
                            model.client.endpoint.port, model.client.endpoint.host, step.path);
                    if (future == null) {
                        future = request.send().onComplete(handler);
                    } else {
                        future = future.compose(v -> {
                            return request.send().onComplete(handler);
                        });
                    }
                }
            }
        }
        Log.debug("ClientRunner started.");
        return future;
    }

    public String renderResponse(HttpResponse<Buffer> response) {
        StringBuilder sb = new StringBuilder()
          .append("-- Headers --\n");
          response.headers().forEach(
            (k,v)->{sb.append(k + " : " + v + "\n");}
          );
          sb.append("-- Body --  " + "\n")
            .append(response.bodyAsString() + "\n");
            return sb.toString();
    }
}
