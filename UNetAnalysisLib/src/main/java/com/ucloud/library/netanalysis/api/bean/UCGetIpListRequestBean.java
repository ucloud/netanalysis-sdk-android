package com.ucloud.library.netanalysis.api.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by joshua on 2018/10/17 15:44.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class UCGetIpListRequestBean extends UCApiBaseRequestBean {
    @SerializedName("longitude")
    private String longitude;
    @SerializedName("latitude")
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
}
