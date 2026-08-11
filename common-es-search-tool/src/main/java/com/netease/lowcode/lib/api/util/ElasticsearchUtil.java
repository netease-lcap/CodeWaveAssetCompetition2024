package com.netease.lowcode.lib.api.util;

import com.alibaba.fastjson2.JSONObject;
import com.netease.lowcode.lib.api.dto.QueryResultDto;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class ElasticsearchUtil {
    private static final Logger log = LoggerFactory.getLogger("LCAP_EXTENSION_LOGGER");

    public static void initClient(String esClientUris, String esClientUsername, String esClientPassword) {
        RestHighLevelClientFactory.initClient(esClientUris, esClientUsername, esClientPassword);
    }

    public static void updateByFields(String index, Map<String, List<String>> queryFields, Map<String, String> updateFields, String url) {
        try {
            UpdateByQueryRequest updateByQueryRequest = new UpdateByQueryRequest(index);
            // 构建查询条件
            queryFields.forEach((field, value) -> updateByQueryRequest.setQuery(QueryBuilders.termsQuery(field, value)));
            StringBuilder scriptBuilder = new StringBuilder("ctx._source.putAll(params);");
            Map<String, Object> params = new HashMap<>(updateFields);
            Script script = new Script(ScriptType.INLINE, "painless", scriptBuilder.toString(), params);
            updateByQueryRequest.setScript(script);
            RestHighLevelClient client = RestHighLevelClientFactory.getClient(url);
            client.updateByQuery(updateByQueryRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.info("连接异常", e);
            throw new RuntimeException(e);
        }
    }

    public static QueryResultDto searchScroll(String url, String scrollTime, String scrollId) {
        QueryResultDto queryResultDto = null;
        try {
            // 创建一个搜索请求
            SearchScrollRequest searchScrollRequest = new SearchScrollRequest(scrollId);
            // 设置滚动时间
            searchScrollRequest.scroll(scrollTime);
            // 执行搜索请求
            SearchResponse searchResponse = RestHighLevelClientFactory.getClient(url).scroll(searchScrollRequest, RequestOptions.DEFAULT);
            SearchHit[] searchHits = searchResponse.getHits().getHits();
            log.info("result count:{}", searchHits.length);
            if (searchHits.length > 0) {
                queryResultDto = new QueryResultDto();
                queryResultDto.setTotalCount(searchResponse.getHits().getTotalHits());
                List<String> dataJsonString = new ArrayList<>();
                queryResultDto.dataJsonString = dataJsonString;
                for (SearchHit hit : searchHits) {
                    JSONObject jsonObject = JSONObject.parseObject(hit.getSourceAsString());
                    jsonObject.put("esId", hit.getId());
                    dataJsonString.add(JSONObject.toJSONString(jsonObject));
                }
                queryResultDto.scrollId = searchResponse.getScrollId();
            }
        } catch (Exception e) {
            log.info("连接异常", e);
            throw new RuntimeException(e);
        }
        return queryResultDto;
    }

    public static QueryResultDto search(String index, SearchSourceBuilder sourceBuilder, BoolQueryBuilder queryBuilder,
                                        Integer pageIndex, Integer pageSize, String url, String scrollTime) {
        QueryResultDto queryResultDto = null;
        try {
            // 创建一个搜索请求
            SearchRequest searchRequest = new SearchRequest();
            searchRequest.indices(index);
            if (scrollTime != null) {
                // 设置滚动时间
                searchRequest.scroll(scrollTime);
            }
            sourceBuilder.query(queryBuilder);
            searchRequest.source(sourceBuilder);
            log.info("sourceBuilder:{}", sourceBuilder);
            // 执行搜索请求
            SearchResponse searchResponse = RestHighLevelClientFactory.getClient(url).search(searchRequest, RequestOptions.DEFAULT);
            SearchHit[] searchHits = searchResponse.getHits().getHits();
            log.info("result count:{}", searchHits.length);
            if (searchHits.length > 0) {
                queryResultDto = new QueryResultDto();
                queryResultDto.setTotalCount(searchResponse.getHits().getTotalHits());
                queryResultDto.setPageSize(pageSize);
                queryResultDto.setPageIndex(pageIndex);
                queryResultDto.setTotalPage((int) Math.ceil((double) queryResultDto.getTotalCount() / pageSize));
                ;
                List<String> dataJsonString = new ArrayList<>();
                queryResultDto.setDataJsonString(dataJsonString);
                ;
                for (SearchHit hit : searchHits) {
                    JSONObject jsonObject = JSONObject.parseObject(hit.getSourceAsString());
                    jsonObject.put("esId", hit.getId());
                    dataJsonString.add(JSONObject.toJSONString(jsonObject));
                }
                queryResultDto.setScrollId(searchResponse.getScrollId());
            }
        } catch (Exception e) {
            log.info("连接异常", e);
            throw new RuntimeException(e);
        }
        return queryResultDto;
    }

    public static String saveDocument(Map<String, Object> doc, String url, String index) {
        RestHighLevelClient client = RestHighLevelClientFactory.getClient(url);
        int retryCount = 0;
        while (retryCount < 3) {
            try {
                IndexResponse response;
                if (doc.get("id") == null) {
                    response = client.index(
                            new IndexRequest(index, "_doc")
                                    .source(doc),
                            RequestOptions.DEFAULT
                    );
                } else {
                    response = client.index(
                            new IndexRequest(index, "_doc")
                                    .id(doc.get("id").toString())
                                    .source(doc),
                            RequestOptions.DEFAULT
                    );
                }
                if (response.status() == RestStatus.CREATED) {
                    return response.getId();
                }
            } catch (IOException e) {
                log.info("保存异常", e);
            } finally {
                retryCount++;
            }
        }
        throw new RuntimeException("保存文档三次重试失败");
    }

    public static Long count(String index, String url) throws IOException {
        // 创建CountRequest对象，指定目标索引
        CountRequest countRequest = new CountRequest(index);
        RestHighLevelClient client = RestHighLevelClientFactory.getClient(url);
// 执行计数请求
        CountResponse countResponse = client.count(countRequest, RequestOptions.DEFAULT);
// 获取文档总数
        return countResponse.getCount();
    }

    public static Boolean bulkSaveDocuments(List<Map<String, Object>> docs, String url, String index) {
        RestHighLevelClient client = RestHighLevelClientFactory.getClient(url);
        int retryCount = 0;
        while (retryCount < 3) {
            try {
                BulkRequest bulkRequest = new BulkRequest();
                for (Map<String, Object> doc : docs) {
                    if (doc.get("id") != null) {
                        bulkRequest.add(new IndexRequest(index, "_doc")
                                .id(doc.get("id").toString())
                                .source(doc));
                    } else {
                        bulkRequest.add(new IndexRequest(index, "_doc")
                                .source(doc));
                    }
                }
                BulkResponse response = client.bulk(bulkRequest, RequestOptions.DEFAULT);
                if (response.hasFailures()) {
                    // 遍历所有响应项
                    for (BulkItemResponse item : response.getItems()) {
                        if (item.isFailed()) {
                            // 获取失败详细信息
                            BulkItemResponse.Failure failure = item.getFailure();
                            log.info(" 失败操作：{},索引：{}，文档id：{}，失败原因：{}，异常码：{}，异常类型：{}"
                                    , item.getOpType(), failure.getIndex(), failure.getId(), failure.getMessage(),
                                    failure.getStatus().getStatus(), failure.getCause().getClass().getSimpleName());
                            // 获取完整的异常堆栈
                            failure.getCause().printStackTrace();
                        }
                    }
                    long failedCount = Arrays.stream(response.getItems())
                            .filter(BulkItemResponse::isFailed)
                            .count();
                    throw new RuntimeException("批量保存文档存在" + failedCount + "条失败情况");
                } else {
                    return true;
                }
            } catch (IOException e) {
                log.info("保存异常", e);
            } finally {
                retryCount++;
            }
        }
        throw new RuntimeException("批量保存文档三次重试失败");
    }
}
