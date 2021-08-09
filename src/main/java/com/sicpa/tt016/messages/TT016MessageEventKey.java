package com.sicpa.tt016.messages;

public interface TT016MessageEventKey {

    interface ACTIVATION {
        String NO_INK_IN_REFEED_MODE_MSG_CODE = "[ACT_100]";
        String NO_INK_IN_REFEED_MODE = "business.activation.refeed.noink";
        String EXCEPTION_CODE_IN_AGING_MSG_CODE = "[ACT_101]";
        String EXCEPTION_CODE_IN_AGING = "activation.err.aging.code";
    }

    interface D900{
        String PLC_D900_OFFLINE_ERROR_MSG_CODE = "D9C_01";
        String PLC_D900_OFFLINE_ERROR = "VIDI.ERROR.CAMERA_OFFLINE";
    }

    interface AUTOMATEDBEAM {
        String AUTOMATED_BEAM_HEAD_TO_HOME_MSG_CODE = "[ABI_01]";
        String AUTOMATED_BEAM_HEAD_TO_HOME = "plc.motor.sku.height.home";
        String AUTOMATED_BEAM_ADJUST_HEIGHT_MSG_CODE = "[ABI_02]";
        String AUTOMATED_BEAM_ADJUST_HEIGHT = "plc.automated.beam.adjust.height";
        String AUTOMATED_BEAM_HEIGHT_SET_MSG_CODE = "[ABI_03]";
        String AUTOMATED_BEAM_HEIGHT_SET = "plc.automated.beam.height.set";
        String AUTOMATED_BEAM_SAFETY_SENSOR_TRIG_MSG_CODE = "[ABW_01]";
        String AUTOMATED_BEAM_SAFETY_SENSOR_TRIG = "plc.automated.beam.reset.activated";
        String AUTOMATED_BEAM_AWAITING_RESET_MSG_CODE = "[ABI_04]";
        String AUTOMATED_BEAM_AWAITING_RESET = "plc.automated.beam.awaiting.reset";
        String AUTOMATED_BEAM_INVALID_HEIGHT_DETECTED_MSG_CODE = "[ABW_02]";
        String AUTOMATED_BEAM_INVALID_HEIGHT_DETECTED = "plc.motor.invalid.height";
        String AUTOMATED_BEAM_ERROR_STATE_MSG_CODE = "[ABW_03]";
        String AUTOMATED_BEAM_ERROR_STATE = "plc.motor.error.state";
        String AUTOMATED_BEAM_ESTOP_STATE_MSG_CODE = "[ABW_04]";
        String AUTOMATED_BEAM_ESTOP_STATE = "plc.motor.estop.state";
        String AUTOMATED_BEAM_ESTOP_SWITCH_STATE_MSG_CODE = "[ABI_05]";
        String AUTOMATED_BEAM_ESTOP_SWITCH_STATE_ENGAGED = "plc.motor.estop.switch.state.engaged";
        String AUTOMATED_BEAM_ESTOP_SWITCH_STATE_RELEASED_MSG_CODE = "[ABI_06]";
        String AUTOMATED_BEAM_ESTOP_SWITCH_STATE_RELEASED = "plc.motor.estop.switch.state.released";
        String PLC_AUTOMATED_BEAM_POWER_ERROR_MSG_CODE = "[ABE_01]";
        String PLC_AUTOMATED_BEAM_POWER_ERROR = "plc.motor.power.error";
        String PLC_AUTOMATED_BEAM_VELOCITY_ERROR_MSG_CODE = "[ABE_02]";
        String PLC_AUTOMATED_BEAM_VELOCITY_ERROR = "plc.motor.velocity.error";
        String SKU_SELECTION_VIEW_ACTIVE = "sku.selection.view.active";
    }
}
