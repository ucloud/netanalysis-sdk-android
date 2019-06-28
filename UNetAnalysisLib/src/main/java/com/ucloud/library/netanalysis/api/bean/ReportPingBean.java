package com.ucloud.library.netanalysis.api.bean;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.ucloud.library.netanalysis.module.UserDefinedData;

/**
 * Created by joshua on 2018/10/17 16:26.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class ReportPingBean extends UCReportBean {
    @SerializedName("ping_data")
    private ReportPingData pingData;
    @SerializedName("ping_status")
    private int pingStatus;
    
    public ReportPingBean(String appKey, PingDataBean pingData, int pingStatus,
                          ReportPingTagBean tag, IpInfoBean ipInfo, UserDefinedData userDefinedData) {
        super(appKey, "ping", tag, ipInfo, userDefinedData);
        if (pingData != null) {
            this.timestamp = pingData.timestamp;
            this.pingData = new ReportPingData(pingData.getDelay(), pingData.getLoss());
        }
        this.pingStatus = pingStatus;
    }
    
    public ReportPingData getPingData() {
        return pingData;
    }
    
    public void setPingData(PingDataBean pingData) {
        if (pingData != null)
            this.pingData = new ReportPingData(pingData.getDelay(), pingData.getLoss());
    }
    
    public int getPingStatus() {
        return pingStatus;
    }
    
    public void setPingStatus(int pingStatus) {
        this.pingStatus = pingStatus;
    }
    
    public static class ReportPingData {
        @SerializedName("delay")
        private int delay;
        @SerializedName("loss")
        private int loss;
        
        public ReportPingData(int delay, int loss) {
            this.delay = delay;
            this.loss = loss;
        }
        
        public int getDelay() {
            return delay;
        }
        
        public void setDelay(int delay) {
            this.delay = delay;
        }
        
        public int getLoss() {
            return loss;
        }
        
        public void setLoss(int loss) {
            this.loss = loss;
        }
        
        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }
}
