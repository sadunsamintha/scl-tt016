package com.sicpa.ttth.view.sku.barcode;

import com.google.common.eventbus.Subscribe;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.client.common.view.mvc.AbstractView;
import com.sicpa.standard.gui.components.buttons.shape.DirectionButton;
import com.sicpa.standard.gui.components.virtualKeyboard.VirtualKeyboardPanel;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.sasscl.view.LanguageSwitchEvent;

public class BarcodeSkuView extends AbstractView<IBarcodeSkuListener, BarcodeSkuModel> {

    protected BarcodeSkuPanel barcodeSkuPanel;

    public BarcodeSkuView() { }

    private void initGUI() {
        setLayout(new MigLayout("ltr,fill"));
        add(new JLabel(Messages.get("sku.barcode.title") + " "));
        add(new JSeparator(), "growx, pushx, wrap");
        add(getBarcodeSkuPanel(), "span, split 2, pushy, growx, growy");
    }

    public void refresh() {
        removeAll();
        this.barcodeSkuPanel = null;
        initGUI();
    }

    public BarcodeSkuPanel getBarcodeSkuPanel() {
        if (barcodeSkuPanel == null) {
            barcodeSkuPanel = new BarcodeSkuPanel();
        }
        return barcodeSkuPanel;
    }

    @Override
    public void modelChanged() { }

    @Subscribe
    public void handleLanguageSwitch(LanguageSwitchEvent evt) {
        refresh();
    }

    private class BarcodeSkuPanel extends JPanel {

        private JButton saveButton;
        private JPanel barcodePanel;
        private JPanel buttonPanel;
        private JLabel barcodeIdLabel;
        private JTextField barcodeIdText;
        private DirectionButton buttonBack;

        public BarcodeSkuPanel() {
            initGui();
        }

        private void initGui() {
            setLayout(new MigLayout("ltr,fill"));
            add(getBarcodePanel(), "newline push, grow, align center, wrap");
            add(getButtonsPanel(), "newline push, grow, align center, wrap");
            SwingUtilities.invokeLater(() -> getBarcodeText().requestFocus());
        }

        private JButton getSaveButton() {
            if (saveButton == null) {
                saveButton = new JButton(Messages.get("sku.button.save.label"));
                saveButton.addActionListener(e -> saveSelected(barcodeIdText.getText()));
            }
            return saveButton;
        }

        public JButton getButtonBack() {
            if (this.buttonBack == null) {
                this.buttonBack = new DirectionButton(DirectionButton.Direction.LEFT);
                this.buttonBack.addActionListener(e -> returnToSelection());
                this.buttonBack.setMinimumSize(new Dimension(100, 40));
                this.buttonBack.setPreferredSize(new Dimension(100, 40));
                this.buttonBack.setMaximumSize(new Dimension(100, 40));
                this.buttonBack.setName("buttonBack");
            }

            return this.buttonBack;
        }

        public JLabel getBarcodeLabel() {
            if (barcodeIdLabel == null) {
                barcodeIdLabel = new JLabel(Messages.get("sku.barcode.label"));
                barcodeIdLabel.setMinimumSize(new Dimension(70, 40));
                barcodeIdLabel.setForeground(SicpaColor.BLUE_DARK);
            }
            return barcodeIdLabel;
        }

        public JTextField getBarcodeText() {
            if (barcodeIdText == null) {
                barcodeIdText = new JTextField();
                barcodeIdText.setMinimumSize(new Dimension(400, 60));
                barcodeIdText.setMaximumSize(new Dimension(400, 60));
                barcodeIdText.setBackground(SicpaColor.BLUE_DARK);
                VirtualKeyboardPanel virtualKeyboardPanel = VirtualKeyboardPanel.getNumericKeyboard(barcodeIdText);
                VirtualKeyboardPanel.attachKeyboardDialog(barcodeIdText, virtualKeyboardPanel);
            }
            return barcodeIdText;
        }

        public JPanel getBarcodePanel() {
            if (barcodePanel == null) {
                barcodePanel = new JPanel();
                barcodePanel.setLayout(new MigLayout("ltr,fill,wrap 2"));
                barcodePanel.add(getBarcodeLabel(), "push, align right");
                barcodePanel.add(getBarcodeText(), "push, align left");
            }
            return barcodePanel;
        }

        public JPanel getButtonsPanel() {
            if (buttonPanel == null) {
                buttonPanel = new JPanel();
                buttonPanel.setLayout(new MigLayout("ltr,fill,wrap 2"));
                buttonPanel.add(getButtonBack(), "align left");
                buttonPanel.add(getSaveButton(), "align right");
            }
            return buttonPanel;
        }
    }

    private void saveSelected(String strBatchId) {
        for (IBarcodeSkuListener listener : listeners) {
            listener.saveBarcode(strBatchId);
        }
    }

    private void returnToSelection() {
        for (IBarcodeSkuListener listener : listeners) {
            listener.returnToSelection();
        }
    }
}