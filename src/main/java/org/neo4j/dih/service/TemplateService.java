package org.neo4j.dih.service;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

/**
 * Velocity template service.
 */
public class TemplateService {

    /**
     * The logger
     */
    private static final Logger log = LoggerFactory.getLogger(TemplateService.class);

    /**
     * Compile a velocity template.
     *
     * @param template The template script to use
     * @param variables Variable that will be available into the script
     * @return The compile template
     */
    public static String compile(String template, Map<String, Object> variables) {

        // Initialize velocity
        Properties props = new Properties();
        props.setProperty(VelocityEngine.RESOURCE_LOADER, "classpath");
        props.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.Log4JLogChute");
        props.setProperty("runtime.log.logsystem.log4j.logger", "VELOCITY");
        props.setProperty("classpath." + VelocityEngine.RESOURCE_LOADER + ".class", ClasspathResourceLoader.class.getName());
        Velocity.init(props);

        // Put all variable into velocity context
        VelocityContext context = new VelocityContext();
        for (String key : variables.keySet()) {
            context.put(key, variables.get(key));
        }
        // Adding a var 'i' that is unique for this script
        // It serves as an ID
        context.put("i", System.nanoTime());

        // Compile the template with variables
        StringWriter sw = new StringWriter();
        Velocity.evaluate(context, sw, "DIH", template);

        // Return the compile template
        return sw.toString();
    }
}
