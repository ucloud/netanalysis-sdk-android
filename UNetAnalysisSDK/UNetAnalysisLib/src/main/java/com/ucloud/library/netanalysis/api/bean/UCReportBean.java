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
    @SerializedName("ip_info")
    protected IpInfoBean ipInfo;
    
    public UCReportBean(String token, String action) {
        super(token);
        this.action = action;
        this.timestamp = System.currentTimeMillis() / 1000;
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
    
    public IpInfoBean getIpInfo() {
        return ipInfo;
    }
    
    public void setIpInfo(IpInfoBean ipInfo) {
        this.ipInfo = ipInfo;
    }
}
