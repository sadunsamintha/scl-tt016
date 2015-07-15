package com.sicpa.standard.sasscl.devices.printer.xcode.mapping;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.devices.printer.xcode.IExCodeBehavior;
import com.sicpa.standard.sasscl.model.ProductionParameters;

public class MappingExtendedCodeBehavior implements IMappingExtendedCodeBehavior {

	private static final Logger logger = LoggerFactory.getLogger(MappingExtendedCodeBehavior.class);

	protected ProductionParameters productionParameters;
	protected IExCodeBehavior defaultBehavior;
	protected final Map<String, IExCodeBehavior> mapping = new HashMap<>();

	@Override
	public IExCodeBehavior get(String printerName) {
		IExCodeBehavior res = mapping.get(printerName);
		if (res == null) {
			res = defaultBehavior;
			logger.info("using default ex code behavior: {} for printer {}", res.getClass().getCanonicalName(),
					printerName);
		} else {
			logger.info("using ex code behavior: {} for printer {}", res.getClass().getCanonicalName(), printerName);
		}
		return res;
	}

	public void setDefaultBehavior(IExCodeBehavior defaultBehavior) {
		this.defaultBehavior = defaultBehavior;
	}

	public void setMapping(Map<String, IExCodeBehavior> mapping) {
		this.mapping.putAll(mapping);
	}

	public void addToMapping(String printerName, IExCodeBehavior behavior) {
		mapping.put(printerName, behavior);
	}

	public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}

}
