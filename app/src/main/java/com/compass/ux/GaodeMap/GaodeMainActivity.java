package com.compass.ux.GaodeMap;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import dji.common.error.DJIError;
import dji.common.flightcontroller.FlightControllerState;
import dji.common.flightcontroller.virtualstick.FlightCoordinateSystem;
import dji.common.flightcontroller.virtualstick.RollPitchControlMode;
import dji.common.flightcontroller.virtualstick.VerticalControlMode;
import dji.common.flightcontroller.virtualstick.YawControlMode;
import dji.common.mission.waypoint.Waypoint;
import dji.common.mission.waypoint.WaypointMission;
import dji.common.mission.waypoint.WaypointMissionDownloadEvent;
import dji.common.mission.waypoint.WaypointMissionExecutionEvent;
import dji.common.mission.waypoint.WaypointMissionFinishedAction;
import dji.common.mission.waypoint.WaypointMissionFlightPathMode;
import dji.common.mission.waypoint.WaypointMissionHeadingMode;
import dji.common.mission.waypoint.WaypointMissionUploadEvent;
import dji.common.useraccount.UserAccountState;
import dji.common.util.CommonCallbacks;
import dji.sdk.base.BaseProduct;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.mission.waypoint.WaypointMissionOperator;
import dji.sdk.mission.waypoint.WaypointMissionOperatorListener;
import dji.sdk.products.Aircraft;
import dji.sdk.sdkmanager.DJISDKManager;
import dji.sdk.useraccount.UserAccountManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.compass.ux.R;
import com.compass.ux.takephoto.FPVDemoApplication;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


