package com.redhat.qe.kiali.ui;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Jeeva Kandasamy (jkandasa)
 */

@Slf4j
public abstract class UIAbstract extends CommonUtils {

    private static final long WAIT_TIME_DEFAULT = 1000 * 5;

    protected SwsDriverUI driver;

    public UIAbstract(SwsDriverUI driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    protected List<String> children(String parentIdentifier, String childIdentifier) {
        WebElement parent = element(parentIdentifier);
        ArrayList<String> menus = new ArrayList<String>();
        for (WebElement el : parent.findElements(By.xpath(childIdentifier))) {
            menus.add(el.getText());
        }
        return menus;
    }

    protected WebElement element(String identifier) {
        return driver.findElement(By.xpath(identifier));
    }

    protected WebElement element(String identifier, Object... arguments) {
        _logger.debug("identifier:{{}}, arguments:{}", identifier, arguments);
        return driver.findElement(By.xpath(MessageFormat.format(identifier, arguments)));
    }

    protected WebElement element(String parentIdentifier, String childIdentifier, Object... arguments) {
        _logger.debug("identifier[parent:{{}}, child:{{}}], arguments:{}",
                parentIdentifier, childIdentifier, arguments);
        if (parentIdentifier != null) {
            WebElement parent = element(parentIdentifier);
            return parent.findElement(By.xpath(MessageFormat.format(childIdentifier, arguments)));
        } else {
            return driver.findElement(By.xpath(MessageFormat.format(childIdentifier, arguments)));
        }

    }

    protected WebElement element(WebElement parent, String childIdentifier, Object... arguments) {
        _logger.debug("identifier[parent:{{}}, child:{{}}], arguments:{}",
                parent.toString(), childIdentifier, arguments);
        return parent.findElement(By.xpath(MessageFormat.format(childIdentifier, arguments)));
    }

    protected List<WebElement> elements(String identifier) {
        return driver.findElements(By.xpath(identifier));
    }

    protected List<WebElement> elements(String identifier, Object... arguments) {
        _logger.debug("identifier:{{}}, arguments:{}", identifier, arguments);
        return driver.findElements(By.xpath(MessageFormat.format(identifier, arguments)));
    }

    protected List<WebElement> elements(String parentIdentifier, String childIdentifier, Object... arguments) {
        _logger.debug("identifier[parent:{{}}, child:{{}}], arguments:{}",
                parentIdentifier, childIdentifier, arguments);
        WebElement parent = element(parentIdentifier);
        return parent.findElements(By.xpath(MessageFormat.format(childIdentifier, arguments)));
    }

    // helpers
    protected boolean isElementPresent(String identifier, Object... arguments) {
        return isElementPresent(null, identifier, arguments);
    }

    protected boolean isElementPresent(String parentIdentifier, String childIdentifier, Object... arguments) {
        try {
            element(parentIdentifier, childIdentifier, arguments);
            return true;
        } catch (Exception ex) {
            _logger.trace("Exception,", ex);
            return false;
        }
    }

    protected WebElement waitForElement(String identifier, long waitTime) {
        return waitForElement(identifier, waitTime);
    }

    protected WebElement waitForElement(String identifier, long waitTime, Object... arguments) {
        return waitForElement(null, identifier, waitTime, arguments);
    }

    protected WebElement waitForElement(String identifier, Object... arguments) {
        return waitForElement(identifier, WAIT_TIME_DEFAULT, arguments);
    }

    protected WebElement waitForElement(String parentIdentifier, String childIdentifier, long waitTime,
            Object... arguments) {
        _logger.debug("identifier[parent:{{}}, child:{{}}], arguments:{}, waitTime:{}ms",
                parentIdentifier, childIdentifier, arguments, waitTime);
        if (arguments != null && childIdentifier.length() > 0) {
            childIdentifier = MessageFormat.format(childIdentifier, arguments);
        }
        WebElement parent = null;
        if (parentIdentifier != null) {
            parent = element(parentIdentifier);
        }
        while (waitTime > 0) {
            try {
                sleep(200);
                waitTime -= 200;
                if (parent != null) {
                    return parent.findElement(By.xpath(childIdentifier));
                } else {
                    return driver.findElement(By.xpath(childIdentifier));
                }
            } catch (Exception ex) {
                _logger.trace("Exception,", ex);
            }
        }
        throw new RuntimeException("Element not found! identifier[parent:{" + parentIdentifier + "}, child:{"
                + childIdentifier + "}]");
    }

    protected WebElement waitForElement(String parentIdentifier, String childIdentifier, Object... arguments) {
        return waitForElement(parentIdentifier, childIdentifier, WAIT_TIME_DEFAULT, arguments);
    }
}
