package com.redhat.qe.kiali.ui.components;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.redhat.qe.kiali.ui.KialiWebDriver;
import com.redhat.qe.kiali.ui.UIAbstract;

/**
 * @author Jeeva Kandasamy (jkandasa)
 */

public class Button extends UIAbstract {
    private String identifier = "//button";

    public Button(KialiWebDriver driver) {
        this(driver, null);
    }

    public Button(KialiWebDriver driver, String identifier) {
        super(driver);
        if (identifier != null) {
            this.identifier = identifier;
        }
    }

    public void click() {
        WebElement button = element(identifier);
        button.click();
    }

    public void doubleClick() {
        WebElement button = element(identifier);
        Actions action = new Actions(driver);
        //Double click
        action.doubleClick(button).perform();
    }

    public String text() {
        return element(identifier).getText();
    }
}
