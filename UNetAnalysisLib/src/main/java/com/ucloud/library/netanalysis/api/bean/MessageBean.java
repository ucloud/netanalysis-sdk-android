package com.ucloud.library.netanalysis.api.bean;

import com.ucloud.library.netanalysis.annotation.JsonParam;
import com.ucloud.library.netanalysis.parser.JsonSerializable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by joshua on 2018/10/17 17:04.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class MessageBean implements JsonSerializable {
    @JsonParam("message")
    private String message;
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    @Override
    public String toString() {
        return toJson().toString();
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("message", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }
}
