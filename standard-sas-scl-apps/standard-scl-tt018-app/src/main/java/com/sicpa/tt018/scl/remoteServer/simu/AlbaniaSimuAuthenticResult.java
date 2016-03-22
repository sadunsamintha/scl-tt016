package com.sicpa.tt018.scl.remoteServer.simu;

import com.sicpa.tt018.common.security.authenticator.StdAlbaniaAuthenResult;

public class AlbaniaSimuAuthenticResult extends StdAlbaniaAuthenResult {

	public AlbaniaSimuAuthenticResult(int codeType, int sequence) {
		super(true, true, 1, 1L, 20L, codeType, sequence);
	}

}
