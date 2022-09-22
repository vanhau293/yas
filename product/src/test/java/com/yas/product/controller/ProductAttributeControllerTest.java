package com.yas.product.controller;

import com.yas.product.exception.BadRequestException;
import com.yas.product.exception.NotFoundException;
import com.yas.product.model.attribute.ProductAttribute;
import com.yas.product.model.attribute.ProductAttributeGroup;
import com.yas.product.repository.ProductAttributeGroupRepository;
import com.yas.product.repository.ProductAttributeRepository;
import com.yas.product.viewmodel.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

public class ProductAttributeControllerTest {

    ProductAttributeRepository productAttributeRepository;
    ProductAttributeGroupRepository productAttributeGroupRepository;
    ProductAttributeController productAttributeController;
    UriComponentsBuilder uriComponentsBuilder;
    Principal principal;
    ProductAttribute productAttribute = new ProductAttribute();

    ProductAttributeGroup productAttributeGroup = new ProductAttributeGroup();

    @BeforeEach
    void setUp(){
        productAttributeRepository = mock(ProductAttributeRepository.class);
        productAttributeGroupRepository = mock(ProductAttributeGroupRepository.class);
        uriComponentsBuilder = mock(UriComponentsBuilder.class);
        principal = mock(Principal.class);
        productAttributeController = new ProductAttributeController(productAttributeRepository, productAttributeGroupRepository);
        productAttributeGroup.setId(1L);
        productAttributeGroup.setName("Computer");
        productAttribute.setId(1L);
        productAttribute.setName("Ram");
        productAttribute.setProductAttributeGroup(productAttributeGroup);
    }

    @Test
    void listProductAttributes_ValidListProductAttributeGetVm_Success(){
        List<ProductAttribute> listProductAttribute = List.of(productAttribute);
        when(productAttributeRepository.findAll()).thenReturn(listProductAttribute);
        ResponseEntity<List<ProductAttributeGetVm>> result = productAttributeController.listProductAttributes();
        assertThat(result.getStatusCode(),is(HttpStatus.OK));
        assertEquals(Objects.requireNonNull(result.getBody()).size(), listProductAttribute.size());
        for(int i=0;i<listProductAttribute.size();i++){
            assertEquals(result.getBody().get(i).id(), listProductAttribute.get(i).getId());
            assertEquals(result.getBody().get(i).name(), listProductAttribute.get(i).getName());
        }
    }

    @Test
    void getProductAttribute_FinProductAttributeById_ThrowException(){
        when(productAttributeRepository.findById(1L)).thenReturn(Optional.empty());
        NotFoundException notFoundException =  Assertions.assertThrows(NotFoundException.class,
                () -> productAttributeController.getProductAttribute(1L));
        assertThat(notFoundException.getMessage(),is("Product attribute 1 is not found"));
    }

    @Test
    void getProductAttribute_FindProductAttribute_Success(){
        when(productAttributeRepository.findById(1L)).thenReturn(Optional.of(productAttribute));
        ResponseEntity<ProductAttributeGetVm> result = productAttributeController.getProductAttribute(1L);
        assertEquals(Objects.requireNonNull(result.getBody()).name(), productAttribute.getName());
        assertEquals(result.getBody().id(), productAttribute.getId());
        assertEquals(result.getBody().id(), productAttribute.getId());
        assertEquals(result.getBody().productAttributeGroup(), productAttribute.getProductAttributeGroup().getName());
    }

    @Test
    void createProductAttribute_FindIdProductAttributeGroup_ThrowException(){
        ProductAttributePostVm productAttributePostVm = new ProductAttributePostVm("Ram",1L);
        when(productAttributeGroupRepository.findById(productAttributePostVm.productAttributeGroupId())).thenReturn(Optional.empty());
        BadRequestException exception =  Assertions.assertThrows(BadRequestException.class,
                () -> productAttributeController.createProductAttribute(productAttributePostVm, UriComponentsBuilder.fromPath("/product-attribute/{id}"), principal));
        assertThat(exception.getMessage(),is("Product attribute group 1 is not found"));
    }

