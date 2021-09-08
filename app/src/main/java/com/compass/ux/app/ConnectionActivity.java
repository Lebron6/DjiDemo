package com.compass.ux.app;

import androidx.annotation.NonNull;

import dji.common.airlink.OcuSyncBandwidth;
import dji.common.airlink.OcuSyncFrequencyBand;
import dji.common.airlink.PhysicalSource;
import dji.common.airlink.SignalQualityCallback;
import dji.common.airlink.WifiChannelInterference;
import dji.common.battery.BatteryState;
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
import dji.common.flightcontroller.RTKState;
import dji.common.flightcontroller.VisionDetectionState;
import dji.common.flightcontroller.WindDirection;
import dji.common.flightcontroller.flightassistant.PerceptionInformation;
import dji.common.flightcontroller.rtk.NetworkServiceSettings;
import dji.common.flightcontroller.rtk.NetworkServiceState;
import dji.common.flightcontroller.rtk.RTKBaseStationInformation;
import dji.common.flightcontroller.rtk.RTKConnectionStateWithBaseStationReferenceSource;
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
import dji.common.mission.waypoint.WaypointMissionFinishedAction;
import dji.common.mission.waypoint.WaypointMissionFlightPathMode;
import dji.common.mission.waypoint.WaypointMissionHeadingMode;
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
import dji.common.remotecontroller.GPSData;
import dji.common.useraccount.UserAccountState;
import dji.common.util.CommonCallbacks;
import dji.keysdk.AirLinkKey;
import dji.keysdk.DiagnosticsKey;
import dji.keysdk.FlightControllerKey;
import dji.keysdk.GimbalKey;
import dji.keysdk.KeyManager;
import dji.keysdk.callback.GetCallback;
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
import dji.sdk.mission.timeline.TimelineElement;
import dji.sdk.mission.timeline.TimelineEvent;
import dji.sdk.mission.waypoint.WaypointMissionOperator;
import dji.sdk.mission.waypoint.WaypointV2ActionListener;
import dji.sdk.mission.waypoint.WaypointV2MissionOperator;
import dji.sdk.mission.waypoint.WaypointV2MissionOperatorListener;
import dji.sdk.network.RTKNetworkServiceProvider;
import dji.sdk.payload.Payload;
import dji.sdk.products.Aircraft;
import dji.sdk.remotecontroller.RemoteController;
import dji.sdk.sdkmanager.DJISDKManager;
import dji.sdk.sdkmanager.LiveStreamManager;
import dji.sdk.useraccount.UserAccountManager;
import dji.ux.widget.MapWidget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.compass.ux.R;
import com.compass.ux.bean.FlightControllerBean;
import com.compass.ux.bean.LaserMeasureInformBean;
import com.compass.ux.bean.MissionPointBean;
import com.compass.ux.bean.SettingValueBean;
import com.compass.ux.bean.StorageStateBean;
import com.compass.ux.bean.StringsBean;
import com.compass.ux.bean.TransmissionSetBean;
import com.compass.ux.bean.UpdateActionToWebBean;
import com.compass.ux.bean.WayPointsBean;
import com.compass.ux.bean.WayPointsV2Bean;
import com.compass.ux.bean.WebInitializationBean;
import com.compass.ux.utils.ModuleVerificationUtil;
import com.compass.ux.netty_lib.NettyService;
import com.compass.ux.netty_lib.activity.NettyActivity;
import com.compass.ux.netty_lib.netty.NettyClient;
import com.compass.ux.netty_lib.zhang.Communication;
import com.compass.ux.utils.PagerUtils;
import com.compass.ux.utils.DeleteUtil;
import com.compass.ux.utils.LocationUtils;
import com.compass.ux.utils.MapConvertUtils;
import com.compass.ux.utils.SPUtils;
import com.compass.ux.utils.WenDuUtils;
import com.compass.ux.utils.fastClick;
import com.compass.ux.view.TabNavitationLayout;
import com.compass.ux.xclog.CrashHandler;
import com.compass.ux.xclog.XcFileLog;
import com.compass.ux.xclog.XcLogConfig;
import com.dji.mapkit.core.maps.DJIMap;
import com.dji.mapkit.core.models.DJILatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DecimalFormat;
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

public class ConnectionActivity extends NettyActivity implements MissionControl.Listener, View.OnClickListener, TextureView.SurfaceTextureListener, DJIDiagnostics.DiagnosticsInformationCallback, RTK.RTKBaseStationListCallback {
    private String liveShowUrl = "";
    private static final String TAG = ConnectionActivity.class.getName();
    private TextView mTextConnectionStatus;
    private TextView mTextProduct;
    private TextView mVersionTv;
    private TextView text_net_rtk_state, text_net_rtk_account_state, text_plane_status;
    private Button mBtnOpen, btn_download, btn_gaode, btn_simulator, btn_login, btn_pl, btn_voice_end;
    private EditText et_zoom;
    private TabNavitationLayout tab_change;
    private RelativeLayout layout_sort, layout_air_info;
    private TextView tv_zoom, tv_horizontal_speed, tv_control_distance, tv_distance, tv_heigth, tv_vertical_speed;
    private TextView tv_txt_1, tv_txt_2, tv_txt_3, tv_txt_4, tv_txt_5, tv_txt_6, tv_txt_7,
            tv_txt_8, tv_txt_9, tv_txt_10, tv_txt_11, tv_txt_12, tv_txt_13, tv_txt_14, tv_txt_15, tv_txt_16, tv_txt_17;
    private MapWidget mapWidget;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;


    private int deviceWidth;
    private int deviceHeight;

    private EditText et_url;


    private FlightController mFlightController;
    private RemoteController mRemoteController;
    private FlightAssistant mFlightAssistant;
    private OcuSyncLink ocuSyncLink;
    private RTK mRTK;
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
    ViewGroup parentView = null;
    RelativeLayout layout_previewer_container = null;

    FlightControllerKey wind_direction_key;
    FlightControllerKey wind_speed_key;

    FlightControllerBean flightControllerBean = null;
    Camera camera = null;
    Communication communication_flightController = null;
    Communication communication_rtk = null;
    Communication communication_takePhoto = null;
    Communication communication_battery = null;
    Communication communication_rtkState = null;
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
    SettingValueBean.NetRTKBean setRtkBean = new SettingValueBean.NetRTKBean();//监听
    SettingValueBean.BatteryStateBean batteryStateBean = new SettingValueBean.BatteryStateBean();//监听
    SettingValueBean.NetRTKBean.Info infoBean = new SettingValueBean.NetRTKBean.Info();

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
    String mWayPointActionV2 = "";
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

    private MissionPointBean mMissionPointBean;
    private WayPointsV2Bean.WayPointsBean.WayPointActionBean.VoiceBean mVoiceBeanShottingContant;
    private LiveStreamManager.OnLiveChangeListener mOnLiveChangeListenner;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_connection);

        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        final Display display = windowManager.getDefaultDisplay();
        Point outPoint = new Point();
        display.getRealSize(outPoint);
        deviceHeight = outPoint.y;
        deviceWidth = outPoint.x;
        mapWidget = findViewById(R.id.map_widget);
        mapWidget.initAMap(new MapWidget.OnMapReadyListener() {
            @Override
            public void onMapReady(@NonNull DJIMap map) {
                map.setOnMapClickListener(new DJIMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(DJILatLng latLng) {
                        onViewClick(mapWidget);
                    }
                });
                map.getUiSettings().setZoomControlsEnabled(false);
            }
        });
        mapWidget.onCreate(savedInstanceState);

        mHandler = new Handler(Looper.getMainLooper());
        initAllKeys();
        initUI();
        initCustomLoggers();
        //注册广播接收器以接收设备连接的更改。
        IntentFilter filter = new IntentFilter();
        filter.addAction(FLAG_CONNECTION_CHANGE);
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
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        mapWidget.onResume();
    }

    @Override
    public void onPause() {
        Log.e(TAG, "onPause");
        uninitPreviewer();
        mapWidget.onPause();
        super.onPause();
    }

    //    在mVideoSurfaceTextureView 上显示和重置实时视频流
    private void initPreviewer() {
        BaseProduct product = ApronApp.getProductInstance();
        if (product == null || !product.isConnected()) {
            showToast(getString(R.string.disconnected));
        } else {
            if (null != mVideoSurface) {
                mVideoSurface.setSurfaceTextureListener(this);
            }
            if (!product.getModel().equals(Model.UNKNOWN_AIRCRAFT)) {

                VideoFeeder.getInstance().getPrimaryVideoFeed().addVideoDataListener(mReceivedVideoDataListener);
                VideoFeeder.getInstance().setTranscodingDataRate(1f);
                String cvs = VideoFeeder.getInstance().getPrimaryVideoFeed().getVideoSource().value() + "";
                showToast(cvs);
                if (cvs.equals("5")) {
                    currentVideoSource = "1";//FPV
                } else {
                    currentVideoSource = "0";//相机
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
//        removeWaypointMissionListener();
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
        mapWidget.onDestroy();
        super.onDestroy();

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapWidget.onLowMemory();
    }

    @Override
    public void onDetachedFromWindow() {

        super.onDetachedFromWindow();
        if (mOnLiveChangeListenner != null && isLiveStreamManagerOn()) {
            DJISDKManager.getInstance().getLiveStreamManager().unregisterListener(mOnLiveChangeListenner);
        }
    }

    private void initUI() {
        layout_previewer_container = (RelativeLayout) findViewById(R.id.layout_previewer_container);
        mTextConnectionStatus = (TextView) findViewById(R.id.text_connection_status);
        mTextProduct = (TextView) findViewById(R.id.text_product_info);
        mVersionTv = (TextView) findViewById(R.id.textView2);
        text_net_rtk_state = (TextView) findViewById(R.id.text_net_rtk_state);
        text_net_rtk_account_state = (TextView) findViewById(R.id.text_net_rtk_account_state);
        text_plane_status = (TextView) findViewById(R.id.text_plane_status);
        mVersionTv.setText(getResources().getString(R.string.sdk_version, DJISDKManager.getInstance().getSDKVersion()));
        mBtnOpen = (Button) findViewById(R.id.btn_open);
        mBtnOpen.setOnClickListener(this);
        mBtnOpen.setEnabled(true);
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

        Button btLoginAcc = findViewById(R.id.bt_login_account);
        btLoginAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginAccount();
            }
        });
        findViewById(R.id.btn_stop_live_show).setOnClickListener(this);
        findViewById(R.id.btn_start_live_show).setOnClickListener(this);

        mVideoSurface = (TextureView) findViewById(R.id.video_previewer_surface);
        parentView = (ViewGroup) findViewById(R.id.root_view);
        mVideoSurface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onViewClick(mVideoSurface);
            }
        });
        if (null != mVideoSurface) {
            mVideoSurface.setSurfaceTextureListener(this);
        }
        TextView tvWendu = findViewById(R.id.text_wendu);
        tvWendu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> listWendu = WenDuUtils.getThermalInfo();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (listWendu != null && listWendu.get(1) != null) {
                            tvWendu.setText(listWendu.get(1));
                        } else {
                            tvWendu.setText("Failed");
                        }
                    }
                });
            }
        });
        tab_change = findViewById(R.id.tab_change);
        layout_sort = findViewById(R.id.layout_sort);
        layout_air_info = findViewById(R.id.layout_air_info);
        tv_zoom = findViewById(R.id.tv_zoom);
        tv_horizontal_speed = findViewById(R.id.tv_horizontal_speed);
        tv_heigth = findViewById(R.id.tv_heigth);
        tv_distance = findViewById(R.id.tv_distance);
        tv_vertical_speed = findViewById(R.id.tv_vertical_speed);
        tv_control_distance = findViewById(R.id.tv_control_distance);
        tv_txt_1 = findViewById(R.id.tv_txt_1);
        tv_txt_2 = findViewById(R.id.tv_txt_2);
        tv_txt_3 = findViewById(R.id.tv_txt_3);
        tv_txt_4 = findViewById(R.id.tv_txt_4);
        tv_txt_5 = findViewById(R.id.tv_txt_5);
        tv_txt_6 = findViewById(R.id.tv_txt_6);
        tv_txt_7 = findViewById(R.id.tv_txt_7);
        tv_txt_8 = findViewById(R.id.tv_txt_8);
        tv_txt_9 = findViewById(R.id.tv_txt_9);
        tv_txt_10 = findViewById(R.id.tv_txt_10);
        tv_txt_11 = findViewById(R.id.tv_txt_11);
        tv_txt_12 = findViewById(R.id.tv_txt_12);
        tv_txt_13 = findViewById(R.id.tv_txt_13);
        tv_txt_14 = findViewById(R.id.tv_txt_14);
        tv_txt_15 = findViewById(R.id.tv_txt_15);
        tv_txt_16 = findViewById(R.id.tv_txt_16);
        tv_txt_17 = findViewById(R.id.tv_txt_17);
//        changeGimbalAngle(-60);
        String[] titles2 = new String[]{"云台视角", "FPV视角"};

        tab_change.setViewPager(this, titles2, R.drawable.bg_selector_left, R.color.white,
                R.drawable.bg_selector_right, R.color.white, R.color.colorTheme,
                10, 0, 1f, true);
        tab_change.setOnTitleClickListener(new TabNavitationLayout.OnTitleClickListener() {
            @Override
            public void onTitleClick(int v) {
                switch (v) {
                    case 0:
                        if (mCodecManager != null) {
                            mCodecManager.switchSource(CAMERA);
                        }
                        break;
                    case 1:
                        if (mCodecManager != null) {
                            mCodecManager.switchSource(FPV);
                        }
                        break;
                }
            }
        });

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

//                Toast.makeText(this, "开始打印", Toast.LENGTH_SHORT).show();
//                while (i<1000000){
//                    i++;
//                    Log.e("MainActivity","---time-----"+i+"--------" );
//                    XcFileLog.getInstace().i("MainActivity", "----time----"+i+"--------" );
//                }
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
//                send(PagerUtils.getInstance().TTSREPEATINS);
//                send(data);
//                }
//                testTTSYY();
                sendDataToOSDK(PagerUtils.getInstance().OPEN_LIGHT);
                break;
            }
            case R.id.btn_simulator: {
                //去控制飞机
//                Intent intent = new Intent(this, SimulatorMainActivity.class);
//                startActivity(intent);
//                isLiveShowOn();
//                startLiveShow(null);
//                resumeWaypointV2(null);
//                new Thread() {
//                    @Override
//                    public void run() {
//                        PagerUtils instance = PagerUtils.getInstance();
//                        String bytesToString = instance.bytesToString(instance.readFileFromAssets(ConnectionActivity.this, null, "jun.mp3"));
//                        byte[] bytes = instance.HexString2Bytes(bytesToString);
//                        voiceByteData = instance.splitBytes(bytes, 120);
//                        send(instance.MP3STARTSINS);
//                        upLoadMP3(voiceByteData[0]);
//                    }
//                }.start();
//                sendDataToOSDK(PagerUtils.getInstance().CLOSE_LIGHT);
//                testBaseStationRTK();
                //水平避障
                mFlightAssistant.getHorizontalVisionObstacleAvoidanceEnabled(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        activeObstacleAvoidance = aBoolean;

                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });
                break;
            }
            case R.id.btn_voice_end: {
//                send(PagerUtils.getInstance().MP3STOPSINS);
                mRTK.setRtkEnabled(false, new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (djiError == null) {
                            Log.e("D-RTK:关闭", "setRTK true");
                        } else {
                            Log.e("D-RTK:关闭", "setRTK false" + djiError.getDescription());
                        }
                    }
                });
            }
            break;

            case R.id.btn_login:
//                DJISDKManager.getInstance().getLiveStreamManager().stopStream();
//                Toast.makeText(getApplicationContext(), "Stop Live Show", Toast.LENGTH_SHORT).show();
//                pauseWaypointV2(null);
//                send(PagerUtils.getInstance().TTSSTOPINS);
                break;
            case R.id.btn_start_live_show:
