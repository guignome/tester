package com.redhat;

import com.redhat.ConfigurationModel.ClientConfiguration.Scenario;
import com.redhat.ConfigurationModel.ClientConfiguration.Scenario.Step;

import io.quarkus.logging.Log;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;

public class ClientRunner extends Thread{
    private ConfigurationModel model;
    private Vertx vertx;

    public Vertx getVertx() {
        return vertx;
    }

    public void setVertx(Vertx vertx) {
        this.vertx = vertx;
    }

    public ConfigurationModel getModel() {
        return model;
    }

    public void setModel(ConfigurationModel model) {
        this.model = model;
    }

    @Override
    public void run() {
        if (model.client == null) {
            return;
        }
        WebClient client = WebClient.create(vertx);

        for (int i = 0; i < model.client.topology.local.repeat; i++) {
            for (Scenario scenario : model.client.scenarios) {
                for (Step step : scenario.steps) {
                    client.get(
                            model.client.endpoint.port, model.client.endpoint.host, step.GET).send()
                            .onSuccess(response -> Log.info("Received response: " + response.bodyAsString()))
                            .onFailure(err -> Log.error("Something went wrong! ", err));
                }
            }
        }
    }
}
