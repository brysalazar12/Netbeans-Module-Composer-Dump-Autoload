package org.bry.composer;

import org.netbeans.modules.php.api.executable.InvalidPhpExecutableException;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.openide.util.NbBundle;

/**
 *
 * @author Bryan Salazar
 */
public class DumpAutoloadOptimizeAction extends BaseComposerExtAction{

	@NbBundle.Messages("DumpAutoloadOptimizeAction.name=Dump-Autoload-Optimize")
	@Override
	protected String getName() {
		return Bundle.DumpAutoloadOptimizeAction_name();
	}

	@Override
	protected void runCommand(PhpModule phpModule) throws InvalidPhpExecutableException {
		ComposerExt.getDefault().dumpAutoloadOptimize(phpModule);
	}

}
