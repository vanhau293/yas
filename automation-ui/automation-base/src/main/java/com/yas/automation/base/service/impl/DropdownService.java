package com.yas.automation.base.service.impl;

import com.yas.automation.base.service.InputService;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.springframework.stereotype.Component;

@Component
public class DropdownService implements InputService {

    @Override
    public void setValue(WebElement webElement, Object value) {
        Select selectBrand = new Select(webElement);
        selectBrand.selectByVisibleText((String) value);
    }
}
