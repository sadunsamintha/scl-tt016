/**
 * Author	: CDeAlmeida
 * Date		: 22 Jul 2010
 *
 * Copyright (c) 2010 SICPA Security Solutions, all rights reserved.
 *
 */
package com.sicpa.standard.sasscl.sicpadata.generator.impl;

import com.sicpa.standard.common.log.StdLogger;
import com.sicpa.standard.printer.xcode.*;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.sicpadata.CryptographyException;
import com.sicpa.standard.sasscl.sicpadata.generator.AbstractEncoder;
import com.sicpa.standard.sasscl.sicpadata.generator.EncoderEmptyException;
import junit.framework.Assert;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author CDeAlmeida
 * 
 */
public class EncoderNoEncryptionSimulator extends AbstractEncoder {

	private static final long serialVersionUID = 1L;

	private static final StdLogger LOGGER = StdLogger.getLogger(EncoderNoEncryptionSimulator.class);
	

	protected int max;

	protected long sequence;
	
	private ExtendedCodeFactory extendedCodeFactory;


	public EncoderNoEncryptionSimulator(final long batchid,int id, final int min, final int max, int year, long subsystemId, int codeTypeId) {
		super(batchid,id, year, subsystemId, codeTypeId);
		this.max = max;
		this.sequence = min;
		
		if(codeTypeId >= CodeType.ExtendedCodeId){
			ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("config/productionConfig/extended-code.xml");

			this.extendedCodeFactory = (ExtendedCodeFactory) ctx.getBean(String.valueOf(codeTypeId));
			if(extendedCodeFactory == null)
				LOGGER.error("********************EncoderNoEncryptionSimulator: cannot load bean:  {}", codeTypeId);

			Assert.assertNotNull(extendedCodeFactory);
			ctx.close();
			if(extendedCodeFactory instanceof ExtendedCodeFileFactory){
				((ExtendedCodeFileFactory)extendedCodeFactory).setRewindAllowed(true);
			}
			LOGGER.info("********************EncoderNoEncryptionSimulator: bean:  {} loaded from config/productionConfig/extended-code.xml", codeTypeId);
		}
	}

	public EncoderNoEncryptionSimulator(final long batchid,int id, final int min, final int max, int year, int codeTypeId) {
		this(batchid,id, min, max, year, 0, codeTypeId);
	}

	@Override
	public String getEncryptedCode() throws CryptographyException {
		if (this.sequence > this.max) {
			throw new EncoderEmptyException();
		} else {
			updateDateOfUse();
			return String.valueOf(this.sequence++);
		}
	}

	@Override
	public ExtendedCode getExtendedCode() throws CryptographyException {
		
//		LOGGER.info("******************** getExtendedCode");

		if(extendedCodeFactory instanceof ExtendedCodeFileFactory){
			ExtendedCode xCode = ((ExtendedCodeFileFactory)extendedCodeFactory).getNext();
			if(xCode == null)
				throw new EncoderEmptyException();
//			LOGGER.info("******************** got ExtendedCode from file: {}", (String)(xCode.getBlock(0).getData()));
			return xCode;
		}
			
		if (this.sequence > this.max) {
			throw new EncoderEmptyException();
		} else {
			List<Object> compositeCode = new ArrayList<Object>();

			
			int numBlock = extendedCodeFactory.getBlockFactories().size();
			for(int i=0; i<numBlock; i++)
			{
				BlockFactory bf = extendedCodeFactory.getBlockFactories().get(i);
				switch(bf.getType())
				{
					case DMTX:
						String strCode = getEncryptedCode();
						compositeCode.add(strCode);
						break;
					case ASCII_TEXT:
						String strText = getText(bf);
						compositeCode.add(strText);
						break;
					case BITMAP_LOGO:
						long[] bmp = getBitmapLogo(bf);
						compositeCode.add(bmp);
						break;						
					case DSL: // Assuming first param is a datamatrix other are texts of length 6
						String strCodeDsl = getEncryptedCode();
						TextBlockFactory bft= new TextBlockFactory();
						bft.setLength(10);
						for(int k=0; k<((DslBlockFactory)bf).getDslParamNumber()-1; k++)
							strCodeDsl += "|"+getText(bft);
						compositeCode.add(strCodeDsl);
						break;
				}				
			}
			ExtendedCode xcode = extendedCodeFactory.create(compositeCode);

			return xcode;
		}
	}


	private long[] getBitmapLogo(BlockFactory bf) {
		long[] bmp = new long[5];
		
		int height = ((BitmapBlockFactory)bf).getHeight();
		
		long mask = (((long)-1) & ~Long.highestOneBit(-1) >> (64-height-1));
		long vert = Long.highestOneBit(mask) | 1;
		bmp[0] = mask;
		bmp[1] = vert;
		bmp[2] = vert | ((mask >> height/4) & (mask << height/4));
		bmp[3] = vert;		
		bmp[4] = mask;
		
		return bmp;
	}

	private String getText(BlockFactory bf) {
		final int LEN = ((TextBlockFactory)bf).getLength();
		String _text = "023456789BCDFGHJKLMNPQRSTVWXYZ";//bcdfghjklmnpqrstvwxyz"; //abcdefghijklmnopqrstuvwxyz //ABCDEFGHIJKLMNOPQRSTUVWXYZ //0123456789
		
		Random rand = new Random();
		String sr = "";
		while (sr.length() < LEN)
		{
			int _at = Math.abs(rand.nextInt())% (_text.length());
			sr = sr + _text.substring(_at,_at+1);
		}   					

		if(bf.getOptions().contains(Option.TWO_LINES) && !sr.contains("|"))
			sr = sr.substring(0, sr.length()/2) + "|" + sr.substring(sr.length()/2, sr.length());
					
		return sr;
	}

	public long getNumberOfAvailableEncryptedCode() {
		return this.max - this.sequence;
	}

	@Override
	public boolean isEncoderEmpty() {
		return getNumberOfAvailableEncryptedCode() <= 0;
	}

	public int getMax() {
		return this.max;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + max;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		EncoderNoEncryptionSimulator other = (EncoderNoEncryptionSimulator) obj;
		if (max != other.max)
			return false;
		return true;
	}

	public long getSequence() {
		return sequence;
	}
}
