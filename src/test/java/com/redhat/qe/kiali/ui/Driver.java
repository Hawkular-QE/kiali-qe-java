package com.redhat.qe.kiali.ui;

import com.redhat.qe.kiali.rest.KialiRestClient;

import lombok.Builder;
import lombok.Getter;

/**
 * @author Jeeva Kandasamy (jkandasa)
 */
@Getter
@Builder
public class Driver {
    private String kialiHostname;
    private KialiWebDriver webDriver;
    private KialiRestClient restClient;

    public void tearDown() {
        if (webDriver != null) {
            webDriver.quit();
        }
    }
}
