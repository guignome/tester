package com.redhat.tester.api.views;

import java.beans.PropertyChangeEvent;
import java.io.BufferedInputStream;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.tester.results.ResultCollector;

import io.quarkus.logging.Log;
import io.vertx.core.Vertx;
import jakarta.websocket.RemoteEndpoint.Async;

public class JSonResultView implements View{
    private String filename;
    BufferedInputStream reader = null;
    Vertx vertx;
    long timerid;
    Async ws;

    public JSonResultView(Vertx vertx, Async ws, String filename) {
        this.ws = ws;
        this.filename = filename;
        this.vertx = vertx;
        
    }

    void sendViewUpdate() {
        String fullFileName = ResultCollector.DEFAULT_RESULT_REPOSITORY_FOLDER.resolve(filename).toString();
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
                    ws.sendText(fulljson.toString());
                });
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'propertyChange'");
    }

    @Override
    public void start() {
        sendViewUpdate();
        timerid = vertx.setPeriodic(5000, l->{
             sendViewUpdate();
        });
    }

    @Override
    public void stop() {
        vertx.cancelTimer(timerid);
    }
}
