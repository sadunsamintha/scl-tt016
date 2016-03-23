package com.sicpa.tt018.scl.remoteServer.simu;

import com.sicpa.standard.client.common.utils.ConfigUtils;
import com.sicpa.std.common.api.sku.dto.MarketTypeDto;
import com.sicpa.tt018.interfaces.scl.master.dto.SkuProductDTO;
import com.thoughtworks.xstream.XStream;

public class AlbaniaRemoteServerXStream {

	public static void configureXstreamAlbaniaRemoteServerSimuModel() {

		XStream xstreamConfig = ConfigUtils.getXStream();
		xstreamConfig.alias("AlbaniaRemoteServerModelSimulator", AlbaniaRemoteServerModelSimulator.class);
		xstreamConfig.alias("marketTypeDTO", MarketTypeDto.class);
		xstreamConfig.alias("skuDTO", SkuProductDTO.class);

	}
}
