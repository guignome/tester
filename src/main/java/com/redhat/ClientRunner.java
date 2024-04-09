package com.redhat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.redhat.ConfigurationModel.Header;
import com.redhat.ConfigurationModel.Variable;
import com.redhat.ConfigurationModel.ClientConfiguration.Endpoint;
import com.redhat.ConfigurationModel.ClientConfiguration.Suite;
import com.redhat.ConfigurationModel.ClientConfiguration.Suite.Step;

import io.quarkus.logging.Log;
import io.vertx.core.Future;
import io.vertx.core.Promise;
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
    private TemplateRenderer renderer;
    WebClient client;
    private ContextMap ctx = new ContextMap();
    private StepIterator it;
    public static final String CLIENT_ID_VAR = "clientId";
    public static final String RESULT_VAR = "result";
    private static int idCounter = 0;
    private String id;

    public ClientRunner() {
        id = String.valueOf(idCounter++);
    }

    public String getId() {
        return id;
    }

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

    public void setRenderer(TemplateRenderer renderer) {
        this.renderer = renderer;
    }

    Promise<?> prom;

    // Implementation of run but using an iterator
    public Future<?> run() {
        Log.debug("Client Runner Running.");
        if (model.client == null || model.client.suites.isEmpty()) {
            return Future.succeededFuture();
        }
        // Initialize the context
        Variable clientId = new Variable();
        clientId.name = CLIENT_ID_VAR;
        clientId.value = String.valueOf(id);
        List<Variable> init = new ArrayList<>();
        init.addAll(model.variables);
        init.add(clientId);
        ctx.initializeGlobalVariables(init);

        prom = Promise.promise();

        it = new StepIterator(model.client.suites, model.client.topology.local.repeat, ctx);
        if (it.hasNext()) {
            processStep(it.next());
        } else {
            prom.complete();
        }
        return prom.future();
    }

    private Future<HttpResponse<Buffer>> processStep(Step step) {
        Endpoint targetEndpoint = model.client.getEndpoint(step.endpoint);
        if (targetEndpoint == null) {
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
                renderer.extrapolate(absoluteUri, ctx));
        for (Header header : step.headers) {
            request.putHeader(renderer.extrapolate(header.name, ctx),
                    renderer.extrapolate(header.value, ctx));
        }

        Buffer body = Buffer.buffer(renderer.extrapolate(step.body, ctx));
        resultCollector.beforeStep(step, ctx);
        Log.debugf("  Sending request: %s %s", request.method().toString(), request.uri());
        return request.sendBuffer(body)
                .onSuccess(r -> {
                    ctx.put("result", r);
                    resultCollector.afterStep(step, ctx);
                    System.out.println(renderRequest(request));
                    System.out.println(renderResponse(r));

                    // Process the following step
                    if (it.hasNext()) {
                        processStep(it.next());
                    } else {
                        prom.complete();
                    }
                })
                .onFailure(t -> {
                    ctx.put(RESULT_VAR, t);
                    resultCollector.afterStep(step, ctx);
                    if (it.hasNext()) {
                        processStep(it.next());
                    } else {
                        prom.complete();
                    }
                });
    }

    public static String renderRequest(HttpRequest<Buffer> req) {
        String prefix = req.ssl()?"https://":"http://";
        return new StringBuilder().append(req.method().name())
            .append(' ').append(prefix).append(req.host()).append(':').append(req.port()).append(req.uri()).toString();
    }

    public static String renderResponse(HttpResponse<Buffer> response) {
        StringBuilder sb = new StringBuilder()
                .append("┌───────────────────────────").append(" HTTP ").append(response.statusCode()).append(' ').append("─────────────────────────┐\n");
        response.headers().forEach(
                (k, v) -> {
                    sb.append(String.format("│%-20s│ %-40s│\n", k,v));
                });
        sb.append("├──────────────────────────").append("   Body   ").append("──────────────────────────┤\n")
                .append(response.bodyAsString())
                .append("\n└──────────────────────────────────────────────────────────────┘\n");
        return sb.toString();
    }

    private static class StepIterator implements Iterator<Step> {

        List<Suite> suites;
        int repeat;
        int currentRepeat = 0;
        Iterator<Suite> suiteIterator;
        Suite suite;
        Iterator<Step> stepIterator;
        ContextMap ctx;

        public StepIterator(List<Suite> suites, int repeat, ContextMap ctx) {
            this.suites = suites;
            this.repeat = repeat;
            suiteIterator = suites.iterator();
            suite = suiteIterator.next();
            stepIterator = suite.steps.iterator();
            this.ctx = ctx;
            ctx.initializeLocalVariables(suite.variables);
        }

        @Override
        public boolean hasNext() {
            return stepIterator.hasNext() || suiteIterator.hasNext() || currentRepeat < repeat - 1;
        }

        @Override
        public Step next() {
            if (stepIterator.hasNext()) {
                return stepIterator.next();
            } else if (suiteIterator.hasNext()) {
                suite = suiteIterator.next();
                stepIterator = suite.steps.iterator();
                ctx.initializeLocalVariables(suite.variables);
                return stepIterator.next();
            } else if (currentRepeat < repeat - 1) {
                currentRepeat++;
                suiteIterator = suites.iterator();
                suite = suiteIterator.next();
                stepIterator = suite.steps.iterator();
                return stepIterator.next();
            }
            return null;
        }
    }
}
