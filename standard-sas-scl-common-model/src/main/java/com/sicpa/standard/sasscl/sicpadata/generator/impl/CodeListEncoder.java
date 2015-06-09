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

	private static final long serialVersionUID = 435948152922637022L;

	protected final List<String> encryptedCodes = new ArrayList<String>();

	public CodeListEncoder(final long batchid,final int id, final int year, final long subsystemId, int codeTypeId) {
		super(batchid,id, year, subsystemId, codeTypeId);
	}

	public CodeListEncoder(final long batchid,final int id, final int year, final long subsystemId, final List<String> codes, int codeTypeId) {
		this(batchid,id, year, subsystemId, codeTypeId);
		this.addEncryptedCodes(codes);
	}

	protected List<String> getEncryptedCodes() {
		return this.encryptedCodes;
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
