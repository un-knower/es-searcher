package com.star.searcher.support.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.star.searcher.annotation.ESProperty;
import com.star.searcher.annotation.ESProperty.SearchMethod;
import com.star.searcher.annotation.ESSorter;
import com.star.searcher.model.Page;
import com.star.searcher.support.ElasticsearchService;
import com.star.searcher.util.JsonUtil;
import com.star.searcher.util.ReflectUtil;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0.0
 * @since 2018-06-06 01:03:00
 */
public class ElasticsearchServiceImpl implements ElasticsearchService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchServiceImpl.class);

    private Client client;

    public ElasticsearchServiceImpl(Client client) {
        this.client = client;
    }

    @Override
    public <T> Page<T> search(List<String> indices, List<String> types, QueryBuilder queryBuilder, SortBuilder sortBuilder, Integer from, Integer size, Class<T> dataClass) {
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(indices.toArray(new String[0])).setTypes(types.toArray(new String[0]))
                .setQuery(queryBuilder).addSort(sortBuilder).setFrom(from).setSize(size);
        SearchResponse response = searchRequestBuilder.get();
        Page<T> page = new Page<>();
        if (response == null || response.getHits() == null || response.getHits().getTotalHits() < 0) {
            LOGGER.warn("查询Elasticsearch返回为空");
            page.setTotal(0);
            page.setData(new ArrayList<>());
        } else {
            page.setTotal(response.getHits().getTotalHits());
            List<Map<String, Object>> list = Arrays.stream(response.getHits().getHits()).map(SearchHit::getSourceAsMap).collect(Collectors.toList());
            page.setData(JsonUtil.fromJson(JsonUtil.toJson(list), new TypeReference<List<T>>() {
            }));
        }
        return page;
    }

    @Override
    public <T> Page<T> search(List<String> indices, List<String> types, Object queryObj, Integer from, Integer size, Class<T> dataClass) {
        QueryBuilder queryBuilder = genQueryBuilder(queryObj);
        SortBuilder sortBuilder = genSortBuilder(queryObj);
        return search(indices, types, queryBuilder, sortBuilder, from, size, dataClass);
    }

    private SortBuilder genSortBuilder(Object queryObj) {
        SortBuilder sortBuilder = null;
        List<Field> fields = ReflectUtil.getAllDeclaredFields(queryObj.getClass());
        for (Field field : fields) {
            ESSorter esSorter = field.getAnnotation(ESSorter.class);
            ESProperty esProperty = field.getAnnotation(ESProperty.class);
            if (esSorter == null || esProperty == null) {
                continue;
            }
            sortBuilder = SortBuilders.fieldSort(esProperty.value()).order(esSorter.value());
        }
        return sortBuilder;
    }

    private QueryBuilder genQueryBuilder(Object queryObj) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        List<Field> fields = ReflectUtil.getAllDeclaredFields(queryObj.getClass());
        for (Field field : fields) {
            ESProperty esProperty = field.getAnnotation(ESProperty.class);
            if (esProperty == null) {
                continue;
            }
            if (Collection.class.isAssignableFrom(field.getType())) {
                Collection collection = (Collection) getFieldValue(field, queryObj);
                if (collection == null) {
                    continue;
                }
                for (Object o : collection) {
                    boolQueryBuilder.must(genSingleQueryBuilder(esProperty.value(), o, esProperty.method()));
                }
            } else {
                Object value = getFieldValue(field, queryObj);
                if (value == null) {
                    continue;
                }
                boolQueryBuilder.must(genSingleQueryBuilder(esProperty.value(), value, esProperty.method()));
            }
        }
        return boolQueryBuilder;
    }

    private QueryBuilder genSingleQueryBuilder(String field, Object value, SearchMethod searchMethod) {
        QueryBuilder queryBuilder = null;
        switch (searchMethod) {
            case NULL:
                queryBuilder = QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery(field));
                break;
            case NOT_NULL:
                queryBuilder = QueryBuilders.existsQuery(field);
                break;
            case EQUAL:
                queryBuilder = QueryBuilders.termQuery(field, value);
                break;
            case LIKE:
                queryBuilder = QueryBuilders.wildcardQuery(field, "*" + value + "*");
                break;
            case GTE:
                queryBuilder = QueryBuilders.rangeQuery(field).gte(value);
                break;
            case GT:
                queryBuilder = QueryBuilders.rangeQuery(field).gt(value);
                break;
            case LTE:
                queryBuilder = QueryBuilders.rangeQuery(field).lte(value);
                break;
            case LT:
                queryBuilder = QueryBuilders.rangeQuery(field).lt(value);
                break;
            default:
                break;
        }
        return queryBuilder;
    }

    private Object getFieldValue(Field field, Object object) {
        try {
            field.setAccessible(true);
            return field.get(object);
        } catch (IllegalAccessException e) {
            LOGGER.error("取值失败", e);
            return null;
        }
    }
}
