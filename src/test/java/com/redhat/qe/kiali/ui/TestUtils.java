package com.redhat.qe.kiali.ui;

import java.io.File;
import java.util.Map;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Jeeva Kandasamy (jkandasa)
 */

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestUtils {

    @SuppressWarnings("unchecked")
    public static Object getValue(Map<String, Object> source, String keyString, Object defaultValue) {
        String[] keys = keyString.split("\\.");
        Object value = null;
        Map<String, Object> _source = source;
        int keysSize = keys.length;
        while (keysSize > 0) {
            if (keysSize == 1) {
                value = _source.get(keys[keys.length - keysSize]);
                break;
            } else {
                _source = (Map<String, Object>) _source.get(keys[keys.length - keysSize]);
                keysSize--;
            }
            if (_source == null) {
                break;
            }
        }
        if (value != null) {
            return value;
        }
        return defaultValue;
    }

    public static File getFileFromResource(String filename) {
        try {
            //Get file from resources folder
            ClassLoader classLoader = ClassLoader.getSystemClassLoader();
            return new File(classLoader.getResource(filename).getFile());
        } catch (Exception ex) {
            _logger.error("Exception on file[{}] query from resource ", filename, ex);
            return null;
        }
    }

    public static String getProperty(String key) {
        return getProperty(key, null);
    }

    public static String getProperty(String key, String defaultValue) {
        // check it on properties
        String value = System.getProperty(key);
        // check it on env variable
        if (value == null) {
            value = System.getenv(key);
        }
        return value != null ? value : defaultValue;
    }
}