//                startLiveShow(null);
//                restartLiveShow();
                break;
            case R.id.btn_stop_live_show:
                DJISDKManager.getInstance().getLiveStreamManager().stopStream();
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
//        Toast.makeText(getApplicationContext(), "Start Live Show", Toast.LENGTH_SHORT).show();
//        ToastUtils.setResultToToast("Start Live Show");
        if (!isLiveStreamManagerOn()) {
            return;
        } else {
            /***
             * 新加的推流的状态改变监听，没有测试
             */
            mOnLiveChangeListenner = new LiveStreamManager.OnLiveChangeListener() {
                @Override
                public void onStatusChanged(int i) {
                    XcFileLog.getInstace().i("飞机飞行流程监听推流状态改变", "status changed : " + i);
                    Log.d("飞机飞行流程监听推流状态改变", "status changed : " + i);
                }
            };
            DJISDKManager.getInstance().getLiveStreamManager().registerListener(mOnLiveChangeListenner);

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
                DJISDKManager.getInstance().getLiveStreamManager().setStartTime();
            }
        }.start();
    }

    private void stopLiveShow() {
        if (!isLiveStreamManagerOn()) {
            return;
        }
        DJISDKManager.getInstance().getLiveStreamManager().stopStream();
        showToast("Stop Live Show");
    }

    private void restartLiveShow(Communication communication) {
        int delayTime;
        if (communication != null && communication.getPara() != null) {
            String delay = communication.getPara().get("delay");
            if (TextUtils.isEmpty(delay)) {
                delayTime = 2000;
            } else {
                delayTime = (Integer.valueOf(delay)) * 1000;
            }
        } else {
            delayTime = 2000;
        }
        if (!isLiveStreamManagerOn()) {
            return;
        }
        DJISDKManager.getInstance().getLiveStreamManager().stopStream();
        XcFileLog.getInstace().i("liveStatus", DJISDKManager.getInstance().getLiveStreamManager().isStreaming() + "");
        new Handler().postDelayed(new Runnable() {//测试延时两秒重启推流
            @Override
            public void run() {
                startLiveShow(communication);
            }
        }, delayTime);

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
            initRemoteController();
            initCamera();
//            initGimbal();
//            addWaypointMissionListener();//添加航点的监听
            initErrorLog();//初始化错误日志
            initOcuSyncLink();
            initPreviewer();
            startLiveShow(null);//开始推流
            initBattery();


            Log.d("广播航点-EU", "广播");
            if (isAppInterrupt && mWayPointsV2BeanInterrupt != null) {
                Log.d("飞机飞行流程获取回复航线数据", mWayPointsV2BeanInterrupt.toString());
                XcFileLog.getInstace().i("飞机飞行流程获取回复航线数据gson格式化后", mWayPointsV2BeanInterrupt != null ? mWayPointsV2BeanInterrupt.toString() : "空的");
                if (mWayPointsV2BeanInterrupt != null) {
                    setWayV2UpListener(mWayPointsV2BeanInterrupt, null);
                }
            }
        }
    };

    //遥控器
    private void initRemoteController() {
        Aircraft aircraft = ApronApp.getAircraftInstance();
        if (aircraft == null || !aircraft.isConnected()) {
            mRemoteController = null;
            return;
        } else {
            mRemoteController = aircraft.getRemoteController();
            mRemoteController.setGPSDataCallback(new GPSData.Callback() {
                @Override
                public void onUpdate(GPSData gpsData) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mFlightControllerState != null) {
                                double length = LocationUtils.getDistance(gpsData.getLocation().getLongitude() + "", gpsData.getLocation().getLatitude() + ""
                                        , mFlightControllerState.getAircraftLocation().getLongitude() + "", mFlightControllerState.getAircraftLocation().getLatitude() + "");
                                tv_control_distance.setText(length + "(m)");
                            }
                        }
                    });
                }
            });
        }
    }

    private FlightControllerState mFlightControllerState;
    GPSData gpsData;

    private void initCustomLoggers() {
        XcFileLog.init(new XcLogConfig());
        CrashHandler.getInstance().init();
    }

    private void initOcuSyncLink() {
        try {
            ocuSyncLink = ApronApp.getAirLinkInstance().getOcuSyncLink();
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
//                        Toast.makeText(ConnectionActivity.this,v + "",Toast.LENGTH_SHORT).show();
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
                ApronApp.getAirLinkInstance().getWiFiLink().setChannelInterferenceCallback(new WiFiLink.ChannelInterferenceCallback() {
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
        BaseProduct mProduct = ApronApp.getProductInstance();

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
            if (isM300Product()) {
                tv_zoom.setVisibility(View.VISIBLE);
                tab_change.setVisibility(View.VISIBLE);
            } else if (isINSPIRE2Product()) {
                tv_zoom.setVisibility(View.GONE);
                tab_change.setVisibility(View.VISIBLE);
            } else {
                tv_zoom.setVisibility(View.GONE);
                tab_change.setVisibility(View.GONE);
            }
        } else {
            Log.v(TAG, "refreshSDK: False");
//            mBtnOpen.setEnabled(false);

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

    DecimalFormat df1 = new DecimalFormat("#.00");

    //    我们首先检查飞机是否不为零并且已连接，然后调用getFlightController()飞机方法获取mFlightController变量。
//    重写onUpdate()方法来获取最新的仿真状态数据，然后调用getYaw()，getPitch()，getRoll()，getPositionX()，getPositionY()和getPositionZ()
//    的方法SimulatorState来获得更新的偏航，俯仰，滚转位X，位置▲和positionZ值并显示它们mTextView。
    private void initFlightController() {
        Aircraft aircraft = ApronApp.getAircraftInstance();
        if (aircraft == null || !aircraft.isConnected()) {
            mFlightController = null;
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
                        mFlightControllerState = flightControllerState;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (gpsData != null) {
                                    double length = LocationUtils.getDistance(gpsData.getLocation().getLongitude() + "", gpsData.getLocation().getLatitude() + ""
                                            , flightControllerState.getAircraftLocation().getLongitude() + "", flightControllerState.getAircraftLocation().getLatitude() + "");
                                    tv_control_distance.setText(length + "(m)");
                                }
                                tv_distance.setText("距返航点" + df1.format(LocationUtils.getDistance(flightControllerState.getHomeLocation().getLongitude() + "", flightControllerState.getHomeLocation().getLatitude() + ""
                                        , flightControllerState.getAircraftLocation().getLongitude() + "", flightControllerState.getAircraftLocation().getLatitude() + "")) + "m");//返航距离
                                tv_horizontal_speed.setText(Math.abs(flightControllerState.getVelocityX()) + "(m/s)");//水平速度
                                tv_vertical_speed.setText(Math.abs(flightControllerState.getVelocityY()) + "(m/s)");//垂直速度
                                tv_heigth.setText(flightControllerState.getAircraftLocation().getAltitude() + "  (m)");

                            }
                        });

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
                                communication_isFlying.setEquipmentId(ApronApp.EQUIPMENT_ID);
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
//
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
                                    communication_length.setEquipmentId(ApronApp.EQUIPMENT_ID);
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
                                    communication_gohomelength.setEquipmentId(ApronApp.EQUIPMENT_ID);
                                    communication_gohomelength.setMethod((Constant.GO_HOME_LENGTH));
                                    communication_gohomelength.setResult(goHomelength + "");
                                    NettyClient.getInstance().sendMessage(communication_gohomelength, null);
                                }

                                flightControllerBean.setGoHomeLength(length);
                                flightControllerBean.setWindSpeed(subWindSpeed);
                                flightControllerBean.setWindDirection(subWindDirection);
                                flightControllerBean.setAreMotorsOn(flightControllerState.areMotorsOn());
                                flightControllerBean.setFlying(flightControllerState.isFlying());
                                if ((flightControllerState.getAircraftLocation().getAltitude() + "").equals("NaN")) {//高度
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
                                communication_flightController.setEquipmentId(ApronApp.EQUIPMENT_ID);
                                communication_flightController.setMethod((Constant.flightController));
                                XcFileLog.getInstace().i("飞机飞行流程获取的飞机控制数据", gson.toJson(flightControllerBean, FlightControllerBean.class));
                                communication_flightController.setResult(gson.toJson(flightControllerBean, FlightControllerBean.class));
//                        Log.d("MMMMM",communication_flightController.toString());
                                NettyClient.getInstance().sendMessage(communication_flightController, null);
                            }

                            //左上角飞行状态
                            if (KeyManager.getInstance() != null) {
                                KeyManager.getInstance().getValue(diagnosticsKey, new GetCallback() {
                                    @Override
                                    public void onSuccess(Object o) {
                                        text_plane_status.setText("status:" + o.toString());
                                        String planeStatusResult = "";
                                        planeStatusResult = o.toString();
//                        Log.d("diagnosticsKey", planeStatusResult);
                                        StringsBean stringsBean = new StringsBean();
                                        stringsBean.setValue(planeStatusResult);
                                        if (communication_plane_status == null) {
                                            communication_plane_status = new Communication();
                                        }
                                        communication_plane_status.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                                        communication_plane_status.setEquipmentId(ApronApp.EQUIPMENT_ID);
                                        communication_plane_status.setMethod((Constant.LINK_PLANE_STATUS));
                                        communication_plane_status.setResult(gson.toJson(stringsBean, StringsBean.class));
                                        NettyClient.getInstance().sendMessage(communication_plane_status, null);
                                    }

                                    @Override
                                    public void onFailure(DJIError djiError) {
                                        if (djiError != null) {
                                            text_plane_status.setText("status:" + djiError.getDescription());
                                        } else {
                                            text_plane_status.setText("status:" + "unknow");
                                        }

                                    }
                                });
                            }
                            //每秒一次
                            if (fastClick.flightControllerClick()) {
                                //每秒返回遥控器信号
                                StringsBean upLinkBean = new StringsBean();
                                upLinkBean.setValue(webInitializationBean.getUpLink() + "");
                                if (communication_up_link == null) {
                                    communication_up_link = new Communication();
                                }
                                communication_up_link.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                                communication_up_link.setEquipmentId(ApronApp.EQUIPMENT_ID);
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
                                communication_down_link.setEquipmentId(ApronApp.EQUIPMENT_ID);
                                communication_down_link.setMethod((Constant.DOWN_LOAD_SIGNAL));
                                communication_down_link.setResult(gson.toJson(upLinkBean, StringsBean.class));
                                NettyClient.getInstance().sendMessage(communication_down_link, null);


                            }


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
                if (ApronApp.getProductInstance().getAirLink() != null) {
                    ApronApp.getProductInstance().getAirLink().setUplinkSignalQualityCallback(new SignalQualityCallback() {
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
                if (ApronApp.getProductInstance().getAirLink() != null) {
                    ApronApp.getProductInstance().getAirLink().setDownlinkSignalQualityCallback(new SignalQualityCallback() {
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

                mFlightAssistant = mFlightController.getFlightAssistant();
                //向上避障
                mFlightAssistant.getUpwardVisionObstacleAvoidanceEnabled(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        upwardsAvoidance = aBoolean;
                    }

                    @Override
                    public void onFailure(DJIError djiError) {
                    }
                });
                //向下避障
                mFlightAssistant.getLandingProtectionEnabled(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        landingProtection = aBoolean;
                    }

                    @Override
                    public void onFailure(DJIError djiError) {
                    }
                });

                //水平避障
                mFlightAssistant.getHorizontalVisionObstacleAvoidanceEnabled(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        activeObstacleAvoidance = aBoolean;
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });
                //精确着陆
                mFlightAssistant.getPrecisionLandingEnabled(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        precisionLand = aBoolean;
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });
                //视觉定位
                mFlightAssistant.getVisionAssistedPositioningEnabled(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        visionAssistedPosition = aBoolean;
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });

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

                    //rtk
                    mRTK = mFlightController.getRTK();
                    addRTKStatus();//rtk实时状态返回
                    mRTK.setRtkBaseStationListCallback(this);//监听搜索到的基站


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
//        camera = FPVDemoApplication.getProductInstance().getCameras().get(0);
        List<Camera> cameras = ApronApp.getProductInstance().getCameras();//H20T
        camera = cameras != null ? cameras.get(0) : null;
        if (camera != null) {
            List<Lens> lensList = camera.getLenses();
            Log.d("获取镜头", (lensList == null ? (null + "") : lensList.size() + ""));
        }
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
                    if (cameraVideoStreamSource != null) {
                        mCameraVideoStreamSource = cameraVideoStreamSource.value() + "";
                        webInitializationBean.setCurrentLens(cameraVideoStreamSource.value() + "");
                    }


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
                    communication_StorageState.setEquipmentId(ApronApp.EQUIPMENT_ID);
                    communication_StorageState.setMethod((Constant.STORAGE_STATE));
                    communication_StorageState.setResult(gson.toJson(bean, StorageStateBean.class));
                    NettyClient.getInstance().sendMessage(communication_StorageState, null);
                }
            });
            if (isM300Product() && camera.getLenses() != null && camera.getLenses().size() > 0) {
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
                Log.e("相机位置", camera.getIndex() + "");

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
                        Log.d("飞机飞行流程初始化数据红外变焦焦距", thermalDigitalZoomFactor.value() + "");
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

                        Log.d("HHHHHFocalLength", hybridZoomSpec.getFocalLengthStep() + "");//11
                        Log.d("HHHHHMaxH", hybridZoomSpec.getMaxHybridFocalLength() + "");//55620
                        Log.d("HHHHHMinH", hybridZoomSpec.getMinHybridFocalLength() + "");//317
                        Log.d("HHHHHMaxO", hybridZoomSpec.getMaxOpticalFocalLength() + "");//5562
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });
//                获取当前变焦焦距
                camera.getLens(0).getHybridZoomFocalLength(new CommonCallbacks.CompletionCallbackWith<Integer>() {
                    @Override
                    public void onSuccess(Integer integer) {
                        Log.d("飞机飞行流程初始化数据变焦焦距", integer + "");
                        webInitializationBean.setHybridZoom(getSmallZoomValue(integer));
//                        tv_zoom.setText("变焦   " + integer + "x");

                    }

                    @Override
                    public void onFailure(DJIError djiError) {
//                    Log.d("HHHHHcurr", djiError.toString());
                    }
                });
            } else if (camera != null) {
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
                Log.e("相机位置", camera.getIndex() + "");

//                //返回曝光模式
                camera.getExposureMode(new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.ExposureMode>() {
                    @Override
                    public void onSuccess(SettingsDefinitions.ExposureMode exposureMode) {
                        webInitializationBean.setExposureMode(exposureMode.value());
                    }

                    @Override
                    public void onFailure(DJIError djiError) {
                    }
                });
                //返回iso数据
                camera.getISO(new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.ISO>() {
                    @Override
                    public void onSuccess(SettingsDefinitions.ISO iso) {
                        webInitializationBean.setISO(iso.value());
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });
                //返回shutter数据
                camera.getShutterSpeed(new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.ShutterSpeed>() {
                    @Override
                    public void onSuccess(SettingsDefinitions.ShutterSpeed shutterSpeed) {
                        webInitializationBean.setShutter(shutterSpeed.value());
                    }

                    @Override
                    public void onFailure(DJIError djiError) {
                    }
                });
                //返回曝光补偿
                camera.getExposureCompensation(new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.ExposureCompensation>() {
                    @Override
                    public void onSuccess(SettingsDefinitions.ExposureCompensation exposureCompensation) {
                        webInitializationBean.setExposureCompensation(exposureCompensation.value());
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });

                camera.getFocusMode(new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.FocusMode>() {
                    @Override
                    public void onSuccess(SettingsDefinitions.FocusMode focusMode) {
                        webInitializationBean.setFocusMode(focusMode.value() + "");
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });

                camera.getAELock(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        webInitializationBean.setLockExposure(aBoolean ? "0" : "1");
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });

                camera.getThermalDigitalZoomFactor(new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.ThermalDigitalZoomFactor>() {
                    @Override
                    public void onSuccess(SettingsDefinitions.ThermalDigitalZoomFactor thermalDigitalZoomFactor) {
                        webInitializationBean.setThermalDigitalZoom(thermalDigitalZoomFactor.value() + "");
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });

                camera.getDisplayMode(new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.DisplayMode>() {
                    @Override
                    public void onSuccess(SettingsDefinitions.DisplayMode displayMode) {
                        webInitializationBean.setHyDisplayMode(displayMode.value() + "");
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });


//                获取变焦距离
                camera.getHybridZoomSpec(new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.HybridZoomSpec>() {
                    @Override
                    public void onSuccess(SettingsDefinitions.HybridZoomSpec hybridZoomSpec) {

                        Log.d("HHHHHFocalLength", hybridZoomSpec.getFocalLengthStep() + "");//11
                        Log.d("HHHHHMaxH", hybridZoomSpec.getMaxHybridFocalLength() + "");//55620
                        Log.d("HHHHHMinH", hybridZoomSpec.getMinHybridFocalLength() + "");//317
                        Log.d("HHHHHMaxO", hybridZoomSpec.getMaxOpticalFocalLength() + "");//5562
                    }

                    @Override
                    public void onFailure(DJIError djiError) {
                        Log.d("HHHHHcurr", "getHybridZoomSpec\\\\" + djiError.toString());
                    }
                });
//                获取当前变焦焦距
                camera.getHybridZoomFocalLength(new CommonCallbacks.CompletionCallbackWith<Integer>() {
                    @Override
                    public void onSuccess(Integer integer) {
                        Log.d("HHHHHcurr", integer + "");
                        webInitializationBean.setHybridZoom(integer);
//                        tv_zoom.setText("变焦   " + integer + "x");
                    }

                    @Override
                    public void onFailure(DJIError djiError) {
                        Log.d("HHHHHcurr", "getHybridZoomFocalLength\\\\" + djiError.toString());
                    }
                });
                camera.getTapZoomMultiplier(new CommonCallbacks.CompletionCallbackWith<Integer>() {
                    @Override
                    public void onSuccess(Integer integer) {
                        Log.d("HHHHHcurr", integer + "");
                        webInitializationBean.setHybridZoom(integer);
                    }

                    @Override
                    public void onFailure(DJIError djiError) {
                        Log.d("HHHHHcurr", "getTapZoomMultiplier\\\\" + djiError.toString());

                    }
                });
                camera.getDigitalZoomFactor(new CommonCallbacks.CompletionCallbackWith<Float>() {
                    @Override
                    public void onSuccess(Float aFloat) {
                        Log.d("HHHHHcurr", aFloat + "");
//                        webInitializationBean.setHybridZoom(Integer.parseInt(aFloat + ""));
                    }

                    @Override
                    public void onFailure(DJIError djiError) {
                        Log.d("HHHHHcurr", "getDigitalZoomFactor\\\\" + djiError.toString());

                    }
                });
                camera.getOpticalZoomFocalLength(new CommonCallbacks.CompletionCallbackWith<Integer>() {
                    @Override
                    public void onSuccess(Integer integer) {
                        Log.d("HHHHHcurr", integer + "");

                    }

                    @Override
                    public void onFailure(DJIError djiError) {
                        Log.d("HHHHHcurr", "getOpticalZoomFocalLength\\\\" + djiError.getDescription());
                    }
                });
            } else {
                showToast("请检查摄像头或者其他挂载类型！！！");
            }
        }
    }

    float voltageOne, voltageTwo;//电池电压是否改变
    int sendBatteryOneInfo2SocketTime = 0;//控制电池1信息向服务器发送的次数
    int sendBatteryTwoInfo2SocketTime = 0;//控制电池1信息向服务器发送的次数

    //获取电池信息
    private void initBattery() {
        Log.e("mg航点-EU", "执行initBattery");
        BaseProduct product = ApronApp.getProductInstance();
        if (product != null) {
            List<Battery> batteries = product.getBatteries();
            if (batteries != null) {
                Battery battery0 = batteries.get(0);
                battery0.setStateCallback(new BatteryState.Callback() {
                    @Override
                    public void onUpdate(BatteryState batteryState) {
                        battery0.getCellVoltages(new CommonCallbacks.CompletionCallbackWith<Integer[]>() {
                            @Override
                            public void onSuccess(Integer[] integers) {
                                int childBatteryAll = (integers[0] + integers[1] + integers[2] + integers[3] + integers[4] + integers[5] + integers[6] + integers[7] + integers[8] + integers[9] + integers[10] + integers[11]);
                                if (voltageOne != ((float) childBatteryAll / 12000)) {
                                    voltageOne = ((float) childBatteryAll / 12000);
                                    batteryStateBean.setVoltageOne(df.format(voltageOne));
                                    sendBatteryOneInfo2SocketTime++;
                                    if (sendBatteryOneInfo2SocketTime == 6) {
                                        sendBatteryOneInfo2SocketTime = 0;
                                        submitBatteryInfo(batteryState, battery0);
                                    }
                                }

                            }

                            @Override
                            public void onFailure(DJIError djiError) {
                            }
                        });

                    }
                });
                if (batteries.size() > 1) {
                    Battery battery1 = batteries.get(1);
                    battery1.setStateCallback(new BatteryState.Callback() {
                        @Override
                        public void onUpdate(BatteryState batteryState) {
                            battery1.getCellVoltages(new CommonCallbacks.CompletionCallbackWith<Integer[]>() {
                                @Override
                                public void onSuccess(Integer[] integers) {
                                    int childBatteryAll = (integers[0] + integers[1] + integers[2] + integers[3] + integers[4] + integers[5] + integers[6] + integers[7] + integers[8] + integers[9] + integers[10] + integers[11]);
                                    if (voltageTwo != ((float) childBatteryAll / 12000)) {
                                        voltageTwo = ((float) childBatteryAll / 12000);
                                        batteryStateBean.setVoltageTwo(df.format(voltageTwo));
                                        sendBatteryTwoInfo2SocketTime++;
                                        if (sendBatteryTwoInfo2SocketTime == 6) {
                                            sendBatteryTwoInfo2SocketTime = 0;
                                            submitBatteryInfo(batteryState, battery1);
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(DJIError djiError) {
                                }
                            });
                        }
                    });
                }
            } else {
                Log.e("mg航点-EU", "电池初始化");
            }
        }
    }


    DecimalFormat df = new DecimalFormat("0.000");//鏍煎紡鍖栧皬鏁?

    private void submitBatteryInfo(BatteryState batteryState, Battery battery) {
        switch (battery.getIndex()) {
            case 0:
                batteryStateBean.setBattery_discharges_one(batteryState.getNumberOfDischarges());
                batteryStateBean.setBattery_temperature_one(batteryState.getTemperature());
                batteryStateBean.setPersentOne(batteryState.getChargeRemainingInPercent());
                batteryStateBean.setIsConnectOne(battery.isConnected() ? 0 : -1);
                break;
            case 1:
                batteryStateBean.setBattery_discharges_two(batteryState.getNumberOfDischarges());
                batteryStateBean.setBattery_temperature_two(batteryState.getTemperature());
                batteryStateBean.setPersentTwo(batteryState.getChargeRemainingInPercent());
                batteryStateBean.setIsConnectTwo(battery.isConnected() ? 0 : -1);
                break;
        }

        if (communication_battery == null) {
            communication_battery = new Communication();
        }
        communication_battery.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
        communication_battery.setEquipmentId(ApronApp.EQUIPMENT_ID);
        communication_battery.setMethod((Constant.BatteryPAV));
        communication_battery.setCode(200);
        communication_battery.setResult(gson.toJson(batteryStateBean, SettingValueBean.BatteryStateBean.class));
        NettyClient.getInstance().sendMessage(communication_battery, null);
    }


    String gimbal_pitch_speed = "", gimbal_yaw_speed = "";
    int currentGimbalAngle;

    private void initGimbal() {
//        try {
        gimbal = ((Aircraft) ApronApp.getProductInstance()).getGimbal();
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
                if (gimbalState != null && gimbalState.getAttitudeInDegrees() != null) {
                    webInitializationBean.setHorizontalAngle(gimbalState.getAttitudeInDegrees().getYaw() + "");
                    webInitializationBean.setPitchAngle(gimbalState.getAttitudeInDegrees().getPitch() + "");
                    gimbalStatePitch = gimbalState.getAttitudeInDegrees().getPitch() + "";
                    if (currentGimbalAngle != (int) gimbalState.getAttitudeInDegrees().getPitch()) {
                        currentGimbalAngle = (int) gimbalState.getAttitudeInDegrees().getPitch();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                changeGimbalAngle(currentGimbalAngle);
                            }
                        });
                    }

                }

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
                Log.e("初始云台角度:", integer + "");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        changeGimbalAngle(integer);
                    }
                });
            }

            @Override
            public void onFailure(DJIError djiError) {

            }
        });

        gimbal.getControllerSmoothingFactor(YAW, new CommonCallbacks.CompletionCallbackWith<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                yaw_CSF = integer.toString();
                Log.e("初始云台角度:", integer + "-----");
            }

            @Override
            public void onFailure(DJIError djiError) {

            }
        });
    }

    //        } catch (Exception e) {
