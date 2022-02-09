package com.compass.ux.bean;

public class MissionState {

    private static class MissionStateHolder {
        private static final MissionState INSTANCE = new MissionState();
    }

    private MissionState() {
    }

    public static final MissionState getInstance() {
        return MissionStateHolder.INSTANCE;
    }

    private String calculateTotalTime;
    private String lastCalculatedTotalTime;
    private String calculateTotalDistance;
    private int missionState;
    private String offIndex;
    private String missionName;
    private int waypointSize;
    //用于标识 go-home 命令的不同阶段的枚举。部分飞行器根据固件仅支持部分状态。
    //例如：
    //Phantom 4 Pro V2.0 和 Mavic 2 Enterprise Dual 仅支持GO_DOWN_TO_GROUND。
    //Matrice 300 RTK 仅支持NOT_EXECUTING,GO_UP_TO_HEIGHT,AUTO_FLY_TO_HOME_POINT和GO_DOWN_TO_GROUND.
    private String goHomeState;

    public String getGoHomeState() {
        return goHomeState;
    }

    public void setGoHomeState(String goHomeState) {
        this.goHomeState = goHomeState;
    }

    public int getWaypointSize() {
        return waypointSize;
    }

    public void setWaypointSize(int waypointSize) {
        this.waypointSize = waypointSize;
    }

    public String getMissionName() {
        return missionName;
    }

    public void setMissionName(String missionName) {
        this.missionName = missionName;
    }

    public String getCalculateTotalTime() {
        return calculateTotalTime;
    }

    public void setCalculateTotalTime(String calculateTotalTime) {
        this.calculateTotalTime = calculateTotalTime;
    }

    public String getLastCalculatedTotalTime() {
        return lastCalculatedTotalTime;
    }

    public void setLastCalculatedTotalTime(String lastCalculatedTotalTime) {
        this.lastCalculatedTotalTime = lastCalculatedTotalTime;
    }

    public String getCalculateTotalDistance() {
        return calculateTotalDistance;
    }

    public void setCalculateTotalDistance(String calculateTotalDistance) {
        this.calculateTotalDistance = calculateTotalDistance;
    }

    public int getMissionState() {
        return missionState;
    }

    public void setMissionState(int missionState) {
        this.missionState = missionState;
    }

    public String getOffIndex() {
        return offIndex;
    }

    public void setOffIndex(String offIndex) {
        this.offIndex = offIndex;
    }
}
