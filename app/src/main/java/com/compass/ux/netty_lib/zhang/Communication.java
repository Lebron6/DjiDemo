package com.compass.ux.netty_lib.zhang;

import java.io.Serializable;
import java.util.Map;

/**
 * @author zhangchuang
 * @Description
 * @date 2020/6/15 12:08 下午
 **/
public class Communication implements Serializable {

  /**
   * 请求标识
   */
  private String requestId;

  /**
   * 设备编号
   */
  private String equipmentId;

  /**
   * 方法名
   */
  private String method;

  /**
   * 请求参数
   */
  private Map<String, String> para;

  /**
   * 返回值
   */
  private String result;

  /**
   * 请求时间
   */
  private String requestTime;

  /**
   * 响应时间
   */
  private String responseTime;


  /**
   * 返回状态
   */
  private int code;

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }
  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  public String getEquipmentId() {
    return equipmentId;
  }

  public void setEquipmentId(String equipmentId) {
    this.equipmentId = equipmentId;
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public Map<String, String> getPara() {
    return para;
  }

  public void setPara(Map<String, String> para) {
    this.para = para;
  }

  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  public String getRequestTime() {
    return requestTime;
  }

  public void setRequestTime(String requestTime) {
    this.requestTime = requestTime;
  }

  public String getResponseTime() {
    return responseTime;
  }

  public void setResponseTime(String responseTime) {
    this.responseTime = responseTime;
  }

  public Communication(String requestId, String apronId, String method, Map<String, String> para, String result, String requestTime, String responseTime) {
    this.requestId = requestId;
    this.equipmentId = equipmentId;
    this.method = method;
    this.para = para;
    this.result = result;
    this.requestTime = requestTime;
    this.responseTime = responseTime;
  }

  public Communication(){}



  @Override
  public String toString() {
    return "Communication{" +
            "requestId='" + requestId + '\'' +
            ", equipmentId='" + equipmentId + '\'' +
            ", method='" + method + '\'' +
            ", para=" + para +
            ", result='" + result + '\'' +
            ", requestTime='" + requestTime + '\'' +
            ", responseTime='" + responseTime + '\'' +
            '}';
  }
}
