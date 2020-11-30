package com.lupus.opener.messages;

import com.lupus.utils.ColorUtil;

public enum EconomyMessages {
	INSUFFICIENT_MONEY("&4Potrzebujesz &6%string%&2$ &4by kupić tą usługe");
	String message;
	EconomyMessages(String message){
		this.message = ColorUtil.text2Color(message);
	}
	public String toString(String string){
		return message.replace("%string%",string);
	}
	public String toString(){
		return message;
	}
}
