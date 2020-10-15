package com.compass.ux;


import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.compass.ux.GaodeMap.DJIDemoApplication;
import com.compass.ux.netty_lib.zhang.RsaUtil;
import com.compass.ux.simulator.DJISimulatorApplication;
import com.compass.ux.takephoto.FPVDemoApplication;
import com.compass.ux.utils.FileUtils;
import com.secneo.sdk.Helper;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;

/**
 * Created by xhf on 2020-06-08
 */
public class MApplication extends Application {
//    public static String EQUIPMENT_ID= RsaUtil.encrypt("Mobile_01");
    public static String EQUIPMENT_ID="";
//    public static String UPLOAD_URL="";
//    public static String UPLOAD_URL="http://10.0.1.122/oauth/file/upload";
    public static String UPLOAD_URL="http://61.155.157.42:7070/oauth/file/upload";



    private FPVDemoApplication fpvDemoApplication;
//    private DJISimulatorApplication simulatorApplication;
//    private DJIDemoApplication djiDemoApplication;
    @Override
    protected void attachBaseContext(Context paramContext) {
        super.attachBaseContext(paramContext);



        Helper.install(MApplication.this);
        //拍照
        if (fpvDemoApplication == null) {
            fpvDemoApplication = new FPVDemoApplication();
            fpvDemoApplication.setContext(this);
        }
        //控制飞行及云台
//        if (simulatorApplication == null) {
//            simulatorApplication = new DJISimulatorApplication();
//            simulatorApplication.setContext(this);
//        }
        //  高德航点
//        if (djiDemoApplication == null) {
//            djiDemoApplication = new DJIDemoApplication();
//            djiDemoApplication.setContext(this);
//        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        fpvDemoApplication.onCreate();
//        simulatorApplication.onCreate();
//        djiDemoApplication.onCreate();

        CrashReport.initCrashReport(getApplicationContext(), "4a94e8a682", true);
    }

}
