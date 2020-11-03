package com.compass.ux.simulator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import dji.common.battery.BatteryState;
import dji.common.error.DJIError;
import dji.common.error.DJISDKError;
import dji.common.flightcontroller.FlightControllerState;
import dji.common.flightcontroller.FlightMode;
import dji.common.flightcontroller.simulator.InitializationData;
import dji.common.flightcontroller.simulator.SimulatorState;
import dji.common.flightcontroller.virtualstick.FlightControlData;
import dji.common.flightcontroller.virtualstick.FlightCoordinateSystem;
import dji.common.flightcontroller.virtualstick.RollPitchControlMode;
import dji.common.flightcontroller.virtualstick.VerticalControlMode;
import dji.common.flightcontroller.virtualstick.YawControlMode;
import dji.common.gimbal.CapabilityKey;
import dji.common.gimbal.Rotation;
import dji.common.gimbal.RotationMode;
import dji.common.model.LocationCoordinate2D;
import dji.common.useraccount.UserAccountState;
import dji.common.util.CommonCallbacks;
import dji.common.util.DJIParamMinMaxCapability;
import dji.keysdk.FlightControllerKey;
import dji.keysdk.KeyManager;
import dji.keysdk.callback.ActionCallback;
import dji.log.DJILog;
import dji.sdk.base.BaseComponent;
import dji.sdk.base.BaseProduct;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.gimbal.Gimbal;
import dji.sdk.products.Aircraft;
import dji.sdk.sdkmanager.DJISDKInitEvent;
import dji.sdk.sdkmanager.DJISDKManager;
import dji.sdk.useraccount.UserAccountManager;

import com.compass.ux.R;
import com.compass.ux.takephoto.FPVDemoApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;


public class SimulatorMainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = SimulatorMainActivity.class.getName();

