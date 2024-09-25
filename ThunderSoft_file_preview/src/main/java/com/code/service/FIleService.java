package com.code.service;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.code.config.DHS3Config;
import com.code.entity.AwsUtil;
import com.code.entity.PreviewVo;
import com.netease.lowcode.core.annotation.NaslLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.*;

/**
 * @author: zhouzz
 * @date: 2024/3/14 11:42
 * @description: 文件预览
 */

@Service
public class FIleService {
	private static final Logger logger = LoggerFactory.getLogger("LCAP_EXTENSION_LOGGER");

	@Autowired
	private AwsUtil awsUtil;

	@Resource
	private DHS3Config dhs3Config;

	public static  final String PREVIEW_URL ="{}/SynapDocViewServer/viewer/doc.html?key={}&convType=html&convLocale=en_US&contextPath=/SynapDocViewServer/";

	/**
	 * @param objectKey s3 objectKey返回文件名
	 * @param watermarkText 水印文字
	 */
	@NaslLogic
	public PreviewVo getPreviewVo(String objectKey,String watermarkText) {
		logger.info("文件预览开始:{},{}",objectKey,watermarkText);
		String domainUrl = dhs3Config.previewDomainUrl;

		PreviewVo vo = new PreviewVo();
		try {
			//获取文件地址
			String preJsonUrl = domainUrl.concat("/SynapDocViewServer/jobJson");
			String tempUrl = awsUtil.downloadFile(objectKey, 30);
			logger.info("文件预览tempUrl:{}", tempUrl);
			tempUrl = tempUrl.replace("https://", "http://");

			//请求水印
			Map<String, Object> map = new HashMap<>();
			map.put("fid", UUID.randomUUID());
			map.put("filePath", tempUrl);
			map.put("convertType", 0);
			map.put("fileType", "URL");
			map.put("format", "PDF");
			map.put("watermarkText", watermarkText);
			String keyJson = HttpUtil.get(preJsonUrl, map);
			logger.info("文件预览keyJson:{}", keyJson);
			if (!JSONUtil.isJson(keyJson)) {
				throw new RuntimeException("预览失败" + keyJson);
			}

			JSONObject preViewObj = JSONUtil.parseObj(keyJson);
			String preKey = preViewObj.getStr("key", "");
			String previewUrl = StrUtil.format(PREVIEW_URL, domainUrl, preKey);
			vo.setPreviewUrl(previewUrl);
			vo.setObjectKey(objectKey);
			vo.setFileName(objectKey);
		}catch (Exception e){
			logger.error("文件预览异常:{}", e.getMessage());
		}
		return vo;
	}


}

