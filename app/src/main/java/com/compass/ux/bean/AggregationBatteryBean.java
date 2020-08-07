package com.compass.ux.bean;

import java.util.Arrays;

/**
 * Created by xhf on 2020-07-06
 */
public class AggregationBatteryBean {

    /**
     *当前连接的电池数量。
     */
    private int numberOfConnectedBatteries;

    /**
     *返回流过电池的实时电流。负值表示电池正在放电。
     */
    private int current;

    /**
     * 返回电池组提供的当前电压（mV）。
     */
    private int voltage;

    /**
     *true如果该组中的电池之一具有低电压电池。时true，飞机不允许起飞。
     */
    private boolean isLowCellVoltageDetected;

    /**
     *返回电池充满电后存储在电池中的总电量（以mAh（毫安小时）为单位）。
     */
    private int fullChargeCapacity;

    /**
     * 返回以mAh（毫安小时）为单位存储在电池中的剩余能量。
     */
    private int chargeRemaining;

    /**
     *返回范围为[0,100]的电池组中剩余的电量百分比。
     */
    private int chargeRemainingInPercent;

    /**
     *返回组中所有电池中的最高温度（摄氏度），范围为[-128，127]度。
     */
    private int highestTemperature;

    /**
     *true如果组中的一块电池的固件版本与其他电池不同。如果是true，则不允许飞机起飞。
     */
    private boolean isFirmwareDifferenceDetected;


    /**
     * 返回电池组中电池的概述。如果未连接电池，则isConnected属性为，false且getChargeRemainingInPercent为零。对于Matrice 600，此数组中有6个元素。
     */
    private BatteryOverviewBean[] batteryOverviews;

    /**
     *true如果组中的电池之一已断开连接。时true，飞机不允许起飞。
     */
    private boolean isAnyBatteryDisconnected;

    /**
     *true如果两个电池的电压（高于1.5V）之间存在明显差异。时 true，飞机不允许起飞。
     */
    private boolean isVoltageDifferenceDetected;

    /**
     *true该组中的一块电池是否已损坏。时true，飞机不允许起飞
     */
    private boolean isCellDamaged;

    public static class BatteryOverviewBean{
        /**
         * 电池的索引。索引从0开始。对于Matrice 600，数字1的电池仓与索引0相关。
         */
        private int index;

        /**
         *true 当前已将电池连接至飞机。
         */
        private boolean isConnected;

        /**
         *电池的剩余电量百分比范围为[0，100]。
         */
        private int chargeRemainingInPercent;

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public boolean isConnected() {
            return isConnected;
        }

        public void setConnected(boolean connected) {
            isConnected = connected;
        }

        public int getChargeRemainingInPercent() {
            return chargeRemainingInPercent;
        }

        public void setChargeRemainingInPercent(int chargeRemainingInPercent) {
            this.chargeRemainingInPercent = chargeRemainingInPercent;
        }
    }

    public int getNumberOfConnectedBatteries() {
        return numberOfConnectedBatteries;
    }

