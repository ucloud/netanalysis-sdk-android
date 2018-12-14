package com.ucloud.library.netanalysis.api.bean;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Created by joshua on 2018/10/17 16:40.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class NetDataBean {
    @SerializedName("dst_ip")
    protected String dst_ip;
    @SerializedName("src_ip")
    protected String src_ip;
    
    public String getDst_ip() {
        return dst_ip;
    }
    
    public void setDst_ip(String dst_ip) {
        this.dst_ip = dst_ip;
    }
    
    public String getSrc_ip() {
        return src_ip;
    }
    
    public void setSrc_ip(String src_ip) {
        this.src_ip = src_ip;
    }
    
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
