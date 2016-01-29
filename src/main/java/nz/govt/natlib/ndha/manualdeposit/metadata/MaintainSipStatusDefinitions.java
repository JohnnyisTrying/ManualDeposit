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

import nz.govt.natlib.ndha.common.exlibris.SIPStatus;
import nz.govt.natlib.ndha.common.guiutilities.FormControl;

public class MaintainSipStatusDefinitions extends javax.swing.JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2057321331896491162L;
	private MetaDataConfiguratorPresenter thePresenter;
	private FormControl formControl;
	private SIPStatus currentStatus;
	private boolean isSystemUpdate = false;

	/** Creates new form MaintainSipStatusDefinitions */
	public MaintainSipStatusDefinitions(
			MetaDataConfiguratorPresenter presenter, FormControl control) {
		initComponents();
		thePresenter = presenter;
		thePresenter.addSipStatusHandlers(lstStatii);
		formControl = control;
	}

	private void loadSipStatusData(boolean isCancel) {
		String configName = "SipStatusDataForm";
		String sipFile = "";
		if (formControl != null) {
			sipFile = formControl.getExtra(configName, "");
		}
		try {
			sipFile = thePresenter.loadSipStatusFile(sipFile, isCancel);
			formControl.setExtra(configName, sipFile);
			lblFileName.setText(sipFile);
		} catch (Exception ex) {
			showError("Error loading file", "Could not open specified file\n"
					+ ex.getMessage());
		}
	}

	public void showSipStatus(SIPStatus status) {
		isSystemUpdate = true;
		currentStatus = status;
		txtStatus.setText(currentStatus.getStatus());
		txtDescription.setText(currentStatus.getDescription());
		chkCleanUp.setSelected(currentStatus.isNeedsTidying());
		chkSuccess.setSelected(currentStatus.isSuccessState());
		chkFailure.setSelected(currentStatus.isFailureState());
		isSystemUpdate = false;
	}

	private void updateStatus() {
		if (!isSystemUpdate) {
			currentStatus.setStatus(txtStatus.getText());
			currentStatus.setDescription(txtDescription.getText());
			currentStatus.setIsSuccessState(chkSuccess.isSelected());
			currentStatus.setIsFailureState(chkFailure.isSelected());
			currentStatus.setNeedsTidying(chkCleanUp.isSelected());
			thePresenter.updateSipStatus(currentStatus);
		}
	}

	private void saveSipStatusData() {
		try {
			thePresenter.saveSipStatusFile();
			showMessage("File saved", "SIP Status file has been saved");
		} catch (Exception ex) {
			showError("Error saving file", "Could not save SIP Status file\n"
					+ ex.getMessage());
		}
	}

	private void showMessage(String header, String message) {
		JOptionPane.showMessageDialog(this, message, header,
				JOptionPane.INFORMATION_MESSAGE);
	}

	private void showError(String header, String message) {
		JOptionPane.showMessageDialog(this, message, header,
				JOptionPane.ERROR_MESSAGE);
	}

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

		btnSelectApplicationFile = new javax.swing.JButton();
		lblFileName = new javax.swing.JLabel();
		scrlStatusList = new javax.swing.JScrollPane();
		lstStatii = new javax.swing.JList();
		txtStatus = new javax.swing.JTextField();
		txtDescription = new javax.swing.JTextField();
		chkSuccess = new javax.swing.JCheckBox();
		chkFailure = new javax.swing.JCheckBox();
		chkCleanUp = new javax.swing.JCheckBox();
		btnAddNew = new javax.swing.JButton();
		btnDelete = new javax.swing.JButton();
		jLabel1 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		jLabel3 = new javax.swing.JLabel();
		jLabel4 = new javax.swing.JLabel();
		btnSave = new javax.swing.JButton();
		btnCancel = new javax.swing.JButton();
		jLabel5 = new javax.swing.JLabel();

		btnSelectApplicationFile.setText("Select SIP Status Definitions File");
		btnSelectApplicationFile
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						btnSelectApplicationFileActionPerformed(evt);
					}
				});

		lblFileName.setText(" ");

		scrlStatusList.setViewportView(lstStatii);

		txtStatus.addCaretListener(new javax.swing.event.CaretListener() {
			public void caretUpdate(javax.swing.event.CaretEvent evt) {
				txtStatusCaretUpdate(evt);
			}
		});

		txtDescription.addCaretListener(new javax.swing.event.CaretListener() {
			public void caretUpdate(javax.swing.event.CaretEvent evt) {
				txtDescriptionCaretUpdate(evt);
			}
		});

		chkSuccess.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				chkSuccessActionPerformed(evt);
			}
		});

		chkFailure.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				chkFailureActionPerformed(evt);
			}
		});

		chkCleanUp.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				chkCleanUpActionPerformed(evt);
			}
		});

		btnAddNew.setText("Add New");
		btnAddNew.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnAddNewActionPerformed(evt);
			}
		});

		btnDelete.setText("Delete");
		btnDelete.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnDeleteActionPerformed(evt);
			}
		});

		jLabel1.setText("Status");

		jLabel2.setText("Success indicator");

		jLabel3.setText("Initiates clean up");

		jLabel4.setText("Description");

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

		jLabel5.setText("Failure indicator");

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout
				.setHorizontalGroup(layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								layout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING,
																false)
														.addGroup(
																layout
																		.createSequentialGroup()
																		.addComponent(
																				btnAddNew)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				Short.MAX_VALUE)
																		.addComponent(
																				btnDelete))
														.addComponent(
																scrlStatusList,
																javax.swing.GroupLayout.Alignment.TRAILING)
														.addComponent(
																btnSelectApplicationFile,
																javax.swing.GroupLayout.Alignment.TRAILING,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																lblFileName,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																278,
																Short.MAX_VALUE)
														.addGroup(
																layout
																		.createSequentialGroup()
																		.addComponent(
																				btnSave)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																				152,
																				Short.MAX_VALUE)
																		.addComponent(
																				btnCancel))
														.addGroup(
																layout
																		.createSequentialGroup()
																		.addGroup(
																				layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addComponent(
																								jLabel5)
																						.addComponent(
																								jLabel1)
																						.addComponent(
																								jLabel2)
																						.addComponent(
																								jLabel3)
																						.addComponent(
																								jLabel4))
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addGroup(
																				layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addGroup(
																								layout
																										.createSequentialGroup()
																										.addPreferredGap(
																												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																										.addGroup(
																												layout
																														.createParallelGroup(
																																javax.swing.GroupLayout.Alignment.LEADING)
																														.addComponent(
																																txtDescription,
																																javax.swing.GroupLayout.DEFAULT_SIZE,
																																192,
																																Short.MAX_VALUE)
																														.addComponent(
																																txtStatus,
																																javax.swing.GroupLayout.DEFAULT_SIZE,
																																192,
																																Short.MAX_VALUE)
																														.addComponent(
																																chkSuccess)))
																						.addComponent(
																								chkCleanUp)
																						.addComponent(
																								chkFailure))))
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
																btnSelectApplicationFile)
														.addComponent(
																lblFileName))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																layout
																		.createSequentialGroup()
																		.addComponent(
																				scrlStatusList,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				347,
																				Short.MAX_VALUE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																		.addGroup(
																				layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.BASELINE)
																						.addComponent(
																								btnAddNew)
																						.addComponent(
																								btnDelete)))
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
																								txtStatus,
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
																								jLabel4)
																						.addComponent(
																								txtDescription,
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
																								chkSuccess))
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addGroup(
																				layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.BASELINE)
																						.addComponent(
																								jLabel5)
																						.addComponent(
																								chkFailure))
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addGroup(
																				layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.BASELINE)
																						.addComponent(
																								jLabel3)
																						.addComponent(
																								chkCleanUp))
																		.addGap(
																				27,
																				27,
																				27)
																		.addGroup(
																				layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.BASELINE)
																						.addComponent(
																								btnSave)
																						.addComponent(
																								btnCancel))))
										.addContainerGap()));
   }// </editor-fold>//GEN-END:initComponents

	private void chkFailureActionPerformed(java.awt.event.ActionEvent evt) {
		updateStatus();
	}

	private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {
		this.loadSipStatusData(true);
	}

	private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {
		saveSipStatusData();
	}

	private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {
		thePresenter.deleteSipStatus();
	}

	private void btnAddNewActionPerformed(java.awt.event.ActionEvent evt) {
		thePresenter.addSipStatus();
	}

	private void chkCleanUpActionPerformed(java.awt.event.ActionEvent evt) {
		updateStatus();
	}

	private void chkSuccessActionPerformed(java.awt.event.ActionEvent evt) {
		updateStatus();
	}

	private void txtDescriptionCaretUpdate(javax.swing.event.CaretEvent evt) {
		updateStatus();
	}

	private void txtStatusCaretUpdate(javax.swing.event.CaretEvent evt) {
		updateStatus();
	}

	private void btnSelectApplicationFileActionPerformed(
			java.awt.event.ActionEvent evt) {
		loadSipStatusData(false);
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton btnAddNew;
	private javax.swing.JButton btnCancel;
	private javax.swing.JButton btnDelete;
	private javax.swing.JButton btnSave;
	private javax.swing.JButton btnSelectApplicationFile;
	private javax.swing.JCheckBox chkCleanUp;
	private javax.swing.JCheckBox chkFailure;
	private javax.swing.JCheckBox chkSuccess;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JLabel lblFileName;
	private javax.swing.JList lstStatii;
	private javax.swing.JScrollPane scrlStatusList;
	private javax.swing.JTextField txtDescription;
	private javax.swing.JTextField txtStatus;
	// End of variables declaration//GEN-END:variables
	
}