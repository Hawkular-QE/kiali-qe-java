package com.redhat.qe.kiali.ui.bugtracker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.redhat.qe.kiali.ui.TestUtils;
import com.redhat.qe.kiali.ui.YamlFactory;
import com.redhat.qe.rest.jira.JiraRestClient;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Jeeva Kandasamy (jkandasa)
 */

@Slf4j
public class BugTracker {
    private static String yamlFile = null;
    private static Map<String, Object> yamlData = null;
    private static Map<String, Blocker> BLOCKERS = null;
    private static JiraRestClient jiraCliet = null;

    private static final String IDENTIFIER_JIRIA = "JR:";
    private static final String KEY_BLOCKERS = "blockers";

    public static Map<String, Blocker> getBlockers() {
        if (BLOCKERS == null) {
            BLOCKERS = new HashMap<String, Blocker>();
            initialize();
        }
        return BLOCKERS;
    }

    @SuppressWarnings("unchecked")
    public static void initialize() {
        if (yamlData == null) {
            try {
                // update yaml file
                yamlFile = TestUtils.getProperty(KEY_BLOCKERS);
                if (yamlFile == null) { // check it on source
                    //Get file from resources folder
                    File file = TestUtils.getFileFromResource("blockers.yaml");
                    if (file != null) {
                        yamlData = YamlFactory.getMap(file);
                    }
                } else {
                    yamlData = YamlFactory.getMap(yamlFile);
                }

                if (yamlData == null) {
                    _logger.warn("blockers yaml file not found! skipping bug status verification");
                    return;
                }

                loadClients();
                // load non-block list
                List<String> jiraNonBlockList = (List<String>) TestUtils.getValue(
                        yamlData, "bug-trackers.jira.non-blocking-list", new ArrayList<String>());
                // load tests
                HashMap<String, Object> tests = (HashMap<String, Object>) TestUtils.getValue(
                        yamlData, "blockers", new HashMap<String, Object>());
                _logger.debug("Tests:{}", tests);
                for (String test : tests.keySet()) {
                    _logger.debug("Test:{}", test);
                    Map<String, String> bugList = new HashMap<String, String>();
                    for (String bugId : (List<String>) tests.get(test)) {
                        // Jira issue
                        if (bugId.startsWith(IDENTIFIER_JIRIA)) {
                            String bugIdFinal = bugId.replaceFirst(IDENTIFIER_JIRIA, "").trim();
                            Map<String, Object> bugData = jiraCliet.issue(bugIdFinal);
                            // get status from "fields.resolution"
                            String currentState = (String) TestUtils.getValue(bugData, "fields.resolution", null);
                            // if "fields.resolution" is null, get it from "fields.status.name" 
                            currentState = (String) TestUtils.getValue(bugData, "fields.status.name", null);
                            if (currentState == null) {
                                currentState = "Unable to get the status";
                            }
                            bugList.put(bugId, currentState.trim().toLowerCase());
                        }
                    }
                    boolean isBlocked = false;
                    for (String bugId : bugList.keySet()) {
                        if (!jiraNonBlockList.contains(bugList.get(bugId))) {
                            isBlocked = true;
                        }
                    }
                    Blocker blocker = Blocker.builder()
                            .name(test)
                            .blocked(isBlocked)
                            .list(bugList)
                            .build();
                    BLOCKERS.put(test, blocker);
                }
                _logger.debug("Blockers:{}", BLOCKERS);
            } catch (IOException ex) {
                _logger.error("Exception, ", ex);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void loadClients() {
        // load Jira rest client
        Map<String, Object> jiraConfig = (Map<String, Object>) TestUtils.getValue(
                yamlData, "bug-trackers.jira", new HashMap<String, Object>());
        _logger.debug("JiraConfig:{}", jiraConfig);
        jiraCliet = new JiraRestClient((String) jiraConfig.get("url"), null, null);
    }

}
