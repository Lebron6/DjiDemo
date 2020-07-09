package com.compass.ux.utils;

/**
 * Created by xhf on 2020-07-07
 */
public class fastClick {
    private static long lastClickTime;

    public synchronized static boolean Click() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 1000) {
//            CommonUtil.showShortToast(context,"客官请慢点");
            lastClickTime = time;
            return false;
        }
        lastClickTime = time;
        return true;
    }
}
