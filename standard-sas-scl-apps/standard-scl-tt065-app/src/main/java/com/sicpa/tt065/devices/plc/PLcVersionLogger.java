package com.sicpa.tt065.devices.plc;

import com.sicpa.standard.sasscl.devices.plc.IPlcAdaptor;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PLcVersionLogger implements IPlcVersionLogger{

    private PlcProvider plcProvider;

    //private IPlcAdaptor plc;
    private static final Logger logger = LoggerFactory.getLogger(PLcVersionLogger.class);

    public void logPLCVersion() {

        if (plcProvider != null){

            IPlcAdaptor plc = plcProvider.get();

            if (plc != null && plc.isConnected()) {

                logger.info("PLC Version: " + plc.getPlcVersion());
            } else {

                logger.debug("plc is null");
            }
        } else {

            logger.debug("plcProvider is null");
        }

    }

    public void setPlcProvider(PlcProvider plcProvider) {
        this.plcProvider = plcProvider;
    }

}
