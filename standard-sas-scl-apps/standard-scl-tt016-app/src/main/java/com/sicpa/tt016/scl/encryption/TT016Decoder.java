package com.sicpa.tt016.scl.encryption;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.ioc.BeanProvider;
import com.sicpa.standard.crypto.codes.AlphaNumCode;
import com.sicpa.standard.crypto.codes.NumericCode;
import com.sicpa.standard.crypto.codes.StringBasedCode;
import com.sicpa.standard.sasscl.ioc.BeansName;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.DecodedCameraCode;
import com.sicpa.standard.sasscl.sicpadata.CryptographyException;
import com.sicpa.standard.sasscl.sicpadata.reader.IAuthenticator;
import com.sicpa.standard.sasscl.sicpadata.reader.IDecodedResult;
import com.sicpa.tt016.common.security.authenticator.IMoroccoAuthenResult;
import com.sicpa.tt016.common.security.authenticator.IMoroccoAuthenticator;

@SuppressWarnings("serial")
public class TT016Decoder implements IAuthenticator {

	private static final String SCL_MODE = "SCL";
	private static final String SAS_MODE = "SAS";

	private static final Logger logger = LoggerFactory.getLogger(TT016Decoder.class);

	private IMoroccoAuthenticator codeAuthenticator;

	private static boolean isMsasLeg = "true".equals(((Properties) BeanProvider.getBean(BeansName.ALL_PROPERTIES)).getProperty("production.config.server.msaslegacy"))?true:false ;
	
	public TT016Decoder(IMoroccoAuthenticator authenticator) {
		setAuthenticator(authenticator);
	}

	public void setAuthenticator(IMoroccoAuthenticator authenticator) {
		this.codeAuthenticator = authenticator;
	}

	public IDecodedResult decode(String mode, String encryptedCode) throws CryptographyException {
		if (mode.equals(SCL_MODE)) {
			return decodeSCL(encryptedCode);
		} else if (mode.equals(SAS_MODE)) {
			return decodeSAS(encryptedCode);
		}
		throw new IllegalArgumentException("illegal mode:" + mode);
	};

	private IDecodedResult decodeSCL(String encryptedCode) {
		DecodedCameraCode res = new DecodedCameraCode();
		try {
			StringBasedCode code = new NumericCode(encryptedCode);
			IMoroccoAuthenResult decodeResult = codeAuthenticator.authenticate(
					IMoroccoAuthenticator.Mode.DM8x18_SCRAMBLED, code);
			res.setBatchId((int) decodeResult.getBatchId());
			res.setSequence(decodeResult.getSequence());
			res.setAuthenticated(true);
			return res;
		} catch (Exception e) {
			logger.error("DECODER.ERROR.DECODING " + encryptedCode);
			return null;
		}
	}

	private IDecodedResult decodeSAS(String encryptedCode) {
		DecodedCameraCode res = new DecodedCameraCode();
		try {
			StringBasedCode code = null;
			if(isMsasLeg) {
				 code = new AlphaNumCode(encryptedCode);
			} else {
				 code = new NumericCode(encryptedCode);
			}
			IMoroccoAuthenResult decodeResult = codeAuthenticator.authenticate(IMoroccoAuthenticator.Mode.STD, code);
			res.setBatchId((int) decodeResult.getBatchId());
			res.setSequence(decodeResult.getSequence());
			res.setCodeType(new CodeType(decodeResult.getType()));
			if(decodeResult.isValid()) {
				res.setAuthenticated(true);
			}
			return res;
		} catch (Exception e) {
			logger.error("DECODER.ERROR.DECODING " + encryptedCode);
			return null;
		}
	}
}
