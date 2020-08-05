package com.compass.ux;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import dji.common.airlink.SignalQualityCallback;
import dji.common.battery.AggregationState;
import dji.common.battery.BatteryOverview;
import dji.common.battery.BatteryState;
import dji.common.battery.PairingState;
import dji.common.camera.CameraVideoStreamSource;
import dji.common.camera.LaserMeasureInformation;
import dji.common.camera.PhotoTimeLapseSettings;
import dji.common.camera.SettingsDefinitions;
import dji.common.error.DJIError;
import dji.common.error.DJISDKError;
import dji.common.flightcontroller.FlightControllerState;
import dji.common.flightcontroller.LEDsSettings;
import dji.common.flightcontroller.ObstacleDetectionSector;
import dji.common.flightcontroller.VisionDetectionState;
import dji.common.flightcontroller.WindDirection;
import dji.common.flightcontroller.adsb.AirSenseAirplaneState;
import dji.common.flightcontroller.adsb.AirSenseSystemInformation;
import dji.common.flightcontroller.flightassistant.PerceptionInformation;
import dji.common.flightcontroller.virtualstick.FlightControlData;
import dji.common.flightcontroller.virtualstick.FlightCoordinateSystem;
import dji.common.flightcontroller.virtualstick.RollPitchControlMode;
import dji.common.flightcontroller.virtualstick.VerticalControlMode;
import dji.common.flightcontroller.virtualstick.YawControlMode;
import dji.common.gimbal.Rotation;
import dji.common.gimbal.RotationMode;
import dji.common.logics.warningstatuslogic.WarningStatusItem;
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
import dji.keysdk.CameraKey;
import dji.keysdk.DiagnosticsKey;
import dji.keysdk.FlightControllerKey;
import dji.keysdk.KeyManager;
import dji.keysdk.callback.GetCallback;
import dji.keysdk.callback.KeyListener;
import dji.sdk.base.BaseComponent;
import dji.sdk.base.BaseProduct;
import dji.sdk.base.DJIDiagnostics;
import dji.sdk.battery.Battery;
import dji.sdk.camera.Camera;
import dji.sdk.camera.LensCapabilities;
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
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.compass.ux.bean.AggregationBatteryBean;
import com.compass.ux.bean.BatteryPersentAndVoltageBean;
import com.compass.ux.bean.BatteryStateBean;
import com.compass.ux.bean.CameraDataBean;
import com.compass.ux.bean.FlightControllerBean;
import com.compass.ux.bean.LaserMeasureInformBean;
import com.compass.ux.bean.StringsBean;
import com.compass.ux.bean.TextMessageBean;
import com.compass.ux.downloadpic.DefaultLayoutActivity;
import com.compass.ux.live.live.LiveStreamView;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dji.common.camera.CameraVideoStreamSource.DEFAULT;
import static dji.common.camera.CameraVideoStreamSource.INFRARED_THERMAL;
import static dji.common.camera.CameraVideoStreamSource.WIDE;
import static dji.common.camera.CameraVideoStreamSource.ZOOM;
import static dji.common.camera.SettingsDefinitions.ShutterSpeed.SHUTTER_SPEED_1_20000;
import static dji.common.camera.SettingsDefinitions.ShutterSpeed.SHUTTER_SPEED_1_30;
import static dji.common.flightcontroller.WindDirection.EAST;
import static dji.common.flightcontroller.WindDirection.NORTH;
import static dji.common.flightcontroller.WindDirection.NORTH_EAST;
import static dji.common.flightcontroller.WindDirection.NORTH_WEST;
import static dji.common.flightcontroller.WindDirection.SOUTH;
import static dji.common.flightcontroller.WindDirection.SOUTH_EAST;
import static dji.common.flightcontroller.WindDirection.SOUTH_WEST;
import static dji.common.flightcontroller.WindDirection.WEST;
import static dji.common.flightcontroller.WindDirection.WINDLESS;
import static dji.common.gimbal.Axis.PITCH;
import static dji.common.gimbal.Axis.YAW;
import static dji.common.gimbal.Axis.YAW_AND_PITCH;
import static dji.common.gimbal.ResetDirection.CENTER;
import static dji.common.gimbal.ResetDirection.UP_OR_DOWN;
import static dji.keysdk.FlightControllerKey.HOME_LOCATION_LATITUDE;
import static dji.keysdk.FlightControllerKey.WIND_DIRECTION;
import static dji.keysdk.FlightControllerKey.WIND_SPEED;
import static dji.sdk.codec.DJICodecManager.VideoSource.CAMERA;
import static dji.sdk.codec.DJICodecManager.VideoSource.FPV;

