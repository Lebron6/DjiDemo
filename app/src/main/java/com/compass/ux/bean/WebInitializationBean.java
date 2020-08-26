package com.compass.ux.bean;

/**
 * Created by xhf
 * on 2020-08-12 10:48
 * web端刚进入页面获取的数据主要是一些不经常变的数据
 */
public class WebInitializationBean {
    //ISO
    private int ISO;
    //曝光补偿
    private int exposureCompensation;
    //曝光模式
    private int exposureMode;
    //Shutter
    private Float shutter;
    //相机模式
    private int cameraMode;
    //遥控器信息
    private int upLink;
    //图传信息
    private int downLink;
    //变焦焦距
    private int hybridZoom;
    //获取当前是哪个镜头
    private String currentLens;
    //获取对焦模式
    private String focusMode;
    //获取是否曝光锁定
    private String lockExposure;
    //获取红外的焦距
    private String thermalDigitalZoom;

    public String getThermalDigitalZoom() {
        return thermalDigitalZoom;
    }

    public void setThermalDigitalZoom(String thermalDigitalZoom) {
        this.thermalDigitalZoom = thermalDigitalZoom;
    }

    public String getLockExposure() {
        return lockExposure;
    }

    public void setLockExposure(String lockExposure) {
        this.lockExposure = lockExposure;
    }

    public String getFocusMode() {
        return focusMode;
    }

    public void setFocusMode(String focusMode) {
        this.focusMode = focusMode;
    }

    public String getCurrentLens() {
        return currentLens;
    }

    public void setCurrentLens(String currentLens) {
        this.currentLens = currentLens;
    }

    public int getHybridZoom() {
        return hybridZoom;
    }

    public void setHybridZoom(int hybridZoom) {
        this.hybridZoom = hybridZoom;
    }

    public int getISO() {
        return ISO;
    }

    public void setISO(int ISO) {
        this.ISO = ISO;
    }

    public int getExposureCompensation() {
        return exposureCompensation;
    }

    public void setExposureCompensation(int exposureCompensation) {
        this.exposureCompensation = exposureCompensation;
    }

    public int getExposureMode() {
        return exposureMode;
    }

    public void setExposureMode(int exposureMode) {
        this.exposureMode = exposureMode;
    }

    public Float getShutter() {
        return shutter;
    }

    public void setShutter(Float shutter) {
        this.shutter = shutter;
    }

    public int getCameraMode() {
        return cameraMode;
    }

    public void setCameraMode(int cameraMode) {
        this.cameraMode = cameraMode;
    }

    public int getUpLink() {
        return upLink;
    }

    public void setUpLink(int upLink) {
        this.upLink = upLink;
    }

    public int getDownLink() {
        return downLink;
    }

    public void setDownLink(int downLink) {
        this.downLink = downLink;
    }
}
