package com.redhat.tester.api;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.tester.ConfigurationModel;
import com.redhat.tester.api.views.JSonResultView;
import com.redhat.tester.api.views.ResultsView;
import com.redhat.tester.api.views.RuntimeView;
import com.redhat.tester.api.views.Stoppable;

import io.quarkus.logging.Log;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

public class UIServer {
    Vertx vertx;
    TesterApi api;
    Map<String, Stoppable> views = new HashMap<>();

    public UIServer(Vertx vertx, TesterApi api) {
        this.vertx = vertx;
        this.api = api;
    }

    public void init() {
        // Start the static content router:
        Router router = Router.router(vertx);
        router.route("/*").handler(StaticHandler.create());

        vertx.createHttpServer().webSocketHandler(ws -> {
            System.out.println("Web Socket Connected.");

            ws.textMessageHandler(text -> {
                System.out.println("Received: " + text);
                // Create JSON object
                ObjectMapper mapper = new ObjectMapper();

                try {
                    // msg = mapper.readValue(text, ServerMessage.class);
                    JsonNode jsonMsg = mapper.readTree(text);
                    String kind = jsonMsg.get("kind").asText();
                    JsonNode data = jsonMsg.get("data");

                    // switch(msg.kind) {
                    switch (kind) {
                        case "init":
                            System.out.println("Received init message.");
                            JsonObject json = new JsonObject()
                                    .put("kind", "init")
                                    .put("data", "Initialized!");
                            ws.writeTextMessage(json.encode());
                            break;
                        case "startModel":
                            ConfigurationModel model = mapper.readValue(data.get("model").toString(),
                                    ConfigurationModel.class);
                            var future = api.executeClientAndServer(model);
                            break;
                        case "stopModel":
                            api.stop();
                            break;
                        case "watch":
                            String resourceType = jsonMsg.get("resourceType").asText();
                            String instance = jsonMsg.get("resourceInstance").asText();
                            switch (resourceType) {
                                case "runtime":
                                    var rtView = new RuntimeView(vertx, ws, api);
                                    break;
                                case "results":
                                    var resultsView = new ResultsView(ws);
                                    break;
                                case "jsonResult":
                                    var jsonResultView = new JSonResultView(vertx, ws, instance);
                                    views.put(resourceType + "/" + instance, jsonResultView);
                                    break;
                                default:
                                    Log.errorf("Unknown message resource type: %s", resourceType);
                            }
                            break;
                        case "stopWatch":
                            String resourceType2 = jsonMsg.get("resourceType").asText();
                            String instance2 = jsonMsg.get("resourceInstance").asText();
                            String viewKey = resourceType2 + "/" + instance2;
                            var stoppedView = views.get(viewKey);
                            if (stoppedView == null) {
                                Log.warnf("Received stopWatch message for view that was not registered: %s", viewKey);
                            } else {
                                stoppedView.stop();
                                views.remove(viewKey);
                            }
                            break;
                        default:
                            Log.errorf("Unknown message kind: %s", kind);
                    }
                } catch (JsonProcessingException e) {
                    Log.error(e);
                }
            });
        }).requestHandler(router).listen(8081);
        System.out.println("UIServer started.");
    }
}
