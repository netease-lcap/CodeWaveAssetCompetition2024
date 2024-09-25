import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.netease.lowcode.pdf.extension.utils.JSONObjectUtil;

public class JSONObjectUtilTest {

    public static void main(String[] args) {

        JSONArray array = new JSONArray();
        JSONObject e1 = new JSONObject();
        e1.put("a","fff");
        array.add(e1);
        JSONObject e2 = new JSONObject();
        e2.put("b","fsd");
        array.add(e2);
        JSONObject e3 = new JSONObject();
        e3.put("c","fcd");
        array.add(e3);
        JSONObjectUtil.jsonArrayRemove0(array);
        System.out.println();
    }
}
