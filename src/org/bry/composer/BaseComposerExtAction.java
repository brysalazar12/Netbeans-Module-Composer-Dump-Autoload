/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bry.composer;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.netbeans.modules.php.api.executable.InvalidPhpExecutableException;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.UiUtils;
import org.netbeans.modules.php.composer.ui.options.ComposerOptionsPanelController;
import org.openide.util.NbBundle;

/**
 *
 * @author Bryan Salazar
 */
abstract class BaseComposerExtAction extends AbstractAction{

	protected BaseComposerExtAction() {
		putValue("noIconInMenu", true);  // NOI18N
		String name = getName();
		putValue(NAME, name);
		putValue(SHORT_DESCRIPTION, name);
		putValue("menuText", name);  // NOI18N
	}

	protected abstract String getName();

	protected abstract void runCommand(PhpModule phpModule) throws InvalidPhpExecutableException;

	@NbBundle.Messages("BaseComposerExtAction.error.composer.notValid=Composer is not valid.")
	@Override
	public final void actionPerformed(ActionEvent e) {
		PhpModule phpModule = PhpModule.Factory.inferPhpModule();
		if(phpModule == null) {
			return;
		}
		try {
			runCommand(phpModule);
		} catch(InvalidPhpExecutableException ex) {
			UiUtils.invalidScriptProvided(Bundle.BaseComposerExtAction_error_composer_notValid(), ComposerOptionsPanelController.OPTIONS_SUBPATH);
		}
	}

}
