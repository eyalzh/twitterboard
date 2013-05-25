package org.samson.bukkit.plugins.twitterboard;

import java.util.Date;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.SignChangeEvent;

public class TwitterBoardEventListener implements Listener {

	private static final int COOLDOWN_TIME_MS = 20000;
	private TwitterBoardPlugin plugin;

	public TwitterBoardEventListener(TwitterBoardPlugin plugin) {
		this.plugin = plugin;
	}
	
	/*
	 * onSignChangeEvent
	 * 
	 * Catch the event of placing the twitter board and cancel if done by non-ops on the server
	 */
	@EventHandler
	public void onSignChangeEvent(SignChangeEvent event) {
		
		if (event.getLine(0).equalsIgnoreCase(getMainTwitterDirective())) {
			Player player = event.getPlayer();
			if (! player.isOp()) {
				plugin.getLogger().warning("non-op player " + player.getName() + " tried to place a twitter board!");
				event.setCancelled(true);
			}
		}
		
	}
	
	/*
	 * onBlockRedstoneEvent
	 * 
	 * Update/generate the board if redstone applied to the twitter board sign
	 */
	@EventHandler
	public void onBlockRedstoneEvent(BlockRedstoneEvent event) {
		
		Block block = event.getBlock();
		
		if(block.getType() == Material.WALL_SIGN && event.getNewCurrent() > 0) {
			
			Sign sign = (Sign) block.getState();
			
			if (sign.getLine(0).equalsIgnoreCase(getMainTwitterDirective())) {
				updateTwitterBoard(block, sign);
			}
		}
	}

	private void updateTwitterBoard(Block block, Sign sign) {
		boolean oppositeConfiguration = false;
		
		String username = sign.getLine(1);
		
		if(username != null && username.length() > 0) {		

			long now = new Date().getTime();
			
			if ( now > plugin.getLastUpdated() + COOLDOWN_TIME_MS) {
				
				// Figure out which color code to use, if any:
				String colorCode = sign.getLine(2);
				String colorCodeTranslated = "";
				String colorCodeSpecialChar = plugin.getConfig().getString("colorcodechar", "&");
				if (colorCode != null && colorCode.startsWith(colorCodeSpecialChar) == true) {
					colorCodeTranslated = ChatColor.translateAlternateColorCodes(colorCodeSpecialChar.charAt(0), colorCode);
				} else {
					String defColor = plugin.getConfig().getString("defaultcolor");
					if (defColor != null && defColor.isEmpty() == false && defColor.startsWith("&") == true) {
						colorCodeTranslated = ChatColor.translateAlternateColorCodes('&', defColor);
					}
				}
			
				// Check for opposite block setup:
				String oppositeConfig = sign.getLine(3);
				if(oppositeConfig != null && oppositeConfig.equalsIgnoreCase("opposite")) {
					
					org.bukkit.material.Sign signm = new org.bukkit.material.Sign(block.getType(),block.getData());
					BlockFace face = signm.getAttachedFace();
			
					Location loc = block.getLocation();
					
					// Move the location to the other side of the wall 
					loc.add(face.getModX() * 2, face.getModY() * 2, face.getModZ() * 2);
			
					Block oppositeBlock = block.getWorld().getBlockAt(loc);
					if (oppositeBlock.getType() == Material.WALL_SIGN) {
						block = oppositeBlock;
						oppositeConfiguration = true;
					} 
					
				}
				
				TwitterBoard board = new TwitterBoard(block, username, colorCodeTranslated, oppositeConfiguration);
				board.setLogger(plugin.getLogger());
				board.setTwitterService(plugin.getTwitterService());
				board.update();
				
				plugin.setLastUpdated(now);
				
			} 
			
		}
	}
	
	private String getMainTwitterDirective() {
		String mainDirective = plugin.getConfig().getString("maindirective"); 
		
		if (mainDirective == null || mainDirective.isEmpty()) {
			mainDirective = TwitterBoard.TWITTER_DEFAULT_DIRECTIVE;
		}
		
		return mainDirective;
	}
	
}
