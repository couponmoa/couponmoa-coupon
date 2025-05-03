package com.couponmoa.backend.couponmoacoupon.domain.coupon.controller.v2;

import com.couponmoa.backend.couponmoacoupon.domain.coupon.entity.Search;
import com.couponmoa.backend.couponmoacoupon.domain.coupon.service.v2.CouponRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/recommend")
@RequiredArgsConstructor
public class CouponRecommendationController {

    private final CouponRecommendationService recommendationService;

        @GetMapping
        public List<Search> getRecommendations(@RequestHeader("X-User-Id") Long userId) throws IOException {
            return recommendationService.getAIRecommendations(); // userId 제거됨
        }
    }

