package com.compass.ux.bean;

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
