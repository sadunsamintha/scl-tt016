package com.sicpa.tt016.scl.remote.assembler;

import com.sicpa.standard.sasscl.model.EncoderInfo;
import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;
import com.sicpa.standard.sasscl.sicpadata.reader.IAuthenticator;
import com.sicpa.tt016.common.dto.EncoderInfoDTO;
import com.sicpa.tt016.common.dto.EncoderSclDTO;
import com.sicpa.tt016.common.security.authenticator.IMoroccoAuthenticator;
import com.sicpa.tt016.scl.remote.dto.DecoderDTO;
import com.sicpa.tt016.scl.remote.dto.EncoderDTO;

public class EncryptionConverter {

	public IAuthenticator convert(IMoroccoAuthenticator decoder) {
		return new DecoderDTO(decoder);
	}

	public EncoderInfoDTO convert(EncoderInfo info) {
		EncoderInfoDTO dto = new EncoderInfoDTO();
		dto.setBatchId(info.getBatchId());
		dto.setCodeTypeId(info.getCodeTypeId());
		dto.setEncoderOnClientDate(info.getOnClientDate());
		dto.setFinished(info.isFinished());
		dto.setFirstCodeDate(info.getFirstCodeDate());
		dto.setLastCodeDate(info.getLastCodeDate());
		dto.setLastUsedSequence(info.getSequence());
		return dto;
	}

	public IEncoder convert(EncoderSclDTO dto, int subsystemId, int codeTypeId) {
		return new EncoderDTO(dto.getMoroccoEncoder(), subsystemId, codeTypeId);
	}

}
