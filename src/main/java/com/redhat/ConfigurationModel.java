package com.redhat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class ConfigurationModel {

    public ClientConfiguration client;

    public ServerConfiguration server;

    public static class ClientConfiguration {
        public Topology topology = new Topology();
        public Endpoint endpoint = new Endpoint();
        public List<Suite> suites = new ArrayList<>();

        public static class Topology {
            public Local local = new Local();

            public static class Local {
                public int parallel = 1;
                public int repeat = 1;
            }
        }

        public static class Endpoint {
            public String host = "localhost";
            public int port = 80;
        }

        public static class Suite {
            public String name = "suite1";
            public List<Step> steps = new ArrayList<>();

            public static class Step {
                public String method = "GET";
                public String path = "/";
                public String body;
                public List<Header> headers;
                public List<Assertion> assertions;
            }

            public static class Assertion {
                String name;
                String body;
            }
        }
    }

    public static class ServerConfiguration {
        public Endpoint endpoint;

        public List<Handlers> handlers = new ArrayList<>();

        public static class Handlers {
            public String path;
            public String method;
            public int delay = 1;
            public String response;
        }

        public static class Endpoint {
            public int port;
        }
    }

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
