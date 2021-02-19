package com.compass.ux.ui;

import androidx.annotation.Nullable;

import dji.common.airlink.OcuSyncBandwidth;
import dji.common.airlink.OcuSyncFrequencyBand;
import dji.common.airlink.PhysicalSource;
import dji.common.airlink.SignalQualityCallback;
import dji.common.airlink.WifiChannelInterference;
import dji.common.camera.CameraVideoStreamSource;
import dji.common.camera.LaserMeasureInformation;
import dji.common.camera.PhotoTimeLapseSettings;
import dji.common.camera.SettingsDefinitions;
import dji.common.camera.StorageState;
import dji.common.camera.WatermarkSettings;
import dji.common.error.DJIError;
import dji.common.error.DJIWaypointV2Error;
import dji.common.flightcontroller.ConnectionFailSafeBehavior;
import dji.common.flightcontroller.FlightControllerState;
import dji.common.flightcontroller.GravityCenterState;
import dji.common.flightcontroller.LEDsSettings;
import dji.common.flightcontroller.ObstacleDetectionSector;
import dji.common.flightcontroller.VisionDetectionState;
import dji.common.flightcontroller.WindDirection;
import dji.common.flightcontroller.flightassistant.PerceptionInformation;
import dji.common.flightcontroller.rtk.CoordinateSystem;
import dji.common.flightcontroller.rtk.NetworkServiceSettings;
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
import dji.common.mission.waypointv2.Action.InterruptRecoverActionType;
import dji.common.mission.waypointv2.Action.WaypointActuator;
import dji.common.mission.waypointv2.Action.WaypointAircraftControlParam;
import dji.common.mission.waypointv2.Action.WaypointAircraftControlRotateYawParam;
import dji.common.mission.waypointv2.Action.WaypointAircraftControlStartStopFlyParam;
import dji.common.mission.waypointv2.Action.WaypointCameraActuatorParam;
import dji.common.mission.waypointv2.Action.WaypointCameraZoomParam;
import dji.common.mission.waypointv2.Action.WaypointGimbalActuatorParam;
import dji.common.mission.waypointv2.Action.WaypointReachPointTriggerParam;
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
import dji.keysdk.DiagnosticsKey;
import dji.keysdk.FlightControllerKey;
import dji.keysdk.GimbalKey;
import dji.keysdk.KeyManager;
import dji.keysdk.callback.GetCallback;
import dji.keysdk.callback.KeyListener;
import dji.keysdk.callback.SetCallback;
import dji.sdk.airlink.OcuSyncLink;
import dji.sdk.airlink.WiFiLink;
import dji.sdk.base.BaseProduct;
import dji.sdk.base.DJIDiagnostics;
import dji.sdk.battery.Battery;
import dji.sdk.camera.Camera;
import dji.sdk.camera.Lens;
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
import dji.sdk.sdkmanager.DJISDKManager;
import dji.sdk.useraccount.UserAccountManager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
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
import com.compass.ux.takephoto.FPVDemoApplication;
import com.compass.ux.utils.PagerUtils;
import com.compass.ux.utils.DeleteUtil;
import com.compass.ux.utils.LocationUtils;
import com.compass.ux.utils.MapConvertUtils;
import com.compass.ux.utils.SPUtils;
import com.compass.ux.utils.fastClick;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static dji.common.camera.CameraVideoStreamSource.DEFAULT;
import static dji.common.camera.CameraVideoStreamSource.INFRARED_THERMAL;
import static dji.common.camera.CameraVideoStreamSource.WIDE;
import static dji.common.camera.CameraVideoStreamSource.ZOOM;
import static dji.common.camera.SettingsDefinitions.ExposureMode.MANUAL;
import static dji.common.camera.SettingsDefinitions.ExposureMode.PROGRAM;
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
    private Button mBtnOpen, btn_download, btn_gaode, btn_simulator, btn_login, btn_pl, btn_voice_end;
    private EditText et_zoom;
    private EditText et_url;
    private CheckBox repeat_send_checkbox;
    private SeekBar seek_bar_volume;

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
    double goHomeLat = 0, goHomeLong = 0;
    boolean visionAssistedPosition, precisionLand, upwardsAvoidance, collisionAvoidance, landingProtection;
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
    private boolean isCapture = false;//抓拍组合动作

    //航线V2
    private WaypointV2MissionOperator waypointV2MissionOperator = null;
    public static WaypointV2Mission.Builder waypointV2MissionBuilder = null;
    private List<WaypointV2> waypointV2List = new ArrayList<>();
    private WaypointV2MissionOperatorListener waypointV2MissionOperatorListener;
    private boolean canUploadMission = false;
    private boolean canStartMission = false;
    private WaypointV2ActionListener waypointV2ActionListener = null;
    private List<WaypointV2Action> waypointV2ActionList = new ArrayList<>();
    private ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);
    private ScheduledFuture<?> scheduledFuture;

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
//                VideoFeeder.getInstance().setTranscodingDataRate(10.0f);
                String cvs = VideoFeeder.getInstance().getPrimaryVideoFeed().getVideoSource().value() + "";
                if (cvs.equals("5")) {
                    currentVideoSource = "1";
                } else {
                    currentVideoSource = "0";
                }
                webInitializationBean.setCurrentVideoSource(currentVideoSource);
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
        if (DJISDKManager.getInstance().getProduct() != null) {
            DJISDKManager.getInstance().getProduct().setDiagnosticsInformationCallback(null);//关闭错误日志
        }
        //waypointV2destory
        tearDownListener();

        if (mCodecManager != null) {
            mCodecManager.cleanSurface();
            mCodecManager = null;
        }
        if (scheduledFuture != null && !scheduledFuture.isCancelled() && !scheduledFuture.isDone()) {
            scheduledFuture.cancel(true);
        }
        executorService.shutdownNow();
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
        btn_voice_end = findViewById(R.id.btn_voice_end);
        btn_voice_end.setOnClickListener(this);
        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
        et_zoom = findViewById(R.id.et_zoom);
//        repeat_send_checkbox = findViewById(R.id.repeat_send_checkbox);
//        repeat_send_checkbox.setOnCheckedChangeListener(onCheckedChangeListener);
        seek_bar_volume = findViewById(R.id.seek_bar_volume);
        seek_bar_volume.setOnSeekBarChangeListener(onSeekBarChangeListener);
        mVideoSurface = (TextureView) findViewById(R.id.video_previewer_surface);
        if (null != mVideoSurface) {
            mVideoSurface.setSurfaceTextureListener(this);
        }
    }

    SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            PagerUtils instance = PagerUtils.getInstance();
            byte[] content = instance.intToBytes(seekBar.getProgress());
            byte[] ins = {0x56};
//            Log.e("测试音量",seekBar.getProgress()+"");
            testSendTTS(instance.dataCopy(ins, content));
        }
    };

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
//                stopWaypointV2(null);
//                if (repeat_send_checkbox.isChecked()) {
//                    scheduledFuture = executorService.scheduleAtFixedRate(repeatRunnable, 100, 1000/3, TimeUnit.MILLISECONDS);
//                    //ToastUtils.showToast("start send date frequently");
//                } else {
                testSendTTS(ttsRepeat);
                testSendTTS(data);
//                }

                break;
            }
            case R.id.btn_simulator: {
                //去控制飞机
//                Intent intent = new Intent(this, SimulatorMainActivity.class);
//                startActivity(intent);
//                isLiveShowOn();
//                startLiveShow(null);
//                resumeWaypointV2(null);
                testSendTTS(voiceStart);
                PagerUtils instance = PagerUtils.getInstance();
                String bytesToString = instance.bytesToString(instance.readFileFromAssets(this, null, "6353.mp3"));
                byte[] bytes = instance.HexString2Bytes(bytesToString);
                voiceByteData = instance.splitBytes(bytes, 120);
                testSendVoice(voiceByteData[0]);
                break;
            }
            case R.id.btn_voice_end: {
                testSendTTS(voiceEnd);
            }
            break;

            case R.id.btn_login:
//                DJISDKManager.getInstance().getLiveStreamManager().stopStream();
//                Toast.makeText(getApplicationContext(), "Stop Live Show", Toast.LENGTH_SHORT).show();
//                pauseWaypointV2(null);
                testSendTTS(ttsStop);
                break;
            default:
                break;
        }
    }

    private byte voiceByteData[][];

    private void testSendVoice(byte[] test) {
        if (FPVDemoApplication.getProductInstance() != null) {
            Payload payload = FPVDemoApplication.getProductInstance().getPayload();
            payload.sendDataToPayload(test, voiceCallBack);
        } else {
            showToast("未检测到payload设备");
        }
    }

    CommonCallbacks.CompletionCallback voiceCallBack = new CommonCallbacks.CompletionCallback() {
        @Override
        public void onResult(DJIError djiError) {
            if (djiError != null) {
                showToast("sendDataFail+" + djiError.getErrorCode());
            } else {
                voiceByteData = PagerUtils.getInstance().deleteAt(voiceByteData, 0);
                if (voiceByteData.length > 0) {
                    testSendVoice(voiceByteData[0]);
                } else {
                    testSendTTS(voiceEnd);
                }
            }
        }
    };

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

                //关闭音频
