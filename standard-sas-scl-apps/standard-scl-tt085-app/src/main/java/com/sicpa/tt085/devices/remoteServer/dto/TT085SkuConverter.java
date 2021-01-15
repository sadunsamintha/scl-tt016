package com.sicpa.tt085.devices.remoteServer.dto;

import com.sicpa.gssd.tt021_tr_server.common.sca.api.staticdata.sku.dto.TrCountryDto;
import com.sicpa.standard.sasscl.devices.remote.impl.dtoConverter.SkuConverter;
import com.sicpa.standard.sasscl.productionParameterSelection.node.AbstractProductionParametersNode;
import com.sicpa.std.common.api.base.dto.BaseDto;
import com.sicpa.std.common.api.base.dto.ComponentBehaviorDto;
import com.sicpa.std.common.api.sku.dto.MarketTypeDto;
import com.sicpa.std.common.api.sku.dto.PackagingTypeSkuDto;
import com.sicpa.std.common.api.staticdata.codetype.dto.CodeTypeDto;
import com.sicpa.std.common.api.staticdata.sku.dto.SkuProductDto;
import com.sicpa.tt085.model.TT085Country;
import com.sicpa.tt085.productionParameterSelection.node.impl.CountryNode;

public class TT085SkuConverter extends SkuConverter {
	
	@Override
	protected void convert(AbstractProductionParametersNode<?> convertedParentRoot,
			ComponentBehaviorDto<? extends BaseDto<Long>> child) {

		if (child.getNodeValue() instanceof MarketTypeDto) {
			convertMarketTypeDto(child, convertedParentRoot);
			return;
		}
		if (child.getNodeValue() instanceof SkuProductDto) {
			convertSkuProductDto(child, convertedParentRoot);
			return;
		}
		if (child.getNodeValue() instanceof CodeTypeDto) {
			convertCodeTypeDto(child, convertedParentRoot);
			return;
		}
		if (child.getNodeValue() instanceof PackagingTypeSkuDto) {
			convertPackagingTypeSkuDto(child, convertedParentRoot);
			return;
		}
		if (child.getNodeValue() instanceof TrCountryDto) {
			convertCountryDto(child, convertedParentRoot);
			return;
		}
		convertNavigationDto(child, convertedParentRoot);
	}
	
	protected void convertCountryDto(ComponentBehaviorDto<? extends BaseDto<Long>> child,
			final AbstractProductionParametersNode<?> convertedParentRoot) {
		TrCountryDto countryDto = (TrCountryDto) child.getNodeValue();
        String countryDisplayDescription;
        if(countryDto.getIso2CharCountryCode() != null
                && !countryDto.getIso2CharCountryCode().isEmpty()
                && !countryDto.getIso2CharCountryCode().equals("null")) {
            countryDisplayDescription  = TT085CountryMessages.getInstance().isKnownIsoCode(countryDto.getIso2CharCountryCode()) ?
                    TT085CountryMessages.getInstance().getCountryName(countryDto.getIso2CharCountryCode()) : countryDto.getInternalDescription();
        } else {
            countryDisplayDescription = countryDto.getInternalDescription();
        }
		TT085Country country = new TT085Country(countryDto.getId(), countryDto.getIso2CharCountryCode(), countryDisplayDescription);
		CountryNode cn = new CountryNode(country);
		convertedParentRoot.addChildren(cn);
		convertDMSProductionParameter(child, cn);
	}

}
