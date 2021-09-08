//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.compass.ux.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.compass.ux.R;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntRange;
import androidx.annotation.Keep;
import androidx.annotation.MainThread;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import dji.common.battery.AggregationState;
import dji.common.battery.ConnectionState;
import dji.common.battery.WarningRecord;
import dji.common.flightcontroller.BatteryThresholdBehavior;
import dji.keysdk.BatteryKey;
import dji.keysdk.DJIKey;
import dji.keysdk.FlightControllerKey;
import dji.keysdk.KeyManager;
import dji.keysdk.ProductKey;
import dji.log.DJILog;
import dji.ux.R.color;
import dji.ux.R.drawable;
import dji.ux.R.id;
import dji.ux.R.string;
import dji.ux.R.styleable;
import dji.ux.base.DynamicFrameLayoutWidget;
import dji.ux.model.base.BaseDynamicWidgetAppearances;

public class BatteryWidget extends dji.ux.widget.BatteryWidget {
    private static final String TAG = "BatteryWidget";
    private static final int EXCLUDE_ICON_VIEW = 1;
    private static final int EXCLUDE_PERCENTAGE_VIEW = 2;
    private static final int EXCLUDE_SINGLE_VOLTAGE_VIEW = 4;
    private static final int EXCLUDE_DOUBLE_VOLTAGE_VIEW = 8;
    private ImageView iconImageView;
    private TextView valueTextView;
    private int batteryPercentage;
    private ConnectionState batteryConnectionState;
    private BatteryThresholdBehavior batteryWarningLevel;
    private String batteryPerInString;
    private int goHomeBattery;
    private int currentBatteryIconId;
    private int currentTextColorId;
    private DJIKey batteryEnergyRemainPercentageKey;
    private DJIKey batteryConnectionStateKey;
    private DJIKey batteryRemainFlightKey;
    private DJIKey batteryNeedToGoHomeKey;
    private boolean isConnected;
    private BatteryKey battery2EnergyRemainPercentageKey;
    private TextView value2TextView;
    private TextView value1TextView;
    private ImageView multiIconImageView;
    private ProductKey isProductConnectKey;
    private BatteryKey batteryAggregationKey;
    private AggregationState aggregationState;
    private String battery2PerInString;
    private TextView voltage1TextView;
    private TextView voltage2TextView;
    private int voltageBgId;
    private BatteryKey batteryCellVoltageKey;
    private BatteryKey battery2CellVoltageKey;
    private String battery1VoltageString;
    private String battery2VoltageString;
    private TextView voltageTextView;

    public BatteryWidget(Context var1) {
        this(var1, (AttributeSet) null, 0);
    }

    public BatteryWidget(Context var1, AttributeSet var2) {
        this(var1, var2, 0);
    }

    public BatteryWidget(Context var1, AttributeSet var2, int var3) {
        super(var1, var2, var3);
        this.batteryConnectionState = ConnectionState.NORMAL;
        this.batteryWarningLevel = BatteryThresholdBehavior.FLY_NORMALLY;
        this.goHomeBattery = 0;
        this.currentBatteryIconId = R.mipmap.icon_battery_nor;
        this.currentTextColorId = R.color.colorTheme;
    }

    private void initViewByAttribute(int var1) {
        if ((var1 & 1) == 1) {
            this.setVisibility(this.iconImageView, 8);
            this.setVisibility(this.multiIconImageView, 8);
            this.iconImageView = null;
            this.multiIconImageView = null;
        }

        if ((var1 & 2) == 2) {
            this.setVisibility(this.valueTextView, 8);
            this.setVisibility(this.value1TextView, 8);
            this.setVisibility(this.value2TextView, 8);
            this.valueTextView = null;
            this.value1TextView = null;
            this.value2TextView = null;
        }

        if ((var1 & 8) == 8) {
            this.setVisibility(this.voltage1TextView, 8);
            this.setVisibility(this.voltage2TextView, 8);
            this.voltage1TextView = null;
            this.voltage2TextView = null;
        }

        if ((var1 & 4) == 4) {
            this.setVisibility(this.voltageTextView, 8);
            this.voltageTextView = null;
        }

    }

    private void setVisibility(View var1, int var2) {
        if (var1 != null) {
            var1.setVisibility(var2);
        }

    }

    private void setText(TextView var1, String var2) {
        if (var1 != null) {
            var1.setText(var2);
        }

    }

    private void setText(TextView var1, @StringRes int var2) {
        if (var1 != null) {
            var1.setText(var2);
        }

    }