//    private static final String[] REQUIRED_PERMISSION_LIST = new String[]{
//            Manifest.permission.VIBRATE,
//            Manifest.permission.INTERNET,
//            Manifest.permission.ACCESS_WIFI_STATE,
//            Manifest.permission.WAKE_LOCK,
//            Manifest.permission.ACCESS_COARSE_LOCATION,
//            Manifest.permission.ACCESS_NETWORK_STATE,
//            Manifest.permission.ACCESS_FINE_LOCATION,
//            Manifest.permission.CHANGE_WIFI_STATE,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.BLUETOOTH,
//            Manifest.permission.BLUETOOTH_ADMIN,
//            Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.READ_PHONE_STATE,
//    };
//    private List<String> missingPermission = new ArrayList<>();
//    private AtomicBoolean isRegistrationInProgress = new AtomicBoolean(false);
//    private static final int REQUEST_PERMISSION_CODE = 12345;

    protected TextView mConnectStatusTextView;
    private Button mBtnEnableVirtualStick;
    private Button mBtnDisableVirtualStick;
    private ToggleButton mBtnSimulator;
    private Button mBtnTakeOff, btn_init, btn_yt_p, btn_yt_r, btn_yt_y;
    private Button mBtnLand;
    private TextView mTextView;

    private OnScreenJoystick mScreenJoystickRight;
    private OnScreenJoystick mScreenJoystickLeft;

    private FlightController mFlightController;
    private Timer mSendVirtualStickDataTimer;
    private SendVirtualStickDataTask mSendVirtualStickDataTask;
    private float mPitch;
    private float mRoll;
    private float mYaw;
    private float mThrottle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        checkAndRequestPermissions();
        setContentView(R.layout.activity_simulator_main);
        initUI();
        initFlightController();
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(FPVDemoApplication.FLAG_CONNECTION_CHANGE);
//        registerReceiver(mReceiver, filter);
    }


    @Override
    public void onPause() {
        Log.e(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.e(TAG, "onStop");
        super.onStop();
    }

    public void onReturn(View view) {
        Log.e(TAG, "onReturn");
        this.finish();
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy");
//        unregisterReceiver(mReceiver);
        if (null != mSendVirtualStickDataTimer) {
            mSendVirtualStickDataTask.cancel();
            mSendVirtualStickDataTask = null;
            mSendVirtualStickDataTimer.cancel();
            mSendVirtualStickDataTimer.purge();
            mSendVirtualStickDataTimer = null;
        }
        super.onDestroy();
    }

    private void initUI() {

        mBtnEnableVirtualStick = (Button) findViewById(R.id.btn_enable_virtual_stick);
        mBtnDisableVirtualStick = (Button) findViewById(R.id.btn_disable_virtual_stick);
        mBtnTakeOff = (Button) findViewById(R.id.btn_take_off);
        mBtnLand = (Button) findViewById(R.id.btn_land);
        mBtnSimulator = (ToggleButton) findViewById(R.id.btn_start_simulator);
        mTextView = (TextView) findViewById(R.id.textview_simulator);
        mConnectStatusTextView = (TextView) findViewById(R.id.ConnectStatusTextView);
        mScreenJoystickRight = (OnScreenJoystick) findViewById(R.id.directionJoystickRight);
        mScreenJoystickLeft = (OnScreenJoystick) findViewById(R.id.directionJoystickLeft);
        btn_init = findViewById(R.id.btn_init);
        btn_yt_p = findViewById(R.id.btn_yt_p);
        btn_yt_r = findViewById(R.id.btn_yt_r);
        btn_yt_y = findViewById(R.id.btn_yt_y);

        mBtnEnableVirtualStick.setOnClickListener(this);
        mBtnDisableVirtualStick.setOnClickListener(this);
        mBtnTakeOff.setOnClickListener(this);
        mBtnLand.setOnClickListener(this);
        btn_init.setOnClickListener(this);
        btn_yt_p.setOnClickListener(this);
        btn_yt_r.setOnClickListener(this);
        btn_yt_y.setOnClickListener(this);

//        如果mBtnSimulator选中了切换按钮，则显示mTextView。接下来，如果a mFlightController不为null，则通过start()
//        向其传递一个InitializationData带有LocationCoordinate2D结构（纬度23和经度113），updateFrequency 10和satelliteCount 10参数的方法来调用DJISimulator 的方法。
//        接着，在此改变onResult()的方法start()中，调用showToast()方法来显示启动模拟器结果给用户。
//        同样，如果mBtnSimulator未选中切换按钮，则调用stop()DJISimulator 的方法以停止模拟器。此外，重写该onResult()方法并调用该showToast()方法以向用户显示停止模拟器结果。
//        mBtnSimulator.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//
//                    mTextView.setVisibility(View.VISIBLE);
//
//                    if (mFlightController != null) {
//
//                        mFlightController.getSimulator()
//                                .start(InitializationData.createInstance(new LocationCoordinate2D(23, 113), 10, 10),
//                                        new CommonCallbacks.CompletionCallback() {
//                                            @Override
//                                            public void onResult(DJIError djiError) {
//                                                if (djiError != null) {
//                                                    showToast(djiError.getDescription());
//                                                }else
//                                                {
//                                                    showToast("Start Simulator Success");
//                                                }
//                                            }
//                                        });
//                    }
//
//                } else {
//
//                    mTextView.setVisibility(View.INVISIBLE);
//
//                    if (mFlightController != null) {
//                        mFlightController.getSimulator()
//                                .stop(new CommonCallbacks.CompletionCallback() {
//                                          @Override
//                                          public void onResult(DJIError djiError) {
//                                              if (djiError != null) {
//                                                  showToast(djiError.getDescription());
//                                              }else
//                                              {
//                                                  showToast("Stop Simulator Success");
//                                              }
//                                          }
//                                      }
//                                );
//                    }
//                }
//            }
//        });


//        覆盖setJoystickListener的onTouch()方法，并通过检查和变量的值是否小于0.02来过滤和的值。如果值太小，我们不应将虚拟摇杆数据频繁发送给飞行控制器。pXpY
//        获得最大的俯仰和横滚控制速度，然后将其存储到pitchJoyControlMaxSpeed和rollJoyControlMaxSpeed变量中。由于的值pX在-1（左）和1（右）之间，所以的值pY在-1（下）和1（上）之间，我们使用pitchJoyControlMaxSpeed和rollJoyControlMaxSpeed值相乘来更新mPitch和mRoll数据。这里我们以遥控器的模式2（美式模式）为例。
//        最后，我们检查是否mSendVirtualStickDataTimer为null，并通过调用该SendVirtualStickDataTask()方法来创建它。然后，创建mSendVirtualStickDataTimer并调用其schedule()方法，以mSendVirtualStickDataTask在后续执行之间传递变量，0毫秒的延迟和200毫秒来触发计时器。
//        类似地，实现变量的setJoystickListener()方法mScreenJoystickRight以更新mYaw和mThrottle值，并触发计时器以将虚拟摇杆数据发送到飞机的飞行控制器。
        mScreenJoystickLeft.setJoystickListener(new OnScreenJoystickListener() {

            @Override
            public void onTouch(OnScreenJoystick joystick, float pX, float pY) {
                if (Math.abs(pX) < 0.02) {
                    pX = 0;
                }

                if (Math.abs(pY) < 0.02) {
                    pY = 0;
                }
//                float verticalJoyControlMaxSpeed = 2;
//                float yawJoyControlMaxSpeed = 30;
                float verticalJoyControlMaxSpeed = 1;
                float yawJoyControlMaxSpeed = 15;

                mYaw = (float) (yawJoyControlMaxSpeed * pX);
                mThrottle = (float) (verticalJoyControlMaxSpeed * pY);

                if (null == mSendVirtualStickDataTimer) {
                    mSendVirtualStickDataTask = new SendVirtualStickDataTask();
                    mSendVirtualStickDataTimer = new Timer();
                    mSendVirtualStickDataTimer.schedule(mSendVirtualStickDataTask, 0, 200);
                }

            }

        });

        mScreenJoystickRight.setJoystickListener(new OnScreenJoystickListener() {

            @Override
            public void onTouch(OnScreenJoystick joystick, float pX, float pY) {


                if (Math.abs(pX) < 0.02) {
                    pX = 0;
                }

                if (Math.abs(pY) < 0.02) {
                    pY = 0;
                }
                float pitchJoyControlMaxSpeed = 5;//3,5,10
                float rollJoyControlMaxSpeed = 5;//3,5,10



                mPitch = (float) (pitchJoyControlMaxSpeed * pX);

                mRoll = (float) (rollJoyControlMaxSpeed * pY);

                if (null == mSendVirtualStickDataTimer) {
                    mSendVirtualStickDataTask = new SendVirtualStickDataTask();
                    mSendVirtualStickDataTimer = new Timer();
                    mSendVirtualStickDataTimer.schedule(mSendVirtualStickDataTask, 100, 200);
                }

            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_enable_virtual_stick:
                if (mFlightController != null) {

                    mFlightController.setVirtualStickModeEnabled(true, new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (djiError != null) {
                                showToast(djiError.getDescription());
                            } else {
                                showToast("Enable Virtual Stick Success");
                            }
                        }
                    });


                }
                break;
            case R.id.btn_disable_virtual_stick:
                if (mFlightController != null) {
                    mFlightController.setVirtualStickModeEnabled(false, new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (djiError != null) {
                                showToast(djiError.getDescription());
                            } else {
                                showToast("Disable Virtual Stick Success");
                            }
                        }
                    });
                }
                break;
            //起飞
            case R.id.btn_take_off:
//            ATTI=A模式 GPS_SPORT=S模式  GPS_ATTI=P模式
//                mFlightController.getState().setFlightMode(FlightMode.ATTI);
                if (mFlightController != null) {
                    mFlightController.startTakeoff(
                            new CommonCallbacks.CompletionCallback() {
                                @Override
                                public void onResult(DJIError djiError) {
                                    if (djiError != null) {
                                        showToast(djiError.getDescription());
                                    } else {
                                        showToast("Take off Success");
                                    }
                                }
                            }
                    );
                }
                break;
            //降落
            case R.id.btn_land:
                if (mFlightController != null) {
                    mFlightController.getState().setIslandingConfirmationNeeded(false);
                    mFlightController.startLanding(
                            new CommonCallbacks.CompletionCallback() {
                                @Override
                                public void onResult(DJIError djiError) {
                                    if (djiError != null) {
                                        showToast(djiError.getDescription());
                                    } else {
                                        showToast("Start Landing");
                                    }
                                }
                            }
                    );
                }
                break;
            case R.id.btn_init:
                initFlightController();
                break;
            case R.id.btn_yt_p:
//                -30~90
                Number minValue = ((DJIParamMinMaxCapability) (FPVDemoApplication.getProductInstance().getGimbal().getCapabilities().get(CapabilityKey.ADJUST_PITCH))).getMax();
                Rotation.Builder builder = new Rotation.Builder().mode(RotationMode.ABSOLUTE_ANGLE).time(2);

                builder.pitch(minValue.floatValue());
//                    builder.yaw(minValue.floatValue());
//                    builder.roll(minValue.floatValue());
                builder.build();
                sendRotateGimbalCommand(builder.build());


                break;
            case R.id.btn_yt_r:
//                0~0
//                Number minValue2 = ((DJIParamMinMaxCapability) (FPVDemoApplication.getProductInstance().getGimbal().getCapabilities().get(CapabilityKey.ADJUST_ROLL))).getMax();
//                Rotation.Builder builder2 = new Rotation.Builder().mode(RotationMode.ABSOLUTE_ANGLE).time(2);
//
//                builder2.roll(minValue2.floatValue());
//                builder2.build();
//                sendRotateGimbalCommand(builder2.build());
                //无法变
                break;
            case R.id.btn_yt_y:
//                -15~15
                Number minValue3 = ((DJIParamMinMaxCapability) (FPVDemoApplication.getProductInstance().getGimbal().getCapabilities().get(CapabilityKey.ADJUST_YAW))).getMax();
                Rotation.Builder builder3 = new Rotation.Builder()
                        .mode(RotationMode.ABSOLUTE_ANGLE).time(2);

                builder3.yaw(minValue3.floatValue());
                builder3.build();
                sendRotateGimbalCommand(builder3.build());
                break;
            default:
                break;
        }
    }

    private void sendRotateGimbalCommand(Rotation rotation) {
        Gimbal gimbal = FPVDemoApplication.getProductInstance().getGimbal();
        if (gimbal == null) {
            return;
        }
        gimbal.rotate(rotation, new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                showToast("Rotation Success");
            }
        });
    }


    @Override
    public void onResume() {
        Log.e(TAG, "onResume");
        super.onResume();
        updateTitleBar();
        initFlightController();
//        loginAccount();
    }


    //    调用sendVirtualStickFlightControlData()FlightController 的方法以发送虚拟操纵杆飞行控制数据。
