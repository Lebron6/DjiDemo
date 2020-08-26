package com.compass.ux.bean;

import dji.common.camera.SettingsDefinitions;
import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;

/**
 * Created by xhf
 * on 2020-08-05 09:49
 */
public class CameraDataBean {
    //ISO
    private String ISO;
    //曝光补偿
    private String ExposureCompensation;
    //曝光模式
    private String ExposureMode;
    //Shutter
    private String Shutter;

    public String getShutter() {
        return Shutter;
    }

    public void setShutter(String shutter) {
        Shutter = shutter;
    }


    public String getExposureMode() {
        return ExposureMode;
    }

    public void setExposureMode(String exposureMode) {
        ExposureMode = exposureMode;
    }
    public String getISO() {
        return ISO;
    }

    public void setISO(String ISO) {
        this.ISO = ISO;
    }

    public String getExposureCompensation() {
        return ExposureCompensation;
    }

    public void setExposureCompensation(String exposureCompensation) {
        ExposureCompensation = exposureCompensation;
    }






}
