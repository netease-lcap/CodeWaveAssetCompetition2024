import com.netease.lowcode.pdf.extension.Excel2Pdf;
import com.netease.lowcode.pdf.extension.structures.BaseResponse;
import com.netease.lowcode.pdf.extension.structures.CreateByXlsxRequest;

public class Excel2PdfTest {

    public static void main(String[] args) {
        String s = "{\n" +
                "    \"name\":\"测试名字\",\n" +
                "    \"list\":[\n" +
                "        {\n" +
                "            \"no\":1,\n" +
                "            \"name\":\"项目1\",\n" +
                "            \"std\":\"国标1\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"no\":2,\n" +
                "            \"name\":\"项目2\",\n" +
                "            \"std\":\"国标2\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"no\":3,\n" +
                "            \"name\":\"项目3\",\n" +
                "            \"std\":\"国标3\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        CreateByXlsxRequest request = new CreateByXlsxRequest();
        request.setJsonData(s);
        request.setExportFileName("测试.pdf");
        request.setTemplateUrl("https://dev-excel2pdf-kehfan.app.codewave.163.com:443/upload/app/4c1cce32-ed6c-4659-878d-2b5ab749e24d/白贝壳测试模板_20240822114115167.xlsx");
        BaseResponse baseResponse = Excel2Pdf.xlsx2pdf(request);
        System.out.println(baseResponse);
    }
}