    private void setTextColor(TextView var1, @ColorInt int var2) {
        if (var1 != null) {
            var1.setTextColor(var2);
        }

    }

    private void setImageResource(ImageView var1, @DrawableRes int var2) {
        if (var1 != null) {
            var1.setImageResource(var2);
        }

    }

    private void setBackgroundResource(View var1, @DrawableRes int var2) {
        if (var1 != null) {
            var1.setBackgroundResource(var2);
        }

    }

    @SuppressLint("WrongConstant")
    private void updateVisibleViews() {
        ImageView var1;
        TextView var2;
        if (this.isDoubleBattery()) {
            if ((var1 = this.multiIconImageView) != null && var1.getVisibility() == 8 || (var2 = this.value1TextView) != null && var2.getVisibility() == 8 || (var2 = this.voltage1TextView) != null && var2.getVisibility() == 8) {
                this.setVisibility(this.multiIconImageView, 0);
                this.setVisibility(this.value1TextView, 0);
                this.setVisibility(this.value2TextView, 0);
                this.setVisibility(this.voltage1TextView, 0);
                this.setVisibility(this.voltage2TextView, 0);
                this.setVisibility(this.iconImageView, 8);
                this.setVisibility(this.valueTextView, 8);
                this.setVisibility(this.voltageTextView, 8);
                this.getWidgetAppearances().updateDynamicElementAppearances();
            }
        } else if ((var1 = this.iconImageView) != null && var1.getVisibility() == 8 || (var2 = this.valueTextView) != null && var2.getVisibility() == 8 || (var2 = this.voltageTextView) != null && var2.getVisibility() == 8) {
            this.setVisibility(this.iconImageView, 0);
            this.setVisibility(this.valueTextView, 0);
            this.setVisibility(this.voltageTextView, 0);
            this.setVisibility(this.multiIconImageView, 8);
            this.setVisibility(this.value1TextView, 8);
            this.setVisibility(this.value2TextView, 8);
            this.setVisibility(this.voltage1TextView, 8);
            this.setVisibility(this.voltage2TextView, 8);
            this.getWidgetAppearances().updateDynamicElementAppearances();
        }

    }

    private void updateUIResources() {
        int var1 = this.getResources().getColor(this.currentTextColorId);
        this.updateVisibleViews();
        if (this.isDoubleBattery()) {
            this.setImageResource(this.multiIconImageView, this.currentBatteryIconId);
            this.setTextColor(this.value1TextView, var1);
            this.setText(this.value1TextView, this.batteryPerInString);
            this.setTextColor(this.value2TextView, var1);
            this.setText(this.value2TextView, this.battery2PerInString);
            this.setBackgroundResource(this.voltage1TextView, this.voltageBgId);
            this.setBackgroundResource(this.voltage2TextView, this.voltageBgId);
            this.setText(this.voltage1TextView, this.battery1VoltageString);
            this.setText(this.voltage2TextView, this.battery2VoltageString);
            this.setTextColor(this.voltage1TextView, var1);
            this.setTextColor(this.voltage2TextView, var1);
        } else {
            this.setImageResource(this.iconImageView, this.currentBatteryIconId);
            this.setTextColor(this.valueTextView, var1);
            this.setText(this.valueTextView, this.batteryPerInString);
            this.setBackgroundResource(this.voltageTextView, this.voltageBgId);
            this.setText(this.voltageTextView, this.battery1VoltageString);
            this.setTextColor(this.voltageTextView, var1);
        }

        if (!this.isConnected) {
            this.setText(this.valueTextView, string.string_default_value);
            this.setText(this.value1TextView, string.string_default_value);
            this.setText(this.value2TextView, string.string_default_value);
            this.setText(this.voltageTextView, string.string_default_value);
            this.setText(this.voltage1TextView, string.string_default_value);
            this.setText(this.voltage2TextView, string.string_default_value);
        }

    }

    private float getMinVoltage(Object var1) {
        if (var1 != null && var1 instanceof Integer[]) {
            Integer[] var4;
            if ((var4 = (Integer[]) var1).length <= 0) {
                return 0.0F;
            } else {
                int var5 = var4[0];
                int var2 = var4.length;

                for (int var3 = 0; var3 < var2; ++var3) {
                    var5 = Math.min(var5, var4[var3]);
                }

                return (float) var5 * 1.0F / 1000.0F;
            }
        } else {
            return 0.0F;
        }
    }

