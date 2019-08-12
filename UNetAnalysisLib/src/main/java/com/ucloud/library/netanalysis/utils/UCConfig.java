package com.ucloud.library.netanalysis.utils;

/**
 * Created by joshua on 2019-07-09 13:23.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class UCConfig {
    public enum LogLevel {
        RELEASE,
        DEBUG,
        TEST
    }
    
    private LogLevel logLevel;
    private boolean isAutoDetect = true;
    
    /**
     * @param logLevel     Log级别，default = {@link UCConfig.LogLevel.RELEASE}
     * @param isAutoDetect 是否开启自动检测, default = true
     */
    public UCConfig(LogLevel logLevel, boolean isAutoDetect) {
        this.logLevel = logLevel;
        this.isAutoDetect = isAutoDetect;
    }
    
    /**
     * 构造方法
     *
     * @param isAutoDetect 是否开启自动检测, default = true
     */
    public UCConfig(boolean isAutoDetect) {
        this(LogLevel.RELEASE, isAutoDetect);
    }
    
    /**
     * 构造方法
     *
     * @param logLevel Log级别，default = {@link UCConfig.LogLevel.RELEASE}
     */
    public UCConfig(LogLevel logLevel) {
        this(logLevel, true);
    }
    
    /**
     * 构造方法
     * LogLevel = {@link UCConfig.LogLevel.RELEASE}
     * isAutoDetect = true
     */
    public UCConfig() {
        this(LogLevel.RELEASE, true);
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
    
    public boolean isAutoDetect() {
        return isAutoDetect;
    }
    
    public UCConfig setAutoDetect(boolean autoDetect) {
        isAutoDetect = autoDetect;
        return this;
    }
}
