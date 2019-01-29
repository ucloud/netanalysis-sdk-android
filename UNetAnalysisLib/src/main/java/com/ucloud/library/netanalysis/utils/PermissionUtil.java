package com.ucloud.library.netanalysis.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class PermissionUtil {
    private static final String TAG = "PermissionUtil";

    public static boolean checkPermission(Context context, String permission) {
        boolean flag = true;
        if (Build.VERSION.SDK_INT >= 23)
            flag = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;

        return flag;
    }

    public static void requestPermissions(Activity activity, List<String> permissions, int requestCode) {
        if (permissions == null || permissions.isEmpty())
            return;

        String[] permission = permissions.toArray(new String[permissions.size()]);
        requestPermissions(activity, permission, requestCode);
    }

    public static void requestPermissions(Activity activity, String[] permissions, int requestCode) {
        if (permissions == null || permissions.length <= 0)
            return;

        if (Build.VERSION.SDK_INT >= 23) {
            for (String per : permissions)
                JLog.saveLog(TAG, "requestPermissions--->" + per);

            ActivityCompat.requestPermissions(activity, permissions, requestCode);
        }
    }

    public static boolean shouldShowRequestPermissionRationale(Activity activity, String permission) {
        if (Build.VERSION.SDK_INT >= 23) {
            boolean res = ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
            if (res)
                JLog.saveLog(TAG, "shouldShowRequestPermissionRationale->[permission]: "
                        + permission);
            return res;
        }

        return false;
    }

    private void requestPermission(Activity activity, String permission, int requestCode) {
        if (TextUtils.isEmpty(permission))
            return;

        if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
        }
    }

    public static List<String> makePermission(Context context, String[] permission) {
        List<String> permissions = new ArrayList<>();

        if (permission == null || permission.length == 0)
            return permissions;

        for (String per : permission) {
            if (! checkPermission(context, per))
                permissions.add(per);
        }

        return permissions;
    }


}
