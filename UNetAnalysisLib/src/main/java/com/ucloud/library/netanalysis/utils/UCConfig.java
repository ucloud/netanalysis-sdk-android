package com.ucloud.library.netanalysis.utils;

/**
 * Created by joshua on 2019-07-09 13:23.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class UCConfig {
    public enum LogLevel {
        TEST,
        DEBUG,
        RELEASE
    }
    
    private LogLevel logLevel;
    
    public UCConfig(LogLevel logLevel) {
        this.logLevel = logLevel;
    }
    
    public UCConfig() {
        this(LogLevel.RELEASE);
    }
    
    public void handleConfig() {
        switch (logLevel) {
            case TEST: {
                JLog.SHOW_TEST = true;
                JLog.SHOW_DEBUG = true;
                JLog.SHOW_VERBOSE = true;
                JLog.SHOW_INFO = true;
                JLog.SHOW_WARN = true;
                JLog.SHOW_ERROR = true;
                break;
            }
            case DEBUG: {
                JLog.SHOW_TEST = false;
                JLog.SHOW_DEBUG = true;
                JLog.SHOW_VERBOSE = true;
                JLog.SHOW_INFO = true;
                JLog.SHOW_WARN = true;
                JLog.SHOW_ERROR = true;
                break;
            }
            case RELEASE: {
                JLog.SHOW_TEST = false;
                JLog.SHOW_DEBUG = false;
                JLog.SHOW_VERBOSE = false;
                JLog.SHOW_INFO = true;
                JLog.SHOW_WARN = false;
                JLog.SHOW_ERROR = true;
                break;
            }
        }
    }
    
    public UCConfig setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
        return this;
    }
    
    public LogLevel getLogLevel() {
        return logLevel;
    }
}
