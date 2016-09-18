package com.jzheadley.ramhacks.repository.search;

import com.jzheadley.ramhacks.domain.FinancialData;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the FinancialData entity.
 */
public interface FinancialDataSearchRepository extends ElasticsearchRepository<FinancialData, Long> {
}
