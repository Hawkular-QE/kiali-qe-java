package com.redhat.qe.kiali.ui;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Jeeva Kandasamy (jkandasa)
 */

@Slf4j
public class KialiWebDriver extends RemoteWebDriver {

    private static final String ENSURE_PAGE_SAFE = "return {"
            + "jquery: (typeof jQuery === \"undefined\") ? true : jQuery.active < 1,"
            + "prototype: (typeof Ajax === \"undefined\") ? true : Ajax.activeRequestCount < 1,"
            + "document: document.readyState == \"complete\""
            + "}";

    private CommonUtils utils = new CommonUtils();

    public KialiWebDriver() {
        super();
    }

    public KialiWebDriver(Capabilities capabilities) {
        super(capabilities);
    }

    public KialiWebDriver(CommandExecutor commandExecutor, Capabilities capabilities) {
        super(commandExecutor, capabilities);
    }

    public KialiWebDriver(URL remoteAddress, Capabilities capabilities) {
        super(remoteAddress, capabilities);
    }

    protected void ensurePageSafe() {
        long timeOut = 1000 * 10;
        while (timeOut > 0) {
            if (isPageReady()) {
                return;
            }
            utils.sleep(200);
            timeOut -= 200;
        }
        _logger.warn("Looks like page not ready for more than {}ms", timeOut);
    }

    public void navigateTo(String relativePath, boolean force) {
        String toUrl = null;
        try {
            toUrl = new URI(this.getCurrentUrl()).resolve(relativePath).toString();
            _logger.debug("Navigate to url[relative:{{}}, to:{{}}, current:{{}}]",
                    relativePath, toUrl, getCurrentUrl());
            if (force || !getCurrentUrl().equals(toUrl)) {
                this.navigate().to(toUrl);
            }
        } catch (URISyntaxException ex) {
            _logger.error("Exception, ", ex);
            throw new RuntimeException("Exception: " + ex.getMessage());
        }
    }

    public void mousehover(WebElement element) {
        Actions builder = new Actions(this);
        builder.moveToElement(element).build().perform();
    }

    @Override
    public WebElement findElement(By by) {
        ensurePageSafe();
        _logger.debug("{}", by.toString());
        return super.findElement(by);
    }

    @Override
    public List<WebElement> findElements(By by) {
        ensurePageSafe();
        _logger.debug("{}", by.toString());
        return super.findElements(by);
    }

    private boolean isPageReady() {
        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) this.executeScript(ENSURE_PAGE_SAFE);
        _logger.trace("ENSURE_PAGE_SAFE: {}", result);
        for (String key : result.keySet()) {
            if (!(Boolean) result.get(key)) {
                return false;
            }
        }
        return true;
    }
}
