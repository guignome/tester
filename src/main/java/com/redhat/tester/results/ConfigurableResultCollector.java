package com.redhat.tester.results;

import java.util.Map;

import com.redhat.tester.ConfigurationModel;
import com.redhat.tester.ConfigurationModel.ClientConfiguration.Suite;
import com.redhat.tester.ConfigurationModel.ClientConfiguration.Suite.Step;

public class ConfigurableResultCollector implements ResultCollector{

    @Override
    public String getFormat() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getFormat'");
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
    public void init(ConfigurationModel model) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'init'");
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'close'");
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
    public ResultSummary getCurrentResultSummary() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCurrentResultSummary'");
    }

    @Override
    public String getResultFileName() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getResultFileName'");
    }
    
}
