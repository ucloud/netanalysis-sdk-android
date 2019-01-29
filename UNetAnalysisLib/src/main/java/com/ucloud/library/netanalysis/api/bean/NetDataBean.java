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
    @SerializedName("timestamp")
    protected long timestamp;
    
    public String getDst_ip() {
        return dst_ip;
    }
    
    public void setDst_ip(String dst_ip) {
        this.dst_ip = dst_ip;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
