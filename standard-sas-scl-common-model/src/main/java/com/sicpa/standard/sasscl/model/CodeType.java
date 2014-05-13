package com.sicpa.standard.sasscl.model;

import java.io.Serializable;

import javax.swing.ImageIcon;

import com.sicpa.standard.sasscl.model.custom.Customizable;

public class CodeType extends Customizable implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final long ExtendedCodeId = 100;
	
	protected long id;
	protected String description;
	protected ImageIcon image;

	public CodeType(final long id) {
		super();
		this.id = id;
	}

	public long getId() {
		return this.id;
	}

	public void setId(final long id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
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
		CodeType other = (CodeType) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CodeType [id=" + id + ", description=" + description + "]";
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ImageIcon getImage() {
		return image;
	}

	public void setImage(ImageIcon image) {
		this.image = image;
	}
}
