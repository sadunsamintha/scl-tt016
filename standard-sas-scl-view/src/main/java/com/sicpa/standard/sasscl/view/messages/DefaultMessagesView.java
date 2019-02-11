package com.sicpa.standard.sasscl.view.messages;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import com.sicpa.standard.gui.screen.machine.component.warning.DefaultMessagesPanel;
import com.sicpa.standard.gui.screen.machine.component.warning.Error;
import com.sicpa.standard.gui.screen.machine.component.warning.Warning;

@SuppressWarnings("serial")
public class DefaultMessagesView extends JPanel implements IMessagesView {

	protected DefaultMessagesPanel delegate;

	public DefaultMessagesView() {
		initGUI();
	}

	protected void initGUI() {
		setLayout(new BorderLayout());
		add(getDelegate());
	}

	public DefaultMessagesPanel getDelegate() {
		if (delegate == null) {
			delegate = new MessageViewPanel();
		}
		return delegate;
	}

	@Override
	public void addError(String code, String text) {
		Error error = new Error(code, text);
		error.setRemoveable(true);
		delegate.getModel().addOrUpdateMessage(error);
	}

	@Override
	public void addWarning(String code, String text) {
		delegate.getModel().addOrUpdateMessage(new Warning(code, text, true));
	}

	@Override
	public void removeAllMessages() {
		delegate.getModel().reset();
	}
}
