package me.hawkfalcon.mctag;

import java.util.Arrays;
//import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Events implements Listener{
	private MCTag plugin;
	private TheMethods method;
	
	private boolean arena_mode;
	private boolean tagback;
	private boolean freeze;
	private boolean commands;
	
	public Events(MCTag m, TheMethods me) {
		this.plugin = m;
		this.method = me;
		this.freeze = MCTag.vars.Modes_Freeze;
		this.arena_mode = MCTag.vars.Modes_Arena;
		this.tagback = MCTag.vars.Player_Allow__Tagback;
		this.commands = MCTag.vars.Player_Commands__In__Arena;
	}

	//if player quits
	@EventHandler
	public void onDisconnect(PlayerQuitEvent event) {
		String player = event.getPlayer().getName();
		//if its the player who is it
		if (player.equals(plugin.playerIt)) {
			method.restoreArmor(player);		
			//arena
			if (arena_mode) {
				if (plugin.playersInGame.size() < 1){
					for (String p : plugin.playersInGame) {
						Bukkit.getPlayer(p).sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + plugin.playerIt + " has left, randomly selecting next person to be it!");
					}
					method.selectPlayerFromArena();
				}
				else {
					method.gameOff();
				}
				if (plugin.playersInGame.contains(player)){
					plugin.playersInGame.remove(player);
					for (String p : plugin.playersInGame) {
						Bukkit.getPlayer(p).sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + plugin.playerIt + " has left the game!");
					}
				}
			}
			//not arena
			else {
				if (Arrays.asList(plugin.getServer().getOnlinePlayers()).size() < 1){

					plugin.getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + plugin.playerIt + " has left, randomly selecting next person to be it!");
					method.selectPlayer();
				}
				else {
					method.gameOff();
				}
			}
		}
		if (plugin.playersInGame.contains(event.getPlayer().getName())){
			plugin.playersInGame.remove(event.getPlayer().getName());
		}
		if(plugin.frozenPlayers.contains(player)){
			method.removeIce(player);
		}
	}
	//freeze player
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true) 
	public void freezePlayers(PlayerMoveEvent event){		
		//is it freeze tag?
		if (freeze){
			//get frozen players
			if (plugin.frozenPlayers.contains(event.getPlayer().getName())){
				Block fromBlock = event.getFrom().getBlock();
				Block toBlock = event.getTo().getBlock();
				if (!(fromBlock.getX() == toBlock.getX() && fromBlock.getZ() == toBlock.getZ())) {
					event.setCancelled(true);
				}
			}
		}
	}
	//no commands
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true) 
	public void nocommands(PlayerCommandPreprocessEvent event){	
		//check config?
		if (!commands || event.getPlayer().hasPermission("MCTag.nocommands.bypass")){
			if (arena_mode) {
				if (!event.getMessage().toLowerCase().startsWith("/tag")) {
					if (plugin.playersInGame.contains(event.getPlayer().getName())){
						//no commands
						event.setCancelled(true);
						event.getPlayer().sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "You can't use commands in the arena!");
					}
				}
			}
		}
	}
	//stop placing while frozen
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		String player = event.getPlayer().getName();
		if (plugin.frozenPlayers.contains(player)) {
			event.setCancelled(true);
		}
	}
	//stop breaking while frozen
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		String player = event.getPlayer().getName();
		if (plugin.frozenPlayers.contains(player)) {
			event.setCancelled(true);
		}
	}
	@EventHandler
	public void deadPlayer(PlayerDeathEvent event){
		//List<ItemStack> itemid = event.getDrops();
		if(event.getEntity().getPlayer().getName().equals(plugin.playerIt)) {
		plugin.getServer().broadcastMessage("DEBUGDie " + plugin.playerIt + "|" + event.getEntity().getPlayer().getName() + " DROPED== " + event.getDrops());
		
            event.getDrops().remove(Material.CHAINMAIL_HELMET);
            event.getDrops().remove(Material.CHAINMAIL_CHESTPLATE);
            event.getDrops().remove(Material.CHAINMAIL_LEGGINGS);
            event.getDrops().remove(Material.CHAINMAIL_BOOTS);
		}
		if(plugin.frozenPlayers.contains(event.getEntity().getPlayer().getName())) {
            event.getDrops().remove(Material.ICE);
		}
		
	}
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		String player = event.getPlayer().getName();
		plugin.getServer().broadcastMessage("DEBUGResp " + player);
		if (plugin.playersInGame.contains(player)){
			method.teleportPlayer(player);
		}
		if (plugin.playerIt.equals(player)){ 
			Player players = plugin.getServer().getPlayer(player);
			PlayerInventory inv = players.getInventory();
			inv.setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
			inv.setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
			inv.setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
			inv.setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));
		}
		if(plugin.frozenPlayers.contains(player)){
			Player players = Bukkit.getServer().getPlayer(player);
			PlayerInventory inv = players.getInventory();
			inv.setHelmet(new ItemStack(Material.ICE));
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if(((Player)event.getWhoClicked()).getName().equals(plugin.playerIt)){
			if(event.getSlotType() == SlotType.ARMOR) {
				event.setCancelled(true);
				((Player)event.getWhoClicked()).sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "You can't change armor while you are it!");
			}
		}
		if(plugin.frozenPlayers.contains(((Player)event.getWhoClicked()).getName())){
			if(event.getSlotType() == SlotType.ARMOR) {
				event.setCancelled(true);
				((Player)event.getWhoClicked()).sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "You can't change armor while you frozen!");
			}
		}

	}
}
