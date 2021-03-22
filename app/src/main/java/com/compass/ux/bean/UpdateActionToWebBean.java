package com.compass.ux.bean;

import java.util.List;

public class UpdateActionToWebBean {
    private int pointIndex;
    private List<String> actionIndex;
    private String actionType;
    private boolean isOld;

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

    public String getActionType() {
        return actionType;
    }

    public List<String> getActionIndex() {
        return actionIndex;
    }

    public void setActionIndex(List<String> actionIndex) {
        this.actionIndex = actionIndex;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    @Override
    public String toString() {
        return "UpdateActionToWebBean{" +
                "pointIndex=" + pointIndex +
                ", actionIndex=" + actionIndex +
                ", actionType='" + actionType + '\'' +
                '}';
    }
}
