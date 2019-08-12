package com.ucloud.library.netanalysis.api.bean;

/**
 * Created by joshua on 2018/12/27 13:31.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class ReportPingTagBean extends ReportTagBean {
    private String dstIp;
    private int TTL;
    
    public ReportPingTagBean(String appId, String dstIp, int TTL) {
        super(appId);
        this.dstIp = dstIp;
        this.TTL = TTL;
    }
    
    public String getDstIp() {
        return dstIp;
    }
    
    public void setDstIp(String dstIp) {
        this.dstIp = dstIp;
    }
    
    public int getTTL() {
        return TTL;
    }
    
    public void setTTL(int TTL) {
        this.TTL = TTL;
    }
    
    protected String makeReportString() {
        StringBuilder sb = new StringBuilder(super.makeReportString());
        sb.append(String.format(",dst_ip=%s", (dstIp == null ? "" : dstIp)));
        sb.append(String.format(",TTL=%d", TTL));
        return sb.toString();
    }
}
