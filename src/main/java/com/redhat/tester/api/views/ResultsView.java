package com.redhat.tester.api.views;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.core.http.ServerWebSocket;

public class ResultsView {
    
    public ResultsView(ServerWebSocket ws) {
        //TODO Auto-generated constructor stub
    }

    public JsonNode render() {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.createObjectNode()
            .put("kind", "runtimeView")
            .set("data",
                objectMapper.createObjectNode()
                .put("running",true)
                .put("reportName", "result123.json")
            );
    }
}
