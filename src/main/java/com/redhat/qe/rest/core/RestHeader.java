package com.redhat.qe.rest.core;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpMessage;

/**
 * @author Jeeva Kandasamy (jkandasa)
 */

public class RestHeader {

    public static RestHeader getDefault() {
        RestHeader header = new RestHeader();
        header.put("User-Agent", "Mozilla/5.0");
        return header;
    }

    private Map<String, Object> map = new HashMap<String, Object>();

    public void addAuthorization(String username, String password) {
        if (username != null) {
            map.put("Authorization",
                    "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes()));
        }
    }

    public void addAuthorization(String token) {
        if (token != null) {
            map.put("Authorization", "Bearer " + token);
        }
    }

    public void addJsonContentType() {
        map.put("Content-Type", "application/json");
    }

    public Object get(String key) {
        return map.get(key);
    }

    public Set<String> keySets() {
        return map.keySet();
    }

    public void put(String key, String value) {
        map.put(key, value);
    }

    public Object remove(String key) {
        return map.remove(key);
    }

    @Override
    public String toString() {
        return map.toString();
    }

    public void updateHeaders(HttpMessage httpMessage) {
        for (String key : map.keySet()) {
            httpMessage.setHeader(key, String.valueOf(map.get(key)));
        }
    }
}