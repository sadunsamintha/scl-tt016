package com.sicpa.ttth.remote.utils;

public class ChecksumUtil {
    private static int startingShift = 100;//>0
    private static int modular = 103;//prime number

    public static void initialize( int startShift, int mod)
    {
        startingShift=startShift;
        modular=mod;
    }

    /**
     * Add checksum to string input
     *
     * @param stringInput
     * @return stringInput with its checksum added to the end after dash -
     */
    public static String addChecksumToString(String stringInput)
    {
        String checkSum = calculateCheckSum(stringInput);
        return stringInput + "-" + checkSum;
    }

    /**
     * Calculate the checksum for string input
     *
     * @param stringInput
     * @return checksum of stringInput
     */
    private static String calculateCheckSum(String stringInput)
    {
        int sum=0;
        int i=0;
        for (char ch: stringInput.toCharArray()) {
            sum+= (int) ch *(startingShift+i++);
        }
        int checkSum=sum % modular;
        return formatCheckSum(checkSum);

    }

    /**
     * Validate the stringInput with checksum
     *
     * @param stringInput
     * @return true if checksum is correct, false if incorrect
     */
    public static boolean validateCheckSum(String stringInput)
    {
        String checkSum = stringInput.substring(stringInput.length() - 3);
        String rawStringInput = stringInput.substring(0,stringInput.length() - 4);
        return checkSum.equals(calculateCheckSum(rawStringInput));
    }

    private static String formatCheckSum(int checkSum)
    {
        if(checkSum<10)
            return "00"+checkSum;
        else if(checkSum<100)
            return "0"+ checkSum;
        else
            return ""+checkSum;
    }
}