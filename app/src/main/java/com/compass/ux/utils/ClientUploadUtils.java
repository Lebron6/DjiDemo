package com.compass.ux.utils;

import com.compass.ux.MApplication;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by xhf
 * on 2020-07-28 17:11
 */


public class ClientUploadUtils {

    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("*/*");

    public static ResponseBody upload(String url, File file, String fileName, String type) throws Exception {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)//设置连接超时时间
                .readTimeout(120, TimeUnit.SECONDS)//设置读取超时时间
                .build();


//        addfile.getName();
        //2.创建RequestBody
        RequestBody fileBody = RequestBody.create(MEDIA_TYPE_PNG, file);
////3.构建MultipartBody
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
//                .addFormDataPart("uavName","uav_1")
                .addFormDataPart("type", type)
                .addFormDataPart("uavName", MApplication.EQUIPMENT_ID)
//                .addFormDataPart("file", fileName, RequestBody.create(MediaType.parse("multipart/form-data"), addfile))
                .addFormDataPart("file", fileName, fileBody)
                .build();

        Request request = new Request.Builder()
                .addHeader("Content-Type", "multipart/form-data")
                .url(url)
                .post(requestBody)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        return response.body();
    }


//    static void main(String[] args) throws IOException {
//        try {
//            String fileName = "com.jdsoft.biz.test.zip";
//            String filePath = "D:\\ExtJsTools\\Sencha\\Cmd\\repo\\pkgs\\test.zip";
//            String url = "10.0.1.122/oauth/file/upload";
//            System.out.println(upload(url, filePath, fileName).string());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
