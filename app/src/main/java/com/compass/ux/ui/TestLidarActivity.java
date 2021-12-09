package com.compass.ux.ui;

import com.google.gson.Gson;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.SeekBar;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.compass.ux.R;
import com.compass.ux.async.Log;
import com.compass.ux.utils.MapConvertUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public class TestLidarActivity extends Activity{
    private static final int WIDTH_MAX = 50;
    private static final int HUE_MAX = 255;
    private static final int ALPHA_MAX = 255;
    private AMap aMap;
    private MapView mapView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_lidar);
        mapView = (MapView) findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        init();
    }
    /**
     * 初始化AMap对象
     */
    private void init() {

        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
    }

    private void setUpMap() {
        LatLng latLng=new LatLng(31.294432,120.670738);
        aMap.addMarker(new MarkerOptions().position(latLng).title("0").snippet("DefaultMarker"));

//        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));// 设置指定的可视区域地图
        aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, 20, 0, 0)));

//        // 绘制一个圆形
//       aMap.addCircle(new CircleOptions().center(latLng)
//                .radius(1000).strokeColor(Color.argb(50, 1, 1, 1))
//                .fillColor(Color.argb(50, 1, 1, 1)).strokeWidth(25));

//        Double radio[]=new Double[]{31.294432,120.670738};

//        List<LatLng> circleAxis = MapConvertUtils.getCircleAxis(radio, 10);
//        List<LatLng> ass=new ArrayList<>();
//        for (int i = 0; i <circleAxis.size() ; i++) {
//            if ((i & 1) != 0){
//                ass.add(circleAxis.get(i));
//            }
//        }
//        circleAxis.removeAll(ass);
//        Log.e("圆弧数量:", circleAxis.size()+"");
//        for (int i = 0; i <circleAxis.size() ; i++) {
//            if (i%5==0){
//                aMap.addMarker(new MarkerOptions().position(circleAxis.get(i)).title(""+i).snippet("DefaultMarker"));
//            }
//        }
        addPolylinescircle(latLng,10);
    }

    public void addPolylinescircle(LatLng centerpoint, int radius) {
        double r = 6371000.79;
        PolylineOptions options = new PolylineOptions();
        int numpoints = 360;
        double phase = 2 * Math.PI / numpoints;
        List<LatLng> latLngs = new ArrayList<>();
        //画图
        for (int i = 0; i < numpoints; i++) {
            /**
             * 计算坐标点
             */
            double dx = (radius * Math.cos(i * phase));
            double dy = (radius * Math.sin(i * phase));//乘以1.6 椭圆比例

            /**
             * 转换成经纬度
             */
            double dlng = dx / (r * Math.cos(centerpoint.latitude * Math.PI / 180) * Math.PI / 180);
            double dlat = dy / (r * Math.PI / 180);
            double newlng = centerpoint.longitude + dlng;
            LatLng latLng = new LatLng(centerpoint.latitude + dlat, newlng);
            options.add(latLng);
            latLngs.add(latLng);
        }
        aMap.addPolyline(options.width(10).useGradient(true).setDottedLine(true));

        for (int i = 0; i < latLngs.size(); i++) {
            Log.e("iiii",i+"");
            if (i % 36 == 0) {
                aMap.addMarker(new MarkerOptions().position(latLngs.get(i)).title("" + i).snippet("DefaultMarker"));
            }

        }
    }
    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }


}
