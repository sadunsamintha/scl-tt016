package com.sicpa.standard.sasscl.view.snapshot;

import java.awt.Frame;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.gui.screen.machine.AbstractMachineFrame;
import com.sicpa.standard.sasscl.controller.view.event.WarningViewEvent;

public class SnapshotViewController implements ISnapshotViewListener {

	private static final Logger logger = LoggerFactory.getLogger(SnapshotViewController.class);

	protected SnapshotViewModel model;

	public SnapshotViewController() {
		model = new SnapshotViewModel();
	}

	@Override
	public void takeSnapshot() {
		if (model.isBusy()) {
			return;
		}

		model.setBusy(true);
		model.notifyModelChanged();
		takeScreenShot();
		model.setBusy(false);
		model.notifyModelChanged();
	}

	public SnapshotViewModel getModel() {
		return model;
	}

	protected void takeScreenShot() {
		try {
			Robot robot = new Robot();
			BufferedImage screenshot = robot.createScreenCapture(getView().getBounds());

			File folder = new File(getFolder());
			folder.mkdirs();

			saveimage(screenshot, folder.getPath() + "/" + createFileName());
			EventBusService.post(new WarningViewEvent("screenshot.taken", true));

		} catch (Exception e) {
			logger.error("fail to take screenshot", e);
		}
	}

	protected String getFolder() {
		return "screenshot";
	}

	protected String createFileName() {
		return new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()) + ".png";
	}

	protected void saveimage(BufferedImage img, String file) throws IOException {
		ImageIO.write(img, "PNG", new File(file));
	}

	protected AbstractMachineFrame getView() {
		for (Frame f : JFrame.getFrames()) {
			if (f instanceof AbstractMachineFrame) {
				return (AbstractMachineFrame) f;
			}
		}
		return null;
	}

}
