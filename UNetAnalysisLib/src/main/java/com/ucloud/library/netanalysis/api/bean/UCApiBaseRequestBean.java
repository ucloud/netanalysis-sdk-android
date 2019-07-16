package com.ucloud.library.netanalysis.api.bean;

import com.ucloud.library.netanalysis.parser.JsonSerializable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by joshua on 2018/10/17 15:44.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class UCApiBaseRequestBean implements JsonSerializable {
    protected String appKey;
    
    public UCApiBaseRequestBean(String appKey) {
        this.appKey = appKey;
    }
    
    public String getAppKey() {
        return appKey;
    }
    
    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }
    
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("app_key", appKey);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