//            StringWriter sw = new StringWriter();
//            PrintWriter pw = new PrintWriter(sw);
//            e.printStackTrace(pw);
//            Log.d("初始化云台错误", sw.toString());
//        }
    int test = 0;

    boolean isAppInterrupt = false;
    WayPointsV2Bean mWayPointsV2BeanInterrupt;

    @Override
    protected void notifyData(Communication message) {
        Communication communication = message;
        switch (communication.getMethod()) {
            case Constant.LIVE_PATH://后台拿到的推流地址

                liveShowUrl = communication.getPara().get("desRtmpUrl");

                String route = communication.getPara().get("route");
                if (route == null) {
                    Log.d("飞机飞行流程获取回复航线数据", "空的");
                    XcFileLog.getInstace().i("飞机飞行流程获取回复航线数据", "route为空的");
                } else {
                    Log.d("飞机飞行流程获取回复航线数据gson格式化前", route);
                    mWayPointsV2BeanInterrupt = gson.fromJson(route, WayPointsV2Bean.class);
                    isAppInterrupt = true;
                }
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
                currentStartMission = 0;
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
                change_lens(communication, null);
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
                if (mVoiceBeanShottingContant == null) {
                    mVoiceBeanShottingContant = new WayPointsV2Bean.WayPointsBean.WayPointActionBean.VoiceBean();
                    mVoiceBeanShottingContant.setFlag("0");
                }
                webInitializationBean.setVoiceBean(mVoiceBeanShottingContant);
                communication.setResult(gson.toJson(webInitializationBean, WebInitializationBean.class));
                communication.setCode(200);
                communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                Log.d("飞机飞行流程", "获取初始值" + communication.toString());
                Toast.makeText(ConnectionActivity.this, "", Toast.LENGTH_SHORT).show();
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
                stopLiveShow();
                break;
            //重启推流
            case Constant.RESTART_LIVE:
                restartLiveShow(communication);
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
//                setNSCS();
                break;
            //设置坐标系
            case Constant.SET_RTK_NETWORK:
                //测试
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
            case Constant.GET_IS_FLYING:
                getIsFlying(communication);
                break;
            //判断RTK是否在用
            case Constant.ISRTKBEINGUSED:
                getRTKUseState(communication);
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
                break;
            case Constant.SEND_VOICE_COMMAND:
                sendTTS2Payload(communication);
                break;
            case Constant.SEND_VOICE_MP3:
                sendMP32Payload(communication);
                break;
            case Constant.END_VOICE:
                send(PagerUtils.getInstance().TTSSTOPINS, communication);
                break;
            case Constant.VOLUME_CONTROL://暂不可用
                send(PagerUtils.getInstance().TTSSTOPINS, communication);
                break;
        }
    }

    //获取飞行状态
    private void getIsFlying(Communication communication) {
        Aircraft aircraft = ApronApp.getAircraftInstance();
        if (aircraft == null || !aircraft.isConnected()) {
            communication.setResult("Disconnected");
            communication.setCode(-1);
            communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            NettyClient.getInstance().sendMessage(communication, null);

        } else {
            FlightController mFlightController = aircraft.getFlightController();
            if (mFlightController != null) {
                communication.setResult(mFlightController.getState().isFlying() ? "1" : "0");
                communication.setCode(200);
                communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                NettyClient.getInstance().sendMessage(communication, null);
            }
        }
    }

    //rtk实时状态
    private int isRTKBeingUsed = 0;
    //控制rtk每秒向服务器发送的次数
    private int sendRTKState2SocketTime = 0;

    //获取飞行状态
    private void getRTKUseState(Communication communication) {

        communication.setResult(isRTKBeingUsed + "");
        communication.setCode(200);
        communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
        NettyClient.getInstance().sendMessage(communication, null);
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
                    gimbal = ApronApp.getProductInstance().getGimbal();
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
                    gimbal = ((Aircraft) ApronApp.getProductInstance()).getGimbals().get(0);
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
                gimbal = ((Aircraft) ApronApp.getProductInstance()).getGimbals().get(0);
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
//        Gimbal gimbal = FPVDemoApplication.getProductInstance().getGimbal();
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
//        Gimbal gimbal = FPVDemoApplication.getProductInstance().getGimbal();
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

    String mCameraVideoStreamSource;

    //切换镜头
    private void change_lens(Communication communication, String cameraType) {
        String type = "";
        if (isCapture) {
            type = "2";
        } else if (cameraType == null) {
            type = communication.getPara().get(Constant.TYPE);
        } else if (communication == null) {
            type = cameraType;
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
            if (camera != null && camera.isMultiLensCameraSupported()) {
                //只支持H20T系列
                camera.setCameraVideoStreamSource(source, new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (djiError == null && communication == null) {
                            getMissionUpdateData(Constant.MISSION_UPDATE_MODE, communication);
                        } else if (isCapture) {//抓拍
                            camera_up_and_down_by_a(communication);
                        } else {
                            getMissionUpdateData(null, communication);
                        }
                    }
                });
            } else if (camera != null && !camera.isMultiLensCameraSupported()) {//不支持多视频源流
                if (isCapture) {//抓拍
                    camera_up_and_down_by_a(communication);
                } else {
                    CommonDjiCallback(null, communication);
                }
            } else {
                communication.setResult("型号不支持");
                communication.setCode(-1);
                communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                NettyClient.getInstance().sendMessage(communication, null);
            }
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
                        communication.setEquipmentId(ApronApp.EQUIPMENT_ID);
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
                    communication_laser_status.setEquipmentId(ApronApp.EQUIPMENT_ID);
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

    private boolean isINSPIRE2Product() {
        if (DJISDKManager.getInstance().getProduct() == null) {
            return false;
        }
        Model model = DJISDKManager.getInstance().getProduct().getModel();
        return model == Model.INSPIRE_2;
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
            Log.d("从前端接收到的数据变焦zoom", type);
            //设置类型不为空并且相机支持多视频流源
            if (!TextUtils.isEmpty(type) && camera.isMultiLensCameraSupported()) {
                camera.getLens(0).setHybridZoomFocalLength(getbigZoomValue(type), new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (isCapture) {
                            isCapture = false;
                        } else {

                        }
                        CommonDjiCallback(djiError, communication);
                        webInitializationBean.setHybridZoom(Integer.parseInt(type));
//                        tv_zoom.setText("变焦   " + Integer.parseInt(type) + "x");

                    }
                });
            } else if (!TextUtils.isEmpty(type) && camera.isHybridZoomSupported()) {
                camera.setHybridZoomFocalLength(Integer.parseInt(type), new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (isCapture) {
                            isCapture = false;
                        } else {

                        }
                        CommonDjiCallback(djiError, communication);
                        webInitializationBean.setHybridZoom(Integer.parseInt(type));
//                        tv_zoom.setText("变焦   " + Integer.parseInt(type) + "x");

                        Log.d("isHybridZoomSupported", djiError.getDescription());
                    }
                });

            } else if (!TextUtils.isEmpty(type) && camera.isTapZoomSupported()) {//光学变焦
                camera.setTapZoomMultiplier(Integer.parseInt(type), new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (isCapture) {
                            isCapture = false;
                        } else {

                        }
                        CommonDjiCallback(djiError, communication);
                        webInitializationBean.setHybridZoom(Integer.parseInt(type));
//                        tv_zoom.setText("变焦   " + Integer.parseInt(type) + "x");
                    }
                });
                Log.d("isTapZoomSupported", "true");
            } /*else if (!TextUtils.isEmpty(type) && camera.isOpticalZoomSupported()) {//设置焦距
                camera.setOpticalZoomFocalLength(117, new CommonCallbacks.CompletionCallback() {
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
                Log.d("isOpticalZoomSupported", "true");
            }*/ /*else if (!TextUtils.isEmpty(type) && camera.isDigitalZoomSupported()) {//数码变焦
                camera.setDigitalZoomFactor(Float.parseFloat("2.0"), new CommonCallbacks.CompletionCallback() {
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

                Log.d("isDigitalZoomSupported", "true");
            }*/

        }


    }

    /**
     * //修改变焦数据为从前端拿2-200自己计算然后放入官方的sdk
     *
     * @param smallZoomFromWeb
     * @return
     */
    private int getbigZoomValue(String smallZoomFromWeb) {
        int zoomLength = Integer.parseInt(smallZoomFromWeb);
        int bigZoom = (47549 - 317) / 199 * (zoomLength - 2) + 317;
        return bigZoom;
    }

    private int getSmallZoomValue(int bigZoomFromDJ) {

        int smallZoom = (bigZoomFromDJ - 317) / ((47549 - 317) / 199 + 2);
        return smallZoom;
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
                                        XcFileLog.getInstace().i("setMaxFlightRadiusLimit：",  maxFlightRadius);
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
        setRtkBean.setInfo(infoBean);
        settingValueBean.setRtkBean(setRtkBean);
        settingValueBean.setBatteryStateBean(batteryStateBean);
        if (communication == null) {
            communication = new Communication();
        }
        communication.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
        communication.setEquipmentId(ApronApp.EQUIPMENT_ID);
        communication.setMethod((Constant.GET_SETTING_DATA));
        communication.setCode(200);
        communication.setResult(gson.toJson(settingValueBean, SettingValueBean.class));
        NettyClient.getInstance().sendMessage(communication, null);
        Log.d("mg航点-EU", gson.toJson(settingValueBean, SettingValueBean.class));
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
        if (!TextUtils.isEmpty(type) && mFlightAssistant != null) {
            mFlightAssistant.setVisionAssistedPositioningEnabled(type.equals("1") ? true : false, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    visionAssistedPosition = type.equals("1") ? true : false;
                    CommonDjiCallback(djiError, communication);
                }
            });
        }
    }

    //精确着陆
    private void setPrecisionLand(Communication communication) {
        String type = communication.getPara().get(Constant.TYPE);
        if (!TextUtils.isEmpty(type) && mFlightAssistant != null) {
            mFlightAssistant.setPrecisionLandingEnabled(type.equals("1") ? true : false, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    precisionLand = type.equals("1") ? true : false;
                    CommonDjiCallback(djiError, communication);
                }
            });
        }

