package com.netease.lib.hktool.service;

import com.alibaba.fastjson.JSON;
import com.hikvision.artemis.sdk.ArtemisHttpUtil;
import com.hikvision.artemis.sdk.config.ArtemisConfig;
import com.netease.lib.hktool.config.HKapiConfig;
import com.netease.lib.hktool.reslut.ResponseEntity;
import com.netease.lib.hktool.structure.ParkIndexCodesDto;
import com.netease.lowcode.core.annotation.NaslLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 停车库管理api
 */
@Component
public class ParkHKAPIService {
    @Autowired
    private HKapiConfig hKapiConfig;

    /**
     * 获取停车库列表
     * /api/resource/v1/park/parkList
     *
     * @return
     */

    @NaslLogic(enhance = false)
    public ResponseEntity parkList(ParkIndexCodesDto parkIndexCodesDto){
        ResponseEntity responseEntity = new ResponseEntity();
        ArtemisConfig config = getArtemisConfig();
        String getCamsApi = hKapiConfig.getArtemisPath() + "/api/resource/v1/park/parkList";
        String body = JSON.toJSON(parkIndexCodesDto).toString();
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
