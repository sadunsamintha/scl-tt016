package com.sicpa.standard.sasscl.devices.brs;


import com.sicpa.common.device.reader.CodeReceiver;
import com.sicpa.common.device.reader.DisconnectionListener;
import com.sicpa.common.device.reader.lifecheck.EchoListener;

public interface BrsReaderDevice extends  CodeReceiver, DisconnectionListener, EchoListener {

}
