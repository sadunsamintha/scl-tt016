package com.sicpa.standard.gui.components.dnd;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DragSourceMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.graphics.GraphicsUtilities;

import com.sicpa.standard.gui.utils.Pair;
import com.sicpa.standard.gui.utils.ThreadUtils;

public class DnDGhostManager {

	protected static DnDGhostDialog dialog;

	public static void enableDrag(final JComponent comp, final Transferable t) {
		enableDrag(comp, t, null, null);
	}

	public static void enableDrag(final JComponent comp, final Transferable t,
			final DnDStartStopCallback startStopCallback, final DnDFailureCallBack failAction) {

		maybeInitDrag();
		DragGestureListener dgl = new DragGestureListener() {
			@Override
			public void dragGestureRecognized(final DragGestureEvent dge) {
				if (DnDLock.isLocked()) {
					return;
				}
				DnDLock.get();

				if (startStopCallback != null) {
					startStopCallback.dragStart(t, dge.getDragOrigin());
				}

				Component source = dge.getComponent();
				BufferedImage img = GraphicsUtilities.createCompatibleTranslucentImage(source.getWidth(),
						source.getHeight());
				Graphics2D g = img.createGraphics();
				source.paint(g);
				g.dispose();
				dialog.setImage(img);
				dialog.setAnimProgress(0);
				dge.startDrag(Cursor.getDefaultCursor(), t, new DragSourceListener() {

					@Override
					public void dropActionChanged(final DragSourceDragEvent dsde) {
					}

					@Override
					public void dragOver(final DragSourceDragEvent dsde) {
					}

					@Override
					public void dragExit(final DragSourceEvent dse) {
					}

					@Override
					public void dragEnter(final DragSourceDragEvent dsde) {
					}

					@Override
					public void dragDropEnd(final DragSourceDropEvent dsde) {
						if (dsde.getDropSuccess()) {
							dialog.showSuccessFeedback();
						} else {
							if (failAction != null) {
								failAction.dropFailed(t, dsde.getLocation());
								dialog.setImage(null);
								dialog.setVisible(false);
								DnDLock.release();
							} else {
								Component source = dsde.getDragSourceContext().getComponent();
								Point oldLoc = (Point) source.getLocation().clone();
								SwingUtilities.convertPointToScreen(oldLoc, source.getParent());

								Point dropPoint = dsde.getLocation();
								dialog.showFailureFeedback(oldLoc, dropPoint);

							}
						}
						if (startStopCallback != null) {
							startStopCallback.dragStop(t, dsde.getLocation());
						}
					}
				});
			}
		};

		DragSource dragSource = DragSource.getDefaultDragSource();
		dragSource.createDefaultDragGestureRecognizer(comp, DnDConstants.ACTION_COPY, dgl);
	}

	private static void maybeInitDrag() {
		if (dialog == null) {
			ThreadUtils.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					dialog = new DnDGhostDialog();
					dialog.setSize(1, 1);
					dialog.dispose();
				}
			});
			DragSource dragSource = DragSource.getDefaultDragSource();
			dragSource.addDragSourceMotionListener(new DragSourceMotionListener() {
				@Override
				public void dragMouseMoved(final DragSourceDragEvent dsde) {
					Point p = (Point) dsde.getLocation().clone();
					dialog.setPoint(p);
					dialog.setVisible(true);
				}
			});
		}
	}

	public static void enableDrop(final JComponent comp, final DataFlavor flavor, final DnDSuccessCallBack callback) {
		CompositeDropTarget cdt;
		if (comp.getDropTarget() == null || !(comp.getDropTarget() instanceof CompositeDropTarget)) {
			cdt = new CompositeDropTarget();
		} else {
			cdt = (CompositeDropTarget) comp.getDropTarget();
		}
		cdt.add(new Pair<DataFlavor, DnDSuccessCallBack>(flavor, callback));
		comp.setDropTarget(cdt);
	}
}
