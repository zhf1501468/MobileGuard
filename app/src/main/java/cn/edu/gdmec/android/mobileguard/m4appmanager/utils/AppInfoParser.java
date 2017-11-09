package cn.edu.gdmec.android.mobileguard.m4appmanager.utils;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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

    public  static List<AppInfo> getAppInfos(Context context){
        PackageManager pm = context.getPackageManager();
        List<PackageInfo>packInfos = pm.getInstalledPackages(0);
        List<AppInfo> appinfos= new ArrayList<AppInfo>();
        for(PackageInfo packInfo:packInfos){
            AppInfo appinfo = new AppInfo();
            String packname = packInfo.packageName;
            appinfo.packageName =packname;
            Drawable icon = packInfo.applicationInfo.loadIcon(pm);
            appinfo.icon= icon;
            String appname = packInfo.applicationInfo.loadLabel(pm).toString();
            appinfo.appName = appname;

            String appversion = packInfo.versionName;
            appinfo.appVersion = appversion;

            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy年MM月dd号 hh:mm:ss");
            long installdate = packInfo.firstInstallTime;
            appinfo.inStalldate = dateformat.format(installdate);;

            try {
                packInfo = pm.getPackageInfo(appinfo.packageName, PackageManager.GET_PERMISSIONS);
                String[] permissions = packInfo.requestedPermissions;
                List<String> a = new ArrayList<String>();
                if (permissions != null){
                    for (String str : permissions){
                        a.add ( str );
                    }
                }
                appinfo.Permissions = Pattern.compile("\\b([\\w\\W])\\b").matcher(a.toString().substring(1,a.toString().length()-1)).replaceAll(".");

            }catch (Exception e){
                e.printStackTrace();
            }

            try {
                packInfo = pm.getPackageInfo ( appinfo.packageName, PackageManager.GET_SIGNATURES );
                Signature[] signatures = packInfo.signatures;
                CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
                X509Certificate cert = (X509Certificate) certFactory.generateCertificate ( new ByteArrayInputStream( signatures[0].toByteArray ()));
                String certmsg = "";
                certmsg += cert.getIssuerDN().toString ();
                certmsg += cert.getSubjectDN().toString();
                appinfo.certMsg = certmsg;
            }catch (Exception e){
                e.printStackTrace();
            }
            String apkpath = packInfo.applicationInfo.sourceDir;
            appinfo.apkPath = apkpath;
            File file = new File(apkpath);
            long appSize = file.length();
            appinfo.appSize = appSize;
            int flags = packInfo.applicationInfo.flags;
            if ((ApplicationInfo.FLAG_EXTERNAL_STORAGE & flags)!=0){
                appinfo.isInRoom = false;
            }else {
                appinfo.isInRoom = true;
            }
            appinfos.add(appinfo);
            appinfo = null;
        }
        return  appinfos;
    }

}
//新增2333