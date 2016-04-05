/*
 * Author   		: JBarbieri
 * Date     		: 20-Oct-2010
 *
 * Project  		: tt016-spl
 * Package 			: com.sicpa.tt016.spl.business.model.encryption
 * File   			: IDecoderDTO.java
 *
 * Revision 		: $Revision$
 * Last modified	: $LastChangedDate$
 * Last modified by	: $LastChangedBy$
 * 
 * Copyright (c) 2010 SICPA Product Security SA, all rights reserved.
 */
package com.sicpa.tt016.scl.remote;

import java.io.Serializable;

import com.sicpa.tt016.scl.remote.dao.CodeDAO;

/**
 * Decoder DTO. Decode encrypted code.
 */
public interface IDecoderDTO extends Serializable {

	/**
	 * Decode the value
	 * 
	 * @param codeDAO
	 *            encrypted codeDAO
	 * @return CodeDAO decrypted codeDAO
	 * @throws Exception
	 */
	public CodeDAO decode(CodeDAO codeDAO) throws Exception;
}
