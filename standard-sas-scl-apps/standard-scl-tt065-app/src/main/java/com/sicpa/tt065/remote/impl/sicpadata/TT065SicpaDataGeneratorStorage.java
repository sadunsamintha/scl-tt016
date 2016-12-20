package com.sicpa.tt065.remote.impl.sicpadata;

import com.sicpa.standard.sasscl.devices.remote.impl.sicpadata.SicpaDataGeneratorStorage;
import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;
import com.sicpa.standard.sicpadata.spi.manager.ServiceProviderException;
import com.sicpa.std.common.api.coding.dto.SicpadataGeneratorDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class TT065SicpaDataGeneratorStorage extends SicpaDataGeneratorStorage {
    private static final Logger logger = LoggerFactory.getLogger(TT065SicpaDataGeneratorStorage.class);

    @Override
    public void storePendingSDGen(SicpadataGeneratorDto generator) {
        int year = generator.getYear();
        IEncoder encoder = new TT065SicpaDataGeneratorWrapper(generator, year, subsystemIdProvider.get(), cryptoFieldsConfig);
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
           if (encoder instanceof TT065SicpaDataGeneratorWrapper) {
               list.add(((TT065SicpaDataGeneratorWrapper) encoder).getGenerator());
           }
       }
       return list;
   }
}
