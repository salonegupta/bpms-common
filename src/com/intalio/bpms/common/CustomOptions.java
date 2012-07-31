package com.intalio.bpms.common;

import org.apache.axis2.client.Options;
import org.apache.axis2.transport.http.HTTPConstants;


public class CustomOptions extends Options {
	
	public CustomOptions(){
		super();
		this.setProperty(HTTPConstants.REUSE_HTTP_CLIENT, org.apache.axis2.Constants.VALUE_TRUE);
	}
}
