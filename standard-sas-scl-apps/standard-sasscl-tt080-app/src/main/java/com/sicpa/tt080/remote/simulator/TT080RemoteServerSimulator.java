package com.sicpa.tt080.remote.simulator;

import java.util.Date;
import java.util.Random;

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
import com.sicpa.tt080.remote.stdCrypto.TT080EncoderNoEncryptionSimulator;
import com.sicpa.tt080.remote.stdCrypto.TT080CryptoEncoderWrapperSimulator;

public class TT080RemoteServerSimulator extends RemoteServerSimulator {
  public TT080RemoteServerSimulator(RemoteServerSimulatorModel model) throws RemoteServerException {
    super(model);
  }

  public TT080RemoteServerSimulator(String configFile) throws RemoteServerException {
    super(configFile);
  }

  @Override
  public IEncoder createOneEncoder(int year, int codeTypeId) throws UnknownModeException, UnknownVersionException,
      UnknownSystemTypeException, SicpadataException, NoSuchPasswordException {
    IEncoder encoder;
    if (simulatorModel.isUseCrypto()) {
      encoder = createCryptoEncoder(year, codeTypeId);
    } else {
      encoder = createNoEncryptionEncoder(year, codeTypeId);
    }
    encoder.setOnClientDate(new Date());
    return encoder;
  }

  private IEncoder createCryptoEncoder(int year, int codeTypeId) throws UnknownModeException,
      UnknownVersionException, UnknownSystemTypeException, SicpadataException {
    IBSicpadataGenerator bEncoder = generateEncoders(currentEncoderIndex, codeTypeId);
    int id = new Random().nextInt(Integer.MAX_VALUE);
    return new TT080CryptoEncoderWrapperSimulator(id, id, bEncoder, year, getSubsystemID(),
        simulatorModel.getNumberOfCodesByEncoder(), cryptoFieldsConfig, codeTypeId);
  }

  private IEncoder createNoEncryptionEncoder(int year, int codeTypeId) {
    return new TT080EncoderNoEncryptionSimulator(0, new Random().nextInt(Integer.MAX_VALUE), 0,
        simulatorModel.getNumberOfCodesByEncoder(), year, getSubsystemID(), codeTypeId);
  }


}
