package org.samson.bukkit.plugins.twitterboard;

import java.util.logging.Logger;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.samson.bukkit.plugins.twitterboard.twitterservice.TwitterService;

public class TwitterBoard {

	public static final String TWITTER_DEFAULT_DIRECTIVE = "[twitter]";
	public static final int LINES_IN_A_SIGN = 4;
	public static final int CHARACTERS_IN_LINE = 15;
	public static final int MAX_SIGNS = 4;
	
	private Block block;
	private String twitterQuery;
	private String colorCodes;
	private boolean isOppositeConfiguration;	

	private Logger logger;
	private TwitterService twitterService;
	
	public TwitterBoard(Block block, String twitterQuery, String colorCodes, boolean isOppositeConfiguration) {
		this.block = block;
		this.twitterQuery = twitterQuery;
		this.colorCodes = colorCodes;
		this.isOppositeConfiguration = isOppositeConfiguration;
	}

	public void update() {

		String latestTweet = getLatestTweetFrom(twitterQuery);
		
		if (latestTweet == null) {
			return;
		}
		
		clearBoard();
		
		String [] lines = splitToLines(latestTweet, CHARACTERS_IN_LINE - colorCodes.length());

		int noOfLines = lines.length;
		
		if (noOfLines > 0) {
			
			int signIndex = 0;
			int numberOfSigns = (int) Math.ceil((double)noOfLines / LINES_IN_A_SIGN);

			Location signLoc = block.getLocation();
			Block blockBelow;
			if (isOppositeConfiguration == false) {
				blockBelow = block.getWorld().getBlockAt(signLoc.subtract(0, 1, 0));	
			} else {
				blockBelow = block;
			}

			while ((blockBelow.getType() == Material.AIR || blockBelow.getType() == Material.WALL_SIGN) && signIndex < numberOfSigns) {
				
				if (blockBelow.getType() != Material.WALL_SIGN) {
					blockBelow.setType(Material.WALL_SIGN);	
				}

				blockBelow.setData(block.getData());
				Sign nextSign = (Sign) blockBelow.getState();					

				int startLine = signIndex * LINES_IN_A_SIGN;
				
				for (int j = 0; startLine + j < Math.min(lines.length, startLine + LINES_IN_A_SIGN); j++) {
					nextSign.setLine(j, colorCodes + lines[startLine + j]);
				}
				
				nextSign.update();
				
				signIndex++;
				blockBelow = block.getWorld().getBlockAt(signLoc.subtract(0, 1, 0));

			}				
			
		}		
		
	}
	
	public void setLogger(Logger logger) {
		this.logger = logger;
	}
	
	public void setTwitterService(TwitterService service) {
		twitterService = service;
	}

	@SuppressWarnings("unused")
	private void log(String msg) {
		if (logger != null) {
			logger.info(msg);
		}
	}
	
	private String[] splitToLines(String dummyText, int charactersInLine) {
		String wrappedText = WordUtils.wrap(dummyText, charactersInLine, "\n", true);
		String [] lines = wrappedText.split("\n");
		return lines;
	}	
	
	private String getLatestTweetFrom(String user) {
		
		String res = null;

		if (twitterService != null) {
			res = twitterService.getLatestTweet(user);	
		}
		
	    return res;
		
	}
	
	private void clearBoard() {
		
		int i = 0;
		
		Location signLoc = block.getLocation();
		Block blockBelow;
		if (isOppositeConfiguration == false) {
			blockBelow = block.getWorld().getBlockAt(signLoc.subtract(0, 1, 0));	
		} else {
			blockBelow = block;
		}		
		
		while (i < MAX_SIGNS && blockBelow.getType() == Material.WALL_SIGN) {
			
			Sign nextSign = (Sign) blockBelow.getState();					

			for (int j = 0; j < LINES_IN_A_SIGN; j++) {
				nextSign.setLine(j, "");
			}
			
			nextSign.update();			
			
			blockBelow = block.getWorld().getBlockAt(signLoc.subtract(0, 1, 0));
			i++;
		}
		
	}
	
}