public class ConnectionActivity extends NettyActivity implements View.OnClickListener, TextureView.SurfaceTextureListener, DJIDiagnostics.DiagnosticsInformationCallback {

    private String liveShowUrl = "rtmp://172.16.16.1:1935/live/Mobile_01";
    //        private String liveShowUrl = "rtmp://12203.lsspublish.aodianyun.com/889/01";
//    private String liveShowUrl = "rtmp://10.0.2.185:1935/live/Mobile_01";
//        private String liveShowUrl = "rtmp://push.yunxi.tv/yunxi-host/test_e8646a24f2874d3da2480253dd2b5f1d?auth_key=1595556840-0-0-2e99590ae87f93f338196332537690f1";
    private static final String TAG = ConnectionActivity.class.getName();


    private TextView mTextConnectionStatus;
    private TextView mTextProduct;
    private TextView mVersionTv;
    private Button mBtnOpen, btn_download, btn_gaode, btn_simulator, btn_login;

    private FlightController mFlightController;
    private RemoteController mRemoteController;
    private Battery battery;
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
    String windDirection = "";
    String windSpeed = "";

    FlightControllerKey wind_direction_key;
    FlightControllerKey wind_speed_key;

    FlightControllerBean flightControllerBean = null;
    BatteryStateBean batteryStateBean = null;
    AggregationBatteryBean aggregationBatteryBean = null;
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
    private int currentProgress = -1;

    private boolean havePermission = false;

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
//            mMediaManager.exitMediaDownloading();
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

//                DJISDKManager.getInstance().getLiveStreamManager().stopStream();
//                Toast.makeText(getApplicationContext(), "Stop Live Show", Toast.LENGTH_SHORT).show();

                break;

            }
            case R.id.btn_download: {
                //去下载
                Intent intent = new Intent(this, DefaultLayoutActivity.class);
                startActivity(intent);
//                String aaa="/storage/emulated/0/DjiMedia/DJI_0009.jpg";
//                String bbb="http://61.155.157.42:7070/oauth/file/upload";
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            String result=ClientUploadUtils.upload(bbb,aaa,"DJI_0009.jpg").string();
//                            Log.e(TAG,"result="+result);
//                        } catch (Exception e) {
////                            Toast.makeText(getApplicationContext(), "error="+e.toString(), Toast.LENGTH_LONG).show();
//                            Log.e(TAG,e.toString());
//                            e.printStackTrace();
//                        }
//                    }
//                }).start();

                boolean beaconsOn = true;//顶部和底部
                boolean frontLEDsOn = true;//前臂
                boolean rearLEDsOn = true;//后壁
                boolean statusIndicatorOn = true;//状态指示灯
                LEDsSettings.Builder builder = new LEDsSettings.Builder().beaconsOn(beaconsOn).frontLEDsOn(frontLEDsOn).rearLEDsOn(rearLEDsOn).statusIndicatorOn(statusIndicatorOn);
                mFlightController.setLEDsEnabledSettings(builder.build(), new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (djiError != null) {
//                            communication.setResult(djiError.getDescription());
//                            communication.setCode(-1);
//                            communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
//                            NettyClient.getInstance().sendMessage(communication, null);
                        } else {
//                            communication.setResult("Success");
//                            communication.setCode(200);
//                            communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
//                            NettyClient.getInstance().sendMessage(communication, null);
                        }

                    }
                });


                break;
            }

            case R.id.btn_gaode: {
                //去高德地图
//                Intent intent = new Intent(this, GaodeMainActivity.class);
//                startActivity(intent);
                //下载
//                initMediaManager();
                camera_center_pitch(new Communication());
                break;
            }
            case R.id.btn_simulator: {
                //去控制飞机
//                Intent intent = new Intent(this, SimulatorMainActivity.class);
//                startActivity(intent);
//                isLiveShowOn();

//                Toast.makeText(this, "isVideoEncodingEnabled" + DJISDKManager.getInstance().getLiveStreamManager().isVideoEncodingEnabled() + "", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(this, LiveStreamView.class);
//                startActivity(intent);
                camera_center_yaw(new Communication());
                break;

            }
            case R.id.btn_login:
