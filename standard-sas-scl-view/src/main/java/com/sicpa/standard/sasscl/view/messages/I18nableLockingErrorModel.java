package com.sicpa.standard.sasscl.view.messages;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.gui.components.layeredComponents.lock.lockingError.LockingErrorModel;

public class I18nableLockingErrorModel extends LockingErrorModel {

	Map<String, Object[]> paramsMap = new HashMap<String, Object[]>();

	public void addMessage(String key, String message, Object... params) {
		paramsMap.put(message, params);
		super.addMessage(key, message);
	}

	@Override
	public void removeAllMessages() {
		super.removeAllMessages();
		paramsMap.clear();
	}

	@Override
	public Collection<String> getMessages() {
		List<String> translatedMsgs = new ArrayList<String>();

		for (String msgLangKey : super.getMessages()) {
			translatedMsgs.add(MessageFormat.format(Messages.get(msgLangKey), paramsMap.get(msgLangKey)));
		}
		return translatedMsgs;
	}
}
