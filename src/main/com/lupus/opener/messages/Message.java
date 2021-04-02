package com.lupus.opener.messages;

import com.lupus.gui.utils.ConfigUtility;
import com.lupus.gui.utils.TextUtility;
import com.lupus.opener.CaseOpener;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public enum Message {
	DESTROY_MESSAGE_ON,
	DESTROY_MESSAGE_OFF,
	CASE_EXISTS,
	CASE_GIVEN_DONT_EXISTS,
	COMMAND_CREATE_SUCCESSFUL,
	COMMAND_EDIT_WEIGHT_SUCCESS,
	COMMAND_GET_CASE_SUCCESS,
	COMMAND_GIVE_KEY_SUCCESS_MESSAGE_PLAYER,
	COMMAND_GIVE_KEY_SUCCESS_MESSAGE_ADMIN,
	COMMAND_TAKE_KEY_FAIL_NO_KEYS_LEFT,
	COMMAND_TAKE_KEY_SUCCESS_MESSAGE_PLAYER,
	COMMAND_TAKE_KEY_SUCCESS_MESSAGE_ADMIN,

	COMMAND_BUY_KEY_BOUGHT,
	COMMAND_CHANGE_KEY_NEED,
	COMMAND_CHANGE_KEY_SUCCESS,

	COMMAND_KEYS_TOP,
	COMMAND_KEYS_BOTTOM,
	COMMAND_KEYS_KEY,

	QUANTITY_MORE_THAN_ZERO,
	NOT_ENOUGH_KEYS,
	KEY_SEND_SUCCESS_SENDER,
	KEY_SEND_SUCCESS_RECEIVER,

	BUY_CASE_INVENTORY_NAME,
	BUY_CASE_PRICE_LORE,

	DROP_CHANCE_LORE,
	CHEST_LIST_ADMIN_LORE,
	CHEST_LIST_PLAYER_LORE,

	CHEST_LIST_INVENTORY_NAME,

	ITEM_EDITOR_ACCEPT_NAME,
	ITEM_EDITOR_DENY_NAME,
	ITEM_EDITOR_INFO_NAME,
	ITEM_EDITOR_INFO_LORE,

	WINNER_MESSAGE,

	CASE_OPENING_BACKGROUND_GLASS_LORE,
	CASE_OPENING_BACKGROUND_GLASS_NAME,
	CASE_OPENING_POINTER_GLASS_LORE,
	CASE_OPENING_POINTER_GLASS_NAME,

	STATTRACK_KILLS_FORMATING,
	STATTRACK_KILLS_NAME,
	NULL_ITEM_IN_HAND,
	STATTRACK_ITEM_SET_PROPERLY,
	NO_KEY,
	ALERADY_OPENING,

	SAVING_INIT,
	INSUFFICIENT_MONEY,
	TOP_KEYS_MAIN_INVENTORY_NAME,
	SAVING_END,
	CASE_PLACED_PROPERLY,
	CASE_HAS_0_KEYS,
	SELECTABLE_TOP_FILLER_NAME,
	SELECTABLE_TOP_FILLER_LORE,
	;
	private static final char REPLACEMENT_CHAR = '%';
	private String text;
	Message(){
		this.text = name();
	}
	private void setText(String text){
		if (text == null)
			return;
		this.text = TextUtility.color(text);
	}
	public static void load(){
		CaseOpener.getMainPlugin().saveResource("Messages.yml",false);
		FileConfiguration config = ConfigUtility.getConfig(CaseOpener.getMainPlugin(),"Messages.yml");
		for (Message value : values()) {
			value.setText(config.getString(value.name() ));
		}
	}
	@Override
	public String toString(){
		return text.replace("\\n","\n");
	}
	public String toString(MessageReplaceQuery query){
		return toString(query.toMap());
	}
	public String toString(Map<String,String> replacementMap){
		String copiedText = text;
		for (Map.Entry<String, String> replacementEntry : replacementMap.entrySet()) {
			String key = new StringBuilder().
					append(REPLACEMENT_CHAR).
					append(replacementEntry.getKey()).
					append(REPLACEMENT_CHAR).
					toString();
			copiedText = copiedText.replace(key, replacementEntry.getValue());
		}
		return copiedText.replace("\\n","\n");

	}
}
