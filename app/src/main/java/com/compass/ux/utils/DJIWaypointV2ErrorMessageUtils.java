package com.compass.ux.utils;

import dji.common.error.DJIWaypointV2Error;

public class DJIWaypointV2ErrorMessageUtils {

    public static final int MISSION_COUNT_OVER_RANGE = -2001;
    public static final int MISSION_COUNT_TOO_FEW = -2002;
    public static final int MISSION_MAX_FLIGHT_SPEED_INVALID = -2004;
    public static final int MISSION_AUTO_FLIGHT_SPEED_INVALID = -2005;
    public static final int MISSION_GOTO_FIRST_WAYPOINT_MODE_INVALID = -2006;
    public static final int MISSION_FINISHED_ACTION_INVALID = -2007;
    public static final int MISSION_EXIT_MISSION_ON_RC_SIGNAL_LOST_INVALID = -2008;
    public static final int MISSION_ALREADY_EXECUTING = -2010;
    public static final int UPLOAD_WAYPOINT_COUNT_OVER_MISSION_WAYPOINT_COUNT = -2012;
    public static final int MISSION_WAYPOINT_DISTANCE_TOO_CLOSE = -2014;
    public static final int MISSION_WAYPOINT_DISTANCE_TO_FAR = -2015;
    public static final int MISSION_WAYPOINT_MAX_FLIGHT_SPEED_OVER_MISSION_MAX_FLIGHT_SPEED = -2016;
    public static final int WAYPOINT_AUTO_FLIGHT_SPEED_OVER_WAYPOINT_MAX_FLIGHT_SPEED = -2017;
    public static final int WAYPOINT_AUTO_FLIGHT_SPEED_OVER_MISSION_MAX_FLGHT_SPEED = -2018;
    public static final int WAYPOINT_HEADING_MODE_INVALID = -2019;
    public static final int WAYPOINT_HEADING_INVALID = -2020;
    public static final int WAYPOINT_TURN_MODE_INVALID = -2021;
    public static final int WAYPOINT_FLIGHT_PATH_MODE_INVALID = -2022;
    public static final int LAST_WAYPOINT_FLIGHT_PATH_MODE_INVALID = -2024;
    public static final int UPLOAD_WAYPOINT_INDEX_NOT_CONTINUE = -2025;
    public static final int FIRST_WAYPOINT_FLIGHT_PATH_MODE_INVALID = -2026;
    public static final int WAYPOINT_DAMPING_DISTANCE_INVALID = -2027;
    public static final int WAYPOINT_COORDINATE_INVALID = -2028;
    public static final int EXCEED_FLYING_RADIUS_LIMIT = -2030;
    public static final int EXCEED_FLYING_HEIGHT_LIMIT = -2031;
    public static final int SDK_VERSION_NOT_MATCHED = -2032;
    public static final int DOWNLOAD_MISSION_RANGE_OVER_STORAGE_COUNT = -3001;
    public static final int NO_MISSION_UPLOADED_IN_AIRCRAFT = -3002;
    public static final int DOWNLOAD_WAYPOINT_NOT_UPLOADED = -3003;
    public static final int AIRCRAFT_CURRENT_POSITION_TO_FIRST_WAYPOINT_TOO_FAR = -4001;
    public static final int UPLOADED_WAYPOINTS_TOO_FEW = -4005;
    public static final int NO_MISSION_EXECUTING = -4006;
    public static final int MISSION_ALREADY_STARTED = -4007;
    public static final int MISSION_ALREADY_STOPPED = -4008;
    public static final int NO_EXECUTING_MISSION_TO_RECOVER = -4011;
    public static final int MISSION_ALREADY_INTERRUPTED = -4012;
    public static final int RECOVER_NO_RECORDED_POINT = -4015;
    public static final int AIRCRAFT_FLYING_STATUS_ERROR = -5001;
    public static final int AIRCRAFT_STATE_HOME_POINT_NOT_RECORD = -5002;
    public static final int AIRCRAFT_RTK_NOT_READY = -5004;
    public static final int AIRCRAFT_GPS_SIGNAL_WEEK  = -5003;
    public static final int MISSION_CROSS_NO_FLY_ZONE = -6001;
    public static final int AIRCRAFT_LOW_BATTERY = -6002;
    public static final int UPLOADED_ACTION_ID_DUPLICATED = -7001;
    public static final int ACTION_STORAGE_NOT_ENOUGH = -7002;
    public static final int DOWNLOAD_ACTIONS_FAILED = -7004;
    public static final int UPLOAD_ACTION_TRIGGER_TYPE_INVALID = -8001;
    public static final int ACTION_ASSOCIATE_TRIGGER_TYPE_INVALID = -8005;
    public static final int ACTION_INTERVAL_TRIGGER_TYPE_INVALID = -8006;
    public static final int ACTION_ACTUATOR_NOT_SUPPORT = -9001;
    public static final int ACTION_ACTUATOR_TYPE_INVALID = -9002;
    public static final int ACTION_ACTUATOR_OPERATION_TYPE_INVALID = -9003;
    public static final int ACTION_ACTUATOR_GIMBAL_PARAM_INVALID = -11001;
    public static final int ACTION_ROTATE_GIMBAL_FAILED = -11004;
    public static final int ACTION_STOP_GIMBAL_UNIFORM_CONTROL_FAILED = -11005;
    public static final int ACTION_FLIGHT_TYPE_NOT_SUPPORT_HOVER = -12007;
    public static final int ACTION_ACTUATOR_NAVIGATION_FAIL_TO_PRECISE_SHOOTPOHTO = -14001;
    public static final int ACTION_ACTUATOR_NAVIGATION_PRECISE_SHOOTPOHTO_TIMEOUT = -14002;
    public static final int ACTION_ROTATE_AIRCRAFT_YAW_PARAM_INVALID = -12001;
    public static final int ACTION_ACTUATOR_NAVIGATION_EXEC_FAILED = -14003;
    public static final int ACTION_ACTUATOR_CAMERA_SEND_SINGLE_SHOT_CMD_TO_CAMERA_FAILED = -15001;
    public static final int ACTION_ACTUATOR_CAMERA_SEND_VIDEO_START_CMD_TO_CAMERA_FAILED = -15002;
    public static final int ACTION_ACTUATOR_CAMERA_SEND_VIDEO_STOP_CMD_TO_CAMERA_FAILED = -15003;
    public static final int ACTION_ACTUATOR_CAMERA_FOCUS_PARAM_XY_INVALID = -15004;
    public static final int ACTION_ACTUATOR_CAMERA_SEND_FOCUS_CMD_TO_CAMERA_FAILED = -15005;
    public static final int ACTION_ACTUATOR_CAMERA_SEND_FOCALIZE_CMD_TO_CAMERA_FAILED = -15006;
    public static final int ACTION_ACTUATOR_CAMERA_FOCAL_DISTANCE_INVALID = -15007;
    public static final int ACTION_ROTATE_AIRCRAFT_YAW_FAILED = -12002;
    public static final int ACTION_AIRCRAFT_START_STOP_FLY_FAILED = -12004;
    public static final int MISSION_WAYPOINT_INTERRUPT_REASON_AVOID = -13;
    public static final int MISSION_WAYPOINT_INTERRUPT_REASON_AVOID_RADIUS_LIMIT = -14;
    public static final int MISSION_WAYPOINT_INTERRUPT_REASON_AVOID_HEIGHT_LIMIT = -15;
    public static final int MISSION_WAYPOINT_INTERRUPT_REASON_AVOID_RTK_UNHEALTHY = -16;
    public static final int MISSION_WAYPOINT_INTERRUPT_REASON_AVOID_USER_REQ_BREAK = -17;
    public static final int MISSION_WAYPOINT_COMMAND_CANNOT_EXECUTE = -1;
    public static final int MISSION_WAYPOINT_COMMAND_EXECUTION_FAILED = -2;
    public static final int MISSION_WAYPOINT_INVALID_PARAMETERS = -3;
    public static final int MISSION_WAYPOINT_COMMON_TIMEOUT = -4;
    public static final int MISSION_WAYPOINT_PRODUCT_CONNECT_FAILED = -5;
    public static final int MISSION_WAYPOINT_SYSTEM_BUSY = -6;
    public static final int MISSION_WAYPOINT_OPERATION_CANCEL_BY_USER = -7;
    public static final int MISSION_WAYPOINT_UPLOAD_MEDIA_FILE_FAILED = -8;
    public static final int MISSION_WAYPOINT_SDR_RESERVE_FAILED = -9;
    public static final int MISSION_WAYPOINT_CHECK_PHOTO_STORAGE_PACK_ERROR = -10;
    public static final int MISSION_WAYPOINT_SDR_LINK_RESERVE_FAILED_CAUSE_MOTOR_ON = -11;
    public static final int MISSION_WAYPOINT_ACTION_TRIGGER_NOT_MATCH_ACTUATOR = -12;
    public static final int MISSION_WAYPOINT_TRAJECTORY_REPLAY_INVALID_LOCATION = -100;
    public static final int MISSION_WAYPOINT_TRAJECTORY_REPLAY_NO_WAYPOINT_TO_REMOVE = -101;
    public static final int MISSION_WAYPOINT_TRAJECTORY_REPLAY_GIMBAL_ATTI_ERROR = -102;
    public static final int MISSION_WAYPOINT_INVALID_INPUT_DATA_FC_LENGTH = -1001;
    public static final int MISSION_WAYPOINT_INVALID_INPUT_DATA_FLOAT_NUMBER = -1002;
    public static final int MISSION_END_INDEX_INVALID = -2003;
    public static final int MISSION_WAYPOINT_INDEX_INVALID = -2011;
    public static final int MISSION_WAYPOINT_START_INDEX_NOT_IN_END_OF_LAST_UPLOAD = -2013;
    public static final int MISSION_CONTROL_START_STOP_INVALID  = -4002;
    public static final int MISSION_CONTROL_PAUSE_RESUME_INVALID = -4003;
    public static final int MISSION_CONTROL_INTERRUPT_RECOVER_INVALID = -4004;
    public static final int MISSION_CONTROL_MISSION_ALREADY_PAUSED = -4009;
    public static final int MISSION_CONTROL_NO_RUNNING_MISSION_FOR_RESUME = -4010;
    public static final int MISSION_CONTROL_NOT_SUPPORT_PAUSE_RESUME = -4013;
    public static final int MISSION_CONTROL_NOT_SUPPORT_INTERRUPT_RESUME = -4014;
    public static final int RECOVER_NO_CURRENT_PROJECTION_POINT = -4016;
    public static final int RECOVER_NO_NEXT_PROJECTION_POINT = -4017;
    public static final int UPLOAD_ACTION_TRIGGER_REACH_END_INDEX_LESS_START_INDEX = -8002;
    public static final int UPLOAD_ACTION_TRIGGER_REACH_INTERVAL_COUNT_INVALID = -8003;
    public static final int UPLOAD_ACTION_TRIGGER_REACH_AUTO_TERMINATE_INVALID = -8004;
    public static final int UPLOAD_ACTION_ACTUATOR_SPRAY_EXTERNAL_SPRAY_MODE_INVALID = -10001;
    public static final int UPLOAD_ACTION_ACTUATOR_SPRAY_FLOW_SPEED_INVALID = -10002;
    public static final int UPLOAD_ACTION_ACTUATOR_SPRAY_FLOW_SPEED_PRE_MU_INVALID = -10003;
    public static final int ACTION_ACTUATOR_PAYLOAD_FAIL_TO_SEND_CMD_TO_PAYLOAD = -13001;


