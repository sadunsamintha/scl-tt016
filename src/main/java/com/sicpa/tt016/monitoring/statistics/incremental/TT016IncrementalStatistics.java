package com.sicpa.tt016.monitoring.statistics.incremental;

import java.io.Serializable;

import com.sicpa.standard.sasscl.monitoring.statistics.incremental.IncrementalStatistics;
import com.sicpa.standard.sasscl.monitoring.system.AbstractSystemEvent;
import com.sicpa.tt016.monitoring.system.event.GrossNettCountSystemEvent;

import lombok.Getter;
import lombok.Setter;

public class TT016IncrementalStatistics extends IncrementalStatistics implements Serializable{

    private static final long serialVersionUID = 1L;
    
    @Setter
    @Getter
    int grossCount;

    @Setter
    @Getter
    int nettCount;

    public TT016IncrementalStatistics() {
    }
    
    @Override
    public void handleSystemEvent(final AbstractSystemEvent event) {
    	super.handleSystemEvent(event);
    	if (event instanceof GrossNettCountSystemEvent) {
			if (event.getType().toString().equals("GROSS_NETT_COUNT_CHANGED")) {
				setNettCount(this.getNettCount()+((GrossNettCountSystemEvent) event).getNettCount());
				setGrossCount(this.getGrossCount()+((GrossNettCountSystemEvent) event).getGrossCount());
			}
		}
    	
    }
}