//                DJISDKManager.getInstance().getLiveStreamManager().setAudioStreamingEnabled(false);
//                DJISDKManager.getInstance().getLiveStreamManager().setAudioMuted(false);
                DJISDKManager.getInstance().getLiveStreamManager().setVideoEncodingEnabled(true);

                int result = DJISDKManager.getInstance().getLiveStreamManager().startStream();
                DJISDKManager.getInstance().getLiveStreamManager().setStartTime();


                //wang
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String sss = "startLive:" + result +
                                "\n isVideoStreamSpeedConfigurable:" + DJISDKManager.getInstance().getLiveStreamManager().isVideoStreamSpeedConfigurable() +
                                "\n isLiveAudioEnabled:" + DJISDKManager.getInstance().getLiveStreamManager().isLiveAudioEnabled() +
                                "\n isStreaming:" + DJISDKManager.getInstance().getLiveStreamManager().isStreaming() +
                                "\n VideoEncodingEnabled:" + DJISDKManager.getInstance().getLiveStreamManager().isVideoEncodingEnabled();
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
            initGimbal();
            addWaypointMissionListener();//添加航点的监听
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
                                goHomeLat = flightControllerState.getHomeLocation().getLatitude();
                                goHomeLong = flightControllerState.getHomeLocation().getLongitude();
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
        //接入扩音器时camera数量为2，多一个是payloadCamera,这里我们暂取第一位
        camera = FPVDemoApplication.getProductInstance().getCameras().get(0);
        if (camera != null) {
            //设置比例为16：9
//            camera.getLens(camera.getIndex()).setPhotoAspectRatio(RATIO_16_9, new CommonCallbacks.CompletionCallback() {
//                @Override
//                public void onResult(DJIError djiError) {
//                    if(djiError!=null){
//                        Log.d("RATIO_16_9_Lens",djiError.toString());
//                    }else{
//                        Log.d("RATIO_16_9_Lens","success");
//                    }
//                }
//            });

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
                Log.e("相机位置", camera.getIndex()+"");

//                //返回曝光模式
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





//                获取变焦距离
            camera.getLens(camera.getIndex()).getHybridZoomSpec(new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.HybridZoomSpec>() {
                @Override
                public void onSuccess(SettingsDefinitions.HybridZoomSpec hybridZoomSpec) {

                    Log.d("HHHHHFocalLength",hybridZoomSpec.getFocalLengthStep()+"");//11
                    Log.d("HHHHHMaxH",hybridZoomSpec.getMaxHybridFocalLength()+"");//55620
                    Log.d("HHHHHMinH",hybridZoomSpec.getMinHybridFocalLength()+"");//317
                    Log.d("HHHHHMaxO",hybridZoomSpec.getMaxOpticalFocalLength()+"");//5562
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            });
//                获取当前变焦焦距
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


    @Override
    protected void notifyData(Communication message) {
        Communication communication = message;

        switch (communication.getMethod()) {
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
                communication.setResult(lastFlying ? "1" : "0");
                communication.setCode(200);
                communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                NettyClient.getInstance().sendMessage(communication, null);
                break;
            //获取返航点经纬度
            case "getSLngLat":
                if (goHomeLat == 0 && goHomeLong == 0) {
                    communication.setResult("undefined");
                } else {
                    LatLng latLngHome = MapConvertUtils.getGDLatLng(goHomeLat, goHomeLong, ConnectionActivity.this);
                    communication.setResult(latLngHome.longitude + "," + latLngHome.latitude);
                }
                communication.setCode(200);
                communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                NettyClient.getInstance().sendMessage(communication, null);
                break;
            //抓拍模式
            case Constant.CAPTURE:
                isCapture = true;
                setCapture(communication);
                //文本喊话
            case Constant.SEND_VOICE_COMMAND:
                sendTTS2Payload(communication);
                break;
            case Constant.SEND_VOICE_MP3:
               String MP3HexString = communication.getPara().get("MP3");
               Log.e("mp3Byte字符串",MP3HexString);
                communication.setCode(200);
                communication.setResult("Success");
                communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                NettyClient.getInstance().sendMessage(communication, null);
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
                        if (isCapture) {
                            setCameraZoom(communication);
                        } else {
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
        String type = "";
        if (isCapture) {
            type = "2";
        } else {
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
                        if (isCapture) {//抓拍
                            camera_up_and_down_by_a(communication);
                        } else {
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
                        if (isCapture) {
                            isCapture = false;
                        } else {

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

    private void setPrecisionLandingEnabled(Communication communication) {

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
                    upwardsAvoidance = type.equals("1") ? true : false;
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
    private void setLandingProtection(Communication communication) {
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
                    landingProtection = type.equals("1") ? true : false;
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
            //For M300RTK, you need to actively request an I frame.
            mCodecManager.resetKeyFrame();
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
        SPUtils.put(ConnectionActivity.this, "waypoint", "wayPoints", wayPoints);
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
            if (TextUtils.isEmpty(wayPoints)) {
                wayPoints = SPUtils.get(ConnectionActivity.this, "waypoint", "wayPoints", "") + "";
                targetWaypointIndex = Integer.parseInt(SPUtils.get(ConnectionActivity.this, "waypoint", "targetWaypointIndex", "0") + "");
            }

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
//                SPUtils.getInstance().put("targetWaypointIndex",targetWaypointIndex);
                SPUtils.put(ConnectionActivity.this, "waypoint", "targetWaypointIndex", "0");
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
        communication_upload_mission = communication;
        //loadMission
        if (waypointV2MissionOperator.getCurrentState().equals(WaypointV2MissionState.READY_TO_UPLOAD) || waypointV2MissionOperator.getCurrentState().equals(WaypointV2MissionState.READY_TO_EXECUTE)) {
            waypointV2MissionOperator.loadMission(createWaypointMission(communication), new CommonCallbacks.CompletionCallback<DJIWaypointV2Error>() {
                @Override
                public void onResult(DJIWaypointV2Error djiWaypointV2Error) {

                    if (djiWaypointV2Error == null) {
                        Toast.makeText(ConnectionActivity.this, "Mission is loaded successfully", Toast.LENGTH_SHORT).show();
                        waypointV2MissionOperator.uploadMission(new CommonCallbacks.CompletionCallback<DJIWaypointV2Error>() {
                            @Override
                            public void onResult(DJIWaypointV2Error djiWaypointV2Error) {
                                if (djiWaypointV2Error != null) {
                                    Toast.makeText(ConnectionActivity.this, djiWaypointV2Error.getDescription(), Toast.LENGTH_SHORT).show();
                                    communication_upload_mission.setResult(djiWaypointV2Error.getDescription());
                                    communication_upload_mission.setCode(-1);
                                    communication_upload_mission.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                                    NettyClient.getInstance().sendMessage(communication_upload_mission, null);
                                }
                            }
                        });
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
                    communication_upload_mission.setResult(waypointV2MissionUploadEvent.getError().getDescription());
                    communication_upload_mission.setCode(-1);
                    communication_upload_mission.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                    NettyClient.getInstance().sendMessage(communication_upload_mission, null);
                }
                Log.d("wayPointV2Upload", "PreviousState=" + waypointV2MissionUploadEvent.getPreviousState()
                        + "\nCurrentState=" + waypointV2MissionUploadEvent.getCurrentState());
                if (waypointV2MissionUploadEvent.getPreviousState() == WaypointV2MissionState.UPLOADING
                        && waypointV2MissionUploadEvent.getCurrentState() == WaypointV2MissionState.READY_TO_EXECUTE) {
                    Toast.makeText(ConnectionActivity.this, "Mission is uploaded successfully", Toast.LENGTH_SHORT).show();
                    communication_upload_mission.setResult("Mission is uploaded successfully");
                    communication_upload_mission.setCode(200);
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
                    uploadWaypointAction();
                }
                //上传航线成功
                if (actionUploadEvent.getPreviousState() == ActionState.UPLOADING
                        && actionUploadEvent.getCurrentState() == ActionState.READY_TO_EXECUTE) {
                    Toast.makeText(ConnectionActivity.this, "Actions are uploaded successfully", Toast.LENGTH_SHORT).show();

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
            List<WayPointsV2Bean.WayPointsBean> myWayPointActionList = gson.fromJson(wayPointAction, new TypeToken<WayPointsV2Bean.WayPointsBean>() {
            }.getType());
            waypointV2ActionList.clear();
            for (int i = 0; i < myWayPointActionList.size(); i++) {
                WaypointTrigger waypointAction0Trigger = null;
                WaypointActuator waypointAction0Actuator = null;
                int curr = i + 1;
                waypointAction0Trigger = new WaypointTrigger.Builder()
                        .setTriggerType(ActionTypes.ActionTriggerType.REACH_POINT)
                        .setReachPointParam(new WaypointReachPointTriggerParam.Builder()
                                .setStartIndex(curr)
                                .setAutoTerminateCount(curr)
                                .build())
                        .build();

                for (int j = 0; j < myWayPointActionList.get(i).getWayPointAction().size(); j++) {
                    switch (myWayPointActionList.get(i).getWayPointAction().get(j).getActionType()) {
                        case "0"://悬停
                            waypointAction0Actuator = new WaypointActuator.Builder()
                                    .setActuatorType(ActionTypes.ActionActuatorType.AIRCRAFT_CONTROL)
                                    .setAircraftControlActuatorParam(new WaypointAircraftControlParam.Builder()
                                            .setAircraftControlType(ActionTypes.AircraftControlType.START_STOP_FLY)
                                            .setFlyControlParam(new WaypointAircraftControlStartStopFlyParam.Builder()
                                                    .setStartFly(false)
                                                    .build())
                                            .build())
                                    .build();
                            break;
                        case "1"://继续飞行
                            waypointAction0Actuator = new WaypointActuator.Builder()
                                    .setActuatorType(ActionTypes.ActionActuatorType.AIRCRAFT_CONTROL)
                                    .setAircraftControlActuatorParam(new WaypointAircraftControlParam.Builder()
                                            .setAircraftControlType(ActionTypes.AircraftControlType.START_STOP_FLY)
                                            .setFlyControlParam(new WaypointAircraftControlStartStopFlyParam.Builder()
                                                    .setStartFly(true)
                                                    .build())
                                            .build())
                                    .build();
                            break;
                        case "2"://旋转
                            waypointAction0Actuator = new WaypointActuator.Builder()
                                    .setActuatorType(ActionTypes.ActionActuatorType.AIRCRAFT_CONTROL)
                                    .setAircraftControlActuatorParam(new WaypointAircraftControlParam.Builder()
                                            .setAircraftControlType(ActionTypes.AircraftControlType.ROTATE_YAW)
                                            .setRotateYawParam(new WaypointAircraftControlRotateYawParam.Builder()
                                                    .setYawAngle(Float.parseFloat(myWayPointActionList.get(i).getWayPointAction().get(j).getYawAngle()))
                                                    .setDirection(WaypointV2MissionTypes.WaypointV2TurnMode.find(Integer.parseInt(myWayPointActionList.get(i).getWayPointAction().get(j).getDirection())))
//                                            .setRelative()
                                                    .build())
                                            .build())
                                    .build();
                            break;
                        case "3"://云台
                            waypointAction0Actuator = new WaypointActuator.Builder()
                                    .setActuatorType(ActionTypes.ActionActuatorType.GIMBAL)
                                    .setGimbalActuatorParam(new WaypointGimbalActuatorParam.Builder()
                                            .operationType(ActionTypes.GimbalOperationType.ROTATE_GIMBAL)
                                            .rotation(new Rotation.Builder()
                                                    .mode(RotationMode.ABSOLUTE_ANGLE)
                                                    .pitch(Float.parseFloat(myWayPointActionList.get(i).getWayPointAction().get(j).getPitch()))
                                                    .roll(0)
                                                    .yaw(Float.parseFloat(myWayPointActionList.get(i).getWayPointAction().get(j).getPitch()))
                                                    .time(2)
                                                    .build())
                                            .build())
                                    .build();
                            break;
                        case "4"://变焦
                            waypointAction0Actuator = new WaypointActuator.Builder()
                                    .setActuatorType(ActionTypes.ActionActuatorType.CAMERA)
                                    .setCameraActuatorParam(new WaypointCameraActuatorParam.Builder()
                                            .setCameraOperationType(ActionTypes.CameraOperationType.ZOOM)
                                            .setZoomParam(new WaypointCameraZoomParam.Builder()
                                                    .setFocalLength(Integer.parseInt(myWayPointActionList.get(i).getWayPointAction().get(j).getFocalLength()))
                                                    .build())
                                            .build())
                                    .build();
                            break;
                        case "5"://拍照
                            waypointAction0Actuator = new WaypointActuator.Builder()
                                    .setActuatorType(ActionTypes.ActionActuatorType.CAMERA)
                                    .setCameraActuatorParam(new WaypointCameraActuatorParam.Builder()
                                            .setCameraOperationType(ActionTypes.CameraOperationType.SHOOT_SINGLE_PHOTO)
                                            .build())
                                    .build();
                            break;
                        case "6"://开始录像
                            waypointAction0Actuator = new WaypointActuator.Builder()
                                    .setActuatorType(ActionTypes.ActionActuatorType.CAMERA)
                                    .setCameraActuatorParam(new WaypointCameraActuatorParam.Builder()
                                            .setCameraOperationType(ActionTypes.CameraOperationType.START_RECORD_VIDEO)
                                            .build())
                                    .build();
                            break;
                        case "7"://结束录像
                            waypointAction0Actuator = new WaypointActuator.Builder()
                                    .setActuatorType(ActionTypes.ActionActuatorType.CAMERA)
                                    .setCameraActuatorParam(new WaypointCameraActuatorParam.Builder()
                                            .setCameraOperationType(ActionTypes.CameraOperationType.STOP_RECORD_VIDEO)
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

                    //如果是悬停 加上等待时间
                    if (myWayPointActionList.get(i).getWayPointAction().get(j).getActionType().equals("0")) {
                        WaypointTrigger waypointAction1Trigger = new WaypointTrigger.Builder()
                                .setTriggerType(ActionTypes.ActionTriggerType.ASSOCIATE)
                                .setAssociateParam(new WaypointV2AssociateTriggerParam.Builder()
                                        .setAssociateActionID(actionId)
                                        .setAssociateType(ActionTypes.AssociatedTimingType.SIMULTANEOUSLY)
                                        .setWaitingTime(Integer.parseInt(myWayPointActionList.get(i).getWayPointAction().get(j).getWaitingTime()))
                                        .build())
                                .build();

                        WaypointActuator waypointAction1Actuator = new WaypointActuator.Builder()
                                .setActuatorType(ActionTypes.ActionActuatorType.AIRCRAFT_CONTROL)
                                .setAircraftControlActuatorParam(new WaypointAircraftControlParam.Builder()
                                        .setAircraftControlType(ActionTypes.AircraftControlType.START_STOP_FLY)
                                        .setFlyControlParam(new WaypointAircraftControlStartStopFlyParam.Builder()
                                                .setStartFly(true)
                                                .build())
                                        .build())
                                .build();

                        WaypointV2Action waypointAction1 = new WaypointV2Action.Builder()
                                .setActionID(actionId)//0会报错sdkbug
                                .setTrigger(waypointAction1Trigger)
                                .setActuator(waypointAction1Actuator)
                                .build();
                        waypointV2ActionList.add(waypointAction1);

                    }

                }


//                switch (myWayPointActionList.get(i).getTrigger().getTriggerType()) {
//                    case "5"://reachPoint
//                        waypointAction0Trigger = new WaypointTrigger.Builder()
//                                .setTriggerType(ActionTypes.ActionTriggerType.REACH_POINT)
//                                .setReachPointParam(new WaypointReachPointTriggerParam.Builder()
//                                        .setStartIndex(Integer.parseInt(myWayPointActionList.get(i).getTrigger().getReachPointParam().getStartIndex()))
//                                        .setAutoTerminateCount(Integer.parseInt(myWayPointActionList.get(i).getTrigger().getReachPointParam().getAutoTerminateCount()))
//                                        .build())
//                                .build();
//                        break;
//                    case "1"://ASSOCIATE
//                        waypointAction0Trigger = new WaypointTrigger.Builder()
//                                .setTriggerType(ActionTypes.ActionTriggerType.ASSOCIATE)
//                                .setAssociateParam(new WaypointV2AssociateTriggerParam.Builder()
//                                        .setAssociateActionID(Integer.parseInt(myWayPointActionList.get(i).getTrigger().getAssociateParam().getAssociateActionID()))
//                                        .setAssociateType(ActionTypes.AssociatedTimingType.find(Integer.parseInt(myWayPointActionList.get(i).getTrigger().getAssociateParam().getAssociateType())))
//                                        .setWaitingTime(Integer.parseInt(myWayPointActionList.get(i).getTrigger().getAssociateParam().getAssociateType()))
//                                        .build())
//                                .build();
//
//                        break;
//                    case "2"://TRAJECTORY
//                        waypointAction0Trigger = new WaypointTrigger.Builder()
//                                .setTriggerType(ActionTypes.ActionTriggerType.TRAJECTORY)
//                                .setTrajectoryParam(new WaypointTrajectoryTriggerParam.Builder()
//                                        .setStartIndex(Integer.parseInt(myWayPointActionList.get(i).getTrigger().getTrajectoryParam().getStartIndex()))
//                                        .setEndIndex(Integer.parseInt(myWayPointActionList.get(i).getTrigger().getTrajectoryParam().getStartIndex()))
//                                        .build())
//                                .build();
//                        break;
//                    case "3"://SIMPLE_INTERVAL
//                        waypointAction0Trigger = new WaypointTrigger.Builder()
//                                .setTriggerType(ActionTypes.ActionTriggerType.SIMPLE_INTERVAL)
//                                .setIntervalTriggerParam(new WaypointIntervalTriggerParam.Builder()
//                                        .setType(ActionTypes.ActionIntervalType.TIME)
//                                        .setStartIndex(Integer.parseInt(myWayPointActionList.get(i).getTrigger().getIntervalTriggerParam().getStartIndex()))
//                                        .setInterval(Integer.parseInt(myWayPointActionList.get(i).getTrigger().getIntervalTriggerParam().getInterval()))
//                                        .build())
//                                .build();
//                        break;
//
//
//                }

//                switch (myWayPointActionList.get(i).getActuator().getActuatorType()) {
//                    case "0"://camera
//                        waypointAction0Actuator = new WaypointActuator.Builder()
//                                .setActuatorType(ActionTypes.ActionActuatorType.CAMERA)
//                                .setCameraActuatorParam(new WaypointCameraActuatorParam.Builder()
//                                        .setCameraOperationType(ActionTypes.CameraOperationType.find(Integer.parseInt(myWayPointActionList.get(i).getActuator().getCameraOperationType().getOperationType())))
//                                        .build())
//                                .build();
//                        break;
//                    case "3"://AIRCRAFT_CONTROL
//                        if (myWayPointActionList.get(i).getActuator().getAircraftControlType().getOperationType().equals("1")) {//悬停
//                            waypointAction0Actuator = new WaypointActuator.Builder()
//                                    .setActuatorType(ActionTypes.ActionActuatorType.AIRCRAFT_CONTROL)
//                                    .setAircraftControlActuatorParam(new WaypointAircraftControlParam.Builder()
//                                            .setAircraftControlType(ActionTypes.AircraftControlType.START_STOP_FLY)
//                                            .setFlyControlParam(new WaypointAircraftControlStartStopFlyParam.Builder()
//                                                    .setStartFly(myWayPointActionList.get(i).getActuator().getAircraftControlType().getStartFly().equals("1") ? true : false)
//                                                    .build())
//                                            .build())
//                                    .build();
//                        } else if (myWayPointActionList.get(i).getActuator().getAircraftControlType().getOperationType().equals("2")) {
//                            waypointAction0Actuator = new WaypointActuator.Builder()
//                                    .setActuatorType(ActionTypes.ActionActuatorType.AIRCRAFT_CONTROL)
//                                    .setAircraftControlActuatorParam(new WaypointAircraftControlParam.Builder()
//                                            .setAircraftControlType(ActionTypes.AircraftControlType.ROTATE_YAW)
//                                            .setRotateYawParam(new WaypointAircraftControlRotateYawParam.Builder()
//                                                    .setYawAngle(Float.parseFloat(myWayPointActionList.get(i).getActuator().getAircraftControlType().getYawAngle()))
//                                                    .setDirection(WaypointV2MissionTypes.WaypointV2TurnMode.find(Integer.parseInt(myWayPointActionList.get(i).getActuator().getAircraftControlType().getDirection())))
////                                            .setRelative()
//                                                    .build())
//                                            .build())
//                                    .build();
//                        }
//                        break;
//
//                    case "1"://GIMBAL
//                        waypointAction0Actuator = new WaypointActuator.Builder()
//                                .setActuatorType(ActionTypes.ActionActuatorType.GIMBAL)
//                                .setGimbalActuatorParam(new WaypointGimbalActuatorParam.Builder()
//                                        .operationType(ActionTypes.GimbalOperationType.find(Integer.parseInt(myWayPointActionList.get(i).getActuator().getGimbalActuatorParam().getOperationType())))
//                                        .rotation(new Rotation.Builder()
//                                                .mode(RotationMode.ABSOLUTE_ANGLE)
//                                                .pitch(Float.parseFloat(myWayPointActionList.get(i).getActuator().getGimbalActuatorParam().getPitch()))
//                                                .roll(0)
//                                                .yaw(Float.parseFloat(myWayPointActionList.get(i).getActuator().getGimbalActuatorParam().getYaw()))
//                                                .time(Integer.parseInt(myWayPointActionList.get(i).getActuator().getGimbalActuatorParam().getTime()))
//                                                .build())
//                                        .build())
//                                .build();
//                        break;
//
//                }


//                int actionId = i++;
//                WaypointV2Action waypointAction0 = new WaypointV2Action.Builder()
//                        .setActionID(actionId)//0会报错sdkbug
//                        .setTrigger(waypointAction0Trigger)
//                        .setActuator(waypointAction0Actuator)
//                        .build();
//                waypointV2ActionList.add(waypointAction0);

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
//        Actuator waypointAction1Actuator = new WaypointActuator.Builder()
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
        Log.d("wayPointsV2", wayPoints);

//        wayPointAction = communication.getPara().get("action");
//        Log.d("wayPointAction",wayPointAction);
        if (!TextUtils.isEmpty(wayPoints)) {
            List<WayPointsV2Bean.WayPointsBean> myWayPointList = gson.fromJson(wayPoints, new TypeToken<List<WayPointsV2Bean.WayPointsBean>>() {
            }.getType());

            waypointV2List.clear();
            for (int i = 0; i < myWayPointList.size(); i++) {
                double[] latLng = MapConvertUtils.getDJILatLng(Double.parseDouble(myWayPointList.get(i).getLatitude()), Double.parseDouble(myWayPointList.get(i).getLongitude()));

                WaypointV2 waypoint0 = new WaypointV2.Builder()
                        //设置经纬度
                        .setCoordinate(new LocationCoordinate2D(latLng[0], latLng[1]))
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
//        if (canStartMission) {

        waypointV2MissionOperator.startMission(new CommonCallbacks.CompletionCallback<DJIWaypointV2Error>() {
            @Override
            public void onResult(DJIWaypointV2Error djiWaypointV2Error) {
                if (djiWaypointV2Error == null) {
                    Toast.makeText(ConnectionActivity.this, djiWaypointV2Error == null ? "Mission is started successfully" : djiWaypointV2Error.getDescription(), Toast.LENGTH_SHORT).show();
                }
                if (djiWaypointV2Error != null) {
                    Log.d("WMUEWMUE", "startWaypointMissionV2报错");
                    communication.setResult("startMission报错:" + djiWaypointV2Error.getDescription());
                    communication.setCode(-1);
                    communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                    NettyClient.getInstance().sendMessage(communication, null);
                } else {
                    Log.d("WMUEWMUE", "startWaypointMissionV2成功");
                    communication.setResult("Success");
                    communication.setCode(200);
                    communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                    NettyClient.getInstance().sendMessage(communication, null);
                }
            }
        });

//            canStartMission=false;
//        } else {
//            Toast.makeText(ConnectionActivity.this, "Wait for mission to be uploaded", Toast.LENGTH_SHORT).show();
//            communication.setResult("startMission报错: Wait for mission to be uploaded");
//            communication.setCode(-1);
//            communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
//            NettyClient.getInstance().sendMessage(communication, null);
//        }
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
        String type = communication.getPara().get(Constant.TYPE);

        if (waypointV2MissionOperator.getCurrentState().equals(WaypointV2MissionState.INTERRUPTED)) {
            waypointV2MissionOperator.recoverMission(InterruptRecoverActionType.find(Integer.parseInt(type)), new CommonCallbacks.CompletionCallback<DJIWaypointV2Error>() {
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

        if (webInitializationBean.getCurrentLens().equals("3")) {//变焦
            camera_up_and_down_by_a(communication);
        } else {//不是变焦要去变
            change_lens(communication);

        }
    }

    private String testStringVoice = "4944330400000000013754585858000000120000036d616a6f725f6272616e64006d7034320054585858000000110000036d696e6f725f76657273696f6e003000545858580000001c000003636f6d70617469626c655f6272616e6473006d70343169736f6d00545045320000000b000003e5bd95e99fb3e69cba00544452430000000600000332303231005449543200000008000003e5bd95e99fb300545353450000000f0000034c61766635382e32392e3130300000000000000000000000fffb54000000000000000000000000000000000000000000000000000000000000000000496e666f0000000f000000f600005d000005070a0c0f111416191b1e212326282b2d303235373a3c40424447494c4e515356585b5d616365686a6d6f727477797c7e828486898b8e909395989a9da0a2a5a7aaacafb1b4b6b9bbbec1c3c6c8cbcdd0d2d5d7dadce0e2e4e7e9eceef1f3f6f8fbfd000000004c61766335382e35340000000000000000000000002404c00000000000005d00734ec9b0fffb1444000fb00000690000000807c1d55500091f000001a400000020158719d000a6040028008c018933a001673c008baa95e66bc03e49f0c11b4c9e7c91e07b8fb4f9e1d69c61f24f9e1d69c61f24e8108888c54b1724495838d152c584c1fffb1444170f10000069000000081e6007c8042301400001a40000002081005d60108c05c6cb3e3975875854b1a2e49582470a1b3d088000e058b542d00655195485aa16a16aa75525552a80b4ea81d51954642a16a81dbfefe8f7f42a240100fffb1444158fd00000690000000821a00788046302400001a40000002016001cc0000000a62a8a624965249a62a8a2124b2920e98aa288496b313924b29245315453d640087c00e87b21ef80ac011d1fe1ef80ec04663fa1ef007403331f987bfffb14441f8f100000690000000821201765046300400001a40000002080005cd8108c01400c55cbf1774de0df85f17fa6f0eecbf17e9770ee8df8bf05dc3b15ea3dc4bd47b897a8f712f9ee55882c8e422927229e42c8e422927229e42c8e42fffb14441c8ff00000690000000818800770046300400001a40000002000001e001000042927229e42c8e422927225d5356312da5a331896d2d5632da5aac65b484125699544b4d2aac5699544b4d2aac134c351295a44ac134d2a2c56986afffffb14442e0ff0000069000000080cc00760000000000001a4000000207a005cc0108c01ffffffd44480224ac1534d1a0b0a36288a92346cd0a362c3648d3a6851b1c9b6090d14142e18787878f00000011bcc3c7ffffd7ecebf66b056fffffffffb1444360f700000690000000810a01720040000000001a40000002077001d94108c0bffd7d5edeaf6f57b7aaa100002c5aa16a3555d55aaab6a3554eaa4aaa5542d54eaa4aaa554aaadabffffdfd1efb28aea040000a62a8a624965249a62fffb14443c8f100000690000000804e01740000000000001a40000002083805d98108c01a8a2124b2920e98aa2884b56453924b29245315453ce5b13d551c9f89e52a393f272953967e1f8824f5bb91eb77675fb3afaeaea3dc4bd47b897a8f7fffb1444478ff0000069000000080ba01740040001400001a40000002020801dc000000012f9ee57ffffffffa6ffffffffd210495a65512d34aab15a65512d34aab04d30d44a56912b04d34a8b15a61dffffffffd555577798d4a16a58c0ab09fffb14445b8ff00000690000000804e01790000000000001a4000000201d805d400000001124b52ef63588a977dac454b50f18787878f00000011bcc3c7ffffd7ecebf66b055ffffffffd65771e652557b8f3292abdc7994955ee3cc965a6018fffb1444730ff0000069000000081de00775046300400001a4000000201d801c800000002c56247478aca6e8e9594c91d2c56237479594c91d2c56277479594d11d2c56183d020a35687948abe7d642522af9f5b2522bbd6ca22b50a2807e029fffb14447e0f700000690000000821201755042300400001a4000000204a805d400000008aa29818342c92698aa288492ca48381024081018342c1838102408101a62a8a77d00137772ae06fa221c08009bbb950e2de2221a041095c5b938716fffb1444820ff0000069000000080a400780040000000001a40000002000001e00100004f10426810425716fb871670200148a4f5bb91eb77675fb3afad019b86220977acfc31292ef9f86252b3f0fc4128e587e941451a0582ae51e50762589fffb14449a8ff0000069000000080cc00760000000000001a40000002014001e400000005ca3dc4b129d2c7940d415f3d06a255c308aa8604308af0901991493964721641391493964721641391493964721641391493d004004c77262897ea4fffb1444af8f700000690000000804e01730000000000001a40000002077001d94108c0bbaa581c737f74bab734737f74bab734715f99b088413e20b2820e8e206092b0c4a509a40d4092b0c0d28495031012130c0d284958621284d206395d9fffb1444bc0f100000690000000804e01740000000000001a40000002064801f20108c04a202446d113cf923c0c11b4c9e3c44f2619b4cbcf113c0819a464b9e207c8a90928ac49582a48d1a0b0a325445491a366851b144d923468a851b144dfffb1444cb0ff0000069000000080ba01740040001400001a40000002020801dc00000009224345050b5100000de1e7d62306003ef9bfc478070c0f3f37f88e199f7e7f74000000000007878787a4000000101e1e1e3f500004261354a2630a3fffb1444df0ff00000690000000804e01790000000000001a4000000204b001d00000000908ba20ebd7bfbb3e9d1b8bffffd5edeaf6ea069000096610023870316fb8000d80f9350f40616f810d0d9713d35134384f80e4060f729b37d09cc22fffb1444f10f700000690000000823447720042300400001a40000002054801c6000000049632404f4aa9c62f21758e4bd5bb57d567d92804fffffffad4bb8f068389ea10e9192663086476f59a36ec97e45081cc5f2a843a6fb23c391d41041fffb1444f28f100000690000000825801768046300400001a400000020a7921b80318d291db07d07ffe000b1a784a79ef2286b6ad3f7400717480a21e4cf8789d48d8a431552101167396e5145dea690f0109c0a788d0938475931f53375ef94fffb1444e88f70000069000000080a400780046300000001a4000000205a005dd4100000ad2d98db889260050f4499e4694450ba276c951059e417557b5a9b3421cffb93577f20e8196118e9dd14b57ae9ace0838541913dee525b3c5c08fbd3fffb1444f60f10000069000000081b000768000000000001a4000000208e831da4118c49a5d86d92dbaeb246c00001089149a644e2e09ce9566222822464ab0e805a9f5c08a39ae429468e38e94a3ce1fafa74ae777295515ff0d57fbffd1cfbfffb1444f48f10000069000000081c000705040000400001a4000000209d029d20118c09540072db2daddd80042c58a02d583e9ba49bed9311367b0bb5131c511f4751eae5fb6d6b8acb0ea63124be950e405408c054164480e7a59c234b1e1efffb1444f08f10000069000000081f600715040000400001a4000000208c805da4118c01984f5d840342901560312aad05e42da3e052f7de48682978cb2075cdfce087ddffd8a6991103d0660368d7a62edd3414b5242149b69152052832f52ffffb1464ed03f0d800b92820000214a01710040000424001010008402020801d40000000476b28850604caa5aa3642638d2dd48b274e349f7a4b5ffc7effce67e7f72fde4c28b140a5908047c39b2c08e1d20dd1f7b3282690bdf3227f6a6655fffb1464e601d0fe0541c8221810092485010023d04250032b940080202a00538a8020009a2ff5aa844911163c9a579b786b63b02c302031468c1d031dbe9da30fad0c7692104020d85060970c199698bfa2ac23f3a9021f091a48c1ece11a9cfffb1464e10001121bcf0624400000000d20c0000001940148bc3000200000348380000497646a3d50042302930e93d1464ab2522c480cc94199c4119c40d6d3be558aac95ddd492a73824120118239038b7a4aee1d7c43b72a0ec377dce76b3fffb1464e783f0750451a8c1108800000d2000000103a41f3f0118c000000034800000044ae46aaaa903a0e36d9318a6535aa811932ade0d1a55a754a0496f7a56409d3f40b4bf52b10f3a1427826f820d93088ab982012c55266446dcb17adefffb1464ef81f0f70b516026087000000d2000000103dc273f032441000000348000000473aaa3880505c0d8761ac2d0482e6021cf3f87101371808db249bf6b5fd6eaa6910527061c280b4c2a6294450433c515b0a2c44e723538f40f1eb67efffb1464ee81f0e509586822300c00000d2000000103c8293a0120c088000034800000049aea8111312b20f8964491d99f259c362266e96f4894d113e6fbbc15d4f60f77226bed7c420695df28ad36de71331db8341cec59df7b6fff7af208d1fffb1464ef01f0ec0ad360a6184800000d2000000103e42343049844c0000034800000049ef7eac9ea7596dc8cfc1100a80f9f86a0a9c8c2a79e1041644a117d2ee3249507582016d629a24a7d29ce07181e34df8a55c61313938d1b307a1f4bfffb1464ee81f0f4094e80e9180200000d2000000103b0274102992000000034800000046cf00b6bd9bbb5b55efadda6aa8447e60989825903577b062ea937bdaf90541773b27b09aa3d5b3cd7f85dadc1324106081c71de809636b121495365fffb1464ee8ff0e4074f0046104200000d2000000103f42d3a030468080000348000000402898c4bcfd8a5cc2b70bdad35846a4204a8539162105a67800c732b02c8d921c174704ac77c8f430dba9fd95bfb5d92b8524980f00abb38264398cafffb1464ee0ff0eb0bcf00a6105000000d20000001039c413c0108600000003480000004ced3b665e119c9fbbff97f38ded7aad54adf240502b407895ad1125bb4c2453c704070d234b505b586c64d54b8ba195db6db6d77f280e204892a41defffb1464ee81f0ed0bcfa826185000000d2000000103b443432304504000003480000004c71af69879c41578ac5526528d7ffb2ab6ddb5b2c8000001102230e3cca331a412eca506c7ef0de44397b7c5e458040c4ae0b151f899b94613aab558fffb1464ee83f0f508d04121320000000d200000010384294301246140000034800000047932e099d704d84c6ac5c0eb4b22afb78e7b16c2ca34aab5ec95a870c384dcd6959b629e2b4687ca0e6b05c48e4c090b63c108eca427c165339ec805fffb1464ef01f0e40e4f8121129000000d2000000103d8254323252000000034800000044dab08b34d5375a59a37feb626ef3795d76dfed741000001146a619307747d9e532786092a24e7b457f8a255d9235184842d724410b940f23a848533fffb1464ef0ff1120cce00e9302200000d200000010380273c0018218800003480000004d3a9ea054461838da1c106fb8ed09b6d75b6c8000001ba80f8041b2985122cd3110d430921b6ff6cc52dd08bf7a593010ab471c1389154ad2a554886fffb1464ed81f1280c5058ec301000000d2000000103c83b3a040cc4c800003480000004e556c3297d2d3cb56d9767eb96dbb5b6c8000001206ad1e2a8d894c75444224af139215ec6b465e9236e40104cc521c34355a6066be8ae42adc294f1fffb1464e98ff1090dce80a9301200000d200000010358153c0108604000003480000004bf8ff89c7982373bdbfe3b9ddda5ee83c0800114b2c0b6c14eca21cd6b84ad658b419808e3990a091f4cd48a22c8059fc9912e156bca40d4a9c9e48cfffb1464e98ff11c18ce0026186200000d20000001037c1b3c02986048000034800000040e0fb22c94c996bc5876e42a7e0ca24a16d6cc7d1e6cd8cc883abc607d652dc3bdb58f163f15351f7e7ab5f20c849051f346afa583dd9ce8c082a2d9fffb1464e781f0f508cfa866301000000d20000001037421618118c0f000003480000004b79e6e1a536f2861671d49e235519452097410a4c1b5c9650ced549ac1834834800ac8b8e394cecf66d4253058d240c4611931d170156823f2035363fffb1464e801f0e508d968e6300e00000d20000001040c273ca124c04000003480000004633e30e486988f240d47af592faf1891c2c5156428a597c0d7d873d74d17d2cb67dee1794fd7d5f933fcadbf6430a1264c400a9c1e8eb5149c616c5cfffb1464e78ff0e50a4f00c618c000000d2000000103b82b3a0294c00800003480000004c298e9f7159fd3a975529a5234d2419c10d01a17978acc09d3c14acd4a3ca88c38070996556e568a7187f5a5555550c6c843643a5868d09da45b6614fffb1464e801f0d508db6846301c00000d2000000103e82b498118c0a0000034800000042636e31206c38dd80369beabb5cd557efea180b1881c924517ae7d9992531c824f4d3b556344843b35ea510808c0244ac8e18024db212ef10e68d73dfffb1464e901f0ec07d9e906309e00000d20000001039025430124c000000034800000048faa5b754fee35ff68f9294851a4db49451307b44007397659856ac0a643c488907e4d671155b732955db6df6b6480422c3a839395338ec490a5135dfffb1464ea01f0e007596826300e00000d200000010414233d0124c04800003480000004b78b227d5fefb9f7e76a3bef90002408c2481a8c01d168b208834150b0055353c4ed76d3867b7946345a69080c2528c2159b7249c30106c5d632189bfffb1464e981f0fa0ad0c046184000000d20000001035c253a01184140000034800000040344c589daa525b31b656a25a8e185d00ee00a46130c710064f1692066dd276495c4742e28f79e0927b90aa3450112068aac1ed8352ec700f71fe667fffb1464ea0ff108164e00c3134200000d20000001036c2b3c00986100000034800000044b6a5bca67b3cbe17d58f3d8dd79ebacae080800010c112592247a520f8a142269cd72ddd8344e17fa838e56ea7d2848259cd36586519836ae106566fffb1464e981f0f30750d8a9300000000d2000000103f0233a01246048000034800000045e9d108d51ecc87baefa9164b251bb2549a1200441258a0b48e83ced3cc220e1453d40526e5a69fab7fc5bda0224687481913ed2a58823960bab1e50fffb1464e881f0d803cf0026300200000d200000010390294103a4c04000003480000004226964d62e375d62c9a35572b8ab8d7400043260d9e4028c3631c2a52862e51b1ec76c98d69d4f92ebe8fe4dc8971a9a706a4a70f5d2211f9cc99df5fffb1464ea81f10b0a50d8ac301000000d2000000103b829430498434000003480000004f29a05ee3790abcf7a25ffbd6d96db2ed13300040d878e8a6c290b759d09544a498409893b34b911696b174f04d00e858a3655809783c0573c6c1429fffb1464e881f0e40ad1c026185000000d20000001038c193e01186008000034800000043981adf506effbffdf3ddf93ea0bc0075cc9e40150d2f5050aa8a92041245ec258ac64ba1aed4c933a902f0248f3c922c7c95945a1106ad7380e83cbfffb1464ea01f0e90851a046300800000d2000000103841d6ba119203800003480000004f8a7d5e21a66ab7ddb528dcffd8a7f800745088428206a159825e9d71c9c182a8a4431e4452a802ceb12cd21a4a492364842823960706969906532d1fffb1464eb03f0f60850c026300000000d2000000103b01f4102a44100000034800000044d440d29279bb4c75fff6d8625340f0fa44a2d94c9098d3f75621d81d78afc97f80e4efc70b13a03bc99dcf105c9243118517138730799129cca9055fffb1464ea81f1061050d8c11b0000000d20000001039c1f3e018c624800003480000004c38b86a251a6bdff4bd683ee840081203cd130609da688d0652a854b4da72c594c1c95c754cae471f4b38b2251322b6834503181ca8afe9c6100aea1fffb1464e981f0e207d3e846183800000d2000000103ec3b476024610000003480000004fd09c4e21ee7d96f76b9667f5584d34d1479654af5f41cd501e4c08dfb8e8b6c6f06d263923c580d3c294105479a41585dcf5160efcb5c1c91984ae6fffb1464e981f0ee07d26026300000000d20000001037c1940a118c0000000348000000461c8dfc923253baccd96a040fe2a77ff51a10022d0960306c1f4909593ed0530f9b232a3ed30d92b56c6fe85c052e0da2254978915fee4631e1502b0fffb1464ea81f1010a526148300200000d2000000103b41b3c00a4010800003480000004639a55f16d4725d551239d75f6fdbed6c920000120d618b4603a3527d83c44a1f4f29ce2f608355b1fea77051e99238c731846db5612a3d94df6561afffb1464e981f0f50a536049301800000d2000000103ac193c0324624800003480000004af72fcb9899fd49276de9f5f7daabf7ffefb0a000001403071225404308560e160a9c78077f62996af18c76c4a7e6254dc510911a44e5694498681f6fffb1464e983f0ed08cfa926112000000d2000000103e8333803046c080000348000000487565c02da1aae97127a661659e6aa5aaa232083485257483106412e98880f296a6b08dec57bebad488042008693b63a317bac7612779063851969acfffb1464e901f0ee0b4f28621a8000000d20000001035c2349828cc020000034800000042903c2e89a773a720fbfbf622ad6edbfd68a20000129b2176143044a20694204c347c8d4353c2a4cd10faefe0f4036f232d336b6a54fd0028eef73d3fffb1464ea81f10b164e0049181200000d2000000103301d4760a4c020000034800000041a1e15268b58f36164faa994a14ab13949dbbf31213bf604883b407d89c8e169d87e4e5f91a6f39f19733ff4932ca4240a6550ca42dcac2966069668fffb1464eb03f0fe0a4fc048480000000d2000000103d41d3a0308480800003480000004c22a121ef726b5eeab8ad5c4c759503c10524e265741e160c3cfda714f1a271c9aa68b72a21c6a38216ecfdae4765b6d926010e14027491120274814fffb1464e98ff10112ce014c183000000d2000000103801b3c002060c800003480000004841a7c36e0f3d8a32734d77ff7b6cb030001010096101a4f2040c4709381d3e82ee3d969054f46ffd90311e285af0c8e7261a2239016c718b443360cfffb1464e901f0f109d14826086000000d200000010368293c02a46080000034800000049c46c9edc45fb0d41deec97f24a2b5b75d75b6c80000010676190b16487991d1230322d0dc70679acff7eb8f759b6b247031080500089b9fcab1c469fffb1464ea01f0f00a5a692331bc00000d2000000103e4433803046888000034800000041a07de396a4f719dacfa5557880738171590232845d2168071b64c267a335ef583167e88ec6514ead1b6a201f0ad328712391d546d24a453ace54f73fffb1464e901f0c6085be823180c00000d2000000104702b3f0125200000003480000004383de37ebbfffd2e997020a8cd9a1cf2d1c9489bc996a083d7b1c61a500021d9511ce7c4e7e7dd953511bb39b748325040689e8991a5ad6aebb61050fffb1464e881f0d00850c046005000000d2000000103e8373a0118614800003480000004c30503d8a0f801bbb3ec4a9feaa0c320408022042229d41f9a2097caaa8d5c9a8085a863d6165c679f7866da158cc0d06c4c2478f48e8486acc2c92cfffb1464e981f0f4075ae9211a0c00000d200000010370493c04844cc000003480000004d5d868e61d49e57cd37b3827fcddeb98fab41a0f000596119aee60ee3c530e38a8657366539277ebea506585101094c7134ed8814a540500064c42aafffb1464ea83f1070d4e01801b0200000d2000000103481d410298c000000034800000043c6135dae8abe967acbd6aaa402086908942ad07124f9c38f25da9383e433a2e843ba2465ccb8c7fff7fd677ab8a0850f42eca210ec6715dab7d642afffb1464ea81f1100ace0126192200000d20000001031c215d8124c03800003480000004283a25638d3355c926f5a928844748885464ad240a255d542dfadfb32e888995ebf332a5f8baab88e1674fce0ce3041c6019d07146eb6e1e07828433fffb1464eb01f0f7075a68a6300c00000d2000000104305d360118630800003480000004a7082cfb96ba72dc8ceb03e987888888877db6000020240b391881106981ad1a8da874100ea45c66708633bf9780b28e025958a42b0ce0e8a2dcba57fffb1464e881f0e707d9e849309e00000d20000001034c1f63a130c0300000348000000445afc5aa884cbf76245b6ffe36edaab108b9600b40036ea4c01e65f43357b988bdd9ffce6e20a338d6febe412a173814c8525328177082ef960dd16afffb1464ea81f10b124ea92311c200000d20000001030c1b3e009860880000348000000412e067d291fd9517071bfdb37de5eafee2e0b1e7132ca34f3420cd044a83a1f3c7ceb6b6fb92e2156b9851a59c1e017652ba363b5a519e7fd741f3befffb1464eb81f110094e0049300200000d2000000103c0214100992000000034800000046466cdfb2647772e999dc9a78fc140249f6fdf074e4f4f67c58376031338ab52b46d62975a243c51552c85f706882280c07c9d76f5e85774032a6af4fffb1464e981f0e50ece8063180000000d2000000104a8293d0124c08800003480000004edacd990f4dbdd0b2876d856f9a654630d0c61e68a9d7688989988000000dd012860555861445314a779c98842400e0b08576d366ed3694d8b0176c0fffb1464e681f0b007d0a828102000000d2000000103941d410118c000000034800000047058e8abc82c9536d8c5d3f62a2fa00303850f2241db52cb24a4a2af830b79142c9b4e98a258cb08548b68d2a8d50e281ae806e64b30d34f1eda92c4fffb1464eb01f11709cfc0a9301200000d2000000103643f3c031862000000348000000486d42a31cb1b623f3330deea821a32262235d41f5bcc3ee38b569a4227faee75f833fec7f7efdeb975a9b36230088de286f5846e912619758c2113fbfffb1464ea03f0f11ace8029182000000d2000000103bc2f3ca3046e00000034800000042a5ad6d61a076b6da6b53cfe9f25875020206e10481842182c416a18098b4a4eee3b521773cba91549c15004483985015587e63c0f0b6a1c62f6dafdfffb1464ea01f1080b5df846185c00000d2000000103a4213a039860c8000034800000041ae431d2acda8d5a473bfdef6154c92c49c01020b19048293622b2934bbaeb7730dc4b73f89e81674a9186132579a470697b64e23374d8f5f78b48b6fffb1464e88ff0e1044f0123300200000d2000000103d01d3a01a0c008000034800000048d69c31fffb733f3dfaa6a894780853d606916696684857b1c5a8db1e8609c8b5f45e3e753b9b5cb6d9646ff085151938885c1b3d461b078f347875bfffb1464e90000e506514518000000000d20a00001054c894018830008000034830000009a8925af882a7fa00487a90c38d744b58c245868302136147d61154ea2670eb18c3612d6379c0b128e4c3481b8e344ae65177d496e44a29d4fafd37efffb1464e38ff0a202d08708000000000d20e0000104907b3800a461080000348000000495bb19c67624a6e19577c005850c04c07043320ca2e2c226de70dcd71cef4ed6ad9af983368629e8a08b16430409450d000a35949cae2c5da96d9935fffb1464e501f0a105e0f063189e00000d2000000103c0233ca3a52000000034800000043c894a155237246d5c20050e00c0055492883c24e2c58366d20846631e442c1ee6effdccf7b7218a8a3172a2a3b6c9ad848d8a2e7cd9b6515a6769c8fffb1464e981f0f70acf28c6400000000d200000010374214101a4c040000034800000049fae96184085e7843e307c704c8c073a31a31c68258fdc68ebfde9934f781c7addf681eab2fd490418611a330ce0e5024a76adfb8fb189b933902c9cfffb1464ea0ff0ea084e80a9402200000d20000001040c2b3804846848000034800000042153f7a1ffbb4ee4740feb3803c8b02511274d08f64027da55ba99e142c4a77b5fdc8c7d02c1525110ceeeeeed6d02a4006cf9d1705ce5408b60c34dfffb1464e90ff0d904cf0043101000000d2000000103f44b3a02a46088000034800000043057762b6ab7317595b9652d78109732511c0d1f18c1659c513529daa08072c186e95d4cb122f295a7a624a49113285922a2cceb0c72c853458b2028fffb1464e981f0bf09525846300a00000d2000000104101d3aa484c60800003480000004798e8b48a86157e2cb55897df30b88c2223e86b080c7481aa519bd9e4bb16fcd53ff9ffda0e5b5a8dcc6a03b288440903c54103a5164cb0404055c18fffb1464eb01f0e909504046103000000d200000010344215d82a4c030000034800000041630898822f3b6efd75f3a95810d83859098c4d795cc91453b9f14fbdc342607bd6f4358c45ca9d22983ecd90830265b1231d2a2149946eb82fcb9defffb1464ed03f0f607cf2826104000000d2000000103f47338048c4b40000034800000044f843bfbdf7ffa0dcfcfc426a77ba81f8004153a4c2354fcd1b681b9da1149c51c74f288935d6a6bf44ee2c809ced730fa06630246181060d9d26644fffb1464eb83f0be08502822182000000d2000000103d0233f00a441400000348000000472207b481b44f0abe721bb6e069898f151febdf9afa11365d5ff80028508c4490003c0040643c871a734c1dc4d1746bfa3d565b6edae80310a190189fffb1464ee01f10007d1e029300000000d2000000103e08338030470c80000348000000431babb1a8cec0417152f77fea56ac9b49258a00450190472c918a09078000b3dad70de9433f361e1522e6bb4a6dcb6d90d46080c4c99a4a8b4915299fffb1464ec8ff0ff0ace8049182200000d200000010460473e008cc0080000348000000474636958876eab7e7428f535356778766b688000349e1d8a88605d31e1059c416aa182e35066d6b214d35edfdb55295a846756449f5b46c189b9c33efffb1464e901f0cb0fcf8026102000000d2000000103802371e7b0607000003480000004c45f2ccb13316038e4fa6f43cf74d01081b35812dd78aa66f9fd0206edf30704e0713070b8b8e3be77d3699b89d56236457c34749b82dc809cf75251fffb1464ec01f10e0bd15949301800000d200000010408254584844a00000034800000042bac5146b5b387411b1caa7f0a05d8de45358ebfc1a75d41b0c61865c8c836d395d51a988543af62ef3f72d1df91f203e070996950859c8a02812e08fffb1464e901f0e806ce8069309200000d2000000103901d40a398c000000034800000046085e755e8321c343006f12ffffd0ab0180dac29f9b4a1b370f2ad10368b86065116483c38ec60b3d875bdbff25001c009f1c4ce221b0fa31042d786fffb1464ea0ff104124e00ac182200000d2000000103d01f3c02b1200800003480000004ae373441e91a8b8e63c2bffd7b6a5e47025d1aa00e442202e57317df68f9c53ac4bd661c8698fff8f9c1fe1f576d441522546cae0502a591089cc62bfffb1464e881f0e8095028e9183000000d200000010508293d06b1200800003480000004bdd19a3cf0cf2655c9f4518f407a3e53c0d94353af90c324941223153ed4004ddc11135b50f9adff6274a184400612a2455228ee1c8c82d091e50368fffb1464e381f0c703d0a8a6101000000d2000000102d4236fa10c603000003480000004d5762fa691cd21cf57ab6f1920e802607e854b51660f1c649215a29a1a55b23065a7924ab9bc7a35508a018c1245c905d310a41437050162daabdce1fffb1464e981f0f906546046103000000d20000001034c2363831861700000348000000477305c10ac047cc3b71dea77552a5f1eb0609be4ec0841f90b2a547c924e8a64c72df7ce4da5121700fa404370512708c2a95cab6556aaa2ec326ba4fffb1464ea81f115095cf98630ac00000d2000000103a833460c30648000003480000004c9b85ef69b674db46f754808082844e34a9512b84892eb4c102d676865d5919cbe25cc06f533640ffa54c1a60e2348a07a188902b0cc132ea35cc1b0fffb1464e88ff0d706520178482000000d2000000104703f460c24690000003480000004a0af788c98b7b24d5dafd049881026c4fdd27132a30d7690d40a39e0c2aa112113d4fa696e8f6254a080020182ae4345a9181092fa0d19407113e0d1fffb1464e70ff0d70f52830f30b000000d2000000103b0274a07bd80800000348000000453f14d528a16f6d3d54d7a14c412350c3886e9c843c8d079151fac79d661b9831c6061b0792d99b294507580fa1eb364a14d97b354871d6932404903fffb1464e88ff0f30ad18198303000000d20000001037c274607b122800000348000000400e3e87b0692b1aca6a4dc17a8aa9b6d8a22a3078b0b64e400f02ce38e196107d889f9d75eba7ed8b71e9113c1466308c079192b4b2155786ab49b8cfffb1464e90ff0d408d101e3608000000d200000010428414007a4688000003480000004dea51540fe7071e654322aa55554677788787c28000038013d5e13802ad494043449291147120bbbfa06801888848ec0741e1e3ed461748e9b175157fffb1464e90ff0e60a5081e3128000000d2000000103a4274207ad280000003480000004b12cb140602c56320fe394901a148eae587da228f12d1566669385bbf036fd6fe8ffddb13a5f2aaef8bb52d524038b3080f5a62552902e64021b6cbdfffb1464ea01f0e7085129e6789000000d200000010410254007b122800000348000000484452a7b8d9adec48a2e5e00ca27208113905e36e3eeec82907a8879d0f344f1f900b5ab9727a94fff1bce08560386e8524858d2b320f81b93a40e12fffb1464e90ff0be095101e948a000000d2000000103fc294007bc6140000034800000049126d87c65eb6e8824591eaf0392ef008075214ddd498cf7f502869f6d55411baabaa5574e35dd3e3f762f3dd98d44ecbf0ee475061890d097b48d4bfffb1464eb0ff0fa115001e6128000000d2000000103b41f4207b1228000003480000004d8a004489b49606aa5eab6de8550493e4bb1df4b6a158102410244e625594724e9aeb3d8aaf253e9ffff44c8dac4886e00e24de150c725c7666ff388fffb1464ea8ff0e2085081e9021000000d2000000103cc234207bd2280000034800000042d2524104bee172fceab3fdef7f5ab3f9ab3fae15c8e882e95a8c84d8d3149c15bc5927e6fbb8b55ffff400539afd2cadd0001acc03625090942c701fffb1464eb0ff0f40950030f60a000000d2000000104241f4007bcc10000003480000004520541458c5045b6ac1332db26254d23d1e7aa084fe580002408f18d1ada244830ee64c846d8f4daf7920e1948509a9c66988d436c20150a518ef020fffb1464e90ff0df07d0830c10a000000d2000000103c8234007b0c280000034800000049c710a5a6c5dac477d5afb9c2da135ed25c386976e90001813c200a1b16f51c2354b2063a38bc43db7d95235bb76b6481b861d658028524e8d54429cfffb1464e981f0e207ddf8c3198c00000d2000000103b4253c048864000000348000000434c2a52487d99c5a9a53fad58202985c8bfc1004d911902d692708ca144239372bf6e2ffa60231e110111b26aa8d14c7fb3115d02c8087eefd3e1b2ffffb1464ea81f0f506cf0129309200000d20000001038417432318c04000003480000004f61cfe18f6ad7e2beed2001a58204c85504f2b071511120e92dd0c09050346e791fffebe89406b42a38904304c0a85592d40005c1740743a8ed9be65fffb1464eb0001090b4e0524600200000d20a00001055c953a18918000000034830000004ca1aa4ad534557798767d680000182ce654858ac59d632869a6a12133a0fcf27d6cf452891cc8283a2954339a0f1301a075c89330e180aefaddaf14fffb1464e30001101955063c400000000d20c000000288214abc90000000003483800004aeb18a4debbfdad9400000c12070bc221445e7a6b390145250ce645de8b3366121f02aff76070790bc7f135881c1e932d62c2ad60e116c3a201ded21fffb1464e581f0ad08512969085000000d2000000104cc1f3c0c60c108000034800000047ffbea7d40f01b84ec5c7a641d0a8bb9c3dc936e696d02c296a06552c86ab720600a4bad8f16437133454508824e8ba529cb1c020f954cfd46acb47ffffb1464e581f0ad0751030f488000000d2000000104741b55a4b1202000003480000004669a6ca6ccce9250282ba6303c693a287a0ab498b8e67215ff90e4107fc731d5d3d381f50d7b0813cc54017481a682dd24d41e7bd00ea90f39405a72fffb1464e681f0c10652c9e3309800000d2000000103b81f40a7a4c200000034800000040d5da3ccfb2ab43e325acac6f95367ddb0a52aa08bc249b0e04d16356de52433e79a08ad2a2221281c06f01690740c23ac50f26220b091683c1bd270fffb1464e981f0ed09cf830c112000000d2000000103942369a318c03000003480000004c23d8d7fd56ec67795553ac52382091a52460c62e268871f100d08991c86fbb54ead5f6dafd8fd3e20d730252008c503c0a2895029255ecf4e677d77fffb1464ea0ff0c606d0030908b000000d20000001041c233c0cbc628800003480000004a9fb776d4efdd5d9ff6a152f5bbfc2db280001127581f2449ecd62876188a70e6d95af0f74e83ce620027ede5a2b69528e1f3683a728094aa16e16affffb1464eb01f0d40651c1e9309000000d20000001038c19400c24c240000034800000040451cba9b3c9bfe4574a2f0003a27822282de171704ed4924686e23338ad4d227392cdfa07567d31716e757f3bedebff74ba0518a73d086114450c88fffb1464ed81f0e3085e78c2311c00000d2000000103a0173e0c24c240000034800000049c2948c66f91726afefcc50e4b2db25e40069908e3140e1a30d2ccd984f2561d4394d99d690bbd342073de5d96fed160efffaa0a0bd16c84d1c80881fffb1464ee81f0f40c5be8c11b1c00000d2000000103981b49292cc040000034800000041118116891208161f16e0eda952a1fc004a835e304a61e0e406cd5945d618050a1f5b83b3a42e1ef21507346db9e2ad5baed759200a8002924681138fffb1464ee81f0ed03d0030f600000000d20000001039c414fa7984320000034800000043ef291a1e0e07dcb38842f90eb377a861a87269a1a6cf918c234b058260161b32b50716932618e3f45d9c7f62c9ed189829496ed762b3956c5177b95fffb1464ef01f12918ce8329192000000d2000000103441344ac3cc00000003480000004fe87cd83236b79f6e4bedfd3d4131b9372f621887d750dbc4902c37dcf46939e2e8246ecb744f3c5d0dff75bba5df94ce0e09314606a9830c723538cfffb1464ed01f0df09d0032c18a000000d2000000103e01d430c2cc00000003480000004478ae4b6df7dc6c1db3636284606b802640d60f83c1d5e131c1ef8a3799bb49387b05ec6b961d668aea5a2b4444cc446d8084a3306288d08989d0f82fffb1464ed8ff11105cf8318301200000d2000000103e419400c24624800003480000004c944a0305cba56f60aba6ba2005a93a095073c0e08c88b16e1084a6205433599c507d2f6da3d0aeeef1768bc23d22212334003c9112636e8eb860405fffb1464ea81f0ee095be849184c00000d20000001039413470c3d20280000348000000442a352b808d8a30cbae888eabdfd7582014d48f770984a2a06215b0b587cb34e356227111968ceda909d3accb77daef4503bd0ac3ed27501771264b4fffb1464eb0bf1320a4fab0c309200000d2000000102cc1546a7bc600000003480000004fe9c0668c9e71837e3ad0ba401da01000c88e29e222ec3b05c139196443c07c705ae9c59b5c2e150c249fdf5a52402482c228bb655a44b03ab165a33fffb1464ea81f1240d576189313c00000d200000010334114f27acc02000003480000004f8e513baeb1af6ff429ca71731085e3383a1f0eb2785693306422b6a4151a2a2b5905356d145268bfcb4443a8bf1c740230e03b0c4c85c66ce160c1afffb1464e901f10708502b0f300000000d200000010360236ba49cc630000034800000040291b5aeb74e8529256646d5490af87f45462c059e9b1bb1541f0b9f3b0085733733cc7b3d8de04b02ccdd0a30ae66575e16632dd8704a96341b30b6fffb1464e901f0f606cf8309308000000d2000000103841d42b581800000003482800004d4a65172a85fad948d113a1c390f8085c804c0c6b34cbb83521175cb14c517e8adaff599c3946b9212d87e66649c5c25a58d16ac2a067a006323d828fffb1464e98001452453863ca00200000d20c0000002641f481cf0000000003483800004bf14e80da295008006634642986417279c0ec743f9249b4f0c5f685eb32a0e2e6be4beced41ea38020c59076250eae95ccecca84b3486aa0304a9167fffb1464e981f0e909d0a9ec482000000d2000000103b4257de3a4417000003480000004877dada96c5c516c0f203493a1f25f82ad0ca6607a4f1e2eb506664109fa0e7ef6b671f09287d2e8878ce2fee1316519523bc37c22bcbaabc6906187fffb1464ea01f0f50650c30c300000000d2000000103f81f40ac1842c000003480000004d69b5ae8e39ad55d87b4d42c9a7800e111cd0a962ca583e9911c3d88ac2322f0b5b629f584d320b914803505921a9cb411ab46cf2e9d081e3526463efffb1464e881f0e2074f8309308000000d200000010358316da49864f0000034800000047e3c0203f46de81fab1f204842ca1444044948faf292250d3f920815055400c56ea775a9b76a6c2e71796971db8b195915e3e0d9b71b1a1d125436d1fffb1464eb01f1160b4fa9e6199000000d200000010324154007b1200000003480000004670ebef4cbbd85118fad981c0b20a1ea0bf642e05f6f8cedae89215a3e79eca7da9bcdb3f1a0d184814941f00d16fe63133f653890da9661e7844275fffb1464eb0ff0fd084f830900c000000d2000000103b023400c30c280000034800000049e0e1d6ff195feebe76d8c7a180a0d201f250211984364d85997ab9af860fc9f8d2a03200ff2d2e1409c9d09b2c684070b7012162a180d26aae307cafffb1464ea0ff0cb075081ef481000000d2000000103ac234007b1028000003480000004c94f0f93ae75fe6ec258364779ab7cee3c3267384c36ab8929ba7c00488f0740b21900418414369898b9d0f5fb9a36617a82ac657da686b2a815cadcfffb1464ec8ff0e109d0030918c000000d2000000103cc233e07b0c2c000003480000004ee0b5955429f6861905aa7d760cd3ae8dbf3f4554e4bbfdb40584a060a861120ea104b7e7f00004b235f4ec20c22e8d2b1ad4a949100d234caa28745fffb1464ed0bf0f909502b0c30a000000d2000000103b4194007bd80000000348000000492dfeb1570e9abb6edc4f7f9993afefd31c5bc521061dc643581163695eccc81e850fda6d02b1cb67fddd8c9a079d7222d10e57411587e2f8cd86730fffb1464ec83f0f207d001e9311000000d2000000103a02740a7a4628000003480000004c27082c249849eb6238c4f7d0aa180408e1db5715716015836327871a53983f5285e96696bce32ea5b4aca926dadb008214e446780b1359b2ab3a93bfffb1464ec83f0e9054f01e6102000000d2000000103dc2d3f07acc000000034800000047b8b1818a056d77ffb15006fba50002e1060c746841d020049944e4888c7820385a5cd1bf8eedf6bd5788c2203b2613ce0a82d56844698e261c9f42bfffb1464ec81f0fa0d504126193000000d2000000103d81f3c14c00000000034828000041441c65232b7745a006a4120ac2471f0498e36380a98cc865921c26aa6ee85bccff5f7570496e042abc3f9e0d830ca73a68d52f6b035182b677ae7d4fffb1464eb8001662552063ce00000000d20c00000023c0d4a1c900000000034838000046dddb20c6d8f05868aec44af7a0fb2d27a8286d23893c5571ea82ae997778f3c2ba4c1b3e0fe00246f99685060e3e0e8aac2436d46ffcb57279abdd6fffb1464ea03f0e416cf0120139000000d2000000105a07d36a7a465c800003480000004e7eb5a75fd27555281a06064020d8e1638dbd2487160a969d6180f51fdb2bd573ec0788490e11922da333321a40f10c74f7a5e87a83b2120492cb5d3fffb1464e30ff0ee074f81e9310000000d2000000102ec17420c3cc2400000348000000498a2065f6a156a6badbac9000001a1508577a280a48b05c2a80787ae9701df45b8b4d47b7ffffb514441006b4a0e50f5e2d38d32170928e85e2aa359fffb1464e601f08a055ca043181e00000d2000000105082949879846e8000034800000043537e8da7a90f45ccc0458f3a92364aebe6c4751d71b32127a080ea87ee5b6392739897874c00b4ef28a07de1de12643b6b0070cd70798269ef3ba62fffb1464e70ff0cc05d00318780000000d2000000103c8213e0c3cc2800000348000000495ddf27a97c7b9d5466bfed6b8020001022b46da6148735d28b2e22ca0e3c0c285f634f5fffa8fd11b835419c95088d0a7e91673d69c0d8d2e82461efffb1464e901f0e2054f830f489000000d2000000103602769a1192070000034800000048a5652e6814b7a7a158e854c47a5c201afa64dea0642aca86b4aa9d5b7429548b76f67fb7aeb5eb06e029efba8918581285a874c2e89dd226c70684ffffb1464eb01f0f706d249ec309800000d20000001036015400c6180000000348000000406340e79162fabfd3b5506a644e07c16625d949315a5a42c916321e55ac26080153b5a2665cf67d23348bc440c40f2bac24e518ac37418f258f6c82dfffb1464ec01f0df07d1430f401000000d2000000103981b400c3d220000003480000004275769ad99bd77fff8ffba0fbe0a225b378d79200501445586499a120ea7e63d1e895235274133c26327bfd594a2c070cd4cd81ae86c8f052a2c541bfffb1464ed8ff0f40550030f588000000d2000000103d8193e08bd2048000034800000045087635a06a1db16b5c5e91c9beaac20087d1dd1eb5ac26a7200151063c9c01aa733fe31fec7dbfecea79a9b6992db6dadb240250258b052a3164cb4fffb1464ec81f0d405d001f8480000000d2000000103f8273ea7b0c240000034800000044984b06d987d2e4ede205d360102d15069e7832a9a653491ae988c021e30546b4db4ed683f17cbb09c5d749cbc822b2c18845589f4a48c38121e0819fffb1464ed01f0e5055a6989301c00000d2000000103a82173a49920700000348000000442a0420406ba452c3ca228dbfd3d4a0aa996a07084499c03cbd2b2a95a78fc763860412d9aa9fa16469bd3c56ab64083d7090e910da76bd83c747ce7fffb1464ee0ff0f40dcf830618b000000d2000000104141d3c0c24c24800003480000004b2c8977eeb6b3d57d7ae90e2dd55c15863451567bb3d3bad22231c160547962018d0e1f3d5c19639ce58cbe5e6dc00455607206d1330898407594d77fffb1464ec01f0f20a5ae8a6301c00000d2000000103c02140a7a4c2000000348000000465ac79f809ab0b96a5a2ee87b251aaea608aeb30c13cc3745e6aa97af291b3ff8b63e7caf03b52b4d0a74bc720178c3934d32056b2db7b8e375c9828fffb1464eb81f0ff24cf830610c000000d20000001038c1b53a618c0200000348000000438405a47c5ba80e6b87b66498358ec84d280048e4176f8a2e9e87eada84f1f80b5160e4d08567d11d2d3d198af9e61da5bddde19416d760c0609a1c1fffb1464eb01f105075041f8300000000d2000000103d8193c0c60c008000034800000048520cc66f7858b43f01eeaecc8b7d09e858400af18501b03274d41f0380870e7b31f68ad5dddfd42d4318401a4240c902b101908bc4b94bcabaee99cfffb1464e901f0fe0953612f300800000d200000010370173e04e10000000034800000041d05d092021e5129a6b6d292ea87059a701c390e189920298f8496c8e81046345e7de611a97e4b47a0138c03c4c347053a487577293940e8a15b0361fffb1464e901f0fa07ce8309189200000d20000001032c2167a398c2b000003480000004258c8f108a8472b435738a45e8ba870032cc1f48db5f1b222c5c85082185882330f2eebd256619d9983830a86e2bc6321086181f8845c218cacf4044fffb1464ea81f11506cf2998480000000d2000000103981d4107bcc000000034800000047588b3e9c0a3fe25d2a92b1316040410bf5d0039d6010b20b489d4c2853e6826e01b268b8697010c63b6efb2c042e3e1cdfb43958261397867516874fffb1464e881f0ea0950c058480000000d2000000103a4793c0c18430000003480000004b243a871d8876ed6eccdc00e2c704e3a5b5e08984250de1873f72dc6f51716a4ca637dda68e935fbfba83fb45508dc750f703202f4b8bbc072091570fffb1464e903f0ea07cf018f580000000d2000000103f83b3ca79842c000003480000004f17a94445f5b2bc7b6aa15e02e0da8f9c252143db97a503a1c0e0c3462a1e9342d1a51969bc769556a08e370e002538158a0623b22f03ea0f74b09fefffb1464e88ff0e110cf01e618b000000d200000010394493a0898438000003480000004d9972b1d87dd5aed6b5b9ab6d53b00043a83a14a605269e59340f11747284c1036dbb8bf5bdcd4a524c3b528b09fada20a356981764a495a20d874b1fffb1464ea01f1560ad0e1ec30ca00000d2000000102b81540013c40800000348000000460545764d234a35c72b6bd8ab96651ab0002102a69a78959964191943030c014ab90eae8478b558488dc39879e84c9b2f3c4df98b2135895680ce25efffb1464e781f0b3034f80af100000000d200000010420373ea0bcc00000003480000004b0c19111d042101d4dce08031d0a80b0cb2d1a456ca69a4fac33164b5d84d6d622d1ca56917ae5d60452813a747e5c788341201888eda40110e86802fffb1464e98ff0d50acf804f300000000d200000010444373a07a4c2c0000034800000041607901740812a332d2c8fbd758462c0a92d61316201e7185e581172120136780ce6ce9d382584595e98fb36b35b678102230741079a1568461546d2fffb1464e905f0d80ad0004c184000000d2000000103dc4f3ea3bc6000000034800000046725ed126dbf5a55a03089869c84e481cb248a04b74b86d78dca4431aa1ffe023d7dcfd8d6b44a3fff757211891c9987a42910d42ca1889b59b28808fffb1464ea05f0fe09cfa84f300000000d2000000103282540a3bcc04000003480000004512c4df97ee7bde0eeff91963f99f9aa810112d54962cf34506e31b371723a738e2a96ab3ae91b3de98e0d4392212adaca9d6ee72aecf08223094278fffb1464eb83f1070a4e830918b200000d20000001038c253ea79862c00000348000000493d6e0005572c445364bf78ee2bacd78cf92df36aa850999152e2c850699ee21806341ff52ffd9c28036e40d4a8214279aeb982e408545567d9e1a4afffb1464ea8ff0eb084f018d300000000d2000000103fc193a083cc008000034800000046ddd2b6842c9cf387fe5215e5584626b2c298992049b8353a6013289adbe7cad236edf5577777dd19a379bfc20c8888898d232914329f639f5ee353ffffb1464e983f0d1064fa9af300000000d2000000103f4253f07a4c240000034800000044b42c088b869e3626af6b15399558080c4c9f5909f104ec96b6f69dce027277193b0cadc767142eb2ff7e3e19279628ab4c30c3570dbe9341ac69217fffb1464ea81f0df0952e026184800000d2000000104503d3807a44340000034800000044b0f5aa8a6f8f5151ed42aaabc0f69725b807960b3db4267b438c0ca89c8ad3dd3260663fd253ab774092fda4e918054520045da06388044401653c7fffb1464e901f0cf0a4f00e9184000000d2000000104101f3f0798c240000034800000041c18c52872da37d060567b5a688100d88b6ac85028737cfa87aa29f228f26c0c88ed04a6c499318b7f9fb882294b6541c413d1844d292c7dd4c2e1d5fffb1464e981f0ec08cf00e9183000000d2000000103182d6383a4c030000034800000048001479eaceb87b726c0dfb1c8494387a0eaf23a80334f959300a4a358bad685e14abd2928d143c422f4d2f7591b72a195c8b5dd4033a58f0e925504fffb1464ec0ff1160d4e00cf301200000d20000001040c353a039860c8000034800000044cd33f80e69827d3386535cedb76d6c820000120c893f461fd0482618d2a6c86069aab49d1a834a9d1bffa3ab8033851276508c630cba9a3674f1393fffb1464e803f0c810d000a8184000000d2000000104e83539079848c80000348000000448aa5af799f54c97630c5da85981a0091b476a5d8793109c238e5206646058f9e0cb04bdc924ee7460a49a175628b03bcab093461418cf70f2b7530dfffb1464e581f09b0a5000e6103000000d2000000103f8454782a461680000348000000438a44c049ad6feef6b9120e7e8230d91ce4c6c611b114a3031413880ea541e2c50c529730e32a3ab3e67690848147148c13d80a64258358c0673c821fffb1464e98ff0fa0a4e80eb300200000d2000000103a8313c0524c000000034800000047dfef99f5297b7fdbfbbd49f858842050914b0494451220d18cabe3767295b862beff7984fb663f6e6e0babd1c4623880331050d679912e41479b64cfffb1464e90ff0ea0a4f00ec304000000d20000001035c293e0518c04000003480000004f86072449c8bbfb9d52ae5a1a55c8fe85c52185a3962246c24d03581fff87dea1ae7bfaffdaa236cdf50fdb05b402128c109024a2889cf69ca206328fffb1464ea8ff1180b4e81e918b200000d200000010360153e0598604000003480000004b5cb61c69a5d01bb09fa5a8a5f00041e0363d228a0ca3b20b7299112e1e8c1575ae0a80b63659ac17f24a0704750b24123608ebcf9b377de700f1e6efffb1464e98ff0ee0ccf004c301000000d2000000103c8293c00bcc00000003480000004c8e96bf1f37e1af394d6e43fc4049b0a44fc1c83095081c45334ce6ae1eb75fb5bfb7d4fed7fa15f7d9d9d63b5d919028421a94035a8d7a46905a6d3fffb1464e981f1070cd041e610d000000d2000000103103540a7986300000034800000041abce30e81ebc3a6d3f7b5b0a0455870fa02db842a29d1c4597ee8b3b35be4337bb9ee39dae29cb5fe1afe462a8533568199030caaea2f5dc80aef4bfffb1464ea81f1020b5a6926413c00000d2000000103b0293e07a4c28000003480000004d2e8e2c040a214b382c8a100c8f5f6aa69aaa50071c0282cc5526bb8a2851d6a71d77bee9f5fbb081b212a860b360b49d28d7a37370ab9ea174cbb27fffb1464e983f0f70acf00ef300000000d2000000103602b3ea1b4c00000003480000004b1f4741797bfce3ccae35f3a8f7924adff00031c3e0c280aa14ad8615e94b8a731388b4505d8cba85b12942052832ec852ed56b71855eee0a00d6414fffb1464ea8ff10409ce81e918b000000d2000000103a4193c02bcc0080000348000000454698092ca10b8a1d2f61ff1026abff20473f448d0a68092622acd8be6940e8e357a4bbc485516ced6d67975f641e60567856f49c7cb256e5a57d153fffb1464e981f0e2074f00ef181200000d20000001037c234b8124c0200000348000000498169c11c904f3c71b3539dc8136b6b86a111a6a4180913224341906542b41c06642e775a459b99aaff5429c1b87d0198596634dd9ee7ef469bfa8c1fffb1464eb01f0fb0b4e8306193200000d2000000103c423430334c00000003480000004daa6dd46b3d776148f9c77f0a597b6edaefac8030001294100d86923a614d73aa3d55405b5c20eeeffd17a346121c73070c5c49895fbc25d8be945b0fffb1464ea03f0ef0ccfa946184000000d2000000103a40f3c0434c00800003480000004aed69a7b7fd1decaff132fc8819310049918846ee11e96d489549022c77c7d724391631f3bf50dc3f5d25b7cfffba1029d3825251a5f20670d949cf3fffb1464ea81f0f605cfaa03189200000d200000010394214da4a4c0280000348000000400c90cf55fea7de5d6af571dbadaed76b6ff80042c8834cc954996e6ca083d83828f0f9941f66fbb67af8972249a3114580c8c54429eed2bd2aec261fffb1464ea81f0f5094e818618b200000d2000000104304b40a584640000003480000004e72fd9de56efa0bd2fdcdfd73773800441403723076e5e9744053e3b489358792c66a17501171c88c4a69a9b8d474487c95484af7ae579c8396e6f71fffb1464e801f0b407d1c0c1198a00000d2000000104202f380494c40800003480000004fbfc6e1beffaf21632dff691b76db6d6c10300010c1c6811a850fa3cbd4e59a9479018a96870d7c5467fa9b0a0560b094cd49024988cd9eddbef169dfffb1464ea01f0f90a52e0c918c800000d2000000103c425380299200000003480000004e885d525bddef34f8d79bfe3aa8238ea0830c68c83cce3f66da6eb2b5fc46fae4e8c5f1ffaeff2967509010db286ad27e6a73af1853e652133b224dffffb1464e901f0f10a4fa846301000000d2000000104388b3804847148000034800000047eab6b4b95f4f81ffec7355e9a64a091dc1c486a112755d29d24f4ba8e1621b0d52e43cd0addc08f06925032e38d44a1100528098a2ad464da6a6018fffb1464e701f0c108d14026182000000d2000000103fc293a0118c008000034800000043c2e68e1fad5c4f6ffe9e501942259a3866148495df685dbc24ab7e505b0fb1c2b95a9768539365f91836061a35cc262a533796b47cfa0da9f050a4afffb1464e901f0ea0c5a6826186c00000d200000010394233c01846a0800003480000004745d87f5dfec3eddfeb18bf491aa41c0a0017604ab8983ca328e56e958712215689f7b22eb93a22642e9f7a56c2007071240759e99987b9cec54bc64fffb1464ea03f1030d4e0046186200000d2000000103842b3ea4846688000034800000042097aa1920dae5d2e55914d84a965db6d6780004326b29640e1658b41048e4cc3e09aaf496b35ac04dc8914909b5c2d3349611470e2229568a501053fffb1464e981f0e608d960e9185c00000d2000000103e423380324c2880000348000000409a40ed36fb1828c8de946f1cb5f00042c51ba30ab5fb30705d2483e0208094dd41595dcaa251ad2741f7978587955aeaef73960ce6eff301fd5fcb2fffb1464e987f0f20a4fa826105000000d200000010390153ca1244088000034800000048477cb5df9794639aef5f6668014b1c06c40c5230b2263ada5a218c3069a0442e3cbbd8c8c40b660510d8808c74295faf4259b38fd5d1dd961bc9478fffb1464ea01f0fb0a59e849300c00000d200000010398173c038cc0080000348000000459d134fddbd6530376d5fe6ae7098a82848ca8ca387a418e569b8fbeba86d7af90c97f99648a859ad9bd91bed10aa09920888eb168c52d64f1da6863fffb1464e98ff0df06cf0046182200000d2000000103c0273a0298c0080000348000000474a60d81744ac0aaa54907d21381a6ca85b013f5dc344eb7369cc922b818dbba17fa020aa277db2ff2546a0b0985c0b46a76d02ba07aa26806878c10fffb1464ea81f0fe09cfc029301000000d2000000103681f498018c00000003480000004218cb4be2ef598e874541c9fd0044019542e60c7416140ad7c3dc19ceef2aaa93a8b75b5f73f3f76ed1b7b5e8b883b0084d9a40c04af048c850d8182fffb1464eb0ff10211cf01211b9200000d2000000103b43f3e01186248000034800000042b63543002e5aee5c43ad2fcd5921480e0a71203144dcf17af05616c860945d41bda6df61bbd48ae90f84bad6e1c9896a9fc216b486519d4c584c123fffb1464ea01f0ea0a50c066300000000d2000000103f029430288c00000003480000004e7714c41009bd66d8be8fd1d0ac35601850e82aa9331666d749c728a596647ab0d0c160f9f17eeef78419a642e364616bc9cb28c2629120749a54a0ffffb1464e981f0d209d8e046303c00000d20000001043829418024c0400000348000000468223773eaea913ffffe215748909c0c5ba3b81c131fb6db2ca25081043036c0a02614fc7ffaa62100a000751902650020a2bea30c5cb18b442ddfe4fffb1464e983f0f4064fa8a6300000000d2000000103881b3c02b060c8000034800000049f4b98d703fd944bf7d05b35e4c02e1644b4e5a60b4a51343310ae34b2cc50d21ffc5e4dc73ad92649b880be260e9e401ef08c31ab516f3e55a36dabfffb1464ea0ff0f0084e804f300000000d2000000103b0133c03a58008000034800000046b95525fdd3cd7b8137e535b95842e014c155409edea222a875fd18337bc5ea4b2d019a5a4a0298b875c61b5b3084385cea691234fe0a4c35da31e16fffb1464ea0ff0fc10ce812311d200000d2000000103b0513a04844d000000348000000484428a1e2e2ff5ed8e2c8debaa9a150074c1800161183d8902a02537634921964e10456a27d7c50a0f63d158380416888c8512025b1752a6509e6808fffb1464e98ff0f5064e80ec302200000d20000001036c233a00b060400000348000000495b114c7011069d3cca936271e62aa440061e5167818ef49db8be59198ef07a4679eb30e477ad1adb698528d0012a3441491e0d13a49ab860257a28efffb1464ea81f10e08cf28e9301200000d20000001037c193c03246240000034800000041f0c495fb2b04b2765d52f749ad29a8322130945294f7e29305c60e302f6d254ebe87a7adacb1f5289b060507454985d13b34c4a7d9027b9a87b8703fffb1464e981f0fb0950c0c6308000000d2000000103802b42033cc100000034800000049f8db8416fd51d3f6adfc9698adeddbfb5f800042e042d41259b569a2acdb5920780a8aa82a8d696010493424e2c951640993b835f34b1316664a8c0fffb1464e98ff0f2104f8169185000000d2000000104d4874204986380000034800000047091e827778dff5bff73889ee2503464c1a0e4948db0511f4dbeb3f3b92e5d0699bddfd5df7fcd229df211909e111416d86613b4432601481d6de92dfffb1464e501f07f035ba062180e00000d2000000104342b42a118c008000034800000040b8e5bf6a6bfedbf8cbbf5f85550b4e38dc2c18b2b8431086e9bb5eb174952cf0c8a83e80fa2b6d18a763bd7577f810101105ea491c304028e58261dfffb1464ea0ff0e91dd080c11b6000000d2000000103f4273e0324c048000034800000044b5cc53d71eeda85c319e81a81f8c0b273888f9d04c9d20a0200826e961c26a3c18120ac53739b91513d6d645c8d1741f12c4433e5f8e4c8d0911462fffb1464e981f0ee0c500049184000000d2000000103a82943012cc000000034800000047a8a2701d49a745735eeeb62188030f44a0888ccb47c2d30220152507387907d4cf5fcf91fd0906c30151e0641c4068325d82cd761320603dfd30591fffb1464ea01f0fd0bd0c0c312a000000d2000000103a81f3ea3a4c00000003480000004d19bbbff7f3730ffddf1ea3a2a8476095408507806e7351961ef381c413290d9b53897364971020588cbdad060385ca89fc62ff3756760d01000b903fffb1464e981f0ca0dd0c0c11a0800000d2000000104884b4381a461400000348000000461f9d8e00ae514277d9a1ad71645a6de22282a10c53146aab1086706187609a2d14dc28658cec4ebb243a311ec801967123832113366bd62450fe67ffffb1464e88ff0d1074f8049300000000d2000000103ec1b3a0118c00800003480000004a80c82e1fe04f93fdbc1bf729569809c0a70e870e4d006432f24141a2cb6b0a3828587ef41c76b488fa61825894b8d1216b2c3d828364e114e072189fffb1464e981f0cf0954e0e2300800000d20000001041c253ca014c00800003480000004e9885cd0aadaa69318d82571f34a84312385a81505a85c3d58a07a26260e9b608e02df3e071ed6f6efe21a64781308142e56ab4d379e671ee421a1abfffb1464ea03f0e309cf00c301b200000d2000000103f81d3ca3a4c048000034800000040328ce70c2e7d35b0ec79299965d8447921eb0677277498b60585815bcaf3f2fbff64f6e8bffd6a7f2eaee888888789887d8010b01900b16c6a91750fffb1464e981f0f70ed220a6483800000d2000000102f4154721184080000034800000042f900d936011cee8ae958520a09ae88974a808462491760c651b04c6d50c65894e6fb83f8702e8d439ee88f210d113ccb9184dab5196b03cd2971d47fffb1464ec01f1260e4f81e911c000000d2000000103842748a63c648000003480000004dbe458af7ba3398ca546e7a9d9835c5821293653bb2df7331d740d1178c2050a31a44d52c48e2100f954a86df24e542c1518e4f6062904ccb044ca87fffb1464e98ff0c70751014c303000000d20000001044c253e04a4414800003480000004440265a5672fda5582872d3d4ea588881d8ea1722d5f32f2eefbfd1f3e5a420af53fd6effea86fbd565660bca8624123828c01d930a1e62a7040e41dfffb1464e98ff0ef07d00186489000000d2000000103d8273e05b041400000348000000448a56051e45ad68ab0c2fbd7a54a408f0930f8663872353ed0f8c60410f200caac5a252269eb8662d8c81849cc9a3171442e666154f604d0ba064ce3fffb1464e90ff0d609d00121198000000d2000000104202d3c06180388000034800000048eb1164b0c589c552a42ea77880712c1ce593da3a83b84921b854ad2a24758110a9635425c6a8b695352d2b2206142006603014825692c321a3f4d65fffb1464e90ff0de054f804c481000000d2000000103f42b3a01306100000034800000041c9f5ec58317694229ad69a2a1aa0100a05331a8a85190794619221780415b943aa783c2f1fdd6f9a424f00a0498586323c41139088c794486460f78fffb1464e903f0d805cf8046301000000d200000010414673ca6044f00000034800000040ed31e761137650d3ce361caa206895b51a7284515404165ec139b32054fdff39beab959fcffbffd674a170b24e11e4f2041958db8bb5ea773b82775fffb1464e901f0ec094f80a6104200000d200000010340217de29900300000348000000467c80ec71b41a3f71e5749f375e260f38d31e3053c2280a897d2a0955e6cacf1291f0ae6dd851b2166bff5774d04502d022063a88ec0f4d52c7a3548fffb1464eb0ff100104f00a9184000000d2000000103e02d3a00a460c8000034800000048956bac8143f67f81b996154aa82408988901947c1e034515e9b53178673127f143edb73beffdbbb5ee54ff5e4b4f11ac254c2cf3556b4f5340b78f2fffb1464e981f0e90acf0049300000000d2000000103ec23410498c28000003480000004b09188c3628735ab45f5f14a850a309c5dd8daaaa38a8054012277fb6a215ede29c0721d6dbbddffcef44740850b020480d4870a0dd36c54409b5cdbfffb1464e901f0d005cf8026480200000d20000001042c2d3d049862c00000348000000481de7548eac7dd5097fffc8d527eaadeefb7fb09400001c49396418893059b4c6600997ad243f5ea0d987f80b05d128a105ccd1e4ed61004cc3c8838fffb1464e90ff0dc0c4f0046189000000d2000000103982f3c0118608000003480000004a1548e158d539d34ff16bb75abffd2e080ac68c263514502e49ad3db53ca09ce63e515b2a52b440a6229b6dfbffb6d2c0e272823210d3a08327455b9fffb1464ea81f0f70acfa8c6192000000d2000000103bc2d410118c000000034800000044063d138394e5c71b41bffa17dfe1500870284638caf4cc51c1a551b634132a00428fcea589adf5ae2aa25eaa48eb43e86cec828232a9b83a420c965fffb1464ea01f0ee07d040c1300000000d2000000103b8273ea11841400000348000000497a7a6a40f3fdd3f67b879fcd735325691400085c7d1e7867949496622d447468d42d63d7094f58ab90cb351b84923729e64a012d896e1c8cb1225b3fffb1464ea03f0e306cf0069182200000d2000000104004f3aa39841c000003480000004429aa8560ce28574d3467c27e50bfe5067f4d1b52614db5c338974b08878699c9112225117cb2bf9654c414d45332e31303055555555555555555555fffb1464e981f0ef074e80c3311200000d2000000103a829410298c04000003480000004555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555fffb1464e981f0ea07ce8046184200000d2000000103a8254100a4c04000003480000004555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555fffb1464ea0ff0f506ce8046480200000d2000000103f42d3801846c0800003480000004555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555fffb1464e881f0d808db68c11a1c00000d2000000103b41f3ca0a4c00000003480000004555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555fffb1464ea01f0f60b5040a6186000000d2000000103a0236ba0a4c07000003480000004555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555fffb1464ea01f10008d04043480000000d2000000103d44f3a00a4604800003480000004555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555fffb1464e883f0e50a4fc0e2300000000d2000000104784530030c520800003480000004555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555fffb1464e60ff0a406408021180000000d20000001000001a40000002000003480000004555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555";
    private byte[] voiceStart = {0x24, 0x00, 0x07, 0x75, 0x01, 0x00, 0x23};//开始上传录音音频
    private byte[] voiceEnd = {0x24, 0x00, 0x07, 0x75, 0x02, 0x00, 0x23};//结束上传录音音频

    private byte[] data = {0x24, 0x00, 0x0A, 0x54, (byte) 0xc2, (byte) 0xde, (byte) 0xc5, (byte) 0xcc, 0x00, 0x23};//文本：罗盘
    private byte[] ttsRepeat = {0x24, 0x00, 0x07, 0x73, 0x65, 0x00, 0x23};//循环播放文本指令
    private byte[] ttsStop = {0x24, 0x00, 0x0a, 0x79, (byte) 0xFD, 0x00, 0x01, 0x02, 0x00, 0x23};//文本停止

    private void testSendTTS(byte[] test) {

        if (FPVDemoApplication.getProductInstance() != null) {
            Payload payload = FPVDemoApplication.getProductInstance().getPayload();
            payload.sendDataToPayload(test, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError != null) {
                        showToast("Error:" + djiError.getErrorCode());
                    }
                }
            });
        } else {
            showToast("未检测到payload设备");
        }
    }

    /**
     * 拼接文本指令
     *
     * @param communication
     */
    private void sendTTS2Payload(Communication communication) {
        String tts = communication.getPara().get("word");
        if (TextUtils.isEmpty(tts)) {
            tts = "未检测到语音文本";
        }
        PagerUtils pagerUtils = PagerUtils.getInstance();
        byte[] content = pagerUtils.HexString2Bytes(pagerUtils.toChineseHex(tts));
        byte[] data = pagerUtils.dataCopy(pagerUtils.TTSINS, content);
        send(data, communication);
    }

    /**
     * 发送指令到Payload
     *
     * @param data
     * @param communication
     */
    private void send(byte[] data, Communication communication) {
        if (FPVDemoApplication.getProductInstance() != null) {
            Payload payload = FPVDemoApplication.getProductInstance().getPayload();
            payload.sendDataToPayload(data, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    CommonDjiCallback(djiError, communication);
                }
            });
        } else {
            showToast("未检测到设备!");
            communication.setResult("payload is null :" + "检测不到扩音器");
            communication.setCode(-1);
            communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            NettyClient.getInstance().sendMessage(communication, null);
        }
    }

}
