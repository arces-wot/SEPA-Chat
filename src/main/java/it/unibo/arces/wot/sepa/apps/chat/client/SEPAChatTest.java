package it.unibo.arces.wot.sepa.apps.chat.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.unibo.arces.wot.sepa.apps.chat.ChatClient;
import it.unibo.arces.wot.sepa.apps.chat.ChatMonitor;
import it.unibo.arces.wot.sepa.apps.chat.DeleteAll;
import it.unibo.arces.wot.sepa.apps.chat.JSAPProvider;
import it.unibo.arces.wot.sepa.apps.chat.UserRegistration;
import it.unibo.arces.wot.sepa.apps.chat.Users;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPABindingsException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAPropertiesException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAProtocolException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;

public class SEPAChatTest {
	private static final Logger logger = LogManager.getLogger();
	
	private static int N_CLIENTS = 2;
	private static int BASE = 0;
	private static int MESSAGES = 1;

	private static Users users;
	private static List<ChatClient> clients = new ArrayList<ChatClient>();

	private static ChatMonitor monitor;

	private static JSAPProvider cfg;

	public static void main(String[] args) throws SEPASecurityException, SEPAPropertiesException, SEPAProtocolException, IOException, InterruptedException, SEPABindingsException {
		init();	
		basicChatTest();
	}
	
	private static void init() throws SEPASecurityException, SEPAPropertiesException, SEPAProtocolException, IOException {
		cfg = new JSAPProvider();

		BASE = cfg.getJsap().getExtendedData().get("base").getAsInt();
		N_CLIENTS = cfg.getJsap().getExtendedData().get("clients").getAsInt();
		MESSAGES = cfg.getJsap().getExtendedData().get("messages").getAsInt();

		deleteAllClients();
		registerClients();

		users = new Users();
	}

	private static void basicChatTest() throws SEPAProtocolException, SEPAPropertiesException, SEPASecurityException,
			InterruptedException, IOException, SEPABindingsException {

		users.joinChat();
		
		monitor = new ChatMonitor(users.getUsers(), MESSAGES);
		monitor.start();
		
		for (String user : users.getUsers()) {
			ChatClient client = new BasicClient(user, users, MESSAGES,monitor);
			clients.add(client);
		}
		
		for (ChatClient client : clients) {
			Thread th = new Thread(client);
			th.start();
		}
		
		monitor.join();
		
		System.exit(0);
	}

	private static void deleteAllClients()
			throws SEPAProtocolException, SEPAPropertiesException, SEPASecurityException, IOException {
		DeleteAll client = new DeleteAll();
		client.clean();

		client.close();
	}

	private static void registerClients() throws SEPAProtocolException, SEPAPropertiesException, SEPASecurityException, IOException {
		// Register chat BOTS
		UserRegistration registration = new UserRegistration();
		for (int i = BASE; i < BASE + N_CLIENTS; i++) {
			logger.info("Register client: "+"ChatBot" + i);
			registration.register("ChatBot" + i);
		}

		registration.close();
	}
}
