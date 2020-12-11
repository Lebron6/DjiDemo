package com.compass.ux.bean;

/**
 * Created by xhf
 * on 2020-12-10 14:23
 */
public class TransmissionSetBean {
    //图传码率
    private String transcodingDataRate;
    //带宽
    private String channelBandwidth;
    //工作频段
    private String frequencyBand;
    //干扰
    private String interferencePower;


    public String getTranscodingDataRate() {
        return transcodingDataRate;
    }

    public void setTranscodingDataRate(String transcodingDataRate) {
        this.transcodingDataRate = transcodingDataRate;
    }

    public String getChannelBandwidth() {
        return channelBandwidth;
    }

    public void setChannelBandwidth(String channelBandwidth) {
        this.channelBandwidth = channelBandwidth;
    }

    public String getFrequencyBand() {
        return frequencyBand;
    }

    public void setFrequencyBand(String frequencyBand) {
        this.frequencyBand = frequencyBand;
    }

    public String getInterferencePower() {
        return interferencePower;
    }

    public void setInterferencePower(String interferencePower) {
        this.interferencePower = interferencePower;
    }
}
