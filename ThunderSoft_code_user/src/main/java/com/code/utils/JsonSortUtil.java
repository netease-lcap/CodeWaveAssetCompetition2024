package com.code.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.*;

/**
 * @author: cfn
 * @date: 2024/5/14 9:23
 * @description:
 */
public class JsonSortUtil {

	/**
	 * 对单词列表进行冒泡排序
	 * 直接操作对象地址 无需返回
	 *
	 * @param words ["name","age"]
	 */
	private static void wordSort(ArrayList<String> words) {
		for (int i = words.size() - 1; i > 0; i--) {
			for (int j = 0; j < i; j++) {
				if (words.get(j).compareToIgnoreCase(words.get(j + 1)) > 0) {
					String temp = words.get(j);
					words.set(j, words.get(j + 1));
					words.set(j + 1, temp);
				}
			}
		}
	}

	/**
	 * 对单层json排序
	 *
	 * @param json
	 */
	private static JSONObject getAloneKeys(JSONObject json) {
		ArrayList<String> aloneKeys = new ArrayList<>();
		for (String key : json.keySet()) {
			aloneKeys.add(key);
		}
		// 排序
		JsonSortUtil.wordSort(aloneKeys);
		// 整理排序后的json
		JSONObject newJson = new JSONObject(new LinkedHashMap<>());
		for (String key : aloneKeys) {
			newJson.put(key, json.get(key));
		}
		return newJson;
	}

	/**
	 * 递归每一层（当前是判断下一层是JSONObject类型的场景）
	 *
	 * @param json
	 * @return
	 */
	private static JSONObject getAloneKeysRec(JSONObject json) {
		JSONObject newJson = getAloneKeys(json);

		for (Map.Entry<String, Object> entry : newJson.entrySet()) {
			// JSONObject类型
			if (entry.getValue() != null && entry.getValue().getClass().equals(JSONObject.class)) {
				newJson.put(entry.getKey(), getAloneKeysRec((JSONObject) entry.getValue()));
			}
		}

		return newJson;
	}

	/**
	 * 对JSONObject的key根据首字母排序 若首字母相同对比下一个字母 依次类推
	 * 备注：当前未覆盖JSONArray的场景
	 *
	 * @param json
	 * @return 排序后的新json
	 */
	public static JSONObject startSort(JSONObject json) {
		// 第1层
		JSONObject jsonAlone = getAloneKeys(json);
		// 第2-n层
		for (Map.Entry<String, Object> entry : jsonAlone.entrySet()) {
			// 这里仅判断JSONObject类型的场景（若需要覆盖JSONArray场景 对应添加即可）
			if (entry.getValue().getClass().equals(JSONObject.class)) {
				jsonAlone.put(entry.getKey(), getAloneKeysRec((JSONObject) entry.getValue()));
			}
		}
		return jsonAlone;
	}

	/**
	 * 对JSONObject的key根据首字母排序 若首字母相同对比下一个字母 依次类推
	 * 备注：当前未覆盖JSONArray的场景
	 *
	 * @param jsonObject
	 * @return 排序后的新json
	 */
	public static JSONObject sortJSONObject(JSONObject jsonObject) {
		// Create a TreeMap with a custom comparator to sort keys by alphabetical order
		Map<String, Object> sortedMap = new TreeMap<>(new Comparator<String>() {
			@Override
			public int compare(String key1, String key2) {
				// Compare keys lexicographically
				return key1.compareTo(key2);
			}
		});

		// Populate the TreeMap with the entries from the original JSONObject
		for (String key : jsonObject.keySet()) {
			Object value = jsonObject.get(key);
			if (value instanceof JSONObject) {
				// Recursively sort nested JSONObject
				value = sortJSONObject((JSONObject) value);
			} else if (value instanceof JSONArray) {
				// Recursively sort JSONArray
				value = sortJSONArray((JSONArray) value);
			}
			sortedMap.put(key, value);
		}

		// Create a new JSONObject from the sorted map
		return new JSONObject(sortedMap);
	}
	public static JSONArray sortJSONArray(JSONArray jsonArray) {
		JSONArray sortedArray = new JSONArray();
		for (int i = 0; i < jsonArray.size(); i++) {
			Object element = jsonArray.get(i);
			if (element instanceof JSONObject) {
				// Recursively sort nested JSONObject
				element = sortJSONObject((JSONObject) element);
			} else if (element instanceof JSONArray) {
				// Recursively sort nested JSONArray
				element = sortJSONArray((JSONArray) element);
			}
			sortedArray.add(element);
		}
		return sortedArray;
	}

}
