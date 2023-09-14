package com.redhat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class ConfigurationModel {

    public ConfigurationModel() {
        super();
    }

    public ClientConfiguration client;

    public ServerConfiguration server;

    @RegisterForReflection
    public static class ClientConfiguration {
        public Topology topology = new Topology();
        public Endpoint endpoint = new Endpoint();
        public List<Suite> suites = new ArrayList<>();

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
            public String host = "localhost";
            public int port = 80;
        }

        @RegisterForReflection
        public static class Suite {
            public String name = "suite1";
            public List<Step> steps = new ArrayList<>();

            @RegisterForReflection
            public static class Step {
                public String method = "GET";
                public String path = "/";
                public String body;
                public List<Header> headers;
                public List<Assertion> assertions;
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
        public Endpoint endpoint;

        public List<Handler> handlers = new ArrayList<>();

        @RegisterForReflection
        public static class Handler {
            public String path;
            public String method;
            public int delay = 1;
            public String response;
        }

        @RegisterForReflection
        public static class Endpoint {
            public int port;
        }
    }

    @RegisterForReflection
    public static class Header {
        public String name = "";
        public String value = "";
    }

    public static ConfigurationModel loadFromFile(File file)
            throws StreamReadException, DatabindException, IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        return mapper.readValue(file, ConfigurationModel.class);
    }

}
