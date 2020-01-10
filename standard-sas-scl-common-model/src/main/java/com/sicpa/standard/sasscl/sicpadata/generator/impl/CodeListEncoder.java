package com.sicpa.standard.sasscl.sicpadata.generator.impl;

import com.sicpa.standard.gui.utils.Pair;
import com.sicpa.standard.sasscl.model.ProductionParameters;
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
	protected String getEncryptedCode(ProductionParameters productionParameters) throws CryptographyException {
		if (isEncoderEmpty()) {
			throw new EncoderEmptyException("Encoder is empty");
		}
		updateDateOfUse();
		return this.encryptedCodes.remove(0);
	}
	
	@Override
	public List<Pair<String, String>> getEncryptedCodesPair(long numberOfCodes) throws CryptographyException {
		List<Pair<String, String>> codes = new ArrayList<>();
		codes.add(new Pair<String, String>("", ""));
		return codes;
	}

	@Override
	public List<Pair<String, String>> getEncryptedCodesPair(long numberOfCodes,
			ProductionParameters productionParameters) throws CryptographyException {
		List<Pair<String, String>> codes = new ArrayList<>();
		codes.add(new Pair<String, String>("", ""));
		return codes;
	}
	
	@Override
	@Deprecated
	protected Pair<String, String> getEncryptedCodePair() throws CryptographyException {
		throw new CryptographyException("Deprecated");
	}

	@Override
	@Deprecated
	protected Pair<String, String> getEncryptedCodePair(ProductionParameters productionParameters)
			throws CryptographyException {
		throw new CryptographyException("Deprecated");
	}

	@Override
	public boolean isEncoderEmpty() {
		return this.encryptedCodes.size() == 0;
	}
}
