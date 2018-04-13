package com.redhat.qe.kiali.ui.components;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebElement;

import com.redhat.qe.kiali.model.services.Health;
import com.redhat.qe.kiali.model.services.Service;
import com.redhat.qe.kiali.ui.KialiWebDriver;

/**
 * @author Jeeva Kandasamy (jkandasa)
 */

public class ListViewService extends ListView<Service> {

    private ComponentsHelper componentsHelper = null;

    public ListViewService(KialiWebDriver driver) {
        this(driver, null);
    }

    public ListViewService(KialiWebDriver driver, String identifier) {
        super(driver, identifier);
        componentsHelper = new ComponentsHelper(driver);
    }

    @Override
    public List<Service> items() {
        ArrayList<Service> items = new ArrayList<Service>();
        for (WebElement el : elements(identifier, ITEMS)) {
            String[] text = element(el, ITEM_TEXT).getText().split("\\n");

            //PodStatus pod = componentsHelper.podStatus(el);
            //Health health = componentsHelper.health(el);

            items.add(Service.builder()
                    .name(text[0])
                    .namespace(text[1])
                    //.health(health) // TODO: issue with tooltip
                    .build());
        }
        return items;
    }

    @Override
    public void open(Service service) {
        open(service.getName(), service.getNamespace());
    }

}
