package com.netease.lowcode.pdf.extension.structures;

import com.netease.lowcode.core.annotation.NaslStructure;

@NaslStructure
public class CreateByTemplateRequest {

    public String templateUrl;
    public String jsonData;
    public String exportFileName;

    public String getTemplateUrl() {
        return templateUrl;
    }

    public void setTemplateUrl(String templateUrl) {
        this.templateUrl = templateUrl;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    public String getExportFileName() {
        return exportFileName;
    }

    public void setExportFileName(String exportFileName) {
        this.exportFileName = exportFileName;
    }
}
