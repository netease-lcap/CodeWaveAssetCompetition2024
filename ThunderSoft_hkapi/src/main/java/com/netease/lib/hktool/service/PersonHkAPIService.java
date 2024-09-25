package com.netease.lib.hktool.service;

import com.alibaba.fastjson.JSON;
import com.hikvision.artemis.sdk.ArtemisHttpUtil;
import com.hikvision.artemis.sdk.config.ArtemisConfig;
import com.netease.lib.hktool.config.HKapiConfig;
import com.netease.lib.hktool.reslut.ResponseEntity;
import com.netease.lib.hktool.structure.*;
import com.netease.lowcode.core.annotation.NaslLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 人员信息
 */
@Component
public class PersonHkAPIService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PersonHkAPIService.class);

    @Autowired
    private HKapiConfig hKapiConfig;

//    /**
//     * 单个添加，personID保证不重复，包括已经删除人员ID也不能重复
//     *
//     * @return
//     */
//
//    @NaslLogic
//    public ResponseEntity singleAdd(PersonDto personDto) {
//        ResponseEntity responseEntity = new ResponseEntity();
//        ArtemisConfig config = getArtemisConfig();
//        String getCamsApi = hKapiConfig.getArtemisPath() + "/api/resource/v2/person/single/add";
//        String body = JSON.toJSONString(personDto);
//        Map<String, String> path = new HashMap<String, String>(2) {
//            {
//                put("https://", getCamsApi);
//            }
//        };
//        String reslut = null;
//        try {
//            reslut = ArtemisHttpUtil.doPostStringArtemis(config, path, body, null, null, "application/json");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        if (reslut != null) {
//            responseEntity = JSON.parseObject(reslut, ResponseEntity.class);
//        }
//        return responseEntity;
//    }
//
//    /**
//     * 批量添加，personID保证不重复，包括已经删除人员ID也不能重复
//     *
//     * @param
//     * @return
//     */
//    @NaslLogic
//    public ResponseEntity batchAdd(List<PersonDto> personDtos) {
//        ResponseEntity responseEntity = new ResponseEntity();
//        ArtemisConfig config = getArtemisConfig();
//        String getCamsApi = hKapiConfig.getArtemisPath() + "/api/resource/v1/person/batch/add";
//        String body = JSON.toJSONString(personDtos);
//        Map<String, String> path = new HashMap<String, String>(2) {
//            {
//                put("https://", getCamsApi);
//            }
//        };
//        String reslut = null;
//        try {
//            reslut = ArtemisHttpUtil.doPostStringArtemis(config, path, body, null, null, "application/json");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        if (reslut != null) {
//            responseEntity = JSON.parseObject(reslut, ResponseEntity.class);
//        }
//        return responseEntity;
//    }

    /**
     * 创建人员信息，并下发到智慧工地
     *
     * @param
     * @return
     */
    @NaslLogic(enhance = false)
    public ResponseEntity personsSave(List<PersonDto> personDtos) {
        ResponseEntity responseEntity = new ResponseEntity();
        ArtemisConfig config = getArtemisConfig();
        String getCamsApi = hKapiConfig.getArtemisPath() + "/api/ccms/basic/v2/persons/save";
        String body = JSON.toJSONString(personDtos);
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
     * 下发人员权限到闸机
     *
     * @param
     * @return
     */
    @NaslLogic(enhance = false)
    public ResponseEntity authConfigAdd(AuthConfigDto authConfigDto) {
        ResponseEntity responseEntity = new ResponseEntity();
        ArtemisConfig config = getArtemisConfig();
        String getCamsApi = hKapiConfig.getArtemisPath() + "/api/acps/v1/auth_config/add";
        String body = JSON.toJSONString(authConfigDto);
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
     * 查询门禁点，选择门禁点类型
     *
     * @param
     * @return
     */
    @NaslLogic(enhance = false)
    public ResponseEntity doorSearch(DoorManangerDto doorManangerDto) {
        ResponseEntity responseEntity = new ResponseEntity();
        ArtemisConfig config = getArtemisConfig();
        String getCamsApi = hKapiConfig.getArtemisPath() + "/api/resource/v2/door/search";
        String body = JSON.toJSONString(doorManangerDto);
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
     * 查询人员对应的设备通道的权限配置和下载状态
     *
     * @return
     */
    @NaslLogic(enhance = false)
    public ResponseEntity authItemList(AuthItemDto authItemDto) {
        ResponseEntity responseEntity = new ResponseEntity();
        ArtemisConfig config = getArtemisConfig();
        String getCamsApi = hKapiConfig.getArtemisPath() + "/api/acps/v1/auth_item/list/search";
        String body = JSON.toJSONString(authItemDto);
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
     * 根据人员数据、设备通道删除已配置的权限，合作方配置的tagId用于让多个应用共用出入控制权限服务时，用以区分各自的配置信息，
     * 即只能删除同一个tagId的权限配置信息。入参中人员数据、设备通道至少一个不为空。
     *
     * @return
     */
    @NaslLogic(enhance = false)
    public ResponseEntity authConfigDelete(AuthConfigDto authConfigDto) {
        ResponseEntity responseEntity = new ResponseEntity();
        ArtemisConfig config = getArtemisConfig();
        String getCamsApi = hKapiConfig.getArtemisPath() + "/api/acps/v1/auth_config/delete";
        String body = JSON.toJSONString(authConfigDto);
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
     * 人脸更新接口，支持添加删除更新
     * /api/ccms/basic/v2/person/face/save
     *
     * @return
     */
    @NaslLogic(enhance = false)
    public ResponseEntity faceSave(PersonFaceDto personFaceDto) {
        ResponseEntity responseEntity = new ResponseEntity();
        ArtemisConfig config = getArtemisConfig();
        String getCamsApi = hKapiConfig.getArtemisPath() + "/api/ccms/basic/v2/person/face/save";
        String body = JSON.toJSON(personFaceDto).toString();
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
     *
     * 更新人员 V2
     * /api/ccms/basic/v2/persons/update
     * @return
     */
    @NaslLogic(enhance = false)
    public ResponseEntity personsUpdate(List<PersonDto> personDtos) {
        ResponseEntity responseEntity = new ResponseEntity();
        ArtemisConfig config = getArtemisConfig();
        String getCamsApi = hKapiConfig.getArtemisPath() + "/api/ccms/basic/v2/persons/update";
        String body = JSON.toJSONString(personDtos);
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
     *
     * 人员信息删除V2
     * /api/ccms/basic/v2/persons/deletion
     * @return
     */
    @NaslLogic(enhance = false)
    public ResponseEntity personsDeletion(OrgDeleteDto orgDeleteDtos) {
        ResponseEntity responseEntity = new ResponseEntity();
        ArtemisConfig config = getArtemisConfig();
        String getCamsApi = hKapiConfig.getArtemisPath() + "/api/ccms/basic/v2/persons/deletion";
        String body = JSON.toJSONString(orgDeleteDtos);
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
     *
     * 查询人员 V2
     * /api/ccms/basic/v2/persons/query
     * @return
     */
    @NaslLogic(enhance = false)
    public ResponseEntity personsQuery(PersonQueryDto personQueryDto) {
        ResponseEntity responseEntity = new ResponseEntity();
        ArtemisConfig config = getArtemisConfig();
        String getCamsApi = hKapiConfig.getArtemisPath() + "/api/ccms/basic/v2/persons/query";
        String body = JSON.toJSONString(personQueryDto);
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
