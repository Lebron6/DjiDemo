package com.compass.ux.bean;

/**
 * Created by xhf
 * on 2020-08-10 17:29
 */
public class StorageStateBean {
    //剩余可拍照时间
    private long AvailableCaptureCount;
    //剩余可录像秒数
    private int AvailableRecordingTimeInSeconds;

    public long getAvailableCaptureCount() {
        return AvailableCaptureCount;
    }

    public void setAvailableCaptureCount(long availableCaptureCount) {
        AvailableCaptureCount = availableCaptureCount;
    }

    public int getAvailableRecordingTimeInSeconds() {
        return AvailableRecordingTimeInSeconds;
    }

    public void setAvailableRecordingTimeInSeconds(int availableRecordingTimeInSeconds) {
        AvailableRecordingTimeInSeconds = availableRecordingTimeInSeconds;
    }
}
