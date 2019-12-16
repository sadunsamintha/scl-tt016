package com.sicpa.standard.gui.components.layeredComponents;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.animation.timing.interpolation.PropertySetter;
import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.AbstractLayerUI;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;

import com.jidesoft.swing.DefaultOverlayable;
import com.jidesoft.swing.OverlayableIconsFactory;
import com.jidesoft.swing.OverlayableUtils;
import com.sicpa.standard.gui.plaf.SicpaFont;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.utils.PaintUtils;

/**
 * Allow to add a hint message on generic JComponent
 * 
 * @author DIelsch
 * 
 */
public class HintManager {

	public static enum eViewHint {
		layer, icon
	}

	private static ArrayList<InfoComp> infoComps = new ArrayList<InfoComp>();

	private HintManager() {
	}

	/**
	 * Remove the component from the hint manager
	 * 
	 * @param comp
	 *            the component to remove
	 */
	public static void unregisterHint(final JComponent comp) {
		if (comp == null) {
			throw new IllegalArgumentException("The JComponent can not be null");
		}
		for (InfoComp info : getInfoComps(comp)) {
			if (infoComps.contains(info)) {
				infoComps.remove(info);
			}
		}
	}

	/**
	 * Remove all the components from the hint manager that have this key
	 * 
	 * @param key
	 *            the key to specify which component should be remove from the
	 *            hint manager
	 */
	public static void unregisterHint(final String key) {
		for (InfoComp info : getInfoComps(key)) {
			infoComps.remove(info);
		}
	}

	/**
	 * Remove all components from the hint manager
	 */
	public static void unregisterAllHint() {
		infoComps.clear();
	}

	/**
	 * Register and return a hintable component
	 * 
	 * @param in
	 *            The component that will get hinted
	 * @param msg
	 *            The message to be displayed
	 * @return A wrapper for the component
	 */
	public static JComponent registerHint(final JComponent textComp, final String msg) {
		return registerHint(textComp, eViewHint.layer, msg, "com.sicpa.standard.gui.components.hint.nokey", true,
				Color.WHITE, Color.BLACK, 0.3f, 0, 0);
	}

	/**
	 * Register and return a hintable component
	 * 
	 * @param in
	 *            The component that will get hinted
	 * @param type
	 *            The type of view layer or icon
	 * @param msg
	 *            The message to be displayed
	 * @return A wrapper for the component
	 */
	public static JComponent registerHint(final JComponent textComp, final eViewHint type, final String msg) {
		return registerHint(textComp, type, msg, "com.sicpa.standard.gui.components.hint.nokey", true, Color.WHITE,
				Color.BLACK, 0.3f, 0, 0);
	}

	/**
	 * Register and return a hintable component
	 * 
	 * @param in
	 *            The component that will get hinted
	 * @param type
	 *            The type of view layer or icon
	 * @param msg
	 *            The message to be displayed
	 * @param bgVisible
	 *            If the background should be visible or not
	 * @return A wrapper for the component
	 */
	public static JComponent registerHint(final JComponent textComp, final eViewHint type, final String msg,
			final boolean bgVisible) {
		return registerHint(textComp, type, msg, "com.sicpa.standard.gui.components.hint.nokey", bgVisible, Color.WHITE,
				Color.BLACK, 0.3f, 0, 0);
	}

	/**
	 * Register and return a hintable component
	 * 
	 * @param in
	 *            The component that will get hinted
	 * @param type
	 *            The type of view layer or icon
	 * @param msg
	 *            The message to be displayed
	 * @param bgVisible
	 *            If the background should be visible or not
	 * @param inStringcolor
	 *            The surrounding color of the message
	 * @param outStringColor
	 *            The color of the message
	 * @return A wrapper for the component
	 */
	public static JComponent registerHint(final JComponent textComp, final eViewHint type, final String msg,
			final boolean bgVisible, final Color inStringcolor, final Color outStringColor) {
		return registerHint(textComp, type, msg, "com.sicpa.standard.gui.components.hint.nokey", bgVisible,
				inStringcolor, outStringColor, 0.3f, 0, 0);
	}

	/**
	 * Register and return a hintable component
	 * 
	 * @param in
	 *            The component that will get hinted
	 * @param type
	 *            The type of view layer or icon
	 * @param msg
	 *            The message to be displayed
	 * @param bgVisible
	 *            If the background should be visible or not
	 * @param inStringcolor
	 *            The surrounding color of the message
	 * @param outStringColor
	 *            The color of the message
	 * @param backGroundAlpha
	 *            The translucidity of the background, range 0..1 , ignored if
	 *            bgVisible==false
	 * @return A wrapper for the component
	 */
	public static JComponent registerHint(final JComponent textComp, final eViewHint type, final String msg,
			final boolean bgVisible, final Color inStringcolor, final Color outStringColor, final float backGroundAlpha) {
		return registerHint(textComp, type, msg, "com.sicpa.standard.gui.components.hint.nokey", bgVisible,
				inStringcolor, outStringColor, backGroundAlpha, 0, 0);
	}

