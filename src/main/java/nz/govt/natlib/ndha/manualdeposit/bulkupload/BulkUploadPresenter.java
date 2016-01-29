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

import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import nz.govt.natlib.ndha.manualdeposit.AppProperties;
import nz.govt.natlib.ndha.manualdeposit.ManualDepositPresenter;
import nz.govt.natlib.ndha.manualdeposit.customui.TableRenderer;
import nz.govt.natlib.ndha.manualdeposit.exceptions.BulkLoadException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class BulkUploadPresenter {

	private final IBulkUpload uploadFrame;
	private final AppProperties theAppProperties;
	private JTable theJobListTable;
	private JLabel noOfFilesLabel;
	private BulkUploadQueueManagement bulkUploadManager;
	private final ManualDepositPresenter manualDepositParent;
	private boolean cancelTheLoad = false;
	private boolean loadingFiles = false;
	private final static Log LOG = LogFactory.getLog(BulkUploadPresenter.class);

	public static BulkUploadPresenter create(final IBulkUpload frame,
			final ManualDepositPresenter parent,
			final AppProperties appProperties) {
		return new BulkUploadPresenter(frame, parent, appProperties);
	}

	public BulkUploadPresenter(final IBulkUpload frame,
			final ManualDepositPresenter parent,
			final AppProperties appProperties) {
		uploadFrame = frame;
		theAppProperties = appProperties;
		manualDepositParent = parent;
		uploadFrame.setPresenter(this);
		setNoOfFiles();
	}

	public void setLoadingFiles(final boolean isLoading) {
		loadingFiles = isLoading;
		uploadFrame.setCanClose(!loadingFiles);
	}

	public boolean isLoadingFiles() {
		return loadingFiles;
	}

	public void showBulkUploads() {
		uploadFrame.showView();
		setNoOfFiles();
	}

	public int getCompletedJobCount() {
		if (bulkUploadManager == null) {
			return 0;
		} else {
			return bulkUploadManager.getCompletedJobCount();
		}
	}

	public void checkButtons() {
		uploadFrame.chkButtons();
	}

	public void setStatus(final String statusMessage) {
		uploadFrame.setStatus(statusMessage);
	}

	public void setMaxProgress(final int max) {
		uploadFrame.setMaxProgress(max);
	}

	public void setCurrentProgress(final int current) {
		uploadFrame.setCurrentProgress(current);
	}

	public void setProgressVisible(final boolean isVisible) {
		uploadFrame.setProgressVisible(isVisible);
	}

	public void setNoOfFiles() {
		final int noOfFiles = bulkUploadManager.getNoOfFiles();
		noOfFilesLabel.setText(String
				.format("Total no of files: %d", noOfFiles));
	}

	public void addBulkUpload(final BulkUploadItem item)
			throws BulkLoadException {
		// _bulkUploadManager should be set when the handlers are initialised
		if (bulkUploadManager == null) {
			throw new BulkLoadException("Bulk upload manager not set");
		}
		bulkUploadManager.addBulkUploadItem(item);
	}

	public void setQueryDPS(final Boolean queryDPS) {
		bulkUploadManager.setCheckDPS(queryDPS);
	}

	public boolean bulkLoadCancelled() {
		return cancelTheLoad;
	}

	public void cancelLoad() {
		if (uploadFrame.confirm("Do you wish to cancel this bulk load?")) {
			cancelTheLoad = true;
			try {
				bulkUploadManager.cancelJobs(false);
			} catch (BulkLoadException ex) {
				String message = "Error cancelling jobs";
				LOG.error(message, ex);
				uploadFrame.showError("An error occurred", message, ex);
			}
			closeDown();
			uploadFrame.closeForm();
		}
	}

	private void setupColumns() {
		final TableRenderer renderer = new TableRenderer();
		final int width = 50;
		for (int i = 0; i < theJobListTable.getColumnCount(); i++) {
			final TableColumn col = theJobListTable.getColumnModel().getColumn(
					i);
			col.setCellRenderer(renderer);
			col.setResizable(true);
			if (i < (theJobListTable.getColumnCount() - 1)) {
				col.setPreferredWidth(width);
				col.setWidth(width);
			}
		}
	}

	public void addJobListTableModelAndHandlers(final JTable jobListTable,
			final JLabel lblNoOfFiles) {
		theJobListTable = jobListTable;
		noOfFilesLabel = lblNoOfFiles;
		final BulkUploadTableModel resultsTableModel = new BulkUploadTableModel();
		theJobListTable.setModel(resultsTableModel);
		theJobListTable.setSurrendersFocusOnKeystroke(true);
		theJobListTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		theJobListTable
				.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		theJobListTable.setColumnSelectionAllowed(false);
		theJobListTable.setRowSelectionAllowed(true);
		setupColumns();
		final ListSelectionModel listSelectionModel = theJobListTable
				.getSelectionModel();
		listSelectionModel
				.addListSelectionListener(new ListSelectionListener() {
					public void valueChanged(final ListSelectionEvent e) {
						tableRowSelected(e);
					}
				});
		theJobListTable.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(final java.awt.event.MouseEvent evt) {
				tableMouseClicked(evt);
			}

			public void mouseReleased(final java.awt.event.MouseEvent evt) {
				tblJobListMouseReleased(evt);
			}
		});
		try {
			bulkUploadManager = BulkUploadQueueManagement.create(
					theAppProperties, theJobListTable, uploadFrame,
					manualDepositParent, this);
		} catch (Exception ex) {
			uploadFrame
					.showError(
							"Error creating bulk upload manager",
							" An error occurred while creating the bulk upload manager",
							ex);
		}
	}

	private void tableRowSelected(final ListSelectionEvent e) {
	}

	private void tblJobListMouseReleased(final java.awt.event.MouseEvent evt) {
		if (evt.isPopupTrigger()) {
			if (theJobListTable.getSelectedRowCount() == 0) {
				final int row = theJobListTable.rowAtPoint(evt.getPoint());
				if (row > -1) {
					theJobListTable.setRowSelectionInterval(row, row);
				}
			}
			final JPopupMenu menu = bulkUploadManager.getBulkUploadMenu();
			if (menu != null) {
				menu.show(evt.getComponent(), evt.getX(), evt.getY());
			}

		}
	}

	private void tableMouseClicked(final java.awt.event.MouseEvent evt) {
		if (evt.isPopupTrigger()) {
			if (theJobListTable.getSelectedRowCount() == 0) {
				final int row = theJobListTable.rowAtPoint(evt.getPoint());
				if (row > -1) {
					theJobListTable.setRowSelectionInterval(row, row);
				}
			}
			final JPopupMenu menu = bulkUploadManager.getBulkUploadMenu();
			if (menu != null) {
				menu.show(evt.getComponent(), evt.getX(), evt.getY());
			}

		}
	}

	public void clearCompletedJobs() {
		bulkUploadManager.clearCompletedJobs();
		setNoOfFiles();
	}

	public boolean jobsRunning() {
		if (bulkUploadManager == null) {
			return false;
		} else {
			return bulkUploadManager.isRunning();
		}
	}

	public void closeDown() {
		bulkUploadManager.closeDown();
	}

	public void jobsFinished() {
		uploadFrame.chkButtons();
	}

	public void submitJobs() {
		bulkUploadManager.startRunning();
	}
}
