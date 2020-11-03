package com.compass.ux.bean;

import java.util.List;

/**
 * Created by xhf on 2020-06-23
 */
public class WayPointsBean {

    /**
     * latitude : 1
     * longitude : 1
     * altitude : 1
     * speed : 1
     * wayPointAction : [{"actionType":0,"actionParam":0}]
     */

    private String latitude;
    private String longitude;
    private String altitude;
    private String speed;
    private String gimbalPitch;
    private String cornerRadiusInMeters;
    private String turnMode;


    public String getTurnMode() {
        return turnMode;
    }

    public void setTurnMode(String turnMode) {
        this.turnMode = turnMode;
    }





    private List<WayPointActionBean> wayPointAction;

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

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }


    public String getGimbalPitch() {
        return gimbalPitch;
    }

    public void setGimbalPitch(String gimbalPitch) {
        this.gimbalPitch = gimbalPitch;
    }


    public List<WayPointActionBean> getWayPointAction() {
        return wayPointAction;
    }

    public void setWayPointAction(List<WayPointActionBean> wayPointAction) {
        this.wayPointAction = wayPointAction;
    }


    public String getCornerRadiusInMeters() {
        return cornerRadiusInMeters;
    }

    public void setCornerRadiusInMeters(String cornerRadiusInMeters) {
        this.cornerRadiusInMeters = cornerRadiusInMeters;
    }

    public static class WayPointActionBean {
        /**
         * actionType : 0
         * actionParam : 0
         */

        private int actionType;
        private int actionParam;

        public int getActionType() {
            return actionType;
        }

        public void setActionType(int actionType) {
            this.actionType = actionType;
        }

        public int getActionParam() {
            return actionParam;
        }

        public void setActionParam(int actionParam) {
            this.actionParam = actionParam;
        }
    }
}
