package com.sicpa.tt018.scl.remoteServer.simu;

import java.util.ArrayList;
import java.util.List;

import com.sicpa.tt018.interfaces.scl.master.dto.AlbaniaEncoderDTO;
import com.sicpa.tt018.interfaces.scl.master.dto.MarketTypeDTO;
import com.sicpa.tt018.interfaces.security.IAlbaniaAuthenticator;

public class AlbaniaRemoteServerModelSimulator {

	private int internalEncoderSequence = 0;

	private MarketTypeDTO marketTypeDTO;

	public IAlbaniaAuthenticator loadSimuAuthenticator() {
		return new AlbaniaSimuAuthenticator();
	}

	public void setMarketTypeDTO(MarketTypeDTO marketTypeDTO) {
		this.marketTypeDTO = marketTypeDTO;
	}

	public MarketTypeDTO getMarketTypeDTO() {
		return marketTypeDTO;
	}

	public List<AlbaniaEncoderDTO> provideEncoders(int quantity, int codeTypeId, int subSystemId) {

		List<AlbaniaEncoderDTO> listEncoders = new ArrayList<AlbaniaEncoderDTO>();

		for (int i = 0; i < quantity; i++) {
			AlbaniaEncoderDTO simuEncoderDTO = new AlbaniaEncoderDTO();
			simuEncoderDTO.setCryptoEncoder(new AlbaniaSimuEncoder(internalEncoderSequence, codeTypeId));
			simuEncoderDTO.setSubsystemId(subSystemId);
			listEncoders.add(simuEncoderDTO);
		}
		return listEncoders;
	}

}
