package com.netease.lib.hktool.service;

import com.alibaba.fastjson.JSON;
import com.hikvision.artemis.sdk.ArtemisHttpUtil;
import com.hikvision.artemis.sdk.config.ArtemisConfig;
import com.netease.lib.hktool.config.HKapiConfig;
import com.netease.lib.hktool.structure.OrgDeleteDto;
import com.netease.lib.hktool.structure.OrgDto;
import com.netease.lib.hktool.structure.RegionDto;
import com.netease.lib.hktool.structure.RegionQueryDto;
import com.netease.lib.hktool.reslut.ResponseEntity;
import com.netease.lowcode.core.annotation.NaslLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 区域api
 */
@Component
public class RegionHkAPIService {
    @Autowired
    private HKapiConfig hKapiConfig;
    private static final org.slf4j.Logger log=org.slf4j.LoggerFactory.getLogger(RegionHkAPIService.class);


    /**
     * 添加区域
     * 接口实际 /api/ccms/basic/v2/regions/save
     *
     * @return
     */

    @NaslLogic(enhance = false)
    public ResponseEntity regionsSave(List<RegionDto> regionDtos) {
        ResponseEntity responseEntity = new ResponseEntity();
        ArtemisConfig config = getArtemisConfig();
        String getCamsApi = hKapiConfig.getArtemisPath() + "/api/ccms/basic/v2/regions/save";
        String body = JSON.toJSON(regionDtos).toString();
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
     * 删除区域
     * /api/ccms/basic/v2/regions/deletion
     *
     * @return
     */

    @NaslLogic(enhance = false)
    public ResponseEntity regionsDeletion(OrgDeleteDto orgDeleteDto) {
        ResponseEntity responseEntity = new ResponseEntity();
        ArtemisConfig config = getArtemisConfig();
        String getCamsApi = hKapiConfig.getArtemisPath() + "/api/ccms/basic/v2/regions/deletion";
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
     * 更新区域
     * /api/ccms/basic/v2/regions/update
     *
     * @return
     */
    @NaslLogic(enhance = false)
    public ResponseEntity regionsUpdate(List<RegionDto> regionDtos) {
        ResponseEntity responseEntity = new ResponseEntity();
        ArtemisConfig config = getArtemisConfig();
        String getCamsApi = hKapiConfig.getArtemisPath() + "/api/ccms/basic/v2/regions/update";
        String body = JSON.toJSON(regionDtos).toString();
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
     * 查询区域
     *  /api/ccms/basic/v2/regions/query
     * @return
     */

    @NaslLogic(enhance = false)
    public ResponseEntity regionsQuery(RegionQueryDto regionDto) {
        ResponseEntity responseEntity = new ResponseEntity();
        ArtemisConfig config = getArtemisConfig();
        String getCamsApi = hKapiConfig.getArtemisPath() + "/api/ccms/basic/v2/regions/query";
        String body = JSON.toJSON(regionDto).toString();
        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put("https://", getCamsApi);
            }
        };

        String reslut = null;
        log.info("请求参数："+body);
        try {
            reslut = ArtemisHttpUtil.doPostStringArtemis(config, path, body, null, null, "application/json");
            log.info("查询区域返回值："+reslut);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("查询区域调用失败");
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
