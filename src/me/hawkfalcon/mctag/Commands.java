package me.hawkfalcon.mctag;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands {
    private static MCTag plugin;
    private static TheMethods method;

    private static boolean arena_mode;
    private static boolean tagback;

    public static void setup(MCTag m, TheMethods me) {
        plugin = m;
        method = me;
        arena_mode = MCTag.vars.Modes_Arena;
        tagback = MCTag.vars.Player_Allow__Tagback;
    }

    @ReflectCommand.Command(
            name = "start",
            alias = "on",
            root = "mctag",
            rootAlias = "tag",
            permission = "MCTag.start"
    )
    public static boolean start(Player sender) {
        if (StringUtils.isEmpty(MCTag.vars.Spawn_Location)) {
            sender.sendMessage("Please set spawn point first!");
            return true;
        }

        int online = plugin.getServer().getOnlinePlayers().length;

        //player is already it
        if (sender.getName().equals(plugin.playerIt)) {
            sender.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + MCTag.vars.Message_On__Already__It);
        }
        //start game
        else {
            boolean freeze = MCTag.vars.Modes_Freeze;
            //normal tag
            if (!freeze) {
                //no games are on
                if ((!plugin.gameOn) || (plugin.startBool)) {
                    //not arena mode
                    if (!arena_mode) {
                        //more than 1 player on
                        if (online > 1) {
                            plugin.gameOn = true;
                            plugin.startBool = false;
                            MCTag.util.broadcast(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + MCTag.vars.Message_On__Game__Start);
                            method.selectPlayer();
                        }
                        //1 player
                        else {
                            sender.sendMessage(ChatColor.RED + "There must be at least 2 people online to play tag");

                        }
                    }
                    //arena mode
                    else {
                        plugin.gameOn = true;
                        plugin.startBool = false;
                        MCTag.util.broadcast(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + MCTag.vars.Message_On__Game__Start__In__Arena);
                        method.startGameWith(sender.getName());
                    }
                }
                //game on already
                else {
                    sender.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "There is already a game of tag started!");
                }
            }
            //freeze tag
            else if (freeze) {
                //no games are on
                if ((!plugin.gameOn) || (plugin.startBool)) {
                    if (!arena_mode) {
                        //more then 2 people on
                        if (online > 2) {
                            plugin.gameOn = true;
                            plugin.startBool = false;
                            MCTag.util.broadcast(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + MCTag.vars.Message_On__Game__Freezetag__Start);
                            method.selectPlayer();
                        }
                        else {
                            sender.sendMessage(ChatColor.RED + "There must be at least 3 people online to play freeze tag!");
                        }
                    }
                    else {
                        plugin.gameOn = true;
                        plugin.startBool = false;
                        MCTag.util.broadcast(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + MCTag.vars.Message_On__Game__Freezetag__Start__In__Arena);
                        method.startGameWith(sender.getName());
                    }
                }
                else {
                    sender.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "There is already a game of tag started!");
                }
            }
            else {
                ErrorLogger.generateErrorLog(new RuntimeException("Error #102"));
            }
        }
        return true;
    }

    @ReflectCommand.Command(
            name = "stop",
            alias = "off",
            root = "mctag",
            rootAlias = "tag",
            permission = "MCTag.stop"
    )
    public boolean stop(Player sender) {
        if (plugin.gameOn)
            method.gameOff();
        else
            sender.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "There is no game to stop!");
        return true;
    }

    @ReflectCommand.Command(
            name = "it",
            alias = "whosit",
            root = "mctag",
            rootAlias = "tag",
            permission = "MCTag.it"
    )
    public boolean whosit(Player sender) {
        //someone is it
        if (plugin.playerIt != null)
            sender.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.GOLD + plugin.playerIt + " is currently it!");
        else
            sender.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.GOLD + "Nobody is currently it!");
        return true;
    }

    @ReflectCommand.Command(
            name = "players",
            alias = "numplayers",
            root = "mctag",
            rootAlias = "tag",
            permission = "MCTag.players"
    )
    public boolean numPlayers(Player sender) {
        sender.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.GOLD + "Players in arena:");
        for (String p : plugin.playersInGame)
            sender.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.GOLD + "- " + p);
        return true;
    }

    @ReflectCommand.Command(
            name = "setspawn",
            root = "mctag",
            rootAlias = "tag",
            permission = "MCTag.setspawn"
    )
    public boolean setspawn(Player sender) {
        Location loc = sender.getLocation();
        String location = (loc.getWorld().getName() + "|" + loc.getX() + "|" + loc.getY() + "|" + loc.getZ());
        MCTag.vars.Spawn_Location = location;
        MCTag.vars.save();
        sender.sendMessage(ChatColor.GOLD + "Spawn point set!");
        return true;
    }

    @ReflectCommand.Command(
            name = "join",
            root = "mctag",
            rootAlias = "tag",
            permission = "MCTag.join"
    )
    public boolean join(Player sender) {
        if (plugin.gameOn) {
            if (!plugin.playersInGame.contains(sender.getName())) {
                sender.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + "You have joined the game!");
                method.joinPlayer(sender.getName());
            } else {
                sender.sendMessage(ChatColor.RED + "You are already in a game!");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "There is no game started!");

        }
        return true;
    }

    @ReflectCommand.Command(
            name = "leave",
            alias = "gtfo",
            root = "mctag",
            rootAlias = "tag",
            permission = "MCTag.leave"
    )
    public boolean leave(Player sender) {
        if (arena_mode) {
            if (plugin.playersInGame.contains(sender.getName())) {
                method.restoreArmor(sender.getName());
                sender.teleport(sender.getServer().getWorlds().get(0).getSpawnLocation());
                plugin.playersInGame.remove(sender.getName());
                if (sender.getName().equals(plugin.playerIt)) {
                    if (plugin.playersInGame.size() > 1) {
                        method.restoreArmor(plugin.playerIt);
                        for (String p : plugin.playersInGame) {
                            Bukkit.getPlayer(p).sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + plugin.playerIt + " has left, randomly selecting next person to be it!");
                        }
                        plugin.frozenPlayers.clear();
                        method.selectPlayerFromArena();
                    } else {
                        method.gameOff();
                    }
                }
                else {
                    for (String p : plugin.playersInGame) {
                        Bukkit.getPlayer(p).sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.BLUE + sender.getName() + " has left the game!");
                    }
                }
            }
            else {
                sender.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "You are not in the game!");
            }
        } else {
            sender.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "Arena mode is off!");
        }
        return true;
    }

    @ReflectCommand.Command(
            name = "reload",
            root = "mctag",
            rootAlias = "tag",
            permission = "MCTag.reload",
            sender = ReflectCommand.Sender.EVERYONE
    )
    public boolean reload(CommandSender sender) {
        MCTag.vars.load();
        sender.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.GOLD + "Configuration reloaded!");
        return true;
    }

    @ReflectCommand.Command(
            name = "tagback",
            root = "mctag",
            rootAlias = "tag",
            permission = "MCTag.tagbacktoggle",
            usage = "<command> [on|off]",
            sender = ReflectCommand.Sender.EVERYONE
    )
    public boolean tagback(CommandSender sender, String flag) {
        if (!plugin.gameOn) {
            if (flag.equalsIgnoreCase("on")) {
                if (!plugin.gameOn) {
                    if (!tagback) {
                        MCTag.vars.Player_Allow__Tagback = true;
                        MCTag.vars.save();
                        sender.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.DARK_AQUA + "Tagbacks are now allowed!");
                    } else {
                        sender.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "Tagbacks are already allowed!");
                    }
                }
            } else if (flag.equalsIgnoreCase("off")) {
                if (!plugin.gameOn) {
                    boolean tagback = MCTag.vars.Player_Allow__Tagback;
                    //tagbacks are on
                    if (tagback) {
                        MCTag.vars.Player_Allow__Tagback = false;
                        MCTag.vars.save();
                        sender.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.DARK_AQUA + "Tagbacks are now forbidden!");
                    }
                    //tagbacks are off
                    else {
                        sender.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "Tagbacks are already forbidden!");
                    }
                }
            }
        } else {
            sender.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "You can't switch modes while a game is running!");
        }

        return true;
    }

    @ReflectCommand.Command(
            name = "freezetag",
            root = "mctag",
            rootAlias = "tag",
            permission = "MCTag.tagbacktoggle",
            usage = "<command> [on|off]",
            sender = ReflectCommand.Sender.EVERYONE
    )
    public boolean freezetag(CommandSender sender, String flag) {
        if (!plugin.gameOn) {
            if (flag.equalsIgnoreCase("on")) {
                if (!plugin.gameOn) {
                    //tagbacks are off
                    if (!tagback) {
                        MCTag.vars.Modes_Freeze = true;
                        MCTag.vars.save();
                        sender.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.DARK_AQUA + "Freezetag is now enabled!");
                    }
                    //tagbacks are on
                    else {
                        sender.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "Freezetag is already enabled!");
                    }

                }
            } else if (flag.equalsIgnoreCase("off")) {
                if (!plugin.gameOn) {
                    boolean tagback = MCTag.vars.Modes_Freeze;
                    //tagbacks are on
                    if (tagback) {
                        MCTag.vars.Modes_Freeze = false;
                        MCTag.vars.save();
                        sender.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.DARK_AQUA + "Freezetag is now disabled!");
                    }
                    //tagbacks are off
                    else {
                        sender.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "Freezetag is already disabled!");
                    }
                }
            }
        } else {
            sender.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "You can't switch modes while a game is running!");
        }

        return true;
    }

    @ReflectCommand.Command(
            name = "arena",
            root = "mctag",
            rootAlias = "tag",
            permission = "MCTag.tagbacktoggle",
            usage = "<command> [on|off]",
            sender = ReflectCommand.Sender.EVERYONE
    )
    public boolean arena(CommandSender sender, String flag) {
        if (!plugin.gameOn) {
            if (flag.equalsIgnoreCase("on")) {
                if (!plugin.gameOn) {
                    if (!tagback) {
                        MCTag.vars.Modes_Arena = true;
                        MCTag.vars.save();
                        sender.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.DARK_AQUA + "Arena mode is now enabled!");
                    } else {
                        sender.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "Arena mode is already enabled!");
                    }
                }
            } else if (flag.equalsIgnoreCase("off")) {
                if (!plugin.gameOn) {
                    boolean tagback = MCTag.vars.Modes_Arena;
                    if (tagback) {
                        MCTag.vars.Modes_Arena = false;
                        MCTag.vars.save();
                        sender.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.DARK_AQUA + "Arena mode is now disabled!");
                    } else {
                        sender.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "Arena mode is already disabled!");
                    }
                }
            }
        } else {
            sender.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "MCTag" + ChatColor.WHITE + "] " + ChatColor.RED + "You can't switch modes while a game is running!");
        }
        return true;
    }
}
