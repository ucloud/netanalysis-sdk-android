package com.ucloud.library.netanalysis.utils;

import android.text.TextUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by joshua on 2018/8/28 10:01.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class IPUtil {
    private static final String TAG = "IPUtil";
    
    public static boolean isIPv4(String ip) {
        if (TextUtils.isEmpty(ip))
            return false;
        
        return ip.matches("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");
    }
    
    public static List<InetAddress> parseIPv4Addresses(String host) throws UnknownHostException {
        List<InetAddress> list = new ArrayList<>();
        InetAddress[] arrInetAddress = InetAddress.getAllByName(host);
        for (InetAddress addr : arrInetAddress) {
            if (IPUtil.isIPv4(addr.getHostAddress()))
                list.add(addr);
        }
        
        return list;
    }
    
    public static InetAddress parseIPv4Address(String host) throws UnknownHostException {
        List<InetAddress> arrInetAddress = IPUtil.parseIPv4Addresses(host);
        if (arrInetAddress != null && arrInetAddress.size() > 0)
            return arrInetAddress.get(0);
        
        throw new UnknownHostException(host);
    }
    
    public static String parseIPv4AddressString(String host) throws UnknownHostException {
        InetAddress address = parseIPv4Address(host);
        if (address == null)
            throw new UnknownHostException(host);
        
        return address.getHostAddress();
    }
}
