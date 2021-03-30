package com.compass.ux.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import dji.common.error.DJIError;
import dji.common.error.DJISDKError;
import dji.common.useraccount.UserAccountState;
import dji.common.util.CommonCallbacks;
import dji.sdk.base.BaseComponent;
import dji.sdk.base.BaseProduct;
import dji.sdk.sdkmanager.DJISDKInitEvent;
import dji.sdk.sdkmanager.DJISDKManager;
import dji.sdk.useraccount.UserAccountManager;
import dji.thirdparty.afinal.core.AsyncTask;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.compass.ux.R;
import com.compass.ux.app.Constant;
import com.compass.ux.app.MApplication;
import com.compass.ux.bean.TextMessageBean;
import com.compass.ux.netty_lib.zhang.RsaUtil;
import com.compass.ux.utils.FileUtils;
import com.google.gson.Gson;
import com.yanzhenjie.permission.AndPermission;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.compass.ux.app.MApplication.HAVE_Permission;

public class FirstActivity extends AppCompatActivity {
    private static final String TAG = FirstActivity.class.getName();
    File file = new File(Environment.getExternalStorageDirectory().getPath() + "/Shebei");
    private List<String> missingPermission = new ArrayList<>();
    private static final int REQUEST_PERMISSION_CODE = 12345;
    private AtomicBoolean isRegistrationInProgress = new AtomicBoolean(false);
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
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
//        checkAndRequestPermissions();
//        if(HAVE_Permission){
//            Intent intent=new Intent(FirstActivity.this,ConnectionActivity.class);
//            startActivity(intent);
//            finish();
//        }else{
        AndPermission.with(this)
                .runtime()
                .permission(REQUIRED_PERMISSION_LIST)
                .onGranted(permissions -> {
                    // Storage permission are allowed.
                    HAVE_Permission = true;
                    getvalues();
                })
                .onDenied(permissions -> {
                    // Storage permission are not allowed.
                    HAVE_Permission = false;
                    finish();
                })
                .start();
//        }

    }


    void getvalues() {
        if (TextUtils.isEmpty(MApplication.EQUIPMENT_ID)) {
            MApplication.EQUIPMENT_ID = "Mobile_01";
            Intent intent = new Intent(FirstActivity.this, ConnectionActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(FirstActivity.this, ConnectionActivity.class);
            startActivity(intent);
            finish();
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
        } else {
//            havePermission = true;

            if (TextUtils.isEmpty(MApplication.EQUIPMENT_ID)) {
                String text_message = FileUtils.readString(file.getAbsolutePath(), "utf-8");

                if (!TextUtils.isEmpty(text_message)) {
                    TextMessageBean textMessageBean = gson.fromJson(text_message, TextMessageBean.class);
                    String mobile_Id = textMessageBean.getEquip_id();
                    MApplication.EQUIPMENT_ID = mobile_Id;
//                    MApplication.UPLOAD_URL = textMessageBean.getUpload_url();
                    Log.d("FileUtils", "FileUtils=" + mobile_Id);
                    Intent intent = new Intent(FirstActivity.this, ConnectionActivity.class);
                    startActivity(intent);
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
////            havePermission = true;
//            startSDKRegistration();
//            loginAccount();
//            if (TextUtils.isEmpty(MApplication.EQUIPMENT_ID) || TextUtils.isEmpty(MApplication.UPLOAD_URL)) {
//                String text_message = FileUtils.readString(file.getAbsolutePath(), "utf-8");
//
//                if (!TextUtils.isEmpty(text_message)) {
//                    TextMessageBean textMessageBean = gson.fromJson(text_message, TextMessageBean.class);
//                    String mobile_Id = RsaUtil.encrypt(textMessageBean.getEquip_id());
//                    MApplication.EQUIPMENT_ID = mobile_Id;
//                    MApplication.UPLOAD_URL = textMessageBean.getUpload_url();
//                    Log.d("FileUtils", "FileUtils=" + mobile_Id);
//                    Intent intent=new Intent(FirstActivity.this,ConnectionActivity.class);
//                    startActivity(intent);
//                } else {
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                Thread.sleep(10000);
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Toast.makeText(getApplicationContext(), "配置文件为空,请去添加", Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                                finish();
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }).start();
//                }
//            }
//        } else {
//            showToast("Missing permissions!!!");
////            havePermission = false;
//        }
//    }

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
                        public void onProductChanged(BaseProduct baseProduct) {

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

//        Handler handler = new Handler(Looper.getMainLooper());
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
        Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_LONG).show();
//            }
//        });

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

}
