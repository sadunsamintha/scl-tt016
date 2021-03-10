package com.sicpa.standard.sasscl.devices.d900;

public class D900CameraNewCodeEvent extends D900CameraCodeEvent{
    public D900CameraNewCodeEvent(String code) {
        super(code);
    }

    @Override
    public String toString() {
        return "D900CameraNewCodeEvent{" + "code=" + getCode() + '}';
    }
}
