package com.sicpa.standard.sasscl.view.messages;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.messages.IMessageCodeMapper;
import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.controller.view.event.ErrorViewEvent;
import com.sicpa.standard.sasscl.controller.view.event.WarningViewEvent;

public class MessagesViewController {

	private static final Logger logger = LoggerFactory.getLogger(MessagesViewController.class);

	protected IMessageCodeMapper messageCodeMapper;
	protected IMessagesView view;
	protected final List<ApplicationFlowState> statesRemoveMessages = new ArrayList<ApplicationFlowState>();

	public MessagesViewController() {
		statesRemoveMessages.add(ApplicationFlowState.STT_STARTING);
	}

	protected String getFullCode(final String langKey, String codeExt) {
		String code = this.messageCodeMapper.getMessageCode(langKey);
		if (codeExt != null) {
			code += " " + codeExt;
		}
		return code;
	}

	@Subscribe
	public void addWarning(final WarningViewEvent evt) {
		addWarning(evt.getMessageLangKey(), evt.getCodeExt(), evt.getParams());
	}

	@Subscribe
	public void addError(final ErrorViewEvent evt) {
		// the try catch here is needed to avoid an infinite loop:
		// in case something bad happens in any subscriber a message is sent,
		// so if something goes wrong here a message would be sent and infinite loop would be created
		try {
			addError(evt.getMessageLangKey(), evt.getCodeExt(), evt.getParams());
		} catch (Exception e) {
			logger.error("error adding error on message view:" + evt.getMessageLangKey(), e);
		}
	}

	public void addError(final String langKey, String codeExt, Object... params) {
		view.addError(getFullCode(langKey, codeExt), Messages.format(langKey, params));
	}

	public void addWarning(final String langKey, String codeExt, Object... params) {
		view.addWarning(getFullCode(langKey, codeExt), Messages.format(langKey, params));
	}

	public void setMessageCodeMapper(IMessageCodeMapper messageCodeMapper) {
		this.messageCodeMapper = messageCodeMapper;
	}

	public void setView(IMessagesView view) {
		this.view = view;
	}

	@Subscribe
	public void processStateChanged(ApplicationFlowStateChangedEvent evt) {
		if (statesRemoveMessages.contains(evt.getCurrentState())) {
			view.removeAllMessages();
		}
	}
}
