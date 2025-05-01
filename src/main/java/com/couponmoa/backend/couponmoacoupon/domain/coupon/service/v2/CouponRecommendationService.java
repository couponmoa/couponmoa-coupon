package com.couponmoa.backend.couponmoacoupon.domain.coupon.service.v2;

import com.couponmoa.backend.couponmoacoupon.domain.coupon.entity.Search;
import com.couponmoa.backend.couponmoacoupon.domain.coupon.entity.SearchHistory;
import com.couponmoa.backend.couponmoacoupon.domain.coupon.repository.SearchHistoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponRecommendationService {

    private final CouponElasticsearchService couponElasticsearchService;
    private final SearchHistoryRepository searchHistoryRepository;
    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    public List<Search> getAIRecommendations(Long userId) throws IOException {
        List<String> userKeywords = searchHistoryRepository.findByUserId(userId.toString()).stream()
                .map(SearchHistory::getKeyword)
                .distinct()
                .limit(5)
                .collect(Collectors.toList());

        List<String> keywords = userKeywords.isEmpty() ?
                couponElasticsearchService.getPopularKeywords(5) : userKeywords;

        List<Search> allCoupons = couponElasticsearchService.getAllCoupons();

        String prompt = "Based on user keywords " + keywords + ", recommend up to 5 coupon IDs from:\n" +
                allCoupons.stream()
                        .map(c -> String.format("ID: %d, Name: %s, Description: %s",
                                c.getCouponId(), c.getName(), c.getDescription()))
                        .reduce("", (a, b) -> a + b + "\n") +
                "Return JSON with 'recommended_coupon_ids' key containing up to 5 IDs.";

        String response = chatClient.prompt().user(prompt).call().content();
        List<Long> recommendedIds = extractRecommendedIds(response);

        return allCoupons.stream()
                .filter(c -> recommendedIds.contains(c.getCouponId()))
                .toList();
    }

    private List<Long> extractRecommendedIds(String response) {
        try {
            Map<String, List<Long>> parsed = objectMapper.readValue(response, Map.class);
            List<Long> recommendedIds = parsed.get("recommended_coupon_ids");
            return recommendedIds != null ? recommendedIds : Collections.emptyList();
        } catch (Exception e) {
            log.error("Failed to parse Gemini response", e);
            return Collections.emptyList();
        }
    }
}