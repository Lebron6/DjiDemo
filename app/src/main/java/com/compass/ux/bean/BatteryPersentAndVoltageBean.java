package com.compass.ux.bean;

import java.util.List;

/**
 * Created by xhf
 * on 2020-08-05 18:34
 */
public class BatteryPersentAndVoltageBean {
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