	/**
	 * Register and return a hintable component
	 * 
	 * @param in
	 *            The component that will get hinted
	 * @param type
	 *            The type of view layer or icon
	 * @param msg
	 *            The message to be displayed
	 * @param bgVisible
	 *            If the background should be visible or not
	 * @param inStringcolor
	 *            The surrounding color of the message
	 * @param outStringColor
	 *            The color of the message
	 * @param xOffset
	 *            x offset for the text
	 * @param yOffset
	 *            y offset for the text
	 * @param backGroundAlpha
	 *            The translucidity of the background, range 0..1 , ignored if
	 *            bgVisible==false
	 * @return A wrapper for the component
	 */
	public static JComponent registerHint(final JComponent textComp, final eViewHint type, final String msg,
			final boolean bgVisible, final Color inStringcolor, final Color outStringColor,
			final float backGroundAlpha, final int xOffset, final int yoffset) {
		return registerHint(textComp, type, msg, "com.sicpa.standard.gui.components.hint.nokey", bgVisible,
				inStringcolor, outStringColor, backGroundAlpha, xOffset, yoffset);
	}

	/**
	 * Register and return a hintable component
	 * 
	 * @param in
	 *            The component that will get hinted
	 * @param type
	 *            The type of view layer or icon
	 * @param msg
	 *            The message to be displayed
	 * @param key
	 *            The key identifying a group of components
	 * @param bgVisible
	 *            If the background should be visible or not
	 * @param inStringcolor
	 *            The surrounding color of the message
	 * @param outStringColor
	 *            The color of the message
	 * @param backGroundAlpha
	 *            The translucidity of the background, range from 0..1 , ignored
	 *            if bgVisible==false
	 * @param xOffset
	 *            x offset for the text
	 * @param yOffset
	 *            y offset for the text
	 * @return A wrapper for the component
	 */
	public static JComponent registerHint(final JComponent in, final eViewHint type, final String msg,
			final String key, final boolean bgVisible, final Color inStringcolor, final Color outStringColor,
			final float backGroundAlpha, final int xOffset, final int yoffset) {
		if (in == null) {
			throw new IllegalArgumentException("The JComponent can not be null");
		}

		JComponent comp = null;

		InfoComp info = new InfoComp();
		info.actualComp = in;
		info.message = msg;
		info.key = key;
		info.backgroundVisible = bgVisible;
		info.inStringColor = inStringcolor;
		info.outStringColor = outStringColor;
		info.alpha = backGroundAlpha;
		// info.xOffset = xOffset;
		// info.yOffset = yoffset;
		switch (type) {
		case layer:
			comp = getLayerHint(info, msg);
			break;
		case icon:
			comp = getHintIcon(in, msg);
			break;
		}
		info.hintedComp = comp;

		infoComps.add(info);

		return comp;
	}

	/**
	 * Set visible the message for all the component that doesn't have a
	 * specific key
	 * 
	 * @param visible
	 */
	public static void setHintVisible(final boolean visible) {
		setHintVisible(visible, "com.sicpa.standard.gui.components.hint.nokey");
	}

	/**
	 * Set visible the message for all the components that match given key
	 * 
	 * @param visible
	 * @param key
	 *            The key specifying which components will be affected
	 */
	public static void setHintVisible(final boolean visible, final String key) {
		ArrayList<InfoComp> comps = getInfoComps(key);
		if (!comps.isEmpty()) {
			for (InfoComp info : comps) {
				if (info.hintedComp instanceof JXLayer) {
					((HintLayer) (((JXLayer) info.hintedComp).getUI())).setVisible(visible);
				} else if (info.hintedComp instanceof MyOverlayable) {
					((MyOverlayable) info.hintedComp).setIconVisible(visible);
				}
			}
		}
	}

	/**
	 * Set visible the message for the given component
	 * 
	 * @param visible
	 * @param comp
	 *            The component that will have his text visible
	 */
	public static void setHintVisible(final boolean visible, final JComponent comp) {
		if (comp == null) {
			throw new IllegalArgumentException("The JComponent can not be null");
		}
		ArrayList<InfoComp> comps = getInfoComps(comp);
		if (!comps.isEmpty()) {
			for (InfoComp info : comps) {
				if (info.hintedComp instanceof JXLayer) {
					((HintLayer) (((JXLayer) info.hintedComp).getUI())).setVisible(visible);
				} else if (info.hintedComp instanceof MyOverlayable) {
					((MyOverlayable) info.hintedComp).setIconVisible(visible);
				}
			}
		}
	}

