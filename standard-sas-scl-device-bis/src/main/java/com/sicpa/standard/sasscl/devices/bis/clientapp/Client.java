package com.sicpa.standard.sasscl.devices.bis.clientapp;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.springframework.util.CollectionUtils;

import com.sicpa.standard.sasscl.devices.bis.BisAdaptorException;
import com.sicpa.standard.sasscl.devices.bis.IBisControllerListener;
import com.sicpa.standard.sasscl.devices.bis.controller.BisRemoteServer;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.std.bis2.core.messages.RemoteMessages;
import com.sicpa.std.bis2.core.messages.RemoteMessages.Alert;
import com.sicpa.std.bis2.core.messages.RemoteMessages.LifeCheck;
import com.sicpa.std.bis2.core.messages.RemoteMessages.RecognitionResultMessage;
import com.sicpa.std.bis2.core.messages.RemoteMessages.SkuMessage;

public class Client extends JFrame implements IBisControllerListener {

	private static final long serialVersionUID = -5002326855091536463L;

	private BisRemoteServer bisController;

	// GUI
	private JPanel mainPanel;

	private JButton connectButton;

	private JButton sendSKUButton;

	private JButton startButton;

	private JButton stopButton;

	private JButton disconnectButton;

	private JButton clearButton;

	private JTextArea textArea;

	private StringBuffer strBuffer = new StringBuffer();

	private String ip = "localhost";
	private int port = 8020;
	private int connectionLifeCheckInterval = 1000;
	private int recognitionResultRequestInterval = 1000;

	public void setup() {
		bisController = new BisRemoteServer();
		bisController.setIp(ip);
		bisController.setConnectionLifeCheckIntervalMs(connectionLifeCheckInterval);
		bisController.setRecognitionResultRequestIntervalMs(recognitionResultRequestInterval);
		bisController.setPort(port);
		bisController.addListener(this);
		bisController.init();
	}

	public void initView() {
		this.mainPanel = new JPanel();
		this.mainPanel.setLayout(new BorderLayout());

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());

		this.connectButton = new JButton("Connect");
		this.connectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					bisController.connect();
				} catch (BisAdaptorException e1) {
					e1.printStackTrace();
				}
			}
		});

		this.sendSKUButton = new JButton("Send SKU");
		this.sendSKUButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				List<SKU> skuList = new ArrayList<SKU>();
				skuList.add(new SKU(1, "a"));
				skuList.add(new SKU(2, "b"));
				skuList.add(new SKU(3, "c"));
				skuList.add(new SKU(4, "d"));
				skuList.add(new SKU(5, "e"));
				sendSKUList(skuList);
			}
		});

		this.startButton = new JButton("Start");
		this.startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					bisController.start();
				} catch (BisAdaptorException e1) {
					e1.printStackTrace();
				}
			}
		});

		this.stopButton = new JButton("Stop");
		this.stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				bisController.stop();
			}
		});

		this.disconnectButton = new JButton("Disconnect");
		this.disconnectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				bisController.disconnect();
			}
		});

		this.clearButton = new JButton("Clear Log");
		this.clearButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				textArea.setText("");
				strBuffer = new StringBuffer();
			}
		});

		buttonPanel.add(this.connectButton);
		buttonPanel.add(this.sendSKUButton);
		buttonPanel.add(this.startButton);
		buttonPanel.add(this.stopButton);
		buttonPanel.add(this.disconnectButton);
		buttonPanel.add(this.clearButton);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setEditable(false);
		textArea.setVisible(true);

		JScrollPane scroll = new JScrollPane(textArea);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		this.mainPanel.add(scroll, BorderLayout.CENTER);
		this.mainPanel.add(buttonPanel, BorderLayout.SOUTH);

		this.setSize(new Dimension(600, 500));
		this.add(this.mainPanel);
		this.setVisible(true);
	}

	public static void main(String[] args) {
		try {
			Client client = new Client();
			client.setup();
			client.initView();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendSKUList(List<SKU> skuList) {
		if (!CollectionUtils.isEmpty(skuList)) {
			List<SkuMessage> controllerSkus = new ArrayList<SkuMessage>();
			for (SKU sku : skuList) {
				controllerSkus.add(RemoteMessages.SkuMessage.newBuilder().setId(sku.getId())
						.setDescription(sku.getDescription()).build());
			}
			bisController.sendSkuList(RemoteMessages.SkusMessage.newBuilder().addAllSku(controllerSkus).build());
		}
	}

	@Override
	public void onConnection() {
		strBuffer.append("BIS is connected.\n");
		this.textArea.setText(strBuffer.toString());
	}

	@Override
	public void onDisconnection() {
		strBuffer.append("BIS is not connected.\n");
		this.textArea.setText(strBuffer.toString());
	}

	@Override
	public void lifeCheckReceived(LifeCheck lifeCheckResponse) {
		strBuffer.append("Life Check received : \n");
		strBuffer.append("\t Type : " + lifeCheckResponse.getType() + "\n");
		this.textArea.setText(strBuffer.toString());
	}

	@Override
	public void alertReceived(Alert command) {
		strBuffer.append("Getting alert command : \n");
		strBuffer.append("\t Severity : " + command.getSeverity().getValueDescriptor().getName() + " \n");
		strBuffer.append("\t Who : " + command.getWhoId() + " : " + command.getWhoDesc() + "\n");
		strBuffer.append("\t When : " + command.getWhen() + "\n");
		strBuffer.append("\t What : " + command.getWhat() + "\n");
		this.textArea.setText(strBuffer.toString());
	}

	@Override
	public void recognitionResultReceived(RecognitionResultMessage result) {
		strBuffer.append("Getting RecognitionResultMessage : \n");
		strBuffer.append("\t SKU ID :" + result.getConfidence().getId() + "\n");
		strBuffer.append("\t confident :" + result.getConfidence().getConfidence() + "\n");
	}

	@Override
	public void onLifeCheckFailed() {
		strBuffer.append("BIS is disconnected.\n");
		this.textArea.setText(strBuffer.toString());
	}

	@Override
	public void otherMessageReceived(Object result) {
		strBuffer.append("Other unknown messages : -----> \n");
		strBuffer.append(result);
		strBuffer.append("\n------ end of unknown msg content ----");
		strBuffer.append("\n\n");
		this.textArea.setText(strBuffer.toString());
	}
}
