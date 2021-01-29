package com.compass.ux.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import dji.common.airlink.FrequencyInterference;
import dji.common.airlink.OcuSyncBandwidth;
import dji.common.airlink.OcuSyncFrequencyBand;
import dji.common.airlink.PhysicalSource;
import dji.common.airlink.SignalQualityCallback;
import dji.common.airlink.WifiChannelInterference;
import dji.common.battery.AggregationState;
import dji.common.battery.BatteryOverview;
import dji.common.battery.BatteryState;
import dji.common.camera.CameraVideoStreamSource;
import dji.common.camera.LaserMeasureInformation;
import dji.common.camera.PhotoTimeLapseSettings;
import dji.common.camera.SettingsDefinitions;
import dji.common.camera.StorageState;
import dji.common.camera.WatermarkSettings;
import dji.common.error.DJIError;
import dji.common.error.DJISDKError;
import dji.common.error.DJIWaypointV2Error;
import dji.common.flightcontroller.ConnectionFailSafeBehavior;
import dji.common.flightcontroller.FlightControllerState;
import dji.common.flightcontroller.GravityCenterState;
import dji.common.flightcontroller.LEDsSettings;
import dji.common.flightcontroller.ObstacleDetectionSector;
import dji.common.flightcontroller.RTKState;
import dji.common.flightcontroller.VisionDetectionState;
import dji.common.flightcontroller.WindDirection;
import dji.common.flightcontroller.flightassistant.ObstacleAvoidanceSensorState;
import dji.common.flightcontroller.flightassistant.PerceptionInformation;
import dji.common.flightcontroller.rtk.CoordinateSystem;
import dji.common.flightcontroller.rtk.NetworkServicePlansState;
import dji.common.flightcontroller.rtk.NetworkServiceSettings;
import dji.common.flightcontroller.rtk.NetworkServiceState;
import dji.common.flightcontroller.rtk.RTKBaseStationInformation;
import dji.common.flightcontroller.rtk.ReferenceStationSource;
import dji.common.flightcontroller.virtualstick.FlightControlData;
import dji.common.flightcontroller.virtualstick.FlightCoordinateSystem;
import dji.common.flightcontroller.virtualstick.RollPitchControlMode;
import dji.common.flightcontroller.virtualstick.VerticalControlMode;
import dji.common.flightcontroller.virtualstick.YawControlMode;
import dji.common.gimbal.Axis;
import dji.common.gimbal.GimbalMode;
import dji.common.gimbal.GimbalState;
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
import dji.common.mission.waypoint.WaypointTurnMode;
import dji.common.mission.waypointv2.Action.ActionDownloadEvent;
import dji.common.mission.waypointv2.Action.ActionExecutionEvent;
import dji.common.mission.waypointv2.Action.ActionState;
import dji.common.mission.waypointv2.Action.ActionTypes;
import dji.common.mission.waypointv2.Action.ActionUploadEvent;
import dji.common.mission.waypointv2.Action.WaypointActuator;
import dji.common.mission.waypointv2.Action.WaypointAircraftControlParam;
import dji.common.mission.waypointv2.Action.WaypointAircraftControlRotateYawParam;
import dji.common.mission.waypointv2.Action.WaypointAircraftControlStartStopFlyParam;
import dji.common.mission.waypointv2.Action.WaypointCameraActuatorParam;
import dji.common.mission.waypointv2.Action.WaypointCameraCustomNameParam;
import dji.common.mission.waypointv2.Action.WaypointGimbalActuatorParam;
import dji.common.mission.waypointv2.Action.WaypointIntervalTriggerParam;
import dji.common.mission.waypointv2.Action.WaypointReachPointTriggerParam;
import dji.common.mission.waypointv2.Action.WaypointTrajectoryTriggerParam;
import dji.common.mission.waypointv2.Action.WaypointTrigger;
import dji.common.mission.waypointv2.Action.WaypointV2Action;
import dji.common.mission.waypointv2.Action.WaypointV2AssociateTriggerParam;
import dji.common.mission.waypointv2.WaypointV2;
import dji.common.mission.waypointv2.WaypointV2Mission;
import dji.common.mission.waypointv2.WaypointV2MissionDownloadEvent;
import dji.common.mission.waypointv2.WaypointV2MissionExecutionEvent;
import dji.common.mission.waypointv2.WaypointV2MissionState;
import dji.common.mission.waypointv2.WaypointV2MissionTypes;
import dji.common.mission.waypointv2.WaypointV2MissionUploadEvent;
import dji.common.model.LocationCoordinate2D;
import dji.common.product.Model;
import dji.common.useraccount.UserAccountState;
import dji.common.util.CommonCallbacks;
import dji.keysdk.AirLinkKey;
import dji.keysdk.BatteryKey;
import dji.keysdk.DJIKey;
import dji.keysdk.DiagnosticsKey;
import dji.keysdk.FlightControllerKey;
import dji.keysdk.GimbalKey;
import dji.keysdk.KeyManager;
import dji.keysdk.PayloadKey;
import dji.keysdk.callback.ActionCallback;
import dji.keysdk.callback.GetCallback;
import dji.keysdk.callback.KeyListener;
import dji.keysdk.callback.SetCallback;
import dji.midware.data.manager.P3.DJIPayloadUsbDataManager;
import dji.sdk.airlink.OcuSyncLink;
import dji.sdk.airlink.WiFiLink;
import dji.sdk.base.BaseComponent;
import dji.sdk.base.BaseProduct;
import dji.sdk.base.DJIDiagnostics;
import dji.sdk.battery.Battery;
import dji.sdk.camera.Camera;
import dji.sdk.camera.VideoFeeder;
import dji.sdk.codec.DJICodecManager;
import dji.sdk.flightcontroller.FlightAssistant;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.flightcontroller.RTK;
import dji.sdk.gimbal.Gimbal;
import dji.sdk.mission.MissionControl;
import dji.sdk.mission.waypoint.WaypointMissionOperator;
import dji.sdk.mission.waypoint.WaypointMissionOperatorListener;
import dji.sdk.mission.waypoint.WaypointV2ActionListener;
import dji.sdk.mission.waypoint.WaypointV2MissionOperator;
import dji.sdk.mission.waypoint.WaypointV2MissionOperatorListener;
import dji.sdk.network.RTKNetworkServiceProvider;
import dji.sdk.payload.Payload;
import dji.sdk.products.Aircraft;
import dji.sdk.remotecontroller.RemoteController;
import dji.sdk.sdkmanager.DJISDKInitEvent;
import dji.sdk.sdkmanager.DJISDKManager;
import dji.sdk.useraccount.UserAccountManager;
import dji.thirdparty.afinal.core.AsyncTask;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.compass.ux.app.Constant;
import com.compass.ux.app.MApplication;
import com.compass.ux.R;
import com.compass.ux.bean.BatteryPersentAndVoltageBean;
import com.compass.ux.bean.FlightControllerBean;
import com.compass.ux.bean.LaserMeasureInformBean;
import com.compass.ux.bean.RTKBean;
import com.compass.ux.bean.SettingValueBean;
import com.compass.ux.bean.ObstacleAvoidanceSensorStateBean;
import com.compass.ux.bean.StorageStateBean;
import com.compass.ux.bean.StringsBean;
import com.compass.ux.bean.TransmissionSetBean;
import com.compass.ux.bean.WayPointsBean;
import com.compass.ux.bean.WayPointsV2Bean;
import com.compass.ux.bean.WebInitializationBean;
import com.compass.ux.netty_lib.NettyService;
import com.compass.ux.netty_lib.activity.NettyActivity;
import com.compass.ux.netty_lib.netty.NettyClient;
import com.compass.ux.netty_lib.zhang.Communication;
import com.compass.ux.netty_lib.zhang.RsaUtil;
import com.compass.ux.takephoto.FPVDemoApplication;
import com.compass.ux.utils.ClientUploadUtils;
import com.compass.ux.utils.DateTransformationUtils;
import com.compass.ux.utils.DeleteUtil;
import com.compass.ux.utils.LocationUtils;
import com.compass.ux.utils.MapConvertUtils;
import com.compass.ux.utils.fastClick;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static dji.common.camera.CameraVideoStreamSource.DEFAULT;
import static dji.common.camera.CameraVideoStreamSource.INFRARED_THERMAL;
import static dji.common.camera.CameraVideoStreamSource.WIDE;
import static dji.common.camera.CameraVideoStreamSource.ZOOM;
import static dji.common.camera.SettingsDefinitions.ExposureMode.MANUAL;
import static dji.common.camera.SettingsDefinitions.ExposureMode.PROGRAM;
import static dji.common.camera.SettingsDefinitions.PhotoAspectRatio.RATIO_16_9;
import static dji.common.camera.SettingsDefinitions.ThermalDigitalZoomFactor.UNKNOWN;
import static dji.common.camera.SettingsDefinitions.ThermalDigitalZoomFactor.X_1;
import static dji.common.camera.SettingsDefinitions.ThermalDigitalZoomFactor.X_2;
import static dji.common.camera.SettingsDefinitions.ThermalDigitalZoomFactor.X_4;
import static dji.common.camera.SettingsDefinitions.ThermalDigitalZoomFactor.X_8;
import static dji.common.flightcontroller.flightassistant.PerceptionInformation.DJIFlightAssistantObstacleSensingDirection.Downward;
import static dji.common.flightcontroller.flightassistant.PerceptionInformation.DJIFlightAssistantObstacleSensingDirection.Horizontal;
import static dji.common.flightcontroller.flightassistant.PerceptionInformation.DJIFlightAssistantObstacleSensingDirection.Upward;
import static dji.common.gimbal.Axis.PITCH;
import static dji.common.gimbal.Axis.YAW;
import static dji.internal.logics.CommonUtil.ONE_METER_OFFSET;
import static dji.keysdk.FlightControllerKey.HOME_LOCATION_LATITUDE;
import static dji.keysdk.FlightControllerKey.HOME_LOCATION_LONGITUDE;
import static dji.keysdk.FlightControllerKey.WIND_DIRECTION;
import static dji.keysdk.FlightControllerKey.WIND_SPEED;
import static dji.sdk.codec.DJICodecManager.VideoSource.CAMERA;
import static dji.sdk.codec.DJICodecManager.VideoSource.FPV;

public class ConnectionActivity extends NettyActivity implements View.OnClickListener, TextureView.SurfaceTextureListener, DJIDiagnostics.DiagnosticsInformationCallback, RTK.RTKBaseStationListCallback {
    private String liveShowUrl = "";
    private static final String TAG = ConnectionActivity.class.getName();
    private TextView mTextConnectionStatus;
    private TextView mTextProduct;
    private TextView mVersionTv;
    private Button mBtnOpen, btn_download, btn_gaode, btn_simulator, btn_login,btn_pl;
    private EditText et_zoom;
    private EditText et_url;

    private FlightController mFlightController;
    private RemoteController mRemoteController;
    private FlightAssistant mFlightAssistant;
    private OcuSyncLink ocuSyncLink;
    private RTK mRTK;
    private Battery battery;
    private Gimbal gimbal;
    private Timer mSendVirtualStickDataTimer;
    private float mPitch;
    private float mRoll;
    private float mYaw;
    private float mThrottle;
    private float altitude = 100.0f;//高度
    private float mSpeed = 10.0f;//速度
    private float mGimbalPitch = 0.2f;//弧度
    private String mTurnMode = "0";//旋转
    private SendVirtualStickDataTask mSendVirtualStickDataTask;
    private double droneLocationLat = 0.0, droneLocationLng = 0.0;
    private Marker droneMarker = null;
    private WaypointMissionFinishedAction mFinishedAction = WaypointMissionFinishedAction.NO_ACTION;
    private WaypointMissionHeadingMode mHeadingMode = WaypointMissionHeadingMode.AUTO;
    private WaypointMissionFlightPathMode mFlightPathMode = WaypointMissionFlightPathMode.NORMAL;
    public static WaypointMission.Builder waypointMissionBuilder;
    private WaypointMissionOperator instance;
    private List<Waypoint> waypointList = new ArrayList<>();

    String destDirName = Environment.getExternalStorageDirectory().getPath() + "/DjiMedia/";
    String destDirNameCompress = Environment.getExternalStorageDirectory().getPath() + "/DjiMediaCompress/";
    File destDir = new File(destDirName);
    File destDirCompress = new File(destDirNameCompress);
    File compressAddress = new File(Environment.getExternalStorageDirectory().getPath() + "/CompressAddress");
    String FileName = "";
    private String currentEquipment = "";//获取当前是什么无人机
    private Handler mHandler;
    public static final String FLAG_CONNECTION_CHANGE = "dji_sdk_connection_change";
    private Gson gson = new Gson();


    VideoFeeder.VideoDataListener mReceivedVideoDataListener = null;
    DJICodecManager mCodecManager = null;
    TextureView mVideoSurface = null;

    FlightControllerKey wind_direction_key;
    FlightControllerKey wind_speed_key;

    FlightControllerBean flightControllerBean = null;
    RTKBean rtkBean = null;
    Camera camera = null;
    Communication communication_flightController = null;
    Communication communication_rtk = null;
    Communication communication_takePhoto = null;
    Communication communication_battery = null;
    Communication communication_up_link = null;
    Communication communication_down_link = null;
    Communication communication_TSB = null;
    Communication communication_plane_status = null;
    Communication communication_laser_status = null;
    Communication communication_error_log = null;
    Communication communication_ObstacleAvoidanceSensorState = null;
    Communication communication_StorageState = null;
    Communication communication_isFlying = null;
    Communication communication_gohomelength = null;
    Communication communication_havePic = null;
    Communication communication_length = null;
    Communication communication_download_pic = null;
    Communication communication_onExecutionFinish = null;
    Communication communication_BS_info;
    Communication communication_upload_mission;
    private int currentProgress = -1;
    private boolean lastFlying = false;//判断是否起飞
    private boolean lastDistance = false;//判断距离
    String lowBatteryWarning = "", seriousLowBatteryWarning = "";
    boolean smartReturnToHomeEnabled;
    double goHomelength;//飞机回家距离

    boolean isQianHouGet = false;
    boolean isZuoYouGet = false;
    boolean isShangXiaGet = false;
    boolean isHangXianPause = false;//判断航线是否暂停
    double HangXianPauselongitude = 0, HangXianPauselatitude = 0;//当航线暂停时记录点
    int targetWaypointIndex = 0;
    boolean sgpre;
    String wayPoints = "";
    String wayPointAction = "";
    double goHomeLat=0,goHomeLong=0;
    boolean visionAssistedPosition, precisionLand, upwardsAvoidance, collisionAvoidance,landingProtection;
    FlightControllerKey flightControllerKey1 = FlightControllerKey.create(FlightControllerKey.VISION_ASSISTED_POSITIONING_ENABLED);
    FlightControllerKey flightControllerKey2 = FlightControllerKey.create(FlightControllerKey.PRECISION_LANDING_ENABLED);
    FlightControllerKey flightControllerKey3 = FlightControllerKey.create(FlightControllerKey.UPWARDS_AVOIDANCE_ENABLED);
    FlightControllerKey flightControllerKey4 = FlightControllerKey.create(FlightControllerKey.COLLISION_AVOIDANCE_ENABLED);
    FlightControllerKey flightControllerKey5 = FlightControllerKey.create(FlightControllerKey.LANDING_PROTECTION_ENABLED);
    GimbalKey gimbalKey1 = GimbalKey.create(GimbalKey.YAW_ANGLE_WITH_AIRCRAFT_IN_DEGREE);
    DiagnosticsKey diagnosticsKey = DiagnosticsKey.create(DiagnosticsKey.SYSTEM_STATUS);
    AirLinkKey airLinkKey1 = AirLinkKey.createWiFiLinkKey(AirLinkKey.CHANNEL_INTERFERENCE);
    String avoidanceDistanceUpward = "", avoidanceDistanceDownward = "", maxPerceptionDistanceUpward = "", maxPerceptionDistanceDownward = "", avoidanceDistanceHorizontal = "", maxPerceptionDistanceHorizontal = "";
    boolean activeObstacleAvoidance;
    String channelBandwidth = "", frequencyBand = "", transcodingDataRate = "", interferencePower = "", currentVideoSource = "";
    int downloadToPadCount = 0;//下载到pad时记录当前下载了第几个
    WebInitializationBean webInitializationBean = new WebInitializationBean();
    String gimbalStatePitch = "";//云台度数
    //这是获取左上角飞行状态的
//    private Handler handlerStartDownload = new Handler();

//    //之前的下载
//    private Runnable runnableStartDownload = new Runnable() {
//        public void run() {
//            // TODOAuto-generated method stub
//            handlerStartDownload.postDelayed(this, 1 * 1000);//设置延迟时间，此处是1秒
//            //需要执行的代码
//            StringsBean sb = new StringsBean();
//            sb.setValue("1");
//            if (communication_download_pic == null) {
//                communication_download_pic = new Communication();
//            }
//            communication_download_pic.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
//            communication_download_pic.setEquipmentId(MApplication.EQUIPMENT_ID);
//            communication_download_pic.setMethod((Constant.IS_DOWNLOAD));
//            communication_download_pic.setResult("1");
//            NettyClient.getInstance().sendMessage(communication_download_pic, null);
//
//        }
//    };
    private boolean pitchRangeExtension;
    private String pitch_CSF, yaw_CSF;
    private String beacons_b;
    private String front_b;
    private String rear_b;
    private String statusIndicator_b;

    private String goHomeHeightInMeters = "";
    private String maxFlightHeight = "";
    private String maxFlightRadius = "";
    private boolean maxFlightRadiusLimitationEnabled;
    private String wrj_heading = "";
    private boolean isCapture=false;//抓拍组合动作

    //航线V2
    private WaypointV2MissionOperator waypointV2MissionOperator = null;
    public static WaypointV2Mission.Builder waypointV2MissionBuilder = null;
    private List<WaypointV2> waypointV2List = new ArrayList<>();
    private WaypointV2MissionOperatorListener waypointV2MissionOperatorListener;
    private boolean canUploadMission = false;
    private boolean canStartMission = false;
    private WaypointV2ActionListener waypointV2ActionListener = null;
    private List<WaypointV2Action> waypointV2ActionList = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_connection);
        mHandler = new Handler(Looper.getMainLooper());
        initAllKeys();
        initUI();
        //注册广播接收器以接收设备连接的更改。
        IntentFilter filter = new IntentFilter();
        filter.addAction(FPVDemoApplication.FLAG_CONNECTION_CHANGE);
        registerReceiver(mReceiver, filter);
        //开启一个服务进行长连接
        final Intent intent = new Intent(this, NettyService.class);
        startService(intent);

        //视频流
        //我们mReceivedVideoDataListener使用VideoFeeder的初始化变量VideoDataListener()。
        //在回调内部，我们重写其onReceive()方法以获取原始H264视频数据并将其发送给mCodecManager解码
        mReceivedVideoDataListener = new VideoFeeder.VideoDataListener() {
            @Override
            public void onReceive(byte[] videoBuffer, int size) {
                if (mCodecManager != null) {
                    mCodecManager.sendDataToDecoder(videoBuffer, size);
                }
            }
        };

        //模型赋初始值
        webInitializationBean.setISO(65535);
        webInitializationBean.setExposureCompensation(65535);
        webInitializationBean.setShutter(255.0F);
        webInitializationBean.setExposureMode(65535);
        webInitializationBean.setCameraMode(255);
        webInitializationBean.setUpLink(0);
        webInitializationBean.setDownLink(0);
        et_url = findViewById(R.id.et_url);
        et_url.setText(liveShowUrl);


    }

    private void initAllKeys() {
        wind_direction_key = FlightControllerKey.create(FlightControllerKey.WIND_DIRECTION);
        wind_speed_key = FlightControllerKey.create(WIND_SPEED);
    }

    @Override
    public void onResume() {
        Log.e(TAG, "onResume");
        super.onResume();

//        initPreviewer();

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
                String cvs = VideoFeeder.getInstance().getPrimaryVideoFeed().getVideoSource().value() + "";
                if (cvs.equals("5")) {
                    currentVideoSource = "1";
                } else {
                    currentVideoSource = "0";
                }
                webInitializationBean.setCurrentVideoSource(currentVideoSource);
//                Log.d("currentVideoSource",currentVideoSource+"");
            }
        }

    }

    private void uninitPreviewer() {
        if (camera != null) {
            // Reset the callback
            VideoFeeder.getInstance().getPrimaryVideoFeed().addVideoDataListener(null);
        }
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
        removeWaypointMissionListener();
        uninitPreviewer();
//        planeStatusHandler.removeCallbacks(planeStatusTask);//关闭轮询
        if (DJISDKManager.getInstance().getProduct() != null) {
            DJISDKManager.getInstance().getProduct().setDiagnosticsInformationCallback(this);//关闭错误日志
        }
        //waypointV2destory
        tearDownListener();
        unInitListener();
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
        btn_pl = findViewById(R.id.btn_pl);
        btn_pl.setOnClickListener(this);
        et_zoom = findViewById(R.id.et_zoom);

        mVideoSurface = (TextureView) findViewById(R.id.video_previewer_surface);
        if (null != mVideoSurface) {
            mVideoSurface.setSurfaceTextureListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_open: {
                loginAccount();
//                Intent intent = new Intent(this, DefaultLayoutActivity.class);
//                startActivity(intent);

//                Intent intent = new Intent(this, TakePhotoActivity.class);
//                startActivity(intent);

//                waypoint_plan_V2(null);
                break;

            }
            case R.id.btn_download: {
                loginOut();
//                startWaypointV2(null);
                break;
            }

            case R.id.btn_gaode: {
                stopWaypointV2(null);
                break;
            }
            case R.id.btn_simulator: {
                //去控制飞机
//                Intent intent = new Intent(this, SimulatorMainActivity.class);
//                startActivity(intent);
//                isLiveShowOn();
//                startLiveShow(null);
                resumeWaypointV2(null);
                break;
            }

            case R.id.btn_login:
//                DJISDKManager.getInstance().getLiveStreamManager().stopStream();
//                Toast.makeText(getApplicationContext(), "Stop Live Show", Toast.LENGTH_SHORT).show();
                pauseWaypointV2(null);
                break;
            case R.id.btn_pl:
                initListener();
//                sendData();
                break;
            default:
                break;
        }
    }



    private boolean isLiveStreamManagerOn() {
        if (DJISDKManager.getInstance().getLiveStreamManager() == null) {
            //    ToastUtils.setResultToToast("No live stream manager!");
            //wang
            Toast.makeText(getApplicationContext(), "No live stream manager!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    void startLiveShow(Communication communication) {
        //wang
        Toast.makeText(getApplicationContext(), "Start Live Show", Toast.LENGTH_SHORT).show();
//        ToastUtils.setResultToToast("Start Live Show");
        if (!isLiveStreamManagerOn()) {
            return;
        }
        if (DJISDKManager.getInstance().getLiveStreamManager().isStreaming()) {
            //wang
            Toast.makeText(getApplicationContext(), "already started!", Toast.LENGTH_SHORT).show();
//            ToastUtils.setResultToToast("already started!");
            if (communication != null) {
                communication.setResult("already started!");
                communication.setCode(-1);
                communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                NettyClient.getInstance().sendMessage(communication, null);
            }

            return;
        }
        new Thread() {
            @Override
            public void run() {
                DJISDKManager.getInstance().getLiveStreamManager().setLiveUrl(et_url.getText().toString().trim());
                Log.d("et_url", et_url.getText().toString().trim());
                int result = DJISDKManager.getInstance().getLiveStreamManager().startStream();
                DJISDKManager.getInstance().getLiveStreamManager().setStartTime();

                //wang
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String sss = "startLive:" + result +
                                "\n isVideoStreamSpeedConfigurable:" + DJISDKManager.getInstance().getLiveStreamManager().isVideoStreamSpeedConfigurable() +
                                "\n isLiveAudioEnabled:" + DJISDKManager.getInstance().getLiveStreamManager().isLiveAudioEnabled() +
                                "\n isStreaming:" + DJISDKManager.getInstance().getLiveStreamManager().isStreaming();
                        Toast.makeText(getApplicationContext(), sss, Toast.LENGTH_SHORT).show();
                        if (communication != null) {
                            communication.setResult(result + "");
                            communication.setCode(200);
                            communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                            NettyClient.getInstance().sendMessage(communication, null);
                        }
                    }
                });

            }
        }.start();
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
            initBattery();
//            initGimbal();
            addWaypointMissionListener();//添加航点的监听
