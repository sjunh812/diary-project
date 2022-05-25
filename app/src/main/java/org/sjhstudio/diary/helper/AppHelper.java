package org.sjhstudio.diary.helper;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Build;

public class AppHelper {

    private PackageInfo packageInfo = null;
    private String versionName;
    private String modelName;
    private String osName;

    public AppHelper(Context context) {
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch(Exception e) {
            e.printStackTrace();
        }

        if(packageInfo != null) {
            versionName = packageInfo.versionName;
        }

        modelName = Build.MODEL;
        osName = Build.VERSION.RELEASE;
    }

    public String getVersionName() {
        return versionName;
    }

    public String getModelName() {
        return modelName;
    }

    public String getOsName() {
        return osName;
    }

}
