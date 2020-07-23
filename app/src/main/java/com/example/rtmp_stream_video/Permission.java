package com.example.rtmp_stream_video;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

public class Permission {

    public static int checkSelfPermission(@NonNull Context context, @NonNull String permission) {
        return PermissionChecker.checkSelfPermission(context, permission);
    }

    public static void requestPermissions(@Nullable Activity activity, @NonNull String[] permissions,
                            int requestCode) {
        if (activity == null) {
            return;
        }
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }

    public static boolean shouldShowRequestPermissionRationale(@Nullable Activity activity,
                                                 @NonNull String permission) {
        if (activity == null) {
            return false;
        }

        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
    }

    boolean isPermissionPermanentlyDenied(@Nullable Activity activity,
                                          @NonNull String permission) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return false;
        }
        return !shouldShowRequestPermissionRationale(activity, permission);
    }

    public static void openSetting(Context context){
        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + context.getPackageName()));
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(myAppSettings);
    }

    public static void handlePermission(@NonNull int[] grantResults, Context context, int requestCode, String stringPermission){
        if (grantResults.length > 0) {
            boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (storageAccepted) {
                // permission is granted
                Toast.makeText(context, "Permission granted", Toast.LENGTH_SHORT).show();
            }
            else
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    if (Permission.shouldShowRequestPermissionRationale((Activity) context, stringPermission)) {
                        Permission.requestPermissions((Activity) context,new String[]{stringPermission}, requestCode);
                    }
                    else
                    {
                        Permission.openSetting(context);
                    }
                }
            }
        }
    }
}
