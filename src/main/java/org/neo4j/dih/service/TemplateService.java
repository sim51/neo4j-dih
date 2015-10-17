package org.neo4j.dih.service;

import org.apache.commons.io.FileUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.tools.ToolManager;
import org.neo4j.dih.exception.DIHRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.StringWriter;
import java.util.Map;

/**
 * Velocity template service.
 *
 * @author bsimard
 * @version $Id: $Id
 */
public class TemplateService {

    /**
     * The logger
     */
    private static final Logger log = LoggerFactory.getLogger(TemplateService.class);

    /**
     * Singleton instance of this service.
     */
    private static TemplateService instance = null;

    /**
     * Velocity engine.
     */
    private VelocityEngine velocity;

    /**
     * Get instance of the singleton.
     *
     * @return a {@link org.neo4j.dih.service.TemplateService} object.
     */
    public static synchronized TemplateService getInstance() {
        if(instance == null) {
            instance = new TemplateService();
        }
        return instance;
    }

    private TemplateService() {
        this.velocity = new VelocityEngine();
        velocity.setProperty(VelocityEngine.RESOURCE_LOADER, "classpath");
        velocity.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.Log4JLogChute");
        velocity.setProperty("runtime.log.logsystem.log4j.logger", "VELOCITY");
        velocity.setProperty("classpath." + VelocityEngine.RESOURCE_LOADER + ".class", ClasspathResourceLoader.class.getName());
        velocity.init();
    }

    /**
     * Compile a velocity template.
     *
     * @param template The template script to use
     * @param variables Variable that will be available into the script
     * @return The compile template
     */
    public String compile(String template, Map<String, Object> variables) {
        VelocityContext context = constructContext(variables);
        // Compile the template with variables
        StringWriter sw = new StringWriter();
        velocity.evaluate(context, sw, "DIH", template);
        // Return the compile template
        String result = sw.toString();
        if(log.isDebugEnabled())
            log.debug("Velocity compile template \n template => %s \n\n variables => %s \n\n result => %s", template, variables, result);
        return result;
    }

    /**
     * Compile a velocity template.
     *
     * @param template The template script to use
     * @param variables Variable that will be available into the script
     * @return The compile template
     */
    public String compile(File template, Map<String, Object> variables) {
        try {
            String templateContent =  FileUtils.readFileToString(template, "UTF-8");
            return this.compile(templateContent, variables);
        } catch (java.io.IOException e) {
            throw  new DIHRuntimeException("Error on generate volicity template " + template.getName());
        }
    }

    /**
     * Construct the velocity with all var passed.
     * @param variables
     * @return
     */
    private VelocityContext constructContext(Map<String, Object> variables) {
        // adding velocity tools to context
        ToolManager velocityToolManager = new ToolManager();
        velocityToolManager.configure("velocity-tools.xml");
        VelocityContext context = new VelocityContext(velocityToolManager.createContext());

        // Put all variable into velocity context
        for (String key : variables.keySet()) {
            context.put(key, variables.get(key));
        }
        // Adding a var 'i' that is unique for this script
        // It serves as an ID
        context.put("i", System.nanoTime());

        return context;
    }

}
