package me.hawkfalcon.mctag;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Tag implements Listener {

	private final MCTag plugin;
	private final TheMethods method;
	
    private final boolean freeze;
    private final boolean airInHand;
    private final boolean tagback;
    private final boolean arena_mode;
    private final boolean tag_damage;

    public Tag(MCTag m, TheMethods me) {
		this.plugin = m;
		this.method = me;
        this.freeze = MCTag.vars.Modes_Freeze;
        this.airInHand = true; //TODO: Add node in GlobalVariables for this - added, update
        this.tagback = MCTag.vars.Player_Allow__Tagback;
        this.arena_mode = MCTag.vars.Modes_Arena;
        this.tag_damage = false; //TODO: Add node in GlobalVariables for this added, update
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onTag(EntityDamageByEntityEvent event) {

        if ((event.getEntity() instanceof Player && event.getDamager() instanceof Player)) {

            String damager = ((Player) event.getDamager()).getName(), player = ((Player) event.getEntity()).getName();
            if (damager.equals(plugin.playerIt)) {

                //check if player holds air or air mode is off
                if ((Bukkit.getPlayerExact(damager).getItemInHand().getType() == Material.AIR) || (!airInHand)) {
                    //normal tag
                    if (!freeze) {
                        //tagbacks on
                        if (tagback) {
                            if (arena_mode) {
                                if (plugin.playersInGame.contains(player)) {
                                    method.tagPlayer(player);
                                }
                            } else {
                                method.tagPlayer(player);
                            }
                        } //tagback off
                        else {
                            //previouslyit
                            if (player.equals(plugin.previouslyIt)) {
                            	MCTag.util.message(Bukkit.getPlayerExact(damager), MCTag.vars.Message_On__No__Tagback);
                            } //normal
                            else {
                                method.tagPlayer(player);
                                plugin.previouslyIt = damager;

                            }

                        }
                        if (!tag_damage) {
                            event.setCancelled(true);
                        }
                    } //freezetag
                    else if (freeze) {
                        int theAmount = Arrays.asList(plugin.getServer().getOnlinePlayers()).size();
                        int playersingame = plugin.playersInGame.size();
                        int playersFrozen = plugin.frozenPlayers.size();
                        //player is not already frozen
                        if (!plugin.frozenPlayers.contains(player)) {
                            //everyone's frozen
                            if (!arena_mode) {
                                method.freezePlayer(player);

                                //everyones frozen
                                if (playersFrozen == theAmount - 2) {
                                	plugin.getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.GOLD + plugin.playerIt + " has won the game of freeze tag!");
                                	plugin.getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + "Randomly selecting next player to be it!");
                                    for (String p : plugin.frozenPlayers) {
                                        method.removeIce(p);
                                    }
                                    plugin.frozenPlayers.clear();
                                    method.rewardPlayer(damager);
                                    method.selectPlayer();
                                }
                            } //arena mode
                            else {
                                if (plugin.playersInGame.contains(player)) {
                                    method.freezePlayer(player);
                                    //everyones frozen
                                    if (playersFrozen == playersingame - 2) {
                                        for (String p : plugin.playersInGame) {
                                            Bukkit.getPlayer(p).sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.GOLD + plugin.playerIt + " has won the game of freeze tag!");
                                            Bukkit.getPlayer(p).sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + "Randomly selecting next player to be it!");
                                        }
                                        for (String p : plugin.frozenPlayers) {
                                            method.removeIce(p);
                                        }
                                        plugin.frozenPlayers.clear();
                                        method.rewardPlayer(damager);
                                        method.selectPlayerFromArena();

                                    }
                                }

                            }
                            if (!tag_damage) {
                                event.setCancelled(true);
                            }
                        }
                    } //if anything goes wrong
                    else {
                        if (airInHand) {
                            Bukkit.getPlayer(damager).sendMessage(ChatColor.RED + "You must have air in your hand to tag somebody");
                        }

                    }
                }
            } //unfreeze
            else if (!damager.equals(plugin.playerIt)) {
                //is person hit frozen?
                //frozen?
                if (plugin.frozenPlayers.contains(player)) {
                    //not arena mode
                    if (!arena_mode) {
                    	plugin.getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.DARK_GREEN + player + " is unfrozen!");
                        method.removeFrozenPlayer(player);

                    } //arena mode
                    else {
                        if (plugin.playersInGame.contains(damager)) {
                            for (String p : plugin.playersInGame) {
                                Bukkit.getPlayer(p).sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.DARK_GREEN + player + " is unfrozen!");
                            }
                            method.removeFrozenPlayer(player);
                        }
                    }
                    if (!tag_damage) {
                        event.setCancelled(true);
                    }
                }
            } //if anything goes wrong
            else {
                if (airInHand) {
                    Bukkit.getPlayer(damager).sendMessage(ChatColor.RED + "You must have air in your hand to tag somebody");
                }
                if (!tag_damage) {
                    event.setCancelled(true);
                }

            }
        }
    }
}
