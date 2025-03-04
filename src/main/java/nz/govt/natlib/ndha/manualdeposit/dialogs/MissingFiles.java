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

package nz.govt.natlib.ndha.manualdeposit.dialogs;

import java.util.List;

import javax.swing.DefaultListModel;

import nz.govt.natlib.ndha.common.guiutilities.FormControl;
import nz.govt.natlib.ndha.common.mets.FileSystemObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author __USER__
 */
public class MissingFiles extends javax.swing.JDialog {

	private static final long serialVersionUID = 987531731032854120L;
	private final static Log LOG = LogFactory.getLog(MissingFiles.class);
	private final List<FileSystemObject> theMissingFiles;
	@SuppressWarnings("unused")
	private FormControl theFormControl;
	private final String theSettingsPath;

	/** Creates new form MissingFiles */
	public MissingFiles(final java.awt.Frame parent, final boolean modal,
			final String settingsPath, final List<FileSystemObject> missingFiles) {
		super(parent, modal);
		initComponents();
		theSettingsPath = settingsPath;
		theMissingFiles = missingFiles;
		showMissingFiles();
	}

	private void showMissingFiles() {
		final DefaultListModel model = new DefaultListModel();
		lstMissingFiles.setModel(model);
		for (FileSystemObject fso : theMissingFiles) {
			model.addElement(fso.getFullPath());
		}
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {

		scrlMissingFiles = new javax.swing.JScrollPane();
		lstMissingFiles = new javax.swing.JList();
		btnOkay = new javax.swing.JButton();
		jLabel1 = new javax.swing.JLabel();

		setTitle("Missing Files");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowOpened(final java.awt.event.WindowEvent evt) {
				formWindowOpened(evt);
			}
		});

		scrlMissingFiles.setViewportView(lstMissingFiles);

		btnOkay.setText("Ok");
		btnOkay.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnOkayActionPerformed(evt);
			}
		});

		jLabel1
				.setText("The following files are listed in the checksum digest but are missing:");

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
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
																javax.swing.GroupLayout.Alignment.TRAILING)
														.addComponent(
																scrlMissingFiles,
																javax.swing.GroupLayout.Alignment.LEADING,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																380,
																Short.MAX_VALUE)
														.addComponent(
																btnOkay,
																javax.swing.GroupLayout.Alignment.LEADING,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																380,
																Short.MAX_VALUE)
														.addComponent(
																jLabel1,
																javax.swing.GroupLayout.Alignment.LEADING))
										.addContainerGap()));
		layout
				.setVerticalGroup(layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								javax.swing.GroupLayout.Alignment.TRAILING,
								layout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(jLabel1)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												scrlMissingFiles,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												240, Short.MAX_VALUE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(btnOkay)
										.addContainerGap()));

		pack();
	}// </editor-fold>

	// GEN-END:initComponents

	private void formWindowOpened(final java.awt.event.WindowEvent evt) {
		try {
			theFormControl = new FormControl(this, theSettingsPath);
		} catch (Exception ex) {
			LOG.error("Error loading form parameters", ex);
		}
	}

	private void btnOkayActionPerformed(final java.awt.event.ActionEvent evt) {
		this.setVisible(false);
	}

	// GEN-BEGIN:variables
	// Variables declaration - do not modify
	private javax.swing.JButton btnOkay;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JList lstMissingFiles;
	private javax.swing.JScrollPane scrlMissingFiles;
	// End of variables declaration//GEN-END:variables

}