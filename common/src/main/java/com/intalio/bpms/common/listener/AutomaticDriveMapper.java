/**
 * Copyright (C) 1999-2015, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with
 * the written permission of Intalio Inc. or in accordance with
 * the terms and conditions stipulated in the agreement/contract
 * under which the program(s) have been supplied.
 */

package com.intalio.bpms.common.listener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

/**
 * This listener is used to map a common drive to the system if
 * MAP_DRIVE_COMMAND property has been specified in ODE_PROPERTIES_FILE file.
 * This class is invoked by the server before initializing a web application.
 * 
 * @author Salone
 * 
 */
public class AutomaticDriveMapper implements ServletContextListener {

    private static final Logger logger = Logger
            .getLogger(AutomaticDriveMapper.class);

    private static final String OS_NAME = "os.name";

    private static final String WINDOWS_OS_NAME = "Windows";

    private static final String SERVER_CONFIG_DIRECTORY = "INTALIO_CONF";

    private static final String ODE_PROPERTIES_FILE = "ode-axis2.properties";

    private static final String MAP_DRIVE_COMMAND = "ode-axis2.exec";

    private static final String DEPLOY_DIRECTORY = "ode-axis2.deploy.dir";

    /**
     * Matches input OS name against current OS name using os.name system
     * property.
     * 
     * @param requiredOSName
     * @return
     */
    private boolean isMatchingCurrentOS(String requiredOSName) {
        String osName = System.getProperty(OS_NAME);
        if (osName == null) {
            return false;
        }

        return osName.startsWith(requiredOSName);
    }

    /**
     * Loads properties from ODE_PROPERTIES_FILE file. It loads the file from
     * SERVER_CONFIG_DIRECTORY location.
     * 
     * @return
     */
    private Properties loadProperties() {
        String root = System.getProperty(SERVER_CONFIG_DIRECTORY);
        Properties props = new Properties();

        try {
            props.load(new FileInputStream(new File(root + File.separator
                    + ODE_PROPERTIES_FILE)));
        } catch (Exception e) {
            logger.error("Not able to load " + ODE_PROPERTIES_FILE + " file", e);
        }

        if (props.isEmpty()) {
            logger.error("Not able to load properties from "
                    + ODE_PROPERTIES_FILE + " file. Please verify if "
                    + ODE_PROPERTIES_FILE + " is configured properly.");
        }

        return props;
    }

    /**
     * Executes input map drive command.
     * 
     * @param mapCommand
     */
    private void mapDrive(String mapCommand) {
        Runtime rt = Runtime.getRuntime();

        try {
            logger.info("Trying to map drive using command " + mapCommand);
            rt.exec(mapCommand);
            logger.info("Mapped drive has been created successfully.");
        } catch (IOException e) {
            logger.error("Not able to execute map drive command " + mapCommand,
                    e);
        }
    }

    /**
     * Maps OS drive based on the given command. This happens only when
     * following conditions are met:
     * 1. OS is Windows
     * 2. ODE_PROPERTIES_FILE file contains DEPLOY_DIRECTORY property.
     * 3. ODE_PROPERTIES_FILE file contains MAP_DRIVE_COMMAND property.
     * 4. Configured deploy directory based on DEPLOY_DIRECTORY property
     * doesn't exists.
     */
    public void mapDriveIfRequired() {
        if (isMatchingCurrentOS(WINDOWS_OS_NAME)) {
            Properties props = loadProperties();

            String deployDirPath = props.getProperty(DEPLOY_DIRECTORY);
            String mapCommand = props.getProperty(MAP_DRIVE_COMMAND);

            if (deployDirPath != null && !deployDirPath.equals("")) {
                File deployDir = new File(deployDirPath);

                if (mapCommand != null && !mapCommand.equals("")) {
                    if (!deployDir.exists()) {
                        mapDrive(mapCommand);
                    } else {
                        logger.info("Configured " + DEPLOY_DIRECTORY + " path "
                                + deployDirPath + " exists.");
                    }
                } else {
                    logger.error(MAP_DRIVE_COMMAND
                            + " property is not configured in "
                            + ODE_PROPERTIES_FILE + " file");
                }
            } else {
                logger.error(DEPLOY_DIRECTORY
                        + " property is not configured in "
                        + ODE_PROPERTIES_FILE + " file");
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        mapDriveIfRequired();
    }
}
