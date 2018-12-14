package com.ucloud.library.netanalysis.command.net.ping;


import com.ucloud.library.netanalysis.command.bean.UCommandStatus;

/**
 * Created by joshua on 2018/9/6 16:18.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public interface PingCallback {
    void onPingFinish(PingResult result, UCommandStatus status);
}
