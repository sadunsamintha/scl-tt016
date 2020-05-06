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


public class TTTHDataExCodeUtil {

  private static final Logger logger = LoggerFactory.getLogger(TTTHDataExCodeUtil.class);

  private static final String STANDARD_TH_FILE = "profiles/TTTH-SCL/config/printer/th_bmp.txt";

  private static Long[] thImg = getImageSrcCode(STANDARD_TH_FILE);

  private static List<StringBuilder> resultList;

  private static final int HEX_DECIMAL_RADIX = 16;

  private static final int BINARY_DECIMAL_RADIX = 2;

  public static long[] getThBitmapLogo() {
    long[] bmp = new long[thImg.length];

    for (int x = 0; x < thImg.length; x++) {
      bmp[x] = thImg[x];
    }

    return bmp;
  }

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

  private static List<Long> convertResultListToLongArrayList() {
    return resultList.stream().map(sb -> toHexa(sb.toString())).collect(Collectors.toList());
  }

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

  private static Long toHexa(String binary) {
    int decimal = Integer.parseInt(binary, BINARY_DECIMAL_RADIX);

    String str = Long.toString(decimal, HEX_DECIMAL_RADIX).toUpperCase();

    return Long.parseLong(str, HEX_DECIMAL_RADIX);
  }

}
