package com.compass.ux.bean;

import java.util.List;

/**
 * Created by xhf
 * on 2021-01-19 14:23
 * https://xiaozhuanlan.com/topic/9718620354
 */
//{
//        "speed":"5",
//        "altitude":"120",
//        "flightPathMode":"",
//        "wayPoints":[
//        {
//        "speed":"5",
//        "altitude":"120",
//        "latitude":"31.32584",
//        "turnMode":"-1",
//        "longitude":"120.547518",
//        "flightPathMode":"",
//        "headingMode":""
//        }
//        ],
//        "headingMode":"0",
//        "finishedAction":"1",
//        "action":[
//        {
//        "trigger":{
//        "triggerType":"1",
//        "reachPointParam":{
//        "startIndex":"1",
//        "autoTerminateCount":"1"
//        },
//        "associateParam":{
//        "associateActionID":"1",
//        "associateType":"1",
//        "waitingTime":"1"
//        },
//        "trajectoryParam":{
//        "startIndex":"1",
//        "endIndex":"1"
//        },
//        "intervalTriggerParam":{
//        "startIndex":"1",
//        "type":"1",
//        "interval":"1"
//        }
//        }
//        },
//        {
//        "actuator":{
//        "actuatorType":"1",
//        "gimbalActuatorParam":{
//        "operationType":"1",
//        "pitch":"1",
//        "yaw":"1",
//        "time":"1"
//        },
//        "cameraOperationType":{
//        "operationType":"1",
//        "time":"1"
//        },
//        "aircraftControlType":{
//        "operationType":"1",
//        "startFly":"1",
//        "direction":"1",
//        "yawAngle":"1"
//        }
//        }
//        }
//        ]
//        }
public class WayPointsV2Bean {
    /**
     * speed : 5
     * altitude : 120
     * flightPathMode :
     * wayPoints : [{"speed":"5","altitude":"120","latitude":"31.32584","turnMode":"-1","longitude":"120.547518","flightPathMode":"","headingMode":"","wayPointAction":[{"actionType":"","yawAngle":"","direction":"","pitch":"","yaw":"","focalLength":"","waitingTime":""}]}]
     * headingMode : 0
     * finishedAction : 1
     */

    private String speed;
    private String altitude;
    private String flightPathMode;
    private String headingMode;
    private String finishedAction;
    private List<WayPointsBean> wayPoints;

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public String getFlightPathMode() {
        return flightPathMode;
    }

    public void setFlightPathMode(String flightPathMode) {
        this.flightPathMode = flightPathMode;
    }

    public String getHeadingMode() {
        return headingMode;
    }

    public void setHeadingMode(String headingMode) {
        this.headingMode = headingMode;
    }

    public String getFinishedAction() {
        return finishedAction;
    }

    public void setFinishedAction(String finishedAction) {
        this.finishedAction = finishedAction;
    }

    public List<WayPointsBean> getWayPoints() {
        return wayPoints;
    }

    public void setWayPoints(List<WayPointsBean> wayPoints) {
        this.wayPoints = wayPoints;
    }

    public static class WayPointsBean {
        /**
         * speed : 5
         * altitude : 120
         * latitude : 31.32584
         * turnMode : -1
         * longitude : 120.547518
         * flightPathMode :
         * headingMode :
         * wayPointAction : [{"actionType":"","yawAngle":"","direction":"","pitch":"","yaw":"","focalLength":"","waitingTime":""}]
         */

        private String speed;
        private String altitude;
        private String latitude;
        private String turnMode;
        private String longitude;
        private String flightPathMode;
        private String headingMode;
        private VoiceBean voice;
        private List<WayPointActionBean> wayPointAction;

        public VoiceBean getVoice() {
            return voice;
        }

        public void setVoice(VoiceBean voice) {
            this.voice = voice;
        }

        public String getSpeed() {
            return speed;
        }

        public void setSpeed(String speed) {
            this.speed = speed;
        }

        public String getAltitude() {
            return altitude;
        }

