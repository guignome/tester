package com.redhat.tester.api.views;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.core.http.ServerWebSocket;

public class JSonResultView {

    public JSonResultView(ServerWebSocket ws) {
        //TODO Auto-generated constructor stub
    }

    public JsonNode render() {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.createObjectNode()
            .put("kind", "jsonResultView")
            .set("data",
                objectMapper.createObjectNode()
                .put("running",true)
                .put("reportName", "result123.json")
            );
    }
    
}
