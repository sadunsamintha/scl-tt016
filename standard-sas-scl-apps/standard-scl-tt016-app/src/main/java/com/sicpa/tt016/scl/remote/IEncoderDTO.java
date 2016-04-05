/*
 * Author   		: JBarbieri
 * Date     		: 20-Oct-2010
 *
 * Project  		: tt016-spl
 * Package 			: com.sicpa.tt016.spl.business.model.encryption
 * File   			: IEncoderDTO.java
 *
 * Revision 		: $Revision$
 * Last modified	: $LastChangedDate$
 * Last modified by	: $LastChangedBy$
 * 
 * Copyright (c) 2010 SICPA Product Security SA, all rights reserved.
 */
package com.sicpa.tt016.scl.remote;

import java.io.Serializable;

import com.sicpa.standard.sasscl.sicpadata.generator.EncoderEmptyException;

/**
 * Encoder. Return encrypted code to be printed using printer.
 */
public interface IEncoderDTO extends Serializable {

	/**
	 * Get encrypted code.
	 * 
	 * @return enc
	 * @throws EncoderEmptyException
	 * @throws Exception
	 */
	String getEncryptedCode() throws EncoderEmptyException, Exception;

	/**
	 * Tells whether the encoder can create more encrypted code or not
	 * 
	 * @return true if encoder is empty, false otherwise.
	 */
	boolean isEncoderEmpty();

	/**
	 * Must load the password before be able to use an encoder
	 */

	void load(String password);

	/**
	 * Check if subsystemID into the Encoder is equals to Configuration
	 * subSystemID
	 * 
	 * @return
	 */
	boolean isValid();

	/**
	 * Subsystem is set to the encoder only first time the encoder is received
	 * from MAster
	 * 
	 * @param subsystemId
	 */
	public void setEncoderSubsystemId(int subsystemId);

	/**
	 * Used by to update legacy encoder into new encoder incorporating
	 * subsystemId
	 * 
	 * 
	 * @return
	 */
	public long getBatchId();

}
