package com.compass.ux.app;


import android.app.Application;
import android.content.Context;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.secneo.sdk.Helper;
import dji.common.product.Model;
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

    private FPVDemoApplication fpvDemoApplication;

    @Override
    protected void attachBaseContext(Context paramContext) {
        super.attachBaseContext(paramContext);
        Helper.install(ApronApp.this);
        //拍照
        if (fpvDemoApplication == null) {
            fpvDemoApplication = new FPVDemoApplication();
            fpvDemoApplication.setContext(this);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        fpvDemoApplication.onCreate();
        initConfig();
//        Thread.setDefaultUncaughtExceptionHandler(this);

//        初始化全局异常崩溃
//        内存泄漏检测
//        if (!LeakCanary.isInAnalyzerProcess(this)) {
//            LeakCanary.install(this);
//        }
// queryAndLoadNewPatch不可放在attachBaseContext 中，否则无网络权限，建议放在后面任意时刻，如onCreate中
//        SophixManager.getInstance().queryAndLoadNewPatch();//查询是否有新的补丁
    }

    /**
     * Logger 初始化配置
     */
    private void initConfig() {
        PrettyFormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)  // 隐藏线程信息 默认：显示
                .methodCount(0)         // 决定打印多少行（每一行代表一个方法）默认：2
                .methodOffset(7)        // (Optional) Hides internal method calls up to offset. Default 5
//                .tag("JASON_LOGGER")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .build();

        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
            @Override
            public boolean isLoggable(int priority, String tag) {
//                return super.isLoggable(priority, tag);
                return true;
            }
        });
    }

    /**
     * This function is used to get the instance of DJIBaseProduct. If no product is connected, it
     * returns null.
     */
    public static synchronized BaseProduct getProductInstance() {
        if (null == mProduct) {
            mProduct = DJISDKManager.getInstance().getProduct();
        }
        return mProduct;
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

    public static boolean isMavicAir2() {
        BaseProduct baseProduct = getProductInstance();
        if (baseProduct != null) {
            return baseProduct.getModel() == Model.MAVIC_AIR_2;
        }
        return false;
    }

    public static boolean isM300() {
        BaseProduct baseProduct = getProductInstance();
        if (baseProduct != null) {
            return baseProduct.getModel() == Model.MATRICE_300_RTK;
        }
        return false;
    }
//    @Override
//    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
//        Intent intent = new Intent(this, FirstActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
//                Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//        android.os.Process.killProcess(android.os.Process.myPid());
//
//    }
}
