package com.sicpa.standard.sasscl.devices.brs.model;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class BrsModel {

    private int mId;
    private String mIp1;
    private String mIp2;
    private String mIp3;
    private String mIp4;
    private int mPort;
    private String brsType;

    public int getPort() {
        return mPort;
    }

    public void setPort(int port) {
        this.mPort = port;
    }

    public String getIp1() {
        return mIp1;
    }

    public void setIp1(String mIp) {
        this.mIp1 = mIp;
    }

    public String getIp2() {
        return mIp2;
    }

    public void setIp2(String mIp) {
        this.mIp2 = mIp;
    }

    public String getIp3() {
        return mIp3;
    }

    public void setIp3(String mIp3) {
        this.mIp3 = mIp3;
    }

    public String getIp4() {
        return mIp4;
    }

    public void setIp4(String mIp4) {
        this.mIp4 = mIp4;
    }



    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public String getBrsType() {
        return brsType;
    }

    public void setBrsType(String brsType) {
        this.brsType = brsType;
    }

    /**
     * Returns brs ip addresses which are not null or emtpy.
     *
     * @return a list of active brs ip addresses.
     */
    public List<String> getActiveAddresses() {
        List<String> activeAddresses = new ArrayList<>();
        if(StringUtils.isNotBlank(mIp1)) {
            activeAddresses.add(mIp1);
        }
        if(StringUtils.isNotBlank(mIp2)) {
            activeAddresses.add(mIp2);
        }
        if(StringUtils.isNotBlank(mIp3)) {
            activeAddresses.add(mIp3);
        }
        if(StringUtils.isNotBlank(mIp4)) {
            activeAddresses.add(mIp4);
        }

        return activeAddresses;
    }

    @Override
    public String toString() {
        return "BrsModel{" +
                "mIp1='" + mIp1 + '\'' +
                ", mIp2='" + mIp2 + '\'' +
                ", mIp3='" + mIp3 + '\'' +
                ", mIp4='" + mIp4 + '\'' +
                ", mPort=" + mPort +
                ", brsType='" + brsType + '\'' +
                '}';
    }

}
