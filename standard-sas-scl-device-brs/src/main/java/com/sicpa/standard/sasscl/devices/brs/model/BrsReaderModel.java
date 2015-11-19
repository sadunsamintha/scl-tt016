package com.sicpa.standard.sasscl.devices.brs.model;


public class BrsReaderModel {

    private final String address;

    private final int port;

    private final BrsType type;

    private final int brsLifeCheckInterval;

    private final int brsLifeCheckTimeout;

    private final int brsLifeCheckNumberOfRetries;

    private BrsReaderModel(BrsReaderModelBuilder builder) {
        this.address = builder.address;
        this.port = builder.port;
        this.type = builder.type;
        this.brsLifeCheckInterval = builder.brsLifeCheckInterval;
        this.brsLifeCheckTimeout = builder.brsLifeCheckTimeout;
        this.brsLifeCheckNumberOfRetries = builder.brsLifeCheckNumberOfRetries;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public BrsType getType() {
        return type;
    }

    public int getBrsLifeCheckInterval() {
        return brsLifeCheckInterval;
    }

    public int getBrsLifeCheckTimeout() {
        return brsLifeCheckTimeout;
    }

    public int getBrsLifeCheckNumberOfRetries() {
        return brsLifeCheckNumberOfRetries;
    }

    public static class BrsReaderModelBuilder {

        private final String address;

        private final int port;

        private final BrsType type;

        private int brsLifeCheckInterval = 1000;

        private int brsLifeCheckTimeout = 2000;

        private int brsLifeCheckNumberOfRetries = 3;

        public BrsReaderModelBuilder(String address, int port, BrsType type) {
            this.address = address;
            this.port = port;
            this.type = type;
        }

        public BrsReaderModelBuilder brsLifeCheckInterval(int brsLifeCheckInterval) {
            this.brsLifeCheckInterval = brsLifeCheckInterval;
            return this;
        }

        public BrsReaderModelBuilder brsLifeCheckTimeout(int brsLifeCheckTimeout) {
            this.brsLifeCheckTimeout = brsLifeCheckTimeout;
            return this;
        }

        public BrsReaderModelBuilder brsLifeCheckNumberOfRetries(int brsLifeCheckNumberOfRetries) {
            this.brsLifeCheckNumberOfRetries = brsLifeCheckNumberOfRetries;
            return this;
        }

        public BrsReaderModel build() {
          return new BrsReaderModel(this);
        }



    }


}
