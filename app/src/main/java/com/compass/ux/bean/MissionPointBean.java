package com.compass.ux.bean;

public class MissionPointBean {
    private WayPointsV2Bean.WayPointsBean.WayPointActionBean.VoiceBean voiceBean;
    private int pointIndex;
    private String shoutingType;

    public String getShoutingType() {
        return shoutingType;
    }

    public void setShoutingType(String shoutingType) {
        this.shoutingType = shoutingType;
    }

    public WayPointsV2Bean.WayPointsBean.WayPointActionBean.VoiceBean getVoiceBean() {
        return voiceBean;
    }

    public void setVoiceBean(WayPointsV2Bean.WayPointsBean.WayPointActionBean.VoiceBean voiceBean) {
        this.voiceBean = voiceBean;
    }

    public int getPointIndex() {
        return pointIndex;
    }

    public void setPointIndex(int pointIndex) {
        this.pointIndex = pointIndex;
    }

    @Override
    public String toString() {
        return "MissionPointBean{" +
                "voiceBean=" + voiceBean +
                ", pointIndex=" + pointIndex +
                ", shoutingType='" + shoutingType + '\'' +
                '}';
    }
}
