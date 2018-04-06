package com.redhat.qe.kiali.ui.components;

import com.redhat.qe.kiali.ui.KialiWebDriver;
import com.redhat.qe.kiali.ui.UIAbstract;

/**
 * @author Jeeva Kandasamy (jkandasa)
 */

public class Sort extends UIAbstract {
    private static final String ORDER_BY_ASC = ".//*[contains(@class, \"sort-direction\") and contains(@class, \"-asc\")]";
    private static final String ORDER_BY_DESC = ".//*[contains(@class, \"sort-direction\") and contains(@class, \"-desc\")]";

    private String identifier = "//*[contains(@class, \"form-group\")]/button/*[contains(@class, \"sort-direction\")]/..";

    public Sort(KialiWebDriver driver) {
        this(driver, null);
    }

    public Sort(KialiWebDriver driver, String identifier) {
        super(driver);
        if (identifier != null) {
            this.identifier = identifier;
        }
    }

    public void ascending() {
        orderBy(true);
    }

    public void descending() {
        orderBy(false);
    }

    public boolean isAscending() {
        try {
            element(identifier, ORDER_BY_ASC);
            return true;
        } catch (Exception ex) {
            element(identifier, ORDER_BY_DESC);
            return false;
        }
    }

    private void orderBy(boolean ascensing) {
        if (isAscending() != ascensing) {
            element(identifier).click();
        }
    }

}
