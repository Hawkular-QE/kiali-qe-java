package com.redhat.qe.kiali.ui.tests.menu;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.redhat.qe.kiali.model.Version;
import com.redhat.qe.kiali.ui.TestAbstract;
import com.redhat.qe.kiali.ui.components.About;
import com.redhat.qe.kiali.ui.enums.RootPageEnum.TOP_RIGHT_MENU;
import com.redhat.qe.kiali.ui.enums.RootPageEnum.VERSION;
import com.redhat.qe.kiali.ui.pages.RootPage;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Jeeva Kandasamy (jkandasa)
 */

@Slf4j
public class TestNavbar extends TestAbstract {

    @BeforeClass
    public void loadObject() {
        rootPage = new RootPage(webDriver());
    }

    /**
     * Test description:
     *   Tests about dialog. this dialog available in navbar
     * Steps:
     *      - open about dialog
     *      - check application name
     *      - check applications versions
     *      - check we have defined version name and version text
     *      - close the about dialog
     */
    @Test
    public void testAbout() {
        // open about dialog
        About about = rootPage.navbar().about();
        // check application name
        Assert.assertEquals(about.applicationName(), "Kiali");
        // check application versions
        // load defined versions
        VERSION[] definedVersions = VERSION.values();
        List<Version> versions = about.versions();
        _logger.debug("Versions:{}", versions);
        Assert.assertEquals(versions.size(), definedVersions.length);
        // check we have defined versions
        for (Version version : versions) {
            Assert.assertNotNull(VERSION.fromText(version.getName()), "Checking version name:" + version.toString());
            Assert.assertTrue(version.getVersion().trim().length() > 0,
                    "Checking version text size: " + version.toString());
        }
        // TODO: check versions via REST API
        // close about dialog
        about.close();
    }

    /**
     * Test description:
     *   Tests top right side menu on navbar
     * Steps:
     *      - get available menu items
     *      - check the count
     *      - check the items available
     */
    @Test
    public void testTopRightSideMenu() {
        // nav bar right top menu options
        TOP_RIGHT_MENU[] menusDefined = TOP_RIGHT_MENU.values();
        // get available menu items
        List<String> options = rootPage.navbar().menuRight().options();
        _logger.debug("Right side menu options:{}", options);
        // check the count
        Assert.assertEquals(options.size(), menusDefined.length);
        // check menu items
        for (TOP_RIGHT_MENU item : menusDefined) {
            Assert.assertTrue(contains(options, item.getText()));
        }
    }
}
