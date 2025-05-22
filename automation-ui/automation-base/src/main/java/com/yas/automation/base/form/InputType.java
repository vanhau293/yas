package com.yas.automation.base.form;

import lombok.Getter;

@Getter
public enum InputType {

    TEXT("textService"),
    DROPDOWN("dropdownService"),
    FILE("fileService"),
    CHECKBOX("checkBoxService");

    private final String serviceName;

    InputType(String serviceName) {
        this.serviceName = serviceName;
    }

}
