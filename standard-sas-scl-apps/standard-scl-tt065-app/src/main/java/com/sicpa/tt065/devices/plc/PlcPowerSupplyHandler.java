package com.sicpa.tt065.devices.plc;


import com.sicpa.standard.client.common.utils.StringMap;
import com.sicpa.standard.sasscl.controller.flow.IFlowControl;
import com.sicpa.standard.sasscl.devices.plc.IPlcListener;
import com.sicpa.standard.sasscl.devices.plc.IPlcValuesLoader;
import com.sicpa.standard.sasscl.devices.plc.event.PlcEvent;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import static com.sicpa.standard.sasscl.devices.plc.PlcLineHelper.getLinesVariableName;

public class PlcPowerSupplyHandler {

	private static final int FORCE_SHUTDOWN_DELAY_MIN = 1;
	private static final int EXIT_APPLICATION_DELAY_SEC = 10;

	private static final Logger logger = LoggerFactory.getLogger(PlcPowerSupplyHandler.class);


	private PlcProvider plcProvider;
	private String upsVarName;
	private String systemTypeVarName;
	private IPlcValuesLoader loader;
	private IFlowControl flowControl;
	private Timer timer = null;

	private final Map<Integer, Short> systemTypes = new HashMap<>();

    public void shutdown(boolean value) {

    	if (!value){
			logger.info("No power supply");
			timer = new Timer();
			timer.schedule(new ShutdownTask(), EXIT_APPLICATION_DELAY_SEC *1000);
            timer.schedule(new ExitGentlyTask(), EXIT_APPLICATION_DELAY_SEC *1000);
        }else{
			logger.info("Power supply Ok");
			if (timer != null) {
				timer.cancel(); //Terminate the timer thread
				timer.purge();
			}
        }
    }

	public void setFlowControl(IFlowControl flowControl) {
		this.flowControl = flowControl;
	}

	class ExitGentlyTask extends TimerTask {
		public void run() {
			timer = null;
			logger.info("Trying to exit application gently...");
			flowControl.notifyExitApplication();
		}
	}

	class ShutdownTask extends TimerTask {
        public void run() {
			Runtime runtime = Runtime.getRuntime();

			logger.info("Preparing to shut down in " + FORCE_SHUTDOWN_DELAY_MIN + " seconds...");

			try {
				Process proc = runtime.exec("shutdown -r +" + FORCE_SHUTDOWN_DELAY_MIN);
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.exit(0);
		}
    }


	public void setPlcProvider(PlcProvider plcProvider) {
		this.plcProvider = plcProvider;
		plcProvider.addChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				addUpsStatusListener();
			}
		});
	}

	private void addUpsStatusListener() {
		plcProvider.get().addPlcListener(new IPlcListener() {
			@Override
			public void onPlcEvent(PlcEvent event) {
                handleUpsEvent(event);
			}

			@Override
			public List<String> getListeningVariables() {
				return new ArrayList<>(getLinesVariableName(upsVarName));
			}
		});
	}

	private void handleUpsEvent(PlcEvent event) {
		Boolean value = Boolean.parseBoolean(String.valueOf(event.getValue()));
		logger.debug("UPS:" + value);
        shutdown(value);
	}

	private Map<Integer, Short> getSystemTypesByLine() {
		if (systemTypes.isEmpty()) {
			for (Entry<Integer, StringMap> entry : loader.getValues().entrySet()) {
				int lineIndex = entry.getKey();
				for (Entry<String, String> e : entry.getValue().entrySet()) {
					if (e.getKey().equals(systemTypeVarName)) {
						systemTypes.put(lineIndex, Short.parseShort(e.getValue()));
					}
				}
			}
		}
		return systemTypes;
	}

	public void setSystemTypeVarName(String systemTypeVarName) {
		this.systemTypeVarName = systemTypeVarName;
	}

	public void setUpsVarName(String upsVarName) {
		this.upsVarName = upsVarName;
	}

	public void setLoader(IPlcValuesLoader loader) {
		this.loader = loader;
	}
}
