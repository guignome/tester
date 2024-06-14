package com.redhat.tester.api;

import java.util.List;

import com.redhat.tester.ConfigurationModel.ClientConfiguration.Suite.Step;
import com.redhat.tester.ConfigurationModel.Variable;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class ServerMessage {
    
    public ServerMessage(){
        super();
    }
    public String kind;
    public StartData data;

    @RegisterForReflection
    public static class StartData {
        public Step step;
        public List<Variable> variables;
        public int repeat;
        public int parallel;        
    }

}
