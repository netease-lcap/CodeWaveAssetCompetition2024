package com.netease.lowcode.lib.api;


import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netease.lowcode.core.annotation.NaslLogic;
import com.netease.lowcode.lib.api.config.CommonEsSearchConfig;
import com.netease.lowcode.lib.api.config.SortTypeEnum;
import com.netease.lowcode.lib.api.dto.QueryItemsDto;
import com.netease.lowcode.lib.api.dto.QueryItemsListDto;
import com.netease.lowcode.lib.api.dto.QueryRequestParam;
import com.netease.lowcode.lib.api.dto.QueryResultDto;
import com.netease.lowcode.lib.api.util.ElasticsearchUtil;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ElasticsearchApi {
    private static final Logger log = LoggerFactory.getLogger("LCAP_EXTENSION_LOGGER");
    @Autowired
    private CommonEsSearchConfig commonEsSearchConfig;

    @PostConstruct
    public void init() {
        ElasticsearchUtil.initClient(commonEsSearchConfig.getEsClientUris(), commonEsSearchConfig.getEsClientUsername(), commonEsSearchConfig.getEsClientPassword());
    }

    /**
     * 补充默认参数
     *
     * @param queryRequestParam
     */
    private void fillDefaultParam(QueryRequestParam queryRequestParam) {
        if (StringUtils.isEmpty(queryRequestParam.getPageIndex())) {
            queryRequestParam.setPageIndex(1);
        }
        if (StringUtils.isEmpty(queryRequestParam.getPageSize())) {
            queryRequestParam.setPageSize(20);
        }
        if (StringUtils.isEmpty(queryRequestParam.getIndex())) {
            throw new IllegalArgumentException("索引名称不能为空");
        }
        if (queryRequestParam.getSortInfo() != null) {
            if (StringUtils.isEmpty(queryRequestParam.getSortInfo().sortType) || StringUtils.isEmpty(queryRequestParam.getSortInfo().getSortParam())) {
                queryRequestParam.setSortInfo(null);
            }
        }
        if (queryRequestParam.getLogicalOperator() == null) {
            queryRequestParam.setLogicalOperator(1);
        }

        if (queryRequestParam.getLogicalOperator() != 1 && queryRequestParam.getLogicalOperator() != 2) {
            throw new IllegalArgumentException("逻辑运算符logicalOperator不合法");
        }
        if (queryRequestParam.getScrollTime() == null || queryRequestParam.getScrollTime() <= 0) {
            queryRequestParam.setScrollTime(null);
        }
        if (!CollectionUtils.isEmpty(queryRequestParam.getQueryItems()) && !CollectionUtils.isEmpty(queryRequestParam.getQueryItemList())) {
            throw new IllegalArgumentException("queryItemList和queryItems不能同时传入");
        }
//        if (!CollectionUtils.isEmpty(queryRequestParam.getQueryItems())) {
//            QueryItemsListDto queryItemsListDto = new QueryItemsListDto();
//            queryItemsListDto.setQueryItems(queryRequestParam.getQueryItems());
//            queryRequestParam.setQueryItemList(new ArrayList<>());
//            queryRequestParam.getQueryItemList().add(queryItemsListDto);
//            // 1表示queryItems内多个条件之间为与；2表示queryItems内多个条件之间为或。所以这边需要旋转一下。
//            // fillQueryBuilder执行时，1是外与内或。不旋转的话会执行内或。
//            queryRequestParam.setLogicalOperator(queryRequestParam.getLogicalOperator() == 1 ? 2 : 1);
//            queryRequestParam.setQueryItems(null);
//        }
    }

    /**
     * 修改文档
     *
     * @param index        索引名称
     * @param queryFields  查询内容。key为字段名，value为字段值
     * @param updateFields 更新内容。key为字段名，value为字段值
     * @return
     */
    @NaslLogic
    public Boolean updateDocumentByFields(String index, Map<String, List<String>> queryFields, Map<String, String> updateFields) {
        try {
            log.info("index:{},queryFields:{},updateFields:{}", index, JSONObject.toJSONString(queryFields), JSONObject.toJSONString(updateFields));
            ElasticsearchUtil.updateByFields(index, queryFields, updateFields, commonEsSearchConfig.getEsClientUris());
        } catch (Exception e) {
            log.error("更新异常", e);
            throw new IllegalArgumentException(e.getMessage());
        }
        return true;
    }

    /**
     * 查询文档总数。es存在延迟
     *
     * @param index
     * @return
     */
    @NaslLogic
    public Long count(String index) {
        try {
            return ElasticsearchUtil.count(index, commonEsSearchConfig.getEsClientUris());
        } catch (IOException e) {
            log.error("count异常", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * 保存文档
     *
     * @param docEntity 文档对象
     * @param index     索引名称
     * @param <T>
     * @return
     */
    @NaslLogic
    public <T> String saveDocument(T docEntity, String index) {
        ObjectMapper objectMapper = new ObjectMapper();
        // 将对象转为Map
        Map<String, Object> doc = objectMapper.convertValue(docEntity, new TypeReference<Map<String, Object>>() {
        });
        doc.forEach((key, value) -> {
            if (value instanceof ArrayList || value instanceof HashMap) {
                try {
                    doc.put(key, objectMapper.writeValueAsString(value));
                } catch (JsonProcessingException e) {
                    log.error("{} json转换异常", key, e);
                    doc.put(key, null);
                }
            }

        });
        return ElasticsearchUtil.saveDocument(doc, commonEsSearchConfig.getEsClientUris(), index);
    }

    /**
     * 批量保存文档
     *
     * @param docEntityList 文档对象列表
     * @param index         索引名称
     * @param <T>
     * @return
     */
    @NaslLogic
    public <T> Boolean bulkSaveDocuments(List<T> docEntityList, String index) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> docs = docEntityList.stream()
                .map(entity -> objectMapper.convertValue(entity,
                        new TypeReference<Map<String, Object>>() {
                        }))
                .collect(Collectors.toList());
        return ElasticsearchUtil.bulkSaveDocuments(docs, commonEsSearchConfig.getEsClientUris(), index);
    }

    /**
     * 滚动搜索
     *
     * @param scrollTime 滚动搜索有效时间段，不传即不使用滚动搜索
     * @param scrollId   滚动id，滚动搜索时非首次使用必填
     * @return
     * @throws IllegalArgumentException
     */
    @NaslLogic
    public QueryResultDto queryScroll(Integer scrollTime, String scrollId) throws IllegalArgumentException {
        log.info("scrollId:{}", scrollId);
        LocalDateTime now1 = LocalDateTime.now();
        if (scrollTime != null) {
        }
        if (scrollId == null || scrollTime == null || scrollTime == 0) {
            throw new IllegalArgumentException("scrollId和scrollTime不能为空");
        }
        String scrollTimeStr = scrollTime + "m";
        QueryResultDto queryResultDto = ElasticsearchUtil.searchScroll(commonEsSearchConfig.getEsClientUris(), scrollTimeStr, scrollId);
        LocalDateTime now2 = LocalDateTime.now();
        log.info("查询耗时：{}", Duration.between(now1, now2).toMillis());
        return queryResultDto;
    }

    /**
     * 普通搜索
     *
     * @param queryRequestParam
     * @return
     * @throws IllegalArgumentException
     */
    @NaslLogic
    public QueryResultDto query(QueryRequestParam queryRequestParam) throws IllegalArgumentException {
        return queryCommon(queryRequestParam);
    }


    public QueryResultDto queryCommon(QueryRequestParam queryRequestParam) throws IllegalArgumentException {
        try {
            log.info("queryRequestParam:{}", JSONObject.toJSONString(queryRequestParam));
            if (!CollectionUtils.isEmpty(queryRequestParam.getQueryItems()) && !CollectionUtils.isEmpty(queryRequestParam.getQueryItemList())) {
                throw new IllegalArgumentException("queryItems和queryItemList不能同时存在");
            }
            LocalDateTime now1 = LocalDateTime.now();
            fillDefaultParam(queryRequestParam);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            // 分页
            if (queryRequestParam.getPageSize() <= 0 || queryRequestParam.getPageIndex() <= 0) {
                throw new IllegalArgumentException("pageSize和pageIndex必须大于0");
            }
            sourceBuilder.from((queryRequestParam.getPageIndex() - 1) * queryRequestParam.getPageSize()); // 起始索引
            sourceBuilder.size(queryRequestParam.getPageSize()); // 每页大小
            // Include和exclude
            if (!StringUtils.isEmpty(queryRequestParam.getIncludeFields()) && !StringUtils.isEmpty(queryRequestParam.getExcludeFields())) {
                sourceBuilder.fetchSource(queryRequestParam.getIncludeFields().toArray(new String[]{}), queryRequestParam.getExcludeFields().toArray(new String[]{}));
            } else if (!StringUtils.isEmpty(queryRequestParam.getIncludeFields())) {
                sourceBuilder.fetchSource(queryRequestParam.getIncludeFields().toArray(new String[]{}), null);
            } else if (!StringUtils.isEmpty(queryRequestParam.getExcludeFields())) {
                sourceBuilder.fetchSource(null, queryRequestParam.getExcludeFields().toArray(new String[]{}));
            }
            // 排序
            if (queryRequestParam.getSortInfo() != null) {
                SortOrder sortOrder = SortTypeEnum.getEnumByValue(queryRequestParam.getSortInfo().sortType);
                if (sortOrder == null) {
                    throw new IllegalArgumentException("排序类型不合法");
                }
                sourceBuilder.sort(queryRequestParam.getSortInfo().getSortParam(), sortOrder);
            }
            BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
            // 查询条件 1与2或
            if (!CollectionUtils.isEmpty(queryRequestParam.getQueryItems())) {
                queryBuilder = fillQueryBuilderQueryItems(queryRequestParam.getLogicalOperator(), queryRequestParam.getQueryItems());
            } else if (!CollectionUtils.isEmpty(queryRequestParam.getQueryItemList())) {
                // 查询条件 1与或 2或与
                queryBuilder = fillQueryBuilderQueryList(queryRequestParam.getLogicalOperator(), queryRequestParam.getQueryItemList());
            }
            String scrollTime = null;
            if (queryRequestParam.getScrollTime() != null) {
                scrollTime = queryRequestParam.getScrollTime() + "m";
            }
            // 普通搜索
            QueryResultDto resultDto = ElasticsearchUtil.search(queryRequestParam.getIndex(), sourceBuilder, queryBuilder,
                    queryRequestParam.getPageIndex(), queryRequestParam.getPageSize(), commonEsSearchConfig.getEsClientUris(), scrollTime);
            LocalDateTime now2 = LocalDateTime.now();
            log.info("查询耗时：{}", Duration.between(now1, now2).toMillis());
            return resultDto;
        } catch (Exception e) {
            log.error("查询异常", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private Map<Object, Object> handleQueryItemsDto(String queryValue) {
        Map<Object, Object> map = new HashMap<>();
        String[] queryValues = queryValue.split(",");
        if (queryValues.length != 1 && queryValues.length != 2) {
            throw new IllegalArgumentException("查询内容queryValue不合法：" + queryValue);
        }
        if (queryValues.length == 2) {
            if (StringUtils.isEmpty(queryValues[0])) {
                queryValues[0] = null;
            }
            map.put(queryValues[0], queryValues[1]);
        } else if (queryValue.contains(",")) {
            map.put(queryValues[0], null);
        } else {
            throw new IllegalArgumentException("查询内容queryValue不合法：" + queryValue);
        }
        return map;
    }

    private BoolQueryBuilder fillQueryBuilderQueryList(Integer logicalOperator, List<QueryItemsListDto> queryItemList) {
        //1与2或
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (logicalOperator == 1) {
            queryItemList.forEach(queryItemListDto -> {
                BoolQueryBuilder queryBuilderItem = QueryBuilders.boolQuery();
                queryItemListDto.getQueryItems().forEach(queryItemDto -> {
                    //查询类型，精确1 termQuery、模糊2 wildcardQuery、范围3 rangeQuery
                    if (queryItemDto.getQueryType() == 1) {
                        queryBuilderItem.should(QueryBuilders.termQuery(queryItemDto.getParameter(), queryItemDto.getQueryValue()));
                    } else if (queryItemDto.getQueryType() == 2) {
                        queryBuilderItem.should(QueryBuilders.wildcardQuery(queryItemDto.getParameter(), "*" + queryItemDto.getQueryValue() + "*"));
                    } else if (queryItemDto.getQueryType() == 3) {
                        Map<Object, Object> mapQueryValue = handleQueryItemsDto(queryItemDto.getQueryValue());
                        mapQueryValue.forEach((key, value) -> {
                            if (key != null && value != null) {
                                queryBuilderItem.should(QueryBuilders.rangeQuery(queryItemDto.getParameter()).gte(key).lte(value));
                            } else if (key != null) {
                                queryBuilderItem.should(QueryBuilders.rangeQuery(queryItemDto.getParameter()).gte(key));
                            } else if (value != null) {
                                queryBuilderItem.should(QueryBuilders.rangeQuery(queryItemDto.getParameter()).lte(value));
                            }
                        });
                    } else if (queryItemDto.getQueryType() == 4) {
                        queryBuilder.should(QueryBuilders.boolQuery().mustNot(QueryBuilders.termQuery(queryItemDto.getParameter(), queryItemDto.getQueryValue())));
                    } else if (queryItemDto.getQueryType() == 5) {
                        queryBuilderItem.should(QueryBuilders.matchQuery(queryItemDto.getParameter(), queryItemDto.getQueryValue()));
                    }
                });
                queryBuilder.must(queryBuilderItem);
            });
        } else {
            queryItemList.forEach(queryItemListDto -> {
                BoolQueryBuilder queryBuilderItem = QueryBuilders.boolQuery();
                queryItemListDto.getQueryItems().forEach(queryItemDto -> {
                    //查询类型，精确1 termQuery、模糊2 wildcardQuery、范围3 rangeQuery
                    if (queryItemDto.getQueryType() == 1) {
                        queryBuilderItem.must(QueryBuilders.termQuery(queryItemDto.getParameter(), queryItemDto.getQueryValue()));
                    } else if (queryItemDto.getQueryType() == 2) {
                        queryBuilderItem.must(QueryBuilders.wildcardQuery(queryItemDto.getParameter(), "*" + queryItemDto.getQueryValue() + "*"));
                    } else if (queryItemDto.getQueryType() == 3) {
                        Map<Object, Object> mapQueryValue = handleQueryItemsDto(queryItemDto.getQueryValue());
                        mapQueryValue.forEach((key, value) -> {
                            if (key != null && value != null) {
                                queryBuilderItem.must(QueryBuilders.rangeQuery(queryItemDto.getParameter()).gte(key).lte(value));
                            } else if (key != null) {
                                queryBuilderItem.must(QueryBuilders.rangeQuery(queryItemDto.getParameter()).gte(key));
                            } else if (value != null) {
                                queryBuilderItem.must(QueryBuilders.rangeQuery(queryItemDto.getParameter()).lte(value));
                            }
                        });
                    } else if (queryItemDto.getQueryType() == 4) {
                        queryBuilder.mustNot(QueryBuilders.termQuery(queryItemDto.getParameter(), queryItemDto.getQueryValue()));
                    } else if (queryItemDto.getQueryType() == 5) {
                        queryBuilderItem.must(QueryBuilders.matchQuery(queryItemDto.getParameter(), queryItemDto.getQueryValue()));
                    }
                });
                queryBuilder.should(queryBuilderItem);
            });
        }
        return queryBuilder;
    }


    private BoolQueryBuilder fillQueryBuilderQueryItems(Integer logicalOperator, List<QueryItemsDto> queryItems) {
        //1与2或
        BoolQueryBuilder queryBuilderItem = QueryBuilders.boolQuery();
        if (logicalOperator == 2) {
            queryItems.forEach(queryItemDto -> {
                //查询类型，精确1 termQuery、模糊2 wildcardQuery、范围3 rangeQuery
                if (queryItemDto.getQueryType() == 1) {
                    queryBuilderItem.should(QueryBuilders.termQuery(queryItemDto.getParameter(), queryItemDto.getQueryValue()));
                } else if (queryItemDto.getQueryType() == 2) {
                    queryBuilderItem.should(QueryBuilders.wildcardQuery(queryItemDto.getParameter(), "*" + queryItemDto.getQueryValue() + "*"));
                } else if (queryItemDto.getQueryType() == 3) {
                    Map<Object, Object> mapQueryValue = handleQueryItemsDto(queryItemDto.getQueryValue());
                    mapQueryValue.forEach((key, value) -> {
                        if (key != null && value != null) {
                            queryBuilderItem.should(QueryBuilders.rangeQuery(queryItemDto.getParameter()).gte(key).lte(value));
                        } else if (key != null) {
                            queryBuilderItem.should(QueryBuilders.rangeQuery(queryItemDto.getParameter()).gte(key));
                        } else if (value != null) {
                            queryBuilderItem.should(QueryBuilders.rangeQuery(queryItemDto.getParameter()).lte(value));
                        }
                    });
                } else if (queryItemDto.getQueryType() == 4) {
                    queryBuilderItem.should(QueryBuilders.boolQuery().mustNot(QueryBuilders.termQuery(queryItemDto.getParameter(), queryItemDto.getQueryValue())));
                } else if (queryItemDto.getQueryType() == 5) {
                    queryBuilderItem.should(QueryBuilders.matchQuery(queryItemDto.getParameter(), queryItemDto.getQueryValue()));
                }
            });
        } else {
            queryItems.forEach(queryItemDto -> {
                //查询类型，精确1 termQuery、模糊2 wildcardQuery、范围3 rangeQuery
                if (queryItemDto.getQueryType() == 1) {
                    queryBuilderItem.must(QueryBuilders.termQuery(queryItemDto.getParameter(), queryItemDto.getQueryValue()));
                } else if (queryItemDto.getQueryType() == 2) {
                    queryBuilderItem.must(QueryBuilders.wildcardQuery(queryItemDto.getParameter(), queryItemDto.getQueryValue()));
                } else if (queryItemDto.getQueryType() == 3) {
                    Map<Object, Object> mapQueryValue = handleQueryItemsDto(queryItemDto.getQueryValue());
                    mapQueryValue.forEach((key, value) -> {
                        if (key != null && value != null) {
                            queryBuilderItem.must(QueryBuilders.rangeQuery(queryItemDto.getParameter()).gte(key).lte(value));
                        } else if (key != null) {
                            queryBuilderItem.must(QueryBuilders.rangeQuery(queryItemDto.getParameter()).gte(key));
                        } else if (value != null) {
                            queryBuilderItem.must(QueryBuilders.rangeQuery(queryItemDto.getParameter()).lte(value));
                        }
                    });
                } else if (queryItemDto.getQueryType() == 4) {
                    queryBuilderItem.mustNot(QueryBuilders.termQuery(queryItemDto.getParameter(), queryItemDto.getQueryValue()));
                } else if (queryItemDto.getQueryType() == 5) {
                    queryBuilderItem.must(QueryBuilders.matchQuery(queryItemDto.getParameter(), queryItemDto.getQueryValue()));
                }
            });
        }
        return queryBuilderItem;
    }
}
