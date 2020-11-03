package com.compass.ux.app;


import android.app.Application;
import android.content.Context;

import com.compass.ux.R;
import com.compass.ux.crash.CaocConfig;
import com.compass.ux.takephoto.FPVDemoApplication;
import com.compass.ux.ui.ConnectionActivity;
import com.secneo.sdk.Helper;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by xhf on 2020-06-08
 */
public class MApplication extends Application {
//    public static String EQUIPMENT_ID= RsaUtil.encrypt("Mobile_01");
    public static String EQUIPMENT_ID="";
    public static String UPLOAD_URL="http://61.155.157.42:7070/oauth/file/upload";
    public static boolean HAVE_Permission=false;



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
        //bugly
        CrashReport.initCrashReport(getApplicationContext(), "4a94e8a682", true);
        //初始化全局异常崩溃
        initCrash();


        //内存泄漏检测
        if (!LeakCanary.isInAnalyzerProcess(this)) {
            LeakCanary.install(this);
        }


    }


    private void initCrash() {
        CaocConfig.Builder.create()
                .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT) //背景模式,开启沉浸式
                .enabled(true) //是否启动全局异常捕获
                .showErrorDetails(true) //是否显示错误详细信息
                .showRestartButton(true) //是否显示重启按钮
                .trackActivities(true) //是否跟踪Activity
                .minTimeBetweenCrashesMs(2000) //崩溃的间隔时间(毫秒)
                .errorDrawable(R.mipmap.ic_launcher) //错误图标
                .restartActivity(ConnectionActivity.class) //重新启动后的activity
//                .errorActivity(YourCustomErrorActivity.class) //崩溃后的错误activity
//                .eventListener(new YourCustomEventListener()) //崩溃后的错误监听
                .apply();
    }

}
