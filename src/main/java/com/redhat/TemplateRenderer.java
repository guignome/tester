package com.redhat;

import java.util.Map;

import io.quarkus.qute.Engine;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

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
}
