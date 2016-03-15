package com.sicpa.standard.sasscl.devices.camera.simulator;

import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;

public interface CameraSimuCodeTransformer extends Function<String, Pair<Boolean, String>> {
}