package com.sicpa.standard.sasscl.controller.message;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.messages.ActionEvent;
import com.sicpa.standard.sasscl.messages.ActionMessageType;
import com.sicpa.standard.sasscl.messages.SASDefaultMessagesMapping;

/**
 * responsible to received <code>MessageEvent</code> convert it to a more specific event and post it in the event bus
 * 
 * @author DIelsch
 * 
 */
public class MessagesHandler {

	private static Logger logger = LoggerFactory.getLogger(MessagesHandler.class);

	SASDefaultMessagesMapping messagesMapping;

	public MessagesHandler() {

	}

	/**
	 * Retrieves the ActionMessageType then constructs and sends the event
	 */
	@Subscribe
	public void notifyMessage(final MessageEvent evt) {

		String key = evt.getKey();

		ActionMessageType type = getType(key);

		String paramString = "";
		if (evt.getParams() != null) {
			List<Object> params = new ArrayList<Object>();
			for (Object o : evt.getParams()) {
				params.add(o);
			}
			paramString = params.toString();
		}

		logger.debug("message received {}- params {}- fowarding {}",
				new Object[] { evt.getKey(), paramString, evt.getParams(), type.getActionEventClass() });

		ActionEvent actionEvt = createActionEvent(evt, type.getActionEventClass());
		if (actionEvt != null) {
			post(actionEvt);
		}
	}

	/**
	 * Get the action message type based on the specified message key.
	 * 
	 * @param key
	 *            message key
	 * @return action message type. Action message warning will be returned if no action message matches specified key.
	 */
	private ActionMessageType getType(String key) {
		ActionMessageType type = (ActionMessageType) messagesMapping.getMessageType(key);

		if (type == null) {
			logger.info("no action type defined for :" + key + " defaulting to warning");
			return ActionMessageType.WARNING;
		}

		return type;
	}

	private ActionEvent createActionEvent(MessageEvent sourceEvent, Class<? extends ActionEvent> clazz) {
		if (clazz == null) {
			return null;
		}

		try {
			ActionEvent actionEvt = null;
			actionEvt = clazz.newInstance();

			if (actionEvt != null) {
				actionEvt.setKey(sourceEvent.getKey());
				actionEvt.setParams(sourceEvent.getParams());
				actionEvt.setSource(sourceEvent.getSource());
				return actionEvt;
			}
		} catch (Exception e) {
			logger.error("failed to instantiate action event  class=" + clazz, e);
		}
		return null;
	}

	private void post(ActionEvent evt) {
		EventBusService.post(evt);
	}

	public SASDefaultMessagesMapping getMessagesMapping() {
		return messagesMapping;
	}

	public void setMessagesMapping(SASDefaultMessagesMapping messagesMapping) {
		this.messagesMapping = messagesMapping;
	}

}
