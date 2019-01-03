package com.ucloud.library.netanalysis.api.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by joshua on 2018/12/27 13:31.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class ReportTagBean {
    @SerializedName("app_id")
    protected String appId;
    @SerializedName("platform")
    protected int platform = 0;
    
    protected ReportTagBean(String appId) {
        this.appId = appId;
    }
    
    public String getAppId() {
        return appId;
    }
    
    public void setAppId(String appId) {
        this.appId = appId;
    }
    
    public int getPlatform() {
        return platform;
    }
    
    protected String makeReportString() {
        StringBuffer sb = new StringBuffer();
        sb.append(String.format("app_id=%s", appId));
        sb.append(String.format(",platform=%d", platform));
        return sb.toString();
    }
    
}
