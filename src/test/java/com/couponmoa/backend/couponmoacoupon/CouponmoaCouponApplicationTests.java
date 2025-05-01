package com.couponmoa.backend.couponmoacoupon;

import com.couponmoa.backend.couponmoacoupon.domain.coupon.repository.SearchHistoryRepository;
import com.couponmoa.backend.couponmoacoupon.domain.coupon.repository.SearchRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
@EnableAutoConfiguration(exclude = {
        ElasticsearchDataAutoConfiguration.class,
        ElasticsearchRepositoriesAutoConfiguration.class
})
class CouponmoaCouponApplicationTests {

    @MockitoBean
    private SearchRepository searchRepository;

    @MockitoBean
    private SearchHistoryRepository searchHistoryRepository;

    @Test
    void contextLoads() {
    }

}
