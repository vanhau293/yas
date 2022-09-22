package com.yas.product.viewmodel;

import com.yas.product.model.attribute.ProductAttributeGroup;

import javax.validation.constraints.NotEmpty;

public record ProductAttributeGroupPostVm(@NotEmpty String name) {

    public ProductAttributeGroup toModel(){
        ProductAttributeGroup productAttributeGroup = new ProductAttributeGroup();
        productAttributeGroup.setName(name);
        return productAttributeGroup;
    }
}
