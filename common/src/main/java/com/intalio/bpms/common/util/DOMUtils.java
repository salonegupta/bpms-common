/**
 * Copyright (C) 1999-2015, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with
 * the written permission of Intalio Inc. or in accordance with
 * the terms and conditions stipulated in the agreement/contract
 * under which the program(s) have been supplied.
 */

package com.intalio.bpms.common.util;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Utility class for XML DOM related functions
 * 
 * @author Cyril Antony
 * 
 */
public class DOMUtils {

    private DOMUtils() {
    }

    /**
     * Find a child node with the given local name
     * 
     * @param element
     * @param childLocalName
     * @return
     */
    public static Node getChildWithLocalName(Element element,
            String childLocalName) {
        NodeList childNodes = element.getChildNodes();
        int length = childNodes.getLength();
        for (int i = 0; i < length; i++) {
            Node child = childNodes.item(i);
            String localName = getLocalName(child);
            if (childLocalName.equals(localName)) {
                return child;
            }
        }
        return null;
    }

    /**
     * The org.w3c.dom.Node.getLocalName() method returns NULL at certain cases.
     * This wrapper method is to check and return proper local names in those
     * cases
     * 
     * @param variable
     * @return
     */
    public static String getLocalName(Node variable) {
        String name = variable.getLocalName();
        if (null == name) {
            String[] splits = variable.getNodeName().split(":");
            name = splits[splits.length - 1];
        }
        return name;
    }

}
