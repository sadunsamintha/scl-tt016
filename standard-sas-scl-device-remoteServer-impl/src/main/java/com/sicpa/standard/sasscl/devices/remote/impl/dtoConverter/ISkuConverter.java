package com.sicpa.standard.sasscl.devices.remote.impl.dtoConverter;

import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionParameterRootNode;
import com.sicpa.std.common.api.activation.dto.AuthorizedProductsDto;

public interface ISkuConverter {
	ProductionParameterRootNode convert(AuthorizedProductsDto products);
}