//        if (KeyManager.getInstance() != null) {
//            KeyManager.getInstance().setValue(flightControllerKey2, type.equals("1") ? true : false, new SetCallback() {
//                @Override
//                public void onSuccess() {
//                    communication.setResult("Success");
//                    communication.setCode(200);
//                    communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
//                    NettyClient.getInstance().sendMessage(communication, null);
//                }
//
//                @Override
//                public void onFailure(DJIError djiError) {
//                    communication.setResult(djiError.toString());
//                    communication.setCode(-1);
//                    communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
//                    NettyClient.getInstance().sendMessage(communication, null);
//                }
//            });
//        }
    }


    //向上刹停
    private void setUpwardsAvoidance(Communication communication) {
        String type = communication.getPara().get(Constant.TYPE);
        if (!TextUtils.isEmpty(type) && mFlightAssistant != null) {
            mFlightAssistant.setUpwardVisionObstacleAvoidanceEnabled(type.equals("1") ? true : false, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    CommonDjiCallback(djiError, communication);
                    if (djiError == null) {
                        upwardsAvoidance = type.equals("1") ? true : false;
                    }
                }
            });
        }

    }

    //下避障
    private void setLandingProtection(Communication communication) {
        String type = communication.getPara().get(Constant.TYPE);
        if (!TextUtils.isEmpty(type) && mFlightAssistant != null) {
            mFlightAssistant.setLandingProtectionEnabled(type.equals("1") ? true : false, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    CommonDjiCallback(djiError, communication);
                    if (djiError == null) {
                        landingProtection = type.equals("1") ? true : false;
                    }
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
        mFlightAssistant.setHorizontalVisionObstacleAvoidanceEnabled(type.equals("1") ? true : false, new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                if (djiError == null) {
                    activeObstacleAvoidance = type.equals("1") ? true : false;
                    CommonDjiCallback(djiError, communication);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            text_net_rtk_account_state.setText("设置四项：" + activeObstacleAvoidance);
                        }
                    });
                }
//
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
        if (ModuleVerificationUtil.isRtkAvailable()) {
            if (mRTK != null) {
                mRTK.setRtkEnabled(type.equals("1") ? true : false, new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        CommonDjiCallback(djiError, communication);
                    }
                });

            }
            //检测RKT开关
            mRTK.getRtkEnabled(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
                @Override
                public void onSuccess(Boolean aBoolean) {
                    setRtkBean.setRtkSwitch(aBoolean ? 1 : 0);
                }

                @Override
                public void onFailure(DJIError djiError) {
                    setRtkBean.setRtkSwitch(-1);
                }
            });
        } else {
            showToast("RTK不可用");
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
            communication_BS_info.setEquipmentId(ApronApp.EQUIPMENT_ID);
            communication_BS_info.setMethod((Constant.BS_INFO));
            communication_BS_info.setCode(200);
            communication_BS_info.setResult(gson.toJson(stringsBean));
            NettyClient.getInstance().sendMessage(communication_BS_info, null);
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
        }
    }

    //设置参考站源 D-RTK=BASE_STATION
    private void setRSS(Communication communication) {
        String type = communication.getPara().get(Constant.TYPE);
        setRtkBean.setServiceType(Integer.parseInt(type));
        if (mRTK != null && !TextUtils.isEmpty(type)) {
            mRTK.setReferenceStationSource(ReferenceStationSource.find(Integer.parseInt(type)), new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    CommonDjiCallback(djiError, communication);
                }
            });
        }
    }


    //设置自定义网络rtk
