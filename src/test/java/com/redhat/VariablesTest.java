package com.redhat;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;

import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
public class VariablesTest {

    @Inject
    TemplateRenderer renderer;

    @Test
    public void testLoadYaml() throws StreamReadException, DatabindException, IOException {
        Log.info("Running testLoadYaml test.\n");
        File[] files = new File[1];
        files[0] = new File("src/test/resources/example6.yaml");
        ConfigurationModel model = ConfigurationModel.loadFromFile(files);
        assertNotNull(model);
        assertEquals(3, model.variables.size());
    }

    @Test
    public void testExtrapoloate() {
      
        Map<String,Object> ctx = new HashMap<>() {{
            put("firstName","John");
            put("lastName","Doe");
        }};
        String result = renderer.extrapolate("Hello {firstName}", ctx);
        assertTrue(result.contains("John"));
        assertFalse(result.contains("Doe"));
    }
}
