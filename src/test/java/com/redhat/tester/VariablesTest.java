package com.redhat.tester;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.redhat.tester.ConfigurationModel.ClientConfiguration.Suite.Step;
import com.redhat.tester.ConfigurationModel.Variable;
import io.quarkus.logging.Log;
import io.quarkus.qute.Qute;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.impl.HttpResponseImpl;
import jakarta.inject.Inject;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class VariablesTest {

    @Inject
    TemplateRenderer renderer;

    @Test
    public void testLoadYaml() throws StreamReadException, DatabindException, IOException {
        Log.info("Running testLoadYaml test.\n");
        Path[] paths = new Path[1];
        paths[0] = Paths.get("src/test/resources/example6.yaml");
        ConfigurationModel model = ConfigurationModel.loadFromFile(paths);
        assertNotNull(model);
        assertEquals(3, model.variables.size());
    }

    @Test
    public void testExtrapoloate() {

        Map<String, Object> ctx = new HashMap<>() {
            {
                put("firstName", "John");
                put("lastName", "Doe");
            }
        };
        String result = renderer.extrapolate("Hello {firstName}", ctx);
        assertTrue(result.contains("John"));
        assertFalse(result.contains("Doe"));
    }

    @Test
    public void testAssertion() {
        String res = Qute.fmt("{result.equals(0)}", Map.of("result", 0));
        assertEquals("true", res);

        HttpResponse<Buffer> response = new HttpResponseImpl<Buffer>(null, 200, null, null, null, null, null, null);
        assertTrue(renderer.evaluateAssertion(Step.DEFAULT_ASSERTION, Map.of("result", response)));

        assertFalse(renderer.evaluateAssertion(Step.DEFAULT_ASSERTION, Map.of()));
    }

    @Test
    public void testOverrideVarWithEnvironment() {
        List<Variable> global = List.of(new Variable("env", "dev"),
                new Variable("endpoint", "test1"));
        List<Variable> local = List.of(new Variable("hostname", "value2"),
                new Variable("endpoint", "test2"));

        ContextMap ctx = new ContextMap();
        ctx.initializeGlobalVariables(global);
        ctx.initializeLocalVariables(local);

        assertEquals("dev", ctx.get("env"));
        assertEquals("test2", ctx.get("endpoint"));
        assertNotEquals("value2", ctx.get("hostname"));
        assertNotNull(ctx.get("hostname"));


    }
}
