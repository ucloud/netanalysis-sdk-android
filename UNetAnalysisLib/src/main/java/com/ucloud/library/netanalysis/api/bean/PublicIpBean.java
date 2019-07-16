package com.ucloud.library.netanalysis.api.bean;

import com.ucloud.library.netanalysis.parser.JsonSerializable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by joshua on 2018/10/17 17:26.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class PublicIpBean implements JsonSerializable {
    private String ret;
    private IpInfoBean ipInfo;
    
    public String getRet() {
        return ret;
    }
    
    public void setRet(String ret) {
        this.ret = ret;
    }
    
    public IpInfoBean getIpInfo() {
        return ipInfo;
    }
    
    public void setIpInfo(IpInfoBean ipInfo) {
        this.ipInfo = ipInfo;
    }
    
    @Override
    public String toString() {
        return toJson().toString();
    }
    
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        
        try {
            json.put("ret", ret);
            json.put("data", ipInfo == null ? JSONObject.NULL : ipInfo.toJson());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        return json;
    }
}
