package com.example.shop_clothes.service.Impl;

import com.example.shop_clothes.dto.record.ShippingMethodResponse;
import com.example.shop_clothes.enums.ShippingMethod;
import com.example.shop_clothes.service.ShippingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShippingServiceImpl implements ShippingService {
    public List<ShippingMethodResponse> listShippingMethod() {
        return Arrays.stream(ShippingMethod.values())
                .map(sm -> new ShippingMethodResponse(
                        sm.getDisplayName(),
                        sm.getFee()
                ))
                .toList();
    }

}
