package com.redhat.qe.bugtracker;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.redhat.qe.kiali.KialiUtils;
import com.redhat.qe.rest.jira.JiraClient;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Jeeva Kandasamy (jkandasa)
 */

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BugTrackerFactory {
    private static final String KEY_BLOCKERS = "blockers";
    public static final String IDENTIFIER_JIRIA = "JR:";

    // yaml data
    private static String yamlFile = null;
    private static Map<String, Object> yamlData = null;
    public static final AtomicBoolean INITIALIZE_SUCCESS = new AtomicBoolean(false);

    // issue tracker tools REST client
    private static JiraClient jiraCliet = null;

    // bugs map
    private static Map<String, Blocker> BLOCKERS = new HashMap<String, Blocker>();
    private static Map<String, List<String>> NON_BLOCKING_LIST = new HashMap<String, List<String>>();
    private static Map<String, String> BUGS = new HashMap<String, String>();

    // update query parameters for JIRA rest query
    @SuppressWarnings("serial")
    private static Map<String, Object> jiraQueryParms = new HashMap<String, Object>() {
        {
            put("fields", "resolution,status");
        }
    };

    @SuppressWarnings("unchecked")
    public static void initialize() {
        if (yamlData == null && !INITIALIZE_SUCCESS.get()) {
            try {
                // update yaml file
                yamlFile = KialiUtils.getProperty(KEY_BLOCKERS);
                if (yamlFile == null) { // check it on source
                    //Get file from resources folder
                    File file = KialiUtils.getFileFromResource("blockers.yaml");
                    if (file != null) {
                        yamlData = YamlFactory.getMap(file);
                    }
                } else {
                    yamlData = YamlFactory.getMap(yamlFile);
                }

                if (yamlData == null) {
                    _logger.warn("blockers yaml file not found! Blocker feature will be disabled!");
                    return;
                }

                loadClients();
                // load non-block list
                List<String> nonBlockingListJira = (List<String>) KialiUtils.getValue(
                        yamlData, "bug-trackers.jira.non-blocking-list", new ArrayList<String>());
                NON_BLOCKING_LIST.put(IDENTIFIER_JIRIA, normalizeList(nonBlockingListJira));
                // load tests
                HashMap<String, Object> tests = (HashMap<String, Object>) KialiUtils.getValue(
                        yamlData, "blockers", new HashMap<String, Object>());
                _logger.debug("Tests:{}", tests);
                for (String test : tests.keySet()) {
                    _logger.debug("Test:{}", test);
                    Map<String, String> bugsList = new HashMap<String, String>();
                    for (String bugId : (List<String>) tests.get(test)) {
                        if (BUGS.get(bugId) != null) {
                            bugsList.put(bugId, BUGS.get(bugId));
                        } else {
                            String currentStatus = currentStatus(bugId);
                            bugsList.put(bugId, currentStatus);
                            BUGS.put(bugId, currentStatus);
                        }
                    }
                    Blocker blocker = Blocker.builder()
                            .name(test)
                            .bugs(bugsList)
                            .build();
                    BLOCKERS.put(test, blocker);
                }
                INITIALIZE_SUCCESS.set(true);
                _logger.info("successfully initialized.");
                _logger.debug("Blockers:{}", BLOCKERS);
            } catch (Exception ex) {
                _logger.error("Exception, ", ex);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void loadClients() {
        // load Jira rest client
        Map<String, Object> jiraConfig = (Map<String, Object>) KialiUtils.getValue(
                yamlData, "bug-trackers.jira", new HashMap<String, Object>());
        _logger.debug("JiraConfig:{}", jiraConfig);
        jiraCliet = new JiraClient((String) jiraConfig.get("url"), null, null);
    }

    private static String currentStatus(String bugId) {
        String currentState = null;
        // Jira issue
        if (bugId.startsWith(IDENTIFIER_JIRIA)) {
            String bugIdFinal = bugId.replaceFirst(IDENTIFIER_JIRIA, "").trim();
            Map<String, Object> bugData = jiraCliet.issue(bugIdFinal, jiraQueryParms);
            // get status from "fields.resolution.name"
            currentState = (String) KialiUtils.getValue(bugData, "fields.resolution.name");
            // if "fields.resolution" is null, get it from "fields.status.name" 
            if (currentState == null) {
                currentState = (String) KialiUtils.getValue(bugData, "fields.status.name");
            }
            currentState = normalizeString(currentState);
            if (currentState == null) {
                @SuppressWarnings("unchecked")
                List<String> errorMessage = (List<String>) KialiUtils.getValue(bugData, "errorMessages");
                if (errorMessage != null && !errorMessage.isEmpty()) {
                    currentState = errorMessage.toString();
                } else {
                    currentState = "Unable to get the status";
                }
            }
        }
        return currentState;
    }

    private static List<String> normalizeList(List<String> list) {
        List<String> newList = new ArrayList<String>();
        for (String data : list) {
            newList.add(normalizeString(data));
        }
        return newList;
    }

    private static String normalizeString(String source) {
        if (source != null) {
            return KialiUtils.normalizeSpace(source, "_").toLowerCase();
        }
        return source;
    }

    public static List<String> getNonBlockingList(String identifier) {
        if (NON_BLOCKING_LIST.get(identifier) == null) {
            NON_BLOCKING_LIST.put(identifier, new ArrayList<String>());
        }
        return NON_BLOCKING_LIST.get(identifier);
    }

    public static Map<String, Blocker> getBlockers() {
        return BLOCKERS;
    }

    public static void updateBlocker(String reference, String[] bugs) {
        if (getBlockers().get(reference) == null) {
            getBlockers().put(reference, Blocker.builder()
                    .name(reference)
                    .build());
        }
        Blocker blocker = getBlockers().get(reference);
        for (String bugId : bugs) {
            if (BUGS.get(bugId) == null) {
                BUGS.put(bugId, currentStatus(bugId));
            }
            blocker.getBugs().put(bugId, BUGS.get(bugId));
        }
    }

}
