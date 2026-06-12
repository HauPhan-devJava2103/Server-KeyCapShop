package com.vn.keycap_server.service.address;

import java.util.List;

import com.vn.keycap_server.dto.response.address.AddressResponse;

public interface IAddressService {

    List<AddressResponse> getAllAddressByUserId(Long userId);

}
