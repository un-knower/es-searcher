package com.star.searcher.support;

import com.star.searcher.model.Page;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.SortBuilder;

import java.util.List;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0.0
 * @since 2018-06-06 01:00:00
 */
public interface ElasticsearchService {
    <T> Page<T> search(List<String> indices, List<String> types, QueryBuilder queryBuilder, SortBuilder sortBuilder, Integer from, Integer size, Class<T> dataClass);

    <T> Page<T> search(List<String> indices, List<String> types, Object queryObj, Integer from, Integer size, Class<T> dataClass);
}
