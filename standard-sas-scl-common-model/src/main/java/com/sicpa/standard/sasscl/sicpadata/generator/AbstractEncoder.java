package com.sicpa.standard.sasscl.sicpadata.generator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.sicpadata.CryptographyException;

/**
 * Class that defines generic method for encoders
 */
public abstract class AbstractEncoder implements IEncoder {

	private static final Logger logger = LoggerFactory.getLogger(AbstractEncoder.class);

	private static final long serialVersionUID = 1L;

	protected long id;
	protected long batchId;
	protected long subsystemId;
	protected int year;
	protected int codeTypeId;
	protected Date firstCodeDate;
	protected Date lastCodeDate;
	protected Date onClientDate;

	public AbstractEncoder() {
	}

	public AbstractEncoder(final long batchid, final long id, int year, long subsystemId, int codeTypeId) {
		this.batchId = batchid;
		this.id = id;
		this.year = year;
		this.subsystemId = subsystemId;
		this.codeTypeId = codeTypeId;
	}

	protected abstract String getEncryptedCode() throws CryptographyException;

	protected void updateDateOfUse() {
		if (firstCodeDate == null) {
			firstCodeDate = new Date();
		}
		lastCodeDate = new Date();
	}

	@Override
	public synchronized List<String> getEncryptedCodes(final long numberCodes) throws CryptographyException {
		List<String> codes = new ArrayList<String>();
		String code = null;
		for (int i = 0; i < numberCodes; i++) {

			try {
				code = this.getEncryptedCode();
			} catch (EncoderEmptyException e) {
				if (codes.isEmpty()) {
					throw e;
				} else {
					return codes;
				}
			} catch (Exception e) {
				logger.error("fail to get code from the encoder:" + id, e);
				EventBusService.post(new MessageEvent(MessageEventKey.Coding.ERROR_GETTING_CODES_FROM_ENCODER));
				break;
			}
			if (code == null) {
				break;
			}
			codes.add(code);
		}
		return codes;
	}

	@Override
	public long getId() {
		return this.id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	@Override
	public int getYear() {
		return year;
	}

	@Override
	public long getSubsystemId() {
		return subsystemId;
	}

	@Override
	public Date getOnClientDate() {
		return onClientDate;
	}

	@Override
	public synchronized Date getFirstCodeDate() {
		return firstCodeDate;
	}

	@Override
	public synchronized Date getLastCodeDate() {
		return lastCodeDate;
	}

	@Override
	public int getCodeTypeId() {
		return codeTypeId;
	}

	@Override
	public void setOnClientDate(Date onClientDate) {
		this.onClientDate = onClientDate;
	}

	public void setSubsystemId(long subsystemId) {
		this.subsystemId = subsystemId;
	}

	public void setBatchId(long batchId) {
		this.batchId = batchId;
	}

	public long getBatchId() {
		return batchId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (batchId ^ (batchId >>> 32));
		result = prime * result + codeTypeId;
		result = prime * result + ((firstCodeDate == null) ? 0 : firstCodeDate.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((lastCodeDate == null) ? 0 : lastCodeDate.hashCode());
		result = prime * result + ((onClientDate == null) ? 0 : onClientDate.hashCode());
		result = prime * result + (int) (subsystemId ^ (subsystemId >>> 32));
		result = prime * result + year;
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
		AbstractEncoder other = (AbstractEncoder) obj;
		if (batchId != other.batchId)
			return false;
		if (codeTypeId != other.codeTypeId)
			return false;
		if (firstCodeDate == null) {
			if (other.firstCodeDate != null)
				return false;
		} else if (!firstCodeDate.equals(other.firstCodeDate))
			return false;
		if (id != other.id)
			return false;
		if (lastCodeDate == null) {
			if (other.lastCodeDate != null)
				return false;
		} else if (!lastCodeDate.equals(other.lastCodeDate))
			return false;
		if (onClientDate == null) {
			if (other.onClientDate != null)
				return false;
		} else if (!onClientDate.equals(other.onClientDate))
			return false;
		if (subsystemId != other.subsystemId)
			return false;
		if (year != other.year)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AbstractEncoder [id=" + id + ", batchId=" + batchId + ", subsystemId=" + subsystemId + ", year=" + year
				+ ", codeTypeId=" + codeTypeId + ", firstCodeDate=" + firstCodeDate + ", lastCodeDate=" + lastCodeDate
				+ ", onClientDate=" + onClientDate + "]";
	}
}
