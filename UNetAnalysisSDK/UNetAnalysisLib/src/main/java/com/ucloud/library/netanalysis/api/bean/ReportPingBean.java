package com.ucloud.library.netanalysis.api.bean;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Created by joshua on 2018/10/17 16:26.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class ReportPingBean extends UCReportBean {
    @SerializedName("ping_data")
    private ReportPingData pingData;
    
    public ReportPingBean(String appKey, PingDataBean pingData, ReportPingTagBean tag, IpInfoBean ipInfo) {
        super(appKey, "ping", tag, ipInfo);
        if (pingData != null)
            this.pingData = new ReportPingData(pingData.getDelay(), pingData.getLoss());
    }
    
    public ReportPingData getPingData() {
        return pingData;
    }
    
    public void setPingData(PingDataBean pingData) {
        if (pingData != null)
            this.pingData = new ReportPingData(pingData.getDelay(), pingData.getLoss());
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
