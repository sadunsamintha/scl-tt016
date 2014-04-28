package com.sicpa.standard.sasscl.sicpadata.generator;

import java.text.MessageFormat;

import com.sicpa.standard.sasscl.sicpadata.CryptographyException;

/**
 * 
 * This exception is thrown when the encoder is empty, e.g. the encoder cannot provide anymore code
 * 
 * @author YYang
 * 
 */
public class EncoderEmptyException extends CryptographyException {

	private static final long serialVersionUID = 5349899701707374836L;

	public EncoderEmptyException() {
	}

	public EncoderEmptyException(final String message) {
		super(message);
	}

	public EncoderEmptyException(final String message, final Throwable e) {
		super(message, e);
	}

	public EncoderEmptyException(final Throwable cause, final String msgKey, final Object... msgParam) {
		super(MessageFormat.format(msgKey, msgParam), cause);
	}

}
