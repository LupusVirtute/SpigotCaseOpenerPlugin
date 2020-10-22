package com.lupus.opener.commands.sub.admin;






import com.lupus.command.framework.commands.PlayerCommand;
import com.lupus.opener.listeners.BlockManipulationListener;
import com.lupus.opener.messages.GeneralMessages;
import com.lupus.utils.ColorUtil;
import com.lupus.utils.Usage;
import org.bukkit.entity.Player;

public class AllowDestructionCMD extends PlayerCommand {

	public AllowDestructionCMD() {
		super("destroy",
				Usage.usage("/case destroy","[yes/no]"),
				ColorUtil.text2Color("&6Pozwala lub nie pozwala na niszczenie miejsc skrzyń &a&lyes&0&l/&4&lno"),
				1);
	}

	@Override
	protected void run(Player executor, String[] args) {
		if (!executor.hasPermission("case.admin.remove")) {
			executor.sendMessage(GeneralMessages.INSUFFICIENT_PERMISSIONS.toString());
			return;
		}
		if (args[0].contains("yes")) {
			BlockManipulationListener.isTimeForDestroy = true;
			executor.sendMessage(ColorUtil.text2Color("&4Włączono niszczenie lokacji skrzyń"));
		} else {
			BlockManipulationListener.isTimeForDestroy = false;
			executor.sendMessage(ColorUtil.text2Color("&4Wyłączono niszczenie lokacji skrzyń"));
		}
		return;
	}
}
