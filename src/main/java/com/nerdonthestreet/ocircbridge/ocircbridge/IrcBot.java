package com.nerdonthestreet.ocircbridge.ocircbridge;

import java.io.DataOutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.MessageEvent;

public class IrcBot extends ListenerAdapter {
	public static PircBotX ircBot2;
	
	// This code is called when we connect to IRC.
	public void onConnect(ConnectEvent event) throws Exception {
		if (!ConfigLoader.ircBotPassword.isEmpty()) {
			App.ircBot2.send().message("NickServ", "identify " + ConfigLoader.ircBotPassword);
			System.out.println("Connected to IRC and sent authentication request to NickServ.");
		} else {
			System.out.println("Connected to IRC (no NickServ password configured.)");
		}
	}
	
	// This code is called when we're disconnected from IRC.
	public void onDisconnect(DisconnectEvent event) throws Exception {
		System.out.println("Disconnected from IRC. Attempting to reconnect every 10 seconds for 30 minutes...");
	}
	
	// This code is called when a user joins IRC.
	public void onJoin(JoinEvent event) throws Exception {
		// Keep our ircBot2 object fresh (should run once when we join, at the least.)
		ircBot2 = event.getBot();
	}
	
	// This code is called when a new message is received in IRC (other than messages this bot sent; PircBotX is smart enough not to include those.)
	public void onMessage(MessageEvent event) throws Exception {
		
		// Store the message text in a string.
		String messageToSend = event.getMessage();
		System.out.println("Sending message from IRC to Composr: " + messageToSend);
		
		// Check that we are logged in, re-login if not.
		if (WebsiteSession.check() != true) {
			System.out.println("Relogging...");
			WebsiteSession.initialize();
			WebsiteSession.login();
		}
		
		// Set up the URL we will POST to, with our current Session ID.
		String postUrlString = "https://" + ConfigLoader.composrWebsite + "/site/messages.php?action=post&keep_session=" + WebsiteSession.sessionId; //  + "&utheme=Nerd_on_the_Street"
		URL postUrl = new URL(postUrlString);
		HttpsURLConnection httpsConnection = (HttpsURLConnection) postUrl.openConnection();
		
		// Set up our POST request, with our current CSRF Token.
		httpsConnection.setRequestMethod("POST");
		httpsConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
		String postParameters = "room_id=2&message=" + messageToSend + "&csrf_token=" + WebsiteSession.csrfToken; // &font=sans-serif&colour=black&message_id=2787&event_id=1097
		
		// Make the POST request.
		httpsConnection.setDoOutput(true);
		DataOutputStream outputStream = new DataOutputStream(httpsConnection.getOutputStream());
		outputStream.writeBytes(postParameters);
		outputStream.flush();
		outputStream.close();
		
		// Get the response code so we know if it worked or not.
		int responseCode = httpsConnection.getResponseCode();
		System.out.println("Sent POST, response code: " + responseCode);
		
		// The below code is commented out because we don't need to read the full response from Composr.
		/**BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpsConnection.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = bufferedReader.readLine()) != null) {
			response.append(inputLine);
		}
		bufferedReader.close();
		System.out.println(response.toString());**/
		
		// Keep our IrcBot2 object fresh (probably not necessary.)
		ircBot2 = event.getBot();
	}
	
}
