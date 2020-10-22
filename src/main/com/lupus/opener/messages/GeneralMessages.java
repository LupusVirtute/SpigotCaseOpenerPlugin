package com.lupus.opener.messages;


import com.lupus.utils.ColorUtil;
import org.bukkit.ChatColor;

public enum GeneralMessages {
	LOGO("&7[&6&lT&3&lO&f&lC&7]"),
	NO_KEY("&4Nie posiadasz klucza do tej skrzyni"),
	INSUFFICIENT_PERMISSIONS( ChatColor.DARK_RED + "Nie masz dostępu do tej komendy"),
	PLAYER_OFFLINE(ChatColor.RED  + "Gracz jest offline");
	String message;
	GeneralMessages(String message){
		this.message = ColorUtil.text2Color(message);
	}
	public String toString(String string){
		return message.replace("%string%",string);
	}
	public String toString(){
		return message;
	}
}
