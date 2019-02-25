package com.ucloud.library.netanalysis.module;

/**
 * Created by joshua on 2018/9/19 14:53.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * GPRS    2G(2.5) General Packet Radia Service 114kbps
 * EDGE    2G(2.75G) Enhanced Data Rate for GSM Evolution 384kbps
 * UMTS    3G WCDMA 联通3G Universal MOBILE Telecommunication System 完整的3G移动通信技术标准
 * CDMA    2G 电信 Code Division Multiple Access 码分多址
 * EVDO_0  3G (EVDO 全程 CDMA2000 1xEV-DO) Evolution - Data Only (Data Optimized) 153.6kps - 2.4mbps 属于3G
 * EVDO_A  3G 1.8mbps - 3.1mbps 属于3G过渡，3.5G
 * 1xRTT   2G CDMA2000 1xRTT (RTT - 无线电传输技术) 144kbps 2G的过渡,
 * HSDPA   3.5G 高速下行分组接入 3.5G WCDMA High Speed Downlink Packet Access 14.4mbps
 * HSUPA   3.5G High Speed Uplink Packet Access 高速上行链路分组接入 1.4 - 5.8 mbps
 * HSPA    3G (分HSDPA,HSUPA) High Speed Packet Access
 * IDEN    2G Integrated Dispatch Enhanced Networks 集成数字增强型网络 （属于2G，来自维基百科）
 * EVDO_B  3G EV-DO Rev.B 14.7Mbps 下行 3.5G
 * LTE     4G Long Term Evolution FDD-LTE 和 TDD-LTE , 3G过渡，升级版 LTE Advanced 才是4G
 * EHRPD   3G CDMA2000向LTE 4G的中间产物 Evolved High Rate Packet Data HRPD的升级
 * HSPAP   3G HSPAP 比 HSDPA 快些
 */
public enum UCNetStatus {
    /** 无网络连接 */
    NET_STATUS_NOT_REACHABLE("NOT_REACHABLE"),
    /** WIFI网络 */
    NET_STATUS_WIFI("WIFI"),
    /** 4G网络 */
    NET_STATUS_4G("4G"),
    /** 3G网络 */
    NET_STATUS_3G("3G"),
    /** 2G网络 */
    NET_STATUS_2G("2G"),
    /** 未知类型 */
    NET_STATUS_UNKNOW("UNKNOW");
    
    private String value;
    
    UCNetStatus(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static UCNetStatus parseStatusByNetworkInfo(NetworkInfo networkInfo) {
        if (networkInfo == null)
            return NET_STATUS_NOT_REACHABLE;
        
        if (ConnectivityManager.TYPE_WIFI == networkInfo.getType())
            return NET_STATUS_WIFI;
        
        switch (networkInfo.getSubtype()) {
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
            case TelephonyManager.NETWORK_TYPE_GSM:
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return NET_STATUS_2G;
            case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return NET_STATUS_3G;
            case TelephonyManager.NETWORK_TYPE_IWLAN:
            case TelephonyManager.NETWORK_TYPE_LTE:
                return NET_STATUS_4G;
            default:
                String subtypeName = networkInfo.getSubtypeName();
                if (subtypeName.equalsIgnoreCase("TD-SCDMA")
                        || subtypeName.equalsIgnoreCase("WCDMA")
                        || subtypeName.equalsIgnoreCase("CDMA2000")) {
                    return NET_STATUS_3G;
                }
                
                return NET_STATUS_UNKNOW;
        }
    }
}