//                loginAccount();
                startLiveShow();

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
            addListener();
            planeStatusHandler.post(planeStatusTask);//立即调用获取飞机当前是否可以起飞
            initErrorLog();//初始化错误日志

        }
    };
    private void initErrorLog(){
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
                        LatLng latLng= MapConvertUtils.getGDLatLng(flightControllerState.getAircraftLocation().getLatitude(), flightControllerState.getAircraftLocation().getLongitude(),ConnectionActivity.this);
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
                        LatLng latLngHome= MapConvertUtils.getGDLatLng(flightControllerState.getHomeLocation().getLatitude(), flightControllerState.getHomeLocation().getLongitude(),ConnectionActivity.this);
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

//            BatteryKey battery=BatteryKey.create(BatteryKey.BATTERY_TYPE);
//            KeyManager.getInstance().getValue(battery, new GetCallback() {
//                @Override
//                public void onSuccess(Object o) {
//
//
//                }
//
//                @Override
//                public void onFailure(DJIError djiError) {
//
//                }
//            });
//            BatteryKey battery=BatteryKey.create(BatteryKey.OVERVIEWS);
//            BatteryKey batteryVoltages=BatteryKey.create(BatteryKey.CELL_VOLTAGES);
//            KeyManager.getInstance().getValue(battery, new GetCallback() {
//                @Override
//                public void onSuccess(Object o) {
//                    if(o instanceof BatteryOverview[]){
//                        for (int i = 0; i <((BatteryOverview[]) o).length ; i++) {
//                            Log.d("HHHHHPercent",((BatteryOverview[]) o)[i].getChargeRemainingInPercent()+"");
//                        }
//                    }
//                }
//
//                @Override
//                public void onFailure(DJIError djiError) {
//                    Log.d("HHHHHPercent",djiError+"");
//                }
//            });
//            KeyManager.getInstance().getValue(batteryVoltages, new GetCallback() {
//                @Override
//                public void onSuccess(Object o) {
//                    if(o instanceof Integer []){
//                        for (int i = 0; i <((Integer[]) o).length ; i++) {
//                            Log.d("HHHHHVoltages",((Integer[]) o)[i]+"");
//                        }
//                    }
//                }
//                @Override
//                public void onFailure(DJIError djiError) {
//
//                }
//            });

            //聚合电量
//            FPVDemoApplication.getProductInstance().getBattery().setAggregationStateCallback(new AggregationState.Callback() {
//                @Override
//                public void onUpdate(AggregationState aggregationState) {
//                    Log.d("aggregationState", aggregationState.toString());
//                    if (aggregationBatteryBean == null) {
//                        aggregationBatteryBean = new AggregationBatteryBean();
//                    }
//                    aggregationBatteryBean.setNumberOfConnectedBatteries(aggregationState.getNumberOfConnectedBatteries());
//                    aggregationBatteryBean.setCurrent(aggregationState.getCurrent());
//                    aggregationBatteryBean.setVoltage(aggregationState.getVoltage());
//                    aggregationBatteryBean.setLowCellVoltageDetected(aggregationState.isLowCellVoltageDetected());
//                    aggregationBatteryBean.setFullChargeCapacity(aggregationState.getFullChargeCapacity());
//                    aggregationBatteryBean.setChargeRemaining(aggregationState.getChargeRemaining());
//                    aggregationBatteryBean.setChargeRemainingInPercent(aggregationState.getChargeRemainingInPercent());
//                    aggregationBatteryBean.setHighestTemperature(aggregationState.getHighestTemperature());
//                    aggregationBatteryBean.setFirmwareDifferenceDetected(aggregationState.isFirmwareDifferenceDetected());
//                    AggregationBatteryBean.BatteryOverviewBean[] batteryOverviewBean = new AggregationBatteryBean.BatteryOverviewBean[aggregationState.getBatteryOverviews().length];
//                    for (int i = 0; i < aggregationState.getBatteryOverviews().length; i++) {
//                        batteryOverviewBean[i].setChargeRemainingInPercent(aggregationState.getBatteryOverviews()[i].getChargeRemainingInPercent());
//                        batteryOverviewBean[i].setIndex(aggregationState.getBatteryOverviews()[i].getIndex());
//                    }
//                    aggregationBatteryBean.setBatteryOverviews(batteryOverviewBean);
//                    aggregationBatteryBean.setAnyBatteryDisconnected(aggregationState.isAnyBatteryDisconnected());
//                    aggregationBatteryBean.setVoltageDifferenceDetected(aggregationState.isVoltageDifferenceDetected());
//                    aggregationBatteryBean.setCellDamaged(aggregationState.isCellDamaged());
//                    Log.d("NNNNN", aggregationBatteryBean.toString());
//                    if (communication_aggregation_battery == null) {
//                        communication_aggregation_battery = new Communication();
//                    }
//                    communication_aggregation_battery.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
//                    communication_aggregation_battery.setEquipmentId(MApplication.EQUIPMENT_ID);
//                    communication_aggregation_battery.setMethod(RsaUtil.encrypt("aggregationBattery"));
//                    communication_aggregation_battery.setResult(gson.toJson(aggregationBatteryBean, AggregationBatteryBean.class));
//                    NettyClient.getInstance().sendMessage(communication_aggregation_battery, null);
//                }
//            });
            //电量
//            FPVDemoApplication.getProductInstance().getBattery().setStateCallback(new BatteryState.Callback() {
//                @Override
//                public void onUpdate(BatteryState batteryState) {
//                    Log.d("batteryState",batteryState.toString());
//                    if (fastClick.batteryClick()) {
//                        if (batteryStateBean == null) {
//                            batteryStateBean = new BatteryStateBean();
//                        }
//                        batteryStateBean.setFullChargeCapacity(batteryState.getFullChargeCapacity());
//                        batteryStateBean.setChargeRemaining(batteryState.getChargeRemaining());
//                        batteryStateBean.setChargeRemainingInPercent(batteryState.getChargeRemainingInPercent());
//                        batteryStateBean.setDesignCapacity(batteryState.getDesignCapacity());
//                        batteryStateBean.setVoltage(batteryState.getVoltage());
//                        batteryStateBean.setCurrent(batteryState.getCurrent());
//                        batteryStateBean.setLifetimeRemaining(batteryState.getLifetimeRemaining());
//                        batteryStateBean.setTemperature(batteryState.getTemperature());
//                        batteryStateBean.setNumberOfDischarges(batteryState.getNumberOfDischarges());
//
//                        if (communication_battery == null) {
//                            communication_battery = new Communication();
//                        }
//                        communication_battery.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
//                        communication_battery.setEquipmentId(MApplication.EQUIPMENT_ID);
//                        communication_battery.setMethod(RsaUtil.encrypt("battery"));
//                        communication_battery.setResult(gson.toJson(batteryStateBean, BatteryStateBean.class));
////                        Log.d("NNNNN",communication_battery.toString());
//                        NettyClient.getInstance().sendMessage(communication_battery, null);
//                    }
//                }
//            });

            FlightAssistant mFlightAssistant = mFlightController.getFlightAssistant();
            mFlightAssistant.setVisionDetectionStateUpdatedCallback(new VisionDetectionState.Callback() {
                //                视觉系统可以以60度水平视场（FOV）和55度垂直FOV看到飞机前方。水平视场分为四个相等的扇区，此类给出了一个扇区的距离和警告级别。
                @Override
                public void onUpdate(VisionDetectionState visionDetectionState) {
//                    double ObstacleDistanceInMeters= visionDetectionState.getObstacleDistanceInMeters();
                    ObstacleDetectionSector[] mArray = visionDetectionState.getDetectionSectors();
//                    Log.d("MMMMM","ObstacleDistanceInMeters="+ObstacleDistanceInMeters);
                    String aa = "";
                    Log.d("MMMMM", "=======================");
                    for (int i = 0; i < mArray.length; i++) {
//                        检测到的飞机障碍物距离，以米为单位。

                        aa = "ObstacleDistanceInMeters" + i + "=" + mArray[i].getObstacleDistanceInMeters() + "\nWarningLevel=" + mArray[i].getWarningLevel();
//                        基于距离的警告级别。
                        Log.d("MMMMM", aa);
                    }
                    Log.d("MMMMM", "=======================");

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
            mFlightAssistant.setVisualPerceptionInformationCallback(new CommonCallbacks.CompletionCallbackWith<PerceptionInformation>() {
                @Override
                public void onSuccess(PerceptionInformation perceptionInformation) {
//                    String sss="";
//
//                    for (int i = 0; i <perceptionInformation.getDistances().length ; i++) {
//                        sss+=i+"---"+perceptionInformation.getDistances()[i]+"===";
//                    }
//                    Log.d("NNNNN","getDistances="+sss);
//                    Log.d("NNNNN","perceptionInformation.getDistances().length="+perceptionInformation.getDistances().length);
//                    Log.d("NNNNN","getDataPackageIndex="+perceptionInformation.getDataPackageIndex());
//                    Log.d("NNNNN","getAngleInterval="+perceptionInformation.getAngleInterval());
//                    Log.d("NNNNN","getUpwardObstacleDistance="+perceptionInformation.getUpwardObstacleDistance());
//                    Log.d("NNNNN","getDownwardObstacleDistance="+perceptionInformation.getDownwardObstacleDistance());
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            });

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
            if ((var4 = (Integer[])var1).length <= 0) {
                return 0.0F;
            } else {
                int var5 = var4[0];
                int var2 = var4.length;

                for(int var3 = 0; var3 < var2; ++var3) {
                    var5 = Math.min(var5, var4[var3]);
                }

                return (float)var5 * 1.0F / 1000.0F;
            }
        } else {
            return 0.0F;
        }
    }


    int returnISO=65535,returnExposureCompensation=65535;
    float returnShutter=255.0F;
    private void initCamera() {
        camera = FPVDemoApplication.getCameraInstance();


        if(isM300Product()){
            //返回激光测距数据
            if (isM300Product()) {
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
            }
            //返回iso数据
            camera.getLens(camera.getIndex()).getISO(new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.ISO>() {
                @Override
                public void onSuccess(SettingsDefinitions.ISO iso) {
                    returnISO=iso.value();
//                    Log.d("HHHHHreturnISO",returnISO+"");
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            });
            //返回曝光补偿
            camera.getLens(camera.getIndex()).getExposureCompensation(new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.ExposureCompensation>() {
                @Override
                public void onSuccess(SettingsDefinitions.ExposureCompensation exposureCompensation) {
                    returnExposureCompensation=exposureCompensation.value();
//                    Log.d("HHHHHCompensation",returnExposureCompensation+"");
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            });

            CameraDataBean cameraDataBean = new CameraDataBean();
            cameraDataBean.setISO(returnISO+"");
            cameraDataBean.setExposureCompensation(returnExposureCompensation+"");

            if (communication_camera_data == null) {
                communication_camera_data = new Communication();
            }
            communication_camera_data.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            communication_camera_data.setEquipmentId(MApplication.EQUIPMENT_ID);
            communication_camera_data.setMethod(RsaUtil.encrypt(Constant.CAMERA_DATA));
            communication_camera_data.setResult(gson.toJson(cameraDataBean, CameraDataBean.class));
            NettyClient.getInstance().sendMessage(communication_camera_data, null);

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

            camera.getLens(camera.getIndex()).getHybridZoomFocalLength(new CommonCallbacks.CompletionCallbackWith<Integer>() {
                @Override
                public void onSuccess(Integer integer) {
                    Log.d("HHHHHcurr",integer+"");
                }

                @Override
                public void onFailure(DJIError djiError) {
                    Log.d("HHHHHcurr",djiError.toString());
                }
            });
//            470
            camera.getLens(camera.getIndex()).setHybridZoomFocalLength(470, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
//                    Log.d("HHHHHset",djiError.toString());
                }
            });



//            SettingsDefinitions.ISO.find(0)
//            camera.getLens(camera.getIndex()).setISO();
            //返回快门速度
//            CameraKey shutterkey=CameraKey.createLensKey(CameraKey.SHUTTER_SPEED,0,0);
//            Object obj=KeyManager.getInstance().getValue(shutterkey);
//            if(obj instanceof SettingsDefinitions.ShutterSpeed){
//                Log.d("HHHHHreturnShutter",((SettingsDefinitions.ShutterSpeed) obj).value()+"");
//            }

//            camera.getLens(camera.getIndex()).getShutterSpeed(new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.ShutterSpeed>() {
//                @Override
//                public void onSuccess(SettingsDefinitions.ShutterSpeed shutterSpeed) {
//                    returnShutter=shutterSpeed.value();
//                    Log.d("HHHHHreturnShutter",returnShutter+"");
//                }
//
//                @Override
//                public void onFailure(DJIError djiError) {
//                    Log.d("HHHHHreturnShutter",djiError.toString()+"");
//                }
//            });

//            for (int i = 0; i <camera.getCapabilities().ISORange().length ; i++) {
//                Log.d("HHHHHreturnShutter",camera.getCapabilities().ISORange()[i]+"");
//
//            }


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

//        Gimbal gimbal = FPVDemoApplication.getProductInstance().getGimbal();
//        gimbal.getCapabilities(


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
        }
//        camera.getLens(camera.getIndex()).setDigitalZoomFactor();
    }
    String battery_one="0",battery_two="0",battery_voltages_one="0",battery_voltages_two="0";
    private void initBattery(){
        battery=FPVDemoApplication.getProductInstance().getBattery();
        BatteryKey battery_per_one= BatteryKey.create(BatteryKey.CHARGE_REMAINING_IN_PERCENT);
        BatteryKey battery_per_two= BatteryKey.create(BatteryKey.CHARGE_REMAINING_IN_PERCENT,1);
        BatteryKey battery_voltage_one= BatteryKey.create(BatteryKey.CELL_VOLTAGES,1);
        BatteryKey battery_voltage_two= BatteryKey.create(BatteryKey.CELL_VOLTAGES,1);
        KeyManager.getInstance().addListener(battery_per_one, new KeyListener() {
            @Override
            public void onValueChange(Object o, Object o1) {
                if (fastClick.flightControllerClick()) {
                    battery_one=o1.toString();
                    submitBatteryPersentAndV();
                }

            }
        });
        KeyManager.getInstance().addListener(battery_per_two, new KeyListener() {
            @Override
            public void onValueChange(Object o, Object o1) {
                if (fastClick.flightControllerClick()) {
                    battery_two = o1.toString();
                    submitBatteryPersentAndV();
                }
            }
        });

        KeyManager.getInstance().addListener(battery_voltage_one, new KeyListener() {
            @Override
            public void onValueChange(Object o, Object o1) {
                if (fastClick.flightControllerClick()) {
                    battery_voltages_one=getMinVoltage(o1)+"";
                    submitBatteryPersentAndV();
                }

            }
        });
        KeyManager.getInstance().addListener(battery_voltage_two, new KeyListener() {
            @Override
            public void onValueChange(Object o, Object o1) {
                if (fastClick.flightControllerClick()) {
                    battery_voltages_two = getMinVoltage(o1) + "";
                    submitBatteryPersentAndV();
                }
            }
        });
    }

    private void submitBatteryPersentAndV() {
        BatteryPersentAndVoltageBean batteryPersentAndVoltageBean = new BatteryPersentAndVoltageBean();
        batteryPersentAndVoltageBean.setPersentOne(battery_one);
        batteryPersentAndVoltageBean.setPersentTwo(battery_two);
        batteryPersentAndVoltageBean.setVoltageOne(battery_voltages_one);
        batteryPersentAndVoltageBean.setVoltageTwo(battery_voltages_two);
        if (communication_battery == null) {
            communication_battery = new Communication();
        }
        communication_battery.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
        communication_battery.setEquipmentId(MApplication.EQUIPMENT_ID);
        communication_battery.setMethod(RsaUtil.encrypt(Constant.BatteryPAV));
        communication_battery.setResult(gson.toJson(batteryPersentAndVoltageBean, BatteryPersentAndVoltageBean.class));
        if(!TextUtils.isEmpty(battery_one)&&!TextUtils.isEmpty(battery_two)&&!TextUtils.isEmpty(battery_voltages_one)&&!TextUtils.isEmpty(battery_voltages_two))
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
//                    在完成块中，如果没有错误，请调用该showProgressDialog()方法以显示获取文件的进度，
//                    并调用该getFileList()方法以获取媒体文件列表。
//                    最后，初始化FetchMediaTaskScheduler来安排获取媒体文件任务。
                    FPVDemoApplication.getCameraInstance().setMode(SettingsDefinitions.CameraMode.MEDIA_DOWNLOAD, new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError error) {
                            if (error == null) {
                                Log.e(TAG, "Set cameraMode success");
                                getFileList();
                            } else {
                                Log.e(TAG, "Set cameraMode failed" + error.toString());
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
                Log.e(TAG, "Media Manager is busy.");
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
                            Toast.makeText(ConnectionActivity.this, "拿到list去下载", Toast.LENGTH_SHORT).show();
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
                    Log.e(TAG, "fileallname=" + filePath + "/" + FileName);
                    String result = ClientUploadUtils.upload(MApplication.UPLOAD_URL, filePath + "/" + FileName, FileName).toString();
                    Toast.makeText(ConnectionActivity.this, "result:" + result, Toast.LENGTH_SHORT).show();
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
                camera.getMode(new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.CameraMode>() {
                    @Override
                    public void onSuccess(SettingsDefinitions.CameraMode cameraMode) {
                        if (cameraMode == SettingsDefinitions.CameraMode.MEDIA_DOWNLOAD) {
                            communication.setResult("正在下载中");
                            communication.setCode(-1);
                            communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                            NettyClient.getInstance().sendMessage(communication, null);
                        } else {
                            camera_start_shoot(communication);
                        }
                    }

                    @Override
                    public void onFailure(DJIError djiError) {
                    }
                });
                break;
            //结束拍照
            case Constant.CAMERE_STOP_SHOOT:
                camera_stop_shoot(communication);
                break;
            //开始录像
            case Constant.CAMERE_START_RECORE:
                camera.getMode(new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.CameraMode>() {
                    @Override
                    public void onSuccess(SettingsDefinitions.CameraMode cameraMode) {
                        if (cameraMode == SettingsDefinitions.CameraMode.MEDIA_DOWNLOAD) {
                            communication.setResult("正在下载中");
                            communication.setCode(-1);
                            communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                            NettyClient.getInstance().sendMessage(communication, null);
                        } else {
                            camera_start_recode(communication);
                        }
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });
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
//            mFlightController.getState().setIslandingConfirmationNeeded(false);
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

    //回家
    private void startGoHome(Communication communication) {
        if (mFlightController != null) {
            mFlightController.startGoHome(new CommonCallbacks.CompletionCallback() {
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
            });
        }
    }

    //暂停回家
    private void CancelGoHome(Communication communication) {
        if (mFlightController != null) {
            mFlightController.cancelGoHome(new CommonCallbacks.CompletionCallback() {
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

    //摄像头归零
    private void camera_center(Communication communication) {
        Gimbal gimbal = FPVDemoApplication.getProductInstance().getGimbal();
        if (gimbal == null) {
            return;
        }
        gimbal.reset(new CommonCallbacks.CompletionCallback() {
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

    }

    //    垂直回中
    private void camera_center_pitch(Communication communication) {
//        Gimbal gimbal = FPVDemoApplication.getProductInstance().getGimbal();
//        if (gimbal == null) {
//            return;
//        }
//        gimbal.reset(PITCH, CENTER, new CommonCallbacks.CompletionCallback() {
//            @Override
//            public void onResult(DJIError djiError) {
//                if (djiError != null) {
//                    communication.setResult(djiError.getDescription());
//                    communication.setCode(-1);
//                    communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
//                    NettyClient.getInstance().sendMessage(communication, null);
//                } else {
//                    communication.setResult("Success");
//                    communication.setCode(200);
//                    communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
//                    NettyClient.getInstance().sendMessage(communication, null);
//                }
//            }
//        });

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
//        gimbal.reset(YAW, CENTER, new CommonCallbacks.CompletionCallback() {
//            @Override
//            public void onResult(DJIError djiError) {
//                if (djiError != null) {
//                    communication.setResult(djiError.getDescription());
//                    communication.setCode(-1);
//                    communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
//                    NettyClient.getInstance().sendMessage(communication, null);
//                } else {
//                    communication.setResult("Success");
//                    communication.setCode(200);
//                    communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
//                    NettyClient.getInstance().sendMessage(communication, null);
//                }
//            }
//        });

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
            camera.setCameraVideoStreamSource(source, new CommonCallbacks.CompletionCallback() {
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
            camera.setCameraVideoStreamSource(source, new CommonCallbacks.CompletionCallback() {
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
//                        communication.setResult("Success");
//                        communication.setCode(200);
//                        communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
//                        NettyClient.getInstance().sendMessage(communication, null);
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
//                        communication.setResult("Success");
//                        communication.setCode(200);
//                        communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
//                        NettyClient.getInstance().sendMessage(communication, null);
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
        boolean beacons = Boolean.parseBoolean(communication.getPara().get(Constant.BEACONS));//顶部底部
        boolean front = Boolean.parseBoolean(communication.getPara().get(Constant.FRONT));//前臂
        boolean rear = Boolean.parseBoolean(communication.getPara().get(Constant.REAR));//后壁
        boolean statusIndicator = Boolean.parseBoolean(communication.getPara().get(Constant.STATUS_INDICATOR));//指示灯
        if (mFlightController != null) {
            LEDsSettings.Builder builder = new LEDsSettings.Builder().beaconsOn(beacons).frontLEDsOn(front).rearLEDsOn(rear).statusIndicatorOn(statusIndicator);
            mFlightController.setLEDsEnabledSettings(builder.build(), new CommonCallbacks.CompletionCallback() {
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
            communication.setResult("飞行控制类获取失败");
            communication.setCode(-1);
            communication.setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            NettyClient.getInstance().sendMessage(communication, null);
        }
    }
    //设置iso
    private void setISO(Communication communication) {
        String type = communication.getPara().get(Constant.TYPE);
        if(isM300Product()&&camera!=null){
            camera.getLens(camera.getIndex()).setISO(SettingsDefinitions.ISO.find(Integer.parseInt(type)), new CommonCallbacks.CompletionCallback() {
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
        }

    }
    //设置曝光补偿
    private void setExposureCom(Communication communication) {
        String type = communication.getPara().get(Constant.TYPE);
        if(isM300Product()&&camera!=null){
            camera.getLens(camera.getIndex()).setExposureCompensation(SettingsDefinitions.ExposureCompensation.find(Integer.parseInt(type)), new CommonCallbacks.CompletionCallback() {
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
    // the list contains all "type == DJIDiagnostics.DJIDiagnosticsType.DEVICE_HEALTH_INFORMATION" element at real time.
    @Override
    public void onUpdate(List<DJIDiagnostics> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        List<String> mlist=new ArrayList<>();
        for (DJIDiagnostics diagnostics : list) {
            Log.d("ErrorUpdate",diagnostics.getReason());
            mlist.add(diagnostics.getReason());
        }

        if (communication_error_log == null) {
            communication_error_log = new Communication();
        }
        communication_error_log.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
        communication_error_log.setEquipmentId(MApplication.EQUIPMENT_ID);
        communication_error_log.setMethod(RsaUtil.encrypt(Constant.DIAGNOSTICS));
        communication_error_log.setResult(gson.toJson(mlist));
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
        Log.d("HHHHH","speed="+speed);
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


}
