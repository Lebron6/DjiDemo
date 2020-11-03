package com.compass.ux.bean;

import dji.common.flightcontroller.GPSSignalLevel;

/**
 * Created by xhf on 2020-07-06
 */
public class FlightControllerBean {
    /**
     *true 电动机打开。
     */
    private boolean areMotorsOn;

    /**
     *true 如果飞机正在飞行。
     */
    private boolean isFlying;

    /**
     *获取飞机的当前位置作为坐标纬度
     */
    private double aircraftLatitude;

    /**
     *获取飞机的当前位置作为坐标经度
     */
    private double aircraftLongitude;

    /**
     *获取飞机的当前位置作为坐标海拔
     */
    private double aircraftAltitude;

    /**
     *飞机原点位置相对于海平面的相对高度（以米为单位）。
     */
    private float takeoffLocationAltitude;

    /**
     *pitch
     */
    private double attitudePitch;

    /**
     *roll
     */
    private double attitudeRoll;

    /**
     *Yaw
     */
    private double attitudeYaw;

    /**
     * 使用NED（北-东-下）坐标系，飞机在x方向上的当前速度，以米/秒为单位。
     */
    private float velocityX;

    /**
     * 使用NED（北-东-下）坐标系，飞机在y方向上的当前速度，以米/秒为单位。
     */
    private float velocityY;

    /**
     * 使用NED（北-东-下）坐标系，飞机在z方向上的当前速度，以米/秒为单位。
     */
    private float velocityZ;

    /**
     *自飞机发动机开启以来的累积飞行时间（以秒为单位）。
     */
    private int flightTimeInSeconds;

    /**
     *卫星数
     */
    private int satelliteCount;

    /**
     * 代表GPS信号水平的枚举类，用于测量信号质量。
     *      LEVEL_0(0),
     *     LEVEL_1(1),
     *     LEVEL_2(2),
     *     LEVEL_3(3),
     *     LEVEL_4(4),
     *     LEVEL_5(5),
     *     LEVEL_6(6),
     *     LEVEL_7(7),
     *     LEVEL_8(8),
     *     LEVEL_9(9),
     *     LEVEL_10(10),
     *     NONE(255);
     */
    private int GPSSignalLevel;

    /**
     *true IMU正在预热。
     */
    private boolean isIMUPreheating;

    /**
     *true正在使用超声波传感器。可能影响超声测量质量或是否正在使用的变量是地面以上的高度和地面的类型（如果它能很好地反射声波）
     * 。通常，当飞机离地面不到8m时，超声波传感器就会工作。
     */
    private boolean isUltrasonicBeingUsed;

    /**
     *超声传感器测量的飞机高度，以米为单位。该数据仅在isUltrasonicBeingUsed返回时可用true。高度的精度为0.1m。当高度低于5米时，此值具有参考意义。
     */
    private float ultrasonicHeightInMeters;

    /**
     *true正在使用视觉传感器。可能影响视觉测量质量或是否正在使用的变量是地面高度和地面类型（如果纹理足够丰富）。通常，视觉传感器在飞机离地面不到3m时起作用。
     */
    private boolean isVisionPositioningSensorBeingUsed;

    /**
     * 告诉飞机如何解释向前，向后，向左和向右的飞行命令
     * AIRCRAFT_HEADING(255),飞机应相对于飞机前部移动
     *     COURSE_LOCK(1),飞机应相对于锁定的航向运动
     *     HOME_LOCK(2);飞机应相对于原点径向移动
     */
    private int orientationMode;

    /**
     *强风引起的警告。
     0  LEVEL_0	No wind warning.
     1  LEVEL_1	The wind speed is high. Fly with caution and ensure the aircraft remains within the line of sight.
     2  LEVEL_2	Strong Wind. Fly with caution and ensure the aircraft remains within line of sight. It is more serious than LEVEL_1.
     255  UNKNOWN	Unknown.
     */
    private int flightWindWarning;

    /**
     *根据剩余电池寿命建议采取的措施。
     * FLY_NORMALLY	Remaining battery life sufficient for normal flying.
     * GO_HOME	Remaining battery life sufficient to go home.
     * LAND_IMMEDIATELY	Remaining battery life sufficient to land immediately.
     * UNKNOWN	Unknown.
     */
    private int batteryThresholdBehavior;

    /**
     *true 电池低于电池电量不足警告阈值。
     */
    private boolean isLowerThanBatteryWarningThreshold;

    /**
     *true 电池低于严重的电池不足警告阈值。
     */
    private boolean isLowerThanSeriousBatteryWarningThreshold;

    /**
     *电池寿命周期内的飞行计数。上电时清除。
     */
    private int flightCount;

