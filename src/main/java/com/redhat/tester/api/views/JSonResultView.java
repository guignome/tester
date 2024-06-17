package com.redhat.tester.api.views;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.WatchService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.logging.Log;
import io.vertx.core.http.ServerWebSocket;

public class JSonResultView {
    private ServerWebSocket ws;
    private String filename;


    public JSonResultView(ServerWebSocket ws, String filename) {
        this.ws = ws;
        this.filename = filename;
        try {
            Files.newInputStream(Paths.get(filename), StandardOpenOption.READ);
        } catch (IOException e) {
            Log.error(e);
        }

    }

    public JsonNode render() {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.createObjectNode()
            .put("kind", "viewUpdate")
            .put("resource",filename)
            .set("data",
                objectMapper.createObjectNode()
                .put("reportName", "result123.json")
            );
    }

    void sendViewUpdate() {
        String msg = render().toString();
        ws.writeTextMessage(msg);
    }
    
}
