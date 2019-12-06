package com.sicpa.tt016.remote.impl.sicpadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.devices.remote.impl.sicpadata.SicpaDataGeneratorWrapper;
import com.sicpa.standard.sasscl.devices.remote.stdCrypto.ICryptoFieldsConfig;
import com.sicpa.std.common.api.coding.dto.SicpadataGeneratorDto;

@SuppressWarnings("serial")
public class TT016SicpaDataGeneratorWrapper extends SicpaDataGeneratorWrapper {

	public final static String BLOCK_SEPARATOR = ">-<" ;
	public final static String EXPORT_PREFIX = "EX" ;

    private static final Logger logger = LoggerFactory.getLogger(TT016SicpaDataGeneratorWrapper.class);

    public TT016SicpaDataGeneratorWrapper(SicpadataGeneratorDto sdgen, int year, long subsystemId, ICryptoFieldsConfig cryptoFieldsConfig) {
        super(sdgen, year, subsystemId, cryptoFieldsConfig);
    }
}
