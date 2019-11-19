package com.sicpa.standard.gui.screen.machine.component.warning;

import java.util.Date;

public abstract class Message {
	private Date time;
	private String code;
	private String message;
	private boolean removeable;

	public Date getTime() {
		return this.time;
	}

	public void setTime(final Date time) {
		this.time = time;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(final String code) {
		this.code = code;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(final String message) {
		this.message = message;
	}

	public Message() {
		this.code = "";
		this.message = "";
		this.removeable = false;
		this.time = new Date(System.currentTimeMillis());
	}

	public Message(final String code, final String message, final Date time) {
		super();
		this.code = code;
		this.message = message;
		this.time = time;
	}

	public Message(final String code, final String message, final boolean removeable) {
		super();
		this.code = code;
		this.message = message;
		this.removeable = removeable;
		this.time = new Date(System.currentTimeMillis());
	}

	public Message(final String code, final String message) {
		this(code, message, true);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.code == null) ? 0 : this.code.hashCode());
		result = prime * result + ((this.message == null) ? 0 : this.message.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Message other = (Message) obj;
		if (this.code == null) {
			if (other.code != null) {
				return false;
			}
		} else if (!this.code.equals(other.code)) {
			return false;
		}
		if (this.message == null) {
			if (other.message != null) {
				return false;
			}
		} else if (!this.message.equals(other.message)) {
			return false;
		}
		return true;
	}

	public boolean isRemoveable() {
		return this.removeable;
	}

	public void setRemoveable(final boolean removeable) {
		this.removeable = removeable;
	}

	@Override
	public String toString() {
		return "Message [code=" + code + ", message=" + message + "]";
	}

}
