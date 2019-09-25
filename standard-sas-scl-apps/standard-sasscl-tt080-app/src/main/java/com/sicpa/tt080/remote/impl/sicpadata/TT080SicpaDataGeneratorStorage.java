package com.sicpa.tt080.remote.impl.sicpadata;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

import com.sicpa.standard.sasscl.devices.remote.impl.sicpadata.SicpaDataGeneratorStorage;
import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;
import com.sicpa.standard.sicpadata.spi.manager.ServiceProviderException;
import com.sicpa.std.common.api.coding.dto.SicpadataGeneratorDto;

@Slf4j
public class TT080SicpaDataGeneratorStorage extends SicpaDataGeneratorStorage  {

	@Override
    public void storePendingSDGen(final SicpadataGeneratorDto generator) {
        final int year = generator.getYear();
        final IEncoder encoder = new TT080SicpaDataGeneratorWrapper(generator, year, subsystemIdProvider.get(), cryptoFieldsConfig);
        encoder.setOnClientDate(new Date());
        storage.saveEncoders(year, encoder);

        try {
            fileSequenceStorageProvider.storeSequence(generator.getId(), generator.getLastUsedSeq());
        } catch (ServiceProviderException e) {
            log.error("Unable to Store Sequence", e);
        }
    }

   @Override
   protected List<SicpadataGeneratorDto> convertEncoder(List<IEncoder> encoders) {
       final List<SicpadataGeneratorDto> list = new ArrayList<>();
       for (final IEncoder encoder : storage.getPendingEncoders()) {
           if (encoder instanceof TT080SicpaDataGeneratorWrapper) {
               list.add(((TT080SicpaDataGeneratorWrapper) encoder).getGenerator());
           }
       }
       return list;
   }
   
	
}
