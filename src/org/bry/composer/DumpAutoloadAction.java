/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bry.composer;

import org.netbeans.modules.php.api.executable.InvalidPhpExecutableException;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.openide.util.NbBundle;

/**
 *
 * @author Bryan Salazar
 */
public class DumpAutoloadAction extends BaseComposerExtAction{

	@NbBundle.Messages("DumpAutoloadAction.name=Dump-Autoload")
	@Override
	protected String getName() {
		return Bundle.DumpAutoloadAction_name();
	}

	@Override
	protected void runCommand(PhpModule phpModule) throws InvalidPhpExecutableException {
		ComposerExt.getDefault().dumpAutoload(phpModule);
	}

}
