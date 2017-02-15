package com.sicpa.tt016.util;

import com.sicpa.standard.client.common.utils.DateUtils;
import com.sicpa.standard.sasscl.common.storage.FileStorage;
import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;
import com.sicpa.tt016.scl.encryption.TT016Encoder;
import com.sicpa.tt016.spl.devices.remoteServer.dto.EncoderDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Date;
import java.util.stream.Stream;

import static com.sicpa.standard.sasscl.common.storage.FileStorage.FILE_ENCODER;
import static com.sicpa.standard.sasscl.common.storage.FileStorage.FOLDER_ENCODER_BUFFER;

public class LegacyEncoderConverter {

    private final static Logger logger = LoggerFactory.getLogger(LegacyEncoderConverter.class);

    private static final String legacyEncoderFileSuffix = ".spl";
    private static final String newEncoderDateFormat = "yyyy-MM-dd--HH-mm-ss-SSS";

    private FileStorage fileStorage;
    private int codeTypeId;

    public void convertLegacyEncoders() {
        String pathEncoders = fileStorage.getInternalFolder() + "/" + "encoders";
        final int[] i = new int[]{0};

        try (Stream<Path> paths = Files.walk(Paths.get(pathEncoders), 1)) {
            paths.filter(path -> Files.isRegularFile(path) && isLegacyEncoderFile(path))
                    .forEach((path) -> {
                                try {
                                    EncoderDTO legacyEncoder = loadLegacyEncoder(path);
                                    IEncoder convertedEncoder = convertLegacy2New(legacyEncoder);

                                    String convertedEncoderFilename = MessageFormat.format(
                                            FILE_ENCODER,
                                            "CodeType" + convertedEncoder.getCodeTypeId(),
                                            "xxxx",
                                            DateUtils.format(newEncoderDateFormat, new Date()),
                                            i[0]++);
                                    fileStorage.saveInternal(convertedEncoder,
                                            FOLDER_ENCODER_BUFFER + "/" + convertedEncoderFilename);

                                    Files.delete(path);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    logger.error("Error converting legacy encoder", e);
                                }
                            }
                    );

        } catch (IOException e) {
            logger.error("Error loading legacy encoder files", e);
        }
    }

    private IEncoder convertLegacy2New(EncoderDTO legacyEncoder) {
        TT016Encoder newEncoder = new TT016Encoder(legacyEncoder.getEncoder(), legacyEncoder.getSubSystemId(),
                codeTypeId);
        newEncoder.setCurrentIndex(legacyEncoder.getCurrentIndex());
        newEncoder.setEncoderDownloadedDate(legacyEncoder.getEncoderDownloadedDate());
        newEncoder.setEncoderSubsystemId(legacyEncoder.getSubSystemId());
        newEncoder.setFinishedAndSentToMaster(legacyEncoder.isFinishedAndSentToMaster());
        newEncoder.setFirstCodeDate(new Date(legacyEncoder.getFirstCodeDate()));
        newEncoder.setLastCodeDate(new Date(legacyEncoder.getLastCodeDate()));
        newEncoder.setRemainingCodes(legacyEncoder.getRemainingCodes());

        return newEncoder;
    }

    private EncoderDTO loadLegacyEncoder(Path path) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toString()));
        EncoderDTO legacyEncoder = (EncoderDTO) ois.readObject();

        ois.close();

        return legacyEncoder;
    }

    private boolean isLegacyEncoderFile(Path path) {
        return path.getFileName().toString().endsWith(legacyEncoderFileSuffix);
    }

    public void setFileStorage(FileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }

    public void setCodeTypeId(int codeTypeId) {
        this.codeTypeId = codeTypeId;
    }
}
