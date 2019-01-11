package com.ucloud.library.netanalysis.api.bean;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Created by joshua on 2018/10/17 15:44.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class UCApiBaseRequestBean {
    @SerializedName("app_key")
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
        return new Gson().toJson(this);
    }
}
