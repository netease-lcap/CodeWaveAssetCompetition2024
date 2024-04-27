package org.example.product.structure;

import com.netease.lowcode.core.annotation.NaslStructure;

/*增加产品的返回的数据结构*/
@NaslStructure
public class AddProductVo {
    public Integer code;


    public String msg;

    public AddProductResult result;
    public String success;
    public int getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }


    public AddProductResult getResult() {
        return result;
    }

    public void setResult(AddProductResult result) {
        this.result = result;
    }
    @Override
    public String toString() {
        return "AddProductVo{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", result='" + result + '\'' +
                ", success='" + success + '\'' +
                '}';
    }
}
