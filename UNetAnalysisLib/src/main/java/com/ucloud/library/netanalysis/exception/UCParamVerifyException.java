package com.ucloud.library.netanalysis.exception;

/**
 * Created by joshua on 2019/1/12 13:33.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class UCParamVerifyException extends UCException {
    public UCParamVerifyException() {
    }
    
    public UCParamVerifyException(String message) {
        super(message);
    }
    
    public UCParamVerifyException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public UCParamVerifyException(Throwable cause) {
        super(cause);
    }
}
