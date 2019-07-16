package com.ucloud.library.netanalysis.api.bean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by joshua on 2018/10/17 15:44.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class UCGetIpListRequestBean extends UCApiBaseRequestBean {
    private String longitude;
    private String latitude;
    
    public UCGetIpListRequestBean(String appKey) {
        super(appKey);
    }
    
    public UCGetIpListRequestBean(String appKey, String longitude, String latitude) {
        super(appKey);
        this.longitude = longitude;
        this.latitude = latitude;
    }
    
    public String getLongitude() {
        return longitude;
    }
    
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
    
    public String getLatitude() {
        return latitude;
    }
    
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
    
    @Override
    public String toString() {
        return toJson().toString();
    }
    
    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();
        try {
            json.put("longitude", longitude);
            json.put("latitude", latitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
