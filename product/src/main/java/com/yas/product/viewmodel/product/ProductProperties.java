package com.yas.product.viewmodel.product;

import java.util.List;

public interface ProductProperties {
    Long id();

    String name();

    String slug();

    String sku();

    String gtin();

    Double price();

    Long thumbnailMediaId();

    List<Long> productImageIds();

}