//    https://bbs.dji.com/thread-247389-1-1.html
    private void setRTKNetwork(Communication communication) {
        RTKNetworkServiceProvider provider = DJISDKManager.getInstance().getRTKNetworkServiceProvider();
        if (ModuleVerificationUtil.isNetRtkAvailable()) {
            //设置网络RTK账号
            infoBean.setUsername(communication.getPara().get("username"));
            infoBean.setPassword(communication.getPara().get("password"));
            infoBean.setMountPoint(communication.getPara().get("mountPoint"));
            infoBean.setIp(communication.getPara().get("ip"));
            infoBean.setPort(Integer.parseInt(communication.getPara().get("port")));

            NetworkServiceSettings.Builder builder = new NetworkServiceSettings.Builder()
                    .userName(infoBean.getUsername()).password(infoBean.getPassword()).ip(infoBean.getIp())
                    .mountPoint(infoBean.getMountPoint()).port(infoBean.getPort());
            provider.setCustomNetworkSettings(builder.build());

            //启动网络RTK
            provider.startNetworkService(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    CommonDjiCallback(djiError, communication);
                }
            });
            //账号连接状态
            provider.addNetworkServiceStateCallback(new NetworkServiceState.Callback() {
                @Override
                public void onNetworkServiceStateUpdate(NetworkServiceState networkServiceState) {
                    String description5 = String.valueOf(networkServiceState.getChannelState());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            text_net_rtk_account_state.setText("RTK账号状态:" + description5);
                        }
                    });
                }
            });

        }
    }

    double latitude;

    //监听RTK状态
    private void addRTKStatus() {
        if (ModuleVerificationUtil.isRtkAvailable()) {

            mRTK = ((Aircraft) ApronApp.getProductInstance()).getFlightController().getRTK();
            if (mRTK != null) {
                mRTK.addReferenceStationSourceCallback(new ReferenceStationSource.Callback() {
                    @Override
                    public void onReferenceStationSourceUpdate(ReferenceStationSource referenceStationSource) {
                        setRtkBean.setServiceType(referenceStationSource.value);
                    }
                });
                //检测RKT开关
                mRTK.getRtkEnabled(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        setRtkBean.setRtkSwitch(aBoolean ? 1 : 0);
                    }

                    @Override
                    public void onFailure(DJIError djiError) {
                        setRtkBean.setRtkSwitch(-1);
                    }
                });
                if (isM300Product()) {
                    //D-RTK实时返回信息
                    mRTK.setRtkConnectionStateWithBaseStationCallback(new RTK.RTKConnectionStateWithBaseStationReferenceSourceCallback() {
                        @Override
                        public void onUpdate(RTKConnectionStateWithBaseStationReferenceSource rtkConnectionStateWithBaseStationReferenceSource, RTKBaseStationInformation rtkBaseStationInformation) {
                            infoBean.setRtkBaseStationInformation(rtkBaseStationInformation);
                            setRtkBean.setInfo(infoBean);
                        }
                    });
                }

                //监听RTK状态
                mRTK.setStateCallback(new RTKState.Callback() {
                    @Override
                    public void onUpdate(RTKState rtkState) {
                        sendRTKState2SocketTime++;
                        if (sendRTKState2SocketTime == 12) {
                            sendRTKState2SocketTime = 0;
                            isRTKBeingUsed = rtkState.isRTKBeingUsed() ? 1 : 0;
                            infoBean.setRtkState(rtkState);
                            if (communication_rtkState == null) {
                                communication_rtkState = new Communication();
                            }
                            communication_rtkState.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                            communication_rtkState.setEquipmentId(ApronApp.EQUIPMENT_ID);
                            communication_rtkState.setMethod((Constant.RTKBean));
                            communication_rtkState.setCode(1);
                            setRtkBean.setInfo(infoBean);
                            communication_rtkState.setResult(gson.toJson(setRtkBean, SettingValueBean.NetRTKBean.class));
                            NettyClient.getInstance().sendMessage(communication_rtkState, null);
                        }
                    }
                });
            }


        }
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
            communication_error_log.setEquipmentId(ApronApp.EQUIPMENT_ID);
            communication_error_log.setMethod((Constant.DIAGNOSTICS));
            communication_error_log.setResult(gson.toJson(stringsBean));
            NettyClient.getInstance().sendMessage(communication_error_log, null);
        }


    }


    /**
     * 航线飞行过程中triggers事件监听
     *
     * @param timelineElement
     * @param timelineEvent
     * @param djiError
     */
    @Override
    public void onEvent(TimelineElement timelineElement, TimelineEvent timelineEvent, DJIError djiError) {
        Log.d("triggers事件监听", "timelineElement" + (timelineElement != null ? timelineElement.getTriggers().size() : "null") + "timelineEvent" + (timelineEvent != null ? timelineEvent.name() : "null"));
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
        Log.d("测试左旋右旋", communication != null ? communication.toString() : "null");
        try {

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
        } catch (Exception e) {
            e.printStackTrace();
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

    int currentStartMission = 0;

    //开始航点自动飞行
    private void startWaypointMission(Communication communication) {
        String currentState = getWaypointMissionOperator().getCurrentState().toString();
        Log.d("飞机飞行流程", currentState);
        XcFileLog.getInstace().i("飞机飞行流程", currentState);
        getWaypointMissionOperator().startMission(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError error) {
                if (error != null && currentStartMission < 5) {
                    currentStartMission += 1;
                    startWaypointMission(communication);
                } else if (currentStartMission >= 5) {
                    Log.d("飞机飞行流程", "startWaypointMission报错");
                    XcFileLog.getInstace().i("飞机飞行流程", "startWaypointMission报错");
                    communication.setResult("startMission报错:" + error.getDescription() + "currentState:" + currentState);
                    communication.setCode(-1);
                    communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                    NettyClient.getInstance().sendMessage(communication, null);
                } else {
                    Log.d("飞机飞行流程", "startWaypointMission成功");
                    XcFileLog.getInstace().i("飞机飞行流程", "startWaypointMission成功");
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

    Communication mUpLoadActionCommunication;

    //航线规划v2
    private void waypoint_plan_V2(Communication communication) {
        MissionControl.getInstance().removeAllListeners();
        waypointV2MissionOperator = MissionControl.getInstance().getWaypointMissionV2Operator();
        Log.d("飞机飞行流程", "获取数据" + communication.getPara().get(Constant.WAY_POINTS));
        XcFileLog.getInstace().i("飞机飞行流程", "获取数据" + communication.getPara().get(Constant.WAY_POINTS));
        this.mUpLoadActionCommunication = communication;
        communication_upload_mission = communication;
        setWayV2UpListener(null, communication);
        Log.d("飞机飞行流程", "开始加载起飞方法loadmission");
        XcFileLog.getInstace().i("飞机飞行流程", "开始加载起飞方法loadmission");
        //loadMission
        mLoadMissionCount = 0;
        loadMission(communication);
    }

    int mLoadMissionCount;

    private void loadMission(Communication communication) {

        WaypointV2MissionState state = null;
        int i = 0;
        while (++i < 15) {
            state = waypointV2MissionOperator.getCurrentState();

            if (state.equals(WaypointV2MissionState.READY_TO_UPLOAD) || state.equals(WaypointV2MissionState.READY_TO_EXECUTE)) {
                break;
            }
            XcFileLog.getInstace().i("航线规划", "循环回调，当前状态为：" + state);

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (Exception e) {
            }

        }
        if (i == 15) {
            XcFileLog.getInstace().i("航线规划", "等待了15秒，state 不为 READY_TO_UPLOAD 或 READY_TO_EXECUTE , 当前状态为 ：" + state);
        } else {
            XcFileLog.getInstace().i("航线规划", "第" + i + "秒，获取到state正常状态成功，状态为" + state);
        }


        if (waypointV2MissionOperator.getCurrentState().equals(WaypointV2MissionState.READY_TO_UPLOAD) || waypointV2MissionOperator.getCurrentState().equals(WaypointV2MissionState.READY_TO_EXECUTE)) {
            XcFileLog.getInstace().i("飞机飞行流程", mLoadMissionCount + "开始加载起飞方法loadmission");
            Log.d("飞机飞行流程", mLoadMissionCount + "开始加载起飞方法loadmission");
            waypointV2MissionOperator.loadMission(createWaypointMission(communication), new CommonCallbacks.CompletionCallback<DJIWaypointV2Error>() {
                @Override
                public void onResult(DJIWaypointV2Error djiWaypointV2Error) {
                    XcFileLog.getInstace().i("航线规划", "loadMission回调，当前状态为：" + waypointV2MissionOperator.getCurrentState());
                    if (djiWaypointV2Error == null) {
                        Toast.makeText(ConnectionActivity.this, "Mission is loaded successfully", Toast.LENGTH_SHORT).show();
                        waypointV2MissionOperator.uploadMission(new CommonCallbacks.CompletionCallback<DJIWaypointV2Error>() {
                            @Override
                            public void onResult(DJIWaypointV2Error djiWaypointV2Error) {
                                XcFileLog.getInstace().i("航线规划", "uploadMission回调，当前状态为：" + waypointV2MissionOperator.getCurrentState());
                                if (djiWaypointV2Error != null) {
                                    Log.d("飞机飞行流程", "开始加载起飞方法loadmissio2失败" + djiWaypointV2Error.getDescription());
                                    XcFileLog.getInstace().i("飞机飞行流程", "开始加载起飞方法loadmissio2失败" + djiWaypointV2Error.getDescription());

                                    Toast.makeText(ConnectionActivity.this, djiWaypointV2Error.getDescription(), Toast.LENGTH_SHORT).show();
                                    communication_upload_mission.setResult(djiWaypointV2Error.getDescription());
                                    communication_upload_mission.setCode(-1);
                                    communication_upload_mission.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                                    NettyClient.getInstance().sendMessage(communication_upload_mission, null);
                                } else {
                                    XcFileLog.getInstace().i("飞机飞行流程", "开始加载起飞方法loadmissio2成功");
                                }
                            }
                        });
                    } else {
                        Log.d("飞机飞行流程", "开始加载起飞方法loadmissio1失败" + djiWaypointV2Error.getDescription());
                        Toast.makeText(ConnectionActivity.this, djiWaypointV2Error.getDescription(), Toast.LENGTH_SHORT).show();
                        XcFileLog.getInstace().i("飞机飞行流程", "开始加载起飞方法loadmissio1失败" + djiWaypointV2Error.getDescription());
                        communication_upload_mission.setResult(djiWaypointV2Error.getDescription());
                        communication_upload_mission.setCode(-1);
                        communication_upload_mission.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                        NettyClient.getInstance().sendMessage(communication_upload_mission, null);
                    }
                }
            });
        } else if (mLoadMissionCount < 3) {
            mLoadMissionCount++;
            XcFileLog.getInstace().i("飞机飞行流程", mLoadMissionCount + "开始加载起飞方法loadmission");
            Log.d("飞机飞行流程", mLoadMissionCount + "开始加载起飞方法loadmission");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    /**
                     *要执行的操作
                     */
                    loadMission(communication);
                }
            }, 3000);//3秒后执行Runnable中的run方法
        } else {
            XcFileLog.getInstace().i("飞机飞行流程", "The mission can be loaded only when the operator state is READY_TO_UPLOAD or READY_TO_EXECUTE");
            Toast.makeText(ConnectionActivity.this, "The mission can be loaded only when the operator state is READY_TO_UPLOAD or READY_TO_EXECUTE", Toast.LENGTH_SHORT).show();
            communication_upload_mission.setResult("The mission can be loaded only when the operator state is READY_TO_UPLOAD or READY_TO_EXECUTE");
            communication_upload_mission.setCode(-1);
            communication_upload_mission.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            NettyClient.getInstance().sendMessage(communication_upload_mission, null);
        }
    }

    int currentPoint;

    /**
     * @param wayPointsV2Bean 为空的时候代表第一次正常飞行，不为空代表闪退之后数据处理需要的流程
     * @param communication   跟第一个参数相反
     */
    private void setWayV2UpListener(WayPointsV2Bean wayPointsV2Bean, Communication communication) {
        if (wayPointsV2Bean != null) {
            //恢复航线重新部署数据
            XcFileLog.getInstace().i("飞机飞行流程航线因为闪退重新部署", "wayPointsV2Bean ==" + wayPointsV2Bean.toString());
            uploadWaypointAction(wayPointsV2Bean, null);
        }
        waypointV2MissionOperatorListener = new WaypointV2MissionOperatorListener() {
            @Override
            public void onDownloadUpdate(WaypointV2MissionDownloadEvent waypointV2MissionDownloadEvent) {
            }

            @Override
            public void onUploadUpdate(WaypointV2MissionUploadEvent waypointV2MissionUploadEvent) {
                if (waypointV2MissionUploadEvent != null && waypointV2MissionUploadEvent.getError() != null) {
                    XcFileLog.getInstace().i("飞机飞行流程开始加载起飞方法loadmissio1失败", waypointV2MissionUploadEvent.getError().getDescription());

                    Log.d("飞机飞行流程", "开始加载起飞方法loadmissio1失败" + waypointV2MissionUploadEvent.getError().getDescription());
                    Toast.makeText(ConnectionActivity.this, waypointV2MissionUploadEvent.getError().getDescription(), Toast.LENGTH_SHORT).show();
                    communication_upload_mission.setResult(waypointV2MissionUploadEvent.getError().getDescription());
                    communication_upload_mission.setCode(-1);
                    communication_upload_mission.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                    NettyClient.getInstance().sendMessage(communication_upload_mission, null);
                }

                Log.d("wayPointV2Upload", "PreviousState=" + waypointV2MissionUploadEvent.getPreviousState()
                        + "\nCurrentState=" + waypointV2MissionUploadEvent.getCurrentState());
                /*if (waypointV2MissionUploadEvent.getPreviousState() == WaypointV2MissionState.UPLOADING
                        && waypointV2MissionUploadEvent.getCurrentState() == WaypointV2MissionState.READY_TO_EXECUTE) {
                    Log.d("航点-EU", "上传航线成功");
                    Log.d("飞机飞行流程", "上传航线成功");
                    communication_upload_mission.setEquipmentId(MApplication.EQUIPMENT_ID);
                    communication_upload_mission.setResult("Mission is uploaded successfully");
                    communication_upload_mission.setCode(200);
                    communication_upload_mission.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                    NettyClient.getInstance().sendMessage(communication_upload_mission, null);
                    Log.d("飞机飞行流程", "上传航点动作成功");

                }*/
            }

            @Override
            public void onExecutionUpdate(WaypointV2MissionExecutionEvent waypointV2MissionExecutionEvent) {
                if (waypointV2MissionExecutionEvent != null && waypointV2MissionExecutionEvent.getProgress() != null) {
                    targetWaypointIndex = waypointV2MissionExecutionEvent.getProgress().getTargetWaypointIndex();
                    XcFileLog.getInstace().i("飞机飞行流程到达航点当前航线记录数据", "当前航点" + targetWaypointIndex + "mMissionPointBean==" + (mMissionPointBean != null ? mMissionPointBean.toString() : "null")
                            + "\n" + "mVoiceBeanShottingContant==" + (mVoiceBeanShottingContant != null ? mVoiceBeanShottingContant.toString() : "null") + "\n" + "mUpdateActionToWebBeans ==" + (mUpdateActionToWebBeans != null ? mUpdateActionToWebBeans.toString() : "null"));
                    Log.d("循环之前航点上传-EU",
                            waypointV2MissionExecutionEvent.getProgress().isWaypointReached() + "\n" + targetWaypointIndex
                                    + "\n" + "mMissionPointBean==" + (mMissionPointBean != null ? mMissionPointBean.toString() : "null")
                                    + "\n" + (mVoiceBeanShottingContant != null ? mVoiceBeanShottingContant.toString() : "null") + "\n" + "mUpdateActionToWebBeans ==" + (mUpdateActionToWebBeans != null ? mUpdateActionToWebBeans.toString() : "null"));
                    for (int i = 0; i < mMissionPointBeans.size(); i++) {
                        mMissionPointBean = mMissionPointBeans.get(i);
                        if (targetWaypointIndex == mMissionPointBean.getPointIndex() && "9".equals(mMissionPointBean.getShoutingType())) {
                            mVoiceBeanShottingContant = mMissionPointBean.getVoiceBean();
                            if (mVoiceBeanShottingContant == null) {
                                mVoiceBeanShottingContant = new WayPointsV2Bean.WayPointsBean.WayPointActionBean.VoiceBean();
                            }
                            mVoiceBeanShottingContant.setFlag("0");
                            missionSendStop(PagerUtils.getInstance().TTSSTOPINS, mMissionPointBean);
                            mMissionPointBeans.remove(i);
                            Log.d("飞机飞行流程", "当前航点" + targetWaypointIndex + mMissionPointBeans.toString() + "\n" + mMissionPointBean != null ? mMissionPointBean.toString() : "Null");
                            break;
                        } else if (targetWaypointIndex == mMissionPointBean.getPointIndex() && "8".equals(mMissionPointBean.getShoutingType())) {
                            mVoiceBeanShottingContant = mMissionPointBean.getVoiceBean();
                            if (mVoiceBeanShottingContant == null) {
                                mVoiceBeanShottingContant = new WayPointsV2Bean.WayPointsBean.WayPointActionBean.VoiceBean();
                            }
                            mVoiceBeanShottingContant.setFlag("1");
                            wayPointSendTTS2Payload(mMissionPointBean.getVoiceBean());
                            mMissionPointBeans.remove(i);
                            Log.d("飞机飞行流程", "当前航点" + targetWaypointIndex + mMissionPointBeans.toString() + "\n" + mMissionPointBean != null ? mMissionPointBean.toString() : "Null");
                            break;
                        } else if (targetWaypointIndex == mMissionPointBean.getPointIndex() && i == mMissionPointBeans.size() - 1) {
                            send(PagerUtils.getInstance().TTSSTOPINS);
                            if (communication_onExecutionFinish == null) {
                                communication_onExecutionFinish = new Communication();
                            }
                            communication_onExecutionFinish.setEquipmentId(ApronApp.EQUIPMENT_ID);
                            communication_onExecutionFinish.setCode(200);
                            communication_onExecutionFinish.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                            communication_onExecutionFinish.setResult(gson.toJson(mMissionPointBean));
                            communication_onExecutionFinish.setMethod(Constant.MISSIONWAYPOINT);
                            NettyClient.getInstance().sendMessage(communication_onExecutionFinish, null);
                            mMissionPointBeans.remove(i);
                            Log.d("飞机飞行流程", "当前航点" + targetWaypointIndex + mMissionPointBeans.toString() + "\n" + mMissionPointBean != null ? mMissionPointBean.toString() : "Null");
                            break;
                        } else if (targetWaypointIndex == mMissionPointBean.getPointIndex()) {
                            if (communication_onExecutionFinish == null) {
                                communication_onExecutionFinish = new Communication();
                            }
                            communication_onExecutionFinish.setEquipmentId(ApronApp.EQUIPMENT_ID);
                            communication_onExecutionFinish.setCode(200);
                            communication_onExecutionFinish.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                            communication_onExecutionFinish.setResult(gson.toJson(mMissionPointBean));
                            communication_onExecutionFinish.setMethod(Constant.MISSIONWAYPOINT);
                            NettyClient.getInstance().sendMessage(communication_onExecutionFinish, null);
                            mMissionPointBeans.remove(i);
                            Log.d("飞机飞行流程", "当前航点" + targetWaypointIndex + mMissionPointBeans.toString() + "\n" + mMissionPointBean != null ? mMissionPointBean.toString() : "Null");
                            break;
                        }
                    }
                    for (int i = 0; i < mUpdateActionToWebBeans.size() && waypointV2MissionExecutionEvent.getProgress().isWaypointReached(); i++) {
                        int intIndex = mUpdateActionToWebBeans.get(i).getPointIndex();
                        currentPoint = targetWaypointIndex;
                        Log.d("当前到达的航点上传-EU", intIndex + "\n" + targetWaypointIndex + "" + waypointV2MissionExecutionEvent.getProgress().isWaypointReached() + System.currentTimeMillis());
                        XcFileLog.getInstace().i("当前到达的航点上传-EU", intIndex + "\n" + targetWaypointIndex + "" + waypointV2MissionExecutionEvent.getProgress().isWaypointReached() + System.currentTimeMillis());
                        //如果航点动作里面有变焦则将当前模式变为变焦模式
                        if (intIndex == targetWaypointIndex && mUpdateActionToWebBeans.get(i) != null && mUpdateActionToWebBeans.get(i).getActionIndex() != null && mUpdateActionToWebBeans.get(i).getActionType().contains("4")) {
                            Log.d("切换变焦动作上传-EU", "onExecutionUpdate");
                            change_lens(null, "2");
                        }
                        if (intIndex == targetWaypointIndex && mUpdateActionToWebBeans.get(i) != null && mUpdateActionToWebBeans.get(i).getActionIndex() != null && mUpdateActionToWebBeans.get(i).getActionType().contains("0")) {
                            if (communication_onExecutionFinish == null) {
                                communication_onExecutionFinish = new Communication();
                            }
                            communication_onExecutionFinish.setEquipmentId(ApronApp.EQUIPMENT_ID);
                            communication_onExecutionFinish.setCode(200);
                            communication_onExecutionFinish.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                            communication_onExecutionFinish.setResult("{\"result\":\"" + intIndex + "--" + mUpdateActionToWebBeans.get(i).getWaitTime() + "\"}");
                            communication_onExecutionFinish.setMethod("hoverPhoto");
                            NettyClient.getInstance().sendMessage(communication_onExecutionFinish, null);
                            mUpdateActionToWebBeans.get(i).setOld(true);
                            return;
                        }
                    }
                }
            }

            @Override
            public void onExecutionStart() {
                Log.d("飞机飞行流程", "onExecutionStart");
            }

            @Override
            public void onExecutionFinish(DJIWaypointV2Error djiWaypointV2Error) {

                if (communication_onExecutionFinish == null) {
                    communication_onExecutionFinish = new Communication();
                }
                /**
                 * 航点结束时停止喊话
                 */
                send(PagerUtils.getInstance().TTSSTOPINS);
                XcFileLog.getInstace().i("飞机飞行流程结束", "发送关闭喊话器结束");
                Log.d("飞机飞行流程", "飞机航线完成");
                communication_onExecutionFinish.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                communication_onExecutionFinish.setEquipmentId(ApronApp.EQUIPMENT_ID);
                communication_onExecutionFinish.setMethod((Constant.ON_EXECUTION_FINISH));
                communication_onExecutionFinish.setResult("2");
                NettyClient.getInstance().sendMessage(communication_onExecutionFinish, null);
            }

            @Override
            public void onExecutionStopped() {
                Log.d("飞机飞行流程", "onExecutionStopped");
            }
        };

        waypointV2ActionListener = new
                WaypointV2ActionListener() {
                    @Override
                    public void onDownloadUpdate(ActionDownloadEvent actionDownloadEvent) {
                    }

                    @Override
                    public void onUploadUpdate(ActionUploadEvent actionUploadEvent) {
                        XcFileLog.getInstace().i("航点动作状态监听",
                                "PreviousState:" + actionUploadEvent.getPreviousState() +
                                        "CurrentState:" + actionUploadEvent.getCurrentState());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                text_net_rtk_state.setText("PreviousState:" + actionUploadEvent.getPreviousState());
                                text_net_rtk_account_state.setText("CurrentState:" + actionUploadEvent.getCurrentState());
                            }
                        });
                        if (actionUploadEvent.getCurrentState().equals(ActionState.READY_TO_UPLOAD)) {
                            if (wayPointsV2Bean != null) {

                            } else {
                                XcFileLog.getInstace().i("飞机飞行流程航线正常流程", "第一次正常上传航线走的方法");
                                //正常流程部署数据
                                uploadWaypointAction(null, communication);
                            }
                        }
                        //上传航线成功
                        if (actionUploadEvent.getPreviousState() == ActionState.UPLOADING
                                && actionUploadEvent.getCurrentState() == ActionState.READY_TO_EXECUTE) {
                            communication_upload_mission.setResult("Mission is uploaded successfully");
                            communication_upload_mission.setCode(200);
                            communication_upload_mission.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                            NettyClient.getInstance().sendMessage(communication_upload_mission, null);
                            XcFileLog.getInstace().i("飞机飞行流程", "上传航点动作成功有航点动作");
                        }
                    }

                    @Override
                    public void onExecutionUpdate(ActionExecutionEvent actionExecutionEvent) {
                        if (actionExecutionEvent != null) {
                            Log.d("飞机飞行流程", "当前状态" + actionExecutionEvent.getCurrentState() + "之前状态" + actionExecutionEvent.getPreviousState() + "\n" + (actionExecutionEvent.getProgress() == null ? "null" : actionExecutionEvent.getProgress().getExecutionActionID()));
                        }
                    }

                    @Override
                    public void onExecutionStart(int i) {
                        Log.d("上传-EU", "onExecutionStart" + i);
                    }

                    @Override
                    public void onExecutionFinish(int i, DJIWaypointV2Error djiWaypointV2Error) {
                        Log.d("飞机飞行流程", "onExecutionFinish" + i);
                        XcFileLog.getInstace().i("飞机飞行流程", "onExecutionFinish" + i + "\n" + "mUpdateActionToWebBeans==" + (mUpdateActionToWebBeans != null ? mUpdateActionToWebBeans.toString() : "null"));
                        Log.d("飞机飞行流程", "mUpdateActionToWebBeans==" + (mUpdateActionToWebBeans != null ? mUpdateActionToWebBeans.toString() : "null"));
                        for (int j = 0; j < mUpdateActionToWebBeans.size(); j++) {
                            Log.d("1完成航点动作上传-EU", "onExecutionFinish" + mUpdateActionToWebBeans.get(j).toString());
                            int pointIndex = mUpdateActionToWebBeans.get(j).getPointIndex();
                            List<String> actionIndexList = mUpdateActionToWebBeans.get(j).getActionIndex();
                            if (currentPoint == pointIndex && actionIndexList != null) {
                                for (int k = 0; k < actionIndexList.size(); k++) {
                                    int actionIndex = Integer.parseInt(actionIndexList.get(k));
                                    XcFileLog.getInstace().i("飞机飞行流程", "当前到达的航点并执行的动作" + "currentPoint==" + currentPoint
                                            + "mUpdateActionToWebBeans" + mUpdateActionToWebBeans.get(j).toString());
                                    if (i == actionIndex && waypointV2ActionList != null && waypointV2ActionList.size() > actionIndex && waypointV2ActionList.get(actionIndex - 1) != null
                                            && waypointV2ActionList.get(actionIndex - 1).getActuator() != null && waypointV2ActionList.get(actionIndex - 1).getActuator().getGimbalActuatorParam() != null) {
                                        Log.d("飞机飞行流程", "当前航点动作的数据" + waypointV2ActionList.get(actionIndex - 1).getActuator().getGimbalActuatorParam().getRotation().getPitch()
                                                + "\n" + "TriggerType.name===" + waypointV2ActionList.get(actionIndex - 1).getTrigger().getTriggerType().name());
                                        XcFileLog.getInstace().i("飞机飞行流程", "当前航点动作的数据" + waypointV2ActionList.get(actionIndex - 1).getActuator().getGimbalActuatorParam().getRotation().getPitch()
                                                + "\n" + "TriggerType.name===" + waypointV2ActionList.get(actionIndex - 1).getTrigger().getTriggerType().name());
                                    }
                                    if (i == actionIndex && k == actionIndexList.size() - 2) {
                                        Log.d("完成航点动作上传-EU", "onExecutionFinish" + i);
                                        //当前航点中航点动作已经执行完毕只剩下悬停时间结束之后可以继续飞行
                                        getMissionUpdateData(Constant.MISSION_UPDATE_MODE, communication);
                                    }

                                    if (i == actionIndex && k == actionIndexList.size() - 1) {
                                        //张闯要求屏蔽
//                                        change_lens(null, "1");
                                        Log.d("切换广角动作上传-EU", "onExecutionFinish" + i + j);
                                        mUpdateActionToWebBeans.remove(j);
                                        return;
                                    }
                                }
                            }
                        }
                    }
                };

        if (wayPointsV2Bean != null && MissionControl.getInstance() != null) {
            waypointV2MissionOperator = MissionControl.getInstance().getWaypointMissionV2Operator();
            XcFileLog.getInstace().i("飞机飞行流程", "通过MissionControl获取waypointV2MissionOperator");
            Log.d("飞机飞行流程", "通过MissionControl获取waypointV2MissionOperator" + waypointV2MissionOperator);
        }
        /*if (DJISDKManager.getInstance() != null && DJISDKManager.getInstance().getMissionControl() != null) {
            waypointV2MissionOperator = DJISDKManager.getInstance().getMissionControl().getWaypointMissionV2Operator();
            XcFileLog.getInstace().i("飞机飞行流程", "通过DJISDKManager获取waypointV2MissionOperator");
            Log.d("飞机飞行流程", "通过DJISDKManager获取waypointV2MissionOperator" + waypointV2MissionOperator);
        }*/
        if (waypointV2MissionOperator != null) {
            try {
                waypointV2MissionOperator.addWaypointEventListener(waypointV2MissionOperatorListener);
                waypointV2MissionOperator.addActionListener(waypointV2ActionListener);
            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                XcFileLog.getInstace().i("飞机飞行流程添加航线监听错误原因", sw.toString());
                Log.d("飞机飞行流程添加航线监听错误原因", sw.toString());

            }

        }
        XcFileLog.getInstace().i("飞机飞行流程添加航线监听waypointV2MissionOperator==", waypointV2MissionOperator == null ? "null" : "不为空");
    }

    private void missionSendStop(byte[] bytes, MissionPointBean missionPointBean) {
        if (ApronApp.getProductInstance() != null && ApronApp.getProductInstance().getPayload() != null) {
            Payload payload = ApronApp.getProductInstance().getPayload();
            payload.sendDataToPayload(bytes, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (communication_onExecutionFinish == null) {
                        communication_onExecutionFinish = new Communication();
                    }
                    communication_onExecutionFinish.setEquipmentId(ApronApp.EQUIPMENT_ID);
                    communication_onExecutionFinish.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                    communication_onExecutionFinish.setMethod(Constant.MISSIONWAYPOINT);
                    if (djiError != null) {
                        showToast("Error:" + djiError.getErrorCode());
                        communication_onExecutionFinish.setCode(-1);
                        communication_onExecutionFinish.setResult("{\"result\":\"" + djiError.getDescription() + "\"}");
                        Log.d("飞机飞行流程", "航线喊话停止传给接口的值" + djiError.getDescription());

                    } else {
                        communication_onExecutionFinish.setCode(200);
                        communication_onExecutionFinish.setResult(gson.toJson(missionPointBean));
                        Log.d("飞机飞行流程", "航线喊话停止传给接口的值" + gson.toJson(missionPointBean));
                    }
                    mMissionPointBean.setShoutingType("9");
                    NettyClient.getInstance().sendMessage(communication_onExecutionFinish, null);
                }
            });
        } else {
            showToast("未检测到payload设备");
        }
    }

    private void getMissionUpdateData(String method, Communication missionUpdateComm) {
        if (missionUpdateComm == null) {
            missionUpdateComm = new Communication();
        }
        WebInitializationBean missionUpdateBean = new WebInitializationBean();
        if (camera != null && camera.getLens(0) != null) {
            // 获取当前变焦焦距
            camera.getLens(0).getHybridZoomFocalLength(new CommonCallbacks.CompletionCallbackWith<Integer>() {
                @Override
                public void onSuccess(Integer integer) {
                    Log.d("飞机飞行流程焦距上传-EU", integer + "");
                    XcFileLog.getInstace().i("飞机飞行流程焦距上传-EU", integer + "");
                    missionUpdateBean.setHybridZoom(getSmallZoomValue(integer));
                }

                @Override
                public void onFailure(DJIError djiError) {
//                    Log.d("HHHHHcurr", djiError.toString());
                }
            });
            if (VideoFeeder.getInstance() != null && VideoFeeder.getInstance().getPrimaryVideoFeed() != null && VideoFeeder.getInstance().getPrimaryVideoFeed().getVideoSource() != null) {
                String cvs = VideoFeeder.getInstance().getPrimaryVideoFeed().getVideoSource().value() + "";
                if (cvs.equals("5")) {
                    currentVideoSource = "1";//FPV
                } else {
                    currentVideoSource = "0";//相机
                }
                missionUpdateBean.setCurrentVideoSource(currentVideoSource);
            }
            camera.getLens(2).getThermalDigitalZoomFactor(new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.ThermalDigitalZoomFactor>() {
                @Override
                public void onSuccess(SettingsDefinitions.ThermalDigitalZoomFactor thermalDigitalZoomFactor) {
                    Log.d("飞机飞行流程红外焦距上传-EU", thermalDigitalZoomFactor.value() + "");
                    XcFileLog.getInstace().i("飞机飞行流程红外焦距上传-EU", thermalDigitalZoomFactor.value() + "");
                    missionUpdateBean.setThermalDigitalZoom(thermalDigitalZoomFactor.value() + "");
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            });

            Communication finalMissionUpdateComm = missionUpdateComm;
            camera.getCameraVideoStreamSource(new CommonCallbacks.CompletionCallbackWith<CameraVideoStreamSource>() {
                @Override
                public void onSuccess(CameraVideoStreamSource cameraVideoStreamSource) {
                    if (cameraVideoStreamSource != null) {
                        mCameraVideoStreamSource = cameraVideoStreamSource.value() + "";
                        missionUpdateBean.setCurrentLens(cameraVideoStreamSource.value() + "");
                        Log.d("模式上传-EU", cameraVideoStreamSource.value() + "");
                        Log.d("变焦完成数据上传-EU", gson.toJson(missionUpdateBean, WebInitializationBean.class));
                        if (method == null) {
                            Log.d("飞机飞行流程", cameraVideoStreamSource.value() + "");
                            finalMissionUpdateComm.setResult(gson.toJson(missionUpdateBean, WebInitializationBean.class));
                            finalMissionUpdateComm.setCode(200);
                            finalMissionUpdateComm.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                            NettyClient.getInstance().sendMessage(finalMissionUpdateComm, null);
                            Log.d("飞机飞行流程", finalMissionUpdateComm.toString() + gson.toJson(missionUpdateBean, WebInitializationBean.class) + "");

                        } else {
                            finalMissionUpdateComm.setResult(gson.toJson(missionUpdateBean, WebInitializationBean.class));
                            finalMissionUpdateComm.setCode(200);
                            finalMissionUpdateComm.setMethod(method);
                            finalMissionUpdateComm.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                            NettyClient.getInstance().sendMessage(finalMissionUpdateComm, null);
                        }
                    }
                }

                @Override
                public void onFailure(DJIError djiError) {
                }
            });
        } else {
            missionUpdateComm.setResult("当前设备不支持");
            missionUpdateComm.setCode(-1);
            missionUpdateComm.setMethod(method);
            missionUpdateComm.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            NettyClient.getInstance().sendMessage(missionUpdateComm, null);
        }

    }

    //为悬停时候添加判断的集合
    //更新航点动作的时候需要上传web所需工具集合
    List<UpdateActionToWebBean> mUpdateActionToWebBeans = new ArrayList<>();
    List<MissionPointBean> mMissionPointBeans = new ArrayList<>();

    private void uploadWaypointAction(WayPointsV2Bean wayPointsV2Bean, Communication communication) {
        XcFileLog.getInstace().i("飞机飞行流程上传航线", "wayPointsV2Bean==" + (wayPointsV2Bean != null ? wayPointsV2Bean.toString() : "null") + "\n" + "communication=" + (communication != null ? communication.toString() : "null"));
        WayPointsV2Bean.WayPointsBean.WayPointActionBean.VoiceBean voiceBeanShottingContant = null;
//        Toast.makeText(this, "获取netty推过来的数据" + new Gson().toJson(communication), Toast.LENGTH_SHORT).show();
        if (this.mUpLoadActionCommunication != null && this.mUpLoadActionCommunication.getPara() != null) {
            mWayPointActionV2 = this.mUpLoadActionCommunication.getPara().get(Constant.WAY_POINTS);
        }
        Log.d("飞机飞行流程上传航点动作-EU", "仅仅测试" + mWayPointActionV2);
        List<WayPointsV2Bean.WayPointsBean> myWayPointActionList;
        if (!TextUtils.isEmpty(mWayPointActionV2) || wayPointsV2Bean != null) {
            try {
                Log.d("获取的航点信息", mWayPointActionV2);
                Log.d("测试悬停mWayPointActionV2", mWayPointActionV2 + "");
                if (wayPointsV2Bean != null) {
                    myWayPointActionList = wayPointsV2Bean.getWayPoints();
                } else {//这里拿到所有航点
                    myWayPointActionList = gson.fromJson(mWayPointActionV2, new TypeToken<List<WayPointsV2Bean.WayPointsBean>>() {
                    }.getType());
                }
            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                Toast.makeText(ConnectionActivity.this, sw.toString(), Toast.LENGTH_LONG).show();
                return;
            }
            int actionId = 0;
            if (waypointV2ActionList != null) {
                waypointV2ActionList.clear();
            }
            if (mUpdateActionToWebBeans != null) {
                mUpdateActionToWebBeans.clear();
            }
            try {
                for (int i = 0; i < myWayPointActionList.size(); i++) {
                    WayPointsV2Bean.WayPointsBean wayPointsBean = myWayPointActionList.get(i);
                    String hasWaitTime = "0";
                    List<WayPointsV2Bean.WayPointsBean.WayPointActionBean> wayPointActionBeanList = wayPointsBean.getWayPointAction();
                    for (int i1 = 0; i1 < wayPointActionBeanList.size(); i1++) {
                        WayPointsV2Bean.WayPointsBean.WayPointActionBean wayPointActionBean = wayPointActionBeanList.get(i1);
                        if ("0".equals(wayPointActionBean.getActionType())) {
                            wayPointActionBeanList.remove(i1);
                            wayPointActionBeanList.add(0, wayPointActionBean);
                            hasWaitTime = "1";
                            break;
                        }
                    }
                    //如果没有悬停且航点动作事件超过1个的时候默认设置一个执行航点动作的时间
                    if ("0".equals(hasWaitTime) && wayPointsBean.getWayPointAction() != null && wayPointActionBeanList.size() > 1) {
                        WayPointsV2Bean.WayPointsBean.WayPointActionBean wayPointActionBean = new WayPointsV2Bean.WayPointsBean.WayPointActionBean();
                        wayPointActionBean.setActionType("0");
                        wayPointActionBean.setWaitingTime(wayPointActionBeanList.size() + "");
                        wayPointActionBeanList.add(0, wayPointActionBean);
                    }
                    //创建一个航点以及航点动作的bean类
                    UpdateActionToWebBean updateActionToWebBean = new UpdateActionToWebBean();
                    MissionPointBean missionPointBean = new MissionPointBean();
                    missionPointBean.setPointIndex(i + 1);
                    updateActionToWebBean.setPointIndex(i + 1);
                    //创建一个航点动作的集合
                    List<String> actionTypeList = new ArrayList<>();
                    List<String> actionIndexList = new ArrayList<>();
                    WaypointTrigger waypointAction0Trigger = null;
                    WaypointActuator waypointAction0Actuator = null;
                    boolean isFirstAction = true;
                    for (int j = 0; j < wayPointActionBeanList.size(); j++) {
                        WayPointsV2Bean.WayPointsBean.WayPointActionBean wayPointActionBean = wayPointsBean.getWayPointAction().get(j);
                        if (!"8".equals(wayPointActionBean.getActionType()) && !"9".equals(wayPointActionBean.getActionType())) {
                            actionId += 1;
//                        Toast.makeText(ConnectionActivity.this, "每一个组合设置Index" + i, Toast.LENGTH_SHORT).show();
                            if (isFirstAction) {
                                isFirstAction = false;
                                waypointAction0Trigger = new WaypointTrigger.Builder()
                                        .setTriggerType(ActionTypes.ActionTriggerType.REACH_POINT)
                                        .setReachPointParam(new WaypointReachPointTriggerParam.Builder()
                                                .setStartIndex(i)
                                                .setAutoTerminateCount(i)
                                                .build())
                                        .build();
                            } else {
                                waypointAction0Trigger = new WaypointTrigger.Builder()
                                        .setTriggerType(ActionTypes.ActionTriggerType.ASSOCIATE)
                                        .setAssociateParam(new WaypointV2AssociateTriggerParam.Builder()
                                                .setAssociateActionID(actionId - 1)
                                                .setAssociateType(ActionTypes.AssociatedTimingType.AFTER_FINISHED)
                                                .setWaitingTime(Float.parseFloat(wayPointActionBean.getWaitingTime() == null ? "0" : wayPointActionBean.getWaitingTime()))
                                                .build())
                                        .build();
                            }
                        }
                        switch (wayPointActionBean.getActionType()) {
                            case "0"://悬停
                                actionTypeList.add("0");
                                actionIndexList.add(actionId + "");
                                updateActionToWebBean.setWaitTime(wayPointsBean.getWayPointAction().get(0).getWaitingTime());
//                                xTIntList.add((i + 1) + "--" + wayPointsV2Bean.getWayPointAction().get(0).getWaitingTime());
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
                                actionTypeList.add("2");
                                actionIndexList.add(actionId + "");
                                waypointAction0Actuator = new WaypointActuator.Builder()
                                        .setActuatorType(ActionTypes.ActionActuatorType.AIRCRAFT_CONTROL)
                                        .setAircraftControlActuatorParam(new WaypointAircraftControlParam.Builder()
                                                .setAircraftControlType(ActionTypes.AircraftControlType.ROTATE_YAW)
                                                .setRotateYawParam(new WaypointAircraftControlRotateYawParam.Builder()
                                                        .setYawAngle(Float.parseFloat(wayPointActionBean.getYawAngle()))
                                                        .setDirection(WaypointV2MissionTypes.WaypointV2TurnMode.find(Integer.parseInt(wayPointActionBean.getDirection())))
//                                            .setRelative()
                                                        .build())
                                                .build())
                                        .build();
                                break;
                            case "3"://云台
                                actionTypeList.add("3");
                                actionIndexList.add(actionId + "");
                                updateActionToWebBean.setPitch(wayPointActionBean.getPitch() == null ? "0" : wayPointActionBean.getPitch());
                                waypointAction0Actuator = new WaypointActuator.Builder()
                                        .setActuatorType(ActionTypes.ActionActuatorType.GIMBAL)
                                        .setGimbalActuatorParam(new WaypointGimbalActuatorParam.Builder()
                                                .operationType(ActionTypes.GimbalOperationType.ROTATE_GIMBAL)
                                                .rotation(new Rotation.Builder()
                                                        .mode(RotationMode.ABSOLUTE_ANGLE)
                                                        .pitch(Float.parseFloat(wayPointActionBean.getPitch() == null ? "0" : wayPointActionBean.getPitch()))
                                                        .roll(0)
//                                                        .yaw(Float.parseFloat(wayPointActionBean.getYaw() == null ? "0" : wayPointActionBean.getYaw()))
//                                                        .yaw(0)
                                                        .time(2)
                                                        .build())
                                                .build())
                                        .build();
                                break;
                            case "4"://变焦
                                actionTypeList.add("4");
                                actionIndexList.add(actionId + "");
                                //修改变焦数据为从前端拿2-200自己计算然后放入官方的sdk
                                waypointAction0Actuator = new WaypointActuator.Builder()
                                        .setActuatorType(ActionTypes.ActionActuatorType.CAMERA)
                                        .setCameraActuatorParam(new WaypointCameraActuatorParam.Builder()
                                                .setCameraOperationType(ActionTypes.CameraOperationType.ZOOM)
                                                .setZoomParam(new WaypointCameraZoomParam.Builder()
                                                        .setFocalLength(getbigZoomValue(wayPointActionBean.getFocalLength()))
                                                        .build())
                                                .build())
                                        .build();
                                break;
                            case "5"://拍照
                                actionTypeList.add("5");
                                actionIndexList.add(actionId + "");
                                waypointAction0Actuator = new WaypointActuator.Builder()
                                        .setActuatorType(ActionTypes.ActionActuatorType.CAMERA)
                                        .setCameraActuatorParam(new WaypointCameraActuatorParam.Builder()
                                                .setCameraOperationType(ActionTypes.CameraOperationType.SHOOT_SINGLE_PHOTO)
                                                .build())
                                        .build();
                                break;
                            case "6"://开始录像
                                actionTypeList.add("6");
                                actionIndexList.add(actionId + "");
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
                            case "8"://开始喊话
                                missionPointBean.setShoutingType("8");
                                voiceBeanShottingContant = wayPointActionBean.getVoice();
                                voiceBeanShottingContant.setFlag("1");
                                missionPointBean.setVoiceBean(voiceBeanShottingContant);
                                break;
                            case "9"://结束喊话
                                if (voiceBeanShottingContant != null) {
                                    voiceBeanShottingContant.setFlag("0");
                                    missionPointBean.setVoiceBean(voiceBeanShottingContant);
                                } else {
                                    voiceBeanShottingContant = new WayPointsV2Bean.WayPointsBean.WayPointActionBean.VoiceBean();
                                    voiceBeanShottingContant.setFlag("0");
                                    missionPointBean.setVoiceBean(voiceBeanShottingContant);
                                }
                                missionPointBean.setShoutingType("9");
                                break;
                        }
                        if ("8".equals(wayPointActionBean.getActionType()) || "9".equals(wayPointActionBean.getActionType())) {

                        } else {
                            WaypointV2Action waypointAction0 = new WaypointV2Action.Builder()
                                    .setActionID(actionId)//0会报错sdkbug
                                    .setTrigger(waypointAction0Trigger)
                                    .setActuator(waypointAction0Actuator)
                                    .build();
                            waypointV2ActionList.add(waypointAction0);
                        }

                        //如果是悬停并且当前是航点的最后一个动作
                        if ("0".equals(wayPointsBean.getWayPointAction().get(0).getActionType()) && j == wayPointsBean.getWayPointAction().size() - 1) {
                            actionId++;
                            actionTypeList.add("1");
                            actionIndexList.add(actionId + "");
                            waypointAction0Trigger = new WaypointTrigger.Builder()
                                    .setTriggerType(ActionTypes.ActionTriggerType.ASSOCIATE)
                                    .setAssociateParam(new WaypointV2AssociateTriggerParam.Builder()
                                            .setAssociateActionID(actionId - 1)
                                            .setAssociateType(ActionTypes.AssociatedTimingType.AFTER_FINISHED)
                                            .setWaitingTime(Float.parseFloat(("30".equals(wayPointsBean.getWayPointAction().get(0).getWaitingTime()) ? "120" :
                                                    wayPointsBean.getWayPointAction().get(0).getWaitingTime())))
                                            .build())
                                    .build();
                            waypointAction0Actuator = new WaypointActuator.Builder()
                                    .setActuatorType(ActionTypes.ActionActuatorType.AIRCRAFT_CONTROL)
                                    .setAircraftControlActuatorParam(new WaypointAircraftControlParam.Builder()
                                            .setAircraftControlType(ActionTypes.AircraftControlType.START_STOP_FLY)
                                            .setFlyControlParam(new WaypointAircraftControlStartStopFlyParam.Builder()
                                                    .setStartFly(true)
                                                    .build())
                                            .build())
                                    .build();

                            WaypointV2Action waypointAction0 = new WaypointV2Action.Builder()
                                    .setActionID(actionId)//0会报错sdkbug
                                    .setTrigger(waypointAction0Trigger)
                                    .setActuator(waypointAction0Actuator)
                                    .build();
                            waypointV2ActionList.add(waypointAction0);
                        }
                    }
                    updateActionToWebBean.setActionIndex(actionIndexList);
                    updateActionToWebBean.setActionType(actionTypeList);
                    mMissionPointBeans.add(missionPointBean);

                    if (actionIndexList != null && actionIndexList.size() != 0 && actionTypeList != null && actionTypeList.size() != 0) {
                        mUpdateActionToWebBeans.add(updateActionToWebBean);
                    }
                    Log.d("飞机飞行流程上传航线储存数据", mUpdateActionToWebBeans.toString());
                }
                XcFileLog.getInstace().i("飞机飞行流程初始化喊话的内容", "mMissionPointBeans==" + (mMissionPointBeans != null ? mMissionPointBeans.toString() : "null"));
                Log.d("飞机飞行流程初始化喊话的内容", "mMissionPointBeans==" + (mMissionPointBeans != null ? mMissionPointBeans.toString() : "null"));

                if (wayPointsV2Bean == null) {
                    if (waypointV2MissionOperator != null && waypointV2MissionOperator.getLoadedActions() != null) {
                        waypointV2MissionOperator.getLoadedActions().clear();
                    }
                    if (waypointV2ActionList.size() == 0) {
                        communication_upload_mission.setEquipmentId(ApronApp.EQUIPMENT_ID);
                        communication_upload_mission.setResult("Mission is uploaded successfully");
                        communication_upload_mission.setCode(200);
                        communication_upload_mission.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                        NettyClient.getInstance().sendMessage(communication_upload_mission, null);
                        Log.d("飞机飞行流程", "上传航点动作成功当前没有规划航点动作");
                    } else {
                        waypointV2MissionOperator.uploadWaypointActions(waypointV2ActionList, new CommonCallbacks.CompletionCallback<DJIWaypointV2Error>() {
                            @Override
                            public void onResult(DJIWaypointV2Error djiWaypointV2Error) {
                                if (djiWaypointV2Error != null) {
                                    communication_upload_mission.setEquipmentId(ApronApp.EQUIPMENT_ID);
                                    communication_upload_mission.setResult(djiWaypointV2Error.getDescription());
                                    communication_upload_mission.setCode(-1);
                                    communication_upload_mission.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                                    NettyClient.getInstance().sendMessage(communication_upload_mission, null);
                                    XcFileLog.getInstace().i("飞机飞行流程", "上传失败" + djiWaypointV2Error.getDescription());
                                } else {
                                    Log.d("飞机飞行流程", "上传航点动作成功");
                                }
                            }
                        });
                    }
                }
            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                Log.d("添加航点动作捕获异常", sw.toString());
                return;
            }
        }


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
                        .setDampingDistance(20f)
                        //设置经纬度
                        .setCoordinate(new LocationCoordinate2D(latLng[0], latLng[1]))
                        //高度[-200,500]
                        .setAltitude(Double.parseDouble(myWayPointList.get(i).getAltitude()))
                        //设置路径模式
                        .setFlightPathMode(WaypointV2MissionTypes.WaypointV2FlightPathMode.find(Integer.parseInt(myWayPointList.get(i).getFlightPathMode())))
                        .setHeadingMode(WaypointV2MissionTypes.WaypointV2HeadingMode.find(Integer.parseInt(myWayPointList.get(i).getHeadingMode())))
                        .setTurnMode(WaypointV2MissionTypes.WaypointV2TurnMode.find(Integer.parseInt(myWayPointList.get(i).getTurnMode())))
                        .setAutoFlightSpeed(Float.parseFloat(myWayPointList.get(i).getSpeed()))
                        .setUsingWaypointAutoFlightSpeed(true)
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
        Log.d("开始飞行航点-EU", "紧紧测试" + communication.getPara().get(Constant.WAY_POINTS));
        WaypointV2MissionState state = null;
        int i = 0;
        while (++i < 15) {
            state = waypointV2MissionOperator.getCurrentState();

            if (state.equals(WaypointV2MissionState.READY_TO_EXECUTE)) {
                break;
            }
            XcFileLog.getInstace().i("航线开始飞行", "循环回调，当前状态为：" + state.name() + "  " + state);

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (Exception e) {
            }
        }
        if (i == 15) {
            XcFileLog.getInstace().i("航线开始飞行", "等待了15秒，state 不为 READY_TO_UPLOAD 或 READY_TO_EXECUTE , 当前状态为 ：" + state);
        } else {
            XcFileLog.getInstace().i("航线开始飞行", "第" + i + "秒，获取到state正常状态成功，状态为" + state);
        }

        waypointV2MissionOperator.startMission(new CommonCallbacks.CompletionCallback<DJIWaypointV2Error>() {
            @Override
            public void onResult(DJIWaypointV2Error djiWaypointV2Error) {
                if (djiWaypointV2Error == null) {
                    Toast.makeText(ConnectionActivity.this, djiWaypointV2Error == null ? "Mission is started successfully" : djiWaypointV2Error.getDescription(), Toast.LENGTH_SHORT).show();
                }
//                if (djiWaypointV2Error != null && currentStartMission < 5) {
//
//                    currentStartMission += 1;
//                    startWaypointMission(communication);
//                }else
                if (djiWaypointV2Error != null) {

                    Log.d("飞机飞行流程", "startWaypointMissionV2报错");
                    XcFileLog.getInstace().i("航线开始飞行", djiWaypointV2Error.getDescription() + "      " + djiWaypointV2Error.toString());
                    communication.setResult("startMission报错:" + djiWaypointV2Error.getDescription());
                    communication.setCode(-1);
                    communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                    NettyClient.getInstance().sendMessage(communication, null);
                } else {
                    Log.d("飞机飞行流程", "tartWaypointMissionV2成功");
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
        if (waypointV2MissionOperator != null && (waypointV2MissionOperator.getCurrentState().equals(WaypointV2MissionState.EXECUTING)
                || waypointV2MissionOperator.getCurrentState().equals(WaypointV2MissionState.INTERRUPTED))) {
            waypointV2MissionOperator.stopMission(new CommonCallbacks.CompletionCallback<DJIWaypointV2Error>() {
                @Override
                public void onResult(DJIWaypointV2Error djiWaypointV2Error) {
                    Toast.makeText(ConnectionActivity.this, djiWaypointV2Error == null ? "The mission has been stopped" : djiWaypointV2Error.getDescription(), Toast.LENGTH_SHORT).show();
                    send(PagerUtils.getInstance().TTSSTOPINS);
                }
            });
        }
        communication.setResult("Success");
        communication.setCode(200);
        communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
        NettyClient.getInstance().sendMessage(communication, null);
    }


    //暂停航线
    private void pauseWaypointV2(Communication communication) {
        if (waypointV2MissionOperator.getCurrentState().equals(WaypointV2MissionState.EXECUTING)) {
            waypointV2MissionOperator.interruptMission(new CommonCallbacks.CompletionCallback<DJIWaypointV2Error>() {
                @Override
                public void onResult(DJIWaypointV2Error djiWaypointV2Error) {
                    XcFileLog.getInstace().i("pauseWaypointV2:", djiWaypointV2Error == null ? "The mission has been interrupted" : djiWaypointV2Error.getDescription());
                    CommonDjiCallback(djiWaypointV2Error, communication);
                }

            });
        }
    }

    private void resumeWaypointV2(Communication communication) {
        String type = communication.getPara().get(Constant.TYPE);
        if (waypointV2MissionOperator != null && waypointV2MissionOperator.getCurrentState() != null && (waypointV2MissionOperator.getCurrentState().equals(WaypointV2MissionState.INTERRUPTED) || waypointV2MissionOperator.getCurrentState().equals(WaypointV2MissionState.READY_TO_UPLOAD))) {
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
                        isHangXianPause = false;
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

    /**
     * 航线中监听到的变化推送给前端
     *
     * @param djiError
     * @param communication
     */
    private void missionDjiCallback(DJIError djiError, Communication communication) {

    }

    //通用的Callback
    private void CommonDjiCallback(DJIError djiError, Communication communication) {
        if (communication == null) {
            communication = new Communication();

        }
        if (djiError != null) {
            Log.d("飞机飞行流程", djiError.getDescription());
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
            change_lens(communication, null);
        }
    }

    private void sendDataToOSDK(byte[] data) {
        Aircraft aircraft = ApronApp.getAircraftInstance();

        if (aircraft == null) {
        } else {

            mFlightController = aircraft.getFlightController();
            if (mFlightController != null) {
                mFlightController.sendDataToOnboardSDKDevice(data, new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (djiError != null) {
                            showToast(djiError.getDescription());
                        }
                    }
                });
            } else {
                showToast("mFlightController=null");
            }
        }
    }

    /**
     * 航点动作监听到的喊话
     *
     * @param voiceBean
     */
    private void wayPointSendTTS2Payload(WayPointsV2Bean.WayPointsBean.WayPointActionBean.VoiceBean voiceBean) {
        XcFileLog.getInstace().i("飞机飞行流程航点喊话信息", "voiceBean==" + (voiceBean != null ? voiceBean.toString() : "null"));

        String tts = voiceBean.getWord();
        String sign = "[" + "d" + "]";//标记
        String fre = "0";
        if (TextUtils.isEmpty(tts)) {
            tts = "未检测到语音文本";
        } else {
            fre = voiceBean.getModel();
            String volume = voiceBean.getVolume();//音量
            String tone = voiceBean.getTone();//性别 52男 53女
            String speed = voiceBean.getSpeed();//语速
            String sex = "53";
            if (tone.equals("0")) {
                sex = "53";
            } else {
                sex = "52";
            }
            sign = "[" + "v" + volume + "]" + "[" + "s" + speed + "]" + "[" + "m" + sex + "]";//科大讯飞标记使用
        }

        PagerUtils pagerUtils = PagerUtils.getInstance();
        byte[] content = pagerUtils.HexString2Bytes(pagerUtils.toChineseHex(sign + tts));
        byte[] data = pagerUtils.dataCopy(voiceBean.getFlag().equals("1") ? pagerUtils.TTSINS : pagerUtils.TTSSTOPINS, content);
        send(fre.equals("0") ? pagerUtils.TTSONEINS : pagerUtils.TTSREPEATINS);
        if (ApronApp.getProductInstance() != null && ApronApp.getProductInstance().getPayload() != null)
            ApronApp.getProductInstance().getPayload().sendDataToPayload(data, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (communication_onExecutionFinish == null) {
                        communication_onExecutionFinish = new Communication();
                    }
                    if (djiError != null) {
                        showToast("Error:" + djiError.getErrorCode());
                        communication_onExecutionFinish.setEquipmentId(ApronApp.EQUIPMENT_ID);
                        communication_onExecutionFinish.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                        communication_onExecutionFinish.setResult("{\"result\":\"" + djiError.getDescription() + "\"}");
                        communication_onExecutionFinish.setCode(-1);
                        communication_onExecutionFinish.setMethod(Constant.MISSIONWAYPOINT);
                        NettyClient.getInstance().sendMessage(communication_onExecutionFinish, null);
                        Log.d("飞机飞行流程", "喊话失败推送给前端" + communication_onExecutionFinish.toString());

                    } else {
                        communication_onExecutionFinish.setEquipmentId(ApronApp.EQUIPMENT_ID);
                        communication_onExecutionFinish.setCode(200);
                        communication_onExecutionFinish.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                        communication_onExecutionFinish.setResult(gson.toJson(mMissionPointBean));
                        communication_onExecutionFinish.setMethod(Constant.MISSIONWAYPOINT);
                        NettyClient.getInstance().sendMessage(communication_onExecutionFinish, null);
                        Log.d("飞机飞行流程", "喊话成功推送给前端" + communication_onExecutionFinish.toString());
                    }
                }
            });
    }

    /**
     * 拼接文本指令
     *
     * @param communication
     */
    private void sendTTS2Payload(Communication communication) {
        String tts = communication.getPara().get("word");
        String sign = "[" + "d" + "]";//标记
        String fre = "0";
        if (TextUtils.isEmpty(tts)) {
            tts = "未检测到语音文本";
        } else {
            fre = communication.getPara().get("model");
            String volume = communication.getPara().get("volume");//音量
            String tone = communication.getPara().get("tone");//性别 52男 53女
            String speed = communication.getPara().get("speed");//语速
            String sex = "53";
            if (tone.equals("0")) {
                sex = "53";
            } else {
                sex = "52";
            }
            sign = "[" + "v" + volume + "]" + "[" + "s" + speed + "]" + "[" + "m" + sex + "]";//科大讯飞标记使用
        }

        PagerUtils pagerUtils = PagerUtils.getInstance();
        byte[] content = pagerUtils.HexString2Bytes(pagerUtils.toChineseHex(sign + tts));
        byte[] data = pagerUtils.dataCopy(pagerUtils.TTSINS, content);
        send(fre.equals("0") ? pagerUtils.TTSONEINS : pagerUtils.TTSREPEATINS);
        send(data, communication);
    }

    /**
     * 发送即时语音指令
     *
     * @param communication
     */
    private void sendMP32Payload(Communication communication) {
        String mp3 = communication.getPara().get("MP3");
        if (TextUtils.isEmpty(mp3)) {
            showToast("未检测到音频");
            return;
        }
        PagerUtils pagerUtils = PagerUtils.getInstance();
        send(pagerUtils.MP3STARTSINS);//开始上传
        new Thread() {
            @Override
            public void run() {
                byte[] bytes = pagerUtils.HexString2Bytes(mp3);
                voiceByteData = pagerUtils.splitBytes(bytes, 128);
                upLoadMP3(voiceByteData[0], communication);
            }
        }.start();
    }

    private void send(byte[] bytes) {
        if (ApronApp.getProductInstance() != null && ApronApp.getProductInstance().getPayload() != null) {
            Payload payload = ApronApp.getProductInstance().getPayload();
            payload.sendDataToPayload(bytes, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError != null) {
                        showToast("Error:" + djiError.getErrorCode());
                        XcFileLog.getInstace().i("飞机飞行流程喊话结束失败", "发送关闭喊话器结束失败" + djiError.getErrorCode());
                    } else {
                        XcFileLog.getInstace().i("飞机飞行流程喊话结束成功", "发送关闭喊话器结束成功");
                    }
                }
            });
        } else {
            showToast("未检测到payload设备");
        }
    }

    /**
     * 发送指令到Payload
     *
     * @param data
     * @param communication
     */
    private void send(byte[] data, Communication communication) {
        if (ApronApp.getProductInstance() != null) {
            Payload payload = ApronApp.getProductInstance().getPayload();
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

    private byte voiceByteData[][];


    private void upLoadMP3(byte[] mp3) {
        if (ApronApp.getProductInstance() != null) {
            Payload payload = ApronApp.getProductInstance().getPayload();
            payload.sendDataToPayload(mp3, voiceCallBack);
        } else {
            showToast("未检测到payload设备");
        }
    }

    /**
     * 上传音频文件,将结果回传给服务器
     *
     * @param mp3
     * @param communication
     */
    private void upLoadMP3(byte[] mp3, Communication communication) {
        if (ApronApp.getProductInstance() != null) {
            Payload payload = ApronApp.getProductInstance().getPayload();
            payload.sendDataToPayload(mp3, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError != null) {
                        communication.setResult(djiError.getDescription());
                        communication.setCode(-1);
                        communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                        NettyClient.getInstance().sendMessage(communication, null);
                    } else {
                        PagerUtils pagerUtils = PagerUtils.getInstance();
                        voiceByteData = pagerUtils.deleteAt(voiceByteData, 0);

                        if (voiceByteData.length > 0) {
                            upLoadMP3(voiceByteData[0], communication);
                        } else {
                            send(pagerUtils.MP3STOPSINS);//结束上传
                            send(pagerUtils.MP3OPENINS);//开始播放
                            communication.setResult("Success");
                            communication.setCode(200);
                            communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                            NettyClient.getInstance().sendMessage(communication, null);
                        }
                    }
                }
            });
        } else {
            showToast("未检测到payload设备");
        }
    }

    //音频文件分发
    CommonCallbacks.CompletionCallback voiceCallBack = new CommonCallbacks.CompletionCallback() {
        @Override
        public void onResult(DJIError djiError) {
            if (djiError != null) {
                showToast("sendDataFail+" + djiError.getErrorCode());
            } else {
                PagerUtils pagerUtils = PagerUtils.getInstance();
                voiceByteData = pagerUtils.deleteAt(voiceByteData, 0);
                if (voiceByteData.length > 0) {
                    upLoadMP3(voiceByteData[0]);
                } else {
                    send(pagerUtils.MP3STOPSINS);//结束上传
                    send(pagerUtils.MP3OPENINS);//开始播放
                }
            }
        }
    };
    private boolean isMapMini = true;

    private void onViewClick(View view) {
        if (view == mVideoSurface && !isMapMini) {
            showPanels();
            resizeFPVWidget(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, 0, 0);
            ResizeAnimation mapViewAnimation = new ResizeAnimation(mapWidget, deviceWidth, deviceHeight, 257, 154, 0);
            mapWidget.startAnimation(mapViewAnimation);
            isMapMini = true;
        } else if (view == mapWidget && isMapMini) {
            hidePanels();
            resizeFPVWidget(257, 154, 0, 3);
            ResizeAnimation mapViewAnimation = new ResizeAnimation(mapWidget, 257, 154, deviceWidth, deviceHeight, 0);
            mapWidget.startAnimation(mapViewAnimation);
            isMapMini = false;
        }
    }

    private void resizeFPVWidget(int width, int height, int margin, int fpvInsertPosition) {
        RelativeLayout.LayoutParams fpvParams = (RelativeLayout.LayoutParams) layout_previewer_container.getLayoutParams();
        fpvParams.height = height;
        fpvParams.width = width;
        fpvParams.leftMargin = margin;
        fpvParams.bottomMargin = margin;
        if (isMapMini) {
            fpvParams.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
            fpvParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            fpvParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        } else {
            fpvParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            fpvParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
            fpvParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        }
        layout_previewer_container.setLayoutParams(fpvParams);

        parentView.removeView(layout_previewer_container);
        parentView.addView(layout_previewer_container, fpvInsertPosition);
    }

    private void hidePanels() {
        tab_change.setVisibility(View.GONE);
        tv_zoom.setVisibility(View.GONE);
        layout_sort.setVisibility(View.GONE);
        layout_air_info.setVisibility(View.GONE);
    }

    private void showPanels() {
        tab_change.setVisibility(View.VISIBLE);
        if (isM300Product()) {
            tv_zoom.setVisibility(View.VISIBLE);
        } else {
            tv_zoom.setVisibility(View.GONE);
        }
        if (isINSPIRE2Product() || isM300Product()) {
            tab_change.setVisibility(View.VISIBLE);
        } else {
            tab_change.setVisibility(View.GONE);
        }
        layout_sort.setVisibility(View.VISIBLE);
        layout_air_info.setVisibility(View.VISIBLE);
    }


    private void changeGimbalAngle(int currentAngle) {
        hideTxtForGimbalAngle();
        if (currentAngle == 24 || currentAngle == 25 || currentAngle == 26 || currentAngle == 27 || currentAngle == 28 || currentAngle == 29 || currentAngle == 30) {
            tv_txt_1.setVisibility(View.VISIBLE);
            tv_txt_1.setText(currentAngle + "");
        } else if (currentAngle == 17 || currentAngle == 18 || currentAngle == 19 || currentAngle == 20 || currentAngle == 21 || currentAngle == 22 || currentAngle == 23) {
            tv_txt_2.setVisibility(View.VISIBLE);
            tv_txt_2.setText(currentAngle + "");
        } else if (currentAngle == 10 || currentAngle == 11 || currentAngle == 12 || currentAngle == 13 || currentAngle == 14 || currentAngle == 15 || currentAngle == 16) {
            tv_txt_3.setVisibility(View.VISIBLE);
            tv_txt_3.setText(currentAngle + "");
        } else if (currentAngle == 3 || currentAngle == 4 || currentAngle == 5 || currentAngle == 6 || currentAngle == 7 || currentAngle == 8 || currentAngle == 9) {
            tv_txt_4.setVisibility(View.VISIBLE);
            tv_txt_4.setText(currentAngle + "");
        } else if (currentAngle == -4 || currentAngle == -3 || currentAngle == -2 || currentAngle == -1 || currentAngle == 0 || currentAngle == 1 || currentAngle == 2) {
            tv_txt_5.setVisibility(View.VISIBLE);
            tv_txt_5.setText(currentAngle + "");
        } else if (currentAngle == -11 || currentAngle == -10 || currentAngle == -9 || currentAngle == -8 || currentAngle == -7 || currentAngle == -6 || currentAngle == -5) {
            tv_txt_6.setVisibility(View.VISIBLE);
            tv_txt_6.setText(currentAngle + "");
        } else if (currentAngle == -18 || currentAngle == -17 || currentAngle == -16 || currentAngle == -15 || currentAngle == -14 || currentAngle == -13 || currentAngle == -12) {
            tv_txt_7.setVisibility(View.VISIBLE);
            tv_txt_7.setText(currentAngle + "");
        } else if (currentAngle == -25 || currentAngle == -24 || currentAngle == -23 || currentAngle == -22 || currentAngle == -21 || currentAngle == -20 || currentAngle == -19) {
            tv_txt_8.setVisibility(View.VISIBLE);
            tv_txt_8.setText(currentAngle + "");
        } else if (currentAngle == -32 || currentAngle == -31 || currentAngle == -30 || currentAngle == -29 || currentAngle == -28 || currentAngle == -27 || currentAngle == -26) {
            tv_txt_9.setVisibility(View.VISIBLE);
            tv_txt_9.setText(currentAngle + "");
        } else if (currentAngle == -39 || currentAngle == -38 || currentAngle == -37 || currentAngle == -36 || currentAngle == -35 || currentAngle == -34 || currentAngle == -33) {
            tv_txt_10.setVisibility(View.VISIBLE);
            tv_txt_10.setText(currentAngle + "");
        } else if (currentAngle == -46 || currentAngle == -45 || currentAngle == -44 || currentAngle == -43 || currentAngle == -42 || currentAngle == -41 || currentAngle == -40) {
            tv_txt_11.setVisibility(View.VISIBLE);
            tv_txt_11.setText(currentAngle + "");
        } else if (currentAngle == -53 || currentAngle == -52 || currentAngle == -51 || currentAngle == -50 || currentAngle == -49 || currentAngle == -48 || currentAngle == -47) {
            tv_txt_12.setVisibility(View.VISIBLE);
            tv_txt_12.setText(currentAngle + "");
        } else if (currentAngle == -60 || currentAngle == -59 || currentAngle == -58 || currentAngle == -57 || currentAngle == -56 || currentAngle == -55 || currentAngle == -54) {
            tv_txt_13.setVisibility(View.VISIBLE);
            tv_txt_13.setText(currentAngle + "");
        } else if (currentAngle == -67 || currentAngle == -66 || currentAngle == -65 || currentAngle == -64 || currentAngle == -63 || currentAngle == -62 || currentAngle == -61) {
            tv_txt_14.setVisibility(View.VISIBLE);
            tv_txt_14.setText(currentAngle + "");
        } else if (currentAngle == -74 || currentAngle == -73 || currentAngle == -72 || currentAngle == -71 || currentAngle == -70 || currentAngle == -69 || currentAngle == -68) {
            tv_txt_15.setVisibility(View.VISIBLE);
            tv_txt_15.setText(currentAngle + "");
        } else if (currentAngle == -81 || currentAngle == -80 || currentAngle == -79 || currentAngle == -78 || currentAngle == -77 || currentAngle == -76 || currentAngle == -75) {
            tv_txt_16.setVisibility(View.VISIBLE);
            tv_txt_16.setText(currentAngle + "");
        } else if (currentAngle == -90 || currentAngle == -89 || currentAngle == -88 || currentAngle == -87 || currentAngle == -86 || currentAngle == -85 || currentAngle == -84 || currentAngle == -83 || currentAngle == -82) {
            tv_txt_17.setVisibility(View.VISIBLE);
            tv_txt_17.setText(currentAngle + "");
        } else if (currentAngle > 30) {
            tv_txt_1.setVisibility(View.VISIBLE);
            tv_txt_1.setText(currentAngle + "");
        } else if (currentAngle < -90) {
            tv_txt_17.setVisibility(View.VISIBLE);
            tv_txt_17.setText(currentAngle + "");
        }

    }

    private void hideTxtForGimbalAngle() {
        tv_txt_1.setVisibility(View.INVISIBLE);
        tv_txt_2.setVisibility(View.INVISIBLE);
        tv_txt_3.setVisibility(View.INVISIBLE);
        tv_txt_4.setVisibility(View.INVISIBLE);
        tv_txt_5.setVisibility(View.INVISIBLE);
        tv_txt_6.setVisibility(View.INVISIBLE);
        tv_txt_7.setVisibility(View.INVISIBLE);
        tv_txt_8.setVisibility(View.INVISIBLE);
        tv_txt_9.setVisibility(View.INVISIBLE);
        tv_txt_10.setVisibility(View.INVISIBLE);
        tv_txt_11.setVisibility(View.INVISIBLE);
        tv_txt_12.setVisibility(View.INVISIBLE);
        tv_txt_13.setVisibility(View.INVISIBLE);
        tv_txt_14.setVisibility(View.INVISIBLE);
        tv_txt_15.setVisibility(View.INVISIBLE);
        tv_txt_16.setVisibility(View.INVISIBLE);
        tv_txt_17.setVisibility(View.INVISIBLE);
    }

    private class ResizeAnimation extends Animation {

        private View mView;
        private int mToHeight;
        private int mFromHeight;

        private int mToWidth;
        private int mFromWidth;
        private int mMargin;

        private ResizeAnimation(View v, int fromWidth, int fromHeight, int toWidth, int toHeight, int margin) {
            mToHeight = toHeight;
            mToWidth = toWidth;
            mFromHeight = fromHeight;
            mFromWidth = fromWidth;
            mView = v;
            mMargin = margin;
            setDuration(300);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            float height = (mToHeight - mFromHeight) * interpolatedTime + mFromHeight;
            float width = (mToWidth - mFromWidth) * interpolatedTime + mFromWidth;
            RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) mView.getLayoutParams();
            p.height = (int) height;
            p.width = (int) width;
            p.rightMargin = mMargin;
            p.bottomMargin = mMargin;
            mView.requestLayout();
        }
    }
}