        public void setAltitude(String altitude) {
            this.altitude = altitude;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getTurnMode() {
            return turnMode;
        }

        public void setTurnMode(String turnMode) {
            this.turnMode = turnMode;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getFlightPathMode() {
            return flightPathMode;
        }

        public void setFlightPathMode(String flightPathMode) {
            this.flightPathMode = flightPathMode;
        }

        public String getHeadingMode() {
            return headingMode;
        }

        public void setHeadingMode(String headingMode) {
            this.headingMode = headingMode;
        }

        public List<WayPointActionBean> getWayPointAction() {
            return wayPointAction;
        }

        public void setWayPointAction(List<WayPointActionBean> wayPointAction) {
            this.wayPointAction = wayPointAction;
        }

        @Override
        public String toString() {
            return "WayPointsBean{" +
                    "speed='" + speed + '\'' +
                    ", altitude='" + altitude + '\'' +
                    ", latitude='" + latitude + '\'' +
                    ", turnMode='" + turnMode + '\'' +
                    ", longitude='" + longitude + '\'' +
                    ", flightPathMode='" + flightPathMode + '\'' +
                    ", headingMode='" + headingMode + '\'' +
                    ", wayPointAction=" + wayPointAction +
                    '}';
        }

        public static class WayPointActionBean {
            /**
             * actionType :
             * yawAngle :
             * direction :
             * pitch :
             * yaw :
             * focalLength :
             * waitingTime :
             */

            private String actionType;
            private String yawAngle;
            private String direction;
            private String pitch;
            private String yaw;
            private String focalLength;
            private String waitingTime;

            public String getActionType() {
                return actionType;
            }

            public void setActionType(String actionType) {
                this.actionType = actionType;
            }

            public String getYawAngle() {
                return yawAngle;
            }

            public void setYawAngle(String yawAngle) {
                this.yawAngle = yawAngle;
            }

            public String getDirection() {
                return direction;
            }

            public void setDirection(String direction) {
                this.direction = direction;
            }

            public String getPitch() {
                return pitch;
            }

            public void setPitch(String pitch) {
                this.pitch = pitch;
            }

            public String getYaw() {
                return yaw;
            }

            public void setYaw(String yaw) {
                this.yaw = yaw;
            }

            public String getFocalLength() {
                return focalLength;
            }

            public void setFocalLength(String focalLength) {
                this.focalLength = focalLength;
            }

            public String getWaitingTime() {
                return waitingTime;
            }

            public void setWaitingTime(String waitingTime) {
                this.waitingTime = waitingTime;
            }

            @Override
            public String toString() {
                return "WayPointActionBean{" +
                        "actionType='" + actionType + '\'' +
                        ", yawAngle='" + yawAngle + '\'' +
                        ", direction='" + direction + '\'' +
                        ", pitch='" + pitch + '\'' +
                        ", yaw='" + yaw + '\'' +
                        ", focalLength='" + focalLength + '\'' +
                        ", waitingTime='" + waitingTime + '\'' +
                        '}';
            }
        }
        public static class VoiceBean{
            private String flag;
            private String word;
            private String volume;
            private String speed;

            public String getFlag() {
                return flag;
            }

            public void setFlag(String flag) {
                this.flag = flag;
            }

            public String getWord() {
                return word;
            }

            public void setWord(String word) {
                this.word = word;
            }

            public String getVolume() {
                return volume;
            }

            public void setVolume(String volume) {
                this.volume = volume;
            }

            public String getSpeed() {
                return speed;
            }

            public void setSpeed(String speed) {
                this.speed = speed;
            }

            public String getTone() {
                return tone;
            }

            public void setTone(String tone) {
                this.tone = tone;
            }

            public String getModel() {
                return model;
            }

            public void setModel(String model) {
                this.model = model;
            }

            private String tone;
            private String model;
        }
    }


//    /**
//     * speed : 5
//     * altitude : 120
//     * flightPathMode :
//     * wayPoints : [{"speed":"5","altitude":"120","latitude":"31.32584","turnMode":"-1","longitude":"120.547518","flightPathMode":"","headingMode":""}]
//     * headingMode : 0
//     * finishedAction : 1
//     * action : [{"trigger":{"triggerType":"1","reachPointParam":{"startIndex":"1","autoTerminateCount":"1"},"associateParam":{"associateActionID":"1","associateType":"1","waitingTime":"1"},"trajectoryParam":{"startIndex":"1","endIndex":"1"},"intervalTriggerParam":{"startIndex":"1","type":"1","interval":"1"}}},{"actuator":{"actuatorType":"1","gimbalActuatorParam":{"operationType":"1","pitch":"1","yaw":"1","time":"1"},"cameraOperationType":{"operationType":"1","time":"1"},"aircraftControlType":{"operationType":"1","startFly":"1","direction":"1","yawAngle":"1"}}}]
//     */
//
//    private String speed;
//    private String altitude;
//    private String flightPathMode;
//    private String headingMode;
//    private String finishedAction;
//    private List<WayPointsBean> wayPoints;
//    private List<ActionBean> action;
//
//    public String getSpeed() {
//        return speed;
//    }
//
//    public void setSpeed(String speed) {
//        this.speed = speed;
//    }
//
//    public String getAltitude() {
//        return altitude;
//    }
//
//    public void setAltitude(String altitude) {
//        this.altitude = altitude;
//    }
//
//    public String getFlightPathMode() {
//        return flightPathMode;
//    }
//
//    public void setFlightPathMode(String flightPathMode) {
//        this.flightPathMode = flightPathMode;
//    }
//
//    public String getHeadingMode() {
//        return headingMode;
//    }
//
//    public void setHeadingMode(String headingMode) {
//        this.headingMode = headingMode;
//    }
//
//    public String getFinishedAction() {
//        return finishedAction;
//    }
//
//    public void setFinishedAction(String finishedAction) {
//        this.finishedAction = finishedAction;
//    }
//
//    public List<WayPointsBean> getWayPoints() {
//        return wayPoints;
//    }
//
//    public void setWayPoints(List<WayPointsBean> wayPoints) {
//        this.wayPoints = wayPoints;
//    }
//
//    public List<ActionBean> getAction() {
//        return action;
//    }
//
//    public void setAction(List<ActionBean> action) {
//        this.action = action;
//    }
//
//    public static class WayPointsBean {
//        /**
//         * speed : 5
//         * altitude : 120
//         * latitude : 31.32584
//         * turnMode : -1
//         * longitude : 120.547518
//         * flightPathMode :
//         * headingMode :
//         */
//
//        private String speed;
//        private String altitude;
//        private String latitude;
//        private String turnMode;
//        private String longitude;
//        private String flightPathMode;
//        private String headingMode;
//
//        public String getSpeed() {
//            return speed;
//        }
//
//        public void setSpeed(String speed) {
//            this.speed = speed;
//        }
//
//        public String getAltitude() {
//            return altitude;
//        }
//
//        public void setAltitude(String altitude) {
//            this.altitude = altitude;
//        }
//
//        public String getLatitude() {
//            return latitude;
//        }
//
//        public void setLatitude(String latitude) {
//            this.latitude = latitude;
//        }
//
//        public String getTurnMode() {
//            return turnMode;
//        }
//
//        public void setTurnMode(String turnMode) {
//            this.turnMode = turnMode;
//        }
//
//        public String getLongitude() {
//            return longitude;
//        }
//
//        public void setLongitude(String longitude) {
//            this.longitude = longitude;
//        }
//
//        public String getFlightPathMode() {
//            return flightPathMode;
//        }
//
//        public void setFlightPathMode(String flightPathMode) {
//            this.flightPathMode = flightPathMode;
//        }
//
//        public String getHeadingMode() {
//            return headingMode;
//        }
//
//        public void setHeadingMode(String headingMode) {
//            this.headingMode = headingMode;
//        }
//    }
//
//    public static class ActionBean {
//        /**
//         * trigger : {"triggerType":"1","reachPointParam":{"startIndex":"1","autoTerminateCount":"1"},"associateParam":{"associateActionID":"1","associateType":"1","waitingTime":"1"},"trajectoryParam":{"startIndex":"1","endIndex":"1"},"intervalTriggerParam":{"startIndex":"1","type":"1","interval":"1"}}
//         * actuator : {"actuatorType":"1","gimbalActuatorParam":{"operationType":"1","pitch":"1","yaw":"1","time":"1"},"cameraOperationType":{"operationType":"1","time":"1"},"aircraftControlType":{"operationType":"1","startFly":"1","direction":"1","yawAngle":"1"}}
//         */
//
//        private TriggerBean trigger;
//        private ActuatorBean actuator;
//
//        public TriggerBean getTrigger() {
//            return trigger;
//        }
//
//        public void setTrigger(TriggerBean trigger) {
//            this.trigger = trigger;
//        }
//
//        public ActuatorBean getActuator() {
//            return actuator;
//        }
//
//        public void setActuator(ActuatorBean actuator) {
//            this.actuator = actuator;
//        }
//
//        public static class TriggerBean {
//            /**
//             * triggerType : 1
//             * reachPointParam : {"startIndex":"1","autoTerminateCount":"1"}
//             * associateParam : {"associateActionID":"1","associateType":"1","waitingTime":"1"}
//             * trajectoryParam : {"startIndex":"1","endIndex":"1"}
//             * intervalTriggerParam : {"startIndex":"1","type":"1","interval":"1"}
//             */
//
//            private String triggerType;
//            private ReachPointParamBean reachPointParam;
//            private AssociateParamBean associateParam;
//            private TrajectoryParamBean trajectoryParam;
//            private IntervalTriggerParamBean intervalTriggerParam;
//
//            public String getTriggerType() {
//                return triggerType;
//            }
//
//            public void setTriggerType(String triggerType) {
//                this.triggerType = triggerType;
//            }
//
//            public ReachPointParamBean getReachPointParam() {
//                return reachPointParam;
//            }
//
//            public void setReachPointParam(ReachPointParamBean reachPointParam) {
//                this.reachPointParam = reachPointParam;
//            }
//
//            public AssociateParamBean getAssociateParam() {
//                return associateParam;
//            }
//
//            public void setAssociateParam(AssociateParamBean associateParam) {
//                this.associateParam = associateParam;
//            }
//
//            public TrajectoryParamBean getTrajectoryParam() {
//                return trajectoryParam;
//            }
//
//            public void setTrajectoryParam(TrajectoryParamBean trajectoryParam) {
//                this.trajectoryParam = trajectoryParam;
//            }
//
//            public IntervalTriggerParamBean getIntervalTriggerParam() {
//                return intervalTriggerParam;
//            }
//
//            public void setIntervalTriggerParam(IntervalTriggerParamBean intervalTriggerParam) {
//                this.intervalTriggerParam = intervalTriggerParam;
//            }
//
//            public static class ReachPointParamBean {
//                /**
//                 * startIndex : 1
//                 * autoTerminateCount : 1
//                 */
//
//                private String startIndex;
//                private String autoTerminateCount;
//
//                public String getStartIndex() {
//                    return startIndex;
//                }
//
//                public void setStartIndex(String startIndex) {
//                    this.startIndex = startIndex;
//                }
//
//                public String getAutoTerminateCount() {
//                    return autoTerminateCount;
//                }
//
//                public void setAutoTerminateCount(String autoTerminateCount) {
//                    this.autoTerminateCount = autoTerminateCount;
//                }
//            }
//
//            public static class AssociateParamBean {
//                /**
//                 * associateActionID : 1
//                 * associateType : 1
//                 * waitingTime : 1
//                 */
//
//                private String associateActionID;
//                private String associateType;
//                private String waitingTime;
//
//                public String getAssociateActionID() {
//                    return associateActionID;
//                }
//
//                public void setAssociateActionID(String associateActionID) {
//                    this.associateActionID = associateActionID;
//                }
//
//                public String getAssociateType() {
//                    return associateType;
//                }
//
//                public void setAssociateType(String associateType) {
//                    this.associateType = associateType;
//                }
//
//                public String getWaitingTime() {
//                    return waitingTime;
//                }
//
//                public void setWaitingTime(String waitingTime) {
//                    this.waitingTime = waitingTime;
//                }
//            }
//
//            public static class TrajectoryParamBean {
//                /**
//                 * startIndex : 1
//                 * endIndex : 1
//                 */
//
//                private String startIndex;
//                private String endIndex;
//
//                public String getStartIndex() {
//                    return startIndex;
//                }
//
//                public void setStartIndex(String startIndex) {
//                    this.startIndex = startIndex;
//                }
//
//                public String getEndIndex() {
//                    return endIndex;
//                }
//
//                public void setEndIndex(String endIndex) {
//                    this.endIndex = endIndex;
//                }
//            }
//
//            public static class IntervalTriggerParamBean {
//                /**
//                 * startIndex : 1
//                 * type : 1
//                 * interval : 1
//                 */
//
//                private String startIndex;
//                private String type;
//                private String interval;
//
//                public String getStartIndex() {
//                    return startIndex;
//                }
//
//                public void setStartIndex(String startIndex) {
//                    this.startIndex = startIndex;
//                }
//
//                public String getType() {
//                    return type;
//                }
//
//                public void setType(String type) {
//                    this.type = type;
//                }
//
//                public String getInterval() {
//                    return interval;
//                }
//
//                public void setInterval(String interval) {
//                    this.interval = interval;
//                }
//            }
//        }
//
//        public static class ActuatorBean {
//            /**
//             * actuatorType : 1
//             * gimbalActuatorParam : {"operationType":"1","pitch":"1","yaw":"1","time":"1"}
//             * cameraOperationType : {"operationType":"1","time":"1"}
//             * aircraftControlType : {"operationType":"1","startFly":"1","direction":"1","yawAngle":"1"}
//             */
//
//            private String actuatorType;
//            private GimbalActuatorParamBean gimbalActuatorParam;
//            private CameraOperationTypeBean cameraOperationType;
//            private AircraftControlTypeBean aircraftControlType;
//
//            public String getActuatorType() {
//                return actuatorType;
//            }
//
//            public void setActuatorType(String actuatorType) {
//                this.actuatorType = actuatorType;
//            }
//
//            public GimbalActuatorParamBean getGimbalActuatorParam() {
//                return gimbalActuatorParam;
//            }
//
//            public void setGimbalActuatorParam(GimbalActuatorParamBean gimbalActuatorParam) {
//                this.gimbalActuatorParam = gimbalActuatorParam;
//            }
//
//            public CameraOperationTypeBean getCameraOperationType() {
//                return cameraOperationType;
//            }
//
//            public void setCameraOperationType(CameraOperationTypeBean cameraOperationType) {
//                this.cameraOperationType = cameraOperationType;
//            }
//
//            public AircraftControlTypeBean getAircraftControlType() {
//                return aircraftControlType;
//            }
//
//            public void setAircraftControlType(AircraftControlTypeBean aircraftControlType) {
//                this.aircraftControlType = aircraftControlType;
//            }
//
//            public static class GimbalActuatorParamBean {
//                /**
//                 * operationType : 1
//                 * pitch : 1
//                 * yaw : 1
//                 * time : 1
//                 */
//
//                private String operationType;
//                private String pitch;
//                private String yaw;
//                private String time;
//
//                public String getOperationType() {
//                    return operationType;
//                }
//
//                public void setOperationType(String operationType) {
//                    this.operationType = operationType;
//                }
//
//                public String getPitch() {
//                    return pitch;
//                }
//
//                public void setPitch(String pitch) {
//                    this.pitch = pitch;
//                }
//
//                public String getYaw() {
//                    return yaw;
//                }
//
//                public void setYaw(String yaw) {
//                    this.yaw = yaw;
//                }
//
//                public String getTime() {
//                    return time;
//                }
//
//                public void setTime(String time) {
//                    this.time = time;
//                }
//            }
//
//            public static class CameraOperationTypeBean {
//                /**
//                 * operationType : 1
//                 * time : 1
//                 */
//
//                private String operationType;
//                private String time;
//
//                public String getOperationType() {
//                    return operationType;
//                }
//
//                public void setOperationType(String operationType) {
//                    this.operationType = operationType;
//                }
//
//                public String getTime() {
//                    return time;
//                }
//
//                public void setTime(String time) {
//                    this.time = time;
//                }
//            }
//
//            public static class AircraftControlTypeBean {
//                /**
//                 * operationType : 1
//                 * startFly : 1
//                 * direction : 1
//                 * yawAngle : 1
//                 */
//
//                private String operationType;
//                private String startFly;
//                private String direction;
//                private String yawAngle;
//
//                public String getOperationType() {
//                    return operationType;
//                }
//
//                public void setOperationType(String operationType) {
//                    this.operationType = operationType;
//                }
//
//                public String getStartFly() {
//                    return startFly;
//                }
//
//                public void setStartFly(String startFly) {
//                    this.startFly = startFly;
//                }
//
//                public String getDirection() {
//                    return direction;
//                }
//
//                public void setDirection(String direction) {
//                    this.direction = direction;
//                }
//
//                public String getYawAngle() {
//                    return yawAngle;
//                }
//
//                public void setYawAngle(String yawAngle) {
//                    this.yawAngle = yawAngle;
//                }
//            }
//        }
//    }


}
