package me.hawkfalcon.mctag;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class Commands implements CommandExecutor{
	private MCTag plugin;
	private TheMethods method;
	
	private boolean arena_mode;
	private boolean tagback;
	public Commands(MCTag m, TheMethods me) {
		this.plugin = m;
		this.method = me;
		this.arena_mode = MCTag.vars.Modes_Arena;
		this.tagback = MCTag.vars.Player_Allow__Tagback;
	}

	//commands
	@EventHandler
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		//tag
		if (cmd.getName().equalsIgnoreCase("tag")||cmd.getName().equalsIgnoreCase("mctag")) {
			if (sender instanceof Player){
				Player player = (Player) sender;				
				//nothing
				if (args.length == 0){
					player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.AQUA + plugin.commands);
					return true;
				}
				if (args.length == 1) {
					//start game
					if (args[0].equalsIgnoreCase("start")||args[0].equalsIgnoreCase("on")){
						if (sender.hasPermission("MCTag.start")) {
							//player is already it
							if (player.getName().equals(plugin.playerIt)) {
								player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "You are already it!");
							}
							//start game
							else {
								boolean freeze = plugin.getConfig().getBoolean("freeze_tag");
								//normal tag
								if (!freeze){
									//no games are on
									if ((!plugin.gameOn) || (plugin.startBool)){
										//not arena mode
										if (!arena_mode){
											int playersonline = Arrays.asList(plugin.getServer().getOnlinePlayers()).size();
											//more than 1 player on
											if (playersonline > 1){
												plugin.gameOn = true;
												plugin.startBool = false;
												plugin.getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + "A game of tag has begun!");
												method.selectPlayer();
											}
											//1 player
											else {
												player.sendMessage(ChatColor.RED + "There must be at least 2 people online to play tag");

											}
										}
										//arena mode
										else{
											plugin.gameOn = true;
											plugin.startBool = false;
											plugin.getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + "A game of tag has begun! Type /tag join to join the game");
											method.startGameWith(player.getName());
										}
									}
									//game on already
									else {
										player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "There is already a game of tag started!");
									}
								}
								//freeze tag
								else if (freeze) {
									//no games are on
									if ((!plugin.gameOn) || (plugin.startBool)){
										if (!arena_mode){
											int playersonline = Arrays.asList(plugin.getServer().getOnlinePlayers()).size();
											//more then 2 people on
											if (playersonline > 2){
												plugin.gameOn = true;
												plugin.startBool = false;
												plugin.getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + "A game of freeze tag has begun!");
												method.selectPlayer();
											}
											//2- players
											else {
												player.sendMessage(ChatColor.RED + "There must be at least 3 people online to play freeze tag");
											}
										}
										//arena mode
										else {
											plugin.gameOn = true;
											plugin.startBool = false;
											plugin.getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + "A game of freeze tag has begun! Type /tag join to join the game");
											method.startGameWith(player.getName());
										}
									}
									//game already on
									else {
										player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "There is already a game of tag started!");

									}
								}
								//this shouldn't happen, but just in case
								else {
									player.sendMessage("Error #102");

								}
							}
						} 
						//no permission
						else {
							player.sendMessage(ChatColor.RED + "You don't have permission!");
						}
						return true;
					}
					//stop game
					if (args[0].equalsIgnoreCase("stop")||args[0].equalsIgnoreCase("off")){
						if (sender.hasPermission("MCTag.stop")) {
							//game is on
							if (plugin.gameOn){
								method.gameOff();
							}
							//game is off
							else {
								player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "There is no game to stop!");

							}
						} 
						//no perms
						else {
							player.sendMessage(ChatColor.RED + "You don't have permission!");
						}
						return true;

					}
					//who is it
					if (args[0].equalsIgnoreCase("it")){
						if (sender.hasPermission("MCTag.it")) {
							//someone is it
							if (plugin.playerIt != null){
								player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.GOLD + plugin.playerIt + " is currently it!");
							}
							//noone is it
							else {
								player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.GOLD +  "Nobody is currently it!");

							}
						}
						//no perms
						else {
							player.sendMessage(ChatColor.RED + "You don't have permission!");
						}
						return true;
					}
					//list players
					if (args[0].equalsIgnoreCase("players")){
						if (sender.hasPermission("MCTag.players")) {
							player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.GOLD + "Players in arena:");
							for (String p : plugin.playersInGame) {
								player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.GOLD + "- " + p);
							}
						}
						//no perms
						else {
							player.sendMessage(ChatColor.RED + "You don't have permission!");
						}
						return true;
					}
					//setspawn
					if (args[0].equalsIgnoreCase("setspawn")){
						if (sender.hasPermission("MCTag.setspawn")) {
							Location loc = player.getLocation();
							String location = (loc.getWorld().getName() + "|" + loc.getX() + "|" + loc.getY() + "|" + loc.getZ());
							this.plugin.getConfig().set("spawn_location", location);
							this.plugin.saveConfig();				
							player.sendMessage(ChatColor.GOLD + "Spawn point set!");					
						}
						//no perms
						else {
							player.sendMessage(ChatColor.RED + "You don't have permission!");
						}
						return true;
					}
					//join game
					if (args[0].equalsIgnoreCase("join")){
						if (sender.hasPermission("MCTag.join")) {
							if (plugin.gameOn){
								if (!plugin.playersInGame.contains(player.getName())){
									player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + "You have joined the game!");
									method.joinPlayer(player.getName());
								}
								else {
									player.sendMessage(ChatColor.RED + "You are already in the game!");

								}
							}
							else {
								player.sendMessage(ChatColor.RED + "There is no game started!");

							}
						}
						//no perms
						else {
							player.sendMessage(ChatColor.RED + "You don't have permission!");
						}
						return true;
					}
					//leave game
					if (args[0].equalsIgnoreCase("leave")){
						if (sender.hasPermission("MCTag.leave")) {
							//arena mode on
							if (arena_mode){
								if (plugin.playersInGame.contains(player.getName())){
									plugin.playersInGame.remove(player.getName());
									if (player.getName() == plugin.playerIt){
										if (plugin.playersInGame.size() > 1) {
											for (String p : plugin.playersInGame) {
												Bukkit.getPlayer(p).sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + plugin.playerIt + " has left, randomly selecting next person to be it!");
											}
											plugin.frozenPlayers.clear();
											method.selectPlayerFromArena();
										}
										else{
											method.gameOff();
										}
									}
									for (String p : plugin.playersInGame) {
										Bukkit.getPlayer(p).sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + plugin.playerIt + " has left the game!");
									}
									player.teleport(player.getServer().getWorld("0").getSpawnLocation());
								}
								//You are already in the game
								else {
									player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED +  "You are not in the game!");
								}
							}
							else {
								player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED +  "Arena mode is off!");
							}
						}
						//no perms
						else {
							player.sendMessage(ChatColor.RED + "You don't have permission!");
						}
						return true;
					}
					//reload config
					if (args[0].equalsIgnoreCase("reload")){
						if (sender.hasPermission("MCTag.reload")) {
							this.plugin.reloadConfig();
							player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.GOLD + "Config reloaded");
						}
						//no perms
						else {
							player.sendMessage(ChatColor.RED + "You don't have permission!");
						}
						return true;
					}
					//Misspellings
					else {
						player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.AQUA + plugin.commands);
					}
					return true;


				}
				if (args.length == 2) {
					//tagback toggle
					if (args[0].equalsIgnoreCase("tagback")){
						//tagback on
						if (args[1].equalsIgnoreCase("allow")||args[1].equalsIgnoreCase("on")){
							if (sender.hasPermission("MCTag.tagbackallow")) {
								if (!plugin.gameOn) {									
									//tagbacks are off
									if (!tagback){
										this.plugin.getConfig().set("allow_tagbacks", true);
										this.plugin.saveConfig();								
										player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.DARK_AQUA + "Tagbacks are now allowed!");
									}
									//tagbacks are on
									else {
										player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "Tagbacks are already allowed!");
									}

								} 
								else {
									player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "You can't switch modes while a game is running!");
								}
							}
							//no perms
							else {
								player.sendMessage(ChatColor.RED + "You don't have permission!");
							}
							return true;

						}
						//tagbacks off
						if (args[1].equalsIgnoreCase("forbid")||args[1].equalsIgnoreCase("off")){
							if (sender.hasPermission("MCTag.tagbackforbid")) {
								if (!plugin.gameOn) {
									boolean tagback = plugin.getConfig().getBoolean("allow_tagbacks");
									//tagbacks are on
									if (tagback){
										this.plugin.getConfig().set("allow_tagbacks", false);
										this.plugin.saveConfig();								
										player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.DARK_AQUA + "Tagbacks are now forbidden!");
									}
									//tagbacks are off
									else {
										player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "Tagbacks are already forbidden!");

									}
								} 
								else {
									player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "You can't switch modes while a game is running!");	
								}
							}
							//no perms
							else {
								player.sendMessage(ChatColor.RED + "You don't have permission!");
							}
							return true;

						}
					}
					//freezetag toggle
					if (args[0].equalsIgnoreCase("freezetag")){
						//tagback on
						if (args[1].equalsIgnoreCase("on")){
							if (sender.hasPermission("MCTag.freezetagon")) {
								if (!plugin.gameOn) {
									boolean freeze = plugin.getConfig().getBoolean("freeze_tag");
									//freezetag is off
									if (!freeze){
										this.plugin.getConfig().set("freeze_tag", true);
										this.plugin.saveConfig();								
										player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.DARK_AQUA + "Freeze tag is now enabled!");
									}
									//freezetag is on
									else{
										player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "Freeze tag is already on!");

									}
								} 
								else {
									player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "You can't switch modes while a game is running!");
								}
							}
							//no perms
							else {
								player.sendMessage(ChatColor.RED + "You don't have permission!");
							}
							return true;

						}
						//freezetag off
						if (args[1].equalsIgnoreCase("off")){
							if (sender.hasPermission("MCTag.freezetagoff")) {
								boolean freeze = plugin.getConfig().getBoolean("freeze_tag");
								if (!plugin.gameOn) {
									//freezetag is on
									if (freeze){
										this.plugin.getConfig().set("freeze_tag", false);
										this.plugin.saveConfig();								
										player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.DARK_AQUA + "Freeze tag is now disabled!");
									}
									//freezetag is off
									else{
										player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "Freeze tag is already off!");

									}
								} 
								else {
									player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "You can't switch modes while a game is running!");
								}
							}
							//no perms
							else {
								player.sendMessage(ChatColor.RED + "You don't have permission!");
							}
							return true;
						}
						//misspelled
						else {
							player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.AQUA + plugin.commands);
						}
					}
					//misspelled
					else {
						player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.AQUA + plugin.commands);
					}
					return true;
				}
				//misspelled
				else {
					player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.AQUA + plugin.commands);
				}
			}
			sender.sendMessage("Not from console. It would crash.");

			return true;

		}
		return true;

	}
}
