package com.redhat.tester.api;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.tester.ConfigurationModel;
import com.redhat.tester.ConfigurationModel.ClientConfiguration;
import com.redhat.tester.ConfigurationModel.Variable;
import com.redhat.tester.api.views.JSonResultView;
import com.redhat.tester.api.views.ResultsView;
import com.redhat.tester.api.views.RuntimeView;

import io.quarkus.logging.Log;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;;

public class UIServer {
    Vertx vertx;
    TesterApi api;

    public UIServer(Vertx vertx, TesterApi api) {
        this.vertx = vertx;
        this.api = api;
    }

    public void init() {
        // Start the static content router:
        Router router = Router.router(vertx);
        router.route("/static/*").handler(StaticHandler.create());

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
                        case "startClient":
                            ClientConfiguration config = mapper.readValue(data.get("client").toString(), ClientConfiguration.class);
                            List<Variable> variables = mapper.readValue(data.get("variables").toString(), new TypeReference<List<Variable>>() { });
                            Future<?> fut = api.executeClient(config, variables);
                            JsonNode res = mapper.createObjectNode()
                                .put("kind", "clientStatus")
                                .set("data",mapper.createObjectNode()
                                    .put("running", true)
                                );
                            ws.writeTextMessage(res.asText());
                            break;
                        case "startModel":
                            ConfigurationModel model = mapper.readValue(data.get("model").toString(),ConfigurationModel.class);
                            var future = api.executeClientAndServer(model);
                        case "stopClient":
                            break;
                        case "watch":
                            String resource = data.get("resource").asText();
                            switch (resource) {
                                case "runtime":
                                    var rtView = new RuntimeView(vertx,ws, api);
                                case "results":
                                    var resultsView = new ResultsView(ws);
                                case "jsonresult":
                                    var jsonResultView = new JSonResultView(ws);
                                    break;
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
