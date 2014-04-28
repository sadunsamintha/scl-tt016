package com.sicpa.standard.sasscl.view.startstop;

import com.sicpa.standard.client.common.view.mvc.AbstractView;

@SuppressWarnings("serial")
public abstract class AbstractStartStopView extends AbstractView<IStartStopViewListener, StartStopModel> {

	public void fireStart() {
		synchronized (listeners) {
			for (IStartStopViewListener l : listeners) {
				l.start();
			}
		}
	}

	public void fireStop() {
		synchronized (listeners) {
			for (IStartStopViewListener l : listeners) {
				l.stop();
			}
		}
	}
}
