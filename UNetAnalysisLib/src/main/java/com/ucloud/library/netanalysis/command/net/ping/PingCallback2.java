package com.ucloud.library.netanalysis.command.net.ping;

/**
 * Created by joshua on 2018/9/6 16:18.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public interface PingCallback2 extends PingCallback{
    void onPing(SinglePackagePingResult result);
}