	/**
	 * 
	 * @param info
	 * @param msg
	 * @return The wrapper component for a layer view
	 */
	private static JXLayer<JComponent> getLayerHint(final InfoComp info, final String msg) {
		JXLayer<JComponent> res = new JXLayer<JComponent>(info.actualComp);
		final HintLayer layer = new HintLayer(info);
		res.setUI(layer);
		return res;
	}

	/**
	 * 
	 * @param comp
	 * @param msg
	 * @return The wrapper component for an icon view
	 */
	private static DefaultOverlayable getHintIcon(final JComponent comp, final String msg) {
		JLabel info = new JLabel(OverlayableUtils.getPredefinedOverlayIcon(OverlayableIconsFactory.INFO));
		info.setToolTipText(msg);
		info.setVisible(false);

		JXLayer<JComponent> res = new JXLayer<JComponent>(comp);
		final IconLayer layer = new IconLayer(comp, info);
		res.setUI(layer);
		MyOverlayable over = new MyOverlayable(res, info, DefaultOverlayable.NORTH_EAST, layer);
		over.setOverlayLocationInsets(new Insets(4, 0, 0, 4));
		return over;
	}

	/**
	 * icon view
	 * 
	 * @author DIelsch
	 * 
	 */
	private static class MyOverlayable extends DefaultOverlayable {
		private IconLayer layer;

		public MyOverlayable(final JComponent actualComponent, final JComponent overlayComponent,
				final int overlayLocation, final IconLayer layer) {
			super(actualComponent, overlayComponent, overlayLocation);
			this.layer = layer;
		}

		private void setIconVisible(final boolean flag) {
			this.layer.setVisible(flag);
		}
	}

	/**
	 * layer view
	 * 
	 * @author DIelsch
	 * 
	 */
	public static class HintLayer extends AbstractLayerUI<JComponent> {
		private InfoComp info;
		private boolean visible;

		private float alpha = 0f;

		public HintLayer(final InfoComp comp) {
			super();

			this.info = comp;
			this.visible = false;
		}

		public void setVisible(final boolean flag) {
			this.visible = flag;
			this.info.hintedComp.repaint();
			float target;
			if (this.visible) {
				target = 1;

			} else {
				target = 0;
			}
			PropertySetter.createAnimator(500, this, "alpha", target).start();
		}

		@Override
		protected void paintLayer(final Graphics2D g2, final JXLayer<? extends JComponent> l) {
			super.paintLayer(g2, l);

			if ((this.visible || this.alpha != 0) && !this.info.message.isEmpty()) {
				Font tooltipFont = SubstanceLookAndFeel.getFontPolicy().getFontSet(null, null).getSmallFont();
				float a = this.info.alpha - (1 - this.alpha);
				if (a < 0) {
					a = 0;
				} else if (a > 1) {
					a = 1f;
				}

				int cw = this.info.hintedComp.getWidth();
				int ch = this.info.hintedComp.getHeight();
				Rectangle rect = new Rectangle(0, 0, cw, ch);

				Graphics2D g = (Graphics2D) g2.create();
				g.setFont(tooltipFont);
				AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, a);
				g.setComposite(composite);
				if (this.info.backgroundVisible) {
					g.setColor(this.info.backgroundColor);
					g.fill(rect);
				}

				composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.alpha);
				g.setComposite(composite);

				PaintUtils.drawMultiLineHighLightText(g, rect, 5, 0.9f, this.info.message, 0, 0,
						this.info.inStringColor, this.info.outStringColor, true, true);

