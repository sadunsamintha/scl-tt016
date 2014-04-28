package com.sicpa.standard.sasscl.view.messages;


public interface IMessagesView {

	public void addError(String code, String text);

	public void addWarning(String code, String text);

	public void removeAllMessages();

}
