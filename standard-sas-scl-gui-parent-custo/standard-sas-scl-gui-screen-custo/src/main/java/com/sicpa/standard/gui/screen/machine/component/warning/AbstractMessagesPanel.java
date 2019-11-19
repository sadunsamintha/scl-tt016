package com.sicpa.standard.gui.screen.machine.component.warning;

import javax.swing.JPanel;

import com.sicpa.standard.gui.screen.machine.impl.SPL.stats.MessageEvent;
import com.sicpa.standard.gui.screen.machine.impl.SPL.stats.MessageEventUpdate;

@SuppressWarnings("serial")
public abstract class AbstractMessagesPanel extends JPanel {
	private MessagesModel model;

	public AbstractMessagesPanel() {
		this(new MessagesModel());
	}

	public AbstractMessagesPanel(final MessagesModel model) {
		if (model == null) {
			setModel(new MessagesModel());
		} else {
			setModel(model);
		}
	}

	protected abstract void modelMessageAdded(MessageEvent evt);

	protected abstract void modelMessageRemove(MessageEvent evt);

	protected abstract void modelReseted();

	protected abstract void modelMessageUpdated(MessageEventUpdate evt);

	public void setModel(final MessagesModel model) {
		if (this.model == model) {
			return;
		}
		this.model = model;
		model.addMessageListener(new MessagesListener() {

			@Override
			public void messageRemoved(final MessageEvent evt) {
				modelMessageRemove(evt);
			}

			@Override
			public void messageAdded(final MessageEvent evt) {
				modelMessageAdded(evt);
			}

			@Override
			public void reseted() {
				modelReseted();
			}

			@Override
			public void messageUpdated(final MessageEventUpdate evt) {
				modelMessageUpdated(evt);
			}
		});
	}

	public MessagesModel getModel() {
		return this.model;
	}
}
