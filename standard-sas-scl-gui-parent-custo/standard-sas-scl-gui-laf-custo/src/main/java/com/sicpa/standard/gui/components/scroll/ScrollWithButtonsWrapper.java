package com.sicpa.standard.gui.components.scroll;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.pushingpixels.substance.api.colorscheme.UltramarineColorScheme;
import org.pushingpixels.substance.internal.utils.SubstanceImageCreator;

import com.jidesoft.swing.DefaultOverlayable;
import com.sicpa.standard.gui.plaf.SicpaFont;
import com.sicpa.standard.gui.utils.ScrollUtils;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.gui.utils.ScrollUtils.ScrollDirection;

/**
 * wrap a JscrollPane, hide the scrollBar, add 2 buttons to scroll up/down, add the ability to scroll by a mouse dragged
 * 
 * @author DIelsch
 * 
 */
public class ScrollWithButtonsWrapper extends JPanel {
	public enum ButtonLocation {
		BOTTOM, RIGHT, BOTTOM_RIGHT
	}

	private JScrollPane scroll;
	private JComponent view;
	private JButton buttonNext;
	private JButton buttonPrevious;
	private int blockIncrement;
	private DefaultOverlayable overlayPanel1;
	private DefaultOverlayable overlayPanel2;
	private JPanel mainPanel;
	private ButtonLocation buttonLocation;

	public ScrollWithButtonsWrapper(final JScrollPane scroll) {
		this(scroll, null);
	}

	public ScrollWithButtonsWrapper(final JScrollPane scroll, final ButtonLocation location) {
		if (location == null) {
			this.buttonLocation = ButtonLocation.BOTTOM;
		} else {
			this.buttonLocation = location;
		}
		this.scroll = scroll;
		this.view = (JComponent) scroll.getViewport().getView();
		this.blockIncrement = scroll.getVerticalScrollBar().getBlockIncrement();
		initGUI();
		initListener();
		viewComponentResized();
	}

	public void setButtonLocation(final ButtonLocation buttonLocation) {
		if (this.mainPanel != null) {
			remove(this.mainPanel);
		}
		if (this.overlayPanel2 != null) {
			remove(this.overlayPanel2);
		}

		this.buttonLocation = buttonLocation;
		this.mainPanel = null;
		this.overlayPanel1 = null;
		this.overlayPanel2 = null;
		initGUI();
	}

	public void setBlockIncrement(final int blockIncrement) {
		this.blockIncrement = blockIncrement;
	}

	private void initGUI() {
		setLayout(new BorderLayout());
		if (this.buttonLocation == ButtonLocation.BOTTOM) {
			add(getMainPanel());
		} else {
			if (this.buttonLocation == ButtonLocation.BOTTOM_RIGHT) {
				this.getButtonPrevious().setPreferredSize(new Dimension(120, 43));
				this.getButtonNext().setPreferredSize(new Dimension(120, 43));
				this.getOverlayPanel1().setOverlayLocationInsets(new Insets(0, 0, 0, -125));

			} else if (this.buttonLocation == ButtonLocation.RIGHT) {
				this.getButtonPrevious().setPreferredSize(new Dimension(60, 35));
				this.getButtonNext().setPreferredSize(new Dimension(60, 35));
				this.getOverlayPanel1().setOverlayLocationInsets(new Insets(0, 0, 0, 0));
			}
			add(getOverlayPanel2(), BorderLayout.CENTER);
		}
		SmoothScrolling.addVerticalSmoothScrolling(this.scroll);
	}

