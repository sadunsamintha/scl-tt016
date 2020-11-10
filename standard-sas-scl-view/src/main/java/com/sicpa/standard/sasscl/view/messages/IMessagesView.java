package com.sicpa.standard.sasscl.view.messages;

import com.sicpa.standard.gui.screen.machine.component.warning.DefaultMessagesPanel;

public interface IMessagesView {

	public void addError(String code, String text);

	public void addWarning(String code, String text);

	public void removeAllMessages();
	
	public DefaultMessagesPanel getDelegate();

}
