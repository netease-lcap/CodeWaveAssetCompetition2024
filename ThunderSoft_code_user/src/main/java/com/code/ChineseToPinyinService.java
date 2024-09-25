package com.code;

import com.netease.lowcode.core.annotation.NaslLogic;
import net.sourceforge.pinyin4j.PinyinHelper;

/**
 * @author: cfn
 * @date: 2024/5/9 13:52
 * @description: 汉语转拼音
 */
public class ChineseToPinyinService {

	/**
	 * 汉字转拼音
	 *
	 * @param chinese 中文
	 * @return
	 */
	@NaslLogic
	public static String getPinyin(String chinese) {
		// 设置不带声调的输出选项
		net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat format = new net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat();
		format.setToneType(net.sourceforge.pinyin4j.format.HanyuPinyinToneType.WITHOUT_TONE);
		StringBuilder output = new StringBuilder();
		for (char c : chinese.toCharArray()) {
			try {
				String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c, format);
				if (pinyinArray != null && pinyinArray.length > 0) {
					output.append(pinyinArray[0]);
				} else {
					output.append(c);
				}
			} catch (Exception e) {
				output.append(c);
			}
		}
		return output.toString();
	}

}
