package com.sicpa.standard.sasscl.view.advancedControl.component.leibinger;

import com.sicpa.standard.client.common.view.mvc.AbstractObservableModel;

import java.util.ArrayList;
import java.util.Collection;

public class LeibingerPrinterControlViewModel extends AbstractObservableModel {

	private final Collection<String> printers = new ArrayList<>();

	public void addPrinter(String printerId) {
		synchronized (printers) {
			printers.add(printerId);
		}
	}

	public Collection<String> getPrinters() {
		synchronized (printers) {
			return new ArrayList<>(printers);
		}
	}

	public void reset() {
		synchronized (printers) {
			printers.clear();
		}
	}
}
