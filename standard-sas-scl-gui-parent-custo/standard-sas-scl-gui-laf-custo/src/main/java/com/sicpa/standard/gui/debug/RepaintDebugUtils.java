package com.sicpa.standard.gui.debug;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.ext.DebugRepaintingUI;

public class RepaintDebugUtils {

	public static void installDebugLayer(JFrame w) {
		JComponent comp = (JComponent) w.getContentPane();
		JXLayer<JComponent> layer = new JXLayer<JComponent>(comp);
		final DebugRepaintingUI dp = new DebugRepaintingUI();
		layer.setUI(dp);
		JPanel p = new JPanel(new BorderLayout());
		p.add(layer);
		w.setContentPane(p);
	}

	public static JComponent wrapComponent(JComponent comp) {
		JXLayer<JComponent> layer = new JXLayer<JComponent>(comp);
		final DebugRepaintingUI dp = new DebugRepaintingUI();
		layer.setUI(dp);
		return layer;
	}
}
