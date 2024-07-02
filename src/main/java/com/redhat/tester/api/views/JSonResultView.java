package com.redhat.tester.api.views;

import java.io.BufferedInputStream;
import java.nio.file.Paths;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.tester.results.ResultCollector;

import io.quarkus.logging.Log;
import io.vertx.core.Vertx;
import io.vertx.core.http.ServerWebSocket;

public class JSonResultView implements Stoppable{
    private ServerWebSocket ws;
    private String filename;
    BufferedInputStream reader = null;
    Vertx vertx;
    long timerid;

    public JSonResultView(Vertx vertx, ServerWebSocket ws, String filename) {
        this.ws = ws;
        this.filename = filename;
        this.vertx = vertx;
        sendViewUpdate();
        timerid = vertx.setPeriodic(5000, l->{
             sendViewUpdate();
        });
    }

    void sendViewUpdate() {
        String fullFileName = Paths.get(ResultCollector.DEFAULT_RESULT_REPOSITORY_FOLDER.getName(), filename).toString();
        vertx.fileSystem().readFile(fullFileName)
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

    @Override
    public void stop() {
        vertx.cancelTimer(timerid);
    }
}
