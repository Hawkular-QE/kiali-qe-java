package com.redhat.qe.kiali.ui.pages;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebElement;

import com.redhat.qe.kiali.model.rules.Handler;
import com.redhat.qe.kiali.model.rules.Instance;
import com.redhat.qe.kiali.ui.KialiWebDriver;

/**
 * @author Jeeva Kandasamy (jkandasa)
 */

public class IstioMixerRuleDetailPage extends RootPage {

    private static final String RULE_NAME = "//*[contains(@class, \"card-pf-title\")]//*[contains(@class, \"pficon-migration\")]/..";
    private static final String BASE = "//*[contains(@class, \"card-pf-body\")]//*[contains(@class, \"progress-description\")]/strong[normalize-space(text())=\"{0}\"]";

    private static final String URI = "/console/namespaces/{0}/rules/{1}";
    private String namespace;
    private String ruleName;

    public IstioMixerRuleDetailPage(KialiWebDriver driver, String namespace, String ruleName) {
        super(driver);
        this.namespace = namespace;
        this.ruleName = ruleName;
        load();
    }

    @Override
    public void reload() {
        super.reload();
    }

    @Override
    protected void load() {
        driver.navigateTo(format(URI, namespace, ruleName), false);
        super.load();
    }

    private String[] actionData(WebElement el) {
        String nameHTML = el.getAttribute("innerHTML");
        // input => <strong>Handler</strong>: handler<br><strong>Adapter</strong>: prometheus
        String[] nameArr = nameHTML.split("<br>");
        String name1 = null;
        String name2 = null;
        if (nameArr.length == 2) {
            // to remove only tags, this one => <([^>]+)>
            // input[<strong>Handler</strong>: handler], output[handler] => <([^*]+)>:
            name1 = nameArr[0].replaceAll("<([^*]+)>:", "").trim();
            name2 = nameArr[1].replaceAll("<([^*]+)>:", "").trim();
        }
        el = element(el, "./../..//textarea");
        String spec = el.getText();

        String[] data = new String[3];

        data[0] = name1;
        data[1] = name2;
        data[2] = spec;
        return data;
    }

    public String ruleName() {
        return element(RULE_NAME).getText();
    }

    public String match() {
        return element(format(BASE + "/../../div[2]", "Match")).getText().trim();
    }

    public Handler handler() {
        String[] data = actionData(element(format(BASE + "/..", "Handler")));
        return Handler.builder()
                .name(data[0])
                .adapter(data[1])
                .spec(data[2])
                .build();
    }

    public List<Instance> instances() {
        List<Instance> instances = new ArrayList<Instance>();
        List<WebElement> els = elements(format(BASE + "/..", "Instance"));
        for (WebElement el : els) {
            String[] data = actionData(el);
            instances.add(Instance.builder()
                    .name(data[0])
                    .template(data[1])
                    .spec(data[2])
                    .build());
        }
        return instances;
    }

    public Instance instance(String instance) {
        List<WebElement> els = elements(format(BASE + "/..", "Instance"));
        for (WebElement el : els) {
            String[] data = actionData(el);
            if (data[0].equals(instance)) {
                return Instance.builder()
                        .name(data[0])
                        .template(data[1])
                        .spec(data[2])
                        .build();
            }
        }
        return null;
    }

}
