package me.hawkfalcon.mctag;


import net.minecraft.server.MinecraftServer;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import static java.lang.System.getProperty;

/**
 * Custom pluginLogger to save errors. Multiple-instance safe
 *
 * @author Icyene, Xiaomao
 */

public class ErrorLogger extends PluginLogger {

    private static Field mcLogger, craftbukkitServer, pluginLogger, prepend;

    private ErrorLogger(Plugin context) {
        super(context);
    }

    @Override
    public void log(LogRecord logRecord) {
        if (!generateErrorLog(logRecord))
            super.log(logRecord);
    }

    public static void register(Plugin context, String name, String pack, String tracker) {
        try {
            if (!(pluginLogger.get(context) instanceof ErrorLogger)) {
                ErrorLogger cLog = new ErrorLogger(context);
                pluginLogger.set(context, cLog);
            }
            if (!(mcLogger.get(craftbukkitServer) instanceof ErrorLogger)) {
                ErrorLogger pLog = new ErrorLogger(context);
                prepend.set(pLog, ""); //NYET ALIAS SERVER UNDER FIRST REGISTERED PLUGIN!
                mcLogger.set(craftbukkitServer, pLog);
            }
            HashMap<String, List<String>> registry = loadMap();
            registry.put(name, Arrays.asList(pack, tracker, "\n" + StringUtils.center(name, 54 + name.length(), '=')));
            saveMap(registry);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean generateErrorLog(LogRecord record) {
        Throwable thrown;
        if ((thrown = record.getThrown()) == null)
            return false;
        String ERROR = ExceptionUtils.getStackTrace(thrown), NAME = "", TICKETS = "", ENDL = "";
        Plugin PLUGIN = null;
        for (Map.Entry<String, ArrayList<String>> entry : ((HashMap<String, ArrayList<String>>) loadMap()).entrySet()) {
            try {
                List<String> data = entry.getValue();
                if (ERROR.contains(data.get(0))) { //If the ERROR contains the package
                    NAME = entry.getKey();
                    PLUGIN = Bukkit.getPluginManager().getPlugin(NAME);
                    TICKETS = data.get(1);
                    ENDL = data.get(2);

                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }      
        if (ERROR.contains(NAME + " has encountered an error!") && ERROR.contains(ErrorLogger.class.getName()))  //Check if its not our own
            return false;
        boolean disable = false;
        StringBuilder err = new StringBuilder();
        err.append("\n=============" + NAME + " has encountered an error!=============")
                .append("\nStacktrace:\n" + ERROR)
                .append("\n" + NAME + " version: " + PLUGIN.getDescription().getVersion())
                .append("\nBukkit message: " + record.getMessage())
                .append("\nPlugins loaded: " + Arrays.asList(Bukkit.getPluginManager().getPlugins()))
                .append("\nCraftBukkit version: " + Bukkit.getServer().getBukkitVersion())
                .append("\nJava version: " + getProperty("java.version"))
                .append("\nOS info: " + getProperty("os.arch") + " " + getProperty("os.name") + ", " + getProperty("os.version"))
                .append("\nPlease report this error to the " + NAME + " ticket tracker (" + TICKETS + ")!");
        ERROR = ERROR.toLowerCase();
        if (ERROR.contains("nullpointerexception") || ERROR.contains("stackoverflowexception")) {
            err.append("\nA critical error has been thrown. " + NAME + " has been disabled to prevent further damage.");
            disable = true;
        } else {
            err.append("\nError was minor; " + NAME + " will continue operating.");
        }
        try {
            //One-liner beauty.
            String FILE_NAME = NAME + "_" + String.format("%032x", new BigInteger(1, MessageDigest.getInstance("MD5").digest(err.toString().getBytes()))).substring(0, 6) + ".error.log"; //Name is PLUGIN_NAME with first 6 chars of md5 appended
            File root = new File(PLUGIN.getDataFolder(), "errors");
            if (!root.exists())
                root.mkdir();
            File dump = new File(root.getAbsoluteFile(), FILE_NAME);
            if (!dump.exists()) {
                dump.createNewFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(dump));
                writer.write((err.toString() + ENDL).substring(1)); //Remove the extra /n
                writer.close();
                err.append("\nThis has been saved to the file ./" + PLUGIN.getName() + "/errors/" + FILE_NAME);
            }
        } catch (Exception e) {
            err.append("\nErrors occured while saving to file. Not saved.");
        }
        err.append(ENDL);
        System.err.println(err);
        if (disable)
            Bukkit.getServer().getPluginManager().disablePlugin(PLUGIN);
        return true;
    }

    private static void setup() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                LogRecord o_u_screwd = new LogRecord(Level.SEVERE, "Oh, yea. You're screwed."); //Forced to set a name, you say?
                o_u_screwd.setMessage("Bukkit did not catch this, so no additional info is available.");
                o_u_screwd.setThrown(throwable);
                generateErrorLog(o_u_screwd);
            }
        });
        try {
            mcLogger = MinecraftServer.class.getDeclaredField("log");
            mcLogger.setAccessible(true);
            craftbukkitServer = CraftServer.class.getDeclaredField("console");
            craftbukkitServer.setAccessible(true);
            pluginLogger = JavaPlugin.class.getDeclaredField("logger");
            pluginLogger.setAccessible(true);
            prepend = PluginLogger.class.getDeclaredField("pluginName");
            prepend.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveMap(HashMap map) {          
        System.setProperty("__ErrorLogger__", map.toString());
    }

    private static HashMap loadMap() {
        String pro = getProperty("__ErrorLogger__");
        if (StringUtils.isEmpty(pro))
            return new HashMap();          
        List<String> format = Arrays.asList(pro.replace("=[", ", ").replace("]", "").replace("{", "").replace("}", "").split(", "));          
        HashMap<String, List<String>> ret = new HashMap<String, List<String>>();
        ret.put(format.get(0), Arrays.asList(format.get(1), format.get(2), format.get(3)));
        return ret;
    }

    static {
        setup();
    }
}