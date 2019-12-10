package com.sicpa.standard.gui.screen.machine.impl.SPL.CustomisationSample;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.sicpa.standard.gui.screen.machine.impl.SPL.stats.StatisticsModel;

public class SampleCustomStatisticsModel extends StatisticsModel {

	public static final String PROPERTY_PRINTER_WARNING = "printerWarning";

	private int printerWarning;

	public SampleCustomStatisticsModel() {
		super();
		this.printerWarning = 0;
	}

	public void setPrinterWarning(final int number) {
		int oldValue = this.printerWarning;
		this.printerWarning = number;
		firePrinterWarningChanged(oldValue, this.printerWarning);
	}

	public void addPrinterWarning(final int number) {
		setPrinterWarning(getPrinterWarning() + number);
	}

	public int getPrinterWarning() {
		return this.printerWarning;
	}

	protected void firePrinterWarningChanged(final int oldValue, final int newValue) {
		Object[] listeners = this.listeners.getListenerList();
		PropertyChangeEvent e = null;
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == PropertyChangeListener.class) {
				if (e == null) {
					e = new PropertyChangeEvent(this, PROPERTY_PRINTER_WARNING, oldValue, newValue);
				}
				((PropertyChangeListener) listeners[i + 1]).propertyChange(e);
			}
		}
	}
}
