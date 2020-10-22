package com.lupus.opener.commands.sub.admin;






import com.lupus.command.framework.commands.PlayerCommand;
import com.lupus.opener.gui.ChestList;
import com.lupus.utils.ColorUtil;
import org.bukkit.entity.Player;

public class OpenEditorCMD extends PlayerCommand {
	public OpenEditorCMD(){
		super("editor",
				usage("/case editor"),
				ColorUtil.text2Color("&9Otwiera edytor skrzyń"),
				0

		);
	}
	@Override
	public void run(Player executor, String[] args) {
		ChestList list = new ChestList(ColorUtil.text2Color("&4&lSkrzynki"),executor);
		list.open(executor);
	}
}
