package com.redhat.tester;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.quarkus.logging.Log;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RegisterForReflection
public class ConfigurationModel {
    public static final String DEFAULT_ENDPOINT = "default";
    public static final String DEFAULT_SERVER = "default";

    public ConfigurationModel() {
        super();
    }

    public List<Variable> variables = new ArrayList<>();

    public ClientConfiguration client;

    public List<ServerConfiguration> servers = new ArrayList<>();

    public ServerConfiguration getDefaultServer() {
        return getServer(DEFAULT_SERVER);
    }

    public ServerConfiguration getServer(String name) {
        for (ServerConfiguration currentServer : servers) {
            if (name.equals(currentServer.name)) {
                return currentServer;
            }
        }
        return null;
    }

    @RegisterForReflection
    public static class Variable {
        public String name;
        public String value;
        public Variable(){}
        public Variable(String name, String value) {
            this.name = name;
            this.value = value;
        }

    }

    @RegisterForReflection
    public static class ClientConfiguration {
        public Topology topology = new Topology();
        public List<Endpoint> endpoints = new ArrayList<>();
        public List<Suite> suites = new ArrayList<>();
        private Endpoint defaultEndpoint = null;

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
        
        @RegisterForReflection
        public static class Topology {
            public Local local = new Local();

            @RegisterForReflection
            public static class Local {
                public int parallel = 1;
                public int repeat = 1;
            }
        }

        @RegisterForReflection
        public static class Endpoint {

            public String protocol = "http";
            public String name = DEFAULT_ENDPOINT;
            public String host = "localhost";
            public int port;
            public String prefix = "";
            public boolean isdefault = false;
        }

        @RegisterForReflection
        public static class Suite {
            public String name = "suite1";
            public List<Variable> variables = new ArrayList<>();
            public List<Step> steps = new ArrayList<>();

            @RegisterForReflection
            public static class Step {
                public static Assertion DEFAULT_ASSERTION;
                public static int stepNumber = 0;
                static {
                    DEFAULT_ASSERTION = new Assertion();
                    DEFAULT_ASSERTION.name="HTTP Return Code is OK";
                    DEFAULT_ASSERTION.body="{result.statusCode().equals(200)}";
                }

                public String method = "GET";
                public String path = "/";
                public String body = "";
                public String endpoint = DEFAULT_ENDPOINT;
                public String name = "Step " + stepNumber++;
                public List<Header> headers = new ArrayList<>();
                public List<Assertion> assertions = new ArrayList<>();

                public Step() {
                    assertions.add(DEFAULT_ASSERTION);
                }
            }

            @RegisterForReflection
            public static class Assertion {
                public String name;
                public String body;

            }
        }
    }

    @RegisterForReflection
    public static class ServerConfiguration {

        public String name;
        public String host = "localhost";
        public int port;

        public List<Handler> handlers = new ArrayList<>();

        @RegisterForReflection
        public static class Handler {
            public String path;
            public String method = "GET";
            public int delay = 0;
            public String response;
            public int status = 200;
        }

    }

    @RegisterForReflection
    public static class Header {
        public String name = "";
        public String value = "";

        public Header() {
        }

        public Header(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }

    public static ConfigurationModel loadFromFile(File[] files)
            throws StreamReadException, DatabindException, IOException {
        List<ConfigurationModel> loadedModels = new ArrayList<>();
        for (File file : files) {
            if (file.isDirectory()) {
                loadedModels.add(loadFromFile(file.listFiles((d, n) -> {
                    return n.endsWith(".yml") || n.endsWith(".yaml");
                })));
            } else {
                loadedModels.add(loadFromFile(file));
            }
        }
        // merge
        ConfigurationModel mergedModel = new ConfigurationModel();
        for (ConfigurationModel currentModel : loadedModels) {
            // merge clients
            if (currentModel.client != null) {
                if (mergedModel.client == null) {
                    mergedModel.client = new ClientConfiguration();
                    mergedModel.client.topology = currentModel.client.topology;
                } else {
                    Log.warn("Loading duplicate client definition, one of them will be overriden.");
                }
                // merge client endpoints
                mergedModel.client.endpoints.addAll(currentModel.client.endpoints);

                // merge client suites
                mergedModel.client.suites.addAll(currentModel.client.suites);
            }

            // merge servers
            if (currentModel.servers.size() > 0) {
                mergedModel.servers.addAll(currentModel.servers);
            }
            // merge variables
            if (currentModel.variables.size() > 0) {
                mergedModel.variables.addAll(currentModel.variables);
            }
        }

        return mergedModel;
    }

    public static ConfigurationModel loadFromFile(File file)
            throws StreamReadException, DatabindException, IOException {

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        return mapper.readValue(file, ConfigurationModel.class);
    }
}
