package com.compass.ux.bean;

import java.util.List;

public class UpdateActionToWebBean {
    private int pointIndex;
    private List<String> actionType;
    private List<String> actionIndex;
    private boolean isOld;
    private WayPointsV2Bean.WayPointsBean.VoiceBean voice;

    public WayPointsV2Bean.WayPointsBean.VoiceBean getVoice() {
        return voice;
    }

    public void setVoice(WayPointsV2Bean.WayPointsBean.VoiceBean voice) {
        this.voice = voice;
    }

    public boolean isOld() {
        return isOld;
    }

    public void setOld(boolean old) {
        isOld = old;
    }

    public String getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(String waitTime) {
        this.waitTime = waitTime;
    }

    private String waitTime;

    public int getPointIndex() {
        return pointIndex;
    }

    public void setPointIndex(int pointIndex) {
        this.pointIndex = pointIndex;
    }

    public List<String> getActionType() {
        return actionType;
    }

    public void setActionType(List<String> actionType) {
        this.actionType = actionType;
    }

    public List<String> getActionIndex() {
        return actionIndex;
    }

    public void setActionIndex(List<String> actionIndex) {
        this.actionIndex = actionIndex;
    }

    @Override
    public String toString() {
        return "UpdateActionToWebBean{" +
                "pointIndex=" + pointIndex +
                ", actionType=" + actionType +
                ", actionIndex=" + actionIndex +
                ", isOld=" + isOld +
                ", waitTime='" + waitTime + '\'' +
                '}';
    }
}
