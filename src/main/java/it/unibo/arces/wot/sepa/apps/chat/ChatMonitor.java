package it.unibo.arces.wot.sepa.apps.chat;

import java.util.HashMap;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.unibo.arces.wot.sepa.commons.exceptions.SEPABindingsException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAPropertiesException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAProtocolException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;

public class ChatMonitor extends Thread {
	protected static final Logger logger = LogManager.getLogger();

	boolean allDone;

	class UserMonitor {
		private String user;
		private int messages;

		public UserMonitor(String user, int messages) {
			this.user = user;
			this.messages = messages;
		}

		public int sent = 0;
		public int received = 0;
		public int removed = 0;
		public boolean brokenConnectionRemover = false;
		public boolean brokenConnectionReceiver = false;

		public String toString() {
			return user + "|" + messages + "|" + received + "|" + sent + "|" + removed + "|" + brokenConnectionReceiver
					+ "|" + brokenConnectionRemover;
		}

		public boolean allDone() {
			return brokenConnectionRemover || brokenConnectionReceiver
					|| ((sent == messages) && (received == messages) && (removed == messages));
		}
	}

	private HashMap<String, UserMonitor> messageMap = new HashMap<>();

	public void run() {
		while (!allDone) {
			try {
				monitor();
			} catch (InterruptedException e) {
				return;
			}
//			for (UserMonitor mon : messageMap.values()) {
//				logger.info(mon);
//			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				return;
			}
		}
	}

	public ChatMonitor(Set<String> users, int messages) throws SEPAProtocolException, SEPAPropertiesException,
			SEPASecurityException, SEPABindingsException, InterruptedException {

		for (String user : users)
			messageMap.put(user, new UserMonitor(user, messages * (users.size() - 1)));

//		new Thread() {
//			public void run() {
//				while (!allDone) {
//					logger.info("*******************************************************");
//					logger.info("user|messages|received|sent|removed|brokenRec|brokenRem");
//					logger.info("*******************************************************");
//
//					try {
//						monitor();
//					} catch (InterruptedException e) {
//						return;
//					}
////					for (UserMonitor mon : messageMap.values()) {
////						logger.info(mon);
////					}
////					try {
////						Thread.sleep(5000);
////					} catch (InterruptedException e) {
////						return;
////					}
//				}
//			}
//		}.start();
	}

	private synchronized void monitor() throws InterruptedException {
//		do {
		logger.info("*******************************************************");
		logger.info("user|messages|received|sent|removed|brokenRec|brokenRem");
		logger.info("*******************************************************");
		
		allDone = true;
		for (UserMonitor mon : messageMap.values()) {
			logger.info(mon);
			allDone = allDone && mon.allDone();
		}
//		} while(!allDone);
//		logger.info("****************************");
//		for (UserMonitor mon : messageMap.values()) {
//			logger.info(mon);
//		}
	}

	public synchronized void brokenConnectionReceiver(String user) {
		messageMap.get(user).brokenConnectionReceiver = true;
//		notify();
	}

	public synchronized void messageSent(String user) {
		messageMap.get(user).sent++;
//		notify();
	}

	public synchronized void messageReceived(String user) {
		messageMap.get(user).received++;
//		notify();
	}

	public synchronized void messageRemoved(String user) {
		messageMap.get(user).removed++;
//		notify();
	}

	public void brokenConnectionRemover(String user) {
		messageMap.get(user).brokenConnectionRemover = true;
//		notify();

	}
}
