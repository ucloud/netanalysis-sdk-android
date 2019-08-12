package com.ucloud.library.netanalysis.api.bean;

/**
 * Created by joshua on 2018/12/27 13:31.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class ReportTracerouteTagBean extends ReportTagBean {
    private String dstIp;
    
    public ReportTracerouteTagBean(String appId, String dstIp) {
        super(appId);
        this.dstIp = dstIp;
    }
    
    public String getDstIp() {
        return dstIp;
    }
    
    public void setDstIp(String dstIp) {
        this.dstIp = dstIp;
    }
    
    protected String makeReportString() {
        StringBuilder sb = new StringBuilder(super.makeReportString());
        sb.append(String.format(",dst_ip=%s", (dstIp == null ? "" : dstIp)));
        return sb.toString();
    }
    
}
