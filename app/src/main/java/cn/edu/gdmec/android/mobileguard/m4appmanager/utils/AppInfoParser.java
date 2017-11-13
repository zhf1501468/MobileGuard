package cn.edu.gdmec.android.mobileguard.m4appmanager.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import cn.edu.gdmec.android.mobileguard.m4appmanager.entity.AppInfo;



public class AppInfoParser {
    public static List<AppInfo> getAppInfos(Context context){
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
        List<AppInfo> appInfos = new ArrayList<AppInfo>();
        for (PackageInfo packageInfo:packageInfos){
            AppInfo appInfo = new AppInfo();
            String packname = packageInfo.packageName;
            appInfo.packageName = packname;
            Drawable icon = packageInfo.applicationInfo.loadIcon(pm);
            appInfo.icon = icon;
            String appname = packageInfo.applicationInfo.loadLabel(pm).toString();
            appInfo.appName = appname;




            String appversion = packageInfo.versionName;
            appInfo.appVersion = appversion;

            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy年MM月dd号 hh:mm:ss");
            long installdate = packageInfo.firstInstallTime;
            appInfo.inStalldate = dateformat.format(installdate);;

            try {
                packageInfo = pm.getPackageInfo(appInfo.packageName, PackageManager.GET_PERMISSIONS);
                String[] permissions = packageInfo.requestedPermissions;
                List<String> a = new ArrayList<String>();
                if (permissions != null){
                    for (String str : permissions){
                        a.add ( str );
                    }
                }
                appInfo.Permissions = Pattern.compile("\\b([\\w\\W])\\b").matcher(a.toString().substring(1,a.toString().length()-1)).replaceAll(".");

            }catch (Exception e){
                e.printStackTrace();
            }

            try {
                packageInfo = pm.getPackageInfo ( appInfo.packageName, PackageManager.GET_SIGNATURES );
                Signature[] signatures = packageInfo.signatures;
                CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
                X509Certificate cert = (X509Certificate) certFactory.generateCertificate ( new ByteArrayInputStream( signatures[0].toByteArray ()));
                String certmsg = "";
                certmsg += cert.getIssuerDN().toString ();
                certmsg += cert.getSubjectDN().toString();
                appInfo.certMsg = certmsg;
            }catch (Exception e){
                e.printStackTrace();
            }



            String apkpath = packageInfo.applicationInfo.sourceDir;
            appInfo.apkPath = apkpath;
            File file = new File(apkpath);
            long appSize = file.length();
            appInfo.appSize = appSize;

            int flags = packageInfo.applicationInfo.flags;
            if ((ApplicationInfo.FLAG_EXTERNAL_STORAGE & flags) != 0){
                appInfo.isInRoom = false;
            }else {
                appInfo.isInRoom = true;
            }
            if ((ApplicationInfo.FLAG_SYSTEM & flags) != 0){
                appInfo.isUserApp = false;
            }else{
                appInfo.isUserApp = true;
            }
            appInfos.add(appInfo);
            appInfo = null;
        }
        return appInfos;
    }
}
