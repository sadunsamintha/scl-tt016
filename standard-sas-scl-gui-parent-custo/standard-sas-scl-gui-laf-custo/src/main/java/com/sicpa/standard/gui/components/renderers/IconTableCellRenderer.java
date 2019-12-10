package com.sicpa.standard.gui.components.renderers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.pushingpixels.substance.internal.utils.HashMapKey;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.utils.PaintUtils;
import com.sicpa.standard.gui.utils.lazyMap.LazyResettableHashMap;

public class IconTableCellRenderer extends SicpaTableCellRenderer {

	private static final long serialVersionUID = 1L;

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				JFrame f = new JFrame();
				JTable table = new JTable();
				table.setRowHeight(50);
				DefaultTableModel model = new DefaultTableModel() {
					private static final long serialVersionUID = 1L;

					@Override
					public boolean isCellEditable(final int row, final int column) {
						return false;
					}
				};
				model.addColumn("icon");
				model.addColumn("col 2");

				model.addRow(new Object[] { getImageForMain(), "" });
				model.addRow(new Object[] { getImageForMain(), "" });
				model.addRow(new Object[] { getImageForMain(), "" });
				model.addRow(new Object[] { getImageForMain(), "" });

				table.setModel(model);

				table.getColumnModel().getColumn(0).setCellRenderer(new IconTableCellRenderer());

				f.getContentPane().add(new JScrollPane(table));
				f.setSize(500, 500);
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setVisible(true);
			}
		});
	}

	private static BufferedImage getImageForMain() {
		BufferedImage image = GraphicsUtilities.createCompatibleTranslucentImage(500, 500);
		Graphics g = image.createGraphics();
		Random r = new Random();

		PaintUtils.fillCircle(g, 500, new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255)), 0, 0);
		g.dispose();
		return image;
	}

	private LazyResettableHashMap<ImageIcon> mapImage;

	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
			final boolean hasFocus, final int row, final int column) {
		JLabel res = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		if (value != null) {
			if (this.mapImage == null) {
				this.mapImage = new LazyResettableHashMap<ImageIcon>();
			}
			ImageIcon icon = this.mapImage.get(new HashMapKey(value.hashCode()));
			if (icon == null) {
				Image thumb = null;
				int h = table.getRowHeight();
				int w = table.getColumnModel().getColumn(table.convertColumnIndexToModel(column)).getWidth();

				if (value instanceof Image) {

					Image img = (Image) value;
					float ih = img.getHeight(null);
					float iw = img.getWidth(null);

					float factorH = ih / h;
					float factorW = iw / w;

					float factor = Math.max(factorH, factorW);

					thumb = getThumbnail(img, (int) (img.getWidth(null) / factor), (int) (img.getHeight(null) / factor));

				} else if (value instanceof Icon) {
					Icon iconV=(Icon)value;
					int ih = iconV.getIconHeight();
					int iw = iconV.getIconWidth();

					float factorH = ih / h;
					float factorW = iw / w;

					float factor = Math.max(factorH, factorW);
					thumb = getThumbnail(iconV, (int) (iconV.getIconWidth() / factor), (int) (iconV.getIconHeight() / factor));

				}
				icon = new ImageIcon(thumb);
				this.mapImage.put(new HashMapKey(value), icon);
			}
			res.setIcon(icon);
		} else {
			res.setIcon(null);
		}
		res.setText("");

		return res;
	}

	public Image getThumbnail(final Image img, final int w, final int h) {

		BufferedImage res = null;

		if (img.getWidth(null) <= w && img.getHeight(null) <= h) {
			return img;
		}

		if (img instanceof BufferedImage) {
			res = GraphicsUtilities.createThumbnail((BufferedImage) img, w, h);
		} else {
			BufferedImage buff = GraphicsUtilities.createCompatibleTranslucentImage(img.getWidth(null), img
					.getHeight(null));
			Graphics g = buff.createGraphics();
			g.drawImage(img, 0, 0, null);
			g.dispose();
			res = GraphicsUtilities.createThumbnail(buff, w, h);
		}
		return res;
	}

	public Image getThumbnail(final Icon icon, final int w, final int h) {

		if (icon instanceof ImageIcon) {
			return getThumbnail(((ImageIcon) icon).getImage(), w, h);
		} else {
			BufferedImage buff = GraphicsUtilities.createCompatibleTranslucentImage(icon.getIconWidth(), icon
					.getIconHeight());
			Graphics g = buff.createGraphics();
			icon.paintIcon(null, g, 0, 0);
			g.dispose();
			return getThumbnail(buff, w, h);
		}
	}

	public void clearCache() {
		this.mapImage.reset();
	}
}
