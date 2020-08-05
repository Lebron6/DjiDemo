package com.compass.ux.bean;

/**
 * Created by xhf
 * on 2020-08-04 14:57
 */
public class LaserMeasureInformBean {
    private String latitude;
    private String longitude;
    //海拔
    private String altitude;
    //目标距离
    private String targetDistance;

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public String getTargetDistance() {
        return targetDistance;
    }

    public void setTargetDistance(String targetDistance) {
        this.targetDistance = targetDistance;
    }




}
