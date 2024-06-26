package com.redhat.tester;

import com.redhat.tester.ConfigurationModel.ClientConfiguration.Suite.Assertion;
import io.quarkus.qute.Engine;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateException;
import io.quarkus.qute.TemplateInstance;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Map;

@ApplicationScoped
public class TemplateRenderer {

    @Inject
    Engine engine;

    public String extrapolate(String original, Map<String, Object> variables) {
        // String res = Qute.fmt(original, variables);
        Template template = engine.parse(original);
        TemplateInstance instance = template.instance();
        if (variables != null) {
            variables.forEach(instance::data);
        }
        String res = instance.render();
        return res;
    }

    public boolean evaluateAssertion(Assertion assertion, Map<String,Object> ctx) {
        String result;
        try {
             result = extrapolate(assertion.body, ctx);
        } catch (TemplateException e) {
            return false;
        }

        return "true".equals(result);
    }
}
