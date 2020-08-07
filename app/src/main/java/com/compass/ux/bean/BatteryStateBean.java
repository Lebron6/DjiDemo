package com.compass.ux.bean;

/**
 * Created by xhf on 2020-07-06
 */
public class BatteryStateBean {

    /**
     *返回电池充满电后存储在电池中的总电量（以mAh（毫安小时）为单位）。随着电池继续使用，充满电时电池的能量会随着时间而变化。随着时间的流逝，随着电池继续充电，的值getFullChargeCapacity将减小。
     */
    private int fullChargeCapacity;

    /**
     *返回以mAh（毫安小时）为单位存储在电池中的剩余电量。
     */
    private int chargeRemaining;

    /**
     *返回剩余电池电量百分比，范围为[0，100]。
     */
    private int chargeRemainingInPercent;

    /**
     *返回电池的设计容量（以mAh（毫安小时）为单位）。电池是新电池时的理想容量。该值不会随时间变化。仅智能电池支持。
     */
    private int designCapacity;

    /**
     *返回当前电池电压（mV）。
     */
    private int voltage;

    /**
     *返回电池的实时电流消耗（mA）。负值表示电池正在放电，正值表示正在充电。
     */
    private int current;

    /**
     *以百分比形式返回电池的剩余寿命，范围为[0，100]。新电池将接近100％。当电池经历充电/放电循环时，该值将降低。在不受支持的产品中，此值将始终为0。这些产品为Phantom 4 Pro，Inspire 2和Phantom 4 Advanced。
     */
    private int lifetimeRemaining;

    /**
     *返回摄氏温度，范围为[-128，127]度，以摄氏度为单位。
     */
    private float temperature;

    /**
     *返回电池在其使用寿命中经历的总放电次数。放电总数包括正常使用中的放电和手动设置的放电。
     */
    private int numberOfDischarges;



    //文档有 sdk无
//    /**
//     * 返回电池组中电池的概述。如果未连接电池，则isConnected属性为，false且getChargeRemainingInPercent为零。对于Matrice 600，此数组中有6个元素。
//     */
//    private BatteryOverviewBean batteryOverviews;
//
//    /**
//     *true如果组中的电池之一已断开连接。时true，飞机不允许起飞。
//     */
//    private boolean isAnyBatteryDisconnected;
//
//    /**
//     *true如果两个电池的电压（高于1.5V）之间存在明显差异。时 true，飞机不允许起飞。
//     */
//    private boolean isVoltageDifferenceDetected;
//
//    /**
//     *true该组中的一块电池是否已损坏。时true，飞机不允许起飞
//     */
//    private boolean isCellDamaged;
//
//    private class BatteryOverviewBean{
//        /**
//         * 电池的索引。索引从0开始。对于Matrice 600，数字1的电池仓与索引0相关。
//         */
//        private int index;
//
//        /**
//         *true 当前已将电池连接至飞机。
//         */
//        private boolean isConnected;
//
//        /**
//         *电池的剩余电量百分比范围为[0，100]。
//         */
//        private int chargeRemainingInPercent;
//
//        public int getIndex() {
//            return index;
//        }
//
//        public void setIndex(int index) {
//            this.index = index;
//        }
//
//        public boolean isConnected() {
//            return isConnected;
//        }
//
//        public void setConnected(boolean connected) {
//            isConnected = connected;
//        }
//
//        public int getChargeRemainingInPercent() {
//            return chargeRemainingInPercent;
//        }
//
//        public void setChargeRemainingInPercent(int chargeRemainingInPercent) {
//            this.chargeRemainingInPercent = chargeRemainingInPercent;
//        }
//    }



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

    public int getDesignCapacity() {
        return designCapacity;
    }

    public void setDesignCapacity(int designCapacity) {
        this.designCapacity = designCapacity;
    }

    public int getVoltage() {
        return voltage;
    }

    public void setVoltage(int voltage) {
        this.voltage = voltage;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getLifetimeRemaining() {
        return lifetimeRemaining;
    }

    public void setLifetimeRemaining(int lifetimeRemaining) {
        this.lifetimeRemaining = lifetimeRemaining;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public int getNumberOfDischarges() {
        return numberOfDischarges;
    }

    public void setNumberOfDischarges(int numberOfDischarges) {
        this.numberOfDischarges = numberOfDischarges;
    }


    //电量
//            FPVDemoApplication.getProductInstance().getBattery().setStateCallback(new BatteryState.Callback() {
//                @Override
//                public void onUpdate(BatteryState batteryState) {
//                    Log.d("batteryState",batteryState.toString());
//                    if (fastClick.batteryClick()) {
//                        if (batteryStateBean == null) {
//                            batteryStateBean = new BatteryStateBean();
//                        }
//                        batteryStateBean.setFullChargeCapacity(batteryState.getFullChargeCapacity());
//                        batteryStateBean.setChargeRemaining(batteryState.getChargeRemaining());
//                        batteryStateBean.setChargeRemainingInPercent(batteryState.getChargeRemainingInPercent());
//                        batteryStateBean.setDesignCapacity(batteryState.getDesignCapacity());
//                        batteryStateBean.setVoltage(batteryState.getVoltage());
//                        batteryStateBean.setCurrent(batteryState.getCurrent());
//                        batteryStateBean.setLifetimeRemaining(batteryState.getLifetimeRemaining());
//                        batteryStateBean.setTemperature(batteryState.getTemperature());
//                        batteryStateBean.setNumberOfDischarges(batteryState.getNumberOfDischarges());
//
//                        if (communication_battery == null) {
//                            communication_battery = new Communication();
//                        }
//                        communication_battery.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
//                        communication_battery.setEquipmentId(MApplication.EQUIPMENT_ID);
//                        communication_battery.setMethod(RsaUtil.encrypt("battery"));
//                        communication_battery.setResult(gson.toJson(batteryStateBean, BatteryStateBean.class));
////                        Log.d("NNNNN",communication_battery.toString());
//                        NettyClient.getInstance().sendMessage(communication_battery, null);
//                    }
//                }
//            });

}
