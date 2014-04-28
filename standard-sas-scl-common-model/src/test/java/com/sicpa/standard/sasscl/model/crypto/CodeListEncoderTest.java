package com.sicpa.standard.sasscl.model.crypto;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import junit.framework.Assert;

import org.junit.Test;

import com.sicpa.standard.sasscl.sicpadata.CryptographyException;
import com.sicpa.standard.sasscl.sicpadata.generator.impl.CodeListEncoder;

/**
 * 
 * 
 */
public class CodeListEncoderTest {

	@Test
	public void testCodeListEncoderIsEmpty() {

		CodeListEncoder codeListEncoder = new CodeListEncoder(0,new Random().nextInt(), 2011, 1234,1);

		List<String> codes = new ArrayList<String>();
		codes.add("1");
		codes.add("2");
		codes.add("3");
		codes.add("4");
		codes.add("5");
		codes.add("6");
		codes.add("7");
		codes.add("8");
		codes.add("9");
		codes.add("10");

		codeListEncoder.addEncryptedCodes(codes);

		for (int i = 0; i < 10; i++) {
			try {
				Assert.assertEquals((i + 1) + "", codeListEncoder.getEncryptedCode());
			} catch (CryptographyException e) {
				Assert.fail();
			}
		}

		Assert.assertTrue(codeListEncoder.isEncoderEmpty());

		try {
			codeListEncoder.getEncryptedCode();
			Assert.fail();
		} catch (CryptographyException e) {
			// should throw exception - encoder is empty
		}

	}
}
