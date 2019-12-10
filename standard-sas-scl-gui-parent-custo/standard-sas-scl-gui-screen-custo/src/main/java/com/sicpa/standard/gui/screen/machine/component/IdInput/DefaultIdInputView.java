package com.sicpa.standard.gui.screen.machine.component.IdInput;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXLabel;

import com.sicpa.standard.gui.components.buttons.PaddedButton;
import com.sicpa.standard.gui.components.text.MultiLineLabel;
import com.sicpa.standard.gui.components.text.filter.SizeDocumentFilter;
import com.sicpa.standard.gui.components.text.filter.UpperCaseDocumentFilter;
import com.sicpa.standard.gui.components.virtualKeyboard.VirtualKeyboardPanel;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.gui.plaf.SicpaFont;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.screen.machine.EmptyMachineFrame;
import com.sicpa.standard.gui.utils.ImageUtils;
import com.sicpa.standard.gui.utils.ThreadUtils;

public class DefaultIdInputView extends AbstractIdInputView {

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();

				EmptyMachineFrame f = new EmptyMachineFrame();
				final DefaultIdInputView view = new DefaultIdInputView();
				view.getModel().setDescription("ENTER OR SCAN THE ID");
				f.getStartButton().addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(final ActionEvent e) {
						view.getTextId().setText("P00000000001");
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								view.getButtonOk().doClick();
							}
						});
					}
				});

				f.addLayerToLeftPanel(view);
				view.setVisible(true);

				view.getModel().setError("dsfsdf sdfsd sdfsd");

				f.setVisible(true);
			}
		});
	}

	protected MultiLineLabel labelText;
	protected JTextField textId;
	protected JPanel panelBarcode;
	protected JButton buttonOk;
	protected PaddedButton paddedOk;
	protected JXLabel textError;

	public DefaultIdInputView() {
		initGUI();
	}

	protected void initGUI() {
		setLayout(new MigLayout("", "35[]35", "35[]"));
		add(getPanelBarcode(), "span, h 100,pushx, grow");
		add(getTextId(), "growx ,w 50%,spanx");
		add(getTextError(), "grow,spany 2");
		add(getPaddedOk(), "right, h 75,w 100,wrap");
	}

	public JPanel getPanelBarcode() {
		if (this.panelBarcode == null) {
			this.panelBarcode = new JPanel() {
				private static final long serialVersionUID = 1L;

				int previousW;
				
				@Override
				protected void paintComponent(final Graphics g) {
					super.paintComponent(g);

					Graphics2D g2 = (Graphics2D) g.create();
					if (DefaultIdInputView.this.barcodeImage == null||previousW!=getWidth()) {
						DefaultIdInputView.this.barcodeImage = createBarcodeImage(
								getWidth()-12, getHeight()-7);
					}
					g2.drawImage(DefaultIdInputView.this.barcodeImage, 0, 0,
							getWidth(), getHeight(), null);
					g2.dispose();
					previousW=getWidth();
				}
			};

			this.panelBarcode.setLayout(new MigLayout("fill"));
			this.panelBarcode.add(getLabelText(), "span,growx");
		}
		return this.panelBarcode;
	}

	protected BufferedImage createBarcodeImage(int w, int h) {
		return ImageUtils.createBarcodeImage(w, h);
	}

	@Override
	protected void modelDescriptionChanged(final IdInputEvent evt) {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				getLabelText().setText(evt.getDescription());
			}
		});
	}

	@Override
	protected void modelIdChanged(final IdInputEvent evt) {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (!getTextId().getText().equals(evt.getId())) {
					getTextId().setText(evt.getId());
				}
			}
		});
	}

	@Override
	protected void modelIdInputComplete(final IdInputEvent evt) {
	}

	public JTextField getTextId() {
		if (this.textId == null) {
			this.textId = new JTextField(15);
			this.textId.setHorizontalAlignment(JTextField.CENTER);
			this.textId.setFont(SicpaFont.getFont(30));
			VirtualKeyboardPanel.attachKeyboardDialog(this.textId,
					VirtualKeyboardPanel.getDefaultKeyboard(this.textId));
			this.textId.getDocument().addDocumentListener(
					new DocumentListener() {

						@Override
						public void removeUpdate(final DocumentEvent e) {
							changedUpdate(e);
						}

						@Override
						public void insertUpdate(final DocumentEvent e) {
							changedUpdate(e);
						}

						@Override
						public void changedUpdate(final DocumentEvent e) {
							textIdChanged();
						}
					});
			setMaxCharacter(20);
		}
		return this.textId;
	}

	protected void textIdChanged() {
		getTextId().setForeground(SicpaColor.BLUE_MEDIUM);
		getModel().setId(getTextId().getText());
	}

	public MultiLineLabel getLabelText() {
		if (this.labelText == null) {
			this.labelText = new MultiLineLabel();
			this.labelText.setForeground(SicpaColor.BLUE_DARK);
		}
		return this.labelText;
	}

	public JButton getButtonOk() {
		if (this.buttonOk == null) {
			this.buttonOk = new JButton("OK");
			this.buttonOk.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonOkActionPerformed();

				}
			});
		}
		return this.buttonOk;
	}

	protected void buttonOkActionPerformed() {
		getModel().selectionComplete();
	}

	public PaddedButton getPaddedOk() {
		if (this.paddedOk == null) {
			this.paddedOk = new PaddedButton(getButtonOk());
		}
		return this.paddedOk;
	}

	public void setMaxCharacter(final int max) {
		UpperCaseDocumentFilter ucFilter = new UpperCaseDocumentFilter();
		SizeDocumentFilter sdFilter = new SizeDocumentFilter(max);
		sdFilter.setFilter(ucFilter);
		sdFilter.installFilter(getTextId());
	}

	protected BufferedImage barcodeImage;

	public void setBarcode(final String barcode) {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				getTextId().setText(barcode);
				buttonOkActionPerformed();
			}
		});
	}

	public JXLabel getTextError() {
		if (this.textError == null) {
			this.textError = new JXLabel();
			this.textError.setLineWrap(true);
			this.textError.setForeground(SicpaColor.RED);
		}
		return this.textError;
	}

	@Override
	protected void modelError(final IdInputEvent evt) {
		ThreadUtils.invokeLater(new Runnable() {
			public void run() {
				getTextError().setText(evt.getError());
				if (evt.getError() != null && evt.getError().length() > 0)
					getTextId().setForeground(SicpaColor.RED);
			}
		});
	}

	@Override
	public void setModel(final IdInputmodel model) {
		super.setModel(model);
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				getTextError().setText(model.getError());
				if (model.getError() != null && model.getError().length() > 0) {
					getTextId().setForeground(SicpaColor.RED);
				}
				getLabelText().setText(model.getDescription());
				getTextId().setText(model.getId());
			}
		});
	}
}
