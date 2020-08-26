package com.compass.ux.bean;

/**
 * Created by xhf
 * on 2020-08-26 10:20
 */
public class IMUStateBean {
    private String IMU_index;
    //加速度计
    private String IMU_ARS;
    private String IMU_ARV;
    //陀螺仪
    private String IMU_GYS;
    private String IMU_GYV;

    public String getIMU_index() {
        return IMU_index;
    }

    public void setIMU_index(String IMU_index) {
        this.IMU_index = IMU_index;
    }

    public String getIMU_ARS() {
        return IMU_ARS;
    }

    public void setIMU_ARS(String IMU_ARS) {
        this.IMU_ARS = IMU_ARS;
    }

    public String getIMU_ARV() {
        return IMU_ARV;
    }

    public void setIMU_ARV(String IMU_ARV) {
        this.IMU_ARV = IMU_ARV;
    }

    public String getIMU_GYS() {
        return IMU_GYS;
    }

    public void setIMU_GYS(String IMU_GYS) {
        this.IMU_GYS = IMU_GYS;
    }

    public String getIMU_GYV() {
        return IMU_GYV;
    }

    public void setIMU_GYV(String IMU_GYV) {
        this.IMU_GYV = IMU_GYV;
    }
}
