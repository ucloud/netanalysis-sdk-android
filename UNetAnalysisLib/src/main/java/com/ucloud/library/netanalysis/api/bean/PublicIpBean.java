package com.ucloud.library.netanalysis.api.bean;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Created by joshua on 2018/10/17 17:26.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class PublicIpBean {
    @SerializedName("ret")
    private String ret;
    @SerializedName("data")
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
        return new Gson().toJson(this);
    }
}
