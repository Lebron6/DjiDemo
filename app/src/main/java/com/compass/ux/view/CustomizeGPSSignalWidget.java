package com.compass.ux.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.compass.ux.R;

import androidx.annotation.Nullable;
import dji.common.flightcontroller.GPSSignalLevel;
import dji.ux.model.base.BaseWidgetAppearances;
import dji.ux.widget.FlightModeWidget;
import dji.ux.widget.GPSSignalWidget;

/**
 * 暂时用UX
 */
public class CustomizeGPSSignalWidget extends GPSSignalWidget {
    private ImageView ivFlightMode;

    public CustomizeGPSSignalWidget(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    public void onGPSSignalStatusChange(@Nullable GPSSignalLevel gpsSignalLevel) {
        super.onGPSSignalStatusChange(gpsSignalLevel);
    }

    @Override
    public void onSatelliteNumberChange(int i) {
        super.onSatelliteNumberChange(i);
    }

    @Override
    public void initView(Context context, AttributeSet attributeSet, int i) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.customized_flight_mode, this);
        ivFlightMode = (ImageView) view.findViewById(R.id.iv_flight_mode);
        ivFlightMode.setImageResource(R.mipmap.icon_flight_mode);

    }

    @Override
    protected BaseWidgetAppearances getWidgetAppearances() {
        return null;
    }
}