    public static String getDJIWaypointV2ErrorMsg(DJIWaypointV2Error error) {

        if (error.getErrorCode() == MISSION_COUNT_OVER_RANGE) {
            return "任务航点计数大于最大航点计数 65535。";
        } else if (error.getErrorCode() == MISSION_COUNT_TOO_FEW) {
            return "任务航点计数小于最小航点计数 2。";
        } else if (error.getErrorCode() == MISSION_MAX_FLIGHT_SPEED_INVALID) {
            return "任务最大飞行速度大于 15m/s 或小于 2m/s。 ";
        }else if (error.getErrorCode() == MISSION_AUTO_FLIGHT_SPEED_INVALID ) {
            return "任务自动飞行速度大于最大飞行速度。";
        }else if (error.getErrorCode() == MISSION_GOTO_FIRST_WAYPOINT_MODE_INVALID ) {
            return "任务转到第一个航点模式无效。";
        }else if (error.getErrorCode() == MISSION_FINISHED_ACTION_INVALID ) {
            return "任务完成动作无效。";
        }else if (error.getErrorCode() == MISSION_EXIT_MISSION_ON_RC_SIGNAL_LOST_INVALID ) {
            return "RC 信号丢失的任务退出任务无效。";
        }else if (error.getErrorCode() == MISSION_ALREADY_EXECUTING ) {
            return "尝试上传新任务时已经有一个任务正在执行。";
        }else if (error.getErrorCode() == UPLOAD_WAYPOINT_COUNT_OVER_MISSION_WAYPOINT_COUNT ) {
            return "上传的航点数大于任务航点数。";
        }else if (error.getErrorCode() == MISSION_WAYPOINT_DISTANCE_TOO_CLOSE ) {
            return "航点太靠近两个相邻航点。最小距离为 0.5m。";
        }else if (error.getErrorCode() == MISSION_WAYPOINT_DISTANCE_TO_FAR ) {
            return "航点距离两个相邻航点太远，最大距离为 5 公里。";
        }else if (error.getErrorCode() == MISSION_WAYPOINT_MAX_FLIGHT_SPEED_OVER_MISSION_MAX_FLIGHT_SPEED ) {
            return "有一个上传的航点的速度大于任务的最大速度。";
        }else if (error.getErrorCode() == WAYPOINT_AUTO_FLIGHT_SPEED_OVER_WAYPOINT_MAX_FLIGHT_SPEED ) {
            return "上传的航点有一个航点的自动飞行速度大于最大飞行速度。";
        }else if (error.getErrorCode() == WAYPOINT_AUTO_FLIGHT_SPEED_OVER_MISSION_MAX_FLGHT_SPEED ) {
            return "上传的航点有一个航点的自动飞行速度大于任务最大飞行速度。";
        }else if (error.getErrorCode() == WAYPOINT_HEADING_MODE_INVALID ) {
            return "航点航向模式无效。";
        }else if (error.getErrorCode() == WAYPOINT_HEADING_INVALID ) {
            return "航点航向无效。";
        }else if (error.getErrorCode() == WAYPOINT_TURN_MODE_INVALID ) {
            return "航点转弯模式无效。";
        }else if (error.getErrorCode() == WAYPOINT_FLIGHT_PATH_MODE_INVALID ) {
            return "航点飞行路径模式无效。";
        }else if (error.getErrorCode() == WAYPOINT_DAMPING_DISTANCE_INVALID ) {
            return "航点阻尼距离大于等于相邻航点距离。";
        }else if (error.getErrorCode() == LAST_WAYPOINT_FLIGHT_PATH_MODE_INVALID ) {
            return "最后一个航点的航点飞行路径模式无效。";
        }else if (error.getErrorCode() == UPLOAD_WAYPOINT_INDEX_NOT_CONTINUE ) {
            return "上传航点的索引在存储航点后不继续。";
        }else if (error.getErrorCode() == FIRST_WAYPOINT_FLIGHT_PATH_MODE_INVALID ) {
            return "第一个航点的航点飞行路径模式无效。";
        }else if (error.getErrorCode() == WAYPOINT_COORDINATE_INVALID ) {
            return "航点位置坐标超出合理范围。";
        }else if (error.getErrorCode() == EXCEED_FLYING_RADIUS_LIMIT ) {
            return "航点位置超出半径限制。 ";
        }else if (error.getErrorCode() == EXCEED_FLYING_HEIGHT_LIMIT ) {
            return "航点位置超出高度限制。";
        }else if (error.getErrorCode() == SDK_VERSION_NOT_MATCHED ) {
            return "SDK 版本与固件版本不匹配。";
        }else if (error.getErrorCode() == DOWNLOAD_MISSION_RANGE_OVER_STORAGE_COUNT ) {
            return "预计下载的第一个和最后一个航路点的索引不在飞机存储的航路点范围内。";
        }else if (error.getErrorCode() == NO_MISSION_UPLOADED_IN_AIRCRAFT ) {
            return "飞机上没有上传任务。";
        }else if (error.getErrorCode() == DOWNLOAD_WAYPOINT_NOT_UPLOADED ) {
            return "任务信息尚未上传。";
        }else if (error.getErrorCode() == AIRCRAFT_CURRENT_POSITION_TO_FIRST_WAYPOINT_TOO_FAR ) {
            return "飞机当前位置距离第一个航路点太远。";
        }else if (error.getErrorCode() == UPLOADED_WAYPOINTS_TOO_FEW ) {
            return "上传的航点太少。";
        }else if (error.getErrorCode() == NO_MISSION_EXECUTING ) {
            return "没有任务正在执行。";
        }else if (error.getErrorCode() == MISSION_ALREADY_STARTED ) {
            return "任务已经开始。";
        }else if (error.getErrorCode() == MISSION_ALREADY_STOPPED ) {
            return "任务已经停止。";
        }else if (error.getErrorCode() == NO_EXECUTING_MISSION_TO_RECOVER ) {
            return "没有执行任务来恢复任务。";
        }else if (error.getErrorCode() == MISSION_ALREADY_INTERRUPTED ) {
            return "任务已经中断。";
        }else if (error.getErrorCode() == RECOVER_NO_RECORDED_POINT ) {
            return "没有记录的点可以恢复。";
        }else if (error.getErrorCode() == AIRCRAFT_FLYING_STATUS_ERROR ) {
            return "飞机飞行状态不好。";
        }else if (error.getErrorCode() == AIRCRAFT_STATE_HOME_POINT_NOT_RECORD ) {
            return "飞行器返航点尚未记录。";
        }else if (error.getErrorCode() == AIRCRAFT_GPS_SIGNAL_WEEK ) {
            return "飞机 GPS 信号弱。";
        }else if (error.getErrorCode() == AIRCRAFT_RTK_NOT_READY ) {
            return "飞行器 RTK 未准备好。";
        }else if (error.getErrorCode() == MISSION_CROSS_NO_FLY_ZONE ) {
            return "任务跨越禁飞区。";
        }else if (error.getErrorCode() == AIRCRAFT_LOW_BATTERY ) {
            return "飞机电池电量低。";
        }else if (error.getErrorCode() == UPLOADED_ACTION_ID_DUPLICATED ) {
            return "上传的动作 ID 重复。";
        }else if (error.getErrorCode() == ACTION_STORAGE_NOT_ENOUGH ) {
            return "没有足够的内存空间来存储飞机中的动作。";
        }else if (error.getErrorCode() == DOWNLOAD_ACTIONS_FAILED ) {
            return "下载操作失败。";
        }else if (error.getErrorCode() == UPLOAD_ACTION_TRIGGER_TYPE_INVALID ) {
            return "上传动作触发类型无效。";
        }else if (error.getErrorCode() == ACTION_ASSOCIATE_TRIGGER_TYPE_INVALID ) {
            return "动作关联触发类型无效。";
        }else if (error.getErrorCode() == ACTION_INTERVAL_TRIGGER_TYPE_INVALID ) {
            return "动作间隔触发类型无效。";
        }else if (error.getErrorCode() == ACTION_ACTUATOR_NOT_SUPPORT ) {
            return "不支持动作执行器。";
        }else if (error.getErrorCode() == ACTION_ACTUATOR_TYPE_INVALID ) {
            return "动作执行器类型无效。";
        }else if (error.getErrorCode() == ACTION_ACTUATOR_OPERATION_TYPE_INVALID ) {
            return "动作执行器操作类型无效。";
        }else if (error.getErrorCode() == ACTION_ACTUATOR_GIMBAL_PARAM_INVALID ) {
            return "动作云台执行器参数无效。";
        }else if (error.getErrorCode() == ACTION_ROTATE_GIMBAL_FAILED ) {
            return "动作执行器旋转云台失败。";
        }else if (error.getErrorCode() == ACTION_STOP_GIMBAL_UNIFORM_CONTROL_FAILED ) {
            return "云台未挂载或无法执行停止旋转命令。";
        }else if (error.getErrorCode() == ACTION_FLIGHT_TYPE_NOT_SUPPORT_HOVER ) {
            return "当前航点类型（协调转弯或弯道飞机继续）不支持设置到达航点时的动作。";
        }else if (error.getErrorCode() == ACTION_ACTUATOR_NAVIGATION_FAIL_TO_PRECISE_SHOOTPOHTO ) {
            return "拍摄精确照片失败。";
        }else if (error.getErrorCode() == ACTION_ACTUATOR_NAVIGATION_PRECISE_SHOOTPOHTO_TIMEOUT ) {
            return "拍摄精确照片超时。";
        }else if (error.getErrorCode() == ACTION_ROTATE_AIRCRAFT_YAW_PARAM_INVALID ) {
            return "旋转飞行器偏航参数无效。";
        }else if (error.getErrorCode() == ACTION_ACTUATOR_NAVIGATION_EXEC_FAILED ) {
            return "导航命令执行失败";
        }else if (error.getErrorCode() == ACTION_ACTUATOR_CAMERA_SEND_SINGLE_SHOT_CMD_TO_CAMERA_FAILED ) {
            return "由于没有相机或相机忙，无法向相机发送拍摄照片命令";
        }else if (error.getErrorCode() == ACTION_ACTUATOR_CAMERA_SEND_VIDEO_START_CMD_TO_CAMERA_FAILED ) {
            return "由于没有摄像头或摄像头忙，无法向摄像头发送开始录制命令。";
        }else if (error.getErrorCode() == ACTION_ACTUATOR_CAMERA_SEND_VIDEO_STOP_CMD_TO_CAMERA_FAILED ) {
            return "由于没有摄像头或摄像头不忙，无法向摄像头发送视频停止命令";
        }else if (error.getErrorCode() == ACTION_ACTUATOR_CAMERA_FOCUS_PARAM_XY_INVALID ) {
            return "相机对焦参数 xy 超出有效范围 [0, 1]。";
        }else if (error.getErrorCode() == ACTION_ACTUATOR_CAMERA_SEND_FOCUS_CMD_TO_CAMERA_FAILED ) {
            return "由于没有相机或相机忙，无法发送相机对焦命令。";
        }else if (error.getErrorCode() == ACTION_ACTUATOR_CAMERA_SEND_FOCALIZE_CMD_TO_CAMERA_FAILED ) {
            return "由于没有相机或相机忙，无法发送相机变焦命令。";
        }else if (error.getErrorCode() == ACTION_ACTUATOR_CAMERA_FOCAL_DISTANCE_INVALID ) {
            return "非法变焦焦距。";
        }else if (error.getErrorCode() == ACTION_ROTATE_AIRCRAFT_YAW_FAILED ) {
            return "动作执行器旋转飞行器偏航执行失败。";
        }else if (error.getErrorCode() == ACTION_AIRCRAFT_START_STOP_FLY_FAILED ) {
            return "动作执行器飞机开始停止飞行执行失败。";
        }else if (error.getErrorCode() == MISSION_WAYPOINT_INTERRUPT_REASON_AVOID ) {
            return "航点任务因避障而中断。";
        }else if (error.getErrorCode() == MISSION_WAYPOINT_INTERRUPT_REASON_AVOID_RADIUS_LIMIT ) {
            return "航点任务因达到半径限制而中断。";
        }else if (error.getErrorCode() == MISSION_WAYPOINT_INTERRUPT_REASON_AVOID_HEIGHT_LIMIT ) {
            return "航点任务因高度限制而中断。";
        }else if (error.getErrorCode() == MISSION_WAYPOINT_INTERRUPT_REASON_AVOID_RTK_UNHEALTHY) {
            return "Waypoint 任务因 RTK 信号弱而中断。";
        }else if (error.getErrorCode() == MISSION_WAYPOINT_INTERRUPT_REASON_AVOID_USER_REQ_BREAK ) {
            return "Waypoint 任务因用户中断而中断。";
        }else if (error.getErrorCode() == MISSION_WAYPOINT_COMMAND_CANNOT_EXECUTE ) {
            return "命令无法执行。";
        }else if (error.getErrorCode() == MISSION_WAYPOINT_COMMAND_EXECUTION_FAILED ) {
            return "命令执行失败。";
        }else if (error.getErrorCode() == MISSION_WAYPOINT_INVALID_PARAMETERS ) {
            return "无效参数。" ;
        }else if (error.getErrorCode() == MISSION_WAYPOINT_COMMON_TIMEOUT ) {
            return "通用超时。";
        }else if (error.getErrorCode() == MISSION_WAYPOINT_PRODUCT_CONNECT_FAILED ) {
            return "产品连接失败。";
        }else if (error.getErrorCode() == MISSION_WAYPOINT_SYSTEM_BUSY ) {
            return "系统忙。";
        }else if (error.getErrorCode() == MISSION_WAYPOINT_OPERATION_CANCEL_BY_USER ) {
            return "用户取消操作。";
        }else if (error.getErrorCode() == MISSION_WAYPOINT_UPLOAD_MEDIA_FILE_FAILED ) {
            return "上传媒体文件失败。";
        }else if (error.getErrorCode() == MISSION_WAYPOINT_SDR_RESERVE_FAILED ) {
            return "SDR 预留失败。";
        }else if (error.getErrorCode() == MISSION_WAYPOINT_CHECK_PHOTO_STORAGE_PACK_ERROR ) {
            return "检查照片存储包错误。";
        }else if (error.getErrorCode() == MISSION_WAYPOINT_SDR_LINK_RESERVE_FAILED_CAUSE_MOTOR_ON ) {
            return "SDR 链路保留失败，因为电机开启。";
        }else if (error.getErrorCode() == MISSION_WAYPOINT_ACTION_TRIGGER_NOT_MATCH_ACTUATOR) {
            return "动作执行器和触发器类型不匹配。";
        }else if (error.getErrorCode() == MISSION_WAYPOINT_TRAJECTORY_REPLAY_INVALID_LOCATION ) {
            return "轨迹回放的位置无效。";
        }else if (error.getErrorCode() == MISSION_WAYPOINT_TRAJECTORY_REPLAY_NO_WAYPOINT_TO_REMOVE ) {
            return "轨迹回放没有要删除的航点。";
        }else if (error.getErrorCode() == MISSION_WAYPOINT_TRAJECTORY_REPLAY_GIMBAL_ATTI_ERROR ) {
            return "轨迹回放的云台姿态有误。";
        }else if (error.getErrorCode() == MISSION_WAYPOINT_INVALID_INPUT_DATA_FC_LENGTH ) {
            return "数据传输与协议不匹配。";
        }else if (error.getErrorCode() == MISSION_WAYPOINT_INVALID_INPUT_DATA_FLOAT_NUMBER ) {
            return "无效的浮点数。";
        }else if (error.getErrorCode() == MISSION_END_INDEX_INVALID ) {
            return "最后一个航点索引无效。";
        }else if (error.getErrorCode() == MISSION_WAYPOINT_INDEX_INVALID ) {
            return "航点索引无效，请检查航点索引和航点计数。";
        }else if (error.getErrorCode() == MISSION_WAYPOINT_START_INDEX_NOT_IN_END_OF_LAST_UPLOAD ) {
            return "上传的航点起始索引不在给定范围内。";
        }else if (error.getErrorCode() == MISSION_CONTROL_START_STOP_INVALID ) {
            return "飞机起始航路点距离停止航路点太远。";
        }else if (error.getErrorCode() == MISSION_CONTROL_PAUSE_RESUME_INVALID ) {
            return "无效的暂停恢复操作。";
        }else if (error.getErrorCode() == MISSION_CONTROL_INTERRUPT_RECOVER_INVALID ) {
            return "无效的中断恢复操作。";
        }else if (error.getErrorCode() == MISSION_CONTROL_MISSION_ALREADY_PAUSED ) {
            return "任务已经暂停。";
        }else if (error.getErrorCode() == MISSION_CONTROL_NO_RUNNING_MISSION_FOR_RESUME ) {
            return "任务无法恢复，因为它没有运行。";
        }else if (error.getErrorCode() == MISSION_CONTROL_NOT_SUPPORT_PAUSE_RESUME ) {
            return "任务现在不能暂停或恢复。";
        }else if (error.getErrorCode() == MISSION_CONTROL_NOT_SUPPORT_INTERRUPT_RESUME ) {
            return "任务现在不能被中断或恢复。";
        }else if (error.getErrorCode() == RECOVER_NO_CURRENT_PROJECTION_POINT ) {
            return "目前没有记录的恢复航点。";
        }else if (error.getErrorCode() == RECOVER_NO_NEXT_PROJECTION_POINT ) {
            return "没有记录下一个航点恢复的航点。";
        }else if (error.getErrorCode() == UPLOAD_ACTION_TRIGGER_REACH_END_INDEX_LESS_START_INDEX ) {
            return "上传的到达触发器无效。";
        }else if (error.getErrorCode() == UPLOAD_ACTION_TRIGGER_REACH_INTERVAL_COUNT_INVALID ) {
            return "上传的到达触发无效。";
        }else if (error.getErrorCode() == UPLOAD_ACTION_TRIGGER_REACH_AUTO_TERMINATE_INVALID ) {
            return "上传的到达触发无效。";
        }else if (error.getErrorCode() == UPLOAD_ACTION_ACTUATOR_SPRAY_EXTERNAL_SPRAY_MODE_INVALID ) {
            return "上传的喷雾执行器无效。";
        }else if (error.getErrorCode() == UPLOAD_ACTION_ACTUATOR_SPRAY_FLOW_SPEED_INVALID ) {
            return "上传的喷雾执行器无效。";
        }else if (error.getErrorCode() == UPLOAD_ACTION_ACTUATOR_SPRAY_FLOW_SPEED_PRE_MU_INVALID  ) {
            return "上传的喷雾执行器无效。";
        }else if (error.getErrorCode() == ACTION_ACTUATOR_PAYLOAD_FAIL_TO_SEND_CMD_TO_PAYLOAD  ) {
            return "上传的有效载荷执行器无效。";
        }else{
            return error.getDescription();
        }
    }
}
