package org.samson.bukkit.plugins.twitterboard;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.samson.bukkit.plugins.twitterboard.twitterservice.TwitterService;
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

    		if (args.length > 1 && args[0].equalsIgnoreCase("tweet")) {
	        	return executeTweetCommand(sender, args);
	        }
    		
    	}
    	
    	return false;
    }

	private boolean executeTweetCommand(CommandSender sender, String[] args) {

		TwitterService service = plugin.getTwitterService();
		
		String[] tweetText = Arrays.copyOfRange(args, 1, args.length);
		boolean success = service.tweet(StringUtils.join(tweetText,' '));
		
		if (success) {
			sender.sendMessage(ChatColor.GREEN + "Tweet sent.");
		} else {
			sender.sendMessage(ChatColor.RED + "Could not send tweet. Reason: " + service.getLastError());
		}
		
		return true;
	}

	private boolean executeVersionCommand(CommandSender sender, String[] args) {
		sender.sendMessage("TwitterBoard version " + plugin.getDescription().getVersion());
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
