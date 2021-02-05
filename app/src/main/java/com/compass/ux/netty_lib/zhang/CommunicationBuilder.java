package com.compass.ux.netty_lib.zhang;

import java.util.Map;

public final class CommunicationBuilder {
    private String requestId;
    private String equipmentId;
    private String method;
    private Map<String, String> para;
    private int code;
    private String result;
    private String requestTime;
    private String responseTime;

    private CommunicationBuilder() {
    }

    public static CommunicationBuilder aCommunication() {
        return new CommunicationBuilder();
    }

    public CommunicationBuilder withRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    public CommunicationBuilder withEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
        return this;
    }

    public CommunicationBuilder withMethod(String method) {
        this.method = method;
        return this;
    }

    public CommunicationBuilder withPara(Map<String, String> para) {
        this.para = para;
        return this;
    }

    public CommunicationBuilder withCode(int code) {
        this.code = code;
        return this;
    }

    public CommunicationBuilder withResult(String result) {
        this.result = result;
        return this;
    }

    public CommunicationBuilder withRequestTime(String requestTime) {
        this.requestTime = requestTime;
        return this;
    }

    public CommunicationBuilder withResponseTime(String responseTime) {
        this.responseTime = responseTime;
        return this;
    }

    public Communication build() {
        Communication communication = new Communication();
        communication.setRequestId(requestId);
        communication.setEquipmentId(equipmentId);
        communication.setMethod(method);
        communication.setPara(para);
        communication.setCode(code);
        communication.setResult(result);
        communication.setRequestTime(requestTime);
        communication.setResponseTime(responseTime);
        return communication;
    }
}
