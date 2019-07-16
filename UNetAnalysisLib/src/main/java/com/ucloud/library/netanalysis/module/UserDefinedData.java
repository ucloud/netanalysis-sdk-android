package com.ucloud.library.netanalysis.module;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.ucloud.library.netanalysis.exception.UCParamVerifyException;
import com.ucloud.library.netanalysis.parser.JsonSerializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 * Created by joshua on 2019/1/12 13:25.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class UserDefinedData {
    public static final int LIMIT_LEN_USER_DEFINED_DATA = 1 << 10;
    
    private static final String KEY_USER_DEFINED_PARAM = "key";
    private static final String VALUE_USER_DEFINED_PARAM = "val";
    
    private JSONArray data;
    
    private UserDefinedData(JSONArray data) {
        this.data = data;
    }
    
    public static class Builder {
        private Map<String, String> map;
        
        public Builder(Map<String, String> map) {
            this.map = map;
        }
        
        public Builder() {
        }
        
        public Builder putParam(UserDefinedParam param) {
            if (map == null)
                map = new ArrayMap<>();
            
            map.put(param.key, param.value);
            return this;
        }
        
        public void setData(Map<String, String> map) {
            this.map = map;
        }
        
        public Map<String, String> getData() {
            return map;
        }
        
        public UserDefinedData create() throws UCParamVerifyException {
            JSONArray jArr = new JSONArray();
            if (map == null)
                return new UserDefinedData(jArr);
            
            Set<String> keySet = map.keySet();
            if (map == null)
                return new UserDefinedData(jArr);
            
            Iterator<String> iterator = keySet.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                if (TextUtils.isEmpty(key))
                    continue;
    
                JSONObject json = new JSONObject();
                try {
                    json.put(KEY_USER_DEFINED_PARAM, key);
                    String val = map.get(key);
                    json.put(VALUE_USER_DEFINED_PARAM, val == null ? "" : val);
                } catch (JSONException e) {
                    e.printStackTrace();
                    continue;
                }
                
                jArr.put(json);
            }
            
            String res = jArr.toString();
            int len = res.length();
            if (len > LIMIT_LEN_USER_DEFINED_DATA)
                throw new UCParamVerifyException(String.format("The json string length of user defined map is %d, the limit length is %d. \nJson String is: %s", len, LIMIT_LEN_USER_DEFINED_DATA, res));
            
            return new UserDefinedData(jArr);
        }
        
    }
    
    public static class UserDefinedParam implements Serializable, JsonSerializable {
        private String key;
        private String value;
        
        public UserDefinedParam(String key, String value) {
            this.key = key;
            this.value = value;
        }
        
        public String getKey() {
            return key;
        }
        
        public UserDefinedParam setKey(String key) {
            this.key = key;
            return this;
        }
        
        public String getValue() {
            return value;
        }
        
        public UserDefinedParam setValue(String value) {
            this.value = value == null ? "" : value;
            return this;
        }
        
        @Override
        public String toString() {
            return toJson().toString();
        }
        
        @Override
        public JSONObject toJson() {
            JSONObject json = new JSONObject();
            try {
                json.put(KEY_USER_DEFINED_PARAM, key);
                json.put(VALUE_USER_DEFINED_PARAM, value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return json;
        }
    }
    
    @Override
    public String toString() {
        return data == null || data.length() == 0 ? "" : data.toString();
    }
}