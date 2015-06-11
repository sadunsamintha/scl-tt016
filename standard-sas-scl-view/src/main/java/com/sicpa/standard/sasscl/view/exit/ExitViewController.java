package com.sicpa.standard.sasscl.view.exit;

import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_CONNECTED;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_CONNECTING;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_DISCONNECTING_ON_PARAM_CHANGED;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_NO_SELECTION;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_RECOVERING;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_SELECT_NO_PREVIOUS;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_SELECT_WITH_PREVIOUS;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_STARTING;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.view.question.Answer;
import com.sicpa.standard.client.common.view.question.QuestionEvent;
import com.sicpa.standard.common.util.Messages;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.sasscl.common.log.OperatorLogger;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.controller.flow.IFlowControl;
import com.sicpa.standard.sasscl.devices.remote.IRemoteServer;
import com.sicpa.standard.sasscl.event.LockFullScreenEvent;
import com.sicpa.standard.sasscl.event.UnlockFullScreenEvent;
import com.sicpa.standard.sasscl.view.MainFrame;

public class ExitViewController implements IExitViewListener {

	protected final List<ApplicationFlowState> enableExitState;

	protected ExitViewModel model;

	protected IFlowControl flowControl;

	protected IRemoteServer remoteServer;

	public ExitViewController() {
		this(new ExitViewModel());
	}

	public ExitViewController(ExitViewModel model) {
		this.model = model;
		// Arrays.asList() return an immutable list, so make sure it can be modified in customisation
		enableExitState = new ArrayList<ApplicationFlowState>(Arrays.asList(STT_NO_SELECTION, STT_CONNECTING,
				STT_RECOVERING, STT_CONNECTED, STT_SELECT_WITH_PREVIOUS, STT_SELECT_NO_PREVIOUS,
				STT_DISCONNECTING_ON_PARAM_CHANGED, STT_STARTING));
	}

	/**
	 * Exits from the application and closes it.
	 */
	@Override
	public void exit() {
		EventBusService.post(new LockFullScreenEvent());

		Answer exit = new Answer(Messages.get("question.exit.button.yes"), new Runnable() {
			@Override
			public void run() {
				OperatorLogger.log("Exit Application");
				flowControl.notifyExitApplication();
			}
		});
		exit.setBackgroundColor(SicpaColor.RED);

		Answer cancel = new Answer(Messages.get("question.exit.button.no"), new Runnable() {
			@Override
			public void run() {
				EventBusService.post(new UnlockFullScreenEvent());
			}
		});

		String questionText;
		if (remoteServer.isConnected()) {
			questionText = Messages.get("question.exit.text");
		} else {
			questionText = Messages.get("question.exit.textwithwarning");
		}
		QuestionEvent evt = new QuestionEvent(questionText, cancel, exit);
		EventBusService.post(evt);
	}

	protected MainFrame getMainView() {
		for (Frame f : JFrame.getFrames()) {
			if (f instanceof MainFrame) {
				return (MainFrame) f;
			}
		}
		return null;
	}

	/**
	 * Modifies the model according to the event received.
	 * 
	 * @param evt
	 *            Event that contains the information to modify the model.
	 */
	@Subscribe
	public void processStateChanged(final ApplicationFlowStateChangedEvent evt) {
		model.setExitButtonEnabled(enableExitState.contains(evt.getCurrentState()));
		model.notifyModelChanged();
	}

	public ExitViewModel getModel() {
		return model;
	}

	public void setFlowControl(IFlowControl flowControl) {
		this.flowControl = flowControl;
	}

	public void setRemoteServer(IRemoteServer remoteServer) {
		this.remoteServer = remoteServer;
	}
}
