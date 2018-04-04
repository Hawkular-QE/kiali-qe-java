package com.redhat.qe.bugtracker;

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
    private Map<String, String> bugs;

    public Map<String, String> getBugs() {
        if (bugs == null) {
            bugs = new HashMap<String, String>();
        }
        return bugs;
    }

    public boolean isBlocked() {
        for (String bugId : getBugs().keySet()) {
            if (bugId.startsWith(BugTrackerFactory.IDENTIFIER_JIRIA)) {
                if (!BugTrackerFactory.getNonBlockingList(BugTrackerFactory.IDENTIFIER_JIRIA).contains(
                        getBugs().get(bugId))) {
                    return true;
                }
            }
        }
        return false;
    }

}
