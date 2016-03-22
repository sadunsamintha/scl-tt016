package com.sicpa.tt018.scl.remoteServer.simu;

import java.text.DecimalFormat;

import com.sicpa.standard.crypto.exceptions.CryptoException;
import com.sicpa.tt018.interfaces.security.IAlbaniaEncoder;
import com.sicpa.tt018.interfaces.security.dto.SequenceDTO;

public class AlbaniaSimuEncoder implements IAlbaniaEncoder {

	private static final long serialVersionUID = -8212770159072450676L;

	private DecimalFormat formatCodeType = new DecimalFormat("00");
	private DecimalFormat formatEncoderId = new DecimalFormat("00");
	private DecimalFormat format = new DecimalFormat("000000");
	private static final long SIMU_BATCHID = 999L;
	private static final long SIMU_CAPACITY = 999L;
	private int codeTypeId;

	private String ENCODER_HEADER;

	public AlbaniaSimuEncoder(int encoderId, int codeTypeId) {
		ENCODER_HEADER = formatEncoderId.format(encoderId);
		this.codeTypeId = codeTypeId;
	}

	@Override
	public void load(String userPassword) throws CryptoException {
	}

	@Override
	public String getCode(SequenceDTO sequence) throws CryptoException {
		return ENCODER_HEADER + formatCodeType.format(codeTypeId) + format.format(sequence.getSequence());
	}

	@Override
	public long getCapacity() {
		return SIMU_CAPACITY;
	}

	@Override
	public long getBatchId() {
		return SIMU_BATCHID;
	}

}
