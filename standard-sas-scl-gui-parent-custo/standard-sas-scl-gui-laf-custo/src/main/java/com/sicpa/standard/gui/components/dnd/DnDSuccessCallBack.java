package com.sicpa.standard.gui.components.dnd;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTargetContext;

public interface DnDSuccessCallBack {

	public void dropSuccess(DataFlavor flavor,DropTargetContext context,Transferable transferable,Point location);
	
}
