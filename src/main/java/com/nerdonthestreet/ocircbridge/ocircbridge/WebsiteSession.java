package com.nerdonthestreet.ocircbridge.ocircbridge;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class WebsiteSession {
	
	// Set up our public strings.
	public static String csrfToken;
	public static String sessionId;
	
	// Log in to Composr so the chat comes from our user instead of Guest.
	public static void login() throws IOException {
		
		// The login page for our Composr website (which we will POST to)
		String postUrlString = "https://" + ConfigLoader.composrWebsite + "/login";
		URL postUrl = new URL(postUrlString);
		HttpsURLConnection httpsConnection = (HttpsURLConnection) postUrl.openConnection();
		
		// Set up our POST details
		httpsConnection.setRequestMethod("POST");
		httpsConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
		String postParameters = "csrf_token=" + WebsiteSession.csrfToken + "&login_username=" + ConfigLoader.composrWebUsername + "&password=" + ConfigLoader.composrWebPassword + "\n";
		
		// Make the POST.
		httpsConnection.setDoOutput(true);
		DataOutputStream outputStream = new DataOutputStream(httpsConnection.getOutputStream());
		outputStream.writeBytes(postParameters);
		outputStream.flush();
		outputStream.close();
		
		// Check if it worked.
		int responseCode = httpsConnection.getResponseCode();
		System.out.println("Sent POST, response code: " + responseCode);
		
		// Read the returned webpage from Composr.
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpsConnection.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = bufferedReader.readLine()) != null) {
			response.append(inputLine);
		}
		bufferedReader.close();
		
		// Store the returned webpage HTML in a string.
		String sessionIdInput = response.toString();
		
		// Extract our Session ID from the returned webpage.
    	int sessionIdLocation = sessionIdInput.indexOf("keep_session");
    	int sessionIdStart = sessionIdLocation + 13;
    	int sessionIdEnd = sessionIdStart + 13;
    	WebsiteSession.sessionId = sessionIdInput.substring(sessionIdStart, sessionIdEnd);
    	System.out.println("Composr session ID: " + sessionId);
	}
	
	// Initialize our connection with Composr so it will talk to us.
	public static void initialize() throws IOException {
		// Our Composr website's homepage.
		String url = "https://" + ConfigLoader.composrWebsite;
		
		// Set up our HTTPS connection.
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

		// Set up our GET request.
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", "Mozilla/5.0");
		
		// Get response code & print to the console, so we know if we connected successfully.
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);
		
		// Get the HTML that Composr sent us.
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// Get CSRF token to authenticate with Composr (extract from the HTML that Composr sent us.)
		String csrfTokenInput = response.toString();		
    	int csrfTokenLocation = csrfTokenInput.indexOf("csrf_token");
    	int csrfTokenStart = csrfTokenLocation + 19;
    	int csrfTokenEnd = csrfTokenStart + 13;
    	WebsiteSession.csrfToken = csrfTokenInput.substring(csrfTokenStart, csrfTokenEnd);
	}
	
	// Check if we are currently logged in.
	public static boolean check() throws IOException {
		// Our Composr website's homepage.
		String url = "https://" + ConfigLoader.composrWebsite + "/start?keep_session=" + WebsiteSession.sessionId;
		URL checkUrl = new URL(url);
		HttpsURLConnection httpsConnection = (HttpsURLConnection) checkUrl.openConnection();
		
		// Set up a GET request.
		httpsConnection.setRequestMethod("GET");
		httpsConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
		
		// Get the HTML that Composr sent us.
		BufferedReader in = new BufferedReader(new InputStreamReader(httpsConnection.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
		// Check if the HTML includes our username.
		if (response.toString().contains("cms_username='" + ConfigLoader.composrWebUsername + "'")) {
			System.out.println(ConfigLoader.composrWebUsername + " is logged in.");
			return true;
		} else {
			System.out.println(ConfigLoader.composrWebUsername + " is not logged in.");
			return false;
		}
	}
}
