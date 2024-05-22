package com.redhat.tester.api;

import java.util.List;
import java.util.Map;

import com.redhat.tester.ConfigurationModel.ClientConfiguration.Suite.Step;
import com.redhat.tester.ConfigurationModel.Variable;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Command {
    
    public Command(){
        super();
    }
    public String command;
    public Data data;

    @RegisterForReflection
    public static class Data {
        public Step step;
        public List<Variable> variables;
        public int repeat;
        public int parallel;        
    }

}
