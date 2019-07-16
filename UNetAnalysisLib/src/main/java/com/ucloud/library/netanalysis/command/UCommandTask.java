package com.ucloud.library.netanalysis.command;

import com.ucloud.library.netanalysis.utils.BaseUtil;
import com.ucloud.library.netanalysis.utils.JLog;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.concurrent.Callable;

/**
 * Created by joshua on 2018/9/4 13:53.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public abstract class UCommandTask<T> implements Callable<T> {
    protected final String TAG = getClass().getSimpleName();
    
    protected String command;
    protected Process process;
    protected boolean isRunning = false;
    
    protected InputStream dataInputStream = null;
    protected InputStream errorInputStream = null;
    protected OutputStream outputStream = null;
    protected Result originalResult;
    
    protected T resultData;
    
    protected Process createProcess(String cmd) throws IOException {
        return Runtime.getRuntime().exec(cmd);
    }
    
    protected String execCommand(String command) throws InterruptedException, IOException {
        JLog.D(TAG, "[command]:" + command);
        process = createProcess(command);
        int status = process.waitFor();
        JLog.T(TAG, "[status]: " + status);
        
        BufferedInputStream dataBufferedStream = null;
        BufferedInputStream errorBufferedStream = null;
        
        dataInputStream = process.getInputStream();
        errorInputStream = process.getErrorStream();
        dataBufferedStream = new BufferedInputStream(dataInputStream);
        errorBufferedStream = new BufferedInputStream(errorInputStream);
        String dataStr = "";
        String errorStr = "";
        
        try {
            dataStr = readData(dataBufferedStream);
            errorStr = readData(errorBufferedStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            BaseUtil.closeAllCloseable(dataBufferedStream, dataInputStream, errorBufferedStream, errorInputStream);
            process.destroy();
            parseErrorInfo(errorStr);
        }
        
        return dataStr;
    }
    
    protected abstract void parseInputInfo(String input);
    
    protected abstract void parseErrorInfo(String error);
    
    protected String readData(InputStream input) throws IOException {
        if (input == null)
            return null;
        
        int len = 0;
        byte[] buffer = new byte[1024];
        byte[] cache = null;
        while ((len = input.read(buffer)) > 0) {
            if (cache == null) {
                cache = Arrays.copyOf(buffer, len);
            } else {
                int cacheLen = cache.length;
                byte[] tmp = new byte[cacheLen + len];
                System.arraycopy(cache, 0, tmp, 0, cacheLen);
                System.arraycopy(buffer, 0, tmp, cacheLen, len);
                cache = tmp;
            }
        }
        
        if (cache == null)
            return null;
        
        return new String(cache, Charset.forName("UTF-8"));
    }
    
    protected abstract void stop();
    
    public T getResultData() {
        return resultData;
    }
    
    protected static class Result {
        protected String dataResult;
        protected String errorResult;
        
        protected String getDataResult() {
            return dataResult;
        }
        
        protected Result setDataResult(String dataResult) {
            this.dataResult = dataResult;
            return this;
        }
        
        protected String getErrorResult() {
            return errorResult;
        }
        
        protected Result setErrorResult(String errorResult) {
            this.errorResult = errorResult;
            return this;
        }
    }
}