    @Test
    void createProductAttribute_ValidProductAttributeWithIdProductAttributeGroup_Success(){
        ProductAttributePostVm productAttributePostVm = new ProductAttributePostVm("Ram",1L);
        var ProductAttributeCaptor = ArgumentCaptor.forClass(ProductAttribute.class);
        ProductAttribute savedProductAttribute = mock(ProductAttribute.class);
        when(productAttributeGroupRepository.findById(productAttributePostVm.productAttributeGroupId())).thenReturn(Optional.of(productAttributeGroup));
        when(savedProductAttribute.getProductAttributeGroup()).thenReturn(productAttributeGroup);
        when(productAttributeRepository.saveAndFlush(ProductAttributeCaptor.capture())).thenReturn(savedProductAttribute);
        UriComponentsBuilder newUriComponentsBuilder = mock(UriComponentsBuilder.class);
        UriComponents uriComponents = mock(UriComponents.class);
        when(uriComponentsBuilder.replacePath("/product-attribute/{id}")).thenReturn(newUriComponentsBuilder);
        when(newUriComponentsBuilder.buildAndExpand(savedProductAttribute.getId())).thenReturn(uriComponents);
        ResponseEntity<ProductAttributeGetVm> result = productAttributeController.createProductAttribute(productAttributePostVm
                , uriComponentsBuilder, principal);
        verify(productAttributeRepository).saveAndFlush(ProductAttributeCaptor.capture());
        ProductAttribute productAttributeValue = ProductAttributeCaptor.getValue();
        assertEquals(productAttributeValue.getName(), productAttributePostVm.name());
        assertEquals(Objects.requireNonNull(result.getBody()).productAttributeGroup() , productAttributeGroup.getName() );
    }
    @Test
    void createProductAttribute_ValidProductAttributeWithOutIdProductAttributeGroup_Success(){
        ProductAttributePostVm productAttributePostVm = mock(ProductAttributePostVm.class);
        var ProductAttributeCaptor = ArgumentCaptor.forClass(ProductAttribute.class);
        when(productAttributePostVm.name()).thenReturn("Ram");
        when(productAttributePostVm.productAttributeGroupId()).thenReturn(null);
        ProductAttribute savedProductAttribute = mock(ProductAttribute.class);
        when(productAttributeRepository.saveAndFlush(ProductAttributeCaptor.capture())).thenReturn(savedProductAttribute);
        UriComponentsBuilder newUriComponentsBuilder = mock(UriComponentsBuilder.class);
        UriComponents uriComponents = mock(UriComponents.class);
        when(uriComponentsBuilder.replacePath("/product-attribute/{id}")).thenReturn(newUriComponentsBuilder);
        when(newUriComponentsBuilder.buildAndExpand(savedProductAttribute.getId())).thenReturn(uriComponents);
        ResponseEntity<ProductAttributeGetVm> result = productAttributeController.createProductAttribute(productAttributePostVm
                , uriComponentsBuilder, principal);
        verify(productAttributeRepository).saveAndFlush(ProductAttributeCaptor.capture());
        ProductAttribute productAttributeValue = ProductAttributeCaptor.getValue();
        assertEquals(productAttributeValue.getName(), productAttributePostVm.name());
        assertNull(Objects.requireNonNull(result.getBody()).productAttributeGroup());
    }

    @Test
    void updateProductAttribute_FindIdProductAttribute_ThrowException(){
        ProductAttributePostVm productAttributePostVm = new ProductAttributePostVm("Ram",1L);
        when(productAttributeRepository.findById(1L)).thenReturn(Optional.empty());
        NotFoundException exception =  Assertions.assertThrows(NotFoundException.class,
                () -> productAttributeController.updateProductAttribute(1L,productAttributePostVm));
        assertThat(exception.getMessage(),is("Product attribute group 1 is not found"));
    }
    @Test
    void updateProductAttribute_FindProductAttributeGroupId_ThrowException(){
        ProductAttributePostVm productAttributePostVm = new ProductAttributePostVm("Ram",1L);
        when(productAttributeRepository.findById(1L)).thenReturn(Optional.of(productAttribute));
        when(productAttributeGroupRepository.findById(productAttributePostVm.productAttributeGroupId())).thenReturn(Optional.empty());
        BadRequestException exception =  Assertions.assertThrows(BadRequestException.class,
                () -> productAttributeController.updateProductAttribute(1L,productAttributePostVm));
        assertThat(exception.getMessage(),is("Product attribute group 1 is not found"));
    }
    @Test
    void updateProductAttribute_ValidProductAttributePostVmWithProductAttributeGroupId_Success(){
        ProductAttributePostVm productAttributePostVm = new ProductAttributePostVm("Card",1L);
        when(productAttributeGroupRepository.findById(productAttributePostVm.productAttributeGroupId())).thenReturn(Optional.of(productAttributeGroup));
        when(productAttributeRepository.findById(1L)).thenReturn(Optional.of(productAttribute));
        var ProductAttributeCaptor = ArgumentCaptor.forClass(ProductAttribute.class);
        ProductAttribute savedProductAttribute = mock(ProductAttribute.class);
        when(productAttributeRepository.saveAndFlush(ProductAttributeCaptor.capture())).thenReturn(savedProductAttribute);
        ResponseEntity<Void> result = productAttributeController.updateProductAttribute(1L,productAttributePostVm);
        verify(productAttributeRepository).saveAndFlush(ProductAttributeCaptor.capture());
        ProductAttribute productAttributeValue = ProductAttributeCaptor.getValue();
        assertEquals(productAttributeValue.getName(), productAttributePostVm.name());
        assertThat(result.getStatusCode(),is(HttpStatus.NO_CONTENT));
    }
    @Test
    void updateProductAttribute_ValidProductAttributePostVmWithOutProductAttributeGroupId_Success(){
        ProductAttributePostVm productAttributePostVm = mock(ProductAttributePostVm.class);
        when(productAttributePostVm.name()).thenReturn("CPU");
        when(productAttributePostVm.productAttributeGroupId()).thenReturn(null);
        when(productAttributeRepository.findById(1L)).thenReturn(Optional.of(productAttribute));
        var ProductAttributeCaptor = ArgumentCaptor.forClass(ProductAttribute.class);
        ProductAttribute savedProductAttribute = mock(ProductAttribute.class);
        when(productAttributeRepository.saveAndFlush(ProductAttributeCaptor.capture())).thenReturn(savedProductAttribute);
        ResponseEntity<Void> result = productAttributeController.updateProductAttribute(1L,productAttributePostVm);
        verify(productAttributeRepository).saveAndFlush(ProductAttributeCaptor.capture());
        ProductAttribute productAttributeValue = ProductAttributeCaptor.getValue();
        assertEquals(productAttributeValue.getProductAttributeGroup(),  productAttribute.getProductAttributeGroup());
        assertThat(result.getStatusCode(),is(HttpStatus.NO_CONTENT));
    }
}
