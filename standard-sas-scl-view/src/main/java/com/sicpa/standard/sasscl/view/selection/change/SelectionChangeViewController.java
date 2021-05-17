package com.sicpa.standard.sasscl.view.selection.change;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.controller.flow.IFlowControl;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.provider.impl.SkuListProvider;
import com.sicpa.standard.sasscl.view.MainFrame;

public class SelectionChangeViewController implements ISelectionChangeViewListener {

	protected SelectionChangeViewModel model;
	protected IFlowControl flowControl;
	protected SkuListProvider skuListProvider;

	protected final List<ApplicationFlowState> enableChangeContextState;

	public SelectionChangeViewController() {
		this(new SelectionChangeViewModel());
	}

	public SelectionChangeViewController(SelectionChangeViewModel model) {
		this.model = model;
		enableChangeContextState = new ArrayList<ApplicationFlowState>(Arrays.asList(
				ApplicationFlowState.STT_CONNECTED, ApplicationFlowState.STT_RECOVERING,
				ApplicationFlowState.STT_CONNECTING, ApplicationFlowState.STT_NO_SELECTION));
	}

	public SelectionChangeViewModel getModel() {
		return model;
	}

	@Override
	public void changeContext() {
		if (skuListProvider.get() == null) {
			EventBusService.post(new MessageEvent(MessageEventKey.ProductionParameters.NONE_AVAILABLE));
		} else {
			flowControl.notifyEnterSelectionScreen();
		}
	}

	protected MainFrame getMainView() {
		for (Frame f : JFrame.getFrames()) {
			if (f instanceof MainFrame) {
				return (MainFrame) f;
			}
		}
		return null;
	}

	@Subscribe
	public void handleApplicationStateChange(ApplicationFlowStateChangedEvent evt) {
		model.setChangedContextEnabled(enableChangeContextState.contains(evt.getCurrentState()));
		model.notifyModelChanged();
	}

	public void setFlowControl(IFlowControl flowControl) {
		this.flowControl = flowControl;
	}

	public void setSkuListProvider(SkuListProvider skuListProvider) {
		this.skuListProvider = skuListProvider;
	}
}
