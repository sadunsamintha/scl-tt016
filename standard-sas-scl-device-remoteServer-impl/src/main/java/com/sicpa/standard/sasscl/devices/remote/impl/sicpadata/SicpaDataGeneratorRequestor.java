package com.sicpa.standard.sasscl.devices.remote.impl.sicpadata;

import java.util.Arrays;
import java.util.Collection;

import com.sicpa.std.common.api.coding.business.CodingServiceHandler;
import com.sicpa.std.common.api.coding.dto.SicpadataGeneratorOrderDto;
import com.sicpa.std.server.util.transfer.failover.sdgen.receiver.AbstractSDGenReceiver;
import com.sicpa.std.server.util.transfer.failover.sdgen.spi.IncomingSDGenStorageSpi;

public class SicpaDataGeneratorRequestor implements ISicpaDataGeneratorRequestor {

	protected SicpadataGeneratorOrderDto order;
	protected CodingServiceHandler sender;

	protected AbstractSDGenReceiver<SicpadataGeneratorOrderDto> receiver = new AbstractSDGenReceiver<SicpadataGeneratorOrderDto>() {
		@Override
		protected Collection<SicpadataGeneratorOrderDto> getOrders() {
			return Arrays.asList(order);
		}

		@Override
		protected CodingServiceHandler getSender() {
			return sender;
		}
	};

	public SicpaDataGeneratorRequestor() {
	}

	@Override
	public void requestSicpadataGenerators(SicpadataGeneratorOrderDto dto, CodingServiceHandler sender) {
		this.order = dto;
		this.sender = sender;
		this.receiver.requestSicpadataGenerators();
	}

	public void setIncomingStorageProvider(IncomingSDGenStorageSpi incomingStorageSpi) {
		receiver.setIncomingStorageProvider(incomingStorageSpi);
	}
}
