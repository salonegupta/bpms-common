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

/**
 * Model class to represent Rule details
 * 
 * @author Salone
 * 
 */
public class Rule implements Cloneable {

    private String from;
    private String to;
    private String context;
    private boolean qsappend;
    private String type;
    private String matchType;

    public Rule(String matchType, String from, String to, String context,
            boolean qsappend, String type) {
        this.matchType = matchType;
        this.from = from;
        this.to = to;
        this.context = context;
        this.qsappend = qsappend;
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public boolean isQsappend() {
        return qsappend;
    }

    public void setQsappend(boolean qsappend) {
        this.qsappend = qsappend;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMatchType() {
        return matchType;
    }

    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }

    public String toString() {
        return "Rule {from: " + from + ", to: " + to + ", match-type: "
                + matchType + ", context: " + context + ", qsappend: "
                + qsappend + ", type: " + type + "}";
    }

    public boolean equals(Object object) {
        if (object instanceof Rule) {
            Rule rule = (Rule) object;
            if (rule.context.equals(this.context) && rule.to.equals(this.to)
                    && rule.from.equals(this.from)
                    && rule.type.equals(this.type)) {
                return true;
            }
        }

        return false;
    }

    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
