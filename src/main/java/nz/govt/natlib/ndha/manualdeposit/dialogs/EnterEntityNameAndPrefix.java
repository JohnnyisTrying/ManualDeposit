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

import java.awt.event.KeyEvent;

import nz.govt.natlib.ndha.common.guiutilities.FormControl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EnterEntityNameAndPrefix extends javax.swing.JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2695355861792387632L;
	private final static Log LOG = LogFactory
			.getLog(EnterEntityNameAndPrefix.class);
	private final String theSettingsPath;
	@SuppressWarnings("unused")
	private FormControl frmControl; // NOPMD Needs to be here for it to work
	private boolean cancelled = false;

	/** Creates new form EnterEntityNameAndPrefix */
	public EnterEntityNameAndPrefix(final javax.swing.JFrame parent,
			final boolean modal, final String settingsPath,
			final String entityName, final String filePrefix) {
		super(parent, modal);
		theSettingsPath = settingsPath;
		initComponents();
		txtEnterEntityName.setText(entityName);
		txtEnterFilePrefix.setText(filePrefix);
	}

	private void cancel() {
		cancelled = true;
		this.setVisible(false);
	}

	private void okay() {
		cancelled = false;
		this.setVisible(false);
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public String getEntityName() {
		return txtEnterEntityName.getText();
	}

	public String getFilePrefix() {
		return txtEnterFilePrefix.getText();
	}

	// GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {

		jLabel1 = new javax.swing.JLabel();
		txtEnterEntityName = new javax.swing.JTextField();
		cmdOK = new javax.swing.JButton();
		jLabel2 = new javax.swing.JLabel();
		txtEnterFilePrefix = new javax.swing.JTextField();
		cmdCancel = new javax.swing.JButton();

		setTitle("Enter Entity Name and File Prefix");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowOpened(java.awt.event.WindowEvent evt) {
				formWindowOpened(evt);
			}
		});

		jLabel1.setText("Enter Entity Name");

		txtEnterEntityName
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						txtEnterEntityNameActionPerformed(evt);
					}
				});
		txtEnterEntityName.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(java.awt.event.KeyEvent evt) {
				txtEnterEntityNameKeyPressed(evt);
			}
		});

		cmdOK.setText("Ok");
		cmdOK.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cmdOKActionPerformed(evt);
			}
		});

		jLabel2.setText("Enter File Prefix");

		txtEnterFilePrefix
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						txtEnterFilePrefixActionPerformed(evt);
					}
				});
		txtEnterFilePrefix.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(java.awt.event.KeyEvent evt) {
				txtEnterFilePrefixKeyPressed(evt);
			}
		});

		cmdCancel.setText("Cancel");
		cmdCancel.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cmdCancelActionPerformed(evt);
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
										.addContainerGap()
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(jLabel1)
														.addComponent(jLabel2))
										.addGap(14, 14, 14)
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																txtEnterEntityName,
																javax.swing.GroupLayout.Alignment.TRAILING,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																279,
																Short.MAX_VALUE)
														.addComponent(
																txtEnterFilePrefix,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																279,
																Short.MAX_VALUE)
														.addGroup(
																layout
																		.createSequentialGroup()
																		.addGap(
																				10,
																				10,
																				10)
																		.addComponent(
																				cmdOK,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				75,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addGap(
																				18,
																				18,
																				18)
																		.addComponent(
																				cmdCancel,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				75,
																				javax.swing.GroupLayout.PREFERRED_SIZE)))
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
														.addComponent(jLabel1)
														.addComponent(
																txtEnterEntityName,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(jLabel2)
														.addComponent(
																txtEnterFilePrefix,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED,
												15, Short.MAX_VALUE)
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(cmdOK)
														.addComponent(cmdCancel))
										.addContainerGap()));

		pack();
	}// </editor-fold>

	// GEN-END:initComponents

	private void txtEnterFilePrefixKeyPressed(java.awt.event.KeyEvent evt) {
		if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
			cancel();
		}
	}

	private void cmdCancelActionPerformed(final java.awt.event.ActionEvent evt) {
		cancel();
	}

	private void cmdOKActionPerformed(final java.awt.event.ActionEvent evt) {
		okay();
	}

	private void txtEnterEntityNameKeyPressed(final java.awt.event.KeyEvent evt) {
		if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
			cancel();
		}
	}

	private void formWindowOpened(final java.awt.event.WindowEvent evt) {
		try {
			frmControl = new FormControl(this, theSettingsPath);
		} catch (Exception ex) {
			LOG.error("Error loading form parameters", ex);
		}
	}

	private void txtEnterFilePrefixActionPerformed(
			final java.awt.event.ActionEvent evt) {
		okay();
	}

	private void txtEnterEntityNameActionPerformed(
			final java.awt.event.ActionEvent evt) {
		okay();
	}

	// GEN-BEGIN:variables
	// Variables declaration - do not modify
	private javax.swing.JButton cmdCancel;
	private javax.swing.JButton cmdOK;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JTextField txtEnterEntityName;
	private javax.swing.JTextField txtEnterFilePrefix;
	// End of variables declaration//GEN-END:variables

}