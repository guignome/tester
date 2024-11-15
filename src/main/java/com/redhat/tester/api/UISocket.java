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
import com.redhat.tester.api.views.View;

import io.quarkus.logging.Log;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.server.ServerEndpoint;
import jakarta.websocket.Session;
import jakarta.websocket.RemoteEndpoint.Async;

@ServerEndpoint("/")
@ApplicationScoped
public class UISocket {

    @Inject
    TesterApi api;
    @Inject
    Vertx vertx;

    Map<String, UserSession> sessions = new HashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        UserSession s = new UserSession(session);
        sessions.put(session.getId(), s);
        // session.getAsyncRemote().sendText(session.getId());
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {

    }

    @OnMessage
    public void onMessage(String text, Session session) {
        Log.info("Received: " + text);
        // Create JSON object
        ObjectMapper mapper = new ObjectMapper();
        Async ws = session.getAsyncRemote();

        try {
            // msg = mapper.readValue(text, ServerMessage.class);
            JsonNode jsonMsg = mapper.readTree(text);
            String kind = jsonMsg.get("kind").asText();
            JsonNode data = jsonMsg.get("data");

            // switch(msg.kind) {
            switch (kind) {
                case "init":
                    Log.info("Received init message.");
                    JsonObject json = new JsonObject()
                            .put("kind", "init")
                            .put("data", api.getCommandLineModel());
                    ws.sendText(json.encode());
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
                    View view = null;
                    switch (resourceType) {
                        case "runtime":
                            view = new RuntimeView(vertx, ws, api);
                            break;
                        case "results":
                            view = new ResultsView(ws);
                            break;
                        case "jsonResult":
                            view = new JSonResultView(vertx, ws, instance);
                            break;
                        default:
                            Log.errorf("Unknown message resource type: %s", resourceType);
                    }
                    sessions.get(session.getId()).watch(resourceType, instance, view);
                    break;
                case "stopWatch":
                    String resourceType2 = jsonMsg.get("resourceType").asText();
                    String instance2 = jsonMsg.get("resourceInstance").asText();
                    sessions.get(session.getId()).stopWatching(resourceType2, instance2);
                    break;
                default:
                    Log.errorf("Unknown message kind: %s", kind);
            }
        } catch (JsonProcessingException e) {
            Log.error(e);
        }
    }

    private static class UserSession {
        private Session session;
        private Map<String, View> views = new HashMap<>();

        public UserSession(Session s) {
            this.session = s;
        }

        public void watch(String resourceType, String resourceInstance, View v) {
            views.put(key(resourceType, resourceInstance), v);
            v.start();
        }

        public void stopWatching(String resourceType, String resourceInstance) {
            View v = views.remove(key(resourceType, resourceInstance));
            v.stop();
        }

        private String key(String resourceType, String resourceInstance) {
            return resourceType + '/' + resourceInstance;
        }
    }

    public void init() {
        Log.info("UI Initialized");
    }

}
