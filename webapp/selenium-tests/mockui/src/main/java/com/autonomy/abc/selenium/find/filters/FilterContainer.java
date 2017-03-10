package com.autonomy.abc.selenium.find.filters;

import com.hp.autonomy.frontend.selenium.element.ChevronContainer;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public abstract class FilterContainer extends ChevronContainer{

    private final WebElement container;

    FilterContainer(final WebElement element, final WebDriver webDriver) {
        super(element,webDriver);
        container = element;
    }

    public WebElement getContainer(){
        return container;
    }

    public String filterCategoryName(){
        return filterCategory().getText();
    }

    protected WebElement filterCategory(){
        return container.findElement(By.cssSelector(".collapsible-header h4 span"));
    }

    public String toString(){
        return filterCategoryName();
    }
}
