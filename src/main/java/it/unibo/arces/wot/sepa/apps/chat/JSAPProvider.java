package it.unibo.arces.wot.sepa.apps.chat;

import it.unibo.arces.wot.sepa.commons.exceptions.SEPAPropertiesException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;
import it.unibo.arces.wot.sepa.pattern.JSAP;

import java.io.File;

//import java.io.File;

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
		
		

//		try {
//			appProfile.read(getClass().getClassLoader().getResourceAsStream(jsapFileName));
//		} catch (SEPAPropertiesException | SEPASecurityException e2) {
//			logger.error(e2.getMessage());
//			return;
//		}
		
		String jsapPath = getClass().getClassLoader().getResource(jsapFileName).getPath();
		File f = new File(jsapPath);
		if (!f.exists()) {
			logger.error("File not found: " + jsapPath);
			throw new SEPAPropertiesException("File not found: " + jsapPath);
		}
		
		appProfile = new JSAP(jsapPath);
		
//		try {
//			appProfile.read(jsapFileName);
//		} catch (SEPAPropertiesException | SEPASecurityException e2) {
//			logger.error(e2.getMessage());
//			return;
//		}
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
