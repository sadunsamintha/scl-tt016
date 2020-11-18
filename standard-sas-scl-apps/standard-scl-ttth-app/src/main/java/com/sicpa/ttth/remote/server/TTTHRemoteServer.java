package com.sicpa.ttth.remote.server;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;

import com.sicpa.gssd.ttth.common.api.activation.dto.ValidCodedProductsDto;
import com.sicpa.standard.sasscl.devices.printer.TTTHDataExCodeUtil;
import com.sicpa.standard.sasscl.devices.remote.impl.RemoteServer;
import com.sicpa.standard.sasscl.devices.remote.impl.remoteservices.IRemoteServices;
import com.sicpa.ttth.scl.remote.remoteservices.TTTHRemoteServices;

public class TTTHRemoteServer extends RemoteServer {

    private static final Logger logger = LoggerFactory.getLogger(TTTHRemoteServer.class);

    private IRemoteServices ttthRemoteServices;

    public void setTtthRemoteServices(IRemoteServices ttthRemoteServices) {
        this.ttthRemoteServices = ttthRemoteServices;
    }

    public final Integer getActualCodedCount(String dailyBatchJobId) {
        if (isConnected()) {
            ValidCodedProductsDto actualCodedProductsDto;
            TTTHRemoteServices remoteServices;
            try {
                remoteServices = (TTTHRemoteServices) ((Advised)ttthRemoteServices).getTargetSource().getTarget();
                actualCodedProductsDto = remoteServices
                    .getActualCodedQuantity(dailyBatchJobId);
                return Math.toIntExact(actualCodedProductsDto.getValidCodedQty());
            } catch (Exception e) {
                logger.error("Failed to get coded count from service", e);
                return null;
            }
        } else {
            logger.error("Unable to retrieve coded count, Not connected.");
            return null;
        }
    }

}
