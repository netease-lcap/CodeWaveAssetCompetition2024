import com.netease.lowcode.pdf.extension.PdfGenerator;
import com.netease.lowcode.pdf.extension.itextpdf.PdfUtils;

import java.io.IOException;


public class PdfGeneratorV2Test {

    public static void main(String[] args) throws IOException {

        String s = PdfUtils.readJson("pdf-generator/src/main/resources/requestData.json");

        PdfGenerator.createPDFV2(s,
                "https://dev-fileupload-kehfan.app.codewave.163.com:443/upload/app/e088646d-2b31-40d6-a5a3-bf459b013ea0/template_20240729135630943.json");
    }

}