//            planeStatusHandler.post(planeStatusTask);//立即调用获取飞机当前是否可以起飞
            initErrorLog();//初始化错误日志
            initOcuSyncLink();
            initPreviewer();
            startLiveShow(null);//开始推流


        }
    };

    private void initOcuSyncLink() {
        try {
            ocuSyncLink = FPVDemoApplication.getAirLinkInstance().getOcuSyncLink();
            if (ocuSyncLink != null) {
                ocuSyncLink.assignSourceToPrimaryChannel(PhysicalSource.LEFT_CAM, PhysicalSource.FPV_CAM, new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                    }
                });
                //图传码率
                ocuSyncLink.setVideoDataRateCallback(new OcuSyncLink.VideoDataRateCallback() {
                    @Override
                    public void onUpdate(float v) {

                        transcodingDataRate = v + "";
                        Log.d("WifiChannelInterference", "transcodingDataRate=" + transcodingDataRate);
                    }
                });

                //带宽  有用
                ocuSyncLink.getChannelBandwidth(new CommonCallbacks.CompletionCallbackWith<OcuSyncBandwidth>() {
                    @Override
                    public void onSuccess(OcuSyncBandwidth ocuSyncBandwidth) {
                        switch (ocuSyncBandwidth.value()) {
                            case 0:
                                channelBandwidth = "20MHz";
                                break;
                            case 1:
                                channelBandwidth = "10MHz";
                                break;
                            case 2:
                                channelBandwidth = "40MHz";
                                break;
                        }
                        Log.d("WifiChannelInterference", "channelBandwidth=" + channelBandwidth);

                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });

                //工作频段 2.4g 5.8g 双频
                ocuSyncLink.getFrequencyBand(new CommonCallbacks.CompletionCallbackWith<OcuSyncFrequencyBand>() {
                    @Override
                    public void onSuccess(OcuSyncFrequencyBand ocuSyncFrequencyBand) {
                        switch (ocuSyncFrequencyBand.getValue()) {
                            case 0:
                                frequencyBand = "双频";
                                break;
                            case 1:
                                frequencyBand = "2.4G";
                                break;
                            case 2:
                                frequencyBand = "5.8G";
                                break;
                        }
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });


                //干扰
                FPVDemoApplication.getAirLinkInstance().getWiFiLink().setChannelInterferenceCallback(new WiFiLink.ChannelInterferenceCallback() {
                    @Override
                    public void onUpdate(WifiChannelInterference[] wifiChannelInterferences) {
                        if (wifiChannelInterferences.length > 0) {
                            interferencePower = wifiChannelInterferences[0].getPower() + "";
                            Log.d("WifiChannelInterference", "interferencePower" + interferencePower);
                        }

                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("downLoadPic", "不压缩后上传报错=" + e.toString());
        }


    }

    private void initErrorLog() {
        if (DJISDKManager.getInstance().getProduct() != null) {
            DJISDKManager.getInstance().getProduct().setDiagnosticsInformationCallback(this);
        }
    }

    private void refreshSDKRelativeUI() {
        BaseProduct mProduct = FPVDemoApplication.getProductInstance();

        if (null != mProduct && mProduct.isConnected()) {
            Log.v(TAG, "refreshSDK: True");
            mBtnOpen.setEnabled(true);

            String str = mProduct instanceof Aircraft ? "DJIAircraft" : "DJIHandHeld";
            mTextConnectionStatus.setText("Status: " + str + " connected");

            if (null != mProduct.getModel()) {
                mTextProduct.setText("" + mProduct.getModel().getDisplayName());
                currentEquipment = mProduct.getModel().getDisplayName();
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

    private void loginOut() {
        UserAccountManager.getInstance().loginOut(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {

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
            mRemoteController = null;
            return;
        } else {

            mFlightController = aircraft.getFlightController();
            if (mFlightController != null) {
                mFlightController.setRollPitchControlMode(RollPitchControlMode.VELOCITY);
                mFlightController.setYawControlMode(YawControlMode.ANGULAR_VELOCITY);
                mFlightController.setVerticalControlMode(VerticalControlMode.VELOCITY);
                mFlightController.setRollPitchCoordinateSystem(FlightCoordinateSystem.BODY);//相对于自己

                initGimbal();

                //获取飞行状态d
                mFlightController.setStateCallback(new FlightControllerState.Callback() {
                    @Override
                    public void onUpdate(FlightControllerState flightControllerState) {
                        try {
                            boolean isFlying = flightControllerState.isFlying();
                            //当2个值不一样就发
                            if (lastFlying != isFlying) {
                                lastFlying = isFlying;
                                StringsBean beans = new StringsBean();
                                beans.setValue((flightControllerState.isFlying()) ? "1" : "0");
                                if (communication_isFlying == null) {
                                    communication_isFlying = new Communication();
                                }
                                communication_isFlying.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                                communication_isFlying.setEquipmentId(MApplication.EQUIPMENT_ID);
                                communication_isFlying.setMethod((Constant.IS_FLYING));
//                        communication_isFlying.setResult(gson.toJson(beans, StringsBean.class));
                                communication_isFlying.setResult(isFlying ? "1" : "0");
                                NettyClient.getInstance().sendMessage(communication_isFlying, null);
                                Log.d("isFlying", "isFlying:" + isFlying);
                                //如果已经飞完，并且有拍过照或录过像就开始下载操作
//                        if (!isFlying && isStartShootOrRecord) {
//                            initMediaManager();//去下载和上传
//                        }
                            }
                            //每秒一次
                            if (fastClick.flightControllerClick()) {
                                Object windSpeed = KeyManager.getInstance().getValue((FlightControllerKey.create(WIND_SPEED)));
                                Object windDirection = KeyManager.getInstance().getValue((FlightControllerKey.create(WIND_DIRECTION)));
                                int subWindSpeed = 0;
                                int subWindDirection = 0;
                                if (windSpeed != null && windSpeed instanceof Integer) {
                                    subWindSpeed = (int) windSpeed;
                                }
                                if (windDirection != null && windDirection instanceof WindDirection) {
                                    switch ((WindDirection) windDirection) {
                                        case WINDLESS:
                                            subWindDirection = 0;
                                            break;
                                        case NORTH:
                                            subWindDirection = 1;
                                            break;
                                        case NORTH_EAST:
                                            subWindDirection = 2;
                                            break;
                                        case EAST:
                                            subWindDirection = 3;
                                            break;
                                        case SOUTH_EAST:
                                            subWindDirection = 4;
                                            break;
                                        case SOUTH:
                                            subWindDirection = 5;
                                            break;
                                        case SOUTH_WEST:
                                            subWindDirection = 6;
                                            break;
                                        case WEST:
                                            subWindDirection = 7;
                                            break;
                                        case NORTH_WEST:
                                            subWindDirection = 8;
                                            break;
                                    }

                                }
//                                Log.d(TAG, "=====================================================");
//                                Log.d(TAG, "卫星数=" + flightControllerState.getSatelliteCount() + "");
////                        Log.d(TAG, "windDirection=" + windDirection + "");
////                        Log.d(TAG, "windSpeed=" + windSpeed + "");
//                                Log.d(TAG, "飞机的高度=" + flightControllerState.getUltrasonicHeightInMeters());//低于5米才有参考价值
//                                Log.d(TAG, "getFlightModeString=" + flightControllerState.getFlightModeString());
//                                Log.d(TAG, "getAircraftHeadDirection=" + flightControllerState.getAircraftHeadDirection());
//                                Log.d(TAG, "当前电量能干嘛=" + flightControllerState.getBatteryThresholdBehavior());
//                                Log.d(TAG, "getVelocityX=" + flightControllerState.getVelocityX());
//                                Log.d(TAG, "getVelocityY=" + flightControllerState.getVelocityY());
//                                Log.d(TAG, "getVelocityZ=" + flightControllerState.getVelocityZ());
//                                Log.d(TAG, "HomeLocation.getLatitude=" + flightControllerState.getHomeLocation().getLatitude());
//                                Log.d(TAG, "HomeLocation.getLongitude=" + flightControllerState.getHomeLocation().getLongitude());
//                                Log.d(TAG, "pitch=" + flightControllerState.getAttitude().pitch);
//                                Log.d(TAG, "roll=" + flightControllerState.getAttitude().roll);
//                                Log.d(TAG, "yaw=" + flightControllerState.getAttitude().yaw);
//                                Log.d(TAG, "纬度=" + flightControllerState.getAircraftLocation().getLatitude());
//                                Log.d(TAG, "经度=" + flightControllerState.getAircraftLocation().getLongitude());
//                                Log.d(TAG, "海拔=" + flightControllerState.getAircraftLocation().getAltitude());
//                                Log.d(TAG, "IslandingConfirmationNeeded=" + flightControllerState.isLandingConfirmationNeeded() + "");
                                goHomeLat=flightControllerState.getHomeLocation().getLatitude();
                                goHomeLong=flightControllerState.getHomeLocation().getLongitude();
                                double length = LocationUtils.getDistance(flightControllerState.getHomeLocation().getLongitude() + "", flightControllerState.getHomeLocation().getLatitude() + ""
                                        , flightControllerState.getAircraftLocation().getLongitude() + "", flightControllerState.getAircraftLocation().getLatitude() + "");
                                boolean nowdistance = length > 3000;
                                //判断是否超过三公里
                                if (lastDistance != nowdistance) {
                                    lastDistance = isFlying;
                                    StringsBean beans = new StringsBean();
                                    beans.setValue(lastDistance ? "1" : "0");
                                    if (communication_length == null) {
                                        communication_length = new Communication();
                                    }
                                    communication_length.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                                    communication_length.setEquipmentId(MApplication.EQUIPMENT_ID);
                                    communication_length.setMethod((Constant.DISTANCE_GO_HOME));
//                            communication_length.setResult(gson.toJson(beans, StringsBean.class));
                                    communication_length.setResult(lastDistance ? "1" : "0");
                                    NettyClient.getInstance().sendMessage(communication_length, null);
                                }


                                if (flightControllerBean == null) {
                                    flightControllerBean = new FlightControllerBean();
                                }

                                //当2个值不一样就发
                                if (goHomelength != length) {
                                    goHomelength = length;
                                    //回家距离
                                    if (communication_gohomelength == null) {
                                        communication_gohomelength = new Communication();
                                    }
                                    communication_gohomelength.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                                    communication_gohomelength.setEquipmentId(MApplication.EQUIPMENT_ID);
                                    communication_gohomelength.setMethod((Constant.GO_HOME_LENGTH));
                                    communication_gohomelength.setResult(goHomelength + "");
                                    NettyClient.getInstance().sendMessage(communication_gohomelength, null);
                                }

                                flightControllerBean.setGoHomeLength(length);
                                flightControllerBean.setWindSpeed(subWindSpeed);
                                flightControllerBean.setWindDirection(subWindDirection);
                                flightControllerBean.setAreMotorsOn(flightControllerState.areMotorsOn());
                                flightControllerBean.setFlying(flightControllerState.isFlying());
                                if ((flightControllerState.getAircraftLocation().getAltitude() + "").equals("NaN")) {
                                    flightControllerBean.setAircraftAltitude(0.0);
                                } else {
                                    flightControllerBean.setAircraftAltitude(flightControllerState.getAircraftLocation().getAltitude());
                                }
                                LatLng latLng = MapConvertUtils.getGDLatLng(flightControllerState.getAircraftLocation().getLatitude(), flightControllerState.getAircraftLocation().getLongitude(), ConnectionActivity.this);
                                if ((flightControllerState.getAircraftLocation().getLatitude() + "").equals("NaN")) {
                                    flightControllerBean.setAircraftLatitude(0.0);
                                } else {
//                            flightControllerBean.setAircraftLatitude(flightControllerState.getAircraftLocation().getLatitude());
                                    flightControllerBean.setAircraftLatitude(latLng.latitude);
                                }
                                if ((flightControllerState.getAircraftLocation().getLongitude() + "").equals("NaN")) {
                                    flightControllerBean.setAircraftLongitude(0.0);
                                } else {
//                            flightControllerBean.setAircraftLongitude(flightControllerState.getAircraftLocation().getLongitude());
                                    flightControllerBean.setAircraftLongitude(latLng.longitude);
                                }
                                flightControllerBean.setGimbalStatePitch(gimbalStatePitch);
                                //航线暂停飞行时记录
                                if (isHangXianPause) {
                                    HangXianPauselongitude = latLng.longitude;
                                    HangXianPauselatitude = latLng.latitude;
                                    isHangXianPause = false;

                                }
                                if ((flightControllerState.getTakeoffLocationAltitude() + "").equals("NaN")) {
                                    flightControllerBean.setTakeoffLocationAltitude(0);
                                } else {
                                    flightControllerBean.setTakeoffLocationAltitude(flightControllerState.getTakeoffLocationAltitude());
                                }
                                flightControllerBean.setFlightModeString(flightControllerState.getFlightModeString());
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
                                //获取度数
                                if (mFlightController.getCompass() != null) {
                                    wrj_heading = mFlightController.getCompass().getHeading() + "";
                                    flightControllerBean.setHeading(wrj_heading);
//                            Log.d("DDDDDD","无人机的航向度数："+mFlightController.getCompass().getHeading());
                                }

                                LatLng latLngHome = MapConvertUtils.getGDLatLng(flightControllerState.getHomeLocation().getLatitude(), flightControllerState.getHomeLocation().getLongitude(), ConnectionActivity.this);
                                if ((flightControllerState.getHomeLocation().getLatitude() + "").equals("NaN")) {
                                    flightControllerBean.setHomeLocationLatitude(0);
                                } else {
                                    flightControllerBean.setHomeLocationLatitude(latLngHome.latitude);
//                            flightControllerBean.setHomeLocationLatitude(flightControllerState.getHomeLocation().getLatitude());
                                }
                                if ((flightControllerState.getHomeLocation().getLongitude() + "").equals("NaN")) {
                                    flightControllerBean.setHomeLocationLongitude(0);
                                } else {
                                    flightControllerBean.setHomeLocationLongitude(latLngHome.longitude);
//                            flightControllerBean.setHomeLocationLongitude(flightControllerState.getHomeLocation().getLongitude());
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
                                communication_flightController.setEquipmentId(MApplication.EQUIPMENT_ID);
                                communication_flightController.setMethod((Constant.flightController));
                                communication_flightController.setResult(gson.toJson(flightControllerBean, FlightControllerBean.class));
//                        Log.d("MMMMM",communication_flightController.toString());
                                NettyClient.getInstance().sendMessage(communication_flightController, null);
                            }

                            //左上角飞行状态
                            if (KeyManager.getInstance() != null) {
                                KeyManager.getInstance().getValue(diagnosticsKey, new GetCallback() {
                                    @Override
                                    public void onSuccess(Object o) {
                                        String planeStatusResult = "";
                                        planeStatusResult = o.toString();
//                        Log.d("diagnosticsKey", planeStatusResult);
                                        StringsBean stringsBean = new StringsBean();
                                        stringsBean.setValue(planeStatusResult);
                                        if (communication_plane_status == null) {
                                            communication_plane_status = new Communication();
                                        }
                                        communication_plane_status.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                                        communication_plane_status.setEquipmentId(MApplication.EQUIPMENT_ID);
                                        communication_plane_status.setMethod((Constant.LINK_PLANE_STATUS));
                                        communication_plane_status.setResult(gson.toJson(stringsBean, StringsBean.class));
                                        NettyClient.getInstance().sendMessage(communication_plane_status, null);
                                    }

                                    @Override
                                    public void onFailure(DJIError djiError) {

                                    }
                                });
                            }

                            //每秒返回遥控器信号
                            StringsBean upLinkBean = new StringsBean();
                            upLinkBean.setValue(webInitializationBean.getUpLink() + "");
                            if (communication_up_link == null) {
                                communication_up_link = new Communication();
                            }
                            communication_up_link.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                            communication_up_link.setEquipmentId(MApplication.EQUIPMENT_ID);
                            communication_up_link.setMethod((Constant.UP_LOAD_SIGNAL));
                            communication_up_link.setResult(gson.toJson(upLinkBean, StringsBean.class));
                            NettyClient.getInstance().sendMessage(communication_up_link, null);
                            //每秒返回视图信号
                            StringsBean downLinkBean = new StringsBean();
                            upLinkBean.setValue(webInitializationBean.getDownLink() + "");
                            if (communication_down_link == null) {
                                communication_down_link = new Communication();
                            }
                            communication_down_link.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                            communication_down_link.setEquipmentId(MApplication.EQUIPMENT_ID);
                            communication_down_link.setMethod((Constant.DOWN_LOAD_SIGNAL));
                            communication_down_link.setResult(gson.toJson(upLinkBean, StringsBean.class));
                            NettyClient.getInstance().sendMessage(communication_down_link, null);



                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                });
//            //低电量值，严重低电量，智能返回（都在电池ui那块） 有用
                mFlightController.getLowBatteryWarningThreshold(new CommonCallbacks.CompletionCallbackWith<Integer>() {
                    @Override
                    public void onSuccess(Integer integer) {
                        lowBatteryWarning = integer + "";
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });
                mFlightController.getSeriousLowBatteryWarningThreshold(new CommonCallbacks.CompletionCallbackWith<Integer>() {
                    @Override
                    public void onSuccess(Integer integer) {
                        seriousLowBatteryWarning = integer + "";
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });
                mFlightController.getSmartReturnToHomeEnabled(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        smartReturnToHomeEnabled = aBoolean;
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });

                mFlightController.getLEDsEnabledSettings(new CommonCallbacks.CompletionCallbackWith<LEDsSettings>() {
                    @Override
                    public void onSuccess(LEDsSettings leDsSettings) {
                        beacons_b = leDsSettings.areBeaconsOn() ? "1" : "0";
                        front_b = leDsSettings.areFrontLEDsOn() ? "1" : "0";
                        rear_b = leDsSettings.areRearLEDsOn() ? "1" : "0";
                        statusIndicator_b = leDsSettings.isStatusIndicatorOn() ? "1" : "0";
                        Log.d("DDDDD", "beacons_b=" + beacons_b + " front_b=" + front_b + " rear_b=" + rear_b + " statusIndicator_b=" + statusIndicator_b);
                        webInitializationBean.setBeacons(beacons_b);
                        webInitializationBean.setFront(front_b);
                        webInitializationBean.setRear(rear_b);
                        webInitializationBean.setStatusIndicator(statusIndicator_b);

                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });

                mFlightController.getGoHomeHeightInMeters(new CommonCallbacks.CompletionCallbackWith<Integer>() {
                    @Override
                    public void onSuccess(Integer integer) {
                        goHomeHeightInMeters = integer + "";
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });
                mFlightController.getMaxFlightHeight(new CommonCallbacks.CompletionCallbackWith<Integer>() {
                    @Override
                    public void onSuccess(Integer integer) {
                        maxFlightHeight = integer + "";
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });
                mFlightController.getMaxFlightRadiusLimitationEnabled(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        maxFlightRadiusLimitationEnabled = aBoolean;
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });
                mFlightController.getMaxFlightRadius(new CommonCallbacks.CompletionCallbackWith<Integer>() {
                    @Override
                    public void onSuccess(Integer integer) {
                        maxFlightRadius = integer + "";
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });


                if (KeyManager.getInstance() != null) {

                    //视觉定位
                    KeyManager.getInstance().getValue(flightControllerKey1, new GetCallback() {
                        @Override
                        public void onSuccess(Object o) {
                            visionAssistedPosition = (boolean) o;
                        }

                        @Override
                        public void onFailure(DJIError djiError) {

                        }
                    });
                    //精确着陆
                    KeyManager.getInstance().getValue(flightControllerKey2, new GetCallback() {
                        @Override
                        public void onSuccess(Object o) {
                            precisionLand = (boolean) o;
                        }

                        @Override
                        public void onFailure(DJIError djiError) {

                        }
                    });
                    //向上避免
                    KeyManager.getInstance().getValue(flightControllerKey3, new GetCallback() {
                        @Override
                        public void onSuccess(Object o) {
                            upwardsAvoidance = (boolean) o;
                        }

                        @Override
                        public void onFailure(DJIError djiError) {

                        }
                    });
                    //向下避障
                    KeyManager.getInstance().getValue(flightControllerKey5, new GetCallback() {
                        @Override
                        public void onSuccess(Object o) {
                            landingProtection = (boolean) o;
                        }

                        @Override
                        public void onFailure(DJIError djiError) {

                        }
                    });

                    //向上避障
//                    mFlightAssistant.getUpwardVisionObstacleAvoidanceEnabled(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
//                        @Override
//                        public void onSuccess(Boolean aBoolean) {
//                            upwardsAvoidance = aBoolean;
//                        }
//
//                        @Override
//                        public void onFailure(DJIError djiError) {
//
//                        }
//                    });
//                    //向下避障
//                    mFlightAssistant.getLandingProtectionEnabled(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
//                        @Override
//                        public void onSuccess(Boolean aBoolean) {
//                            landingProtection=aBoolean;
//                        }
//
//                        @Override
//                        public void onFailure(DJIError djiError) {
//
//                        }
//                    });


                    //
                    KeyManager.getInstance().getValue(flightControllerKey4, new GetCallback() {
                        @Override
                        public void onSuccess(Object o) {
                            collisionAvoidance = (boolean) o;
                        }

                        @Override
                        public void onFailure(DJIError djiError) {

                        }
                    });
                }


                //遥控器信号
                if (FPVDemoApplication.getProductInstance().getAirLink() != null) {
                    FPVDemoApplication.getProductInstance().getAirLink().setUplinkSignalQualityCallback(new SignalQualityCallback() {
                        @Override
                        public void onUpdate(int i) {
                            Log.d("AirLink", "Uplink" + i);
                            int level = 0;
                            if (i <= 0) {
                                level = 0;
                            } else if (i > 0 && i <= 20) {
                                level = 1;
                            } else if (i > 20 && i <= 40) {
                                level = 2;
                            } else if (i > 40 && i <= 60) {
                                level = 3;
                            } else if (i > 60 && i <= 80) {
                                level = 4;
                            } else if (i > 80 && i <= 100) {
                                level = 5;
                            }
                            webInitializationBean.setUpLink(level);

                        }
                    });
                }
                //视图信号
                if (FPVDemoApplication.getProductInstance().getAirLink() != null) {
                    FPVDemoApplication.getProductInstance().getAirLink().setDownlinkSignalQualityCallback(new SignalQualityCallback() {
                        @Override
                        public void onUpdate(int i) {
                            Log.d("AirLink", "Downlink" + i);
                            int level = 0;
                            if (i <= 0) {
                                level = 0;
                            } else if (i > 0 && i <= 20) {
                                level = 1;
                            } else if (i > 20 && i <= 40) {
                                level = 2;
                            } else if (i > 40 && i <= 60) {
                                level = 3;
                            } else if (i > 60 && i <= 80) {
                                level = 4;
                            } else if (i > 80 && i <= 100) {
                                level = 5;
                            }
                            webInitializationBean.setDownLink(level);

                        }
                    });
                }
                //遥控器
                mRemoteController = aircraft.getRemoteController();
                //rtk
                mRTK = mFlightController.getRTK();


                mFlightAssistant = mFlightController.getFlightAssistant();
                mFlightAssistant.setVisionDetectionStateUpdatedCallback(new VisionDetectionState.Callback() {
                    //                视觉系统可以以60度水平视场（FOV）和55度垂直FOV看到飞机前方。水平视场分为四个相等的扇区，此类给出了一个扇区的距离和警告级别。
                    @Override
                    public void onUpdate(VisionDetectionState visionDetectionState) {
//                    double ObstacleDistanceInMeters= visionDetectionState.getObstacleDistanceInMeters();
                        ObstacleDetectionSector[] mArray = visionDetectionState.getDetectionSectors();
//                    Log.d("MMMMM","ObstacleDistanceInMeters="+ObstacleDistanceInMeters);
                        String aa = "";
                        for (int i = 0; i < mArray.length; i++) {
//                        检测到的飞机障碍物距离，以米为单位。
                            aa = "ObstacleDistanceInMeters" + i + "=" + mArray[i].getObstacleDistanceInMeters() + "\nWarningLevel=" + mArray[i].getWarningLevel();
//                        基于距离的警告级别。
//                            Log.d("MMMMM", aa);
                        }

                    }
                });

                if (isM300Product()) {
                    //避障4.14没用了
//                    mFlightController.getFlightAssistant().setObstacleAvoidanceSensorStateListener(new CommonCallbacks.CompletionCallbackWith<ObstacleAvoidanceSensorState>() {
//                        @Override
//                        public void onSuccess(ObstacleAvoidanceSensorState obstacleAvoidanceSensorState) {
//                            ObstacleAvoidanceSensorStateBean bean = new ObstacleAvoidanceSensorStateBean();
//                            bean.setAreObstacleAvoidanceSensorsInHorizo​​ntalDirectionEnabled(obstacleAvoidanceSensorState.areObstacleAvoidanceSensorsInHorizontalDirectionEnabled());
//                            bean.setAreObstacleAvoidanceSensorsInHorizo​​ntalDirectionWorking(obstacleAvoidanceSensorState.areObstacleAvoidanceSensorsInHorizontalDirectionWorking());
//                            bean.setAreObstacleAvoidanceSensorsInVerticalDirectionEnabled(obstacleAvoidanceSensorState.areObstacleAvoidanceSensorsInVerticalDirectionEnabled());
//                            bean.setAreObstacleAvoidanceSensorsInVerticalDirectionWorking(obstacleAvoidanceSensorState.areObstacleAvoidanceSensorsInVerticalDirectionWorking());
//                            bean.setUpwardObstacleAvoidanceSensorEnabled(obstacleAvoidanceSensorState.isUpwardObstacleAvoidanceSensorEnabled());
//                            bean.setUpwardObstacleAvoidanceSensorWorking(obstacleAvoidanceSensorState.isUpwardObstacleAvoidanceSensorWorking());
//                            bean.setLeftSideObstacleAvoidanceSensorEnabled(obstacleAvoidanceSensorState.isLeftSideObstacleAvoidanceSensorEnabled());
//                            bean.setLeftSideObstacleAvoidanceSensorWorking(obstacleAvoidanceSensorState.isLeftSideObstacleAvoidanceSensorWorking());
//                            bean.setRightSideObstacleAvoidanceSensorEnabled(obstacleAvoidanceSensorState.isRightSideObstacleAvoidanceSensorEnabled());
//                            bean.setRightSideObstacleAvoidanceSensorWorking(obstacleAvoidanceSensorState.isRightSideObstacleAvoidanceSensorWorking());
//                            bean.setBackwardObstacleAvoidanceSensorEnabled(obstacleAvoidanceSensorState.isBackwardObstacleAvoidanceSensorEnabled());
//                            bean.setBackwardObstacleAvoidanceSensorWorking(obstacleAvoidanceSensorState.isBackwardObstacleAvoidanceSensorWorking());
//
//                            if (communication_ObstacleAvoidanceSensorState == null) {
//                                communication_ObstacleAvoidanceSensorState = new Communication();
//                            }
//                            communication_ObstacleAvoidanceSensorState.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
//                            communication_ObstacleAvoidanceSensorState.setEquipmentId(MApplication.EQUIPMENT_ID);
//                            communication_ObstacleAvoidanceSensorState.setMethod((Constant.OASS));
//                            communication_ObstacleAvoidanceSensorState.setResult(gson.toJson(bean, ObstacleAvoidanceSensorStateBean.class));
//                            NettyClient.getInstance().sendMessage(communication_ObstacleAvoidanceSensorState, null);
//                        }
//
//                        @Override
//                        public void onFailure(DJIError djiError) {
//                        }
//                    });
                    mFlightAssistant.getVisualObstaclesAvoidanceDistance(Upward, new CommonCallbacks.CompletionCallbackWith<Float>() {
                        @Override
                        public void onSuccess(Float aFloat) {
                            avoidanceDistanceUpward = aFloat + "";
                        }

                        @Override
                        public void onFailure(DJIError djiError) {

                        }
                    });
                    mFlightAssistant.getVisualObstaclesAvoidanceDistance(Downward, new CommonCallbacks.CompletionCallbackWith<Float>() {
                        @Override
                        public void onSuccess(Float aFloat) {
                            avoidanceDistanceDownward = aFloat + "";
                        }

                        @Override
                        public void onFailure(DJIError djiError) {

                        }
                    });
                    mFlightAssistant.getVisualObstaclesAvoidanceDistance(Horizontal, new CommonCallbacks.CompletionCallbackWith<Float>() {
                        @Override
                        public void onSuccess(Float aFloat) {
                            avoidanceDistanceHorizontal = aFloat + "";
                        }

                        @Override
                        public void onFailure(DJIError djiError) {

                        }
                    });

                    mFlightAssistant.getMaxPerceptionDistance(Upward, new CommonCallbacks.CompletionCallbackWith<Float>() {
                        @Override
                        public void onSuccess(Float aFloat) {
                            maxPerceptionDistanceUpward = aFloat + "";
                        }

                        @Override
                        public void onFailure(DJIError djiError) {

                        }
                    });
                    mFlightAssistant.getMaxPerceptionDistance(Downward, new CommonCallbacks.CompletionCallbackWith<Float>() {
                        @Override
                        public void onSuccess(Float aFloat) {
                            maxPerceptionDistanceDownward = aFloat + "";
                        }

                        @Override
                        public void onFailure(DJIError djiError) {

                        }
                    });

                    mFlightAssistant.getMaxPerceptionDistance(Horizontal, new CommonCallbacks.CompletionCallbackWith<Float>() {
                        @Override
                        public void onSuccess(Float aFloat) {
                            maxPerceptionDistanceHorizontal = aFloat + "";
                        }

                        @Override
                        public void onFailure(DJIError djiError) {

                        }
                    });

                    mFlightAssistant.getActiveObstacleAvoidanceEnabled(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
                        @Override
                        public void onSuccess(Boolean aBoolean) {
                            activeObstacleAvoidance = aBoolean;
                        }

                        @Override
                        public void onFailure(DJIError djiError) {

                        }
                    });

                    int loginvalue = UserAccountManager.getInstance().getUserAccountState().value();
                    webInitializationBean.setUserAccountState(loginvalue + "");
                }
            }

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


    private void initCamera() {
        camera = FPVDemoApplication.getCameraInstance();
        if (camera != null) {
            //设置比例为16：9
            camera.getLens(camera.getIndex()).setPhotoAspectRatio(RATIO_16_9, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if(djiError!=null){
                        Log.d("RATIO_16_9_Lens",djiError.toString());
                    }else{
                        Log.d("RATIO_16_9_Lens","success");
                    }
                }
            });

            camera.getCameraVideoStreamSource(new CommonCallbacks.CompletionCallbackWith<CameraVideoStreamSource>() {
                @Override
                public void onSuccess(CameraVideoStreamSource cameraVideoStreamSource) {
                    webInitializationBean.setCurrentLens(cameraVideoStreamSource.value() + "");
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            });
            //初始设个拍照模式
            camera.setMode(SettingsDefinitions.CameraMode.SHOOT_PHOTO, null);
            camera.getMode(new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.CameraMode>() {
                @Override
                public void onSuccess(SettingsDefinitions.CameraMode cameraMode) {
                    webInitializationBean.setCameraMode(cameraMode.value());
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            });


            camera.setStorageStateCallBack(new StorageState.Callback() {
                @Override
                public void onUpdate(StorageState storageState) {
                    Log.d("StorageState", storageState.getAvailableCaptureCount() + "");
                    Log.d("StorageState", storageState.isFull() + "");
                    Log.d("StorageState", storageState.getAvailableRecordingTimeInSeconds() + "");
                    StorageStateBean bean = new StorageStateBean();
                    bean.setAvailableCaptureCount(storageState.getAvailableCaptureCount());
                    bean.setAvailableRecordingTimeInSeconds(storageState.getAvailableRecordingTimeInSeconds());
                    if (communication_StorageState == null) {
                        communication_StorageState = new Communication();
                    }
                    communication_StorageState.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                    communication_StorageState.setEquipmentId(MApplication.EQUIPMENT_ID);
                    communication_StorageState.setMethod((Constant.STORAGE_STATE));
                    communication_StorageState.setResult(gson.toJson(bean, StorageStateBean.class));
                    NettyClient.getInstance().sendMessage(communication_StorageState, null);
                }
            });
            if (isM300Product()) {
                //返回激光测距数据
                camera.getLaserEnabled(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        if (aBoolean) {
                            returnLaserData();
                        }
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });
                //返回曝光模式
                camera.getLens(0).getExposureMode(new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.ExposureMode>() {
                    @Override
                    public void onSuccess(SettingsDefinitions.ExposureMode exposureMode) {
                        webInitializationBean.setExposureMode(exposureMode.value());
                    }

                    @Override
                    public void onFailure(DJIError djiError) {
                    }
                });
                //返回iso数据
                camera.getLens(camera.getIndex()).getISO(new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.ISO>() {
                    @Override
                    public void onSuccess(SettingsDefinitions.ISO iso) {
                        webInitializationBean.setISO(iso.value());
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });
                //返回shutter数据
                camera.getLens(0).getShutterSpeed(new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.ShutterSpeed>() {
                    @Override
                    public void onSuccess(SettingsDefinitions.ShutterSpeed shutterSpeed) {
                        webInitializationBean.setShutter(shutterSpeed.value());
                    }

                    @Override
                    public void onFailure(DJIError djiError) {
                    }
                });
                //返回曝光补偿
                camera.getLens(camera.getIndex()).getExposureCompensation(new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.ExposureCompensation>() {
                    @Override
                    public void onSuccess(SettingsDefinitions.ExposureCompensation exposureCompensation) {
                        webInitializationBean.setExposureCompensation(exposureCompensation.value());
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });

                camera.getLens(0).getFocusMode(new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.FocusMode>() {
                    @Override
                    public void onSuccess(SettingsDefinitions.FocusMode focusMode) {
                        webInitializationBean.setFocusMode(focusMode.value() + "");
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });

                camera.getLens(0).getAELock(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        webInitializationBean.setLockExposure(aBoolean ? "0" : "1");
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });

                camera.getLens(2).getThermalDigitalZoomFactor(new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.ThermalDigitalZoomFactor>() {
                    @Override
                    public void onSuccess(SettingsDefinitions.ThermalDigitalZoomFactor thermalDigitalZoomFactor) {
                        webInitializationBean.setThermalDigitalZoom(thermalDigitalZoomFactor.value() + "");
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });

                camera.getLens(2).getDisplayMode(new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.DisplayMode>() {
                    @Override
                    public void onSuccess(SettingsDefinitions.DisplayMode displayMode) {
                        webInitializationBean.setHyDisplayMode(displayMode.value() + "");
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });


//


                //获取变焦距离
//            camera.getLens(camera.getIndex()).getHybridZoomSpec(new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.HybridZoomSpec>() {
//                @Override
//                public void onSuccess(SettingsDefinitions.HybridZoomSpec hybridZoomSpec) {
//
//                    Log.d("HHHHHFocalLength",hybridZoomSpec.getFocalLengthStep()+"");//11
//                    Log.d("HHHHHMaxH",hybridZoomSpec.getMaxHybridFocalLength()+"");//55620
//                    Log.d("HHHHHMinH",hybridZoomSpec.getMinHybridFocalLength()+"");//317
//                    Log.d("HHHHHMaxO",hybridZoomSpec.getMaxOpticalFocalLength()+"");//5562
//                }
//
//                @Override
//                public void onFailure(DJIError djiError) {
//
//                }
//            });
                //获取当前变焦焦距
                camera.getLens(0).getHybridZoomFocalLength(new CommonCallbacks.CompletionCallbackWith<Integer>() {
                    @Override
                    public void onSuccess(Integer integer) {
//                    Log.d("HHHHHcurr", integer + "");
                        webInitializationBean.setHybridZoom(integer);
                    }

                    @Override
                    public void onFailure(DJIError djiError) {
//                    Log.d("HHHHHcurr", djiError.toString());
                    }
                });
            }


        }
    }

    String battery_one = "100", battery_two = "100", battery_voltages_one = "0", battery_voltages_two = "0", battery_temperature_one = "", battery_temperature_two = "";
    List<Float> battery_list_one = new ArrayList<>();
    List<Float> battery_list_two = new ArrayList<>();
    List<String> battery_list_per_one = new ArrayList<>();
    List<String> battery_list_per_two = new ArrayList<>();
    String battery_discharges_one = "", battery_discharges_two = "";
    boolean is_battery_one_change = false, is_battery_two_change = false;

    private void initBattery() {
//        if (FPVDemoApplication.getProductInstance().getBattery() != null) {
//            battery = FPVDemoApplication.getProductInstance().getBattery();
//            battery.setStateCallback(new BatteryState.Callback() {
//                @Override
//                public void onUpdate(BatteryState batteryState) {
//                    batteryState.getChargeRemainingInPercent();
//                }
//            });
//        }

//        FPVDemoApplication.getProductInstance().getBatteries().get(0).setStateCallback(new BatteryState.Callback() {
//            @Override
//            public void onUpdate(BatteryState batteryState) {
//                Log.d("Batteries1",""+batteryState.getChargeRemainingInPercent());
//            }
//        });
//        FPVDemoApplication.getProductInstance().getBatteries().get(1).setStateCallback(new BatteryState.Callback() {
//            @Override
//            public void onUpdate(BatteryState batteryState) {
//                Log.d("Batteries2",""+batteryState.getChargeRemainingInPercent());
//            }
//        });

//        Battery.setAggregationStateCallback(new AggregationState.Callback() {
//            @Override
//            public void onUpdate(AggregationState aggregationState) {
//                BatteryOverview[] batteryOverviews= aggregationState.getBatteryOverviews();
//                for (int i = 0; i <batteryOverviews.length ; i++) {
//                    Log.d("batteryOverviews","当前"+i+"电量"+batteryOverviews[i].getChargeRemainingInPercent());
//                }
//
//            }
//        });


//


        BatteryKey battery_per_one = BatteryKey.create(BatteryKey.CHARGE_REMAINING_IN_PERCENT);
        BatteryKey battery_per_two = BatteryKey.create(BatteryKey.CHARGE_REMAINING_IN_PERCENT, 1);


        BatteryKey battery_voltage_one = BatteryKey.create(BatteryKey.CELL_VOLTAGES);
        BatteryKey battery_voltage_two = BatteryKey.create(BatteryKey.CELL_VOLTAGES, 1);
        BatteryKey temperature_one = BatteryKey.create(BatteryKey.TEMPERATURE);
        BatteryKey temperature_two = BatteryKey.create(BatteryKey.TEMPERATURE, 1);
        BatteryKey discharges_one = BatteryKey.create(BatteryKey.NUMBER_OF_DISCHARGES);
        BatteryKey discharges_two = BatteryKey.create(BatteryKey.NUMBER_OF_DISCHARGES, 1);
        KeyManager.getInstance().addListener(battery_per_one, new KeyListener() {
            @Override
            public void onValueChange(Object o, Object o1) {
                if (fastClick.batteryClick()) {
                    is_battery_one_change = true;
                    battery_one = o1.toString();
                    if (!is_battery_two_change) {
                        battery_two = o1.toString();
                    }
                    Log.d("battery_one", battery_one);
                    if (o1 != null && o1 instanceof Integer[]) {
                        for (int i = 0; i < ((Integer[]) o1).length; i++) {
                            int aaa = ((Integer[]) o1)[i];
                            battery_list_per_one.add(aaa + "");
                        }
                    }
                    Log.d("battery_one", battery_list_per_one.toString());
                    submitBatteryPersentAndV();


                }

            }
        });
        KeyManager.getInstance().addListener(battery_per_two, new KeyListener() {
            @Override
            public void onValueChange(Object o, Object o1) {
                if (fastClick.batteryClick()) {
                    battery_two = o1.toString();
                    is_battery_two_change = true;
                    if (!is_battery_one_change) {
                        battery_one = o1.toString();
                    }

                    Log.d("battery_two", battery_two);
                    if (o1 != null && o1 instanceof Integer[]) {
                        for (int i = 0; i < ((Integer[]) o1).length; i++) {
                            int aaa = ((Integer[]) o1)[i];
                            battery_list_per_two.add(aaa + "");
                        }
                    }
                    Log.d("battery_two", battery_list_per_two.toString());
                    submitBatteryPersentAndV();
                }
            }
        });
        //十几个电池先驻掉有用
        KeyManager.getInstance().addListener(battery_voltage_one, new KeyListener() {
            @Override
            public void onValueChange(Object o, Object o1) {
                if (fastClick.batteryClick()) {
                    battery_voltages_one = getMinVoltage(o1) + "";
                    if (o1 != null && o1 instanceof Integer[]) {
                        for (int i = 0; i < ((Integer[]) o1).length; i++) {
                            int aaa = ((Integer[]) o1)[i];
                            battery_list_one.add((float) (aaa * 1.0F / 1000.0F));
                        }
                    }
                    submitBatteryPersentAndV();
                }

            }
        });
        KeyManager.getInstance().addListener(battery_voltage_two, new KeyListener() {
            @Override
            public void onValueChange(Object o, Object o1) {
                if (fastClick.batteryClick()) {
                    battery_voltages_two = getMinVoltage(o1) + "";
                    if (o1 != null && o1 instanceof Integer[]) {
                        for (int i = 0; i < ((Integer[]) o1).length; i++) {
                            int aaa = ((Integer[]) o1)[i];
                            battery_list_two.add((float) (aaa * 1.0F / 1000.0F));
                        }

                    }
                    submitBatteryPersentAndV();
                }
            }
        });
        KeyManager.getInstance().addListener(temperature_one, new KeyListener() {
            @Override
            public void onValueChange(Object o, Object o1) {
                battery_temperature_one = o1 + "";
                submitBatteryPersentAndV();
            }
        });
        KeyManager.getInstance().addListener(temperature_two, new KeyListener() {
            @Override
            public void onValueChange(Object o, Object o1) {
                battery_temperature_two = o1 + "";
                submitBatteryPersentAndV();
            }
        });
        KeyManager.getInstance().addListener(discharges_one, new KeyListener() {
            @Override
            public void onValueChange(Object o, Object o1) {
                battery_discharges_one = o1 + "";
                submitBatteryPersentAndV();
            }
        });
        KeyManager.getInstance().addListener(discharges_two, new KeyListener() {
            @Override
            public void onValueChange(Object o, Object o1) {
                battery_discharges_two = o1 + "";
                submitBatteryPersentAndV();
            }
        });
        //第一次获取电量
        KeyManager.getInstance().getValue(battery_per_one, new GetCallback() {
            @Override
            public void onSuccess(Object o) {
                int b_one = (Integer) o;
                battery_one = b_one + "";
                KeyManager.getInstance().getValue(battery_per_two, new GetCallback() {
                    @Override
                    public void onSuccess(Object o) {
                        int b_two = (Integer) o;
                        battery_two = b_two + "";
                        submitBatteryPersentAndV();
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });
            }

            @Override
            public void onFailure(DJIError djiError) {

            }
        });


    }

    String gimbal_pitch_speed = "", gimbal_yaw_speed = "";

    private void initGimbal() {
        gimbal = FPVDemoApplication.getProductInstance().getGimbal();
        if (gimbal != null) {
            gimbal.getControllerSpeedCoefficient(PITCH, new CommonCallbacks.CompletionCallbackWith<Integer>() {
                @Override
                public void onSuccess(Integer integer) {
                    gimbal_pitch_speed = integer + "";
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            });
            gimbal.getControllerSpeedCoefficient(YAW, new CommonCallbacks.CompletionCallbackWith<Integer>() {
                @Override
                public void onSuccess(Integer integer) {
                    gimbal_yaw_speed = integer + "";
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            });

//            gimbal.getYawRelativeToAircraftHeading
            //监听左右和上下云台的度数
            gimbal.setStateCallback(new GimbalState.Callback() {
                @Override
                public void onUpdate(GimbalState gimbalState) {//左右y 上下p
                    webInitializationBean.setHorizontalAngle(gimbalState.getAttitudeInDegrees().getYaw() + "");
                    webInitializationBean.setPitchAngle(gimbalState.getAttitudeInDegrees().getPitch() + "");
                    gimbalStatePitch = gimbalState.getAttitudeInDegrees().getPitch() + "";
                }
            });
            //云台俯仰限位扩展
            gimbal.getPitchRangeExtensionEnabled(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
                @Override
                public void onSuccess(Boolean aBoolean) {
                    pitchRangeExtension = aBoolean;
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            });


            gimbal.getControllerSmoothingFactor(PITCH, new CommonCallbacks.CompletionCallbackWith<Integer>() {
                @Override
                public void onSuccess(Integer integer) {
                    pitch_CSF = integer.toString();

                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            });

            gimbal.getControllerSmoothingFactor(YAW, new CommonCallbacks.CompletionCallbackWith<Integer>() {
                @Override
                public void onSuccess(Integer integer) {
                    yaw_CSF = integer.toString();
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            });


        }


    }

    private void submitBatteryPersentAndV() {
        BatteryPersentAndVoltageBean batteryPersentAndVoltageBean = new BatteryPersentAndVoltageBean();
        batteryPersentAndVoltageBean.setPersentOne(battery_one);
        batteryPersentAndVoltageBean.setPersentTwo(battery_two);
        batteryPersentAndVoltageBean.setVoltageOne(battery_voltages_one);
        batteryPersentAndVoltageBean.setVoltageTwo(battery_voltages_two);
        batteryPersentAndVoltageBean.setBattery_list_one(battery_list_one);
        batteryPersentAndVoltageBean.setBattery_list_two(battery_list_two);
        batteryPersentAndVoltageBean.setBattery_list_per_one(battery_list_per_one);
        batteryPersentAndVoltageBean.setBattery_list_per_two(battery_list_per_two);
        batteryPersentAndVoltageBean.setBattery_temperature_one(battery_temperature_one);
        batteryPersentAndVoltageBean.setBattery_temperature_two(battery_temperature_two);
        batteryPersentAndVoltageBean.setBattery_discharges_one(battery_discharges_one);
        batteryPersentAndVoltageBean.setBattery_discharges_two(battery_discharges_two);
        if (communication_battery == null) {
            communication_battery = new Communication();
        }
        communication_battery.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
        communication_battery.setEquipmentId(MApplication.EQUIPMENT_ID);
        communication_battery.setMethod((Constant.BatteryPAV));
        communication_battery.setResult(gson.toJson(batteryPersentAndVoltageBean, BatteryPersentAndVoltageBean.class));
        NettyClient.getInstance().sendMessage(communication_battery, null);
    }


    //长连接最终返回
    @Override
    protected void notifyData(String json) {
        Log.d(TAG, "张闯返回数据=" + json);
        if (TextUtils.isEmpty(json)) {
            return;
        }
        Communication communication = gson.fromJson(json, Communication.class);
        String method = communication.getMethod();
        communication.setMethod((method));

        switch (method) {
            case Constant.LIVE_PATH://后台拿到的推流地址
                liveShowUrl = communication.getPara().get("desRtmpUrl");
                et_url.setText(liveShowUrl);
                Log.d("desRtmpUrl", liveShowUrl);
//                startLiveShow(null);
                break;
            //起飞
            case Constant.START_TAKE_OFF:
                DeleteUtil.deleteDirectory(destDirName);//删除本地文件
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
            //取消降落
            case Constant.CANCEL_LANDING:
                cancelLanding(communication);
                break;
            //云台左右
            case Constant.CAMERA_LEFT_AND_RIGHT:
                camera_left_and_right(communication);
                break;
            //云台上下
            case Constant.CAMERA_UP_AND_DOWN:
                camera_up_and_down(communication);
                break;
            //云台上下固定度数
            case Constant.CAMERA_UP_AND_DOWN_BY_ABSOLUTE_ANGLE:
                camera_up_and_down_by_a(communication);
                break;
            //云台回正
            case Constant.CAMERA_CENTER:
                camera_center(communication);
                break;
            //云台垂直回正
            case Constant.CAMERA_CENTER_PITCH:
                camera_center_pitch(communication);
                break;
            //云台水平回正
            case Constant.CAMERA_CENTER_YAW:
                camera_center_yaw(communication);
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
            //航点规划V2
            case Constant.WAYPOINT_PLAN_V2:
                waypoint_plan_V2(communication);
                break;
            //航线自动飞行开始
            case Constant.WAYPOINT_FLY_START:
                startWaypointMission(communication);
                break;
            //航线自动飞行开始V2
            case Constant.WAYPOINT_FLY_START_V2:
                startWaypointV2(communication);
                break;
            //航线自动飞行停止
            case Constant.WAYPOINT_FLY_STOP:
                stopWaypointMission(communication);
                break;
            //航线自动飞行停止V2
            case Constant.WAYPOINT_FLY_STOP_V2:
                stopWaypointV2(communication);
                break;
            //航线暂停
            case Constant.WAYPOINT_FLY_PAUSE:
                pauseWaypointMission(communication);
                break;
            //航线航线暂停V2
            case Constant.WAYPOINT_FLY_PAUSE_V2:
                pauseWaypointV2(communication);
                break;
            //航线恢复
            case Constant.WAYPOINT_FLY_RESUME:
                resumeWaypointMission(communication);
                break;
            //航线恢复V2
            case Constant.WAYPOINT_FLY_RESUME_V2:
                resumeWaypointV2(communication);
                break;
            //回家
            case Constant.GO_HOME:
                startGoHome(communication);
                break;
            //取消回家
            case Constant.CANCEL_GO_HOME:
                CancelGoHome(communication);
                break;
            //切换广角镜头变焦镜头红外镜头
            case Constant.CHANGE_LENS:
                change_lens(communication);
                break;
            //切换普通视角FPV视角
            case Constant.CHANGE_CAMERA_FPV_VISUAL:
                change_see_view(communication);
                break;
            //切换相机模式
            case Constant.CHANGE_CAMERA_MODE:
                camere_set_mode(communication);
                break;
            //切换相机拍照模式
            case Constant.CHANGE_CAMERA_SHOOT_MODE:

                camera_set_shoot_mode(communication);
                break;
            //开始拍照
            case Constant.CAMERE_START_SHOOT:
                camera_start_shoot(communication);

                break;
            //结束拍照
            case Constant.CAMERE_STOP_SHOOT:
                camera_stop_shoot(communication);
                break;
            //开始录像
            case Constant.CAMERE_START_RECORE:
                camera_start_recode(communication);
                break;
            //结束录像
            case Constant.CAMERE_STOP_RECORE:
                camera_stop_recode(communication);
                break;
            //启用或禁用激光测距
            case Constant.SET_LASER_ENABLE:
                if (isM300Product()) {
                    setLaserEnable(communication);
                }
                break;
            //判断是否开启或警用激光测距
            case Constant.GET_LASER_ENABLE:
                if (isM300Product()) {
                    getLaserEnable(communication);
                }
                break;
            //设置灯光
            case Constant.SET_LED:
                setLed(communication);
                break;
            //设置ISO
            case Constant.SET_ISO:
                setISO(communication);
                break;
            //设置曝光补偿
            case Constant.SET_EXPOSURE_COM:
                setExposureCom(communication);
                break;
            //设置曝光模式
            case Constant.SET_EXPOSURE_MODE:
                setExposureMode(communication);
                break;
            //设置shutter
            case Constant.SET_SHUTTER:
                setShutter(communication);
                break;
            //设置变焦
            case Constant.SET_CAMERA_ZOOM:
                setCameraZoom(communication);
                break;
            //设置对焦模式
            case Constant.SET_FOCUS_MODE:
                camere_set_focus_mode(communication);
                break;
            //曝光锁定
            case Constant.LOCK_EXPOSURE:
                camere_set_lock_exposure(communication);
                break;
            //设置热像仪变焦
            case Constant.SET_THERMAL_DIGITAL_ZOOM:
                camera_setThermalDigitalZoom(communication);
                break;

            //获取初始数据
            case Constant.GET_INITIALIZATION_DATA:
                communication.setResult(gson.toJson(webInitializationBean, WebInitializationBean.class));
                communication.setCode(200);
                communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                NettyClient.getInstance().sendMessage(communication, null);
                break;
            //登录
            case "login":
                loginAccount();
                break;
            //注销
            case "loginOut":
                loginOut();
                break;
            //设置失控后飞机执行的操作
            case Constant.SET_CONNECT_FAIL_BEHAVIOR:
                setConnectionFailBehavior(communication);
                break;
            //格式化内存卡
            case Constant.FORMAT_SDCARD:
                formatSDCard(communication);
                break;
            //设置拍照时led自动开关
            case Constant.SET_LED_AUTO_TURN_OFF:
                setBeaconAutoTurnOffEnabled(communication);
                break;
            //设置调色板
            case Constant.SET_THERMAL_PALETTE:
                setThermalPalette(communication);
                break;
            //设置等温线
            case Constant.SET_THERMAL_ISO_THERM_UNIT:
                setThermalIsothermEnabled(communication);
                break;
            //设置水印
            case Constant.SET_WATER_MARK_SETTINGS:
                setWatermarkSettings(communication);
                break;
            //相机重置参数（恢复出厂设置）
            case Constant.CAMERA_RFS:
                cameraRestoreFactorySettings(communication);
                break;
            //开始推流
            case Constant.START_LIVE:
                startLiveShow(communication);
                break;
            //结束推流
            case Constant.STOP_LIVE:
                DJISDKManager.getInstance().getLiveStreamManager().stopStream();
                Toast.makeText(getApplicationContext(), "Stop Live Show", Toast.LENGTH_SHORT).show();
                break;
            //重启app
            case Constant.RESTART_APP:
                Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                intent.putExtra("REBOOT", "reboot");
                PendingIntent restartIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
                AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent);
                android.os.Process.killProcess(android.os.Process.myPid());
                //设置返航高度
            case Constant.SET_GO_HOME_HEIGHT:
                setGoHomeHeight(communication);
                break;
            //设置限高
            case Constant.SET_MAX_HEIGHT:
                setMaxHeight(communication);
                break;
            //设置重心校准
            case Constant.SET_GRAVITY_CENTER_STATE:
                setGravityCenterState(communication);
                break;
            //设置是否启用最大飞行半径限制(传0没有传1有 然后就是设置值)(type,value)
            case Constant.SET_MFRL:
                setMaxFlightRadiusLimit(communication);
                break;
            //开始校准IMU
            case Constant.START_IMU:
                setIMUStart(communication);
                break;
            //获取设置的一些默认参数
            case Constant.GET_SETTING_DATA:
                getSettingValue(communication);
                break;
            //设置低电量
            case Constant.SET_LOW_BATTERY:
                setLowBattery(communication);
                break;
            //设置严重低电量
            case Constant.SET_SERIOUS_LOW_BATTERY:
                setSeriousLowBattery(communication);
                break;
            //设置智能返航
            case Constant.SET_SMART_GOHOME:
                setSmartGoHome(communication);
                break;
            //设置视觉定位
            case Constant.SET_VISION_ASSISTED:
                setVisionAssistedPosition(communication);
                break;
            //设置精确着陆
            case Constant.SET_PRECISION_LAND:
                setPrecisionLand(communication);
                break;
            //向上避障
            case Constant.SET_UPWARDS_AVOIDANCE:
                setUpwardsAvoidance(communication);
                break;
            //向下避障
            case Constant.SET_LANDING_PROTECTION:
                setLandingProtection(communication);
                break;
            //设置上下感知距离
            case Constant.SET_MAX_PERCEPTION_DISTANCE:
                setMaxPerceptionDistance(communication);
                break;
            //设置上下避障安全距离
            case Constant.SET_AVOIDANCE_DISTANCE:
                setAvoidanceDistance(communication);
                break;
            //设置避障刹车功能
            case Constant.SET_ACTIVITY_OBSTACLE_AVOIDANCE:
                setActiveObstacleAvoidance(communication);
                break;
            //设置偏航俯仰速度
            case Constant.SET_GIMBAL_SPEED:
                setGimbalSpeed(communication);
                break;
            //恢复出厂
            case Constant.RESTORE_FACTORY:
                restoreFactorySettings(communication);
                break;
            //自动校准
            case Constant.START_CALIBRATION:
                startCalibration(communication);
                break;
            //云台偏航缓启停(0,30)
            case Constant.SET_CONTROLLER_SMOOTHING:
                setControllerSmoothingFactor(communication);
                break;

            //rtk开关
            case Constant.SET_RTK:
                setRTK(communication);
                break;
            //开始搜索基站
            case Constant.START_SET_BS:
                startSearchBS(communication);
                break;
            //结束搜索基站
            case Constant.STOP_SET_BS:
                stopSearchBS(communication);
                break;
            //连接基站
            case Constant.CONNECT_BS:
                connectBS(communication);
                break;
            //设置参考站源
            case Constant.SET_RSS:
                setRSS(communication);
                break;
            //设置坐标系
            case Constant.SET_RTK_NETWORK:
                setRTKNetwork(communication);
                break;


            //设置云台限位扩展
            case Constant.PITCH_RANGE_EXTENSION:
                setPitchRangeExtension(communication);
                break;
            //设置工作频段
            case Constant.SET_FREQUENCY_BAND:
                setFrequencyBand(communication);
                break;
            //设置云台模式
            case Constant.SET_GIMBAL_MODE:
                setGimbalMode(communication);
                break;
            //设置红外展示模式
            case Constant.SET_HY_DISPLAY_MODE:
                setHyDisplayMode(communication);
                break;
            case "print":
                TestPrint(communication);
                break;
            //判断是否在飞
            case "getIsFlying":
                communication.setResult(lastFlying?"1":"0");
                communication.setCode(200);
                communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                NettyClient.getInstance().sendMessage(communication, null);
                break;
            //获取返航点经纬度
            case "getSLngLat":
                if(goHomeLat==0&&goHomeLong==0){
                    communication.setResult("undefined");
                }else{
                    LatLng latLngHome = MapConvertUtils.getGDLatLng(goHomeLat,goHomeLong,ConnectionActivity.this);
                    communication.setResult(latLngHome.longitude+","+ latLngHome.latitude);
                }

                communication.setCode(200);
                communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                NettyClient.getInstance().sendMessage(communication, null);
                break;
            //抓拍模式
            case Constant.CAPTURE:
                isCapture=true;
                setCapture(communication);
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
//                            CommonDjiCallback(djiError, communication);
                            if (djiError == null) {//起飞成功调用10次上升
                                communication.setResult("Success");
                                communication.setCode(200);
                                communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                                NettyClient.getInstance().sendMessage(communication, null);

                                for (int i = 0; i < 10; i++) {
                                    mThrottle = 2;
                                    mPitch = 0;
                                    mRoll = 0;
                                    mYaw = 0;
                                    if (mFlightController != null) {
                                        mFlightController.sendVirtualStickFlightControlData(
                                                new FlightControlData(
                                                        mPitch, mRoll, mYaw, mThrottle
                                                ), new CommonCallbacks.CompletionCallback() {
                                                    @Override
                                                    public void onResult(DJIError djiError) {

                                                    }
                                                }
                                        );
                                    }
                                }
                            } else {
                                communication.setResult(djiError.getDescription());
                                communication.setCode(-1);
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
                    CommonDjiCallback(djiError, communication);
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
                    CommonDjiCallback(djiError, communication);
                }
            });
        }
    }

    //降落
    private void startLanding(Communication communication) {
        if (mFlightController != null) {
            mFlightController.startLanding(
                    new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            CommonDjiCallback(djiError, communication);
                        }
                    }
            );
        }
    }

    //取消降落
    private void cancelLanding(Communication communication) {
        if (mFlightController != null) {
            mFlightController.cancelLanding(
                    new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            CommonDjiCallback(djiError, communication);
                        }
                    }
            );
        }
    }

    //回家
    private void startGoHome(Communication communication) {
        if (mFlightController != null) {
            mFlightController.startGoHome(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    CommonDjiCallback(djiError, communication);
                }
            });
        }
    }

    //暂停回家
    private void CancelGoHome(Communication communication) {
        if (mFlightController != null) {
            mFlightController.cancelGoHome(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    CommonDjiCallback(djiError, communication);
                }
            });
        }
    }

    //摄像头上下 30~-90
    private void camera_up_and_down(Communication communication) {
        double PitchAngle = Double.parseDouble(webInitializationBean.getPitchAngle());
        double angle = Double.parseDouble(communication.getPara().get(Constant.ANGLE));
        boolean canmove;
        if (PitchAngle + angle > 30 || PitchAngle + angle < -90) {
            canmove = false;
        } else {
            canmove = true;
        }

        Log.d("PitchAngle", "PitchAngle=" + PitchAngle + "");
        if (canmove) {
//            String angle = communication.getPara().get(Constant.ANGLE);
            Log.d("PitchAngle", "angle=" + angle + "");
            if (!TextUtils.isEmpty(angle + "")) {

                if (gimbal == null) {
                    gimbal = FPVDemoApplication.getProductInstance().getGimbal();
                }
                Rotation.Builder builder = new Rotation.Builder().mode(RotationMode.RELATIVE_ANGLE).time(2);
                builder.pitch(Float.parseFloat(angle + ""));
                builder.build();

                gimbal.rotate(builder.build(), new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (djiError != null) {
                            communication.setResult(djiError.getDescription());
                            communication.setCode(-1);
                            communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                            NettyClient.getInstance().sendMessage(communication, null);
                        } else {
                            communication.setResult((Double.parseDouble(webInitializationBean.getPitchAngle()) + angle) + "");
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
        } else {
            communication.setResult("云台角度已达到最大值");
            communication.setCode(-1);
            communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            NettyClient.getInstance().sendMessage(communication, null);
        }

    }

    //摄像头上下 30~-90
    private void camera_up_and_down_by_a(Communication communication) {

        double PitchAngle = Double.parseDouble(webInitializationBean.getPitchAngle());
        double angle = Double.parseDouble(communication.getPara().get(Constant.ANGLE));
        boolean canmove;
        if (PitchAngle + angle > 30 || PitchAngle + angle < -90) {
            canmove = false;
        } else {
            canmove = true;
        }

        Log.d("PitchAngle", "PitchAngle=" + PitchAngle + "");
        if (canmove) {
//            String angle = communication.getPara().get(Constant.ANGLE);
            Log.d("PitchAngle", "angle=" + angle + "");
            if (!TextUtils.isEmpty(angle + "")) {

                if (gimbal == null) {
                    gimbal = FPVDemoApplication.getProductInstance().getGimbal();
                }
                Rotation.Builder builder = new Rotation.Builder().mode(RotationMode.ABSOLUTE_ANGLE).time(2);
                builder.pitch(Float.parseFloat(angle + ""));
                builder.build();

                gimbal.rotate(builder.build(), new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if(isCapture){
                            setCameraZoom(communication);
                        }else{
                            if (djiError != null) {
                                communication.setResult(djiError.getDescription());
                                communication.setCode(-1);
                                communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                                NettyClient.getInstance().sendMessage(communication, null);
                            } else {
                                communication.setResult((Double.parseDouble(webInitializationBean.getPitchAngle()) + angle) + "");
                                communication.setCode(200);
                                communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                                NettyClient.getInstance().sendMessage(communication, null);
                            }
                        }

                    }
                });
            } else {
                communication.setResult("");
                communication.setCode(-1);
                communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                NettyClient.getInstance().sendMessage(communication, null);
            }
        } else {
            communication.setResult("云台角度已达到最大值");
            communication.setCode(-1);
            communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            NettyClient.getInstance().sendMessage(communication, null);
        }

    }

    double curr_gimbal = 0.0;//当前y轴度数

    //摄像头左右 -320~320
    private void camera_left_and_right(Communication communication) {
        //获取当前云台度数
        KeyManager.getInstance().getValue(gimbalKey1, new GetCallback() {
            @Override
            public void onSuccess(Object o) {
                Log.d("DDDDDDKeyManager", "KeyManager:" + o.toString());
                curr_gimbal = Double.parseDouble(o.toString());
            }

            @Override
            public void onFailure(DJIError djiError) {
            }
        });

        String angle = communication.getPara().get(Constant.ANGLE);
        Log.d("DDDDDDKeyManager", "angle:" + angle);
        if (!TextUtils.isEmpty(angle)) {
//            Rotation.Builder builder = new Rotation.Builder().mode(RotationMode.ABSOLUTE_ANGLE).time(2);
            if (gimbal == null) {
                gimbal = FPVDemoApplication.getProductInstance().getGimbal();
            }
            Rotation.Builder builder = new Rotation.Builder().mode(RotationMode.RELATIVE_ANGLE).time(2);
            builder.yaw(Float.parseFloat(angle));
            builder.build();
            gimbal.rotate(builder.build(), new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    //320-20
                    if (curr_gimbal > 300 || curr_gimbal < -300) {
                        communication.setResult("云台角度已达到最大值");
                        communication.setCode(-1);
                        communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                        NettyClient.getInstance().sendMessage(communication, null);
                    } else {
                        communication.setResult((curr_gimbal + Double.parseDouble(angle)) + "");
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

    //摄像头归零
    private void camera_center(Communication communication) {
        Gimbal gimbal = FPVDemoApplication.getProductInstance().getGimbal();
        if (gimbal == null) {
            return;
        }
        gimbal.reset(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                CommonDjiCallback(djiError, communication);
            }
        });

    }

    //    垂直回中
    private void camera_center_pitch(Communication communication) {
        Rotation.Builder builder = new Rotation.Builder().mode(RotationMode.ABSOLUTE_ANGLE).time(2);
        builder.pitch(Float.parseFloat("0"));
        builder.build();
        Gimbal gimbal = FPVDemoApplication.getProductInstance().getGimbal();
        if (gimbal == null) {
            return;
        }
        gimbal.rotate(builder.build(), new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                CommonDjiCallback(djiError, communication);
            }
        });

    }

    //水平回中
    private void camera_center_yaw(Communication communication) {
        Gimbal gimbal = FPVDemoApplication.getProductInstance().getGimbal();
        if (gimbal == null) {
            return;
        }
        gimbal.resetYaw(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                CommonDjiCallback(djiError, communication);
            }
        });
    }

    //切换镜头
    private void change_lens(Communication communication) {
        String type="";
        if(isCapture){
            type="2";
        }else{
            type = communication.getPara().get(Constant.TYPE);
        }

        if (isM300Product()) {
            CameraVideoStreamSource source = DEFAULT;
            switch (type) {
                case "1":
                    source = WIDE;
                    break;
                case "2":
                    source = ZOOM;
                    break;
                case "3":
                    source = INFRARED_THERMAL;
                    break;

            }
            webInitializationBean.setCurrentLens(type);
            if (camera != null) {
                camera.setCameraVideoStreamSource(source, new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if(isCapture){//抓拍
                            camera_up_and_down_by_a(communication);
                        }else{
                            CommonDjiCallback(djiError, communication);
                        }
                    }
                });
            }
        } else {
            communication.setResult("型号不支持");
            communication.setCode(-1);
            communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            NettyClient.getInstance().sendMessage(communication, null);
        }


    }

    //切换摄像头还是fpv视角
    private void change_see_view(Communication communication) {
        if (isM300Product()) {
            String type = communication.getPara().get(Constant.TYPE);
            DJICodecManager.VideoSource source = CAMERA;
            switch (type) {
                case "0":
                    source = CAMERA;
                    currentVideoSource = "0";
                    break;
                case "1":
                    source = FPV;
                    currentVideoSource = "1";
                    break;
            }
            webInitializationBean.setCurrentVideoSource(currentVideoSource);
            if (mCodecManager != null) {
                mCodecManager.switchSource(source);
                communication.setResult("Success");
                communication.setCode(200);
                communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                NettyClient.getInstance().sendMessage(communication, null);
            } else {
                communication.setResult("error");
                communication.setCode(-1);
                communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                NettyClient.getInstance().sendMessage(communication, null);
            }

        } else {
            communication.setResult("型号不支持");
            communication.setCode(-1);
            communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            NettyClient.getInstance().sendMessage(communication, null);
        }

    }

    //设置相机模式
    private void camere_set_mode(Communication communication) {

        camera.exitPlayback(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                if (djiError == null) {
                    String type = communication.getPara().get(Constant.TYPE);
                    if (camera != null) {
                        SettingsDefinitions.CameraMode cameraMode = SettingsDefinitions.CameraMode.SHOOT_PHOTO;
                        switch (type) {
                            case "0":
                                cameraMode = SettingsDefinitions.CameraMode.SHOOT_PHOTO;
                                break;
                            case "1":
                                cameraMode = SettingsDefinitions.CameraMode.RECORD_VIDEO;
                                break;

                        }
                        camera.setMode(cameraMode, new CommonCallbacks.CompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
                                CommonDjiCallback(djiError, communication);
                            }
                        });

                    } else {
                        communication.setResult("camera==null");
                        communication.setCode(-1);
                        communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                        NettyClient.getInstance().sendMessage(communication, null);
                    }
                } else {
                    communication.setResult(djiError.toString());
                    communication.setCode(-1);
                    communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                    NettyClient.getInstance().sendMessage(communication, null);
                }
            }
        });
    }

    //切换相机拍照模式
    private void camera_set_shoot_mode(Communication communication) {
        String type = communication.getPara().get(Constant.TYPE);
        if (camera != null) {
            camera.getMode(new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.CameraMode>() {
                @Override
                public void onSuccess(SettingsDefinitions.CameraMode cameraMode) {
                    if (cameraMode == SettingsDefinitions.CameraMode.SHOOT_PHOTO) {
                        SettingsDefinitions.ShootPhotoMode shootPhoto = SettingsDefinitions.ShootPhotoMode.SINGLE;
                        switch (type) {
                            case "0":
                                shootPhoto = SettingsDefinitions.ShootPhotoMode.SINGLE;
                                break;
                            case "1":
                                shootPhoto = SettingsDefinitions.ShootPhotoMode.TIME_LAPSE;
//                                shootPhoto = SettingsDefinitions.ShootPhotoMode.BURST;
                                break;
                        }
                        camera.setShootPhotoMode(shootPhoto, new CommonCallbacks.CompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
                                if (djiError != null) {
                                    communication.setResult(djiError.getDescription());
                                    communication.setCode(-1);
                                    communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                                    NettyClient.getInstance().sendMessage(communication, null);
                                } else {
                                    if (type.equals("2")) {//延时模式
                                        int interval = Integer.parseInt(communication.getPara().get("interval"));//拍摄两张照片之间的时间间隔。
                                        int duration = Integer.parseInt(communication.getPara().get("duration"));//相机拍照的总时间。
                                        String fileFormat = communication.getPara().get("fileFormat");//文件格式。
                                        SettingsDefinitions.PhotoTimeLapseFileFormat photoTimeLapseFileFormat = null;
                                        switch (fileFormat) {
                                            case "0":
                                                photoTimeLapseFileFormat = SettingsDefinitions.PhotoTimeLapseFileFormat.VIDEO;
                                                break;
                                            case "1":
                                                photoTimeLapseFileFormat = SettingsDefinitions.PhotoTimeLapseFileFormat.JPEG_AND_VIDEO;
                                                break;
                                        }
                                        camera.setPhotoTimeLapseSettings(new PhotoTimeLapseSettings(interval, duration, photoTimeLapseFileFormat), new CommonCallbacks.CompletionCallback() {
                                            @Override
                                            public void onResult(DJIError djiError) {
                                                CommonDjiCallback(djiError, communication);
                                            }
                                        });
                                    }
                                    communication.setResult("Success");
                                    communication.setCode(200);
                                    communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                                    NettyClient.getInstance().sendMessage(communication, null);
                                }
                            }
                        });
                    } else {
                        communication.setResult("当前相机不为拍照模式");
                        communication.setCode(-1);
                        communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                        NettyClient.getInstance().sendMessage(communication, null);
                    }
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            });
        } else {
            communication.setResult("camera==null");
            communication.setCode(-1);
            communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            NettyClient.getInstance().sendMessage(communication, null);
        }

    }

    //开始拍照
    private void camera_start_shoot(Communication communication) {
        if (camera != null) {
            camera.startShootPhoto(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError != null) {
                        communication.setResult(djiError.getDescription());
                        communication.setCode(-1);
                        communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                        NettyClient.getInstance().sendMessage(communication, null);
                    } else {
                        communication.setResult("成功");
                        communication.setCode(200);
                        communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                        NettyClient.getInstance().sendMessage(communication, null);
                    }
                }
            });
        } else {
            communication.setResult("camera==null");
            communication.setCode(-1);
            communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            NettyClient.getInstance().sendMessage(communication, null);
        }
    }

    //结束拍照
    private void camera_stop_shoot(Communication communication) {
        if (camera != null) {
            camera.stopShootPhoto(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError != null) {
                        communication.setResult(djiError.getDescription());
                        communication.setCode(-1);
                        communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                        NettyClient.getInstance().sendMessage(communication, null);
                    } else {
                        communication.setResult("成功");
                        communication.setCode(200);
                        communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                        NettyClient.getInstance().sendMessage(communication, null);
                    }
                }
            });

        } else {
            communication.setResult("camera==null");
            communication.setCode(-1);
            communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            NettyClient.getInstance().sendMessage(communication, null);
        }
    }

    //开始录像
    private void camera_start_recode(Communication communication) {
        if (camera != null) {
            camera.startRecordVideo(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError != null) {
                        communication.setResult(djiError.getDescription());
                        communication.setCode(-1);
                        communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                        NettyClient.getInstance().sendMessage(communication, null);
                    } else {
                        communication.setResult("成功");
                        communication.setCode(200);
                        communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                        NettyClient.getInstance().sendMessage(communication, null);
                    }
                }
            });

        } else {
            communication.setResult("camera==null");
            communication.setCode(-1);
            communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            NettyClient.getInstance().sendMessage(communication, null);
        }
    }

    //停止录像
    private void camera_stop_recode(Communication communication) {
        if (camera != null) {
            camera.stopRecordVideo(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError != null) {
                        communication.setResult(djiError.getDescription());
                        communication.setCode(-1);
                        communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                        NettyClient.getInstance().sendMessage(communication, null);
                    } else {
//                        initMediaManager();//去下载和上传
                        communication.setResult("成功");
                        communication.setCode(200);
                        communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                        NettyClient.getInstance().sendMessage(communication, null);
                    }
                }
            });
        } else {
            communication.setResult("camera==null");
            communication.setCode(-1);
            communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            NettyClient.getInstance().sendMessage(communication, null);
        }
    }

    //设置激光测距
    private void setLaserEnable(Communication communication) {
        String type = communication.getPara().get(Constant.TYPE);
        if (camera != null) {
            boolean flag = false;
            switch (type) {
                case "0":
                    flag = true;
                    break;
                case "1":
                    flag = false;
                    break;

            }
            camera.setLaserEnabled(flag, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError != null) {
                        communication.setResult(djiError.getDescription());
                        communication.setCode(-1);
                        communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                        NettyClient.getInstance().sendMessage(communication, null);
                    } else {
                        //如果是打开 就发送数据
                        if (type.equals("0")) {
                            returnLaserData();
                        }
                        communication.setResult("Success");
                        communication.setCode(200);
                        communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                        NettyClient.getInstance().sendMessage(communication, null);
                    }
                }
            });

        } else {
            communication.setResult("camera==null");
            communication.setCode(-1);
            communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            NettyClient.getInstance().sendMessage(communication, null);
        }
    }

    //获取激光测距
    private void getLaserEnable(Communication communication) {
        if (camera != null) {

            //返回激光测距数据
            if (isM300Product()) {
                camera.getLaserEnabled(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        if (aBoolean) {
                            returnLaserData();
                        }
                        StringsBean stringsBean = new StringsBean();
                        stringsBean.setValue(aBoolean ? "0" : "1");
                        communication.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                        communication.setEquipmentId(MApplication.EQUIPMENT_ID);
                        communication.setResult(gson.toJson(stringsBean, StringsBean.class));
                        NettyClient.getInstance().sendMessage(communication, null);
                    }

                    @Override
                    public void onFailure(DJIError djiError) {
                        communication.setResult(djiError.getDescription());
                        communication.setCode(-1);
                        communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                        NettyClient.getInstance().sendMessage(communication, null);
                    }
                });
            }

        } else {
            communication.setResult("camera==null");
            communication.setCode(-1);
            communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            NettyClient.getInstance().sendMessage(communication, null);
        }
    }

    private void returnLaserData() {
        if (camera != null && isM300Product()) {
            camera.getLens(camera.getIndex()).setLaserMeasureInformationCallback(new LaserMeasureInformation.Callback() {
                @Override
                public void onUpdate(LaserMeasureInformation laserMeasureInformation) {
                    laserMeasureInformation.getTargetPoint();
                    LaserMeasureInformBean bean = new LaserMeasureInformBean();
                    bean.setAltitude(laserMeasureInformation.getTargetLocation().getAltitude() + "");
                    bean.setLatitude(laserMeasureInformation.getTargetLocation().getLatitude() + "");
                    bean.setLongitude(laserMeasureInformation.getTargetLocation().getLongitude() + "");
                    bean.setTargetDistance(laserMeasureInformation.getTargetDistance() + "");
                    if (communication_laser_status == null) {
                        communication_laser_status = new Communication();
                    }
                    communication_laser_status.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                    communication_laser_status.setEquipmentId(MApplication.EQUIPMENT_ID);
                    communication_laser_status.setMethod((Constant.LASER_DATA));
                    communication_laser_status.setResult(gson.toJson(bean, LaserMeasureInformBean.class));
                    NettyClient.getInstance().sendMessage(communication_laser_status, null);
                }
            });
        }
    }

    private boolean isM300Product() {
        if (DJISDKManager.getInstance().getProduct() == null) {
            return false;
        }
        Model model = DJISDKManager.getInstance().getProduct().getModel();
        return model == Model.MATRICE_300_RTK;
    }

    //设置灯光
    private void setLed(Communication communication) {//只要上下的
        String beacons = communication.getPara().get(Constant.BEACONS);//顶部底部
        String front = communication.getPara().get(Constant.FRONT);//前臂
        String rear = communication.getPara().get(Constant.REAR);//后壁
        String statusIndicator = communication.getPara().get(Constant.STATUS_INDICATOR);//指示灯

        Log.d("DDDDDD", "beacons=" + beacons + " front=" + front + " rear=" + rear + " statusIndicator=" + statusIndicator);
        if (mFlightController != null) {
            if (!TextUtils.isEmpty(beacons)) {
                LEDsSettings.Builder builder = new LEDsSettings.Builder().beaconsOn(beacons.equals("1") ? true : false)
                        .frontLEDsOn(front_b.equals("1") ? true : false).rearLEDsOn(front_b.equals("1") ? true : false).statusIndicatorOn(statusIndicator_b.equals("1") ? true : false);
                mFlightController.setLEDsEnabledSettings(builder.build(), new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        beacons_b = beacons;
                        webInitializationBean.setBeacons(beacons_b);
                        CommonDjiCallback(djiError, communication);

                    }
                });
            }

            if (!TextUtils.isEmpty(front)) {
                LEDsSettings.Builder builder = new LEDsSettings.Builder()
                        .frontLEDsOn(front.equals("1") ? true : false)
                        .rearLEDsOn(front.equals("1") ? true : false)
                        .beaconsOn(beacons_b.equals("1") ? true : false)
                        .statusIndicatorOn(statusIndicator_b.equals("1") ? true : false);
                mFlightController.setLEDsEnabledSettings(builder.build(), new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        front_b = front;
                        webInitializationBean.setFront(front_b);
                        rear_b = rear;
                        webInitializationBean.setRear(rear_b);
                        CommonDjiCallback(djiError, communication);
                    }
                });
            }

