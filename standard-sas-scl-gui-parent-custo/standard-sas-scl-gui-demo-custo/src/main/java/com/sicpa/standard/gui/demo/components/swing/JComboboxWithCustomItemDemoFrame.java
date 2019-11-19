package com.sicpa.standard.gui.demo.components.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.utils.PaintUtils;

public class JComboboxWithCustomItemDemoFrame extends JFrame {
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				JFrame f = new JComboboxWithCustomItemDemoFrame();
				f.setVisible(true);
			}
		});
	}

	public static class MyShape {
		Shape shape;
		String name;

		@Override
		public String toString() {
			return this.name;
		}
	}

	private static class PanelWithShape extends JPanel {
		Shape shape;

		public void setShape(final Shape shape) {
			this.shape = shape;
			repaint();
		}

		@Override
		protected void paintComponent(final Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g.create();
			PaintUtils.turnOnAntialias(g2);
			g2.setColor(Color.RED);
			if (this.shape != null) {
				g2.fill(this.shape);
			}
		}
	}

	private JComboBox combo;
	private PanelWithShape panelShape;

	private JComboBox comboBADWAY;
	private PanelWithShape panelShapeBADWAY;

	public JComboboxWithCustomItemDemoFrame() {
		initGUI();
	}

	private void initGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new MigLayout("fill,debug"));
		getContentPane().add(getCombo(),"growx,");
		getContentPane().add(getPanelShape(), "grow,wrap");

		// show bad way, no visual diff but it's just badly codded
		getContentPane().add(getComboBADWAY(),"growx");
		getContentPane().add(getPanelShapeBADWAY(), "grow,wrap");
		
		setSize(400, 300);
	}

	private JComboBox getCombo() {
		if (this.combo == null) {
			this.combo = new JComboBox();
			populateCombo();
			this.combo.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					comboActionPerformed();
				}
			});

		}
		return this.combo;
	}

	private void populateCombo() {
		getCombo().addItem("Select a shape");

		MyShape s = new MyShape();
		s.name = "square";
		s.shape = getSquare();
		getCombo().addItem(s);

		s = new MyShape();
		s.name = "triangle";
		s.shape = getTriangle();

		getCombo().addItem(s);

		s = new MyShape();
		s.name = "circle";
		s.shape = getCircle();
		getCombo().addItem(s);
	}

	private Shape getSquare() {
		return new Rectangle(5, 5, 100, 100);
	}

	private Shape getCircle() {
		return new Ellipse2D.Float(5, 5, 100, 100);
	}

	private Shape getTriangle() {
		GeneralPath path = new GeneralPath();
		path.moveTo(50, 5);
		path.lineTo(90, 90);
		path.lineTo(10, 90);
		path.closePath();
		return path;
	}

	private void comboActionPerformed() {
		Object o = this.combo.getSelectedItem();
		if (o instanceof MyShape) {
			this.panelShape.setShape(((MyShape) o).shape);
		} else {
			this.panelShape.setShape(null);
		}
	}

	private PanelWithShape getPanelShape() {
		if (this.panelShape == null) {
			this.panelShape = new PanelWithShape();
		}
		return this.panelShape;
	}

	private JComboBox getComboBADWAY() {
		if (this.comboBADWAY == null) {
			this.comboBADWAY = new JComboBox();
			this.comboBADWAY.addItem("Select a shape");
			this.comboBADWAY.addItem("square");
			this.comboBADWAY.addItem("triangle");
			this.comboBADWAY.addItem("circle");

			this.comboBADWAY.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					comboActionPerformedDoneTheBADWay();
				}
			});
		}
		return this.comboBADWAY;
	}

	private PanelWithShape getPanelShapeBADWAY() {
		if (this.panelShapeBADWAY == null) {
			this.panelShapeBADWAY = new PanelWithShape();
		}
		return this.panelShapeBADWAY;
	}

	private void comboActionPerformedDoneTheBADWay() {
		int selected = JComboboxWithCustomItemDemoFrame.this.comboBADWAY.getSelectedIndex();
		switch (selected) {
		case 0:
			this.panelShapeBADWAY.setShape(null);
			break;
		case 1:
			this.panelShapeBADWAY.setShape(getSquare());
			break;
		case 2:
			this.panelShapeBADWAY.setShape(getTriangle());
			break;
		case 3:
			this.panelShapeBADWAY.setShape(getCircle());
			break;
		}
	}
}
