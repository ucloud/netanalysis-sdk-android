package com.ucloud.library.netanalysis.exception;

/**
 * Created by joshua on 2019/1/12 13:30.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class UCException extends Throwable {
    public UCException() {
    }
    
    public UCException(String message) {
        super(message);
    }
    
    public UCException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public UCException(Throwable cause) {
        super(cause);
    }
    
    public UCException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
