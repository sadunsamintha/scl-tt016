package com.sicpa.standard.gui.components.dnd;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTargetContext;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.components.dialog.dropShadow.DialogWithDropShadow;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.utils.ImageUtils;
import com.sicpa.standard.gui.utils.Pair;

public class DnDDemo extends javax.swing.JFrame {

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SicpaLookAndFeelCusto.install();

				DnDSuccessCallBack cbImage = new DnDSuccessCallBack() {
					@Override
					public void dropSuccess(final DataFlavor source, final DropTargetContext context,
							final Transferable transfer, final Point location) {
						Component comp = context.getComponent();
						Image i = null;
						try {
							i = (Image) transfer.getTransferData(source);
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (comp instanceof CollagePanel) {
							((CollagePanel) comp).addImage(i, location);
						}
					}
				};

				Palette paletteLeft = new Palette();
				Palette paletteRight = new Palette();
				paletteLeft.setTitle("Palette");
				paletteRight.setTitle("Palette");

				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				CollagePanel collage = new CollagePanel();
				f.getContentPane().add(collage);

				DnDGhostManager.enableDrag(paletteLeft.img1, paletteLeft.img1);
				DnDGhostManager.enableDrag(paletteLeft.img2, paletteLeft.img2);
				DnDGhostManager.enableDrag(paletteLeft.img3, paletteLeft.img3);
				DnDGhostManager.enableDrag(paletteLeft.img4, paletteLeft.img4);

				DnDGhostManager.enableDrag(paletteRight.img1, paletteRight.img1);
				DnDGhostManager.enableDrag(paletteRight.img2, paletteRight.img2);
				DnDGhostManager.enableDrag(paletteRight.img3, paletteRight.img3);
				DnDGhostManager.enableDrag(paletteRight.img4, paletteRight.img4);

				DnDGhostManager.enableDrop(collage, DataFlavor.imageFlavor, cbImage);

				paletteLeft.setBounds(0, 0, 180, 500);
				f.setBounds(250, 0, 500, 500);
				paletteRight.setBounds(800, 0, 180, 500);

				paletteRight.setVisible(true);
				paletteLeft.setVisible(true);
				f.setVisible(true);

//				RepaintManager.currentManager(f).setDoubleBufferingEnabled(false);

			}
		});
	}

	public static class ImageTransfer extends JLabel implements Transferable {
		private Image img;

		public ImageTransfer(final Image img) {
			super(new ImageIcon(img));
			this.img = img;
		}

		@Override
		public Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException, IOException {
			if (flavor.equals(DataFlavor.imageFlavor)) {
				return this.img;
			} else {
				return new UnsupportedFlavorException(flavor);
			}
		}

		@Override
		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[] { DataFlavor.imageFlavor };
		}

		@Override
		public boolean isDataFlavorSupported(final DataFlavor flavor) {
			return flavor.equals(DataFlavor.imageFlavor);
		}
	}

	public static class Palette extends DialogWithDropShadow {
		ImageTransfer img1;
		ImageTransfer img2;
		ImageTransfer img3;
		ImageTransfer img4;

		public Palette() {
			super(null);
			initGUI();
		}

		private void initGUI() {
			getContentPane().setLayout(new MigLayout("fill,wrap 1"));
			this.img1 = new ImageTransfer(ImageUtils.getShadowedImage(ImageUtils.createRandomStrippedImage(100)));
			this.img2 = new ImageTransfer(ImageUtils.createRandomColorCirlceImage(100));
			this.img3 = new ImageTransfer(ImageUtils.getShadowedImage(ImageUtils.createRandomStrippedImage(150)));
			this.img4 = new ImageTransfer(ImageUtils.createRandomColorCirlceImage(50));

			add(this.img1);
			add(this.img2);
			add(this.img3);
			add(this.img4);
		}
	}

	public static class CollagePanel extends JPanel {
		ArrayList<Pair<Point, Image>> images;

		public CollagePanel() {
			this.images = new ArrayList<Pair<Point, Image>>();
		}

		@Override
		protected void paintComponent(final Graphics g) {
			super.paintComponent(g);
			for (Pair<Point, Image> pair : this.images) {
				g.drawImage(pair.getValue2(), pair.getValue1().x, pair.getValue1().y, null);
			}
		}

		public void addImage(final Image img, final Point p) {
			this.images.add(new Pair<Point, Image>(p, img));
			repaint();
		}
	}
}
