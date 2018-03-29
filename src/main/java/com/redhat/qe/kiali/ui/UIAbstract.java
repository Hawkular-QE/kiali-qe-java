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

    protected KialiWebDriver driver;

    public UIAbstract(KialiWebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    protected List<String> children(String parentIdentifier, String childIdentifier, Object... arguments) {
        return children(parentIdentifier, format(childIdentifier, arguments));
    }

    protected List<String> children(String parentIdentifier, String childIdentifier) {
        _logger.trace("identifier[parent:{{}}, child:{{}}]", parentIdentifier, childIdentifier);
        WebElement parent = element(parentIdentifier);
        ArrayList<String> items = new ArrayList<String>();
        for (WebElement el : parent.findElements(By.xpath(childIdentifier))) {
            items.add(el.getText());
        }
        return items;
    }

    protected WebElement element(String identifier) {
        _logger.trace("identifier:{{}}", identifier);
        return driver.findElement(By.xpath(identifier));
    }

    protected WebElement element(String parentIdentifier, String childIdentifier, Object... arguments) {
        childIdentifier = format(childIdentifier, arguments);
        _logger.trace("identifier[parent:{{}}, child:{{}}]", parentIdentifier, childIdentifier);
        if (parentIdentifier != null) {
            WebElement parent = element(parentIdentifier);
            return parent.findElement(By.xpath(childIdentifier));
        } else {
            return driver.findElement(By.xpath(childIdentifier));
        }

    }

    protected WebElement element(WebElement parent, String childIdentifier, Object... arguments) {
        childIdentifier = format(childIdentifier, arguments);
        _logger.trace("identifier[parent:{{}}, child:{{}}]", parent.toString(), childIdentifier);
        return parent.findElement(By.xpath(childIdentifier));
    }

    protected List<WebElement> elements(String identifier) {
        _logger.trace("identifier:{{}}", identifier);
        return driver.findElements(By.xpath(identifier));
    }

    protected List<WebElement> elements(String parentIdentifier, String childIdentifier, Object... arguments) {
        childIdentifier = format(childIdentifier, arguments);
        _logger.trace("identifier[parent:{{}}, child:{{}}]", parentIdentifier, childIdentifier);
        WebElement parent = element(parentIdentifier);
        return parent.findElements(By.xpath(childIdentifier));
    }

    protected List<WebElement> elements(WebElement parent, String childIdentifier, Object... arguments) {
        childIdentifier = format(childIdentifier, arguments);
        _logger.trace("identifier[parent:{{}}, child:{{}}]", parent.toString(), childIdentifier);
        return parent.findElements(By.xpath(childIdentifier));
    }

    // helpers
    protected boolean isElementPresent(String identifier, Object... arguments) {
        String parent = null;
        return isElementPresent(parent, identifier, arguments);
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

    protected boolean isElementPresent(WebElement parent, String childIdentifier, Object... arguments) {
        try {
            element(parent, childIdentifier, arguments);
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
        if (arguments != null && childIdentifier.length() > 0) {
            childIdentifier = format(childIdentifier, arguments);
        }
        _logger.trace("identifier[parent:{{}}, child:{{}}], waitTime:{}ms",
                parentIdentifier, childIdentifier, waitTime);
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

    public String format(String pattern, Object... arguments) {
        if (arguments != null && arguments.length > 0) {
            return MessageFormat.format(pattern, arguments);
        } else {
            return pattern;
        }
    }

    protected String getXPath(WebElement el) {
        String jscript = "function getPathTo(node) {" +
                "  var stack = [];" +
                "  while(node.parentNode !== null) {" +
                "    stack.unshift(node.tagName);" +
                "    node = node.parentNode;" +
                "  }" +
                "  return stack.join('/');" +
                "}" +
                "return getPathTo(arguments[0]);";
        return (String) driver.executeScript(jscript, el);
    }
}
