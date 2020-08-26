package com.compass.ux;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import dji.common.airlink.SignalQualityCallback;
import dji.common.camera.CameraVideoStreamSource;
import dji.common.camera.LaserMeasureInformation;
import dji.common.camera.PhotoTimeLapseSettings;
import dji.common.camera.SettingsDefinitions;
import dji.common.camera.StorageState;
import dji.common.error.DJIError;
import dji.common.error.DJISDKError;
import dji.common.flightcontroller.ConnectionFailSafeBehavior;
import dji.common.flightcontroller.FlightControllerState;
import dji.common.flightcontroller.GravityCenterState;
import dji.common.flightcontroller.LEDsSettings;
import dji.common.flightcontroller.OSDKEnabledState;
import dji.common.flightcontroller.ObstacleDetectionSector;
import dji.common.flightcontroller.VisionDetectionState;
import dji.common.flightcontroller.WindDirection;
import dji.common.flightcontroller.adsb.AirSenseAirplaneState;
import dji.common.flightcontroller.adsb.AirSenseSystemInformation;
import dji.common.flightcontroller.flightassistant.ObstacleAvoidanceSensorState;
import dji.common.flightcontroller.flightassistant.PerceptionInformation;
import dji.common.flightcontroller.imu.IMUState;
import dji.common.flightcontroller.virtualstick.FlightControlData;
import dji.common.flightcontroller.virtualstick.FlightCoordinateSystem;
import dji.common.flightcontroller.virtualstick.RollPitchControlMode;
import dji.common.flightcontroller.virtualstick.VerticalControlMode;
import dji.common.flightcontroller.virtualstick.YawControlMode;
import dji.common.gimbal.Axis;
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
import dji.keysdk.BatteryKey;
import dji.keysdk.DiagnosticsKey;
import dji.keysdk.FlightControllerKey;
import dji.keysdk.KeyManager;
import dji.keysdk.callback.GetCallback;
import dji.keysdk.callback.KeyListener;
import dji.keysdk.callback.SetCallback;
import dji.sdk.base.BaseComponent;
import dji.sdk.base.BaseProduct;
import dji.sdk.base.DJIDiagnostics;
import dji.sdk.battery.Battery;
import dji.sdk.camera.Camera;
import dji.sdk.camera.VideoFeeder;
import dji.sdk.codec.DJICodecManager;
import dji.sdk.flightcontroller.FlightAssistant;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.gimbal.Gimbal;
import dji.sdk.media.DownloadListener;
import dji.sdk.media.MediaFile;
import dji.sdk.media.MediaManager;
import dji.sdk.mission.waypoint.WaypointMissionOperator;
import dji.sdk.mission.waypoint.WaypointMissionOperatorListener;
import dji.sdk.products.Aircraft;
import dji.sdk.remotecontroller.RemoteController;
import dji.sdk.sdkmanager.DJISDKInitEvent;
import dji.sdk.sdkmanager.DJISDKManager;
import dji.sdk.useraccount.UserAccountManager;
import dji.thirdparty.afinal.core.AsyncTask;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

import android.Manifest;
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

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.compass.ux.bean.BatteryPersentAndVoltageBean;
import com.compass.ux.bean.FlightControllerBean;
import com.compass.ux.bean.IMUStateBean;
import com.compass.ux.bean.LaserMeasureInformBean;
import com.compass.ux.bean.SettingValueBean;
import com.compass.ux.bean.ObstacleAvoidanceSensorStateBean;
import com.compass.ux.bean.PerceptionBean;
import com.compass.ux.bean.StorageStateBean;
import com.compass.ux.bean.StringsBean;
import com.compass.ux.bean.TextMessageBean;
import com.compass.ux.bean.WebInitializationBean;
import com.compass.ux.downloadpic.DefaultLayoutActivity;
import com.compass.ux.netty_lib.NettyService;
import com.compass.ux.netty_lib.activity.NettyActivity;
import com.compass.ux.netty_lib.netty.NettyClient;
import com.compass.ux.netty_lib.zhang.Communication;
import com.compass.ux.netty_lib.zhang.RsaUtil;
import com.compass.ux.takephoto.FPVDemoApplication;
import com.compass.ux.utils.ClientUploadUtils;
import com.compass.ux.utils.FileUtils;
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
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.compass.ux.Constant.GET_LOW_BATTERY;
import static com.compass.ux.Constant.IMU_STATUS;
import static com.compass.ux.Constant.UP;
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

public class ConnectionActivity extends NettyActivity implements View.OnClickListener, TextureView.SurfaceTextureListener, DJIDiagnostics.DiagnosticsInformationCallback {

    private String liveShowUrl = "rtmp://172.16.16.1:1935/live/Mobile_01";
    //            private String liveShowUrl = "rtmp://118.179.93.254:21935/live/Mobile_01";
//    private String liveShowUrl = "rtmp://172.16.16.80:21935/live/Mobile_01";
    //        private String liveShowUrl = "rtmp://push.yunxi.tv/yunxi-host/test_225f76ef74be4dc2810a8c1716cab20a?auth_key=1597209000-0-0-6ab0f55c962338d0c9945408d264615f";
    private static final String TAG = ConnectionActivity.class.getName();


    private TextView mTextConnectionStatus;
    private TextView mTextProduct;
    private TextView mVersionTv;
    private Button mBtnOpen, btn_download, btn_gaode, btn_simulator, btn_login;
    private EditText et_zoom;

    private FlightController mFlightController;
    private RemoteController mRemoteController;
    private FlightAssistant mFlightAssistant;
    private Battery battery;
    private Gimbal gimbal;
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
    File file = new File(Environment.getExternalStorageDirectory().getPath() + "/Shebei");
    File destDir = new File(Environment.getExternalStorageDirectory().getPath() + "/DjiMedia/");
    File compressAddress = new File(Environment.getExternalStorageDirectory().getPath() + "/CompressAddress");
    String FileName = "";
    private String currentEquipment = "";//获取当前是什么无人机
    private Handler mHandler;
    public static final String FLAG_CONNECTION_CHANGE = "dji_sdk_connection_change";
    private Gson gson = new Gson();
    private static final String[] REQUIRED_PERMISSION_LIST = new String[]{
            Manifest.permission.VIBRATE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.CHANGE_NETWORK_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            // 添加这俩权限就不会延迟了
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };
    private List<String> missingPermission = new ArrayList<>();
    private AtomicBoolean isRegistrationInProgress = new AtomicBoolean(false);
    private static final int REQUEST_PERMISSION_CODE = 12345;

    private List<MediaFile> mediaFileList = new ArrayList<MediaFile>();
    private MediaManager mMediaManager;
    private MediaManager.FileListState currentFileListState = MediaManager.FileListState.UNKNOWN;
    VideoFeeder.VideoDataListener mReceivedVideoDataListener = null;
    DJICodecManager mCodecManager = null;
    TextureView mVideoSurface = null;

    FlightControllerKey wind_direction_key;
    FlightControllerKey wind_speed_key;

    FlightControllerBean flightControllerBean = null;
    Camera camera = null;
    Communication communication_flightController = null;
    Communication communication_takePhoto = null;
    Communication communication_battery = null;
    Communication communication_up_link = null;
    Communication communication_down_link = null;
    Communication communication_plane_status = null;
    Communication communication_laser_status = null;
    Communication communication_error_log = null;
    Communication communication_camera_data = null;
    Communication communication_perception_data = null;
    Communication communication_ObstacleAvoidanceSensorState = null;
    Communication communication_StorageState = null;
    Communication communication_isFlying = null;
    Communication communication_Initialization = null;
    Communication communication_imu_status = null;
    Communication communication_low_battery = null;
    private int currentProgress = -1;
    private boolean havePermission = false;
    private boolean lastFlying = false;//判断是否起飞
    String lowBatteryWarning = "", seriousLowBatteryWarning = "";
    boolean smartReturnToHomeEnabled;

    boolean visionAssistedPosition, precisionLand, upwardsAvoidance;
    FlightControllerKey flightControllerKey1 = FlightControllerKey.create(FlightControllerKey.VISION_ASSISTED_POSITIONING_ENABLED);
    FlightControllerKey flightControllerKey2 = FlightControllerKey.create(FlightControllerKey.PRECISION_LANDING_ENABLED);
    FlightControllerKey flightControllerKey3 = FlightControllerKey.create(FlightControllerKey.UPWARDS_AVOIDANCE_ENABLED);
    String avoidanceDistanceUpward = "", avoidanceDistanceDownward = "", maxPerceptionDistanceUpward = "", maxPerceptionDistanceDownward = ""
    ,avoidanceDistanceHorizontal="",maxPerceptionDistanceHorizontal="";
    boolean activeObstacleAvoidance;

