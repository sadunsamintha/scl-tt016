package com.sicpa.standard.sasscl.devices.brs.model;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class BrsModel {

    private int id;
    private String ip1;
    private String ip2;
    private String ip3;
    private String ip4;
    private int port;
    private String brsType;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getIp1() {
        return ip1;
    }

    public void setIp1(String mIp) {
        this.ip1 = mIp;
    }

    public String getIp2() {
        return ip2;
    }

    public void setIp2(String mIp) {
        this.ip2 = mIp;
    }

    public String getIp3() {
        return ip3;
    }

    public void setIp3(String mIp3) {
        this.ip3 = mIp3;
    }

    public String getIp4() {
        return ip4;
    }

    public void setIp4(String mIp4) {
        this.ip4 = mIp4;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
        if(StringUtils.isNotBlank(ip1)) {
            activeAddresses.add(ip1);
        }
        if(StringUtils.isNotBlank(ip2)) {
            activeAddresses.add(ip2);
        }
        if(StringUtils.isNotBlank(ip3)) {
            activeAddresses.add(ip3);
        }
        if(StringUtils.isNotBlank(ip4)) {
            activeAddresses.add(ip4);
        }

        return activeAddresses;
    }

    @Override
    public String toString() {
        return "BrsModel{" +
                "ip1='" + ip1 + '\'' +
                ", ip2='" + ip2 + '\'' +
                ", ip3='" + ip3 + '\'' +
                ", ip4='" + ip4 + '\'' +
                ", port=" + port +
                ", brsType='" + brsType + '\'' +
                '}';
    }

}
