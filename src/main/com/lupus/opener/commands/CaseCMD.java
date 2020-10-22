package com.lupus.opener.commands;

import com.lupus.command.framework.commands.LupusCommand;
import com.lupus.command.framework.commands.SupCommand;
import com.lupus.opener.commands.sub.admin.*;

public class CaseCMD extends SupCommand {

	public CaseCMD() {
		super("case",
				usage("/case help"),
				"&6Komenda administracyjna do skrzyń",
				1,
				new LupusCommand[]{
						new AllowDestructionCMD(),
						new CreateNewCaseCMD(),
						new GetCaseCMD(),
						new GiveKeyCMD(),
						new HelpCMD(),
						new OpenCaseCMD(),
						new OpenEditorCMD(),
						new RemoveKeyCMD(),
						new SaveCasesCMD(),
				});
	}
}