    /**
     * 返回飞机的原点坐标纬度
     */
    private double homeLocationLatitude;

    /**
     * 返回飞机的原点坐标经度
     */
    private double homeLocationLongitude;

    /**
     * 获取回家飞机上升高度
     */
    private int goHomeHeight;

    /**
     *
     * 飞行模式
     */
    private String FlightModeString;



    /**
     * 风向
     * WINDLESS(0),
     * NORTH(1),
     * NORTH_EAST(2),
     * EAST(3),
     * SOUTH_EAST(4),
     * SOUTH(5),
     * SOUTH_WEST(6),
     * WEST(7),
     * NORTH_WEST(8);
     */
    private int WindDirection;
    /**
     * 风速
     */
    private int WindSpeed;

    /**
     * 离家距离
     * @return
     */
    private double goHomeLength;



    public double getGoHomeLength() {
        return goHomeLength;
    }

    public void setGoHomeLength(double goHomeLength) {
        this.goHomeLength = goHomeLength;
    }

    public int getWindDirection() {
        return WindDirection;
    }

    public void setWindDirection(int windDirection) {
        WindDirection = windDirection;
    }

    public int getWindSpeed() {
        return WindSpeed;
    }

    public void setWindSpeed(int windSpeed) {
        WindSpeed = windSpeed;
    }






    public String getFlightModeString() {
        return FlightModeString;
    }

    public void setFlightModeString(String flightModeString) {
        FlightModeString = flightModeString;
    }


    public boolean isAreMotorsOn() {
        return areMotorsOn;
    }

    public void setAreMotorsOn(boolean areMotorsOn) {
        this.areMotorsOn = areMotorsOn;
    }

    public boolean isFlying() {
        return isFlying;
    }

    public void setFlying(boolean flying) {
        isFlying = flying;
    }

    public double getAircraftLatitude() {
        return aircraftLatitude;
    }

    public void setAircraftLatitude(double aircraftLatitude) {
        this.aircraftLatitude = aircraftLatitude;
    }

    public double getAircraftLongitude() {
        return aircraftLongitude;
    }

    public void setAircraftLongitude(double aircraftLongitude) {
        this.aircraftLongitude = aircraftLongitude;
    }

    public double getAircraftAltitude() {
        return aircraftAltitude;
    }

    public void setAircraftAltitude(double aircraftAltitude) {
        this.aircraftAltitude = aircraftAltitude;
    }

    public float getTakeoffLocationAltitude() {
        return takeoffLocationAltitude;
    }

    public void setTakeoffLocationAltitude(float takeoffLocationAltitude) {
        this.takeoffLocationAltitude = takeoffLocationAltitude;
    }

    public double getAttitudePitch() {
        return attitudePitch;
    }

    public void setAttitudePitch(double attitudePitch) {
        this.attitudePitch = attitudePitch;
    }

    public double getAttitudeRoll() {
        return attitudeRoll;
    }

    public void setAttitudeRoll(double attitudeRoll) {
        this.attitudeRoll = attitudeRoll;
    }

    public double getAttitudeYaw() {
        return attitudeYaw;
    }

    public void setAttitudeYaw(double attitudeYaw) {
        this.attitudeYaw = attitudeYaw;
    }

    public float getVelocityX() {
        return velocityX;
    }

    public void setVelocityX(float velocityX) {
        this.velocityX = velocityX;
    }

    public float getVelocityY() {
        return velocityY;
    }

    public void setVelocityY(float velocityY) {
        this.velocityY = velocityY;
    }

    public float getVelocityZ() {
        return velocityZ;
    }

    public void setVelocityZ(float velocityZ) {
        this.velocityZ = velocityZ;
    }

    public int getFlightTimeInSeconds() {
        return flightTimeInSeconds;
    }

    public void setFlightTimeInSeconds(int flightTimeInSeconds) {
        this.flightTimeInSeconds = flightTimeInSeconds;
    }

    public int getSatelliteCount() {
        return satelliteCount;
    }

    public void setSatelliteCount(int satelliteCount) {
        this.satelliteCount = satelliteCount;
    }

    public int getGPSSignalLevel() {
        return GPSSignalLevel;
    }

    public void setGPSSignalLevel(int GPSSignalLevel) {
        this.GPSSignalLevel = GPSSignalLevel;
    }

    public boolean isIMUPreheating() {
        return isIMUPreheating;
    }

    public void setIMUPreheating(boolean IMUPreheating) {
        isIMUPreheating = IMUPreheating;
    }

    public boolean isUltrasonicBeingUsed() {
        return isUltrasonicBeingUsed;
    }

    public void setUltrasonicBeingUsed(boolean ultrasonicBeingUsed) {
        isUltrasonicBeingUsed = ultrasonicBeingUsed;
    }

