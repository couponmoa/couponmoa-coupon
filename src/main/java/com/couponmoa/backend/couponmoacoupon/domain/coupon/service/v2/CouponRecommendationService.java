package com.couponmoa.backend.couponmoacoupon.domain.coupon.service.v2;

import com.couponmoa.backend.couponmoacoupon.domain.coupon.dto.request.CouponDto;
import com.couponmoa.backend.couponmoacoupon.domain.coupon.dto.request.RecommendRequest;
import com.couponmoa.backend.couponmoacoupon.domain.coupon.entity.Search;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponRecommendationService {

    private final CouponElasticsearchService couponElasticsearchService;
    private final RestTemplate restTemplate;

    @Value("${ai.service.url}")
    private String aiServiceUrl;

    private static final String ENDPOINT = "/api/v1/coupon/recommend-ai";

    public List<Search> getAIRecommendations() {
        try {
            List<Search> allCoupons = couponElasticsearchService.getAllCoupons();

            RecommendRequest requestDto = buildRecommendRequest(allCoupons); // userId 제거됨

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<RecommendRequest> entity = new HttpEntity<>(requestDto, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(aiServiceUrl + ENDPOINT, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> body = response.getBody();
                if (body != null && body.get("recommended_coupon_ids") instanceof List<?>) {
                    List<Long> recommendedIds = ((List<?>) body.get("recommended_coupon_ids")).stream()
                            .map(id -> Long.valueOf(id.toString()))
                            .collect(Collectors.toList());

                    return allCoupons.stream()
                            .filter(c -> recommendedIds.contains(c.getCouponId()))
                            .toList();
                }
            }
        } catch (Exception e) {
            log.error("AI 추천 요청 실패", e);
        }

        return Collections.emptyList();
    }

    private RecommendRequest buildRecommendRequest(List<Search> allCoupons) {
        try {
            List<String> keywords = couponElasticsearchService.getPopularKeywords(5);

            List<CouponDto> coupons = allCoupons.stream()
                    .map(c -> new CouponDto(
                            c.getCouponId(),
                            c.getName(),
                            c.getDescription()
                    ))
                    .toList();

            return new RecommendRequest(keywords, coupons);
        } catch (Exception e) {
            log.error("AI 추천 데이터 생성 실패", e);
            return new RecommendRequest(Collections.emptyList(), Collections.emptyList());
        }
    }
}

