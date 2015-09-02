/**
 * Copyright (C) 1999-2015, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with
 * the written permission of Intalio Inc. or in accordance with
 * the terms and conditions stipulated in the agreement/contract
 * under which the program(s) have been supplied.
 */

package com.intalio.bpms.common.velocity;

import java.io.StringWriter;
import java.net.URI;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This class performs the Apache-Velocity Runtime Execution
 * 
 * @author Cyril Antony
 * 
 */
public class VelocityRuntime {
    private static final Log LOGGER = LogFactory.getLog(VelocityRuntime.class);

    private static VelocityRuntime _instance;

    private VelocityEngine ve;

    /**
     * The private constructor initialize the velocity engine
     */
    private VelocityRuntime() {
        Properties props = new Properties();
        props.put("resource.loader", "file");
        props.put("file.resource.loader.description",
                "Velocity File Resource Loader");
        props.put("file.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
        props.put("file.resource.loader.path", "");
        props.put("file.resource.loader.cache", "false");
        props.put("file.resource.loader.modificationCheckInterval", "2");
        ve = new VelocityEngine(props);
        ve.init();

    }

    /**
     * @return the singleton instance of the class
     */
    public static synchronized VelocityRuntime getInstance() {
        if (null == _instance) {
            _instance = new VelocityRuntime();
        }
        return _instance;
    }

    /**
     * 
     * Performs the runtime transformation of velocity template and runtime
     * variable values and return the result
     * 
     * @param templateURI
     * @param variables
     * @return
     */
    public String execute(URI templateURI, NodeList variables) {
        Template t = null;
        try {
            t = ve.getTemplate(templateURI.getPath());
        } catch (ResourceNotFoundException e) {
            LOGGER.error("Velocity template Resource '" + templateURI
                    + "' not found.", e);
            throw e;
        }
        /* create a context and add runtime variable data */
        VelocityContext context = VariableContextBuilder
                .buildVelocityContext(variables);

        /* now render the template into a StringWriter */
        StringWriter writer = new StringWriter();
        t.merge(context, writer);

        String result = writer.toString();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Velocity Runtime Execution completed;\nTemplateURI:"
                    + templateURI + "\nResult:" + result);
        }
        return result;
    }

    public String execute(URI templateURI, Element varsParent) {
        return execute(templateURI, varsParent.getChildNodes());
    }
}
