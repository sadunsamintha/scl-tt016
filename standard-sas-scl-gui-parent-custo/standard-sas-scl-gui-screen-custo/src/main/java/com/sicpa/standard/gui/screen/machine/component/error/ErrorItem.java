package com.sicpa.standard.gui.screen.machine.component.error;
@Deprecated
public class ErrorItem {

	private String title;
	private String text;
	private ErrorType type;
	private String key;

	public ErrorItem(final String title, final String text, final ErrorType type, final String key) {

		if (key == null) {
			throw new IllegalArgumentException("key cannot be null");
		}

		this.key = key;
		this.text = text;
		this.title = title;
		this.type = type;
	}

	@Override
	public String toString() {
		return this.title + " " + this.text;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public String getText() {
		return this.text;
	}

	public void setText(final String text) {
		this.text = text;
	}

	public ErrorType getType() {
		return this.type;
	}

	public void setType(final ErrorType type) {
		this.type = type;
	}

	public String getKey() {
		return this.key;
	}

	public void setKey(final String key) {
		this.key = key;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ErrorItem other = (ErrorItem) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}
}
