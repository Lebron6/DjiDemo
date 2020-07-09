package com.compass.ux;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import dji.common.error.DJIError;
import dji.common.error.DJISDKError;
import dji.common.realname.AircraftBindingState;
import dji.common.realname.AppActivationState;
import dji.common.useraccount.UserAccountState;
import dji.common.util.CommonCallbacks;
import dji.sdk.base.BaseComponent;
import dji.sdk.base.BaseProduct;
import dji.sdk.realname.AppActivationManager;
import dji.sdk.sdkmanager.DJISDKInitEvent;
import dji.sdk.sdkmanager.DJISDKManager;
import dji.sdk.useraccount.UserAccountManager;
import dji.thirdparty.afinal.core.AsyncTask;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

//public class MainActivity extends AppCompatActivity {
//
//    private static final String TAG = MainActivity.class.getName();
//    public static final String FLAG_CONNECTION_CHANGE = "dji_sdk_connection_change";
//    private static BaseProduct mProduct;
//    private Handler mHandler;
//
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
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        // When the compile and target version is higher than 22, please request the following permission at runtime to ensure the SDK works well.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            checkAndRequestPermissions();
//        }
//
//        setContentView(R.layout.activity_main);
//
//        //Initialize DJI SDK Manager
//        mHandler = new Handler(Looper.getMainLooper());
//
//    }
//
//    /**
//     * Checks if there is any missing permissions, and
//     * requests runtime permission if needed.
//     */
//    private void checkAndRequestPermissions() {
//        // Check for permissions
//        for (String eachPermission : REQUIRED_PERMISSION_LIST) {
//            if (ContextCompat.checkSelfPermission(this, eachPermission) != PackageManager.PERMISSION_GRANTED) {
//                missingPermission.add(eachPermission);
//            }
//        }
//        // Request for missing permissions
//        if (missingPermission.isEmpty()) {
//            startSDKRegistration();
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            showToast("Need to grant the permissions!");
//            ActivityCompat.requestPermissions(this,
//                    missingPermission.toArray(new String[missingPermission.size()]),
//                    REQUEST_PERMISSION_CODE);
//        }
//
//    }
//
//    /**
//     * Result of runtime permission request
//     */
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
//
//
//    private void startSDKRegistration() {
//        if (isRegistrationInProgress.compareAndSet(false, true)) {
//            AsyncTask.execute(new Runnable() {
//                @Override
//                public void run() {
//                    showToast("registering, pls wait...");
//                    DJISDKManager.getInstance().registerApp(MainActivity.this.getApplicationContext(), new DJISDKManager.SDKManagerCallback() {
//                        @Override
//                        public void onRegister(DJIError djiError) {
//                            if (djiError == DJISDKError.REGISTRATION_SUCCESS) {
//                                showToast("Register Success");
//                                DJISDKManager.getInstance().startConnectionToProduct();
//                            } else {
//                                showToast("Register sdk fails, please check the bundle id and network connection!");
//                            }
//                            Log.v(TAG, djiError.getDescription());
//                        }
//
//                        @Override
//                        public void onProductDisconnect() {
//                            Log.d(TAG, "onProductDisconnect");
//                            showToast("Product Disconnected");
//                            notifyStatusChange();
//
//                        }
//                        @Override
//                        public void onProductConnect(BaseProduct baseProduct) {
//                            Log.d(TAG, String.format("onProductConnect newProduct:%s", baseProduct));
//                            showToast("Product Connected");
//                            notifyStatusChange();
//
//                        }
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
//                                        notifyStatusChange();
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
//
//    private void notifyStatusChange() {
//        mHandler.removeCallbacks(updateRunnable);
//        mHandler.postDelayed(updateRunnable, 500);
//    }
//
//    private Runnable updateRunnable = new Runnable() {
//
//        @Override
//        public void run() {
//            Intent intent = new Intent(FLAG_CONNECTION_CHANGE);
//            sendBroadcast(intent);
//        }
//    };
//
//    private void showToast(final String toastMsg) {
//
//        Handler handler = new Handler(Looper.getMainLooper());
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_LONG).show();
//            }
//        });
//
//    }
//}

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getName();
    protected Button loginBtn;
    protected Button logoutBtn;
    protected TextView bindingStateTV;
    protected TextView appActivationStateTV;

    private AppActivationManager appActivationManager;
    private AppActivationState.AppActivationStateListener activationStateListener;
    private AircraftBindingState.AircraftBindingStateListener bindingStateListener;


    public static final String FLAG_CONNECTION_CHANGE = "dji_sdk_connection_change";
    private Handler mHandler;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        // When the compile and target version is higher than 22, please request the
