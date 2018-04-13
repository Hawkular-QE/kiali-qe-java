package com.redhat.qe.kiali.ui.listeners;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.OutputType;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import com.redhat.qe.bugtracker.Blocker;
import com.redhat.qe.bugtracker.BugTrackerFactory;
import com.redhat.qe.kiali.ui.Driver;
import com.redhat.qe.kiali.ui.DriverFactory;
import com.redhat.qe.kiali.ui.TestCount;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Jeeva Kandasamy (jkandasa)
 */

@Slf4j
public class KialiTestListener extends TestListenerAdapter implements ISuiteListener {
    private static final String ZALENIUM_MESSAGE = "zaleniumMessage";
    private static final String ZALENIUM_TEST_STATUS = "zaleniumTestPassed";

    private static final ConcurrentHashMap<String, TestCount> TEST_COUNTS = new ConcurrentHashMap<String, TestCount>();
    private static final TestCount SUITE_COUNT = new TestCount();

    private enum STATUS {
        SUCCESS,
        FAILURES,
        SKIPPED,
        START_TIME,
        END_TIME;
    }

    private String getTestName(ITestContext testContext) {
        //String suiteName = testContext.getSuite().getName();
        String testName = testContext.getName();
        //return suiteName + "[" + testName + "]";
        return testName;
    }

    private String getMethodName(ITestResult result) {
        return result.getTestName() + "." + result.getName();
    }

    private void UpdateTestCount(ITestContext testContext, STATUS status) {
        updateTestCount(testContext, status, null);
    }

    private void updateTestCount(ITestContext testContext, STATUS status, Long timestamp) {
        String name = getTestName(testContext);
        if (TEST_COUNTS.get(name) == null) {
            TEST_COUNTS.put(name, new TestCount());
        }
        TestCount testCount = TEST_COUNTS.get(name);
        switch (status) {
            case FAILURES:
                testCount.incrementFailures();
                break;
            case SKIPPED:
                testCount.incrementSkipped();
                break;
            case SUCCESS:
                testCount.incrementSuccess();
                break;
            case START_TIME:
                testCount.setStartTime(timestamp);
                break;
            case END_TIME:
                testCount.setEndTime(timestamp);
                break;
            default:
                break;
        }
    }

    private Cookie cookie(String key, String value) {
        return new Cookie(key, value);
    }

    private void updateCookieAllDrivers(String key, String value) {
        for (String driverName : DriverFactory.getKeysAllDriver()) {
            try {
                DriverFactory.getDriver(driverName).getWebDriver().manage().addCookie(cookie(key, value));
            } catch (Exception ex) {
                _logger.error("Exception, driverName:[{}]", driverName, ex);
            }
        }
    }

    // suite listeners

    @Override
    public void onStart(ISuite suite) {
        SUITE_COUNT.setStartTime(System.currentTimeMillis());
        //DriverFactory.driverUI().manage().addCookie(cookie(ZALENIUM_MESSAGE, "[S] Start: " + suite.getName()));
        _logger.info("*** START[S]- {}", suite.getName());
        _logger.debug("*** Parallel run config[isParallelRunEnabled:{}, threadCount: {}]",
                suite.getXmlSuite().getParallel().isParallel(), suite.getXmlSuite().getThreadCount());
    }

    @Override
    public void onFinish(ISuite suite) {
        SUITE_COUNT.setEndTime(System.currentTimeMillis());
        updateCookieAllDrivers(ZALENIUM_MESSAGE, "[S] End: " + suite.getName());
        _logger.info("*** END[S]- {}", suite.getName());
        StringBuilder builder = new StringBuilder();
        builder.append("\n\n************** SUITE SUMMARY REPORT **************************");
        builder.append("\n").append(suite.getName()).append(": {").append(SUITE_COUNT).append("}");
        builder.append("\n\n******************* TESTS SUMMARY ****************************");
        for (String key : TEST_COUNTS.keySet()) {
            builder.append("\n").append(key).append(": {").append(TEST_COUNTS.get(key)).append("}");
        }
        if (SUITE_COUNT.getTotal() != SUITE_COUNT.getSuccess()) {
            updateCookieAllDrivers(ZALENIUM_TEST_STATUS, "false");
        } else {
            updateCookieAllDrivers(ZALENIUM_TEST_STATUS, "true");
        }
        // call tearDown tasks
        DriverFactory.tearDownAll();

        // known issues and current status
        StringBuilder knownIssues = new StringBuilder();
        for (String key : BugTrackerFactory.getBlockers().keySet()) {
            Blocker blocker = BugTrackerFactory.getBlockers().get(key);
            if (blocker.isBlocked()) {
                if (knownIssues.length() > 0) {
                    knownIssues.append("\n");
                }
                knownIssues.append("\nName: ").append(blocker.getName());
                knownIssues.append("\n--------------------------------------------------------------");
                for (String issueId : blocker.getBugs().keySet()) {
                    knownIssues.append("\n").append(issueId).append(": ").append(blocker.getBugs().get(issueId));
                }
            }
        }
        if (knownIssues.length() > 0) {
            builder.append("\n\n************* BLOCKING ISSUES STATUS *************************");
            builder.append(knownIssues.toString());
        }
        builder.append("\n*************************** END ******************************\n");
        _logger.info("*** Summary report:{}", builder.toString());
    }

