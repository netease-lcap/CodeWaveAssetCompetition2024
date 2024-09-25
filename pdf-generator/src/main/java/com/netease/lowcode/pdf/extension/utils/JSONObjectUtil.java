package com.netease.lowcode.pdf.extension.utils;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.netease.lowcode.pdf.extension.Excel2Pdf;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class JSONObjectUtil {

    /**
     * 将originRow中的freemarker list标签填充数据
     *
     * @param originRow
     * @param requestJsonData
     * @return
     */
    public static List<List<JSONObject>> fillListData(List<JSONObject> originRow, JSONObject requestJsonData) {

        if (Objects.isNull(requestJsonData)) {
            return new ArrayList<>();
        }

        // 获取 list标签名称，一行只填充一个list，不支持多个
        String listName = "";
        for (int i = 0; i < originRow.size(); i++) {
            if(Excel2Pdf.isFreemarkerListTag(originRow.get(i))){
                String cellText = Excel2Pdf.getCellText(originRow.get(i));
                // 获取list名称
                listName = cellText.substring(2, cellText.indexOf("."));
            }
        }
        // 未找到list名
        if(StringUtils.isBlank(listName)){
            return new ArrayList<>();
        }
        // 请求数据不包含该list
        if(!requestJsonData.containsKey(listName)){
            return new ArrayList<>();
        }
        JSONArray requestArrayData = requestJsonData.getJSONArray(listName);
        if(requestArrayData.isEmpty()){
            return new ArrayList<>();
        }

        List<List<JSONObject>> newRows = new ArrayList<>();

        for (int i = 0; i < requestArrayData.size(); i++) {
            // 开始填充
            List<JSONObject> cloneRow = deepCloneList(originRow);
            // 记录当前是否填充过该字段，用于横向分块
            String arrHasRead = "";
            // 填充每一个cell
            for (int j = 0; j < cloneRow.size(); j++) {
                if (Excel2Pdf.isFreemarkerListTag(cloneRow.get(j))) {
                    String cellText = Excel2Pdf.getCellText(cloneRow.get(j));
                    // 属性名
                    String arrName = cellText.substring(cellText.indexOf(".") + 1, cellText.length() - 1);
                    if(StringUtils.equals(arrName,arrHasRead)){
                        // 横向分块，用下一组数据填充
                        i++;
                    }
                    // 判断数据是否越界
                    if (i >= requestArrayData.size()) {
                        // 后续的行填充空数据,结束填充
                        for (int k = j; k < cloneRow.size(); k++) {
                            cloneRow.get(k).getJSONArray("elements").getJSONObject(0).put("text", "");
                        }
                        break;
                    }

                    JSONObject jsonObject = requestArrayData.getJSONObject(i);

                    String value = jsonObject.containsKey(arrName) ? jsonObject.getString(arrName) : "";
                    cloneRow.get(j).getJSONArray("elements").getJSONObject(0).put("text", value);

                    if(StringUtils.isBlank(arrHasRead)){
                        arrHasRead = arrName;
                    }
                }
            }
            newRows.add(cloneRow);
        }

        return newRows;
    }


    public static List<JSONObject> deepCloneList(List<JSONObject> originList) {
        if (CollectionUtils.isEmpty(originList)) {
            return new ArrayList<>();
        }
        List<JSONObject> cloneList = new ArrayList<>();
        for (int i = 0; i < originList.size(); i++) {
            JSONObject oriJsonObject = originList.get(i);
            cloneList.add(JSONObject.parseObject(oriJsonObject.toJSONString()));
        }
        return cloneList;
    }

    public static List<List<JSONObject>> deepCloneList2(List<List<JSONObject>> originList) {
        if (CollectionUtils.isEmpty(originList)) {
            return new ArrayList<>();
        }
        List<List<JSONObject>> cloneList = new ArrayList<>();
        for (int i = 0; i < originList.size(); i++) {
            cloneList.add(deepCloneList(originList.get(i)));
        }
        return cloneList;
    }

    /**
     * 填充cell
     *
     * @param cloneCell
     * @param jsonArray
     * @param arrName
     * @param arrHasRead
     */
    private static void fillCellData(JSONObject cloneCell, JSONArray jsonArray, String arrName, String arrHasRead) {

        // 判断是否开始处理分块部分
        if (StringUtils.equals(arrName, arrHasRead)) {
            // 已经处理过该组数据，移除0
            jsonArrayRemove0(jsonArray);
        }
        if (jsonArray.isEmpty()) {
            return;
        }
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        if (!jsonObject.containsKey(arrName)) {
            // 对象中不包含该属性
            return;
        }
        cloneCell.getJSONArray("elements").getJSONObject(0).put("text", jsonObject.getString(arrName));
    }

    /**
     * 移除JSONArray中的第0个元素(如果存在的话)
     *
     * @param jsonArray
     */
    public static void jsonArrayRemove0(JSONArray jsonArray) {
        if(Objects.isNull(jsonArray)){
            return;
        }
        if(jsonArray.isEmpty()){
            return;
        }
        jsonArray.remove(0);
    }

}
