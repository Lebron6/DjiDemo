package com.compass.ux.app;


import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.compass.ux.R;
import com.compass.ux.crash.CaocConfig;
import com.compass.ux.ui.FirstActivity;
import com.secneo.sdk.Helper;
import com.taobao.sophix.SophixManager;
import com.tencent.bugly.crashreport.CrashReport;

import dji.sdk.airlink.AirLink;
import dji.sdk.base.BaseProduct;
import dji.sdk.camera.Camera;
import dji.sdk.products.Aircraft;
import dji.sdk.products.HandHeld;
import dji.sdk.sdkmanager.DJISDKManager;

/**
 * Created by xhf on 2020-06-08
 */
public class ApronApp extends Application {
    public static String EQUIPMENT_ID;
    public static boolean HAVE_Permission = false;
    private static BaseProduct mProduct;

    @Override
    public void onCreate() {
        super.onCreate();
        Helper.install(ApronApp.this);

//        初始化全局异常崩溃
//        initCrash();

        //内存泄漏检测
//        if (!LeakCanary.isInAnalyzerProcess(this)) {
//            LeakCanary.install(this);
//        }

// 主要是添加下面这句代码
//        MultiDex.install(this);
// queryAndLoadNewPatch不可放在attachBaseContext 中，否则无网络权限，建议放在后面任意时刻，如onCreate中
//        SophixManager.getInstance().queryAndLoadNewPatch();//查询是否有新的补丁
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
                .restartActivity(FirstActivity.class) //重新启动后的activity
//                .errorActivity(YourCustomErrorActivity.class) //崩溃后的错误activity
//                .eventListener(new YourCustomEventListener()) //崩溃后的错误监听
                .apply();
    }

    /**
     * This function is used to get the instance of DJIBaseProduct.
     * If no product is connected, it returns null.
     */
    public static synchronized BaseProduct getProductInstance() {
        if (null == mProduct) {
            mProduct = DJISDKManager.getInstance().getProduct();
        }
        return mProduct;
    }

    public static synchronized Camera getCameraInstance() {

        if (getProductInstance() == null) return null;

        Camera camera = null;

        if (getProductInstance() instanceof Aircraft) {
            camera = ((Aircraft) getProductInstance()).getCamera();

        } else if (getProductInstance() instanceof HandHeld) {
            camera = ((HandHeld) getProductInstance()).getCamera();
        }

        return camera;
    }

    public static synchronized AirLink getAirLinkInstance() {

        if (getProductInstance() == null) return null;

        AirLink airLink = null;

        if (getProductInstance() instanceof Aircraft) {
            airLink = ((Aircraft) getProductInstance()).getAirLink();

        } else if (getProductInstance() instanceof HandHeld) {
            airLink = ((HandHeld) getProductInstance()).getAirLink();
        }

        return airLink;
    }


    public static boolean isAircraftConnected() {
        return getProductInstance() != null && getProductInstance() instanceof Aircraft;
    }

    public static synchronized Aircraft getAircraftInstance() {
        if (!isAircraftConnected()) return null;
        return (Aircraft) getProductInstance();
    }

}
