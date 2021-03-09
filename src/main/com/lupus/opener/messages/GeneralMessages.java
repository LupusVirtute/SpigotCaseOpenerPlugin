package com.lupus.opener.messages;


import com.lupus.gui.utils.TextUtility;
import org.bukkit.ChatColor;

public enum GeneralMessages {
	LOGO("&r[&8&lM&f&lO&r]"),
	NO_KEY("&4Nie posiadasz klucza do tej skrzyni"),
	INSUFFICIENT_PERMISSIONS( ChatColor.DARK_RED + "Nie masz dostępu do tej komendy"),
	PLAYER_OFFLINE(ChatColor.RED  + "Gracz jest offline");
	String message;
	GeneralMessages(String message){
		this.message = TextUtility.color(message);
	}
	public String toString(String string){
		return message.replace("%string%",string);
	}
	public String toString(){
		return message;
	}
}
