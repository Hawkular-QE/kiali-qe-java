package com.redhat.qe.kiali.ui;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.ITestContext;

import com.redhat.qe.kiali.rest.KialiRestClient;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Jeeva Kandasamy (jkandasa)
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DriverFactory {
    private static AtomicBoolean tearDown = new AtomicBoolean(false);
    private static ConcurrentHashMap<String, Driver> driversMap = new ConcurrentHashMap<String, Driver>();

    public static Driver getDriver(final ITestContext testContext) throws MalformedURLException {
        return getDriver(getDriverName(testContext));
    }

    public static Driver getDriver(String key) throws MalformedURLException {
        if (driversMap.get(key) == null) {
            _logger.debug("Initializing drivers with the key:{{}}", key);
            driversMap.put(key, initialize(key));
        }
        return driversMap.get(key);
    }

    // this method will be called after suite
    public static void tearDownAll() {
        if (!tearDown.get()) {
            for (String key : driversMap.keySet()) {
                driversMap.get(key).tearDown();
            }
        }
    }

    public static void removeDriver(String driverName) {
        driversMap.remove(driverName);
    }

    public static List<String> getKeysAllDriver() {
        ArrayList<String> keys = new ArrayList<String>();
        for (String key : driversMap.keySet()) {
            keys.add(key);
        }
        return keys;
    }

    public static String getDriverName(final ITestContext testContext) {
        String suiteName = testContext.getSuite().getName();
        boolean isParallel = testContext.getSuite().getXmlSuite().getParallel().isParallel();
        int threadCount = testContext.getSuite().getXmlSuite().getThreadCount();
        String testName = testContext.getName();
        // if we have thread count more than 1(Parallel), create driver for each test,
        // otherwise single drivers for entire suite
        if (isParallel && threadCount > 1) {
            return suiteName + "[" + testName + "]";
        } else {
            return suiteName;
        }
    }

    private static Driver initialize(String suiteName) throws MalformedURLException {
        String username = "jdoe";
        String password = "password";
        String hostname = System.getProperty("kialiHostname", "localhost");

        String remoteDriver = System.getProperty("seleniumGrid", "http://localhost:4444/wd/hub");

        _logger.debug("Kiali hostname:[{}]", hostname);
        _logger.debug("Selenium grid:[{}]", remoteDriver);

        // load REST client
        KialiRestClient restClient = new KialiRestClient("http://" + hostname, username, password);

        // get version details
        Map<String, String> status = restClient.status();
        String build = MessageFormat.format("Kiali, console:{0}, core:{1}:{2}",
                status.get("Kiali console version"),
                status.get("Kiali core version"), status.get("Kiali core commit hash"));

        _logger.info("Build: {}", build);

        // load UI driver
        DesiredCapabilities caps = DesiredCapabilities.chrome();

        caps.setCapability("platform", "Linux");

        // saucelabs configurations
        //caps.setCapability("platform", "Windows 7");
        //caps.setCapability("name", "saucelabs demo");
        //caps.setCapability("tunnelIdentifier", "zalenium");

        caps.setCapability("zal:name", suiteName);
        caps.setCapability("zal:tz", "Asia/Kolkata");
        caps.setCapability("zal:screenResolution", "1920x1080");
        caps.setCapability("zal:idleTimeout", "60");
        caps.setCapability("zal:recordVideo", "true");
        caps.setCapability("zal:build", build);

        KialiWebDriver webDriver = new KialiWebDriver(new URL(remoteDriver), caps);

        // launch the application and maximize the screen
        webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        webDriver.get("http://" + username + ":" + password + "@" + hostname);

        webDriver.manage().window().maximize();
        _logger.debug("New selenium driver created. SessionId:[{}]", webDriver.getSessionId());

        return Driver.builder()
                .kialiHostname(hostname)
                .webDriver(webDriver)
                .restClient(restClient)
                .build();
    }
}
