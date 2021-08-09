package com.sicpa.tt016.monitoring.mbean.mapping;

public class LegacyTrilightStatusIdProvider {

    private static int LIGHT_OFF = 0;
    private static int LIGHT_ON = 1;
    private static int LIGHT_BLINKING = 2;

    public static int getTrilightStatus(int greenLight, int yellowLight, int redLight) {
        
        if (greenLight == LIGHT_OFF && yellowLight == LIGHT_OFF && redLight == LIGHT_OFF) {
            return 0;   // All lights off
        } else if ((greenLight == LIGHT_ON && yellowLight == LIGHT_OFF && redLight == LIGHT_OFF)
                || (greenLight == LIGHT_BLINKING && yellowLight == LIGHT_OFF && redLight == LIGHT_OFF)) {
            return 1;   // Green
        } else if ((greenLight == LIGHT_OFF && yellowLight == LIGHT_OFF && redLight == LIGHT_ON)
                || (greenLight == LIGHT_OFF && yellowLight == LIGHT_OFF && redLight == LIGHT_BLINKING)) {
            return 2;   // Red
        } else if ((greenLight == LIGHT_OFF && yellowLight == LIGHT_ON && redLight == LIGHT_OFF)
                || (greenLight == LIGHT_OFF && yellowLight == LIGHT_BLINKING && redLight == LIGHT_OFF)) {
            return 3;   // Yellow
        } else if ((greenLight == LIGHT_ON && yellowLight == LIGHT_ON && redLight == LIGHT_OFF)) {
            return 4;   // Green and Yellow
        } else if ((greenLight == LIGHT_ON && yellowLight == LIGHT_BLINKING && redLight == LIGHT_OFF)) {
            return 5;   // Green blinking and yellow blinking
        } else if ((greenLight == LIGHT_OFF && yellowLight == LIGHT_ON && redLight == LIGHT_ON)) {
            return 6;   // Red and yellow
        } else if ((greenLight == LIGHT_OFF && yellowLight == LIGHT_BLINKING && redLight == LIGHT_ON)) {
            return 7;   // Red blinking and yellow blinking
        }

        return -1; // uncovered condition.
    }
}