    WebInitializationBean webInitializationBean = new WebInitializationBean();
    //这是获取左上角飞行状态的
    private Handler planeStatusHandler = new Handler();
    private Runnable planeStatusTask = new Runnable() {
        public void run() {
            // TODOAuto-generated method stub
            planeStatusHandler.postDelayed(this, 1 * 1000);//设置延迟时间，此处是1秒
            //需要执行的代码
            DiagnosticsKey diagnosticsKey = DiagnosticsKey.create(DiagnosticsKey.SYSTEM_STATUS);
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
                        communication_plane_status.setMethod(RsaUtil.encrypt(Constant.LINK_PLANE_STATUS));
                        communication_plane_status.setResult(gson.toJson(stringsBean, StringsBean.class));
                        NettyClient.getInstance().sendMessage(communication_plane_status, null);
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(ConnectionActivity.this, o.toString(), Toast.LENGTH_SHORT).show();
//                            }
//                        });
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        checkAndRequestPermissions();
//        }
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
        //模型赋初始值
        webInitializationBean.setISO(65535);
        webInitializationBean.setExposureCompensation(65535);
        webInitializationBean.setShutter(255.0F);
        webInitializationBean.setExposureMode(65535);
        webInitializationBean.setCameraMode(255);
        webInitializationBean.setUpLink(0);
        webInitializationBean.setDownLink(0);


    }

    private void initAllKeys() {
        wind_direction_key = FlightControllerKey.create(FlightControllerKey.WIND_DIRECTION);
        wind_speed_key = FlightControllerKey.create(WIND_SPEED);
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
//                VideoFeeder.getInstance().getPrimaryVideoFeed().setPriority(VideoFeedPriority.HIGH, new CommonCallbacks.CompletionCallback() {
//                    @Override
//                    public void onResult(DJIError djiError) {
//
//                    }
//                });

            }
        }

    }

    private void uninitPreviewer() {
//        camera = FPVDemoApplication.getCameraInstance();
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
        removeListener();
        uninitPreviewer();
        if (mMediaManager != null) {
            mMediaManager.stop(null);
            mMediaManager.removeFileListStateCallback(this.updateFileListStateListener);
//            调用exitMediaDownloading()方法以退出MEDIA_DOWNLOAD模式并进入SHOOT_PHOTO模式
            mMediaManager.exitMediaDownloading();
//            if (scheduler!=null) {
//                scheduler.removeAllTasks();
//            }
        }
        planeStatusHandler.removeCallbacks(planeStatusTask);//关闭轮询
        DJISDKManager.getInstance().getProduct().setDiagnosticsInformationCallback(this);//关闭错误日志
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
                //去拍照录像
//                Intent intent = new Intent(this, TakePhotoActivity.class);
//                startActivity(intent);
//                Communication communication = new Communication();
//                camera.getMode(new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.CameraMode>() {
//                    @Override
//                    public void onSuccess(SettingsDefinitions.CameraMode cameraMode) {
//                        if (cameraMode == SettingsDefinitions.CameraMode.MEDIA_DOWNLOAD) {
//                            communication.setResult("正在下载中");
//                            communication.setCode(-1);
//                            communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
//                            NettyClient.getInstance().sendMessage(communication, null);
//                        } else {
//                            camera_start_shoot(communication);
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(DJIError djiError) {
//
//                    }
//                });
                break;

            }
            case R.id.btn_download: {
                //去下载
                Intent intent = new Intent(this, DefaultLayoutActivity.class);
                startActivity(intent);


//                String aaa = "/storage/emulated/0/DjiMedia/DJI_0009.jpg";
//                String bbb = "http://61.155.157.42:7070/oauth/file/upload";
//                File downloadFile = new File(aaa);
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            Luban.with(ConnectionActivity.this)
//                                    .load(downloadFile)
//                                    .ignoreBy(100)
//                                    .setTargetDir(compressAddress.getPath())
////                            .filter(new CompressionPredicate() {
////                                @Override
////                                public boolean apply(String path) {
////                                    return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
////                                }
////                            })
//                                    .setCompressListener(new OnCompressListener() {
//                                        @Override
//                                        public void onStart() {
//                                            // 压缩开始前调用，可以在方法内启动 loading UI
//                                        }
//
//                                        @Override
//                                        public void onSuccess(File file) {
//                                            //压缩成功后调用，返回压缩后的图片文件
//                                            Toast.makeText(ConnectionActivity.this, "压缩成功", Toast.LENGTH_SHORT).show();
//                                            String result = null;
//                                            try {
//                                                result = ClientUploadUtils.upload(bbb, file, file.getName()).toString();
//                                            } catch (Exception e) {
//                                                e.printStackTrace();
//                                            }
//                                            Toast.makeText(ConnectionActivity.this, "result:" + result, Toast.LENGTH_SHORT).show();
//                                        }
//
//                                        @Override
//                                        public void onError(Throwable e) {
//                                            //当压缩过程出现问题时调用
//                                        }
//                                    }).launch();
//
////                            String result=ClientUploadUtils.upload(bbb,aaa,"DJI_0009.jpg").string();
////                            Log.e(TAG,"result="+result);
//                        } catch (Exception e) {
////                            Toast.makeText(getApplicationContext(), "error="+e.toString(), Toast.LENGTH_LONG).show();
//                            Log.e(TAG, e.toString());
//                            e.printStackTrace();
//                        }
//                    }
//                }).start();


                break;
            }

            case R.id.btn_gaode: {
                //去高德地图
//                Intent intent = new Intent(this, GaodeMainActivity.class);
//                startActivity(intent);
                //下载
                initMediaManager();

//                camera.setMode(SettingsDefinitions.CameraMode.MEDIA_DOWNLOAD, new CommonCallbacks.CompletionCallback() {
//                    @Override
//                    public void onResult(DJIError djiError) {
//                        Log.d("cameraMode",djiError!=null?djiError.toString():"success");
//                    }
//                });

                break;
            }
            case R.id.btn_simulator: {
                //去控制飞机
//                Intent intent = new Intent(this, SimulatorMainActivity.class);
//                startActivity(intent);
//                isLiveShowOn();
                startLiveShow();
                break;
            }

            case R.id.btn_login:
//                loginAccount();

                DJISDKManager.getInstance().getLiveStreamManager().stopStream();
                Toast.makeText(getApplicationContext(), "Stop Live Show", Toast.LENGTH_SHORT).show();

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

    void startLiveShow() {
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
            return;
        }
        new Thread() {
            @Override
            public void run() {
                DJISDKManager.getInstance().getLiveStreamManager().setLiveUrl(liveShowUrl);
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
                    }
                });

              /*  ToastUtils.setResultToToast("startLive:" + result +
                        "\n isVideoStreamSpeedConfigurable:" + DJISDKManager.getInstance().getLiveStreamManager().isVideoStreamSpeedConfigurable() +
                        "\n isLiveAudioEnabled:" + DJISDKManager.getInstance().getLiveStreamManager().isLiveAudioEnabled());*/
            }
        }.start();
    }

    private void isLiveShowOn() {
        if (!isLiveStreamManagerOn()) {
            return;
        }
        //wang
        Toast.makeText(getApplicationContext(), "Is Live Show On:" + DJISDKManager.getInstance().getLiveStreamManager().isStreaming(), Toast.LENGTH_SHORT).show();
        //ToastUtils.setResultToToast("Is Live Show On:" + DJISDKManager.getInstance().getLiveStreamManager().isStreaming());
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
        } else {
            havePermission = true;

            if (TextUtils.isEmpty(MApplication.EQUIPMENT_ID) || TextUtils.isEmpty(MApplication.UPLOAD_URL)) {
                String text_message = FileUtils.readString(file.getAbsolutePath(), "utf-8");

                if (!TextUtils.isEmpty(text_message)) {
                    TextMessageBean textMessageBean = gson.fromJson(text_message, TextMessageBean.class);
                    String mobile_Id = RsaUtil.encrypt(textMessageBean.getEquip_id());
                    MApplication.EQUIPMENT_ID = mobile_Id;
                    MApplication.UPLOAD_URL = textMessageBean.getUpload_url();
                    Log.d("FileUtils", "FileUtils=" + mobile_Id);
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(10000);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "配置文件为空,请去添加", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                finish();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
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
            havePermission = true;
            startSDKRegistration();
            loginAccount();
            if (TextUtils.isEmpty(MApplication.EQUIPMENT_ID) || TextUtils.isEmpty(MApplication.UPLOAD_URL)) {
                String text_message = FileUtils.readString(file.getAbsolutePath(), "utf-8");

                if (!TextUtils.isEmpty(text_message)) {
                    TextMessageBean textMessageBean = gson.fromJson(text_message, TextMessageBean.class);
                    String mobile_Id = RsaUtil.encrypt(textMessageBean.getEquip_id());
                    MApplication.EQUIPMENT_ID = mobile_Id;
                    MApplication.UPLOAD_URL = textMessageBean.getUpload_url();
                    Log.d("FileUtils", "FileUtils=" + mobile_Id);
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(10000);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "配置文件为空,请去添加", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                finish();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        } else {
            showToast("Missing permissions!!!");
            havePermission = false;
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
            initBattery();
            initGimbal();
            addListener();
            planeStatusHandler.post(planeStatusTask);//立即调用获取飞机当前是否可以起飞
            initErrorLog();//初始化错误日志

        }
    };

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
            mFlightController.setRollPitchControlMode(RollPitchControlMode.VELOCITY);
            mFlightController.setYawControlMode(YawControlMode.ANGULAR_VELOCITY);
            mFlightController.setVerticalControlMode(VerticalControlMode.VELOCITY);
            mFlightController.setRollPitchCoordinateSystem(FlightCoordinateSystem.BODY);

            //获取飞行状态
            mFlightController.setStateCallback(new FlightControllerState.Callback() {
                @Override
                public void onUpdate(FlightControllerState flightControllerState) {
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
                        communication_isFlying.setMethod(RsaUtil.encrypt(Constant.IS_FLYING));
                        communication_isFlying.setResult(gson.toJson(beans, StringsBean.class));
                        NettyClient.getInstance().sendMessage(communication_isFlying, null);
                    }

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

                        Log.d(TAG, "=====================================================");
                        Log.d(TAG, "卫星数=" + flightControllerState.getSatelliteCount() + "");
//                        Log.d(TAG, "windDirection=" + windDirection + "");
//                        Log.d(TAG, "windSpeed=" + windSpeed + "");
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
                        communication_flightController.setMethod(RsaUtil.encrypt("flightController"));
                        communication_flightController.setResult(gson.toJson(flightControllerBean, FlightControllerBean.class));
//                        Log.d("MMMMM",communication_flightController.toString());
                        NettyClient.getInstance().sendMessage(communication_flightController, null);

                    }


                }
            });


            //IMU状态
            mFlightController.setIMUStateCallback(new IMUState.Callback() {
                @Override
                public void onUpdate(IMUState imuState) {
                    Log.d("IMU_index", imuState.getIndex() + "");
                    //加速度计
                    Log.d("IMU_ARS", imuState.getAccelerometerState() + "");
                    Log.d("IMU_ARV", imuState.getAccelerometerValue() + "");
                    //陀螺仪
                    Log.d("IMU_GYS", imuState.getGyroscopeState() + "");
                    Log.d("IMU_GYV", imuState.getGyroscopeValue() + "");
                    IMUStateBean imuStateBean = null;
                    if (imuStateBean == null) {
                        imuStateBean = new IMUStateBean();
                    }
                    imuStateBean.setIMU_index(imuState.getIndex() + "");
                    imuStateBean.setIMU_ARS(imuState.getAccelerometerState() + "");
                    imuStateBean.setIMU_ARV(imuState.getAccelerometerValue() + "");
                    imuStateBean.setIMU_GYS(imuState.getGyroscopeState() + "");
                    imuStateBean.setIMU_GYV(imuState.getGyroscopeValue() + "");

                    if (communication_imu_status == null) {
                        communication_imu_status = new Communication();
                    }
                    communication_imu_status.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                    communication_imu_status.setEquipmentId(MApplication.EQUIPMENT_ID);
                    communication_imu_status.setMethod(RsaUtil.encrypt(IMU_STATUS));
                    communication_imu_status.setResult(gson.toJson(imuStateBean, IMUStateBean.class));
                    NettyClient.getInstance().sendMessage(communication_imu_status, null);
                }
            });
            //低电量值，严重低电量，智能返回（都在电池ui那块）
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
                        StringsBean upLinkBean = new StringsBean();
                        upLinkBean.setValue(level + "");
                        if (communication_up_link == null) {
                            communication_up_link = new Communication();
                        }
                        communication_up_link.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                        communication_up_link.setEquipmentId(MApplication.EQUIPMENT_ID);
                        communication_up_link.setMethod(RsaUtil.encrypt(Constant.UP_LOAD_SIGNAL));
                        communication_up_link.setResult(gson.toJson(upLinkBean, StringsBean.class));
                        NettyClient.getInstance().sendMessage(communication_up_link, null);
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
                        StringsBean upLinkBean = new StringsBean();
                        upLinkBean.setValue(level + "");
                        if (communication_down_link == null) {
                            communication_down_link = new Communication();
                        }
                        communication_down_link.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                        communication_down_link.setEquipmentId(MApplication.EQUIPMENT_ID);
                        communication_down_link.setMethod(RsaUtil.encrypt(Constant.DOWN_LOAD_SIGNAL));
                        communication_down_link.setResult(gson.toJson(upLinkBean, StringsBean.class));
                        NettyClient.getInstance().sendMessage(communication_down_link, null);
                    }
                });
            }

            mFlightController.setASBInformationCallback(new AirSenseSystemInformation.Callback() {
                @Override
                public void onUpdate(AirSenseSystemInformation information) {
                    final StringBuffer sb = new StringBuffer();
                    addLineToSB(sb, "ADSB Info", "");
                    if (information.getWarningLevel() != null) {
                        addLineToSB(sb, "WarningLevel", "" + information.getWarningLevel().name());
                    }
                    if (information.getAirplaneStates() != null && information.getAirplaneStates().length > 0) {
                        for (int i = 0; i < information.getAirplaneStates().length; i++) {

                            AirSenseAirplaneState state = information.getAirplaneStates()[i];
                            if (state != null) {
                                addLineToSB(sb, "", "");
                                addLineToSB(sb, "flight ID", "" + i);
                                addLineToSB(sb, "ICAO Code", "" + state.getCode());
                                addLineToSB(sb, "Heading", "" + state.getHeading());
                                addLineToSB(sb, "Direction", "" + state.getRelativeDirection());
                                addLineToSB(sb, "Distance", "" + state.getDistance());
                                addLineToSB(sb, "Warning level", "" + state.getWarningLevel());
                            }
                        }
                    }
                    Log.d("adsbStateTV", sb.toString());
                }
            });

            //遥控器
            mRemoteController = aircraft.getRemoteController();

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
                //获取感知试图
                mFlightAssistant.setVisualPerceptionInformationCallback(new CommonCallbacks.CompletionCallbackWith<PerceptionInformation>() {
                    @Override
                    public void onSuccess(PerceptionInformation perceptionInformation) {
                        if (fastClick.PerceptionClick()) {
                            PerceptionBean perceptionBean = new PerceptionBean();
                            perceptionBean.setDownwardObstacleDistance(perceptionInformation.getDownwardObstacleDistance());
                            perceptionBean.setUpwardObstacleDistance(perceptionInformation.getUpwardObstacleDistance());
                            perceptionBean.setDistances(perceptionInformation.getDistances());
                            if (communication_perception_data == null) {
                                communication_perception_data = new Communication();
                            }
                            communication_perception_data.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                            communication_perception_data.setEquipmentId(MApplication.EQUIPMENT_ID);
                            communication_perception_data.setMethod(RsaUtil.encrypt(Constant.PERCEPTION_DATA));
                            communication_perception_data.setResult(gson.toJson(perceptionBean, PerceptionBean.class));
                            NettyClient.getInstance().sendMessage(communication_perception_data, null);
                        }
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });

//                mFlightAssistant.setObstacleAvoidanceSensorStateListener(new CommonCallbacks.CompletionCallbackWith<ObstacleAvoidanceSensorState>() {
//                    @Override
//                    public void onSuccess(ObstacleAvoidanceSensorState obstacleAvoidanceSensorState) {
//                        Log.d("mFlightAssistant",obstacleAvoidanceSensorState.isBackwardObstacleAvoidanceSensorEnabled()+"");
//
//                    }
//
//                    @Override
//                    public void onFailure(DJIError djiError) {
//                        Log.d("mFlightAssistant",djiError+"");
//                    }
//                });
                mFlightController.getFlightAssistant().setObstacleAvoidanceSensorStateListener(new CommonCallbacks.CompletionCallbackWith<ObstacleAvoidanceSensorState>() {
                    @Override
                    public void onSuccess(ObstacleAvoidanceSensorState obstacleAvoidanceSensorState) {
                        ObstacleAvoidanceSensorStateBean bean = new ObstacleAvoidanceSensorStateBean();
                        bean.setAreObstacleAvoidanceSensorsInHorizo​​ntalDirectionEnabled(obstacleAvoidanceSensorState.areObstacleAvoidanceSensorsInHorizontalDirectionEnabled());
                        bean.setAreObstacleAvoidanceSensorsInHorizo​​ntalDirectionWorking(obstacleAvoidanceSensorState.areObstacleAvoidanceSensorsInHorizontalDirectionWorking());
                        bean.setAreObstacleAvoidanceSensorsInVerticalDirectionEnabled(obstacleAvoidanceSensorState.areObstacleAvoidanceSensorsInVerticalDirectionEnabled());
                        bean.setAreObstacleAvoidanceSensorsInVerticalDirectionWorking(obstacleAvoidanceSensorState.areObstacleAvoidanceSensorsInVerticalDirectionWorking());
                        bean.setUpwardObstacleAvoidanceSensorEnabled(obstacleAvoidanceSensorState.isUpwardObstacleAvoidanceSensorEnabled());
                        bean.setUpwardObstacleAvoidanceSensorWorking(obstacleAvoidanceSensorState.isUpwardObstacleAvoidanceSensorWorking());
                        bean.setLeftSideObstacleAvoidanceSensorEnabled(obstacleAvoidanceSensorState.isLeftSideObstacleAvoidanceSensorEnabled());
                        bean.setLeftSideObstacleAvoidanceSensorWorking(obstacleAvoidanceSensorState.isLeftSideObstacleAvoidanceSensorWorking());
                        bean.setRightSideObstacleAvoidanceSensorEnabled(obstacleAvoidanceSensorState.isRightSideObstacleAvoidanceSensorEnabled());
                        bean.setRightSideObstacleAvoidanceSensorWorking(obstacleAvoidanceSensorState.isRightSideObstacleAvoidanceSensorWorking());
                        bean.setBackwardObstacleAvoidanceSensorEnabled(obstacleAvoidanceSensorState.isBackwardObstacleAvoidanceSensorEnabled());
                        bean.setBackwardObstacleAvoidanceSensorWorking(obstacleAvoidanceSensorState.isBackwardObstacleAvoidanceSensorWorking());

                        if (communication_ObstacleAvoidanceSensorState == null) {
                            communication_ObstacleAvoidanceSensorState = new Communication();
                        }
                        communication_ObstacleAvoidanceSensorState.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                        communication_ObstacleAvoidanceSensorState.setEquipmentId(MApplication.EQUIPMENT_ID);
                        communication_ObstacleAvoidanceSensorState.setMethod(RsaUtil.encrypt(Constant.OASS));
                        communication_ObstacleAvoidanceSensorState.setResult(gson.toJson(bean, ObstacleAvoidanceSensorStateBean.class));
                        NettyClient.getInstance().sendMessage(communication_ObstacleAvoidanceSensorState, null);
                    }

                    @Override
                    public void onFailure(DJIError djiError) {
                    }
                });

                mFlightAssistant.getObstaclesAvoidanceDistance(Upward, new CommonCallbacks.CompletionCallbackWith<Float>() {
                    @Override
                    public void onSuccess(Float aFloat) {
                        avoidanceDistanceUpward = aFloat + "";
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });
                mFlightAssistant.getObstaclesAvoidanceDistance(Downward, new CommonCallbacks.CompletionCallbackWith<Float>() {
                    @Override
                    public void onSuccess(Float aFloat) {
                        avoidanceDistanceDownward = aFloat + "";
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });
                mFlightAssistant.getObstaclesAvoidanceDistance(Horizontal, new CommonCallbacks.CompletionCallbackWith<Float>() {
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
                        activeObstacleAvoidance=aBoolean;
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });

            }

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
//

        }
    }

    public static void addLineToSB(StringBuffer sb, String name, Object value) {
        if (sb == null) return;
        sb.
                append((name == null || "".equals(name)) ? "" : name + ": ").
                append(value == null ? "" : value + "").
                append("\n");
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

        camera.getCameraVideoStreamSource(new CommonCallbacks.CompletionCallbackWith<CameraVideoStreamSource>() {
            @Override
            public void onSuccess(CameraVideoStreamSource cameraVideoStreamSource) {
                webInitializationBean.setCurrentLens(cameraVideoStreamSource.value() + "");
            }

            @Override
            public void onFailure(DJIError djiError) {

            }
        });

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
                communication_StorageState.setMethod(RsaUtil.encrypt(Constant.STORAGE_STATE));
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

            //获取变焦参数
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


//        FPVDemoApplication.getProductInstance().getAirLink().getLightbridgeLink().setAircraftAntennaRSSICallback(new LightbridgeLink.AntennaRSSICallback() {
//            @Override
//            public void (LightbridgeAntennaRSSI lightbridgeAntennaRSSI) {
//                int Antenna1= lightbridgeAntennaRSSI.getAntenna1();
//                int Antenna2= lightbridgeAntennaRSSI.getAntenna2();
//                Log.d("Antenna", "Antenna1="+Antenna1+" Antenna2="+Antenna2);
//            }
//        });
//        FPVDemoApplication.getProductInstance().getAirLink().getLightbridgeLink().setRemoteControllerAntennaRSSICallback(new LightbridgeLink.AntennaRSSICallback() {
//            @Override
//            public void onUpdate(LightbridgeAntennaRSSI lightbridgeAntennaRSSI) {
//                int Antenna1= lightbridgeAntennaRSSI.getAntenna1();
//                int Antenna2= lightbridgeAntennaRSSI.getAntenna2();
//                Log.d("RemoteControl", "Antenna1="+Antenna1+" Antenna2="+Antenna2);
//
//            }
//        });

//        FPVDemoApplication.getProductInstance().getAirLink().getOcuSyncLink().setVideoDataRateCallback(new OcuSyncLink.VideoDataRateCallback() {
//            @Override
//            public void onUpdate(float v) {
//                Log.d("OcuSyncLink", "OcuSyncLink"+v);
//            }
//        });

        //获取实时状态
//        camera.setSystemStateCallback(new SystemState.Callback() {
//            @Override
//            public void onUpdate(SystemState systemState) {
//            }
//        });

//        camera.getCapabilities().addDJICameraParametersListener(new Capabilities.DJICameraParametersListener() {
//            @Override
//            public void onCameraISORangeChange(SettingsDefinitions.ISO[] isos) {
//
//            }
//
//            @Override
//            public void onCameraExposureCompensationRangeChange(SettingsDefinitions.ExposureCompensation[] exposureCompensations) {
//
//            }
//
//            @Override
//            public void onCameraExposureModeRangeChange(SettingsDefinitions.ExposureMode[] exposureModes) {
//
//            }
//
//            @Override
//            public void onCameraShutterSpeedRangeChange(SettingsDefinitions.ShutterSpeed[] shutterSpeeds) {
//
//            }
//
//            @Override
//            public void onCameraModeRangeChange(SettingsDefinitions.CameraMode[] cameraModes) {
//
//            }
//
//            @Override
//            public void onFlatCameraModeRangeChange(SettingsDefinitions.FlatCameraMode[] flatCameraModes) {
//
//            }
//
//            @Override
//            public void onCameraVideoResolutionAndFrameRateRangeChange(ResolutionAndFrameRate[] resolutionAndFrameRates) {
//                Log.d("CCCCC","====================================");
//                for (int i = 0; i <resolutionAndFrameRates.length ; i++) {
//                    Log.d("CCCCC","resolution="+resolutionAndFrameRates[i].getResolution());
//                    Log.d("CCCCC","frameRate="+resolutionAndFrameRates[i].getFrameRate());
//                }
//            }
//
//            @Override
//            public void onCameraApertureRangeChange(SettingsDefinitions.Aperture[] apertures) {
//
//            }
//
//            @Override
//            public void onCameraSSDRawVideoResolutionRangeChange(SettingsDefinitions.VideoResolution[] videoResolutions) {
//
//            }
//        });
        //获取当前可设置的参数
//        Log.d("CCCCC","====================================");
//        for (int i = 0; i <camera.getCapabilities().videoResolutionAndFrameRateRange().length ; i++) {
//            Log.d("CCCCC","resolution="+camera.getCapabilities().videoResolutionAndFrameRateRange()[i].getResolution());
//            Log.d("CCCCC","frameRate="+camera.getCapabilities().videoResolutionAndFrameRateRange()[i].getFrameRate());
//        }
        //


//        camera.getVideoStandard(new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.VideoStandard>() {
//            @Override
//            public void onSuccess(SettingsDefinitions.VideoStandard videoStandard) {
//            }
//
//            @Override
//            public void onFailure(DJIError djiError) {
//
//            }
//        });
//        camera.getVideoResolutionAndFrameRate(new CommonCallbacks.CompletionCallbackWith<ResolutionAndFrameRate>() {
//            @Override
//            public void onSuccess(ResolutionAndFrameRate resolutionAndFrameRate) {
//                Log.d("CCCCC","getFrameRate="+resolutionAndFrameRate.getFrameRate()+"getResolution="+resolutionAndFrameRate.getResolution());
//            }
//
//            @Override
//            public void onFailure(DJIError djiError) {
//
//            }
//        });

//        //设置视频标准
//        camera.getLens(0).setVideoStandard(SettingsDefinitions.VideoStandard.PAL, new CommonCallbacks.CompletionCallback() {
//            @Override
//            public void onResult(DJIError djiError) {
//
//            }
//        });
//
//        //相机分辨率
//        camera.getLens(0).setVideoResolutionAndFrameRate(new ResolutionAndFrameRate(RESOLUTION_4096x2160, FRAME_RATE_24_FPS), new CommonCallbacks.CompletionCallback() {
//            @Override
//            public void onResult(DJIError djiError) {
//
//            }
//        });
//        //照片尺寸
//        camera.getLens(0).setPhotoAspectRatio(RATIO_4_3, new CommonCallbacks.CompletionCallback() {
//            @Override
//            public void onResult(DJIError djiError) {
//
//            }
//        });
//        //照片格式
//        camera.getLens(0).setPhotoFileFormat(SettingsDefinitions.PhotoFileFormat.JPEG, new CommonCallbacks.CompletionCallback() {
//            @Override
//            public void onResult(DJIError djiError) {
//
//            }
//        });

//        List<Lens> lens=camera.getLenses();
//


        Log.d("NNNNN", "camera.getLenses().size()=" + camera.getLenses().size());
        for (int i = 0; i < camera.getLenses().size(); i++) {
            Log.d("NNNNN", i + "getCameraIndex()" + camera.getLenses().get(i).getCameraIndex());
            Log.d("NNNNN", i + "getType()" + camera.getLenses().get(i).getType());
            Log.d("NNNNN", i + "getDisplayName()" + camera.getLenses().get(i).getDisplayName());
            Log.d("NNNNN", i + "getCapabilities()" + camera.getLenses().get(i).getCapabilities());
            camera.getLenses().get(i).getISO(new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.ISO>() {
                @Override
                public void onSuccess(SettingsDefinitions.ISO iso) {
                    Log.d("NNNNN", "NNNNNiso()" + iso.value());
                }

                @Override
                public void onFailure(DJIError djiError) {
                    Log.d("NNNNN", "NNNNNiso()" + djiError);
                }
            });
        }
    }

    String battery_one = "0", battery_two = "0", battery_voltages_one = "0", battery_voltages_two = "0",battery_temperature_one="",battery_temperature_two="";
    List<Float> battery_list_one=new ArrayList<>();
    List<Float> battery_list_two=new ArrayList<>();
    String battery_discharges_one="",battery_discharges_two="";

    private void initBattery() {
        battery = FPVDemoApplication.getProductInstance().getBattery();
        BatteryKey battery_per_one = BatteryKey.create(BatteryKey.CHARGE_REMAINING_IN_PERCENT);
        BatteryKey battery_per_two = BatteryKey.create(BatteryKey.CHARGE_REMAINING_IN_PERCENT, 1);
        BatteryKey battery_voltage_one = BatteryKey.create(BatteryKey.CELL_VOLTAGES);
        BatteryKey battery_voltage_two = BatteryKey.create(BatteryKey.CELL_VOLTAGES, 1);
        BatteryKey temperature_one = BatteryKey.create(BatteryKey.TEMPERATURE);
        BatteryKey temperature_two = BatteryKey.create(BatteryKey.TEMPERATURE,1);
        BatteryKey discharges_one = BatteryKey.create(BatteryKey.NUMBER_OF_DISCHARGES);
        BatteryKey discharges_two = BatteryKey.create(BatteryKey.NUMBER_OF_DISCHARGES,1);
        KeyManager.getInstance().addListener(battery_per_one, new KeyListener() {
            @Override
            public void onValueChange(Object o, Object o1) {
                if (fastClick.batteryClick()) {
                    battery_one = o1.toString();
                    submitBatteryPersentAndV();
                }

            }
        });
        KeyManager.getInstance().addListener(battery_per_two, new KeyListener() {
            @Override
            public void onValueChange(Object o, Object o1) {
                if (fastClick.batteryClick()) {
                    battery_two = o1.toString();
                    submitBatteryPersentAndV();
                }
            }
        });

        KeyManager.getInstance().addListener(battery_voltage_one, new KeyListener() {
            @Override
            public void onValueChange(Object o, Object o1) {
                if (fastClick.batteryClick()) {
                    battery_voltages_one = getMinVoltage(o1) + "";
                    if (o1 != null && o1 instanceof Integer[]) {
                        battery_list_one.add((float) o1 * 1.0F / 1000.0F);
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
                        battery_list_two.add((float) o1 * 1.0F / 1000.0F);
                    }
                    submitBatteryPersentAndV();
                }
            }
        });
        KeyManager.getInstance().addListener(temperature_one, new KeyListener() {
            @Override
            public void onValueChange(Object o, Object o1) {
                battery_temperature_one=o1+"";
                submitBatteryPersentAndV();
            }
        });
        KeyManager.getInstance().addListener(temperature_two, new KeyListener() {
            @Override
            public void onValueChange(Object o, Object o1) {
                battery_temperature_two=o1+"";
                submitBatteryPersentAndV();
            }
        });
        KeyManager.getInstance().addListener(discharges_one, new KeyListener() {
            @Override
            public void onValueChange(Object o, Object o1) {
                battery_discharges_one=o1+"";
                submitBatteryPersentAndV();
            }
        });
        KeyManager.getInstance().addListener(discharges_two, new KeyListener() {
            @Override
            public void onValueChange(Object o, Object o1) {
                battery_discharges_two=o1+"";
                submitBatteryPersentAndV();
            }
        });
    }
    String gimbal_pitch_speed="",gimbal_yaw_speed="";
    private void initGimbal(){
        gimbal= FPVDemoApplication.getProductInstance().getGimbal();
        gimbal.getControllerSpeedCoefficient(PITCH, new CommonCallbacks.CompletionCallbackWith<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                gimbal_pitch_speed=integer+"";
            }

            @Override
            public void onFailure(DJIError djiError) {

            }
        });
        gimbal.getControllerSpeedCoefficient(YAW, new CommonCallbacks.CompletionCallbackWith<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                gimbal_yaw_speed=integer+"";
            }

            @Override
            public void onFailure(DJIError djiError) {

            }
        });

    }

    private void submitBatteryPersentAndV() {
        BatteryPersentAndVoltageBean batteryPersentAndVoltageBean = new BatteryPersentAndVoltageBean();
        batteryPersentAndVoltageBean.setPersentOne(battery_one);
        batteryPersentAndVoltageBean.setPersentTwo(battery_two);
        batteryPersentAndVoltageBean.setVoltageOne(battery_voltages_one);
        batteryPersentAndVoltageBean.setVoltageTwo(battery_voltages_two);
        batteryPersentAndVoltageBean.setBattery_list_one(battery_list_one);
        batteryPersentAndVoltageBean.setBattery_list_two(battery_list_two);
        batteryPersentAndVoltageBean.setBattery_temperature_one(battery_temperature_one);
        batteryPersentAndVoltageBean.setBattery_temperature_two(battery_temperature_two);
        batteryPersentAndVoltageBean.setBattery_discharges_one(battery_discharges_one);
        batteryPersentAndVoltageBean.setBattery_discharges_two(battery_discharges_two);
        if (communication_battery == null) {
            communication_battery = new Communication();
        }
        communication_battery.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
        communication_battery.setEquipmentId(MApplication.EQUIPMENT_ID);
        communication_battery.setMethod(RsaUtil.encrypt(Constant.BatteryPAV));
        communication_battery.setResult(gson.toJson(batteryPersentAndVoltageBean, BatteryPersentAndVoltageBean.class));
        NettyClient.getInstance().sendMessage(communication_battery, null);
    }


    private void initMediaManager() {
        if (FPVDemoApplication.getProductInstance() == null) {
            mediaFileList.clear();
            Log.e(TAG, "Product disconnected");
            return;
        } else {
            if (null != FPVDemoApplication.getCameraInstance() && FPVDemoApplication.getCameraInstance().isMediaDownloadModeSupported()) {
                mMediaManager = FPVDemoApplication.getCameraInstance().getMediaManager();
                if (null != mMediaManager) {
                    mMediaManager.addUpdateFileListStateListener(this.updateFileListStateListener);

//                    调用的setMode()方法，Camera并将CameraMode设置为MEDIA_DOWNLOAD。
//                    并调用该getFileList()方法以获取媒体文件列表。
//                    最后，初始化FetchMediaTaskScheduler来安排获取媒体文件任务。
//                    camera.setMode(SettingsDefinitions.CameraMode.MEDIA_DOWNLOAD, new CommonCallbacks.CompletionCallback() {
//                        @Override
//                        public void onResult(DJIError error) {
//                            if (error == null) {
//                                Toast.makeText(ConnectionActivity.this, "Set cameraMode success", Toast.LENGTH_SHORT).show();
//                                getFileList();
//                            } else {
//                                Log.e(TAG, "Set cameraMode failed " + error.toString());
//                                Toast.makeText(ConnectionActivity.this, "Set cameraMode failed " + error.toString(), Toast.LENGTH_LONG).show();
//                                Communication communication = new Communication();
//                                communication.setResult(error.getDescription());
//                                communication.setCode(-1);
//                                communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
//                                NettyClient.getInstance().sendMessage(communication, null);
//                            }
//                        }
//                    });

                    camera.enterPlayback(new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (djiError == null) {
                                Toast.makeText(ConnectionActivity.this, "Set cameraMode success", Toast.LENGTH_SHORT).show();
                                getFileList();
                            } else {
                                Log.e(TAG, "Set cameraMode failed " + djiError.toString());
                                Toast.makeText(ConnectionActivity.this, "Set cameraMode failed " + djiError.toString(), Toast.LENGTH_LONG).show();
                                Communication communication = new Communication();
                                communication.setResult(djiError.getDescription());
                                communication.setCode(-1);
                                communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                                NettyClient.getInstance().sendMessage(communication, null);
                            }
                        }
                    });

                    if (mMediaManager.isVideoPlaybackSupported()) {
                        Log.e(TAG, "Camera support video playback!");
                    } else {
                        Log.e(TAG, "Camera does not support video playback!");
                    }
//                    scheduler = mMediaManager.getScheduler();
                }

            } else if (null != FPVDemoApplication.getCameraInstance()
                    && !FPVDemoApplication.getCameraInstance().isMediaDownloadModeSupported()) {
                Log.e(TAG, "Media Download Mode not Supported");
            }
        }
        return;
    }

    /**
     * 在该getFileList()方法中，我们获取最新的mMediaManager对象并检查其是否不为null。
     * 然后检查currentFileListState变量的值。如果状态既不是SYNCING也不为DELETING，
     * 则调用的refreshFileListOfStorageLocation()方法MediaManager以从SD卡刷新文件列表
     */
    private void getFileList() {
        mMediaManager = FPVDemoApplication.getCameraInstance().getMediaManager();
        if (mMediaManager != null) {

            if ((currentFileListState == MediaManager.FileListState.SYNCING) || (currentFileListState == MediaManager.FileListState.DELETING)) {
                Toast.makeText(ConnectionActivity.this, "Media Manager is busy.", Toast.LENGTH_SHORT).show();
            } else {

                mMediaManager.refreshFileListOfStorageLocation(SettingsDefinitions.StorageLocation.SDCARD, new CommonCallbacks.CompletionCallback() {
                    //                    在onResult()回调方法中，如果没有错误，则检查currentFileListState值是否不相等，
//                    MediaManager.FileListState.INCOMPLETE
//                    然后重置mediaFileList列表，lastClickViewIndex和lastClickView变量。
                    @Override
                    public void onResult(DJIError djiError) {
                        if (null == djiError) {
//                            hideProgressDialog();

                            //Reset data
                            if (currentFileListState != MediaManager.FileListState.INCOMPLETE) {
                                mediaFileList.clear();
//                                lastClickViewIndex = -1;
//                                lastClickView = null;
                            }
//                            mediaFileList根据创建的时间对中的媒体文件进行排序。然后，
//                            调用的resume()方法FetchMediaTaskScheduler以恢复调度程序，
//                            并getThumbnails()在onResult()回调方法中调用该方法。
//                            如果存在错误，请调用该hideProgressDialog()方法以隐藏进度对话框。

                            mediaFileList = mMediaManager.getSDCardFileListSnapshot();
                            Collections.sort(mediaFileList, new Comparator<MediaFile>() {
                                @Override
                                public int compare(MediaFile lhs, MediaFile rhs) {
                                    if (lhs.getTimeCreated() < rhs.getTimeCreated()) {
                                        return 1;
                                    } else if (lhs.getTimeCreated() > rhs.getTimeCreated()) {
                                        return -1;
                                    }
                                    return 0;
                                }
                            });

                            downloadPic();
                            Toast.makeText(ConnectionActivity.this, "拿到list去下载" + mediaFileList.size(), Toast.LENGTH_SHORT).show();
//                            scheduler.resume(new CommonCallbacks.CompletionCallback() {
//                                @Override
//                                public void onResult(DJIError error) {
//                                    if (error == null) {
//                                        getThumbnails();
//                                    }
//                                }
//                            });
                        } else {
//                            hideProgressDialog();
//                            setResultToToast("Get Media File List Failed:" + djiError.getDescription());
                            if (communication_takePhoto == null) {
                                communication_takePhoto = new Communication();
                            }
                            communication_takePhoto.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                            communication_takePhoto.setEquipmentId(MApplication.EQUIPMENT_ID);
                            communication_takePhoto.setCode(-1);
                            communication_takePhoto.setMethod(RsaUtil.encrypt(Constant.PHOTO_UP_LOAD));
                            communication_takePhoto.setResult(djiError.getDescription());
                            NettyClient.getInstance().sendMessage(communication_takePhoto, null);
                        }
                    }
                });
            }
        }
    }

    private void downloadPic() {
        if ((mediaFileList.get(0).getMediaType() == MediaFile.MediaType.PANORAMA)
                || (mediaFileList.get(0).getMediaType() == MediaFile.MediaType.SHALLOW_FOCUS)) {
            return;
        }
//        if(mediaFileList.get(0).getMediaType()==MediaFile.MediaType.MOV||mediaFileList.get(0).getMediaType()==MediaFile.MediaType.MP4){
//            downloadFileType="0";//视频
//        }else{
//            downloadFileType="1";//图片
//        }
        FileName = mediaFileList.get(0).getFileName();
        mediaFileList.get(0).fetchFileData(destDir, null, new DownloadListener<String>() {
            @Override
            public void onFailure(DJIError error) {
                Toast.makeText(ConnectionActivity.this, "Download File Failed" + error.getDescription(), Toast.LENGTH_SHORT).show();
                if (communication_takePhoto == null) {
                    communication_takePhoto = new Communication();
                }
                communication_takePhoto.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                communication_takePhoto.setEquipmentId(MApplication.EQUIPMENT_ID);
                communication_takePhoto.setCode(-1);
                communication_takePhoto.setMethod(RsaUtil.encrypt(Constant.PHOTO_UP_LOAD));
                communication_takePhoto.setResult(error.getDescription());
                NettyClient.getInstance().sendMessage(communication_takePhoto, null);
            }

            @Override
            public void onProgress(long total, long current) {
            }

            @Override
            public void onRateUpdate(long total, long current, long persize) {
                int tmpProgress = (int) (1.0 * current / total * 100);
                if (tmpProgress != currentProgress) {
                    currentProgress = tmpProgress;
                    btn_login.setText(currentProgress + "");
                }
            }

            @Override
            public void onStart() {
                currentProgress = -1;
//                ShowDownloadProgressDialog();
            }

            @Override
            public void onSuccess(String filePath) {
//                HideDownloadProgressDialog();
//                setResultToToast();
                Toast.makeText(ConnectionActivity.this, "Download File Success" + ":" + filePath, Toast.LENGTH_LONG).show();
                currentProgress = -1;
                try {

                    File downloadFile = new File(filePath + "/" + FileName);
                    Log.e(TAG, "fileallname=" + filePath + "/" + FileName);
                    if (FileName.endsWith(".JPG") || FileName.endsWith(".jpg") || FileName.endsWith(".JPEG") || FileName.endsWith(".jpeg")) {//图片压缩
                        Luban.with(ConnectionActivity.this)
                                .load(downloadFile)
                                .ignoreBy(100)
                                .setTargetDir(Environment.getExternalStorageDirectory().getPath())
//                            .filter(new CompressionPredicate() {
//                                @Override
//                                public boolean apply(String path) {
//                                    return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
//                                }
//                            })
                                .setCompressListener(new OnCompressListener() {
                                    @Override
                                    public void onStart() {
                                        // 压缩开始前调用，可以在方法内启动 loading UI
                                    }

                                    @Override
                                    public void onSuccess(File file) {
                                        //压缩成功后调用，返回压缩后的图片文件
                                        Toast.makeText(ConnectionActivity.this, "压缩成功", Toast.LENGTH_SHORT).show();
                                        Log.e(TAG, "压缩onSuccess=" + file.getPath());
                                        String result = null;
                                        try {
                                            result = ClientUploadUtils.upload(MApplication.UPLOAD_URL, file, file.getName()).toString();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        Toast.makeText(ConnectionActivity.this, "result:" + result, Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        //当压缩过程出现问题时调用
                                        Log.e(TAG, "压缩onError=" + e.toString());
                                    }
                                }).launch();
                    } else {
                        String result = null;
                        try {
                            result = ClientUploadUtils.upload(MApplication.UPLOAD_URL, downloadFile, downloadFile.getName()).toString();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(ConnectionActivity.this, "result:" + result, Toast.LENGTH_SHORT).show();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
//                if(downloadFileType.equals("0")){//视频
//
//                }else if(downloadFileType.equals("1")){//图片
//                    String base64=ImageUtils.bitmapToBase64(ImageUtils.returnBitMap(filePath));
//                    if (communication_takePhoto == null) {
//                        communication_takePhoto = new Communication();
//                    }
//                    communication_takePhoto.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
//                    communication_takePhoto.setEquipmentId(MApplication.EQUIPMENT_ID);
//                    communication_takePhoto.setCode(200);
//                    communication_takePhoto.setMethod(RsaUtil.encrypt(Constant.PHOTO_UP_LOAD));
//                    communication_takePhoto.setResult(base64);
//                    NettyClient.getInstance().sendMessage(communication_takePhoto, null);
//                    ArrayList<MediaFile> fileToDelete = new ArrayList<MediaFile>();
//                    if (mediaFileList.size() > 0) {
//                        fileToDelete.add(mediaFileList.get(0));
//                        mMediaManager.deleteFiles(fileToDelete, new CommonCallbacks.CompletionCallbackWithTwoParam<List<MediaFile>, DJICameraError>() {
//                            @Override
//                            public void onSuccess(List<MediaFile> x, DJICameraError y) {
//                                Log.e(TAG, "Delete file success");
//                                runOnUiThread(new Runnable() {
//                                    public void run() {
//                                        MediaFile file = mediaFileList.remove(0);
////
////                                    //Reset select view
////                                    lastClickViewIndex = -1;
////                                    lastClickView = null;
////
////                                    //Update recyclerView
////                                    mListAdapter.notifyItemRemoved(index);
//                                    }
//                                });
//                            }
//
//                            @Override
//                            public void onFailure(DJIError error) {
////                            setResultToToast("Delete file failed");
//                            }
//                        });
//                    }
//                }

            }
        });
    }

    private MediaManager.FileListStateListener updateFileListStateListener = new MediaManager.FileListStateListener() {
        @Override
        public void onFileListStateChange(MediaManager.FileListState state) {
            currentFileListState = state;
        }
    };

    //长连接最终返回
    @Override
    protected void notifyData(String json) {
        Log.d(TAG, "张闯返回数据=" + json);
        if (TextUtils.isEmpty(json)) {
            return;
        }
        Communication communication = gson.fromJson(json, Communication.class);
        String method = RsaUtil.decrypt(communication.getMethod());
        communication.setMethod(RsaUtil.encrypt(method));
        switch (method) {
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
            //摄像头回正
            case Constant.CAMERA_CENTER:
                camera_center(communication);
                break;
            //摄像头垂直回正
            case Constant.CAMERA_CENTER_PITCH:
                camera_center_pitch(communication);
                break;
            //摄像头水平回正
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
            //航线自动飞行开始
            case Constant.WAYPOINT_FLY_START:
                startWaypointMission(communication);
                break;
            //航线自动飞行停止
            case Constant.WAYPOINT_FLY_STOP:
                stopWaypointMission(communication);
                break;
            //航线暂停
            case Constant.WAYPOINT_FLY_PAUSE:
                pauseWaypointMission(communication);
                break;
            //航线恢复
            case Constant.WAYPOINT_FLY_RESUME:
                resumeWaypointMission(communication);
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
//                camera.getMode(new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.CameraMode>() {
//                    @Override
//                    public void onSuccess(SettingsDefinitions.CameraMode cameraMode) {
//                        if (cameraMode == SettingsDefinitions.CameraMode.MEDIA_DOWNLOAD) {
//                            communication.setResult("正在下载中");
//                            communication.setCode(-1);
//                            communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
//                            NettyClient.getInstance().sendMessage(communication, null);
//                        } else {
//                            camera_start_shoot(communication);
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(DJIError djiError) {
//                    }
//                });
                break;
            //结束拍照
            case Constant.CAMERE_STOP_SHOOT:
                camera_stop_shoot(communication);
                break;
            //开始录像
            case Constant.CAMERE_START_RECORE:
                camera_start_recode(communication);
//                camera.getMode(new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.CameraMode>() {
//                    @Override
//                    public void onSuccess(SettingsDefinitions.CameraMode cameraMode) {
//                        if (cameraMode == SettingsDefinitions.CameraMode.MEDIA_DOWNLOAD) {
//                            communication.setResult("正在下载中");
//                            communication.setCode(-1);
//                            communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
//                            NettyClient.getInstance().sendMessage(communication, null);
//                        } else {
//                            camera_start_recode(communication);
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(DJIError djiError) {
//
//                    }
//                });
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
                if (communication_Initialization == null) {
                    communication_Initialization = new Communication();
                }
                communication_Initialization.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                communication_Initialization.setEquipmentId(MApplication.EQUIPMENT_ID);
                communication_Initialization.setMethod(RsaUtil.encrypt(Constant.GET_INITIALIZATION_DATA));
                communication_Initialization.setResult(gson.toJson(webInitializationBean, WebInitializationBean.class));
                NettyClient.getInstance().sendMessage(communication_perception_data, null);
                break;




            //设置返航高度
            case Constant.SET_GO_HOME_HEIGHT:
                setGoHomeHeight(communication);
                break;
            //设置限高
            case Constant.SET_MAX_HEIGHT:
                setMaxHeight(communication);
                break;
            //设置失控后飞机执行的操作
            case Constant.SET_CONNECT_FAIL_BEHAVIOR:
                setConnectionFailBehavior(communication);
                break;
            //设置重心校准
            case Constant.SET_GRAVITY_CENTER_STATE:
                setGravityCenterState(communication);
                break;
            //设置是否启用最大飞行半径限制(传1没有传0有 然后就是设置值)(type,value)
            case Constant.SET_MFRL:
                setMaxFlightRadiusLimit(communication);
                break;
            //开始校准IMU
            case Constant.START_IMU:
                setIMUStart(communication);
                break;
            //获取设置的一些默认参数
            case Constant.GET_LOW_BATTERY:
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
            //设置避障
            case Constant.SET_UPWARDS_AVOIDANCE:
                setUpwardsAvoidance(communication);
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



        }
    }

    //起飞
    private void startTakeoff(Communication communication) {
        if (mFlightController != null) {
            mFlightController.startTakeoff(
                    new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            CommonDjiCallback(djiError, communication);
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
//            mFlightController.getState().setIslandingConfirmationNeeded(false);
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
                    CommonDjiCallback(djiError, communication);
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
//            Rotation.Builder builder = new Rotation.Builder().mode(RotationMode.ABSOLUTE_ANGLE).time(2);
            Rotation.Builder builder = new Rotation.Builder().mode(RotationMode.RELATIVE_ANGLE).time(2);
            builder.yaw(Float.parseFloat(angle));
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
        String type = communication.getPara().get(Constant.TYPE);
        if (isM300Product()) {
            CameraVideoStreamSource source = DEFAULT;
            switch (type) {
                case "0":
                    source = WIDE;
                    break;
                case "1":
                    source = ZOOM;
                    break;
                case "2":
                    source = INFRARED_THERMAL;
                    break;

            }
            webInitializationBean.setCurrentLens(type);

            camera.setCameraVideoStreamSource(source, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    CommonDjiCallback(djiError, communication);
                }
            });
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
                    break;
                case "1":
                    source = FPV;
                    break;
            }
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
//                            case "2":
//                                shootPhoto = SettingsDefinitions.ShootPhotoMode.INTERVAL;
//
//                                break;
//                            case "3":
//                                shootPhoto = SettingsDefinitions.ShootPhotoMode.TIME_LAPSE;
//                                break;
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
                        initMediaManager();//去下载和上传
                        Toast.makeText(ConnectionActivity.this, "拍照完成准备下载", Toast.LENGTH_SHORT).show();
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
            camera.startShootPhoto(new CommonCallbacks.CompletionCallback() {
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
    }

    //开始录像
    private void camera_start_recode(Communication communication) {
        if (camera != null) {
            camera.startRecordVideo(new CommonCallbacks.CompletionCallback() {
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
    }

    //停止录像
    private void camera_stop_recode(Communication communication) {
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
                        initMediaManager();//去下载和上传
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
                    communication_laser_status.setMethod(RsaUtil.encrypt(Constant.LASER_DATA));
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
    private void setLed(Communication communication) {
        String beacons = communication.getPara().get(Constant.BEACONS);//顶部底部
        String front = communication.getPara().get(Constant.FRONT);//前臂
        String rear = communication.getPara().get(Constant.REAR);//后壁
        String statusIndicator = communication.getPara().get(Constant.STATUS_INDICATOR);//指示灯
        if (mFlightController != null) {
            LEDsSettings.Builder builder = new LEDsSettings.Builder().beaconsOn(beacons.equals("0") ? true : false).frontLEDsOn(front.equals("0") ? true : false).rearLEDsOn(rear.equals("0") ? true : false).statusIndicatorOn(statusIndicator.equals("0") ? true : false);
            mFlightController.setLEDsEnabledSettings(builder.build(), new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    CommonDjiCallback(djiError, communication);
                }
            });
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
        if (isM300Product() && camera != null) {
            camera.getLens(camera.getIndex()).setISO(SettingsDefinitions.ISO.find(Integer.parseInt(type)), new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    CommonDjiCallback(djiError, communication);
                }
            });
        }

    }

    //设置曝光补偿
    private void setExposureCom(Communication communication) {
        String type = communication.getPara().get(Constant.TYPE);
        if (isM300Product() && camera != null) {
            camera.getLens(camera.getIndex()).setExposureCompensation(SettingsDefinitions.ExposureCompensation.find(Integer.parseInt(type)), new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    CommonDjiCallback(djiError, communication);
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
                }
            });
        }

    }

    //设置变焦
    private void setCameraZoom(Communication communication) {
        if (isM300Product() && camera != null) {
            String type = communication.getPara().get(Constant.TYPE);
            camera.getLens(camera.getIndex()).setHybridZoomFocalLength(Integer.parseInt(type), new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    CommonDjiCallback(djiError, communication);
                }
            });
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
                camera.getLens(camera.getIndex()).setAELock(exposure, new CommonCallbacks.CompletionCallback() {
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
        mFlightController.setGoHomeHeightInMeters(Integer.parseInt(type), new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                CommonDjiCallback(djiError, communication);
            }
        });
    }

    //设置限高20~500
    private void setMaxHeight(Communication communication) {
        String type = communication.getPara().get(Constant.TYPE);
        mFlightController.setMaxFlightHeight(Integer.parseInt(type), new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                CommonDjiCallback(djiError, communication);
            }
        });
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

    //设置是否启用最大半径限制(传1没有 传0有然后就是设置值)
    private void setMaxFlightRadiusLimit(Communication communication) {
        String type = communication.getPara().get(Constant.TYPE);
        String value = communication.getPara().get(Constant.VALUE);
        mFlightController.setMaxFlightRadiusLimitationEnabled(type.equals("0") ? true : false, new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                if (djiError == null) {
                    mFlightController.setMaxFlightRadius(Integer.parseInt(value), new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            CommonDjiCallback(djiError, communication);
                        }
                    });
                } else {
                    communication.setResult(djiError.toString());
                    communication.setCode(-1);
                    communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                    NettyClient.getInstance().sendMessage(communication, null);
                }
            }
        });
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

    //获取低电量严重低电量
    private void getSettingValue(Communication communication) {
        SettingValueBean settingValueBean = new SettingValueBean();
        settingValueBean.setLowBatteryWarning(lowBatteryWarning);
        settingValueBean.setSeriousLowBatteryWarning(seriousLowBatteryWarning);
        settingValueBean.setSmartReturnToHomeEnabled(smartReturnToHomeEnabled);
        settingValueBean.setVisionAssistedPosition(visionAssistedPosition);
        settingValueBean.setPrecisionLand(precisionLand);
        settingValueBean.setUpwardsAvoidance(upwardsAvoidance);
        settingValueBean.setActiveObstacleAvoidance(activeObstacleAvoidance);
        settingValueBean.setAvoidanceDistanceUpward(avoidanceDistanceUpward);
        settingValueBean.setAvoidanceDistanceDownward(avoidanceDistanceDownward);
        settingValueBean.setAvoidanceDistanceHorizontal(avoidanceDistanceHorizontal);
        settingValueBean.setMaxPerceptionDistanceUpward(maxPerceptionDistanceUpward);
        settingValueBean.setMaxPerceptionDistanceDownward(maxPerceptionDistanceDownward);
        settingValueBean.setMaxPerceptionDistanceHorizontal(maxPerceptionDistanceHorizontal);
        settingValueBean.setGimbal_pitch_speed(gimbal_pitch_speed);
        settingValueBean.setGimbal_yaw_speed(gimbal_yaw_speed);

        if (communication == null) {
            communication = new Communication();
        }
        communication.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
        communication.setEquipmentId(MApplication.EQUIPMENT_ID);
        communication.setMethod(RsaUtil.encrypt(GET_LOW_BATTERY));
        communication.setResult(gson.toJson(settingValueBean, IMUStateBean.class));
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

    //障碍物检测
    private void setUpwardsAvoidance(Communication communication) {
        String type = communication.getPara().get(Constant.TYPE);
        if (KeyManager.getInstance() != null) {
            KeyManager.getInstance().setValue(flightControllerKey3, type.equals("1") ? true : false, new SetCallback() {
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

    //设置上下障感知距离
    private void setMaxPerceptionDistance(Communication communication) {
        String type = communication.getPara().get(Constant.TYPE);
        String value = communication.getPara().get(Constant.VALUE);
        PerceptionInformation.DJIFlightAssistantObstacleSensingDirection direction=Downward;
        switch (type){
            case "0":
                direction=Downward;
                break;
            case "1":
                direction=Upward;
                break;
            case "2":
                direction=Horizontal;
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
        PerceptionInformation.DJIFlightAssistantObstacleSensingDirection direction=Downward;
        switch (type){
            case "0":
                direction=Downward;
                break;
            case "1":
                direction=Upward;
                break;
            case "2":
                direction=Horizontal;
                break;
        }
        mFlightAssistant.setObstaclesAvoidanceDistance(Float.parseFloat(value), direction, new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                CommonDjiCallback(djiError, communication);
            }
        });
    }
    //设置避障刹车功能
    private void setActiveObstacleAvoidance(Communication communication){
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
    private void setGimbalSpeed(Communication communication){
        String type = communication.getPara().get(Constant.TYPE);
        String value = communication.getPara().get(Constant.VALUE);
        Axis axis=PITCH;
        switch (type){
            case "0":
                axis=PITCH;
                break;
            case "1":
                axis=YAW;
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
    private void startCalibration(Communication communication){
        gimbal.startCalibration(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                CommonDjiCallback(djiError, communication);
            }
        });
    }
    //恢复出厂
    private void restoreFactorySettings(Communication communication){
        gimbal.restoreFactorySettings(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                CommonDjiCallback(djiError, communication);
            }
        });
    }
    //云台偏航缓启停
    private void setControllerSmoothingFactor(Communication communication){
        String type = communication.getPara().get(Constant.TYPE);
        gimbal.setControllerSmoothingFactor(YAW, Integer.parseInt(type), new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                CommonDjiCallback(djiError, communication);
            }
        });
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
    // the list contains all "type == DJIDiagnostics.DJIDiagnosticsType.DEVICE_HEALTH_INFORMATION" element at real time.
    @Override
    public void onUpdate(List<DJIDiagnostics> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        StringBuilder errorMsg = new StringBuilder();
        for (DJIDiagnostics diagnostics : list) {
//            Log.d("ErrorUpdate", diagnostics.getReason());
            errorMsg.append(diagnostics.getReason()).append(",");
        }
        StringsBean stringsBean = new StringsBean();
        stringsBean.setValue(errorMsg.toString().substring(0, errorMsg.length() - 1) + ",乃要的测试数据");
        Log.d("ErrorUpdate", errorMsg.toString().substring(0, errorMsg.length() - 1));
        if (communication_error_log == null) {
            communication_error_log = new Communication();
        }
        communication_error_log.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
        communication_error_log.setEquipmentId(MApplication.EQUIPMENT_ID);
        communication_error_log.setMethod(RsaUtil.encrypt(Constant.DIAGNOSTICS));
        communication_error_log.setResult(gson.toJson(stringsBean));
        NettyClient.getInstance().sendMessage(communication_error_log, null);
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
                                    CommonDjiCallback(djiError, mCommunication);
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
                                CommonDjiCallback(djiError, mCommunication);
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
                                CommonDjiCallback(djiError, mCommunication);

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
                                CommonDjiCallback(djiError, mCommunication);
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
//        Log.d("HHHHH","speed="+speed);
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
        Log.d("wayPoints", wayPoints);
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
            Gson gson = new Gson();
            String MSlist = gson.toJson(waypointList);
            Log.d("MSlist", MSlist);

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
        if (instance == null && DJISDKManager.getInstance().getMissionControl() != null) {
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

    private void resumeWaypointMission(Communication communication) {
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


}
