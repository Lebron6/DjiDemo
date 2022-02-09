package com.compass.ux.bean;

import java.util.List;

public class WayPointsV2Bean {
    //飞行速度
    private String speed;
    private String name;



    //航线结束动作0 NO_ACTION 不会采取进一步行动。可以通过遥控器控制飞行器。
    //1 GO_HOME 任务完成后回家。飞行器距离返航点20米以内直接降落。
    //2 AUTO_LAND 飞行器将自动降落在最后一个航点。
    //3 GO_FIRST_WAYPOINT 飞行器将返回第一个航点并悬停。
    //4 CONTINUE_UNTIL_STOP 当飞机到达最终航路点时，它会悬停而不结束任务。操纵杆仍可用于将飞机沿其先前的航路点拉回。此任务结束的唯一方法是调用 stopMission。
    private String finishedAction;
    private List<WayPointsBean> wayPoints;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
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
         * speed : 5 altitude : 120 latitude : 31.32584 turnMode : -1 longitude : 120.547518
         * flightPathMode : headingMode : hasWaitingTime; hasVoice wayPointAction :
         * [{"actionType":"","yawAngle":"","direction":"","pitch":"","yaw":"","focalLength":"","waitingTime":""}]
         */
        //自动飞行速度(0-15)
        private String speed;
        //航点高度(5-500)
        private String altitude;
        //航点经纬度
        private String latitude;
        private String longitude;
        //转弯模式：0飞机的航向顺时针旋转。1飞机的航向逆时针旋转。
        private String turnMode;
        //飞行路线模式：0飞行器将沿曲线前往航点并飞越航点。1飞行器将沿曲线前往航点并停在航点处。2飞行器将沿直线前往航点并停在航点处。3飞行器会沿着平滑的曲线从上一个航点飞到下一个航点，而不会在这个航点停下来。4在任务中，飞机将沿直线前往第一个航点。这仅对第一个航点有效。5在任务中，飞机将沿直线前往第一个航点。这仅对第一个航点有效。
        private String flightPathMode;
        //航向模式：0飞机的航向将始终与飞行方向一致。1飞行器航向将设置为到达第一个航点时的航向。在到达第一个航点之前，可以通过遥控器控制飞行器的航向。当飞机到达第一个航点时，其航向将被固定。2飞行器在任务中的航向可以通过遥控器进行控制。3在任务中，飞机的航向会动态变化并适应下一个航点设置的航向。4到达第一个航路点后，飞机将始终朝向兴趣点。5跟随云台视角
        private String headingMode;

        private List<WayPointActionBean> wayPointAction;

        @Override
        public String toString() {
            return "WayPointsBean{" + "speed='" + speed + '\'' +
                    ", altitude='" + altitude + '\'' +
                    ", latitude='" + latitude + '\'' +
                    ", turnMode='" + turnMode + '\'' +
                    ", longitude='" + longitude + '\'' +
                    ", flightPathMode='" + flightPathMode + '\'' +
                    ", headingMode='" + headingMode + '\'' +
                    ", wayPointAction=" + wayPointAction +
                    '}';
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

        public static class WayPointActionBean {
            /**
             * actionType : yawAngle : direction : pitch : yaw : focalLength : waitingTime :
             */
            //动作类型 0悬停 1继续飞行 2旋转 3云台 4变焦 5拍照 6开始录像 7结束录像
            private String actionType;
            //飞机旋转角度(0-180)
            private String yawAngle;
            //航向：0飞机的航向顺时针旋转 1飞机的航向逆时针旋转。
            private String direction;
            //云台垂直角度(绝对值-30-90)
            private String pitch;
            //焦距的整数值
            private String focalLength;
            //悬停时间
            private String waitingTime;
            //喊话实体
            private VoiceBean voice;

            public VoiceBean getVoice() {
                return voice;
            }

            public void setVoice(VoiceBean voice) {
                this.voice = voice;
            }

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
                        ", focalLength='" + focalLength + '\'' +
                        ", waitingTime='" + waitingTime + '\'' +
                        ", voice=" + voice +
                        '}';
            }

            public static class VoiceBean {
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

                @Override
                public String toString() {
                    return "VoiceBean{" +
                            "flag='" + flag + '\'' +
                            ", word='" + word + '\'' +
                            ", volume='" + volume + '\'' +
                            ", speed='" + speed + '\'' +
                            ", tone='" + tone + '\'' +
                            ", model='" + model + '\'' +
                            '}';
                }
            }
        }

    }

}
