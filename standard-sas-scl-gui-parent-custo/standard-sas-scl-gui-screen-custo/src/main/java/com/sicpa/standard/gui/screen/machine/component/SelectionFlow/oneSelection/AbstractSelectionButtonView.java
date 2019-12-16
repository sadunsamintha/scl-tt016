package com.sicpa.standard.gui.screen.machine.component.SelectionFlow.oneSelection;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.swingx.graphics.GraphicsUtilities;

import com.sicpa.standard.gui.components.buttons.toggleButtons.ToggleImageAndTextButton;
import com.sicpa.standard.gui.components.buttons.toggleButtons.ToggleImageButton;
import com.sicpa.standard.gui.components.scroll.SmallScrollBar;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.SelectableItem;
import com.sicpa.standard.gui.utils.ImageUtils;
import com.sicpa.standard.gui.utils.TextUtils;

public abstract class AbstractSelectionButtonView extends AbstractSelectionView {
	private static final long serialVersionUID = 1L;

	private int column;
	private JPanel panel;
	private boolean alreadySelected = false;

	public AbstractSelectionButtonView(final int column, final SelectableItem[] items) {
		this.column = column;
		setSelectableItems(items);
	}

	public AbstractSelectionButtonView() {
	}

	public void setColumn(final int column) {
		this.column = column;
	}

	public void resetSelection() {
		this.alreadySelected = false;
		for (JToggleButton b : this.buttons) {
			b.setSelected(false);
		}
	}

	private List<JToggleButton> buttons = new ArrayList<JToggleButton>();

	public void setSelectableItems(final SelectableItem[] items) {
		this.alreadySelected = false;
		if (this.column <= 0) {
			throw new IllegalArgumentException("column number have to be > 0");
		}
		removeAll();
		this.panel = null;
		setLayout(new BorderLayout());
		JScrollPane scroll = new JScrollPane(getPanel());
		JXLayer<JScrollPane> layer = SmallScrollBar.createLayerSmallScrollBar(scroll);
		SicpaLookAndFeelCusto.flagAsWorkArea(this);
		add(layer, BorderLayout.CENTER);
		String name = "";

		this.buttons.clear();
		for (SelectableItem item : items) {
			if (item != null) {
				if (item.getImage() != null && (item.getText() == null || item.getText().isEmpty())) {
					this.buttons.add(new ButtonItem(item));
				} else {
					this.buttons.add(new ButtonImageItem(item));
				}
				name += item.getId() + ':';
			}
		}

		for (JToggleButton b : this.buttons) {
			if (b instanceof ButtonItem) {
				getPanel().add(b, "grow");
			} else {

				int buttonH = getButtonHeight(b.getText());
				String textBtconstraints = "h " + buttonH + "! ,grow";

				int percentMaxW = (100 / column) - 1;
				textBtconstraints += ", w " + percentMaxW + "%!";

				int gap = 10;
				if (b instanceof ButtonImageItem) {
					BufferedImage img = ((ButtonImageItem) b).getOriginalIcon();
					if (img != null) {
						int imgH = img.getHeight();
						if (imgH > buttonH - gap) {
							img = ImageUtils.createThumbnailKeepRatio(img, img.getWidth(), buttonH - gap);
							((ButtonImageItem) b).setOriginalIcon(img);
							((ButtonImageItem) b).resetImage();
						}
					}
				}

				getPanel().add(b, textBtconstraints);
			}
		}
		setName("buttonFlowView:" + name);
	}

	@Override
	public void beforePreviewInit() {
		for (Component comp : getPanel().getComponents()) {
			if (comp instanceof ToggleImageAndTextButton) {
				((ToggleImageAndTextButton) comp).resetImage();
			}
		}
	}

	protected int getButtonHeight() {
		return 100;
	}

	protected int getButtonHeight(final String text) {

		if (this.dummyImage == null) {
			this.dummyImage = GraphicsUtilities.createCompatibleImage(1, 1);
		}
		Graphics g = this.dummyImage.createGraphics();
		g.setFont(getFont());
		int h = TextUtils.getMultiLineStringBounds(text, g).height + 20;
		g.dispose();
		if (h > getButtonHeight()) {
			return h;
		} else {
			return getButtonHeight();
		}
	}

	public JPanel getPanel() {

		if (this.panel == null) {
			this.panel = new JPanel();
			this.panel.setOpaque(false);
			this.panel.setLayout(new MigLayout("inset " + getPanelInsets() + ",fill,wrap " + this.column));
		}
		return this.panel;
	}

	protected String getPanelInsets() {
		return "0 0 0 0";
	}

	private BufferedImage dummyImage;

	protected class ButtonImageItem extends ToggleImageAndTextButton {
		private static final long serialVersionUID = 1L;

		public ButtonImageItem(final SelectableItem item) {
			super(getTextForButton(item.getText()), item.getImage() == null ? null : item.getImage().getImage());
			setRatio(getRatio());
			setActionAfterAnimation(true);
			addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					fireSelectionChanged(item);
				}
			});
			setName("buttonItem:" + item.getId());
		}

		@Override
		protected void fireActionPerformed(final ActionEvent event) {
			if (AbstractSelectionButtonView.this.alreadySelected) {
				return;
			}
			AbstractSelectionButtonView.this.alreadySelected = true;
			super.fireActionPerformed(event);
		}

		@Override
		protected void fireActionPerformedNoWait(final ActionEvent event) {
			if (isShowing()) {
				super.fireActionPerformedNoWait(event);
			}
		}
	}

	protected class ButtonItem extends ToggleImageButton {
		private static final long serialVersionUID = 1L;

		public ButtonItem(final SelectableItem item) {
			super(SicpaLookAndFeelCusto.maybeGetShadowedImage(item.getImage().getImage()));
			setRatio(getRatio());
			setAlphaMin(1f);
			setSelectionRingColor(SicpaColor.GREEN_DARK);
			setActionAfterAnimation(true);
			addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					fireSelectionChanged(item);
				}
			});
			setName("buttonItem:" + item.getId());
		}

		@Override
		protected void fireActionPerformed(final ActionEvent event) {
			if (AbstractSelectionButtonView.this.alreadySelected) {
				return;
			}
			AbstractSelectionButtonView.this.alreadySelected = true;
			super.fireActionPerformed(event);
		}

		@Override
		protected void fireActionPerformedNoWait(final ActionEvent event) {
			if (isShowing()) {
				super.fireActionPerformedNoWait(event);
			}
		}
	}

	protected float getRatio() {
		return 0.8f;
	}

	public boolean isAlreadySelected() {
		return this.alreadySelected;
	}

	protected String getTextForButton(final String t) {
		return TextUtils.getWrappedLFText(t, getMaxButtonWidth(), getFont());
	}

	/**
	 * use to know when to wrap the text
	 */
	protected abstract int getMaxButtonWidth();
}
