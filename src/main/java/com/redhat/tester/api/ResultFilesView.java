package com.redhat.tester.api;

import java.beans.PropertyChangeEvent;
import java.nio.file.Path;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.redhat.tester.api.views.View;
import com.redhat.tester.results.ResultCollector;

import io.vertx.core.Vertx;
import jakarta.websocket.RemoteEndpoint.Async;

public class ResultFilesView implements View {

    private Async ws;
    private Vertx vertx;

    public ResultFilesView(Vertx vertx, Async ws) {
        this.ws = ws;
        this.vertx = vertx;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'propertyChange'");
    }

    @Override
    public void start() {
        sendViewUpdate();
    }

    @Override
    public void stop() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'stop'");
    }

    void sendViewUpdate() {
        vertx.fileSystem().readDir(ResultCollector.DEFAULT_RESULT_REPOSITORY_FOLDER.toString())
                .onSuccess(s -> {
                    ObjectMapper mapper = new ObjectMapper();

                    ObjectNode response = mapper.createObjectNode()
                            .put("kind", "viewUpdate")
                            .put("resourceType", "resultFiles")
                            .put("resourceInstance", "main");
                    
                            ArrayNode arrayNode = response.putArray("data");
                            s.forEach((f) -> {
                                Path relative = Path.of(f).getFileName();
                                arrayNode.add(relative.toString());
                            });

                    ws.sendText(response.toString());
                });
    }

}
