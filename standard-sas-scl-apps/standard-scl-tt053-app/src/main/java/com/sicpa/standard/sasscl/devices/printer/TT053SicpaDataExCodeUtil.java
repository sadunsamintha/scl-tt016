package com.sicpa.standard.sasscl.devices.printer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TT053SicpaDataExCodeUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(TT053SicpaDataExCodeUtil.class);

	private static final String STANDARD_EXPORT_FILE = "profiles/TT053/config/printer/exp_bmp.txt";

	private static Long[] exportImg = getImageSrcCode(STANDARD_EXPORT_FILE);

	private static List<StringBuilder> resultList;
	
	private static final int HEX_DECIMAL_RADIX=16;
	private static final int BINARY_DECIMAL_RADIX =2;

	/**
	 * This method for covert to bit map logo
	 *
	 * @return long array
	 */
	public static long[] getExportBitmapLogo() {
		long[] bmp = new long[exportImg.length];

		for (int x = 0; x < exportImg.length; x++) {
			bmp[x] = exportImg[x];
		}

		return bmp;
	}

	/**
	 * This method will retrieve the image source code text files
	 *
	 * @param type
	 * @return values of array with type Long
	 */
	private static Long[] getImageSrcCode(String fileName) {

		Long[] outputArray = null;

		resultList = new ArrayList<>();
		URL url = ClassLoader.getSystemResource(fileName);

		try (BufferedReader br = new BufferedReader(
				new FileReader((url == null) ? new File(fileName) : new File(url.toURI())))) {

			String line = br.readLine();

			while (line != null) {
				if (!line.isEmpty()) {
					rotateReverse90(line);
				}
				line = br.readLine();
			}

			List<Long> outputList = convertResultListToLongArrayList();

			outputArray = new Long[outputList.size()];
			outputArray = outputList.toArray(outputArray);
		} catch (Exception e) {
			logger.error("", e);
		}

		return outputArray;
	}

	/**
	 * this method will convert the ArrayList<StringBuilder> to List<Long>
	 *
	 * @return list of values of type Long
	 */
	private static List<Long> convertResultListToLongArrayList() {
		return resultList.stream().map(sb -> toHexa(sb.toString())).collect(Collectors.toList());
	}

	/**
	 * This method will rotate the String builder text from horizontal to
	 * vertical
	 *
	 * @param line
	 */
	private static void rotateReverse90(String line) {
		StringBuilder sb = new StringBuilder(line);
		sb.reverse();
		char[] letterArray = sb.toString().toCharArray();

		if (resultList.size() == 0) {
			for (char aLetterArray : letterArray) {
				resultList.add(new StringBuilder());
			}
		}

		for (int i = 0; i < letterArray.length; i++) {
			StringBuilder sBuilder = resultList.get(i);
			sBuilder.append(letterArray[i]);
		}
	}

	/**
	 * This method will convert from binary to Long
	 *
	 * @param binary
	 * @return Long
	 */
	private static Long toHexa(String binary) {
		int decimal = Integer.parseInt(binary, BINARY_DECIMAL_RADIX);

		String str = Long.toString(decimal, HEX_DECIMAL_RADIX).toUpperCase();

		return Long.parseLong(str, HEX_DECIMAL_RADIX);
	}
}
