package com.sicpa.standard.sasscl.devices.brs.reader;

import com.sicpa.common.device.reader.CodeReader;
import com.sicpa.common.device.reader.brs.factory.BrsFactory;
import com.sicpa.common.device.reader.factory.ReaderFactory;
import com.sicpa.common.device.reader.sick.factory.SickFactory;
import com.sicpa.standard.sasscl.devices.brs.BrsAdaptor;
import com.sicpa.standard.sasscl.devices.brs.model.BrsModel;
import com.sicpa.standard.sasscl.devices.brs.model.BrsType;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class BrsCodeReaderFactory {


    public static List<CodeReaderAdaptor> createReaders(BrsAdaptor brsAdaptor) throws IOException, URISyntaxException {
        List<CodeReaderAdaptor> codeReaders = new ArrayList<>();
        BrsModel brsModel = brsAdaptor.getBrsModel();
        ReaderFactory factory = createReaderFactory(brsAdaptor);
        for (String address : brsModel.getActiveAddresses()) {
            CodeReader reader = factory.createReader(
                    new URI(String.format("brs:%s:%d", address, brsModel.getPort())),
                    brsAdaptor, brsAdaptor);

            codeReaders.add(createReaderAdaptor(reader, brsModel.getBrsType()));
        }
        return codeReaders;
    }


    private  static ReaderFactory createReaderFactory(BrsAdaptor brsAdaptor) {
        BrsModel brsModel = brsAdaptor.getBrsModel();
        final int brsLifeCheckInterval = brsAdaptor.getBrsLifeCheckInterval();
        final int brsLifeCheckTimeout = brsAdaptor.getBrsLifeCheckTimeout();
        final int brsLifeCheckNumberOfRetries = brsAdaptor.getBrsLifeCheckNumberOfRetries();
        ReaderFactory factory = null;
        if (brsModel.getBrsType().equalsIgnoreCase(BrsType.SICK.name())) {
            factory = new SickFactory(brsLifeCheckInterval, brsLifeCheckTimeout, "brs");
        } else if (brsModel.getBrsType().equalsIgnoreCase(BrsType.DATAMAN.name())) {
            factory = new BrsFactory(brsLifeCheckInterval, brsLifeCheckTimeout, brsAdaptor, brsLifeCheckNumberOfRetries);
        }
        return factory;
    }

    private static CodeReaderAdaptor createReaderAdaptor(CodeReader reader, String brsModelType) {
        CodeReaderAdaptor adaptor = null;
        if (brsModelType.equalsIgnoreCase(BrsType.SICK.name())) {
            adaptor = new SickCodeReaderAdaptor(reader);

        } else if (brsModelType.equalsIgnoreCase(BrsType.DATAMAN.name())) {
            adaptor = new DatamanCodeReaderAdaptor(reader);
        }
        return adaptor;
    }
}
