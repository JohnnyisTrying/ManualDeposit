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

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.Timer;

import nz.govt.natlib.ndha.manualdeposit.AppProperties;
import nz.govt.natlib.ndha.manualdeposit.ManualDepositPresenter;
import nz.govt.natlib.ndha.manualdeposit.bulkupload.BulkUploadItem.JobState;
import nz.govt.natlib.ndha.manualdeposit.exceptions.BulkLoadException;
import nz.govt.natlib.ndha.manualdeposit.exceptions.JobQueueException;
import nz.govt.natlib.ndha.manualdeposit.jobmanagement.UploadJob;
import nz.govt.natlib.ndha.manualdeposit.metadata.UserGroupData;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BulkUploadQueueManagement {

	private final static Log LOG = LogFactory
			.getLog(BulkUploadQueueManagement.class);
	private final List<BulkUploadItem> bulkUploadItems = new ArrayList<BulkUploadItem>();
	private final JTable theBulkUploadItemsTable;
	private final AppProperties applicationProperties;
	private final UserGroupData userGroup;
	private boolean bulkUploadIsRunning = false;
	private final IBulkUpload uploadFrame;
	private boolean checkDPS = true;
	private final ManualDepositPresenter theManualDepositParent;
	private final BulkUploadPresenter bulkUploadParent;
	private UploadJob uploadJob;
	private boolean closeDownBulkUpload = false;

	public static BulkUploadQueueManagement create(
			final AppProperties appProperties,
			final JTable bulkUploadItemsTable, final IBulkUpload frame,
			final ManualDepositPresenter manualDepositParent,
			final BulkUploadPresenter parent) throws Exception {
		return new BulkUploadQueueManagement(appProperties,
				bulkUploadItemsTable, frame, manualDepositParent, parent);
	}

	public BulkUploadQueueManagement(final AppProperties appProperties,
			final JTable bulkUploadItemsTable, final IBulkUpload frame,
			final ManualDepositPresenter manualDepositParent,
			final BulkUploadPresenter parent) throws Exception {
		uploadFrame = frame;
		applicationProperties = appProperties;
		userGroup = applicationProperties.getUserData().getUser(
				applicationProperties.getLoggedOnUser()).getUserGroupData();
		theBulkUploadItemsTable = bulkUploadItemsTable;
		theManualDepositParent = manualDepositParent;
		bulkUploadParent = parent;
		final LoadJobs loadJobs = new LoadJobs();
		if (loadJobs.lock()) {
			final Thread jobThread = new Thread(loadJobs);
			jobThread.start();
			loadJobs.unlock();
		} else {
			LOG.debug("Couldn't load jobs");
			throw new BulkLoadException("Couldn't load jobs");
		}
	}

	public void startRunning() {
		bulkUploadIsRunning = true;
	}

	public boolean isRunning() {
		return bulkUploadIsRunning;
	}

	public void closeDown() {
		closeDownBulkUpload = true;
		theManualDepositParent.setProgressBarVisible(true);
	}

	public void setCheckDPS(boolean value) {
		checkDPS = value;
		synchronized (bulkUploadItems) {
			for (BulkUploadItem item : bulkUploadItems) {
				item.setQueryDPS(value);
			}
		}
	}

	public boolean isCheckDPS() {
		return checkDPS;
	}

	public int getNoOfFiles() {
		int noOfFiles = 0;
		synchronized (bulkUploadItems) {
			for (BulkUploadItem item : bulkUploadItems) {
				noOfFiles += item.getEntity().getAllFiles().size();
			}
		}
		return noOfFiles;
	}

	private static FileFilter xmlFilter = new FileFilter() {
		public boolean accept(File file) {
			return file.isFile()
					&& file.getName().toLowerCase().endsWith(".xml");
		}
	};

	public static File[] getUploadFiles(String uploadJobPath) {
		File[] files = null;
		File bulkUploadQueueFile = new File(uploadJobPath);
		if (bulkUploadQueueFile.exists() && bulkUploadQueueFile.isDirectory()) {
			files = bulkUploadQueueFile.listFiles(xmlFilter);
		}
		return files;
	}

	private class LoadJobs implements Runnable {

		private final Lock _loadJobsLock = new ReentrantLock();

		public boolean lock() {
			return _loadJobsLock.tryLock();
		}

		public void unlock() {
			_loadJobsLock.unlock();
		}

		public void run() {
			bulkUploadParent.setStatus("Loading saved jobs ...");
			LOG.debug("Start loadJobs");
			File[] files = getUploadFiles(applicationProperties
					.getApplicationData().getBulkUploadQueuePath());
			if (files != null) {
				int max = files.length;
				bulkUploadParent.setMaxProgress(max);
				bulkUploadParent.setProgressVisible(true);
				int i = 0;
				for (File file : files) {
					bulkUploadParent.setCurrentProgress(i);
					bulkUploadParent.setStatus(String.format(
							"Loading saved jobs (%d of %d) ...", i, max));
					try {
						LOG.debug("Loading job " + file.getAbsolutePath());
						BulkUploadItem job = BulkUploadItem.create(file
								.getPath(), applicationProperties,
								theManualDepositParent);
						addBulkUploadItem(job);
						LOG.debug("Job loaded, adding to queue "
								+ job.getEntityName());
					} catch (Exception ex) {
						String message = "The Job " + file.getName()
								+ " Cannot be loaded\n" + ex.getMessage();
						LOG.info(message);
					}
					i++;
				}
			} else {
				String message = "The Job Queue directory ("
						+ applicationProperties.getApplicationData()
								.getBulkUploadQueuePath()
						+ ") must exist and must be a directory";
				LOG.info(message);
			}
			checkJobQueue();
			bulkUploadParent.setNoOfFiles();
			LOG.debug("End loadJobs");
			bulkUploadParent.setStatus("");
			bulkUploadParent.setProgressVisible(false);
		}

	}

	public void addBulkUploadItem(BulkUploadItem item) {
		synchronized (bulkUploadItems) {
			bulkUploadItems.add(item);
			if (theBulkUploadItemsTable != null) {
				BulkUploadTableModel resultsTableModel = (BulkUploadTableModel) theBulkUploadItemsTable
						.getModel();
				resultsTableModel.addRow(item);
			}
		}
	}

	private void checkJobQueue() {
		if (closeDownBulkUpload) {
			return;
		}
		LOG.debug("Start checkJobQueue");
		int noOfJobsQueryingCMS = 0;
		int noOfJobsNotQueriedCMS = 0;
		int noOfJobsQueryingDPS = 0;
		int noOfJobsNotQueriedDPS = 0;
		int noOfJobsNotQueued = 0;
		int noOfJobsToBeBatched = 0;

		// System.out.println("Start checkJobQueue");
		synchronized (bulkUploadItems) {
			for (BulkUploadItem item : bulkUploadItems) {
				if ((item.getJobState() != JobState.Batching)
						&& (item.getJobState() != JobState.IndigoJobCreated)
						&& (!item.getJobState().isError())) {
					noOfJobsToBeBatched++;
				}
				switch (item.getJobState()) {
				case Requested:
					noOfJobsNotQueriedCMS++;
					break;
				case QueryingCMS:
					noOfJobsQueryingCMS++;
					break;
				case CMSIDRetrieved:
					if (checkDPS) {
						noOfJobsNotQueriedDPS++;
					} else {
						noOfJobsNotQueued++;
					}
					break;
				case QueryingDPS:
					noOfJobsQueryingDPS++;
					break;
				case ItemDoesNotExistInDPS:
					if (!checkDPS) {
						item.setJobState(JobState.CMSIDRetrieved);
					} else {
						noOfJobsNotQueued++;
					}
					break;
				case ItemExistsInDPS:
					if (!checkDPS) {
						item.setJobState(JobState.CMSIDRetrieved);
					}
					break;
				case Batching:
					if ((item.getUploadJobState() != null)
							&& (item.getUploadJobState() != UploadJob.JobState.Batching)) {
						item.setJobState(JobState.IndigoJobCreated);
					}
					break;
				default:
					break;
				}
			}
			if (bulkUploadIsRunning && noOfJobsToBeBatched == 0
					&& !bulkUploadParent.isLoadingFiles()) {
				bulkUploadIsRunning = false;
			}
			if (noOfJobsNotQueriedCMS > 0
					&& noOfJobsQueryingCMS < applicationProperties
							.getApplicationData().getBulkQueryCount()) {
				int noQuerying = 0;
				for (BulkUploadItem item : bulkUploadItems) {
					if (item.getJobState() == JobState.Requested) {
						item.retrieveCMSDetails();
						noQuerying++;
						if (noQuerying + noOfJobsQueryingCMS >= applicationProperties
								.getApplicationData().getBulkQueryCount()) {
							break;
						}
					}
				}
			}
			if (checkDPS
					&& noOfJobsNotQueriedDPS > 0
					&& noOfJobsQueryingDPS < applicationProperties
							.getApplicationData().getBulkQueryCount()) {
				for (BulkUploadItem item : bulkUploadItems) {
					if (item.getJobState() == JobState.CMSIDRetrieved) {
						item.checkDPS();
					}
				}
			}
			if (bulkUploadIsRunning && noOfJobsNotQueued > 0) {
				for (BulkUploadItem item : bulkUploadItems) {
					if (((item.getJobState() == JobState.CMSIDRetrieved) && (!checkDPS))
							|| (item.getJobState() == JobState.ItemDoesNotExistInDPS)) {
						// System.out.println("Need to batch " + item.getCMSID()
						// + ": " + item.getJobState().description());
						if (uploadJob == null) {
							try {
								uploadJob = item.createJob();
							} catch (Exception ex) {
								LOG.error("Error creating job", ex);
								// Should be safe to swallow this error as it
								// would
								// normally be handled before now.
							}
						} else {
							// System.out.println("Add job " + item.getCMSID());
							try {
								item.addToJob(uploadJob);
							} catch (JobQueueException jex) {
								LOG.error("Error adding job", jex);
								// Again, should be safe to swallow this error
								// as it would
								// normally be handled before now.
							}
						}
						noOfJobsToBeBatched--;
						if ((uploadJob.getJobDetail().size() >= userGroup
								.getBulkBatchSize())
								|| ((noOfJobsToBeBatched == 0) && (!bulkUploadParent
										.isLoadingFiles()))) {
							uploadJob.prepareJobToRun();
							theManualDepositParent.addJob(uploadJob);
							uploadJob = null;
						}
					}
				}
			}
		}
		if (theBulkUploadItemsTable != null) {
			theBulkUploadItemsTable.repaint();
		}
		bulkUploadParent.checkButtons();
		Action checkJobQueueAction = new AbstractAction() {
			private static final long serialVersionUID = 5562669711772031634L;

			public void actionPerformed(ActionEvent e) {
				Timer t = (Timer) e.getSource();
				t.stop();
				checkJobQueue();
			}
		};
		new Timer(applicationProperties.getApplicationData()
				.getJobQueueRefreshInterval(), checkJobQueueAction).start();
		LOG.debug("End checkJobQueue");
		// System.out.println("End checkJobQueue");
	}

	public JPopupMenu getBulkUploadMenu() {
		JPopupMenu menu = new JPopupMenu();
		String text = "Cancel job";
		if (theBulkUploadItemsTable.getSelectedRowCount() > 1) {
			text += "s";
		}
		JMenuItem item = new JMenuItem(text);
		item.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cancelJobs(evt);
			}
		});
		menu.add(item);
		BulkUploadTableModel model = (BulkUploadTableModel) theBulkUploadItemsTable
				.getModel();
		int noOfErrorJobsSelected = model
				.getSelectedErrorCount(theBulkUploadItemsTable);
		if (noOfErrorJobsSelected > 0) {
			if (noOfErrorJobsSelected > 1) {
				text = "Resubmit jobs in error";
			} else {
				text = "Resubmit job in error";
			}
			item = new JMenuItem(text);
			item.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					resubmitErrorJobs(evt);
				}
			});
			menu.add(item);
		}
		if (menu == null || menu.getComponentCount() == 0) {
			return null;
		} else {
			return menu;
		}
	}

	public int getCompletedJobCount() {
		int noOfJobs = 0;
		synchronized (bulkUploadItems) {
			for (BulkUploadItem item : bulkUploadItems) {
				if (item.getJobState().isComplete()) {
					noOfJobs++;
				}
			}
		}
		return noOfJobs;
	}

	public void clearCompletedJobs() {
		BulkUploadTableModel model = (BulkUploadTableModel) theBulkUploadItemsTable
				.getModel();
		synchronized (bulkUploadItems) {
			for (BulkUploadItem item : bulkUploadItems) {
				if (item.getJobState().isComplete()) {
					model.removeJob(item);
					item.deleteJob();
				}
			}
		}
	}

	private void killJob(BulkUploadTableModel model, BulkUploadItem job) {
		synchronized (bulkUploadItems) {
			if (job != null) {
				job.deleteJob();
				model.removeJob(job);
				bulkUploadItems.remove(job);
			}
		}
	}

	private class CancelJobs implements Runnable {

		private final Lock cancelJobsLock = new ReentrantLock();
		private boolean onlyCancelSelectedJobs;

		public CancelJobs(boolean onlySelectedJobs) {
			onlyCancelSelectedJobs = onlySelectedJobs;
		}

		public boolean lock() {
			return cancelJobsLock.tryLock();
		}

		public void unlock() {
			cancelJobsLock.unlock();
		}

		public void run() {
			if (onlyCancelSelectedJobs
					&& theBulkUploadItemsTable.getSelectedRows().length == 0) {
				uploadFrame.showError("Could not cancel job",
						"There are no selected jobs available to cancel");
				return;
			}
			String message;
			if (theBulkUploadItemsTable.getSelectedRows().length > 1) {
				message = "Cancel jobs?\n(This may take some time for large numbers)";
			} else {
				message = "Cancel job?";
			}
			if ((!onlyCancelSelectedJobs) || uploadFrame.confirm(message, true)) {
				uploadFrame.setWaitCursor(true);
				BulkUploadTableModel model = (BulkUploadTableModel) theBulkUploadItemsTable
						.getModel();
				uploadFrame.showGlassPane(true);
				if (!onlyCancelSelectedJobs) {
					uploadFrame.setStatus("Cancelling jobs ...");
					int noOfRows = bulkUploadItems.size();
					uploadFrame.setMaxProgress(noOfRows);
					uploadFrame.setProgressVisible(true);
					int counter = 1;
					while (bulkUploadItems.size() > 0) {
						uploadFrame.setCurrentProgress(counter);
						uploadFrame.setStatus(String.format(
								"Cancelling jobs (%d of %d) ...", counter,
								noOfRows));
						counter++;
						BulkUploadItem job = bulkUploadItems.get(0);
						killJob(model, job);
					}
					uploadFrame.setStatus("");
					uploadFrame.setProgressVisible(false);
				} else {
					int noOfRows = theBulkUploadItemsTable.getSelectedRows().length - 1;
					uploadFrame.setMaxProgress(noOfRows);
					uploadFrame.setProgressVisible(true);
					int counter = 0;
					for (int i = noOfRows; i >= 0; i--) {
						uploadFrame.setCurrentProgress(counter);
						uploadFrame.setStatus(String.format(
								"Cancelling jobs (%d of %d) ...", counter,
								noOfRows));
						counter++;
						int selectedRow = theBulkUploadItemsTable
								.getSelectedRows()[i];
						BulkUploadItem job = model.getRow(selectedRow);
						killJob(model, job);
					}
					uploadFrame.setStatus("");
					uploadFrame.setProgressVisible(false);
				}
				uploadFrame.showGlassPane(false);
				uploadFrame.setWaitCursor(false);
			}
		}

	}

	public void cancelJobs(boolean onlySelectedJobs) throws BulkLoadException {
		CancelJobs cancelJobs = new CancelJobs(onlySelectedJobs);
		if (cancelJobs.lock()) {
			Thread t = new Thread(cancelJobs);
			t.start();
			cancelJobs.unlock();
		} else {
			LOG.debug("Couldn't cancel jobs");
			throw new BulkLoadException("Couldn't cancel jobs");
		}
	}

	private void cancelJobs(java.awt.event.ActionEvent evt) {
		if (uploadFrame != null) {
			try {
				cancelJobs(true);
			} catch (BulkLoadException ex) {
				String errorMessage = "Error cancelling bulk load jobs";
				LOG.error(errorMessage, ex);
				uploadFrame.showError("An error occurred", errorMessage, ex);
			}
		}
	}

	private void resubmitErrorJobs(java.awt.event.ActionEvent evt) {
		int[] selectedRows = theBulkUploadItemsTable.getSelectedRows();
		BulkUploadTableModel model = (BulkUploadTableModel) theBulkUploadItemsTable
				.getModel();
		for (int index : selectedRows) {
			BulkUploadItem item = model.getRow(index);
			if (item.getJobState().isError()) {
				item.setJobState(JobState.Requested);
			}
		}
	}
}
