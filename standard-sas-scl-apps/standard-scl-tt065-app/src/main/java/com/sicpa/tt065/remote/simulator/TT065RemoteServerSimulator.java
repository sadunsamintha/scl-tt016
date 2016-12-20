package com.sicpa.tt065.remote.simulator;


import com.sicpa.standard.sasscl.devices.remote.RemoteServerException;
import com.sicpa.standard.sasscl.devices.remote.simulator.RemoteServerSimulator;
import com.sicpa.standard.sasscl.devices.remote.simulator.RemoteServerSimulatorModel;
import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;
import com.sicpa.standard.sicpadata.api.business.IBSicpadataGenerator;
import com.sicpa.standard.sicpadata.api.exception.SicpadataException;
import com.sicpa.standard.sicpadata.api.exception.UnknownModeException;
import com.sicpa.standard.sicpadata.api.exception.UnknownSystemTypeException;
import com.sicpa.standard.sicpadata.api.exception.UnknownVersionException;
import com.sicpa.standard.sicpadata.spi.password.NoSuchPasswordException;
import com.sicpa.tt065.sicpadata.generator.impl.TT065EncoderNoEncryptionSimulator;
import com.sicpa.tt065.stdCrypto.TT065CryptoEncoderWrapperSimulator;

import java.util.Date;
import java.util.Random;

public class TT065RemoteServerSimulator extends RemoteServerSimulator {
    public TT065RemoteServerSimulator(RemoteServerSimulatorModel model) throws RemoteServerException {
        super(model);
    }

    public TT065RemoteServerSimulator(String configFile) throws RemoteServerException {
        super(configFile);
    }

    @Override
    public IEncoder createOneEncoder(int year, int codeTypeId) throws UnknownModeException, UnknownVersionException,
            UnknownSystemTypeException, SicpadataException, NoSuchPasswordException {
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
}
