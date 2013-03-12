package com.intalio.bpms.common;

import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.util.IdleConnectionTimeoutThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiThreadedHttpConnectionManagerFactory {
    private static final Logger LOG = LoggerFactory.getLogger(MultiThreadedHttpConnectionManagerFactory.class);
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
        long idletimeoutinterval = Long.parseLong(HttpConfigProperties.getProperty(HttpConfigProperties.IDLE_CONNECTION_CHECK_INTERVAL, "30000"));
        long idleconnectionTimeOut = Long.parseLong(HttpConfigProperties.getProperty(HttpConfigProperties.IDLE_CONNECTION_TIMEOUT, "30000"));
        int maxConnectionPerHost = Integer.parseInt(HttpConfigProperties.getProperty(HttpConfigProperties.MAX_HOST_CONNECTIONS, "5"));
        int maxTotalConnections = Integer.parseInt(HttpConfigProperties.getProperty(HttpConfigProperties.MAX_TOTAL_CONNECTIONS, "400"));
        int socketTimout = Integer.parseInt(HttpConfigProperties.getProperty(HttpConfigProperties.SOCKET_TIMEOUT, "600000"));

        LOG.debug("idletimeoutinterval is: " + idletimeoutinterval);
        LOG.debug("idleconnectionTimeOut is: " + idleconnectionTimeOut);
        LOG.debug("maxConnectionPerHost is: " + maxConnectionPerHost);
        LOG.debug("maxTotalConnections is: " + maxTotalConnections);
        LOG.debug("socketTimout is: " + socketTimout);

		if (connectionManager == null) {
			synchronized (LOCK) {
				if (connectionManager == null) {
					connectionManager = new MultiThreadedHttpConnectionManager();
					HttpConnectionManagerParams params = new HttpConnectionManagerParams();
					params.setMaxTotalConnections(maxTotalConnections);
					params.setDefaultMaxConnectionsPerHost(maxConnectionPerHost);
					params.setSoTimeout(socketTimout);
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
