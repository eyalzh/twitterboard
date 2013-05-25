package org.samson.bukkit.plugins.twitterboard.twitterservice;

public class TwitterServiceConfig {

	private final String consumerKey;
	private final String consumerSecret;
	private final String consumerToken;
	private final String consumerTokenSecret;
	
	public TwitterServiceConfig(String consumerKey, String consumerSecret,
			String consumerToken, String consumerTokenSecret) {
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
		this.consumerToken = consumerToken;
		this.consumerTokenSecret = consumerTokenSecret;
	}
	
	public String getConsumerKey() {
		return consumerKey;
	}

	public String getConsumerSecret() {
		return consumerSecret;
	}

	public String getConsumerToken() {
		return consumerToken;
	}

	public String getConsumerTokenSecret() {
		return consumerTokenSecret;
	}

	public boolean verifyConfiguration() {
		return keyStringOK(this.consumerKey) &&
				keyStringOK(this.consumerSecret) &&
				keyStringOK(this.consumerToken) &&
				keyStringOK(this.consumerTokenSecret);
	}	
	
	private boolean keyStringOK(String key) {
		return key != null && key.isEmpty() == false;
	}

	@Override
	public String toString() {
		return "TwitterServiceConfig [consumerKey=" + consumerKey
				+ ", consumerSecret=" + consumerSecret + ", consumerToken="
				+ consumerToken + ", consumerTokenSecret="
				+ consumerTokenSecret + "]";
	}
	
}
