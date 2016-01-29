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

package nz.govt.natlib.ndha.manualdeposit.bulkupload;

import java.awt.Cursor;
import java.awt.Font;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import nz.govt.natlib.ndha.common.guiutilities.FormControl;
import nz.govt.natlib.ndha.manualdeposit.FormUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author __USER__
 */
public final class BulkUploadForm extends javax.swing.JDialog implements
		IBulkUpload {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5805942318541102471L;
	private final static Log LOG = LogFactory.getLog(BulkUploadForm.class);
	// Can't replace with a local variable as it wouldn't work then
	@SuppressWarnings("unused")
	private FormControl frmControl; // NOPMD
	private final String theSettingsPath;
	private BulkUploadPresenter bulkPresenter;
	final private JPanel glass;

	public static BulkUploadForm create(final java.awt.Frame parent,
			final boolean modal, final String settingsPath) {
		return new BulkUploadForm(parent, modal, settingsPath);
	}

	/** Creates new form BulkUploadForm */
	public BulkUploadForm(final java.awt.Frame parent, final boolean modal,
			final String settingsPath) {
		super(parent, modal);
		initComponents();
		theSettingsPath = settingsPath;
		glass = (JPanel) this.getGlassPane();
		this.setProgressVisible(false);
		setCanClose(true);
	}

	public void showView() {
		setVisible(true);
	}

	public void setCanClose(final boolean canClose) {
		if (canClose) {
			this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		} else {
			this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		}
	}

	public void showGlassPane(final boolean show) {
		glass.setVisible(show);
	}

	public void setPresenter(final BulkUploadPresenter presenter) {
		bulkPresenter = presenter;
		bulkPresenter.addJobListTableModelAndHandlers(tblJobList, lblNoOfFiles);
		chkButtons();
	}

	public void setStatus(final String statusMessage) {
		lblStatusMessage.setText(statusMessage);
	}

	public void setMaxProgress(final int max) {
		progress.setMaximum(max);
	}

	public void setCurrentProgress(final int current) {
		progress.setValue(current);
	}

	public void setProgressVisible(final boolean isVisible) {
		progress.setVisible(isVisible);
	}

	public void closeForm() {
		this.setVisible(false);
	}

	public void chkButtons() {
		btnClearSubmittedJobs
				.setEnabled(bulkPresenter.getCompletedJobCount() > 0);
		btnSubmit.setEnabled(!bulkPresenter.jobsRunning());
		chkIgnoreExistingDPS.setEnabled(!bulkPresenter.jobsRunning());
	}

	public void setFormFont(final Font theFont) {
		FormUtilities.setFormFont(this, theFont);
	}

	public void showError(final String header, final String message) {
		showError(header, message, null);
	}

	public void showError(final String header, final String message,
			final Exception ex) {
		final StringWriter prompt = new StringWriter();
		if (message != null) {
			prompt.append(message);
			prompt.append("\n");
		}
		if (ex != null) {
			LOG.error(message, ex);
			if (ex.getMessage() != null) {
				prompt.append(ex.getMessage());
				prompt.append("\n");
			}
			final StringWriter writer = new StringWriter();
			ex.printStackTrace(new PrintWriter(writer));
			prompt.append(writer.toString());
		}
		JOptionPane.showMessageDialog(this, prompt.toString(), header,
				JOptionPane.ERROR_MESSAGE);
	}

	public void showMessage(final String header, final String message) {
		JOptionPane.showMessageDialog(this, message, header,
				JOptionPane.INFORMATION_MESSAGE);
	}

	public boolean confirm(final String message) {
		return confirm(message, false);
	}

	public boolean confirm(final String message, final boolean useYesNo) {
		int optionType;
		if (useYesNo) {
			optionType = JOptionPane.YES_NO_OPTION;
		} else {
			optionType = JOptionPane.OK_CANCEL_OPTION;
		}
		return (JOptionPane.showConfirmDialog(this, message, "Please Confirm",
				optionType) == JOptionPane.OK_OPTION);
	}

	public void setWaitCursor(final boolean isWaiting) {
		glass.setVisible(isWaiting);
		if (isWaiting) {
			final Cursor hourglass = new Cursor(Cursor.WAIT_CURSOR);
			setCursor(hourglass);
		} else {
			final Cursor normal = new Cursor(Cursor.DEFAULT_CURSOR);
			setCursor(normal);
		}
	}

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

		scrlJobList = new javax.swing.JScrollPane();
		tblJobList = new javax.swing.JTable();
		pnlButtons = new javax.swing.JPanel();
		btnSubmit = new javax.swing.JButton();
		btnClearSubmittedJobs = new javax.swing.JButton();
		jLabel1 = new javax.swing.JLabel();
		chkIgnoreExistingDPS = new javax.swing.JCheckBox();
		lblNoOfFiles = new javax.swing.JLabel();
		btnCancelLoad = new javax.swing.JButton();
		lblStatusMessage = new javax.swing.JLabel();
		progress = new javax.swing.JProgressBar();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Bulk Upload Manager");
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowOpened(java.awt.event.WindowEvent evt) {
				formWindowOpened(evt);
			}

			public void windowClosing(java.awt.event.WindowEvent evt) {
				formWindowClosing(evt);
			}
		});

		tblJobList.setModel(new javax.swing.table.DefaultTableModel(
				new Object[][] { { null, null, null, null },
						{ null, null, null, null }, { null, null, null, null },
						{ null, null, null, null } }, new String[] { "Title 1",
						"Title 2", "Title 3", "Title 4" }));
		scrlJobList.setViewportView(tblJobList);

		btnSubmit.setText("Submit Jobs");
		btnSubmit.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnSubmitActionPerformed(evt);
			}
		});

		btnClearSubmittedJobs.setText("Clear Deposited Jobs");
		btnClearSubmittedJobs
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						btnClearSubmittedJobsActionPerformed(evt);
					}
				});

		jLabel1.setText("Skip DPS query");

		chkIgnoreExistingDPS
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						chkIgnoreExistingDPSActionPerformed(evt);
					}
				});

		lblNoOfFiles.setText("Total no of files: 5");

		btnCancelLoad.setText("Cancel Bulk Load");
		btnCancelLoad.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnCancelLoadActionPerformed(evt);
			}
		});

		lblStatusMessage
				.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);

		javax.swing.GroupLayout pnlButtonsLayout = new javax.swing.GroupLayout(
				pnlButtons);
		pnlButtons.setLayout(pnlButtonsLayout);
		pnlButtonsLayout
				.setHorizontalGroup(pnlButtonsLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								pnlButtonsLayout
										.createSequentialGroup()
										.addGroup(
												pnlButtonsLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																pnlButtonsLayout
																		.createSequentialGroup()
																		.addContainerGap()
																		.addComponent(
																				btnSubmit)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																		.addComponent(
																				btnClearSubmittedJobs)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																				190,
																				Short.MAX_VALUE)
																		.addComponent(
																				lblNoOfFiles)
																		.addGap(
																				18,
																				18,
																				18)
																		.addComponent(
																				jLabel1)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																		.addComponent(
																				chkIgnoreExistingDPS))
														.addGroup(
																pnlButtonsLayout
																		.createSequentialGroup()
																		.addGap(
																				56,
																				56,
																				56)
																		.addComponent(
																				btnCancelLoad)
																		.addGap(
																				18,
																				18,
																				18)
																		.addComponent(
																				lblStatusMessage,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				297,
																				Short.MAX_VALUE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				progress,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.PREFERRED_SIZE)))
										.addContainerGap()));
		pnlButtonsLayout
				.setVerticalGroup(pnlButtonsLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								pnlButtonsLayout
										.createSequentialGroup()
										.addGroup(
												pnlButtonsLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																chkIgnoreExistingDPS)
														.addComponent(jLabel1)
														.addComponent(btnSubmit)
														.addComponent(
																btnClearSubmittedJobs)
														.addComponent(
																lblNoOfFiles))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)
										.addGroup(
												pnlButtonsLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																lblStatusMessage,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																23,
																Short.MAX_VALUE)
														.addGroup(
																javax.swing.GroupLayout.Alignment.TRAILING,
																pnlButtonsLayout
																		.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.LEADING)
																		.addComponent(
																				btnCancelLoad)
																		.addComponent(
																				progress,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.PREFERRED_SIZE)))
										.addContainerGap()));

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				pnlButtons, javax.swing.GroupLayout.DEFAULT_SIZE,
				javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(scrlJobList,
						javax.swing.GroupLayout.DEFAULT_SIZE, 650,
						Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup().addComponent(scrlJobList,
						javax.swing.GroupLayout.DEFAULT_SIZE, 400,
						Short.MAX_VALUE).addPreferredGap(
						javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(pnlButtons,
								javax.swing.GroupLayout.PREFERRED_SIZE, 58,
								javax.swing.GroupLayout.PREFERRED_SIZE)));

		pack();
   }// </editor-fold>//GEN-END:initComponents

	private void btnCancelLoadActionPerformed(
			final java.awt.event.ActionEvent evt) {
		bulkPresenter.cancelLoad();
	}

	private void formWindowClosing(final java.awt.event.WindowEvent evt) {
		bulkPresenter.closeDown();
	}

	private void btnClearSubmittedJobsActionPerformed(
			final java.awt.event.ActionEvent evt) {
		bulkPresenter.clearCompletedJobs();
		chkButtons();
	}

	private void chkIgnoreExistingDPSActionPerformed(
			final java.awt.event.ActionEvent evt) {
		bulkPresenter.setQueryDPS(!chkIgnoreExistingDPS.isSelected());
		chkButtons();
	}

	private void btnSubmitActionPerformed(final java.awt.event.ActionEvent evt) {
		bulkPresenter.submitJobs();
		chkButtons();
	}

	private void formWindowOpened(final java.awt.event.WindowEvent evt) {
		try {
			frmControl = FormControl.create(this, theSettingsPath);
		} catch (Exception ex) {
			LOG.error("Error loading form parameters", ex);
		}
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton btnCancelLoad;
	private javax.swing.JButton btnClearSubmittedJobs;
	private javax.swing.JButton btnSubmit;
	private javax.swing.JCheckBox chkIgnoreExistingDPS;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel lblNoOfFiles;
	private javax.swing.JLabel lblStatusMessage;
	private javax.swing.JPanel pnlButtons;
	private javax.swing.JProgressBar progress;
	private javax.swing.JScrollPane scrlJobList;
	private javax.swing.JTable tblJobList;
	// End of variables declaration//GEN-END:variables
	
}