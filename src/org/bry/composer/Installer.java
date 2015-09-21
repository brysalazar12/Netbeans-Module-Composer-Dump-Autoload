/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bry.composer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NullAllowed;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.modules.php.api.executable.PhpExecutable;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.composer.options.ComposerOptions;
import org.netbeans.modules.php.composer.ui.options.ComposerOptionsPanelController;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.modules.ModuleInstall;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.actions.Presenter;

public class Installer extends ModuleInstall implements Runnable, ActionListener{
	private volatile File workDir;
	private static final String ANSI_PARAM = "--ansi"; // NOI18N
	private static final String NO_INTERACTION_PARAM = "--no-interaction"; // NOI18N
	private static final String DUMP_AUTOLOAD = "dump-autoload";

	private static final List<String> DEFAULT_PARAMS = Arrays.asList(
		ANSI_PARAM,
		NO_INTERACTION_PARAM
	);

	@Override
	public void restored() {
		SwingUtilities.invokeLater(this);
	}

	@NbBundle.Messages("Composer.run.dumpAutoload=Composer (dump-autoload)")
	public void dumpAutoload(PhpModule phpModule) {
		runCommand(phpModule, DUMP_AUTOLOAD, Bundle.Composer_run_dumpAutoload());
	}

	/**
	 * Add sub-menu in Composer
	 */
	@Override
	public void run() {
		try {
			List<? extends Action> myActions = Utilities.actionsForPath("Projects/org-netbeans-modules-php-phpproject/Actions");
			for(Action action: myActions) {
				if(action != null) {
					if(action instanceof  Presenter.Popup)
					{
						Presenter.Popup popup = (Presenter.Popup) action;
						if(popup.getPopupPresenter() != null) {
							JMenuItem menu = popup.getPopupPresenter();
							if(menu != null && menu.getText().equals("Composer")) {
								JMenu jmenu = (JMenu) popup.getPopupPresenter();
								JMenuItem item = new JMenuItem("Dump-Autoload");
								item.addActionListener(this);
								jmenu.add(item);
							}
						}
					}
				}
			}
		} catch(Exception e) {

		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		PhpModule phpModule = PhpModule.Factory.inferPhpModule();
		dumpAutoload(phpModule);
	}

	private Future<Integer> runCommand(@NullAllowed PhpModule phpModule, String command, String title, List<String> commandParams) {
		PhpExecutable composer = getComposerExecutable(phpModule, title);
		if (composer == null) {
			return null;
		}
		return composer.additionalParameters(mergeParameters(command, DEFAULT_PARAMS, commandParams))
				.run(getDescriptor(phpModule));
	}

	private List<String> mergeParameters(String command, List<String> defaultParams, List<String> commandParams) {
		List<String> allParams = new ArrayList<>(defaultParams.size() + commandParams.size() + 1);
		allParams.addAll(defaultParams);
		allParams.add(command);
		allParams.addAll(commandParams);
		return allParams;
	}

	private ExecutionDescriptor getDescriptor(@NullAllowed PhpModule phpModule) {
		ExecutionDescriptor descriptor = PhpExecutable.DEFAULT_EXECUTION_DESCRIPTOR
				.optionsPath(ComposerOptionsPanelController.getOptionsPath())
				.inputVisible(false);
		if (phpModule != null) {
			final FileObject sourceDirectory = phpModule.getSourceDirectory();
			if (sourceDirectory != null) {
				descriptor = descriptor
						.postExecution(new Runnable() {
							@Override
							public void run() {
								// refresh sources after running command
								sourceDirectory.refresh();
							}
						});
			}
		}
		return descriptor;
	}

	@CheckForNull
	private PhpExecutable getComposerExecutable(@NullAllowed PhpModule phpModule, String title) {
		File dir = resolveWorkDir(phpModule);
		if (dir == null && phpModule != null) {
			warnNoSources(phpModule.getDisplayName());
			return null;
		}
		String composerPath = ComposerOptions.getInstance().getComposerPath();
		PhpExecutable composer = new PhpExecutable(composerPath)
				.optionsSubcategory(ComposerOptionsPanelController.OPTIONS_SUBPATH)
				.displayName(title);
		if (dir != null) {
			composer.workDir(dir);
		}
		return composer;
	}

	private File resolveWorkDir(PhpModule phpModule) {
		if (workDir != null) {
			return workDir;
		}
		if (phpModule == null) {
			return null;
		}
		FileObject sourceDirectory = phpModule.getSourceDirectory();
		if (sourceDirectory == null) {
			// broken project
			return null;
		}
		return FileUtil.toFile(sourceDirectory);
	}

	@NbBundle.Messages({
		"# {0} - project name",
		"Composer.project.noSources=Project {0} has no Source Files."
	})
	private static void warnNoSources(String projectName) {
//		DialogDisplayer.getDefault().notifyLater(new NotifyDescriptor.Message(Bundle.Composer_project_noSources(projectName), NotifyDescriptor.WARNING_MESSAGE));
	}

	private Future<Integer> runCommand(@NullAllowed PhpModule phpModule, String command, String title) {
		return runCommand(phpModule, command, title, Collections.<String>emptyList());
	}
}
