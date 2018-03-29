package com.redhat.qe.kiali.ui.tests.menu;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.redhat.qe.kiali.ui.TestAbstract;
import com.redhat.qe.kiali.ui.enums.RootPageEnum.MAIN_MENU;
import com.redhat.qe.kiali.ui.pages.RootPage;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Jeeva Kandasamy (jkandasa)
 */

@Slf4j
public class TestMenu extends TestAbstract {

    @BeforeClass
    public void loadObject() {
        rootPage = new RootPage(webDriver());
    }

    /**
     * Test description:
     *   Tests number of menus count, names and navigation
     * Steps:
     *      - get menus and check the count
     *      - check the names
     *      - click on menu one by one and check is active
     */
    @Test
    public void testMenu() {
        MAIN_MENU[] menusDefined = MAIN_MENU.values();

        // get menu items
        List<String> menuList = rootPage.menu().items();
        _logger.debug("Menu items:{}", menuList);
        // check the count
        Assert.assertEquals(menuList.size(), menusDefined.length, "Number of menus test");
        // check the names
        for (String item : menuList) {
            Assert.assertNotNull(MAIN_MENU.fromText(item), "Checking menu: " + item);
        }
        // check the navigation
        for (MAIN_MENU item : menusDefined) {
            _logger.debug("Testing menu:[{}]", item.getText());
            rootPage.menu().select(item.getText());
            Assert.assertEquals(item.getText(), rootPage.menu().selected());
        }
    }

    /**
     * Test description:
     *   Tests menu toggle
     * Steps:
     *      - collapse and check the status
     *      - expand and check the status
     */
    @Test
    public void testToggle() {
        // collapse and check the status
        rootPage.menu().collapse();
        Assert.assertTrue(rootPage.menu().isCollapsed());
        // expand and check the status
        rootPage.menu().expand();
        Assert.assertFalse(rootPage.menu().isCollapsed());
    }

}