	private void initListener() {
		this.view.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(final MouseEvent e) {
				viewMousePressed(e);
			}
		});
		this.view.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(final MouseEvent e) {
				viewMouseDragged(e);
			}
		});
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent e) {
				thisComponentResized();
			}

			@Override
			public void componentShown(final ComponentEvent e) {
				thisComponentResized();
			}
		});
		this.view.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent e) {
				viewComponentResized();
			}

			@Override
			public void componentShown(final ComponentEvent e) {
				viewComponentResized();
			}
		});
		this.scroll.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(final AdjustmentEvent e) {
				verticalScrollBarAdjustmentChanged();
			}
		});
	}

	private void verticalScrollBarAdjustmentChanged() {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				ScrollWithButtonsWrapper.this.buttonPrevious.setEnabled(!ScrollUtils
						.isScrollToMax(ScrollWithButtonsWrapper.this.scroll.getVerticalScrollBar()));
				ScrollWithButtonsWrapper.this.buttonNext.setEnabled(ScrollWithButtonsWrapper.this.scroll
						.getVerticalScrollBar().getValue() != 0);
			}
		});
	}

	private void updateButtonsState() {
		// ThreadUtils.invokeLater(new Runnable()
		// {
		// @Override
		// public void run()
		// {
		// ScrollWithButtonsWrapper.this.buttonPrevious.setVisible(!ScrollUtils.
		// isScrollToMax
		// (ScrollWithButtonsWrapper.this.scroll.getVerticalScrollBar()));
		// ScrollWithButtonsWrapper.this.buttonNext.setVisible(
		// ScrollWithButtonsWrapper
		// .this.scroll.getVerticalScrollBar().getValue() != 0);
		// }
		// });
	}

	public JPanel getMainPanel() {
		if (this.mainPanel == null) {
			this.mainPanel = new JPanel()
			// not needed apparently {
			// @Override
			// protected void paintComponent(final Graphics g) {
			// super.paintComponent(g);
			// OverlayableUtils.repaintOverlayable(this);
			// }
			// }
			;
			this.mainPanel.setLayout(new MigLayout("fill, inset 0 0 0 0"));
			this.mainPanel.add(this.scroll, "push , grow,span");
			this.scroll.setBorder(BorderFactory.createEmptyBorder());
			this.scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			this.scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			if (this.buttonLocation == ButtonLocation.BOTTOM) {
				this.mainPanel.add(getButtonNext(), "newline,growx, w 50%");
				this.mainPanel.add(getButtonPrevious(), "growx,w 50%");
			}
		}
		return this.mainPanel;
	}

	public DefaultOverlayable getOverlayPanel1() {
		if (this.overlayPanel1 == null) {
			if (this.buttonLocation == ButtonLocation.RIGHT) {
				this.overlayPanel1 = new DefaultOverlayable(getMainPanel(), getButtonNext(),
						DefaultOverlayable.NORTH_EAST);
			} else {
				this.overlayPanel1 = new DefaultOverlayable(getMainPanel(), getButtonNext(),
						DefaultOverlayable.SOUTH_EAST);
			}
			this.overlayPanel1.setOpaque(false);
		}
		return this.overlayPanel1;
	}

	public DefaultOverlayable getOverlayPanel2() {
		if (this.overlayPanel2 == null) {
			// if (this.buttonLocation == ButtonLocation.RIGHT)
			// {
			// this.overlayPanel2 = new DefaultOverlayable(getOverlayPanel1(),
			// getButtonPrevious(), DefaultOverlayable.SOUTH_EAST);
			// }
			// else
			{
				this.overlayPanel2 = new DefaultOverlayable(getOverlayPanel1(), getButtonPrevious(),
						DefaultOverlayable.SOUTH_EAST);
			}
			this.overlayPanel2.setOpaque(false);
		}
		return this.overlayPanel2;
	}

	private void thisComponentResized() {
		viewComponentResized();
	}

	private void viewComponentResized() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				setScrollButtonsVisible(ScrollWithButtonsWrapper.this.scroll.getVerticalScrollBar().getMaximum() > 5 + ScrollWithButtonsWrapper.this.scroll
						.getVerticalScrollBar().getVisibleAmount());
			}
		});

		updateButtonsState();
	}

	private int oldy;

	private void viewMousePressed(final MouseEvent e) {
		this.oldy = (int) MouseInfo.getPointerInfo().getLocation().getY();
	}

	private void viewMouseDragged(final MouseEvent e) {
		int current = (int) MouseInfo.getPointerInfo().getLocation().getY();
		int offset = this.oldy - current;
		// always UP , if offset<0 => down
		ScrollUtils.scroll(ScrollDirection.UP, offset, this.scroll);
		updateButtonsState();
		this.oldy = current;
	}

	public JButton getButtonNext() {
		if (this.buttonNext == null) {
			this.buttonNext = new JButton();
			this.buttonNext.setFont(SicpaFont.getFont(30));
			Icon icon = SubstanceImageCreator.getArrowIcon(30, SwingConstants.NORTH, new UltramarineColorScheme());

			this.buttonNext.setIcon(icon);
			this.buttonNext.setName("nextScrollButton");
			this.buttonNext.setVisible(false);
			this.buttonNext.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(final MouseEvent e) {
					buttonNextMousePressed();
				}
			});
		}
		return this.buttonNext;
	}

	private void buttonNextMousePressed() {
		new Thread(this.pusher).start();
	}

	private void buttonPreviousMousePressed() {
		new Thread(this.pusher).start();
	}

	private Runnable pusher = new Runnable() {
		public void run() {
			boolean next = ScrollWithButtonsWrapper.this.buttonNext.getModel().isArmed();
			boolean previous = ScrollWithButtonsWrapper.this.buttonPrevious.getModel().isArmed();
			if (next) {
				ScrollUtils.scroll(ScrollDirection.DOWN, ScrollWithButtonsWrapper.this.blockIncrement,
						ScrollWithButtonsWrapper.this.scroll);
			} else {
				ScrollUtils.scroll(ScrollDirection.UP, ScrollWithButtonsWrapper.this.blockIncrement,
						ScrollWithButtonsWrapper.this.scroll);
			}
			updateButtonsState();

			ThreadUtils.sleepQuietly(400);
			next = ScrollWithButtonsWrapper.this.buttonNext.getModel().isArmed();
			previous = ScrollWithButtonsWrapper.this.buttonPrevious.getModel().isArmed();
			while (next || previous) {
				if (next) {
					ScrollUtils.scroll(ScrollDirection.DOWN, ScrollWithButtonsWrapper.this.blockIncrement,
							ScrollWithButtonsWrapper.this.scroll);
				} else {
					ScrollUtils.scroll(ScrollDirection.UP, ScrollWithButtonsWrapper.this.blockIncrement,
							ScrollWithButtonsWrapper.this.scroll);
				}
				updateButtonsState();
				ThreadUtils.sleepQuietly(100);
				next = ScrollWithButtonsWrapper.this.buttonNext.getModel().isArmed();
				previous = ScrollWithButtonsWrapper.this.buttonPrevious.getModel().isArmed();
			}
		}
	};

	public JButton getButtonPrevious() {
		if (this.buttonPrevious == null) {
			this.buttonPrevious = new JButton();
			this.buttonPrevious.setFont(SicpaFont.getFont(30));
			Icon icon = SubstanceImageCreator.getArrowIcon(30, SwingConstants.SOUTH, new UltramarineColorScheme());

			this.buttonPrevious.setIcon(icon);
			this.buttonPrevious.setName("previousScrollButton");
			this.buttonPrevious.setVisible(false);
			this.buttonPrevious.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(final MouseEvent e) {
					buttonPreviousMousePressed();
				}
			});
		}
		return this.buttonPrevious;
	}

	public void setScrollButtonsVisible(final boolean flag) {
		getButtonNext().setVisible(flag);
		getButtonPrevious().setVisible(flag);
	}

	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);
		// OverlayableUtils.repaintOverlayable(this);
	}
}
