package org.bry.composer;
/** Localizable strings for {@link org.bry.composer}. */
@javax.annotation.Generated(value="org.netbeans.modules.openide.util.NbBundleProcessor")
class Bundle {
    /**
     * @return <i>Composer is not valid.</i>
     * @see BaseComposerExtAction
     */
    static String BaseComposerExtAction_error_composer_notValid() {
        return org.openide.util.NbBundle.getMessage(Bundle.class, "BaseComposerExtAction.error.composer.notValid");
    }
    /**
     * @param project_name project name
     * @return <i>Project </i>{@code project_name}<i> has no Source Files.</i>
     * @see ComposerExt
     */
    static String ComposerExt_project_noSources(Object project_name) {
        return org.openide.util.NbBundle.getMessage(Bundle.class, "ComposerExt.project.noSources", project_name);
    }
    /**
     * @return <i>Composer (dump-autoload)</i>
     * @see ComposerExt
     */
    static String ComposerExt_run_dumpAutoload() {
        return org.openide.util.NbBundle.getMessage(Bundle.class, "ComposerExt.run.dumpAutoload");
    }
    /**
     * @return <i>Composer (dump-autoload -o)</i>
     * @see ComposerExt
     */
    static String ComposerExt_run_dumpAutoloadOptimize() {
        return org.openide.util.NbBundle.getMessage(Bundle.class, "ComposerExt.run.dumpAutoloadOptimize");
    }
    /**
     * @return <i>Composer</i>
     * @see ComposerExt
     */
    static String ComposerExt_script_label() {
        return org.openide.util.NbBundle.getMessage(Bundle.class, "ComposerExt.script.label");
    }
    /**
     * @param project_name project name
     * @return <i>Project </i>{@code project_name}<i> has no Source Files.</i>
     * @see Installer
     */
    static String Composer_project_noSources(Object project_name) {
        return org.openide.util.NbBundle.getMessage(Bundle.class, "Composer.project.noSources", project_name);
    }
    /**
     * @return <i>Composer (dump-autoload)</i>
     * @see Installer
     */
    static String Composer_run_dumpAutoload() {
        return org.openide.util.NbBundle.getMessage(Bundle.class, "Composer.run.dumpAutoload");
    }
    /**
     * @return <i>Dump-Autoload</i>
     * @see DumpAutoloadAction
     */
    static String DumpAutoloadAction_name() {
        return org.openide.util.NbBundle.getMessage(Bundle.class, "DumpAutoloadAction.name");
    }
    /**
     * @return <i>Dump-Autoload-Optimize</i>
     * @see DumpAutoloadOptimizeAction
     */
    static String DumpAutoloadOptimizeAction_name() {
        return org.openide.util.NbBundle.getMessage(Bundle.class, "DumpAutoloadOptimizeAction.name");
    }
    private void Bundle() {}
}
