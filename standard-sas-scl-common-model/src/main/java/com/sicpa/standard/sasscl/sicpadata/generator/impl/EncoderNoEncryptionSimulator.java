/**
 * Author	: CDeAlmeida
 * Date		: 22 Jul 2010
 *
 * Copyright (c) 2010 SICPA Security Solutions, all rights reserved.
 *
 */
package com.sicpa.standard.sasscl.sicpadata.generator.impl;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.sicpa.standard.printer.xcode.BitmapBlockFactory;
import com.sicpa.standard.printer.xcode.BlockFactory;
import com.sicpa.standard.printer.xcode.ExtendedCode;
import com.sicpa.standard.printer.xcode.ExtendedCode.Option;
import com.sicpa.standard.printer.xcode.ExtendedCodeFactory;
import com.sicpa.standard.printer.xcode.TextBlockFactory;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.sicpadata.CryptographyException;
import com.sicpa.standard.sasscl.sicpadata.generator.AbstractEncoder;
import com.sicpa.standard.sasscl.sicpadata.generator.EncoderEmptyException;

/**
 * @author CDeAlmeida
 * 
 */
public class EncoderNoEncryptionSimulator extends AbstractEncoder {

	private static final long serialVersionUID = 1L;

	protected int max;

	protected long sequence;
	
	private ExtendedCodeFactory extendedCodeFactory;


	public EncoderNoEncryptionSimulator(final long batchid,int id, final int min, final int max, int year, long subsystemId, int codeTypeId) {
		super(batchid,id, year, subsystemId, codeTypeId);
		this.max = max;
		this.sequence = min;
		
		if(codeTypeId >= CodeType.ExtendedCodeId){
			ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("extended-code.xml");
			
			this.extendedCodeFactory = (ExtendedCodeFactory)ctx.getBean(String.valueOf(codeTypeId));
			Assert.assertNotNull(extendedCodeFactory);
			ctx.close();
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
						int[] bmp = getBitmapLogo(bf);
						compositeCode.add(bmp);
						break;						
				}
				
			}
			ExtendedCode xcode = extendedCodeFactory.create(compositeCode);

			return xcode;
		}
	}

	private int[] getBitmapLogo(BlockFactory bf) {
		int bmp[] = new int[] {0xFF,0x81,0x81,0x81,0x81,0x81,0x81,0xFF,0x00};
		int height = ((BitmapBlockFactory)bf).getHeight();
		for(int i=0; i<bmp.length-1;i++)
			bmp[i] |= ((this.sequence-1) >> i) & 0xFF;
		
		return bmp;
	}

	private String getText(BlockFactory bf) {
		String text = String.valueOf(this.sequence-1);
		while(text.length() < 6)
			text = "0" + text;
		
		if(bf.getOptions().contains(Option.TWO_LINES) && !text.contains("|"))
			text = text.substring(0, text.length()/2) + "|" + text.substring(text.length()/2, text.length());
					
		return text;
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
