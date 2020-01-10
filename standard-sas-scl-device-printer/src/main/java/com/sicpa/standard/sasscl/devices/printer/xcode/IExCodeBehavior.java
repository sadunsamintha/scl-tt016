package com.sicpa.standard.sasscl.devices.printer.xcode;

import java.util.List;

import com.sicpa.standard.gui.utils.Pair;
import com.sicpa.standard.printer.xcode.ExtendedCode;

public interface IExCodeBehavior {

	List<ExtendedCode> createExCodes(List<String> codes);
	List<ExtendedCode> createExCodesPair(List<Pair<String, String>> codes);

}
