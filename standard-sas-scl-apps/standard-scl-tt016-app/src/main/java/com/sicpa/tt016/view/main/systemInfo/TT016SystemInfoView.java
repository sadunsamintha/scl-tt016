package com.sicpa.tt016.view.main.systemInfo;

import com.google.common.eventbus.Subscribe;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import net.miginfocom.swing.MigLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.plc.value.PlcVariable;
import com.sicpa.standard.sasscl.controller.ProductionParametersEvent;
import com.sicpa.standard.sasscl.devices.plc.AutomatedBeamStatus;
import com.sicpa.standard.sasscl.devices.plc.PlcAdaptorException;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;
import com.sicpa.standard.sasscl.view.LanguageSwitchEvent;
import com.sicpa.standard.sasscl.view.main.systemInfo.SystemInfoView;
import com.sicpa.tt016.scl.model.MoroccoSKU;

import static com.sicpa.standard.sasscl.devices.plc.AutomatedBeamPlcEnums.REQUEST_BEAM_CURRENT_POSITION_MM;
import static com.sicpa.standard.sasscl.devices.plc.PlcLineHelper.replaceLinePlaceholder;

public class TT016SystemInfoView extends SystemInfoView {

	private static final Logger logger = LoggerFactory.getLogger(TT016SystemInfoView.class);

	private JLabel labelBeamStatusText;
	private JLabel labelBeamStatusValue;

	private JLabel labelBeamHeightText;
	private JLabel labelBeamHeightValue;

	private boolean isBeamEnabled;

	private int conveyorHeight = 0;
	private int skuHeight = 0;

	private PlcProvider plcProvider;

	public TT016SystemInfoView() {
		super();
	}

	@Override
	protected void initGUI() {
		setLayout(new MigLayout());

		setLayout(new MigLayout("inset 0 0 0 0 ", "[]80[]", "[]15[]10[]"));

		add(getLabelTitle(), "spanx ,split 2,gaptop 5,gapleft 5");
		add(new JSeparator(), "growx,gapright 5");

		add(getLabelAppVersionText(), "gapleft 5");
		add(getLabelAppVersionValue(), "wrap,pushx");

		add(getLabelPlcVersionText(), "gapleft 5");
		add(getLabelPlcVersionValue(), "wrap,pushx");

		add(getLabelTime(), "gapleft 5");
		add(getLabelDateValue(), "");

		add(getLabelBeamStatusText(), "newline, gapleft 5");
		add(getLabelBeamStatusValue(), "wrap,pushx");

		add(getLabelBeamHeightText(), "gapleft 5");
		add(getLabelBeamHeightValue(), "wrap,pushx");

		add(getLabelServerStatus(), "newline ,gapleft 5 , spanx");
	}

	@Subscribe
	public void handleSkuSelection(ProductionParametersEvent evt) {
		skuHeight = ((MoroccoSKU) evt.getProductionParameters().getSku()).getProductHeight();
		updateBeamHeightValue();
	}

	@Subscribe
	public void handleBeamHeightSet(AutomatedBeamStatus evt) {
		if (isBeamEnabled) {
			getLabelBeamStatusValue().setVisible(true);
			getLabelBeamStatusText().setVisible(true);
			getLabelBeamHeightValue().setVisible(true);
			getLabelBeamHeightText().setVisible(true);

			if (evt.isManualMode()) {
				getLabelBeamStatusValue().setText(Messages.get("automated.beam.manual"));
			} else {
				getLabelBeamStatusValue().setText(Messages.get("automated.beam.motorized"));
				conveyorHeight = evt.getBeamConveyorHeight();
				updateBeamHeightValue();
			}

			ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(2);
			scheduledThreadPoolExecutor.scheduleAtFixedRate(this::updateBeamHeightValue,
				0 ,5, TimeUnit.SECONDS);
		}
	}

	@Override
	@Subscribe
	public void handleLanguageSwitch(LanguageSwitchEvent evt) {
		super.handleLanguageSwitch(evt);
		getLabelBeamStatusText().setText(Messages.get("automated.beam.status"));
		getLabelBeamHeightText().setText(Messages.get("automated.beam.height"));
	}

	public JLabel getLabelBeamStatusText() {
		if (labelBeamStatusText == null) {
			labelBeamStatusText = new JLabel();
			labelBeamStatusText.setVisible(false);
		}
		return labelBeamStatusText;
	}

	public JLabel getLabelBeamStatusValue() {
		if (labelBeamStatusValue == null) {
			labelBeamStatusValue = new JLabel();
			labelBeamStatusValue.setVisible(false);
		}
		return labelBeamStatusValue;
	}

	public JLabel getLabelBeamHeightText() {
		if (labelBeamHeightText == null) {
			labelBeamHeightText = new JLabel();
			labelBeamHeightText.setVisible(false);
		}
		return labelBeamHeightText;
	}

	public JLabel getLabelBeamHeightValue() {
		if (labelBeamHeightValue == null) {
			labelBeamHeightValue = new JLabel();
			labelBeamHeightValue.setVisible(false);
		}
		return labelBeamHeightValue;
	}

	private int getBeamActualHeight() throws PlcAdaptorException {
		IPlcVariable<Integer> beamCurrentPosition = PlcVariable.createInt32Var(
			replaceLinePlaceholder(REQUEST_BEAM_CURRENT_POSITION_MM.getNameOnPlc(), 1));

		return plcProvider.get().read(beamCurrentPosition);
	}

	private synchronized void updateBeamHeightValue() {
		try {
			int actualHeight = getBeamActualHeight();
			getLabelBeamHeightValue().setText((skuHeight + conveyorHeight) + "mm|" + actualHeight + "mm" );
		} catch (PlcAdaptorException | NullPointerException e) {
			logger.error("Failed to get beam height from PLC.", e);
			getLabelBeamHeightValue().setText((skuHeight + conveyorHeight) + "mm|NA");
		}
	}

	public void setBeamEnabled(boolean beamEnabled) {
		isBeamEnabled = beamEnabled;
	}

	public void setPlcProvider(PlcProvider plcProvider) {
		this.plcProvider = plcProvider;
	}
}
