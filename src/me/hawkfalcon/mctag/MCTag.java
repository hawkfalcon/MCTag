package me.hawkfalcon.mctag;
//Made by: hawkfalcon. Feel free to use the code


//import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.logging.Logger;

import org.bukkit.command.CommandExecutor;
//import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


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
	CommandExecutor cexec; 
	Listener tag; 

	public static TagUtil util;
	public static GlobalVariables vars;
	public static PluginManager pm;


	public void onEnable() {
		ErrorLogger.register(this, "MCTag", "me.hawkfalcon.mctag", "https://github.com/hawkfalcon/MCTag/issues");
		util = new TagUtil();
		vars = new GlobalVariables(this, "mctag");
		vars.load();

		method = new TheMethods(this);
		tag = new Tag(this, method);
		cexec = new Commands(this, method);



		pm = getServer().getPluginManager();

		log = getLogger();	

		pm.registerEvents(new Events(this, new TheMethods(this)), this);
		pm.registerEvents(tag, this);

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