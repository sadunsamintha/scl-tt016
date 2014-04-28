package com.sicpa.standard.sasscl.devices.camera.transformer;

import java.awt.image.BufferedImage;

import com.sicpa.standard.client.common.utils.StringMap;
import com.sicpa.standard.gui.utils.ImageUtils;

public class CameraImageRotator implements ICameraImageTransformer {

	public static final String KEY_ROTATION_VALUE = "image.transformer.rotate.angle";

	protected int angle;

	@Override
	public BufferedImage transform(BufferedImage img) {
		if (angle != 0) {
			img = ImageUtils.rotate(img, Math.toRadians(angle));
		}
		return img;
	}

	@Override
	public void init(StringMap map) {
		String value = map.get(KEY_ROTATION_VALUE);
		if (value != null) {
			angle = Integer.parseInt(value);
		}

	}

}
