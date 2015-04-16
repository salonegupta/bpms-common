/**
 * Copyright (c) 2005-2014 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 */

package com.intalio.filter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Rule parser to parse rules for request re-direction and forwarding.
 * 
 * @author Salone
 * 
 */
public class RuleParser {

    private static final Logger logger = Logger.getLogger(RuleParser.class);

    public List<Rule> parseRules(String rulesFile) throws Exception {
        List<Rule> rules = new ArrayList<Rule>();

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        InputStream stream = null;

        try {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(rulesFile);
            Document doc = dBuilder.parse(stream);
            doc.getDocumentElement().normalize();

            NodeList nodes = doc.getChildNodes();
            Element rootElement = (Element) nodes.item(0);

            NodeList ruleNodeList = rootElement
                    .getElementsByTagName(RuleXPaths.RULE_XPATH.getXpath());

            for (int i = 0; i < ruleNodeList.getLength(); i++) {
                Element ruleElement = (Element) ruleNodeList.item(i);
                Rule rule = parseRule(ruleElement);
                rules.add(rule);

                logger.info("loaded rule Rule " + rules.size() + " ("
                        + rule.getFrom() + ", " + rule.getTo() + ", "
                        + rule.getType() + ")");

            }
        } catch (Exception e) {
            logger.error("Rule's configuration is not proper. Not able to parse.");
            throw new Exception(
                    "Rule's configuration is not proper. Not able to parse.", e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    logger.warn("not able to close rules configuration input stream");
                }
            }
        }

        return rules;
    }

    private Rule parseRule(Element ruleElement) {
        Element fromElement = (Element) ruleElement.getElementsByTagName(
                RuleXPaths.FROM_XPATH.getXpath()).item(0);
        String from = fromElement.getTextContent();

        Element toElement = (Element) ruleElement.getElementsByTagName(
                RuleXPaths.TO_XPATH.getXpath()).item(0);
        String to = toElement.getTextContent();

        String matchType = ruleElement.getAttribute(RuleXPaths.MATCH_TYPE_XPATH
                .getXpath());
        String context = toElement.getAttribute(RuleXPaths.CONTEXT_XPATH
                .getXpath());
        String qsappend = toElement
                .getAttribute(RuleXPaths.QSAPPEND.getXpath());
        String type = toElement.getAttribute(RuleXPaths.TYPE_XPATH.getXpath());

        return new Rule(matchType, from, to, context,
                Boolean.parseBoolean(qsappend), type);
    }

    static enum RuleXPaths {
        RULE_XPATH("rule"), FROM_XPATH("from"), TO_XPATH("to"), MATCH_TYPE_XPATH(
                "match-type"), CONTEXT_XPATH("context"), QSAPPEND("qsappend"), TYPE_XPATH(
                "type");

        private String xpath;

        RuleXPaths(String xpath) {
            this.xpath = xpath;
        }

        public String getXpath() {
            return xpath;
        }

        public void setXpath(String xpath) {
            this.xpath = xpath;
        }
    }
}
