package com.ucloud.library.netanalysis.api.bean;

import com.ucloud.library.netanalysis.parser.JsonSerializable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by joshua on 2019-07-24 16:54.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class SdkStatus implements JsonSerializable {
    private int enabled;
    
    public int getEnabled() {
        return enabled;
    }
    
    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }
    
    @Override
    public String toString() {
        return toJson().toString();
    }
    
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        
        try {
            json.put("enabled", enabled);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
