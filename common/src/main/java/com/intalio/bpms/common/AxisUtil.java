package com.intalio.bpms.common;

import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.description.TransportOutDescription;
import org.apache.axis2.rpc.client.RPCServiceClient;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.httpclient.HttpClient;

public class AxisUtil {

    public RPCServiceClient getRPCServiceClient() throws AxisFault {
        RPCServiceClient serviceClient = new RPCServiceClient();
        prepareServiceClientOptions(serviceClient, new Options(), true);
        return serviceClient;
    }

    public ServiceClient getServiceClient() throws AxisFault {
        return getServiceClient(new Options());
    }

    public ServiceClient getServiceClient(Options options) throws AxisFault {
        return getServiceClient(options, true);
    }

    public ServiceClient getServiceClient(Options options, boolean isLocalCall)
            throws AxisFault {
        ServiceClient sc = new ServiceClient(null, null);
        prepareServiceClientOptions(sc, options, isLocalCall);
        return sc;
    }

    private void prepareServiceClientOptions(ServiceClient serviceClient,
            Options options, boolean isLocalCall) {
        HttpClient httpClient = new HttpClient(
                MultiThreadedHttpConnectionManagerFactory.getInstance());

        options.setProperty(HTTPConstants.REUSE_HTTP_CLIENT,
                org.apache.axis2.Constants.VALUE_TRUE);
        /*
         * Fix for PXEI-917: Earlier we were setting httpclient in axis2's
         * configContext due to issue of cleanup of idle http thread. Now as
         * com.intalio.bpms.common.MultiThreadedHttpConnectionManagerFactory.
         * idleConnectionTimeoutThread is taking care of that we don't need to
         * set httpClient in configContext of axis2 as it is causing PXEI-917 by
         * overriding the httpclient params.
         */
        options.setProperty(HTTPConstants.CACHED_HTTP_CLIENT, httpClient);
        if (isLocalCall) {
            TransportOutDescription tOut = serviceClient.getAxisConfiguration()
                    .getTransportOut(Constants.TRANSPORT_LOCAL);
            options.setTransportOut(tOut);
        }

        serviceClient.setOptions(options);
    }

    public void closeClient(ServiceClient sc) throws AxisFault {
        if (sc != null) {
            sc.cleanup();
            sc.cleanupTransport();
        }
    }
}