//public class GaodeMainActivity extends FragmentActivity implements View.OnClickListener, AMap.OnMapClickListener {
//
//    protected static final String TAG = "GaodeMainActivity";
//
//    private MapView mapView;
//    private AMap aMap;
//
//    private Button locate, add, clear;
//    private Button config, upload, start, stop,pause,resume;
//
//    private double droneLocationLat = 0.0, droneLocationLng = 0.0;
//    private Marker droneMarker = null;
//    private FlightController mFlightController;
//
//    private boolean isAdd = false;
//    private final Map<Integer, Marker> mMarkers = new ConcurrentHashMap<Integer, Marker>();
//
//
//    private float altitude = 100.0f;
//    private float mSpeed = 10.0f;
//
//    private List<Waypoint> waypointList = new ArrayList<>();
//
//    public static WaypointMission.Builder waypointMissionBuilder;
//    private WaypointMissionOperator instance;
//    private WaypointMissionFinishedAction mFinishedAction = WaypointMissionFinishedAction.NO_ACTION;
//    private WaypointMissionHeadingMode mHeadingMode = WaypointMissionHeadingMode.AUTO;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        setContentView(R.layout.activity_gaode_main);
//
////        IntentFilter filter = new IntentFilter();
////        filter.addAction(FPVDemoApplication.FLAG_CONNECTION_CHANGE);
////        registerReceiver(mReceiver, filter);
//
//
//        mapView = (MapView) findViewById(R.id.map);
//        mapView.onCreate(savedInstanceState);
//
//        initMapView();
//        initUI();
//
//        addListener();
//        initFlightController();
//    }
////    当DJI产品的连接状态改变时，该方法将被调用，我们可以使用它来更新飞机的位置。
////    protected BroadcastReceiver mReceiver = new BroadcastReceiver() {
////        @Override
////        public void onReceive(Context context, Intent intent) {
////            onProductConnectionChange();
////        }
////    };
//
//    @Override
//    protected void onResume(){
//        super.onResume();
//    }
//
//    @Override
//    protected void onPause(){
//        super.onPause();
//    }
//
//    @Override
//    protected void onDestroy(){
//        super.onDestroy();
////        unregisterReceiver(mReceiver);
//        removeListener();
//    }
//
//    /**
//     * @Description : RETURN BTN RESPONSE FUNCTION
//     */
//    public void onReturn(View view){
//        Log.d(TAG, "onReturn");
//        this.finish();
//    }
//
//    private void initUI() {
//        locate = (Button) findViewById(R.id.locate);
//        add = (Button) findViewById(R.id.add);
//        clear = (Button) findViewById(R.id.clear);
//        config = (Button) findViewById(R.id.config);
//        upload = (Button) findViewById(R.id.upload);
//        start = (Button) findViewById(R.id.start);
//        stop = (Button) findViewById(R.id.stop);
//        pause=findViewById(R.id.pause);
//        resume=findViewById(R.id.resume);
//
//        locate.setOnClickListener(this);
//        add.setOnClickListener(this);
//        clear.setOnClickListener(this);
//        config.setOnClickListener(this);
//        upload.setOnClickListener(this);
//        start.setOnClickListener(this);
//        stop.setOnClickListener(this);
//        pause.setOnClickListener(this);
//        resume.setOnClickListener(this);
//    }
//
//    private void initMapView() {
//
//        if (aMap == null) {
//            aMap = mapView.getMap();
//            aMap.setOnMapClickListener(this);// add the listener for click for amap object
//        }
//
////        LatLng shenzhen = new LatLng(22.5362, 113.9454);
//        LatLng shenzhen = new LatLng(31.294327, 120.671033);//苏州国际科技园
//        aMap.addMarker(new MarkerOptions().position(shenzhen).title("Marker in Shenzhen"));
//        aMap.moveCamera(CameraUpdateFactory.newLatLng(shenzhen));
//    }
//
//
//
//    private void showSettingDialog(){
//        LinearLayout wayPointSettings = (LinearLayout)getLayoutInflater().inflate(R.layout.dialog_waypointsetting, null);
//
//        final TextView wpAltitude_TV = (TextView) wayPointSettings.findViewById(R.id.altitude);
//        RadioGroup speed_RG = (RadioGroup) wayPointSettings.findViewById(R.id.speed);
//        RadioGroup actionAfterFinished_RG = (RadioGroup) wayPointSettings.findViewById(R.id.actionAfterFinished);
//        RadioGroup heading_RG = (RadioGroup) wayPointSettings.findViewById(R.id.heading);
//
//        speed_RG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
//
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                if (checkedId == R.id.lowSpeed){
//                    mSpeed = 3.0f;
//                } else if (checkedId == R.id.MidSpeed){
//                    mSpeed = 5.0f;
//                } else if (checkedId == R.id.HighSpeed){
//                    mSpeed = 10.0f;
//                }
//            }
//
//        });
//
//        actionAfterFinished_RG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
////            AUTO_LAND 飞机将在最后一个航点自动降落。
////            CONTINUE_UNTIL_END 如果用户在执行任务时尝试将飞机拉回飞行路线，则飞机将朝着先前的航路点移动并将继续这样做，直到没有更多的航路点返回或用户停止尝试移回飞机。
////            GO_FIRST_WAYPOINT 飞机将返回其第一个航点并悬停就位。
////            GO_HOME 完成任务后，飞机将返回家中。
////            NO_ACTION 任务完成将不会采取进一步的行动
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                Log.d(TAG, "Select finish action");
//                if (checkedId == R.id.finishNone){
//                    mFinishedAction = WaypointMissionFinishedAction.NO_ACTION;
//                } else if (checkedId == R.id.finishGoHome){
//                    mFinishedAction = WaypointMissionFinishedAction.GO_HOME;
//                } else if (checkedId == R.id.finishAutoLanding){
//                    mFinishedAction = WaypointMissionFinishedAction.AUTO_LAND;
//                } else if (checkedId == R.id.finishToFirst){
//                    mFinishedAction = WaypointMissionFinishedAction.GO_FIRST_WAYPOINT;
//                }
//            }
//        });
//
//        heading_RG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
////            AUTO 飞机的航向将始终在飞行方向上。
////            CONTROL_BY_REMOTE_CONTROLLER 飞机的航向将由遥控器控制。
////            TOWARD_POINT_OF_INTEREST 飞机的航向将始终朝着兴趣点前进。
////            USING_INITIAL_DIRECTION 飞机的航向将设置为初始起飞航向。
////            USING_WAYPOINT_HEADING 在航点之间旅行时，飞机的航向将设置为上一个航点的航向。
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                Log.d(TAG, "Select heading");
//
//                if (checkedId == R.id.headingNext) {
//                    mHeadingMode = WaypointMissionHeadingMode.AUTO;
//                } else if (checkedId == R.id.headingInitDirec) {
//                    mHeadingMode = WaypointMissionHeadingMode.USING_INITIAL_DIRECTION;
//                } else if (checkedId == R.id.headingRC) {
//                    mHeadingMode = WaypointMissionHeadingMode.CONTROL_BY_REMOTE_CONTROLLER;
//                } else if (checkedId == R.id.headingWP) {
//                    mHeadingMode = WaypointMissionHeadingMode.USING_WAYPOINT_HEADING;
//                }
//            }
//        });
//
//        new AlertDialog.Builder(this)
//                .setTitle("")
//                .setView(wayPointSettings)
//                .setPositiveButton("Finish",new DialogInterface.OnClickListener(){
//                    public void onClick(DialogInterface dialog, int id) {
//
//                        String altitudeString = wpAltitude_TV.getText().toString();
//                        altitude = Integer.parseInt(nulltoIntegerDefault(altitudeString));
//                        Log.e(TAG,"altitude "+altitude);
//                        Log.e(TAG,"speed "+mSpeed);
//                        Log.e(TAG, "mFinishedAction "+mFinishedAction);
//                        Log.e(TAG, "mHeadingMode "+mHeadingMode);
//                        configWayPointMission();
//                    }
//
//                })
//                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//
//                })
//                .create()
//                .show();
//    }
//
//    String nulltoIntegerDefault(String value){
//        if(!isIntValue(value)) value="0";
//        return value;
//    }
//
//    boolean isIntValue(String val)
//    {
//        try {
//            val=val.replace(" ","");
//            Integer.parseInt(val);
//        } catch (Exception e) {return false;}
//        return true;
//    }
//
//
//    private void onProductConnectionChange()
//    {
//        initFlightController();
//    }
//
//    private void initFlightController() {
//
//        Aircraft aircraft = FPVDemoApplication.getAircraftInstance();
//        if (aircraft == null || !aircraft.isConnected()) {
////            showToast("Disconnected");
//            mFlightController = null;
//            return;
//        } else {
//            mFlightController = aircraft.getFlightController();
//            mFlightController.setRollPitchControlMode(RollPitchControlMode.VELOCITY);
//            mFlightController.setYawControlMode(YawControlMode.ANGULAR_VELOCITY);
//            mFlightController.setVerticalControlMode(VerticalControlMode.VELOCITY);
//            mFlightController.setRollPitchCoordinateSystem(FlightCoordinateSystem.BODY);
//
//            mFlightController.setStateCallback(new FlightControllerState.Callback() {
//                @Override
//                public void onUpdate(FlightControllerState flightControllerState) {
//                    Log.d(TAG, "=====================================================");
//                    Log.d(TAG, "卫星数=" + flightControllerState.getSatelliteCount() + "");
//                    Log.d(TAG, "飞机的高度=" + flightControllerState.getUltrasonicHeightInMeters());//低于5米才有参考价值
//                    Log.d(TAG, "getFlightModeString=" + flightControllerState.getFlightModeString());
//                    Log.d(TAG, "getAircraftHeadDirection=" + flightControllerState.getAircraftHeadDirection());
//                    Log.d(TAG, "当前电量能干嘛=" + flightControllerState.getBatteryThresholdBehavior());
//                    Log.d(TAG, "getVelocityX=" + flightControllerState.getVelocityX());
//                    Log.d(TAG, "getVelocityY=" + flightControllerState.getVelocityY());
//                    Log.d(TAG, "getVelocityZ=" + flightControllerState.getVelocityZ());
//                    Log.d(TAG, "HomeLocation.getLatitude=" + flightControllerState.getHomeLocation().getLatitude());
//                    Log.d(TAG, "HomeLocation.getLongitude=" + flightControllerState.getHomeLocation().getLongitude());
//                    Log.d(TAG, "pitch=" + flightControllerState.getAttitude().pitch);
//                    Log.d(TAG, "roll=" + flightControllerState.getAttitude().roll);
//                    Log.d(TAG, "yaw=" + flightControllerState.getAttitude().yaw);
//                    Log.d(TAG, "纬度=" + flightControllerState.getAircraftLocation().getLatitude());
//                    Log.d(TAG, "经度=" + flightControllerState.getAircraftLocation().getLongitude());
//                    Log.d(TAG, "海拔=" + flightControllerState.getAircraftLocation().getAltitude());
//
//                    droneLocationLat = flightControllerState.getAircraftLocation().getLatitude();
//                    droneLocationLng = flightControllerState.getAircraftLocation().getLongitude();
//                    updateDroneLocation();
//
//                }
//            });
//        }
//
//
////        BaseProduct product = FPVDemoApplication.getProductInstance();
////        if (product != null && product.isConnected()) {
////            if (product instanceof Aircraft) {
////                mFlightController = ((Aircraft) product).getFlightController();
////            }
////        }
////
////        if (mFlightController != null) {
////            mFlightController.setStateCallback(
////                    new FlightControllerState.Callback() {
//////                        通过使用该onUpdate()方法，可以从参数获取飞行控制器的当前状态。
////                        @Override
////                        public void onUpdate(FlightControllerState djiFlightControllerCurrentState) {
////                            droneLocationLat = djiFlightControllerCurrentState.getAircraftLocation().getLatitude();
////                            droneLocationLng = djiFlightControllerCurrentState.getAircraftLocation().getLongitude();
////                            updateDroneLocation();
////
////                        }
////                    });
////        }
//    }
//
//    public static boolean checkGpsCoordination(double latitude, double longitude) {
//        return (latitude > -90 && latitude < 90 && longitude > -180 && longitude < 180) && (latitude != 0f && longitude != 0f);
//    }
//
//    private void updateDroneLocation(){
//
//        LatLng pos = new LatLng(droneLocationLat, droneLocationLng);
//        //添加无人机标记
//        final MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(pos);
//        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.aircraft));
//
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (droneMarker != null) {
//                    droneMarker.remove();
//                }
//
//                if (checkGpsCoordination(droneLocationLat, droneLocationLng)) {
//                    droneMarker = aMap.addMarker(markerOptions);
//                }
//            }
//        });
//    }
//
////    将摄像机移动并将高德地图放大到无人机位置
//    private void cameraUpdate(){
//        LatLng pos = new LatLng(droneLocationLat, droneLocationLng);
//        float zoomlevel = (float) 18.0;
//        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(pos, zoomlevel);
//        aMap.moveCamera(cu);
//    }
//
//    @Override
//    public void onClick(View v) {
//        // TODO Auto-generated method stub
//        switch (v.getId()) {
//            case R.id.locate:{
////                initFlightController();
//                loginAccount();
//////                updateDroneLocation();
//                cameraUpdate();
//                break;
//            }
//            case R.id.add:{
//                enableDisableAdd();
//                break;
//            }
//            case R.id.clear:{
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        aMap.clear();
//                    }
//                });
//                waypointList.clear();
//                waypointMissionBuilder.waypointList(waypointList);
//                updateDroneLocation();
//                break;
//            }
//            case R.id.upload:{
//                uploadWayPointMission();
//                break;
//            }
//            case R.id.start:{
//                startWaypointMission();
//                break;
//            }
//            case R.id.stop:{
//                stopWaypointMission();
//                break;
//            }
//            case R.id.pause:{
//                pauseWaypointMission();
//                break;
//            }
//            case R.id.resume:{
//                resumeWaypointMission();
//                break;
//            }
//            case R.id.config:{
//                showSettingDialog();
//                break;
//            }
//            default:
//                break;
//        }
//    }
//
//    private void setResultToToast(final String string){
//        GaodeMainActivity.this.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(GaodeMainActivity.this, string, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//
//    private void markWaypoint(LatLng point){
//        //Create MarkerOptions object
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(point);
//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
//        Marker marker = aMap.addMarker(markerOptions);//添加
//        mMarkers.put(mMarkers.size(), marker);
//    }
////    实现ADD和CLEAR操作
//    private void enableDisableAdd(){
//        if (isAdd == false) {
//            isAdd = true;
//            add.setText("Exit");
//        }else{
//            isAdd = false;
//            add.setText("Add");
//        }
//    }
//
//
//    public WaypointMissionOperator getWaypointMissionOperator() {
//        if (instance == null) {
//            instance = DJISDKManager.getInstance().getMissionControl().getWaypointMissionOperator();
//        }
//        return instance;
//    }
//
//    private void configWayPointMission(){
//
//        if (waypointMissionBuilder == null){
//
//            waypointMissionBuilder = new WaypointMission.Builder().finishedAction(mFinishedAction)
//                    .headingMode(mHeadingMode)
//                    .autoFlightSpeed(mSpeed)
//                    .maxFlightSpeed(mSpeed)
//                    .flightPathMode(WaypointMissionFlightPathMode.NORMAL);
//
//        }else
//        {
//            waypointMissionBuilder.finishedAction(mFinishedAction)
//                    .headingMode(mHeadingMode)
//                    .autoFlightSpeed(mSpeed)
//                    .maxFlightSpeed(mSpeed)
//                    .flightPathMode(WaypointMissionFlightPathMode.NORMAL);
//
//        }
//
//        if (waypointMissionBuilder.getWaypointList().size() > 0){
//
//            for (int i=0; i< waypointMissionBuilder.getWaypointList().size(); i++){
//                waypointMissionBuilder.getWaypointList().get(i).altitude = altitude;
//            }
//
//            setResultToToast("Set Waypoint attitude successfully");
//        }
////        将其waypointMissionBuilder.build()作为参数传递给操作员加载航路点任务
//        DJIError error = getWaypointMissionOperator().loadMission(waypointMissionBuilder.build());
//        if (error == null) {
//            setResultToToast("loadWaypoint succeeded");
//        } else {
//            setResultToToast("loadWaypoint failed " + error.getDescription());
//        }
//
//
//    }
//
//    private void addListener() {
//        if (getWaypointMissionOperator() != null) {
//            getWaypointMissionOperator().addListener(eventNotificationListener);
//        }
//    }
//
//    private void removeListener() {
//        if (getWaypointMissionOperator() != null) {
//            getWaypointMissionOperator().removeListener(eventNotificationListener);
//        }
//    }
//
//    private WaypointMissionOperatorListener eventNotificationListener = new WaypointMissionOperatorListener() {
//        @Override
//        public void onDownloadUpdate(WaypointMissionDownloadEvent downloadEvent) {
//
//        }
//
//        @Override
//        public void onUploadUpdate(WaypointMissionUploadEvent uploadEvent) {
//
//        }
//
//        @Override
//        public void onExecutionUpdate(WaypointMissionExecutionEvent executionEvent) {
//
//        }
//
//        @Override
//        public void onExecutionStart() {
//
//        }
////        显示一条消息，以在任务执行完成时通知用户。
//        @Override
//        public void onExecutionFinish(@Nullable final DJIError error) {
//            setResultToToast("Execution finished: " + (error == null ? "Success!" : error.getDescription()));
//        }
//    };
//    //    当用户点击“地图视图”的不同位置时，我们将创建一个MarkerOptions对象
//    @Override
//    public void onMapClick(LatLng point) {
//        if (isAdd == true){
//            markWaypoint(point);
//            Waypoint mWaypoint = new Waypoint(point.latitude, point.longitude, altitude);
////            添加一个航点，并实现uploadWayPointMission()将任务上传到操作员的方法
//            if (waypointMissionBuilder != null) {
//                waypointList.add(mWaypoint);
//                waypointMissionBuilder.waypointList(waypointList).waypointCount(waypointList.size());
//            }else
//            {
//                waypointMissionBuilder = new WaypointMission.Builder();
//                waypointList.add(mWaypoint);
//                waypointMissionBuilder.waypointList(waypointList).waypointCount(waypointList.size());
//            }
//        }else{
//            setResultToToast("Cannot Add Waypoint");
//        }
//    }
//
//    private void uploadWayPointMission(){
//        Gson gson=new Gson();
//        String MSlist=gson.toJson(waypointList);
//        Log.d("MSlist",MSlist);
//        getWaypointMissionOperator().uploadMission(new CommonCallbacks.CompletionCallback() {
//            @Override
//            public void onResult(DJIError error) {
//                if (error == null) {
//                    setResultToToast("Mission upload successfully!");
//                } else {
//                    setResultToToast("Mission upload failed, error: " + error.getDescription() + " retrying...");
//                    getWaypointMissionOperator().retryUploadMission(null);
//                }
//            }
//        });
//
//    }
//
//    private void startWaypointMission(){
//
//        getWaypointMissionOperator().startMission(new CommonCallbacks.CompletionCallback() {
//            @Override
//            public void onResult(DJIError error) {
//                setResultToToast("Mission Start: " + (error == null ? "Successfully" : error.getDescription()));
//            }
//        });
//
//    }
//    private void pauseWaypointMission(){
//
//        getWaypointMissionOperator().pauseMission(new CommonCallbacks.CompletionCallback() {
//            @Override
//            public void onResult(DJIError error) {
//                setResultToToast("Mission pauseStart: " + (error == null ? "Successfully" : error.getDescription()));
//            }
//        });
//
//    }
//
//    private void resumeWaypointMission(){
//
//        getWaypointMissionOperator().resumeMission(new CommonCallbacks.CompletionCallback() {
//            @Override
//            public void onResult(DJIError error) {
//                setResultToToast("Mission resumeStart: " + (error == null ? "Successfully" : error.getDescription()));
//            }
//        });
//
//    }
//
//    private void stopWaypointMission(){
//
//        getWaypointMissionOperator().stopMission(new CommonCallbacks.CompletionCallback() {
//            @Override
//            public void onResult(DJIError error) {
//                setResultToToast("Mission Stop: " + (error == null ? "Successfully" : error.getDescription()));
//            }
//        });
//
//    }
//
//    private void loginAccount(){
//
//        UserAccountManager.getInstance().logIntoDJIUserAccount(this,
//                new CommonCallbacks.CompletionCallbackWith<UserAccountState>() {
//                    @Override
//                    public void onSuccess(final UserAccountState userAccountState) {
//                        Log.e(TAG, "Login Success");
//                    }
//                    @Override
//                    public void onFailure(DJIError error) {
//                        setResultToToast("Login Error:"
//                                + error.getDescription());
//                    }
//                });
//    }
//}
