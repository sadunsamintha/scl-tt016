package com.sicpa.standard.gui.screen.machine.component.confirmation;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.EventListenerList;

import com.sicpa.standard.gui.screen.machine.component.confirmation.ConfirmationEvent.ConfirmationType;

public class ConfirmationModel {

	protected String question;
	protected String confirmButtonText;
	protected String cancelButtonText;
	protected List<ConfirmationCallback> callbacks;
	protected boolean confirmButtonEnabled;
	protected EventListenerList listeners;

	public ConfirmationModel() {
		this.listeners = new EventListenerList();
		this.callbacks = new ArrayList<ConfirmationCallback>();
		this.confirmButtonText = "ok";
		this.cancelButtonText = "cancel";
		this.question = "question should be here";
	}

	public void addCallback(final ConfirmationCallback callback) {
		this.callbacks.add(callback);
	}

	public void removeCallback(final ConfirmationCallback callback) {
		this.callbacks.remove(callback);
	}

	public void setQuestion(final String question) {
		this.question = question;
		fireQuestionChanged(question);
	}

	public String getQuestion() {
		return this.question;
	}

	public void setConfirmButtonText(final String confirmButtonText) {
		this.confirmButtonText = confirmButtonText;
		fireConfirmButtonTextChanged(confirmButtonText);
	}

	public String getConfirmButtonText() {
		return this.confirmButtonText;
	}

	public void setCancelButtonText(final String cancelButtonText) {
		this.cancelButtonText = cancelButtonText;
		fireCancelButtonTextChanged(cancelButtonText);
	}

	public String getCancelButtonText() {
		return this.cancelButtonText;
	}

	protected void fireQuestionChanged(final String question) {
		Object[] listeners = this.listeners.getListenerList();
		PropertyChangeEvent e1 = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ConfirmationChangeListener.class) {
				// Lazily create the event:
				if (e1 == null) {
					e1 = new PropertyChangeEvent(this, "question", null, question);
				}
				((ConfirmationChangeListener) listeners[i + 1]).questionTextChanged(e1);
			}
		}
	}

	protected void fireAbort() {
		Object[] listeners = this.listeners.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ConfirmationChangeListener.class) {
				((ConfirmationChangeListener) listeners[i + 1]).abort();
			}
		}
	}

	protected void fireConfirmButtonEnabilityChanged(boolean enable) {
		Object[] listeners = this.listeners.getListenerList();
		PropertyChangeEvent e = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ConfirmationChangeListener.class) {
				if (e == null) {
					e = new PropertyChangeEvent(this, "confirmButtonEnability", null, enable);
				}
				((ConfirmationChangeListener) listeners[i + 1]).confirmButtonEnabilityChanged(e);
			}
		}
	}

	protected void fireConfirmButtonTextChanged(final String confirmButton) {
		Object[] listeners = this.listeners.getListenerList();
		PropertyChangeEvent e = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ConfirmationChangeListener.class) {
				// Lazily create the event:
				if (e == null) {
					e = new PropertyChangeEvent(this, "confirmButton", null, this.confirmButtonText);
				}
				((ConfirmationChangeListener) listeners[i + 1]).confirmButtonTextChanged(e);
			}
		}
	}

	protected void fireCancelButtonTextChanged(final String cancelButton) {
		Object[] listeners = this.listeners.getListenerList();
		PropertyChangeEvent e = null;
		PropertyChangeEvent e2 = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ConfirmationChangeListener.class) {
				// Lazily create the event:
				if (e == null) {
					e = new PropertyChangeEvent(this, "cancelButton", null, cancelButton);
				}
				if (e2 == null) {
					e2 = new PropertyChangeEvent(this, "cancelVisibility", null, isCancelVisible());
				}
				((ConfirmationChangeListener) listeners[i + 1]).cancelButtonTextChanged(e);
				((ConfirmationChangeListener) listeners[i + 1]).cancelButtonVisibilityChanged(e2);
			}
		}
	}

	private void click(final ConfirmationType type) {
		for (ConfirmationCallback callback : this.callbacks) {
			if (callback != null) {
				callback.confirmationTaken(new ConfirmationEvent(type));
			}
		}
	}

	public void confirm() {
		click(ConfirmationType.CONFIRM);
	}

	public void cancel() {
		click(ConfirmationType.CANCEL);
	}

	public void abort() {
		fireAbort();
	}

	public void addConfirmationChangeListener(final ConfirmationChangeListener listener) {
		this.listeners.add(ConfirmationChangeListener.class, listener);
	}

	public void removeConfirmationChangeListener(final ConfirmationChangeListener listener) {
		this.listeners.remove(ConfirmationChangeListener.class, listener);
	}

	public void removeCallbacks() {
		this.callbacks.clear();
	}

	protected void fireAskQuestionChanged(final Boolean ask) {
		Object[] listeners = this.listeners.getListenerList();
		PropertyChangeEvent e1 = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ConfirmationChangeListener.class) {
				// Lazily create the event:
				if (e1 == null) {
					e1 = new PropertyChangeEvent(this, "askQuestion", null, ask);
				}
				((ConfirmationChangeListener) listeners[i + 1]).askQuestion(e1);
			}
		}
	}

	public void ask() {
		fireAskQuestionChanged(true);
	}

	public boolean isCancelVisible() {
		return this.cancelButtonText != null && !this.cancelButtonText.isEmpty();
	}

	public boolean isConfirmButtonEnabled() {
		return confirmButtonEnabled;
	}

	public void setConfirmButtonEnabled(boolean confirmButtonEnability) {
		this.confirmButtonEnabled = confirmButtonEnability;
		fireConfirmButtonEnabilityChanged(confirmButtonEnability);
	}
}
