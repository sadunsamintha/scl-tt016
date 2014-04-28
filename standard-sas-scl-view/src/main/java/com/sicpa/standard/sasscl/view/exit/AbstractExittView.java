package com.sicpa.standard.sasscl.view.exit;

import com.sicpa.standard.client.common.view.mvc.AbstractView;

@SuppressWarnings("serial")
public abstract class AbstractExittView extends AbstractView<IExitViewListener, ExitViewModel> {

	protected IExitViewListener controller;

	/**
	 * Invokes to the method that deals with exiting from the application when the button exit has been pressed by the
	 * user. This method is implemented in the controller.
	 */
	protected void fireExit() {
		for (IExitViewListener l : listeners) {
			l.exit();
		}
	}
}
