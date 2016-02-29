package org.bry.composer;

import org.netbeans.modules.php.api.executable.InvalidPhpExecutableException;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.openide.util.NbBundle;

/**
 *
 * @author Bryan Salazar
 */
public class ShowInstalledAction extends BaseComposerExtAction{

	@NbBundle.Messages("ShowAction.name=Show-Installed")
	@Override
	protected String getName() {
		return Bundle.ShowAction_name();
	}

	@Override
	protected void runCommand(PhpModule phpModule) throws InvalidPhpExecutableException {
		ComposerExt.getDefault().showInstalled(phpModule);
	}

}
