package com.sicpa.standard.sasscl.devices.remote.impl.remoteservices;

import java.util.Collection;
import java.util.List;

import com.sicpa.std.common.api.activation.dto.AuthorizedProductsDto;
import com.sicpa.std.common.api.activation.dto.SicpadataReaderDto;
import com.sicpa.std.common.api.activation.dto.productionData.authenticated.AuthenticatedProductsResultDto;
import com.sicpa.std.common.api.activation.dto.productionData.counted.CountedProductsResultDto;
import com.sicpa.std.common.api.activation.exception.ActivationException;
import com.sicpa.std.common.api.coding.business.CodingServiceHandler;
import com.sicpa.std.common.api.coding.dto.SicpadataGeneratorInfoDto;
import com.sicpa.std.common.api.coding.dto.SicpadataGeneratorInfoResultDto;
import com.sicpa.std.common.api.common.CommonServerRuntimeException;
import com.sicpa.std.common.api.monitoring.dto.EventDto;
import com.sicpa.std.common.api.monitoring.exception.MonitoringException;
import com.sicpa.std.common.api.multilingual.common.CustomResourceBundle;
import com.sicpa.std.common.api.multilingual.dto.AvailableLanguageDto;
import com.sicpa.std.common.api.physical.dto.SubsystemDto;
import com.sicpa.std.common.api.security.dto.LoginDto;

/**
 * facade for the EJB call
 */
public interface IRemoteServices {

	boolean isAlive();

	LoginDto logUserIn(String login, String password) throws Exception;

	Collection<AvailableLanguageDto> getAvailableLanguages() throws Exception;

	CustomResourceBundle getResourceBundle(String lang) throws Exception;

	AuthorizedProductsDto provideAuthorizedProducts();

	void registerActivationProducts(AuthenticatedProductsResultDto data) throws ActivationException;

	void registerCountedProducts(CountedProductsResultDto data) throws ActivationException;

	void login() throws Exception;

	SubsystemDto getSubsystem() throws Exception;

	void registerEvent(EventDto evt) throws MonitoringException;

	SicpadataGeneratorInfoResultDto registerGeneratorsCycle(List<SicpadataGeneratorInfoDto> dtos);

	SicpadataReaderDto provideSicpadataReader() throws ActivationException, CommonServerRuntimeException;

	CodingServiceHandler getCodingService();

	String getCryptoPassword();

}
