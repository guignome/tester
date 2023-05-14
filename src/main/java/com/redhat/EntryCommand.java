package com.redhat;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.RestClientBuilder;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.redhat.ConfigurationModel.ClientConfiguration;
import com.redhat.ConfigurationModel.ClientConfiguration.Endpoint;
import com.redhat.ConfigurationModel.ClientConfiguration.Scenario;
import com.redhat.ConfigurationModel.ClientConfiguration.Scenario.Step;

import io.quarkus.picocli.runtime.annotations.TopCommand;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.uritemplate.UriTemplate;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import io.quarkus.logging.Log;


@CommandLine.Command(name = "tester", mixinStandardHelpOptions = true, description = "Starts the HTTP client.")
class EntryCommand implements Runnable {
    @Parameters(index="0", description= "URL of the resource",defaultValue = "http://localhost:8080")
    URL url;

    @Option(names={"-p", "--port"}, description = "The port number")
    int port;

    @Option(names={"-h","--host"}, description = "The host name")
    String host;

    @Option(names={"-f","--file"}, description = "The file name")
    File file;

    @Inject
    ClientRunner client;

    @Inject
    ServerRunner server;

    
    @Override
    public void run() {
        ConfigurationModel model = null;
        try {
            model = createModelFromOptions();
        } catch (Exception e) {
            Log.error("Couldn't initialize model: ", e);
        }
        server.setModel(model);
        client.setModel(model);
        server.run();
        client.run();
    }

    private ConfigurationModel createModelFromOptions() throws StreamReadException, DatabindException, IOException {
        if(file != null) {
            return ConfigurationModel.loadFromFile(file);
        }
        ConfigurationModel model = new ConfigurationModel();
        model.client = new ClientConfiguration();
        model.client.endpoint = new Endpoint();  
        model.client.endpoint.host = url.getHost();
        model.client.endpoint.port = url.getPort();

        Scenario scenario = new Scenario();
        Step step = new Step();
        step.GET = url.getPath();
        
        model.client.scenarios.add(scenario);
        scenario.steps.add(step);

        return model;
    }
}

