package top.bingchenglin.easyframework.commons.util;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigUtil {
    private static final Logger LOGGER = LogManager.getLogger(ConfigUtil.class);

    private static final String CONFIG_PATH = "config.properties";

    private static Properties configProperties;

    private ConfigUtil() {
        // ignore
    }

    public static String getProperty(String key) {
        String value = getProperties().getProperty(key);
        return value;
    }

    public static String getProperty(String key, String defaultValue) {
        String value = getProperties().getProperty(key, defaultValue);
        return value;
    }

    public static int getInteger(String key) {
        String value = getProperties().getProperty(key);
        return NumberUtils.toInt(value);
    }

    public static int getInteger(String key, int defaultValue) {
        String value = getProperties().getProperty(key);
        return NumberUtils.toInt(value, defaultValue);
    }

    public static long getLong(String key) {
        String value = getProperties().getProperty(key);
        return NumberUtils.toLong(value);
    }

    public static long getLong(String key, int defaultValue) {
        String value = getProperties().getProperty(key);
        return NumberUtils.toLong(value, defaultValue);
    }

    private static Properties getProperties() {
        if (configProperties == null) {
            synchronized (ConfigUtil.class) {
                if (configProperties == null) {
                    configProperties = initProperties(CONFIG_PATH);
                }
            }
        }
        return configProperties;
    }

    public static void initInstance(String configPath) {
        configProperties = initProperties(configPath);
    }

    private static Properties initProperties(String configPath) {
        Properties prop = new Properties();
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream inStream = classLoader.getResourceAsStream(configPath);
            if (inStream == null) {
                throw new FileNotFoundException(configPath + " Not Found!");
            }
            prop.load(inStream);
            LOGGER.info("{} load success.", configPath);
        } catch (IOException e) {
            LOGGER.error("{} load failure!", configPath, e);
        }
        return prop;
    }
}
