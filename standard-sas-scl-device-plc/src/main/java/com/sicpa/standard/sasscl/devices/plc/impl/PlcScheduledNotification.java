package com.sicpa.standard.sasscl.devices.plc.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.codehaus.groovy.runtime.metaclass.MethodMetaProperty.GetBeanMethodMetaProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.plc.value.PlcVariable;
import com.sicpa.standard.sasscl.devices.plc.PlcAdaptorException;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;

public class PlcScheduledNotification {
	
	private final static Logger logger = LoggerFactory.getLogger(PlcScheduledNotification.class);

    private PlcProvider plcProvider;
    
    private String boardTemp;
    private String cpuTemp;
    private String eeCabinetTemp;
    private String ambiantTemp;

    public void log() throws PlcAdaptorException {
    	if(null != plcProvider && null != plcProvider.get() && plcProvider.get().isConnected()) {
	    	List<IPlcVariable> plcTemperatureVars = new ArrayList<IPlcVariable>();
	    	plcTemperatureVars.add(PlcVariable.createByteVar(boardTemp));
	    	plcTemperatureVars.add(PlcVariable.createByteVar(cpuTemp));
	    	plcTemperatureVars.add(PlcVariable.createShortVar(eeCabinetTemp));
	    	plcTemperatureVars.add(PlcVariable.createShortVar(ambiantTemp));
	        for (IPlcVariable plcTemperatureVar : plcTemperatureVars) {
	            try {
	                logger.info("Temperature variable {} received with value {}",plcTemperatureVar.getVariableName(), readPlcVar(plcTemperatureVar).toString());
	            } catch (Exception e) {
	                logger.error("Error reading PLC variable: {}", plcTemperatureVar.getVariableName());
	            }
	        }
    	}
    }

    private Object readPlcVar(IPlcVariable var) throws PlcAdaptorException {
        return plcProvider.get().read(var);
    }
    
    public void setPlcProvider(PlcProvider plcProvider) {
        this.plcProvider = plcProvider;
    }

	public void setBoardTemp(String boardTemp) {
		this.boardTemp = boardTemp;
	}

	public void setCpuTemp(String cpuTemp) {
		this.cpuTemp = cpuTemp;
	}

	public void setEeCabinetTemp(String eeCabinetTemp) {
		this.eeCabinetTemp = eeCabinetTemp;
	}

	public void setAmbiantTemp(String ambiantTemp) {
		this.ambiantTemp = ambiantTemp;
	}
   
}
