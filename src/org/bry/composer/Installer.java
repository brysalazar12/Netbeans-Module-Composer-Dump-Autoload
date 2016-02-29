/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bry.composer;

import java.util.List;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import org.openide.modules.ModuleInstall;
import org.openide.util.Utilities;
import org.openide.util.actions.Presenter;

/**
 *
 * @author Bryan Salazar
 */
public class Installer extends ModuleInstall implements Runnable {
	@Override
	public void restored() {
		SwingUtilities.invokeLater(this);
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
								jmenu.add(new DumpAutoloadAction());
								jmenu.add(new DumpAutoloadOptimizeAction());
								jmenu.add(new UpdateNoScriptsAction());
								jmenu.add(new UpdateNoDevNoScriptsAction());
								jmenu.add(new ShowInstalledAction());
							}
						}
					}
				}
			}
		} catch(Exception e) {

		}
	}
}
