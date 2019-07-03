package com.ucloud.library.netanalysis.api.bean;

import com.ucloud.library.netanalysis.annotation.JsonParam;
import com.ucloud.library.netanalysis.parser.JsonSerializable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by joshua on 2018/10/12 10:41.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class UCApiResponseBean<T extends JsonSerializable> implements JsonSerializable {
    @JsonParam("meta")
    protected MetaBean meta;
    @JsonParam("data")
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

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("meta", meta == null ? JSONObject.NULL : meta.toJson());
            json.put("data", data == null ? JSONObject.NULL : data.toJson());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    public static class MetaBean implements JsonSerializable {
        @JsonParam("code")
        private int code;

        @JsonParam("error")
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
            return toJson().toString();
        }

        @Override
        public JSONObject toJson() {
            JSONObject json = new JSONObject();
            try {
                json.put("code", code);
                json.put("error", error);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return json;
        }
    }

    @Override
    public String toString() {
        return toJson().toString();
    }
}
