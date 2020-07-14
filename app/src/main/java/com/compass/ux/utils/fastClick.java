package com.compass.ux.utils;

/**
 * Created by xhf on 2020-07-07
 */
public class fastClick {
    private static long flightControllerlastClickTime;
    public synchronized static boolean flightControllerClick() {
        long time = System.currentTimeMillis();
        if (time - flightControllerlastClickTime > 1000) {
            flightControllerlastClickTime = time;
            return true;
        }
        return false;
    }

    private static long batteryLastClickTime;
    public synchronized static boolean batteryClick() {
        long time = System.currentTimeMillis();
        if (time - batteryLastClickTime > 1000) {
            batteryLastClickTime = time;
            return true;
        }
        return false;
    }
}
