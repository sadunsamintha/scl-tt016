package com.sicpa.standard.sasscl.view;

import java.awt.Component;
import java.awt.HeadlessException;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.sicpa.standard.client.common.utils.AppUtils;
import com.sicpa.standard.client.common.view.IGUIComponentGetter;
import com.sicpa.standard.client.common.view.mvc.IView;
import com.sicpa.standard.sasscl.controller.flow.IFlowControl;
import com.sicpa.standard.sasscl.provider.impl.SkuListProvider;

public class MainFrameGetter implements IGUIComponentGetter {

	protected MainFrame frame;
	protected MainFrameController viewController;

	protected IView<?> startStopView;
	protected IView<?> exitView;


	protected IView<?> optionsView;
	protected IView<?> selectionChangeView;
	protected JPanel messagesView;
	protected IGUIComponentGetter mainPanelGetter;
	protected IView<?> snapshotView;
	protected SkuListProvider skuListProvider;
	protected IFlowControl flowControl;

	@Override
	public Component getComponent() {
		if (frame == null) {
			try {
				if (!AppUtils.isHeadless()) {
					frame = new MainFrame(viewController, startStopView.getComponent(),
							selectionChangeView.getComponent(), exitView.getComponent(), optionsView.getComponent(),
							messagesView, (JComponent) mainPanelGetter.getComponent(), snapshotView.getComponent(), skuListProvider, flowControl);
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

	public void setSkuListProvider(SkuListProvider skuListProvider) {
		this.skuListProvider = skuListProvider;
	}

	public void setFlowControl(IFlowControl flowControl) {
		this.flowControl = flowControl;
	}
	
}
