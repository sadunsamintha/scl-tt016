package com.sicpa.standard.gui.screen.machine.component.emergency;

import java.awt.Graphics;
import java.beans.PropertyChangeEvent;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXLabel;

import com.sicpa.standard.gui.components.scroll.SmallScrollBar;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.gui.plaf.SicpaFont;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.utils.ThreadUtils;

@Deprecated
public class DefaultEmergencyPanel extends AbstractEmergencyPanel {
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setSize(500, 500);

				EmergencyModel model = new EmergencyModel();
				model
						.setText("dlfj\ndlfj\ndlfj\ndlfj\ndlfj\ndlfj\ndlfj\ndlfj\ndlfj\ndlfj\ndlfj\nsdlfjsdhfkjs lj klj lkj klj ljlk jl kjl�");
				model.setVisible(true);

				DefaultEmergencyPanel p = new DefaultEmergencyPanel();
				p.setModel(model);

				f.getContentPane().add(p);
				f.setVisible(true);
			}
		});
	}

	private JXLabel textArea;
	private JScrollPane scroll;

	public DefaultEmergencyPanel() {
		this(new EmergencyModel());
	}

	public DefaultEmergencyPanel(final EmergencyModel model) {
		super(model);
		initGUI();
		setName("emergencyPanel");
	}

	private void initGUI() {
		setLayout(new MigLayout("fill"));
		add(SmallScrollBar.createLayerSmallScrollBar(getScroll()), "top,grow");
		setOpaque(true);
	}

	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);
		g.setColor(SicpaColor.RED);
		g.fillRect(0, 0, getWidth(), getHeight());
	}

	private JXLabel getTextArea() {
		if (this.textArea == null) {
			this.textArea = new JXLabel();
			this.textArea.setForeground(SicpaColor.BLUE_DARK);
			this.textArea.setFont(SicpaFont.getFont(40));
			this.textArea.setOpaque(false);
			this.textArea.setLineWrap(true);
		}
		return this.textArea;
	}

	private JScrollPane getScroll() {
		if (this.scroll == null) {
			this.scroll = new JScrollPane(getTextArea());
//			SmoothScrolling.enableFullScrolling(this.scroll);
			this.scroll.setOpaque(false);
			this.scroll.getViewport().setOpaque(false);
		}
		return this.scroll;
	}

	@Override
	protected void modelEmergencyTextChanged(final PropertyChangeEvent evt) {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				getTextArea().setText(evt.getNewValue() + "");
			}
		});
	}

	@Override
	protected void modelEmergencyVisibilityChanged(final PropertyChangeEvent evt) {
		ThreadUtils.invokeLater(new Runnable() {

			@Override
			public void run() {
				setVisible((Boolean) evt.getNewValue());
			}
		});
	}

	@Override
	public void setModel(final EmergencyModel model) {
		super.setModel(model);
		ThreadUtils.invokeLater(new Runnable() {

			@Override
			public void run() {
				getTextArea().setText(model.getText());
				setVisible(model.isVisible());
			}
		});
	}
}
