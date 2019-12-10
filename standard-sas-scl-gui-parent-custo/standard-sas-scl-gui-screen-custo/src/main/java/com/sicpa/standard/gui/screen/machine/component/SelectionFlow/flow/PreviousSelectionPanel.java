package com.sicpa.standard.gui.screen.machine.component.SelectionFlow.flow;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Point;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXImagePanel;
import org.jdesktop.swingx.JXImagePanel.Style;

import com.sicpa.standard.gui.I18n.GUIi18nManager;
import com.sicpa.standard.gui.components.buttons.shape.DirectionButton;
import com.sicpa.standard.gui.components.buttons.shape.DirectionButton.Direction;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.gui.plaf.SicpaFont;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.SelectableItem;
import com.sicpa.standard.gui.utils.PaintUtils;
import com.sicpa.standard.gui.utils.ThreadUtils;

public class PreviousSelectionPanel extends JPanel {

	public static final String I18N_SELECT_PREVIOUS_LABEL = GUIi18nManager.SUFFIX
			+ "machine.selectionFlow.previousSelectionLabel";
	public static final String I18N_NO_PREVIOUS_VALUES = GUIi18nManager.SUFFIX
			+ "machine.selectionFlow.noPreviousValues";

	private JButton buttonSelectOld;
	private JPanel panelValues;

	public PreviousSelectionPanel() {
		setLayout(new MigLayout("fill"));
	}

	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setFont(getFont());
		PaintUtils.turnOnAntialias(g2);

		final Point start = new Point(0, 0);
		final Point end = new Point(0, getHeight());
		final float[] fractions = new float[] { 0, 0.5f, 1 };
		final Color[] colors = new Color[] { SicpaColor.BLUE_LIGHT, SicpaColor.BLUE_LIGHT.darker(),
				SicpaColor.BLUE_LIGHT };

		final LinearGradientPaint lgp = new LinearGradientPaint(start, end, fractions, colors);
		g2.setPaint(lgp);
		g2.setComposite(AlphaComposite.SrcOver.derive(0.2f));
		g2.fillRect(0, 0, getWidth(), getHeight());
		g2.dispose();
	}

	public JButton getButtonSelectOld() {

		if (this.buttonSelectOld == null) {
			this.buttonSelectOld = new DirectionButton(Direction.RIGHT);
			this.buttonSelectOld.setName("selectPrevious");
		}
		return this.buttonSelectOld;
	}

	public JPanel getPanelValues() {
		if (this.panelValues == null) {
			this.panelValues = new JPanel();
			this.panelValues.setOpaque(false);
		}
		return this.panelValues;
	}

	private JLabel labelPrevious;

	public JLabel getLabelPrevious() {
		if (this.labelPrevious == null) {
			this.labelPrevious = new JLabel(GUIi18nManager.get(I18N_SELECT_PREVIOUS_LABEL));
			this.labelPrevious.setName(I18N_SELECT_PREVIOUS_LABEL);
			this.labelPrevious.setForeground(SicpaColor.BLUE_DARK);
			this.labelPrevious.setFont(SicpaFont.getFont(10));
		}
		return this.labelPrevious;
	}

	private JLabel labelNoPrevious;

	public JLabel getLabelNoPrevious() {
		if (this.labelNoPrevious == null) {
			this.labelNoPrevious = new JLabel(GUIi18nManager.get(I18N_NO_PREVIOUS_VALUES));
			this.labelNoPrevious.setName(I18N_NO_PREVIOUS_VALUES);
			this.labelNoPrevious.setForeground(SicpaColor.BLUE_DARK);
			this.labelNoPrevious.setFont(SicpaFont.getFont(10));
		}

		return this.labelNoPrevious;
	}

	boolean prefSet = false;

	protected void populate(final SelectableItem[] items) {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				// prefset set to false so that the panel will follow the size of the components added
				prefSet = false;
				removeAll();
				if (items == null) {
					// setVisible(false);
					add(getLabelPrevious(), "gap left 10");
					add(getLabelNoPrevious(), "");
					return;
				}
				setVisible(true);
				boolean first = true;
				getPanelValues().removeAll();

				if (items != null && items.length > 0) {

					add(getLabelPrevious(), "gap left 10");
					add(getPanelValues(), "grow");
					getPanelValues().setLayout(new MigLayout(""));

					for (SelectableItem item : items) {
						{
							if (!first) {
								JLabel sepa = new JLabel(" - ");
								sepa.setForeground(SicpaColor.BLUE_DARK);
								sepa.setFont(SicpaFont.getFont(10));
								getPanelValues().add(sepa);
							} else {
								first = false;
							}
						}
						{
							JLabel labelValueText = new JLabel(item.getText());
							labelValueText.setFont(SicpaFont.getFont(10));
							labelValueText.setForeground(SicpaColor.BLUE_DARK);
							getPanelValues().add(labelValueText, "pushy");
							if (item.getImage() != null) {
								JXImagePanel panelImage = new JXImagePanel();
								panelImage.setOpaque(false);
								panelImage.setImage(SicpaLookAndFeelCusto.maybeGetShadowedImage(item.getImage().getImage()));
								panelImage.setStyle(Style.SCALED_KEEP_ASPECT_RATIO);
								getPanelValues().add(panelImage, "w ::200 ,pushy");
							}

						}
					}
					add(getButtonSelectOld(), "east,gap right 5, h 40! , w 100!");
				}
				setPreferredSize(new Dimension((int) getPreferredSize().getWidth(), 0));
				//pref set to true so the component will follow the size setted
				prefSet = true;
			}
		});
	}

	@Override
	public boolean isPreferredSizeSet() {
		return prefSet;
	}
}
