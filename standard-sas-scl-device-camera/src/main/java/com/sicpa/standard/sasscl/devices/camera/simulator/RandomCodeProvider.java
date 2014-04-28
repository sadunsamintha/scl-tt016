package com.sicpa.standard.sasscl.devices.camera.simulator;

import java.util.Random;

public class RandomCodeProvider implements ICodeProvider {

	/**
	 * Camera code's length
	 */
	protected int LENGTH_OF_CODE = 16;

	@Override
	public String requestCode() {
		return generateCodes(LENGTH_OF_CODE, null);
	}

	/**
	 * Generate random numeric code
	 * 
	 * @param length
	 *            the length of the string should be
	 * @param prefix
	 *            the code will start with the specified prefix
	 * @return an generated alphanumeric with specified length
	 */
	protected String generateCodes(final int length, final String prefix) {
		// the string will have combination of these chars only
		
		//HAS TO BE NUMERIC TO MATCH THE AcceptAllAuthenticator
		String block = "123456789";
		Random random = new Random();

		String code = "";
		int codeLen = 0;

		if (prefix == null || prefix.isEmpty()) {
			codeLen = length;
		} else {
			codeLen = length - prefix.length();
			code = prefix;
		}

		for (int i = 0; i < codeLen; i++) {
			code += block.charAt(random.nextInt(block.length()));
		}
		return code;
	}

}
