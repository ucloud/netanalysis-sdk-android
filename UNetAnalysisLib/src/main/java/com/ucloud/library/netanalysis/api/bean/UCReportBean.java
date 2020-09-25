package com.ucloud.library.netanalysis.api.bean;

import com.ucloud.library.netanalysis.module.UserDefinedData;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by joshua on 2018/10/17 16:31.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class UCReportBean extends UCApiBaseRequestBean {
    protected String action;
    protected long timestamp;
    protected String tag;
    protected String ipInfo;
    protected String userDefinedStr;
    protected UserDefinedData userDefinedData;
    protected String uuid;
    protected int trigger;
    
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
    
    public int getTrigger() {
        return trigger;
    }
    
    public void setTrigger(int trigger) {
        this.trigger = trigger;
    }
    
    @Override
    public String toString() {
        return toJson().toString();
    }
    
    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();
        try {
            json.put("action", action);
            json.put("timestamp", timestamp);
            json.put("tag", tag);
            json.put("ip_info", ipInfo);
            json.put("uuid", uuid);
            json.put("trigger_type", trigger);
            json.put("user_defined", userDefinedData == null ? "" : userDefinedData.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
