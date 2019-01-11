package com.ucloud.library.netanalysis.api.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by joshua on 2018/10/17 16:31.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class UCReportBean extends UCApiBaseRequestBean {
    @SerializedName("action")
    protected String action;
    @SerializedName("timestamp")
    protected long timestamp;
    @SerializedName("tag")
    protected String tag;
    @SerializedName("ip_info")
    protected String ipInfo;
    
    public UCReportBean(String appKey, String action, ReportTagBean tag, IpInfoBean ipInfo) {
        super(appKey);
        this.action = action;
        this.timestamp = System.currentTimeMillis() / 1000;
        this.tag = tag.makeReportString();
        this.ipInfo = ipInfo.makeReportString();
    }
    
    
    public String getAction() {
        return action;
    }
    
    protected void setAction(String action) {
        this.action = action;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    protected void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getTag() {
        return tag;
    }
    
    public void setTag(ReportTagBean tag) {
        if (tag != null)
            this.tag = tag.makeReportString();
    }
    
    public void setTag(String tag) {
        this.tag = tag;
    }
    
    public String getIpInfo() {
        return ipInfo;
    }
    
    public void setIpInfo(IpInfoBean ipInfo) {
        if (ipInfo != null)
            this.ipInfo = ipInfo.makeReportString();
    }
    
    public void setIpInfo(String ipInfo) {
        this.ipInfo = ipInfo;
    }
}
