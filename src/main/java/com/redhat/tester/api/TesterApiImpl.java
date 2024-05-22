package com.redhat.tester.api;

import java.util.List;
import java.util.Map;

import com.redhat.tester.ConfigurationModel;
import com.redhat.tester.ConfigurationModel.ClientConfiguration.Suite;
import com.redhat.tester.ConfigurationModel.ClientConfiguration.Suite.Step;
import com.redhat.tester.ConfigurationModel.Variable;

import java.io.OutputStream;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.client.HttpResponse;

public class TesterApiImpl implements TesterApi{

    public TesterApiImpl() {
        
    }

    @Override
    public int createClient() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createClient'");
    }

    @Override
    public int createServer() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createServer'");
    }

    @Override
    public int createResultCollector() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createResultCollector'");
    }

    @Override
    public void initClient(List<Variable> variables) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'initClient'");
    }

    @Override
    public Future<?> execute(Suite suite) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'execute'");
    }

    @Override
    public Future<?> execute(List<Suite> suites) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'execute'");
    }

    @Override
    public Future<?> execute(List<Suite> suites, int repeat) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'execute'");
    }

    @Override
    public Future<HttpResponse<Buffer>> execute(Step step) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'execute'");
    }

    @Override
    public void initServer() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'initServer'");
    }

    @Override
    public Future<HttpServer> run() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'run'");
    }

    @Override
    public void initResultCollector(String file, ConfigurationModel model) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'initResultCollector'");
    }

    @Override
    public void beforeStep(Step step, Map<String, Object> ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'beforeStep'");
    }

    @Override
    public void afterStep(Step step, Map<String, Object> ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'afterStep'");
    }

    @Override
    public void afterSuite(Suite suite, Map<String, Object> ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'afterSuite'");
    }

    @Override
    public int size() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'size'");
    }

    @Override
    public String renderSummary() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'renderSummary'");
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'close'");
    }

    @Override
    public Future<?> execute(Step step, List<Variable> variables, int repeat, int parallel,OutputStream out) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'execute'");
    }
    
}
