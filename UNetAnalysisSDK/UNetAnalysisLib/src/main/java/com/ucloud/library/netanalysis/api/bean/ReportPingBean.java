package com.ucloud.library.netanalysis.api.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by joshua on 2018/10/17 16:26.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class ReportPingBean extends UCReportBean {
    @SerializedName("ping_data")
    private PingDataBean pingData;
    
    public ReportPingBean(PingDataBean pingData, ReportTagBean tag) {
        super("ping", tag);
        this.pingData = pingData;
    }
    
    public PingDataBean getPingData() {
        return pingData;
    }
    
    public void setPingData(PingDataBean pingData) {
        this.pingData = pingData;
    }
}