    // test listeners

    @Override
    public void onStart(ITestContext context) {
        _logger.info("*** START[T] - {}", context.getName());
        updateTestCount(context, STATUS.START_TIME, System.currentTimeMillis());
        try {
            DriverFactory.getDriver(context).getWebDriver().manage()
            .addCookie(cookie(ZALENIUM_MESSAGE, "[T] Start: " + context.getName()));
        } catch (Exception ex) {
            _logger.error("Exception, ", ex);
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        _logger.info("*** END[T] - {}", context.getName());
        updateTestCount(context, STATUS.END_TIME, System.currentTimeMillis());
        TestCount testCount = TEST_COUNTS.get(getTestName(context));
        _logger.info("*** Test run:{name:[{}], {}}\n", context.getName(), testCount.toString());
        try {
            String driverName = DriverFactory.getDriverName(context);
            Driver driver = DriverFactory.getDriver(driverName);
            driver.getWebDriver().manage().addCookie(cookie(ZALENIUM_MESSAGE, "[T] End: " + context.getName()));
            // if thread count is greater than 1, works on parrel run
            if (context.getSuite().getXmlSuite().getParallel().isParallel()
                    && context.getSuite().getXmlSuite().getThreadCount() > 1) {
                if (testCount.getTotal() == testCount.getSuccess()) {
                    driver.getWebDriver().manage().addCookie(cookie(ZALENIUM_TEST_STATUS, "true"));
                } else {
                    driver.getWebDriver().manage().addCookie(cookie(ZALENIUM_TEST_STATUS, "false"));
                }
                driver.tearDown();
                DriverFactory.removeDriver(driverName);
            }
        } catch (Exception ex) {
            _logger.error("Exception, ", ex);
        }
    }

    // method listeners

    @Override
    public void onTestStart(ITestResult result) {
        _logger.info("*** START[M] - {}", getMethodName(result));
        try {
            DriverFactory.getDriver(result.getTestContext()).getWebDriver().manage()
            .addCookie(cookie(ZALENIUM_MESSAGE, "[M] Start: " + result.getName()));
        } catch (Exception ex) {
            _logger.error("Exception, ", ex);
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        SUITE_COUNT.incrementSuccess();
        UpdateTestCount(result.getTestContext(), STATUS.SUCCESS);
        _logger.info("*** SUCCESS[M] - {}\n", getMethodName(result));
        try {
            DriverFactory.getDriver(result.getTestContext()).getWebDriver().manage()
            .addCookie(cookie(ZALENIUM_MESSAGE, "[M] Success: " + result.getName()));
        } catch (Exception ex) {
            _logger.error("Exception, ", ex);
        }
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
    }

    @Override
    public void onTestFailure(ITestResult result) {
        SUITE_COUNT.incrementFailures();
        UpdateTestCount(result.getTestContext(), STATUS.FAILURES);
        _logger.info("*** FAILED[M] - {}\n", getMethodName(result));
        // report to video test failed
        try {
            DriverFactory.getDriver(result.getTestContext()).getWebDriver().manage()
            .addCookie(cookie(ZALENIUM_MESSAGE, "[M] Failed: " + result.getName()));
        } catch (Exception ex) {
            _logger.error("Exception, ", ex);
        }

        // take screenshot
        // TODO: add code to take screenshot with reports
        try {
            Driver driver = DriverFactory.getDriver(result.getTestContext());
            File screenshot = driver.getWebDriver().getScreenshotAs(OutputType.FILE);
            // reload browser in failure
            driver.getWebDriver().navigate().refresh();
            // store the file on disk
            String distinationName = "/tmp/selenium/sl_" + result.getName() + "_" + System.currentTimeMillis()
                    + ".png";
            FileUtils.copyFile(screenshot, new File(distinationName));
        } catch (Exception ex) {
            _logger.error("Exception,", ex);
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        SUITE_COUNT.incrementSkipped();
        UpdateTestCount(result.getTestContext(), STATUS.SKIPPED);
        _logger.info("*** SKIPPED[M] - {}\n", getMethodName(result));
        try {
            DriverFactory.getDriver(result.getTestContext()).getWebDriver().manage()
            .addCookie(cookie(ZALENIUM_MESSAGE, "[M] Skipped: " + result.getName()));
        } catch (Exception ex) {
            _logger.error("Exception, ", ex);
        }
    }

}
