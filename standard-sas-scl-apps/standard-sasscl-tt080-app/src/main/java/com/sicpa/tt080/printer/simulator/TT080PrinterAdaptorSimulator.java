package com.sicpa.tt080.printer.simulator;

import lombok.extern.slf4j.Slf4j;

import com.sicpa.standard.printer.controller.IPrinterController;
import com.sicpa.standard.sasscl.devices.printer.simulator.PrinterAdaptorSimulator;

import static com.sicpa.standard.sasscl.devices.remote.stdCrypto.StdHrdCryptoEncoderWrapperSimulator.BLOCK_SEPARATOR;


@Slf4j
public class TT080PrinterAdaptorSimulator extends PrinterAdaptorSimulator {

    public TT080PrinterAdaptorSimulator() {
        super();
    }

    public TT080PrinterAdaptorSimulator(final IPrinterController controller) {
        super(controller);
    }

    @Override
    public String requestCode() {
        synchronized (codeBuffer) {
            if (codeBuffer.size() == 0) {
                log.warn("No codes received");
                return null;
            }

            final String code = codeBuffer.remove(0);
            final int i = code.lastIndexOf(BLOCK_SEPARATOR);
            final String qcCode = i > 0 ? code.substring(0, i) : code;

            log.info("Printed Code: {}", code);

            return qcCode;
        }
    }
}
