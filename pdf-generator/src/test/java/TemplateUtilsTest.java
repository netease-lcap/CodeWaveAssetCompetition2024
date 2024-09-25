import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.netease.lowcode.pdf.extension.Excel2Pdf;

import java.util.ArrayList;
import java.util.List;

public class TemplateUtilsTest {

    public static void main1(String[] args) {
        JSONObject cell = new JSONObject();
        JSONArray array = new JSONArray();
        JSONObject paragraph = new JSONObject();
        paragraph.put("text","${list.arr}");
        array.add(paragraph);
        cell.put("elements", array);
        System.out.println(Excel2Pdf.isFreemarkerListTag(cell));
    }

    public static void main2(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("0");
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");

        int i = 2;
        list.add(i,"i0");
        list.add(i+1,"i1");
        list.add(i+2,"i2");

        list.remove(i+3);
        System.out.println();
    }

    public static void main(String[] args) {
        List<List<JSONObject>> tmpCells = new ArrayList<>();
        ArrayList<JSONObject> row1 = new ArrayList<>();
        JSONObject cell1 = new JSONObject();
        JSONArray elements1 = new JSONArray();
        JSONObject para1 = new JSONObject();
        para1.put("text","表头1");
        elements1.add(para1);
        cell1.put("elements", elements1);
        row1.add(cell1);
        tmpCells.add(row1);

        ArrayList<JSONObject> row2 = new ArrayList<>();
        JSONObject cell2 = new JSONObject();
        JSONArray elements2 = new JSONArray();
        JSONObject para2 = new JSONObject();
        para2.put("text","${list.arr}");
        elements2.add(para2);
        cell2.put("elements", elements2);
        row2.add(cell2);
        tmpCells.add(row2);

        ArrayList<JSONObject> row3 = new ArrayList<>();
        JSONObject cell3 = new JSONObject();
        JSONArray elements3 = new JSONArray();
        JSONObject para3 = new JSONObject();
        para3.put("text","数据2");
        elements3.add(para3);
        cell3.put("elements", elements3);
        row3.add(cell3);
        tmpCells.add(row3);

        JSONObject requestJsonData = new JSONObject();
        JSONArray requestList = new JSONArray();
        JSONObject item1 = new JSONObject();
        item1.put("arr","arr数据1");
        requestList.add(item1);
        JSONObject item2 = new JSONObject();
        item2.put("arr","arr数据2");
        requestList.add(item2);
        requestJsonData.put("list", requestList);
        Excel2Pdf.handleFreemarkerList(tmpCells, requestJsonData.toJSONString());
        System.out.println();
    }
}
