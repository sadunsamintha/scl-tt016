package com.sicpa.standard.sasscl.devices.brs.reader;

import com.sicpa.common.device.reader.CodeReader;
import com.sicpa.common.device.reader.brs.factory.BrsFactory;
import com.sicpa.common.device.reader.factory.ReaderFactory;
import com.sicpa.common.device.reader.sick.factory.SickFactory;
import com.sicpa.standard.sasscl.devices.brs.BrsAdaptor;
import com.sicpa.standard.sasscl.devices.brs.BrsReaderController;
import com.sicpa.standard.sasscl.devices.brs.BrsReaderDevice;
import com.sicpa.standard.sasscl.devices.brs.BrsReaderListener;
import com.sicpa.standard.sasscl.devices.brs.model.BrsModel;
import com.sicpa.standard.sasscl.devices.brs.model.BrsReaderModel;
import com.sicpa.standard.sasscl.devices.brs.model.BrsType;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class BrsCodeReaderFactory {


    public static BrsReaderDevice createBrsReaderDevice(BrsReaderModel brsReaderModel,  BrsReaderDevice brsReaderDevice, BrsReaderListener brsReaderListener) throws IOException, URISyntaxException {
        ReaderFactory factory = createReaderFactory(brsReaderModel, brsReaderDevice);
        CodeReader reader = factory.createReader(
                new URI(String.format("brs:%s:%d", brsReaderModel.getIp(), brsReaderModel.getPort())),
                brsReaderDevice, brsReaderDevice);
        CodeReaderAdaptor codeReaderAdaptor = createReaderAdaptor(reader, brsReaderModel.getType(), brsReaderModel.getIp());

        return new BrsReaderController(codeReaderAdaptor, brsReaderListener);
    }


    private static ReaderFactory createReaderFactory(BrsReaderModel brsReaderModel, BrsReaderDevice brsReaderDevice) {
        ReaderFactory factory = null;
        if (brsReaderModel.getType().equals(BrsType.SICK)) {
            factory = new SickFactory(brsReaderModel.getBrsLifeCheckInterval(), brsReaderModel.getBrsLifeCheckTimeout(), "brs");
        } else if (brsReaderModel.getType().equals(BrsType.DATAMAN)) {
            factory = new BrsFactory(brsReaderModel.getBrsLifeCheckInterval(), brsReaderModel.getBrsLifeCheckTimeout(), brsReaderDevice, brsReaderModel.getBrsLifeCheckNumberOfRetries());
        }
        return factory;
    }

    private static CodeReaderAdaptor createReaderAdaptor(CodeReader reader, BrsType brsType, String address) {
        CodeReaderAdaptor adaptor = null;
        if (brsType.equals(BrsType.SICK)) {
            adaptor = new SickCodeReaderAdaptor(reader, address);

        } else if (brsType.equals(BrsType.DATAMAN)) {
            adaptor = new DatamanCodeReaderAdaptor(reader, address);
        }

        return adaptor;
    }
}
