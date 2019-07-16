package com.ucloud.library.netanalysis.api.bean;

import com.ucloud.library.netanalysis.parser.JsonSerializable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by joshua on 2018/10/17 16:31.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class UCReportEncryptBean implements JsonSerializable {
    protected String data;
    
    public UCReportEncryptBean(String data) {
        this.data = data;
    }
    
    public String getData() {
        return data;
    }
    
    public void setData(String data) {
        this.data = data;
    }
    
    @Override
    public String toString() {
        return toJson().toString();
    }
    
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
