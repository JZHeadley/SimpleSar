package com.jzheadley.ramhacks.repository.search;

import com.jzheadley.ramhacks.domain.Parent;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Parent entity.
 */
public interface ParentSearchRepository extends ElasticsearchRepository<Parent, Long> {
}
