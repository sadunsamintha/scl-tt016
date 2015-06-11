package com.sicpa.standard.sasscl.devices.printer.xcode;

import java.util.List;

import com.sicpa.standard.printer.xcode.ExtendedCode;

public interface IExCodeBehavior {

	List<ExtendedCode> createExCodes(List<String> codes);

}
