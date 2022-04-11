package com.compass.ux.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;



import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.compass.ux.app.ApronApp;
import com.compass.ux.utils.FileUtils;
import com.google.gson.Gson;
//import com.taobao.sophix.SophixManager;
import com.yanzhenjie.permission.AndPermission;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.compass.ux.app.ApronApp.HAVE_Permission;

public class FirstActivity extends AppCompatActivity {
    private static final String TAG = FirstActivity.class.getName();
    File file = new File(Environment.getExternalStorageDirectory().getPath() + "/Shebei");
    private static final int REQUEST_PERMISSION_CODE = 12345;
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
    }


    void getvalues() {
        if (TextUtils.isEmpty(ApronApp.EQUIPMENT_ID)) {
                        String filePath = FileUtils.createIfNotExist(Environment.getExternalStorageDirectory().getPath() + "/Shebei");
                        FileUtils.writeString(filePath, "Mobile_03test", "utf-8");
                        ApronApp.EQUIPMENT_ID = FileUtils.readString(file.getAbsolutePath(), "utf-8");
                    Intent intent = new Intent(FirstActivity.this, ConnectionActivity.class);
                    startActivity(intent);
                    finish();
        }
    }

}
