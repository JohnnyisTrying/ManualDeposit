/**
 * Software License
 *
 * Copyright 2007/2010 National Library of New Zealand.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0 
 *
 * or the file "LICENSE.txt" included with the software.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 */

package nz.govt.natlib.ndha.manualdeposit;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import nz.govt.natlib.ndha.manualdeposit.login.ILoginView;
import nz.govt.natlib.ndha.manualdeposit.login.Login;
import nz.govt.natlib.ndha.manualdeposit.login.LoginPresenter;
import nz.govt.natlib.ndha.manualdeposit.metadata.IMetaDataConfigurator;
import nz.govt.natlib.ndha.manualdeposit.metadata.MetaDataConfigurator;
import nz.govt.natlib.ndha.manualdeposit.metadata.MetaDataConfiguratorPresenter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Main application class<br>
 * Creates a new deposit form & presenter and runs the form
 */
public class App {
	private final static Log LOG = LogFactory.getLog(App.class);

	public static void main(final String[] args) {
		LOG.debug("Starting Indigo Application");
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			LOG.error("Error setting look and feel", ex);
		}

		if ((args.length > 0)
				&& (args[0] != null)
				&& ((args[0].equalsIgnoreCase("metadata")) || (args[0]
						.equalsIgnoreCase("/metadata")))) {
			final IMetaDataConfigurator configurator = new MetaDataConfigurator();
			final MetaDataConfiguratorPresenter presenter = new MetaDataConfiguratorPresenter(
					configurator);
			configurator.setPresenter(presenter);
			presenter.setup();
		} else {
			final IManualDepositMainFrame deposit = new ManualDepositMain();
			AppProperties appProperties = null;
			try {
				appProperties = new AppProperties();
			} catch (Exception ex) {
				final String message = "Error loading application properties - "
						+ ex.getMessage();
				LOG.error(message, ex);
				JOptionPane.showMessageDialog(null, message,
						"Error Loading Application Properties",
						JOptionPane.ERROR_MESSAGE);
				System.exit(-1);
			}
			final ILoginView login = new Login(null, true, appProperties
					.getApplicationData().getSettingsPath());
			try {
				final LoginPresenter loginPresenter = new LoginPresenter(login,
						appProperties);
				final ManualDepositPresenter presenter = new ManualDepositPresenter(
						deposit, login, loginPresenter, appProperties);
				deposit.setPresenter(presenter);
				presenter.setupScreen();
			} catch (Exception ex) {
				LOG.error("Error running Indigo", ex);
				JOptionPane.showMessageDialog(null, ex.getMessage(),
						"Error running Indigo", JOptionPane.ERROR_MESSAGE);
				System.exit(-1);
			}
		}
	}

}
