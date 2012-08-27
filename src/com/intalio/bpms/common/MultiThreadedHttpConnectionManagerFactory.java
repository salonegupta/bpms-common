package com.intalio.bpms.common;

import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.util.IdleConnectionTimeoutThread;

public class MultiThreadedHttpConnectionManagerFactory {
	private static long idleconnectionTimeOut = 30 * 1000;
	private static long idletimeoutinterval = 30 * 1000;
	private static int maxConnectionPerHost = 5;
	private static int maxTotalConnections = 400;
	private static MultiThreadedHttpConnectionManager connectionManager = null;
	private static Object LOCK = new Object();
	private static IdleConnectionTimeoutThread idleConnectionTimeoutThread = null;

	private MultiThreadedHttpConnectionManagerFactory() {
	}

	public static void shutdown() {
		if (idleConnectionTimeoutThread != null) {
			idleConnectionTimeoutThread.shutdown();
			idleConnectionTimeoutThread = null;
		}
	}

	public static MultiThreadedHttpConnectionManager getInstance() {
        String maxConnectionPerHostAsString = System.getProperty("maxConnectionPerHost");
        if (maxConnectionPerHostAsString != null) {
            maxConnectionPerHost = Integer.parseInt(maxConnectionPerHostAsString);
        }
        String maxTotalConnectionsAsString = System.getProperty("maxTotalConnections");
        if (maxTotalConnectionsAsString != null) {
            maxTotalConnections = Integer.parseInt(maxTotalConnectionsAsString);
        }
		if (connectionManager == null) {
			synchronized (LOCK) {
				if (connectionManager == null) {
					connectionManager = new MultiThreadedHttpConnectionManager();
					HttpConnectionManagerParams params = new HttpConnectionManagerParams();
					params.setMaxTotalConnections(maxTotalConnections);
					params.setDefaultMaxConnectionsPerHost(maxConnectionPerHost);
					// Setting to 10 minutes
					params.setSoTimeout(10 * 60 * 1000);
					connectionManager.setParams(params);
					idleConnectionTimeoutThread = new IdleConnectionTimeoutThread();
					idleConnectionTimeoutThread.setConnectionTimeout(idleconnectionTimeOut);
					idleConnectionTimeoutThread.setTimeoutInterval(idletimeoutinterval);
					idleConnectionTimeoutThread.addConnectionManager(connectionManager);
					connectionManager.setParams(params);
					idleConnectionTimeoutThread.start();
				}
			}
		}
		return connectionManager;
	}
}