				g.dispose();
			}
		}

		public void setAlpha(final float alpha) {
			this.alpha = alpha;
			this.info.hintedComp.repaint();
		}

		public float getAlpha() {
			return this.alpha;
		}
	}

	/**
	 * utility class for icon view
	 * 
	 * @author DIelsch
	 * 
	 */
	public static class IconLayer extends AbstractLayerUI<JComponent> {
		private JLabel label;
		private boolean visible;

		public IconLayer(final JComponent comp, final JLabel info) {
			super();
			this.label = info;
			this.visible = false;
		}

		public void setVisible(final boolean flag) {
			this.visible = flag;
			this.label.setVisible(this.visible);
		}

		@Override
		protected void paintLayer(final Graphics2D g2, final JXLayer<? extends JComponent> l) {
			super.paintLayer(g2, l);
			OverlayableUtils.repaintOverlayable(l);
		}
	}

	public static void main(final String[] args) {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				SicpaLookAndFeelCusto.setFont(SicpaFont.getFont(20));
				SicpaLookAndFeelCusto.setSmallFont(SicpaFont.getFont(15));
				JFrame f = new JFrame();
				f.setSize(800, 600);
				SicpaLookAndFeelCusto.flagAsWorkArea((JComponent) f.getContentPane());
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setLayout(new MigLayout("fill,wrap 2"));

				JScrollPane jsp = new JScrollPane(new JTree());
				jsp.setPreferredSize(new Dimension(100, 100));
				f.getContentPane().add(registerHint(jsp, eViewHint.layer, "HINT MSG"), "grow");

				JButton button = new JButton("action");
				button.setPreferredSize(new Dimension(150, 25));
				f.getContentPane().add(registerHint(button, eViewHint.icon, "HINT MSG"));

				button = new JButton("action");
				button.setPreferredSize(new Dimension(150, 25));
				f.getContentPane().add(registerHint(button, eViewHint.layer, "HINT MSG"));

				final JTextArea jta = new JTextArea();
				jta.setPreferredSize(new Dimension(200, 200));
				f.getContentPane().add(registerHint(jta, eViewHint.layer, "Your message here"));

				final JToggleButton b = new JToggleButton("toggle hint");
				f.getContentPane().add(b);

				b.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(final ActionEvent e) {
						setHintVisible(b.isSelected());
						setHintMessage("test:\n" + new Random().nextInt() + "test:\n" + new Random().nextInt()
								+ "test:\n" + new Random().nextInt() + "test:\n" + new Random().nextInt(), jta);
					}
				});

				f.setVisible(true);
			}
		});
	}

	/**
	 * class to stock properties of hintable components
	 * 
	 * @author DIelsch
	 * 
	 */
	private static class InfoComp {

		private JComponent hintedComp;
		private JComponent actualComp;
		private String message;
		private boolean backgroundVisible;
		private Color backgroundColor;
		private float alpha;
		private String key;
		private Color outStringColor;
		private Color inStringColor;

		// private int xOffset;
		// private int yOffset;

		public InfoComp() {
			this.alpha = 0.3f;
			this.backgroundVisible = true;
			this.backgroundColor = Color.GRAY;
			this.outStringColor = Color.BLACK;
			this.inStringColor = Color.WHITE;
			// this.xOffset = 0;
			// this.yOffset = 0;
		}
	}

	private static ArrayList<InfoComp> getInfoComps(final String key) {
		ArrayList<InfoComp> list = new ArrayList<InfoComp>();
		for (InfoComp info : infoComps) {
			if (info.key.equals(key)) {
				list.add(info);
			}
		}
		return list;
	}

	private static ArrayList<InfoComp> getInfoComps(final JComponent comp) {
		ArrayList<InfoComp> list = new ArrayList<InfoComp>();
		for (InfoComp info : infoComps) {
			if (info.actualComp.equals(comp)) {
				list.add(info);
			}
		}
		return list;
	}

	/**
	 * Set a new message for a given component
	 * 
	 * @param msg
	 *            The new message
	 * @param comp
	 *            The component on which one to show the new message
	 */
	public static void setHintMessage(final String msg, final JComponent comp) {
		for (InfoComp info : getInfoComps(comp)) {
			info.message = msg;
			info.hintedComp.repaint();
		}

	}

	/**
	 * Set the background visible for a given component
	 * 
	 * @param flag
	 * @param comp
	 */
	public static void setBackgroundvisible(final boolean flag, final JComponent comp) {
		for (InfoComp info : getInfoComps(comp)) {
			info.backgroundVisible = flag;
			info.hintedComp.repaint();
		}
	}

	/**
	 * Set the background color for a given component
	 * 
	 * @param bg
	 *            The background color
	 * @param comp
	 */
	public static void setBackgroundColor(final Color bg, final JComponent comp) {
		for (InfoComp info : getInfoComps(comp)) {
			info.backgroundColor = bg;
			info.hintedComp.repaint();
		}
	}

	/**
	 * Set the translucidty of a component background
	 * 
	 * @param alpha
	 *            The translucidity, range 0..11
	 * @param comp
	 */
	public static void setBackgroundAlpha(final float alpha, final JComponent comp) {
		for (InfoComp info : getInfoComps(comp)) {
			info.alpha = alpha;
			info.hintedComp.repaint();
		}
	}

}