//        // following permissions at runtime to ensure the
//        // SDK work well.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.VIBRATE,
//                            Manifest.permission.INTERNET, Manifest.permission.ACCESS_WIFI_STATE,
//                            Manifest.permission.WAKE_LOCK, Manifest.permission.ACCESS_COARSE_LOCATION,
//                            Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_FINE_LOCATION,
//                            Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
//                            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.SYSTEM_ALERT_WINDOW,
//                            Manifest.permission.READ_PHONE_STATE,
//                    }
//                    , 1);
//        }


         //When the compile and target version is higher than 22, please request the following permission at runtime to ensure the SDK works well.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkAndRequestPermissions();
        }

        setContentView(R.layout.activity_main);

        //Initialize DJI SDK Manager
        mHandler = new Handler(Looper.getMainLooper());
        initUI();
        initData();
    }

    @Override
    public void onResume() {
        Log.e(TAG, "onResume");
        setUpListener();
        super.onResume();
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

    public void onReturn(View view){
        Log.e(TAG, "onReturn");
        this.finish();
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy");
        tearDownListener();
        super.onDestroy();
    }

    private void initUI(){

        bindingStateTV = (TextView) findViewById(R.id.tv_binding_state_info);
        appActivationStateTV = (TextView) findViewById(R.id.tv_activation_state_info);
        loginBtn = (Button) findViewById(R.id.btn_login);
        logoutBtn = (Button) findViewById(R.id.btn_logout);
        loginBtn.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);

    }


    private void loginAccount(){

        UserAccountManager.getInstance().logIntoDJIUserAccount(MainActivity.this,
                new CommonCallbacks.CompletionCallbackWith<UserAccountState>() {
                    @Override
                    public void onSuccess(final UserAccountState userAccountState) {
                        Toast.makeText(getApplicationContext(),"Login Success",Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailure(DJIError error) {
                        Toast.makeText(getApplicationContext(),"Login Error:"
                                + error.getDescription(),Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void logoutAccount(){
        UserAccountManager.getInstance().logoutOfDJIUserAccount(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError error) {
                if (null == error) {
                    Toast.makeText(getApplicationContext(),"Login Success",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(),"Login Error:"
                            + error.getDescription(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_login:{
                loginAccount();
                break;
            }
            case R.id.btn_logout:{
                logoutAccount();
                break;
            }
            default:
                break;
        }
    }


    private void initData(){
        setUpListener();
        //初始化DJISDKManager
        appActivationManager = DJISDKManager.getInstance().getAppActivationManager();

        if (appActivationManager != null) {
            appActivationManager.addAppActivationStateListener(activationStateListener);
            appActivationManager.addAircraftBindingStateListener(bindingStateListener);
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    appActivationStateTV.setText("" + appActivationManager.getAppActivationState());
                    bindingStateTV.setText("" + appActivationManager.getAircraftBindingState());
                }
            });
        }
    }

    private void setUpListener() {
        // Example of Listener
        activationStateListener = new AppActivationState.AppActivationStateListener() {
            @Override
            public void onUpdate(final AppActivationState appActivationState) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        appActivationStateTV.setText("" + appActivationState);
                    }
                });
            }
        };

        bindingStateListener = new AircraftBindingState.AircraftBindingStateListener() {

            @Override
            public void onUpdate(final AircraftBindingState bindingState) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bindingStateTV.setText("" + bindingState);
                    }
                });
            }
        };
    }

    private void tearDownListener() {
        if (activationStateListener != null) {
            appActivationManager.removeAppActivationStateListener(activationStateListener);
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    appActivationStateTV.setText("Unknown");
                }
            });
        }
        if (bindingStateListener !=null) {
            appActivationManager.removeAircraftBindingStateListener(bindingStateListener);
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bindingStateTV.setText("Unknown");
                }
            });
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
        if (missingPermission.isEmpty()) {
            startSDKRegistration();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            showToast("Need to grant the permissions!");
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
                    DJISDKManager.getInstance().registerApp(MainActivity.this.getApplicationContext(), new DJISDKManager.SDKManagerCallback() {
                        @Override
                        public void onRegister(DJIError djiError) {
                            if (djiError == DJISDKError.REGISTRATION_SUCCESS) {
                                showToast("Register Success");
                                DJISDKManager.getInstance().startConnectionToProduct();
                            } else {
                                showToast("Register sdk fails, please check the bundle id and network connection!");
                            }
                            Log.v(TAG, djiError.getDescription());
                        }

                        @Override
                        public void onProductDisconnect() {
                            Log.d(TAG, "onProductDisconnect");
                            showToast("Product Disconnected");
                            notifyStatusChange();

                        }
                        @Override
                        public void onProductConnect(BaseProduct baseProduct) {
                            Log.d(TAG, String.format("onProductConnect newProduct:%s", baseProduct));
                            showToast("Product Connected");
                            notifyStatusChange();

                        }
                        @Override
                        public void onComponentChange(BaseProduct.ComponentKey componentKey, BaseComponent oldComponent,
                                                      BaseComponent newComponent) {

                            if (newComponent != null) {
                                newComponent.setComponentListener(new BaseComponent.ComponentListener() {

                                    @Override
                                    public void onConnectivityChange(boolean isConnected) {
                                        Log.d(TAG, "onComponentConnectivityChanged: " + isConnected);
                                        notifyStatusChange();
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
                }
            });
        }
    }

    private void notifyStatusChange() {
        mHandler.removeCallbacks(updateRunnable);
        mHandler.postDelayed(updateRunnable, 500);
    }

    private Runnable updateRunnable = new Runnable() {

        @Override
        public void run() {
            Intent intent = new Intent(FLAG_CONNECTION_CHANGE);
            sendBroadcast(intent);
        }
    };

    private void showToast(final String toastMsg) {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_LONG).show();
            }
        });

    }


}
