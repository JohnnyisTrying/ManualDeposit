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

package nz.govt.natlib.ndha.manualdeposit.metadata;

import javax.swing.JOptionPane;

import nz.govt.natlib.ndha.common.guiutilities.FormControl;

public class MaintainUsers extends javax.swing.JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5224023551844514521L;
	private MetaDataConfiguratorPresenter thePresenter;
	private FormControl formControl;
	private IndigoUser currentUser;
	private boolean isSystemUpdate = false;

	/** Creates new form MaintainUsers */
	public MaintainUsers(MetaDataConfiguratorPresenter presenter,
			FormControl control) {
		initComponents();
		thePresenter = presenter;
		formControl = control;
		thePresenter.addUserHandlers(lstUsers);
	}

	private void saveUserData() {
		try {
			thePresenter.saveUsersFile();
			showMessage("File saved", "Users file has been saved");
		} catch (Exception ex) {
			showError("Error saving file", "Could not save users file\n"
					+ ex.getMessage());
		}
	}

	private void updateUser() {
		if (!isSystemUpdate) {
			currentUser.setUserName(txtUserName.getText());
			currentUser.setUserGroupDataFile(txtMetaDataFile.getText());
			currentUser.setAllowBulkLoad(chkAllowBulkLoad.isSelected());
			thePresenter.updateUser(currentUser);
		}
	}

	private void loadUserData(boolean isCancel) {
		String configName = "UserDataForm";
		String userFile = "";
		if (formControl != null) {
			userFile = formControl.getExtra(configName, "");
		}
		try {
			userFile = thePresenter.loadUserFile(userFile, isCancel);
			formControl.setExtra(configName, userFile);
			lblEditingDetails.setText("Editing "
					+ thePresenter.getUserConfigPath());
		} catch (Exception ex) {
			showError("Error loading file", "Could not open specified file\n"
					+ ex.getMessage());
		}
	}

	private void showError(String header, String message) {
		JOptionPane.showMessageDialog(this, message, header,
				JOptionPane.ERROR_MESSAGE);
	}

	private void showMessage(String header, String message) {
		JOptionPane.showMessageDialog(this, message, header,
				JOptionPane.INFORMATION_MESSAGE);
	}

	private boolean confirm(String message) {
		if (JOptionPane.showConfirmDialog(this, message, "Please Confirm",
				JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
			return true;
		} else {
			return false;
		}
	}

	public void showUser(IndigoUser user) {
		currentUser = user;
		isSystemUpdate = true;
		txtMetaDataFile.setText(currentUser.getUserGroupDataFile());
		txtUserName.setText(currentUser.getUserName());
		chkAllowBulkLoad.setSelected(currentUser.isAllowBulkLoad());
		isSystemUpdate = false;
	}

	private void getMetaDataFile() {
		String theFile = thePresenter.getMetaDataFile(formControl.getExtra(
				"MetaDataFile", ""));
		if (theFile != "") {
			txtMetaDataFile.setText(theFile);
			formControl.setExtra("MetaDataFile", theFile);
		}
	}

	private void addUser() {
		try {
			thePresenter.addUser();
		} catch (Exception ex) {
			showError("Error loading user", "Could not display user\n"
					+ ex.getMessage());
		}
	}

	private void deleteUser() {
		if (confirm("Delete user?")) {
			thePresenter.deleteUser();
		}
	}

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		btnSelectUsersFile = new javax.swing.JButton();
		scrlUsers = new javax.swing.JScrollPane();
		lstUsers = new javax.swing.JList();
		jLabel1 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		txtUserName = new javax.swing.JTextField();
		txtMetaDataFile = new javax.swing.JTextField();
		btnBrowse = new javax.swing.JButton();
		btnSave = new javax.swing.JButton();
		btnCancel = new javax.swing.JButton();
		btnNew = new javax.swing.JButton();
		btnDelete = new javax.swing.JButton();
		lblEditingDetails = new javax.swing.JLabel();
		chkAllowBulkLoad = new javax.swing.JCheckBox();
		jLabel3 = new javax.swing.JLabel();

		btnSelectUsersFile.setText("Select Users File");
		btnSelectUsersFile
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						btnSelectUsersFileActionPerformed(evt);
					}
				});

		scrlUsers.setViewportView(lstUsers);

		jLabel1.setText("User Name");

		jLabel2.setText("Group Meta Data File");

		txtUserName.addCaretListener(new javax.swing.event.CaretListener() {
			public void caretUpdate(javax.swing.event.CaretEvent evt) {
				txtUserNameCaretUpdate(evt);
			}
		});

		txtMetaDataFile.addCaretListener(new javax.swing.event.CaretListener() {
			public void caretUpdate(javax.swing.event.CaretEvent evt) {
				txtMetaDataFileCaretUpdate(evt);
			}
		});

		btnBrowse.setText("Browse");
		btnBrowse.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnBrowseActionPerformed(evt);
			}
		});

		btnSave.setText("Save");
		btnSave.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnSaveActionPerformed(evt);
			}
		});

		btnCancel.setText("Cancel");
		btnCancel.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnCancelActionPerformed(evt);
			}
		});

		btnNew.setText("New");
		btnNew.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnNewActionPerformed(evt);
			}
		});

		btnDelete.setText("Delete");
		btnDelete.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnDeleteActionPerformed(evt);
			}
		});

		lblEditingDetails.setText("Currently editing new user file");

		chkAllowBulkLoad.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				chkAllowBulkLoadActionPerformed(evt);
			}
		});

		jLabel3.setText("Allow Bulk Load");

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout
				.setHorizontalGroup(layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								javax.swing.GroupLayout.Alignment.TRAILING,
								layout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																layout
																		.createSequentialGroup()
																		.addComponent(
																				btnNew)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																				29,
																				Short.MAX_VALUE)
																		.addComponent(
																				btnDelete))
														.addComponent(
																scrlUsers,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																149,
																Short.MAX_VALUE)
														.addComponent(
																btnSelectUsersFile,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																149,
																Short.MAX_VALUE))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																layout
																		.createSequentialGroup()
																		.addGroup(
																				layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addComponent(
																								jLabel2)
																						.addComponent(
																								jLabel1)
																						.addComponent(
																								jLabel3))
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addGroup(
																				layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addComponent(
																								chkAllowBulkLoad)
																						.addComponent(
																								txtUserName,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								83,
																								javax.swing.GroupLayout.PREFERRED_SIZE)
																						.addGroup(
																								layout
																										.createSequentialGroup()
																										.addComponent(
																												txtMetaDataFile,
																												javax.swing.GroupLayout.DEFAULT_SIZE,
																												359,
																												Short.MAX_VALUE)
																										.addPreferredGap(
																												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																										.addComponent(
																												btnBrowse))
																						.addGroup(
																								layout
																										.createSequentialGroup()
																										.addComponent(
																												btnSave)
																										.addGap(
																												67,
																												67,
																												67)
																										.addComponent(
																												btnCancel))))
														.addComponent(
																lblEditingDetails))
										.addContainerGap()));
		layout
				.setVerticalGroup(layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								layout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																btnSelectUsersFile)
														.addComponent(
																lblEditingDetails))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																layout
																		.createSequentialGroup()
																		.addGroup(
																				layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.BASELINE)
																						.addComponent(
																								jLabel1)
																						.addComponent(
																								txtUserName,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								javax.swing.GroupLayout.PREFERRED_SIZE))
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addGroup(
																				layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.BASELINE)
																						.addComponent(
																								jLabel2)
																						.addComponent(
																								btnBrowse)
																						.addComponent(
																								txtMetaDataFile,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								javax.swing.GroupLayout.PREFERRED_SIZE))
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addGroup(
																				layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.TRAILING)
																						.addComponent(
																								jLabel3)
																						.addComponent(
																								chkAllowBulkLoad))
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																				149,
																				Short.MAX_VALUE)
																		.addGroup(
																				layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.BASELINE)
																						.addComponent(
																								btnSave)
																						.addComponent(
																								btnCancel)))
														.addGroup(
																layout
																		.createSequentialGroup()
																		.addComponent(
																				scrlUsers,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				210,
																				Short.MAX_VALUE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																		.addGroup(
																				layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.BASELINE)
																						.addComponent(
																								btnNew)
																						.addComponent(
																								btnDelete))))
										.addContainerGap()));
   }// </editor-fold>//GEN-END:initComponents

	private void chkAllowBulkLoadActionPerformed(java.awt.event.ActionEvent evt) {
		updateUser();
	}

	private void txtMetaDataFileCaretUpdate(javax.swing.event.CaretEvent evt) {
		updateUser();
	}

	private void txtUserNameCaretUpdate(javax.swing.event.CaretEvent evt) {
		updateUser();
	}

	private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {
		deleteUser();
	}

	private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {
		addUser();
	}

	private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {
		loadUserData(true);
	}

	private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {
		saveUserData();
	}

	private void btnBrowseActionPerformed(java.awt.event.ActionEvent evt) {
		getMetaDataFile();
	}

	private void btnSelectUsersFileActionPerformed(
			java.awt.event.ActionEvent evt) {
		loadUserData(false);
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton btnBrowse;
	private javax.swing.JButton btnCancel;
	private javax.swing.JButton btnDelete;
	private javax.swing.JButton btnNew;
	private javax.swing.JButton btnSave;
	private javax.swing.JButton btnSelectUsersFile;
	private javax.swing.JCheckBox chkAllowBulkLoad;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel lblEditingDetails;
	private javax.swing.JList lstUsers;
	private javax.swing.JScrollPane scrlUsers;
	private javax.swing.JTextField txtMetaDataFile;
	private javax.swing.JTextField txtUserName;
    // End of variables declaration//GEN-END:variables

}