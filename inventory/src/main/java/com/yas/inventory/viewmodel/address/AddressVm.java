package com.yas.inventory.viewmodel.address;

import lombok.Builder;

@Builder
public record AddressVm(Long id, String contactName, String phone, String addressLine1, String city, String zipCode,
                        Long districtId, Long stateOrProvinceId, Long countryId) {

}