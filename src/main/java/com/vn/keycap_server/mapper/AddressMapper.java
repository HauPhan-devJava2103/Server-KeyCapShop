package com.vn.keycap_server.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import com.vn.keycap_server.dto.response.address.AddressResponse;
import com.vn.keycap_server.modal.Address;

@Component
@Mapper(componentModel = "spring")
public interface AddressMapper {

    @Mapping(target = "province", source = "address", qualifiedByName = "toProvince")
    @Mapping(target = "district", source = "address", qualifiedByName = "toDistrict")
    @Mapping(target = "ward", source = "address", qualifiedByName = "toWard")
    AddressResponse toAddressResponse(Address address);

    List<AddressResponse> toAddressResponseList(List<Address> addresses);

    @Named("toProvince")
    default AddressResponse.LocationInfo toProvince(Address address) {
        return new AddressResponse.LocationInfo(
                address.getProvinceCode(),
                address.getProvinceName());
    }

    @Named("toDistrict")
    default AddressResponse.LocationInfo toDistrict(Address address) {
        return new AddressResponse.LocationInfo(
                address.getDistrictCode(),
                address.getDistrictName());
    }

    @Named("toWard")
    default AddressResponse.LocationInfo toWard(Address address) {
        return new AddressResponse.LocationInfo(
                address.getWardCode(),
                address.getWardName());
    }
}
