package com.sicpa.standard.sasscl.devices.camera.transformer;

import java.awt.image.BufferedImage;

import com.sicpa.standard.client.common.utils.StringMap;

public interface ICameraImageTransformer {

	BufferedImage transform(BufferedImage img);

	void init(StringMap map);

}
