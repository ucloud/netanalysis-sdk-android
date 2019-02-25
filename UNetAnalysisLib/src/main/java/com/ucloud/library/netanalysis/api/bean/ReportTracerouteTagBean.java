package com.ucloud.library.netanalysis.api.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by joshua on 2018/12/27 13:31.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class ReportTracerouteTagBean extends ReportTagBean {
    @SerializedName("dst_ip")
    private String dstIp;
    
    public ReportTracerouteTagBean(String appId, String dstIp) {
        this(appId, dstIp, null);
    }
    
    public ReportTracerouteTagBean(String appId, String dstIp, String optionalData) {
        super(appId, optionalData);
        this.dstIp = dstIp;
    }
    
    public String getDstIp() {
        return dstIp;
    }
    
    public void setDstIp(String dstIp) {
        this.dstIp = dstIp;
    }
    
    protected String makeReportString() {
        StringBuffer sb = new StringBuffer(super.makeReportString());
        sb.append(String.format(",dst_ip=%s", (dstIp == null ? "" : dstIp)));
        return sb.toString();
    }
    
}
