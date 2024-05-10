package com.redhat.tester;

import com.redhat.tester.ConfigurationModel.ClientConfiguration.Suite;
import com.redhat.tester.ConfigurationModel.ClientConfiguration.Suite.Step;
import com.redhat.tester.ConfigurationModel.Variable;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.client.HttpResponse;
import java.util.List;
import java.util.Map;

public interface TesterApi {

    int createClient();
    int createServer();
    int createResultCollector();

    //client API
    void initClient(List<Variable> variables);
    Future<?> execute(Suite suite);
    Future<?> execute(List<Suite> suites);
    Future<?> execute(List<Suite> suites, int repeat);
    Future<HttpResponse<Buffer>> execute(Step step);

    //Server API
    void initServer();
    Future<HttpServer> run();

    //ResultCollector API

    void initResultCollector(String file, ConfigurationModel model);
    void beforeStep(Step step, Map<String, Object> ctx);
    void afterStep(Step step, Map<String, Object> ctx);
    void afterSuite(Suite suite, Map<String, Object> ctx);
    int size();
    String renderSummary();
    void close();
    
}
