package com.sicpa.tt016.scl.business.coding.impl;

import java.util.List;

import com.sicpa.standard.sasscl.business.coding.impl.Coding;
import com.sicpa.standard.sasscl.common.storage.IStorage;
import com.sicpa.standard.sasscl.monitoring.MonitoringService;
import com.sicpa.standard.sasscl.monitoring.system.SystemEventType;
import com.sicpa.standard.sasscl.monitoring.system.event.BasicSystemEvent;
import com.sicpa.standard.sasscl.sicpadata.CryptographyException;
import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;

/**
 * <p>
 * Provide codes to the printer when the application is running in SCL mode.
 * </p>
 * <p>
 * It implements the <code>IPrinterListener</code> interface so it can be notified whenever the printer requests for
 * codes.
 * </p>
 *
 */
public class TT016Coding extends Coding {

	public TT016Coding() {
		super();
	}

	public TT016Coding(final IStorage storage) {
		super(storage);
	}
	
	@Override
	protected void generateCodes(IEncoder encoder, final List<String> codes, final long numberCodes)
			throws CryptographyException {
		// Gets the codes from the current encoder and adds them to the
		// container to be send to the printer.
		codes.addAll(encoder.getEncryptedCodes(numberCodes - codes.size(), productionParameters));
		MonitoringService.addSystemEvent(new BasicSystemEvent(SystemEventType.GET_CODE_FROM_ENCODER, encoder.getId() + ""));
	}
}