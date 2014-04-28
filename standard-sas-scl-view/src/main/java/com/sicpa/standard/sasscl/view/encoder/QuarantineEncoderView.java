package com.sicpa.standard.sasscl.view.encoder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.sasscl.common.storage.QuarantineReason;

public class QuarantineEncoderView extends JPanel {

	private static final long serialVersionUID = 1L;
	
	public void refresh() {
		removeAll();
		setLayout(new MigLayout("wrap 1"));
		for (String enc : getEncodersInQuarantine()) {
			JLabel label = new JLabel(enc);
			label.setForeground(SicpaColor.RED);
			add(label);
		}

		revalidate();
		repaint();
	}

	protected List<String> getEncodersInQuarantine() {
		List<String> res = new ArrayList<String>();
		res.addAll(getEncodersInQuarantine(new File("quarantine/" + QuarantineReason.LOAD_ERROR.getSubFolder())));
		res.addAll(getEncodersInQuarantine(new File("quarantine/" + QuarantineReason.INVALID_ENCODER.getSubFolder())));
		return res;
	}

	protected List<String> getEncodersInQuarantine(File f) {
		List<String> res = new ArrayList<String>();

		if (f.isDirectory()) {
			String[] files = f.list();
			if (files != null)
				for (String aFileName : files) {
					res.add(aFileName);
				}
		}
		return res;
	}
}