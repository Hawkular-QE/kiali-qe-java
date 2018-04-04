package com.redhat.qe.kiali.ui;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Jeeva Kandasamy (jkandasa)
 */

@Slf4j
public class CommonUtils {

    public Integer integerValueOf(String value) {
        if (value != null) {
            return Integer.valueOf(value);
        }
        return null;
    }

    public List<Integer> integerList(List<String> stringList) {
        ArrayList<Integer> integerList = new ArrayList<Integer>();
        for (String item : stringList) {
            integerList.add(Integer.valueOf(item));
        }
        return integerList;
    }

    public String normalizeSpace(String source) {
        return normalizeSpace(source, "");
    }

    public String normalizeSpace(String source, String replacement) {
        return source.replaceAll("\\s+", replacement).trim();
    }

    public void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (Exception ex) {
            _logger.error("Exception,", ex);
        }
    }

}
