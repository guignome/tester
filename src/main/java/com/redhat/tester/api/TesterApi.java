package com.redhat.tester.api;

import com.redhat.tester.ConfigurationModel.ClientConfiguration;
import com.redhat.tester.ConfigurationModel.ServerConfiguration;
import com.redhat.tester.ConfigurationModel;
import com.redhat.tester.ConfigurationModel.Variable;

import java.beans.PropertyChangeListener;
import io.vertx.core.Future;
import java.util.List;

public interface TesterApi {

    // int createClient();
    // int createServer();
    // int createResultCollector();

    // //client API
    // void initClient(List<Variable> variables);
    // Future<?> execute(Suite suite);
    // Future<?> execute(List<Suite> suites);
    Future<?> executeClient(ClientConfiguration config, List<Variable> variables);

    Future<?> executeServer(ServerConfiguration config, List<Variable> variables);

    Future<?> executeClientAndServer(ConfigurationModel model);
    // Future<?> execute(List<Suite> suites, int repeat);
    // Future<?> execute(Step step);

    // //Server API
    // void initServer();
    // Future<HttpServer> run();

    // //ResultCollector API

    // void initResultCollector(String file, ConfigurationModel model);
    // void beforeStep(Step step, Map<String, Object> ctx);
    // void afterStep(Step step, Map<String, Object> ctx);
    // void afterSuite(Suite suite, Map<String, Object> ctx);
    // int size();
    // String renderSummary();
    // void close();

    // State management
    void addPropertyChangeListener(PropertyChangeListener listener);

    void removePropertyChangeListener(PropertyChangeListener listener);

    String getStatus();

}
