package org.samson.bukkit.plugins.twitterboard;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.samson.bukkit.plugins.twitterboard.twitterservice.DefaultTwitterServiceImpl;
import org.samson.bukkit.plugins.twitterboard.twitterservice.MockTwitterServiceImpl;
import org.samson.bukkit.plugins.twitterboard.twitterservice.TwitterService;
import org.samson.bukkit.plugins.twitterboard.twitterservice.TwitterServiceConfig;

public class TwitterBoardPlugin extends JavaPlugin {
	
	private final TwitterBoardEventListener eventListener = new TwitterBoardEventListener(this);
	private final TwitterBoardCommandExecuter commandExecutor = new TwitterBoardCommandExecuter(this);	

	private TwitterService twitterService;

	private long lastUpdated = 0; 
	
	@Override
	public void onDisable() {
	}
	
	@Override
	public void onEnable() {
		
		this.saveDefaultConfig();		

		TwitterServiceConfig twitterConfig = new TwitterServiceConfig(
				this.getConfig().getString("consumerkey"), 
				this.getConfig().getString("consumersecret"), 
				this.getConfig().getString("accesstoken"), 
				this.getConfig().getString("accesstokensecret"));

		if (twitterConfig.verifyConfiguration() == false) {
			this.getLogger().info("plugin configuration missing application keys. Please edit config.yml");
		} else {
			
			if (this.getConfig().getBoolean("mockmode", false)) {
				twitterService = new MockTwitterServiceImpl();
				this.getLogger().info("Twitterboard is using mock service for testing");
			} else {
				twitterService = new DefaultTwitterServiceImpl(twitterConfig);
			}
			
			twitterService.setLogger(this.getLogger());
			PluginManager pm = this.getServer().getPluginManager();
			pm.registerEvents(eventListener, this);
			
		}

		getCommand("twitterboard").setExecutor(commandExecutor);
		
	}
	
	public long getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(long lastUpdated) {
		this.lastUpdated = lastUpdated;
	}	
	
	public TwitterService getTwitterService() {
		return twitterService;
	}

	
}