    private boolean isDoubleBattery() {
        Object var1;
        if ((var1 = KeyManager.getInstance().getValue(this.batteryAggregationKey)) != null) {
            this.aggregationState = (AggregationState) var1;
            return this.aggregationState.getNumberOfConnectedBatteries() > 1;
        } else {
            return false;
        }
    }

    private void updateCurrentResources() {
        if (this.isDoubleBattery()) {
            this.updateDoubleBatteriesResources();
        } else {
            this.updateSingleBatteryResources();
        }

    }

    private boolean checkAreTwoBatteriesOverHeating() {
        for (int var2 = 0; var2 < 2; ++var2) {
            Object var1;
            if ((var1 = KeyManager.getInstance().getValue(BatteryKey.create("LatestWarningRecord", var2))) != null && ((WarningRecord) var1).isOverHeated()) {
                return true;
            }
        }

        return false;
    }

    private void updateDoubleBatteriesResources() {
        this.currentTextColorId =R.color.colorTheme;
        boolean var1 = this.checkAreTwoBatteriesOverHeating();
        this.voltageBgId = drawable.battery_voltage_bg_normal;

        BatteryThresholdBehavior var2;
        if ((var2 = this.batteryWarningLevel) != BatteryThresholdBehavior.GO_HOME && var2 != BatteryThresholdBehavior.LAND_IMMEDIATELY && this.isConnected) {
            if (var1) {
                this.currentTextColorId = context.getResources().getColor(R.color.yellow);
                this.currentBatteryIconId = drawable.ic_topbar_double_battery_overheat_warning;
            } else {
                this.currentBatteryIconId =R.mipmap.icon_battery_nor;
            }
        } else {
            this.currentTextColorId = color.red;
            this.voltageBgId = drawable.battery_voltage_bg_error;
            this.currentBatteryIconId = drawable.ic_topbar_double_battery_error;
        }

    }

    private void updateSingleBatteryResources() {
        BatteryThresholdBehavior var1;
        Object[] var3;
        if (this.batteryPercentage > this.goHomeBattery && (var1 = this.batteryWarningLevel) != BatteryThresholdBehavior.GO_HOME && this.isConnected) {
            if (var1 == BatteryThresholdBehavior.LAND_IMMEDIATELY) {
                this.currentBatteryIconId = drawable.ic_topbar_battery_thunder;
                this.currentTextColorId = color.red;
                this.voltageBgId = drawable.battery_voltage_bg_error;
                var3 = new Object[0];
                DJILog.d("BatteryWidget", "Battery warning level is 2", var3);
            } else {
                this.currentBatteryIconId = R.mipmap.icon_battery_nor;
                this.currentTextColorId = R.color.colorTheme;
                this.voltageBgId = drawable.battery_voltage_bg_normal;
            }
        } else {
            this.currentBatteryIconId = drawable.ic_topbar_battery_dangerous;
            this.currentTextColorId = color.red;
            this.voltageBgId = drawable.battery_voltage_bg_error;
            String var10000 = "Battery percentage less than goHomeBattery or warning level is 1" + this.batteryPercentage + " " + this.goHomeBattery + " " + this.batteryWarningLevel;
            var3 = new Object[0];
            DJILog.d("BatteryWidget", var10000, var3);
        }

        if (this.batteryConnectionState != ConnectionState.NORMAL) {
            this.currentBatteryIconId = drawable.ic_topbar_battery_error;
            if (this.batteryPercentage > this.goHomeBattery && (var1 = this.batteryWarningLevel) != BatteryThresholdBehavior.GO_HOME && var1 != BatteryThresholdBehavior.LAND_IMMEDIATELY) {
                this.currentTextColorId = R.color.colorTheme;
            } else {
                this.currentTextColorId = color.red;
                Object[] var2 = new Object[0];
                DJILog.d("BatteryWidget", "Battery error and battery percentage is less than goHomeBattery or level is 1 or 2", var2);
            }
        }

    }

    @MainThread
    @Keep
    public void onBatteryPercentageChange(@IntRange(from = 0L, to = 100L) int var1) {
        this.updateUIResources();
    }

    @MainThread
    @Keep
    public void onBatteryConnectionStateChange(@Nullable ConnectionState var1) {
        this.updateUIResources();
    }

    @MainThread
    @Keep
    public void onRemainingBatteryStateChange(@Nullable BatteryThresholdBehavior var1) {
        this.updateUIResources();
    }

