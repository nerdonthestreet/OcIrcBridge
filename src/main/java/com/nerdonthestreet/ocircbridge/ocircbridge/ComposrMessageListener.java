package com.nerdonthestreet.ocircbridge.ocircbridge;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ComposrMessageListener {
	public static void websiteMessageListener() {
		
		// Connect to MariaDB with the MySQL connector.
		try (Connection SqlConnection = DriverManager.getConnection("jdbc:mysql://" + ConfigLoader.composrSqlServer + ":3306/composr", ConfigLoader.composrSqlUsername, ConfigLoader.composrSqlPassword)) {
			
			// Log that we have successfully connected to our MariaDB database.
    		System.out.println("SQL connected.");
    		
    		/*
    		 * The below block will get & print all messages in the database:
    		 * 
    		PreparedStatement getAllChatMessages = SqlConnection.prepareStatement("SELECT * FROM " + ConfigLoader.composrChatTable + " WHERE room_id='" + ConfigLoader.composrRoomId + "' ORDER BY id ASC");
    		ResultSet allChatMessages = getAllChatMessages.executeQuery();
    		
    		while (allChatMessages.next()) {
    			System.out.print(allChatMessages.getInt(1));
    			System.out.print(": ");
    			System.out.print(allChatMessages.getString(7) + "\n");
    		}
    		*/
    		
			// Get only the most recent chat message, so we know what our startingId is.
			PreparedStatement getMostRecentChatMessage = SqlConnection.prepareStatement("SELECT * FROM " + ConfigLoader.composrChatTable + " WHERE room_id='" + ConfigLoader.composrRoomId + "' ORDER BY id DESC LIMIT 1");
			ResultSet mostRecentChatMessage = getMostRecentChatMessage.executeQuery();
    		
    		// Set our starting ID; any messages past this one will be sent to IRC after bot is started.
			mostRecentChatMessage.first(); // Move the pointer to the first (and only) SQL result.
    		int startingId = mostRecentChatMessage.getInt(1);
    		
    		// Log our starting ID & the last chat message before we started.
    		System.out.println("Our starting ID is " + startingId + ", and the message contents was: " + mostRecentChatMessage.getString(7));
    		
    		// Give our IRC bot some time to get connected & joined.
    		System.out.println("Sleeping for 7 seconds...");
    		Thread.sleep(7000);
    		System.out.println("Listening for new messages from Composr...");
    		
    		// Continuously listen for messages by checking the Composr database every 0.75 seconds
    		while(true) {  			
    			
    			// Only attempt to touch the Composr database if our IRC bot is good to go.
    			if (App.ircBot2.getState().toString() == "CONNECTED") {
    			
    				// Get all chat messages from the correct chat room with an ID higher than the ID we started with.
    				PreparedStatement getNewChatMessages = SqlConnection.prepareStatement("SELECT * FROM cms_chat_messages WHERE room_id='" + ConfigLoader.composrRoomId + "' AND id>" + startingId + " ORDER BY id ASC");
    				ResultSet newChatMessages = getNewChatMessages.executeQuery();
    					
    				// Forward each new message one-by-one. 
    				while (newChatMessages.next()) {
    					
    					// Get the numeric user ID of the sending Composr user
    					int sendingUserId = newChatMessages.getInt(5);
    					
    					// Only forward the message if it did not come from us
    					if (sendingUserId != 205) {
    						
    						// Get the message sender, look up their username from their ID
    						PreparedStatement getWebsiteMembers = SqlConnection.prepareStatement("SELECT * FROM cms_f_members WHERE id = " + sendingUserId); // Set up SQL call
    						ResultSet websiteMembers = getWebsiteMembers.executeQuery(); // Execute SQL, should receive one row with matching ID
    						websiteMembers.first(); // Move the pointer to the first (and only) result
    						String messageSender = websiteMembers.getString(2); // Get username & convert to string
    						
    						// Get message contents, print message info to console
    						String messageFromWebsite = newChatMessages.getString(7);
    						System.out.print("Got new message #");
    						System.out.print(newChatMessages.getInt(1));
    						System.out.print(" from <" + messageSender + ">: ");
    						System.out.print(messageFromWebsite + "\n");
    						
    						// Format message for IRC
    						String messageToIrc = "<" + messageSender + "> " + messageFromWebsite;
    						
    						// Send message to IRC
    						App.ircBot2.send().message(ConfigLoader.ircChannel, messageToIrc);
    						
    						// Once we're done forwarding all the new messages, update our last known ID to the ID of the last message.
    						if (newChatMessages.isLast()) {
    							startingId = newChatMessages.getInt(1);
    							System.out.println("All received messages have been forwarded.");
    						}
    					}
    				}
    				
    				// Wait a bit before checking for new messages (so we don't kill our CPU/IO).
    				Thread.sleep(750);
    			}
    		}
    	
    	// If we can't connect to SQL, we crash.
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		throw new IllegalStateException("Cannot connect to the database.", e);
    	} catch (InterruptedException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
		
	}
}
