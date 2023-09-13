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

import io.quarkus.logging.Log;
import io.quarkus.picocli.runtime.annotations.TopCommand;
import io.quarkus.test.junit.QuarkusTest;
import picocli.CommandLine;

@QuarkusTest
@Disabled
public class ConfigurationModelTest {

    @Inject
    @TopCommand
    EntryCommand entryCommand;
    
    @Test
    public void testLoadYaml() throws StreamReadException, DatabindException, IOException {
        Log.info("Running testLoadYaml test.\n");
        ConfigurationModel model1 = ConfigurationModel.loadFromFile(new File("src/test/resources/example1.yaml"));
        assertNotNull(model1);
        ConfigurationModel model2 = ConfigurationModel.loadFromFile(new File("src/test/resources/example2.yaml"));
        assertNotNull(model2);
        ConfigurationModel model3 = ConfigurationModel.loadFromFile(new File("src/test/resources/example3.yaml"));
        assertNotNull(model3);
    }

    @Test
    public void testCLI() {
        Log.info("Running testCLI test.\n");
        CommandLine cmd = new CommandLine(entryCommand);
        int exitCode = cmd.execute("-P","2", "-R","3","-m","GET","https://api.publicapis.org/random");
        assertEquals(0, exitCode, "Non 0 exit code.");
    }

}
