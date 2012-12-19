package me.hawkfalcon.mctag;
import me.hawkfalcon.mctag.MCTag;



public class GlobalVariables extends ReflectConfiguration {

public GlobalVariables(MCTag m, String s) {
super(m, s);
}

public String Message_On__It = "%p is now it!";
public String Message_On__Already__It = "You are already it!";
public String Message_On__Game__Start = "A game of tag has begun!";
public String Message_On__Game__Freezetag__Start = "A game of freeze tag has begun!";
public String Message_On__Game__Start__In__Arena = "A game of tag has begun! Type /tag join to join the game";
public String Message_On__Game__Freezetag__Start__In__Arena = "A game of freeze tag has begun! Type /tag join to join the game";
public String Message_On__Not__Enough__Players__To__Give__Award = "There were not enough players in the game to recieve an award!";
public String Message_On__Reward = "You have recieved %a diamonds as a reward for winning freeze tag!";
public String Message_On__Player__Frozen = "%p is now frozen!";
public String Message_On__Join = "%p is now in the game!";
public String Message_On__Join__When__Already__In = "You are already in a game!";
public String Message_On__No__Tagback = "No tagbacks!";

public String Reward_Item__Item = "DIAMOND";
public int Reward_Item__Amount = 1;

public boolean Modes_Arena = false;
public boolean Modes_Freeze = false;

public boolean Player_Allow__Tagback = false;
public boolean Player_Commands__In__Arena = false;
public boolean Player_Damage__From__Tagger = true;
public boolean Player_Air__In__Hand__To__Tag = true;

public String Spawn_Location = " ";


}