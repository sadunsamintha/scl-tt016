package com.sicpa.ttth.view.sku.batch;

import com.google.common.eventbus.Subscribe;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
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
import com.sicpa.standard.sasscl.model.BatchJobHistory;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.view.LanguageSwitchEvent;

public class BatchJobIdSkuView extends AbstractView<IBatchJobIdSkuListener, BatchJobIdSKUModel> {

    private BatchJobIdSkuPanel batchJobIdSkuPanel;
    private DailyBatchRequestRepository dailyBatchRequestRepository;
    private ProductionParameters productionParameters;
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

    public void setProductionParameters(ProductionParameters productionParameters) {
        this.productionParameters = productionParameters;
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
        private JTextField batchJobIdSkuTextField;
        private JButton autoBatchJobIdButton;
        private DirectionButton buttonBack;

        public BatchJobIdSkuPanel() {
            initGui();
        }

        private void initGui() {
            setLayout(new MigLayout("ltr,fill"));
            add(getBatchJobIdPanel(), "push, grow, align center, wrap");
            add(getNavButtonPanel(), "newline push, grow, align center, wrap");
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
                    Messages.get("sku.batch.job.seq.label"), getBatchJobSeqTextField(),
                    Messages.get("sku.id.label"), getBatchJobSkuTextField()
                };
                manualBatchJobIdButton.addActionListener(e -> {
                    getBatchJobSeqTextField().setText("");
                    getBatchJobSkuTextField().setText("");
                   int option = JOptionPane.showConfirmDialog(this, fields,
                       Messages.get("sku.button.batch.offline.mode"),
                       JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null);
                   if (option == JOptionPane.YES_OPTION) {
                       generateBatchJobId(getBatchJobSeqTextField().getText(), getBatchJobSkuTextField().getText());
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

        public JTextField getBatchJobSkuTextField() {
            if (batchJobIdSkuTextField == null) {
                batchJobIdSkuTextField = new JTextField(getBatchJobIdSize());
                batchJobIdSkuTextField.setPreferredSize(new Dimension(120, 40));
                batchJobIdSkuTextField.setBackground(SicpaColor.BLUE_ULTRA_LIGHT);
                VirtualKeyboardPanel virtualKeyboardPanel = VirtualKeyboardPanel.getNumericKeyboard(batchJobIdSkuTextField);
                VirtualKeyboardPanel.attachKeyboardDialog(batchJobIdSkuTextField, virtualKeyboardPanel);
            }
            return batchJobIdSkuTextField;
        }

        private JButton getAutoBatchJobIdButton() {
            if (autoBatchJobIdButton == null) {
                autoBatchJobIdButton = new JButton(Messages.get("sku.button.batch.auto.mode"));
                if (productionParameters.getProductionMode().equals(ProductionMode.STANDARD)) {
                    if (!dailyBatchRequestRepository.getDailyBatchRequests().isEmpty()) {
                        ArrayList<String> choices = new ArrayList<>();
                        dailyBatchRequestRepository
                            .getDailyBatchRequests()
                            .forEach(e -> choices.add(e.getBatchJobId()));
                        autoBatchJobIdButton.addActionListener(e -> {
                            JOptionPane pane = getBatchSelectionPane(Messages.get("sku.button.batch.auto.mode"), choices.toArray());
                            JDialog dialog = pane.createDialog(this, Messages.get("sku.button.batch.auto.mode"));
                            dialog.setSize(350, 180);
                            dialog.setVisible(true);
                            dialog.dispose();
                            batchJobId = (String) pane.getInputValue();
                            if (batchJobId != null && !batchJobId.equals("uninitializedValue")) {
                                saveSelected(batchJobId);
                            }
                        });
                    } else {
                        autoBatchJobIdButton.addActionListener(e ->
                            JOptionPane.showMessageDialog(this, Messages.get("sku.batch.job.unavailable")));
                    }
                } else {
                    if (!dailyBatchRequestRepository.getBatchJobHistory().getDailyBatchHistory().isEmpty()) {
                        BatchJobHistory jobHistory = dailyBatchRequestRepository.getBatchJobHistory();
                        jobHistory.sortHistory();
                        ArrayList<String> choices = new ArrayList<>(jobHistory
                            .getDailyBatchHistory()
                            .keySet());
                        Collections.reverse(choices);
                        autoBatchJobIdButton.addActionListener(e -> {
                            JOptionPane pane = getBatchSelectionPane(Messages.get("sku.button.batch.auto.mode"), choices.toArray());
                            JDialog dialog = pane.createDialog(this, Messages.get("sku.button.batch.auto.mode.prompt"));
                            dialog.setSize(350, 180);
                            dialog.setVisible(true);
                            dialog.dispose();
                            batchJobId = (String) pane.getInputValue();
                            if (batchJobId != null && !batchJobId.equals("uninitializedValue")) {
                                if (productionParameters.getProductionMode().equals(ProductionMode.REFEED_CORRECTION)){
                                    //Manual flow for correction refeed.
                                    saveSelectedHist(batchJobId);
                                } else {
                                    //Normal flow for normal refeed.
                                    saveSelected(batchJobId);
                                }
                            }
                        });
                    } else {
                        autoBatchJobIdButton.addActionListener(e ->
                            JOptionPane.showMessageDialog(this, Messages.get("sku.batch.history.unavailable")));
                    }
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
                if (productionParameters.getProductionMode().equals(ProductionMode.STANDARD)) {
                    batchJobIdPanel.add(Box.createRigidArea(new Dimension(0, 18)), "newline push, align center");
                    batchJobIdPanel.add(getManualBatchJobIDButton(), "newline push, align center");
                }
            }
            return batchJobIdPanel;
        }

        public JPanel getNavButtonPanel() {
            if (buttonPanel == null) {
                buttonPanel = new JPanel();
                buttonPanel.setLayout(new MigLayout("ltr,fill,wrap 2"));
                buttonPanel.add(getButtonBack(), "align left");
            }
            return buttonPanel;
        }

        public JOptionPane getBatchSelectionPane(String title, Object[] choices) {
            JOptionPane pane = new JOptionPane(title, JOptionPane.PLAIN_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION, null,
                null, null);
            pane.setWantsInput(true);
            pane.setSelectionValues(choices);
            pane.setInitialSelectionValue(choices[0]);
            pane.selectInitialValue();

            return pane;
        }
    }

    private void saveSelected(String strBatchJobId) {
        for (IBatchJobIdSkuListener listener : listeners) {
            listener.saveBatchJobId(strBatchJobId);
        }
    }

    private void saveSelectedHist(String strBatchJobId) {
        for (IBatchJobIdSkuListener listener : listeners) {
            listener.saveBatchJobHist(strBatchJobId);
        }
    }

    private void generateBatchJobId(String batchJobSeq, String batchJobSkuId) {
        for (IBatchJobIdSkuListener listener : listeners) {
            listener.generateBatchJobId(batchJobSeq, batchJobSkuId);
        }
    }

    private void returnToSelection() {
        for (IBatchJobIdSkuListener listener : listeners) {
            listener.returnToSelection();
        }
    }
}