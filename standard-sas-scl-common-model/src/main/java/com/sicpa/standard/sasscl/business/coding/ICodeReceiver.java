package com.sicpa.standard.sasscl.business.coding;

import java.util.List;

import com.sicpa.standard.gui.utils.Pair;

public interface ICodeReceiver {

	void provideCode(List<String> codes,Object requestor) throws CodeReceivedFailedException;
	void provideCodePair(List<Pair<String, String>> codes,Object requestor) throws CodeReceivedFailedException;
}
