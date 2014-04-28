package com.sicpa.standard.sasscl.monitoring.system;

import java.io.Serializable;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.monitor.IMonitorObject;
import com.sicpa.standard.util.FieldToString;

public abstract class AbstractSystemEvent implements IMonitorObject, Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger logger= LoggerFactory.getLogger(AbstractSystemEvent.class);

	protected SystemEventLevel level;
	protected Date date;
	protected SystemEventType type;

	public AbstractSystemEvent() {
	}

	public AbstractSystemEvent(final SystemEventLevel level, final SystemEventType type) {
		this.level = level;
		this.type = type;
		this.date = new Date();
	}

	@Override
	public String[] getFields() {
		return FieldToString.getFieldsValue(this);
	}

	@Override
	public void setFields(final String[] fields) {
		try {

			FieldToString.setFieldsValue(this, fields);
		} catch (Exception e) {
			// do not crash but display empty info on the screen
			logger.error("", e);
		}
	}

	public SystemEventLevel getLevel() {
		return this.level;
	}

	public void setLevel(final SystemEventLevel level) {
		this.level = level;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(final Date date) {
		this.date = date;
	}

	public SystemEventType getType() {
		return this.type;
	}

	public void setType(final SystemEventType type) {
		this.type = type;
	}

	public abstract Object getMessage();
}
