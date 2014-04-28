package com.sicpa.standard.sasscl.devices.camera.transformer;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.jdesktop.swingx.graphics.GraphicsUtilities;

import com.sicpa.standard.client.common.utils.StringMap;

public class CropImageTransformer implements ICameraImageTransformer {

	public static final String KEY_X = "image.transformer.crop.x";
	public static final String KEY_Y = "image.transformer.crop.y";
	public static final String KEY_H = "image.transformer.crop.h";
	public static final String KEY_W = "image.transformer.crop.w";

	protected BufferedImage cache;

	protected int x;
	protected int y;
	protected int w;
	protected int h;

	public BufferedImage transform(BufferedImage img) {

		if (w <= 0 || h <= 0) {
			return img;
		}
		if (cache == null) {
			cache = GraphicsUtilities.createCompatibleImage(w, h);
		}

		Graphics2D g = cache.createGraphics();
		g.fillRect(0, 0, w, h);
		g.drawImage(img, -x, -y, null);
		g.dispose();

		return cache;
	}

	@Override
	public void init(StringMap map) {
		x = getParam(map, KEY_X);
		y = getParam(map, KEY_Y);
		w = getParam(map, KEY_W);
		h = getParam(map, KEY_H);
	}

	protected int getParam(StringMap map, String key) {
		String sval = map.get(key);
		if (sval == null) {
			return 0;
		} else {
			return Integer.parseInt(sval);
		}
	}
}
