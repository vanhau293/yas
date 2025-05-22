package com.yas.tax.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.yas.commonlibrary.IntegrationTestConfiguration;
import com.yas.tax.model.TaxClass;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(IntegrationTestConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TaxClassRepositoryIT {

    @Autowired
    private TaxClassRepository taxClassRepository;

    @BeforeEach
    void insertTestData(){
        taxClassRepository.save(TaxClass.builder().name("test_tax_class").id(1L).build());
        taxClassRepository.save(TaxClass.builder().name("another_tax_class").id(2L).build());
    }

    @AfterEach
    void tearDown(){
        taxClassRepository.deleteAll();
    }

    @Test
    void test_existByName_shouldReturnTrue_whenTaxClassNameExists(){
        assertThat(taxClassRepository.existsByName("test_tax_class")).isTrue();
    }

    @Test
    void test_existByName_shouldReturnFalse_whenTaxClassNameNotExists(){
        assertThat(taxClassRepository.existsByName("dummy_class")).isFalse();
    }

    @Test
    void test_existsByNameNotUpdatingTaxClass_shouldReturnTrue_whenThereIsAClassWithSameNameAndDiffID(){
        assertThat(taxClassRepository.existsByNameNotUpdatingTaxClass("test_tax_class", 2L)).isTrue();
    }

    @Test
    void test_existsByNameNotUpdatingTaxClass_shouldReturnFalse_whenThereIsNoClassWithSameName(){
        assertThat(taxClassRepository.existsByNameNotUpdatingTaxClass("dummy_class", 1L)).isFalse();
    }
}
