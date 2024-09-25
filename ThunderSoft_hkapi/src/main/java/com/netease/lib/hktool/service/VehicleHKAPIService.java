package com.netease.lib.hktool.service;

import com.alibaba.fastjson.JSON;
import com.hikvision.artemis.sdk.ArtemisHttpUtil;
import com.hikvision.artemis.sdk.config.ArtemisConfig;
import com.netease.lib.hktool.config.HKapiConfig;
import com.netease.lib.hktool.reslut.ResponseEntity;
import com.netease.lib.hktool.structure.CarBindDto;
import com.netease.lib.hktool.structure.CarFeeDto;
import com.netease.lib.hktool.structure.EventSubDto;
import com.netease.lib.hktool.structure.VehicleDto;
import com.netease.lowcode.core.annotation.NaslLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class VehicleHKAPIService {
    @Autowired
    private HKapiConfig hKapiConfig;
    /**
     * 批量车辆增加接口
     *
     * @return
     */

    @NaslLogic(enhance = false)
    public ResponseEntity vehicleAdd(List<VehicleDto> vehicleDtos) {
        ResponseEntity responseEntity = new ResponseEntity();
        ArtemisConfig config = getArtemisConfig();
        String getCamsApi = hKapiConfig.getArtemisPath() + "/api/resource/v1/vehicle/batch/add";
        String body = JSON.toJSONString(vehicleDtos);
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
     * 车辆修改接口
     *
     * @return
     */

    @NaslLogic(enhance = false)
    public ResponseEntity vehicleUpdate(VehicleDto vehicleDto) {
        ResponseEntity responseEntity = new ResponseEntity();
        ArtemisConfig config = getArtemisConfig();
        String getCamsApi = hKapiConfig.getArtemisPath() + "/api/resource/v1/vehicle/single/update";
        String body = JSON.toJSONString(vehicleDto);
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
     * 批量车辆删除接口
     *
     * @return
     */
    @NaslLogic(enhance = false)
    public ResponseEntity vehicleDelete(VehicleDto vehicleDto) {
        ResponseEntity responseEntity = new ResponseEntity();
        ArtemisConfig config = getArtemisConfig();
        String getCamsApi = hKapiConfig.getArtemisPath() + "/api/resource/v1/vehicle/batch/delete";
        String body = JSON.toJSONString(vehicleDto);
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
     * 固定车管理（停车场服务）
     *
     * @return
     */
    @NaslLogic(enhance = false)
    public ResponseEntity carCharge(CarFeeDto carFeeDto) {
        ResponseEntity responseEntity = new ResponseEntity();
        ArtemisConfig config = getArtemisConfig();
        String getCamsApi = hKapiConfig.getArtemisPath() + "/api/pms/v1/car/charge";
        String body = JSON.toJSONString(carFeeDto);
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
     * 车辆群组绑定
     *
     * @return
     */
    @NaslLogic(enhance = false)
    public ResponseEntity carCategoryBind(CarBindDto carBindDto) {
        ResponseEntity responseEntity = new ResponseEntity();
        ArtemisConfig config = getArtemisConfig();
        String getCamsApi = hKapiConfig.getArtemisPath() + "/api/pms/v1/car/categoryBind";
        String body = JSON.toJSONString(carBindDto);
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
     * 按事件类型订阅事件
     * /api/eventService/v1/eventSubscriptionByEventTypes
     * @return
     */
    @NaslLogic(enhance = false)
    public ResponseEntity eventSubscriptionByEventTypes(EventSubDto eventSubDto) {
        ResponseEntity responseEntity = new ResponseEntity();
        ArtemisConfig config = getArtemisConfig();
        String getCamsApi = hKapiConfig.getArtemisPath() + "/api/eventService/v1/eventSubscriptionByEventTypes";
        String body = JSON.toJSONString(eventSubDto);
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
