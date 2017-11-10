package cn.edu.gdmec.android.mobileguard.m4appmanager.entity;

import android.content.pm.PermissionInfo;
import android.graphics.drawable.Drawable;

/**
 * Created by DONG on 2017/11/5.
 */

public class AppInfo {
    public String packageName;
    public Drawable icon;
    public String appName;
    public String apkPath;
    public long appSize;
    public boolean isInRoom;
    public boolean isUserApp;
    public boolean isSelected = false;
//------------------------- 添加内容 start   ------------------------------------------
    public String appVersion;
    public String inStalldate;
    public String Permissions;
    public String certMsg = "";
//------------------------- 添加内容 end   ---------------------------------------------

    public String getAppLocation(boolean isInRoom){
        if (isInRoom){
            return "手机内存";
        }else{
            return "外部存储";
        }
    }
}
