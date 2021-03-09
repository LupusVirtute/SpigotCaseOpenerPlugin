package com.lupus.opener.commands;

import com.lupus.command.framework.commands.CommandMeta;
import com.lupus.command.framework.commands.LupusCommand;
import com.lupus.command.framework.commands.SupCommand;
import com.lupus.opener.commands.sub.admin.*;

public class CaseCMD extends SupCommand {

	static CommandMeta meta = new CommandMeta().
			setName("case").
			setUsage(usage("/case help")).
			setDescription(colorText("&6Komenda administracyjna do skrzyń")).
			addPermission("case.admin");
	public CaseCMD() {
		super(meta,
				new LupusCommand[]{
						new AllowDestructionCMD(),
						new CreateNewCaseCMD(),
						new GetCaseCMD(),
						new GiveKeyCMD(),
						new OpenCaseCMD(),
						new OpenEditorCMD(),
						new RemoveKeyCMD(),
						new SaveCasesCMD(),
						new EditWeightCMD(),
						new ReloadAllCMD(),
						new SetStarTrackCommand(),
				});
	}
}
