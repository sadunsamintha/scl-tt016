package com.sicpa.standard.sasscl.view.encoder;

import java.awt.Component;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.view.SecuredComponentGetter;
import com.sicpa.standard.sasscl.common.storage.IStorage;
import com.sicpa.standard.sasscl.security.SasSclPermission;

public class EncodersInfoViewGetter extends SecuredComponentGetter {

	public EncodersInfoViewGetter() {
		super(SasSclPermission.DISPLAY_ENCODERS_VIEW, "view.encoder.title");
	}

	protected EncodersInfoView view;
	protected IStorage storage;

	@Override
	public Component getComponent() {
		if (view == null) {
			view = new EncodersInfoView();
			EventBusService.register(view);
			view.setStorage(storage);
		}
		return view;
	}

	public void setStorage(IStorage storage) {
		this.storage = storage;
	}
}
