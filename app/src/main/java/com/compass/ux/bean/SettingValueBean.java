package com.compass.ux.bean;

import java.util.List;

import dji.common.flightcontroller.HeadingSolution;
import dji.common.flightcontroller.PositioningSolution;
import dji.common.flightcontroller.RTKState;
import dji.common.flightcontroller.rtk.DataSource;
import dji.common.flightcontroller.rtk.LocationStandardDeviation;
import dji.common.model.LocationCoordinate2D;

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
    private boolean collisionAvoidance;
    //上避障
    private boolean upwardsAvoidance;

    //下避障
    private boolean landingProtection;
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

    //云台俯仰限位扩展
    private boolean pitchRangeExtension;
    //云台偏航俯仰缓启停
    private String pitch_CSF;
    private String yaw_CSF;
    //返航高度
    private String goHomeHeightInMeters = "";
    //限高
    private String maxFlightHeight = "";
    //限远
    private String maxFlightRadius = "";
    //限远开关
    private boolean maxFlightRadiusLimitationEnabled;

    public String getGoHomeHeightInMeters() {
        return goHomeHeightInMeters;
    }

    public void setGoHomeHeightInMeters(String goHomeHeightInMeters) {
        this.goHomeHeightInMeters = goHomeHeightInMeters;
    }

    public String getMaxFlightHeight() {
        return maxFlightHeight;
    }

    public void setMaxFlightHeight(String maxFlightHeight) {
        this.maxFlightHeight = maxFlightHeight;
    }

    public String getMaxFlightRadius() {
        return maxFlightRadius;
    }

    public void setMaxFlightRadius(String maxFlightRadius) {
        this.maxFlightRadius = maxFlightRadius;
    }

    public boolean isMaxFlightRadiusLimitationEnabled() {
        return maxFlightRadiusLimitationEnabled;
    }

    public void setMaxFlightRadiusLimitationEnabled(boolean maxFlightRadiusLimitationEnabled) {
        this.maxFlightRadiusLimitationEnabled = maxFlightRadiusLimitationEnabled;
    }

    public String getPitch_CSF() {
        return pitch_CSF;
    }

    public void setPitch_CSF(String pitch_CSF) {
        this.pitch_CSF = pitch_CSF;
    }

    public String getYaw_CSF() {
        return yaw_CSF;
    }

    public void setYaw_CSF(String yaw_CSF) {
        this.yaw_CSF = yaw_CSF;
    }

    public boolean isPitchRangeExtension() {
        return pitchRangeExtension;
    }

    public void setPitchRangeExtension(boolean pitchRangeExtension) {
        this.pitchRangeExtension = pitchRangeExtension;
    }

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


    public boolean isCollisionAvoidance() {
        return collisionAvoidance;
    }

    public void setCollisionAvoidance(boolean collisionAvoidance) {
        this.collisionAvoidance = collisionAvoidance;
    }

    public boolean isLandingProtection() {
        return landingProtection;
    }

    public void setLandingProtection(boolean landingProtection) {
        this.landingProtection = landingProtection;
    }

    private NetRTKBean rtkBean;
    private BatteryStateBean batteryStateBean;

    public BatteryStateBean getBatteryStateBean() {
        return batteryStateBean;
    }

    public void setBatteryStateBean(BatteryStateBean batteryStateBean) {
        this.batteryStateBean = batteryStateBean;
    }

    public NetRTKBean getRtkBean() {
        return rtkBean;
    }

    public void setRtkBean(NetRTKBean rtkBean) {
        this.rtkBean = rtkBean;
    }
    public static class BatteryStateBean{
        //是否连接
        private int isConnectOne;
        private int isConnectTwo;

        public int getIsConnectOne() {
            return isConnectOne;
        }

        public void setIsConnectOne(int isConnectOne) {
            this.isConnectOne = isConnectOne;
        }

        public int getIsConnectTwo() {
            return isConnectTwo;
        }

        public void setIsConnectTwo(int isConnectTwo) {
            this.isConnectTwo = isConnectTwo;
        }

        //电池1剩余百分百
        private int persentOne;
        //电池2剩余百分百
        private int persentTwo;
        //电池1电压
        private String voltageOne;
        //电池2电压
        private String voltageTwo;
        //电池1电压组
        private List<Float> battery_list_one;
        //电池2电压组
        private List<Float> battery_list_two;
        //电池1电量组
        private List<String> battery_list_per_one;
        //电池2电量组
        private List<String> battery_list_per_two;
        //温度1
        private float battery_temperature_one;
        //温度2
        private float battery_temperature_two;
        //循环次数1
        private int battery_discharges_one;
        //循环次数2
        private int battery_discharges_two;

        public List<String> getBattery_list_per_one() {
            return battery_list_per_one;
        }

        public void setBattery_list_per_one(List<String> battery_list_per_one) {
            this.battery_list_per_one = battery_list_per_one;
        }

        public List<String> getBattery_list_per_two() {
            return battery_list_per_two;
        }

        public void setBattery_list_per_two(List<String> battery_list_per_two) {
            this.battery_list_per_two = battery_list_per_two;
        }

        public int getBattery_discharges_one() {
            return battery_discharges_one;
        }

        public void setBattery_discharges_one(int battery_discharges_one) {
            this.battery_discharges_one = battery_discharges_one;
        }

        public int getBattery_discharges_two() {
            return battery_discharges_two;
        }

        public void setBattery_discharges_two(int battery_discharges_two) {
            this.battery_discharges_two = battery_discharges_two;
        }

        public float getBattery_temperature_one() {
            return battery_temperature_one;
        }

        public void setBattery_temperature_one(float battery_temperature_one) {
            this.battery_temperature_one = battery_temperature_one;
        }

        public float getBattery_temperature_two() {
            return battery_temperature_two;
        }

        public void setBattery_temperature_two(float battery_temperature_two) {
            this.battery_temperature_two = battery_temperature_two;
        }

        public List<Float> getBattery_list_one() {
            return battery_list_one;
        }

        public void setBattery_list_one(List<Float> battery_list_one) {
            this.battery_list_one = battery_list_one;
        }

        public List<Float> getBattery_list_two() {
            return battery_list_two;
        }

        public void setBattery_list_two(List<Float> battery_list_two) {
            this.battery_list_two = battery_list_two;
        }

        public int getPersentOne() {
            return persentOne;
        }

        public void setPersentOne(int persentOne) {
            this.persentOne = persentOne;
        }

        public int getPersentTwo() {
            return persentTwo;
        }

        public void setPersentTwo(int persentTwo) {
            this.persentTwo = persentTwo;
        }

        public String getVoltageOne() {
            return voltageOne;
        }

        public void setVoltageOne(String voltageOne) {
            this.voltageOne = voltageOne;
        }

        public String getVoltageTwo() {
            return voltageTwo;
        }

        public void setVoltageTwo(String voltageTwo) {
            this.voltageTwo = voltageTwo;
        }

    }

    public static class NetRTKBean{

        private int rtkSwitch;//SETRTK状态
        private int serviceType;//RTK类型
        private Info info;

        public Info getInfo() {
            return info;
        }

        public void setInfo(Info info) {
            this.info = info;
        }

        public int getRtkSwitch() {
            return rtkSwitch;
        }

        public void setRtkSwitch(int rtkSwitch) {
            this.rtkSwitch = rtkSwitch;
        }

        public int getServiceType() {
            return serviceType;
        }

        public void setServiceType(int serviceType) {
            this.serviceType = serviceType;
        }

        public static class Info{

            private boolean isRTKBeingUsed;
            private String username;
            private String password;
            private String ip;
            private String mountPoint;
            private int port;
            private String baseStationLatitude;
            private String baseStationLongitude;
            private PositioningSolution positioningSolution;
           private RTKState rtkState;

            public RTKState getRtkState() {
                return rtkState;
            }

            public void setRtkState(RTKState rtkState) {
                this.rtkState = rtkState;
            }

            public PositioningSolution getPositioningSolution() {
                return positioningSolution;
            }

            public void setPositioningSolution(PositioningSolution positioningSolution) {
                this.positioningSolution = positioningSolution;
            }

            public boolean isRTKBeingUsed() {
                return isRTKBeingUsed;
            }

            public void setRTKBeingUsed(boolean RTKBeingUsed) {
                isRTKBeingUsed = RTKBeingUsed;
            }

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public String getPassword() {
                return password;
            }

            public void setPassword(String password) {
                this.password = password;
            }

            public String getIp() {
                return ip;
            }

            public void setIp(String ip) {
                this.ip = ip;
            }

            public String getMountPoint() {
                return mountPoint;
            }

            public void setMountPoint(String mountPoint) {
                this.mountPoint = mountPoint;
            }

            public int getPort() {
                return port;
            }

            public void setPort(int port) {
                this.port = port;
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
        }

    }

}
