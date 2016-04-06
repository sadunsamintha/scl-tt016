package com.sicpa.standard.sasscl.devices.remote.impl.dtoConverter;

import java.util.List;

import com.sicpa.standard.sasscl.devices.remote.GlobalMonitoringToolInfo;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.EncoderInfo;
import com.sicpa.standard.sasscl.model.PackagedProducts;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionParameterRootNode;
import com.sicpa.standard.sasscl.sicpadata.reader.IAuthenticator;
import com.sicpa.std.common.api.activation.dto.AuthorizedProductsDto;
import com.sicpa.std.common.api.activation.dto.SicpadataReaderDto;
import com.sicpa.std.common.api.activation.dto.productionData.authenticated.AuthenticatedProductsResultDto;
import com.sicpa.std.common.api.activation.dto.productionData.counted.CountedProductsResultDto;
import com.sicpa.std.common.api.coding.dto.SicpadataGeneratorInfoDto;
import com.sicpa.std.common.api.coding.dto.SicpadataGeneratorOrderDto;
import com.sicpa.std.common.api.monitoring.dto.EventDto;

public interface IDtoConverter {

	ProductionParameterRootNode convert(AuthorizedProductsDto products);

	CountedProductsResultDto convertToCountedDto(PackagedProducts products);

	AuthenticatedProductsResultDto convertToActivationDto(PackagedProducts products);

	EventDto createEventDto(GlobalMonitoringToolInfo info);
	
	List<SicpadataGeneratorInfoDto> createEncoderInfo(List<EncoderInfo> infos);
	
	SicpadataGeneratorOrderDto newEncoderRequestOrder(int quantity, CodeType codeType, int year);
	
	IAuthenticator convert(SicpadataReaderDto readerDto);
		
}
