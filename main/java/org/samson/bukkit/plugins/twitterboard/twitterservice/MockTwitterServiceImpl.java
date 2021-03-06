package org.samson.bukkit.plugins.twitterboard.twitterservice;

import java.util.Date;
import java.util.logging.Logger;

/*
 * Useful for testing...
 */
public class MockTwitterServiceImpl implements TwitterService {

	private Logger logger;
	
	@Override
	public String getLatestTweet(String username) {
		String res = null;
		
		long now = new Date().getTime();
		if (Math.random() > 0.5) {
			res = "This is a test tweet. The time now is " + now + " and the sun is shining above the world! #mocking @isfun";
		} else {
			res = "This is a 2nd test tweet. time is " + now; // A shorter tweet
		}
		
		return res;
	}

	@Override
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	@Override
	public boolean testKeys() {
		return true;
	}

	@Override
	public boolean tweet(String string) {
		logger.info("string to tweet is " + string);
		return true;
	}

	@Override
	public String getLastError() {
		return "unknown error";
	}
	
}