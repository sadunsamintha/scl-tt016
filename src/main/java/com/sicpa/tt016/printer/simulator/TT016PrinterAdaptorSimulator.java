package com.sicpa.tt016.printer.simulator;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.utils.TaskExecutor;
import com.sicpa.standard.gui.utils.Pair;
import com.sicpa.standard.printer.controller.IPrinterController;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.printer.PrinterAdaptorException;
import com.sicpa.standard.sasscl.devices.printer.simulator.PrinterAdaptorSimulator;

public class TT016PrinterAdaptorSimulator extends PrinterAdaptorSimulator {
    private static final Logger logger = LoggerFactory.getLogger(TT016PrinterAdaptorSimulator.class);


    public TT016PrinterAdaptorSimulator() {
        super();
    }

    public TT016PrinterAdaptorSimulator(IPrinterController controller) {
        super(controller);
    }
    
    @Override
	public void doStart() throws PrinterAdaptorException {
		resetCodes();
		if (askCodeDelayMs > 0) {
			scheduledAskTaskFutur = TaskExecutor.scheduleWithFixedDelay(new AskCodesTask(), 1000,
					TimeUnit.MILLISECONDS, "AskCodesTask");
		}
		fireDeviceStatusChanged(DeviceStatus.STARTED);
	}

    @Override
    public String requestCode() {
        synchronized (codePairBuffer) {
            if (codePairBuffer.size() == 0) {
                logger.warn("No codes received");
                return null;
            }

            final Pair<String, String> code = codePairBuffer.remove(0);
            String qcCode = code.getValue1();
            logger.info("Printed Code: {} {}", code.getValue1(), code.getValue2());
            return qcCode;
        }
    }
    
    private class AskCodesTask implements Runnable {
		@Override
		public void run() {
			if (codePairBuffer.size() < 100)
				notifyRequestCodesToPrint(1000L);
		}
	}
}
