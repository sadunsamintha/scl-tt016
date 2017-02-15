/*
 * Author   		: JBarbieri
 * Date     		: 20-Oct-2010
 *
 * Project  		: tt016-spl
 * Package 			: com.sicpa.tt016.spl.devices.remoteServer.dto
 * File   			: EncoderDTO.java
 *
 * Revision 		: $Revision$
 * Last modified	: $LastChangedDate$
 * Last modified by	: $LastChangedBy$
 * 
 * Copyright (c) 2010 SICPA Product Security SA, all rights reserved.
 */
package com.sicpa.tt016.spl.devices.remoteServer.dto;

import com.sicpa.standard.common.log.StdLogger;
import com.sicpa.standard.crypto.codes.StringBasedCode;
import com.sicpa.standard.crypto.exceptions.CryptoException;
import com.sicpa.tt016.common.security.bean.SequenceBean;
import com.sicpa.tt016.common.security.encoder.IMoroccoEncoder;
import com.sicpa.tt016.spl.business.model.encryption.IEncoderDTO;

import java.util.Date;

/**
 * Encoder. Encrypted a code.
 */
public class EncoderDTO implements IEncoderDTO {

	private static final long serialVersionUID = 6300425543038958314L;

	private IMoroccoEncoder mEncoder;
	private long mRemainingCodes = 0;
	private long mCurrentIndex = 0;
	private int mSubSystemId;
	private final static StdLogger LOGGER = StdLogger.getLogger(EncoderDTO.class);

	private long firstCodeDate = -1;
	private long lastCodeDate = -1;

	private boolean finishedAndSentToMaster;

	private Date encoderDownloadedDate;

	private long initialIndex = 0;

	/**
	 * Create an EncoderDTO.
	 * 
	 * @param encoder
	 */
	public EncoderDTO(IMoroccoEncoder encoder) {
		mEncoder = encoder;
		mRemainingCodes = mEncoder.getCapacity();
		mCurrentIndex = 0;
	}

	public void setEncoderSubsystemId(int subsystemId) {
		mSubSystemId = subsystemId;
	}

	public long getBatchId() {
		return mEncoder.getBatchId();
	}

	public boolean isValid() {
		return true;
	}

	/**
	 * Get next encrypted code.
	 * 
	 * @return Encrypted code.
	 * @throws EncoderEmptyException
	 *             No more code can be encrypted.
	 * @throws Exception
	 */
	public String getEncryptedCode() throws EncoderEmptyException, Exception {
		// Throw an exception when it remain no more code. the sequence of an
		// encoder is [0; 10 000 000[
		if (mRemainingCodes <= 0)
			throw new EncoderEmptyException();
		lastCodeDate = System.currentTimeMillis();
		if (firstCodeDate <= 0) {
			firstCodeDate = lastCodeDate;
		}

		SequenceBean seq = new SequenceBean();
		seq.setSequence(mCurrentIndex);
		StringBasedCode code = (StringBasedCode) mEncoder.getCode(seq);
		mRemainingCodes--;
		mCurrentIndex++;
		return code.getCode();
	}

	@Override
	public String toString() {
		return "EncoderDTO [mRemainingCodes=" + mRemainingCodes
				+ ", mCurrentIndex=" + mCurrentIndex + ", mSubSystemId="
				+ mSubSystemId + ", batchId=" + mEncoder.getBatchId()
				+ ", capacity=" + mEncoder.getCapacity() + "]";
	}

	public void setCurrentIndex(long currentIndex) {
		this.mCurrentIndex = currentIndex;
	}

	public long getCurrentIndex() {
		return mCurrentIndex;
	}

	/**
	 * Test if encoder is empty, i.e. no more code can be encrypted.
	 * 
	 * @return true if encoder is empty, false otherwise.
	 */
	public boolean isEncoderEmpty() {
		// When mRemainingCodes <= 0 that means that the encoder sequence = 9
		// 999 999 and sequence must be [0; 10 000 000[
		return mRemainingCodes <= 0;
	}

	/**
	 * Encoder is empty.
	 */
	public static class EncoderEmptyException extends Exception {

		private static final long serialVersionUID = -2821280773645833426L;

		public EncoderEmptyException() {
			super();
		}

		public EncoderEmptyException(String name) {
			super(name);
		}
	}

	@Override
	public void load(String password) {
		try {
			mEncoder.load(password);
		} catch (CryptoException e) {
			LOGGER.error("Error loading encoder", e);
		}
	}

	public IMoroccoEncoder getEncoder() {
		return mEncoder;
	}

	public long getFirstCodeDate() {
		return firstCodeDate;
	}

	public long getLastCodeDate() {
		return lastCodeDate;
	}

	public long getRemainingCodes() {
		return mRemainingCodes;
	}

	public void setRemainingCodes(long remainingCodes) {
		mRemainingCodes = remainingCodes;
	}

	public void setFinishedAndSentToMaster(boolean finishedAndSentToMaster) {
		this.finishedAndSentToMaster = finishedAndSentToMaster;
	}

	public boolean isFinishedAndSentToMaster() {
		return finishedAndSentToMaster;
	}

	public void setEncoderDownloadedDate(Date encoderDownloadedDate) {
		this.encoderDownloadedDate = encoderDownloadedDate;
	}

	public Date getEncoderDownloadedDate() {
		return encoderDownloadedDate;
	}

	public int getSubSystemId() {
		return mSubSystemId;
	}

	public long getInitialIndex() {
		return initialIndex;
	}

	public void setInitialIndex(long initialIndex) {
		this.initialIndex = initialIndex;
	}
}
