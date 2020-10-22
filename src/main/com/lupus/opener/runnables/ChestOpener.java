package com.lupus.opener.runnables;

import com.lupus.opener.CaseOpener;
import com.lupus.opener.gui.OpeningCase;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ChestOpener extends BukkitRunnable {
	OpeningCase mcCase;
	Player forPlayer;
	public ChestOpener(OpeningCase openingCase,Player forPlayer){
		mcCase = openingCase;
		this.forPlayer = forPlayer;
	}
	@Override
	public void run() {
		if (!forPlayer.isOnline()) {
			return;
		}
		boolean b = mcCase.doABarrelRoll(forPlayer);
		int time = (int)(((float)mcCase.getCurrentIndex() / (float)mcCase.getWinnerIndex()) * 9f)+1;
		if (!b)
			new ChestOpener(mcCase,forPlayer).runTaskLater(CaseOpener.getMainPlugin(),time);
	}
}
