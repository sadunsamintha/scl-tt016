package com.sicpa.standard.sasscl.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.sicpa.standard.client.common.utils.ObjectUtils;
import com.sicpa.standard.sasscl.sicpadata.reader.IDecodedResult;

/**
 * 
 * Class that defines the data structure to keep the decoded result of an encrypted code
 * 
 */
public class DecodedCameraCode implements IDecodedResult {

	protected CodeType codeType;

	protected long batchId;

	protected long sequence;

	protected boolean authenticated;

	private String mode;

	private Integer version;

	public DecodedCameraCode() {
	}

	public DecodedCameraCode(final CodeType codeType, final long batchId, final long sequence,
			final boolean authenticated) {
		this.codeType = codeType;
		this.batchId = batchId;
		this.sequence = sequence;
		this.authenticated = authenticated;
	}

	// getter and setter

	public long getBatchId() {
		return this.batchId;
	}

	public void setBatchId(final long batchId) {
		this.batchId = batchId;
	}

	public long getSequence() {
		return this.sequence;
	}

	public void setSequence(final long sequence) {
		this.sequence = sequence;
	}

	@Override
	public boolean isAuthenticated() {
		return this.authenticated;
	}

	@Override
	public void setAuthenticated(final boolean authenticated) {
		this.authenticated = authenticated;
	}

	public CodeType getCodeType() {
		return this.codeType;
	}

	public void setCodeType(final CodeType codeType) {
		this.codeType = codeType;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getMode() {
		return mode;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Integer getVersion() {
		return version;
	}

	@Override
	public int hashCode() {
		return ObjectUtils.computeCompositeHashCode(this.authenticated, this.batchId, this.codeType, this.sequence);
	}

	@Override
	public boolean equals(final Object obj) {

		if (obj instanceof DecodedCameraCode) {
			if (obj == this) {
				return true;
			}
			return EqualsBuilder.reflectionEquals(this, obj);
		}
		return false;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}


}
