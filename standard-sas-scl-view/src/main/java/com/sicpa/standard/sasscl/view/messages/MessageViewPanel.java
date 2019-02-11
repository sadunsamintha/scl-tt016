package com.sicpa.standard.sasscl.view.messages;

import java.awt.event.MouseEvent;

import com.sicpa.standard.gui.screen.machine.component.warning.DefaultMessagesPanel;
import com.sicpa.standard.gui.screen.machine.component.warning.Message;

import net.miginfocom.swing.MigLayout;

public class MessageViewPanel extends DefaultMessagesPanel {
	
	int lastX=0, lastY=0;
	long timeMouseDown=0;
	int dClkRes = 400;
	
	public MessageViewPanel() {
		super();
	}
	
	protected void messageLogMouseReleased(final MouseEvent evt) {
		if (isDoubleClick(evt)) {
			Message msg = (Message) messageLog.getSelectedValue();
			if (msg == null || !msg.isRemoveable()) {
				return;
			}
			getModel().removeMessage(msg);
			lastX = 0;
			lastY = 0;
			timeMouseDown = 0;
		}
	}
	
	private boolean isDoubleClick(MouseEvent evt) {
		if ((Math.abs(evt.getX()-lastX) < 10) && 
				(Math.abs(evt.getY()-lastY) < 10) && 
				((evt.getWhen()-timeMouseDown) < dClkRes)) {
			return true;
		}else {
			timeMouseDown = evt.getWhen();
	        lastX=evt.getX();
	        lastY=evt.getY();
			return false;
		}
	}
}
