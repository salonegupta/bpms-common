/**
 * Copyright (C) 1999-2015, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with
 * the written permission of Intalio Inc. or in accordance with
 * the terms and conditions stipulated in the agreement/contract
 * under which the program(s) have been supplied.
 */

package com.intalio.bpms.common.velocity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.intalio.bpms.common.util.DOMUtils;

/**
 * Apache Velocity Runtime Engine requires a Velocity-variable-context object
 * for the template tranformation. This Utility class builds a variable context
 * from the given XML based varibles.
 * 
 * @author Cyril Antony
 * 
 */
public class VariableContextBuilder {

    private VariableContextBuilder() {
    }

    /**
     * Method traverse the xml variables and construct a velocity compliant
     * variable object and set them in a velocity context. The velocity context
     * is returned after building.
     * 
     * @param variables
     * @return
     */
    public static VelocityContext buildVelocityContext(NodeList variables) {
        VelocityContext velocityContext = new VelocityContext();
        int varSize = variables.getLength();
        for (int i = 0; i < varSize; i++) {
            Node node = variables.item(i);
            if (!(node instanceof Element)) {
                continue;
            }
            Element variable = (Element) node;
            String name = DOMUtils.getLocalName(variable);
            Object value = getVariableObj(variable);
            velocityContext.put(name, value);
        }
        return velocityContext;
    }

    /**
     * Method traverse the given xml elements and construct a velocity compliant
     * variable object and set them in a velocity context. The velocity context
     * is returned after building.
     * 
     * @param variables
     * @return
     */
    public static VelocityContext buildVelocityContext(Element... variables) {
        VelocityContext velocityContext = new VelocityContext();
        int varSize = variables.length;
        for (int i = 0; i < varSize; i++) {
            Node node = variables[i];
            if (!(node instanceof Element)) {
                continue;
            }
            Element variable = (Element) node;
            String name = DOMUtils.getLocalName(variable);
            Object value = getVariableObj(variable);
            velocityContext.put(name, value);
        }
        return velocityContext;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static Object getVariableObj(Element xml) {
        NodeList variables = xml.getChildNodes();
        if (variables.getLength() == 1) {
            Node textNode = variables.item(0);
            if (textNode.getNodeType() == Element.TEXT_NODE) {
                return xml.getTextContent();
            }
        }

        Map map = new HashMap();
        List list = new ArrayList();
        boolean isMap = true;
        for (int i = 0; i < variables.getLength(); i++) {
            Node next = variables.item(i);
            if (!(next instanceof Element)) {
                // ignore #text child
                continue;
            }
            Object obj = getVariableObj((Element) next);
            String nodeName = DOMUtils.getLocalName(next);
            if (isMap) {
                if (map.containsKey(nodeName)) {
                    isMap = false;
                    list.add(map.remove(nodeName));
                    list.add(obj);
                } else {
                    map.put(nodeName, obj);
                }
            } else {
                list.add(obj);
            }
        }
        return isMap ? map : list;
    }

}