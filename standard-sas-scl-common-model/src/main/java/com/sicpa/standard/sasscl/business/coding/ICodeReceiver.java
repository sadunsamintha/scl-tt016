package com.sicpa.standard.sasscl.business.coding;

import java.util.List;

import com.sicpa.standard.printer.xcode.ExtendedCode;

public interface ICodeReceiver {

	void provideCode(List<String> c,Object o) throws CodeReceivedFailedException;

	void provideExtendedCode(List<ExtendedCode> c,Object o) throws CodeReceivedFailedException;

}
