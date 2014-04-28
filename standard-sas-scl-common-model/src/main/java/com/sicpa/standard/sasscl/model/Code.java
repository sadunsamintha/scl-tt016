package com.sicpa.standard.sasscl.model;

import java.io.Serializable;

import com.sicpa.standard.sasscl.model.custom.Customizable;

public class Code extends Customizable implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String stringCode;

	// Other decoded information for code

	protected CodeType codeType;

	protected long encoderId;

	protected long sequence;

	public CodeType getCodeType() {
		return this.codeType;
	}

	public void setCodeType(final CodeType codeType) {
		this.codeType = codeType;
	}

	public long getEncoderId() {
		return this.encoderId;
	}

	public void setEncoderId(final long batchId) {
		this.encoderId = batchId;
	}

	public long getSequence() {
		return this.sequence;
	}

	public void setSequence(final long sequence) {
		this.sequence = sequence;
	}

	public Code() {
		this.stringCode = "";
	}

	public Code(final String stringCode) {
		super();
		this.stringCode = stringCode;
	}

	public String getStringCode() {
		return this.stringCode;
	}

	@Override
	public String toString() {
		return this.stringCode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((stringCode == null) ? 0 : stringCode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Code other = (Code) obj;
		if (stringCode == null) {
			if (other.stringCode != null)
				return false;
		} else if (!stringCode.equals(other.stringCode))
			return false;
		return true;
	}

	public void setStringCode(final String stringCode) {
		this.stringCode = stringCode;
	}
}
