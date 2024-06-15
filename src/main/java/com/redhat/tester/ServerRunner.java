package com.redhat.tester;

import com.redhat.tester.ConfigurationModel.ServerConfiguration;
import com.redhat.tester.ConfigurationModel.ServerConfiguration.Handler;
import com.redhat.tester.ConfigurationModel.Variable;
import io.quarkus.logging.Log;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

import java.util.List;
import java.util.Map;

public class ServerRunner {

  private final Vertx vertx;
  private TemplateRenderer renderer;
  private Map<String, Object> ctx = new ContextMap();
  private HttpServer httpServer;

  public Vertx getVertx() {
    return vertx;
  }

  public ServerRunner(Vertx vertx, TemplateRenderer renderer) {
    this.vertx = vertx;
    this.renderer = renderer;
    httpServer = vertx.createHttpServer();
  }
  /**
   * Start the server with the given config
   * @param variables
   * @param config
   * @return
   */
  public Future<HttpServer> run(List<Variable> variables, ServerConfiguration config) {

   // Initialize the context
   for (Variable var : variables) {
    ctx.put(var.name, var.value);
  }

    Router router = Router.router(vertx);

    // Mount the handler for all incoming requests at every path and HTTP method
    for (Handler handler : config.handlers) {
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
    Future<HttpServer> future = httpServer
        // Handle every request using the router
        .requestHandler(router)
        // Start listening
        .listen(config.port, config.host);
    Log.debug(String.format("ServerRunner %s started on interface %s and port %s.",
        config.name,
        config.host,
        config.port));
    return future;
  }

  /**
   * Stops the running server
   * @return A future that completes when the stop operation is completed.
   */
  public Future<Void> stop() {
    return httpServer.close();
  }

}
