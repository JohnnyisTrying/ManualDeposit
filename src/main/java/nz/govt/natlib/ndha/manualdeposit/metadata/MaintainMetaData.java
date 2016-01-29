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

public class MaintainMetaData extends javax.swing.JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -210498809897691063L;
	private MetaDataConfiguratorPresenter thePresenter;
	private boolean isSystem = false;
	private FormControl formControl;

	/** Creates new form MaintainMetaData */
	public MaintainMetaData(MetaDataConfiguratorPresenter presenter,
			FormControl control) {
		initComponents(); // NOPMD
		String warningText = "Do NOT delete the two CMS Fields\n"
				+ "This may cause the system to function incorrectly.\n"
				+ "If these fields are not required, then make them invisible";
		jTextArea1.setText(warningText);
		thePresenter = presenter;
		formControl = control;
		thePresenter.addMetaDataHandlers(lstDataList, cmbDataType,
				pnlListItems, lstListItems, lstMapsToCMS, cmbCMSField, lstDNX,
				lstDNXDetail, lstDC, lstDCXsi, lstDCTerms, tabDnxDc);
	}

	public void loadData(IMetaDataTypeExtended theData, String dcOther) {
		// Just the data that isn't available to the presenter
		isSystem = true;
		chkCompulsory.setSelected(theData.getIsCompulsory());
		chkIsCustomizable.setSelected(theData.getIsCustomizable());
		chkVisible.setSelected(theData.getIsVisible());
		chkMultipleRows.setSelected(theData.getAllowsMultipleRows());
		chkSavedWithTemplate.setSelected(theData.getSavedWithTemplate());
		chkSetBySystem.setSelected(theData.getIsSetBySystem());
		chkUploaded.setSelected(theData.getWillBeUploaded());
		cmbDataType.setSelectedItem(theData.getDataType());
		cmbCMSField.setSelectedItem(theData.getCMSFieldName());
		txtDescription.setText(theData.getDataFieldDescription());
		txtFieldName.setText(theData.getDataFieldName());
		txtDCOther.setText(dcOther);
		txtMaximumLength.setText(String
				.format("%d", theData.getMaximumLength()));
		txtDefaultValue.setText(theData.getDefaultValue());
		btnAddListData.setText("Add");
		chkPopulateFromCMS.setSelected(theData.getIsPopulatedFromCMS());
		checkButtons();
		isSystem = false;
	}

	private void updateData() {
		if (!isSystem) {
			int maximumLength = 100;
			try {
				maximumLength = Integer.parseInt(txtMaximumLength.getText());
			} catch (Exception ex) {
			}
			thePresenter.updateData(txtFieldName.getText(), txtDescription
					.getText(), txtDefaultValue.getText(), maximumLength,
					chkCompulsory.isSelected(), chkVisible.isSelected(),
					chkSavedWithTemplate.isSelected(), chkMultipleRows
							.isSelected(), chkSetBySystem.isSelected(),
					chkUploaded.isSelected(), chkPopulateFromCMS.isSelected(),
					txtDCOther.getText(), chkIsCustomizable.isSelected());
			checkButtons();
		}
	}

	public void checkButtons() {
		boolean dataEnabled = (lstDataList.getSelectedValue() != null);
		pnlData.setEnabled(dataEnabled);
		chkCompulsory.setEnabled(dataEnabled);
		chkIsCustomizable.setEnabled(dataEnabled);
		chkVisible.setEnabled(dataEnabled);
		chkMultipleRows.setEnabled(dataEnabled);
		chkSavedWithTemplate.setEnabled(dataEnabled);
		chkSetBySystem.setEnabled(dataEnabled);
		chkUploaded.setEnabled(dataEnabled);
		cmbDataType.setEnabled(dataEnabled);
		txtDescription.setEnabled(dataEnabled);
		txtFieldName.setEnabled(dataEnabled);
		txtDefaultValue.setEnabled(dataEnabled);
		txtMaximumLength.setEnabled(dataEnabled);
		tabDnxDc.setEnabled(dataEnabled);
		lstDNX.setEnabled(dataEnabled);
		lstDNXDetail.setEnabled(dataEnabled);
		lstDC.setEnabled(dataEnabled);
		lstDCXsi.setEnabled(dataEnabled);
		lstListItems.setEnabled(dataEnabled);
		txtDCOther.setEnabled(dataEnabled);
		txtNewListItemDisplay.setEnabled(dataEnabled);
		txtNewListItemValue.setEnabled(dataEnabled);
		chkPopulateFromCMS.setEnabled(dataEnabled);
		btnAddListData.setEnabled(dataEnabled
				&& thePresenter
						.canSaveLookupData(txtNewListItemValue.getText()));
		btnAddNew.setEnabled(!thePresenter.getIsDirty());
		btnCancel.setEnabled(dataEnabled && thePresenter.getIsDirty());
		btnCancelDataItem.setEnabled(dataEnabled
				&& thePresenter
						.canSaveLookupData(txtNewListItemValue.getText()));
		btnDelete.setEnabled(dataEnabled && !thePresenter.getIsDirty());
		btnDeleteDataItem.setEnabled(dataEnabled
				&& thePresenter.canDeleteLookupData());
		btnMoveDown.setEnabled(dataEnabled && thePresenter.canMoveItem(false));
		btnMoveDownListItem.setEnabled(dataEnabled
				&& thePresenter.canMoveLookupItem(false));
		btnMoveUp.setEnabled(dataEnabled && thePresenter.canMoveItem(true));
		btnMoveUpListItem.setEnabled(dataEnabled
				&& thePresenter.canMoveLookupItem(true));
		btnSave.setEnabled(dataEnabled
				&& thePresenter.getIsDirty()
				&& thePresenter.canSave(txtFieldName.getText(), txtDCOther
						.getText(), chkUploaded.isSelected()));
		btnSelectConfigurationFile.setEnabled(!thePresenter.getIsDirty());
		boolean isUploadable = chkUploaded.isSelected();
		lblDnxDc.setVisible(isUploadable);
		lblFieldMapping.setVisible(isUploadable);
		tabDnxDc.setVisible(isUploadable);
		boolean mapsCMS = chkPopulateFromCMS.isSelected();
		cmbCMSField.setEnabled(mapsCMS);
		pnlMapsToCMS.setVisible(mapsCMS);
		boolean listItemSelected = lstListItems.getSelectedValue() != null;
		txtCMSMapping.setEnabled(listItemSelected);
		btnAddCmsMapping.setEnabled(listItemSelected);
		btnDeleteCmsMapping.setEnabled(lstMapsToCMS.getSelectedValue() != null);
		btnCancelCmsMapping.setEnabled(listItemSelected);
	}

	public void editLookupValue(MetaDataListValues value) {
		if (value == null) {
			txtNewListItemDisplay.setText("");
			txtNewListItemValue.setText("");
		} else {
			txtNewListItemDisplay.setText(value.getDisplay());
			txtNewListItemValue.setText(value.getValue());
		}
		btnAddListData.setText("Update");
		checkButtons();
	}

	public void editCmsMappingValue(String value) {
		if (value == null) {
			txtCMSMapping.setText("");
		} else {
			txtCMSMapping.setText(value);
		}
		btnAddCmsMapping.setText("Update");
		checkButtons();
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrlDataList = new javax.swing.JScrollPane();
        lstDataList = new javax.swing.JList();
        btnMoveUp = new javax.swing.JButton();
        btnMoveDown = new javax.swing.JButton();
        pnlButtons = new javax.swing.JPanel();
        btnAddNew = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        btnSelectConfigurationFile = new javax.swing.JButton();
        pnlData = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        lblDnxDc = new javax.swing.JLabel();
        lblFieldMapping = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtFieldName = new javax.swing.JTextField();
        txtDescription = new javax.swing.JTextField();
        cmbDataType = new javax.swing.JComboBox();
        txtDefaultValue = new javax.swing.JTextField();
        txtMaximumLength = new javax.swing.JTextField();
        chkCompulsory = new javax.swing.JCheckBox();
        chkUploaded = new javax.swing.JCheckBox();
        chkVisible = new javax.swing.JCheckBox();
        chkSetBySystem = new javax.swing.JCheckBox();
        chkSavedWithTemplate = new javax.swing.JCheckBox();
        chkMultipleRows = new javax.swing.JCheckBox();
        chkPopulateFromCMS = new javax.swing.JCheckBox();
        cmbCMSField = new javax.swing.JComboBox();
        pnlListItems = new javax.swing.JPanel();
        txtNewListItemValue = new javax.swing.JTextField();
        txtNewListItemDisplay = new javax.swing.JTextField();
        btnAddListData = new javax.swing.JButton();
        btnCancelDataItem = new javax.swing.JButton();
        btnDeleteDataItem = new javax.swing.JButton();
        scrlListItems = new javax.swing.JScrollPane();
        lstListItems = new javax.swing.JList();
        btnMoveUpListItem = new javax.swing.JButton();
        btnMoveDownListItem = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        pnlMapsToCMS = new javax.swing.JPanel();
        lblMapsToCMS = new javax.swing.JLabel();
        scrlMapsToCMS = new javax.swing.JScrollPane();
        lstMapsToCMS = new javax.swing.JList();
        txtCMSMapping = new javax.swing.JTextField();
        btnAddCmsMapping = new javax.swing.JButton();
        btnDeleteCmsMapping = new javax.swing.JButton();
        btnCancelCmsMapping = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        tabDnxDc = new javax.swing.JTabbedPane();
        pnlDNX = new javax.swing.JPanel();
        scrlDNX = new javax.swing.JScrollPane();
        lstDNX = new javax.swing.JList();
        scrlDNXDetail = new javax.swing.JScrollPane();
        lstDNXDetail = new javax.swing.JList();
        pnlDC = new javax.swing.JPanel();
        scrlDC = new javax.swing.JScrollPane();
        lstDC = new javax.swing.JList();
        txtDCOther = new javax.swing.JTextField();
        lblOther = new javax.swing.JLabel();
        pnlDCXsi = new javax.swing.JPanel();
        scrlDCXsi = new javax.swing.JScrollPane();
        lstDCXsi = new javax.swing.JList();
        pnlDCTerms = new javax.swing.JPanel();
        scrlDCTerms = new javax.swing.JScrollPane();
        lstDCTerms = new javax.swing.JList();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        lblCMSField = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        lblCustomizable = new javax.swing.JLabel();
        chkIsCustomizable = new javax.swing.JCheckBox();
        btnSave = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        lblFileName = new javax.swing.JLabel();

        scrlDataList.setViewportView(lstDataList);

        btnMoveUp.setText("Move Up");
        btnMoveUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMoveUpActionPerformed(evt);
            }
        });

        btnMoveDown.setText("Move Down");
        btnMoveDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMoveDownActionPerformed(evt);
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

        javax.swing.GroupLayout pnlButtonsLayout = new javax.swing.GroupLayout(pnlButtons);
        pnlButtons.setLayout(pnlButtonsLayout);
        pnlButtonsLayout.setHorizontalGroup(
            pnlButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlButtonsLayout.createSequentialGroup()
                .addComponent(btnAddNew)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnDelete)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlButtonsLayout.setVerticalGroup(
            pnlButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(btnAddNew)
                .addComponent(btnDelete))
        );

        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        btnSelectConfigurationFile.setText("Select Configuration File");
        btnSelectConfigurationFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectConfigurationFileActionPerformed(evt);
            }
        });

        jLabel7.setText("Set By System?");

        jLabel8.setText("Uploaded To DPS?");

        lblDnxDc.setText("DNX/DC/DC Terms?");

        lblFieldMapping.setText("Field Mapping");

        jLabel2.setText("Description");

        jLabel3.setText("Data Type");

        jLabel4.setLabelFor(chkCompulsory);
        jLabel4.setText("Compulsory?");

        jLabel5.setLabelFor(chkSavedWithTemplate);
        jLabel5.setText("Saved With Template?");

        jLabel6.setText("Allows Multiple Rows?");

        txtFieldName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dataChanged(evt);
            }
        });
        txtFieldName.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                MaintainMetaData.this.caretUpdate(evt);
            }
        });

        txtDescription.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dataChanged(evt);
            }
        });
        txtDescription.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                MaintainMetaData.this.keyTyped(evt);
            }
        });
        txtDescription.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                MaintainMetaData.this.caretUpdate(evt);
            }
        });

        cmbDataType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dataChanged(evt);
            }
        });

        txtDefaultValue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dataChanged(evt);
            }
        });
        txtDefaultValue.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                MaintainMetaData.this.caretUpdate(evt);
            }
        });

        txtMaximumLength.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dataChanged(evt);
            }
        });
        txtMaximumLength.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                MaintainMetaData.this.caretUpdate(evt);
            }
        });

        chkCompulsory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dataChanged(evt);
            }
        });

        chkUploaded.setSelected(true);
        chkUploaded.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dataChanged(evt);
            }
        });

        chkVisible.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dataChanged(evt);
            }
        });

        chkSetBySystem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dataChanged(evt);
            }
        });

        chkSavedWithTemplate.setSelected(true);
        chkSavedWithTemplate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dataChanged(evt);
            }
        });

        chkMultipleRows.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dataChanged(evt);
            }
        });

        chkPopulateFromCMS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dataChanged(evt);
            }
        });

        cmbCMSField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dataChanged(evt);
            }
        });

        pnlListItems.setBorder(javax.swing.BorderFactory.createTitledBorder("List Items"));

        txtNewListItemValue.setToolTipText("If Display value is blank then Upload value will show");
        txtNewListItemValue.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                MaintainMetaData.this.caretUpdate(evt);
            }
        });

        txtNewListItemDisplay.setToolTipText("If Display value is blank then Upload value will show");
        txtNewListItemDisplay.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                MaintainMetaData.this.caretUpdate(evt);
            }
        });

        btnAddListData.setText("Add");
        btnAddListData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddListDataActionPerformed(evt);
            }
        });

        btnCancelDataItem.setText("Cancel");
        btnCancelDataItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelDataItemActionPerformed(evt);
            }
        });

        btnDeleteDataItem.setText("Delete");
        btnDeleteDataItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteDataItemActionPerformed(evt);
            }
        });

        scrlListItems.setViewportView(lstListItems);

        btnMoveUpListItem.setText("Move Up");
        btnMoveUpListItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMoveUpListItemActionPerformed(evt);
            }
        });

        btnMoveDownListItem.setText("Move Down");
        btnMoveDownListItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMoveDownListItemActionPerformed(evt);
            }
        });

        jLabel12.setText("Display Value");

        jLabel13.setText("Upload Value");

        lblMapsToCMS.setText("Maps to CMS values:");

        scrlMapsToCMS.setViewportView(lstMapsToCMS);

        btnAddCmsMapping.setText("Add");
        btnAddCmsMapping.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddCmsMappingActionPerformed(evt);
            }
        });

        btnDeleteCmsMapping.setText("Delete");
        btnDeleteCmsMapping.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteCmsMappingActionPerformed(evt);
            }
        });

        btnCancelCmsMapping.setText("Cancel");
        btnCancelCmsMapping.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelCmsMappingActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlMapsToCMSLayout = new javax.swing.GroupLayout(pnlMapsToCMS);
        pnlMapsToCMS.setLayout(pnlMapsToCMSLayout);
        pnlMapsToCMSLayout.setHorizontalGroup(
            pnlMapsToCMSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMapsToCMSLayout.createSequentialGroup()
                .addComponent(lblMapsToCMS)
                .addContainerGap(121, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlMapsToCMSLayout.createSequentialGroup()
                .addComponent(btnAddCmsMapping)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                .addComponent(btnDeleteCmsMapping)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCancelCmsMapping))
            .addComponent(scrlMapsToCMS, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
            .addComponent(txtCMSMapping, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
        );
        pnlMapsToCMSLayout.setVerticalGroup(
            pnlMapsToCMSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMapsToCMSLayout.createSequentialGroup()
                .addComponent(lblMapsToCMS)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrlMapsToCMS, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtCMSMapping, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addGroup(pnlMapsToCMSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancelCmsMapping)
                    .addComponent(btnAddCmsMapping)
                    .addComponent(btnDeleteCmsMapping)))
        );

        javax.swing.GroupLayout pnlListItemsLayout = new javax.swing.GroupLayout(pnlListItems);
        pnlListItems.setLayout(pnlListItemsLayout);
        pnlListItemsLayout.setHorizontalGroup(
            pnlListItemsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlListItemsLayout.createSequentialGroup()
                .addGap(89, 89, 89)
                .addGroup(pnlListItemsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scrlListItems, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
                    .addComponent(pnlMapsToCMS, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlListItemsLayout.createSequentialGroup()
                        .addComponent(btnAddListData)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                        .addComponent(btnDeleteDataItem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancelDataItem))
                    .addGroup(pnlListItemsLayout.createSequentialGroup()
                        .addGroup(pnlListItemsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlListItemsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNewListItemValue, javax.swing.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
                            .addComponent(txtNewListItemDisplay, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlListItemsLayout.createSequentialGroup()
                        .addComponent(btnMoveUpListItem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 53, Short.MAX_VALUE)
                        .addComponent(btnMoveDownListItem)))
                .addContainerGap())
        );
        pnlListItemsLayout.setVerticalGroup(
            pnlListItemsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlListItemsLayout.createSequentialGroup()
                .addGroup(pnlListItemsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(txtNewListItemValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlListItemsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(txtNewListItemDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlListItemsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancelDataItem)
                    .addComponent(btnAddListData)
                    .addComponent(btnDeleteDataItem))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrlListItems)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlListItemsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnMoveUpListItem)
                    .addComponent(btnMoveDownListItem))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlMapsToCMS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel1.setText("Field Name");

        scrlDNX.setViewportView(lstDNX);

        scrlDNXDetail.setViewportView(lstDNXDetail);

        javax.swing.GroupLayout pnlDNXLayout = new javax.swing.GroupLayout(pnlDNX);
        pnlDNX.setLayout(pnlDNXLayout);
        pnlDNXLayout.setHorizontalGroup(
            pnlDNXLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDNXLayout.createSequentialGroup()
                .addComponent(scrlDNX, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrlDNXDetail, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))
        );
        pnlDNXLayout.setVerticalGroup(
            pnlDNXLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrlDNX, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
            .addComponent(scrlDNXDetail, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
        );

        tabDnxDc.addTab("DNX", pnlDNX);

        lstDC.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstDCValueChanged(evt);
            }
        });
        scrlDC.setViewportView(lstDC);

        txtDCOther.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dataChanged(evt);
            }
        });
        txtDCOther.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                MaintainMetaData.this.caretUpdate(evt);
            }
        });

        lblOther.setText("Other");

        javax.swing.GroupLayout pnlDCLayout = new javax.swing.GroupLayout(pnlDC);
        pnlDC.setLayout(pnlDCLayout);
        pnlDCLayout.setHorizontalGroup(
            pnlDCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrlDC, javax.swing.GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE)
            .addGroup(pnlDCLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblOther)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtDCOther, javax.swing.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE))
        );
        pnlDCLayout.setVerticalGroup(
            pnlDCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlDCLayout.createSequentialGroup()
                .addComponent(scrlDC, javax.swing.GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblOther)
                    .addComponent(txtDCOther, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        tabDnxDc.addTab("DC", pnlDC);

        scrlDCXsi.setPreferredSize(new java.awt.Dimension(258, 130));

        scrlDCXsi.setViewportView(lstDCXsi);

        javax.swing.GroupLayout pnlDCXsiLayout = new javax.swing.GroupLayout(pnlDCXsi);
        pnlDCXsi.setLayout(pnlDCXsiLayout);
        pnlDCXsiLayout.setHorizontalGroup(
            pnlDCXsiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrlDCXsi, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE)
        );
        pnlDCXsiLayout.setVerticalGroup(
            pnlDCXsiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrlDCXsi, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
        );

        tabDnxDc.addTab("DCxsi", pnlDCXsi);

        scrlDCTerms.setViewportView(lstDCTerms);

        javax.swing.GroupLayout pnlDCTermsLayout = new javax.swing.GroupLayout(pnlDCTerms);
        pnlDCTerms.setLayout(pnlDCTermsLayout);
        pnlDCTermsLayout.setHorizontalGroup(
            pnlDCTermsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrlDCTerms, javax.swing.GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE)
        );
        pnlDCTermsLayout.setVerticalGroup(
            pnlDCTermsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrlDCTerms, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
        );

        tabDnxDc.addTab("DC Terms", pnlDCTerms);

        jLabel9.setText("Maximum Length");

        jLabel11.setText("Visible?");

        jLabel16.setText("Default Value");

        jLabel17.setText("Populate from CMS?");

        lblCMSField.setText("CMS Field");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        lblCustomizable.setLabelFor(chkIsCustomizable);
        lblCustomizable.setText("Customizable?");

        chkIsCustomizable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dataChanged(evt);
            }
        });

        javax.swing.GroupLayout pnlDataLayout = new javax.swing.GroupLayout(pnlData);
        pnlData.setLayout(pnlDataLayout);
        pnlDataLayout.setHorizontalGroup(
            pnlDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDataLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDataLayout.createSequentialGroup()
                        .addGroup(pnlDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel1)
                            .addComponent(jLabel9)
                            .addComponent(jLabel11)
                            .addComponent(jLabel17))
                        .addGap(6, 6, 6)
                        .addGroup(pnlDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtFieldName)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlDataLayout.createSequentialGroup()
                                .addGroup(pnlDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(chkCompulsory)
                                    .addGroup(pnlDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(chkVisible)
                                        .addComponent(chkPopulateFromCMS)
                                        .addComponent(chkSavedWithTemplate)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(pnlDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel8)
                                    .addComponent(lblCMSField))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pnlDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cmbCMSField, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(pnlDataLayout.createSequentialGroup()
                                        .addGroup(pnlDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(chkUploaded)
                                            .addComponent(chkSetBySystem)
                                            .addGroup(pnlDataLayout.createSequentialGroup()
                                                .addComponent(chkMultipleRows)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(lblCustomizable)
                                                .addGap(18, 18, 18)
                                                .addComponent(chkIsCustomizable)))
                                        .addGap(0, 0, Short.MAX_VALUE))))
                            .addComponent(cmbDataType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtDescription)
                            .addComponent(txtDefaultValue, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtMaximumLength)))
                    .addComponent(jLabel16)
                    .addGroup(pnlDataLayout.createSequentialGroup()
                        .addGroup(pnlDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblFieldMapping)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblDnxDc, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(tabDnxDc)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlListItems, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlDataLayout.setVerticalGroup(
            pnlDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDataLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlListItems, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlDataLayout.createSequentialGroup()
                        .addGroup(pnlDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtFieldName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(cmbDataType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel16)
                            .addComponent(txtDefaultValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(txtMaximumLength, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jLabel8)
                            .addComponent(chkUploaded)
                            .addComponent(chkCompulsory))
                        .addGap(2, 2, 2)
                        .addGroup(pnlDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(pnlDataLayout.createSequentialGroup()
                                .addGroup(pnlDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel11)
                                    .addComponent(chkVisible)
                                    .addComponent(jLabel7)
                                    .addComponent(chkSetBySystem))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pnlDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel5)
                                    .addComponent(chkSavedWithTemplate)
                                    .addComponent(jLabel6)
                                    .addComponent(chkMultipleRows)))
                            .addGroup(pnlDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(chkIsCustomizable)
                                .addComponent(lblCustomizable)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pnlDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel17)
                            .addComponent(chkPopulateFromCMS)
                            .addComponent(lblCMSField)
                            .addComponent(cmbCMSField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(7, 7, 7)
                        .addGroup(pnlDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlDataLayout.createSequentialGroup()
                                .addComponent(lblDnxDc)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblFieldMapping)
                                .addGap(18, 18, 18)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(tabDnxDc, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))
                .addContainerGap())
        );

        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel10.setText("WARNING");

        jScrollPane1.setBorder(null);

        jTextArea1.setBackground(new java.awt.Color(212, 208, 200));
        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setText("Do NOT delete the two CMS Fields");
        jScrollPane1.setViewportView(jTextArea1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrlDataList, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnMoveUp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 76, Short.MAX_VALUE)
                        .addComponent(btnMoveDown))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlButtons, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(120, 120, 120)
                        .addComponent(btnSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnCancel))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnSelectConfigurationFile)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblFileName)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnSelectConfigurationFile)
                            .addComponent(lblFileName))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(scrlDataList)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnMoveUp)
                        .addComponent(btnMoveDown))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnCancel)
                        .addComponent(btnSave))
                    .addComponent(pnlButtons, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

	private void btnCancelCmsMappingActionPerformed(
			java.awt.event.ActionEvent evt) {
		lstMapsToCMS.clearSelection();
		txtCMSMapping.setText("");
		btnAddCmsMapping.setText("Add");
		checkButtons();
	}

	private void btnDeleteCmsMappingActionPerformed(
			java.awt.event.ActionEvent evt) {
		thePresenter.deleteCmsMappingItem();
		checkButtons();
	}

	private void btnAddCmsMappingActionPerformed(java.awt.event.ActionEvent evt) {
		if (btnAddCmsMapping.getText().equals("Add")) {
			thePresenter.addCmsMappingItem(txtCMSMapping.getText());
		} else {
			thePresenter.updateCmsLookupValue(txtCMSMapping.getText());
		}
		lstMapsToCMS.clearSelection();
		btnAddCmsMapping.setText("Add");
		txtCMSMapping.setText("");
		checkButtons();
	}

	private void lstDCValueChanged(javax.swing.event.ListSelectionEvent evt) {
		if (lstDC.getSelectedValue() != null) {
			txtDCOther.setText("");
		}
	}

	private void caretUpdate(javax.swing.event.CaretEvent evt) {
		updateData();
	}

	private void keyTyped(java.awt.event.KeyEvent evt) {
		updateData();
	}

	private void dataChanged(java.awt.event.ActionEvent evt) {
		updateData();
	}

	private void loadConfig(boolean isCancel) {
		String configName = "MetaDataFile";
		String metaDataFile = "";
		if (formControl != null) {
			metaDataFile = formControl.getExtra(configName, "");
		}
		try {
			metaDataFile = thePresenter.loadConfigurationFile(metaDataFile,
					isCancel);
			formControl.setExtra(configName, metaDataFile);
		} catch (Exception ex) {
			showError("Error loading file", "Could not open specified file\n"
					+ ex.getMessage());
		}
		lblFileName.setText(metaDataFile);
		checkButtons();
	}

	private void showError(String header, String message) {
		JOptionPane.showMessageDialog(this, message, header,
				JOptionPane.ERROR_MESSAGE);
	}

	private void btnSelectConfigurationFileActionPerformed(
			java.awt.event.ActionEvent evt) {
		loadConfig(false);
	}

	private void btnAddListDataActionPerformed(java.awt.event.ActionEvent evt) {
		if (btnAddListData.getText().equals("Add")) {
			thePresenter.addDataLookupItem(txtNewListItemValue.getText(),
					txtNewListItemDisplay.getText());
		} else {
			thePresenter.saveDataLookupItem(txtNewListItemValue.getText(),
					txtNewListItemDisplay.getText());
		}
		lstListItems.clearSelection();
		btnAddListData.setText("Add");
		txtNewListItemDisplay.setText("");
		txtNewListItemValue.setText("");
		checkButtons();
	}

	private void btnCancelDataItemActionPerformed(java.awt.event.ActionEvent evt) {
		lstListItems.clearSelection();
		txtNewListItemDisplay.setText("");
		btnAddListData.setText("Add");
		thePresenter.loadCmsMappingData();
		checkButtons();
	}

	private void btnDeleteDataItemActionPerformed(java.awt.event.ActionEvent evt) {
		thePresenter.deleteLookupItem();
		btnAddListData.setText("Add");
		checkButtons();
	}

	private void btnMoveDownListItemActionPerformed(
			java.awt.event.ActionEvent evt) {
		thePresenter.moveLookupItem(false);
	}

	private void btnMoveUpListItemActionPerformed(java.awt.event.ActionEvent evt) {
		thePresenter.moveLookupItem(true);
	}

	private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {
		this.loadConfig(true);
	}

	private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {
		thePresenter.saveConfigurationFile();
		checkButtons();
	}

	private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {
		thePresenter.deleteItem();
		checkButtons();
	}

	private void btnAddNewActionPerformed(java.awt.event.ActionEvent evt) {
		thePresenter.addNewItem();
		checkButtons();
	}

	private void btnMoveDownActionPerformed(java.awt.event.ActionEvent evt) {
		thePresenter.moveItem(false);
	}

	private void btnMoveUpActionPerformed(java.awt.event.ActionEvent evt) {
		thePresenter.moveItem(true);
	}

	public boolean confirm(String message) {
		if (JOptionPane.showConfirmDialog(this, message, "Please Confirm",
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			return true;
		} else {
			return false;
		}
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddCmsMapping;
    private javax.swing.JButton btnAddListData;
    private javax.swing.JButton btnAddNew;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnCancelCmsMapping;
    private javax.swing.JButton btnCancelDataItem;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnDeleteCmsMapping;
    private javax.swing.JButton btnDeleteDataItem;
    private javax.swing.JButton btnMoveDown;
    private javax.swing.JButton btnMoveDownListItem;
    private javax.swing.JButton btnMoveUp;
    private javax.swing.JButton btnMoveUpListItem;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSelectConfigurationFile;
    private javax.swing.JCheckBox chkCompulsory;
    private javax.swing.JCheckBox chkIsCustomizable;
    private javax.swing.JCheckBox chkMultipleRows;
    private javax.swing.JCheckBox chkPopulateFromCMS;
    private javax.swing.JCheckBox chkSavedWithTemplate;
    private javax.swing.JCheckBox chkSetBySystem;
    private javax.swing.JCheckBox chkUploaded;
    private javax.swing.JCheckBox chkVisible;
    private javax.swing.JComboBox cmbCMSField;
    private javax.swing.JComboBox cmbDataType;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel lblCMSField;
    private javax.swing.JLabel lblCustomizable;
    private javax.swing.JLabel lblDnxDc;
    private javax.swing.JLabel lblFieldMapping;
    private javax.swing.JLabel lblFileName;
    private javax.swing.JLabel lblMapsToCMS;
    private javax.swing.JLabel lblOther;
    private javax.swing.JList lstDC;
    private javax.swing.JList lstDCTerms;
    private javax.swing.JList lstDCXsi;
    private javax.swing.JList lstDNX;
    private javax.swing.JList lstDNXDetail;
    private javax.swing.JList lstDataList;
    private javax.swing.JList lstListItems;
    private javax.swing.JList lstMapsToCMS;
    private javax.swing.JPanel pnlButtons;
    private javax.swing.JPanel pnlDC;
    private javax.swing.JPanel pnlDCTerms;
    private javax.swing.JPanel pnlDCXsi;
    private javax.swing.JPanel pnlDNX;
    private javax.swing.JPanel pnlData;
    private javax.swing.JPanel pnlListItems;
    private javax.swing.JPanel pnlMapsToCMS;
    private javax.swing.JScrollPane scrlDC;
    private javax.swing.JScrollPane scrlDCTerms;
    private javax.swing.JScrollPane scrlDCXsi;
    private javax.swing.JScrollPane scrlDNX;
    private javax.swing.JScrollPane scrlDNXDetail;
    private javax.swing.JScrollPane scrlDataList;
    private javax.swing.JScrollPane scrlListItems;
    private javax.swing.JScrollPane scrlMapsToCMS;
    private javax.swing.JTabbedPane tabDnxDc;
    private javax.swing.JTextField txtCMSMapping;
    private javax.swing.JTextField txtDCOther;
    private javax.swing.JTextField txtDefaultValue;
    private javax.swing.JTextField txtDescription;
    private javax.swing.JTextField txtFieldName;
    private javax.swing.JTextField txtMaximumLength;
    private javax.swing.JTextField txtNewListItemDisplay;
    private javax.swing.JTextField txtNewListItemValue;
    // End of variables declaration//GEN-END:variables

}