package com.sicpa.standard.sasscl.devices.brs.model;


public class BrsReaderModel {

    private String ip;

    private String port;

    private BrsType type;

    private int brsLifeCheckInterval;

    private int brsLifeCheckTimeout;

    private int brsLifeCheckNumberOfRetries;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public BrsType getType() {
        return type;
    }

    public void setType(BrsType type) {
        this.type = type;
    }

    public int getBrsLifeCheckInterval() {
        return brsLifeCheckInterval;
    }

    public void setBrsLifeCheckInterval(int brsLifeCheckInterval) {
        this.brsLifeCheckInterval = brsLifeCheckInterval;
    }

    public int getBrsLifeCheckTimeout() {
        return brsLifeCheckTimeout;
    }

    public void setBrsLifeCheckTimeout(int brsLifeCheckTimeout) {
        this.brsLifeCheckTimeout = brsLifeCheckTimeout;
    }

    public int getBrsLifeCheckNumberOfRetries() {
        return brsLifeCheckNumberOfRetries;
    }

    public void setBrsLifeCheckNumberOfRetries(int brsLifeCheckNumberOfRetries) {
        this.brsLifeCheckNumberOfRetries = brsLifeCheckNumberOfRetries;
    }
}
