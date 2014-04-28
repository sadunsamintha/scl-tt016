package com.sicpa.standard.sasscl.view;

import java.awt.Component;
import java.awt.HeadlessException;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.sicpa.standard.client.common.utils.AppUtils;
import com.sicpa.standard.client.common.view.IGUIComponentGetter;
import com.sicpa.standard.client.common.view.mvc.IView;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.SelectionFlowViewFactory;

public class MainFrameGetter implements IGUIComponentGetter {

	protected MainFrame frame;
	protected MainFrameController viewController;
	protected SelectionFlowViewFactory viewFactory;

	protected IView<?> startStopView;
	protected IView<?> exitView;
	protected IView<?> optionsView;
	protected IView<?> selectionChangeView;
	protected JPanel messagesView;
	protected IGUIComponentGetter mainPanelGetter;
	protected IView<?> snapshotView;
	private boolean fingerPrintLogin;

	@Override
	public Component getComponent() {
		if (this.frame == null) {
			try {
				if (!AppUtils.isHeadless()) {
					this.frame = new MainFrame(this.viewController, startStopView.getComponent(),
							selectionChangeView.getComponent(), exitView.getComponent(), optionsView.getComponent(),
							messagesView, (JComponent) mainPanelGetter.getComponent(), snapshotView.getComponent(),
							fingerPrintLogin);
				}
			} catch (HeadlessException e) {
			}
		}
		return this.frame;
	}

	public void setViewController(final MainFrameController viewController) {
		this.viewController = viewController;
	}

	public void setViewFactory(SelectionFlowViewFactory viewFactory) {
		this.viewFactory = viewFactory;
	}

	public void setStartStopView(IView<?> startStopView) {
		this.startStopView = startStopView;
	}

	public void setExitView(IView<?> exitView) {
		this.exitView = exitView;
	}

	public void setOptionsView(IView<?> optionsView) {
		this.optionsView = optionsView;
	}

	public void setSelectionChangeView(IView<?> selectionChangeView) {
		this.selectionChangeView = selectionChangeView;
	}

	public void setMessagesView(JPanel messagesView) {
		this.messagesView = messagesView;
	}

	public void setMainPanelGetter(IGUIComponentGetter mainPanelGetter) {
		this.mainPanelGetter = mainPanelGetter;
	}

	public void setSnapshotView(IView<?> snapshotView) {
		this.snapshotView = snapshotView;
	}

	public boolean isFingerPrintLogin() {
		return fingerPrintLogin;
	}

	public void setFingerPrintLogin(boolean fingerPrintLogin) {
		this.fingerPrintLogin = fingerPrintLogin;
	}
}
