package com.redhat;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class ConfigurationModelTest {
    
    @Test
    public void testLoadYaml() throws StreamReadException, DatabindException, IOException {
        ConfigurationModel model = ConfigurationModel.loadFromFile(new File("src/test/resources/example1.yaml"));
    }
}
