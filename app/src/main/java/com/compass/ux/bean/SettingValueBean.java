package com.compass.ux.bean;

/**
 * Created by xhf
 * on 2020-08-26 11:29
 */
public class SettingValueBean {
    //低电量
    private String lowBatteryWarning;
    //严重低电量
    private String seriousLowBatteryWarning;
    //智能返航
    private boolean smartReturnToHomeEnabled;
    //视觉定位
    private boolean visionAssistedPosition;
    //精准着陆
    private boolean precisionLand;
    //障碍物检测
    private boolean upwardsAvoidance;
//    上避障安全距离范围为[1.0，10]
    private String avoidanceDistanceUpward;
//    下避障安全距离范围为[0.1，3]
    private String avoidanceDistanceDownward;
    //水平避障安全距离范围为[1.0，5]
    private String avoidanceDistanceHorizontal;
    //上避障感知距离5m〜45m
    private String maxPerceptionDistanceUpward;
    //下避障感知距离5m〜45m
    private String maxPerceptionDistanceDownward;
    //水平障感知距离5m〜45m
    private String maxPerceptionDistanceHorizontal;
    //避障刹车功能
    private boolean activeObstacleAvoidance;
    //云台俯仰速度1——100
    private String gimbal_pitch_speed;
    //云台偏航速度1——100
    private String gimbal_yaw_speed;
    //带宽
    private String channelBandwidth;
    //工作频段 2.4g 5.8g 双频
    private String frequencyBand;
    //图传码率
    private String transcodingDataRate;
    //干扰功率范围为[-60，-100] dBm。较小的负值表示干扰较小，通信质量较好。所有中文自己根据值写一下
    private String interferencePower;


    public String getChannelBandwidth() {
        return channelBandwidth;
    }

    public void setChannelBandwidth(String channelBandwidth) {
        this.channelBandwidth = channelBandwidth;
    }

    public String getFrequencyBand() {
        return frequencyBand;
    }

    public void setFrequencyBand(String frequencyBand) {
        this.frequencyBand = frequencyBand;
    }

    public String getTranscodingDataRate() {
        return transcodingDataRate;
    }

    public void setTranscodingDataRate(String transcodingDataRate) {
        this.transcodingDataRate = transcodingDataRate;
    }

    public String getInterferencePower() {
        return interferencePower;
    }

    public void setInterferencePower(String interferencePower) {
        this.interferencePower = interferencePower;
    }

    public String getGimbal_pitch_speed() {
        return gimbal_pitch_speed;
    }

    public void setGimbal_pitch_speed(String gimbal_pitch_speed) {
        this.gimbal_pitch_speed = gimbal_pitch_speed;
    }

    public String getGimbal_yaw_speed() {
        return gimbal_yaw_speed;
    }

    public void setGimbal_yaw_speed(String gimbal_yaw_speed) {
        this.gimbal_yaw_speed = gimbal_yaw_speed;
    }

    public String getAvoidanceDistanceHorizontal() {
        return avoidanceDistanceHorizontal;
    }

    public void setAvoidanceDistanceHorizontal(String avoidanceDistanceHorizontal) {
        this.avoidanceDistanceHorizontal = avoidanceDistanceHorizontal;
    }

    public String getMaxPerceptionDistanceHorizontal() {
        return maxPerceptionDistanceHorizontal;
    }

    public void setMaxPerceptionDistanceHorizontal(String maxPerceptionDistanceHorizontal) {
        this.maxPerceptionDistanceHorizontal = maxPerceptionDistanceHorizontal;
    }

    public boolean isActiveObstacleAvoidance() {
        return activeObstacleAvoidance;
    }

    public void setActiveObstacleAvoidance(boolean activeObstacleAvoidance) {
        this.activeObstacleAvoidance = activeObstacleAvoidance;
    }

    public String getLowBatteryWarning() {
        return lowBatteryWarning;
    }

    public void setLowBatteryWarning(String lowBatteryWarning) {
        this.lowBatteryWarning = lowBatteryWarning;
    }

    public String getSeriousLowBatteryWarning() {
        return seriousLowBatteryWarning;
    }

    public void setSeriousLowBatteryWarning(String seriousLowBatteryWarning) {
        this.seriousLowBatteryWarning = seriousLowBatteryWarning;
    }

    public boolean isSmartReturnToHomeEnabled() {
        return smartReturnToHomeEnabled;
    }

    public void setSmartReturnToHomeEnabled(boolean smartReturnToHomeEnabled) {
        this.smartReturnToHomeEnabled = smartReturnToHomeEnabled;
    }

    public boolean isVisionAssistedPosition() {
        return visionAssistedPosition;
    }

    public void setVisionAssistedPosition(boolean visionAssistedPosition) {
        this.visionAssistedPosition = visionAssistedPosition;
    }

    public boolean isPrecisionLand() {
        return precisionLand;
    }

    public void setPrecisionLand(boolean precisionLand) {
        this.precisionLand = precisionLand;
    }

    public boolean isUpwardsAvoidance() {
        return upwardsAvoidance;
    }

    public void setUpwardsAvoidance(boolean upwardsAvoidance) {
        this.upwardsAvoidance = upwardsAvoidance;
    }

    public String getAvoidanceDistanceUpward() {
        return avoidanceDistanceUpward;
    }

    public void setAvoidanceDistanceUpward(String avoidanceDistanceUpward) {
        this.avoidanceDistanceUpward = avoidanceDistanceUpward;
    }

    public String getAvoidanceDistanceDownward() {
        return avoidanceDistanceDownward;
    }

    public void setAvoidanceDistanceDownward(String avoidanceDistanceDownward) {
        this.avoidanceDistanceDownward = avoidanceDistanceDownward;
    }

    public String getMaxPerceptionDistanceUpward() {
        return maxPerceptionDistanceUpward;
    }

    public void setMaxPerceptionDistanceUpward(String maxPerceptionDistanceUpward) {
        this.maxPerceptionDistanceUpward = maxPerceptionDistanceUpward;
    }

    public String getMaxPerceptionDistanceDownward() {
        return maxPerceptionDistanceDownward;
    }

    public void setMaxPerceptionDistanceDownward(String maxPerceptionDistanceDownward) {
        this.maxPerceptionDistanceDownward = maxPerceptionDistanceDownward;
    }
}
