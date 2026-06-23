package com.vn.keycap_server.mapper;

import java.util.List;

import com.vn.keycap_server.dto.request.address.CreateAddressRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import com.vn.keycap_server.dto.response.address.AddressResponse;
import com.vn.keycap_server.dto.response.address.LocationInfoResponse;
import com.vn.keycap_server.modal.Address;

@Component
@Mapper(componentModel = "spring")
public interface AddressMapper {

    @Mapping(target = "province", source = "address", qualifiedByName = "toProvince")
    @Mapping(target = "district", source = "address", qualifiedByName = "toDistrict")
    @Mapping(target = "ward", source = "address", qualifiedByName = "toWard")
    AddressResponse toAddressResponse(Address address);

    List<AddressResponse> toAddressResponseList(List<Address> addresses);

    // @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    Address toAddress(CreateAddressRequest request);

    // @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateAddressFromRequest(CreateAddressRequest request, @MappingTarget Address address);

    @Named("toProvince")
    default LocationInfoResponse toProvince(Address address) {
        return new LocationInfoResponse(
                address.getProvinceCode(),
                address.getProvinceName());
    }

    @Named("toDistrict")
    default LocationInfoResponse toDistrict(Address address) {
        return new LocationInfoResponse(
                address.getDistrictCode(),
                address.getDistrictName());
    }

    @Named("toWard")
    default LocationInfoResponse toWard(Address address) {
        return new LocationInfoResponse(
                address.getWardCode(),
                address.getWardName());
    }
}
