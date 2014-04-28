package com.sicpa.standard.sasscl.monitoring.system.event;

import java.util.Date;

import com.sicpa.standard.sasscl.monitoring.system.AbstractSystemEvent;
import com.sicpa.standard.sasscl.monitoring.system.SystemEventLevel;
import com.sicpa.standard.sasscl.monitoring.system.SystemEventType;

public class OfflineCountingSystemEvent extends AbstractSystemEvent {

	protected static final long serialVersionUID = 6279800035175000295L;

	protected int quantity;
	protected Date from;
	protected Date to;

	/**
	 * 
	 * @param quantity
	 * @param from
	 * @param to
	 */
	public OfflineCountingSystemEvent(int quantity, Date from, Date to) {
		super(SystemEventLevel.INFO, SystemEventType.OFFLINE_COUNTING);
		this.quantity = quantity;
		this.from = from;
		this.to = to;
	}

	@Override
	public String getMessage() {
		return "quantity : " + quantity + " , from : " + this.from.toString() + ", to : " + this.to.toString();
	}

	// getter and setter

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public Date getFrom() {
		return from;
	}

	public void setFrom(Date from) {
		this.from = from;
	}

	public Date getTo() {
		return to;
	}

	public void setTo(Date to) {
		this.to = to;
	}

}
