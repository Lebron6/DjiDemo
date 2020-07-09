package com.compass.ux;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import dji.common.accessory.AccessoryAggregationState;
import dji.common.battery.BatteryState;
import dji.common.camera.SettingsDefinitions;
import dji.common.error.DJIError;
import dji.common.error.DJISDKError;
import dji.common.flightcontroller.FlightControllerState;
import dji.common.flightcontroller.FlightMode;
import dji.common.flightcontroller.ObstacleDetectionSector;
import dji.common.flightcontroller.VisionControlState;
import dji.common.flightcontroller.VisionDetectionState;
import dji.common.flightcontroller.flightassistant.ObstacleAvoidanceSensorState;
import dji.common.flightcontroller.flightassistant.PerceptionInformation;
import dji.common.flightcontroller.virtualstick.FlightControlData;
import dji.common.flightcontroller.virtualstick.FlightCoordinateSystem;
import dji.common.flightcontroller.virtualstick.RollPitchControlMode;
import dji.common.flightcontroller.virtualstick.VerticalControlMode;
import dji.common.flightcontroller.virtualstick.YawControlMode;
import dji.common.gimbal.Rotation;
import dji.common.gimbal.RotationMode;
import dji.common.mission.waypoint.Waypoint;
import dji.common.mission.waypoint.WaypointAction;
import dji.common.mission.waypoint.WaypointActionType;
import dji.common.mission.waypoint.WaypointMission;
import dji.common.mission.waypoint.WaypointMissionDownloadEvent;
import dji.common.mission.waypoint.WaypointMissionExecutionEvent;
import dji.common.mission.waypoint.WaypointMissionFinishedAction;
import dji.common.mission.waypoint.WaypointMissionFlightPathMode;
import dji.common.mission.waypoint.WaypointMissionHeadingMode;
import dji.common.mission.waypoint.WaypointMissionUploadEvent;
import dji.common.product.Model;
import dji.common.useraccount.UserAccountState;
import dji.common.util.CommonCallbacks;
import dji.internal.logics.CommonUtil;
import dji.keysdk.CameraKey;
import dji.keysdk.FlightControllerKey;
import dji.keysdk.KeyManager;
import dji.keysdk.callback.GetCallback;
import dji.keysdk.callback.SetCallback;
import dji.sdk.base.BaseComponent;
import dji.sdk.base.BaseProduct;
import dji.sdk.camera.Camera;
import dji.sdk.camera.Lens;
import dji.sdk.camera.VideoFeeder;
import dji.sdk.codec.DJICodecManager;
import dji.sdk.flightcontroller.FlightAssistant;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.flightcontroller.LandingGear;
import dji.sdk.gimbal.Gimbal;
import dji.sdk.mission.waypoint.WaypointMissionOperator;
import dji.sdk.mission.waypoint.WaypointMissionOperatorListener;
import dji.sdk.products.Aircraft;
import dji.sdk.sdkmanager.DJISDKInitEvent;
import dji.sdk.sdkmanager.DJISDKManager;
import dji.sdk.useraccount.UserAccountManager;
import dji.thirdparty.afinal.core.AsyncTask;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.compass.ux.GaodeMap.DJIDemoApplication;
import com.compass.ux.GaodeMap.GaodeMainActivity;
import com.compass.ux.bean.BatteryStateBean;
import com.compass.ux.bean.FlightControllerBean;
import com.compass.ux.downloadpic.DefaultLayoutActivity;
import com.compass.ux.live.live.LiveStreamView;
import com.compass.ux.netty_lib.NettyService;
import com.compass.ux.netty_lib.activity.NettyActivity;
import com.compass.ux.netty_lib.netty.NettyClient;
import com.compass.ux.netty_lib.zhang.Communication;
import com.compass.ux.netty_lib.zhang.RsaUtil;
import com.compass.ux.simulator.SimulatorMainActivity;
import com.compass.ux.takephoto.FPVDemoApplication;
import com.compass.ux.takephoto.TakePhotoActivity;
import com.compass.ux.utils.fastClick;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConnectionActivity extends NettyActivity implements View.OnClickListener, TextureView.SurfaceTextureListener {

    private static final String TAG = ConnectionActivity.class.getName();

    private TextView mTextConnectionStatus;
    private TextView mTextProduct;
    private TextView mVersionTv;
    private Button mBtnOpen, btn_download, btn_gaode, btn_simulator, btn_login;

    private FlightController mFlightController;
    private Timer mSendVirtualStickDataTimer;
    private float mPitch;
    private float mRoll;
    private float mYaw;
    private float mThrottle;
    private float altitude = 100.0f;//高度
    private float mSpeed = 10.0f;//速度
    private SendVirtualStickDataTask mSendVirtualStickDataTask;
    private double droneLocationLat = 0.0, droneLocationLng = 0.0;
    private Marker droneMarker = null;
    private WaypointMissionFinishedAction mFinishedAction = WaypointMissionFinishedAction.NO_ACTION;
    private WaypointMissionHeadingMode mHeadingMode = WaypointMissionHeadingMode.AUTO;
    public static WaypointMission.Builder waypointMissionBuilder;
    private WaypointMissionOperator instance;
    private List<Waypoint> waypointList = new ArrayList<>();

    private Handler mHandler;
    public static final String FLAG_CONNECTION_CHANGE = "dji_sdk_connection_change";
    private Gson gson;
    private static final String[] REQUIRED_PERMISSION_LIST = new String[]{
            Manifest.permission.VIBRATE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
    };
    private List<String> missingPermission = new ArrayList<>();
    private AtomicBoolean isRegistrationInProgress = new AtomicBoolean(false);
    private static final int REQUEST_PERMISSION_CODE = 12345;


    VideoFeeder.VideoDataListener mReceivedVideoDataListener = null;
    DJICodecManager mCodecManager = null;
    TextureView mVideoSurface = null;
    FlightControllerBean flightControllerBean = null;
    BatteryStateBean batteryStateBean = null;
    Camera camera = null;
    Communication communication_flightController = null;
    Communication communication_battery = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        checkAndRequestPermissions();
//        }
        setContentView(R.layout.activity_connection);
        mHandler = new Handler(Looper.getMainLooper());
        initUI();

        //注册广播接收器以接收设备连接的更改。
        IntentFilter filter = new IntentFilter();
        filter.addAction(FPVDemoApplication.FLAG_CONNECTION_CHANGE);
        registerReceiver(mReceiver, filter);
        //开启一个服务进行长连接
        final Intent intent = new Intent(this, NettyService.class);
        startService(intent);
        gson = new Gson();

        //视频流
//        我们mReceivedVideoDataListener使用VideoFeeder的初始化变量VideoDataListener()。
//        在回调内部，我们重写其onReceive()方法以获取原始H264视频数据并将其发送给mCodecManager解码
        mReceivedVideoDataListener = new VideoFeeder.VideoDataListener() {

            @Override
            public void onReceive(byte[] videoBuffer, int size) {
                if (mCodecManager != null) {
                    mCodecManager.sendDataToDecoder(videoBuffer, size);
                }
            }
        };


    }


    @Override
    public void onResume() {
        Log.e(TAG, "onResume");
        super.onResume();
        initPreviewer();

        if (mVideoSurface == null) {
            Log.e(TAG, "mVideoSurface is null");
        }
    }

    @Override
    public void onPause() {
        Log.e(TAG, "onPause");
        uninitPreviewer();
        super.onPause();
    }


    //    在mVideoSurfaceTextureView 上显示和重置实时视频流
    private void initPreviewer() {

        BaseProduct product = FPVDemoApplication.getProductInstance();

        if (product == null || !product.isConnected()) {
            showToast(getString(R.string.disconnected));
        } else {
            if (null != mVideoSurface) {
                mVideoSurface.setSurfaceTextureListener(this);
            }
            if (!product.getModel().equals(Model.UNKNOWN_AIRCRAFT)) {
                VideoFeeder.getInstance().getPrimaryVideoFeed().addVideoDataListener(mReceivedVideoDataListener);
            }
        }
    }

    private void uninitPreviewer() {
//        camera = FPVDemoApplication.getCameraInstance();
//        if (camera != null){
//            // Reset the callback
//            VideoFeeder.getInstance().getPrimaryVideoFeed().addVideoDataListener(null);
//        }
    }

    public void onReturn(View view) {
        Log.e(TAG, "onReturn");
        this.finish();
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy");
        unregisterReceiver(mReceiver);
        if (null != mSendVirtualStickDataTimer) {
            mSendVirtualStickDataTask.cancel();
            mSendVirtualStickDataTask = null;
            mSendVirtualStickDataTimer.cancel();
            mSendVirtualStickDataTimer.purge();
            mSendVirtualStickDataTimer = null;
        }
        removeListener();
        uninitPreviewer();
        super.onDestroy();

    }


    private void initUI() {
        mTextConnectionStatus = (TextView) findViewById(R.id.text_connection_status);
        mTextProduct = (TextView) findViewById(R.id.text_product_info);

        mVersionTv = (TextView) findViewById(R.id.textView2);
        mVersionTv.setText(getResources().getString(R.string.sdk_version, DJISDKManager.getInstance().getSDKVersion()));

        mBtnOpen = (Button) findViewById(R.id.btn_open);
        mBtnOpen.setOnClickListener(this);
        mBtnOpen.setEnabled(false);

        btn_download = findViewById(R.id.btn_download);
        btn_download.setOnClickListener(this);
        btn_gaode = findViewById(R.id.btn_gaode);
        btn_gaode.setOnClickListener(this);
        btn_simulator = findViewById(R.id.btn_simulator);
        btn_simulator.setOnClickListener(this);
        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);

        mVideoSurface = (TextureView) findViewById(R.id.video_previewer_surface);
        if (null != mVideoSurface) {
            mVideoSurface.setSurfaceTextureListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_open: {
                //去拍照录像
                Intent intent = new Intent(this, TakePhotoActivity.class);

                //直播
//                Intent intent = new Intent(this, LiveStreamView.class);
                startActivity(intent);

//                Communication communication=gson.fromJson(json,Communication.class);
//                communication.setResult("200");
//                communication.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
//                NettyClient.getInstance().sendMessage(communication,null);
                break;

            }
            case R.id.btn_download: {
                //去下载
                Intent intent = new Intent(this, DefaultLayoutActivity.class);
                startActivity(intent);
                break;
            }

            case R.id.btn_gaode: {
                //去高德地图
                Intent intent = new Intent(this, GaodeMainActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.btn_simulator: {
                //去控制飞机
                Intent intent = new Intent(this, SimulatorMainActivity.class);
                startActivity(intent);
                break;

            }
            case R.id.btn_login:
                loginAccount();
                break;


            default:
                break;
        }
    }

    /**
     * Checks if there is any missing permissions, and
     * requests runtime permission if needed.
     */
    private void checkAndRequestPermissions() {
        // Check for permissions
        for (String eachPermission : REQUIRED_PERMISSION_LIST) {
            if (ContextCompat.checkSelfPermission(this, eachPermission) != PackageManager.PERMISSION_GRANTED) {
                missingPermission.add(eachPermission);
            }
        }
        // Request for missing permissions
        if (!missingPermission.isEmpty() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,
                    missingPermission.toArray(new String[missingPermission.size()]),
                    REQUEST_PERMISSION_CODE);
        }

    }

    /**
     * Result of runtime permission request
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Check for granted permission and remove from missing list
        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (int i = grantResults.length - 1; i >= 0; i--) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    missingPermission.remove(permissions[i]);
                }
            }
        }
        // If there is enough permission, we will start the registration
        if (missingPermission.isEmpty()) {
            startSDKRegistration();
            loginAccount();
        } else {
            showToast("Missing permissions!!!");
        }
    }

    private void startSDKRegistration() {
        if (isRegistrationInProgress.compareAndSet(false, true)) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    showToast("registering, pls wait...");
                    DJISDKManager.getInstance().registerApp(getApplicationContext(), new DJISDKManager.SDKManagerCallback() {
                        @Override
                        public void onRegister(DJIError djiError) {
                            if (djiError == DJISDKError.REGISTRATION_SUCCESS) {
                                Log.e("App registration", DJISDKError.REGISTRATION_SUCCESS.getDescription());
                                DJISDKManager.getInstance().startConnectionToProduct();
                                showToast("Register Success");

                            } else {
                                showToast("Register sdk fails, check network is available");
                            }
                            Log.v(TAG, djiError.getDescription());
                        }

                        @Override
                        public void onProductDisconnect() {
                            Log.d(TAG, "onProductDisconnect");
                            showToast("Product Disconnected");

                        }

                        @Override
                        public void onProductConnect(BaseProduct baseProduct) {
                            Log.d(TAG, String.format("onProductConnect newProduct:%s", baseProduct));
                            showToast("Product Connected");

                        }

                        @Override
                        public void onComponentChange(BaseProduct.ComponentKey componentKey, BaseComponent oldComponent,
                                                      BaseComponent newComponent) {

                            if (newComponent != null) {
                                newComponent.setComponentListener(new BaseComponent.ComponentListener() {

                                    @Override
                                    public void onConnectivityChange(boolean isConnected) {
                                        Log.d(TAG, "onComponentConnectivityChanged: " + isConnected);
                                    }
                                });
                            }
                            Log.d(TAG,
                                    String.format("onComponentChange key:%s, oldComponent:%s, newComponent:%s",
                                            componentKey,
                                            oldComponent,
                                            newComponent));

                        }

                        @Override
                        public void onInitProcess(DJISDKInitEvent djisdkInitEvent, int i) {

                        }

                        @Override
                        public void onDatabaseDownloadProgress(long l, long l1) {

                        }

                    });


                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage("dji.go.v4");
                    if (launchIntent != null) {
                        startActivity(launchIntent);//null pointer check in case package name was not found
                    } else {
                        showToast("Cannot redirect to DJI Go 4, Please check if DJI Go 4 has installed");
                    }

                    Intent launchIntent2 = getPackageManager().getLaunchIntentForPackage("dji.pilot");
                    if (launchIntent2 != null) {
                        startActivity(launchIntent2);//null pointer check in case package name was not found
                    } else {
                        showToast("Cannot redirect to DJI Go, Please check if DJI Go has installed");
                    }
                }
            });
        }
    }


    private void showToast(final String toastMsg) {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_LONG).show();
            }
        });

    }

    //连接成功后调用
    protected BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshSDKRelativeUI();
            initFlightController();
            initCamera();
            addListener();
        }
    };

    private void refreshSDKRelativeUI() {
        BaseProduct mProduct = FPVDemoApplication.getProductInstance();

        if (null != mProduct && mProduct.isConnected()) {
            Log.v(TAG, "refreshSDK: True");
            mBtnOpen.setEnabled(true);

            String str = mProduct instanceof Aircraft ? "DJIAircraft" : "DJIHandHeld";
            mTextConnectionStatus.setText("Status: " + str + " connected");

            if (null != mProduct.getModel()) {
                mTextProduct.setText("" + mProduct.getModel().getDisplayName());
            } else {
                mTextProduct.setText(R.string.product_information);
            }

        } else {
            Log.v(TAG, "refreshSDK: False");
            mBtnOpen.setEnabled(false);

            mTextProduct.setText(R.string.product_information);
            mTextConnectionStatus.setText(R.string.connection_loose);
        }
    }

    private void loginAccount() {

        UserAccountManager.getInstance().logIntoDJIUserAccount(this,
                new CommonCallbacks.CompletionCallbackWith<UserAccountState>() {
                    @Override
                    public void onSuccess(final UserAccountState userAccountState) {
                        Log.e(TAG, "Login Success");
                    }

                    @Override
                    public void onFailure(DJIError error) {
                        showToast("Login Error:"
                                + error.getDescription());
                    }
                });
    }


    //    我们首先检查飞机是否不为零并且已连接，然后调用getFlightController()飞机方法获取mFlightController变量。
//    重写onUpdate()方法来获取最新的仿真状态数据，然后调用getYaw()，getPitch()，getRoll()，getPositionX()，getPositionY()和getPositionZ()
//    的方法SimulatorState来获得更新的偏航，俯仰，滚转位X，位置▲和positionZ值并显示它们mTextView。
    private void initFlightController() {
        Aircraft aircraft = FPVDemoApplication.getAircraftInstance();
        if (aircraft == null || !aircraft.isConnected()) {
            showToast("Disconnected");
            mFlightController = null;
            return;
        } else {
            mFlightController = aircraft.getFlightController();
            mFlightController.setRollPitchControlMode(RollPitchControlMode.VELOCITY);
            mFlightController.setYawControlMode(YawControlMode.ANGULAR_VELOCITY);
            mFlightController.setVerticalControlMode(VerticalControlMode.VELOCITY);
            mFlightController.setRollPitchCoordinateSystem(FlightCoordinateSystem.BODY);
            //设置回家时上升高度
//            mFlightController.setGoHomeHeightInMeters(100, new CommonCallbacks.CompletionCallback() {
//                @Override
//                public void onResult(DJIError djiError) {
//
//                }
//            });
            mFlightController.setStateCallback(new FlightControllerState.Callback() {
                @Override
                public void onUpdate(FlightControllerState flightControllerState) {
                    Log.d(TAG, "=====================================================");
                    Log.d(TAG, "卫星数=" + flightControllerState.getSatelliteCount() + "");
                    Log.d(TAG, "飞机的高度=" + flightControllerState.getUltrasonicHeightInMeters());//低于5米才有参考价值
                    Log.d(TAG, "getFlightModeString=" + flightControllerState.getFlightModeString());
                    Log.d(TAG, "getAircraftHeadDirection=" + flightControllerState.getAircraftHeadDirection());
                    Log.d(TAG, "当前电量能干嘛=" + flightControllerState.getBatteryThresholdBehavior());
                    Log.d(TAG, "getVelocityX=" + flightControllerState.getVelocityX());
                    Log.d(TAG, "getVelocityY=" + flightControllerState.getVelocityY());
                    Log.d(TAG, "getVelocityZ=" + flightControllerState.getVelocityZ());
                    Log.d(TAG, "HomeLocation.getLatitude=" + flightControllerState.getHomeLocation().getLatitude());
                    Log.d(TAG, "HomeLocation.getLongitude=" + flightControllerState.getHomeLocation().getLongitude());
                    Log.d(TAG, "pitch=" + flightControllerState.getAttitude().pitch);
                    Log.d(TAG, "roll=" + flightControllerState.getAttitude().roll);
                    Log.d(TAG, "yaw=" + flightControllerState.getAttitude().yaw);
                    Log.d(TAG, "纬度=" + flightControllerState.getAircraftLocation().getLatitude());
                    Log.d(TAG, "经度=" + flightControllerState.getAircraftLocation().getLongitude());
                    Log.d(TAG, "海拔=" + flightControllerState.getAircraftLocation().getAltitude());
                    Log.d(TAG, "IslandingConfirmationNeeded=" + flightControllerState.isLandingConfirmationNeeded() + "");
                        if (flightControllerBean == null) {
                            flightControllerBean = new FlightControllerBean();
                        }
                        flightControllerBean.setAreMotorsOn(flightControllerState.areMotorsOn());
                        flightControllerBean.setFlying(flightControllerState.isFlying());
                        if((flightControllerState.getAircraftLocation().getAltitude()+"").equals("NaN")){
                            flightControllerBean.setAircraftAltitude(0.0);
                        }else{
                            flightControllerBean.setAircraftAltitude(flightControllerState.getAircraftLocation().getAltitude());
                        }
                        if((flightControllerState.getAircraftLocation().getLatitude()+"").equals("NaN")){
                            flightControllerBean.setAircraftLatitude(0.0);
                        }else{
                            flightControllerBean.setAircraftLatitude(flightControllerState.getAircraftLocation().getLatitude());
                        }
                        if((flightControllerState.getAircraftLocation().getLongitude()+"").equals("NaN")){
                            flightControllerBean.setAircraftLongitude(0.0);
                        }else{
                            flightControllerBean.setAircraftLongitude(flightControllerState.getAircraftLocation().getLongitude());
                        }
                        if((flightControllerState.getTakeoffLocationAltitude()+"").equals("NaN")){
                            flightControllerBean.setTakeoffLocationAltitude(0);
                        }else{
                            flightControllerBean.setTakeoffLocationAltitude(flightControllerState.getTakeoffLocationAltitude());
                        }
                        flightControllerBean.setAttitudePitch(flightControllerState.getAttitude().pitch);
                        flightControllerBean.setAttitudeRoll(flightControllerState.getAttitude().roll);
                        flightControllerBean.setAttitudeYaw(flightControllerState.getAttitude().yaw);
                        flightControllerBean.setVelocityX(flightControllerState.getVelocityX());
                        flightControllerBean.setVelocityY(flightControllerState.getVelocityY());
                        flightControllerBean.setVelocityZ(flightControllerState.getVelocityZ());
                        flightControllerBean.setFlightTimeInSeconds(flightControllerState.getFlightTimeInSeconds());
                        flightControllerBean.setSatelliteCount(flightControllerState.getSatelliteCount());
                        flightControllerBean.setIMUPreheating(flightControllerState.isIMUPreheating());
                        flightControllerBean.setUltrasonicBeingUsed(flightControllerState.isUltrasonicBeingUsed());
                        flightControllerBean.setUltrasonicHeightInMeters(flightControllerState.getUltrasonicHeightInMeters());
                        flightControllerBean.setVisionPositioningSensorBeingUsed(flightControllerState.isVisionPositioningSensorBeingUsed());
                        flightControllerBean.setLowerThanBatteryWarningThreshold(flightControllerState.isLowerThanBatteryWarningThreshold());
                        flightControllerBean.setLowerThanSeriousBatteryWarningThreshold(flightControllerState.isLowerThanSeriousBatteryWarningThreshold());
                        flightControllerBean.setFlightCount(flightControllerState.getFlightCount());
                        if((flightControllerState.getHomeLocation().getLatitude()+"").equals("NaN")){
                            flightControllerBean.setHomeLocationLatitude(0);
                        }else{
                            flightControllerBean.setHomeLocationLatitude(flightControllerState.getHomeLocation().getLatitude());
                        }
                        if((flightControllerState.getHomeLocation().getLongitude()+"").equals("NaN")){
                            flightControllerBean.setHomeLocationLongitude(0);
                        }else{
                            flightControllerBean.setHomeLocationLongitude(flightControllerState.getHomeLocation().getLongitude());
                        }


                        flightControllerBean.setGoHomeHeight(flightControllerState.getGoHomeHeight());
                        int GPS_SignalLevel = 255;
                        switch (flightControllerState.getGPSSignalLevel()) {
                            case LEVEL_0:
                                GPS_SignalLevel = 0;
                                break;
                            case LEVEL_1:
                                GPS_SignalLevel = 1;
                                break;
                            case LEVEL_2:
                                GPS_SignalLevel = 2;
                                break;
                            case LEVEL_3:
                                GPS_SignalLevel = 3;
                                break;
                            case LEVEL_4:
                                GPS_SignalLevel = 4;
                                break;
                            case LEVEL_5:
                                GPS_SignalLevel = 5;
                                break;
                            case LEVEL_6:
                                GPS_SignalLevel = 6;
                                break;
                            case LEVEL_7:
                                GPS_SignalLevel = 7;
                                break;
                            case LEVEL_8:
                                GPS_SignalLevel = 8;
                                break;
                            case LEVEL_9:
                                GPS_SignalLevel = 9;
                                break;
                            case LEVEL_10:
                                GPS_SignalLevel = 10;
                                break;
                            case NONE:
                                GPS_SignalLevel = 255;
                                break;
                        }
                        flightControllerBean.setGPSSignalLevel(GPS_SignalLevel);
                        int orientationMode = 255;
                        switch (flightControllerState.getOrientationMode()) {
                            case HOME_LOCK:
                                orientationMode = 2;
                                break;
                            case COURSE_LOCK:
                                orientationMode = 1;
                                break;
                            case AIRCRAFT_HEADING:
                                orientationMode = 255;
                                break;
                        }
                        flightControllerBean.setOrientationMode(orientationMode);
                        int flightWindWarning = 255;
                        switch (flightControllerState.getFlightWindWarning()) {
                            case LEVEL_0:
                                flightWindWarning = 0;
                                break;
                            case LEVEL_1:
                                flightWindWarning = 1;
                                break;
                            case LEVEL_2:
                                flightWindWarning = 2;
                                break;
                            case UNKNOWN:
                                flightWindWarning = 255;
                                break;
                        }
                        flightControllerBean.setFlightWindWarning(flightWindWarning);

                        int batteryThresholdBehavior = 255;
                        switch (flightControllerState.getBatteryThresholdBehavior()) {
                            case GO_HOME:
                                batteryThresholdBehavior = 1;
                                break;
                            case FLY_NORMALLY:
                                batteryThresholdBehavior = 0;
                                break;
                            case LAND_IMMEDIATELY:
                                batteryThresholdBehavior = 2;
                                break;
                            case UNKNOWN:
                                batteryThresholdBehavior = 255;
                                break;
                        }
                        flightControllerBean.setBatteryThresholdBehavior(batteryThresholdBehavior);
                        if (communication_flightController == null) {
                            communication_flightController = new Communication();
                        }
                        communication_flightController.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                        communication_flightController.setEquipmentId(Constant.APRONID);
                        communication_flightController.setMethod(RsaUtil.encrypt("flightController"));
                        communication_flightController.setResult(gson.toJson(flightControllerBean,FlightControllerBean.class));
//                        Log.d("MMMMM",communication_flightController.toString());
                        NettyClient.getInstance().sendMessage(communication_flightController, null);

                }
            });
            //电量
            FPVDemoApplication.getProductInstance().getBattery().setStateCallback(new BatteryState.Callback() {
                @Override
                public void onUpdate(BatteryState batteryState) {
                        if (batteryStateBean == null) {
                            batteryStateBean = new BatteryStateBean();
                        }
                        batteryStateBean.setFullChargeCapacity(batteryState.getFullChargeCapacity());
                        batteryStateBean.setChargeRemaining(batteryState.getChargeRemaining());
                        batteryStateBean.setChargeRemainingInPercent(batteryState.getChargeRemainingInPercent());
                        batteryStateBean.setDesignCapacity(batteryState.getDesignCapacity());
                        batteryStateBean.setVoltage(batteryState.getVoltage());
                        batteryStateBean.setCurrent(batteryState.getCurrent());
                        batteryStateBean.setLifetimeRemaining(batteryState.getLifetimeRemaining());
                        batteryStateBean.setTemperature(batteryState.getTemperature());
                        batteryStateBean.setNumberOfDischarges(batteryState.getNumberOfDischarges());

                        if (communication_battery == null) {
                            communication_battery = new Communication();
                        }
                        communication_battery.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                        communication_battery.setEquipmentId(Constant.APRONID);
                        communication_battery.setMethod(RsaUtil.encrypt("battery"));
                        communication_battery.setResult(gson.toJson(batteryStateBean,BatteryStateBean.class));
//                        Log.d("NNNNN",communication_battery.toString());
                        NettyClient.getInstance().sendMessage(communication_battery, null);
                }
            });

            FlightAssistant mFlightAssistant = mFlightController.getFlightAssistant();
            mFlightAssistant.setVisionDetectionStateUpdatedCallback(new VisionDetectionState.Callback() {
//                视觉系统可以以60度水平视场（FOV）和55度垂直FOV看到飞机前方。水平视场分为四个相等的扇区，此类给出了一个扇区的距离和警告级别。
                @Override
                public void onUpdate(VisionDetectionState visionDetectionState) {
//                    double ObstacleDistanceInMeters= visionDetectionState.getObstacleDistanceInMeters();
                    ObstacleDetectionSector[] mArray=visionDetectionState.getDetectionSectors();
//                    Log.d("MMMMM","ObstacleDistanceInMeters="+ObstacleDistanceInMeters);
                    String aa="";
                    Log.d("MMMMM","=======================");
                    for (int i = 0; i <mArray.length ; i++) {
//                        检测到的飞机障碍物距离，以米为单位。

                        aa="ObstacleDistanceInMeters"+i+"="+mArray[i].getObstacleDistanceInMeters()+"\nWarningLevel="+mArray[i].getWarningLevel();
//                        基于距离的警告级别。
                        Log.d("MMMMM",aa);
                    }
                    Log.d("MMMMM","=======================");

                }
            });
//            mFlightAssistant.setVisionControlStateUpdatedcallback(new VisionControlState.Callback() {
//                @Override
//                public void onUpdate(VisionControlState visionControlState) {
//                    isBraking true 飞机是否自动刹车以避免碰撞。
//                    isAvoidingActiveObstacleCollision true 如果飞机避免避开障碍物撞向飞机。
//                    isAscentLimitedByObstacle 如果飞机由于在其上方1m之内检测到障碍物而无法进一步上升，则为“是”。
//                    isPerformingPrecisionLanding 如果飞机正在精确着陆，则为是
//                    landingProtectionState 获取飞机的着陆保护状态。启用着陆保护后，此状态有效。
//                }
//            });
//            设置避障传感器状态侦听器。这个是判断是否启用的 没啥用
//            mFlightAssistant.setObstacleAvoidanceSensorStateListener(new CommonCallbacks.CompletionCallbackWith<ObstacleAvoidanceSensorState>() {
//                @Override
//                public void onSuccess(ObstacleAvoidanceSensorState obstacleAvoidanceSensorState) {
//                }
//
//                @Override
//                public void onFailure(DJIError djiError) {
//
//                }
//            });
//            启用避免碰撞。启用后，飞机将停止并尝试绕过检测到的障碍物。
//            mFlightAssistant.setCollisionAvoidanceEnabled(true, new CommonCallbacks.CompletionCallback() {
//                @Override
//                public void onResult(DJIError djiError) {
//
//                }
//            });
//            启用/禁用向上避免。当Inspire 2的朝上的红外传感器检测到障碍物时，飞机将放慢其上升速度并与障碍物保持至少1米的距离。
//            传感器具有10度水平视场（FOV）和10度垂直视场。最大检测距离为5m。
            //当然还有向下 仅Matrice 300 RTK支持。
//            mFlightAssistant.setUpwardAvoidanceEnabled(true, new CommonCallbacks.CompletionCallback() {
//                @Override
//                public void onResult(DJIError djiError) {
//
//                }
//            });
//            启用/禁用着陆保护。在自动着陆期间，面向下的视觉传感器将检查地面是否足够平坦以安全着陆。
//            如果不是这样，并且启用了降落保护，则降落将中止，需要由用户手动执行。
//            mFlightAssistant.setLandingProtectionEnabled(true, new CommonCallbacks.CompletionCallback() {
//                @Override
//                public void onResult(DJIError djiError) {
//
//                }
//            });
//            启用/禁用精确着陆。启用后，飞机将以可视方式（以及GPS）记录其起飞位置。在返乡行动中，飞机将尝试使用其他视觉信息进行精确着陆。
//            仅当起飞期间成功记录了家乡位置并且在飞行过程中未更改家乡位置时，此方法才适用于“返乡”操作。
//            mFlightAssistant.setPrecisionLandingEnabled(true, new CommonCallbacks.CompletionCallback() {
//                @Override
//                public void onResult(DJIError djiError) {
//
//                }
//            });
//            启用视觉定位。视觉定位用于增强GPS，以在悬停时提高定位精度并在飞行时提高速度计算。
//            mFlightAssistant.setVisionAssistedPositioningEnabled(true, new CommonCallbacks.CompletionCallback() {
//                @Override
//                public void onResult(DJIError djiError) {
//
//                }
//            });
//            设置接合主动制动器的距离，以避开障碍物
//            感知方向。如果为DJIFlightController_DJIFlightAssistantObstacleSensingDirection_Downward，则距离范围为[0.1，3]。
//            如果为DJIFlightController_DJIFlightAssistantObstacleSensingDirection_Upward，则距离范围为[1.0，10]。
//            如果为DJIFlightController_DJIFlightAssistantObstacleSensingDirection_Horizontal，则距离范围为[1.0，5]。
//            mFlightAssistant.setObstaclesAvoidanceDistance(3, PerceptionInformation.DJIFlightAssistantObstacleSensingDirection.Downward, new CommonCallbacks.CompletionCallback() {
//                @Override
//                public void onResult(DJIError djiError) {
//
//                }
//            });
//            设置可以测量的最大感知距离。仅Matrice 300 RTK支持。各个方向的距离范围是5m〜45m。
//            向上	向上感应。
//            向下	向下感应。
//            卧式	水平场。
//            mFlightAssistant.setMaxPerceptionDistance(45, PerceptionInformation.DJIFlightAssistantObstacleSensingDirection.Downward, new CommonCallbacks.CompletionCallback() {
//                @Override
//                public void onResult(DJIError djiError) {
//
//                }
//            });

            //获取感知试图
//            mFlightAssistant.setVisualPerceptionInformationCallback(new CommonCallbacks.CompletionCallbackWith<PerceptionInformation>() {
//                @Override
//                public void onSuccess(PerceptionInformation perceptionInformation) {
//                    String sss=perceptionInformation.getDistances().toString();
//                    Log.d("NNNNN","getDistances="+sss);
//                    Log.d("NNNNN","getDataPackageIndex="+perceptionInformation.getDataPackageIndex());
//                    Log.d("NNNNN","getAngleInterval="+perceptionInformation.getAngleInterval());
//                    Log.d("NNNNN","getUpwardObstacleDistance="+perceptionInformation.getUpwardObstacleDistance());
//                    Log.d("NNNNN","getDownwardObstacleDistance="+perceptionInformation.getDownwardObstacleDistance());
//                }
//
//                @Override
//                public void onFailure(DJIError djiError) {
//
//                }
//            });

        }
    }

    private void initCamera() {
//        camera = FPVDemoApplication.getCameraInstance();
//        List<Lens> lens=camera.getLenses();
//        int lensIndex = CommonUtil.getLensIndex(0,SettingsDefinitions.LensType.ZOOM);
//        KeyManager.getInstance().getValue(CameraKey.createLensKey(CameraKey.HYBRID_ZOOM_SPEC, 0, lensIndex), new GetCallback() {
//            @Override
//            public void onSuccess(Object o) {
//                Log.d(TAG,o.toString());
//            }
//
//            @Override
//            public void onFailure(DJIError djiError) {
//                Log.d(TAG,djiError.toString());
//
//            }
//        });
    }

    //长连接最终返回
    @Override
    protected void notifyData(String json) {
        Log.d(TAG, "张闯返回数据=" + json);
        if (TextUtils.isEmpty(json)) {
            return;
        }
        Communication communication = gson.fromJson(json, Communication.class);
        switch (RsaUtil.decrypt(communication.getMethod())) {
            //起飞
            case Constant.START_TAKE_OFF:
                startTakeoff(communication);
                break;
            //获取控制权限
            case Constant.ENABLE_VIRTUAL_STICK:
                enable_virtual_stick(communication);
                break;
            //取消控制权限
            case Constant.DISABLE_VIRTUAL_STICK:
                disable_virtual_stick(communication);
                break;
            //降落
            case Constant.START_LANDING:
                startLanding(communication);
                break;
            //摄像头左右
            case Constant.CAMERA_LEFT_AND_RIGHT:
                camera_left_and_right(communication);
                break;
            //摄像头上下
            case Constant.CAMERA_UP_AND_DOWN:
                camera_up_and_down(communication);
                break;
            //上升下降
            case Constant.RISE_AND_FALL:
                fly_rise_and_fall(communication);
                break;
            //向左向右自转
            case Constant.TURN_LEFT_AND_TURN_RIGHT:
                turn_left_and_turn_right(communication);
                break;
            //前进后退
            case Constant.FLY_FORWARD_AND_BACK:
                fly_forward_and_back(communication);
                break;
            //往左往右
            case Constant.FLY_LEFT_AND_RIGHT:
                fly_left_and_right(communication);
                break;
            //航点规划
            case Constant.WAYPOINT_PLAN:
                waypoint_plan(communication);
                break;
            //航线自动飞行开始
            case Constant.WAYPOINT_FLY_START:
                startWaypointMission(communication);
                break;
            //航线自动飞行停止
            case Constant.WAYPOINT_FLY_STOP:
                stopWaypointMission(communication);
                break;
        }


    }

    //起飞
    private void startTakeoff(Communication communication) {
        if (mFlightController != null) {
            mFlightController.startTakeoff(
                    new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (djiError != null) {
//                                showToast(djiError.getDescription());
                                communication.setResult(djiError.getDescription());
                                communication.setCode(-1);
                                communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                                NettyClient.getInstance().sendMessage(communication, null);
                            } else {
//                                showToast("Take off Success");
                                communication.setResult("Success");
                                communication.setCode(200);
                                communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                                NettyClient.getInstance().sendMessage(communication, null);
                            }
                        }
                    }
            );
        }
    }

    //获取sdk控制无人机权限
    private void enable_virtual_stick(Communication communication) {
        if (mFlightController != null) {
            mFlightController.setVirtualStickModeEnabled(true, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError != null) {
//                        showToast(djiError.getDescription());
                        communication.setResult(djiError.getDescription());
                        communication.setCode(-1);
                        communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                        NettyClient.getInstance().sendMessage(communication, null);
                    } else {
//                        showToast("Enable Virtual Stick Success");
                        communication.setResult("Success");
                        communication.setCode(200);
                        communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                        NettyClient.getInstance().sendMessage(communication, null);
                    }
                }
            });


        }
    }

    //取消sdk控制无人机权限
    private void disable_virtual_stick(Communication communication) {
        if (mFlightController != null) {
            mFlightController.setVirtualStickModeEnabled(false, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError != null) {
//                        showToast(djiError.getDescription());
                        communication.setResult(djiError.getDescription());
                        communication.setCode(-1);
                        communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                        NettyClient.getInstance().sendMessage(communication, null);
                    } else {
//                        showToast("Disable Virtual Stick Success");
                        communication.setResult("Success");
                        communication.setCode(200);
                        communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                        NettyClient.getInstance().sendMessage(communication, null);
                    }
                }
            });
        }
    }

    //降落
    private void startLanding(Communication communication) {
        if (mFlightController != null) {
            mFlightController.getState().setIslandingConfirmationNeeded(false);
            mFlightController.startLanding(
                    new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (djiError != null) {
//                                showToast(djiError.getDescription());
                                communication.setResult(djiError.getDescription());
                                communication.setCode(-1);
                                communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                                NettyClient.getInstance().sendMessage(communication, null);
                            } else {
//                                showToast("Start Landing");
                                communication.setResult("Success");
                                communication.setCode(200);
                                communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                                NettyClient.getInstance().sendMessage(communication, null);
                            }
                        }
                    }
            );
        }
    }

    //摄像头上下 -30~90
    private void camera_up_and_down(Communication communication) {
        String angle = communication.getPara().get(Constant.ANGLE);
        if (!TextUtils.isEmpty(angle)) {
            Rotation.Builder builder = new Rotation.Builder().mode(RotationMode.ABSOLUTE_ANGLE).time(2);
            builder.pitch(Float.parseFloat(angle));
            builder.build();
            Gimbal gimbal = FPVDemoApplication.getProductInstance().getGimbal();
            if (gimbal == null) {
                return;
            }
            gimbal.rotate(builder.build(), new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError != null) {
                        communication.setResult(djiError.getDescription());
                        communication.setCode(-1);
                        communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                        NettyClient.getInstance().sendMessage(communication, null);
                    } else {
                        communication.setResult("Success");
                        communication.setCode(200);
                        communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                        NettyClient.getInstance().sendMessage(communication, null);
                    }
                }
            });
        } else {
            communication.setResult("");
            communication.setCode(-1);
            communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            NettyClient.getInstance().sendMessage(communication, null);
        }

    }

    //摄像头左右 -15~15
    private void camera_left_and_right(Communication communication) {
        String angle = communication.getPara().get(Constant.ANGLE);
        if (!TextUtils.isEmpty(angle)) {
            Rotation.Builder builder = new Rotation.Builder().mode(RotationMode.ABSOLUTE_ANGLE).time(2);
            builder.yaw(Float.parseFloat(angle));
            builder.build();
            Gimbal gimbal = FPVDemoApplication.getProductInstance().getGimbal();
            if (gimbal == null) {
                return;
            }
            gimbal.rotate(builder.build(), new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError != null) {
                        communication.setResult(djiError.getDescription());
                        communication.setCode(-1);
                        communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                        NettyClient.getInstance().sendMessage(communication, null);
                    } else {
                        communication.setResult("Success");
                        communication.setCode(200);
                        communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                        NettyClient.getInstance().sendMessage(communication, null);
                    }
                }
            });
        } else {
            communication.setResult("");
            communication.setCode(-1);
            communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            NettyClient.getInstance().sendMessage(communication, null);
        }
    }

    //这四个 视频相关
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.e(TAG, "onSurfaceTextureAvailable");
        if (mCodecManager == null) {
            mCodecManager = new DJICodecManager(this, surface, width, height);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.e(TAG, "onSurfaceTextureSizeChanged");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.e(TAG, "onSurfaceTextureDestroyed");
        if (mCodecManager != null) {
            mCodecManager.cleanSurface();
            mCodecManager = null;
        }

        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    class SendVirtualStickDataTask extends TimerTask {
        @Override
        public void run() {
            if (mFlightController != null) {
                mFlightController.sendVirtualStickFlightControlData(
                        new FlightControlData(
                                mPitch, mRoll, mYaw, mThrottle
                        ), new CommonCallbacks.CompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
                                if (djiError != null) {
                                    mCommunication.setResult(djiError.getDescription());
                                    mCommunication.setCode(-1);
                                    mCommunication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                                    NettyClient.getInstance().sendMessage(mCommunication, null);
                                } else {
                                    mCommunication.setResult("Success");
                                    mCommunication.setCode(200);
                                    mCommunication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                                    NettyClient.getInstance().sendMessage(mCommunication, null);
                                }
                            }
                        }
                );
            }
        }
    }

    Communication mCommunication;

    //飞机前进后退 -10~10
    private void fly_forward_and_back(Communication communication) {
        String speed = communication.getPara().get(Constant.SPEED);
        if (!TextUtils.isEmpty(speed)) {
            mRoll = Float.parseFloat(speed);
            mPitch = 0;
            mYaw = 0;
            mThrottle = 0;
            mCommunication = communication;

            if (null == mSendVirtualStickDataTimer) {
//                mSendVirtualStickDataTask = new SendVirtualStickDataTask();
//                mSendVirtualStickDataTimer = new Timer();
//                mSendVirtualStickDataTimer.schedule(mSendVirtualStickDataTask, 100, 200);
                if (mFlightController != null) {
                    mFlightController.sendVirtualStickFlightControlData(
                            new FlightControlData(
                                    mPitch, mRoll, mYaw, mThrottle
                            ), new CommonCallbacks.CompletionCallback() {
                                @Override
                                public void onResult(DJIError djiError) {
                                    if (djiError != null) {
                                        mCommunication.setResult(djiError.getDescription());
                                        mCommunication.setCode(-1);
                                        mCommunication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                                        NettyClient.getInstance().sendMessage(mCommunication, null);
                                    } else {
                                        mCommunication.setResult("Success");
                                        mCommunication.setCode(200);
                                        mCommunication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                                        NettyClient.getInstance().sendMessage(mCommunication, null);
                                    }
                                }
                            }
                    );
                }
            }
        } else {
            communication.setResult("");
            communication.setCode(-1);
            communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            NettyClient.getInstance().sendMessage(communication, null);
        }


    }

    //飞机往左往右 -10~10
    private void fly_left_and_right(Communication communication) {
        String speed = communication.getPara().get(Constant.SPEED);
        if (!TextUtils.isEmpty(speed)) {
            mPitch = Float.parseFloat(speed);
            mRoll = 0;
            mYaw = 0;
            mThrottle = 0;
            mCommunication = communication;

//            if (null == mSendVirtualStickDataTimer) {
//                mSendVirtualStickDataTask = new SendVirtualStickDataTask();
//                mSendVirtualStickDataTimer = new Timer();
//                mSendVirtualStickDataTimer.schedule(mSendVirtualStickDataTask, 100, 200);
//            }

            if (mFlightController != null) {
                mFlightController.sendVirtualStickFlightControlData(
                        new FlightControlData(
                                mPitch, mRoll, mYaw, mThrottle
                        ), new CommonCallbacks.CompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
                                if (djiError != null) {
                                    mCommunication.setResult(djiError.getDescription());
                                    mCommunication.setCode(-1);
                                    mCommunication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                                    NettyClient.getInstance().sendMessage(mCommunication, null);
                                } else {
                                    mCommunication.setResult("Success");
                                    mCommunication.setCode(200);
                                    mCommunication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                                    NettyClient.getInstance().sendMessage(mCommunication, null);
                                }
                            }
                        }
                );
            }
        } else {
            communication.setResult("");
            communication.setCode(-1);
            communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            NettyClient.getInstance().sendMessage(communication, null);
        }

    }

    //飞机上升下降-30~30
    private void fly_rise_and_fall(Communication communication) {
        Log.d(TAG, "communication=" + communication.toString());
        String speed = communication.getPara().get(Constant.SPEED);
        if (!TextUtils.isEmpty(speed)) {
            mThrottle = Float.parseFloat(speed);
            mPitch = 0;
            mRoll = 0;
            mYaw = 0;
            mCommunication = communication;

//            if (null == mSendVirtualStickDataTimer) {
//                mSendVirtualStickDataTask = new SendVirtualStickDataTask();
//                mSendVirtualStickDataTimer = new Timer();
//                mSendVirtualStickDataTimer.schedule(mSendVirtualStickDataTask, 0, 200);
//            }
            if (mFlightController != null) {
                mFlightController.sendVirtualStickFlightControlData(
                        new FlightControlData(
                                mPitch, mRoll, mYaw, mThrottle
                        ), new CommonCallbacks.CompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
                                if (djiError != null) {
                                    mCommunication.setResult(djiError.getDescription());
                                    mCommunication.setCode(-1);
                                    mCommunication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                                    NettyClient.getInstance().sendMessage(mCommunication, null);
                                } else {
                                    mCommunication.setResult("Success");
                                    mCommunication.setCode(200);
                                    mCommunication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                                    NettyClient.getInstance().sendMessage(mCommunication, null);
                                }
                            }
                        }
                );
            }
        } else {
            communication.setResult("");
            communication.setCode(-1);
            communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            NettyClient.getInstance().sendMessage(communication, null);
        }


    }

    //飞机自己左转右转 -2~2
    private void turn_left_and_turn_right(Communication communication) {
        String speed = communication.getPara().get(Constant.SPEED);
        if (!TextUtils.isEmpty(speed)) {
            mYaw = Float.parseFloat(speed);
            mThrottle = 0;
            mPitch = 0;
            mRoll = 0;
            mCommunication = communication;
//            if (null == mSendVirtualStickDataTimer) {
////                mSendVirtualStickDataTask = new SendVirtualStickDataTask();
////                mSendVirtualStickDataTimer = new Timer();
////                mSendVirtualStickDataTimer.schedule(mSendVirtualStickDataTask, 0, 200);
////            }
            if (mFlightController != null) {
                mFlightController.sendVirtualStickFlightControlData(
                        new FlightControlData(
                                mPitch, mRoll, mYaw, mThrottle
                        ), new CommonCallbacks.CompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
                                if (djiError != null) {
                                    mCommunication.setResult(djiError.getDescription());
                                    mCommunication.setCode(-1);
                                    mCommunication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                                    NettyClient.getInstance().sendMessage(mCommunication, null);
                                } else {
                                    mCommunication.setResult("Success");
                                    mCommunication.setCode(200);
                                    mCommunication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                                    NettyClient.getInstance().sendMessage(mCommunication, null);
                                }
                            }
                        }
                );
            }
        } else {
            communication.setResult("");
            communication.setCode(-1);
            communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            NettyClient.getInstance().sendMessage(communication, null);
        }
    }

    //航点规划
    private void waypoint_plan(Communication communication) {
        String speed = communication.getPara().get(Constant.SPEED);
        if (!TextUtils.isEmpty(speed)) {
            mSpeed = Float.parseFloat(speed);
        }
        String finishedAction = communication.getPara().get(Constant.FINISHED_ACTION);
        if (!TextUtils.isEmpty(finishedAction)) {
            switch (finishedAction) {
                case "0":
                    mFinishedAction = WaypointMissionFinishedAction.NO_ACTION;
                    break;
                case "2":
                    mFinishedAction = WaypointMissionFinishedAction.AUTO_LAND;
                    break;
                case "3":
                    mFinishedAction = WaypointMissionFinishedAction.GO_FIRST_WAYPOINT;
                    break;
                case "4":
                    mFinishedAction = WaypointMissionFinishedAction.CONTINUE_UNTIL_END;
                    break;
                case "1":
                default:
                    mFinishedAction = WaypointMissionFinishedAction.GO_HOME;
                    break;

            }
        }

        String headingMode = communication.getPara().get(Constant.HEADING_MODE);
        if (TextUtils.isEmpty(headingMode)) {
            switch (headingMode) {
                case "1":
                    mHeadingMode = WaypointMissionHeadingMode.USING_INITIAL_DIRECTION;
                    break;
                case "2":
                    mHeadingMode = WaypointMissionHeadingMode.CONTROL_BY_REMOTE_CONTROLLER;
                    break;
                case "3":
                    mHeadingMode = WaypointMissionHeadingMode.USING_WAYPOINT_HEADING;
                    break;
                case "4":
                    mHeadingMode = WaypointMissionHeadingMode.TOWARD_POINT_OF_INTEREST;
                    break;
                case "0":
                default:
                    mHeadingMode = WaypointMissionHeadingMode.AUTO;
                    break;
            }
        }


//        mSpeed = 10.0f;
//            AUTO_LAND 飞机将在最后一个航点自动降落。
//            CONTINUE_UNTIL_END 如果用户在执行任务时尝试将飞机拉回飞行路线，则飞机将朝着先前的航路点移动并将继续这样做，直到没有更多的航路点返回或用户停止尝试移回飞机。
//            GO_FIRST_WAYPOINT 飞机将返回其第一个航点并悬停就位。
//            GO_HOME 完成任务后，飞机将返回家中。
//            NO_ACTION 任务完成将不会采取进一步的行动
//        mFinishedAction = WaypointMissionFinishedAction.GO_HOME;
//            AUTO 飞机的航向将始终在飞行方向上。
//            CONTROL_BY_REMOTE_CONTROLLER 飞机的航向将由遥控器控制。
//            TOWARD_POINT_OF_INTEREST 飞机的航向将始终朝着兴趣点前进。
//            USING_INITIAL_DIRECTION 飞机的航向将设置为初始起飞航向。
//            USING_WAYPOINT_HEADING 在航点之间旅行时，飞机的航向将设置为上一个航点的航向。
//        mHeadingMode = WaypointMissionHeadingMode.AUTO;
//        altitude = 0;

        // add点
        String wayPoints = communication.getPara().get(Constant.WAY_POINTS);
//            List<WayPointsBean> myWayPointList=new ArrayList<>();
        if (!TextUtils.isEmpty(wayPoints)) {
            List<WayPointsBean> myWayPointList = gson.fromJson(wayPoints, new TypeToken<List<WayPointsBean>>() {
            }.getType());
            waypointList.clear();
            for (int i = 0; i < myWayPointList.size(); i++) {
                add_point(myWayPointList.get(i));
            }
        }


        if (waypointMissionBuilder == null) {
            waypointMissionBuilder = new WaypointMission.Builder().finishedAction(mFinishedAction)
                    .headingMode(mHeadingMode)
                    .autoFlightSpeed(mSpeed)
                    .maxFlightSpeed(mSpeed)
                    .flightPathMode(WaypointMissionFlightPathMode.NORMAL);
        } else {
            waypointMissionBuilder.finishedAction(mFinishedAction)
                    .headingMode(mHeadingMode)
                    .autoFlightSpeed(mSpeed)
                    .maxFlightSpeed(mSpeed)
                    .flightPathMode(WaypointMissionFlightPathMode.NORMAL);

        }
//        if (waypointMissionBuilder.getWaypointList().size() > 0) {
//            for (int i = 0; i < waypointMissionBuilder.getWaypointList().size(); i++) {
//                waypointMissionBuilder.getWaypointList().get(i).altitude = altitude;
//            }
////            setResultToToast("Set Waypoint attitude successfully");
//        }
//        将其waypointMissionBuilder.build()作为参数传递给操作员加载航路点任务
        DJIError error = getWaypointMissionOperator().loadMission(waypointMissionBuilder.build());
        if (error == null) {
//            setResultToToast("loadWaypoint succeeded");
            //成功之后upload
            getWaypointMissionOperator().uploadMission(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError error) {
                    if (error == null) {
//                        setResultToToast("Mission upload successfully!");
                        communication.setResult("Success");
                        communication.setCode(200);
                        communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                        NettyClient.getInstance().sendMessage(communication, null);
                    } else {
//                        setResultToToast("Mission upload failed, error: " + error.getDescription() + " retrying...");
                        getWaypointMissionOperator().retryUploadMission(null);
                        communication.setResult(error.getDescription());
                        communication.setCode(-1);
                        communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                        NettyClient.getInstance().sendMessage(communication, null);
                    }
                }
            });


        } else {
//            setResultToToast("loadWaypoint failed " + error.getDescription());
            communication.setResult(error.getDescription());
            communication.setCode(-1);
            communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            NettyClient.getInstance().sendMessage(communication, null);
        }
    }

    public WaypointMissionOperator getWaypointMissionOperator() {
        if (instance == null) {
            instance = DJISDKManager.getInstance().getMissionControl().getWaypointMissionOperator();
        }
        return instance;
    }

    //开始航点自动飞行
    private void startWaypointMission(Communication communication) {

        getWaypointMissionOperator().startMission(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError error) {
                if (error != null) {
                    communication.setResult(error.getDescription());
                    communication.setCode(-1);
                    communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                    NettyClient.getInstance().sendMessage(communication, null);
                } else {
                    communication.setResult("Success");
                    communication.setCode(200);
                    communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                    NettyClient.getInstance().sendMessage(communication, null);
                }
//                setResultToToast("Mission Start: " + (error == null ? "Successfully" : error.getDescription()));
            }
        });

    }

    //停止航点自动飞行
    private void stopWaypointMission(Communication communication) {
        getWaypointMissionOperator().stopMission(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError error) {
//                setResultToToast("Mission Stop: " + (error == null ? "Successfully" : error.getDescription()));
                if (error != null) {
                    communication.setResult(error.getDescription());
                    communication.setCode(-1);
                    communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                    NettyClient.getInstance().sendMessage(communication, null);
                } else {
                    communication.setResult("Success");
                    communication.setCode(200);
                    communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                    NettyClient.getInstance().sendMessage(communication, null);
                }
            }
        });
    }

    private void add_point(WayPointsBean wayPointsBean) {
        Waypoint mWaypoint = new Waypoint(Double.parseDouble(wayPointsBean.getLatitude()), Double.parseDouble(wayPointsBean.getLongitude()), Float.parseFloat(wayPointsBean.getAltitude()));
        mWaypoint.speed = Float.parseFloat(wayPointsBean.getSpeed());
        for (int i = 0; i < wayPointsBean.getWayPointAction().size(); i++) {
            switch (wayPointsBean.getWayPointAction().get(i).getActionType()) {
                case 0:
                    mWaypoint.addAction(new WaypointAction(WaypointActionType.STAY, wayPointsBean.getWayPointAction().get(i).getActionParam()));
                    break;
                case 1:
                    mWaypoint.addAction(new WaypointAction(WaypointActionType.START_TAKE_PHOTO, 0));
                    break;
                case 2:
                    mWaypoint.addAction(new WaypointAction(WaypointActionType.START_RECORD, 0));
                    break;
                case 3:
                    mWaypoint.addAction(new WaypointAction(WaypointActionType.STOP_RECORD, 1));
                    break;
                case 4:
                    mWaypoint.addAction(new WaypointAction(WaypointActionType.ROTATE_AIRCRAFT, wayPointsBean.getWayPointAction().get(i).getActionParam()));
                    break;
                case 5:
                    mWaypoint.addAction(new WaypointAction(WaypointActionType.GIMBAL_PITCH, wayPointsBean.getWayPointAction().get(i).getActionParam()));
                    break;
            }
        }
//            添加一个航点，并实现uploadWayPointMission()将任务上传到操作员的方法
        if (waypointMissionBuilder != null) {
            waypointList.add(mWaypoint);
            waypointMissionBuilder.waypointList(waypointList).waypointCount(waypointList.size());
        } else {
            waypointMissionBuilder = new WaypointMission.Builder();
            waypointList.add(mWaypoint);
            waypointMissionBuilder.waypointList(waypointList).waypointCount(waypointList.size());
        }
    }

    private void addListener() {
        if (getWaypointMissionOperator() != null) {
            getWaypointMissionOperator().addListener(eventNotificationListener);
        }
    }

    private void removeListener() {
        if (getWaypointMissionOperator() != null) {
            getWaypointMissionOperator().removeListener(eventNotificationListener);
        }
    }

    //航线规划监听
    private WaypointMissionOperatorListener eventNotificationListener = new WaypointMissionOperatorListener() {
        @Override
        public void onDownloadUpdate(WaypointMissionDownloadEvent downloadEvent) {
        }

        @Override
        public void onUploadUpdate(WaypointMissionUploadEvent uploadEvent) {
        }

        @Override
        public void onExecutionUpdate(WaypointMissionExecutionEvent executionEvent) {

        }

        @Override
        public void onExecutionStart() {

        }

        //        显示一条消息，以在任务执行完成时通知用户。
        @Override
        public void onExecutionFinish(@Nullable final DJIError error) {
            showToast("Execution finished: " + (error == null ? "Success!" : error.getDescription()));
        }
    };
}
