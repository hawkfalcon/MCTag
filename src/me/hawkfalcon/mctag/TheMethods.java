package me.hawkfalcon.mctag;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class TheMethods {

    private final MCTag plugin;
    private final boolean arena_mode;


    public TheMethods(MCTag m) {
        this.plugin = m;
        this.arena_mode = MCTag.vars.Modes_Arena;
    }

    //selects player randomly
    public void selectPlayer() {
        List<Player> players = Arrays.asList(plugin.getServer().getOnlinePlayers());
        int theSize = players.size();
        Random random = new Random();
        String theNextString = players.get(random.nextInt(theSize)).getName();
        //try again
        if (theNextString == plugin.playerIt) {
            selectPlayer();
        }
        //tag player
        else {
            tagPlayer(theNextString);

        }
    }

    public void startGameWith(String player) {
        joinPlayer(player);
        tagPlayer(player);
    }

    public void selectPlayerFromArena() {
        List<Player> players = Arrays.asList(plugin.getServer().getOnlinePlayers());
        int theSize = plugin.playersInGame.size();
        Random random = new Random();
        String theNextString = players.get(random.nextInt(theSize)).getName();
        //try again
        if (theNextString == plugin.playerIt) {
            selectPlayerFromArena();
        }
        //tag player
        else {
            tagPlayerFromArena(theNextString);

        }
    }

    //rewards diamonds
    public void rewardPlayer(String playerstring) {
        Player player = Bukkit.getPlayer(playerstring);
        if (!arena_mode) {
            player.getInventory().addItem(new ItemStack(Material.DIAMOND, MCTag.vars.Reward_Diamond__Amount));
            MCTag.util.message(player, MCTag.vars.Message_On__Reward.replace("%a", "" + MCTag.vars.Reward_Diamond__Amount));
        } else {
            if (plugin.playersInGame.size() > 2) {
                player.getInventory().addItem(new ItemStack(Material.DIAMOND, MCTag.vars.Reward_Diamond__Amount));
            } else {
                MCTag.util.message(player, MCTag.vars.Message_On__Not__Enough__Players__To__Give__Award);
            }
        }
    }

    //tags player
    public void tagPlayer(String player) {
        if (plugin.playerIt != null) {
            restoreArmor(plugin.playerIt);
        }
        plugin.playerIt = player;
        setArmor(player);

        //arena mode
        if (arena_mode) {
            for (String p : plugin.playersInGame) {
                MCTag.util.message(Bukkit.getPlayerExact(p), MCTag.vars.Message_On__It.replace("%p", player));
            }
        }
        //not arena mode
        else {
            MCTag.util.broadcast(MCTag.vars.Message_On__It.replace("%p", player));
        }
        //smoke!
        for (int i = 0; i <= 8; i++)
            Bukkit.getPlayer(player).getWorld().playEffect(Bukkit.getPlayer(player).getLocation(), Effect.SMOKE, i);
    }

    public void tagPlayerFromArena(String player) {
        if (plugin.playersInGame.contains(player)) {
            if (plugin.playerIt != null) {
                restoreArmor(plugin.playerIt);
            }
            plugin.playerIt = player;
            setArmor(player);
            for (String p : plugin.playersInGame) {
                MCTag.util.message(Bukkit.getPlayerExact(p), MCTag.vars.Message_On__It.replace("%p", player));
            }
            //smoke!
            for (int i = 0; i <= 8; i++)
                Bukkit.getPlayerExact(player).getWorld().playEffect(Bukkit.getPlayer(player).getLocation(), Effect.SMOKE, i);
        }
    }

    //freezes player
    public void freezePlayer(String player) {

        //player is not already frozen
        if (!plugin.frozenPlayers.contains(player)) {
            plugin.frozenPlayers.add(player);
            //selfMount(player);
            setIce(player);
            for (int i = 0; i <= 8; i++) {
                Bukkit.getPlayer(player).getWorld().playEffect(Bukkit.getPlayer(player).getLocation(), Effect.SMOKE, i);
            }
            if (!arena_mode) {
                MCTag.util.broadcast(MCTag.vars.Message_On__Player__Frozen);
            } else {
                for (String p : plugin.playersInGame) {
                    MCTag.util.message(Bukkit.getPlayerExact(p), MCTag.vars.Message_On__Player__Frozen);
                }
            }

        }
    }

    public void removeFrozenPlayer(String player) {
        //Player p = plugin.getServer().getPlayerExact(player);
        plugin.frozenPlayers.remove(player);
        //p.eject();
        removeIce(player);
    }

    //public void teleportPlayer(String player) {
    //String[] loc = MCTag.vars.Spawn_Location.split("\\|");
    //Bukkit.getPlayerExact(player).teleport(new Location(Bukkit.getWorld(loc[0]), Double.parseDouble(loc[1]), Double.parseDouble(loc[2]), Double.parseDouble(loc[3])));
    //}
    public void teleportPlayer(String player) {
        String[] loc = MCTag.vars.Spawn_Location.split("\\|");
        System.out.println(Arrays.asList(loc));
        Player p = Bukkit.getPlayerExact(player);
        if (p == null) {
            System.out.println("FETCHED PLAYER IS NULL!!!!!!!!!!!!!!!!!!");
            return;
        }
        World to = Bukkit.getWorld(loc[0]);
        System.out.println(loc[0]);
        if (to == null) {
            System.out.println("FETCHED WORLD IS NULL!!!!!!!!!!!!!!!!!!");
            return;
        }
        p.teleport(new Location(Bukkit.getWorld(loc[0]), Double.parseDouble(loc[1]), Double.parseDouble(loc[2]), Double.parseDouble(loc[3])));
    }

    public void joinPlayer(String player) {
        //arena mode on

        if (arena_mode) {
            if (!plugin.playersInGame.contains(player)) {

                for (String p : plugin.playersInGame) {
                    MCTag.util.message(Bukkit.getPlayerExact(p), MCTag.vars.Message_On__Join.replace("%p", player));
                }
                teleportPlayer(player);
                plugin.playersInGame.add(player);

            }
            //You are already in the game
            else {
                MCTag.util.message(Bukkit.getPlayerExact(player), MCTag.vars.Message_On__Join__When__Already__In);
            }
        } else {
            Bukkit.getPlayer(player).sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "Arena mode is off!");
        }
    }

    public void gameOff() {
        plugin.getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + "The game of tag has ended!");
        restoreArmor(plugin.playerIt);
        for (String p : plugin.frozenPlayers) {
            removeIce(p);
        }
        cleanUp();
    }

    public void cleanUp() {
        plugin.gameOn = false;
        plugin.playerIt = null;
        plugin.previouslyIt = null;
        plugin.frozenPlayers.clear();
        plugin.playersInGame.clear();
        plugin.taggerArmor.clear();
        plugin.frozenHead = null;
        plugin.taggerarmor = false;
    }

    public void setArmor(String p) {
        Player pl = plugin.getServer().getPlayer(p);
        plugin.taggerarmor = true;
        plugin.taggerArmor.put(p, pl.getInventory().getArmorContents());
        PlayerInventory inv = pl.getInventory();
        inv.setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
        inv.setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
        inv.setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
        inv.setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));
        pl.sendMessage("DEBUG" + "ARMORON");
    }

    public void restoreArmor(String p) {
        Player pl = plugin.getServer().getPlayer(p);
        PlayerInventory pi = pl.getInventory();
        if (plugin.taggerArmor.containsKey(p)) {
            pi.setArmorContents(plugin.taggerArmor.get(p));
            plugin.taggerArmor.remove(p);
            pl.sendMessage("DEBUG" + "ARMOROFF");
        }
        plugin.taggerarmor = false;
    }

    public void setIce(String p) {
        Player pl = plugin.getServer().getPlayer(p);
        PlayerInventory inv = pl.getInventory();
        plugin.frozenHead.put(p, pl.getInventory().getHelmet());
        inv.setHelmet(new ItemStack(Material.ICE));
        pl.sendMessage("DEBUG" + "ICY");
    }

    public void removeIce(String p) {
        Player pl = plugin.getServer().getPlayer(p);
        PlayerInventory inv = pl.getInventory();
        if (plugin.frozenHead.containsKey(p)) {
            inv.setHelmet(plugin.frozenHead.get(p));
            plugin.frozenHead.remove(p);
            pl.sendMessage("DEBUG" + "ICYOFF");
        }
    }

}