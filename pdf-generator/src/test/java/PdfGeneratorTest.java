import com.netease.lowcode.pdf.extension.PdfGenerator;
import com.netease.lowcode.pdf.extension.structures.BaseResponse;
import com.netease.lowcode.pdf.extension.structures.CreateRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PdfGeneratorTest {

    public static void main(String[] args) {
        CreateRequest request = new CreateRequest();
        request.fileName = "test.pdf";
        request.data = new ArrayList<>();
        // 第一部分数据 图片
        HashMap<String, List<String>> m1 = new HashMap<>();
        ArrayList<String> arr1 = new ArrayList<>();
        arr1.add("实验室时间校准记录");
        m1.put("pdf-paragraph", arr1);
        m1.put("pdf-title",arr1);
        request.data.add(m1);
        // 第二部分数据 段落
        HashMap<String,List<String>> m2 = new HashMap<>();
        ArrayList<String> arr2 = new ArrayList<>();
        arr2.add("起草人/日期,审核人/日期,批准人/日期");
        arr2.add("xxx/2024-10-01,xxx/2024-10-01,xxx/2024-10-01");
        arr2.add("-,-,-");
        m2.put("pdf-table",arr2);
        request.data.add(m2);
        // 第三部分数据 表格
        HashMap<String, List<String>> m3 = new HashMap<>();
        ArrayList<String> arr3 = new ArrayList<>();
        arr3.add("化验室: 001号化验室");
        m3.put("pdf-paragraph", arr3);
        request.data.add(m3);
        // 第四部分 表格
        HashMap<String,List<String>> m4 = new HashMap<>();
        ArrayList<String> arr4 = new ArrayList<>();
        arr4.add("设备名称,设备编号/安装位置,设备显示时间,北京时间,检查人/日期,是否校正,校正人/日期,复核人");
        arr4.add("xx,xx,xx,xx,xx,xx,xx/xx,xx");
        m4.put("pdf-table",arr4);
        request.data.add(m4);

        BaseResponse response = PdfGenerator.createPDF(request);

        System.out.println();
    }

}
