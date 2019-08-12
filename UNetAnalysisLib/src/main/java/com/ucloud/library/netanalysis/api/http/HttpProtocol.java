package com.ucloud.library.netanalysis.api.http;

/**
 * Created by joshua on 2019-07-07 19:11.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public enum HttpProtocol {
    HTTP("http://"),
    HTTPS("https://");
    
    private final String protocol;
    
    HttpProtocol(String protocol) {
        this.protocol = protocol;
    }
    
    public String getProtocol() {
        return protocol;
    }
}
