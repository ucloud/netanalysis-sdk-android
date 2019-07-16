package com.ucloud.library.netanalysis.exception;

/**
 * Created by joshua on 2019/1/12 13:33.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class UCHttpException extends UCException {
    public UCHttpException() {
    }
    
    public UCHttpException(String message) {
        super(message);
    }
    
    public UCHttpException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public UCHttpException(Throwable cause) {
        super(cause);
    }
    
    public UCHttpException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
