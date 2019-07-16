package com.ucloud.library.netanalysis.module;

import com.ucloud.library.netanalysis.parser.JsonSerializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by joshua on 2018/9/19 10:56.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class UCAnalysisResult implements JsonSerializable {
    /**
     * 自定义IP地址或域名列表的分析结果, {@link List<IpReport>}
     */
    private List<IpReport> ipReports;
    
    public List<IpReport> getIpReports() {
        return ipReports;
    }
    
    public void setIpReports(List<IpReport> ipReports) {
        this.ipReports = ipReports;
    }
    
    @Override
    public String toString() {
        return toJson().toString();
    }
    
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        JSONArray jarr = new JSONArray();
        if (ipReports != null && !ipReports.isEmpty()) {
            for (IpReport report : ipReports) {
                if (report == null || report.toJson().length() == 0)
                    continue;
                jarr.put(report.toJson());
            }
        }
        try {
            json.put("IpReports", jarr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
