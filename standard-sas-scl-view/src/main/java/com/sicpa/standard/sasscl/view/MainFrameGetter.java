package com.sicpa.standard.sasscl.view;

import java.awt.Component;
import java.awt.HeadlessException;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.sicpa.standard.client.common.utils.AppUtils;
import com.sicpa.standard.client.common.view.IGUIComponentGetter;
import com.sicpa.standard.client.common.view.mvc.IView;

public class MainFrameGetter implements IGUIComponentGetter {

	private MainFrame frame;
	private MainFrameController viewController;

	private IView<?> startStopView;
	private IView<?> exitView;
	private IView<?> optionsView;
	private IView<?> selectionChangeView;
	private JPanel messagesView;
	private IGUIComponentGetter mainPanelGetter;
	private IView<?> snapshotView;

	@Override
	public Component getComponent() {
		if (frame == null) {
			try {
				if (!AppUtils.isHeadless()) {
					frame = new MainFrame(viewController, startStopView.getComponent(),
							selectionChangeView.getComponent(), exitView.getComponent(), optionsView.getComponent(),
							messagesView, (JComponent) mainPanelGetter.getComponent(), snapshotView.getComponent());
				}
			} catch (HeadlessException e) {
			}
		}
		return frame;
	}

	public void setViewController(final MainFrameController viewController) {
		this.viewController = viewController;
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
}
