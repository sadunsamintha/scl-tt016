package com.sicpa.standard.sasscl.devices.remote.impl.remoteservices;

import java.util.List;

import com.sicpa.gssd.tt021_tr.server.ext.sca.api.TrAuthenticatedProductsResultDto;
import com.sicpa.gssd.tt021_tr.server.ext.sca.api.TrCountedProductsResultDto;
import com.sicpa.std.common.api.activation.dto.AuthorizedProductsDto;
import com.sicpa.std.common.api.activation.dto.SicpadataReaderDto;
import com.sicpa.std.common.api.activation.exception.ActivationException;
import com.sicpa.std.common.api.coding.business.CodingServiceHandler;
import com.sicpa.std.common.api.coding.dto.SicpadataGeneratorInfoDto;
import com.sicpa.std.common.api.coding.dto.SicpadataGeneratorInfoResultDto;
import com.sicpa.std.common.api.common.CommonServerRuntimeException;
import com.sicpa.std.common.api.physical.dto.SubsystemDto;
import com.sicpa.std.common.api.security.dto.LoginDto;

public interface ITT021RemoteServices {

	void login() throws Exception;
	LoginDto logUserIn(String login, String password) throws Exception;
	boolean isAlive();
	
	SubsystemDto getSubsystem() throws Exception;
	AuthorizedProductsDto provideAuthorizedProducts();
	SicpadataReaderDto provideSicpadataReader() throws ActivationException, CommonServerRuntimeException;
	CodingServiceHandler getCodingService();
	void registerActivationProducts(TrAuthenticatedProductsResultDto data) throws ActivationException;
	void registerCountedProducts(TrCountedProductsResultDto trCountedProductsResultDto) throws ActivationException;
	SicpadataGeneratorInfoResultDto registerGeneratorsCycle(List<SicpadataGeneratorInfoDto> dtos);
}
