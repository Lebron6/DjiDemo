package com.compass.ux.netty_lib.constant;


public class UrlConstant {

    /**
     * socket 服务器IP地址
     */

    //测试环境
//    public static final String SOCKET_HOST = "36.154.125.61";

    //正式环境（苏恩）
//    public static final String SOCKET_HOST = "47.102.102.224";
// 正式环境（亭苑）
    public static final String SOCKET_HOST = "124.70.162.197";
//测试环境(亭苑)
//    public static final String SOCKET_HOST = "61.155.157.50";
    /**
     * socket 服务器端口号
     */

    //正式环境（苏恩/亭苑）
    public static final int SOCKET_PORT = 60000;
// 
//    //测试环境(亭苑/车管所)
//    public static final int SOCKET_PORT = 60001;

    /**
     * 上传图片接口
     */

    //测试环境
//    public static final String PHOTO_UPLOAD_URL="http://36.154.125.61:17070";

    //正式环境
    public static final String PHOTO_UPLOAD_URL="http://124.70.162.197:7070";
}
