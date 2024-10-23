package com.redhat.tester;

import com.redhat.tester.ConfigurationModel.Header;
import com.redhat.tester.ConfigurationModel.ServerConfiguration;
import com.redhat.tester.ConfigurationModel.ServerConfiguration.Handler;
import com.redhat.tester.ConfigurationModel.Variable;
import io.quarkus.logging.Log;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.net.KeyCertOptions;
import io.vertx.core.net.PemKeyCertOptions;
import io.vertx.ext.web.Router;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ServerRunner {

  private final Vertx vertx;
  private TemplateRenderer renderer;
  private Map<String, Object> ctx = new ContextMap();
  List<HttpServer> servers = new ArrayList<>();

  public Vertx getVertx() {
    return vertx;
  }

  public ServerRunner(Vertx vertx, TemplateRenderer renderer) {
    this.vertx = vertx;
    this.renderer = renderer;
  }

  /**
   * Start the server with the given config
   * 
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
        Log.infof("Received %s Request: %s %s", context.request().version().name(), context.request().method(),
            context.request().path());
        renderFullRequest(context.request());
        if (handler.delay == 0) {
          handleResponse(context, handler);
        } else {
          context.vertx().setTimer(handler.delay, tid -> handleResponse(context, handler));
        }
        Log.info("Response sent.");
      });
    }

    // Create the HTTP server
    HttpServer httpServer = null;
    if (config.tls != null) {
      // TLS config
      KeyCertOptions opts = new PemKeyCertOptions()
          .setCertPath(config.tls.certPath)
          .setKeyPath(config.tls.keyPath);
      HttpServerOptions options = new HttpServerOptions()
          .setUseAlpn(true)
          .setSsl(true)
          .setKeyCertOptions(opts);
      httpServer = vertx.createHttpServer(options);
    } else {
      // non TLS
      httpServer = vertx.createHttpServer();
    }
    servers.add(httpServer);

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

  private void handleResponse(io.vertx.ext.web.RoutingContext context,Handler handler) {
    context.response().setStatusCode(handler.status);
    //Add headers to response.
    for(Header header: handler.response.headers) {
      context.response().putHeader(header.name, header.value);
    }
    if(handler.response.generatedBodySize != 0) {
      context.response().putHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(handler.response.generatedBodySize));
      for(int i=0;i<handler.response.generatedBodySize;i++) {
        context.response().write("a");
      }
    } else if (handler.response.body != null) {
      context.response().putHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(handler.response.body.length()));
      context.response().write(renderer.extrapolate(handler.response.body, ctx));
    }
    
    context.response().end();
  }

  /**
   * Stops the running server
   * 
   * @return A future that completes when the stop operation is completed.
   */
  public Future<?> stop() {
    List<Future<Void>> futures = new ArrayList<>();
    servers.forEach(s -> {
      futures.add(s.close());
    });
    return Future.all(futures);
  }

  public static void renderFullRequest(HttpServerRequest req) {

    StringBuilder sb = new StringBuilder()
        .append("┌──────────────────────────────────────────────────────────────┐\n");
    req.headers().forEach(
        (k, v) -> {
          sb.append(String.format("│%-20s│ %-40s│\n", k, v));
        });
    sb.append("├──────────────────────────").append("   Body   ").append("──────────────────────────┤\n");
    req.body().onComplete(a -> {
      sb.append(a.result().toString())
          .append("\n└──────────────────────────────────────────────────────────────┘\n");
      Log.debug(sb.toString());
    });
  }
}
