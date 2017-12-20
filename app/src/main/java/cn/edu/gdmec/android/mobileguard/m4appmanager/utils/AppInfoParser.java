package cn.edu.gdmec.android.mobileguard.m4appmanager.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import cn.edu.gdmec.android.mobileguard.m4appmanager.entity.AppInfo;

public class AppInfoParser {

    public static List<AppInfo> getAppInfos(Context context){
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packInfos = pm.getInstalledPackages(PackageManager.GET_SIGNATURES
                +PackageManager.GET_PERMISSIONS+PackageManager.GET_ACTIVITIES);
        List<AppInfo> appinfos = new ArrayList<AppInfo>();
        for(PackageInfo packInfo:packInfos){
            AppInfo appinfo = new AppInfo();
            String packname = packInfo.packageName;
            appinfo.packageName = packname;
            Drawable icon = packInfo.applicationInfo.loadIcon(pm);
            appinfo. icon = icon;
            String appname = packInfo.applicationInfo.loadLabel(pm).toString();
            appinfo.appName = appname;
            String apkpath = packInfo.applicationInfo.sourceDir;
            appinfo.apkPath = apkpath;
            File file = new File(apkpath);
            long appSize = file.length();
            appinfo.appSize = appSize;
            int flags = packInfo.applicationInfo.flags;
            if((ApplicationInfo.FLAG_EXTERNAL_STORAGE & flags)!=0){

                appinfo.isInRoom = false;
            }else{

                appinfo.isInRoom = true;
            }
            if((ApplicationInfo.FLAG_SYSTEM&flags)!=0){

                appinfo.isUserApp = false;
            }else{

                appinfo.isUserApp = true;
            }

            appinfo.versionName = packInfo.versionName;
            appinfo.firstInstallTime = packInfo.firstInstallTime;

            StringBuilder sb = new StringBuilder();
            if(packInfo.requestedPermissions !=null){
                for(String per:packInfo.requestedPermissions){
                    sb.append(per+"\n");
                }
                appinfo.requestedPermissions = sb.toString();
            }
            sb.delete(0,sb.length());
            if(packInfo.activities !=null){
                for(ActivityInfo activityInfo:packInfo.activities){
                    sb.append(activityInfo.name+"\n");
                }
                appinfo.activities=sb.toString();
            }

            final Signature[] arrSignatures = packInfo.signatures;
            for (final Signature sig : arrSignatures) {

                final byte[] rawCert = sig.toByteArray();
                InputStream certStream = new ByteArrayInputStream(rawCert);
                try {
                    CertificateFactory certFactory = CertificateFactory.getInstance("X509");
                    X509Certificate x509Cert = (X509Certificate) certFactory.generateCertificate(certStream);
                    appinfo.signature ="Certificate issuer: " + x509Cert.getIssuerDN() + "\n";
                } catch (CertificateException e) {

                }
            }

            appinfos.add(appinfo);
            appinfo = null;
        }
        return appinfos;
    }
}