    public float getUltrasonicHeightInMeters() {
        return ultrasonicHeightInMeters;
    }

    public void setUltrasonicHeightInMeters(float ultrasonicHeightInMeters) {
        this.ultrasonicHeightInMeters = ultrasonicHeightInMeters;
    }

    public boolean isVisionPositioningSensorBeingUsed() {
        return isVisionPositioningSensorBeingUsed;
    }

    public void setVisionPositioningSensorBeingUsed(boolean visionPositioningSensorBeingUsed) {
        isVisionPositioningSensorBeingUsed = visionPositioningSensorBeingUsed;
    }

    public int getOrientationMode() {
        return orientationMode;
    }

    public void setOrientationMode(int orientationMode) {
        this.orientationMode = orientationMode;
    }

    public int getFlightWindWarning() {
        return flightWindWarning;
    }

    public void setFlightWindWarning(int flightWindWarning) {
        this.flightWindWarning = flightWindWarning;
    }

    public int getBatteryThresholdBehavior() {
        return batteryThresholdBehavior;
    }

    public void setBatteryThresholdBehavior(int batteryThresholdBehavior) {
        this.batteryThresholdBehavior = batteryThresholdBehavior;
    }

    public boolean isLowerThanBatteryWarningThreshold() {
        return isLowerThanBatteryWarningThreshold;
    }

    public void setLowerThanBatteryWarningThreshold(boolean lowerThanBatteryWarningThreshold) {
        isLowerThanBatteryWarningThreshold = lowerThanBatteryWarningThreshold;
    }

    public boolean isLowerThanSeriousBatteryWarningThreshold() {
        return isLowerThanSeriousBatteryWarningThreshold;
    }

    public void setLowerThanSeriousBatteryWarningThreshold(boolean lowerThanSeriousBatteryWarningThreshold) {
        isLowerThanSeriousBatteryWarningThreshold = lowerThanSeriousBatteryWarningThreshold;
    }

    public int getFlightCount() {
        return flightCount;
    }

    public void setFlightCount(int flightCount) {
        this.flightCount = flightCount;
    }

    public double getHomeLocationLatitude() {
        return homeLocationLatitude;
    }

    public void setHomeLocationLatitude(double homeLocationLatitude) {
        this.homeLocationLatitude = homeLocationLatitude;
    }

    public double getHomeLocationLongitude() {
        return homeLocationLongitude;
    }

    public void setHomeLocationLongitude(double homeLocationLongitude) {
        this.homeLocationLongitude = homeLocationLongitude;
    }

    public int getGoHomeHeight() {
        return goHomeHeight;
    }

    public void setGoHomeHeight(int goHomeHeight) {
        this.goHomeHeight = goHomeHeight;
    }

    @Override
    public String toString() {
        return "FlightControllerBean{" +
                "areMotorsOn=" + areMotorsOn +
                ", isFlying=" + isFlying +
                ", aircraftLatitude=" + aircraftLatitude +
                ", aircraftLongitude=" + aircraftLongitude +
                ", aircraftAltitude=" + aircraftAltitude +
                ", takeoffLocationAltitude=" + takeoffLocationAltitude +
                ", attitudePitch=" + attitudePitch +
                ", attitudeRoll=" + attitudeRoll +
                ", attitudeYaw=" + attitudeYaw +
                ", velocityX=" + velocityX +
                ", velocityY=" + velocityY +
                ", velocityZ=" + velocityZ +
                ", flightTimeInSeconds=" + flightTimeInSeconds +
                ", satelliteCount=" + satelliteCount +
                ", GPSSignalLevel=" + GPSSignalLevel +
                ", isIMUPreheating=" + isIMUPreheating +
                ", isUltrasonicBeingUsed=" + isUltrasonicBeingUsed +
                ", ultrasonicHeightInMeters=" + ultrasonicHeightInMeters +
                ", isVisionPositioningSensorBeingUsed=" + isVisionPositioningSensorBeingUsed +
                ", orientationMode=" + orientationMode +
                ", flightWindWarning=" + flightWindWarning +
                ", batteryThresholdBehavior=" + batteryThresholdBehavior +
                ", isLowerThanBatteryWarningThreshold=" + isLowerThanBatteryWarningThreshold +
                ", isLowerThanSeriousBatteryWarningThreshold=" + isLowerThanSeriousBatteryWarningThreshold +
                ", flightCount=" + flightCount +
                ", homeLocationLatitude=" + homeLocationLatitude +
                ", homeLocationLongitude=" + homeLocationLongitude +
                ", goHomeHeight=" + goHomeHeight +
                '}';
    }
}
