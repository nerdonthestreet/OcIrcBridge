package com.nerdonthestreet.ocircbridge.ocircbridge;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
	
	// Initialize attributes (global variables) we will use to store config values.
	static String ircBotName;
	static String ircBotPassword;
	static String ircServer;
	static int ircPort;
	static String ircChannel;
	static String composrWebsite;
	static String composrWebUsername;
	static String composrWebPassword;
	static int composrWebId;
	static String composrSqlServer;
	static String composrSqlUsername;
	static String composrSqlPassword;
	static String composrChatTable;
	static String composrRoomId;
	
	public static void loadConfig(String configFile) throws IOException {
		
		// Load in our config file as a Java properties file.
		Properties prop = new Properties();
		InputStream propFile = new FileInputStream(configFile);
		prop.load(propFile);
		
		// Set our attributes (global variables).
		ConfigLoader.ircBotName = prop.getProperty("ircBotName");
		ConfigLoader.ircBotPassword = prop.getProperty("ircBotPassword");
		ConfigLoader.ircServer = prop.getProperty("ircServer");
		ConfigLoader.ircPort = Integer.parseInt(prop.getProperty("ircPort"));
		ConfigLoader.ircChannel = prop.getProperty("ircChannel");
		ConfigLoader.composrWebsite = prop.getProperty("composrWebsite");
		ConfigLoader.composrWebUsername = prop.getProperty("composrWebUsername");
		ConfigLoader.composrWebPassword = prop.getProperty("composrWebPassword");
		ConfigLoader.composrWebId = Integer.parseInt(prop.getProperty("composrWebId"));
		ConfigLoader.composrSqlServer = prop.getProperty("composrSqlServer");
		ConfigLoader.composrSqlUsername = prop.getProperty("composrSqlUsername");
		ConfigLoader.composrSqlPassword = prop.getProperty("composrSqlPassword");
		ConfigLoader.composrChatTable = prop.getProperty("composrChatTable");
		ConfigLoader.composrRoomId = prop.getProperty("composrRoomId");
		
	}
}
