package com.redhat.qe.kiali.ui.components;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.WebElement;

import com.redhat.qe.kiali.ui.KialiWebDriver;
import com.redhat.qe.kiali.ui.UIAbstract;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Jeeva Kandasamy (jkandasa)
 */

@Slf4j
public class CanvasImage extends UIAbstract {
    private String identifier = "//canvas";

    private static final String SCRIPT = "return arguments[0].toDataURL('image/png').substring(22);";

    public CanvasImage(KialiWebDriver driver) {
        this(driver, null);
    }

    public CanvasImage(KialiWebDriver driver, String identifier) {
        super(driver);
        if (identifier != null) {
            this.identifier = identifier;
        }
    }

    public void imageToDisk(String filename) {
        try {
            WebElement canvas = element(identifier);
            String canvasBase64 = (String) driver.executeScript(SCRIPT, canvas);
            byte[] decodedImg = Base64.getDecoder().decode(canvasBase64);
            File file = new File(filename);
            FileUtils.writeByteArrayToFile(file, decodedImg);
            // FileUtils.copyFile(captureElementOnScreen(canvas), new File(filename));
        } catch (IOException ex) {
            _logger.error("Exception on copy", ex);
            throw new RuntimeException("Exception on copy: " + ex.getMessage());
        }
    }
}
