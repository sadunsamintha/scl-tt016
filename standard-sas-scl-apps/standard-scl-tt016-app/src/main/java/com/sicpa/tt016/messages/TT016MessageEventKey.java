package com.sicpa.tt016.messages;

public interface TT016MessageEventKey {

    interface ACTIVATION {
        String NO_INK_IN_REFEED_MODE_MSG_CODE = "[ACT_100]";
        String NO_INK_IN_REFEED_MODE = "business.activation.refeed.noink";
    }

    interface AUTOMATEDBEAM {
        String AUTOMATED_BEAM_HEAD_TO_HOME_MSG_CODE = "[PLC_79]";
        String AUTOMATED_BEAM_HEAD_TO_HOME = "plc.motor.sku.height.home";
        String AUTOMATED_BEAM_ADJUST_HEIGHT_MSG_CODE = "[PLC_80]";
        String AUTOMATED_BEAM_ADJUST_HEIGHT = "plc.automated.beam.adjust.height";
        String AUTOMATED_BEAM_HEIGHT_SET_MSG_CODE = "[PLC_81]";
        String AUTOMATED_BEAM_HEIGHT_SET = "plc.automated.beam.height.set";
        String AUTOMATED_BEAM_SAFETY_SENSOR_TRIG_MSG_CODE = "[PLC_82]";
        String AUTOMATED_BEAM_SAFETY_SENSOR_TRIG = "plc.automated.beam.reset.activated";
        String AUTOMATED_BEAM_AWAITING_RESET_MSG_CODE = "[PLC_83]";
        String AUTOMATED_BEAM_AWAITING_RESET = "plc.automated.beam.awaiting.reset";
        String AUTOMATED_BEAM_INVALID_HEIGHT_DETECTED_MSG_CODE = "[PLC_84]";
        String AUTOMATED_BEAM_INVALID_HEIGHT_DETECTED = "plc.motor.invalid.height";
        String SKU_SELECTION_VIEW_ACTIVE = "sku.selection.view.active";
    }
}
