package com.redhat.tester;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.quarkus.logging.Log;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties(value = "kind")
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

    public Results results = new Results();

    @JsonIgnore
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
    @JsonIgnoreProperties(value = "kind")
    public static class Results {
        public String format;
        public String filename;
    }

    @RegisterForReflection
    @JsonIgnoreProperties(value = "kind")
    public static class Variable {
        public String name;
        public String value;

        public Variable() {
        }

        public Variable(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }

    @RegisterForReflection
    @JsonIgnoreProperties(value = "kind")
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
        @JsonIgnoreProperties(value = "kind")
        public static class Topology {
            public Local local = new Local();

            @RegisterForReflection
            @JsonIgnoreProperties(value = "kind")
            public static class Local {
                public int parallel = 1;
                public int repeat = 1;
            }
        }

        @RegisterForReflection
        @JsonIgnoreProperties(value = "kind")
        public static class Endpoint {

            public String protocol = "http";
            public String name = DEFAULT_ENDPOINT;
            public String host = "localhost";
            public int port;
            public String prefix = "";
            public boolean isdefault = false;
            public HttpOptions httpOptions = new HttpOptions();

            @RegisterForReflection
            @JsonIgnoreProperties(value = "kind")
            public static class HttpOptions {
                public boolean trustAll = true;
                public String protocolVersion = "HTTP_1_1";
            }
        }

        @RegisterForReflection
        @JsonIgnoreProperties(value = "kind")
        public static class Suite {
            public String name = "suite1";
            public List<Variable> variables = new ArrayList<>();
            public List<Step> steps = new ArrayList<>();

            @RegisterForReflection
            @JsonIgnoreProperties(value = "kind")
            public static class Step {
                public static Assertion DEFAULT_ASSERTION;
                public static int stepNumber = 0;
                static {
                    DEFAULT_ASSERTION = new Assertion();
                    DEFAULT_ASSERTION.name = "HTTP Return Code is OK";
                    DEFAULT_ASSERTION.body = "{result.statusCode().equals(200)}";
                }

                public String method = "GET";
                public String path = "/";
                public String body = "";
                public String endpoint = DEFAULT_ENDPOINT;
                public String name = "Step " + stepNumber++;
                public String register;
                public List<Header> headers = new ArrayList<>();
                public List<Assertion> assertions = new ArrayList<>();

                public Step() {
                    assertions.add(DEFAULT_ASSERTION);
                }
            }

            @RegisterForReflection
            @JsonIgnoreProperties(value = "kind")
            public static class Assertion {
                public String name;
                public String body;
            }
        }
    }

    @RegisterForReflection
    @JsonIgnoreProperties(value = "kind")
    public static class ServerConfiguration {

        public String name;
        public String host = "localhost";
        public int port;
        public Tls tls = null;

        public List<Handler> handlers = new ArrayList<>();

        @RegisterForReflection
        @JsonIgnoreProperties(value = "kind")
        public static class Tls {
            public String keyPath;
            public String certPath;
        }

        @RegisterForReflection
        @JsonIgnoreProperties(value = "kind")
        public static class Handler {
            public String path;
            public String method = "GET";
            public int delay = 0;
            public Response response;
            public int status = 200;
        }

        @RegisterForReflection
        @JsonIgnoreProperties(value = "kind")
        public static class Response {
            public String body;
            public int generatedBodySize;
            public List<Header> headers = new ArrayList<>();
        }
    }

    @RegisterForReflection
    @JsonIgnoreProperties(value = "kind")
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

    public static ConfigurationModel load(URL[] urls)
            throws StreamReadException, DatabindException, IOException, URISyntaxException {
        List<ConfigurationModel> loadedModels = new ArrayList<>();
        for (URL url : urls) {
            ConfigurationModel loadedModel = load(url);
            if (loadedModel != null) {
                loadedModels.add(loadedModel);
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

    public static ConfigurationModel load(URL url)
            throws StreamReadException, DatabindException, IOException, URISyntaxException, MalformedURLException {

        if (url.getProtocol().equals("file")) {
            Path path = Path.of(url.getPath());
            if (Files.isDirectory(path)) {
                ArrayList<URL> paths = new ArrayList<>();
                Files.newDirectoryStream(path).forEach(p -> {
                    try {
                        paths.add(p.toUri().toURL());
                    } catch (MalformedURLException e) {
                        Log.error("Invalid url: " + p, e);
                    }
                });
                return load((URL[]) paths.toArray());
            } else if (path.toString().endsWith(".yml") || path.toString().endsWith(".yaml")) {
                ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
                return mapper.readValue(path.toUri().toURL(), ConfigurationModel.class);
            } else {
                Log.warnf("Ignoring file %s", path.toString());
            }
        } else {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            return mapper.readValue(url, ConfigurationModel.class);
        }
        return null;
    }

    private static URL toURL(URI uri) throws MalformedURLException, URISyntaxException {
        if (uri.isAbsolute()) {
            return uri.toURL();
        } else {
            // Default to file protocol if the uri does not have a scheme.
            return new URI("file", uri.toString(), null).toURL();
        }
    }

    public static ConfigurationModel load(URI uri)
            throws StreamReadException, DatabindException, MalformedURLException, IOException, URISyntaxException {
        return load(toURL(uri));
    }

    public static ConfigurationModel load(String uri)
            throws StreamReadException, DatabindException, MalformedURLException, IOException, URISyntaxException {
        return load(new URI(uri));
    }

    public static ConfigurationModel load(String[] paths)
            throws StreamReadException, DatabindException, MalformedURLException, IOException, URISyntaxException {
        URI[] uris = Arrays.stream(paths)
        .map(p -> {
            try {
                return new URI(p);
            } catch (URISyntaxException e) {
                Log.errorf("Wrong uri format: %s", p);
            }
            return null;
        })
        .toArray(URI[]::new);
        return load(uris);
    }

    public static ConfigurationModel load(URI[] uris)
            throws StreamReadException, DatabindException, IOException, URISyntaxException {
        URL[] urls = Arrays.stream(uris)
                .map(uri -> {
                    try {
                        return toURL(uri);
                    } catch (MalformedURLException | URISyntaxException e) {
                        Log.errorf("Wrong uri format: %s", uri);
                    }
                    return null;
                })
                .toArray(URL[]::new);
        return load(urls);
    }
}
