package com.sicpa.standard.gui.plaf.colorScheme.editor;

import java.awt.Color;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.miginfocom.swing.MigLayout;

import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.SubstanceSkin;
import org.pushingpixels.substance.api.SubstanceSkin.ColorSchemes;
import org.pushingpixels.substance.api.painter.decoration.ArcDecorationPainter;
import org.pushingpixels.substance.api.painter.decoration.BrushedMetalDecorationPainter;
import org.pushingpixels.substance.api.painter.decoration.ClassicDecorationPainter;
import org.pushingpixels.substance.api.painter.decoration.FlatDecorationPainter;
import org.pushingpixels.substance.api.painter.decoration.Glass3DDecorationPainter;
import org.pushingpixels.substance.api.painter.decoration.MarbleNoiseDecorationPainter;
import org.pushingpixels.substance.api.painter.decoration.MatteDecorationPainter;
import org.pushingpixels.substance.api.painter.decoration.SubstanceDecorationPainter;
import org.pushingpixels.substance.api.painter.fill.ClassicFillPainter;
import org.pushingpixels.substance.api.painter.fill.GlassFillPainter;
import org.pushingpixels.substance.api.painter.fill.MatteFillPainter;
import org.pushingpixels.substance.api.painter.fill.StandardFillPainter;
import org.pushingpixels.substance.api.painter.fill.SubduedFillPainter;
import org.pushingpixels.substance.api.painter.fill.SubstanceFillPainter;

import com.sicpa.standard.gui.components.renderers.OptimumFontTableCellRenderer;
import com.sicpa.standard.gui.components.table.BeanReaderJTable;
import com.sicpa.standard.gui.painter.SicpaDecorationPainter;
import com.sicpa.standard.gui.painter.SicpaFillPainter;
import com.sicpa.standard.gui.plaf.FrameComponentDemo;
import com.sicpa.standard.gui.plaf.SicpaFont;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelConfig;
import com.sicpa.standard.gui.plaf.SicpaSkin;
import com.sicpa.standard.gui.plaf.colorScheme.SicpaColorScheme;
import com.sicpa.standard.gui.plaf.colorScheme.SicpaColorScheme.SicpaColorSchemeSetter;
import com.sicpa.standard.gui.utils.JTableUtils;

public class FrameColorSchemeEditor extends JFrame {
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				FrameColorSchemeEditor f = new FrameColorSchemeEditor();
				f.setVisible(true);

