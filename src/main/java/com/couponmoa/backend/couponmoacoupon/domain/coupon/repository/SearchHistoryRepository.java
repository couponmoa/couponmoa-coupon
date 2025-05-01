package com.couponmoa.backend.couponmoacoupon.domain.coupon.repository;

import com.couponmoa.backend.couponmoacoupon.domain.coupon.entity.SearchHistory;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface SearchHistoryRepository extends ElasticsearchRepository<SearchHistory, String> {
    List<SearchHistory> findByUserId(String userId);
}

