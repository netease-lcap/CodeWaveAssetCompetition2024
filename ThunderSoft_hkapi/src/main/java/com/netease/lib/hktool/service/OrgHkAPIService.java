package com.netease.lib.hktool.service;

import com.alibaba.fastjson.JSON;
import com.hikvision.artemis.sdk.ArtemisHttpUtil;
import com.hikvision.artemis.sdk.config.ArtemisConfig;
import com.netease.lib.hktool.config.HKapiConfig;
import com.netease.lib.hktool.reslut.ResponseEntity;
import com.netease.lib.hktool.structure.OrgDeleteDto;
import com.netease.lib.hktool.structure.OrgDto;
import com.netease.lib.hktool.structure.OrgQueryDto;
import com.netease.lowcode.core.annotation.NaslLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 组织管理api
 */
@Component
public class OrgHkAPIService {
    @Autowired
    private HKapiConfig hKapiConfig;

    /**
     * 添加参建单位（组织）
     * 接口实际url:/api/ccms/api/basic/v2/units/save
     *
     * @return
     */

    @NaslLogic(enhance = false)
    public ResponseEntity unitsSave(List<OrgDto> listOrg) {
        ResponseEntity responseEntity = new ResponseEntity();
        ArtemisConfig config = getArtemisConfig();
        String getCamsApi = hKapiConfig.getArtemisPath() + "/api/ccms/api/basic/v2/units/save";
        String body = JSON.toJSON(listOrg).toString();
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
     * 删除参建单位（组织）
     * 接口实际url:/api/ccms/basic/v2/units/deletion
     *
     * @return
     */

    @NaslLogic(enhance = false)
    public ResponseEntity unitsDeletion(OrgDeleteDto orgDeleteDto) {
        ResponseEntity responseEntity = new ResponseEntity();
        ArtemisConfig config = getArtemisConfig();
        String getCamsApi = hKapiConfig.getArtemisPath() + "/api/ccms/basic/v2/units/deletion";
        String body = JSON.toJSON(orgDeleteDto).toString();
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
     * 更新参建单位（组织）
     * /api/ccms/basic/v2/units/update
     *
     * @return
     */
    @NaslLogic(enhance = false)
    public ResponseEntity unitsUpdate(List<OrgDto> listOrg) {
        ResponseEntity responseEntity = new ResponseEntity();
        ArtemisConfig config = getArtemisConfig();
        String getCamsApi = hKapiConfig.getArtemisPath() + "/api/ccms/basic/v2/units/update";
        String body = JSON.toJSON(listOrg).toString();
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
     * 查询参建单位（组织）
     *  /api/ccms/basic/v2/units/query
     *
     * @return
     */
    @NaslLogic(enhance = false)
    public ResponseEntity unitsQuery(OrgQueryDto orgQueryDto) {
        ResponseEntity responseEntity = new ResponseEntity();
        ArtemisConfig config = getArtemisConfig();
        String getCamsApi = hKapiConfig.getArtemisPath() + "/api/ccms/basic/v2/units/query";
        String body = JSON.toJSON(orgQueryDto).toString();
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
        config.setHost(hKapiConfig.getHost()); // 代理API网关nginx服务器ip端口
        config.setAppKey(hKapiConfig.getAppKey());  // 秘钥appkey
        config.setAppSecret(hKapiConfig.getAppSecret());// 秘钥appSecret
        return config;
    }


}
