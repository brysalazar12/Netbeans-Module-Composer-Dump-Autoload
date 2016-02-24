package org.bry.composer;

import org.netbeans.modules.php.api.executable.InvalidPhpExecutableException;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.openide.util.NbBundle;

/**
 *
 * @author Bryan Salazar
 */
public class UpdateNoDevNoScriptsAction extends BaseComposerExtAction {

	@NbBundle.Messages("UpdateNoDevNoScriptsAction.name=Update-No-Dev-No-Scripts")
	@Override
	protected String getName() {
		return Bundle.UpdateNoDevNoScriptsAction_name();
	}

	@Override
	protected void runCommand(PhpModule phpModule) throws InvalidPhpExecutableException {
		ComposerExt.getDefault().updateNoDevNoScripts(phpModule);
	}

}
