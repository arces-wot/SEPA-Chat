package it.unibo.arces.wot.sepa.apps.chat;

import it.unibo.arces.wot.sepa.commons.exceptions.SEPAPropertiesException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;
import it.unibo.arces.wot.sepa.commons.security.ClientSecurityManager;
import it.unibo.arces.wot.sepa.pattern.JSAP;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JSAPProvider {
	private static final Logger logger = LogManager.getLogger();

	private final JSAP appProfile;
	
	private final long timeout = 5000;
	private final long nRetry = 0;

	public JSAPProvider() throws SEPAPropertiesException, SEPASecurityException {
		String jsapFileName = "chat.jsap";

		if (System.getProperty("testConfiguration") != null) {
			jsapFileName = System.getProperty("testConfiguration");
			logger.info("JSAP from property testConfiguration: " + jsapFileName);
		} else if (System.getProperty("secure") != null) {
			jsapFileName = "chat-secure.jsap";
			logger.info("JSAP secure default: " + jsapFileName);
		}

		String path = getClass().getClassLoader().getResource(jsapFileName).getPath();
		File f = new File(path);
		if (!f.exists()) {
			logger.error("File not found: " + path);
			throw new SEPAPropertiesException("File not found: "+path);
		}
		
		appProfile = new JSAP(path);
	}
	
	public ClientSecurityManager getSecurityManager() throws SEPASecurityException {
		if (!appProfile.isSecure()) return null;
		
		String path = getClass().getClassLoader().getResource("sepa.jks").getPath();
		File f = new File(path);
		if (!f.exists()) {
			logger.error("File not found: " + path);
			throw new SEPASecurityException("File not found: "+path);
		}
		return new ClientSecurityManager(appProfile.getAuthenticationProperties(),f.getPath(), "sepa2017");
	}
	
	public JSAP getJsap() {
		return appProfile;
	}
	
	public long getTimeout() {
		return timeout;
	}
	
	public long getNRetry() {
		return nRetry;
	}
}
