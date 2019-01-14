package com.ucloud.library.netanalysis.module;

import android.text.TextUtils;

import com.ucloud.library.netanalysis.exception.UCParamVerifyException;

import java.util.regex.Pattern;


/**
 * Created by joshua on 2019/1/12 13:25.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class OptionalParam {
    public static final int OPT_PARAM_KEY_LEN_LIMIT = 20;
    public static final int OPT_PARAM_VALUE_LEN_LIMIT = 90;
    
    private static final String REGULAR = "(?<=,)|(?<==)";
    
    private String key;
    private String value;
    
    public OptionalParam(String key, String value) throws UCParamVerifyException {
        checkParam(key, value);
        this.key = key;
        this.value = value;
    }
    
    public String getKey() {
        return key;
    }
    
    public OptionalParam setKey(String key) throws UCParamVerifyException {
        checkParam(key, value);
        this.key = key;
        return this;
    }
    
    public String getValue() {
        return value;
    }
    
    public OptionalParam setValue(String value) throws UCParamVerifyException {
        checkParam(key, value);
        this.value = value;
        return this;
    }
    
    private void checkParam(String key, String value) throws UCParamVerifyException {
        if (TextUtils.isEmpty(key))
            throw new UCParamVerifyException("The key can not be null or empty!");
        
        if (key.length() > 20)
            throw new UCParamVerifyException(String.format("The length of key is %d byte(s), the maximum length is %d bytes",
                    key.length(), OPT_PARAM_KEY_LEN_LIMIT));
        
        if (value.length() > 90)
            throw new UCParamVerifyException(String.format("The length of value is %d byte(s), the maximum length is %d bytes",
                    value.length(), OPT_PARAM_VALUE_LEN_LIMIT));
        
        Pattern pattern = Pattern.compile(REGULAR);
        if (pattern.matcher(key).find())
            throw new UCParamVerifyException(String.format("The key: %s can not contain ',' and '='", key));
        
        if (pattern.matcher(value).find())
            throw new UCParamVerifyException(String.format("The value: %s can not contain ',' and '='", value));
    }
    
    @Override
    public String toString() {
        return String.format("%s=%s", key, value);
    }
}