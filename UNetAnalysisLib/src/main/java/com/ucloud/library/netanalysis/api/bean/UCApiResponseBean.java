package com.ucloud.library.netanalysis.api.bean;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Created by joshua on 2018/10/12 10:41.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class UCApiResponseBean<T> {
    @SerializedName("meta")
    protected MetaBean meta;
    @SerializedName("data")
    protected T data;
    
    public MetaBean getMeta() {
        return meta;
    }
    
    public void setMeta(MetaBean meta) {
        this.meta = meta;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
    
    public static class MetaBean {
        @SerializedName("code")
        private int code;
        
        @SerializedName("error")
        private String error;
        
        public int getCode() {
            return code;
        }
        
        public void setCode(int code) {
            this.code = code;
        }
        
        public String getError() {
            return error;
        }
        
        public void setError(String error) {
            this.error = error;
        }
        
        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }
    
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
