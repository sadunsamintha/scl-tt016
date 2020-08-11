package com.sicpa.ttth.storage;

import java.io.File;
import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.storage.ISimpleFileStorage;
import com.sicpa.standard.client.common.storage.StorageException;
import com.sicpa.standard.sasscl.common.storage.FileStorage;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductionParameters;


public class TTTHFileStorage extends FileStorage {

	private final static Logger logger = LoggerFactory.getLogger(TTTHFileStorage.class);

	private final String dataFolder;
	private final String internalFolder;
	private final String quarantineFolder;

	private String timeStampFormat = "yyyy-MM-dd--HH-mm-ss-SSS";

	private ISimpleFileStorage storageBehavior;

	public TTTHFileStorage(final String baseFolder, String internalFolder, String quarantineFolder,
                           ISimpleFileStorage storageBehavior) {
		super(baseFolder, internalFolder, quarantineFolder, storageBehavior);

		this.quarantineFolder = quarantineFolder;
		this.dataFolder = baseFolder;
		this.internalFolder = internalFolder;
		this.storageBehavior = storageBehavior;
	}
}
