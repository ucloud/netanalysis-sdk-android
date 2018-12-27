package com.ucloud.library.netanalysis.api.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by joshua on 2018/10/17 16:47.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class ReportTracerouteBean extends UCReportBean {
    @SerializedName("traceroute_data")
    private TracerouteDataBean tracerouteData;
    
    public ReportTracerouteBean(TracerouteDataBean tracerouteData, ReportTagBean tag) {
        super("traceroute", tag);
        this.tracerouteData = tracerouteData;
    }
    
    public TracerouteDataBean getTracerouteData() {
        return tracerouteData;
    }
    
    public void setTracerouteData(TracerouteDataBean tracerouteData) {
        this.tracerouteData = tracerouteData;
    }
}
