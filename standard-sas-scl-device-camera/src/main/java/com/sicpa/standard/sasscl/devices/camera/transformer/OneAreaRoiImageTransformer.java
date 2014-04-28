package com.sicpa.standard.sasscl.devices.camera.transformer;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.utils.StringMap;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.sasscl.devices.camera.ICameraAdaptor;

public class OneAreaRoiImageTransformer implements IRoiCameraImageTransformer {

	private static final Logger logger = LoggerFactory.getLogger(OneAreaRoiImageTransformer.class);

	protected String aliasROI_StartRow;
	protected String aliasROI_x;
	protected String aliasROI_y;
	protected String aliasROI_w;
	protected String aliasROI_h;

	protected boolean roiInit = false;
	protected ICameraAdaptor camera;

	protected Rectangle roi;

	@Override
	public BufferedImage transform(BufferedImage img) {
		if (!roiInit) {
			initROI();
			roiInit = true;
		}

		if (roi != null) {
			Graphics2D g = img.createGraphics();
			g.setColor(SicpaColor.BLUE_MEDIUM);
			g.drawRect(roi.x, roi.y, roi.width, roi.height);
			g.dispose();
		}
		return img;
	}

	protected void initROI() {
		try {

			int startRow = (int) Float.parseFloat(camera.readParameter(aliasROI_StartRow));

			int x = (int) Float.parseFloat(camera.readParameter(aliasROI_x));
			int y = (int) Float.parseFloat(camera.readParameter(aliasROI_y));
			int w = (int) Float.parseFloat(camera.readParameter(aliasROI_w));
			int h = (int) Float.parseFloat(camera.readParameter(aliasROI_h));

			roi = new Rectangle(x, y - startRow, w, h);

		} catch (Exception e) {
			logger.error("", e);
		}
	}

	@Override
	public void init(StringMap map) {

	}

	public void setAliasROI_StartRow(String aliasROI_StartRow) {
		this.aliasROI_StartRow = aliasROI_StartRow;
	}

	public void setAliasROI_x(String aliasROI_x) {
		this.aliasROI_x = aliasROI_x;
	}

	public void setAliasROI_y(String aliasROI_y) {
		this.aliasROI_y = aliasROI_y;
	}

	public void setAliasROI_w(String aliasROI_w) {
		this.aliasROI_w = aliasROI_w;
	}

	public void setAliasROI_h(String aliasROI_h) {
		this.aliasROI_h = aliasROI_h;
	}

	public void setCamera(ICameraAdaptor camera) {
		this.camera = camera;
	}
}