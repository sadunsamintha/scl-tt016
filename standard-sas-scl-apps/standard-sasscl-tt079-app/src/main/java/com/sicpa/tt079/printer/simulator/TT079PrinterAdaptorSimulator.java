package com.sicpa.tt079.printer.simulator;

import com.sicpa.standard.printer.controller.IPrinterController;
import com.sicpa.standard.sasscl.devices.printer.simulator.PrinterAdaptorSimulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.sicpa.tt079.remote.impl.sicpadata.TT079SicpaDataGeneratorWrapper.BLOCK_SEPARATOR;


public class TT079PrinterAdaptorSimulator extends PrinterAdaptorSimulator {
    private static final Logger logger = LoggerFactory.getLogger(TT079PrinterAdaptorSimulator.class);


    public TT079PrinterAdaptorSimulator() {
        super();
    }

    public TT079PrinterAdaptorSimulator(IPrinterController controller) {
        super(controller);
    }

    @Override
    public String requestCode() {
        synchronized (codeBuffer) {
            if (codeBuffer.size() == 0) {
                logger.warn("No codes received");
                return null;
            }

            final String code = codeBuffer.remove(0);
            int i = code.lastIndexOf(BLOCK_SEPARATOR);
            String qcCode = i > 0 ? code.substring(0, i) : code;
            logger.info("Printed Code: {}", code);
            return qcCode;
        }
    }
}
