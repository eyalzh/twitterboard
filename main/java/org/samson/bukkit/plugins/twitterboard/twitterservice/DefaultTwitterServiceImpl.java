package org.samson.bukkit.plugins.twitterboard.twitterservice;

import java.util.logging.Logger;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/*
 * The default twitter service implementation using the Twitter4j lib.
 */
public class DefaultTwitterServiceImpl implements TwitterService {

	private static final boolean DEBUG_ENABLED = false;
	
	private TwitterFactory twitterFactory;
	private Logger logger;
	
	private String lastError = "";
		
	public DefaultTwitterServiceImpl(TwitterServiceConfig twitterConfig) {
		
		ConfigurationBuilder cb = new ConfigurationBuilder();
		
		cb.setDebugEnabled(DEBUG_ENABLED)
		  .setOAuthConsumerKey(twitterConfig.getConsumerKey())
		  .setOAuthConsumerSecret(twitterConfig.getConsumerSecret())
		  .setOAuthAccessToken(twitterConfig.getConsumerToken())
		  .setOAuthAccessTokenSecret(twitterConfig.getConsumerTokenSecret());		
		
		Configuration c = cb.build();
		
		twitterFactory = new TwitterFactory(c);
	}
	
	@Override
	public String getLatestTweet(String username) {
		
		String res = null;
		
		Twitter twitterInstance = twitterFactory.getInstance();
		
		try {
			ResponseList<Status> userTimeline = twitterInstance.getUserTimeline(username);
			if (! userTimeline.isEmpty()) {
			    Status s = userTimeline.get(0);
			    res = s.getText();
			}
		} catch (TwitterException e) {
			error("Cannot get the timeline of user " + username + " Details: "+ e.getMessage());
		}
	    
	    return res;		
		
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}
	
	@SuppressWarnings("unused")
	private void log(String msg) {
		if (logger != null) {
			logger.info(msg);
		}
	}
	
	private void error(String msg) {
		if (logger != null) {
			logger.severe(msg);
		}
	}

	@Override
	public boolean testKeys() {
		
		Twitter twitterInstance = twitterFactory.getInstance();
		
		try {
			twitterInstance.verifyCredentials();
			return true;
		} catch (TwitterException e) {
			error("Cannot verify credentials: "+ e.getMessage());
		}
		
		return false;
	}

	@Override
	public boolean tweet(String string) {
		
		Twitter twitterInstance = twitterFactory.getInstance();
				
	    try {
			twitterInstance.updateStatus(string);
		} catch (TwitterException e) {
			lastError = e.getMessage();
			return false;
		}
		
		return true;
	}

	@Override
	public String getLastError() {
		return lastError;
	}	
	
}
