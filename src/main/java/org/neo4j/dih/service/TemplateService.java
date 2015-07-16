package org.neo4j.dih.service;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

public class TemplateService {

    public String compile(String templateName, Map<String, Object> variables) {
        // initialize velocity
        Properties props = new Properties();
        props.setProperty(VelocityEngine.RESOURCE_LOADER, "classpath");
        props.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.Log4JLogChute");
        props.setProperty("runtime.log.logsystem.log4j.logger", "VELOCITY");
        props.setProperty("classpath." + VelocityEngine.RESOURCE_LOADER + ".class", ClasspathResourceLoader.class.getName());
        Velocity.init(props);
        VelocityContext context = new VelocityContext();

        for (String key : variables.keySet()) {
            context.put(key, variables.get(key));
        }

        // put parameter for template
        StringWriter sw = new StringWriter();
        Velocity.evaluate(context, sw, "DIH", templateName);

        return sw.toString();
    }
}
