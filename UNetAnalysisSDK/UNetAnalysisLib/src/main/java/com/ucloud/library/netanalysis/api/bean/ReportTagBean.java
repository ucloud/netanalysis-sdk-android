package com.ucloud.library.netanalysis.api.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by joshua on 2018/12/27 13:31.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class ReportTagBean extends IpInfoBean {
    @SerializedName("app_key")
    private String appKey;
    @SerializedName("app_id")
    private String appId;
    @SerializedName("platform")
    private String platform = "Android";
    
    private ReportTagBean(String appKey, String appId) {
        this.appKey = appKey;
        this.appId = appId;
    }
    
    public String getAppKey() {
        return appKey;
    }
    
    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }
    
    public String getAppId() {
        return appId;
    }
    
    public void setAppId(String appId) {
        this.appId = appId;
    }
    
    public String getPlatform() {
        return platform;
    }
    
    public void setPlatform(String platform) {
        this.platform = platform;
    }
    
    public static ReportTagBean generate(String appKey, String appId, IpInfoBean ipInfo) {
        ReportTagBean tagBean = new ReportTagBean(appKey, appId);
        tagBean.copy(ipInfo);
        return tagBean;
    }
    
}
