package com.redhat.qe.kiali.ui.components;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebElement;

import com.redhat.qe.kiali.ui.KialiWebDriver;
import com.redhat.qe.kiali.ui.UIAbstract;

/**
 * @author Jeeva Kandasamy (jkandasa)
 */

public class ButtonGroup extends UIAbstract {
    private String identifier = "//*[contains(@class, \"btn-group\")]";

    private static final String BUTTON_NAME = "//button[text()=\"{0}\"]";

    public ButtonGroup(KialiWebDriver driver) {
        this(driver, null);
    }

    public ButtonGroup(KialiWebDriver driver, String identifier) {
        super(driver);
        if (identifier != null) {
            this.identifier = identifier;
        }
    }

    private Button button(String buttonText) {
        return new Button(driver, format(identifier + BUTTON_NAME, buttonText));
    }

    public void select(String text) {
        button(text).click();
    }

    public String selected() {
        return normalizeSpace(element(identifier + "//button[contains(@class, \"active\")]").getText());
    }

    public List<String> options() {
        ArrayList<String> buttons = new ArrayList<String>();
        for (WebElement el : elements(identifier + "//button")) {
            buttons.add(normalizeSpace(el.getText()));
        }
        return buttons;
    }

}
