package com.redhat.qe.kiali.ui.pages;

import com.redhat.qe.kiali.ui.KialiWebDriver;
import com.redhat.qe.kiali.ui.components.Button;
import com.redhat.qe.kiali.ui.components.ButtonGroup;
import com.redhat.qe.kiali.ui.components.CanvasImage;
import com.redhat.qe.kiali.ui.components.Dropdown;
import com.redhat.qe.kiali.ui.enums.RootPageEnum.MAIN_MENU;

/**
 * @author Jeeva Kandasamy (jkandasa)
 */

public class GraphPage extends RootPage {

    private static final String TOOL_BAR = "//*[contains(@class, \"container-fluid\")]//*[contains(@class, \"btn-toolbar\")]";

    private Dropdown dropdownNamespace;
    private ButtonGroup buttonGroupInterval;
    private ButtonGroup buttonGroupStyle;
    private Button buttonRefresh;
    private CanvasImage canvasImage;

    public GraphPage(KialiWebDriver driver) {
        super(driver, MAIN_MENU.GRAPH);
    }

    @Override
    protected void load() {
        super.load();
        dropdownNamespace = new Dropdown(driver, TOOL_BAR + "/*[contains(@class, \"btn-group\")][1]");
        buttonGroupInterval = new ButtonGroup(driver, TOOL_BAR + "/*[contains(@class, \"btn-group\")][2]");
        buttonGroupStyle = new ButtonGroup(driver, TOOL_BAR + "/*[contains(@class, \"btn-group\")][3]");
        buttonRefresh = new Button(driver, "//button[text()=\"Refresh\"]");
        canvasImage = new CanvasImage(driver, "//canvas[3]");
    }

    public Dropdown namespace() {
        return dropdownNamespace;
    }

    public ButtonGroup interval() {
        return buttonGroupInterval;
    }

    public ButtonGroup style() {
        return buttonGroupStyle;
    }

    public Button refresh() {
        return buttonRefresh;
    }

    public CanvasImage canvas() {
        return canvasImage;
    }
}
