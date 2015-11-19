package com.sicpa.standard.sasscl.devices.brs.reader;

import com.sicpa.common.device.reader.CodeReader;
import com.sicpa.common.device.reader.brs.factory.BrsFactory;
import com.sicpa.common.device.reader.factory.ReaderFactory;
import com.sicpa.common.device.reader.sick.factory.SickFactory;
import com.sicpa.standard.sasscl.devices.brs.model.BrsReaderModel;
import com.sicpa.standard.sasscl.devices.brs.model.BrsType;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class CodeReaderFactory {


    public static CodeReaderAdaptor createCodeReaderAdaptor(BrsReaderModel brsReaderModel, CodeReaderController codeReaderController) throws URISyntaxException, IOException {
        ReaderFactory factory = createReaderFactory(brsReaderModel, codeReaderController);

        CodeReader reader = factory.createReader(
                new URI(String.format("brs:%s:%d", brsReaderModel.getAddress(), brsReaderModel.getPort())),
                codeReaderController, codeReaderController);

        return createReaderAdaptor(reader, brsReaderModel.getType());
    }


    private static ReaderFactory createReaderFactory(BrsReaderModel brsReaderModel, CodeReaderController codeReaderController) {
        ReaderFactory factory = null;
        switch (brsReaderModel.getType()) {
            case SICK:
                factory = new SickFactory(brsReaderModel.getBrsLifeCheckInterval(), brsReaderModel.getBrsLifeCheckTimeout(), "brs");
                break;
            case DATAMAN:
                factory = new BrsFactory(brsReaderModel.getBrsLifeCheckInterval(), brsReaderModel.getBrsLifeCheckTimeout(), codeReaderController, brsReaderModel.getBrsLifeCheckNumberOfRetries());
                break;
        }
        return factory;
    }

    private static CodeReaderAdaptor createReaderAdaptor(CodeReader reader, BrsType brsType) {
        CodeReaderAdaptor adaptor = null;
        switch (brsType) {
            case SICK:
                adaptor = new SickCodeReaderAdaptor(reader);
                break;
            case DATAMAN:
                adaptor = new DatamanCodeReaderAdaptor(reader);
                break;
        }
        return adaptor;
    }
}
