package com.ucloud.library.netanalysis.api.bean;

import com.ucloud.library.netanalysis.annotation.JsonParam;
import com.ucloud.library.netanalysis.parser.JsonSerializable;

import org.json.JSONObject;

/**
 * Created by joshua on 2018/10/17 15:44.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class UCApiBaseRequestBean implements JsonSerializable {
    @JsonParam("app_key")
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
    public String toString() {
        return toJson().toString();
    }

    @Override
    public JSONObject toJson() {
        return null;
    }
}
