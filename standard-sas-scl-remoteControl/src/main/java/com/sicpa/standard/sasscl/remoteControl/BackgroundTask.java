package com.sicpa.standard.sasscl.remoteControl;

import java.awt.Frame;

import com.sicpa.standard.common.log.StdLogger;
import com.sicpa.standard.gui.utils.ThreadUtils;

public class BackgroundTask {

	private static StdLogger logger = new StdLogger(BackgroundTask.class);
	protected Runnable task;

	public BackgroundTask(final Runnable task) {
		this.task = task;
	}

	public void start() {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				getView().setBusy(true);
			}
		});
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					BackgroundTask.this.task.run();
				} catch (Exception e) {
					logger.error("", e);
				}
				ThreadUtils.invokeLater(new Runnable() {
					@Override
					public void run() {
						getView().setBusy(false);
					}
				});
			}
		}).start();
	}

	protected RemoteControlFrame getView() {
		for (Frame f : Frame.getFrames()) {
			if (f instanceof RemoteControlFrame) {
				return (RemoteControlFrame) f;
			}
		}
		return null;
	}

}
