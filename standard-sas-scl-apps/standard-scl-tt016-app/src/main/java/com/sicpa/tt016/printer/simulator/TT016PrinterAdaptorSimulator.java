package com.sicpa.tt016.printer.simulator;

import com.sicpa.standard.printer.controller.IPrinterController;
import com.sicpa.standard.sasscl.devices.printer.simulator.PrinterAdaptorSimulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.sicpa.tt016.remote.impl.sicpadata.TT016SicpaDataGeneratorWrapper.BLOCK_SEPARATOR;


public class TT016PrinterAdaptorSimulator extends PrinterAdaptorSimulator {
    private static final Logger logger = LoggerFactory.getLogger(TT016PrinterAdaptorSimulator.class);


    public TT016PrinterAdaptorSimulator() {
        super();
    }

    public TT016PrinterAdaptorSimulator(IPrinterController controller) {
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