//            if (!TextUtils.isEmpty(rear)) {
//                LEDsSettings.Builder builder = new LEDsSettings.Builder()
//                        .rearLEDsOn(rear.equals("1") ? true : false);
//                mFlightController.setLEDsEnabledSettings(builder.build(), new CommonCallbacks.CompletionCallback() {
//                    @Override
//                    public void onResult(DJIError djiError) {
//                        rear_b = rear;
//                        webInitializationBean.setRear(rear_b);
//                        CommonDjiCallback(djiError, communication);
//                    }
//                });
//            }

            if (!TextUtils.isEmpty(statusIndicator)) {
                LEDsSettings.Builder builder = new LEDsSettings.Builder().statusIndicatorOn(statusIndicator.equals("1") ? true : false)
                        .frontLEDsOn(front_b.equals("1") ? true : false)
                        .rearLEDsOn(front_b.equals("1") ? true : false)
                        .beaconsOn(beacons_b.equals("1") ? true : false);
                mFlightController.setLEDsEnabledSettings(builder.build(), new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        statusIndicator_b = statusIndicator;
                        webInitializationBean.setStatusIndicator(statusIndicator_b);
                        CommonDjiCallback(djiError, communication);
                    }
                });

            }


        } else {
            communication.setResult("飞行控制类获取失败");
            communication.setCode(-1);
            communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            NettyClient.getInstance().sendMessage(communication, null);
        }
    }

    //设置iso
    private void setISO(Communication communication) {
        String type = communication.getPara().get(Constant.TYPE);
        if (isM300Product() && camera != null && !TextUtils.isEmpty(type)) {
            camera.getLens(0).setISO(SettingsDefinitions.ISO.find(Integer.parseInt(type)), new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    CommonDjiCallback(djiError, communication);
                    webInitializationBean.setISO(Integer.parseInt(type));
                }
            });
        }

    }

    //设置曝光补偿
    private void setExposureCom(Communication communication) {
        String type = communication.getPara().get(Constant.TYPE);
        if (isM300Product() && camera != null && !TextUtils.isEmpty(type)) {
            camera.getLens(0).setExposureCompensation(SettingsDefinitions.ExposureCompensation.find(Integer.parseInt(type)), new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    CommonDjiCallback(djiError, communication);
                    webInitializationBean.setExposureCompensation(Integer.parseInt(type));
                }
            });
        }
    }

    //设置曝光模式
    private void setExposureMode(Communication communication) {
        String type = communication.getPara().get(Constant.TYPE);
        if (isM300Product() && camera != null) {
            SettingsDefinitions.ExposureMode exposureMode = SettingsDefinitions.ExposureMode.UNKNOWN;
            switch (type) {
                case "1":
                    exposureMode = PROGRAM;
                    break;
                case "4":
                    exposureMode = MANUAL;
                    break;
            }
            camera.getLens(0).setExposureMode(exposureMode, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    CommonDjiCallback(djiError, communication);
                    webInitializationBean.setExposureMode(Integer.parseInt("type"));


                }
            });
        }
    }

    //设置shutter
    private void setShutter(Communication communication) {
        String type = communication.getPara().get(Constant.TYPE);
        if (isM300Product() && camera != null) {
            camera.getLens(0).setShutterSpeed(SettingsDefinitions.ShutterSpeed.find(Float.parseFloat(type)), new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    CommonDjiCallback(djiError, communication);
                    webInitializationBean.setShutter(Float.parseFloat(type));
                }
            });
        }
    }

    //设置变焦
    private void setCameraZoom(Communication communication) {
        if (isM300Product() && camera != null) {
            String type = communication.getPara().get("zoom");
            if (!TextUtils.isEmpty(type)) {
                camera.getLens(0).setHybridZoomFocalLength(Integer.parseInt(type), new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if(isCapture){
                            isCapture=false;
                        }else{

                        }
                        CommonDjiCallback(djiError, communication);
                        webInitializationBean.setHybridZoom(Integer.parseInt(type));
                    }
                });
            }

        }
    }


    //设置对焦模式
    private void camere_set_focus_mode(Communication communication) {
        String type = communication.getPara().get(Constant.TYPE);
        if (camera != null) {
            SettingsDefinitions.FocusMode focusMode = SettingsDefinitions.FocusMode.AUTO;
            switch (type) {
                case "0":
                    focusMode = SettingsDefinitions.FocusMode.MANUAL;
                    break;
                case "1":
                    focusMode = SettingsDefinitions.FocusMode.AUTO;
                    break;
                case "2":
                    focusMode = SettingsDefinitions.FocusMode.AFC;
                    break;
                default:
                    focusMode = SettingsDefinitions.FocusMode.UNKNOWN;
                    break;

            }
            webInitializationBean.setFocusMode(type);

            if (isM300Product()) {
                camera.getLens(0).setFocusMode(focusMode, new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        CommonDjiCallback(djiError, communication);
                    }
                });
            } else {
                communication.setResult("设备不支持");
                communication.setCode(-1);
                communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                NettyClient.getInstance().sendMessage(communication, null);
            }

        } else {
            communication.setResult("camera==null");
            communication.setCode(-1);
            communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            NettyClient.getInstance().sendMessage(communication, null);
        }
    }

    //设置曝光锁定
    private void camere_set_lock_exposure(Communication communication) {
        String type = communication.getPara().get(Constant.TYPE);
        boolean exposure = false;
        if (camera != null) {
            switch (type) {
                case "0":
                    exposure = true;
                    break;
                case "1":
                    exposure = false;
                    break;

            }
            webInitializationBean.setLockExposure(type);

            if (isM300Product()) {
                camera.getLens(0).setAELock(exposure, new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        CommonDjiCallback(djiError, communication);
                    }
                });

            } else {
                communication.setResult("设备不支持");
                communication.setCode(-1);
                communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                NettyClient.getInstance().sendMessage(communication, null);
            }

        } else {
            communication.setResult("camera==null");
            communication.setCode(-1);
            communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            NettyClient.getInstance().sendMessage(communication, null);
        }
    }

    //设置红外的焦距
    private void camera_setThermalDigitalZoom(Communication communication) {
        String type = communication.getPara().get(Constant.TYPE);
        SettingsDefinitions.ThermalDigitalZoomFactor thermalDigitalZoomFactor;
        if (camera != null) {
            switch (type) {
                case "0":
                    thermalDigitalZoomFactor = X_1;
                    break;
                case "1":
                    thermalDigitalZoomFactor = X_2;
                    break;
                case "2":
                    thermalDigitalZoomFactor = X_4;
                    break;
                case "3":
                    thermalDigitalZoomFactor = X_8;
                    break;
                default:
                    thermalDigitalZoomFactor = UNKNOWN;
                    break;

            }
            webInitializationBean.setThermalDigitalZoom(type);

            if (isM300Product()) {
                camera.getLens(2).setThermalDigitalZoomFactor(thermalDigitalZoomFactor, new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        CommonDjiCallback(djiError, communication);
                    }
                });

            } else {
                communication.setResult("设备不支持");
                communication.setCode(-1);
                communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                NettyClient.getInstance().sendMessage(communication, null);
            }

        } else {
            communication.setResult("camera==null");
            communication.setCode(-1);
            communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            NettyClient.getInstance().sendMessage(communication, null);
        }
    }

    //设置返航高度
    private void setGoHomeHeight(Communication communication) {
        String type = communication.getPara().get(Constant.TYPE);
        if (mFlightController != null && !TextUtils.isEmpty(type)) {
            mFlightController.setGoHomeHeightInMeters(Integer.parseInt(type), new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        goHomeHeightInMeters = type;
                    }
                    CommonDjiCallback(djiError, communication);
                }
            });
        }
    }

    //设置限高20~500
    private void setMaxHeight(Communication communication) {
        String type = communication.getPara().get(Constant.TYPE);
        if (mFlightController != null) {
            mFlightController.setMaxFlightHeight(Integer.parseInt(type), new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        maxFlightHeight = type;
                    }
                    CommonDjiCallback(djiError, communication);
                }
            });
        }
    }

    //设置失控状态做的操作
    private void setConnectionFailBehavior(Communication communication) {
        String type = communication.getPara().get(Constant.TYPE);
        mFlightController.setConnectionFailSafeBehavior(ConnectionFailSafeBehavior.find(Integer.parseInt(type)), new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                CommonDjiCallback(djiError, communication);
            }
        });
    }

    //设置中心校准
    private void setGravityCenterState(Communication communication) {
        mFlightController.setGravityCenterStateCallback(new GravityCenterState.Callback() {
            @Override
            public void onUpdate(GravityCenterState gravityCenterState) {
                if (gravityCenterState.getCalibrationState() != GravityCenterState.GravityCenterCalibrationState.SUCCESSFUL) {
                    communication.setResult("failed");
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

    private void setPrecisionLandingEnabled(Communication communication){

    }

    //设置是否启用最大半径限制(传0没有 传1有然后就是设置值)
    private void setMaxFlightRadiusLimit(Communication communication) {
        String type = communication.getPara().get(Constant.TYPE);
        String value = communication.getPara().get(Constant.VALUE);
        if (mFlightController != null) {
            mFlightController.setMaxFlightRadiusLimitationEnabled(type.equals("1") ? true : false, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        maxFlightRadiusLimitationEnabled = type.equals("1") ? true : false;
                        if (maxFlightRadiusLimitationEnabled && !TextUtils.isEmpty(value)) {

                            mFlightController.setMaxFlightRadius(Integer.parseInt(value), new CommonCallbacks.CompletionCallback() {
                                @Override
                                public void onResult(DJIError djiError) {
                                    if (djiError == null) {
                                        maxFlightRadius = value;
                                    }
                                    CommonDjiCallback(djiError, communication);
                                }
                            });
                        } else {
                            communication.setResult("success");
                            communication.setCode(200);
                            communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                            NettyClient.getInstance().sendMessage(communication, null);
                        }

                    } else {
                        communication.setResult(djiError.toString());
                        communication.setCode(-1);
                        communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                        NettyClient.getInstance().sendMessage(communication, null);
                    }
                }
            });
        }


    }

    //设置IMU
    private void setIMUStart(Communication communication) {
        mFlightController.startIMUCalibration(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                CommonDjiCallback(djiError, communication);
            }
        });
    }

    //获取设置的一些值
    private void getSettingValue(Communication communication) {
        SettingValueBean settingValueBean = new SettingValueBean();
        settingValueBean.setLowBatteryWarning(lowBatteryWarning);
        settingValueBean.setSeriousLowBatteryWarning(seriousLowBatteryWarning);
        settingValueBean.setSmartReturnToHomeEnabled(smartReturnToHomeEnabled);
        settingValueBean.setVisionAssistedPosition(visionAssistedPosition);
        settingValueBean.setPrecisionLand(precisionLand);
        settingValueBean.setUpwardsAvoidance(upwardsAvoidance);
        settingValueBean.setLandingProtection(landingProtection);
        settingValueBean.setCollisionAvoidance(collisionAvoidance);
        settingValueBean.setActiveObstacleAvoidance(activeObstacleAvoidance);
        settingValueBean.setAvoidanceDistanceUpward(avoidanceDistanceUpward);
        settingValueBean.setAvoidanceDistanceDownward(avoidanceDistanceDownward);
        settingValueBean.setAvoidanceDistanceHorizontal(avoidanceDistanceHorizontal);
        settingValueBean.setMaxPerceptionDistanceUpward(maxPerceptionDistanceUpward);
        settingValueBean.setMaxPerceptionDistanceDownward(maxPerceptionDistanceDownward);
        settingValueBean.setMaxPerceptionDistanceHorizontal(maxPerceptionDistanceHorizontal);
        settingValueBean.setGimbal_pitch_speed(gimbal_pitch_speed);
        settingValueBean.setGimbal_yaw_speed(gimbal_yaw_speed);
        settingValueBean.setChannelBandwidth(channelBandwidth);
        settingValueBean.setFrequencyBand(frequencyBand);
        settingValueBean.setInterferencePower(interferencePower);
        settingValueBean.setTranscodingDataRate(transcodingDataRate);

        settingValueBean.setPitchRangeExtension(pitchRangeExtension);
        settingValueBean.setPitch_CSF(pitch_CSF);
        settingValueBean.setYaw_CSF(yaw_CSF);
        settingValueBean.setGoHomeHeightInMeters(goHomeHeightInMeters);
        settingValueBean.setMaxFlightHeight(maxFlightHeight);
        settingValueBean.setMaxFlightRadius(maxFlightRadius);
        settingValueBean.setMaxFlightRadiusLimitationEnabled(maxFlightRadiusLimitationEnabled);


        if (communication == null) {
            communication = new Communication();
        }
        communication.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
        communication.setEquipmentId(MApplication.EQUIPMENT_ID);
        communication.setMethod((Constant.GET_SETTING_DATA));
        communication.setResult(gson.toJson(settingValueBean, SettingValueBean.class));
        NettyClient.getInstance().sendMessage(communication, null);
    }

    //设置低电量
    private void setLowBattery(Communication communication) {
        String type = communication.getPara().get(Constant.TYPE);
        mFlightController.setLowBatteryWarningThreshold(Integer.parseInt(type), new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                CommonDjiCallback(djiError, communication);
            }
        });
    }

    //设置严重低电量
    private void setSeriousLowBattery(Communication communication) {
        String type = communication.getPara().get(Constant.TYPE);
        mFlightController.setSeriousLowBatteryWarningThreshold(Integer.parseInt(type), new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                CommonDjiCallback(djiError, communication);
            }
        });
    }

    //设置智能返航
    private void setSmartGoHome(Communication communication) {
        String type = communication.getPara().get(Constant.TYPE);
        mFlightController.setSmartReturnToHomeEnabled(type.equals("1") ? true : false, new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                CommonDjiCallback(djiError, communication);
            }
        });
    }

    //设置视觉定位
    private void setVisionAssistedPosition(Communication communication) {
        String type = communication.getPara().get(Constant.TYPE);
        if (KeyManager.getInstance() != null) {
            KeyManager.getInstance().setValue(flightControllerKey1, type.equals("1") ? true : false, new SetCallback() {
                @Override
                public void onSuccess() {
                    communication.setResult("Success");
                    communication.setCode(200);
                    communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                    NettyClient.getInstance().sendMessage(communication, null);
                }

                @Override
                public void onFailure(DJIError djiError) {
                    communication.setResult(djiError.toString());
                    communication.setCode(-1);
                    communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                    NettyClient.getInstance().sendMessage(communication, null);
                }
            });
        }
    }

    //精确着陆
    private void setPrecisionLand(Communication communication) {
        String type = communication.getPara().get(Constant.TYPE);
        if (KeyManager.getInstance() != null) {
            KeyManager.getInstance().setValue(flightControllerKey2, type.equals("1") ? true : false, new SetCallback() {
                @Override
                public void onSuccess() {
                    communication.setResult("Success");
                    communication.setCode(200);
                    communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                    NettyClient.getInstance().sendMessage(communication, null);
                }

                @Override
                public void onFailure(DJIError djiError) {
                    communication.setResult(djiError.toString());
                    communication.setCode(-1);
                    communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                    NettyClient.getInstance().sendMessage(communication, null);
                }
            });
        }
    }

    //向上刹停
    private void setUpwardsAvoidance(Communication communication) {
        String type = communication.getPara().get(Constant.TYPE);

//        if(!TextUtils.isEmpty(type)&&mFlightAssistant!=null){
//            mFlightAssistant.setUpwardVisionObstacleAvoidanceEnabled(type.equals("1") ? true : false, new CommonCallbacks.CompletionCallback() {
//                @Override
//                public void onResult(DJIError djiError) {
//                    CommonDjiCallback(djiError, communication);
//                }
//            });
//        }

        if (KeyManager.getInstance() != null) {
            KeyManager.getInstance().setValue(flightControllerKey3, type.equals("1") ? true : false, new SetCallback() {
                @Override
                public void onSuccess() {
                    upwardsAvoidance=type.equals("1") ? true : false;
                    communication.setResult("Success");
                    communication.setCode(200);
                    communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                    NettyClient.getInstance().sendMessage(communication, null);
                }

                @Override
                public void onFailure(DJIError djiError) {
                    communication.setResult(djiError.toString());
                    communication.setCode(-1);
                    communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                    NettyClient.getInstance().sendMessage(communication, null);
                }
            });
        }
    }
    //下避障
    private void setLandingProtection(Communication communication){
        String type = communication.getPara().get(Constant.TYPE);
//        if(!TextUtils.isEmpty(type)&&mFlightAssistant!=null){
//            mFlightAssistant.setLandingProtectionEnabled(type.equals("1") ? true : false, new CommonCallbacks.CompletionCallback() {
//                @Override
//                public void onResult(DJIError djiError) {
//                    if(djiError==null){
//                        landingProtection=type.equals("1")?true:false;
//                    }
//                    CommonDjiCallback(djiError, communication);
//                }
//            });
//        }


//        if(!TextUtils.isEmpty(type)&&mFlightAssistant!=null){
//            mFlightAssistant.setUpwardVisionObstacleAvoidanceEnabled(type.equals("1") ? true : false, new CommonCallbacks.CompletionCallback() {
//                @Override
//                public void onResult(DJIError djiError) {
//                    CommonDjiCallback(djiError, communication);
//                }
//            });
//        }

        if (KeyManager.getInstance() != null) {
            KeyManager.getInstance().setValue(flightControllerKey5, type.equals("1") ? true : false, new SetCallback() {
                @Override
                public void onSuccess() {
                    landingProtection=type.equals("1") ? true : false;
                    communication.setResult("Success");
                    communication.setCode(200);
                    communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                    NettyClient.getInstance().sendMessage(communication, null);
                }

                @Override
                public void onFailure(DJIError djiError) {
                    communication.setResult(djiError.toString());
                    communication.setCode(-1);
                    communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                    NettyClient.getInstance().sendMessage(communication, null);
                }
            });
        }

    }


    //设置上下障感知距离
    private void setMaxPerceptionDistance(Communication communication) {
        String type = communication.getPara().get(Constant.TYPE);
        String value = communication.getPara().get(Constant.VALUE);
        PerceptionInformation.DJIFlightAssistantObstacleSensingDirection direction = Downward;
        switch (type) {
            case "0":
                direction = Downward;
                break;
            case "1":
                direction = Upward;
                break;
            case "2":
                direction = Horizontal;
                break;
        }
        mFlightAssistant.setMaxPerceptionDistance(Float.parseFloat(value), direction, new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                CommonDjiCallback(djiError, communication);
            }
        });
    }

    //设置上下安全距离
    private void setAvoidanceDistance(Communication communication) {
        String type = communication.getPara().get(Constant.TYPE);
        String value = communication.getPara().get(Constant.VALUE);
        PerceptionInformation.DJIFlightAssistantObstacleSensingDirection direction = Downward;
        switch (type) {
            case "0":
                direction = Downward;
                break;
            case "1":
                direction = Upward;
                break;
            case "2":
                direction = Horizontal;
                break;
        }
        mFlightAssistant.setVisualObstaclesAvoidanceDistance(Float.parseFloat(value), direction, new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                CommonDjiCallback(djiError, communication);
            }
        });
    }

    //设置避障刹车功能
    private void setActiveObstacleAvoidance(Communication communication) {
        String type = communication.getPara().get(Constant.TYPE);
        mFlightAssistant.setActiveObstacleAvoidanceEnabled(type.equals("1") ? true : false, new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                CommonDjiCallback(djiError, communication);
            }
        });
        mFlightAssistant.setCollisionAvoidanceEnabled(type.equals("1") ? true : false, new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {

            }
        });
    }

    //设置偏航俯仰速度
    private void setGimbalSpeed(Communication communication) {
        String type = communication.getPara().get(Constant.TYPE);
        String value = communication.getPara().get(Constant.VALUE);
        Axis axis = PITCH;
        switch (type) {
            case "0":
                axis = PITCH;
                break;
            case "1":
                axis = YAW;
                break;
        }
        gimbal.setControllerSpeedCoefficient(axis, Integer.parseInt(value), new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                CommonDjiCallback(djiError, communication);
            }
        });
    }

    //自动校准
    private void startCalibration(Communication communication) {
        gimbal.startCalibration(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                CommonDjiCallback(djiError, communication);
            }
        });
    }

    //恢复出厂
    private void restoreFactorySettings(Communication communication) {
        gimbal.restoreFactorySettings(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                CommonDjiCallback(djiError, communication);
            }
        });
    }

    //云台偏航缓启停
    private void setControllerSmoothingFactor(Communication communication) {
        String type = communication.getPara().get(Constant.TYPE);
        String value = communication.getPara().get(Constant.VALUE);
        Axis axis = PITCH;
        switch (type) {
            case "0":
                axis = PITCH;
                break;
            case "1":
                axis = YAW;
                break;
        }
        gimbal.setControllerSmoothingFactor(axis, Integer.parseInt(value), new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                CommonDjiCallback(djiError, communication);
            }
        });
    }


    //格式化sdcard
    private void formatSDCard(Communication communication) {
        if (camera != null) {
            camera.formatSDCard(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    CommonDjiCallback(djiError, communication);
                }
            });
        }
    }

    //设置拍照时led开关
    private void setBeaconAutoTurnOffEnabled(Communication communication) {
        String type = communication.getPara().get(Constant.TYPE);
        if (camera != null && isM300Product()) {
            camera.setBeaconAutoTurnOffEnabled(type.equals("1") ? true : false, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    CommonDjiCallback(djiError, communication);
                }
            });
        }
    }

    //设置调色板
    private void setThermalPalette(Communication communication) {
        if (camera != null && isM300Product()) {
            String type = communication.getPara().get(Constant.TYPE);
            camera.getLens(2).setThermalPalette(SettingsDefinitions.ThermalPalette.find(Integer.parseInt(type)), new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    CommonDjiCallback(djiError, communication);
                }
            });
        }
    }

    //设置等温度线
    private void setThermalIsothermEnabled(Communication communication) {
        if (camera != null && isM300Product()) {
            String type = communication.getPara().get(Constant.TYPE);
            String min_value = communication.getPara().get(Constant.MIN_VALUE);
            String max_value = communication.getPara().get(Constant.MAX_VALUE);
            boolean tf = type.equals("1") ? true : false;
            camera.getLens(2).setThermalIsothermUnit(SettingsDefinitions.ThermalIsothermUnit.CELSIUS, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError != null) {
                        communication.setResult(djiError.getDescription());
                        communication.setCode(-1);
                        communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                        NettyClient.getInstance().sendMessage(communication, null);
                    } else {
                        camera.getLens(2).setThermalIsothermEnabled(tf, new CommonCallbacks.CompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
                                CommonDjiCallback(djiError, communication);
                            }
                        });
                        if (tf) {
                            camera.getLens(2).setThermalIsothermUpperValue(Integer.parseInt(max_value), new CommonCallbacks.CompletionCallback() {
                                @Override
                                public void onResult(DJIError djiError) {

                                }
                            });
                            camera.getLens(2).setThermalIsothermLowerValue(Integer.parseInt(min_value), new CommonCallbacks.CompletionCallback() {
                                @Override
                                public void onResult(DJIError djiError) {

                                }
                            });
                        }
                    }
                }
            });
        }
    }

    //设置水印
    private void setWatermarkSettings(Communication communication) {
        if (camera != null) {
            String video = communication.getPara().get("video");
            String photo = communication.getPara().get("photo");
            WatermarkSettings watermarkSettings = new WatermarkSettings(video.equals("1") ? true : false, photo.equals("1") ? true : false);
            camera.setWatermarkSettings(watermarkSettings, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    CommonDjiCallback(djiError, communication);
                }
            });
        }
    }

    //相机重置参数（应该是恢复出厂设置）
    private void cameraRestoreFactorySettings(Communication communication) {
        if (camera != null) {
            camera.restoreFactorySettings(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    CommonDjiCallback(djiError, communication);
                }
            });
        }
    }

    //设置rtk
    private void setRTK(Communication communication) {
        String type = communication.getPara().get(Constant.TYPE);
        if (mRTK != null) {
            mRTK.setRtkEnabled(type.equals("1") ? true : false, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    CommonDjiCallback(djiError, communication);
                }
            });
        } else {
            communication.setResult("RTK为空");
            communication.setCode(-1);
            communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            NettyClient.getInstance().sendMessage(communication, null);
        }

    }

    //开始搜索基站
    private void startSearchBS(Communication communication) {
        if (mRTK != null) {
            mRTK.startSearchBaseStation(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    CommonDjiCallback(djiError, communication);
                }
            });
        } else {
            communication.setResult("RTK为空");
            communication.setCode(-1);
            communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            NettyClient.getInstance().sendMessage(communication, null);
        }

    }

    //结束搜索基站
    private void stopSearchBS(Communication communication) {
        if (mRTK != null) {
            mRTK.stopSearchBaseStation(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    CommonDjiCallback(djiError, communication);
                }
            });
        } else {
            communication.setResult("RTK为空");
            communication.setCode(-1);
            communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            NettyClient.getInstance().sendMessage(communication, null);
        }
    }

    //连接基站
    private void connectBS(Communication communication) {
        long type = Long.parseLong(communication.getPara().get(Constant.TYPE));
        if (mRTK != null) {
            mRTK.connectToBaseStation(type, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    CommonDjiCallback(djiError, communication);
                }
            });
        } else {
            communication.setResult("RTK为空");
            communication.setCode(-1);
            communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            NettyClient.getInstance().sendMessage(communication, null);
        }
    }

    //设置参考站源
    private void setRSS(Communication communication) {
        String type = communication.getPara().get(Constant.TYPE);
        if (mRTK != null && !TextUtils.isEmpty(type)) {
            mRTK.setReferenceStationSource(ReferenceStationSource.find(Integer.parseInt(type)), new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    CommonDjiCallback(djiError, communication);
                }
            });
        } else {
            communication.setResult("RTK为空");
            communication.setCode(-1);
            communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            NettyClient.getInstance().sendMessage(communication, null);
        }
    }

    //设置坐标系
    private void setNSCS(Communication communication) {
        String type = communication.getPara().get(Constant.TYPE);
        if (!TextUtils.isEmpty(type)) {
            RTKNetworkServiceProvider provider = DJISDKManager.getInstance().getRTKNetworkServiceProvider().getInstance();
            provider.setNetworkServiceCoordinateSystem(CoordinateSystem.find(Integer.parseInt(type)), new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    CommonDjiCallback(djiError, communication);
                }
            });

