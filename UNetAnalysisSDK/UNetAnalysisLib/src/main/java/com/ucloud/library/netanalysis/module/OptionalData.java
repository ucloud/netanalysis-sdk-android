package com.ucloud.library.netanalysis.module;

import android.text.TextUtils;

import com.ucloud.library.netanalysis.exception.UCParamVerifyException;
import com.ucloud.library.netanalysis.utils.JLog;

import java.util.regex.Pattern;


/**
 * Created by joshua on 2019/1/12 13:25.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class OptionalData {
    public static final int OPT_PARAM_LEN_LIMIT = 3;
    public static final int OPT_PARAM_KEY_LEN_LIMIT = 20;
    public static final int OPT_PARAM_VALUE_LEN_LIMIT = 90;
    
    private static final String REGULAR = "(?<=,)|(?<==)";
    
    private OptionalParam[] optionalParams;
    
    public OptionalData(OptionalParam[] optionalParams) throws UCParamVerifyException {
        checkData(optionalParams);
        this.optionalParams = optionalParams;
    }
    
    public OptionalParam[] getOptionalParams() {
        return optionalParams;
    }
    
    public void setOptionalParams(OptionalParam[] optionalParams) throws UCParamVerifyException {
        checkData(optionalParams);
        this.optionalParams = optionalParams;
    }
    
    private void checkData(OptionalParam[] optionalParams) throws UCParamVerifyException {
        if (optionalParams != null && optionalParams.length > OPT_PARAM_LEN_LIMIT)
            throw new UCParamVerifyException(String.format("The maximum length of OptionalParam[] is %d!", OPT_PARAM_LEN_LIMIT));
        
        for (int i = 0; i < OPT_PARAM_LEN_LIMIT; i++) {
            OptionalParam param = optionalParams[i];
            if (param == null)
                continue;
            
            if (TextUtils.isEmpty(param.key))
                throw new UCParamVerifyException(String.format("The key of optionalParams[%d] can not be null or empty!", i));
            
            if (param.key.length() > 20)
                throw new UCParamVerifyException(String.format("The length of optionalParams[%d].key is %d byte(s), the maximum length is %d bytes",
                        i, param.key.length(), OptionalData.OPT_PARAM_KEY_LEN_LIMIT));
            
            if (param.value.length() > 90)
                throw new UCParamVerifyException(String.format("The length of optionalParams[%d].value is %d byte(s), the maximum length is %d bytes",
                        i, param.value.length(), OptionalData.OPT_PARAM_VALUE_LEN_LIMIT));
            
            Pattern pattern = Pattern.compile(REGULAR);
            if (pattern.matcher(param.key).find())
                throw new UCParamVerifyException(String.format("The key of optionalParams[%d]: %s can not contain ',' and '='", i, param.key));
            
            if (pattern.matcher(param.value).find())
                throw new UCParamVerifyException(String.format("The value optionalParams[%d]: %s can not contain ',' and '='", i, param.value));
        }
    }
    
    @Override
    public String toString() {
        if (optionalParams == null || optionalParams.length == 0)
            return "";
        
        StringBuffer sb = new StringBuffer();
        for (OptionalParam pararm : optionalParams)
            sb.append(pararm == null ? "" : (String.format((sb.length() == 0 ? "%s" : ",%s"), pararm.toString())));
        
        return sb.toString();
    }
    
    public static class OptionalParam {
        private String key;
        private String value;
        
        public OptionalParam(String key, String value) {
            this.key = key;
            this.value = value;
        }
        
        public String getKey() {
            return key;
        }
        
        public OptionalParam setKey(String key) {
            this.key = key;
            return this;
        }
        
        public String getValue() {
            return value;
        }
        
        public OptionalParam setValue(String value) {
            this.value = value;
            return this;
        }
        
        @Override
        public String toString() {
            return String.format("%s=%s", key, value);
        }
    }
}