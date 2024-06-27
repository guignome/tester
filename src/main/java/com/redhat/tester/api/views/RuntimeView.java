package com.redhat.tester.api.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.tester.api.TesterApi;

import io.vertx.core.Vertx;
import io.vertx.core.http.ServerWebSocket;

public class RuntimeView implements PropertyChangeListener{
    private ServerWebSocket ws;
    private TesterApi api;
    private Vertx vertx;
    
    public RuntimeView(Vertx vertx, ServerWebSocket ws, TesterApi api) {
        this.ws = ws;
        this.api = api;
        this.vertx = vertx;
        // vertx.setPeriodic(5000, l->{
        //     sendViewUpdate();
        // });
        api.addPropertyChangeListener(this);
        
    }

    public JsonNode render() {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.createObjectNode()
            .put("kind", "viewUpdate")
            .put("resourceType","runtime")
            .put("resourceInstance","main")
            .set("data",
                objectMapper.createObjectNode()
                .put("running",api.isRunning())
                .put("reportName", api.getResultFileName())
            );
    }

    void sendViewUpdate() {
        String msg = render().toString();
        ws.writeTextMessage(msg);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        sendViewUpdate();
    }
}
