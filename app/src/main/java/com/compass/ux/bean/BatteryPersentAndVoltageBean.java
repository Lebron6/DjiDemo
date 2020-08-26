package com.compass.ux.bean;

import java.util.List;

/**
 * Created by xhf
 * on 2020-08-05 18:34
 */
public class BatteryPersentAndVoltageBean {
    //电池1剩余百分百
    private String persentOne;
    //电池2剩余百分百
    private String persentTwo;
    //电池1电压
    private String voltageOne;
    //电池2电压
    private String voltageTwo;
    //电池1电压组
    private List<Float> battery_list_one;
    //电池2电压组
    private List<Float> battery_list_two;
    //温度1
    private String battery_temperature_one;
    //温度2
    private String battery_temperature_two;
    //循环次数1
    private String battery_discharges_one;
    //循环次数2
    private String battery_discharges_two;

    public String getBattery_discharges_one() {
        return battery_discharges_one;
    }

    public void setBattery_discharges_one(String battery_discharges_one) {
        this.battery_discharges_one = battery_discharges_one;
    }

    public String getBattery_discharges_two() {
        return battery_discharges_two;
    }

    public void setBattery_discharges_two(String battery_discharges_two) {
        this.battery_discharges_two = battery_discharges_two;
    }

    public String getBattery_temperature_one() {
        return battery_temperature_one;
    }

    public void setBattery_temperature_one(String battery_temperature_one) {
        this.battery_temperature_one = battery_temperature_one;
    }

    public String getBattery_temperature_two() {
        return battery_temperature_two;
    }

    public void setBattery_temperature_two(String battery_temperature_two) {
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

    public String getPersentOne() {
        return persentOne;
    }

    public void setPersentOne(String persentOne) {
        this.persentOne = persentOne;
    }

    public String getPersentTwo() {
        return persentTwo;
    }

    public void setPersentTwo(String persentTwo) {
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
