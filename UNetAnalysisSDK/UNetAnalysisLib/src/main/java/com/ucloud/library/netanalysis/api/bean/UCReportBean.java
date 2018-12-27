package com.ucloud.library.netanalysis.api.bean;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Created by joshua on 2018/10/17 16:31.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class UCReportBean {
    @SerializedName("action")
    protected String action;
    @SerializedName("timestamp")
    protected long timestamp;
    @SerializedName("tag")
    protected ReportTagBean tag;
    
    public UCReportBean(String action, ReportTagBean tag) {
        this.action = action;
        this.timestamp = System.currentTimeMillis() / 1000;
        this.tag = tag;
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
    
    public ReportTagBean getTag() {
        return tag;
    }
    
    public void setTag(ReportTagBean tag) {
        this.tag = tag;
    }
    
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
