package com.sicpa.tt065.devices.plc;


import com.sicpa.standard.client.common.utils.StringMap;
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

	private static final Logger logger = LoggerFactory.getLogger(PlcPowerSupplyHandler.class);


	private PlcProvider plcProvider;
	private String upsVarName;
	private String systemTypeVarName;
	private IPlcValuesLoader loader;

	private final Map<Integer, Short> systemTypes = new HashMap<>();

    Timer timer;

    public void shutdown(int seconds, boolean value) {
        timer = new Timer();
        if (!value){
            timer.schedule(new ShutdownTask(), seconds*1000);
        }else{
            timer.cancel(); //Terminate the timer thread
        }
    }

    class ShutdownTask extends TimerTask {
        public void run() {
            logger.debug("No power supply. System is shutting down...");
            try {
                Runtime runtime = Runtime.getRuntime();

                Process proc = runtime.exec("shutdown -h now");
                System.exit(0);

            }catch (IOException e) {
                throw new RuntimeException(e);}
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
        shutdown(30, value);
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
