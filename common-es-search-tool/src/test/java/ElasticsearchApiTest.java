//import com.alibaba.fastjson2.JSONObject;
//import com.netease.lowcode.lib.api.ElasticsearchApi;
//import com.netease.lowcode.lib.api.config.CommonEsSearchConfig;
//import com.netease.lowcode.lib.api.dto.QueryItemsDto;
//import com.netease.lowcode.lib.api.dto.QueryItemsListDto;
//import com.netease.lowcode.lib.api.dto.QueryRequestParam;
//import com.netease.lowcode.lib.api.dto.QueryResultDto;
//import com.netease.lowcode.lib.api.spring.CommonEsSearchSpringEnvironmentConfiguration;
//import com.netease.lowcode.lib.api.util.RestHighLevelClientFactory;
//import org.elasticsearch.action.search.ClearScrollRequest;
//import org.elasticsearch.action.search.ClearScrollResponse;
//import org.elasticsearch.client.RequestOptions;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.io.IOException;
//import java.util.*;
//
//
////@SpringBootTest(classes = CommonEsSearchSpringEnvironmentConfiguration.class)
////@RunWith(SpringRunner.class)
//public class ElasticsearchApiTest {
//    @Autowired
//    private ElasticsearchApi elasticsearchApi;
//    @Autowired
//    private CommonEsSearchConfig commonEsSearchConfig;
//
//    @Test
//    public void single() {
//        QueryRequestParam queryRequestParam = new QueryRequestParam();
//        queryRequestParam.setIndex("test_*");
//        queryRequestParam.setPageSize(10);
//        queryRequestParam.setPageIndex(1);
//
//        //内或
//        List<QueryItemsDto> queryResultDtoList = new ArrayList<>();
//        QueryItemsDto queryItemsDto = new QueryItemsDto();
//        queryItemsDto.setQueryType(1);
//        queryItemsDto.setParameter("extend21");
//        queryItemsDto.setQueryValue("5");
//        queryResultDtoList.add(queryItemsDto);
//
//        queryRequestParam.logicalOperator = 1;
//
//        queryRequestParam.queryItems = queryResultDtoList;
//
//        QueryResultDto queryResultDto = elasticsearchApi.query(queryRequestParam);
//        System.out.println(JSONObject.toJSONString(queryResultDto));
//        if (queryResultDto != null && queryResultDto.totalCount != null) {
//            System.out.println(JSONObject.toJSONString(queryResultDto.totalCount));
//        }
//    }
//
//    @Test
//    public void huo() {
//        QueryRequestParam queryRequestParam = new QueryRequestParam();
//        queryRequestParam.setIndex("bak_init_wuliao_zhubiao*");
//        queryRequestParam.setPageSize(10);
//        queryRequestParam.setPageIndex(1);
//
//        //内或
//        List<QueryItemsDto> queryResultDtoList = new ArrayList<>();
//        QueryItemsDto queryItemsDto = new QueryItemsDto();
//        queryItemsDto.setQueryType(1);
//        queryItemsDto.setParameter("extend21");
//        queryItemsDto.setQueryValue("3");
//        queryResultDtoList.add(queryItemsDto);
//        QueryItemsDto queryItemsDto1 = new QueryItemsDto();
//        queryItemsDto1.setQueryType(1);
//        queryItemsDto1.setParameter("extend21");
//        queryItemsDto1.setQueryValue("2");
//        queryResultDtoList.add(queryItemsDto1);
//
//        queryRequestParam.logicalOperator = 2;
//
//        queryRequestParam.queryItems = queryResultDtoList;
//
//        QueryResultDto queryResultDto = elasticsearchApi.query(queryRequestParam);
//        System.out.println(JSONObject.toJSONString(queryResultDto));
//        if (queryResultDto != null && queryResultDto.totalCount != null) {
//            System.out.println(JSONObject.toJSONString(queryResultDto.totalCount));
//        }
//    }
//
//    @Test
//    public void yuhuo() {
//        QueryRequestParam queryRequestParam = new QueryRequestParam();
//        queryRequestParam.setIndex("bak_init_wuliao_zhubiao*");
//        queryRequestParam.setPageSize(10);
//        queryRequestParam.setPageIndex(1);
//
//        //内或
//        List<QueryItemsDto> queryResultDtoList = new ArrayList<>();
//        QueryItemsDto queryItemsDto = new QueryItemsDto();
//        queryItemsDto.setQueryType(1);
//        queryItemsDto.setParameter("extend21");
//        queryItemsDto.setQueryValue("3");
//        queryResultDtoList.add(queryItemsDto);
//        QueryItemsDto queryItemsDto1 = new QueryItemsDto();
//        queryItemsDto1.setQueryType(1);
//        queryItemsDto1.setParameter("extend21");
//        queryItemsDto1.setQueryValue("2");
//        queryResultDtoList.add(queryItemsDto1);
//
//
//        List<QueryItemsListDto> queryItemsListDtoList = new ArrayList<>();
//        QueryItemsListDto queryItemsListDto = new QueryItemsListDto();
//        queryItemsListDto.queryItems = queryResultDtoList;
//        //外与
//        List<QueryItemsDto> queryResultDtoList1 = new ArrayList<>();
//        QueryItemsDto queryItemsDto11 = new QueryItemsDto();
//        queryItemsDto11.setQueryType(1);
//        queryItemsDto11.setParameter("itemName");
//        queryItemsDto11.setQueryValue("栏杆套筒");
//        queryResultDtoList1.add(queryItemsDto11);
//        QueryItemsListDto queryItemsListDto1 = new QueryItemsListDto();
//        queryItemsListDto1.queryItems = queryResultDtoList1;
//
//        queryItemsListDtoList.add(queryItemsListDto);
//        queryItemsListDtoList.add(queryItemsListDto1);
//        queryRequestParam.logicalOperator = 1;
//
//        queryRequestParam.queryItemList = queryItemsListDtoList;
//
//        QueryResultDto queryResultDto = elasticsearchApi.query(queryRequestParam);
//        System.out.println(JSONObject.toJSONString(queryResultDto));
//        if (queryResultDto != null && queryResultDto.totalCount != null) {
//            System.out.println(JSONObject.toJSONString(queryResultDto.totalCount));
//        }
//    }
//
//    // // @Test
//    public void huoyu() {
//        QueryRequestParam queryRequestParam = new QueryRequestParam();
//        queryRequestParam.setIndex("bak_init_wuliao_zhubiao*");
//        queryRequestParam.setPageSize(10);
//        queryRequestParam.setPageIndex(1);
//
//        List<QueryItemsListDto> queryItemsListDtoList = new ArrayList<>();
//
//        //内与
//        List<QueryItemsDto> queryResultDtoList = new ArrayList<>();
//        QueryItemsDto queryItemsDto1 = new QueryItemsDto();
//        queryItemsDto1.setQueryType(1);
//        queryItemsDto1.setParameter("itemName");
//        queryItemsDto1.setQueryValue("栏杆套筒");
//        queryResultDtoList.add(queryItemsDto1);
//        QueryItemsDto queryItemsDto2 = new QueryItemsDto();
//        queryItemsDto2.setQueryType(1);
//        queryItemsDto2.setParameter("itemOrderPostMemo");
//        queryItemsDto2.setQueryValue("印度ADANI MUNDRA T2码头 3台桥吊项目（第一批）");
//        queryResultDtoList.add(queryItemsDto2);
//        QueryItemsListDto queryItemsListDto = new QueryItemsListDto();
//        queryItemsListDto.queryItems = queryResultDtoList;
//        queryItemsListDtoList.add(queryItemsListDto);
//
//
//        //外或
//        List<QueryItemsDto> queryResultDtoList1 = new ArrayList<>();
//        QueryItemsDto queryItemsDto11 = new QueryItemsDto();
//        queryItemsDto11.setQueryType(1);
//        queryItemsDto11.setParameter("itemName");
//        queryItemsDto11.setQueryValue("软管套筒");
//        queryResultDtoList1.add(queryItemsDto11);
////        QueryItemsDto queryItemDto12 = new QueryItemsDto();
////        queryItemDto12.setQueryType(1);
////        queryItemDto12.setParameter("itemName");
////        queryItemDto12.setQueryValue("栏杆套筒");
////        queryResultDtoList1.add(queryItemDto12);
//        QueryItemsListDto queryItemsListDto1 = new QueryItemsListDto();
//        queryItemsListDto1.queryItems = queryResultDtoList1;
//
//        queryItemsListDtoList.add(queryItemsListDto1);
//        queryRequestParam.logicalOperator = 2;
//
//        queryRequestParam.queryItemList = queryItemsListDtoList;
//
//        QueryResultDto queryResultDto = elasticsearchApi.query(queryRequestParam);
//        System.out.println(JSONObject.toJSONString(queryResultDto));
//        if (queryResultDto != null && queryResultDto.totalCount != null) {
//            System.out.println(JSONObject.toJSONString(queryResultDto.totalCount));
//        }
//    }
//
//    @Test
//    public void test3Scroll() {
//        QueryRequestParam queryRequestParam = new QueryRequestParam();
//        queryRequestParam.setIndex("test_*");
////        queryRequestParam.setIndex("bak_init_wuliao_zhubiao*");
//        queryRequestParam.setPageSize(10);
//        queryRequestParam.setPageIndex(1);//kjeo_4wBgaj99AT2rCiQ
////        List<QueryItemsDto> queryResultDtoList = new ArrayList<>();
//////        QueryItemsDto queryItemsDto = new QueryItemsDto();
//////        queryItemsDto.setQueryType(1);
//////        queryItemsDto.setParameter("itemName");
//////        queryItemsDto.setQueryValue(null);
//////        queryResultDtoList.add(queryItemsDto);
////        QueryItemsDto queryItemDto1 = new QueryItemsDto();
////        queryItemDto1.setQueryType(1);
////        queryItemDto1.setParameter("applyPurchaseDep");
////        queryItemDto1.setQueryValue("液压技术研究所设计一处");
////        queryResultDtoList.add(queryItemDto1);
////        queryRequestParam.queryItems = queryResultDtoList;
//        queryRequestParam.scrollTime = 5;
//        QueryResultDto queryResultDto = elasticsearchApi.query(queryRequestParam);
//        System.out.println(JSONObject.toJSONString(queryResultDto));
//        System.out.println(queryResultDto.scrollId);
//    }
//
//    @Test
//    public void testScrollDelete() throws IOException {
//        String url = commonEsSearchConfig.getEsClientUris();
//        RestHighLevelClient client = RestHighLevelClientFactory.getClient(url);
//        ClearScrollRequest request = new ClearScrollRequest();
//        request.addScrollId("");
//        ClearScrollResponse clearScrollResponse = client.clearScroll(request, RequestOptions.DEFAULT);
//        client.close();
//        System.out.println(JSONObject.toJSONString(clearScrollResponse));
//    }
//
//    @Test
//    public void test3ScrollJson() {
//        QueryRequestParam queryRequestParam = JSONObject.parseObject("{\"index\":\"test_*\",\"pageIndex\":1,\"pageSize\":1000,\"queryItems\":[{\"parameter\":\"extend21\",\"queryType\":4,\"queryValue\":\"5\"}],\"scrollTime\":30}", QueryRequestParam.class);
////        queryRequestParam.setScrollTime(30);
//        QueryResultDto queryResultDto = elasticsearchApi.query(queryRequestParam);
//        System.out.println(JSONObject.toJSONString(queryResultDto));
//    }
//
//
//    // @Test
//    public void test3ScrollOther() {
//        QueryResultDto queryResultDto = elasticsearchApi.queryScroll(5, "FGluY2x1ZGVfY29udGV4dF91dWlkDnF1ZXJ5VGhlbkZldGNoAxR4QnBucW84QlNtZFBVSlgyRy1WUQAAAAAg2UtwFlZIaGpJVS0zUWJhbWV5Zmd2ZEVtS0EUeFJwbnFvOEJTbWRQVUpYMkctVlEAAAAAINlLcRZWSGhqSVUtM1FiYW1leWZndmRFbUtBFFVwMW5xbzhCdG1TUFoyVVNHeVJRAAAAAAKZUbcWVVJ1a2tKVTZUcnFaUEJtd1VfdnJ4QQ==");
//        System.out.println(JSONObject.toJSONString(queryResultDto));
//    }
//
//    @Test
//    public void updatebyquery() {
////            [
////        "_id" -> "5dc3a1e0-fe88-4e53-8096-73176ac5ed7c"
////]，更新es：[
////        "extend21" -> "1"
////]index:bak_init_wuliao_zhubiao*,queryFields:{"_id":"JuIB9okBKAV6fKPLaeTC"},updateFields:{"extend21":"1"}
//
//        Map<String, List<String>> queryFields = new HashMap();
//        queryFields.put("_id", Arrays.asList("EFEB9okBF0ozeNo6pHbd", "5dc3a1e0-fe88-4e53-8096-73176ac5ed7c", "1", "-kz_9YkBF0ozeNo6mAi1"));
//        Map updateFields = new HashMap();
//        updateFields.put("extend21", "1");
//        System.out.println(elasticsearchApi.updateDocumentByFields("bak_init_wuliao_zhubiao*", queryFields, updateFields));
//    }
//
//
//    // @Test
//    public void test3() {
//        QueryRequestParam queryRequestParam = new QueryRequestParam();
//        queryRequestParam.setIndex("test_*");
////        queryRequestParam.setIndex("bak_init_wuliao_zhubiao*");
//        queryRequestParam.setPageSize(10);
//        queryRequestParam.setPageIndex(1);
//        queryRequestParam.logicalOperator = 1;
//        List<QueryItemsDto> queryResultDtoList = new ArrayList<>();
//        QueryItemsDto queryItemDto = new QueryItemsDto();
//        queryItemDto.setQueryType(3);
//        queryItemDto.setParameter("extendDate1");
//        queryItemDto.setQueryValue(",1716555001923");
//        queryResultDtoList.add(queryItemDto);
//        QueryItemsDto queryItemDto6 = new QueryItemsDto();
//        queryItemDto6.setQueryType(3);
//        queryItemDto6.setParameter("itemPlanIssueDate");
//        queryItemDto6.setQueryValue(",1716555001923");
//        queryResultDtoList.add(queryItemDto6);
//
//        QueryItemsDto queryItemDto1 = new QueryItemsDto();
//        queryItemDto1.setQueryType(3);
//        queryItemDto1.setParameter("itemShippingDate");
//        queryItemDto1.setQueryValue(",1716555001923");
//        queryResultDtoList.add(queryItemDto1);
//
//        QueryItemsDto queryItemDto2 = new QueryItemsDto();
//        queryItemDto2.setQueryType(3);
//        queryItemDto2.setParameter("extendDate4");
//        queryItemDto2.setQueryValue(",1716555001923");
//        queryResultDtoList.add(queryItemDto2);
//
//        QueryItemsDto queryItemDto3 = new QueryItemsDto();
//        queryItemDto3.setQueryType(3);
//        queryItemDto3.setParameter("itemPlanDeliveryDate");
//        queryItemDto3.setQueryValue(",1716555001923");
//        queryResultDtoList.add(queryItemDto3);
//
//        QueryItemsDto queryItemDto4 = new QueryItemsDto();
//        queryItemDto4.setQueryType(3);
//        queryItemDto4.setParameter("itemPlanArrdate");
//        queryItemDto4.setQueryValue(",1716555001923");
//        queryResultDtoList.add(queryItemDto4);
//
//        QueryItemsDto queryItemDto5 = new QueryItemsDto();
//        queryItemDto5.setQueryType(3);
//        queryItemDto5.setParameter("itemReceivedDate");
//        queryItemDto5.setQueryValue(",1716555001923");
//        queryResultDtoList.add(queryItemDto5);
//        queryRequestParam.queryItems = queryResultDtoList;
////        queryRequestParam.scrollTime = 5;
//        QueryResultDto queryResultDto = elasticsearchApi.query(queryRequestParam);
//        System.out.println(JSONObject.toJSONString(queryResultDto));
//        if (queryResultDto != null && queryResultDto.totalCount != null) {
//            System.out.println(JSONObject.toJSONString(queryResultDto.totalCount));
//        }
//    }
//}