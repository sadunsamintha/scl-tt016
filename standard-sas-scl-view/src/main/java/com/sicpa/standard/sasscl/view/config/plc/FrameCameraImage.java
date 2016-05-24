package com.sicpa.standard.sasscl.view.config.plc;

import java.awt.Image;
import java.awt.Window;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.components.dialog.dropShadow.DialogWithDropShadow;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.gui.utils.ImageUtils;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.devices.camera.CameraImageEvent;

@SuppressWarnings("serial")
public class FrameCameraImage extends DialogWithDropShadow {

	private static final int W_EXTRA_SIZE = 50;
	private static final int H_EXTRA_SIZE = 100;

	private static final int MAX_IMAGE_H = 450;
	private static final int MAX_IMAGE_W = 450;

	private final Map<String, CameraImagePanel> imageByLine = new HashMap<>();

	public FrameCameraImage(Window frame) {
		super(frame, true, true);
		initGUI();
	}

	private void initGUI() {
		setAlwaysOnTop(true);
		getContentPane().setLayout(new MigLayout("fill"));
		getContentPane().setBackground(SicpaColor.BLUE_ULTRA_LIGHT);
		setSize(300, 300);
	}

	public void setImage(CameraImageEvent evt) {
		ThreadUtils.invokeLater(() -> {
			String source = evt.getSource().getName();
			CameraImagePanel panel = getImagePanelFromSource(source);
			panel.setImage(evt.getCameraImage());
			resizeImagesIfTooLarge();
			updateWindowSize();
		});
	}

	private CameraImagePanel getImagePanelFromSource(String source) {
		CameraImagePanel panel = imageByLine.get(source);
		if (panel == null) {
			panel = createAndAddCameraImagePanel(source);
		}
		return panel;
	}

	private CameraImagePanel createAndAddCameraImagePanel(String source) {
		CameraImagePanel panel = new CameraImagePanel(source);
		imageByLine.put(source, panel);
		add(panel, "grow,push");
		return panel;
	}

	private void resizeImagesIfTooLarge() {
		for (CameraImagePanel imgPanel : imageByLine.values()) {
			Image img = imgPanel.getImage();
			int w = img.getWidth(null);
			int h = img.getHeight(null);
			if (w > MAX_IMAGE_W || h > MAX_IMAGE_H) {
				Image scaledImg = ImageUtils.changeSizeKeepRatio(img, MAX_IMAGE_W, MAX_IMAGE_H);
				imgPanel.setImage(scaledImg);
			}
		}
	}

	private void updateWindowSize() {
		int w = 0, h = 0;
		for (CameraImagePanel panel : imageByLine.values()) {
			h = Math.max(h, panel.imageIcon.getIconHeight());
			w += panel.imageIcon.getIconWidth();
		}
		w += W_EXTRA_SIZE;
		h += H_EXTRA_SIZE;

		if (getWidth() < w || getHeight() < h) {
			setSize(w, h);
		}
	}

	private static class CameraImagePanel extends JPanel {
		JLabel labelImage;
		ImageIcon imageIcon;

		CameraImagePanel(String name) {
			setLayout(new MigLayout("fill"));
			setBackground(SicpaColor.BLUE_ULTRA_LIGHT);
			add(new JLabel(name), "wrap");
			labelImage = new JLabel();
			add(labelImage, "grow , push");
		}

		void setImage(Image image) {
			imageIcon = new ImageIcon(image);
			labelImage.setIcon(imageIcon);
		}

		Image getImage() {
			return imageIcon.getImage();
		}
	}
}
