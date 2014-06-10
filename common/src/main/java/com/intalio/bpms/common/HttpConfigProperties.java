package com.intalio.bpms.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpConfigProperties {

    private static final Logger LOG = LoggerFactory.getLogger(HttpConfigProperties.class);

    public static final String IDLE_CONNECTION_CHECK_INTERVAL = "http.idle.connection.check.interval";
    public static final String IDLE_CONNECTION_TIMEOUT = "http.idle.connection.timeout";
    public static final String MAX_HOST_CONNECTIONS = "http.connection-manager.max-per-host";
    public static final String MAX_TOTAL_CONNECTIONS = "http.connection-manager.max-total";
    public static final String SOCKET_TIMEOUT = "http.connection-manager.socket.timeout";
    private static final String CONFIG_FILE_NAME = "http-config.properties";
    private static final String CONFIG_DIR = "com.intalio.bpms.configDirectory";
    private static Properties props = new Properties();

    public static String getProperty(String key, String dflt) {
        return props.getProperty(key, dflt);
    }

    /**
     * It will load the properties at the time when class loads.
     */
    static {
        File cfgFile = new File(System.getProperty(CONFIG_DIR) + File.separator + CONFIG_FILE_NAME);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(cfgFile);
            props.load(fis);
        } catch (FileNotFoundException e) {
            LOG.error("config file does not exists: " + e);
        } catch (IOException e) {
            LOG.warn("Exception is: " + e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    LOG.warn("Exception is: " + e);
                }
            }
        }
    }

}
