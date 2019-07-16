package com.ucloud.library.netanalysis.api.bean;

import com.ucloud.library.netanalysis.parser.JsonSerializable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by joshua on 2018/10/17 16:40.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class NetDataBean implements JsonSerializable {
    protected String dst_ip;
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
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        
        try {
            json.put("dst_ip", dst_ip);
            json.put("timestamp", timestamp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        return json;
    }
}
