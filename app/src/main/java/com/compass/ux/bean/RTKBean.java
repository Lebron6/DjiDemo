package com.compass.ux.bean;

/**
 * Created by xhf
 * on 2020-10-12 10:09
 */
public class RTKBean {
    //移动基站lat
    private String baseStationLatitude;
    //移动基站long
    private String baseStationLongitude;
    //移动基站高度
    private String baseStationAltitude;
    //融合lat
    private String fusionMobileStationLatitude;
    //融合long
    private String fusionMobileStationLongitude;
    //融合高度
    private String fusionMobileStationAltitude;
    //天线1
    private String mobileStationReceiver1GPSInfo;
    private String mobileStationReceiver1BeiDouInfo;
    private String mobileStationReceiver1GLONASSInfo;
    private String mobileStationReceiver1GalileoInfo;

    //天线2
    private String mobileStationReceiver2GPSInfo;
    private String mobileStationReceiver2BeiDouInfo;
    private String mobileStationReceiver2GLONASSInfo;
    private String mobileStationReceiver2GalileoInfo;

    //移动站
    private String baseStationReceiverGPSInfo;
    private String baseStationReceiverBeiDouInfo;
    private String baseStationReceiverGLONASSInfo;
    private String baseStationReceiverGalileoInfo;

    //判断rtk是否正在使用
    private boolean isRTKBeingUsed;
    //离原点的距离
    private String distanceToHomePoint;

    public String getDistanceToHomePoint() {
        return distanceToHomePoint;
    }

    public void setDistanceToHomePoint(String distanceToHomePoint) {
        this.distanceToHomePoint = distanceToHomePoint;
    }



    public boolean isRTKBeingUsed() {
        return isRTKBeingUsed;
    }

    public void setRTKBeingUsed(boolean RTKBeingUsed) {
        isRTKBeingUsed = RTKBeingUsed;
    }




    public String getBaseStationLatitude() {
        return baseStationLatitude;
    }

    public void setBaseStationLatitude(String baseStationLatitude) {
        this.baseStationLatitude = baseStationLatitude;
    }

    public String getBaseStationLongitude() {
        return baseStationLongitude;
    }

    public void setBaseStationLongitude(String baseStationLongitude) {
        this.baseStationLongitude = baseStationLongitude;
    }

    public String getBaseStationAltitude() {
        return baseStationAltitude;
    }

    public void setBaseStationAltitude(String baseStationAltitude) {
        this.baseStationAltitude = baseStationAltitude;
    }

    public String getMobileStationReceiver1GPSInfo() {
        return mobileStationReceiver1GPSInfo;
    }

    public void setMobileStationReceiver1GPSInfo(String mobileStationReceiver1GPSInfo) {
        this.mobileStationReceiver1GPSInfo = mobileStationReceiver1GPSInfo;
    }

    public String getMobileStationReceiver1BeiDouInfo() {
        return mobileStationReceiver1BeiDouInfo;
    }

    public void setMobileStationReceiver1BeiDouInfo(String mobileStationReceiver1BeiDouInfo) {
        this.mobileStationReceiver1BeiDouInfo = mobileStationReceiver1BeiDouInfo;
    }

    public String getMobileStationReceiver1GLONASSInfo() {
        return mobileStationReceiver1GLONASSInfo;
    }

    public void setMobileStationReceiver1GLONASSInfo(String mobileStationReceiver1GLONASSInfo) {
        this.mobileStationReceiver1GLONASSInfo = mobileStationReceiver1GLONASSInfo;
    }

    public String getMobileStationReceiver1GalileoInfo() {
        return mobileStationReceiver1GalileoInfo;
    }

    public void setMobileStationReceiver1GalileoInfo(String mobileStationReceiver1GalileoInfo) {
        this.mobileStationReceiver1GalileoInfo = mobileStationReceiver1GalileoInfo;
    }

    public String getMobileStationReceiver2GPSInfo() {
        return mobileStationReceiver2GPSInfo;
    }

    public void setMobileStationReceiver2GPSInfo(String mobileStationReceiver2GPSInfo) {
        this.mobileStationReceiver2GPSInfo = mobileStationReceiver2GPSInfo;
    }

    public String getMobileStationReceiver2BeiDouInfo() {
        return mobileStationReceiver2BeiDouInfo;
    }

    public void setMobileStationReceiver2BeiDouInfo(String mobileStationReceiver2BeiDouInfo) {
        this.mobileStationReceiver2BeiDouInfo = mobileStationReceiver2BeiDouInfo;
    }

    public String getMobileStationReceiver2GLONASSInfo() {
        return mobileStationReceiver2GLONASSInfo;
    }

    public void setMobileStationReceiver2GLONASSInfo(String mobileStationReceiver2GLONASSInfo) {
        this.mobileStationReceiver2GLONASSInfo = mobileStationReceiver2GLONASSInfo;
    }

    public String getMobileStationReceiver2GalileoInfo() {
        return mobileStationReceiver2GalileoInfo;
    }

    public void setMobileStationReceiver2GalileoInfo(String mobileStationReceiver2GalileoInfo) {
        this.mobileStationReceiver2GalileoInfo = mobileStationReceiver2GalileoInfo;
    }

    public String getBaseStationReceiverGPSInfo() {
        return baseStationReceiverGPSInfo;
    }

    public void setBaseStationReceiverGPSInfo(String baseStationReceiverGPSInfo) {
        this.baseStationReceiverGPSInfo = baseStationReceiverGPSInfo;
    }

    public String getBaseStationReceiverBeiDouInfo() {
        return baseStationReceiverBeiDouInfo;
    }

    public void setBaseStationReceiverBeiDouInfo(String baseStationReceiverBeiDouInfo) {
        this.baseStationReceiverBeiDouInfo = baseStationReceiverBeiDouInfo;
    }

    public String getBaseStationReceiverGLONASSInfo() {
        return baseStationReceiverGLONASSInfo;
    }

    public void setBaseStationReceiverGLONASSInfo(String baseStationReceiverGLONASSInfo) {
        this.baseStationReceiverGLONASSInfo = baseStationReceiverGLONASSInfo;
    }

    public String getBaseStationReceiverGalileoInfo() {
        return baseStationReceiverGalileoInfo;
    }

    public void setBaseStationReceiverGalileoInfo(String baseStationReceiverGalileoInfo) {
        this.baseStationReceiverGalileoInfo = baseStationReceiverGalileoInfo;
    }

    public String getFusionMobileStationLatitude() {
        return fusionMobileStationLatitude;
    }

    public void setFusionMobileStationLatitude(String fusionMobileStationLatitude) {
        this.fusionMobileStationLatitude = fusionMobileStationLatitude;
    }

    public String getFusionMobileStationLongitude() {
        return fusionMobileStationLongitude;
    }

    public void setFusionMobileStationLongitude(String fusionMobileStationLongitude) {
        this.fusionMobileStationLongitude = fusionMobileStationLongitude;
    }

    public String getFusionMobileStationAltitude() {
        return fusionMobileStationAltitude;
    }

    public void setFusionMobileStationAltitude(String fusionMobileStationAltitude) {
        this.fusionMobileStationAltitude = fusionMobileStationAltitude;
    }
}
