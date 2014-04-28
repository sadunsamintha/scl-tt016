package com.sicpa.standard.sasscl.view;

import java.awt.Frame;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.view.mvc.IView;
import com.sicpa.standard.client.common.view.screensflow.IScreenGetter;
import com.sicpa.standard.gui.screen.machine.AbstractMachineFrame;

public abstract class AbstractViewFlowController implements IScreenGetter {

	private static Logger logger = LoggerFactory.getLogger(AbstractViewFlowController.class);

	protected MainFrameController viewController;

	protected boolean active;
	protected IView<?> view;

	@Override
	public final void enter() {
		logger.info("entering:" + getClass().getSimpleName());
		active = true;
		displayView();
	}

	protected void displayView() {
		try {
			viewController.replaceMainPanel(getComponent());
		} catch (java.awt.HeadlessException e) {
		}
	}

	@Override
	public JComponent getComponent() {
		return view.getComponent();
	}

	@Override
	public final void leave() {
		logger.info("leaving:" + getClass().getSimpleName());
		active = false;
		hideView();
	}

	protected void hideView() {

	}

	public void setViewController(MainFrameController viewController) {
		this.viewController = viewController;
	}

	protected MainFrame getView() {
		for (Frame f : JFrame.getFrames()) {
			if (f instanceof AbstractMachineFrame) {
				return (MainFrame) f;
			}
		}
		return null;
	}

	public boolean isActive() {
		return active;
	}

	public void setView(IView<?> view) {
		this.view = view;
	}
}