    public void setNumberOfConnectedBatteries(int numberOfConnectedBatteries) {
        this.numberOfConnectedBatteries = numberOfConnectedBatteries;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getVoltage() {
        return voltage;
    }

    public void setVoltage(int voltage) {
        this.voltage = voltage;
    }

    public boolean isLowCellVoltageDetected() {
        return isLowCellVoltageDetected;
    }

    public void setLowCellVoltageDetected(boolean lowCellVoltageDetected) {
        isLowCellVoltageDetected = lowCellVoltageDetected;
    }

    public int getFullChargeCapacity() {
        return fullChargeCapacity;
    }

    public void setFullChargeCapacity(int fullChargeCapacity) {
        this.fullChargeCapacity = fullChargeCapacity;
    }

    public int getChargeRemaining() {
        return chargeRemaining;
    }

    public void setChargeRemaining(int chargeRemaining) {
        this.chargeRemaining = chargeRemaining;
    }

    public int getChargeRemainingInPercent() {
        return chargeRemainingInPercent;
    }

    public void setChargeRemainingInPercent(int chargeRemainingInPercent) {
        this.chargeRemainingInPercent = chargeRemainingInPercent;
    }

    public int getHighestTemperature() {
        return highestTemperature;
    }

    public void setHighestTemperature(int highestTemperature) {
        this.highestTemperature = highestTemperature;
    }

    public boolean isFirmwareDifferenceDetected() {
        return isFirmwareDifferenceDetected;
    }

    public void setFirmwareDifferenceDetected(boolean firmwareDifferenceDetected) {
        isFirmwareDifferenceDetected = firmwareDifferenceDetected;
    }


    public boolean isAnyBatteryDisconnected() {
        return isAnyBatteryDisconnected;
    }

    public void setAnyBatteryDisconnected(boolean anyBatteryDisconnected) {
        isAnyBatteryDisconnected = anyBatteryDisconnected;
    }

    public boolean isVoltageDifferenceDetected() {
        return isVoltageDifferenceDetected;
    }

    public void setVoltageDifferenceDetected(boolean voltageDifferenceDetected) {
        isVoltageDifferenceDetected = voltageDifferenceDetected;
    }

    public boolean isCellDamaged() {
        return isCellDamaged;
    }

    public void setCellDamaged(boolean cellDamaged) {
        isCellDamaged = cellDamaged;
    }

    public BatteryOverviewBean[] getBatteryOverviews() {
        return batteryOverviews;
    }

    public void setBatteryOverviews(BatteryOverviewBean[] batteryOverviews) {
        this.batteryOverviews = batteryOverviews;
    }

    @Override
    public String toString() {
        return "AggregationBatteryBean{" +
                "numberOfConnectedBatteries=" + numberOfConnectedBatteries +
                ", current=" + current +
                ", voltage=" + voltage +
                ", isLowCellVoltageDetected=" + isLowCellVoltageDetected +
                ", fullChargeCapacity=" + fullChargeCapacity +
                ", chargeRemaining=" + chargeRemaining +
                ", chargeRemainingInPercent=" + chargeRemainingInPercent +
                ", highestTemperature=" + highestTemperature +
                ", isFirmwareDifferenceDetected=" + isFirmwareDifferenceDetected +
                ", batteryOverviews=" + Arrays.toString(batteryOverviews) +
                ", isAnyBatteryDisconnected=" + isAnyBatteryDisconnected +
                ", isVoltageDifferenceDetected=" + isVoltageDifferenceDetected +
                ", isCellDamaged=" + isCellDamaged +
                '}';
    }



    //聚合电量
//            FPVDemoApplication.getProductInstance().getBattery().setAggregationStateCallback(new AggregationState.Callback() {
//                @Override
//                public void onUpdate(AggregationState aggregationState) {
//                    Log.d("aggregationState", aggregationState.toString());
//                    if (aggregationBatteryBean == null) {
//                        aggregationBatteryBean = new AggregationBatteryBean();
//                    }
//                    aggregationBatteryBean.setNumberOfConnectedBatteries(aggregationState.getNumberOfConnectedBatteries());
//                    aggregationBatteryBean.setCurrent(aggregationState.getCurrent());
//                    aggregationBatteryBean.setVoltage(aggregationState.getVoltage());
//                    aggregationBatteryBean.setLowCellVoltageDetected(aggregationState.isLowCellVoltageDetected());
//                    aggregationBatteryBean.setFullChargeCapacity(aggregationState.getFullChargeCapacity());
//                    aggregationBatteryBean.setChargeRemaining(aggregationState.getChargeRemaining());
//                    aggregationBatteryBean.setChargeRemainingInPercent(aggregationState.getChargeRemainingInPercent());
//                    aggregationBatteryBean.setHighestTemperature(aggregationState.getHighestTemperature());
//                    aggregationBatteryBean.setFirmwareDifferenceDetected(aggregationState.isFirmwareDifferenceDetected());
//                    AggregationBatteryBean.BatteryOverviewBean[] batteryOverviewBean = new AggregationBatteryBean.BatteryOverviewBean[aggregationState.getBatteryOverviews().length];
//                    for (int i = 0; i < aggregationState.getBatteryOverviews().length; i++) {
//                        batteryOverviewBean[i].setChargeRemainingInPercent(aggregationState.getBatteryOverviews()[i].getChargeRemainingInPercent());
//                        batteryOverviewBean[i].setIndex(aggregationState.getBatteryOverviews()[i].getIndex());
//                    }
//                    aggregationBatteryBean.setBatteryOverviews(batteryOverviewBean);
//                    aggregationBatteryBean.setAnyBatteryDisconnected(aggregationState.isAnyBatteryDisconnected());
//                    aggregationBatteryBean.setVoltageDifferenceDetected(aggregationState.isVoltageDifferenceDetected());
//                    aggregationBatteryBean.setCellDamaged(aggregationState.isCellDamaged());
//                    Log.d("NNNNN", aggregationBatteryBean.toString());
//                    if (communication_aggregation_battery == null) {
//                        communication_aggregation_battery = new Communication();
//                    }
//                    communication_aggregation_battery.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
//                    communication_aggregation_battery.setEquipmentId(MApplication.EQUIPMENT_ID);
//                    communication_aggregation_battery.setMethod(RsaUtil.encrypt("aggregationBattery"));
//                    communication_aggregation_battery.setResult(gson.toJson(aggregationBatteryBean, AggregationBatteryBean.class));
//                    NettyClient.getInstance().sendMessage(communication_aggregation_battery, null);
//                }
//            });
}
