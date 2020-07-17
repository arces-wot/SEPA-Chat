package it.unibo.arces.wot.sepa.apps.chat.client;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.unibo.arces.wot.sepa.apps.chat.ChatClient;
import it.unibo.arces.wot.sepa.apps.chat.ChatMonitor;
import it.unibo.arces.wot.sepa.apps.chat.Users;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPABindingsException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAPropertiesException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAProtocolException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;

public class BasicClient extends ChatClient {
	private static final Logger logger = LogManager.getLogger();

	protected String user;
	private Users users;
	private int messages = 10;
	private ChatMonitor monitor;
	
	public BasicClient(String userURI, Users users,int messages,ChatMonitor monitor)
			throws SEPAProtocolException, SEPASecurityException, SEPAPropertiesException, SEPABindingsException, IOException, InterruptedException {
		super(userURI);

		this.user = userURI;
		this.users = users;
		this.messages = messages;
		this.monitor = monitor;
	}

	@Override
	public void run() {
		int n = 0;
		
		for (int i = 0; i < messages; i++) {
			for (String receiver : users.getUsers()) {
				if (receiver.equals(user) && users.getUsers().size() > 1)
					continue;
				n++;
				logger.debug(users.getUserName(user) + " SEND MESSAGE (" + n + "/" + messages *  (users.getUsers().size()-1) +")");
				sendMessage(receiver, "MSG #" + n);
			}
		}

		try {
			synchronized (this) {
				wait();
			}
		} catch (InterruptedException e) {
		}
	}

	@Override
	public void onMessageReceived(String userUri, String messageUri, String name, String message,String time) {
		monitor.messageReceived(user);
	}

	@Override
	public void onMessageRemoved(String userUri, String messageUri, String name, String message, String time) {
		monitor.messageRemoved(user);
	}

	@Override
	public void onMessageSent(String userUri, String messageUri, String time) {
		monitor.messageSent(user);
	}

	@Override
	public void onRemoverBrokenConnection(String userUri) {
		monitor.brokenConnectionRemover(user);
	}

	@Override
	public void onReceiverBrokenConnection(String userUri) {
		monitor.brokenConnectionReceiver(userUri);
		
	}
}
