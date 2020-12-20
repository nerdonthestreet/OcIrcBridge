package com.nerdonthestreet.ocircbridge.ocircbridge;

import java.io.IOException;

import javax.net.ssl.SSLSocketFactory;

import org.pircbotx.Configuration;
import org.pircbotx.MultiBotManager;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.managers.ListenerManager;
import org.pircbotx.hooks.managers.ThreadedListenerManager;

/**
 * Hello world!
 *
 */
public class App extends ListenerAdapter {
	
	// Initialize our global IRC bot objects
	public static PircBotX ircBot;
	public static PircBotX ircBot2;
	
	// Main program
    public static void main( String[] args ) throws IOException, IrcException, InterruptedException {
    	
    	// Load config.
    	ConfigLoader.loadConfig(args[0]);
    	
    	// Initialize our Composr connection
    	WebsiteSession.initialize();
    	System.out.println("Composr connection initialized, CSRF token: " + WebsiteSession.csrfToken);
    	WebsiteSession.login();
    	
    	// Set up our PircBotX event listeners
    	System.out.println("Starting IRC bot...");
    	ListenerManager listenerManager = new ThreadedListenerManager();
        listenerManager.addListener(new IrcBot());
    	
        // Set up our PircBotX bot
        final Configuration ircConfig = new Configuration.Builder()
                .setName(ConfigLoader.ircBotName) // Sets the nick of our bot using the config value we loaded in.
                .addServer(ConfigLoader.ircServer, ConfigLoader.ircPort) // Sets the IRC server our bot will connect to from the config.
                .setSocketFactory(SSLSocketFactory.getDefault()) // Connect with SSL/TLS, hardcoded until requested otherwise.
                .addAutoJoinChannel(ConfigLoader.ircChannel) // Sets the IRC channel to join from the config.
                .setListenerManager(listenerManager)
                .setAutoReconnect(true)
                .setAutoReconnectDelay(10000)
                .setAutoReconnectAttempts(10800) // Attempt to reconnect for 30 minutes.
                .buildConfiguration();
        final PircBotX ircBot = new PircBotX(ircConfig);
        
        // Start the bot with MultiBotManager so it doesn't tie up our thread
        MultiBotManager pircbotManager = new MultiBotManager();
        pircbotManager.addNetwork(ircBot);
        pircbotManager.start();
        
        // Check that the IRC bot is connected, and if it is, copy its parameters to ircBot2
        // If it's not connected, give it a second and try again
        while (pircbotManager.getBotById(0).isConnected() == false) {
        	System.out.println("IRC bot is not connected yet, waiting 1 second...");
        	Thread.sleep(1000);
        }
        if (pircbotManager.getBotById(0).isConnected()) {
        	System.out.println("Managed IRC bot is connected in separate thread.");
        	ircBot2 = pircbotManager.getBotById(0); // This is just how PircBotX makes us do things
        }
        
        // Print our second bot's state so we can verify it's connected.
        System.out.println("ircBot2 state: " + App.ircBot2.getState().toString());
        
        // Start our Composr/SQL message listener.
        ComposrMessageListener.websiteMessageListener();
    }
}
