package com.vn.keycap_server.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.vn.keycap_server.service.shipping.IDeliveryEstimateService;
import com.vn.keycap_server.service.shipping.MockDeliveryEstimateService;

/**
 * Cấu hình implementation mặc định cho nghiệp vụ ước tính thời gian giao hàng.
 */
@Configuration
public class DeliveryEstimateConfig {

    /**
     * Đăng ký implementation giả lập chỉ khi chưa có provider giao hàng thật.
     * Sau này chỉ cần tạo bean IDeliveryEstimateService của GHTK, Spring sẽ không
     * khởi tạo mock này.
     */
    @Bean
    @ConditionalOnMissingBean(IDeliveryEstimateService.class)
    IDeliveryEstimateService mockDeliveryEstimateService() {
        return new MockDeliveryEstimateService();
    }
}
