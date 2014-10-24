package com.sicpa.standard.sasscl.view.config.plc;

import java.awt.Image;
import java.awt.Window;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.sicpa.standard.gui.plaf.SicpaColor;
import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.components.dialog.dropShadow.DialogWithDropShadow;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.devices.camera.CameraImageEvent;

@SuppressWarnings("serial")
public class FrameCameraImage extends DialogWithDropShadow {

	protected final Map<String, CameraImagePanel> labels = new HashMap<String, CameraImagePanel>();

	public FrameCameraImage(final Window frame) {
		super(frame, true, true);
		initGUI();
	}

	private void initGUI() {
		setAlwaysOnTop(true);
		getContentPane().setLayout(new MigLayout("fill"));
        getContentPane().setBackground(SicpaColor.BLUE_DARK);
		setSize(300, 300);
	}

	public void setimage(final CameraImageEvent evt) {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {

				String source = evt.getSource().getName();
				CameraImagePanel panel = labels.get(source);
				if (panel == null) {
					panel = new CameraImagePanel(source);
					labels.put(source, panel);
					add(panel, "grow,push");
				}
				panel.setImage(evt.getCameraImage());

                if(labels.size() > 1) {
                    // Resize images
                    int targetWidth = (int) (evt.getCameraImage().getWidth(null)/2.5);
                    int targetHeight = (int) (evt.getCameraImage().getHeight(null)/2.5);
                    for (CameraImagePanel imgPanel : labels.values()) {
                        Image img = imgPanel.getImage().getImage();
                        Image scaledImg = img.getScaledInstance(targetWidth,
                                targetHeight,
                                Image.SCALE_SMOOTH);
                        imgPanel.setImage(scaledImg);
                    }
                }

				updateSize();
			}
		});
	}

	protected void updateSize() {
		int w = 0, h = 0;
		for (CameraImagePanel panel : labels.values()) {
			h = Math.max(h, panel.image.getIconHeight());
			w += panel.image.getIconWidth();
		}
		w += 50;
		h += 100;

		if (getWidth() < w || getHeight() < h) {
			setSize(w, h);
		}
	}

	public static class CameraImagePanel extends JPanel {
		JLabel labelImage;
		ImageIcon image;

		public CameraImagePanel(String name) {
			setLayout(new MigLayout("fill"));
            setBackground(SicpaColor.BLUE_DARK);
			add(new JLabel(name), "wrap");
			labelImage = new JLabel();
			add(labelImage, "grow , push");
		}

		void setImage(Image image) {
			this.image = new ImageIcon(image);
			labelImage.setIcon(this.image);
		}

        public ImageIcon getImage() {
            return image;
        }
    }
}
