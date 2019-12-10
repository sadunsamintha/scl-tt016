package com.sicpa.standard.gui.demo.components.swingx;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jdesktop.swingx.JXImagePanel;
import org.jdesktop.swingx.JXImagePanel.Style;

import com.sicpa.standard.gui.components.renderers.SicpaTreeCellRenderer;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.screen.machine.AbstractMachineFrame;

public class JXImapePanelDemoFrame extends javax.swing.JFrame {

	JXImagePanel imagePanel;
	JComboBox jcbStyle;

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SicpaLookAndFeelCusto.install();
				JXImapePanelDemoFrame inst = new JXImapePanelDemoFrame();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public JXImapePanelDemoFrame() {
		super();
		initGUI();
	}

	private void initGUI() {
		try {
			setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

			getContentPane().add(getImagePanel());
			getContentPane().add(getJcbStyle(), BorderLayout.SOUTH);
			setSize(400, 300);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public JComboBox getJcbStyle() {
		if (this.jcbStyle == null) {
			this.jcbStyle = new JComboBox();
			this.jcbStyle.addItem(Style.CENTERED);
			this.jcbStyle.addItem(Style.SCALED);
			this.jcbStyle.addItem(Style.SCALED_KEEP_ASPECT_RATIO);
			this.jcbStyle.addItem(Style.TILED);

			this.jcbStyle.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					JXImapePanelDemoFrame.this.imagePanel.setStyle((Style) JXImapePanelDemoFrame.this.jcbStyle
							.getSelectedItem());
				}
			});
		}
		return this.jcbStyle;
	}

	public JXImagePanel getImagePanel() {
		if (this.imagePanel == null) {
			this.imagePanel = new JXImagePanel();
			this.imagePanel.setLayout(new BorderLayout());
			try {
				this.imagePanel.setImage(ImageIO.read(AbstractMachineFrame.class.getResource("powerBy.png")));
			} catch (IOException e) {
				e.printStackTrace();
			}

			JTree tree = new JTree();
			JScrollPane scroll = new JScrollPane(tree);

			scroll.setOpaque(false);
			scroll.getViewport().setOpaque(false);
			tree.setOpaque(false);
			tree.setCellRenderer(new SicpaTreeCellRenderer() {
				private static final long serialVersionUID = 1L;

				@Override
				public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean sel,
						final boolean expanded, final boolean leaf, final int row, final boolean hasFocus) {
					JComponent comp = (JComponent) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
							row, hasFocus);
					comp.setOpaque(false);
					comp.setBackground(new Color(255, 255, 255, 0));
					comp.setForeground(Color.WHITE);
					return comp;
				}
			});

			this.imagePanel.add(scroll);

		}
		return this.imagePanel;
	}

}
