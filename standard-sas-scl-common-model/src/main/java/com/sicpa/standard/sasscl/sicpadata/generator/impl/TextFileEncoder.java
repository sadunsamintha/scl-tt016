package com.sicpa.standard.sasscl.sicpadata.generator.impl;

import static org.apache.commons.io.FileUtils.readLines;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.sicpadata.CryptographyException;

public class TextFileEncoder extends CodeListEncoder {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(TextFileEncoder.class);
	
	private String file;
	private transient boolean init = false;

	public TextFileEncoder(long batchid, int id, int year, long subsystemId, int codeTypeId, String file) {
		super(batchid, id, year, subsystemId, codeTypeId);
		this.file = file;
	}

	@Override
	public String getEncryptedCode() throws CryptographyException {
		if (!init) {
			ReadFile();
			init = true;
		}
		return super.getEncryptedCode();
	}

	@SuppressWarnings("unchecked")
	protected void ReadFile() {
		try {
			addEncryptedCodes(readLines(new File(file)));
		} catch (IOException e) {
			logger.error("fail to load codes from file :" + file, e);
		}
	}
}
