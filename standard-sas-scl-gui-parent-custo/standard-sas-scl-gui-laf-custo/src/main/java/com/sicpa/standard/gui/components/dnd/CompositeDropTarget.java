package com.sicpa.standard.gui.components.dnd;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDropEvent;
import java.util.ArrayList;

import com.sicpa.standard.gui.utils.Pair;
import com.sicpa.standard.gui.utils.ThreadUtils;

public class CompositeDropTarget extends DropTarget {
	ArrayList<Pair<DataFlavor, DnDSuccessCallBack>> targets;

	public CompositeDropTarget() {
		super();
		this.targets = new ArrayList<Pair<DataFlavor, DnDSuccessCallBack>>();
	}

	public void add(final Pair<DataFlavor, DnDSuccessCallBack> target) {
		this.targets.add(target);
	}

	@Override
	public synchronized void drop(final DropTargetDropEvent dtde) {
		clearAutoscroll();
		try {
			final DropTargetContext context = dtde.getDropTargetContext();
			final Transferable transfert = dtde.getTransferable();
			for (DataFlavor flavor : dtde.getCurrentDataFlavors()) {
				for (Pair<DataFlavor, DnDSuccessCallBack> target : this.targets) {
					if (flavor.equals(target.getValue1())) {
						dtde.acceptDrop(DnDConstants.ACTION_COPY);
						dtde.dropComplete(true);
						final Pair<DataFlavor, DnDSuccessCallBack>[] finalTarget = new Pair[] { target };
						new Thread(new Runnable() {
							public void run() {
								Point p=(Point)dtde.getLocation().clone();
								p.x-=DnDGhostManager.dialog.getImage().getWidth()/2;
								p.y-=DnDGhostManager.dialog.getImage().getHeight()/2;
								
								ThreadUtils.sleepQuietly((long) (((float)DnDGhostDialog.getSuccessFeedbackDuration())));
								finalTarget[0].getValue2().dropSuccess(finalTarget[0].getValue1(), context, transfert,p);
							}
						}).start();
						return;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		dtde.rejectDrop();
	}
}
