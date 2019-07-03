package com.ucloud.library.netanalysis.api.bean;

import com.ucloud.library.netanalysis.annotation.JsonParam;
import com.ucloud.library.netanalysis.module.UserDefinedData;

/**
 * Created by joshua on 2018/10/17 16:31.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class UCReportBean extends UCApiBaseRequestBean {
    @JsonParam("action")
    protected String action;
    @JsonParam("timestamp")
    protected long timestamp;
    @JsonParam("tag")
    protected String tag;
    @JsonParam("ip_info")
    protected String ipInfo;
    @JsonParam("user_defined")
    protected String userDefinedStr;
    protected transient UserDefinedData userDefinedData;
    @JsonParam("uuid")
    protected String uuid;
    
    public UCReportBean(String appKey, String action, ReportTagBean tag, IpInfoBean ipInfo, UserDefinedData userDefinedData) {
        super(appKey);
        this.action = action;
        this.tag = tag.makeReportString();
        this.ipInfo = ipInfo.makeReportString();
        this.userDefinedData = userDefinedData;
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
    
    public UserDefinedData getUserDefinedData() {
        return userDefinedData;
    }
    
    public String getUserDefinedStr() {
        return userDefinedStr;
    }
    
    public void setUserDefinedStr(String userDefinedStr) {
        this.userDefinedStr = userDefinedStr;
    }
    
    public void setIpInfo(String ipInfo) {
        this.ipInfo = ipInfo;
    }
    
    public String getUuid() {
        return uuid;
    }
    
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
