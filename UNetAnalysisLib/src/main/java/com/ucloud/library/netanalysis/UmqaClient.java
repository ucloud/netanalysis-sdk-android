package com.ucloud.library.netanalysis;

import android.content.Context;
import androidx.annotation.NonNull;

import com.ucloud.library.netanalysis.callback.OnSdkListener;
import com.ucloud.library.netanalysis.module.UCNetworkInfo;
import com.ucloud.library.netanalysis.module.UserDefinedData;
import com.ucloud.library.netanalysis.utils.UCConfig;

import java.util.List;

/**
 * Created by joshua on 2019-07-25 11:04.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class UmqaClient {
    public static final String SDK_VERSION = String.format("Android/%s", BuildConfig.VERSION_NAME);
    public static final int CUSTOM_IP_LIST_SIZE = 5;
    
    private volatile static UCNetAnalysisManager mManager;
    
    /**
     * 初始化SDK模块
     *
     * @param applicationContext application的context
     * @param appKey             UCloud为您的APP分配的APP_KEY
     * @param appSecret          UCloud为您的APP分配的APP_SECRET
     * @return 是否init成功，若重复init，则返回false，需要先destroy后重新init
     */
    public synchronized static boolean init(@NonNull Context applicationContext,
                                            @NonNull String appKey, @NonNull String appSecret) {
        if (mManager == null) {
            mManager = new UCNetAnalysisManager(applicationContext, appKey, appSecret);
            return true;
        }
        
        return false;
    }
    
    /**
     * 注册并启用SDK模块
     *
     * @param listener OnSdkListener回调接口, {@link OnSdkListener}
     * @return 是否register成功，若没有init，则返回false
     */
    public synchronized static boolean register(OnSdkListener listener) {
        return register(listener, null);
    }
    
    /**
     * 注册并启用SDK模块 (带有自定义配置项)
     *
     * @param listener OnSdkListener回调接口, {@link OnSdkListener}
     * @param config   自定义配置项, {@link UCConfig}
     * @return 是否register成功，若没有init，则返回false
     */
    public synchronized static boolean register(OnSdkListener listener, UCConfig config) {
        if (mManager == null)
            return false;
        
        mManager.register(listener, config);
        return true;
    }
    
    /**
     * 注销SDK模块
     *
     * @return 是否unregister成功，若没有init，则返回false
     */
    public synchronized static boolean unregister() {
        if (mManager == null)
            return false;
        
        mManager.unregister();
        return true;
    }
    
    /**
     * 设置自定义的IP列表
     * 1、最多5个IP，多于5个的，自动取前5个
     * 2、不支持填写域名，请填写IP地址
     *
     * @param custonIps 自定义的IP列表
     * @return 是否设置成功，若没有init，则返回false
     */
    public synchronized static boolean setCustomIps(List<String> custonIps) {
        if (mManager == null)
            return false;
        
        mManager.setCustomIps(custonIps);
        return true;
    }
    
    /**
     * 获取已设置的自定义的IP地址
     *
     * @return 已设置的自定义IP列表，null: UmqaClient未init
     */
    public synchronized static List<String> getCustomIps() {
        if (mManager == null)
            return null;
        
        return mManager.getCustomIps();
    }
    
    /**
     * 手动触发网络检测
     *
     * @return 是否触发检测成功，若没有init或者没有register，则返回false
     */
    public synchronized static boolean analyse() {
        if (mManager == null)
            return false;
        
        return mManager.analyse();
    }
    
    /**
     * 设置自定义上报字段
     *
     * @param userDefinedData 自定义上报字段, {@link UserDefinedData}
     * @return 是否设置成功，若没有init，则返回false
     */
    public synchronized static boolean setUserDefinedData(UserDefinedData userDefinedData) {
        if (mManager == null)
            return false;
        
        mManager.setUserDefinedData(userDefinedData);
        return true;
    }
    
    /**
     * 设置SDK回调接口
     *
     * @param listener OnSdkListener回调接口, {@link OnSdkListener}
     * @return 是否设置成功，若没有init，则返回false
     */
    public synchronized static boolean setSdkListener(OnSdkListener listener) {
        if (mManager == null)
            return false;
        
        mManager.setSdkListener(listener);
        return true;
    }
    
    /**
     * 检查当前设备网络状态
     *
     * @return 当前设备的网络状态, null: UmqaClient可能未init
     */
    public synchronized static UCNetworkInfo checkNetworkStatus() {
        if (mManager == null)
            return null;
        
        return mManager.checkNetworkStatus();
    }
    
    /**
     * 销毁SDK模块
     */
    public synchronized static void destroy() {
        if (mManager == null)
            return;
        
        mManager.destroy();
        mManager = null;
    }
}
