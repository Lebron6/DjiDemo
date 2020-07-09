package com.compass.ux;


import android.app.Application;
import android.content.Context;

import com.compass.ux.GaodeMap.DJIDemoApplication;
import com.compass.ux.simulator.DJISimulatorApplication;
import com.compass.ux.takephoto.FPVDemoApplication;
import com.secneo.sdk.Helper;
/**
 * Created by xhf on 2020-06-08
 */
public class MApplication extends Application {
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
    }

}
