package com.ucloud.library.netanalysis.command.net.traceroute;

/**
 * Created by joshua on 2018/9/6 16:18.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public interface TracerouteCallback2 extends TracerouteCallback {
    void onTracerouteNode(TracerouteNodeResult result);
}
