package com.sicpa.ttth.view.sku.batch;

import com.google.common.eventbus.Subscribe;
import java.awt.Dimension;
import java.util.ArrayList;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.client.common.view.mvc.AbstractView;
import com.sicpa.standard.gui.components.buttons.shape.DirectionButton;
import com.sicpa.standard.gui.components.virtualKeyboard.VirtualKeyboardPanel;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.sasscl.devices.remote.impl.dtoConverter.DailyBatchRequestRepository;
import com.sicpa.standard.sasscl.view.LanguageSwitchEvent;

public class BatchJobIdSkuView extends AbstractView<IBatchJobIdSkuListener, BatchJobIdSKUModel> {

    private BatchJobIdSkuPanel batchJobIdSkuPanel;
    private DailyBatchRequestRepository dailyBatchRequestRepository;
    private int batchJobIdSize;

    private String batchJobId;

    public BatchJobIdSkuView() { }

    private void initGUI() {
        setLayout(new MigLayout("ltr,fill"));
        String strSKU = "";
        add(new JLabel(Messages.get("sku.batch.id.title") + " " + strSKU));
        add(new JSeparator(), "growx, pushx, wrap");
        add(getBatchJobIdSkuPanel(), "span, split 2, pushy, growx, growy");
    }

    public void refresh() {
        removeAll();
        this.batchJobIdSkuPanel = null;
        initGUI();
    }

    public BatchJobIdSkuPanel getBatchJobIdSkuPanel() {
        if (batchJobIdSkuPanel == null) {
            batchJobIdSkuPanel = new BatchJobIdSkuPanel();
        }
        return batchJobIdSkuPanel;
    }

    @Override
    public void modelChanged() { }

    public int getBatchJobIdSize() {
        return batchJobIdSize;
    }

    public void setDailyBatchRequestRepository(DailyBatchRequestRepository dailyBatchRequestRepository) {
        this.dailyBatchRequestRepository = dailyBatchRequestRepository;
    }

    public void setBatchJobIdSize(int batchJobIdSize) {
        this.batchJobIdSize = batchJobIdSize;
    }

    @Subscribe
    public void handleLanguageSwitch(LanguageSwitchEvent evt) {
        refresh();
    }

    private class BatchJobIdSkuPanel extends JPanel {

        private JPanel batchJobIdPanel;
        private JPanel buttonPanel;
        private JButton manualBatchJobIdButton;
        private JTextField batchJobIdSeqTextField;
        private JTextField batchJobIdSiteSeqTextField;
        private JButton autoBatchJobIdButton;
        private DirectionButton buttonBack;

        public BatchJobIdSkuPanel() {
            initGui();
        }

