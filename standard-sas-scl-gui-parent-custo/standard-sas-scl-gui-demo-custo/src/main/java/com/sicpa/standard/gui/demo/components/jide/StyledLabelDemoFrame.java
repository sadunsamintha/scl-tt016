package com.sicpa.standard.gui.demo.components.jide;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import com.jidesoft.swing.StyledLabel;
import com.jidesoft.swing.StyledLabelBuilder;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

public class StyledLabelDemoFrame extends javax.swing.JFrame {

	private StyledLabel label;
	private JPanel demoPanel;
	private JPanel helpPanel;
	private JPanel inputPanel;
	private JPanel labelsPanel;

	public static void main(final String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SicpaLookAndFeelCusto.install();
				StyledLabelDemoFrame inst = new StyledLabelDemoFrame();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public StyledLabelDemoFrame() {
		super();
		initGUI();
	}

	private void initGUI() {
		try {
			setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			getLabel();
			getContentPane().add(new JScrollPane(getDemoPanel()));
			setSize(1024, 768);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public StyledLabel getLabel() {
		if (this.label == null) {
			this.label = new StyledLabel();
			StyledLabelBuilder.setStyledText(this.label, "{Preview:bold} of {StyledLabel:f:red}");
		}
		return this.label;
	}

	public JPanel getDemoPanel() {
		if (this.demoPanel == null) {
			this.demoPanel = new JPanel();
			this.demoPanel.setLayout(new BoxLayout(this.demoPanel, BoxLayout.Y_AXIS));
			this.demoPanel.add(getLabelsPanel());
			this.demoPanel.add(getInputPanel());
			this.demoPanel.add(getHelpPanel());
		}
		return this.demoPanel;
	}

	private JPanel getLabelsPanel() {
		if (this.labelsPanel == null) {
			this.label = new StyledLabel();
			StyledLabelBuilder.setStyledText(this.label, "{Preview:bold} of {StyledLabel:f:red}");
			this.label.setHorizontalAlignment(SwingConstants.CENTER);

			this.labelsPanel = new JPanel();
			this.labelsPanel.setLayout(new GridLayout(0, 1, 10, 10));
			this.labelsPanel.add(this.label);
			this.labelsPanel.setBorder(BorderFactory.createTitledBorder(" Preview "));

			this.labelsPanel.setPreferredSize(new Dimension(200, 100));
		}

		return this.labelsPanel;
	}

	private JPanel getHelpPanel() {
		if (this.helpPanel == null) {
			this.helpPanel = new JPanel(new GridLayout(0, 1, 6, 6));
			this.helpPanel.setBorder(BorderFactory.createTitledBorder(" Help "));

			StyledLabel titleLabel = StyledLabelBuilder.createStyledLabel("{Font styles\\::b, f:blue}");
			titleLabel.setFont(titleLabel.getFont().deriveFont(12.0f));
			this.helpPanel.add(titleLabel);
			this.helpPanel.add(StyledLabelBuilder
					.createStyledLabel("   - {plain:b} or {p:b}, i.e. \\{plain text:p} => {plain text:p}"));
			this.helpPanel.add(StyledLabelBuilder
					.createStyledLabel("   - {bold:b} or {b:b}, i.e. \\{bold text:b} => {bold text:b}"));
			this.helpPanel.add(StyledLabelBuilder
					.createStyledLabel("   - {italic:b} or {i:b}, i.e. \\{italic text:i} => {italic text:i}"));
			this.helpPanel
					.add(StyledLabelBuilder
							.createStyledLabel("   - {bolditalic:b} or {bi:b}, i.e. \\{bold and italic text:bi} => {bold and italic:bi}"));

			titleLabel = StyledLabelBuilder.createStyledLabel("{Line styles\\::b,f:blue}");
			titleLabel.setFont(titleLabel.getFont().deriveFont(12.0f));
			this.helpPanel.add(titleLabel);
			this.helpPanel.add(StyledLabelBuilder
					.createStyledLabel("   - {strike:b} or {s:b}, i.e. \\{strikethrough:s} => {strikethrough:s}"));
			this.helpPanel
					.add(StyledLabelBuilder
							.createStyledLabel("   - {doublestrike:b} or {ds:b}, i.e. \\{double strikethrough:ds} => {double strikethrough:ds}"));
			this.helpPanel.add(StyledLabelBuilder
					.createStyledLabel("   - {waved:b} or {w:b}, i.e. \\{waved:w} => {waved:w}"));
			this.helpPanel.add(StyledLabelBuilder
					.createStyledLabel("   - {underlined:b} or {u:b}, i.e. \\{underlined:u} => {underlined:u}"));
			this.helpPanel.add(StyledLabelBuilder
					.createStyledLabel("   - {dotted:b} or {d:b}, i.e. \\{dotted:d} => {dotted:d}"));
			this.helpPanel.add(StyledLabelBuilder
					.createStyledLabel("   - {superscript:b} or {sp:b}, i.e. Java\\{TM:sp} => Java{TM:sp}"));
			this.helpPanel.add(StyledLabelBuilder
					.createStyledLabel("   - {subscipt:b} or {sb:b}, i.e. CO\\{2:sb} => CO{2:sb}"));

			titleLabel = StyledLabelBuilder
					.createStyledLabel("{Using Colors\\::b,f:blue} using {f:b} for font color, {l:b} for line color and {b:b} for backgroundcolor");
			titleLabel.setFont(titleLabel.getFont().deriveFont(12.0f));
			this.helpPanel.add(titleLabel);
			this.helpPanel
					.add(StyledLabelBuilder
							.createStyledLabel("   - {f\\::b} plus color name defined in class Color, i.e. \\{red text:f:red} => {red text:f:red}"));
			this.helpPanel
					.add(StyledLabelBuilder
							.createStyledLabel("   - {l\\::b}: plus color name defined in class Color, i.e. \\{red underline:u, l:red} => {red underline:u, l:red}"));
			this.helpPanel
					.add(StyledLabelBuilder
							.createStyledLabel("   - {b\\::b}: plus color name defined in class Color, i.e. \\{red background:b:red} => {red background:b:red}"));
			this.helpPanel
					.add(StyledLabelBuilder
							.createStyledLabel("   - {f\\::b} or {l\\::b} or {b\\::b}: plus #RRGGBB, i.e. \\{any color:f:#00AA55} => {any color:f:#00AA55}"));
			this.helpPanel
					.add(StyledLabelBuilder
							.createStyledLabel("   - {f\\::b} or {l\\::b} or {b\\::b}: plus #RGB as in CSS, i.e. \\{any color:f:#0A5} => {any color:f:#0A5}"));
			this.helpPanel
					.add(StyledLabelBuilder
							.createStyledLabel("   - {f\\::b} or {l\\::b}or {b\\::b}: plus (R, G, B), i.e. \\{any line color:s, l:(0, 220, 128)} or \\{any background color:b:(0, 120, 128)} => {any line color:s, l:(0, 220, 128)} or {any background color:b:(0, 120, 128)}"));

			titleLabel = StyledLabelBuilder.createStyledLabel("{Special characters\\::b, f:blue}");
			titleLabel.setFont(titleLabel.getFont().deriveFont(12.0f));
			this.helpPanel.add(titleLabel);
			this.helpPanel
					.add(StyledLabelBuilder
							.createStyledLabel("   - Special annotation characters {\\{:b} {\\}:b} {\\(:b} {\\):b} {\\#:b} {\\::b} {\\,:b} should be escaped by \"\\\\\" when they are used as regular text"));
			this.helpPanel.add(StyledLabelBuilder
					.createStyledLabel("   - i.e. \\{\\\\\\{brace\\\\\\}:b} => {\\{brace\\}:b}"));
			this.helpPanel
					.add(StyledLabelBuilder
							.createStyledLabel("   - If you need multiple styles connect them with {,:b} (comma), i.e. \\{bold red text:f:red, b} => {bold red text:f:red, b}"));

		}

		return this.helpPanel;
	}

	private JPanel getInputPanel() {
		if (this.inputPanel == null) {
			this.inputPanel = new JPanel(new BorderLayout(4, 4));
			this.inputPanel.add(new JLabel("Annotated String: "), BorderLayout.BEFORE_LINE_BEGINS);
			final JTextField textField = new JTextField("{Preview:bold} of {StyledLabel:f:red}");
			textField.setColumns(50);
			this.inputPanel.add(textField, BorderLayout.CENTER);
			AbstractAction action = new AbstractAction("Update") {
				public void actionPerformed(final ActionEvent e) {
					StyledLabelDemoFrame.this.label.clearStyleRanges();
					StyledLabelBuilder.setStyledText(StyledLabelDemoFrame.this.label, textField.getText());
				}
			};

			final JButton button = new JButton(action);
			textField.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					button.doClick();
				}
			});

			this.inputPanel.add(button, BorderLayout.AFTER_LINE_ENDS);

			this.inputPanel.setBorder(BorderFactory
					.createTitledBorder(" Type in an annotated string and press enter to see the result "));
		}

		return this.inputPanel;
	}
}
