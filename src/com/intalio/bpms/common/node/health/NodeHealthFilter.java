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

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class will intercept the request and will check for the node health. If
 * the node is unhealthy it will return 503 error else forward the request.
 * 
 */
public class NodeHealthFilter implements Filter {

    private static final Logger LOG = LoggerFactory
            .getLogger(NodeHealthFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest
                && !NodeHealth.isNodeHealthy()) {
            HttpServletResponse resp = (HttpServletResponse) response;
            // returning 503 status as response, if node is unhealthy.
            LOG.warn("Node " + request.getLocalName() + ":"
                    + request.getLocalPort()
                    + " is unhealthy, sending 503 error.");
            resp.sendError(503);
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        // nothing
    }

    @Override
    public void destroy() {
        // nothing
    }

}
