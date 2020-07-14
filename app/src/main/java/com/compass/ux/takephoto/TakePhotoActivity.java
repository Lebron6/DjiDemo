package com.compass.ux.takephoto;

import androidx.appcompat.app.AppCompatActivity;
import dji.common.camera.SettingsDefinitions;
import dji.common.camera.SystemState;
import dji.common.error.DJIError;
import dji.common.product.Model;
import dji.common.util.CommonCallbacks;
import dji.sdk.base.BaseProduct;
import dji.sdk.camera.Camera;
import dji.sdk.camera.VideoFeeder;
import dji.sdk.codec.DJICodecManager;

import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.compass.ux.MainActivity;
import com.compass.ux.R;

public class TakePhotoActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener, View.OnClickListener {
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void onClick(View v) {

    }
//    protected DJICodecManager mCodecManager = null;
//    protected TextureView mVideoSurface = null;
//    private Button mCaptureBtn, mShootPhotoModeBtn, mRecordVideoModeBtn;
//    private ToggleButton mRecordBtn;
//    private TextView recordingTime;
//
//    private static final String TAG = MainActivity.class.getName();
//    protected VideoFeeder.VideoDataListener mReceivedVideoDataListener = null;
//    private Handler handler;
//    Camera camera=null;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        setContentView(R.layout.activity_take_photo);
//        handler = new Handler();
//        initUI();
////        我们mReceivedVideoDataListener使用VideoFeeder的初始化变量VideoDataListener()。
////        在回调内部，我们重写其onReceive()方法以获取原始H264视频数据并将其发送给mCodecManager解码
//        mReceivedVideoDataListener = new VideoFeeder.VideoDataListener() {
//
//            @Override
//            public void onReceive(byte[] videoBuffer, int size) {
//                if (mCodecManager != null) {
//                    mCodecManager.sendDataToDecoder(videoBuffer, size);
//                }
//            }
//        };
//    }
//
//    protected void onProductChange() {
//        initPreviewer();
//    }
//
//    @Override
//    public void onResume() {
//        Log.e(TAG, "onResume");
//        super.onResume();
//        initPreviewer();
//        onProductChange();
//
//        if(mVideoSurface == null) {
//            Log.e(TAG, "mVideoSurface is null");
//        }
//    }
//
//    @Override
//    public void onPause() {
//        Log.e(TAG, "onPause");
//        uninitPreviewer();
//        super.onPause();
//    }
//
//    @Override
//    protected void onDestroy() {
//        Log.e(TAG, "onDestroy");
//        uninitPreviewer();
//        super.onDestroy();
//    }
//
////    在mVideoSurfaceTextureView 上显示和重置实时视频流
//    private void initPreviewer() {
//
//        BaseProduct product = FPVDemoApplication.getProductInstance();
//
//        if (product == null || !product.isConnected()) {
//            showToast(getString(R.string.disconnected));
//        } else {
//            if (null != mVideoSurface) {
//                mVideoSurface.setSurfaceTextureListener(this);
//            }
//            if (!product.getModel().equals(Model.UNKNOWN_AIRCRAFT)) {
//                VideoFeeder.getInstance().getPrimaryVideoFeed().addVideoDataListener(mReceivedVideoDataListener);
//            }
//        }
//    }
//
//    private void uninitPreviewer() {
//        camera = FPVDemoApplication.getCameraInstance();
//        if (camera != null){
//            // Reset the callback
//            VideoFeeder.getInstance().getPrimaryVideoFeed().addVideoDataListener(null);
//        }
//    }
//
//
//    @Override
//    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
//        Log.e(TAG, "onSurfaceTextureAvailable");
//        if (mCodecManager == null) {
//            mCodecManager = new DJICodecManager(this, surface, width, height);
//        }
//    }
//
//    @Override
//    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
//        Log.e(TAG, "onSurfaceTextureSizeChanged");
//    }
//
//    @Override
//    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
//        Log.e(TAG,"onSurfaceTextureDestroyed");
//        if (mCodecManager != null) {
//            mCodecManager.cleanSurface();
//            mCodecManager = null;
//        }
//
//        return false;
//    }
//
//    @Override
//    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
//    }
//
//
//
//    @Override
//    public void onStop() {
//        super.onStop();
//    }
//
//    public void onReturn(View view){
//        this.finish();
//    }
//
//
//    private void initUI() {
//        // init mVideoSurface
//        mVideoSurface = (TextureView)findViewById(R.id.video_previewer_surface);
//
//        recordingTime = (TextView) findViewById(R.id.timer);
//        mCaptureBtn = (Button) findViewById(R.id.btn_capture);
//        mRecordBtn = (ToggleButton) findViewById(R.id.btn_record);
//        mShootPhotoModeBtn = (Button) findViewById(R.id.btn_shoot_photo_mode);
//        mRecordVideoModeBtn = (Button) findViewById(R.id.btn_record_video_mode);
//
//        if (null != mVideoSurface) {
//            mVideoSurface.setSurfaceTextureListener(this);
//        }
//
//        mCaptureBtn.setOnClickListener(this);
//        mRecordBtn.setOnClickListener(this);
//        mShootPhotoModeBtn.setOnClickListener(this);
//        mRecordVideoModeBtn.setOnClickListener(this);
//
//        recordingTime.setVisibility(View.INVISIBLE);
//
//        mRecordBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    recordingTime.setVisibility(View.VISIBLE);
//                    startRecord();
//
//                } else {
//                    recordingTime.setVisibility(View.INVISIBLE);
//                    stopRecord();
//                }
//            }
//        });
//
//        camera = FPVDemoApplication.getCameraInstance();
//        if (camera != null) {
//
//            camera.setSystemStateCallback(new SystemState.Callback() {
//                @Override
//                public void onUpdate(SystemState cameraSystemState) {
//                    if (null != cameraSystemState) {
//                        //获取录制时间
//                        int recordTime = cameraSystemState.getCurrentVideoRecordingTimeInSeconds();
//                        int minutes = (recordTime % 3600) / 60;
//                        int seconds = recordTime % 60;
//
//                        final String timeString = String.format("%02d:%02d", minutes, seconds);
//                        final boolean isVideoRecording = cameraSystemState.isRecording();
//
//                        TakePhotoActivity.this.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                recordingTime.setText(timeString);
//                                /*
//                                 * Update recordingTime TextView visibility and mRecordBtn's check state
//                                 */
//                                if (isVideoRecording){
//                                    recordingTime.setVisibility(View.VISIBLE);
//                                }else
//                                {
//                                    recordingTime.setVisibility(View.INVISIBLE);
//                                }
//                            }
//                        });
//                    }
//                }
//            });
//
//        }
//
//
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.btn_capture:{
//                captureAction();
//                break;
//            }
//            case R.id.btn_shoot_photo_mode:{
//                switchCameraMode(SettingsDefinitions.CameraMode.SHOOT_PHOTO);
//                break;
//            }
//            case R.id.btn_record_video_mode:{
//                switchCameraMode(SettingsDefinitions.CameraMode.RECORD_VIDEO);
//                break;
//            }
//            default:
//                break;
//        }
//    }
//
//    private void captureAction(){
//
////         camera = FPVDemoApplication.getCameraInstance();
//        if (camera != null) {
//
//            SettingsDefinitions.ShootPhotoMode photoMode = SettingsDefinitions.ShootPhotoMode.SINGLE;//设置相机模式
//            camera.setShootPhotoMode(photoMode, new CommonCallbacks.CompletionCallback(){
//                @Override
//                public void onResult(DJIError djiError) {
//                    if (null == djiError) {
//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                //获得照片
//                                camera.startShootPhoto(new CommonCallbacks.CompletionCallback() {
//                                    @Override
//                                    public void onResult(DJIError djiError) {
//                                        if (djiError == null) {
//                                            showToast("take photo: success");
//                                        } else {
//                                            showToast(djiError.getDescription());
//                                        }
//                                    }
//                                });
//                            }
//                        }, 2000);
//                    }
//                }
//            });
//        }
//    }
//
//    private void switchCameraMode(SettingsDefinitions.CameraMode cameraMode){
//
////        camera = FPVDemoApplication.getCameraInstance();
//        if (camera != null) {
//            camera.setMode(cameraMode, new CommonCallbacks.CompletionCallback() {
//                @Override
//                public void onResult(DJIError error) {
//
//                    if (error == null) {
//                        showToast("Switch Camera Mode Succeeded");
//                    } else {
//                        showToast(error.getDescription());
//                    }
//                }
//            });
//        }
//    }
//
//    private void startRecord(){
//
////         camera = FPVDemoApplication.getCameraInstance();
//        if (camera != null) {
//            camera.startRecordVideo(new CommonCallbacks.CompletionCallback(){
//                @Override
//                public void onResult(DJIError djiError)
//                {
//                    if (djiError == null) {
//                        showToast("Record video: success");
//                    }else {
//                        showToast(djiError.getDescription());
//                    }
//                }
//            }); // Execute the startRecordVideo API
//        }
//    }
//
//    // Method for stopping recording
//    private void stopRecord(){
//
////        camera = FPVDemoApplication.getCameraInstance();
//        if (camera != null) {
//            camera.stopRecordVideo(new CommonCallbacks.CompletionCallback(){
//
//                @Override
//                public void onResult(DJIError djiError)
//                {
//                    if(djiError == null) {
//                        showToast("Stop recording: success");
//                    }else {
//                        showToast(djiError.getDescription());
//                    }
//                }
//            }); // Execute the stopRecordVideo API
//        }
//
//    }
//
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

}



