package com.compass.ux.utils;

/**
 * Created by xhf on 2020-07-07
 */
public class FastClick {
    private static long flightControllerlastClickTime;
    public synchronized static boolean flightControllerClick() {
        long time = System.currentTimeMillis();
        if (time - flightControllerlastClickTime > 1000) {
            flightControllerlastClickTime = time;
            return true;
        }
        return false;
    }

    private static long downLoadSignallastClickTime;
    public synchronized static boolean downLoadSignalClick() {
        long time = System.currentTimeMillis();
        if (time - downLoadSignallastClickTime > 1000) {
            downLoadSignallastClickTime = time;
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

    private static long perceptionlastClickTime;
    public synchronized static boolean PerceptionClick() {
        long time = System.currentTimeMillis();
        if (time - perceptionlastClickTime > 1000) {
            perceptionlastClickTime = time;
            return true;
        }
        return false;
    }

    private static long rtkStatelastClickTime;
    public synchronized static boolean rtkStateClick() {
        long time = System.currentTimeMillis();
        if (time - rtkStatelastClickTime > 1000) {
            rtkStatelastClickTime = time;
            return true;
        }
        return false;
    }

    private static long laserDatalastClickTime;
    public synchronized static boolean laserClick() {
        long time = System.currentTimeMillis();
        if (time - laserDatalastClickTime > 1000) {
            laserDatalastClickTime = time;
            return true;
        }
        return false;
    }

    private static long bsInfolastClickTime;
    public synchronized static boolean bsInfoClick() {
        long time = System.currentTimeMillis();
        if (time - bsInfolastClickTime > 1000) {
            bsInfolastClickTime = time;
            return true;
        }
        return false;
    }

    private static long linkPlanelastClickTime;
    public synchronized static boolean linkPlaneClickTimeClick() {
        long time = System.currentTimeMillis();
        if (time - linkPlanelastClickTime > 1000) {
            linkPlanelastClickTime = time;
            return true;
        }
        return false;
    }

    private static long storageStateClickTime;
    public synchronized static boolean storageClickTimeClick() {
        long time = System.currentTimeMillis();
        if (time - storageStateClickTime > 1000) {
            storageStateClickTime = time;
            return true;
        }
        return false;
    }

    private static long diagnosticsClickTime;
    public synchronized static boolean diagnosticsClickTime() {
        long time = System.currentTimeMillis();
        if (time - diagnosticsClickTime > 1000) {
            diagnosticsClickTime = time;
            return true;
        }
        return false;
    }

    private static long isFlyClickTime;
    public synchronized static boolean isFlyClickTime() {
        long time = System.currentTimeMillis();
        if (time - isFlyClickTime > 1000) {
            isFlyClickTime = time;
            return true;
        }
        return false;
    }
}
