package com.yas.recommendation.viewmodel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record CategoryVm(
        Long id,
        String name,
        String description,
        String slug,
        String metaKeyword,
        String metaDescription,
        Short displayOrder,
        Boolean isPublished
) {
}