//    在这里，我们创建FlightControlData从四个浮点型变量对象之前宣称：mPitch，mRoll，mYaw和mThrottle。
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

                            }
                        }
                );
            }
        }
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


//            mFlightController.getSimulator().setStateCallback(new SimulatorState.Callback() {
//                @Override
//                public void onUpdate(final SimulatorState stateData) {
//                    new Handler(Looper.getMainLooper()).post(new Runnable() {
//                        @Override
//                        public void run() {
//
//                            String yaw = String.format("%.2f", stateData.getYaw());
//                            String pitch = String.format("%.2f", stateData.getPitch());
//                            String roll = String.format("%.2f", stateData.getRoll());
//                            String positionX = String.format("%.2f", stateData.getPositionX());
//                            String positionY = String.format("%.2f", stateData.getPositionY());
//                            String positionZ = String.format("%.2f", stateData.getPositionZ());
//
//                            mTextView.setText("Yaw : " + yaw + ", Pitch : " + pitch + ", Roll : " + roll + "\n" + ", PosX : " + positionX +
//                                    ", PosY : " + positionY +
//                                    ", PosZ : " + positionZ);
//                        }
//                    });
//                }
//            });

            mFlightController.setStateCallback(new FlightControllerState.Callback() {
                @Override
                public void onUpdate(FlightControllerState flightControllerState) {
                    Log.d(TAG, "=====================================================");
                    Log.d(TAG, "卫星数=" + flightControllerState.getSatelliteCount() + "");
                    Log.d(TAG, "飞机的高度=" + flightControllerState.getUltrasonicHeightInMeters());//低于5米才有参考价值
                    Log.d(TAG, "getFlightModeString=" + flightControllerState.getFlightModeString());
                    Log.d(TAG, "getFlightMode=" + flightControllerState.getFlightMode());
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
                    Log.d(TAG, "IslandingConfirmationNeeded=" + flightControllerState.isLandingConfirmationNeeded()+"");

                }
            });
            //电量
            FPVDemoApplication.getProductInstance().getBattery().setStateCallback(new BatteryState.Callback() {
                @Override
                public void onUpdate(BatteryState batteryState) {
                    Log.d(TAG, "当前电量=" + batteryState.getChargeRemaining() + "%");


                }
            });


        }
    }

    public void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(SimulatorMainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTitleBar() {
        if (mConnectStatusTextView == null) return;
        boolean ret = false;
        BaseProduct product = FPVDemoApplication.getProductInstance();
        if (product != null) {
            if (product.isConnected()) {
                //The product is connected
                mConnectStatusTextView.setText(FPVDemoApplication.getProductInstance().getModel() + " Connected");
                ret = true;
            } else {
                if (product instanceof Aircraft) {
                    Aircraft aircraft = (Aircraft) product;
                    if (aircraft.getRemoteController() != null && aircraft.getRemoteController().isConnected()) {
                        // The product is not connected, but the remote controller is connected
                        mConnectStatusTextView.setText("only RC Connected");
                        ret = true;
                    }
                }
            }
        }

        if (!ret) {
            // The product or the remote controller are not connected.
            mConnectStatusTextView.setText("Disconnected");
        }
    }

//    protected BroadcastReceiver mReceiver = new BroadcastReceiver() {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            updateTitleBar();
//        }
//    };

//    private void checkAndRequestPermissions() {
//        // Check for permissions
//        for (String eachPermission : REQUIRED_PERMISSION_LIST) {
//            if (ContextCompat.checkSelfPermission(this, eachPermission) != PackageManager.PERMISSION_GRANTED) {
//                missingPermission.add(eachPermission);
//            }
//        }
//        // Request for missing permissions
//        if (!missingPermission.isEmpty() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            ActivityCompat.requestPermissions(this,
//                    missingPermission.toArray(new String[missingPermission.size()]),
//                    REQUEST_PERMISSION_CODE);
//        }
//
//    }

    /**
     * Result of runtime permission request
     */
//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        // Check for granted permission and remove from missing list
//        if (requestCode == REQUEST_PERMISSION_CODE) {
//            for (int i = grantResults.length - 1; i >= 0; i--) {
//                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
//                    missingPermission.remove(permissions[i]);
//                }
//            }
//        }
//        // If there is enough permission, we will start the registration
//        if (missingPermission.isEmpty()) {
//            startSDKRegistration();
//        } else {
//            showToast("Missing permissions!!!");
//        }
//    }

//    private void startSDKRegistration() {
//        if (isRegistrationInProgress.compareAndSet(false, true)) {
//            AsyncTask.execute(new Runnable() {
//                @Override
//                public void run() {
//                    showToast("registering, pls wait...");
//                    DJISDKManager.getInstance().registerApp(getApplicationContext(), new DJISDKManager.SDKManagerCallback() {
//                        @Override
//                        public void onRegister(DJIError djiError) {
//                            if (djiError == DJISDKError.REGISTRATION_SUCCESS) {
//                                DJILog.e("App registration", DJISDKError.REGISTRATION_SUCCESS.getDescription());
//                                DJISDKManager.getInstance().startConnectionToProduct();
//                                showToast("Register Success");
//                            } else {
//                                showToast("Register sdk fails, check network is available");
//                            }
//                            Log.v(TAG, djiError.getDescription());
//                        }
//
//                        @Override
//                        public void onProductDisconnect() {
//                            Log.d(TAG, "onProductDisconnect");
//                            showToast("Product Disconnected");
//
//                        }
//
//                        @Override
//                        public void onProductConnect(BaseProduct baseProduct) {
//                            Log.d(TAG, String.format("onProductConnect newProduct:%s", baseProduct));
//                            showToast("Product Connected");
//
//                        }
//
//                        @Override
//                        public void onComponentChange(BaseProduct.ComponentKey componentKey, BaseComponent oldComponent,
//                                                      BaseComponent newComponent) {
//
//                            if (newComponent != null) {
//                                newComponent.setComponentListener(new BaseComponent.ComponentListener() {
//
//                                    @Override
//                                    public void onConnectivityChange(boolean isConnected) {
//                                        Log.d(TAG, "onComponentConnectivityChanged: " + isConnected);
//                                    }
//                                });
//                            }
//                            Log.d(TAG,
//                                    String.format("onComponentChange key:%s, oldComponent:%s, newComponent:%s",
//                                            componentKey,
//                                            oldComponent,
//                                            newComponent));
//
//                        }
//
//                        @Override
//                        public void onInitProcess(DJISDKInitEvent djisdkInitEvent, int i) {
//
//                        }
//
//                        @Override
//                        public void onDatabaseDownloadProgress(long l, long l1) {
//
//                        }
//                    });
//                }
//            });
//        }
//    }

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


}
