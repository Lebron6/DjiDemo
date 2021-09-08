package com.compass.ux.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.compass.ux.R;

import androidx.annotation.Nullable;
import dji.ux.model.base.BaseWidgetAppearances;
import dji.ux.widget.FlightModeWidget;

public class CustomizeFlightModeWidget extends FlightModeWidget {
    private ImageView ivFlightMode;
    private TextView tvStatus;

    public CustomizeFlightModeWidget(Context context) {
        this(context, null, 0);
    }

    public CustomizeFlightModeWidget(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);

    }

    public CustomizeFlightModeWidget(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override
    public void onFlyControllerModeChange(@Nullable String s) {
        ivFlightMode.setImageResource(R.mipmap.icon_flight_mode);
        tvStatus.setText(s);

    }

    @Override
    public void initView(Context context, AttributeSet attributeSet, int i) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.customized_flight_mode, this);
        tvStatus = (TextView) view.findViewById(R.id.tv_status);
        ivFlightMode = (ImageView) view.findViewById(R.id.iv_flight_mode);
        ivFlightMode.setImageResource(R.mipmap.icon_flight_mode);
        tvStatus.setText("N/A");
        tvStatus.setTextColor(context.getResources().getColor(R.color.colorWhite));
    }

    @Override
    protected BaseWidgetAppearances getWidgetAppearances() {
        return null;
    }
}
