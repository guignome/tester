package com.redhat.tester.api.views;

import java.io.BufferedInputStream;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.logging.Log;
import io.vertx.core.Vertx;
import io.vertx.core.http.ServerWebSocket;

public class JSonResultView {
    private ServerWebSocket ws;
    private String filename;
    BufferedInputStream reader = null;
    Vertx vertx;

    public JSonResultView(Vertx vertx, ServerWebSocket ws, String filename) {
        this.ws = ws;
        this.filename = filename;
        this.vertx = vertx;
        sendViewUpdate();
        vertx.setPeriodic(5000, l->{
             sendViewUpdate();
        });
    }

    void sendViewUpdate() {
        vertx.fileSystem().readFile(filename)
                .onFailure(h -> {
                    Log.error(h);
                })
                .onSuccess(b -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode filejson= null;
                    try {
                        filejson = objectMapper.readTree(b.toString());
                    } catch (JsonMappingException e) {
                        Log.error(e);
                    } catch (JsonProcessingException e) {
                        Log.error(e);
                    }
                    JsonNode fulljson = objectMapper.createObjectNode()
                        .put("kind", "viewUpdate")
                        .put("resourceType","jsonResult")
                        .put("resourceInstance",filename)
                        .set("data",
                            filejson
                        );
                    ws.writeTextMessage(fulljson.toString());
                });
    }
}
