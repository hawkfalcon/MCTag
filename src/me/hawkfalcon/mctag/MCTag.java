package me.hawkfalcon.mctag;
//Made by: hawkfalcon. Feel free to use the code


//import java.io.File;

import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

//import org.bukkit.configuration.file.FileConfiguration;


public class MCTag extends JavaPlugin implements Listener {

    Logger log;
    ArrayList<String> frozenPlayers = new ArrayList<String>();
    ArrayList<String> playersInGame = new ArrayList<String>();

    public HashMap<String, ItemStack[]> taggerArmor = new HashMap<String, ItemStack[]>();
    public HashMap<String, ItemStack> frozenHead = new HashMap<String, ItemStack>();

    String playerIt = null;
    String previouslyIt = null;

    public boolean gameOn = false;
    public boolean startBool = true;
    public boolean taggerarmor = false;

    String commands = "Commands: \n /tag <join|leave> - join or leave the game \n /tag <start|stop> - start and stop game \n /tag it - view tagged player \n /tag players - view joined playrs \n /tag tagback <allow|forbid> - allow and forbid tagback \n /tag freezetag <on|off> - turn freeze tag on and off \n /tag reload - reloads the config \n /tag setspawn - set arena spawnpoint";

    TheMethods method;
    Listener tag;

    public static TagUtil util;
    public static GlobalVariables vars;
    public static PluginManager pm;
    public static ReflectCommand commandRegistrator;

    @Override
    public void onEnable() {
        try {
            ErrorLogger.register(this, "MCTag", "me.hawkfalcon.mctag", "https://github.com/hawkfalcon/MCTag/issues");
            util = new TagUtil();
            vars = new GlobalVariables(this, "mctag");
            vars.load();
            method = new TheMethods(this);
            tag = new Tag(this, method);
            pm = getServer().getPluginManager();
            log = getLogger();
            pm.registerEvents(new Events(this, method), this);
            pm.registerEvents(tag, this);
            Commands.setup(this, method);
            commandRegistrator = new ReflectCommand(this);
            commandRegistrator.register(Commands.class);


            new MetricsLite(this).start();

            startBool = true;
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "MCTag failed to start!");
            ErrorLogger.generateErrorLog(e);
            setEnabled(false);
        }
    }

    @Override
    public void onDisable() {
        method.cleanUp();
        if (gameOn)
            method.gameOff();
    }
}