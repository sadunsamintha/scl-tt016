package com.sicpa.tt016.monitoring.system.event;

import com.sicpa.standard.sasscl.monitoring.system.AbstractSystemEvent;
import com.sicpa.standard.sasscl.monitoring.system.SystemEventLevel;
import com.sicpa.standard.sasscl.monitoring.system.SystemEventType;

import lombok.Getter;
import lombok.Setter;

public class GrossNettCountSystemEvent extends AbstractSystemEvent{
	
	private static final long serialVersionUID = 1L;
	@Getter
	@Setter
	private Integer grossCount;
	
	@Getter
	@Setter
	private Integer nettCount; 
	
	public static final SystemEventType GROSS_NETT_COUNT_CHANGED = new SystemEventType("GROSS_NETT_COUNT_CHANGED");
	
	public GrossNettCountSystemEvent(final Integer nettCount, final Integer grossCount) {
		super(SystemEventLevel.INFO, GROSS_NETT_COUNT_CHANGED);
		this.nettCount = nettCount;
		this.grossCount = grossCount;
	}
	
	public GrossNettCountSystemEvent(final Integer nettCount, final Integer grossCount, final SystemEventType type) {
		super(SystemEventLevel.INFO, type);
		this.nettCount = nettCount;
		this.grossCount = grossCount;
	}

	@Override
	public Integer getMessage() {
		return this.nettCount;
	}
	
}
