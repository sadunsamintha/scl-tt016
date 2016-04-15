package com.sicpa.standard.sasscl.model;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;

public class EncoderInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	// The id of the Encoder
	private long encoderId;
	// The batchid of the Encoder
	private long batchId;;
	// The code Type of the encoder
	private int codeTypeId;
	// The last number that the encoder used for a code
	private long sequence;
	// Date that the first code of the encoder was sent to the printer
	private Date firstCodeDate;
	// Date that the last code of the encoder was sent to the printer
	private Date lastCodeDate;
	// If the encoder is already finish(all the coders were used) or not
	private boolean finished;
	private Date onClientDate;

	protected transient File file;

	public EncoderInfo(IEncoder encoder, boolean finished) {
		this(encoder.getBatchId(), encoder.getId(), encoder.getCodeTypeId(), -1, encoder.getFirstCodeDate(), encoder
				.getLastCodeDate(), finished);
		setOnClientDate(encoder.getOnClientDate());
	}

	public EncoderInfo(long batchId, long encoderId, int codeTypeId, long lastUsedSequence, Date firstCodeDate,
			Date lastCodeDate, boolean finished) {
		this.batchId = batchId;
		this.encoderId = encoderId;
		this.codeTypeId = codeTypeId;
		this.sequence = lastUsedSequence;
		this.firstCodeDate = firstCodeDate;
		this.lastCodeDate = lastCodeDate;
		this.finished = finished;
	}

	public EncoderInfo() {
	}

	public long getEncoderId() {
		return encoderId;
	}

	public void setEncoderId(long id) {
		this.encoderId = id;
	}

	public int getCodeTypeId() {
		return codeTypeId;
	}

	public void setCodeTypeId(int codeTypeId) {
		this.codeTypeId = codeTypeId;
	}

	public long getSequence() {
		return sequence;
	}

	public void setSequence(long sequence) {
		this.sequence = sequence;
	}

	public Date getFirstCodeDate() {
		return firstCodeDate;
	}

	public void setFirstCodeDate(Date firstCodeDate) {
		this.firstCodeDate = firstCodeDate;
	}

	public Date getLastCodeDate() {
		return lastCodeDate;
	}

	public void setLastCodeDate(Date lastCodeDate) {
		this.lastCodeDate = lastCodeDate;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	@Override
	public String toString() {
		return "EncoderInfo [encoderId=" + encoderId + ", batchId=" + batchId + ", codeTypeId=" + codeTypeId
				+ ", sequence=" + sequence + ", firstCodeDate=" + firstCodeDate + ", lastCodeDate=" + lastCodeDate
				+ ", finished=" + finished + "]";
	}

	public void setFile(File file) {
		this.file = file;
	}

	public File getFile() {
		return file;
	}

	public long getBatchId() {
		return batchId;
	}

	public void setOnClientDate(Date onClientDate) {
		this.onClientDate = onClientDate;
	}

	public Date getOnClientDate() {
		return onClientDate;
	}
}
