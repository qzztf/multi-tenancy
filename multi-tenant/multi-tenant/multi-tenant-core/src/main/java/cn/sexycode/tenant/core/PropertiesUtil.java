package cn.sexycode.tenant.core;

import java.util.Properties;

public class PropertiesUtil {
    public static String getProperty(Properties properties, String key, String defaultValue) {
        String property = properties.getProperty(key);
        if (property == null) {
            property = defaultValue;
        }
        return property;
    }
}
