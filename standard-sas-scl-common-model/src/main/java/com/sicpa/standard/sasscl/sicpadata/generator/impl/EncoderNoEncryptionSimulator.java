/**
 * Author	: CDeAlmeida
 * Date		: 22 Jul 2010
 *
 * Copyright (c) 2010 SICPA Security Solutions, all rights reserved.
 *
 */
package com.sicpa.standard.sasscl.sicpadata.generator.impl;

import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.sicpadata.CryptographyException;
import com.sicpa.standard.sasscl.sicpadata.generator.AbstractEncoder;
import com.sicpa.standard.sasscl.sicpadata.generator.EncoderEmptyException;

/**
 * @author CDeAlmeida
 *
 */
public class EncoderNoEncryptionSimulator extends AbstractEncoder {

	private static final long serialVersionUID = 1L;

	protected int max;

	protected long sequence;

	public EncoderNoEncryptionSimulator(final long batchid,int id, final int min, final int max, int year, long subsystemId, int codeTypeId) {
		super(batchid,id, year, subsystemId, codeTypeId);
		this.max = max;
		this.sequence = min;
	}

	public EncoderNoEncryptionSimulator(final long batchid,int id, final int min, final int max, int year, int codeTypeId) {
		this(batchid,id, min, max, year, 0, codeTypeId);
	}

	@Override
	public String getEncryptedCode() throws CryptographyException {
		if (this.sequence > this.max) {
			throw new EncoderEmptyException();
		} else {
			updateDateOfUse();
			return String.valueOf(this.sequence++);
		}
	}
	
	@Override
	protected String getEncryptedCode(ProductionParameters productionParameters) throws CryptographyException {
		if (this.sequence > this.max) {
			throw new EncoderEmptyException();
		} else {
			updateDateOfUse();
			return String.valueOf(this.sequence++);
		}
	}

	public long getNumberOfAvailableEncryptedCode() {
		return this.max - this.sequence;
	}

	@Override
	public boolean isEncoderEmpty() {
		return getNumberOfAvailableEncryptedCode() <= 0;
	}

	public int getMax() {
		return this.max;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + max;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		EncoderNoEncryptionSimulator other = (EncoderNoEncryptionSimulator) obj;
		if (max != other.max)
			return false;
		return true;
	}

	public long getSequence() {
		return sequence;
	}
}
