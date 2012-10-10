package me.hawkfalcon.mctag;
import me.hawkfalcon.mctag.MCTag;



public class GlobalVariables extends ReflectConfiguration {

public GlobalVariables(MCTag m, String s) {
super(m, s);
}

public String Message_On__It = "[MCTag] %p is now it!";
public String Message_On__Not__Enough__Players__To__Give__Award = "[MCTag] There were not enough players in the game to recieve an award!";
public String Message_On__Reward = "[MCTag] You have recieved %a diamonds as a reward for winning freeze tag!";
public String Message_On__Player__Frozen = "[MCTag]  %p is now frozen!";
public String Message_On__Join = "[MCTag] %p is now in the game!";
public String Message_On__Join__When__Already__In = "[MCTag] You are already in a game!";
public String Message_On__No__Tagback = "[MCTag] No tagbacks!";

public int Reward_Diamond__Amount = 1;

public boolean Modes_Arena = true;
public boolean Modes_Freeze = false;

public boolean Player_Allow__Tagback = false;
public boolean Player__Commands__In__Arena = false;

public String Spawn_Location = " ";


}