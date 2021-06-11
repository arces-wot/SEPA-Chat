package it.unibo.arces.wot.sepa.apps.chat;

import it.unibo.arces.wot.sepa.commons.exceptions.SEPABindingsException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAPropertiesException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAProtocolException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;
import it.unibo.arces.wot.sepa.commons.response.ErrorResponse;
import it.unibo.arces.wot.sepa.commons.sparql.Bindings;
import it.unibo.arces.wot.sepa.commons.sparql.BindingsResults;
import it.unibo.arces.wot.sepa.commons.sparql.RDFTermLiteral;
import it.unibo.arces.wot.sepa.commons.sparql.RDFTermURI;

class Receiver extends ChatAggregator {
	private final IMessageHandler handler;
	private String userUri;
	
	public Receiver(String userUri,IMessageHandler handler)
			throws SEPAProtocolException, SEPASecurityException, SEPAPropertiesException, SEPABindingsException {
		super("SENT", "SET_RECEIVED");

		this.setSubscribeBindingValue("receiver", new RDFTermURI(userUri));
		
		this.handler = handler;
		this.userUri = userUri;
	}
	
	@Override
	public void onBrokenConnection(ErrorResponse err) {
		logger.error(err);
		handler.onReceiverBrokenConnection(userUri);
	}

	@Override
	public void onAddedResults(BindingsResults results) {
		super.onAddedResults(results);
		
		logger.debug("onAddedResults");

		for (Bindings bindings : results.getBindings()) {
			logger.debug("SENT " + bindings.getValue("message"));
			
			handler.onMessageReceived(userUri, bindings.getValue("message"), bindings.getValue("name"), bindings.getValue("text"),bindings.getValue("time"));
			
			try {
				this.setUpdateBindingValue("receiver", new RDFTermURI(userUri));
				this.setUpdateBindingValue("sender", new RDFTermURI(bindings.getValue("sender")));
				this.setUpdateBindingValue("sentTime", new RDFTermLiteral(bindings.getValue("time")));
				update();
				
			} catch (SEPASecurityException | SEPAProtocolException | SEPAPropertiesException | SEPABindingsException e) {
				logger.error(e.getMessage());
			}
		}
	}
	
	@Override
	public void onRemovedResults(BindingsResults results) {
		super.onRemovedResults(results);
		
		logger.debug("onRemovedResults");

		for (Bindings bindings : results.getBindings()) {
			logger.debug("REMOVED " + bindings.getValue("message"));
			
			handler.onMessageRemoved(userUri, bindings.getValue("message"), bindings.getValue("name"), bindings.getValue("text"),bindings.getValue("time"));
		}
	}
}
