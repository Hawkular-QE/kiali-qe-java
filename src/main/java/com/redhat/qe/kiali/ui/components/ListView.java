package com.redhat.qe.kiali.ui.components;

import java.util.ArrayList;
import java.util.List;

import com.redhat.qe.kiali.ui.KialiWebDriver;
import com.redhat.qe.kiali.ui.UIAbstract;
import com.redhat.qe.kiali.ui.enums.PaginationEnum.PERPAGE;

/**
 * @author Jeeva Kandasamy (jkandasa)
 */

public abstract class ListView<T> extends UIAbstract {
    protected static final String ITEMS = ".//*[contains(@class, \"list-group-item\")]//*[contains(@class, \"list-view-pf-body\")]";
    protected static final String ITEM_TEXT = ".//*[contains(@class, \"list-group-item-heading\")]";

    protected String SELECT_ITEM = ITEMS + "//span[text()=\"{0}\"]";
    protected String SELECT_ITEM_WITH_NAMESPACE = SELECT_ITEM + "/small[text()=\"{1}\"]";

    protected String identifier = "//*[contains(@class, \"list-view-pf\") and contains(@class, \"list-view-pf-view\")]";

    private Pagination pagination;

    public ListView(KialiWebDriver driver) {
        this(driver, null);
    }

    public ListView(KialiWebDriver driver, String identifier) {
        super(driver);
        if (identifier != null) {
            this.identifier = identifier;
        }
        pagination = new Pagination(driver);
    }

    abstract public List<T> items();

    abstract public void open(T item);

    public void open(String name) {
        open(name, null);
    }

    public void open(String name, String namespace) {
        if (name != null && namespace != null) {
            element(identifier, SELECT_ITEM_WITH_NAMESPACE, name, namespace).click();
        } else if (name != null) {
            element(identifier, SELECT_ITEM, name).click();
        }
    }

    public List<T> allItems() {
        ArrayList<T> items = new ArrayList<T>();
        pagination.moveToFirstPage();
        // set per page to maximum size
        pagination.perPage(PERPAGE.values()[PERPAGE.values().length - 1].getValue());
        for (int page = 1; page <= pagination.pages(); page++) {
            pagination.moveToPage(page);
            items.addAll(items());
        }
        return items;
    }
}
