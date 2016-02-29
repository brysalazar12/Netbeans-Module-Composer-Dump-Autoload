/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bry.composer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import javax.swing.SwingUtilities;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NullAllowed;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.api.extexecution.input.InputProcessor;
import org.netbeans.modules.php.api.executable.InvalidPhpExecutableException;
import org.netbeans.modules.php.api.executable.PhpExecutable;
import org.netbeans.modules.php.api.executable.PhpExecutableValidator;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.api.util.UiUtils;
import org.netbeans.modules.php.api.validation.ValidationResult;
import org.netbeans.modules.php.composer.commands.Composer;
import org.netbeans.modules.php.composer.options.ComposerOptions;
import org.netbeans.modules.php.composer.options.ComposerOptionsValidator;
import org.netbeans.modules.php.composer.output.model.SearchResult;
import org.netbeans.modules.php.composer.output.parsers.Parsers;
import org.netbeans.modules.php.composer.ui.options.ComposerOptionsPanelController;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *
 * @author Bryan Salazar
 */
public class ComposerExt {

    private static final String COMPOSER = "composer"; // NOI18N
    private static final String COMPOSER_PHAR = COMPOSER + ".phar"; // NOI18N
    private static final String COMPOSER_BAT = COMPOSER + ".bat"; // NOI18N

    // commands
    private static final String DUMP_AUTOLOAD_COMMAND = "dump-autoload"; // NOI18N
	private static final String UPDATE = "update"; // NOI18N
	private static final String SHOW = "show";
    // params
    private static final String ANSI_PARAM = "--ansi"; // NOI18N
    private static final String NO_ANSI_PARAM = "--no-ansi"; // NOI18N
    private static final String NO_INTERACTION_PARAM = "--no-interaction"; // NOI18N
	private static final String NO_SCRIPTS_PARAM = "--no-scripts";
	private static final String NO_DEV_PARAM = "--no-dev";
    private static final String OPTIMIZE_PARAM = "-o"; // NOI18N
	private static final String INSTALLED_PARAM = "--installed";
    private static final List<String> DEFAULT_PARAMS = Arrays.asList(
        ANSI_PARAM,
        NO_INTERACTION_PARAM
    );

    private final String composerPath;

    private volatile File workDir;

    public ComposerExt(String composerPath) {
        this.composerPath = composerPath;
    }

    /**
     * Get the default, <b>valid only</b> Composer.
     * @return the default, <b>valid only</b> ComposerExt.
     * @throws InvalidPhpExecutableException if Composer is not valid.
     */
    public static ComposerExt getDefault() throws InvalidPhpExecutableException {
        String composerPath = ComposerOptions.getInstance().getComposerPath();
        String error = validate(composerPath);
        if (error != null) {
            throw new InvalidPhpExecutableException(error);
        }
        return new ComposerExt(composerPath);
    }

    @NbBundle.Messages("ComposerExt.script.label=Composer")
    public static String validate(String composerPath) {
        return PhpExecutableValidator.validateCommand(composerPath, Bundle.ComposerExt_script_label());
    }

    public static boolean isValidOutput(String output) {
        if (output.startsWith("Warning:") // NOI18N
                || output.startsWith("No composer.json found")) { // NOI18N
            return false;
        }
        return true;
    }

	@NbBundle.Messages("ComposerExt.run.showInstalled=Composer (show --installed)")
	public Future<Integer> showInstalled(PhpModule phpModule) {
		assert phpModule != null;
		return runCommand(phpModule, SHOW, Bundle.ComposerExt_run_showInstalled(),Collections.singletonList(INSTALLED_PARAM));
	}

	@NbBundle.Messages("ComposerExt.run.updateNoScripts=Composer (update --no-scripts)")
	public Future<Integer> updateNoScripts(PhpModule phpModule) {
		assert phpModule != null;
		return runCommand(phpModule, UPDATE, Bundle.ComposerExt_run_updateNoScripts(),Collections.singletonList(NO_SCRIPTS_PARAM));
	}

	@NbBundle.Messages("ComposerExt.run.updateNoDevNoScripts=Composer (update --no-dev --no-scripts)")
	public Future<Integer> updateNoDevNoScripts(PhpModule phpModule) {
		assert phpModule != null;
		ArrayList<String> param = new ArrayList<>();
		param.add(NO_DEV_PARAM);
		param.add(NO_SCRIPTS_PARAM);

		return runCommand(phpModule, UPDATE, Bundle.ComposerExt_run_updateNoScripts(),param);
	}

	@NbBundle.Messages("ComposerExt.run.dumpAutoload=Composer (dump-autoload)")
	public Future<Integer> dumpAutoload(PhpModule phpModule) {
		assert phpModule != null;
		return runCommand(phpModule, DUMP_AUTOLOAD_COMMAND, Bundle.ComposerExt_run_dumpAutoload());
	}

	@NbBundle.Messages("ComposerExt.run.dumpAutoloadOptimize=Composer (dump-autoload -o)")
	public Future<Integer> dumpAutoloadOptimize(PhpModule phpModule) {
		assert phpModule != null;
		return runCommand(phpModule, DUMP_AUTOLOAD_COMMAND, Bundle.ComposerExt_run_dumpAutoloadOptimize(),Collections.singletonList(OPTIMIZE_PARAM));
	}

    private Future<Integer> runCommand(@NullAllowed PhpModule phpModule, String command, String title) {
        return runCommand(phpModule, command, title, Collections.<String>emptyList());
    }

    private Future<Integer> runCommand(@NullAllowed PhpModule phpModule, String command, String title, List<String> commandParams) {
        PhpExecutable composer = getComposerExecutable(phpModule, title);
        if (composer == null) {
            return null;
        }
        return composer
                .additionalParameters(mergeParameters(command, DEFAULT_PARAMS, commandParams))
                .run(getDescriptor(phpModule));
    }

    @CheckForNull
    private PhpExecutable getComposerExecutable(@NullAllowed PhpModule phpModule, String title) {
        File dir = resolveWorkDir(phpModule);
        if (dir == null
                && phpModule != null) {
            warnNoSources(phpModule.getDisplayName());
            return null;
        }
        PhpExecutable composer = new PhpExecutable(composerPath)
                .optionsSubcategory(ComposerOptionsPanelController.OPTIONS_SUBPATH)
                .displayName(title);
        if (dir != null) {
            composer.workDir(dir);
        }
        return composer;
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

    @NbBundle.Messages({
        "# {0} - project name",
        "ComposerExt.project.noSources=Project {0} has no Source Files."
    })
    private static void warnNoSources(String projectName) {
        DialogDisplayer.getDefault().notifyLater(
                new NotifyDescriptor.Message(Bundle.ComposerExt_project_noSources(projectName), NotifyDescriptor.WARNING_MESSAGE));
    }

    @CheckForNull
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

    public File getWorkDir() {
        return workDir;
    }

    public void setWorkDir(File workDir) {
        assert workDir == null || workDir.isDirectory() : "Existing directory or null expected: " + workDir;
        this.workDir = workDir;
    }

    //~ Inner classes

    public interface OutputProcessor<T> {
        void process(T item);
    }

    private interface OutputParser {
        void parse(char[] chars);
    }
}
