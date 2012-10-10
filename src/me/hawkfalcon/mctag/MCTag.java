package me.hawkfalcon.mctag;
//Made by: hawkfalcon. Feel free to use the code


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.logging.Logger;

import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

//FIXED!

public class MCTag extends JavaPlugin implements Listener {

	TheMethods method;
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
	
	public CommandExecutor cexec = new Commands(this, new TheMethods(this));
	public Listener Tag = new Tag(this, new TheMethods(this));

	
	public static TagUtil util;
	public static GlobalVariables vars;
	public static PluginManager pm;
	
	
	public void onEnable() {
	
	method = new TheMethods(this);
	
		util = new TagUtil();
		vars = new GlobalVariables(this, "mctag");
		vars.load();
		
		pm = getServer().getPluginManager();
		
		log = getLogger();	
		
		pm.registerEvents(new Events(this, new TheMethods(this)), this);
		pm.registerEvents(Tag, this);
		
		getCommand("Tag").setExecutor(cexec);
		
		try {
		new MetricsLite(this).start();			
		} catch (IOException e) {			
		}
		
		startBool = true;
	}

	public void onDisable() {		
		method.cleanUp();
		if(gameOn)
			method.gameOff();		
	}
}