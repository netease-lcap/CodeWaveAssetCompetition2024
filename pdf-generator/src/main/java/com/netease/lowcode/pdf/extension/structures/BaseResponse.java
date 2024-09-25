package com.netease.lowcode.pdf.extension.structures;

import com.netease.lowcode.core.annotation.NaslStructure;

@NaslStructure
public class BaseResponse {
    public Boolean success;
    public String msg;
    public String trace;
    public String result;
    public String filePath;

    public static BaseResponse OK(String filePath,String result) {
        BaseResponse response = new BaseResponse();
        response.setSuccess(true);
        response.setMsg("OK");
        response.setResult(result);
        response.setFilePath(filePath);
        return response;
    }

    public static BaseResponse FAIL(String msg) {
        return FAIL("",msg);
    }

    public static BaseResponse FAIL(String trace,String msg) {
        BaseResponse response = new BaseResponse();
        response.setSuccess(false);
        response.setMsg(msg);
        response.setTrace(trace);
        return response;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTrace() {
        return trace;
    }

    public void setTrace(String trace) {
        this.trace = trace;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "success=" + success +
                ", msg='" + msg + '\'' +
                ", trace='" + trace + '\'' +
                ", result='" + result + '\'' +
                ", filePath='" + filePath + '\'' +
                '}';
    }
}
