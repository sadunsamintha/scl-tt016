/*
 * Author   		: JBarbieri
 * Date     		: 20-Oct-2010
 *
 * Project  		: tt016-spl
 * Package 			: com.sicpa.tt016.spl.devices.remoteServer.dto
 * File   			: DecoderDTO.java
 *
 * Revision 		: $Revision$
 * Last modified	: $LastChangedDate$
 * Last modified by	: $LastChangedBy$
 * 
 * Copyright (c) 2010 SICPA Product Security SA, all rights reserved.
 */
package com.sicpa.tt016.scl.remote.dto;

import com.sicpa.standard.common.log.LoggerResource;
import com.sicpa.standard.common.log.StdLogger;
import com.sicpa.standard.crypto.codes.NumericCode;
import com.sicpa.standard.crypto.codes.StringBasedCode;
import com.sicpa.tt016.common.security.authenticator.IMoroccoAuthenResult;
import com.sicpa.tt016.common.security.authenticator.IMoroccoAuthenticator;
import com.sicpa.tt016.scl.remote.IDecoderDTO;
import com.sicpa.tt016.scl.remote.dao.CodeDAO;

/**
 * Decoder class retreived from the remote server
 * 
 * @author squeva
 * 
 */
@LoggerResource("language/device")
public class DecoderDTO implements IDecoderDTO {

	/**
	 * Serializable.
	 */
	private static final long serialVersionUID = 5942388103422883830L;

	/**
	 * Logger of the class.
	 */
	private final static StdLogger LOGGER = StdLogger.getLogger(DecoderDTO.class);

	/**
	 * Decoder
	 */
	IMoroccoAuthenticator mCodeAuthenticator;

	/**
	 * 
	 * @param authenticator
	 */
	public DecoderDTO(IMoroccoAuthenticator authenticator) {
		setAuthenticator(authenticator);
	}

	/**
	 * 
	 * @param authenticator
	 */
	public void setAuthenticator(IMoroccoAuthenticator authenticator) {
		mCodeAuthenticator = authenticator;
	}

	// ~ Implementation of IDecoderDTO -----------------------------------------

	/**
	 * Decode an encrypted code.
	 * 
	 * @param code
	 *            encrypted CodeDAO
	 * @return decoded CodeDAO.
	 * @throws Exception
	 * @see com.sicpa.tt016.spl.business.model.encryption.IDecoderDTO#decode(com.sicpa.tt016.spl.business.model.CodeDAO)
	 */
	public CodeDAO decode(CodeDAO code) throws Exception {
		IMoroccoAuthenResult result = null;
		try {
			StringBasedCode Ccode = new NumericCode(code.getEncryptedCode());
			result = mCodeAuthenticator.authenticate(IMoroccoAuthenticator.Mode.DM8x18_SCRAMBLED, Ccode);
		} catch (Exception e) {
			code.setStatus(CodeDAO.CODE_NOT_AUTHENTICATED);
			code.setBatchId(0);
			code.setSequence(0);
			LOGGER.error("DECODER.ERROR.DECODING " + code.getEncryptedCode());
			return code;
		}

		code.setBatchId((int) result.getBatchId());
		code.setSequence(result.getSequence());
		code.setType(result.getType());
		if (result.isValid()) {
			code.setStatus(CodeDAO.CODE_AUTHENTICATED);
		} else {
			code.setStatus(CodeDAO.CODE_NOT_AUTHENTICATED);
			LOGGER.info("DECODER.INFO.CODE_NOT_AUTHENTIC " + code.getEncryptedCode());
		}
		return code;
	}
}
