package com.sicpa.tt016.scl.encryption;

import com.sicpa.standard.crypto.codes.StringBasedCode;
import com.sicpa.standard.crypto.exceptions.CryptoException;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.sicpadata.CryptographyException;
import com.sicpa.standard.sasscl.sicpadata.generator.EncoderEmptyException;
import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;
import com.sicpa.tt016.common.security.bean.SequenceBean;
import com.sicpa.tt016.common.security.encoder.IMoroccoEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressWarnings("serial")
public class TT016Encoder implements IEncoder {

	private final static Logger logger = LoggerFactory.getLogger(TT016Encoder.class);

	private IMoroccoEncoder tt016encoder;
	private long remainingCodes = 0;
	private long currentIndex = 0;
	private int subSystemId;

	private Date firstCodeDate;
	private Date lastCodeDate;
	private boolean finishedAndSentToMaster;
	private Date onClientDate;
	private int codeTypeId;

	public TT016Encoder(IMoroccoEncoder encoder, int subsystemId, int codeTypeId) {
		tt016encoder = encoder;
		remainingCodes = tt016encoder.getCapacity();
		setEncoderSubsystemId(subsystemId);
		currentIndex = 0;
		this.codeTypeId = codeTypeId;
	}

	public void setEncoderSubsystemId(int subsystemId) {
		subSystemId = subsystemId;
	}

	public long getBatchId() {
		return tt016encoder.getBatchId();
	}

	private String getCode() throws CryptoException, EncoderEmptyException {
		if (remainingCodes <= 0) {
			throw new EncoderEmptyException();
		}
		StringBasedCode code = (StringBasedCode) tt016encoder.getCode(createCodeRequestOrder());
		remainingCodes--;
		currentIndex++;
		return code.getCode();
	}

	private SequenceBean createCodeRequestOrder() {
		SequenceBean seq = new SequenceBean();
		seq.setSequence(currentIndex);
		return seq;
	}

	private void updateDateOfUse() {
		lastCodeDate = new Date();
		if (firstCodeDate == null) {
			firstCodeDate = lastCodeDate;
		}
	}

	public void setCurrentIndex(long currentIndex) {
		this.currentIndex = currentIndex;
	}

	public long getCurrentIndex() {
		return currentIndex;
	}

	public boolean isEncoderEmpty() {
		return remainingCodes <= 0;
	}

	public long getRemainingCodes() {
		return remainingCodes;
	}

	public void setRemainingCodes(long remainingCodes) {
		this.remainingCodes = remainingCodes;
	}

	public void setFinishedAndSentToMaster(boolean finishedAndSentToMaster) {
		this.finishedAndSentToMaster = finishedAndSentToMaster;
	}

	public boolean isFinishedAndSentToMaster() {
		return finishedAndSentToMaster;
	}

	public void setEncoderDownloadedDate(Date encoderDownloadedDate) {
		this.onClientDate = encoderDownloadedDate;
	}

	public Date getEncoderDownloadedDate() {
		return onClientDate;
	}

	public int getSubSystemId() {
		return subSystemId;
	}

	@Override
	public long getId() {
		return tt016encoder.getBatchId();
	}

	@Override
	public List<String> getEncryptedCodes(long numberOfCodes) throws CryptographyException {
		updateDateOfUse();
		long numberOfCodesToRequest = Math.min(numberOfCodes,getRemainingCodes());
		try {
			List<String> codes = new ArrayList<>();
			for (long i = 0; i < numberOfCodesToRequest; i++) {
				codes.add(getCode());
			}
			return codes;
		} catch (Exception e) {
			logger.error("Failed to generate code.", e);
			throw new CryptographyException(e, "Failed to generate encrypted code");
		}
	}
	
	@Override
	public List<String> getEncryptedCodes(long numberOfCodes, ProductionParameters productionParameters) throws CryptographyException {
		updateDateOfUse();
		long numberOfCodesToRequest = Math.min(numberOfCodes,getRemainingCodes());
		try {
			List<String> codes = new ArrayList<>();
			for (long i = 0; i < numberOfCodesToRequest; i++) {
				codes.add(getCode());
			}
			return codes;
		} catch (Exception e) {
			logger.error("Failed to generate code.", e);
			throw new CryptographyException(e, "Failed to generate encrypted code");
		}
	}

	@Override
	public int getYear() {
		return 0;
	}

	@Override
	public long getSubsystemId() {
		return subSystemId;
	}

	@Override
	public int getCodeTypeId() {
		return codeTypeId;
	}

	@Override
	public Date getFirstCodeDate() {
		return firstCodeDate;
	}

	@Override
	public Date getLastCodeDate() {
		return lastCodeDate;
	}

	@Override
	public Date getOnClientDate() {
		return onClientDate;
	}

	@Override
	public void setOnClientDate(Date onClientDate) {
		this.onClientDate = onClientDate;
	}

	public void setFirstCodeDate(Date firstCodeDate) {
		this.firstCodeDate = firstCodeDate;
	}

	public void setLastCodeDate(Date lastCodeDate) {
		this.lastCodeDate = lastCodeDate;
	}
}
