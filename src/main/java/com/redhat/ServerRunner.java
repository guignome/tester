package com.redhat;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.redhat.ConfigurationModel.ServerConfiguration.Handlers;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

@Dependent
public class ServerRunner {

  @Inject
  Vertx vertx;

  ConfigurationModel model;

  public void run() {
    if (model.server == null) {
      return;
    }

    Router router = Router.router(vertx);

    // Mount the handler for all incoming requests at every path and HTTP method
    for (Handlers handler : model.server.handlers) {
      router.route(HttpMethod.valueOf(handler.method), handler.path).handler(context -> {
        // Get the address of the request

        // Write a json response
        context.end(handler.response);
      });
    }

    // Create the HTTP server
    vertx.createHttpServer()
        // Handle every request using the router
        .requestHandler(router)
        // Start listening
        .listen(model.server.endpoint.port)
        // Print the port
        .onSuccess(server -> System.out.println(
            "HTTP server started on port " + server.actualPort()));
  }

  public void setModel(ConfigurationModel model) {
    this.model = model;
  }
}
