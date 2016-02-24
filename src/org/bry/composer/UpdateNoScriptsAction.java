package org.bry.composer;

import org.netbeans.modules.php.api.executable.InvalidPhpExecutableException;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.openide.util.NbBundle;
/**
 *
 * @author Bryan Salazar
 */
public class UpdateNoScriptsAction extends BaseComposerExtAction{

	@NbBundle.Messages("UpdateNoScriptsAction.name=Update-No-Scripts")
	@Override
	protected String getName() {
		return Bundle.UpdateNoScriptsAction_name();
	}

	@Override
	protected void runCommand(PhpModule phpModule) throws InvalidPhpExecutableException {
		ComposerExt.getDefault().updateNoScripts(phpModule);
	}

}
