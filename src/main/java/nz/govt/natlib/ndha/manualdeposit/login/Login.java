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

package nz.govt.natlib.ndha.manualdeposit.login;

import java.awt.Font;

import javax.swing.JOptionPane;

import nz.govt.natlib.ndha.common.guiutilities.FormControl;
import nz.govt.natlib.ndha.manualdeposit.FormUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Login extends javax.swing.JDialog implements ILoginView {

	private static final long serialVersionUID = -7508171946486518945L;
	private final static String USER_NAME_ATTR = "UserName";
	private final static Log LOG = LogFactory.getLog(Login.class);
	private final String theSettingsPath;
	private ILoginPresenter thePresenter;
	// @SuppressWarnings("unused")
	private FormControl theFormControl; // NOPMD

	/** Creates new form Login */
	public Login(java.awt.Frame parent, boolean modal, String settingsPath) {
		super(parent, modal);
		LOG.debug("Login form created");
		theSettingsPath = settingsPath;
		initComponents(); // NOPMD
	}

	public void showView() {
		setVisible(true);
	}

	public void setPresenter(ILoginPresenter presenter) {
		thePresenter = presenter;
	}

	public void closeForm() {
		this.setVisible(false);
	}

	public void setFormFont(Font theFont) {
		FormUtilities.setFormFont(this, theFont);
	}

	public void showError(String header, String message) {
		JOptionPane.showMessageDialog(this, message, header,
				JOptionPane.ERROR_MESSAGE);
	}

	public void showMessage(String header, String message) {
		JOptionPane.showMessageDialog(this, message, header,
				JOptionPane.INFORMATION_MESSAGE);
	}

	public boolean confirm(String message) {
		if (JOptionPane.showConfirmDialog(this, message, "Please Confirm",
				JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
			return true;
		} else {
			return false;
		}
	}

	public void tryLogin() {
		String userName = txtUserID.getText();
		theFormControl.setExtra(USER_NAME_ATTR, userName);
		thePresenter.login(userName, new String(txtPassword.getPassword()));
	}

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

		jLabel1 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		txtUserID = new javax.swing.JTextField();
		jLabel3 = new javax.swing.JLabel();
		txtPassword = new javax.swing.JPasswordField();
		btnOK = new javax.swing.JButton();
		btnCancel = new javax.swing.JButton();

		setTitle("DPS Login");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowOpened(java.awt.event.WindowEvent evt) {
				formWindowOpened(evt);
			}
		});

		jLabel1.setText("Please enter your DPS user ID and password");

		jLabel2.setText("User ID");

		txtUserID.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				txtActionPerformed(evt);
			}
		});

		jLabel3.setText("Password");

		txtPassword.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				txtActionPerformed(evt);
			}
		});

		btnOK.setText("Ok");
		btnOK.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnOKActionPerformed(evt);
			}
		});

		btnCancel.setText("Cancel");
		btnCancel.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnCancelActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout
				.setHorizontalGroup(layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								layout
										.createSequentialGroup()
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																layout
																		.createSequentialGroup()
																		.addContainerGap()
																		.addComponent(
																				jLabel1))
														.addGroup(
																layout
																		.createSequentialGroup()
																		.addContainerGap()
																		.addGroup(
																				layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addComponent(
																								jLabel2)
																						.addComponent(
																								jLabel3))
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																		.addGroup(
																				layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addComponent(
																								txtPassword,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								181,
																								Short.MAX_VALUE)
																						.addGroup(
																								layout
																										.createSequentialGroup()
																										.addComponent(
																												btnOK)
																										.addGap(
																												39,
																												39,
																												39)
																										.addComponent(
																												btnCancel))
																						.addComponent(
																								txtUserID,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								181,
																								Short.MAX_VALUE))))
										.addGap(21, 21, 21)));
		layout
				.setVerticalGroup(layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								layout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(jLabel1)
										.addGap(37, 37, 37)
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(jLabel2)
														.addComponent(
																txtUserID,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addGap(18, 18, 18)
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(jLabel3)
														.addComponent(
																txtPassword,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addGap(30, 30, 30)
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(btnCancel)
														.addComponent(btnOK))
										.addContainerGap(20, Short.MAX_VALUE)));

		pack();
   }// </editor-fold>//GEN-END:initComponents

	private void txtActionPerformed(java.awt.event.ActionEvent evt) {
		tryLogin();
	}

	private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {
		thePresenter.cancelLogin();
	}

	private void btnOKActionPerformed(java.awt.event.ActionEvent evt) {
		tryLogin();
	}

	private void formWindowOpened(java.awt.event.WindowEvent evt) {
		try {
			theFormControl = new FormControl(this, theSettingsPath);
			String userName = theFormControl.getExtra(USER_NAME_ATTR, "");
			txtUserID.setText(userName);
			if (!userName.equals("")) {
				this.txtPassword.grabFocus();
			}
		} catch (Exception ex) {
			LOG.error("Error loading form parameters", ex);
		}
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton btnCancel;
	private javax.swing.JButton btnOK;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JPasswordField txtPassword;
	private javax.swing.JTextField txtUserID;
	// End of variables declaration//GEN-END:variables
	
}