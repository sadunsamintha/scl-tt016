package com.sicpa.standard.sasscl.wizard.view.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class TransferableAction implements Transferable {

	public Class<?> clazz;

	public TransferableAction(Class<?> clazz) {
		this.clazz = clazz;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] { new ActionDataFlavor() };
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return flavor instanceof ActionDataFlavor;
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (flavor instanceof ActionDataFlavor) {
			return clazz;
		} else {
			throw new UnsupportedFlavorException(flavor);
		}
	}
}
