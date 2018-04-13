package com.redhat.qe.kiali.ui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
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

    protected File captureElementOnScreen(WebElement element) {
        _logger.debug("element:{}", element);

        File screen = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

        BufferedImage img;
        try {
            // create an instance of Buffered Image from captured screenshot
            img = ImageIO.read(screen);
        } catch (IOException ex) {
            _logger.error("Exception, ", ex);
            throw new RuntimeException("Error on image capture: " + ex.getMessage());
        }

        int width = element.getSize().getWidth();
        int height = element.getSize().getHeight();

        // get the Location of WebElement in a Point. this will provide X & Y co-ordinates of the WebElement
        Point p = element.getLocation();

        int finalHeight = (p.getY() + height) > img.getHeight() ? img.getHeight() - p.getY() : height;
        int finalWidth = (p.getX() + width) > img.getWidth() ? img.getWidth() - p.getX() : width;

        if (finalHeight != height || finalWidth != width) {
            _logger.warn(
                    "Actual widht or height of the element is out of page! "
                            + "Element {position[x:{}, y:{}], actualSize:[height:{}, width:{}],"
                            + " finalSize:[height:{}, width:{}]}, screen:[height:{}, width:{}]",
                            p.getX(), p.getY(), height, width, finalHeight, finalWidth, img.getHeight(), img.getWidth());
        } else {
            _logger.debug(
                    "Element {position[x:{}, y:{}], actualSize:[height:{}, width:{}],"
                            + " finalSize:[height:{}, width:{}]}, screen:[height:{}, width:{}]",
                            p.getX(), p.getY(), height, width, finalHeight, finalWidth, img.getHeight(), img.getWidth());
        }

        // get specific element image
        BufferedImage finalImage = img.getSubimage(p.getX(), p.getY(), finalWidth, finalHeight);

        try {
            // write back the image data for element in File object
            ImageIO.write(finalImage, "png", screen);
        } catch (IOException ex) {
            _logger.error("Exception, ", ex);
            throw new RuntimeException("Error on image copy: " + ex.getMessage());
        }

        return screen;
    }
}
