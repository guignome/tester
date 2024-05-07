package com.redhat;

import com.redhat.ConfigurationModel.ClientConfiguration.Endpoint;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class Endpoints {
    private Endpoint defaultEndpoint = null;
    public List<Endpoint> endpoints = new ArrayList<>();

    public void init(List<Endpoint> endpoints) {
        Log.infof("Initializing Endpoints size %s",endpoints.size());
        this.defaultEndpoint = null;
        this.endpoints = endpoints;
    }

    public Endpoint getDefaultEndpoint() {
        if (defaultEndpoint != null) {
            return defaultEndpoint;
        }
        for (Endpoint currentEndpoint : endpoints) {
            if (currentEndpoint.isdefault) {
                defaultEndpoint = currentEndpoint;
                return defaultEndpoint;
            }
        }
        if (endpoints.size() == 1) {
            defaultEndpoint = endpoints.get(0);
            return defaultEndpoint;
        } else {
            defaultEndpoint = doGetEndpoint(ConfigurationModel.DEFAULT_ENDPOINT);
        }
        return defaultEndpoint;
    }

    public Endpoint getEndpoint(String name) {
        if (ConfigurationModel.DEFAULT_ENDPOINT.equals(name)) {
            return getDefaultEndpoint();
        }
        return doGetEndpoint(name);

    }

    private Endpoint doGetEndpoint(String name) {
        for (Endpoint endpoint : endpoints) {
            if (name.equals(endpoint.name)) {
                return endpoint;
            }
        }
        return null;
    }

}
