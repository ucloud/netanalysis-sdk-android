package com.ucloud.library.netanalysis.api.bean;

import com.ucloud.library.netanalysis.annotation.JsonParam;
import com.ucloud.library.netanalysis.parser.JsonSerializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by joshua on 2018/10/16 14:19.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class IpListBean implements JsonSerializable {
    @JsonParam("info")
    private List<InfoBean> info;
    @JsonParam("url")
    private List<String> url;
    @JsonParam("domain")
    private String domain;

    public static class InfoBean implements JsonSerializable {
        @JsonParam("location")
        private String location;
        @JsonParam("ip")
        private String ip;
        /**
         * 0:不traceroute
         * 1：需要traceroute
         */
        @JsonParam("type")
        private int type;

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public boolean isNeedTraceroute() {
            return type == 1;
        }

        @Override
        public String toString() {
            return toJson().toString();
        }

        @Override
        public JSONObject toJson() {
            JSONObject json = new JSONObject();
            try {
                json.put("location", location);
                json.put("ip", ip);
                json.put("type", type);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return json;
        }
    }

    public List<InfoBean> getInfo() {
        return info;
    }

    public void setInfo(List<InfoBean> info) {
        this.info = info;
    }

    public List<String> getUrl() {
        return url;
    }

    public void setUrl(List<String> url) {
        this.url = url;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public String toString() {
        return toJson().toString();
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        JSONArray arrInfo = new JSONArray();
        if (info != null && ! info.isEmpty()) {
            for (InfoBean bean : info) {
                if (bean == null || bean.toJson().length() == 0)
                    continue;

                arrInfo.put(bean);
            }
        }
        JSONArray arrUrl = new JSONArray();
        if (url != null && ! url.isEmpty()) {
            for (String u : url) {
                arrUrl.put(u);
            }
        }
        try {
            json.put("info", arrInfo);
            json.put("url", arrUrl);
            json.put("domain", domain);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }
}