//            https://bbs.dji.com/thread-247389-1-1.html
            //设置千寻网
//            NetworkServiceSettings.Builder builder=new NetworkServiceSettings.Builder().userName("").password("")
//            provider.setCustomNetworkSettings(builder.build());

        }
    }

    //设置网络rtk
//    https://bbs.dji.com/thread-247389-1-1.html
    private void setRTKNetwork(Communication communication) {
        String username = communication.getPara().get("username");
        String password = communication.getPara().get("password");
        String ip = communication.getPara().get("ip");
        String mountPoint = communication.getPara().get("mountPoint");
        int port = Integer.parseInt(communication.getPara().get("port"));
        RTKNetworkServiceProvider provider = DJISDKManager.getInstance().getRTKNetworkServiceProvider().getInstance();
        if (mRTK != null) {
            mRTK.setReferenceStationSource(ReferenceStationSource.NETWORK_RTK, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                }
            });
        }
        //设置网络坐标系
//        provider.setNetworkServiceCoordinateSystem();

        NetworkServiceSettings.Builder builder = new NetworkServiceSettings.Builder()
                .userName(username).password(password).ip(ip).mountPoint(mountPoint).port(port);
        provider.setCustomNetworkSettings(builder.build());
        provider.startNetworkService(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                CommonDjiCallback(djiError, communication);
            }
        });

    }


    //设置云台俯仰限位扩展
    private void setPitchRangeExtension(Communication communication) {
        String type = communication.getPara().get(Constant.TYPE);
        if (!TextUtils.isEmpty(type)) {
            gimbal.setPitchRangeExtensionEnabled(type.equals("1") ? true : false, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    CommonDjiCallback(djiError, communication);
                }
            });
        }

    }

    //设置工作频段
    private void setFrequencyBand(Communication communication) {
        String type = communication.getPara().get(Constant.TYPE);
        if (!TextUtils.isEmpty(type)) {
            ocuSyncLink.setFrequencyBand(OcuSyncFrequencyBand.find(Integer.parseInt(type)), new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {

                        switch (type) {
                            case "0":
                                frequencyBand = "双频";
                                break;
                            case "1":
                                frequencyBand = "2.4G";
                                break;
                            case "2":
                                frequencyBand = "5.8G";
                                break;
                        }


                        TransmissionSetBean transmissionSetBean = new TransmissionSetBean();
                        transmissionSetBean.setChannelBandwidth(channelBandwidth);
                        transmissionSetBean.setFrequencyBand(frequencyBand);
                        transmissionSetBean.setInterferencePower(interferencePower);
                        transmissionSetBean.setTranscodingDataRate(transcodingDataRate);
                        communication.setResult(gson.toJson(transmissionSetBean, TransmissionSetBean.class));
                        communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                        communication.setCode(200);
                        NettyClient.getInstance().sendMessage(communication, null);
                    } else {
                        communication.setResult(djiError.getDescription());
                        communication.setCode(-1);
                        communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                        NettyClient.getInstance().sendMessage(communication, null);
                    }


                    CommonDjiCallback(djiError, communication);
                }
            });
        }

    }

    //设置云台模式
    private void setGimbalMode(Communication communication) {
        String type = communication.getPara().get(Constant.TYPE);
        if (gimbal != null && !TextUtils.isEmpty(type)) {
            gimbal.setMode(GimbalMode.find(Integer.parseInt(type)), new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    CommonDjiCallback(djiError, communication);
                }
            });
        }
    }

    //设置红外展示模式 1和2
    private void setHyDisplayMode(Communication communication) {
        String type = communication.getPara().get(Constant.TYPE);
        if (camera != null) {
            camera.getLens(2).setDisplayMode(SettingsDefinitions.DisplayMode.find(Integer.parseInt(type)), new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        webInitializationBean.setHyDisplayMode("type");
                        if (type.equals("2")) {
                            camera.setCameraVideoStreamSource(ZOOM, new CommonCallbacks.CompletionCallback() {
                                @Override
                                public void onResult(DJIError djiError) {
                                    camera.setCameraVideoStreamSource(INFRARED_THERMAL, new CommonCallbacks.CompletionCallback() {
                                        @Override
                                        public void onResult(DJIError djiError) {
                                            CommonDjiCallback(djiError, communication);
                                        }
                                    });
                                }
                            });
                        }
                    } else {
                        communication.setResult(djiError.getDescription());
                        communication.setCode(-1);
                        communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                        NettyClient.getInstance().sendMessage(communication, null);
                    }
                }
            });

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

    //所有错误返回
    @Override
    public void onUpdate(List<DJIDiagnostics> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        String errorMsg = "";
        for (int i = 0; i < list.size(); i++) {
            if (!TextUtils.isEmpty(list.get(i).getSolution().trim())) {
                errorMsg += list.get(i).getSolution() + ",";
                Log.d("ErrorUpdate", list.get(i).toString());
            }

        }
        if (!TextUtils.isEmpty(errorMsg)) {
            errorMsg = errorMsg.substring(0, errorMsg.length() - 1);
            StringsBean stringsBean = new StringsBean();
//        stringsBean.setValue(errorMsg.toString().substring(0, errorMsg.length() - 1) + ",乃要的测试数据");
            stringsBean.setValue(errorMsg);
            Log.d("ErrorUpdate", errorMsg.substring(0, errorMsg.length() - 1));
            if (communication_error_log == null) {
                communication_error_log = new Communication();
            }
            communication_error_log.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            communication_error_log.setEquipmentId(MApplication.EQUIPMENT_ID);
            communication_error_log.setMethod((Constant.DIAGNOSTICS));
            communication_error_log.setResult(gson.toJson(stringsBean));
            NettyClient.getInstance().sendMessage(communication_error_log, null);
        }


    }

    //搜索到的rtk监听
    @Override
    public void onUpdate(RTKBaseStationInformation[] rtkBaseStationInformations) {
        String submit = "";
        if (rtkBaseStationInformations.length > 0) {
            for (int i = 0; i < rtkBaseStationInformations.length; i++) {
                submit += rtkBaseStationInformations[i].getBaseStationName() + "-" + rtkBaseStationInformations[i].getBaseStationID() + ",";
            }
            StringsBean stringsBean = new StringsBean();
            stringsBean.setValue(submit);
            if (communication_BS_info == null) {
                communication_BS_info = new Communication();
            }
            communication_BS_info.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            communication_BS_info.setEquipmentId(MApplication.EQUIPMENT_ID);
            communication_BS_info.setMethod((Constant.BS_INFO));
            communication_BS_info.setResult(gson.toJson(stringsBean));
            NettyClient.getInstance().sendMessage(communication_BS_info, null);
        }


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
                                CommonDjiCallback(djiError, mCommunication);
                            }
                        }
                );
            }
        }
    }



    Communication mCommunication;
    //飞机前进后退 -10~10
    private void fly_forward_and_back(Communication communication) {
        isQianHouGet = false;
        String speed = communication.getPara().get(Constant.SPEED);
        if (!TextUtils.isEmpty(speed)) {
            mRoll = Float.parseFloat(speed);
            mPitch = 0;
            mYaw = 0;
            mThrottle = 0;
            mCommunication = communication;
            if (mFlightController != null) {
                mFlightController.sendVirtualStickFlightControlData(new FlightControlData(mPitch, mRoll, mYaw, mThrottle), new CommonCallbacks.CompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
                            }
                        }
                );
            }
            mCommunication.setResult("指令已调用");
            mCommunication.setCode(200);
            mCommunication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            NettyClient.getInstance().sendMessage(mCommunication, null);
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

            if (mFlightController != null) {
                mFlightController.sendVirtualStickFlightControlData(
                        new FlightControlData(
                                mPitch, mRoll, mYaw, mThrottle
                        ), new CommonCallbacks.CompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
//                                CommonDjiCallback(djiError, mCommunication);
                            }
                        }
                );
            }
            mCommunication.setResult("指令已调用");
            mCommunication.setCode(200);
            mCommunication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            NettyClient.getInstance().sendMessage(mCommunication, null);
        } else {
            communication.setResult("");
            communication.setCode(-1);
            communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            NettyClient.getInstance().sendMessage(communication, null);
        }

    }

    //飞机上升下降 -30~30
    private void fly_rise_and_fall(Communication communication) {
        Log.d(TAG, "communication=" + communication.toString());
        String speed = communication.getPara().get(Constant.SPEED);
        if (!TextUtils.isEmpty(speed)) {
            mThrottle = Float.parseFloat(speed);
            mPitch = 0;
            mRoll = 0;
            mYaw = 0;
            mCommunication = communication;
            if (mFlightController != null) {
                mFlightController.sendVirtualStickFlightControlData(
                        new FlightControlData(
                                mPitch, mRoll, mYaw, mThrottle
                        ), new CommonCallbacks.CompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
//                                CommonDjiCallback(djiError, mCommunication);

                            }
                        }
                );
            }
            mCommunication.setResult("指令已调用");
            mCommunication.setCode(200);
            mCommunication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            NettyClient.getInstance().sendMessage(mCommunication, null);
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
            if (mFlightController != null) {
                mFlightController.sendVirtualStickFlightControlData(
                        new FlightControlData(
                                mPitch, mRoll, mYaw, mThrottle
                        ), new CommonCallbacks.CompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
                                if (djiError != null) {
                                    communication.setResult(djiError.getDescription());
                                    communication.setCode(-1);
                                    communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                                    NettyClient.getInstance().sendMessage(communication, null);
                                } else {
                                    communication.setResult(wrj_heading);
                                    communication.setCode(200);
                                    communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                                    NettyClient.getInstance().sendMessage(communication, null);
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
        Log.d("WMUEWMUE", "speed=" + speed);
        String gimbalPitch = communication.getPara().get(Constant.GIMBAL_PITCH);
        String turnMode = communication.getPara().get(Constant.TURN_MODE);
//        Log.d("HHHHH","speed="+speed);
        if (!TextUtils.isEmpty(speed)) {
            mSpeed = Float.parseFloat(speed);
        }
        if (!TextUtils.isEmpty(gimbalPitch)) {
            mGimbalPitch = Float.parseFloat(gimbalPitch);
        }
        if (!TextUtils.isEmpty(turnMode)) {
            mTurnMode = turnMode;
        }
        String finishedAction = communication.getPara().get(Constant.FINISHED_ACTION);
        if (!TextUtils.isEmpty(finishedAction)) {
            mFinishedAction = WaypointMissionFinishedAction.find(Integer.parseInt(finishedAction));
        }

        String headingMode = communication.getPara().get(Constant.HEADING_MODE);
        if (TextUtils.isEmpty(headingMode)) {
            mHeadingMode = WaypointMissionHeadingMode.find(Integer.parseInt(headingMode));
        }


        String mPathMode = communication.getPara().get(Constant.FLIGHT_PATH_MODE);
        if (!TextUtils.isEmpty(mPathMode)) {
            switch (mPathMode) {

                case "1":
                    mFlightPathMode = WaypointMissionFlightPathMode.CURVED;
                    break;
                default:
                    mFlightPathMode = WaypointMissionFlightPathMode.NORMAL;
                    break;

            }
        }
        String gpre = communication.getPara().get(Constant.GPRE);
        sgpre = true;
        if (!TextUtils.isEmpty(gpre)) {
            switch (mPathMode) {
                case "0":
                    sgpre = false;
                    break;
                case "1":
                    sgpre = true;
                    break;
            }
        }

        // add点
        wayPoints = communication.getPara().get(Constant.WAY_POINTS);
        Log.d("wayPoints", wayPoints);
        if (!TextUtils.isEmpty(wayPoints)) {
            List<WayPointsBean> myWayPointList = gson.fromJson(wayPoints, new TypeToken<List<WayPointsBean>>() {
            }.getType());
            waypointList.clear();
            for (int i = 0; i < myWayPointList.size(); i++) {
                add_point(myWayPointList.get(i), mSpeed + "", mGimbalPitch + "", mTurnMode, sgpre);
            }
        }


        if (waypointMissionBuilder == null) {
            waypointMissionBuilder = new WaypointMission.Builder().finishedAction(mFinishedAction)
                    .headingMode(mHeadingMode)
                    .autoFlightSpeed(mSpeed)
                    .maxFlightSpeed(mSpeed)
                    .setGimbalPitchRotationEnabled(sgpre)
                    .flightPathMode(mFlightPathMode);

        } else {
            waypointMissionBuilder.finishedAction(mFinishedAction)
                    .headingMode(mHeadingMode)
                    .autoFlightSpeed(mSpeed)
                    .maxFlightSpeed(mSpeed)
                    .setGimbalPitchRotationEnabled(sgpre)
                    .flightPathMode(mFlightPathMode);

        }
//        将其waypointMissionBuilder.build()作为参数传递给操作员加载航路点任务
        DJIError error = getWaypointMissionOperator().loadMission(waypointMissionBuilder.build());
        if (error == null) {
//            setResultToToast("loadWaypoint succeeded");
            Gson gson = new Gson();
            String MSlist = gson.toJson(waypointList);
            Log.d("MSlist", MSlist);

            //成功之后upload
            getWaypointMissionOperator().uploadMission(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError error) {
                    String currentState = getWaypointMissionOperator().getCurrentState().toString();
                    if (error == null) {
                        //这里不返回给后台 改到监听回调里
                        communication_upload_mission = communication;
                    } else {
                        getWaypointMissionOperator().retryUploadMission(null);
                        communication.setResult(error.getDescription() + "currentState=" + currentState);
                        communication.setCode(-1);
                        communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                        NettyClient.getInstance().sendMessage(communication, null);
                    }
                }
            });


        } else {
            communication.setResult(error.getDescription());
            communication.setCode(-1);
            communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            NettyClient.getInstance().sendMessage(communication, null);
        }
    }

    public WaypointMissionOperator getWaypointMissionOperator() {
        if (instance == null && DJISDKManager.getInstance().getMissionControl() != null) {
            instance = DJISDKManager.getInstance().getMissionControl().getWaypointMissionOperator();
        }
        return instance;
    }


    //开始航点自动飞行
    private void startWaypointMission(Communication communication) {
        String currentState = getWaypointMissionOperator().getCurrentState().toString();
        Log.d("currentState", currentState);
        getWaypointMissionOperator().startMission(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError error) {
                if (error != null) {
                    Log.d("WMUEWMUE", "startWaypointMission报错");
                    communication.setResult("startMission报错:" + error.getDescription() + "currentState:" + currentState);
                    communication.setCode(-1);
                    communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                    NettyClient.getInstance().sendMessage(communication, null);
                } else {
                    Log.d("WMUEWMUE", "startWaypointMission成功");
                    communication.setResult("Success");
                    communication.setCode(200);
                    communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                    NettyClient.getInstance().sendMessage(communication, null);
                }
            }
        });

    }

    //停止航点自动飞行
    private void stopWaypointMission(Communication communication) {
        getWaypointMissionOperator().stopMission(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError error) {
            }
        });
        communication.setResult("Success");
        communication.setCode(200);
        communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
        NettyClient.getInstance().sendMessage(communication, null);
    }

    String isHangXianResume = "0";//航线恢复

    private void resumeWaypointMission(Communication communication) {
        isHangXianResume = communication.getPara().get(Constant.TYPE);
        if (!isHangXianResume.equals("0")) {//如果手动飞了 那么要调结束，上传航点，开始飞行
            //手动飞行
            Log.d("WMUEWMUE", "stopMission");
            //添加航点
            if (!TextUtils.isEmpty(wayPoints)) {
                List<WayPointsBean> myWayPointList = gson.fromJson(wayPoints, new TypeToken<List<WayPointsBean>>() {
                }.getType());
                waypointList.clear();
                //为0 说明没拿到
                for (int i = targetWaypointIndex == 0 ? 0 : targetWaypointIndex - 1; i < myWayPointList.size(); i++) {
                    if (isHangXianResume.equals("1")) {
                        if (i == targetWaypointIndex - 1) {
                            WayPointsBean wb = myWayPointList.get(i);
                            wb.setLatitude(HangXianPauselatitude + "");
                            wb.setLongitude(HangXianPauselongitude + "");
                            add_point(wb, mSpeed + "", mGimbalPitch + "", mTurnMode, sgpre);
                        } else {
                            add_point(myWayPointList.get(i), mSpeed + "", mGimbalPitch + "", mTurnMode, sgpre);
                        }
                    } else if (isHangXianResume.equals("2")) {
                        add_point(myWayPointList.get(i), mSpeed + "", mGimbalPitch + "", mTurnMode, sgpre);
                    }
                }
            }

            if (waypointMissionBuilder == null) {
                Log.d("WMUEWMUE", "waypointMissionBuilder=null");
                waypointMissionBuilder = new WaypointMission.Builder().finishedAction(mFinishedAction)
                        .headingMode(mHeadingMode)
                        .autoFlightSpeed(mSpeed)
                        .maxFlightSpeed(mSpeed)
                        .setGimbalPitchRotationEnabled(sgpre)
                        .flightPathMode(mFlightPathMode);

            } else {
                waypointMissionBuilder.finishedAction(mFinishedAction)
                        .headingMode(mHeadingMode)
                        .autoFlightSpeed(mSpeed)
                        .maxFlightSpeed(mSpeed)
                        .setGimbalPitchRotationEnabled(sgpre)
                        .flightPathMode(mFlightPathMode);

            }
            DJIError error2 = getWaypointMissionOperator().loadMission(waypointMissionBuilder.build());
            if (error2 == null) {
                Log.d("WMUEWMUE", "loadMissionsuccess");
                Gson gson = new Gson();
                String MSlist = gson.toJson(waypointList);
                Log.d("MSlist", MSlist);

                //成功之后upload
                getWaypointMissionOperator().uploadMission(new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError error) {
                        String currentState = getWaypointMissionOperator().getCurrentState().toString();
                        Log.d("currentState", currentState);
                        if (error == null) {
                            //开始恢复航线飞行
                            communication_upload_mission = communication;
                        } else {
                            getWaypointMissionOperator().retryUploadMission(null);
                            communication.setResult("uploadMission报错" + error.getDescription() + "currentState=" + currentState);
                            communication.setCode(-1);
                            communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                            NettyClient.getInstance().sendMessage(communication, null);
                        }
                    }
                });


            } else {
                communication.setResult("loadMission报错" + error2.getDescription());
                communication.setCode(-1);
                communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                NettyClient.getInstance().sendMessage(communication, null);
            }

        }
        //正常的恢复
        else {
            getWaypointMissionOperator().resumeMission(new CommonCallbacks.CompletionCallback() {
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

    }

    private void pauseWaypointMission(Communication communication) {

        getWaypointMissionOperator().pauseMission(new CommonCallbacks.CompletionCallback() {
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
                    isHangXianPause = true;
                }
            }
        });
    }

    private void add_point(WayPointsBean wayPointsBean, String speed, String gimbalPitch, String mTurnMode, boolean sgpre) {
        double[] latLng = MapConvertUtils.getDJILatLng(Double.parseDouble(wayPointsBean.getLatitude()), Double.parseDouble(wayPointsBean.getLongitude()));
        Waypoint mWaypoint = new Waypoint(latLng[0], latLng[1], Float.parseFloat(wayPointsBean.getAltitude()));


//        Waypoint mWaypoint = new Waypoint(Double.parseDouble(wayPointsBean.getLatitude()), Double.parseDouble(wayPointsBean.getLongitude()), Float.parseFloat(wayPointsBean.getAltitude()));

        Log.d("WMUEWMUE", "Latitude:" + wayPointsBean.getLatitude() + "--Longitude:" + wayPointsBean.getLongitude());
        if (TextUtils.isEmpty(wayPointsBean.getSpeed()) || wayPointsBean.getSpeed().equals("0")) {
            mWaypoint.speed = Float.parseFloat(speed);
        } else {
            mWaypoint.speed = Float.parseFloat(wayPointsBean.getSpeed());
        }
        Log.d("WMUEWMUE", "mWaypoint.speed=" + mWaypoint.speed);
        if (!sgpre) {//如果sgpre=true可以手动改
            if (TextUtils.isEmpty(wayPointsBean.getGimbalPitch()) || wayPointsBean.getGimbalPitch().equals("0")) {
                mWaypoint.gimbalPitch = Float.parseFloat(gimbalPitch);
            } else {
                mWaypoint.gimbalPitch = Float.parseFloat(wayPointsBean.getGimbalPitch());
            }
        }


        //设置弧度
        if (!TextUtils.isEmpty(wayPointsBean.getCornerRadiusInMeters())) {
            mWaypoint.cornerRadiusInMeters = Float.parseFloat(wayPointsBean.getCornerRadiusInMeters());
        }
        //1顺0逆
        if (!TextUtils.isEmpty(wayPointsBean.getTurnMode())) {
            if (wayPointsBean.getTurnMode().equals("1")) {
                mWaypoint.turnMode = WaypointTurnMode.CLOCKWISE;
            } else if (wayPointsBean.getTurnMode().equals("0")) {
                mWaypoint.turnMode = WaypointTurnMode.COUNTER_CLOCKWISE;
            }
        } else {
            if (mTurnMode.equals("0")) {
                mWaypoint.turnMode = WaypointTurnMode.COUNTER_CLOCKWISE;
            } else if (mTurnMode.equals("1")) {
                mWaypoint.turnMode = WaypointTurnMode.CLOCKWISE;
            }
        }


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

    private void addWaypointMissionListener() {
        if (getWaypointMissionOperator() != null) {
            getWaypointMissionOperator().addListener(eventNotificationListener);
        }
    }

    private void removeWaypointMissionListener() {
        if (getWaypointMissionOperator() != null) {
            getWaypointMissionOperator().removeListener(eventNotificationListener);
        }
    }

    //航线规划监听
    private WaypointMissionOperatorListener eventNotificationListener = new WaypointMissionOperatorListener() {
        @Override
        public void onDownloadUpdate(WaypointMissionDownloadEvent downloadEvent) {
        }

        //上传航线
        @Override
        public void onUploadUpdate(WaypointMissionUploadEvent waypointMissionUploadEvent) {
            Log.d("WMUEWMUE-onUploadUpdate", waypointMissionUploadEvent.getProgress()
                    + "\ncurrentState" + waypointMissionUploadEvent.getCurrentState() +
                    "\nPreviousState" + waypointMissionUploadEvent.getPreviousState() +
                    "\nerror" + waypointMissionUploadEvent.getError());
//            if (waypointMissionUploadEvent.getError() == null) {
            if (waypointMissionUploadEvent.getCurrentState().toString().equals("READY_TO_EXECUTE")) {

                if (isHangXianResume.equals("1") || isHangXianResume.equals("2")) {//暂停 手动飞再恢复
                    getWaypointMissionOperator().startMission(new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError error) {
                            if (error != null) {
                                Log.d("WMUEWMUE", "startWaypointMission报错");
                                communication_upload_mission.setResult("startMission报错:" + error.getDescription());
                                communication_upload_mission.setCode(-1);
                                communication_upload_mission.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                                NettyClient.getInstance().sendMessage(communication_upload_mission, null);
                            } else {
                                Log.d("WMUEWMUE", "startWaypointMission成功");
                                communication_upload_mission.setResult("Success");
                                communication_upload_mission.setCode(200);
                                communication_upload_mission.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                                NettyClient.getInstance().sendMessage(communication_upload_mission, null);
                            }
                        }
                    });
                } else {//第一次飞的逻辑
                    communication_upload_mission.setResult("Mission upload successfully!+currentState=" + waypointMissionUploadEvent.getCurrentState());
                    communication_upload_mission.setCode(200);
                    communication_upload_mission.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                    NettyClient.getInstance().sendMessage(communication_upload_mission, null);
                }
            } else {
                if (waypointMissionUploadEvent.getError() != null) {
                    communication_upload_mission.setResult(waypointMissionUploadEvent.getError().getDescription());
                    communication_upload_mission.setCode(-1);
                    communication_upload_mission.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                    NettyClient.getInstance().sendMessage(communication_upload_mission, null);
                }
            }


        }

        @Override
        public void onExecutionUpdate(WaypointMissionExecutionEvent executionEvent) {
            if (executionEvent.getCurrentState().toString().equals("EXECUTING")) {
                targetWaypointIndex = executionEvent.getProgress().targetWaypointIndex;
            }

            Log.d("WMUEWMUE-EU", "targetWaypointIndex:" + targetWaypointIndex);
            Log.d("WMUEWMUE-EU", executionEvent.getProgress()
                    + "\ncurrentState" + executionEvent.getCurrentState()
                    + "\nPreviousState" + executionEvent.getPreviousState());
            if (executionEvent.getProgress().totalWaypointCount == executionEvent.getProgress().targetWaypointIndex) {
                if (communication_onExecutionFinish == null) {
                    communication_onExecutionFinish = new Communication();
                }
                communication_onExecutionFinish.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                communication_onExecutionFinish.setEquipmentId(MApplication.EQUIPMENT_ID);
                communication_onExecutionFinish.setMethod((Constant.ON_EXECUTION_FINISH));
                communication_onExecutionFinish.setResult("1");
                NettyClient.getInstance().sendMessage(communication_onExecutionFinish, null);
                Log.d("WMUEWMUE", "推送已发送");
            }


        }

        @Override
        public void onExecutionStart() {

        }

        //        显示一条消息，以在任务执行完成时通知用户。
        @Override
        public void onExecutionFinish(@Nullable final DJIError error) {
//            showToast("Execution finished: " + (error == null ? "Success!" : error.getDescription()));
            Log.d("WMUEWMUE", "Execution finished: " + (error == null ? "Success!" : error.getDescription()));

//            if (communication_onExecutionFinish == null) {
//                communication_onExecutionFinish = new Communication();
//            }
//            communication_onExecutionFinish.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
//            communication_onExecutionFinish.setEquipmentId(MApplication.EQUIPMENT_ID);
//            communication_onExecutionFinish.setMethod((Constant.ON_EXECUTION_FINISH));
//            communication_onExecutionFinish.setResult("2");
//            NettyClient.getInstance().sendMessage(communication_onExecutionFinish, null);
        }
    };


    //航线规划v2
    private void waypoint_plan_V2(Communication communication) {
        waypointV2MissionOperator = MissionControl.getInstance().getWaypointMissionV2Operator();
        setWayV2UpListener();
        Log.d("WV2MOperator", waypointV2MissionOperator.getCurrentState() + "");
        //loadMission
        if (waypointV2MissionOperator.getCurrentState().equals(WaypointV2MissionState.READY_TO_UPLOAD) || waypointV2MissionOperator.getCurrentState().equals(WaypointV2MissionState.READY_TO_EXECUTE)) {
            waypointV2MissionOperator.loadMission(createWaypointMission(communication), new CommonCallbacks.CompletionCallback<DJIWaypointV2Error>() {
                @Override
                public void onResult(DJIWaypointV2Error djiWaypointV2Error) {
                    if (djiWaypointV2Error == null) {
                        canUploadMission = true;
                        Toast.makeText(ConnectionActivity.this, "Mission is loaded successfully", Toast.LENGTH_SHORT).show();
                        //uploadMission
                        if (canUploadMission) {
                            communication_upload_mission = communication;
                            waypointV2MissionOperator.uploadMission(new CommonCallbacks.CompletionCallback<DJIWaypointV2Error>() {
                                @Override
                                public void onResult(DJIWaypointV2Error djiWaypointV2Error) {
                                    if (djiWaypointV2Error != null) {
                                        Toast.makeText(ConnectionActivity.this, djiWaypointV2Error.getDescription(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(ConnectionActivity.this, "Wait for mission to be loaded", Toast.LENGTH_SHORT).show();
                            communication_upload_mission.setResult("Wait for mission to be loaded");
                            communication_upload_mission.setCode(-1);
                            communication_upload_mission.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                            NettyClient.getInstance().sendMessage(communication_upload_mission, null);
                        }
                    } else {
                        Toast.makeText(ConnectionActivity.this, djiWaypointV2Error.getDescription(), Toast.LENGTH_SHORT).show();
                        communication_upload_mission.setResult(djiWaypointV2Error.getDescription());
                        communication_upload_mission.setCode(-1);
                        communication_upload_mission.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                        NettyClient.getInstance().sendMessage(communication_upload_mission, null);
                    }
                }
            });
        } else {
            Toast.makeText(ConnectionActivity.this, "The mission can be loaded only when the operator state is READY_TO_UPLOAD or READY_TO_EXECUTE", Toast.LENGTH_SHORT).show();
            communication_upload_mission.setResult("The mission can be loaded only when the operator state is READY_TO_UPLOAD or READY_TO_EXECUTE");
            communication_upload_mission.setCode(-1);
            communication_upload_mission.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            NettyClient.getInstance().sendMessage(communication_upload_mission, null);
        }


    }

    private void setWayV2UpListener() {
        waypointV2MissionOperatorListener = new WaypointV2MissionOperatorListener() {
            @Override
            public void onDownloadUpdate(WaypointV2MissionDownloadEvent waypointV2MissionDownloadEvent) {
            }

            @Override
            public void onUploadUpdate(WaypointV2MissionUploadEvent waypointV2MissionUploadEvent) {
                if (waypointV2MissionUploadEvent.getError() != null) {
                    Toast.makeText(ConnectionActivity.this, waypointV2MissionUploadEvent.getError().getDescription(), Toast.LENGTH_SHORT).show();
                }

                if (waypointV2MissionUploadEvent.getPreviousState() == WaypointV2MissionState.UPLOADING
                        && waypointV2MissionUploadEvent.getCurrentState() == WaypointV2MissionState.READY_TO_EXECUTE) {
                    Toast.makeText(ConnectionActivity.this, "Mission is uploaded successfully", Toast.LENGTH_SHORT).show();
                    communication_upload_mission.setResult("Mission is uploaded successfully");
                    communication_upload_mission.setCode(200);
                    communication_upload_mission.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                    NettyClient.getInstance().sendMessage(communication_upload_mission, null);
                } else {
                    communication_upload_mission.setResult("Mission is uploaded failed");
                    communication_upload_mission.setCode(-1);
                    communication_upload_mission.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                    NettyClient.getInstance().sendMessage(communication_upload_mission, null);
                }
            }

            @Override
            public void onExecutionUpdate(WaypointV2MissionExecutionEvent waypointV2MissionExecutionEvent) {
            }

            @Override
            public void onExecutionStart() {
            }

            @Override
            public void onExecutionFinish(DJIWaypointV2Error djiWaypointV2Error) {

                if (communication_onExecutionFinish == null) {
                    communication_onExecutionFinish = new Communication();
                }
                communication_onExecutionFinish.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                communication_onExecutionFinish.setEquipmentId(MApplication.EQUIPMENT_ID);
                communication_onExecutionFinish.setMethod((Constant.ON_EXECUTION_FINISH));
                communication_onExecutionFinish.setResult("2");
                NettyClient.getInstance().sendMessage(communication_onExecutionFinish, null);
            }

            @Override
            public void onExecutionStopped() {
            }
        };

        waypointV2ActionListener = new WaypointV2ActionListener() {
            @Override
            public void onDownloadUpdate(ActionDownloadEvent actionDownloadEvent) {
            }

            @Override
            public void onUploadUpdate(ActionUploadEvent actionUploadEvent) {
                if (actionUploadEvent.getCurrentState().equals(ActionState.READY_TO_UPLOAD)) {
                    //TODO 添加执行操作
//                    uploadWaypointAction();
                }
                //上传航线成功
                if (actionUploadEvent.getPreviousState() == ActionState.UPLOADING
                        && actionUploadEvent.getCurrentState() == ActionState.READY_TO_EXECUTE) {
                    Toast.makeText(ConnectionActivity.this, "Actions are uploaded successfully", Toast.LENGTH_SHORT).show();
                    canStartMission = true;
                }

            }

            @Override
            public void onExecutionUpdate(ActionExecutionEvent actionExecutionEvent) {
            }

            @Override
            public void onExecutionStart(int i) {

            }

            @Override
            public void onExecutionFinish(int i, DJIWaypointV2Error djiWaypointV2Error) {


            }
        };

        waypointV2MissionOperator.addWaypointEventListener(waypointV2MissionOperatorListener);
        waypointV2MissionOperator.addActionListener(waypointV2ActionListener);
    }

    private void uploadWaypointAction() {

        if (!TextUtils.isEmpty(wayPointAction)) {
            List<WayPointsV2Bean.ActionBean> myWayPointActionList = gson.fromJson(wayPointAction, new TypeToken<WayPointsV2Bean.ActionBean>() {
            }.getType());
            waypointV2ActionList.clear();
            for (int i = 0; i < myWayPointActionList.size(); i++) {
                WaypointTrigger waypointAction0Trigger = null;
                WaypointActuator waypointAction0Actuator = null;
                switch (myWayPointActionList.get(i).getTrigger().getTriggerType()) {
                    case "5"://reachPoint
                        waypointAction0Trigger = new WaypointTrigger.Builder()
                                .setTriggerType(ActionTypes.ActionTriggerType.REACH_POINT)
                                .setReachPointParam(new WaypointReachPointTriggerParam.Builder()
                                        .setStartIndex(Integer.parseInt(myWayPointActionList.get(i).getTrigger().getReachPointParam().getStartIndex()))
                                        .setAutoTerminateCount(Integer.parseInt(myWayPointActionList.get(i).getTrigger().getReachPointParam().getAutoTerminateCount()))
                                        .build())
                                .build();
                        break;
                    case "1"://ASSOCIATE
                        waypointAction0Trigger = new WaypointTrigger.Builder()
                                .setTriggerType(ActionTypes.ActionTriggerType.ASSOCIATE)
                                .setAssociateParam(new WaypointV2AssociateTriggerParam.Builder()
                                        .setAssociateActionID(Integer.parseInt(myWayPointActionList.get(i).getTrigger().getAssociateParam().getAssociateActionID()))
                                        .setAssociateType(ActionTypes.AssociatedTimingType.find(Integer.parseInt(myWayPointActionList.get(i).getTrigger().getAssociateParam().getAssociateType())))
                                        .setWaitingTime(Integer.parseInt(myWayPointActionList.get(i).getTrigger().getAssociateParam().getAssociateType()))
                                        .build())
                                .build();

                        break;
                    case "2"://TRAJECTORY
                        waypointAction0Trigger = new WaypointTrigger.Builder()
                                .setTriggerType(ActionTypes.ActionTriggerType.TRAJECTORY)
                                .setTrajectoryParam(new WaypointTrajectoryTriggerParam.Builder()
                                        .setStartIndex(Integer.parseInt(myWayPointActionList.get(i).getTrigger().getTrajectoryParam().getStartIndex()))
                                        .setEndIndex(Integer.parseInt(myWayPointActionList.get(i).getTrigger().getTrajectoryParam().getStartIndex()))
                                        .build())
                                .build();
                        break;
                    case "3"://SIMPLE_INTERVAL
                        waypointAction0Trigger = new WaypointTrigger.Builder()
                                .setTriggerType(ActionTypes.ActionTriggerType.SIMPLE_INTERVAL)
                                .setIntervalTriggerParam(new WaypointIntervalTriggerParam.Builder()
                                        .setType(ActionTypes.ActionIntervalType.TIME)
                                        .setStartIndex(Integer.parseInt(myWayPointActionList.get(i).getTrigger().getIntervalTriggerParam().getStartIndex()))
                                        .setInterval(Integer.parseInt(myWayPointActionList.get(i).getTrigger().getIntervalTriggerParam().getInterval()))
                                        .build())
                                .build();
                        break;


                }

                switch (myWayPointActionList.get(i).getActuator().getActuatorType()) {
                    case "0"://camera
                        waypointAction0Actuator = new WaypointActuator.Builder()
                                .setActuatorType(ActionTypes.ActionActuatorType.CAMERA)
                                .setCameraActuatorParam(new WaypointCameraActuatorParam.Builder()
                                        .setCameraOperationType(ActionTypes.CameraOperationType.find(Integer.parseInt(myWayPointActionList.get(i).getActuator().getCameraOperationType().getOperationType())))
                                        .build())
                                .build();
                        break;
                    case "3"://AIRCRAFT_CONTROL
                        if (myWayPointActionList.get(i).getActuator().getAircraftControlType().getOperationType().equals("1")) {//悬停
                            waypointAction0Actuator = new WaypointActuator.Builder()
                                    .setActuatorType(ActionTypes.ActionActuatorType.AIRCRAFT_CONTROL)
                                    .setAircraftControlActuatorParam(new WaypointAircraftControlParam.Builder()
                                            .setAircraftControlType(ActionTypes.AircraftControlType.START_STOP_FLY)
                                            .setFlyControlParam(new WaypointAircraftControlStartStopFlyParam.Builder()
                                                    .setStartFly(myWayPointActionList.get(i).getActuator().getAircraftControlType().getStartFly().equals("1") ? true : false)
                                                    .build())
                                            .build())
                                    .build();
                        } else if (myWayPointActionList.get(i).getActuator().getAircraftControlType().getOperationType().equals("2")) {
                            waypointAction0Actuator = new WaypointActuator.Builder()
                                    .setActuatorType(ActionTypes.ActionActuatorType.AIRCRAFT_CONTROL)
                                    .setAircraftControlActuatorParam(new WaypointAircraftControlParam.Builder()
                                            .setAircraftControlType(ActionTypes.AircraftControlType.ROTATE_YAW)
                                            .setRotateYawParam(new WaypointAircraftControlRotateYawParam.Builder()
                                                    .setYawAngle(Float.parseFloat(myWayPointActionList.get(i).getActuator().getAircraftControlType().getYawAngle()))
                                                    .setDirection(WaypointV2MissionTypes.WaypointV2TurnMode.find(Integer.parseInt(myWayPointActionList.get(i).getActuator().getAircraftControlType().getDirection())))
//                                            .setRelative()
                                                    .build())
                                            .build())
                                    .build();
                        }
                        break;

                    case "1"://GIMBAL
                        waypointAction0Actuator = new WaypointActuator.Builder()
                                .setActuatorType(ActionTypes.ActionActuatorType.GIMBAL)
                                .setGimbalActuatorParam(new WaypointGimbalActuatorParam.Builder()
                                        .operationType(ActionTypes.GimbalOperationType.find(Integer.parseInt(myWayPointActionList.get(i).getActuator().getGimbalActuatorParam().getOperationType())))
                                        .rotation(new Rotation.Builder()
                                                .mode(RotationMode.ABSOLUTE_ANGLE)
                                                .pitch(Float.parseFloat(myWayPointActionList.get(i).getActuator().getGimbalActuatorParam().getPitch()))
                                                .roll(0)
                                                .yaw(Float.parseFloat(myWayPointActionList.get(i).getActuator().getGimbalActuatorParam().getYaw()))
                                                .time(Integer.parseInt(myWayPointActionList.get(i).getActuator().getGimbalActuatorParam().getTime()))
                                                .build())
                                        .build())
                                .build();
                        break;

                }


                int actionId = i++;
                WaypointV2Action waypointAction0 = new WaypointV2Action.Builder()
                        .setActionID(actionId)//0会报错sdkbug
                        .setTrigger(waypointAction0Trigger)
                        .setActuator(waypointAction0Actuator)
                        .build();
                waypointV2ActionList.add(waypointAction0);

            }
        }

        waypointV2MissionOperator.uploadWaypointActions(waypointV2ActionList, new CommonCallbacks.CompletionCallback<DJIWaypointV2Error>() {
            @Override
            public void onResult(DJIWaypointV2Error djiWaypointV2Error) {
                if (djiWaypointV2Error != null) {
                    Toast.makeText(ConnectionActivity.this, djiWaypointV2Error.getDescription(), Toast.LENGTH_SHORT).show();
                }
            }
        });


//        WaypointTrigger waypointAction0Trigger = new WaypointTrigger.Builder()
//                .setTriggerType(ActionTypes.ActionTriggerType.REACH_POINT)
//                .setReachPointParam(new WaypointReachPointTriggerParam.Builder()
//                        .setStartIndex(0)
//                        .setAutoTerminateCount(0)
//                        .build())
//                .build();
//
//        WaypointActuator waypointAction0Actuator = new WaypointActuator.Builder()
//                .setActuatorType(ActionTypes.ActionActuatorType.CAMERA)
//                .setCameraActuatorParam(new WaypointCameraActuatorParam.Builder()
//                        .setCameraOperationType(ActionTypes.CameraOperationType.CUSTOM_NAME)
//                        .setCustomNameParam(new WaypointCameraCustomNameParam.Builder()
//                                .type(ActionTypes.CameraCustomNameType.DIR)
//                                .customName("testFolder")
//                                .build())
//                        .build())
//                .build();
//
//        WaypointV2Action waypointAction0 = new WaypointV2Action.Builder()
//                .setActionID(0)//0会报错
//                .setTrigger(waypointAction0Trigger)
//                .setActuator(waypointAction0Actuator)
//                .build();
//        waypointV2ActionList.add(waypointAction0);
//
//        WaypointTrigger waypointAction1Trigger = new WaypointTrigger.Builder()
//                .setTriggerType(ActionTypes.ActionTriggerType.ASSOCIATE)
//                .setAssociateParam(new WaypointV2AssociateTriggerParam.Builder()
//                        .setAssociateActionID(0)
//                        .setAssociateType(ActionTypes.AssociatedTimingType.AFTER_FINISHED)
//                        .setWaitingTime(0)
//                        .build())
//                .build();
//
//
////        new WaypointAircraftControlRotateYawParam.Builder().setDirection().setYawAngle()
//
//
//        WaypointActuator waypointAction1Actuator = new WaypointActuator.Builder()
//                .setActuatorType(ActionTypes.ActionActuatorType.AIRCRAFT_CONTROL)
//                .setAircraftControlActuatorParam(new WaypointAircraftControlParam.Builder()
//                        .setAircraftControlType(ActionTypes.AircraftControlType.START_STOP_FLY)
////                        .setFlyControlParam(new WaypointAircraftControlStartStopFlyParam.Builder()
////                                .setStartFly(false)
////                                .build())
//
//                        .build())
//                .build();
//
//        WaypointV2Action waypointAction1 = new WaypointV2Action.Builder()
//                .setActionID(1)
//                .setTrigger(waypointAction1Trigger)
//                .setActuator(waypointAction1Actuator)
//                .build();
//        waypointV2ActionList.add(waypointAction1);
//
//        WaypointTrigger waypointAction2Trigger = new WaypointTrigger.Builder()
//                .setTriggerType(ActionTypes.ActionTriggerType.ASSOCIATE)
//                .setAssociateParam(new WaypointV2AssociateTriggerParam.Builder()
//                        .setAssociateActionID(1)
//                        .setAssociateType(ActionTypes.AssociatedTimingType.AFTER_FINISHED)
//                        .setWaitingTime(0)
//                        .build())
//                .build();
//
//        WaypointActuator waypointAction2Actuator = new WaypointActuator.Builder()
//                .setActuatorType(ActionTypes.ActionActuatorType.GIMBAL)
//                .setGimbalActuatorParam(new WaypointGimbalActuatorParam.Builder()
//                        .operationType(ActionTypes.GimbalOperationType.ROTATE_GIMBAL)
//                        .rotation(new Rotation.Builder()
//                                .mode(RotationMode.ABSOLUTE_ANGLE)
//                                .pitch(0)
//                                .roll(0)
//                                .yaw(-60.0f)
//                                .time(3)
//                                .build())
//                        .build())
//                .build();
//
//        WaypointV2Action waypointAction2 = new WaypointV2Action.Builder()
//                .setActionID(2)
//                .setTrigger(waypointAction2Trigger)
//                .setActuator(waypointAction2Actuator)
//                .build();
//        waypointV2ActionList.add(waypointAction2);
//
//        WaypointTrigger waypointAction3Trigger = new WaypointTrigger.Builder()
//                .setTriggerType(ActionTypes.ActionTriggerType.ASSOCIATE)
//                .setAssociateParam(new WaypointV2AssociateTriggerParam.Builder()
//                        .setAssociateActionID(2)
//                        .setAssociateType(ActionTypes.AssociatedTimingType.AFTER_FINISHED)
//                        .setWaitingTime(0)
//                        .build())
//                .build();
//
//        WaypointActuator waypointAction3Actuator = new WaypointActuator.Builder()
//                .setActuatorType(ActionTypes.ActionActuatorType.CAMERA)
//                .setCameraActuatorParam(new WaypointCameraActuatorParam.Builder()
//                        .setCameraOperationType(ActionTypes.CameraOperationType.SHOOT_SINGLE_PHOTO)
//                        .build())
//                .build();
//
//        WaypointV2Action waypointAction3 = new WaypointV2Action.Builder()
//                .setActionID(3)
//                .setTrigger(waypointAction3Trigger)
//                .setActuator(waypointAction3Actuator)
//                .build();
//        waypointV2ActionList.add(waypointAction3);
//
//        WaypointTrigger waypointAction4Trigger = new WaypointTrigger.Builder()
//                .setTriggerType(ActionTypes.ActionTriggerType.ASSOCIATE)
//                .setAssociateParam(new WaypointV2AssociateTriggerParam.Builder()
//                        .setAssociateActionID(3)
//                        .setAssociateType(ActionTypes.AssociatedTimingType.AFTER_FINISHED)
//                        .setWaitingTime(0)
//                        .build())
//                .build();
//
//        WaypointActuator waypointAction4Actuator = new WaypointActuator.Builder()
//                .setActuatorType(ActionTypes.ActionActuatorType.GIMBAL)
//                .setGimbalActuatorParam(new WaypointGimbalActuatorParam.Builder()
//                        .operationType(ActionTypes.GimbalOperationType.ROTATE_GIMBAL)
//                        .rotation(new Rotation.Builder()
//                                .mode(RotationMode.ABSOLUTE_ANGLE)
//                                .pitch(0)
//                                .roll(0)
//                                .yaw(0)
//                                .time(3)
//                                .build())
//                        .build())
//                .build();
//
//        WaypointV2Action waypointAction4 = new WaypointV2Action.Builder()
//                .setActionID(4)
//                .setTrigger(waypointAction4Trigger)
//                .setActuator(waypointAction4Actuator)
//                .build();
//        waypointV2ActionList.add(waypointAction4);
//
//        WaypointTrigger waypointAction5Trigger = new WaypointTrigger.Builder()
//                .setTriggerType(ActionTypes.ActionTriggerType.ASSOCIATE)
//                .setAssociateParam(new WaypointV2AssociateTriggerParam.Builder()
//                        .setAssociateActionID(4)
//                        .setAssociateType(ActionTypes.AssociatedTimingType.AFTER_FINISHED)
//                        .setWaitingTime(0)
//                        .build())
//                .build();
//
//        WaypointActuator waypointAction5Actuator = new WaypointActuator.Builder()
//                .setActuatorType(ActionTypes.ActionActuatorType.AIRCRAFT_CONTROL)
//                .setAircraftControlActuatorParam(new WaypointAircraftControlParam.Builder()
//                        .setAircraftControlType(ActionTypes.AircraftControlType.START_STOP_FLY)
//                        .setFlyControlParam(new WaypointAircraftControlStartStopFlyParam.Builder()
//                                .setStartFly(true)
//                                .build())
//                        .build())
//                .build();
//
//        WaypointV2Action waypointAction5 = new WaypointV2Action.Builder()
//                .setActionID(5)
//                .setTrigger(waypointAction5Trigger)
//                .setActuator(waypointAction5Actuator)
//                .build();
//        waypointV2ActionList.add(waypointAction5);
//
//        /*
//         *  Action combo 2 - Start recording video at waypoint1 and fly to waypoint2
//         *  From waypoint1 to waypoint2, the camera will gradually rotate toward to the ground
//         *  From waypoint2 to waypoint3, the camera will gradually rotate back to the original position
//         *  The video recording will be stopped at waypoint3
//         */
//
//        WaypointTrigger waypointAction6Trigger = new WaypointTrigger.Builder()
//                .setTriggerType(ActionTypes.ActionTriggerType.REACH_POINT)
//                .setReachPointParam(new WaypointReachPointTriggerParam.Builder()
//                        .setStartIndex(1)
//                        .setAutoTerminateCount(1)
//                        .build())
//                .build();
//
//        WaypointActuator waypointAction6Actuator = new WaypointActuator.Builder()
//                .setActuatorType(ActionTypes.ActionActuatorType.CAMERA)
//                .setCameraActuatorParam(new WaypointCameraActuatorParam.Builder()
//                        .setCameraOperationType(ActionTypes.CameraOperationType.START_RECORD_VIDEO)
//                        .build())
//                .build();
//
//        WaypointV2Action waypointAction6 = new WaypointV2Action.Builder()
//                .setActionID(6)
//                .setTrigger(waypointAction6Trigger)
//                .setActuator(waypointAction6Actuator)
//                .build();
//        waypointV2ActionList.add(waypointAction6);
//
//        WaypointTrigger waypointAction7Trigger = new WaypointTrigger.Builder()
//                .setTriggerType(ActionTypes.ActionTriggerType.TRAJECTORY)
//                .setTrajectoryParam(new WaypointTrajectoryTriggerParam.Builder()
//                        .setStartIndex(1)
//                        .setEndIndex(2)
//                        .build())
//                .build();
//
//        WaypointActuator waypointAction7Actuator = new WaypointActuator.Builder()
//                .setActuatorType(ActionTypes.ActionActuatorType.GIMBAL)
//                .setGimbalActuatorParam(new WaypointGimbalActuatorParam.Builder()
//                        .operationType(ActionTypes.GimbalOperationType.AIRCRAFT_CONTROL_GIMBAL)
//                        .rotation(new Rotation.Builder()
//                                .mode(RotationMode.ABSOLUTE_ANGLE)
//                                .pitch(-90.0f)
//                                .roll(0)
//                                .yaw(0)
//                                .time(5)
//                                .build())
//                        .build())
//                .build();
//
//        WaypointV2Action waypointAction7 = new WaypointV2Action.Builder()
//                .setActionID(7)
//                .setTrigger(waypointAction7Trigger)
//                .setActuator(waypointAction7Actuator)
//                .build();
//        waypointV2ActionList.add(waypointAction7);
//
//        WaypointTrigger waypointAction8Trigger = new WaypointTrigger.Builder()
//                .setTriggerType(ActionTypes.ActionTriggerType.TRAJECTORY)
//                .setTrajectoryParam(new WaypointTrajectoryTriggerParam.Builder()
//                        .setStartIndex(2)
//                        .setEndIndex(3)
//                        .build())
//                .build();
//
//        WaypointActuator waypointAction8Actuator = new WaypointActuator.Builder()
//                .setActuatorType(ActionTypes.ActionActuatorType.GIMBAL)
//                .setGimbalActuatorParam(new WaypointGimbalActuatorParam.Builder()
//                        .operationType(ActionTypes.GimbalOperationType.AIRCRAFT_CONTROL_GIMBAL)
//                        .rotation(new Rotation.Builder()
//                                .mode(RotationMode.ABSOLUTE_ANGLE)
//                                .pitch(0)
//                                .roll(0)
//                                .yaw(0)
//                                .time(5)
//                                .build())
//                        .build())
//                .build();
//
//        WaypointV2Action waypointAction8 = new WaypointV2Action.Builder()
//                .setActionID(8)
//                .setTrigger(waypointAction8Trigger)
//                .setActuator(waypointAction8Actuator)
//                .build();
//        waypointV2ActionList.add(waypointAction8);
//
//        WaypointTrigger waypointAction9Trigger = new WaypointTrigger.Builder()
//                .setTriggerType(ActionTypes.ActionTriggerType.REACH_POINT)
//                .setReachPointParam(new WaypointReachPointTriggerParam.Builder()
//                        .setStartIndex(3)
//                        .setAutoTerminateCount(3)
//                        .build())
//                .build();
//
//        WaypointActuator waypointAction9Actuator = new WaypointActuator.Builder()
//                .setActuatorType(ActionTypes.ActionActuatorType.CAMERA)
//                .setCameraActuatorParam(new WaypointCameraActuatorParam.Builder()
//                        .setCameraOperationType(ActionTypes.CameraOperationType.STOP_RECORD_VIDEO)
//                        .build())
//                .build();
//
//        WaypointV2Action waypointAction9 = new WaypointV2Action.Builder()
//                .setActionID(9)
//                .setTrigger(waypointAction9Trigger)
//                .setActuator(waypointAction9Actuator)
//                .build();
//        waypointV2ActionList.add(waypointAction9);
//
//        /*
//         *  Action combo 3 - The aircraft will start shooting photos every 2 seconds from waypoint5 to finish, and all photos will have "testfile" at the end of their name
//         *
//         */
//
//        WaypointTrigger waypointAction10Trigger = new WaypointTrigger.Builder()
//                .setTriggerType(ActionTypes.ActionTriggerType.SIMPLE_INTERVAL)
//                .setIntervalTriggerParam(new WaypointIntervalTriggerParam.Builder()
//                        .setType(ActionTypes.ActionIntervalType.TIME)
//                        .setStartIndex(4)
//                        .setInterval(2)
//                        .build())
//                .build();
//
//        WaypointActuator waypointAction10Actuator = new WaypointActuator.Builder()
//                .setActuatorType(ActionTypes.ActionActuatorType.CAMERA)
//                .setCameraActuatorParam(new WaypointCameraActuatorParam.Builder()
//                        .setCameraOperationType(ActionTypes.CameraOperationType.CUSTOM_NAME)
//                        .setCustomNameParam(new WaypointCameraCustomNameParam.Builder()
//                                .type(ActionTypes.CameraCustomNameType.FILE)
//                                .customName("testFile")
//                                .build())
//                        .build())
//                .build();
//
//        WaypointV2Action waypointAction10 = new WaypointV2Action.Builder()
//                .setActionID(10)
//                .setTrigger(waypointAction10Trigger)
//                .setActuator(waypointAction10Actuator)
//                .build();
//        waypointV2ActionList.add(waypointAction10);
//
//        WaypointTrigger waypointAction11Trigger = new WaypointTrigger.Builder()
//                .setTriggerType(ActionTypes.ActionTriggerType.ASSOCIATE)
//                .setAssociateParam(new WaypointV2AssociateTriggerParam.Builder()
//                        .setAssociateActionID(10)
//                        .setAssociateType(ActionTypes.AssociatedTimingType.AFTER_FINISHED)
//                        // Because set file name and shoot photo is the same module, it is better to set a waiting time.
//                        .setWaitingTime(0.5f)
//                        .build())
//                .build();
//
//        WaypointActuator waypointAction11Actuator = new WaypointActuator.Builder()
//                .setActuatorType(ActionTypes.ActionActuatorType.CAMERA)
//                .setCameraActuatorParam(new WaypointCameraActuatorParam.Builder()
//                        .setCameraOperationType(ActionTypes.CameraOperationType.SHOOT_SINGLE_PHOTO)
//                        .build())
//                .build();
//
//        WaypointV2Action waypointAction11 = new WaypointV2Action.Builder()
//                .setActionID(11)
//                .setTrigger(waypointAction11Trigger)
//                .setActuator(waypointAction11Actuator)
//                .build();
//        waypointV2ActionList.add(waypointAction11);
//
//        waypointV2MissionOperator.uploadWaypointActions(waypointV2ActionList, new CommonCallbacks.CompletionCallback<DJIWaypointV2Error>() {
//            @Override
//            public void onResult(DJIWaypointV2Error djiWaypointV2Error) {
//                if (djiWaypointV2Error != null) {
//                    Toast.makeText(ConnectionActivity.this, djiWaypointV2Error.getDescription(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }

    private WaypointV2Mission createWaypointMission(Communication communication) {

        String speed = communication.getPara().get(Constant.SPEED);
        String turnMode = communication.getPara().get(Constant.TURN_MODE);
        String altitude = communication.getPara().get("altitude");
        String finishedAction = communication.getPara().get(Constant.FINISHED_ACTION);
        String headingMode = communication.getPara().get(Constant.HEADING_MODE);
        if (!TextUtils.isEmpty(speed)) {
            mSpeed = Float.parseFloat(speed);
        }
        String mPathMode = communication.getPara().get(Constant.FLIGHT_PATH_MODE);
        String wayPoints = communication.getPara().get(Constant.WAY_POINTS);
        Log.d("wayPointsV2",wayPoints);

//        wayPointAction = communication.getPara().get("action");
//        Log.d("wayPointAction",wayPointAction);
        if (!TextUtils.isEmpty(wayPoints)) {
            List<WayPointsV2Bean.WayPointsBean> myWayPointList = gson.fromJson(wayPoints, new TypeToken<WayPointsV2Bean.WayPointsBean>() {
            }.getType());
            waypointV2List.clear();
            for (int i = 0; i < myWayPointList.size(); i++) {

                WaypointV2 waypoint0 = new WaypointV2.Builder()
                        //设置经纬度
                        .setCoordinate(new LocationCoordinate2D(Double.parseDouble(myWayPointList.get(i).getLatitude()), Double.parseDouble(myWayPointList.get(i).getLongitude())))
                        //高度[-200,500]
                        .setAltitude(Double.parseDouble(myWayPointList.get(i).getAltitude()))
                        //设置路径模式
                        .setFlightPathMode(WaypointV2MissionTypes.WaypointV2FlightPathMode.find(Integer.parseInt(myWayPointList.get(i).getFlightPathMode())))
                        .setHeadingMode(WaypointV2MissionTypes.WaypointV2HeadingMode.find(Integer.parseInt(myWayPointList.get(i).getHeadingMode())))
                        .setTurnMode(WaypointV2MissionTypes.WaypointV2TurnMode.find(Integer.parseInt(myWayPointList.get(i).getTurnMode())))
                        .setAutoFlightSpeed(Float.parseFloat(myWayPointList.get(i).getSpeed()))

                        .build();
                waypointV2List.add(waypoint0);
            }
        }

        waypointV2MissionBuilder = new WaypointV2Mission.Builder();
        waypointV2MissionBuilder.setMissionID(new Random().nextInt(65535))
                .setMaxFlightSpeed(TextUtils.isEmpty(mPathMode) ? 15f : Float.parseFloat(speed))
                .setAutoFlightSpeed(TextUtils.isEmpty(mPathMode) ? 15f : Float.parseFloat(speed))
//                .setFinishedAction(WaypointV2MissionTypes.MissionFinishedAction.AUTO_LAND)
                .setFinishedAction(WaypointV2MissionTypes.MissionFinishedAction.find(Integer.parseInt(finishedAction)))
                .setGotoFirstWaypointMode(WaypointV2MissionTypes.MissionGotoWaypointMode.SAFELY)
                .setExitMissionOnRCSignalLostEnabled(true)
                .setRepeatTimes(1)
                .addwaypoints(waypointV2List);

        return waypointV2MissionBuilder.build();


    }

    private void startWaypointV2(Communication communication) {
        if (canStartMission) {
            waypointV2MissionOperator.startMission(new CommonCallbacks.CompletionCallback<DJIWaypointV2Error>() {
                @Override
                public void onResult(DJIWaypointV2Error djiWaypointV2Error) {
                    if (djiWaypointV2Error == null) {
                        Toast.makeText(ConnectionActivity.this, djiWaypointV2Error == null ? "Mission is started successfully" : djiWaypointV2Error.getDescription(), Toast.LENGTH_SHORT).show();
                    }
                    if (djiWaypointV2Error != null) {
                        Log.d("WMUEWMUE", "startWaypointMission报错");
                        communication.setResult("startMission报错:" + djiWaypointV2Error.getDescription());
                        communication.setCode(-1);
                        communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                        NettyClient.getInstance().sendMessage(communication, null);
                    } else {
                        Log.d("WMUEWMUE", "startWaypointMission成功");
                        communication.setResult("Success");
                        communication.setCode(200);
                        communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                        NettyClient.getInstance().sendMessage(communication, null);
                    }
                }
            });
        } else {
            Toast.makeText(ConnectionActivity.this, "Wait for mission to be uploaded", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopWaypointV2(Communication communication) {
        if (waypointV2MissionOperator.getCurrentState().equals(WaypointV2MissionState.EXECUTING)
                || waypointV2MissionOperator.getCurrentState().equals(WaypointV2MissionState.INTERRUPTED)) {
            waypointV2MissionOperator.stopMission(new CommonCallbacks.CompletionCallback<DJIWaypointV2Error>() {
                @Override
                public void onResult(DJIWaypointV2Error djiWaypointV2Error) {
                    Toast.makeText(ConnectionActivity.this, djiWaypointV2Error == null ? "The mission has been stopped" : djiWaypointV2Error.getDescription(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        communication.setResult("Success");
        communication.setCode(200);
        communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
        NettyClient.getInstance().sendMessage(communication, null);
    }

    private void pauseWaypointV2(Communication communication) {
        if (waypointV2MissionOperator.getCurrentState().equals(WaypointV2MissionState.EXECUTING)) {
            waypointV2MissionOperator.interruptMission(new CommonCallbacks.CompletionCallback<DJIWaypointV2Error>() {
                @Override
                public void onResult(DJIWaypointV2Error djiWaypointV2Error) {
                    Toast.makeText(ConnectionActivity.this, djiWaypointV2Error == null ? "The mission has been interrupted" : djiWaypointV2Error.getDescription(), Toast.LENGTH_SHORT).show();

                    if (djiWaypointV2Error != null) {
                        communication.setResult(djiWaypointV2Error.getDescription());
                        communication.setCode(-1);
                        communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                        NettyClient.getInstance().sendMessage(communication, null);
                    } else {
                        communication.setResult("Success");
                        communication.setCode(200);
                        communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                        NettyClient.getInstance().sendMessage(communication, null);
                        isHangXianPause = true;
                    }
                }
            });
        }


    }

    private void resumeWaypointV2(Communication communication) {
        if (waypointV2MissionOperator.getCurrentState().equals(WaypointV2MissionState.INTERRUPTED)) {
            waypointV2MissionOperator.recoverMission(new CommonCallbacks.CompletionCallback<DJIWaypointV2Error>() {
                @Override
                public void onResult(DJIWaypointV2Error djiWaypointV2Error) {
                    Toast.makeText(ConnectionActivity.this, djiWaypointV2Error == null ? "The mission has been recovered" : djiWaypointV2Error.getDescription(), Toast.LENGTH_SHORT).show();

                    if (djiWaypointV2Error != null) {
                        communication.setResult(djiWaypointV2Error.getDescription());
                        communication.setCode(-1);
                        communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                        NettyClient.getInstance().sendMessage(communication, null);
                    } else {
                        communication.setResult("Success");
                        communication.setCode(200);
                        communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                        NettyClient.getInstance().sendMessage(communication, null);
                        isHangXianPause = true;
                    }
                }
            });
        }


    }

    private void tearDownListener() {
        if (waypointV2MissionOperator != null) {
            waypointV2MissionOperator.removeWaypointListener(waypointV2MissionOperatorListener);
            waypointV2MissionOperator.removeActionListener(waypointV2ActionListener);
        }
    }


    //通用的Callback
    private void CommonDjiCallback(DJIError djiError, Communication communication) {
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

    private void TestPrint(Communication communication) {
        communication.setResult("true");
        communication.setCode(200);
        communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
        NettyClient.getInstance().sendMessage(communication, null);
    }


    private void setCapture(Communication communication) {
//        String angle = communication.getPara().get("angle");
//        String zoom = communication.getPara().get("zoom");

        if(webInitializationBean.getCurrentLens().equals("3")){//变焦
            camera_up_and_down_by_a(communication);
        }else{//不是变焦要去变
            change_lens(communication);

        }

    }
    private DJIKey getDataKey;
    private DJIKey sendDataKey;
    private DJIKey payloadNameKey;
    private String payloadName = "";

    private void initListener() {
        getDataKey = PayloadKey.create(PayloadKey.GET_DATA_FROM_PAYLOAD);
        sendDataKey = PayloadKey.create(PayloadKey.SEND_DATA_TO_PAYLOAD);//可能是通过此标识向设备发送指令
        payloadNameKey = PayloadKey.create(PayloadKey.PAYLOAD_PRODUCT_NAME);
        if (!TextUtils.isEmpty(sendDataKey.toString())){
            showToast("sendDataKey="+sendDataKey.toString());
        }else{
            showToast("sendDataKey is null");
        }
        if (!TextUtils.isEmpty(payloadNameKey.toString())){
//            showToast("payloadNameKey="+payloadNameKey.toString());
        }else{
            showToast("payloadNameKey is null");
        }

        if (KeyManager.getInstance() != null) {
            KeyManager.getInstance().addListener(getDataKey, getDataListener);
            KeyManager.getInstance().addListener(payloadNameKey, getNameListener);
        }else{
            showToast("KeyloadManager null");
        }
        Object name = KeyManager.getInstance().getValue(payloadNameKey);
        if (name != null) {
            payloadName = name.toString();
//            showToast(payloadName);
        }else{
//            showToast("not found payload");
        }
    }

    private void unInitListener() {
        KeyManager.getInstance().removeListener(getDataListener);
        KeyManager.getInstance().removeListener(getNameListener);
    }
    private KeyListener getDataListener = new KeyListener() {
        @Override
        public void onValueChange(@Nullable Object oldValue, @Nullable final Object newValue) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (newValue instanceof byte[]) {
                            //String str = BytesUtil.byte2hex((byte[]) newValue);
                            byte[] data = (byte[]) newValue;
                            Log.e(TAG, "receiving data size:" + data.length);
                        }
                    }
                });

        }
    };

    private KeyListener getNameListener = new KeyListener() {
        @Override
        public void onValueChange(@Nullable Object oldValue, @Nullable final Object newValue) {
            if (payloadName != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (newValue instanceof String) {
                            payloadName = newValue.toString();
                        }
                    }
                });
            }
        }
    };
    private byte[] data = {0x24,0x00,0x0A,0x54, (byte) 0xc2, (byte) 0xde, (byte) 0xc5, (byte) 0xcc,0x00,0x23};
    private void sendData() {

        KeyManager.getInstance().performAction(sendDataKey, new ActionCallback() {
            @Override
            public void onSuccess() {
                showToast("send data success! " );
            }
            @Override
            public void onFailure(@NonNull DJIError error) {
                showToast("send data failed"+error.getDescription()+"code="+error.getErrorCode());
            }
        }, data);
    }
}
