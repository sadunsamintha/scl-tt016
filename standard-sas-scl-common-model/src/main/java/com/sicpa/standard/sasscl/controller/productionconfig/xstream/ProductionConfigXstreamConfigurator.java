package com.sicpa.standard.sasscl.controller.productionconfig.xstream;

import com.sicpa.standard.client.common.xstream.IXStreamConfigurator;
import com.sicpa.standard.sasscl.controller.productionconfig.ProductionConfig;
import com.sicpa.standard.sasscl.controller.productionconfig.config.CameraConfig;
import com.sicpa.standard.sasscl.controller.productionconfig.config.PrinterConfig;
import com.thoughtworks.xstream.XStream;

public class ProductionConfigXstreamConfigurator implements IXStreamConfigurator {

	@Override
	public void configure(XStream x) {
		x.alias("ProductionConfig", ProductionConfig.class);
		x.alias("PrinterConfig", PrinterConfig.class);
		x.alias("CameraConfig", CameraConfig.class);
		x.aliasField("PlcConfig", ProductionConfig.class, "plcConfig");
		x.aliasField("BisConfig", ProductionConfig.class, "bisConfig");
        x.aliasField("BrsConfig", ProductionConfig.class, "brsConfig");
        x.useAttributeFor(ProductionConfig.class, "authenticatorMode");
		x.useAttributeFor(ProductionConfig.class, "activationBehavior");

		x.addImplicitCollection(ProductionConfig.class, "cameraConfigs", CameraConfig.class);
		x.addImplicitCollection(ProductionConfig.class, "printerConfigs", PrinterConfig.class);

		x.registerConverter(new CameraConfigConverter());
		x.registerConverter(new PrinterConfigConverter());
		x.registerConverter(new PlcConfigConverter());
		x.registerConverter(new BisConfigConverter());
		x.registerConverter(new BrsConfigConverter());

	}
}
