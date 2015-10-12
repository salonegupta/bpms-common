/**
 * Copyright (C) 1999-2015, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with
 * the written permission of Intalio Inc. or in accordance with
 * the termss and conditions stipulated in the agreement/contract
 * under which the program(s) have been supplied.
 */

package com.intalio.bpms.common.filter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * Filter class to add custom headers in HTTP response. It stores all the header
 * details in a map during initialization. Later on for each request which
 * passes through this filter, it adds all the stored headers in the response.
 * 
 * @author Salone
 * 
 */
public class ResponseHeaderFilter implements Filter {

	private Map<String, String> params;

	public ResponseHeaderFilter() {
		params = new HashMap<String, String>();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void init(FilterConfig config) throws ServletException {
		Enumeration<String> names = config.getInitParameterNames();
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			String value = config.getInitParameter(name);
			params.put(name, value);
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		if (response instanceof HttpServletResponse) {
			HttpServletResponse resp = (HttpServletResponse) response;
			for (Entry<String, String> entry : params.entrySet()) {
				resp.setHeader(entry.getKey(), entry.getValue());
			}
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		params.clear();
	}
}
