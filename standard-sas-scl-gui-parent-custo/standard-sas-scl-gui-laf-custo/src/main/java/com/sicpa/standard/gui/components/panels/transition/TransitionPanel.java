package com.sicpa.standard.gui.components.panels.transition;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.swingx.graphics.GraphicsUtilities;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.utils.ImageUtils;
import com.sicpa.standard.gui.utils.ThreadUtils;

public class TransitionPanel extends JPanel {

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.getContentPane().setLayout(new MigLayout("fill"));

				final TransitionPanel p = new TransitionPanel();

				p.panelView.setView(new JTree());
				f.getContentPane().add(p, "grow,push,span");

				JButton b = new JButton("next");
				b.addActionListener(new ActionListener() {
					int cpt = 0;

					@Override
					public void actionPerformed(final ActionEvent e) {
						if (this.cpt % 2 == 0) {
							JPanel t = new JPanel();
							t.setLayout(new MigLayout("fill"));
							t.add(new JButton("qwer"));
							t.add(new JButton("tzuiop"), "grow,wrap");
							t.add(new JButton("qwer"));
							t.add(new JButton("tzuiop"), "grow,wrap");
							t.add(new JButton("qwer"));
							t.add(new JButton("tzuiop"), "grow,wrap");
							t.add(
									new JScrollPane(
											new JLabel(new ImageIcon(ImageUtils.createRandomStrippedImage(400)))),
									"span,grow");
							p.moveNext(t);
						} else {
							JPanel t = new JPanel();
							t.setLayout(new MigLayout("fill"));
							t.add(new JTree(), "grow");
							p.moveNext(t);
						}
						this.cpt++;
					}
				});
				f.getContentPane().add(b);

				final JComboBox combo = new JComboBox();
				combo.addItem(new SlideTransitionUI());
				combo.addItem(new ScribbleTransitionUI());
				combo.addItem(new FadeTransitionUI());
				combo.addItem(new RectanglesTransitionUI());
				f.getContentPane().add(combo);

				combo.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						p.setNextUI((TransitionUI) combo.getSelectedItem());
						p.nextUI.setAnimDuration(1000);
					}
				});
				f.setSize(850, 600);
				f.setVisible(true);
			}
		});
	}

	private JXLayer<JComponent> panelView;
	private TransitionUI nextUI;

	public TransitionPanel() {
		initGUI();
	}

	private void initGUI() {
		setLayout(new BorderLayout());
		add(getPanelView());
	}

	public JXLayer<JComponent> getPanelView() {
		if (this.panelView == null) {
			this.nextUI = new SlideTransitionUI();
			this.nextUI.setAnimDuration(1000);
			this.panelView = new JXLayer<JComponent>();
			this.panelView.setUI(this.nextUI);
			this.panelView.setOpaque(false);
		}
		return this.panelView;
	}

	public void moveNext(final JComponent nextView) {
		move(nextView, true);
	}

	public void movePrevious(final JComponent nextView) {
		move(nextView, false);
	}

	public void move(final JComponent nextView, final boolean next) {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				boolean shouldstartAnim = getPanelView().getWidth() > 0 && getPanelView().getHeight() > 0;
				if (shouldstartAnim) {
					// draw current/previous view
					{
						BufferedImage previousImage = GraphicsUtilities.createCompatibleTranslucentImage(getPanelView()
								.getWidth(), getPanelView().getHeight());
						Graphics2D g = previousImage.createGraphics();
						getPanelView().getView().paint(g);
						g.dispose();
						TransitionPanel.this.nextUI.setPreviousImage(previousImage);
					}

					// prepare the next panel
					JComponent oldView = getPanelView().getView();
					getPanelView().setView(nextView);
					getPanelView().validate();

					// paint image
					{
						BufferedImage nextImage = GraphicsUtilities.createCompatibleTranslucentImage(getPanelView()
								.getWidth(), getPanelView().getHeight());
						Graphics2D g = nextImage.createGraphics();
						getPanelView().paint(g);
						g.dispose();
						TransitionPanel.this.nextUI.setNextImage(nextImage);
					}

					getPanelView().setView(oldView);
					TransitionPanel.this.nextUI.startAnim(next);

					new Thread(new Runnable() {
						@Override
						public void run() {
							if (TransitionPanel.this.nextUI.getAnimDuration() > 100) {
								ThreadUtils.sleepQuietly(TransitionPanel.this.nextUI.getAnimDuration() - 100);
							}
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									getPanelView().setView(nextView);
									getPanelView().validate();
									getPanelView().repaint();
								}
							});
						}
					}).start();

				} else {
					getPanelView().setView(nextView);
					getPanelView().validate();
					getPanelView().repaint();
				}
			}
		});
	}

	public TransitionUI getNextUI() {
		return this.nextUI;
	}

	public void setNextUI(final TransitionUI nextUI) {
		nextUI.setAnimDuration(this.nextUI.getAnimDuration());
		this.nextUI = nextUI;
		getPanelView().setUI(nextUI);
	}
}
