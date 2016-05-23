package com.sicpa.standard.sasscl.sicpadata.generator.impl;

import com.sicpa.standard.sasscl.sicpadata.CryptographyException;
import com.sicpa.standard.sasscl.sicpadata.generator.AbstractEncoder;
import com.sicpa.standard.sasscl.sicpadata.generator.EncoderEmptyException;

import java.util.ArrayList;
import java.util.List;

/**
 * Encoder that keep a list of encrypted codes
 *
 */
public class CodeListEncoder extends AbstractEncoder {

	private static final long serialVersionUID = 1L;

	private final List<String> encryptedCodes = new ArrayList<>();

	public CodeListEncoder(long batchid, int id, int year, long subsystemId, int codeTypeId) {
		super(batchid, id, year, subsystemId, codeTypeId);
	}

	public CodeListEncoder(long batchid, int id, int year, long subsystemId, List<String> codes, int codeTypeId) {
		this(batchid, id, year, subsystemId, codeTypeId);
		this.addEncryptedCodes(codes);
	}

	public void addEncryptedCodes(final List<String> encryptedCodes) {
		this.encryptedCodes.addAll(encryptedCodes);
	}

	@Override
	public String getEncryptedCode() throws CryptographyException {
		if (isEncoderEmpty()) {
			throw new EncoderEmptyException("Encoder is empty");
		}
		updateDateOfUse();
		return this.encryptedCodes.remove(0);
	}

	@Override
	public boolean isEncoderEmpty() {
		return this.encryptedCodes.size() == 0;
	}

}
