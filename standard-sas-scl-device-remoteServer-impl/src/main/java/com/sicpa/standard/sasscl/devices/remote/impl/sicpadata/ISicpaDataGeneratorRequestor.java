package com.sicpa.standard.sasscl.devices.remote.impl.sicpadata;

import com.sicpa.std.common.api.coding.business.CodingServiceHandler;
import com.sicpa.std.common.api.coding.dto.SicpadataGeneratorOrderDto;

public interface ISicpaDataGeneratorRequestor {

	void requestSicpadataGenerators(SicpadataGeneratorOrderDto dto,CodingServiceHandler sender);
	
}
