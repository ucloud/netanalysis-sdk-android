package com.ucloud.library.netanalysis.utils;

import java.util.List;
import java.util.Random;

/**
 * Created by joshua on 2018/12/25 14:51.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class HexFormatter {
    public static int BCD2Int(byte data) {
        data &= 0xff;
        int low = data & 0xf;
        int high = (data >> 4) & 0xf;
        
        return high * 10 + low;
    }
    
    public static byte Int2BCD(int data) {
        return (byte) ((Integer.parseInt(Integer.toString(data), 16)) & 0xff);
    }
    
    public static String makeHexRandomString(int byteSize) {
        if (byteSize < 1)
            return "";
        
        Random random = new Random(System.currentTimeMillis());
        
        StringBuilder sb = new StringBuilder();
        int times = (int) Math.ceil(byteSize / 4.f);
        
        for (int i = 0; i < times; i++) {
            int intRnd = random.nextInt();
            for (int j = 0; j < 4; j++)
                sb.append(String.format("%02X", ((intRnd >> (j * 8)) & 0xff)));
        }
        
        String rnd = sb.toString();
        return rnd.substring(0, byteSize * 2);
    }
    
    public static byte[] formatHexString2ByteArray(String str) {
        if (str == null || str.length() == 0)
            return null;
        
        int byteLen = (int) Math.ceil(str.length() / 2.f);
        return formatHexString2ByteArray(str, byteLen);
    }
    
    public static byte[] formatHexString2ByteArray(String str, int byteLen) {
        if (str == null || str.length() == 0)
            return null;
        
        str = str.trim();
        str = str.replace(" ", "");
        int dlt = str.length() - byteLen * 2;
        if (dlt > 0) {
            str = str.substring(dlt);
        } else if (dlt < 0) {
            dlt = Math.abs(dlt);
            
            if (dlt > 0) {
                StringBuilder sb = new StringBuilder();
                while (dlt > 0) {
                    sb.append("0");
                    sb.append(str);
                    dlt -= 1;
                }
                str = sb.toString();
            }
        }
        
        byte[] arr = new byte[byteLen];
        try {
            for (int i = 0; i < byteLen; i++)
                arr[i] = (byte) Integer.parseInt(str.substring(i * 2, i * 2 + 2), 16);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
        return arr;
    }
    
    public static String formatByteArray2HexString(byte[] data, boolean isUpperCase) {
        if (data == null || data.length == 0)
            return "";
    
        StringBuffer sb = new StringBuffer();
        for (byte b : data)
            sb.append(String.format("%02x", b));
        
        return isUpperCase ? sb.toString().toUpperCase() : sb.toString();
    }
    
    public static String formatByteArray2HexString(List<Byte> data, boolean isUpperCase) {
        if (data == null || data.isEmpty())
            return "";
    
        StringBuffer sb = new StringBuffer();
        for (byte b : data)
            sb.append(String.format("%02x", b));
        
        return isUpperCase ? sb.toString().toUpperCase() : sb.toString();
    }
}
