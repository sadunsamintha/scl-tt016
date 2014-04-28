package com.sicpa.standard.sasscl.sicpadata.generator.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.sicpadata.CryptographyException;

public class TextFileEncoder extends CodeListEncoder {

	private static final long serialVersionUID = 1L;
	private static final Logger logger= LoggerFactory.getLogger(TextFileEncoder.class);
	protected String file;
	protected transient boolean init = false;

	public TextFileEncoder(final long batchid,int id, int year, long subsystemId, int codeTypeId, String file) {
		super(batchid,id, year, subsystemId, codeTypeId);
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
		BufferedReader br = null;
		try {
			encryptedCodes.addAll(FileUtils.readLines(new File(file)));
		} catch (IOException e) {
			logger.error("fail to load codes from file :" + file, e);
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (Exception e) {
			}
		}
	}
}
