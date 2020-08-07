package com.compass.ux.bean;

/**
 * Created by xhf
 * on 2020-08-06 10:52
 */
public class PerceptionBean {
    /**
     * 每个值代表以角度间隔水平360度检测到的障碍物的距离，以毫米为单位。
     * 0度指向飞机的航向。飞机右侧指向90度。180度指向飞机的机尾。
     */
    private int[] distances;
    /**
     * 向上的障碍物距离。
     */
    private int upwardObstacleDistance;


    /**
     * 向下的障碍物距离。
     */
    private int downwardObstacleDistance;


    public int[] getDistances() {
        return distances;
    }

    public void setDistances(int[] distances) {
        this.distances = distances;
    }

    public int getUpwardObstacleDistance() {
        return upwardObstacleDistance;
    }

    public void setUpwardObstacleDistance(int upwardObstacleDistance) {
        this.upwardObstacleDistance = upwardObstacleDistance;
    }

    public int getDownwardObstacleDistance() {
        return downwardObstacleDistance;
    }

    public void setDownwardObstacleDistance(int downwardObstacleDistance) {
        this.downwardObstacleDistance = downwardObstacleDistance;
    }


}
