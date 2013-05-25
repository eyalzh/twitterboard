package org.samson.bukkit.plugins.twitterboard.twitterservice;

import java.util.logging.Logger;

public interface TwitterService {

	public String getLatestTweet(String username);
	public void setLogger(Logger logger);
	public boolean testKeys();
	
}
