package com.ucloud.library.netanalysis.callback;

import com.ucloud.library.netanalysis.module.UCNetworkInfo;
import com.ucloud.library.netanalysis.module.UCSdkStatus;

/**
 * Created by joshua on 2018/9/19 14:11.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public interface OnSdkListener {
    /** 注册结果回调, {@link UCSdkStatus} */
    void onRegister(UCSdkStatus status);
    
    /** 网络情况改变回调, {@link UCNetworkInfo} */
    void onNetworkStatusChanged(UCNetworkInfo networkInfo);
}
