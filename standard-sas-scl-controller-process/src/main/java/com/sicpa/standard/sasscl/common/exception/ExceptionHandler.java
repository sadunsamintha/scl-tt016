package com.sicpa.standard.sasscl.common.exception;

import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.exception.InitializationRuntimeException;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.monitoring.MonitoringService;
import com.sicpa.standard.sasscl.monitoring.system.SystemEventLevel;
import com.sicpa.standard.sasscl.monitoring.system.SystemEventType;
import com.sicpa.standard.sasscl.monitoring.system.event.BasicSystemEvent;

/**
 * Handler of all unchecked exception
 * 
 * @author DIelsch
 * 
 */
public class ExceptionHandler implements UncaughtExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

	public ExceptionHandler() {
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(final Thread t, final Throwable e) {
		logger.error(e.getMessage(), e);
		if (e instanceof InitializationRuntimeException) {
			handleInitializationRuntimeException((InitializationRuntimeException) e);
		} else {
			handleUncaughException(e);
		}
	}

	protected void handleInitializationRuntimeException(final InitializationRuntimeException e) {
		System.exit(-1);
	}

	protected void handleUncaughException(final Throwable e) {
		MonitoringService.addSystemEvent(new BasicSystemEvent(SystemEventLevel.ERROR,
				SystemEventType.UNCAUGHT_EXCEPTION, e.getMessage()));
		EventBusService.post(new MessageEvent(this, MessageEventKey.FlowControl.UNCAUGHT_EXCEPTION, e
				.getMessage()));
		generateUncaughtErrorReport(e);
	}

	public void generateUncaughtErrorReport(Throwable throwable) {
		File folder = new File(getFolder());
		folder.mkdirs();
		Date d = new Date();
		takeScreenShot(d);
		writeReport(throwable, d);
	}

	protected void takeScreenShot(Date d) {
		try {
			Robot robot = new Robot();
			BufferedImage screenshot = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit()
					.getScreenSize()));

			saveimage(screenshot, createFileNameImage(d));

		} catch (HeadlessException e) {
		} catch (Exception e) {
			logger.error("fail to take screenshot", e);
		}
	}

	protected void writeReport(Throwable throwable, Date d) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new File(createFileNameText(d)));
			throwable.printStackTrace(writer);
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	protected String createFileNameImage(Date d) {
		return createFileName(d, "png");
	}

	protected String createFileNameText(Date d) {
		return createFileName(d, "txt");
	}

	protected String createFileName(Date d, String ext) {
		return getFolder() + "/" + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(d) + "." + ext;
	}

	protected void saveimage(BufferedImage img, String file) throws IOException {
		ImageIO.write(img, "PNG", new File(file));
	}

	protected String getFolder() {
		return "uncaught-error";
	}
}
