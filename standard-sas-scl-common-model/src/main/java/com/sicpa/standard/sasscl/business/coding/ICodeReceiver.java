package com.sicpa.standard.sasscl.business.coding;

import java.util.List;

public interface ICodeReceiver {

	void provideCode(List<String> codes,Object requestor) throws CodeReceivedFailedException;
}