    public void initView(Context var1, AttributeSet var2, int var3) {
        super.initView(var1, var2, var3);
//        int var10001 = H.a(var1, var2, styleable.BatteryWidget, styleable.BatteryWidget_excludeView, 0);
        this.iconImageView = (ImageView) this.findViewById(id.imageview_battery_icon);
        this.multiIconImageView = (ImageView) this.findViewById(id.imageview_multi_battery_icon);
        this.valueTextView = (TextView) this.findViewById(id.textview_battery_value);
        this.value1TextView = (TextView) this.findViewById(id.textview_battery1_value);
        this.value2TextView = (TextView) this.findViewById(id.textview_battery2_value);
        this.voltageTextView = (TextView) this.findViewById(id.textview_battery_voltage);
        this.voltage1TextView = (TextView) this.findViewById(id.textview_battery1_voltage);
        this.voltage2TextView = (TextView) this.findViewById(id.textview_battery2_voltage);
//        this.initViewByAttribute(var10001);
    }


    public void initKey() {
        this.isProductConnectKey = ProductKey.create("Connection");
        this.batteryEnergyRemainPercentageKey = BatteryKey.create("ChargeRemainingInPercent");
        this.battery2EnergyRemainPercentageKey = BatteryKey.create("ChargeRemainingInPercent", 1);
        this.batteryCellVoltageKey = BatteryKey.create("CellVoltages");
        this.battery2CellVoltageKey = BatteryKey.create("CellVoltages", 1);
        this.batteryConnectionStateKey = BatteryKey.create("ConnectionState");
        this.batteryAggregationKey = BatteryKey.createBatteryAggregationKey("AggregationState");
        this.batteryRemainFlightKey = FlightControllerKey.create("RemainingBattery");
        this.batteryNeedToGoHomeKey = FlightControllerKey.create("BatteryPercentageNeededToGoHome");
        this.addDependentKey(this.isProductConnectKey);
        this.addDependentKey(this.batteryEnergyRemainPercentageKey);
        this.addDependentKey(this.battery2EnergyRemainPercentageKey);
        this.addDependentKey(this.batteryCellVoltageKey);
        this.addDependentKey(this.battery2CellVoltageKey);
        this.addDependentKey(this.batteryConnectionStateKey);
        this.addDependentKey(this.batteryAggregationKey);
        this.addDependentKey(this.batteryRemainFlightKey);
        this.addDependentKey(this.batteryNeedToGoHomeKey);
    }

    public void transformValue(Object var1, DJIKey var2) {
        if (var2.equals(this.batteryEnergyRemainPercentageKey)) {
            this.batteryPercentage = (Integer) var1;
            this.batteryPerInString = this.getContext().getString(string.battery_percent, new Object[]{this.batteryPercentage});
        } else if (var2.equals(this.battery2EnergyRemainPercentageKey)) {
            int var3 = (Integer) var1;
            this.battery2PerInString = this.getContext().getString(string.battery_percent, new Object[]{var3});
        } else {
            float var4;
            if (var2.equals(this.batteryCellVoltageKey)) {
                var4 = this.getMinVoltage(var1);
                this.battery1VoltageString = super.context.getString(string.battery_voltage_unit, new Object[]{var4});
            } else if (var2.equals(this.battery2CellVoltageKey)) {
                var4 = this.getMinVoltage(var1);
                this.battery2VoltageString = super.context.getString(string.battery_voltage_unit, new Object[]{var4});
            } else if (var2.equals(this.batteryConnectionStateKey)) {
                this.batteryConnectionState = (ConnectionState) var1;
            } else if (var2.equals(this.batteryRemainFlightKey)) {
                this.batteryWarningLevel = (BatteryThresholdBehavior) var1;
            } else if (var2.equals(this.batteryNeedToGoHomeKey)) {
                this.goHomeBattery = (Integer) var1;
            } else if (var2.equals(this.isProductConnectKey)) {
                this.isConnected = (Boolean) var1;
            } else if (var2.equals(this.batteryAggregationKey)) {
                this.aggregationState = (AggregationState) var1;
            }
        }

        if (KeyManager.getInstance() != null) {
            this.updateCurrentResources();
        }

    }

    public void updateWidget(DJIKey var1) {
        if (var1.equals(this.batteryEnergyRemainPercentageKey)) {
            this.onBatteryPercentageChange(this.batteryPercentage);
        } else if (var1.equals(this.batteryConnectionStateKey)) {
            this.onBatteryConnectionStateChange(this.batteryConnectionState);
        } else if (var1.equals(this.batteryRemainFlightKey)) {
            this.onRemainingBatteryStateChange(this.batteryWarningLevel);
        } else {
            this.updateUIResources();
        }

    }
}
