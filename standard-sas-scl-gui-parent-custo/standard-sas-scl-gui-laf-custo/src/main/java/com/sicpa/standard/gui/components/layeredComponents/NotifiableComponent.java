package com.sicpa.standard.gui.components.layeredComponents;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;

import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.AbstractLayerUI;
import org.jdesktop.swingx.JXTaskPane;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.RepeatBehavior;

import com.sicpa.standard.gui.plaf.SicpaColor;

public class NotifiableComponent {
	private NotifiableComponent() {
	}

	public static JXLayer<JComponent> getComponentWithNotification(final JComponent comp) {
		if (comp == null) {
			throw new IllegalArgumentException("The JComponent cannot be null");
		}
		JXLayer<JComponent> res = new JXLayer<JComponent>(comp);

		res.setUI(new NotifiableLayerUI(comp));
		return res;
	}

	public static void alert(final JXLayer<JComponent> pane) {
		if (pane == null) {
			throw new IllegalArgumentException("The Component cannot be null");
		}
		if (!(pane.getUI() instanceof NotifiableLayerUI)) {
			throw new IllegalArgumentException("The Component is not a notifiable component");
		}

		NotifiableLayerUI ui = (NotifiableLayerUI) pane.getUI();

		alert(pane, new Rectangle(0, 0, ui.comp.getWidth(), ui.comp.getHeight()), SicpaColor.RED, false);
	}

	public static void alert(final JXLayer<JComponent> pane, final Shape shape) {
		alert(pane, shape, SicpaColor.RED, false);
	}

	public static void alert(final JXLayer<JComponent> pane, final Paint paint, final boolean infinite) {
		if (pane == null) {
			throw new IllegalArgumentException("The Component can not be null");
		}
		if (!(pane.getUI() instanceof NotifiableLayerUI)) {
			throw new IllegalArgumentException("The Component is not a notifiable component");
		}
		NotifiableLayerUI ui = (NotifiableLayerUI) pane.getUI();
		alert(pane, new Rectangle(0, 0, ui.comp.getWidth(), ui.comp.getHeight()), paint, infinite);
	}

	public static void alert(final JXLayer<JComponent> pane, final Shape shape, final Paint paint,
			final boolean infinite) {
		if (pane == null) {
			throw new IllegalArgumentException("The JComponent cannot be null");
		}
		if (!(pane.getUI() instanceof NotifiableLayerUI)) {
			throw new IllegalArgumentException("The Component is not a notifiable component");
		}

		final NotifiableLayerUI ui = (NotifiableLayerUI) pane.getUI();
		ui.shape = shape;
		ui.paint = paint;

		ui.anim = new Timeline(ui);
		ui.anim.setDuration(500);
		ui.anim.addPropertyToInterpolate("alpha", 0f, 0.5f);

		if (ui.comp instanceof JXTaskPane) {
			final JXTaskPane taskpane = (JXTaskPane) ui.comp;
			if (taskpane.isCollapsed()) {
//				if (ui.anim != null) {
					ui.anim.cancel();
//				}
				ui.anim.playLoop(RepeatBehavior.REVERSE);

				boolean alreadyHaveACollapsedListener = false;
				for (PropertyChangeListener listener : taskpane.getPropertyChangeListeners("collapsed")) {
					if (listener instanceof PropertyChangeListenerOpenTaskPane) {
						alreadyHaveACollapsedListener = true;
						break;
					}
				}
				if (!alreadyHaveACollapsedListener) {
					taskpane.addPropertyChangeListener("collapsed", new PropertyChangeListenerOpenTaskPane(ui,
							taskpane, pane));
				}
			} else {
//				if (ui.anim != null) {
					ui.anim.cancel();
//				}
				ui.anim.playLoop(10, RepeatBehavior.REVERSE);
			}
		} else {
//			if (ui.anim != null) {
				ui.anim.cancel();
//			}
			if (infinite) {
				ui.anim.playLoop(RepeatBehavior.REVERSE);
			} else {
				ui.anim.playLoop(10, RepeatBehavior.REVERSE);
			}
		}
	}

	public static class NotifiableLayerUI extends AbstractLayerUI<JComponent> {
		private float alpha;
		private JComponent comp;
		private Shape shape;
		private Paint paint;

		private Timeline anim;

		public NotifiableLayerUI(final JComponent comp) {
			this.comp = comp;
			this.alpha = 0;
			this.shape = new Rectangle(0, 0, comp.getWidth(), comp.getHeight());
		}

		@Override
		protected void paintLayer(final Graphics2D g, final JXLayer<? extends JComponent> layercomp) {
			super.paintLayer(g, layercomp);

			Graphics2D g2 = (Graphics2D) g.create();
			g2.setPaint(this.paint);
			g2.setComposite(AlphaComposite.SrcOver.derive(this.alpha));
			g2.fill(this.shape);
			g2.dispose();
		}

		public void setAlpha(final float alpha) {
			this.alpha = alpha;
			if (this.comp.getParent() != null) {// repaint the entire jxlayer
				this.comp.getParent().repaint();
			} else {
				this.comp.repaint();
			}
		}

		public float getAlpha() {
			return this.alpha;
		}

		public void stopAnim() {
			if (this.anim != null) {
				this.anim.cancel();
			}
		}
	}

	private static class PropertyChangeListenerOpenTaskPane implements PropertyChangeListener {
		private NotifiableLayerUI ui;
		private JXTaskPane taskpane;
		private JXLayer<JComponent> layerComp;

		public PropertyChangeListenerOpenTaskPane(final NotifiableLayerUI ui, final JXTaskPane taskpane,
				final JXLayer<JComponent> layerComp) {
			this.ui = ui;
			this.taskpane = taskpane;
			this.layerComp = layerComp;
		}

		@Override
		public void propertyChange(final PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals("collapsed")) {
				if (!(Boolean) evt.getNewValue()) {
					this.ui.anim.cancel();
					this.ui.alpha = 0f;

					this.taskpane.removePropertyChangeListener("collapsed", this);

					this.layerComp.repaint();
				}
			}
		}
	};
}
