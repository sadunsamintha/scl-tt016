package com.sicpa.tt016.remote.impl.sicpadata;

import com.sicpa.standard.sasscl.devices.remote.impl.sicpadata.SicpaDataGeneratorStorage;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;
import com.sicpa.standard.sicpadata.spi.manager.ServiceProviderException;
import com.sicpa.std.common.api.coding.dto.SicpadataGeneratorDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TT016SicpaDataGeneratorStorage extends SicpaDataGeneratorStorage  {

	private static final Logger logger = LoggerFactory.getLogger(TT016SicpaDataGeneratorStorage.class);
	
	protected ProductionParameters productionParameters;
	
	@Override
    public void storePendingSDGen(SicpadataGeneratorDto generator) {
        int year = generator.getYear();
        IEncoder encoder = new TT016SicpaDataGeneratorWrapper(generator, year, subsystemIdProvider.get(), cryptoFieldsConfig);
        encoder.setOnClientDate(new Date());
        storage.saveEncoders(year, encoder);
        
        try {
            fileSequenceStorageProvider.storeSequence(generator.getId(), generator.getLastUsedSeq());
        } catch (ServiceProviderException e) {
            logger.error("", e);
        }
    }

   @Override
   protected List<SicpadataGeneratorDto> convertEncoder(List<IEncoder> encoders) {
       List<SicpadataGeneratorDto> list = new ArrayList<SicpadataGeneratorDto>();
       for (IEncoder encoder : storage.getPendingEncoders()) {
           if (encoder instanceof TT016SicpaDataGeneratorWrapper) {
               list.add(((TT016SicpaDataGeneratorWrapper) encoder).getGenerator());
           }
       }
       return list;
   }
   
   public void setProductionParameters(ProductionParameters productionParameters) {
	   this.productionParameters = productionParameters;
   }
}
