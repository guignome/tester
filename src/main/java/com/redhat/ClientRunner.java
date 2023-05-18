package com.redhat;

import java.io.IOException;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.redhat.ConfigurationModel.ClientConfiguration.Scenario;
import com.redhat.ConfigurationModel.ClientConfiguration.Scenario.Step;

import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;

@Dependent
public class ClientRunner {
    private ConfigurationModel model;

    public ConfigurationModel getModel() {
        return model;
    }

    public void setModel(ConfigurationModel model) {
        this.model = model;
    }

    @Inject
    Vertx vertx;

    public void run() {
        if (model.client == null) {
            return;
        }
        WebClient client = WebClient.create(vertx);

        for (Scenario scenario : model.client.scenarios) {
            for (Step step : scenario.steps) {
                client.get(
                        model.client.endpoint.port, model.client.endpoint.host, step.GET).send()
                        .onSuccess(response -> System.out
                                .println("Received response: " + response.bodyAsString()))
                        .onFailure(err -> System.out.println("Something went wrong " + err.getMessage()));
            }
        }

    }
}
