package com.ucloud.library.netanalysis.module;

/**
 * Created by joshua on 2018/9/19 15:14.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public enum UCSdkStatus {
    /**
     * 注册模块成功
     */
    REGISTER_SUCCESS,
    /**
     * 已注册过SDK
     */
    SDK_HAS_BEEN_REGISTERED,
    /**
     * 正在注册SDK中
     */
    SDK_IS_REGISTING,
    /**
     * SDK获取授权失败
     */
    OBTAIN_AUTH_FAILED,
    /**
     * SDK被远程关闭
     */
    SDK_IS_CLOSED_BY_REMOTE
}
