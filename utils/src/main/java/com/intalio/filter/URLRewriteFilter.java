/**
 * Copyright (c) 2005-2014 Intalio inc.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Intalio inc. - initial API and implementation
 */

package com.intalio.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * Filter class used for request re-direction and forwarding based on provided
 * rule configuration.
 * 
 * @author Salone
 * 
 */
public class URLRewriteFilter implements javax.servlet.Filter {

    private static final Logger logger = Logger
            .getLogger(URLRewriteFilter.class);
    private static final String RULES_CONFIG = "urlrewrite.xml";
    private static final Pattern PLACE_HOLDER_PATTERN = Pattern
            .compile("\\$[0-9]+");

    private List<Rule> rules = new ArrayList<Rule>();

    @Override
    public void destroy() {
        rules.clear();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestUri = resolveBaseRequestUri(httpRequest);

        for (Rule rule : rules) {
            Pattern pattern = Pattern.compile(rule.getFrom());
            Matcher matcher = pattern.matcher(requestUri);

            if (matcher.matches()) {
                logger.info("processing request for " + requestUri);
                logger.info("matched rule Rule " + rules.indexOf(rule) + " ("
                        + rule.getFrom() + ", " + rule.getTo() + ", "
                        + rule.getType() + ")");

                try {
                    List<String> matches = new ArrayList<String>();

                    for (int i = 0; i < matcher.groupCount(); i++) {
                        matches.add(matcher.group(i + 1));
                    }

                    Rule enhancedRule = enhanceRule(rule, matches);
                    executeRule(enhancedRule, httpRequest, httpResponse);

                    return;
                } catch (Exception e) {
                    logger.error(e.getLocalizedMessage(), e);
                }

            }
        }

        chain.doFilter(request, response);
    }

    private String resolveBaseRequestUri(HttpServletRequest request) {
        String requestContext = request.getServletContext().getContextPath();
        String resolvedRequestUri = request.getRequestURI();

        if (resolvedRequestUri.startsWith(requestContext)) {
            resolvedRequestUri = resolvedRequestUri.replace(requestContext, "");
        }

        return resolvedRequestUri;
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
        // String logFlag = config.getInitParameter("log-flag");
        RuleParser ruleParser = new RuleParser();
        try {
            rules = ruleParser.parseRules(RULES_CONFIG);
            logger.info("initiated with " + rules.size() + " rules");
            logger.info("conf is ok (loaded)");
        } catch (Exception e) {
            logger.info("initiation failed");
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    private void executeRule(Rule rule, HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException {
        String type = rule.getType();

        if (type.equals("redirect")) {
            doRedirect(rule, request, response);
        } else {
            doForward(rule, request, response);
        }
    }

    private void doRedirect(Rule rule, HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        String targetUri = resolveTargetUri(rule.getTo(), rule.isQsappend(),
                request.getQueryString());

        logger.info("request executed with " + rule.getTo());
        response.sendRedirect(targetUri);
    }

    private void doForward(Rule rule, HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        String context = rule.getContext();

        String targetUri = resolveTargetUri(rule.getTo(), rule.isQsappend(),
                request.getQueryString());

        if (context != null) {
            ServletContext fromContext = request.getServletContext();
            ServletContext toContext = fromContext.getContext("/" + context);

            logger.info("request executed with /" + context + rule.getTo());
            RequestDispatcher dispatcher = toContext
                    .getRequestDispatcher(targetUri);
            dispatcher.forward(request, response);
        } else {
            logger.info("request executed with " + rule.getTo());
            RequestDispatcher dispatcher = request
                    .getRequestDispatcher(targetUri);
            dispatcher.forward(request, response);
        }

    }

    private String resolveTargetUri(String to, boolean qsappend,
            String queryString) {
        if (qsappend && queryString != null && !queryString.isEmpty()) {
            return to + "?" + queryString;
        } else {
            return to;
        }
    }

    public Rule enhanceRule(Rule rule, List<String> matches) throws Exception {
        Rule enhancedRule = (Rule) rule.clone();

        Matcher placeHolderMatcher = PLACE_HOLDER_PATTERN.matcher(enhancedRule
                .getTo());

        while (placeHolderMatcher.find()) {
            String placeHolderToken = placeHolderMatcher.group();

            if (matches.size() == 0) {
                logger.error("from and to parts are not matching for rule ("
                        + rule.getFrom() + ", " + rule.getTo() + ")");
                throw new Exception(
                        "from and to parts are not matching for rule ("
                                + rule.getFrom() + ", " + rule.getTo() + ")");
            }

            String matchedString = matches.remove(0);

            String toUri = enhancedRule.getTo().replace(placeHolderToken,
                    matchedString);
            enhancedRule.setTo(toUri);
        }

        return enhancedRule;
    }
}
