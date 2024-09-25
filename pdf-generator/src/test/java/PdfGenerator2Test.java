import com.alibaba.fastjson2.JSON;
import com.netease.lowcode.pdf.extension.PdfGenerator;
import com.netease.lowcode.pdf.extension.structures.CreateByTemplateRequest;

import java.util.HashMap;
import java.util.Map;

public class PdfGenerator2Test {

    public static void main(String[] args) {
        CreateByTemplateRequest request = new CreateByTemplateRequest();

        Map<String,String> data = new HashMap<>();
        data.put("title","测试申请书-食品");

        request.jsonData = JSON.toJSONString(data);
        PdfGenerator.createPDFByTemplate(request);
    }
}
