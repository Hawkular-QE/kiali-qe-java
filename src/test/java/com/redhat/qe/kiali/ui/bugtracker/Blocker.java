package com.redhat.qe.kiali.ui.bugtracker;

import java.util.HashMap;
import java.util.Map;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * @author Jeeva Kandasamy (jkandasa)
 */

@Data
@Builder
@ToString
public class Blocker {
    private String name;
    private Map<String, String> list;
    private boolean blocked;

    public Map<String, String> getList() {
        if (list == null) {
            list = new HashMap<String, String>();
        }
        return list;
    }
}