        private void initGui() {
            setLayout(new MigLayout("ltr,fill"));
            add(getBatchJobIdPanel(), "push, grow, align center, wrap");
            add(getButtonsPanel(), "newline push, grow, align center, wrap");
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

        private JButton getManualBatchJobIDButton() {
            if (manualBatchJobIdButton == null) {
                manualBatchJobIdButton = new JButton(Messages.get("sku.button.batch.offline.mode"));
                manualBatchJobIdButton.setPreferredSize(new Dimension(180, 50));
                Object[] fields = {
                    Messages.get("sku.batch.job.site.label"), getBatchJobIdSiteTextField(),
                    Messages.get("sku.batch.job.seq.label"), getBatchJobSeqTextField()
                };
                manualBatchJobIdButton.addActionListener(e -> {
                    getBatchJobIdSiteTextField().setText("");
                    getBatchJobSeqTextField().setText("");
                   int option = JOptionPane.showConfirmDialog(this, fields,
                       Messages.get("sku.button.batch.offline.mode"),
                       JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null);
                   if (option == JOptionPane.YES_OPTION) {
                       generateBatchJobId(getBatchJobIdSiteTextField().getText(),
                           getBatchJobSeqTextField().getText());
                   }
                });
            }
            return manualBatchJobIdButton;
        }

        public JTextField getBatchJobSeqTextField() {
            if (batchJobIdSeqTextField == null) {
                batchJobIdSeqTextField = new JTextField(getBatchJobIdSize());
                batchJobIdSeqTextField.setPreferredSize(new Dimension(120, 40));
                batchJobIdSeqTextField.setBackground(SicpaColor.BLUE_ULTRA_LIGHT);
                VirtualKeyboardPanel virtualKeyboardPanel = VirtualKeyboardPanel.getNumericKeyboard(batchJobIdSeqTextField);
                VirtualKeyboardPanel.attachKeyboardDialog(batchJobIdSeqTextField, virtualKeyboardPanel);
            }
            return batchJobIdSeqTextField;
        }

        public JTextField getBatchJobIdSiteTextField() {
            if (batchJobIdSiteSeqTextField == null) {
                batchJobIdSiteSeqTextField = new JTextField(getBatchJobIdSize());
                batchJobIdSiteSeqTextField.setPreferredSize(new Dimension(120, 40));
                batchJobIdSiteSeqTextField.setBackground(SicpaColor.BLUE_ULTRA_LIGHT);
                VirtualKeyboardPanel virtualKeyboardPanel = VirtualKeyboardPanel.getDefaultKeyboard(batchJobIdSiteSeqTextField);
                VirtualKeyboardPanel.attachKeyboardDialog(batchJobIdSiteSeqTextField, virtualKeyboardPanel);
            }
            return batchJobIdSiteSeqTextField;
        }

        private JButton getAutoBatchJobIdButton() {
            if (autoBatchJobIdButton == null) {
                autoBatchJobIdButton = new JButton(Messages.get("sku.button.batch.auto.mode"));
                if (!dailyBatchRequestRepository.getDailyBatchRequests().isEmpty()) {
                    ArrayList<String> choices = new ArrayList<>();
                    dailyBatchRequestRepository
                        .getDailyBatchRequests()
                        .forEach(e -> choices.add(e.getBatchJobId()));
                    autoBatchJobIdButton.addActionListener(e -> {
                        batchJobId = (String) JOptionPane.showInputDialog(this,
                            Messages.get("sku.button.batch.auto.mode"), Messages.get("sku.button.batch.auto.mode.prompt"),
                            JOptionPane.PLAIN_MESSAGE, null, choices.toArray(), choices.get(0));
                        if (batchJobId != null) {
                            saveSelected(batchJobId);
                        }
                    });
                } else {
                    autoBatchJobIdButton.addActionListener(e -> {
                        JOptionPane.showMessageDialog(this, Messages.get("sku.batch.job.unavailable"));
                    });
                }
                autoBatchJobIdButton.setPreferredSize(new Dimension(180, 50));
            }
            return autoBatchJobIdButton;
        }

        public JPanel getBatchJobIdPanel() {
            if (batchJobIdPanel == null) {
                batchJobIdPanel = new JPanel();
                batchJobIdPanel.setLayout(new MigLayout("ltr,fill,wrap 2"));
                batchJobIdPanel.add(Box.createRigidArea(new Dimension(0, 28)), "newline push, align center");
                batchJobIdPanel.add(getAutoBatchJobIdButton(), "newline push, align center");
                batchJobIdPanel.add(Box.createRigidArea(new Dimension(0, 18)), "newline push, align center");
                batchJobIdPanel.add(getManualBatchJobIDButton(), "newline push, align center");
            }
            return batchJobIdPanel;
        }

        public JPanel getButtonsPanel() {
            if (buttonPanel == null) {
                buttonPanel = new JPanel();
                buttonPanel.setLayout(new MigLayout("ltr,fill,wrap 2"));
                buttonPanel.add(getButtonBack(), "align left");
            }
            return buttonPanel;
        }
    }

    private void saveSelected(String strBatchJobId) {
        for (IBatchJobIdSkuListener listener : listeners) {
            listener.saveBatchJobId(strBatchJobId);
        }
    }

    private void generateBatchJobId(String batchJobSite, String batchJobSeq) {
        for (IBatchJobIdSkuListener listener : listeners) {
            listener.generateBatchJobId(batchJobSite, batchJobSeq);
        }
    }

    private void returnToSelection() {
        for (IBatchJobIdSkuListener listener : listeners) {
            listener.returnToSelection();
        }
    }
}