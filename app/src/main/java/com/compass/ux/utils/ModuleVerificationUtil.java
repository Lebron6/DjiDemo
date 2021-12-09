package com.compass.ux.utils;

/**
 * Created by wrs on 2019/6/19,11:53
 * projectName: Ztest5
 * packageName: com.example.admin.ztest.live
 */


import com.compass.ux.app.ApronApp;

import androidx.annotation.Nullable;
import dji.common.product.Model;
import dji.sdk.accessory.AccessoryAggregation;
import dji.sdk.accessory.beacon.Beacon;
import dji.sdk.accessory.speaker.Speaker;
import dji.sdk.accessory.spotlight.Spotlight;
import dji.sdk.base.BaseProduct;
import dji.sdk.products.Aircraft;
import dji.sdk.products.HandHeld;
import dji.sdk.sdkmanager.DJISDKManager;

/**
 * Created by dji on 16/1/6.
 */
public class ModuleVerificationUtil {

    public static boolean isProductModuleAvailable() {
        return (null != ApronApp.getProductInstance());
    }

    //网络rtk
    public static boolean isNetRtkAvailable() {
        return isFlightControllerAvailable() && isAircraft() && (null != DJISDKManager.getInstance().getRTKNetworkServiceProvider());
    }
    public static boolean isAircraft() {
        return ApronApp.getProductInstance() instanceof Aircraft;
    }

    public static boolean isHandHeld() {
        return ApronApp.getProductInstance() instanceof HandHeld;
    }

    //这个是拍照的
    public static boolean isCameraModuleAvailable() {
        return isProductModuleAvailable() && (null != ApronApp.getProductInstance().getCamera());
    }

    public static boolean isPlaybackAvailable() {
        return isCameraModuleAvailable() && (null != ApronApp.getProductInstance()
                .getCamera()
                .getPlaybackManager());
    }

    public static boolean isMediaManagerAvailable() {
        return isCameraModuleAvailable() && (null != ApronApp.getProductInstance()
                .getCamera()
                .getMediaManager());
    }


    //rtk判断
    public static boolean isRtkAvailable() {
        return isFlightControllerAvailable() && isAircraft() && (null != ApronApp.getAircraftInstance()
                .getFlightController()
                .getRTK());
    }


    public static boolean isFlightControllerAvailable() {
        return isProductModuleAvailable() && isAircraft() && (null != ApronApp.getAircraftInstance()
                .getFlightController());
    }
//    public static boolean isRemoteControllerAvailable() {
//        return isProductModuleAvailable() && isAircraft() && (null != FPVDemoApplication.getAircraftInstance()
//                .getRemoteController());
//    }
//
//    public static boolean isFlightControllerAvailable() {
//        return isProductModuleAvailable() && isAircraft() && (null != FPVDemoApplication.getAircraftInstance()
//                .getFlightController());
//    }
//
//    public static boolean isCompassAvailable() {
//        return isFlightControllerAvailable() && isAircraft() && (null != FPVDemoApplication.getAircraftInstance()
//                .getFlightController()
//                .getCompass());
//    }

//    public static boolean isFlightLimitationAvailable() {
//        return isFlightControllerAvailable() && isAircraft();
//    }

    public static boolean isGimbalModuleAvailable() {
        return isProductModuleAvailable() && (null != ApronApp.getProductInstance().getGimbal());
    }

    public static boolean isAirlinkAvailable() {
        return isProductModuleAvailable() && (null != ApronApp.getProductInstance().getAirLink());
    }

    public static boolean isWiFiLinkAvailable() {
        return isAirlinkAvailable() && (null != ApronApp.getProductInstance().getAirLink().getWiFiLink());
    }

    public static boolean isLightbridgeLinkAvailable() {
        return isAirlinkAvailable() && (null != ApronApp.getProductInstance()
                .getAirLink()
                .getLightbridgeLink());
    }

    public static AccessoryAggregation getAccessoryAggregation() {
        Aircraft aircraft = (Aircraft) ApronApp.getProductInstance();

        if (aircraft != null && null != aircraft.getAccessoryAggregation()) {
            return aircraft.getAccessoryAggregation();
        }
        return null;
    }

    public static Speaker getSpeaker() {
        Aircraft aircraft = (Aircraft) ApronApp.getProductInstance();

        if (aircraft != null && null != aircraft.getAccessoryAggregation() && null != aircraft.getAccessoryAggregation().getSpeaker()) {
            return aircraft.getAccessoryAggregation().getSpeaker();
        }
        return null;
    }

    public static Beacon getBeacon() {
        Aircraft aircraft = (Aircraft) ApronApp.getProductInstance();

        if (aircraft != null && null != aircraft.getAccessoryAggregation() && null != aircraft.getAccessoryAggregation().getBeacon()) {
            return aircraft.getAccessoryAggregation().getBeacon();
        }
        return null;
    }

    public static Spotlight getSpotlight() {
        Aircraft aircraft = (Aircraft) ApronApp.getProductInstance();

        if (aircraft != null && null != aircraft.getAccessoryAggregation() && null != aircraft.getAccessoryAggregation().getSpotlight()) {
            return aircraft.getAccessoryAggregation().getSpotlight();
        }
        return null;
    }

//    @Nullable
//    public static Simulator getSimulator() {
//        Aircraft aircraft = FPVDemoApplication.getAircraftInstance();
//        if (aircraft != null) {
//            FlightController flightController = aircraft.getFlightController();
//            if (flightController != null) {
//                return flightController.getSimulator();
//            }
//        }
//        return null;
//    }
//
//    @Nullable
//    public static FlightController getFlightController() {
//        Aircraft aircraft = FPVDemoApplication.getAircraftInstance();
//        if (aircraft != null) {
//            return aircraft.getFlightController();
//        }
//        return null;
//    }

    @Nullable
    public static boolean isMavic2Product() {
        BaseProduct baseProduct = ApronApp.getProductInstance();
        if (baseProduct != null) {
            return baseProduct.getModel() == Model.MAVIC_2_PRO || baseProduct.getModel() == Model.MAVIC_2_ZOOM;
        }
        return false;
    }



}
