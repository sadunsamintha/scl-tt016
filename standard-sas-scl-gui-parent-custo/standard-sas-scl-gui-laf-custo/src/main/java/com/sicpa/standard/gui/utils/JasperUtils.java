package com.sicpa.standard.gui.utils;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import org.jdesktop.swingx.graphics.GraphicsUtilities;

import com.sicpa.standard.gui.components.transition.impl.ShrinkTransition;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

public class JasperUtils {
	public static JFrame getCustomJasperView(final JFrame view) {
		// not possible to change the close action of the jasperViewer,
		// so let's just grab its content pane and put it and another frame and
		// voila!
		final JFrame frame = new JFrame();
		frame.setIconImage(GraphicsUtilities.createCompatibleTranslucentImage(1, 1));
		frame.setTitle(view.getTitle());
		frame.setSize(view.getSize());
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(view.getContentPane(), BorderLayout.CENTER);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				ShrinkTransition t = new ShrinkTransition(frame);
				t.setEndAction(new AbstractAction() {
					@Override
					public void actionPerformed(final ActionEvent e) {
					}
				});
				t.startCloseTransition();
			}
		});
		prepareJasperView((JComponent) frame.getContentPane());

		return frame;
	}

	private static void prepareJasperView(final JComponent comp) {
		// show magnifiyng glass on scroll pane
		// increase size of the panel button
		SicpaLookAndFeelCusto.flagAsDefaultArea(comp);
		for (Component child : comp.getComponents()) {
			if (child instanceof JScrollPane) {
				SicpaLookAndFeelCusto.turnOnScrollPreview((JScrollPane) child);
				((JScrollPane) child).setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
				((JScrollPane) child).setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			} else if (child instanceof JComboBox) {
				child.setPreferredSize(new Dimension(200, 50));
				((JComboBox) child).setEditable(false);
			} else if (child instanceof AbstractButton) {
				child.setPreferredSize(new Dimension(50, 50));
			} else if (child instanceof JTextField) {
				child.setPreferredSize(new Dimension(50, 50));
			}
			if (child instanceof JComponent) {
				prepareJasperView((JComponent) child);
			}
		}
		if (!(comp instanceof JComboBox)) {
			if (comp.getComponentCount() >= 2) {
				if (comp.getComponent(2).getClass() == JButton.class) {// only
																		// if
																		// jbutton
																		// (not
																		// using
																		// instanceof
																		// else
																		// it
																		// remove
																		// some
																		// scrolling
																		// button
																		// )
					comp.remove(2);
				}
			}
		}
	}
}
