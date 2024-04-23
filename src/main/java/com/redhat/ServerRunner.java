package com.redhat;

import java.util.Map;

import com.redhat.ConfigurationModel.ServerConfiguration;
import com.redhat.ConfigurationModel.ServerConfiguration.Handler;
import com.redhat.ConfigurationModel.Variable;

import io.quarkus.logging.Log;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

public class ServerRunner {

  private Vertx vertx;
  private ConfigurationModel model;
  private ServerConfiguration serverConfiguration;
  private TemplateRenderer renderer;
  private Map<String, Object> ctx = new ContextMap();

  public Vertx getVertx() {
    return vertx;
  }

  public ServerRunner(Vertx vertx) {
    this.vertx = vertx;
  }

  public ConfigurationModel getModel() {
    return model;
  }

  public void setServer(ServerConfiguration serverConfiguration) {
    this.serverConfiguration = serverConfiguration;
  }

  public void setRenderer(TemplateRenderer renderer) {
    this.renderer = renderer;
  }

  public Future<HttpServer> run() {
    if (model.servers == null) {
      return Future.succeededFuture();
    }

    // Initialize the context
    for (Variable var : model.variables) {
      ctx.put(var.name, var.value);
    }

    Router router = Router.router(vertx);

    // Mount the handler for all incoming requests at every path and HTTP method
    for (Handler handler : serverConfiguration.handlers) {
      router.route(HttpMethod.valueOf(handler.method), handler.path).handler(context -> {
        if (handler.delay == 0) {
          context.response().setStatusCode(handler.status).end(
              renderer.extrapolate(handler.response, ctx));
        } else {
          context.vertx().setTimer(handler.delay, tid -> context.response().setStatusCode(handler.status).end(
              renderer.extrapolate(handler.response, ctx)));
        }
        System.out.println("Request received. Response sent.");
      });
    }

    // Create the HTTP server
    Future<HttpServer> future = vertx.createHttpServer()
        // Handle every request using the router
        .requestHandler(router)
        // Start listening
        .listen(serverConfiguration.port, serverConfiguration.host);
    Log.debug(String.format("ServerRunner %s started on interface %s and port %s.",
        serverConfiguration.name,
        serverConfiguration.host,
        serverConfiguration.port));
    return future;
  }

  public void setModel(ConfigurationModel model) {
    this.model = model;
  }
}
