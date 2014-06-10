/**
 * Copyright (C) 2006, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with
 * the written permission of Intalio Inc. or in accordance with
 * the terms and conditions stipulated in the agreement/contract
 * under which the program(s) have been supplied.
 *
 * $Id$
 * $Log$
 */
package com.intalio.bpms.common.node.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible for setting node health true and false.
 * 
 */
public class NodeHealth {

    private static final Logger LOG = LoggerFactory.getLogger(NodeHealth.class);
    private static final String IS_CURRENT_NODE_HEALTHY = "current.node.healthy";

    static {
        LOG.debug("Node status is healthy by default.");
        System.setProperty(IS_CURRENT_NODE_HEALTHY, "true");
    }

    public static void setHealthy() {
        LOG.debug("Node status is healthy.");
        System.setProperty(IS_CURRENT_NODE_HEALTHY, "true");
    }

    public static void setUnHealthy() {
        LOG.warn("Node status is unhealthy.");
        System.setProperty(IS_CURRENT_NODE_HEALTHY, "false");
    }

    public static boolean isNodeHealthy() {
        String isCurrentNodeHealthy = System
                .getProperty(NodeHealth.IS_CURRENT_NODE_HEALTHY);
        if (Boolean.parseBoolean(isCurrentNodeHealthy)) {
            LOG.debug("Node is healthy");
            return true;
        }
        LOG.debug("Node is unhealthy");
        return false;
    }

}
