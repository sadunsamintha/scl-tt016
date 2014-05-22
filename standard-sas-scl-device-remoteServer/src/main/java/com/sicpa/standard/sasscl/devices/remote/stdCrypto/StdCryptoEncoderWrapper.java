package com.sicpa.standard.sasscl.devices.remote.stdCrypto;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.sicpa.standard.printer.xcode.BlockFactory;
import com.sicpa.standard.printer.xcode.ExtendedCode;
import com.sicpa.standard.printer.xcode.ExtendedCodeFactory;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.sicpadata.CryptographyException;
import com.sicpa.standard.sasscl.sicpadata.generator.AbstractEncoder;
import com.sicpa.standard.sasscl.sicpadata.generator.EncoderEmptyException;
import com.sicpa.standard.sicpadata.api.business.IBSicpadataGenerator;
import com.sicpa.standard.sicpadata.api.exception.GeneratorCapacityException;
import com.sicpa.standard.sicpadata.api.exception.SicpadataException;

/**
 * Encoder that delegates the encode process to standard crypto business encoder
 */
public class StdCryptoEncoderWrapper extends AbstractEncoder {

	private static final Logger logger = LoggerFactory.getLogger(StdCryptoEncoderWrapper.class);

	private static final long serialVersionUID = 1L;

	protected IBSicpadataGenerator encoder;

	protected ICryptoFieldsConfig cryptoFieldsConfig;
	
	private ExtendedCodeFactory extendedCodeFactory;

	public StdCryptoEncoderWrapper(final long batchid, final int id, final IBSicpadataGenerator encoder,
			final int year, final long subsystemId, final ICryptoFieldsConfig cryptoFieldsConfig, int codeTypeId) {
		super(batchid, id, year, subsystemId, codeTypeId);
		this.encoder = encoder;
		encoder.setId(Long.valueOf(id));
		this.cryptoFieldsConfig = cryptoFieldsConfig;

		if(codeTypeId >= CodeType.ExtendedCodeId){
			ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("extended-code.xml");
			
			this.extendedCodeFactory = (ExtendedCodeFactory)ctx.getBean(String.valueOf(codeTypeId));
			Assert.assertNotNull(extendedCodeFactory);
			ctx.close();
		}
	}

	@Override
	@Deprecated
	public final String getEncryptedCode() throws CryptographyException {
		throw new CryptographyException("Deprecated");
	}

	@Override
	public synchronized List<String> getEncryptedCodes(long numberCodes) throws CryptographyException {
		try {

			updateDateOfUse();

			long numberCodesToGenerate = Math.min(getRemainingCodes(), numberCodes);
			if (numberCodesToGenerate == 0) {
				throw new EncoderEmptyException();
			}
			Object[] dummy = new Object[]{cryptoFieldsConfig.getFields(this)};
			List<String> code = encoder.generate((int) numberCodesToGenerate, dummy);
//			List<String> code = encoder.generate((int) numberCodesToGenerate, cryptoFieldsConfig.getFields(this));

			return code;

		} catch (GeneratorCapacityException e) {
			throw new EncoderEmptyException("", e);
		} catch (SicpadataException e) {
			logger.error("Failed to generate code.", e);
			throw new CryptographyException(e, "Failed to generate encrypted code");
		}
	}

	@Override
	@Deprecated
	public final ExtendedCode getExtendedCode() throws CryptographyException {
		throw new CryptographyException("Deprecated");
	}

	@Override
	public synchronized List<ExtendedCode> getExtendedCodes(long numberCodes) throws CryptographyException {
		try {

			updateDateOfUse();

			long numberCodesToGenerate = Math.min(getRemainingCodes(), numberCodes);
			if (numberCodesToGenerate == 0) {
				throw new EncoderEmptyException();
			}
			
			Object[] dummy = new Object[]{cryptoFieldsConfig.getFields(this)};
			List<String> code = encoder.generate((int) numberCodesToGenerate, dummy);
			
			
			final List<ExtendedCode> codes = new ArrayList<ExtendedCode>();

			for (int i = 0; i < code.size(); i++) {
				
				List<Object> compositeCode = new ArrayList<Object>();
				
				int numBlock = extendedCodeFactory.getBlockFactories().size();
				for(int j=0; j<numBlock; j++)
				{
					BlockFactory bf = extendedCodeFactory.getBlockFactories().get(j);
					if(bf.getOptions().contains(ExtendedCode.Option.STATIC) && i != 0)
						compositeCode.add(null);
					
					else switch(bf.getType())
					{
						case DMTX:
							String strCode = code.get(i);
							compositeCode.add(strCode);
							break;
						case ASCII_TEXT:
							String strText = "AAA|123"; 
//							String strText = getText(bf);
							
							compositeCode.add(strText);
							break;
						case BITMAP_LOGO:
							int[] bmp = new int[] {0xFF,0x81,0x81,0x81,0x81,0x81,0x81,0xFF,0x00};
//							int[] bmp = getBitmapLogo(bf);
							compositeCode.add(bmp);
							break;						
					}
					
				}

				ExtendedCode xcode = extendedCodeFactory.create(compositeCode);
				codes.add(xcode);
			}


			return codes;

		} catch (GeneratorCapacityException e) {
			throw new EncoderEmptyException("", e);
		} catch (SicpadataException e) {
			logger.error("Failed to generate code.", e);
			throw new CryptographyException(e, "Failed to generate encrypted code");
		}
	}

	@Override
	public synchronized boolean isEncoderEmpty() {
		return getRemainingCodes() <= 0;
	}

	public synchronized long getRemainingCodes() {
		try {
			return encoder.getRemainingCapacity();
		} catch (SicpadataException e) {
			logger.error("", e);
			return -1;
		}
	}

	@Override
	public void setId(int id) {
		super.setId(id);
		encoder.setId(Long.valueOf(id));
	}
}
