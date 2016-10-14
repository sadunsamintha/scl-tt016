package com.sicpa.standard.sasscl.devices.plc;

import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.plc.value.PlcVariable;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class PlcJmxInfo implements IPlcJmxInfo {

	private static final Logger logger = LoggerFactory.getLogger(PlcJmxInfo.class);

	private PlcProvider plcProvider;
	private final Collection<IPlcVariable<?>> plcCabinetVars = new ArrayList<>();
	private final Collection<IPlcVariable<?>> plcLineVars = new ArrayList<>();
	private final Set<Integer> indexesToCheck = new HashSet<>();

	private String trilightGreenVarName;
	private String trilightYellowVarName;
	private String trilightRedVarName;

	public String getPlcVersion() {
		return readPlcVersion();
	}

	public String getPlcInfoVars() {
		return readAllInfoVar();
	}

    @Override
	public String getTrilightValues() {
		IPlcAdaptor plcAdaptor = plcProvider.get();

		if (plcAdaptor == null || !plcAdaptor.isConnected()) {
			return "plc not connected";
		}

		try {
			return getTrilightValuesString(plcAdaptor);
        } catch (Exception e) {
            logger.error("", e);
        }

		return "";
	}

	protected Collection<IPlcVariable<?>> transformToIndex(Collection<IPlcVariable<?>> vars, int index) {
		Collection<IPlcVariable<?>> res = new ArrayList<>();
		for (IPlcVariable<?> var : vars) {
			res.add(PlcLineHelper.clone(var, index));
		}
		return res;
	}

	protected String readAllInfoVar() {
		IPlcAdaptor plc = plcProvider.get();
		if (plc == null || !plc.isConnected()) {
			return "plc not connected";
		}
		String res = "";

		try {
			res += readVars(plcCabinetVars);
		} catch (PlcAdaptorException e) {
			logger.error("", e);
		}

		Collection<Integer> indexesFailed = new ArrayList<Integer>();
		for (int index : indexesToCheck) {
			try {
				res += readVars(transformToIndex(plcLineVars, index));
			} catch (PlcAdaptorException e) {
				logger.error("ignoring index in jmx read plc var:" + index);
				indexesFailed.add(index);
			}
		}
		indexesToCheck.removeAll(indexesFailed);

		return res;
	}

	protected String readVars(Collection<IPlcVariable<?>> vars) throws PlcAdaptorException {
		IPlcAdaptor plc = plcProvider.get();
		String res = "";
		int errorCount = 0;
		for (IPlcVariable<?> var : vars) {
			try {
				res += var.getVariableName() + ":" + plc.read(var) + "\n";
			} catch (Exception e) {
				errorCount++;
			}
		}
		if (errorCount >= vars.size()) {
			throw new PlcAdaptorException();
		}
		return res;
	}

	protected String readPlcVersion() {
		IPlcAdaptor plc = plcProvider.get();
		if (plc == null || !plc.isConnected()) {
			return "plc not connected";
		}
		return plc.getPlcVersion();
	}

	private String getTrilightValuesString(IPlcAdaptor plcAdaptor) throws PlcAdaptorException {
		String trilightValues = "";

		for (Integer lineIndex : PlcLineHelper.getLineIndexes()) {
			int greenValue = plcAdaptor.read(PlcVariable.createInt32Var(
					PlcLineHelper.replaceLinePlaceholder(trilightGreenVarName, lineIndex)));
			int yellowValue = plcAdaptor.read(PlcVariable.createInt32Var(
					PlcLineHelper.replaceLinePlaceholder(trilightYellowVarName, lineIndex)));
			int redValue = plcAdaptor.read(PlcVariable.createInt32Var(
					PlcLineHelper.replaceLinePlaceholder(trilightRedVarName, lineIndex)));

			trilightValues += String.format("line%d[green:%d|yellow:%d|red:%d]",
					lineIndex, greenValue, yellowValue, redValue);

		}

		return trilightValues;
	}

	public void setPlcCabinetVars(Collection<IPlcVariable<?>> plcCabinetVars) {
		this.plcCabinetVars.addAll(plcCabinetVars);
	}

	public void setPlcLineVars(Collection<IPlcVariable<?>> plcLineVars) {
		this.plcLineVars.addAll(plcLineVars);
	}

	public void addPlcCabinetVars(Collection<IPlcVariable<?>> plcCabinetVars) {
		this.plcCabinetVars.addAll(plcCabinetVars);
	}

	public void addPlcLineVars(Collection<IPlcVariable<?>> plcLineVars) {
		this.plcLineVars.addAll(plcLineVars);
	}

	public void setPlcProvider(PlcProvider plcProvider) {
		this.plcProvider = plcProvider;
	}

	public void setLineCount(int lineCount) {
		for (int i = 1; i <= lineCount; i++) {
			indexesToCheck.add(i);
		}
	}

    public void setTrilightGreenVarName(String trilightGreenVarName) {
        this.trilightGreenVarName = trilightGreenVarName;
    }

    public void setTrilightYellowVarName(String trilightYellowVarName) {
        this.trilightYellowVarName = trilightYellowVarName;
    }

    public void setTrilightRedVarName(String trilightRedVarName) {
        this.trilightRedVarName = trilightRedVarName;
    }
}
