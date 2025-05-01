package com.couponmoa.backend.couponmoacoupon.domain.coupon.dto.request;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class RecommendRequest {
    private List<String> keywords;
    private List<CouponDto> coupons;
}
