package com.redhat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;

import io.quarkus.picocli.runtime.annotations.TopCommand;
import io.quarkus.test.junit.QuarkusTest;
import picocli.CommandLine;

@QuarkusTest
public class ConfigurationModelTest {

    @Inject
    @TopCommand
    EntryCommand entryCommand;
    
    @Test
    public void testLoadYaml() throws StreamReadException, DatabindException, IOException {
        ConfigurationModel model1 = ConfigurationModel.loadFromFile(new File("src/test/resources/example1.yaml"));
        assertNotNull(model1);
        ConfigurationModel model2 = ConfigurationModel.loadFromFile(new File("src/test/resources/example2.yaml"));
        assertNotNull(model2);
        ConfigurationModel model3 = ConfigurationModel.loadFromFile(new File("src/test/resources/example3.yaml"));
        assertNotNull(model3);
    }

    @Test
    public void testCLI() {
        CommandLine cmd = new CommandLine(entryCommand);
        int exitCode = cmd.execute("http://www.google.com");
        assertEquals(0, exitCode, "Non 0 exit code.");
    }

    @Test
    public void testScenario1() {
        CommandLine cmd = new CommandLine(entryCommand);
        int exitCode = cmd.execute("-f","src/test/resources/example1.yaml");
        assertEquals(0, exitCode, "Non 0 exit code.");
    }

    @Test
    public void testScenario2() {
        CommandLine cmd = new CommandLine(entryCommand);
        int exitCode = cmd.execute("-f","src/test/resources/example2.yaml");
        assertEquals(0, exitCode, "Non 0 exit code.");
    }

    @Test
    public void testScenario3() {
        CommandLine cmd = new CommandLine(entryCommand);
        int exitCode = cmd.execute("-f","src/test/resources/example3.yaml");
        assertEquals(0, exitCode, "Non 0 exit code.");
    }
}
