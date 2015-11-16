package com.sicpa.standard.sasscl.devices.brs.reader;


import com.sicpa.common.device.reader.CodeReader;
import com.sicpa.common.device.reader.imaging.ImageReceiver;

import java.io.IOException;

public abstract class AbstractCodeReaderAdaptor implements CodeReaderAdaptor {

    protected CodeReader codeReader;


    private String id;

    public AbstractCodeReaderAdaptor(CodeReader codeReader, String id) {
        this.id = id;
        this.codeReader = codeReader;
    }


    @Override
    public void start() {
        codeReader.start();

    }

    @Override
    public void stop() {
        codeReader.stop();
    }

    @Override
    public boolean isImageRetrievalSupported() {
        return codeReader.isImageRetrievalSupported();
    }

    @Override
    public void startImageRetrieval(ImageReceiver receiver) {
        codeReader.startImageRetrieval(receiver);

    }

    @Override
    public void stopImageRetrieval() {
        codeReader.stopImageRetrieval();
    }

    @Override
    public void trigger() throws IOException {
        codeReader.trigger();

    }

    @Override
    public void triggerLastImage() throws IOException {
        codeReader.triggerLastImage();

    }

    @Override
    public void config() throws IOException {
        codeReader.config();

    }

    @Override
    public void sendData(byte... data) throws IOException {
        codeReader.sendData(data);

    }

    @Override
    public String getId() {
        return id;
    }
}
