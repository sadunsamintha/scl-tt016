package com.sicpa.standard.gui.screen.machine.component.error;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXPanel;
import org.pushingpixels.substance.internal.utils.SubstanceColorUtilities;
import org.pushingpixels.substance.internal.utils.SubstanceCoreUtilities;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.TimelineState;
import org.pushingpixels.trident.callback.TimelineCallback;
import org.pushingpixels.trident.ease.Spline;

import com.sicpa.standard.gui.components.scroll.SmallScrollBar;
import com.sicpa.standard.gui.components.scroll.SmoothScrolling;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.plaf.ui.SicpaListUI;
import com.sicpa.standard.gui.plaf.ui.SicpaScrollBarUI;
import com.sicpa.standard.gui.utils.ThreadUtils;
@Deprecated
public class DefaultScrollingErrorPanel extends AbstractScrollingErrorPanel {

	private static final int CELL_UNSELECTED_HEIGHT = 40;
	private static final int CELL_SELECTED_HEIGHT = 160;

	public static void main(final String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				JFrame f = new JFrame();
				f.setSize(400, 300);
				f.setVisible(true);
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				DefaultScrollingErrorPanel s = new DefaultScrollingErrorPanel();

				s.setHelpVisible(true);
				f.getContentPane().add(s);

				for (int i = 0; i < 20; i++) {
					s.addError(ErrorType.FATAL, i + "", "title", "<html><li>1111111<li>2222222222");
				}
			}
		});
	}

	private JScrollPane scroll;
	private JList list;
	private DefaultListModel listModel;
	private int selectedIndex = -1;
	private int deselectedIndex = -1;
	private JLabel helpLabel;

	public DefaultScrollingErrorPanel() {
		this(new ScrollingErrorModel());

	}

	public DefaultScrollingErrorPanel(final ScrollingErrorModel model) {
		super(model);
		initGUI();
		setHelpVisible(false);

		addComponentListener(this.ca);

		addHierarchyListener(new HierarchyListener() {
			@Override
			public void hierarchyChanged(final HierarchyEvent e) {
				getParent().removeComponentListener(DefaultScrollingErrorPanel.this.ca);
				getParent().addComponentListener(DefaultScrollingErrorPanel.this.ca);
			}
		});
	}

	private ComponentAdapter ca = new ComponentAdapter() {
		@Override
		public void componentResized(final ComponentEvent e) {
			if (DefaultScrollingErrorPanel.this.list.getUI() instanceof SicpaListUI) {
				((SicpaListUI) (DefaultScrollingErrorPanel.this.list.getUI())).computeSize();
			}
			revalidate();
		}
	};

	private void initGUI() {
		// setOpaque(false);
		setLayout(new BorderLayout());
		add(SmallScrollBar.createLayerSmallScrollBar(getScroll(), false, true, true,true), BorderLayout.CENTER);
	}

	private void listMouseDragged(final MouseEvent e) {

	}

	private void listMouseReleased(final MouseEvent e) {

		if (this.oldPoint != null) {
			int dx = Math.abs(e.getLocationOnScreen().x - this.oldPoint.x);
			int dy = Math.abs(e.getLocationOnScreen().y - this.oldPoint.y);
			if (dx > 10 || dy > 10) {
				return;
			}
		}

		if (maybeDoClickOnHelp(e)) {
			return;
		}

		if (this.selectAnim.getState() == TimelineState.PLAYING_FORWARD) {
			return;
		}

		if (e.getClickCount() == 2 || e.getClickCount() == 1) {

			// if new selection
			if (this.selectedIndex != this.list.getSelectedIndex()) {
				this.deselectedIndex = this.selectedIndex;
				// hideSelectAnim(this.deselectedIndex);
				this.selectedIndex = this.list.getSelectedIndex();
				// startSelectAnim(this.selectedIndex);
				selectAnim(this.deselectedIndex, this.selectedIndex);
			}
		}
	}

	private boolean maybeDoClickOnHelp(final MouseEvent evt) {
		if (getHelpLabel().getIcon() != null && this.selectedIndex == this.list.getSelectedIndex())// test
		// if click on help
		{
			int selected = this.list.getSelectedIndex();
			int iconH = getHelpLabel().getIcon().getIconHeight();
			int iconW = getHelpLabel().getIcon().getIconHeight();
			int y1 = selected * CELL_UNSELECTED_HEIGHT + CELL_SELECTED_HEIGHT / 2 - iconH / 2;
			// int y2 = y1 + iconH;
			int x1 = getHelpLabel().getBounds().x;
			// int x2 = x1 + iconW;
			Rectangle rect = new Rectangle(x1, y1, iconH, iconW);
			if (rect.contains(evt.getPoint())) {
				helpMouseClicked();
			}
			return true;
		}

		return false;
	}

	private void helpMouseClicked() {

	}

	private void timingEvent(final int index, final float fraction) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				DefaultScrollingErrorPanel.this.mapAnim.put(index, fraction);
				if (DefaultScrollingErrorPanel.this.list.getUI() instanceof SicpaListUI) {
					((SicpaListUI) DefaultScrollingErrorPanel.this.list.getUI()).computeSize();
				}
				getList().revalidate();
				getList().repaint();
			}
		});
	}

	private int animDuration = 500;

	public void setAnimDuration(final int animDuration) {
		this.animDuration = animDuration;
	}

	private Timeline selectAnim;

	private void selectAnim(final int oldIndex, final int newIndex) {
		if (this.selectAnim != null) {
			this.selectAnim.cancel();
		}
		this.selectAnim = new Timeline();
		this.selectAnim.setDuration(this.animDuration);
		this.selectAnim.addCallback(new TimelineCallback() {
			@Override
			public void onTimelinePulse(final float durationFraction, final float timelinePosition) {
				DefaultScrollingErrorPanel.this.timingEvent(oldIndex, 1 - timelinePosition);
				DefaultScrollingErrorPanel.this.timingEvent(newIndex, timelinePosition);
			}

			@Override
			public void onTimelineStateChanged(final TimelineState oldState, final TimelineState newState,
					final float durationFraction, final float timelinePosition) {
				if (newState == TimelineState.DONE) {
					DefaultScrollingErrorPanel.this.timingEvent(oldIndex, 0f);
					DefaultScrollingErrorPanel.this.timingEvent(newIndex, 1f);
				}
			}
		});
		this.selectAnim.setEase(new Spline(0.7f));
		this.selectAnim.play();

		this.list.setSelectedIndex(newIndex);
	}

	// store the animprogress for all the warning
	private Map<Integer, Float> mapAnim = new HashMap<Integer, Float>();

	private class ErrorRenderer implements ListCellRenderer {
		private JLabel labelTitle;
		private JPanel mainPanel;
		private JXPanel alphaPanel;
		private JLabel labelInfo;
		private Dimension smallDim;
		private JLabel labelHasContent;

		public ErrorRenderer() {
			this.smallDim = new Dimension(50, CELL_UNSELECTED_HEIGHT);
			getMainPanel();
		}

		public JLabel getLabelHasContent() {
			if (this.labelHasContent == null) {
				this.labelHasContent = new JLabel(">>");
				this.labelHasContent.setForeground(Color.WHITE);
				this.labelHasContent.setName("labelHasContent");
			}
			return this.labelHasContent;
		}

		public JLabel getLabelInfo() {
			if (this.labelInfo == null) {
				this.labelInfo = new JLabel();
			}
			return this.labelInfo;
		}

		public JLabel getLabelTitle() {
			if (this.labelTitle == null) {
				this.labelTitle = new JLabel();
			}
			return this.labelTitle;
		}

		public JPanel getMainPanel() {
			if (this.mainPanel == null) {
				this.mainPanel = new JPanel(new MigLayout("fill"));
				SicpaLookAndFeelCusto.flagAsDefaultArea(this.mainPanel);
				this.mainPanel.add(getLabelTitle(), "");
				this.mainPanel.add(getLabelHasContent(), "wrap");
				this.mainPanel.add(this.getAlphaPanel(), "grow");
				this.mainPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				this.mainPanel.setOpaque(true);
			}
			return this.mainPanel;
		}

		public JXPanel getAlphaPanel() {
			if (this.alphaPanel == null) {
				this.alphaPanel = new JXPanel(new MigLayout("fill")) {
					private static final long serialVersionUID = 1L;

					@Override
					protected void paintComponent(final Graphics arg0) {
					}
				};
				this.alphaPanel.add(getLabelInfo(), "gap bottom 25");
				this.alphaPanel.add(getHelpLabel(), "east");
				this.alphaPanel.setOpaque(false);
			}
			return this.alphaPanel;
		}

		@Override
		public Component getListCellRendererComponent(final JList list, final Object value, final int index,
				final boolean isSelected, final boolean cellHasFocus) {
			ErrorItem item = (ErrorItem) value;
			this.labelTitle.setText(item.getTitle());

			// do not use the isSelected because
			// when the UI compute the size
			// is selected is always false
			Float progress = DefaultScrollingErrorPanel.this.mapAnim.get(index);
			if (progress == null) {
				progress = 0f;
			}

			if (index == DefaultScrollingErrorPanel.this.selectedIndex
					&& DefaultScrollingErrorPanel.this.selectedIndex != -1) {// Select
				// progress
				prepareForAnim(progress, item);
				if (list.getModel().getSize() <= 3) {// if less than 4, the
					// selected take all the
					// remaining space
					int h = (int) (getHeight() * progress) - (list.getModel().getSize() - 1) * CELL_UNSELECTED_HEIGHT;
					this.mainPanel.setPreferredSize(new Dimension(50, h));
				} else {
					this.mainPanel.setPreferredSize(new Dimension(50, (int) ((CELL_SELECTED_HEIGHT) * progress)));
				}
				this.labelHasContent.setVisible(false);
				this.labelInfo.setVisible(true);
			} else if (index == DefaultScrollingErrorPanel.this.deselectedIndex
					&& DefaultScrollingErrorPanel.this.deselectedIndex != -1) {// deselected
				// progress
				prepareForAnim(progress, item);
				this.mainPanel
						.setPreferredSize(new Dimension(50,
								(int) (CELL_SELECTED_HEIGHT - (CELL_SELECTED_HEIGHT - CELL_UNSELECTED_HEIGHT)
										* (1 - progress))));
				this.labelHasContent.setVisible(item.getText() != null && item.getText().length() != 0);
				this.labelInfo.setVisible(true);
			} else {
				// not selected
				this.labelTitle.setForeground(item.getType().getForeground());
				this.labelTitle.setBackground(item.getType().getBackground());
				this.mainPanel.setPreferredSize(this.smallDim);
				getHelpLabel().setVisible(false);
				this.alphaPanel.setAlpha(1f);
				this.mainPanel.setBackground(item.getType().getBackground());
				this.labelHasContent.setVisible(item.getText() != null && item.getText().length() != 0);
				this.labelInfo.setVisible(false);
			}
			return this.mainPanel;
		}

		private void prepareForAnim(final Float progress, final ErrorItem item) {
			Color foreground = SubstanceColorUtilities.getInterpolatedColor(item.getType().getSelectedForeground(),
					item.getType().getForeground(), progress);
			this.labelTitle.setForeground(foreground);
			this.labelInfo.setForeground(foreground);
			this.mainPanel.setBackground(SubstanceColorUtilities.getInterpolatedColor(item.getType()
					.getSelectedBackground(), item.getType().getBackground(), progress));
			this.labelInfo.setText(item.getText());
			getHelpLabel().setVisible(true);
			this.alphaPanel.setAlpha(progress);
		}
	}

	public void addError(final ErrorType type, final String key, final String title, final String text) {
		ErrorItem ei = new ErrorItem(title, text, type, key);
		getModel().addError(ei);
	}

	public void removeAllError() {
		getModel().reset();
	}

	public void removeError(final String key) {
		getModel().removeError(key);
	}

	public boolean hasError() {
		return this.listModel.size() != 0;
	}

	public JList getList() {
		if (this.list == null) {
			this.listModel = new DefaultListModel();
			this.list = new JList(this.listModel);
			this.list.setCellRenderer(new ErrorRenderer());
			this.list.setAutoscrolls(false);
			this.list.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(final MouseEvent e) {
					listMouseReleased(e);
				}

				@Override
				public void mousePressed(final MouseEvent e) {
					listMousePressed(e);
				}
			});
			this.list.addMouseMotionListener(new MouseAdapter() {
				@Override
				public void mouseDragged(final MouseEvent e) {
					listMouseDragged(e);
				}
			});
		}
		return this.list;
	}

	private Point oldPoint;

	private void listMousePressed(final MouseEvent evt) {
		this.oldPoint = evt.getLocationOnScreen();
	}

	public JScrollPane getScroll() {
		if (this.scroll == null) {
			this.scroll = new JScrollPane(getList());
			this.scroll.setName("scroll");
			SmoothScrolling.enableFullScrolling(this.scroll);
			this.scroll.getVerticalScrollBar().putClientProperty(SicpaScrollBarUI.UNIT_INCREMENT_AUTO, Boolean.FALSE);
			this.scroll.setAutoscrolls(false);
			this.scroll.getVerticalScrollBar().setBlockIncrement(1);
		}
		return this.scroll;
	}

	public void setHelpVisible(final boolean helpVisible) {
		if (helpVisible) {
			getHelpLabel().setIcon(SubstanceCoreUtilities.getIcon("resource/32/help-browser.png"));
		} else {
			getHelpLabel().setIcon(null);
		}
		repaint();
	}

	public JLabel getHelpLabel() {
		if (this.helpLabel == null) {
			this.helpLabel = new JLabel();
		}
		return this.helpLabel;
	}

	public Map<Integer, Float> getMapAnim() {
		return this.mapAnim;
	}

	public int getItemCount() {
		return getList().getModel().getSize();
	}

	@Override
	protected void modelErrorAdded(final ScrollingErrorEvent evt) {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				DefaultScrollingErrorPanel.this.listModel.addElement(evt.getItem());
				if (DefaultScrollingErrorPanel.this.listModel.getSize() == 1) {
					DefaultScrollingErrorPanel.this.selectedIndex = 0;
					selectAnim(-1, 0);
				}
				setVisible(true);
			}
		});
	}

	@Override
	protected void modelErrorRemoved(final ScrollingErrorEvent evt) {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				int i = 0;
				while (i < DefaultScrollingErrorPanel.this.listModel.size()) {
					if (((ErrorItem) DefaultScrollingErrorPanel.this.listModel.get(i)).getKey().equals(
							evt.getItem().getKey())) {
						if (DefaultScrollingErrorPanel.this.list.getSelectedIndex() == i && i != 0) {
							DefaultScrollingErrorPanel.this.selectedIndex = i - 1;
							selectAnim(i, i - 1);
						}
						DefaultScrollingErrorPanel.this.listModel.remove(i);
					} else {
						i++;
					}
				}
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						setVisible(!getModel().getItems().isEmpty());
					}
				});
			}
		});
	}

	@Override
	public void setModel(final ScrollingErrorModel model) {
		super.setModel(model);
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (DefaultScrollingErrorPanel.this.selectAnim != null
						&& DefaultScrollingErrorPanel.this.selectAnim.getState() == TimelineState.PLAYING_FORWARD) {
					DefaultScrollingErrorPanel.this.selectAnim.abort();
				}
				DefaultScrollingErrorPanel.this.selectedIndex = -1;
				DefaultScrollingErrorPanel.this.deselectedIndex = -1;

				if (DefaultScrollingErrorPanel.this.listModel != null) {
					DefaultScrollingErrorPanel.this.listModel.clear();
					for (ErrorItem item : model.getItems()) {
						DefaultScrollingErrorPanel.this.listModel.addElement(item);
					}
				}
				DefaultScrollingErrorPanel.this.selectedIndex = model.getItems().isEmpty() ? -1 : 0;
			}
		});
	}
}
