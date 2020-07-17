package it.unibo.arces.wot.sepa.apps.chat;

public interface IMessageHandler {
	public void onMessageReceived(String userUri,String messageUri,String user,String message,String time);
	public void onMessageRemoved(String userUri, String messageUri, String user, String message,String time);
	public void onMessageSent(String userUri, String messageUri,String time);
	public void onReceiverBrokenConnection(String userUri);
	public void onRemoverBrokenConnection(String userUri);
	
	
}
