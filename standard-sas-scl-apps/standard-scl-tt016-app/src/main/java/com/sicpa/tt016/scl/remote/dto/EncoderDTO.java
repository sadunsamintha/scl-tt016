package com.sicpa.tt016.scl.remote.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.crypto.codes.StringBasedCode;
import com.sicpa.standard.crypto.exceptions.CryptoException;
import com.sicpa.standard.sasscl.sicpadata.CryptographyException;
import com.sicpa.standard.sasscl.sicpadata.generator.EncoderEmptyException;
import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;
import com.sicpa.tt016.common.security.bean.SequenceBean;
import com.sicpa.tt016.common.security.encoder.IMoroccoEncoder;

@SuppressWarnings("serial")
public class EncoderDTO implements IEncoder {

	private final static Logger logger = LoggerFactory.getLogger(EncoderDTO.class);

	private IMoroccoEncoder mEncoder;
	private long remainingCodes = 0;
	private long currentIndex = 0;
	private int subSystemId;

	private Date firstCodeDate;
	private Date lastCodeDate;
	private boolean finishedAndSentToMaster;
	private Date onClientDate;

	public EncoderDTO(IMoroccoEncoder encoder, int subsystemId) {
		mEncoder = encoder;
		remainingCodes = mEncoder.getCapacity();
		setEncoderSubsystemId(subsystemId);
		currentIndex = 0;
	}

	public void setEncoderSubsystemId(int subsystemId) {
		subSystemId = subsystemId;
	}

	public long getBatchId() {
		return mEncoder.getBatchId();
	}

	private String getCode() throws CryptoException, EncoderEmptyException {
		if (remainingCodes <= 0) {
			throw new EncoderEmptyException();
		}
		StringBasedCode code = (StringBasedCode) mEncoder.getCode(createCodeRequestOrder());
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

	@Override
	public String toString() {
		return "EncoderDTO [mRemainingCodes=" + remainingCodes + ", mCurrentIndex=" + currentIndex + ", mSubSystemId="
				+ subSystemId + ", batchId=" + mEncoder.getBatchId() + ", capacity=" + mEncoder.getCapacity() + "]";
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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<String> getEncryptedCodes(long numberOfCodes) throws CryptographyException {
		updateDateOfUse();
		try {
			List<String> codes = new ArrayList<>();
			for (long i = 0; i < numberOfCodes; i++) {
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
		// TODO Auto-generated method stub
		return 0;
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
}
