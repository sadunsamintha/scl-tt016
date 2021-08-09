package com.sicpa.tt016.devices.brs;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.gui.screen.machine.component.warning.Message;
import com.sicpa.standard.sasscl.devices.DeviceException;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.brs.BrsAdaptor;
import com.sicpa.standard.sasscl.devices.brs.event.BrsProductEvent;
import com.sicpa.standard.sasscl.devices.brs.model.BrsModel;
import com.sicpa.standard.sasscl.devices.brs.reader.CodeReaderController;
import com.sicpa.standard.sasscl.view.messages.IMessagesView;

public class TT016BrsAdaptor extends BrsAdaptor {
	
	private static final Logger logger = LoggerFactory.getLogger(TT016BrsAdaptor.class);
    
	private boolean blockProduction;
	
	protected IMessagesView view;
	
	public TT016BrsAdaptor(BrsModel model) throws DeviceException {
        super(model);
    }
	
	@Override
    public boolean isBlockProductionStart() {
        return blockProduction;
    }
	
	@Override
    public void onCodeReceived(String code, CodeReaderController brsReaderController) {
    	if(brsReaderController!=null && this.status.equals(DeviceStatus.CONNECTING)) {
    		removeWarningMessage();
    		brsReaderController.onBrsConnected(true);
    	}else {
    		EventBusService.post(new BrsProductEvent(code));
    	}
    }
	
	private void removeWarningMessage() {
		if (!blockProduction) {
        	view.getDelegate().getModel().removeMessage("[DEV_04]");
		}
	}
	
	@Override
    protected void doDisconnect() throws DeviceException {
        logger.debug("disconnecting brs devices");
        try {
        	stopReaders();
            fireDeviceStatusChanged(DeviceStatus.DISCONNECTED);
        }catch(Exception ex) {
        	List<Message> msg = view.getDelegate().getModel().getMessages();
    		for (Message mesg : msg) {
    			logger.info(mesg.getCode());
    		    logger.info(mesg.getMessage());
    		}
        	logger.error("Error disconnecting BRS: {}", ex);
        }
    }
	
	@Override
    public void doStart() throws DeviceException {
        logger.debug("starting brs devices");
        try {
        	startReaders();
            fireDeviceStatusChanged(DeviceStatus.STARTED);
        }catch(Exception ex) {
        	List<Message> msg = view.getDelegate().getModel().getMessages();
    		for (Message mesg : msg) {
    			logger.info(mesg.getCode());
    		    logger.info(mesg.getMessage());
    		}
        	logger.error("Error starting BRS: {}", ex);
        }
    }

	public void setBlockProduction(boolean blockProduction) {
		this.blockProduction = blockProduction;
	}

	public void setView(IMessagesView view) {
		this.view = view;
	}
	
}
