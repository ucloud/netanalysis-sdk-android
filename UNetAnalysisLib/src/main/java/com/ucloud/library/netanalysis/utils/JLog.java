package com.ucloud.library.netanalysis.utils;

import android.util.Log;


/**
 * Created by Joshua_Yin on 2017/12/26 22:28.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */

public class JLog {
    private static volatile JLog mInstance;
    
    private boolean isSaveLog = false;
    
    private static boolean SHOW_TEST = false;
    public static boolean SHOW_DEBUG = false;
    public static boolean SHOW_VERBOSE = true;
    public static boolean SHOW_INFO = true;
    public static boolean SHOW_WARN = true;
    public static boolean SHOW_ERROR = true;
    
    private static final String LOG_POSITION_FORMAT = "[(%s:%s)#%s]: ";
    
    private JLog() {
    
    }
    
    private static JLog getInstance() {
        if (mInstance == null) {
            synchronized (JLog.class) {
                if (mInstance == null) {
                    mInstance = new JLog();
                }
            }
        }
        
        return mInstance;
    }
    
    public static void init(String basePath, int maxSaveDays, boolean isSaveLog) {
        getInstance();
        mInstance.isSaveLog = isSaveLog;
        if (isSaveLog) {
            LogFileUtil.initBasePath(basePath, maxSaveDays);
        }
    }
    
    private static String getLogPosition() {
        StackTraceElement[] trace = new Throwable().fillInStackTrace().getStackTrace();
        if (trace == null || trace.length < 3)
            return "<unknown>";
        
        return String.format(LOG_POSITION_FORMAT, trace[2].getFileName(), trace[2].getLineNumber(),
                trace[2].getMethodName());
    }
    
    public static void V(String TAG, String info) {
        if (SHOW_VERBOSE)
            Log.v(TAG, getLogPosition() + info);
    }
    
    public static void D(String TAG, String info) {
        if (SHOW_DEBUG)
            Log.d(TAG, getLogPosition() + info);
    }
    
    public static void T(String TAG, String info) {
        if (SHOW_TEST)
            Log.i(TAG, getLogPosition() + info);
    }
    
    public static void I(String TAG, String info) {
        if (SHOW_INFO)
            Log.i(TAG, info);
    }
    
    public static void W(String TAG, String info) {
        if (SHOW_WARN)
            Log.w(TAG, getLogPosition() + info);
    }
    
    public static void E(String TAG, String info) {
        if (SHOW_ERROR)
            Log.e(TAG, getLogPosition() + info);
    }
    
    public static void V(String TAG, String info, Throwable throwable) {
        if (SHOW_VERBOSE)
            Log.v(TAG, getLogPosition() + info, throwable);
    }
    
    public static void D(String TAG, String info, Throwable throwable) {
        if (SHOW_DEBUG)
            Log.d(TAG, getLogPosition() + info, throwable);
    }
    
    public static void T(String TAG, String info, Throwable throwable) {
        if (SHOW_TEST)
            Log.v(TAG, getLogPosition() + info, throwable);
    }
    
    public static void I(String TAG, String info, Throwable throwable) {
        if (SHOW_INFO)
            Log.i(TAG, info, throwable);
    }
    
    public static void W(String TAG, String info, Throwable throwable) {
        if (SHOW_WARN)
            Log.w(TAG, getLogPosition() + info, throwable);
    }
    
    public static void E(String TAG, String info, Throwable throwable) {
        if (SHOW_ERROR)
            Log.e(TAG, getLogPosition() + info, throwable);
    }
    
    public static void saveLog(String TAG, String info) {
        if (mInstance != null && mInstance.isSaveLog) {
            info = getLogPosition() + " " + info;
            if (SHOW_DEBUG)
                Log.i(TAG, info);
            LogFileUtil.writeLog(TAG + ": " + info);
        }
    }
    
    public static void saveLog(String TAG, String info, Throwable e) {
        if (mInstance != null && mInstance.isSaveLog) {
            info = getLogPosition() + " " + info;
            if (e != null)
                info += "\n" + Log.getStackTraceString(e);
            
            if (SHOW_ERROR)
                Log.e(TAG, info, e);
            LogFileUtil.writeLog(TAG + ": " + info);
        }
    }
}
