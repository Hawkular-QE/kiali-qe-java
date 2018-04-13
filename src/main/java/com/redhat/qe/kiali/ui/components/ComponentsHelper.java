package com.redhat.qe.kiali.ui.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.WebElement;

import com.redhat.qe.kiali.model.services.DeploymentStatus;
import com.redhat.qe.kiali.model.services.Envoy;
import com.redhat.qe.kiali.model.services.Health;
import com.redhat.qe.kiali.model.services.PodStatus;
import com.redhat.qe.kiali.ui.KialiWebDriver;
import com.redhat.qe.kiali.ui.UIAbstract;

/**
 * @author Jeeva Kandasamy (jkandasa)
 */

public class ComponentsHelper extends UIAbstract {
    private static final String STATUS_BASE = ".//strong[normalize-space(text())=\"{0}\"]/..";
    private static final String POD_STATUS = "Pod status:";
    private static final String HEALTH = "Health:";

    private static final String TOOLTIP = "//*[contains(@class, \"tooltip-inner\")]";
    private static final String HEALTH_TOOLTIP = TOOLTIP + "//h4[text()=\"Healthy\"]/../..";

    public ComponentsHelper(KialiWebDriver driver) {
        super(driver);
    }

    public Map<String, String> labels(List<WebElement> labelElements) {
        Map<String, String> labels = new HashMap<String, String>();
        for (WebElement el : labelElements) {
            labels.put(element(el, ".//*[name()=\"g\"]/*[name()=\"text\"][2]").getText(),
                    element(el, ".//*[name()=\"g\"]/*[name()=\"text\"][3]").getText());
        }
        return labels;
    }

    public PodStatus podStatus(WebElement el) {
        String podUpTotalStr = element(el, format(STATUS_BASE, POD_STATUS)).getText();
        podUpTotalStr = normalizeSpace(podUpTotalStr, "").replaceFirst(normalizeSpace(POD_STATUS), "");

        String iconText = getIconText(element(el, format(STATUS_BASE, POD_STATUS) + "/span"));

        String[] podStatus = podUpTotalStr.split("/");
        PodStatus pod = null;
        if (podStatus.length == 2) {
            pod = PodStatus.builder()
                    .availableReplicas(Integer.valueOf(podStatus[0]))
                    .replicas(Integer.valueOf(podStatus[1]))
                    .status(iconText)
                    .build();
        } else {
            pod = PodStatus.builder()
                    .status(iconText)
                    .build();
        }
        return pod;
    }

    public Health health(WebElement el) {
        String text = normalizeSpace(element(el, format(STATUS_BASE, HEALTH)).getText());
        text = text.replaceFirst(normalizeSpace(HEALTH), "").trim();

        Health health = null;

        if (text.equals("N/A")) {
            health = Health.builder()
                    .iconText(text)
                    .build();
        } else {
            WebElement healthIcon = element(el, format(STATUS_BASE, HEALTH) + "//span[contains(@class, \"pficon\")]");

            // icon text from main page
            String iconText = getIconText(healthIcon);
            // icon text from tooltip
            // getIconString(element(el, ".//strong[contains(text(), \"Deployments status:\")]/span"));

            // mousehover on icon
            driver.mousehover(healthIcon);
            // read data from tooltip
            WebElement tooltip = waitForElement(HEALTH_TOOLTIP);
            List<DeploymentStatus> deploymentStatuses = new ArrayList<DeploymentStatus>();
            for (WebElement depStatus : elements(tooltip, ".//ul/li")) {
                deploymentStatuses.add(getDeploymentStatus(depStatus));
            }

            health = Health.builder()
                    .deploymentStatuses(deploymentStatuses)
                    .envoy(getEnvoy(tooltip))
                    .iconText(iconText)
                    .build();
        }

        return health;
    }

    private Envoy getEnvoy(WebElement el) {
        String rawText = el.getText();
        String text = rawText.substring(rawText.indexOf("Envoy health")).replace("Envoy health", "").trim();
        String[] count = text.replace("(", "").replace(")", "").split("/");
        return Envoy.builder()
                .healthy(Integer.valueOf(count[0].trim()))
                .total(Integer.valueOf(count[1].trim()))
                .iconText(getIconText(element(el, ".//strong[contains(text(), \"Envoy health\")]/span")))
                .build();
    }

    private DeploymentStatus getDeploymentStatus(WebElement el) {
        String rawText = normalizeSpace(el.getText());
        String[] deploymentName = rawText.split("\\(.*?\\)");
        String[] count = rawText.replaceAll(deploymentName[0], "").replace("(", "").replace(")", "").split("/");
        return DeploymentStatus.builder()
                .name(deploymentName[0].trim())
                .available(Integer.valueOf(count[0].trim()))
                .replicas(Integer.valueOf(count[1].trim()))
                .iconText(getIconText(element(el, "./span")))
                .build();
    }

    private String getIconText(WebElement iconElement) {
        String iconClass = iconElement.getAttribute("class");
        if (iconClass.contains("pficon-ok")) {
            return "ok";
        } else if (iconClass.contains("pficon-warning-triangle-o")) {
            return "warning";
        } else {
            return iconClass;
        }
    }
}