				FrameComponentDemo demo = new FrameComponentDemo();
				demo.setBounds(200, 196, 1302, 831);
				demo.setVisible(true);
			}
		});
	}

	private BeanReaderJTable<SubstanceColorScheme> table;
	private JButton buttonShowModif;
	private JButton buttonShowConfig;
	private JComboBox comboFillPainter;
	private JComboBox comboDecoPainter;

	public FrameColorSchemeEditor() {
		initGUI();
	}

	private void initGUI() {
		SicpaLookAndFeelCusto.turnOnMemoryManagementWidget(this);
		getContentPane().setLayout(new MigLayout());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().add(new JScrollPane(getTable()), "grow,push,span");
		getContentPane().add(getComboFillPainter(), "wrap,grow");
		getContentPane().add(getComboDecoPainter(), "wrap,grow");
		getContentPane().add(getButtonShowModif(), "span,split 2");
		getContentPane().add(getButtonShowConfig());

		setSize(1300, 768);
	}

	public JButton getButtonShowModif() {
		if (this.buttonShowModif == null) {
			this.buttonShowModif = new JButton("update LAF");
			this.buttonShowModif.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonShowModifActionPerformed();
				}
			});
		}
		return this.buttonShowModif;
	}

	private void buttonShowModifActionPerformed() {

		Map<String, SubstanceColorScheme> map = new HashMap<String, SubstanceColorScheme>();
		for (SubstanceColorScheme cs : this.table.getAllObjects()) {
			map.put(cs.getDisplayName(), cs);
		}

		SicpaSkin skin = new SicpaSkin(map);

		try {
			UIManager.setLookAndFeel(new SicpaLookAndFeelCusto(skin).laf);
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		SicpaLookAndFeelCusto.setFont(SicpaFont.getFont(18));
		SicpaLookAndFeelCusto.setMessageFont(SicpaFont.getFont(25));
		ToolTipManager.sharedInstance().setDismissDelay(50000);
		updateUI();
	}

	public JButton getButtonShowConfig() {
		if (this.buttonShowConfig == null) {
			this.buttonShowConfig = new JButton("show config");
			this.buttonShowConfig.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonShowConfigActionPerformed();
				}
			});
		}
		return this.buttonShowConfig;
	}

	private void buttonShowConfigActionPerformed() {
		JFrame f = new JFrame();
		JTextArea text = new JTextArea();
		for (SubstanceColorScheme cs : getTable().getAllObjects()) {
			text.append(cs.toString());
			text.append("\n");
		}

		f.getContentPane().add(new JScrollPane(text));
		f.setSize(800, 600);
		f.setVisible(true);

	}

	public BeanReaderJTable<SubstanceColorScheme> getTable() {
		if (this.table == null) {
			this.table = new BeanReaderJTable<SubstanceColorScheme>(new String[] { "displayName", "UltraLightColor",
					"ExtraLightColor", "LightColor", "MidColor", "DarkColor", "UltraDarkColor", "ForegroundColor" });
			this.table.setRowHeight(34);
			this.table.getTableHeader().setReorderingAllowed(false);
			loadSicpaColorScheme();
			JTableUtils.lockWidth(this.table, 0, 230);
			this.table.getColumnModel().getColumn(0).setCellRenderer(new OptimumFontTableCellRenderer(210));
			this.table.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(final MouseEvent e) {
					tableMouseCLicked(e);
				}
			});
			this.table.addPropertyChangeListener("UI", new PropertyChangeListener() {
				@Override
				public void propertyChange(final PropertyChangeEvent evt) {
					FrameColorSchemeEditor.this.table.setRowHeight(34);
				}
			});
		}
		return this.table;
	}

	private void tableMouseCLicked(final MouseEvent evt) {
		if (evt.getClickCount() == 2) {
			int col = getTable().getSelectedColumn();
			int row = getTable().getSelectedRow();

			if (col > 0) {
				String cs = getTable().getSelectedObject().getDisplayName();
				String kind = getTable().getColumnName(col);
				Color newColor = getNewColor((Color) getTable().getValueAt(row, col), cs + " " + kind);
				SicpaColorSchemeSetter setter = (SicpaColorSchemeSetter) getTable().getSelectedObject();

				switch (col) {
				case 1:
					setter.setUltraLightColor(newColor);
					break;
				case 2:
					setter.setExtraLightColor(newColor);
					break;
				case 3:
					setter.setLightColor(newColor);
					break;
				case 4:
					setter.setMidColor(newColor);
					break;
				case 5:
					setter.setDarkColor(newColor);
					break;
				case 6:
					setter.setUltraDarkColor(newColor);
					break;
				case 7:
					setter.setForegroundColor(newColor);
					break;
				}
				getTable().repaint();
			}
		}
	}

	private Color getNewColor(final Color defaultColor, final String title) {
		Color c = JColorChooser.showDialog(this, title, defaultColor);
		return c != null ? c : defaultColor;
	}

	private void loadSicpaColorScheme() {
		ColorSchemes schemes = SubstanceSkin.getColorSchemes(SicpaLookAndFeelConfig.getUrlColorSchemeFile());
		for (int i = 0; i < schemes.size(); i++) {
			getTable().addRow(SicpaColorScheme.create(schemes.get(i)));
		}
	}

	public JComboBox getComboFillPainter() {
		if (this.comboFillPainter == null) {
			this.comboFillPainter = new JComboBox();
			this.comboFillPainter.addItem(new SicpaFillPainter());
			this.comboFillPainter.addItem(new ClassicFillPainter());
			this.comboFillPainter.addItem(new GlassFillPainter());
			this.comboFillPainter.addItem(new MatteFillPainter());
			this.comboFillPainter.addItem(new StandardFillPainter());
			this.comboFillPainter.addItem(new SubduedFillPainter());

			this.comboFillPainter.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					((SicpaSkin) SubstanceLookAndFeel.getCurrentSkin())
							.setFillPainter((SubstanceFillPainter) FrameColorSchemeEditor.this.comboFillPainter
									.getSelectedItem());
					updateUI();
				}
			});
		}
		return this.comboFillPainter;
	}

	public JComboBox getComboDecoPainter() {
		if (this.comboDecoPainter == null) {
			this.comboDecoPainter = new JComboBox();
			this.comboDecoPainter.addItem(new SicpaDecorationPainter());
			this.comboDecoPainter.addItem(new ArcDecorationPainter());
			this.comboDecoPainter.addItem(new BrushedMetalDecorationPainter());
			this.comboDecoPainter.addItem(new ClassicDecorationPainter());
			this.comboDecoPainter.addItem(new FlatDecorationPainter());
			this.comboDecoPainter.addItem(new Glass3DDecorationPainter());
			this.comboDecoPainter.addItem(new MarbleNoiseDecorationPainter());
			this.comboDecoPainter.addItem(new MatteDecorationPainter());

			this.comboDecoPainter.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					((SicpaSkin) SubstanceLookAndFeel.getCurrentSkin())
							.setDecorationPainter((SubstanceDecorationPainter) FrameColorSchemeEditor.this.comboDecoPainter
									.getSelectedItem());
					updateUI();
				}
			});
		}
		return this.comboDecoPainter;
	}

	private void updateUI() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				for (Frame f : JFrame.getFrames()) {
					f.repaint();
				}
			}
		});
	}
}
