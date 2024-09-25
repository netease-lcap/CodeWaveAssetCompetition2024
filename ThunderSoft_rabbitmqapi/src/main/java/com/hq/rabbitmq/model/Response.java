package com.hq.rabbitmq.model;

import com.netease.lowcode.core.annotation.NaslStructure;

@NaslStructure
public class Response {

    public Boolean success;
    public String msg;
    public String errors;

    public static Response OK(String msg) {
        Response resp = new Response();
        resp.setSuccess(true);
        resp.setMsg(msg);
        return resp;
    }

    public static Response FAIL(String msg) {
        Response resp = new Response();
        resp.setSuccess(false);
        resp.setMsg(msg);
        resp.setErrors(null);
        return resp;
    }

    public static Response FAIL(String msg, String errors) {
        Response resp = new Response();
        resp.setSuccess(false);
        resp.setMsg(msg);
        resp.setErrors(errors);
        return resp;
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

    public String getErrors() {
        return errors;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }
}
