package com.sicpa.tt065.remote.simulator;


import java.util.Date;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.devices.remote.RemoteServerException;
import com.sicpa.standard.sasscl.devices.remote.simulator.RemoteServerSimulator;
import com.sicpa.standard.sasscl.devices.remote.simulator.RemoteServerSimulatorModel;
import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;
import com.sicpa.standard.sicpadata.api.business.IBSicpadataGenerator;
import com.sicpa.standard.sicpadata.api.exception.SicpadataException;
import com.sicpa.tt065.redlight.ITT065RemoteServer;
import com.sicpa.tt065.redlight.RedLight;
import com.sicpa.tt065.sicpadata.generator.impl.TT065EncoderNoEncryptionSimulator;
import com.sicpa.tt065.stdCrypto.TT065CryptoEncoderWrapperSimulator;

public class TT065RemoteServerSimulator extends RemoteServerSimulator implements ITT065RemoteServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(TT065RemoteServerSimulator.class);

    public TT065RemoteServerSimulator(RemoteServerSimulatorModel model){
        super(model);
    }

    public TT065RemoteServerSimulator(String configFile) throws RemoteServerException {
        super(configFile);
    }

    @Override
    public IEncoder createOneEncoder(int year, int codeTypeId) throws SicpadataException{
        IEncoder encoder;
        if (simulatorModel.isUseCrypto()) {
            IBSicpadataGenerator bEncoder = this.generateEncoders(currentEncoderIndex, codeTypeId);
            int id = new Random().nextInt(Integer.MAX_VALUE);
            encoder = new TT065CryptoEncoderWrapperSimulator(id, id, bEncoder, year, getSubsystemID(),
                    simulatorModel.getNumberOfCodesByEncoder(), cryptoFieldsConfig, codeTypeId);
        } else {
            encoder = new TT065EncoderNoEncryptionSimulator(0, new Random().nextInt(Integer.MAX_VALUE), 0,
                    simulatorModel.getNumberOfCodesByEncoder(), year, getSubsystemID(), codeTypeId);
        }
        encoder.setOnClientDate(new Date());
        return encoder;
    }


    @Override
    public void sendRedLightInfoToGlobalMonitoringTool(RedLight redLight) {
        LOGGER.info("Info sent to Global Monitoring Tool: {}", RedLight.convertToReadableMessage(redLight));
    }
}
