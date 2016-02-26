package com.sicpa.standard.sasscl.ioc;

import com.sicpa.standard.client.common.ioc.PropertyPlaceholderResources;

public class PropertyPlaceholderResourcesSASSCL extends PropertyPlaceholderResources {

	public static void init() {
		defaultFiles.add("spring/defaultConfig.properties");
		defaultFiles.add("spring/defaultBeans.properties");
		defaultFiles.add("spring/skuCheckBeans.properties");
		defaultFiles.add("spring/visionSystemBeans.properties");
		defaultFiles.add("spring/customBeans.properties");
		defaultFiles.add("spring/brsBeans.properties");
		defaultFiles.add("spring/brs.properties");
		
		defaultFiles.add("config/printer/printer-leibinger.properties");
		defaultFiles.add("config/printer/jobConfig-leibinger.properties");

		defaultFolders.add("config");
	}
}
