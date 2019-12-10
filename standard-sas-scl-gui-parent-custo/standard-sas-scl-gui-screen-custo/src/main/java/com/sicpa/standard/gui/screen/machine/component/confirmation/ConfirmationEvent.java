package com.sicpa.standard.gui.screen.machine.component.confirmation;

public class ConfirmationEvent {
	public static enum ConfirmationType {
		CONFIRM, CANCEL
	};

	private ConfirmationType confirm;

	public ConfirmationEvent(final ConfirmationType confirm) {
		this.confirm = confirm;
	}

	public ConfirmationType getConfirm() {
		return this.confirm;
	}

	public boolean isConfirmed() {
		return this.confirm == ConfirmationType.CONFIRM;
	}

	public boolean isCanceled() {
		return this.confirm == ConfirmationType.CANCEL;
	}

}
