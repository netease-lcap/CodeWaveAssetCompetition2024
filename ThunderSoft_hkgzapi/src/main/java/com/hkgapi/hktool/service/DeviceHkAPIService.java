package com.hkgapi.hktool.service;

import com.alibaba.fastjson.JSON;
import com.hikvision.artemis.sdk.ArtemisHttpUtil;
import com.hikvision.artemis.sdk.config.ArtemisConfig;
import com.hkgapi.hktool.config.HKGapiConfig;
import com.hkgapi.hktool.param.QueryInfo;
import com.hkgapi.hktool.reslut.ResponseEntity;
import com.hkgapi.hktool.structure.AddRuleDto;
import com.hkgapi.hktool.structure.AppPrivilegeDto;
import com.hkgapi.hktool.structure.EventInfoDto;
import com.hkgapi.hktool.structure.QueryInfoDto;
import com.netease.lowcode.core.annotation.NaslLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设备模块api
 */
@Component
public class DeviceHkAPIService {
    @Autowired
    private HKGapiConfig hKGapiConfig;

    /**
     * 查询应用有权限的设备列表
     * 接口实际url:/api/iotrm/v1/device/appPrivilege/page
     *
     * @return
     */

    @NaslLogic(enhance = false)
    public ResponseEntity appPrivilegePage(AppPrivilegeDto appPrivilegeDto) {
        ResponseEntity responseEntity = new ResponseEntity();
        ArtemisConfig config = getArtemisConfig();
        String getCamsApi = hKGapiConfig.getArtemisPath() + "/api/iotrm/v1/device/appPrivilege/page";
        String body = JSON.toJSON(appPrivilegeDto).toString();
        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put("https://", getCamsApi);
            }
        };
        String reslut = null;
        try {
            reslut = ArtemisHttpUtil.doPostStringArtemis(config, path, body, null, null, "application/json");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (reslut != null) {
            responseEntity = JSON.parseObject(reslut, ResponseEntity.class);
        }
        return responseEntity;
    }

    /**
     * 查询历史事件数据
     * 接口实际url:/api/iotrm/v1/eventInfo/queryEvent
     *
     * @return
     */

    @NaslLogic(enhance = false)
    public ResponseEntity deviceAdd(EventInfoDto eventInfoDto) {
        ResponseEntity responseEntity = new ResponseEntity();
        ArtemisConfig config = getArtemisConfig();
        String getCamsApi = hKGapiConfig.getArtemisPath() + "/api/iotrm/v1/eventInfo/queryEvent";
        String body = JSON.toJSON(eventInfoDto).toString();
        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put("https://", getCamsApi);
            }
        };
        String reslut = null;
        try {
            reslut = ArtemisHttpUtil.doPostStringArtemis(config, path, body, null, null, "application/json");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (reslut != null) {
            responseEntity = JSON.parseObject(reslut, ResponseEntity.class);
        }
        return responseEntity;
    }



    /**
     * 设置数据转发规则
     * 接口实际url:/api/openapi/v1/sub/add/rule
     *
     * @return
     */

    @NaslLogic(enhance = false)
    public ResponseEntity addRule(AddRuleDto addRuleDto) {
        ResponseEntity responseEntity = new ResponseEntity();
        ArtemisConfig config = getArtemisConfig();
        String getCamsApi = hKGapiConfig.getArtemisPath() + "/api/openapi/v1/sub/add/rule";
        String body = JSON.toJSON(addRuleDto).toString();
        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put("https://", getCamsApi);
            }
        };
        String reslut = null;
        try {
            reslut = ArtemisHttpUtil.doPostStringArtemis(config, path, body, null, null, "application/json");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (reslut != null) {
            responseEntity = JSON.parseObject(reslut, ResponseEntity.class);
        }
        return responseEntity;
    }

    /**
     * 删除数据转发规则
     * 接口实际url: /api/v1/sub/delete
     *
     * @return
     */

    @NaslLogic(enhance = false)
    public ResponseEntity subDelete(List<String> ids) {
        ResponseEntity responseEntity = new ResponseEntity();
        ArtemisConfig config = getArtemisConfig();
        String getCamsApi = hKGapiConfig.getArtemisPath() + "/api/v1/sub/delete";
        String body = JSON.toJSON(ids).toString();
        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put("https://", getCamsApi);
            }
        };
        String reslut = null;
        try {
            reslut = ArtemisHttpUtil.doPostStringArtemis(config, path, body, null, null, "application/json");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (reslut != null) {
            responseEntity = JSON.parseObject(reslut, ResponseEntity.class);
        }
        return responseEntity;
    }

    /**
     * 查询数据转发规则
     * 接口实际url: /api/openapi/v1/sub/queryInfo
     *
     * @return
     */

    @NaslLogic(enhance = false)
    public ResponseEntity subQueryInfo(QueryInfoDto queryInfoDto) {
        ResponseEntity responseEntity = new ResponseEntity();
        ArtemisConfig config = getArtemisConfig();
        String getCamsApi = hKGapiConfig.getArtemisPath() + "/api/openapi/v1/sub/queryInfo";
        String body = JSON.toJSON(queryInfoDto).toString();
        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put("https://", getCamsApi);
            }
        };
        String reslut = null;
        try {
            reslut = ArtemisHttpUtil.doPostStringArtemis(config, path, body, null, null, "application/json");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (reslut != null) {
            responseEntity = JSON.parseObject(reslut, ResponseEntity.class);
        }
        return responseEntity;
    }


    private ArtemisConfig getArtemisConfig() {
        ArtemisConfig config = new ArtemisConfig();
        config.setHost(hKGapiConfig.getHost()); // 代理API网关nginx服务器ip端口
        config.setAppKey(hKGapiConfig.getAppKey());  // 秘钥appkey
        config.setAppSecret(hKGapiConfig.getAppSecret());// 秘钥appSecret
        return config;
    }


//    public static void main(String[] args) {
//        QueryInfo queryInfo = new QueryInfo();
//
//        queryInfo.setKey("ss");
//        queryInfo.setOperatorT("123");
//        queryInfo.setValue("sdf");
//        String body = JSON.toJSON(queryInfo).toString();
//        System.out.println(body);
//    }

}
