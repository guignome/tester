package com.redhat.tester.api.views;

import java.beans.PropertyChangeEvent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.websocket.RemoteEndpoint.Async;

public class ResultsView implements View{
    
    Async ws;

    public ResultsView(Async ws) {
       this.ws = ws;
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

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'propertyChange'");
    }

    @Override
    public void start() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'start'");
    }

    @Override
    public void stop() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'stop'");
    }
}
