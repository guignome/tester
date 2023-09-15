package com.redhat;

import com.redhat.ConfigurationModel.ServerConfiguration.Handler;

import io.quarkus.logging.Log;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

public class ServerRunner {

  private Vertx vertx;
  private ConfigurationModel model;
  private boolean isReady = false;

  public Vertx getVertx() {
    return vertx;
  }

  public void setVertx(Vertx vertx) {
    this.vertx = vertx;
  }

  public ConfigurationModel getModel() {
    return model;
  }

  public Future<HttpServer> run() {
    if (model.server == null) {
      return Future.succeededFuture();
    }

    Router router = Router.router(vertx);

    // Mount the handler for all incoming requests at every path and HTTP method
    for (Handler handler : model.server.handlers) {
      router.route(HttpMethod.valueOf(handler.method), handler.path).handler(context -> {
        if(handler.delay == 0) {
          context.end(handler.response);
        } else {
          context.vertx().setTimer(handler.delay, tid -> context.end(handler.response));
        }
        System.out.println("Request received. Response sent.");
      });
    }

    // Create the HTTP server
    Future<HttpServer> future = vertx.createHttpServer()
        // Handle every request using the router
        .requestHandler(router)
        // Start listening
        .listen(model.server.endpoint.port);
    Log.debug("ServerRunner started.");
    return future;
  }

  public void setModel(ConfigurationModel model) {
    this.model = model;
  }

  public boolean isReady() {
    return isReady;
  }
}
