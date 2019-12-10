package com.sicpa.standard.gui.screen.machine.component.selectionSummary;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSeparator;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.I18n.GUIi18nManager;
import com.sicpa.standard.gui.components.label.AutoScaledImage;
import com.sicpa.standard.gui.components.text.MultiLineLabel;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.SelectableItem;
import com.sicpa.standard.gui.utils.TextUtils;
import com.sicpa.standard.gui.utils.ThreadUtils;

public class DefaultSummaryPanel extends AbstractSummaryPanel {

	private static final long serialVersionUID = 1L;

	public enum TooLargePolicy {
		DECREASE_FONT_SIZE, WRAP, NONE
	};

	private TooLargePolicy tooLargePolicy;
	public static final String I18N_TITLE = GUIi18nManager.SUFFIX + "machine.selectionSummary.title";
	protected Color labelColor = SicpaColor.BLUE_DARK;
	protected Color titleColor = SicpaColor.BLUE_DARK;

	public DefaultSummaryPanel() {
		super(new SummaryModel());
		this.tooLargePolicy = TooLargePolicy.WRAP;
		initGUI();
	}

	private void initGUI() {
		setLayout(new MigLayout("fill,gap 0 0 0 0, inset 0 0 0 0"));
		setName("summaryPanel");
	}

	@Override
	protected void modelSummaryChanged(final SummaryEvent event) {
		buildSummary(event.getSummary());
	}

	public void buildSummary(final SelectableItem[] items) {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				startBuildind();
				if (items != null) {
					addTitle();
					for (SelectableItem item : items) {
						addItem(item);
					}
				}
				finishBuilding();
			}
		});
	}

	protected void startBuildind() {
		removeAll();
	}

	protected void finishBuilding() {
		add(new JLabel(" "), "pushy");
		revalidate();
		// SwingUtilities.invokeLater(new Runnable() {
		// @Override
		// public void run() {
		// invalidate();
		// validate();
		// }
		// });
	}

	protected void addTitle() {
		JLabel title = new JLabel(GUIi18nManager.get(I18N_TITLE));
		title.setName(I18N_TITLE);
		title.setFont(title.getFont().deriveFont(20f));
		title.setForeground(titleColor);
		add(title, "gapleft 5,gaptop 5,spanx, split 2");
		JSeparator sepa = new JSeparator(JSeparator.HORIZONTAL);
		add(sepa, "growx,gap right 5,wrap");
	}

	protected void addItem(final SelectableItem item) {
		if (item.isShownOnSummary()) {
			String text = item.getFormatedTextForSummary();
			if (text != null && !text.isEmpty()) {
				JComponent label;
				if (this.tooLargePolicy == TooLargePolicy.WRAP) {
					label = new MultiLineLabel(text);
				} else {
					label = new JLabel(text);
					if (this.tooLargePolicy == TooLargePolicy.DECREASE_FONT_SIZE) {
						Font f = TextUtils.getOptimumFont(text, getWidth() - 20, label.getFont());
						label.setFont(f);
					}
				}
				label.setForeground(labelColor);
				add(label, "gap left 20,grow, wrap");
			}
		}
		if (item.getImage() != null && item.getImage().getImage() != null) {
			JComponent imagecomp = generateImageSummaryItem(item);
			Dimension imgCompDim = imagecomp.getPreferredSize();
			if (imgCompDim.getWidth() > getWidth() - 40) {
				imgCompDim.width = getWidth() - 40;
				imagecomp.setPreferredSize(imgCompDim);
			}
			add(imagecomp, "gap left 20, wrap,grow");
		}
	}

	protected JComponent generateImageSummaryItem(SelectableItem item) {
		AutoScaledImage label = new AutoScaledImage(SicpaLookAndFeelCusto.maybeGetShadowedImage(item.getImage().getImage()));
		label.setPreferredSize(new Dimension(item.getImage().getIconWidth(), item.getImage().getIconHeight()));
		return label;
	}

	public void setTooLargePolicy(final TooLargePolicy tooLargePolicy) {
		this.tooLargePolicy = tooLargePolicy;
	}

	public TooLargePolicy getTooLargePolicy() {
		return this.tooLargePolicy;
	}

	public void setLabelColor(Color labelColor) {
		this.labelColor = labelColor;
	}

	public void setTitleColor(Color titleColor) {
		this.titleColor = titleColor;
	}
}
