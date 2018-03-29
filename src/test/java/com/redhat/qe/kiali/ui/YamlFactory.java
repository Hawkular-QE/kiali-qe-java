package com.redhat.qe.kiali.ui;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.Yaml;

/**
 * @author Jeeva Kandasamy (jkandasa)
 */

public class YamlFactory {

    public static HashMap<String, Object> getMap(String fileName) throws IOException {
        return getMap(FileUtils.getFile(fileName));
    }

    @SuppressWarnings("unchecked")
    public static HashMap<String, Object> getMap(File file) throws IOException {
        String yamlFile = getFileAsString(file);
        final Yaml yaml = new Yaml();
        return (HashMap<String, Object>) yaml.load(yamlFile);
    }

    private static String getFileAsString(File file) throws IOException {
        return FileUtils.readFileToString(file, "UTF-8");
    }
}
