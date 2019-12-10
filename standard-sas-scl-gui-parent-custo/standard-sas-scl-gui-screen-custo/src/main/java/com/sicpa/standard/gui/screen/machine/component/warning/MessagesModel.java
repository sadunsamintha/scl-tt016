package com.sicpa.standard.gui.screen.machine.component.warning;

import java.util.ArrayList;
import java.util.List;

import com.sicpa.standard.gui.screen.machine.impl.SPL.stats.MessageEvent;
import com.sicpa.standard.gui.screen.machine.impl.SPL.stats.MessageEventUpdate;
import com.sicpa.standard.gui.utils.ThreadUtils;

public class MessagesModel {

	protected final List<Message> messages = new ArrayList<Message>();
	protected final List<MessagesListener> listeners = new ArrayList<MessagesListener>();

	public MessagesModel() {
	}

	public void addMessage(final Message message) {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				messages.add(message);
				fireMessageAdded(message);
			}
		});
	}

	public void addOrUpdateMessage(final Message message) {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (!messages.contains(message)) {
					addMessage(message);
				} else {

					int index = messages.indexOf(message);
					Message toUpdate = messages.remove(index);

					toUpdate.setTime(message.getTime());
					toUpdate.setMessage(message.getMessage());
					messages.add(toUpdate);

					fireMessageUpdated(index, toUpdate);
				}
			}
		});
	}

	public void removeMessage(final Message message) {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				while (messages.remove(message)) {
					fireMessageRemoved(message);
				}
			}
		});
	}

	public void removeMessage(final String key) {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				List<Message> toRemove = new ArrayList<Message>();
				for (Message m : messages) {
					if (m.getCode().equals(key)) {
						toRemove.add(m);
					}
				}
				for (Message m : toRemove) {
					messages.remove(m);
					fireMessageRemoved(m);
				}
			}
		});
	}

	public void addMessageListener(final MessagesListener listener) {
		synchronized (listeners) {
			this.listeners.add(listener);
		}
	}

	public void removeMessageListener(final MessagesListener listener) {
		synchronized (listeners) {
			this.listeners.remove(listener);
		}
	}

	protected void fireMessageAdded(final Message message) {
		synchronized (listeners) {
			MessageEvent e = new MessageEvent(message);
			for (MessagesListener l : listeners) {
				l.messageAdded(e);
			}
		}
	}

	protected void fireMessageUpdated(int index, final Message message) {
		synchronized (listeners) {
			MessageEventUpdate e = new MessageEventUpdate(message, index);
			for (MessagesListener l : listeners) {
				l.messageUpdated(e);
			}
		}
	}

	protected void fireMessageRemoved(final Message message) {
		synchronized (listeners) {
			MessageEvent e = new MessageEvent(message);
			for (MessagesListener l : listeners) {
				l.messageRemoved(e);
			}
		}
	}

	protected void fireReset() {
		synchronized (listeners) {
			for (MessagesListener l : listeners) {
				l.reseted();
			}
		}
	}

	public void reset() {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				messages.clear();
				fireReset();
			}
		});
	}

	public List<Message> getMessages() {
		return this.messages;
	}
}
