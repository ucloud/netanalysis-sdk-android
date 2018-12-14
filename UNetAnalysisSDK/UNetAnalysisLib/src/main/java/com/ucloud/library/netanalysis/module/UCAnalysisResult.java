package com.ucloud.library.netanalysis.module;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by joshua on 2018/9/19 10:56.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class UCAnalysisResult {
    /** 自定义IP地址或域名列表的分析结果, {@link List<IpReport>} */
    @SerializedName("IpReports")
    private List<IpReport> ipReports;
    
    public List<IpReport> getIpReports() {
        return ipReports;
    }
    
    public void setIpReports(List<IpReport> ipReports) {
        this.ipReports = ipReports;
    }
    
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
