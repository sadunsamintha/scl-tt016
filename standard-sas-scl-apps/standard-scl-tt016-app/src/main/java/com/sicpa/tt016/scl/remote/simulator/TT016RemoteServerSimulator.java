package com.sicpa.tt016.scl.remote.simulator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.devices.remote.RemoteServerException;
import com.sicpa.standard.sasscl.devices.remote.simulator.RemoteServerSimulator;
import com.sicpa.standard.sasscl.devices.remote.simulator.RemoteServerSimulatorModel;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.monitoring.MonitoringService;
import com.sicpa.standard.sasscl.monitoring.system.SystemEventType;
import com.sicpa.standard.sasscl.monitoring.system.event.BasicSystemEvent;
import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;
import com.sicpa.standard.sicpadata.api.business.IBSicpadataGenerator;
import com.sicpa.standard.sicpadata.api.exception.SicpadataException;
import com.sicpa.standard.sicpadata.api.exception.UnknownModeException;
import com.sicpa.standard.sicpadata.api.exception.UnknownSystemTypeException;
import com.sicpa.standard.sicpadata.api.exception.UnknownVersionException;
import com.sicpa.standard.sicpadata.spi.password.NoSuchPasswordException;
import com.sicpa.tt016.common.dto.SkuGrossNetProductCounterDTO;
import com.sicpa.tt016.devices.remote.ITT016RemoteServer;
import com.sicpa.tt016.master.scl.exceptions.InternalException;
import com.sicpa.tt016.remote.stdCrypto.TT016CryptoEncoderWrapperSimulator;
import com.sicpa.tt016.remote.stdCrypto.TT016EncoderNoEncryptionSimulator;

public class TT016RemoteServerSimulator extends RemoteServerSimulator implements ITT016RemoteServer {

	private final static Logger logger = LoggerFactory.getLogger(TT016RemoteServerSimulator.class);
	private boolean refeedAvailable;
	
	public static final String FOLDER_SKU_GROSS_NET = "sku_gross_net";
	
	public TT016RemoteServerSimulator(RemoteServerSimulatorModel model) {
        super(model);
    }

    public TT016RemoteServerSimulator(String configFile) throws RemoteServerException {
        super(configFile);
    }
    
    @Override
    public void sendSkuGrossNetProductCounter(final SkuGrossNetProductCounterDTO[] skuGrossNetProductArray) throws InternalException {
      MonitoringService.addSystemEvent(new BasicSystemEvent(SystemEventType.LAST_SENT_TO_REMOTE_SERVER, skuGrossNetProductArray.length + ""));

      File dir = new File(getRemoteServerSimulatorOutputFolder() + File.separator + FOLDER_SKU_GROSS_NET);
      dir.mkdirs();

      SimpleDateFormat timeStampFormatFileName = new SimpleDateFormat("yyyy-MM-dd--HH-mm-ss-SSS");
      SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
      File f = new File(getRemoteServerSimulatorOutputFolder() + File.separator
          + FOLDER_SKU_GROSS_NET + File.separator + timeStampFormatFileName.format(new Date()));

      Map<Integer, Integer> countBySku = new HashMap<>();

      String skuGrossNetOut = "";
      for (SkuGrossNetProductCounterDTO s : skuGrossNetProductArray) {
    	  skuGrossNetOut += s.getSubsystemId();
    	  skuGrossNetOut += " " + timeStampFormat.format(s.getMeasurementDateTime());
    	  skuGrossNetOut += " " + s.getMode();
    	  skuGrossNetOut += " " + s.getCodetypeId();
    	  skuGrossNetOut += " " + s.getSkuId();
    	  skuGrossNetOut += " " + s.getGross();
    	  skuGrossNetOut += " " + s.getNet();
    	  skuGrossNetOut += "\n";
    	  
    	  Integer key = s.getSkuId();
    	  Integer count = countBySku.get(key);
    	  
    	  if (count == null) {
    		  count = 0;
    	  }
    	  count++;
    	  countBySku.put(key, count);
      }
      String output = "";
      for (Entry<Integer, Integer> entry : countBySku.entrySet()) {
        output += entry.getKey() + " count=" + entry.getValue() + "\n";
      }

      output += skuGrossNetOut;

      FileWriter fw = null;
      BufferedWriter bw = null;
      try {
        f.createNewFile();
        fw = new FileWriter(f);
        bw = new BufferedWriter(fw);
        bw.write(output);
      } catch (Exception e) {
        logger.error("", e);
      } finally {
        if (bw != null) {
          try {
            bw.close();
          } catch (IOException e) {
          }
        }
      }
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
      return new TT016CryptoEncoderWrapperSimulator(id, id, bEncoder, year, getSubsystemID(),
          simulatorModel.getNumberOfCodesByEncoder(), cryptoFieldsConfig, codeTypeId);
    }

    private IEncoder createNoEncryptionEncoder(int year, int codeTypeId) {
    	return new TT016EncoderNoEncryptionSimulator(0, new Random().nextInt(Integer.MAX_VALUE), 0, 
    			simulatorModel.getNumberOfCodesByEncoder(), year, getSubsystemID(), codeTypeId);
    }

    public void setRefeedAvailable(boolean refeedAvailable) {
        this.refeedAvailable = refeedAvailable;
    }
    
    public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}
}
