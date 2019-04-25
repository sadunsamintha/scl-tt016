package com.sicpa.standard.sasscl.devices.brs.reader;


import com.sicpa.common.device.reader.CodeReceiver;
import com.sicpa.common.device.reader.DisconnectionListener;
import com.sicpa.common.device.reader.lifecheck.EchoListener;
import com.sicpa.standard.sasscl.devices.DeviceException;
import com.sicpa.standard.sasscl.devices.brs.reader.CodeReaderAdaptor;

public interface CodeReaderController extends CodeReceiver, DisconnectionListener, EchoListener {


    /**
     * Starts the code reader.
     *
     * @throws DeviceException if something goes wrong while starting the code reader.
     */
    void start() throws DeviceException;

    /**
     * Stops the code reader.
     * @throws DeviceException if something goes wrong while stopping the code reader.
     */
    void stop() throws DeviceException;

    /**
     * Connects to the code reader.
     * @throws DeviceException if something goes wrong during the connection.
     */
    void connect() throws DeviceException;

    /**
     * Disconnects the code reader.
     * @throws DeviceException if something goes wrong during the disconnection.
     */
    void disconnect() throws DeviceException;

    /**
     * Sets the {@link CodeReaderAdaptor} in the controller.
     * @param codeReaderAdaptor The {@link CodeReaderAdaptor} to be set.
     */
    void setCodeReader(CodeReaderAdaptor codeReaderAdaptor);

    /**
     * @return
     */
    String getId();

    /**
     * @return <code>true</code> if the code reader is connected otherwise returns <code>false</code>.
     */
    boolean isConnected();
    
    void onBrsConnected(boolean connected);

}
