package com.ucloud.library.netanalysis.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by joshua on 2018/9/4 14:37.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class BaseUtil {
    public static void closeAllCloseable(Closeable... closeables) {
        if (closeables == null || closeables.length <= 0)
            return;
        
        for (Closeable closeable : closeables) {
            if (closeable != null)
                try {
                    closeable.close();
                } catch (IOException e) {
                    JLog.E("BaseUtil", "filterRsaKey occur error: " + e.getMessage());
                    continue;
                }
        }
    }
}
