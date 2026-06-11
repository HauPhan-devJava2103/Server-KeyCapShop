package com.vn.keycap_server.service.address;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vn.keycap_server.dto.response.address.AddressResponse;
import com.vn.keycap_server.mapper.AddressMapper;
import com.vn.keycap_server.modal.Address;
import com.vn.keycap_server.repository.AddressRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddressService implements IAddressService {

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    @Override
    public List<AddressResponse> getAllAddressByUserId(Long userId) {

        List<Address> addresses = addressRepository.findByUserIdOrderByIsDefaultDescUpdatedAtDesc(userId);
        return addressMapper.toAddressResponseList(addresses);
    }

}
