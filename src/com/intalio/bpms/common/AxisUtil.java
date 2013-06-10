package com.intalio.bpms.common;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.httpclient.HttpClient;

public class AxisUtil {

	public Options getDefaultOptions(){
		Options options = new Options();
		options.setProperty(HTTPConstants.REUSE_HTTP_CLIENT, org.apache.axis2.Constants.VALUE_TRUE);
		return options ;
	}
	
	
	public ServiceClient getServiceClient() throws AxisFault {
		HttpClient httpClient = new HttpClient(MultiThreadedHttpConnectionManagerFactory.getInstance());
		ServiceClient sc = new ServiceClient(null, null);
		sc.setOptions(getDefaultOptions());

        /*
         * Fix for PXEI-917: Earlier we were setting httpclient in axis2's
         * configContext due to issue of cleanup of idle http thread. Now as
         * com.intalio.bpms.common.MultiThreadedHttpConnectionManagerFactory.
         * idleConnectionTimeoutThread is taking care of that we don't need to
         * set httpClient in configContext of axis2 as it is causing PXEI-917 by
         * overriding the httpclient params.
         */
        sc.getOptions().setProperty(HTTPConstants.CACHED_HTTP_CLIENT,
                httpClient);
		return sc;
	}

	public void closeClient(ServiceClient sc) throws AxisFault {
		if (sc != null) {
			sc.cleanup();
			sc.cleanupTransport();
		}
	}
}
