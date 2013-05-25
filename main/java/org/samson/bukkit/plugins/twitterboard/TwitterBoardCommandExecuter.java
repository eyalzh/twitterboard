package org.samson.bukkit.plugins.twitterboard;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.samson.bukkit.plugins.twitterboard.twitterservice.TwitterServiceConfig;

public class TwitterBoardCommandExecuter implements CommandExecutor {

	private TwitterBoardPlugin plugin;

    public TwitterBoardCommandExecuter(TwitterBoardPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    	
    	if (args.length > 0 && command.getName().equalsIgnoreCase("twitterboard")) {
    		
    		if (args[0].equalsIgnoreCase("checkconfig")) {
	        	return executeCheckconfigCommand(sender, args);
	        }
    		
    		if (args[0].equalsIgnoreCase("version")) {
	        	return executeVersionCommand(sender, args);
	        }

    	}
    	
    	return false;
    }

	private boolean executeVersionCommand(CommandSender sender, String[] args) {
		sender.sendMessage("TwitterBoard version " + TwitterBoardPlugin.VERSION);
		return true;
	}

	private boolean executeCheckconfigCommand(CommandSender sender, String[] args) {

		boolean configOk = true;

		TwitterServiceConfig twitterConfig = new TwitterServiceConfig(
				plugin.getConfig().getString("consumerkey"), 
				plugin.getConfig().getString("consumersecret"), 
				plugin.getConfig().getString("accesstoken"), 
				plugin.getConfig().getString("accesstokensecret"));		
		
		
		if (twitterConfig.verifyConfiguration() == false) {
			configOk = false;
			sender.sendMessage("Config problem: Missing or invalid consumer keys and access tokens. Please see the documentation on how to add these.");
		} else if (plugin.getTwitterService().testKeys() == false) {
			configOk = false;
			sender.sendMessage("Config problem: Error in sending requests to Twitter. Recheck the consumer/access tokens, account status and rate limits.");
			sender.sendMessage("For more info, see the server log.");			
		}
		
		String twitterDirective = plugin.getConfig().getString("maindirective");
		
		if(twitterDirective == null || twitterDirective.isEmpty()) {
			configOk = false;
			sender.sendMessage("Config problem: The config variable 'maindirective' must be set in the config file!");
		}
		
		if(configOk) {
			sender.sendMessage("Config is OK.");
		}
		
		return true;
	}
    
    
    
	
}
