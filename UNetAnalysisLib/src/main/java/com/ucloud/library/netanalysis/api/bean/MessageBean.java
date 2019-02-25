package com.ucloud.library.netanalysis.api.bean;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Created by joshua on 2018/10/17 17:04.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class MessageBean {
    @SerializedName("message")
    private String message;
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
