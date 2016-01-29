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

package nz.govt.natlib.ndha.manualdeposit.jobmanagement;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
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
import nz.govt.natlib.ndha.manualdeposit.IManualDepositMainFrame;
import nz.govt.natlib.ndha.manualdeposit.exceptions.JobQueueException;
import nz.govt.natlib.ndha.manualdeposit.metadata.PersonalSettings;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author PlayerM
 * 
 */
public final class JobQueueManagement {

	private final static Log LOG = LogFactory.getLog(JobQueueManagement.class);
	private final AppProperties theAppProperties;
	private final PersonalSettings personalSettings;
	private final IManualDepositMainFrame mainFrame;
	private final List<UploadJob> jobQueueRunning = new ArrayList<UploadJob>();
	private final List<UploadJob> jobQueuePending = new ArrayList<UploadJob>();
	private final List<UploadJob> jobQueueFailed = new ArrayList<UploadJob>();
	private final List<UploadJob> jobQueueDeposited = new ArrayList<UploadJob>();
	private final List<UploadJob> jobQueueInPermanent = new ArrayList<UploadJob>();
	private final List<UploadJob> jobQueueAwaitingCleanup = new ArrayList<UploadJob>();
	private final JTable theJobQueueRunningTable;
	private final JTable theJobQueuePendingTable;
	private final JTable theJobQueueFailedTable;
	private final JTable theJobQueueDepositedTable;
	private final JTable theJobQueueInPermanentTable;
	private JTable theTableSelected = null;

	public static JobQueueManagement create(final AppProperties appProperties,
			final IManualDepositMainFrame frame,
			final JTable jobQueueRunningTable,
			final JTable jobQueuePendingTable,
			final JTable jobQueueFailedTable,
			final JTable jobQueueDepositedTable,
			final JTable jobQueueCompleteTable) throws Exception {
		return new JobQueueManagement(appProperties, frame,
				jobQueueRunningTable, jobQueuePendingTable,
				jobQueueFailedTable, jobQueueDepositedTable,
				jobQueueCompleteTable);
	}

	public JobQueueManagement(final AppProperties appProperties,
			final IManualDepositMainFrame frame,
			final JTable jobQueueRunningTable,
			final JTable jobQueuePendingTable,
			final JTable jobQueueFailedTable,
			final JTable jobQueueDepositedTable,
			final JTable jobQueueCompleteTable) throws Exception {
		theAppProperties = appProperties;
		personalSettings = theAppProperties.getApplicationData()
				.getPersonalSettings();
		mainFrame = frame;
		theJobQueueRunningTable = jobQueueRunningTable;
		theJobQueuePendingTable = jobQueuePendingTable;
		theJobQueueFailedTable = jobQueueFailedTable;
		theJobQueueDepositedTable = jobQueueDepositedTable;
		theJobQueueInPermanentTable = jobQueueCompleteTable;
		loadJobs();
		refreshJobQueue();
	}

	private final FileFilter xmlFilter = new FileFilter() {
		public boolean accept(final File file) {
			return file.isFile()
					&& file.getName().toLowerCase().endsWith(".xml");
		}
	};

	private void loadJobs() throws JobQueueException {
		LOG.debug("Start loadJobs");
		final File jobQueueFile = new File(theAppProperties
				.getApplicationData().getJobQueuePath());
		if (jobQueueFile.exists() && jobQueueFile.isDirectory()) {
			final File[] files = jobQueueFile.listFiles(xmlFilter);
			for (File file : files) {
				try {
					LOG.debug("Loading job " + file.getAbsolutePath());
					final UploadJob job = new UploadJob(file.getPath(),
							theAppProperties);
					LOG.debug("Job loaded, adding to queue "
							+ job.getJobDetail().get(0).getEntityName());
					switch (job.getJobState()) {
					case Running:
						job.resubmitJob();
					case Pending:
					case Requested:
					case Batching:
						jobQueuePending.add(job);
						break;
					case Failed:
						jobQueueFailed.add(job);
						break;
					case Deposited:
						jobQueueDeposited.add(job);
						break;
					case AwaitingCleanup:
						jobQueueAwaitingCleanup.add(job);
					case Complete:
						break;
					default:
						break;
					}
				} catch (Exception ex) {
					final String message = String.format(
							"The Job %s Cannot be loaded\n %s", file.getName(),
							ex.getMessage());
					LOG.error(ex.getMessage(), ex);
					LOG.error("Printing contents of Job Queue folder...");
					saveJobQueueContentsToLOG(jobQueueFile);
					
					throw new JobQueueException("Cannot load job - " + message);
				}
			}
		} else {
			final String message = "The Job Queue directory ("
					+ theAppProperties.getApplicationData().getJobQueuePath()
					+ ") must exist and must be a directory";
			throw new JobQueueException("Cannot run - " + message);
		}
		refreshJobQueue();
		checkJobQueue();
		checkSipStatus();
		LOG.debug("End loadJobs");
	}
	
	// Created 23.Jan.2014 to help identify job queue error
	private void saveJobQueueContentsToLOG(File file){
		for(File childFile: file.listFiles()){
			
			LOG.error(childFile.getAbsolutePath());
			if(childFile.isDirectory()){
				saveJobQueueContentsToLOG(childFile);
			}
		}
	}

	private boolean checkAndMoveJobs(final List<UploadJob> jobsToCheck) {
		LOG.debug("Start checkAndMoveJobs");
		boolean itemsMoved = false;
		for (int i = 0; i < jobsToCheck.size(); i++) {
			UploadJob job = jobsToCheck.get(i);
			if (jobsToCheck.equals(jobQueueAwaitingCleanup)) {
				if (job.getJobState() != UploadJob.JobState.AwaitingCleanup) {
					jobsToCheck.remove(job);
					itemsMoved = true;
				}
			} else {
				switch (job.getJobState()) {
				case Running:
					if (!jobsToCheck.equals(jobQueueRunning)) {
						jobsToCheck.remove(job);
						jobQueueRunning.add(job);
						itemsMoved = true;
					}
					break;
				case Requested:
				case Pending:
				case Batching:
					if (!jobsToCheck.equals(jobQueuePending)) {
						jobsToCheck.remove(job);
						jobQueuePending.add(job);
						itemsMoved = true;
					}
					break;
				case Failed:
					if (!jobsToCheck.equals(jobQueueFailed)) {
						jobsToCheck.remove(job);
						jobQueueFailed.add(job);
						itemsMoved = true;
					}
					break;
				case Deposited:
					if (!jobsToCheck.equals(jobQueueDeposited)) {
						jobsToCheck.remove(job);
						jobQueueDeposited.add(job);
						itemsMoved = true;
					}
					break;
				case AwaitingCleanup:
				case Complete:
					if ((jobsToCheck != jobQueueInPermanent)
							&& (jobsToCheck != jobQueueAwaitingCleanup)) {
						jobsToCheck.remove(job);
						if (job.getJobState() == UploadJob.JobState.AwaitingCleanup) {
							jobQueueInPermanent.add(job);
						}
						jobQueueAwaitingCleanup.add(job);
						itemsMoved = true;
					}
					break;
				default:
					break;
				}
			}
		}
		LOG.debug("End checkAndMoveJobs");
		return itemsMoved;
	}

	private void checkJobQueue() {
		LOG.debug("Start checkJobQueue");
		boolean itemsMoved = checkAndMoveJobs(jobQueueRunning)
				|| checkAndMoveJobs(jobQueuePending)
				|| checkAndMoveJobs(jobQueueFailed)
				|| checkAndMoveJobs(jobQueueDeposited)
				|| checkAndMoveJobs(jobQueueInPermanent)
				|| checkAndMoveJobs(jobQueueAwaitingCleanup);
		while ((!jobQueuePending.isEmpty())
				&& (jobQueueRunning.size() < theAppProperties
						.getApplicationData().getMaximumJobsRunning())) {
			UploadJob job;
			// need to take either the first or last depending on whether the
			// queue is sorted Asc or Desc
			int jobNumber;
			if (personalSettings.isSortPendingAscending()) {
				jobNumber = 0;
			} else {
				jobNumber = jobQueuePending.size() - 1;
			}
			job = jobQueuePending.get(jobNumber);
			while (job.isCreatingCopy() && jobNumber >= 0
					&& jobNumber < jobQueuePending.size()) {
				if (personalSettings.isSortPendingAscending()) {
					jobNumber++;
				} else {
					jobNumber--;
				}
				if (jobNumber >= 0 && jobNumber < jobQueuePending.size()) {
					job = jobQueuePending.get(jobNumber);
				}
			}
			if (job.isCreatingCopy()) {
				break;
			}
			if (job.lock()) {
				jobQueuePending.remove(job);
				jobQueueRunning.add(job);
				itemsMoved = true;
				Thread t = new Thread(job);
				t.start();
				job.unlock();
			} else {
				LOG.debug("Couldn't lock job "
						+ job.getJobDetail().get(0).getEntityName());
				try {
					Thread.sleep(100);
				} catch (Exception ex) {
				}
			}
		}
		if (itemsMoved) {
			refreshJobQueue();
		}
		if (theJobQueuePendingTable != null) {
			theJobQueuePendingTable.repaint();
		}
		if (theJobQueueRunningTable != null) {
			theJobQueueRunningTable.repaint();
		}
		if (theJobQueueFailedTable != null) {
			theJobQueueFailedTable.repaint();
		}
		if (theJobQueueDepositedTable != null) {
			theJobQueueDepositedTable.repaint();
		}
		if (theJobQueueInPermanentTable != null) {
			theJobQueueInPermanentTable.repaint();
		}
		final Action checkJobQueueAction = new AbstractAction() {
			private static final long serialVersionUID = 5562669711772031634L;

			public void actionPerformed(final ActionEvent e) {
				Timer t = (Timer) e.getSource();
				t.stop();
				checkJobQueue();
			}
		};
		new Timer(theAppProperties.getApplicationData()
				.getJobQueueRefreshInterval(), checkJobQueueAction).start();
		LOG.debug("End checkJobQueue");
	}

	private void checkSipStatus() {
		LOG.debug("Start checkSipStatus");
		for (int i = jobQueueDeposited.size() - 1; i >= 0; i--) {
			final UploadJob job = jobQueueDeposited.get(i);
			job.checkSipStatus();
		}
		for (int i = jobQueueAwaitingCleanup.size() - 1; i >= 0; i--) {
			final UploadJob job = jobQueueAwaitingCleanup.get(i);
			job.checkForCleanup();
		}
		final Action checkSipStatusAction = new AbstractAction() {
			private static final long serialVersionUID = -8315654343127184873L;

			public void actionPerformed(final ActionEvent e) {
				Timer t = (Timer) e.getSource();
				t.stop();
				checkSipStatus();
			}
		};
		new Timer(theAppProperties.getApplicationData()
				.getSipStatusRefreshInterval(), checkSipStatusAction).start();
		LOG.debug("End checkSipStatus");
	}

	private void resortTable(final java.awt.event.ActionEvent evt) {
		if (theTableSelected.equals(theJobQueueRunningTable)) {
			personalSettings.setSortRunningAscending(!personalSettings
					.isSortRunningAscending());
		} else if (theTableSelected.equals(theJobQueuePendingTable)) {
			personalSettings.setSortPendingAscending(!personalSettings
					.isSortPendingAscending());
		} else if (theTableSelected.equals(theJobQueueFailedTable)) {
			personalSettings.setSortFailedAscending(!personalSettings
					.isSortFailedAscending());
		} else if (theTableSelected.equals(theJobQueueDepositedTable)) {
			personalSettings.setSortDepositedAscending(!personalSettings
					.isSortDepositedAscending());
		} else if (theTableSelected.equals(theJobQueueInPermanentTable)) {
			personalSettings.setSortCompleteAscending(!personalSettings
					.isSortCompleteAscending());
		}
		refreshJobQueue();
	}

	public UploadJob getUploadJob(final String jobName) {
		UploadJob result = null;
		for (UploadJob job : jobQueueRunning) {
			if (job.getBatchName().equalsIgnoreCase(jobName)) {
				result = job;
				break;
			}
		}
		if (result == null) {
			for (UploadJob job : jobQueuePending) {
				if (job.getBatchName().equalsIgnoreCase(jobName)) {
					result = job;
					break;
				}
			}
		}
		if (result == null) {
			for (UploadJob job : jobQueueFailed) {
				if (job.getBatchName().equalsIgnoreCase(jobName)) {
					result = job;
					break;
				}
			}
		}
		if (result == null) {
			for (UploadJob job : jobQueueDeposited) {
				if (job.getBatchName().equalsIgnoreCase(jobName)) {
					result = job;
					break;
				}
			}
		}
		if (result == null) {
			for (UploadJob job : jobQueueInPermanent) {
				if (job.getBatchName().equalsIgnoreCase(jobName)) {
					result = job;
					break;
				}
			}
		}
		if (result == null) {
			for (UploadJob job : jobQueueAwaitingCleanup) {
				if (job.getBatchName().equalsIgnoreCase(jobName)) {
					result = job;
					break;
				}
			}
		}
		return result;
	}

	public JPopupMenu getJobQueueMenu(final JTable table) {
		theTableSelected = table;
		final JPopupMenu menu = new JPopupMenu();
		boolean sortAscending = false;
		if (table.equals(theJobQueueRunningTable)) {
			sortAscending = personalSettings.isSortRunningAscending();
		} else if (table.equals(theJobQueuePendingTable)) {
			sortAscending = personalSettings.isSortPendingAscending();
		} else if (table.equals(theJobQueueFailedTable)) {
			sortAscending = personalSettings.isSortFailedAscending();
		} else if (table.equals(theJobQueueDepositedTable)) {
			sortAscending = personalSettings.isSortDepositedAscending();
		} else if (table.equals(theJobQueueInPermanentTable)) {
			sortAscending = personalSettings.isSortCompleteAscending();
		}
		JMenuItem item;
		if (sortAscending) {
			item = new JMenuItem("Sort by date in descending order");
		} else {
			item = new JMenuItem("Sort by date in ascending order");
		}
		item.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				resortTable(evt);
			}
		});
		menu.add(item);
		if ((table.equals(theJobQueueRunningTable))
				|| (table.equals(theJobQueuePendingTable))
				|| (table.equals(theJobQueueFailedTable))) {
			StringBuffer text = new StringBuffer();
			text.append("Cancel job");
			if (table.getSelectedRowCount() > 1) {
				text.append('s');
			}
			item = new JMenuItem(text.toString());
			item.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(final java.awt.event.ActionEvent evt) {
					try {
						cancelJobs(evt);
					} catch (JobQueueException ex) {
						String message = "Error cancelling jobs";
						LOG.error(message, ex);
						mainFrame.showError("An error occurred", message, ex);
					}
				}
			});
			menu.add(item);
			if (table.equals(theJobQueueFailedTable)) {
				text = new StringBuffer();
				text.append("Resubmit job");
				if (table.getSelectedRowCount() > 1) {
					text.append('s');
				}
				item = new JMenuItem(text.toString());
				item.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(
							final java.awt.event.ActionEvent evt) {
						resubmitJob(evt);
					}
				});
				menu.add(item);
			}
		}
		if (table.equals(theJobQueueInPermanentTable)) {
			item = new JMenuItem("Clear completed jobs");
			item.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(final java.awt.event.ActionEvent evt) {
					clearFinishedJobs(evt);
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

	private void clearFinishedJobs(final java.awt.event.ActionEvent evt) {
		while (!jobQueueInPermanent.isEmpty()) {
			final UploadJob job = jobQueueInPermanent.get(0);
			final JobQueueTableModel model = (JobQueueTableModel) theJobQueueInPermanentTable
					.getModel();
			model.removeJob(job);
			jobQueueInPermanent.remove(job);
		}
		theJobQueueInPermanentTable.repaint();
	}

	private void resubmitJob(final java.awt.event.ActionEvent evt) {
		final JobQueueTableModel model = (JobQueueTableModel) theTableSelected
				.getModel();
		for (int i = theTableSelected.getSelectedRows().length - 1; i >= 0; i--) {
			int selectedRow = theTableSelected.getSelectedRows()[i];
			UploadJob job = model.getRow(selectedRow);
			boolean submitJob = true;
			if (job.getSIPID() != null && (!job.getSIPID().equals(""))) {
				String message = String
						.format(
								"Job %s has a SIP ID (%s) implying that it has already been submitted.\nAre you sure you want to resubmit this job?",
								job.getJobDetail().get(0).getEntityName(), job
										.getSIPID());
				submitJob = mainFrame.confirm(message, true);
			}
			if (submitJob) {
				job.resubmitJob();
			}
		}
	}

	private class CancelJobs implements Runnable {

		private final Lock _cancelJobsLock = new ReentrantLock();

		public boolean lock() {
			return _cancelJobsLock.tryLock();
		}

		public void unlock() {
			_cancelJobsLock.unlock();
		}

		public void run() {
			if (mainFrame != null) {
				if (theTableSelected.getSelectedRows().length == 0) {
					mainFrame.showError("Could not cancel job",
							"There are no selected jobs available to cancel");
					return;
				}
				String message;
				if (theTableSelected.getSelectedRows().length > 1) {
					message = "Cancel jobs?";
				} else {
					message = "Cancel job?";
				}
				if (mainFrame.confirm(message, true)) {
					JobQueueTableModel model = (JobQueueTableModel) theTableSelected
							.getModel();
					for (int i = theTableSelected.getSelectedRows().length - 1; i >= 0; i--) {
						int selectedRow = theTableSelected.getSelectedRows()[i];
						UploadJob job = model.getRow(selectedRow);
						if(job != null){
							if ((job.getJobState() != UploadJob.JobState.Deposited) || (job.getJobState() != UploadJob.JobState.Complete)) {
								job.cancelJob();
								if (jobQueueRunning.contains(job)) {
									jobQueueRunning.remove(job);
								} else if (jobQueuePending.contains(job)) {
									jobQueuePending.remove(job);
								} else if (jobQueueFailed.contains(job)) {
									jobQueueFailed.remove(job);
								}
								model.removeJob(job);
							}
						}
					}
				}
			}
		}

	}

	private void cancelJobs(final java.awt.event.ActionEvent evt)
			throws JobQueueException {
		CancelJobs cancelJobs = new CancelJobs();
		if (cancelJobs.lock()) {
			Thread t = new Thread(cancelJobs);
			t.start();
			cancelJobs.unlock();
		} else {
			LOG.debug("Couldn't cancel jobs");
			throw new JobQueueException("Couldn't cancel jobs");
		}
	}

	public void addJob(final UploadJob job) {
		LOG.debug("Start addJob");
		job.lock();
		JobQueueTableModel model = (JobQueueTableModel) theJobQueuePendingTable
				.getModel();
		jobQueuePending.add(job);
		Collections.sort(jobQueuePending, new UploadJobComparator(
				personalSettings));
		if ((jobQueuePending.size() == 1)
				|| (personalSettings.isSortPendingAscending())) {
			model.addRow(job);
		} else {
			model.insertJob(job, jobQueuePending.get(0), true);
		}
		job.unlock();
		refreshJobQueue();
		LOG.debug("End addJob");
	}

	private void sortQueues() {
		Collections.sort(jobQueueRunning, new UploadJobComparator(
				personalSettings));
		Collections.sort(jobQueuePending, new UploadJobComparator(
				personalSettings));
		Collections.sort(jobQueueFailed, new UploadJobComparator(
				personalSettings));
		Collections.sort(jobQueueDeposited, new UploadJobComparator(
				personalSettings));
		Collections.sort(jobQueueInPermanent, new UploadJobComparator(
				personalSettings));
	}

	public void refreshJobQueue() {
		LOG.debug("Start refreshJobQueue");
		sortQueues();
		JobQueueTableModel model = (JobQueueTableModel) theJobQueueRunningTable
				.getModel();
		model.clearTable();
		for (int i = 0; i < jobQueueRunning.size(); i++) {
			UploadJob job = jobQueueRunning.get(i);
			model.addRow(job);
		}
		theJobQueueRunningTable.repaint();

		model = (JobQueueTableModel) theJobQueuePendingTable.getModel();
		model.clearTable();
		for (int i = 0; i < jobQueuePending.size(); i++) {
			UploadJob job = jobQueuePending.get(i);
			model.addRow(job);
		}
		theJobQueuePendingTable.repaint();

		model = (JobQueueTableModel) theJobQueueFailedTable.getModel();
		model.clearTable();
		for (int i = 0; i < jobQueueFailed.size(); i++) {
			UploadJob job = jobQueueFailed.get(i);
			model.addRow(job);
		}
		theJobQueueFailedTable.repaint();

		model = (JobQueueTableModel) theJobQueueDepositedTable.getModel();
		model.clearTable();
		for (int i = 0; i < jobQueueDeposited.size(); i++) {
			UploadJob job = jobQueueDeposited.get(i);
			model.addRow(job);
		}
		theJobQueueDepositedTable.repaint();

		model = (JobQueueTableModel) this.theJobQueueInPermanentTable
				.getModel();
		model.clearTable();
		for (int i = 0; i < jobQueueInPermanent.size(); i++) {
			UploadJob job = jobQueueInPermanent.get(i);
			model.addRow(job);
		}
		theJobQueueInPermanentTable.repaint();
		LOG.debug("End refreshJobQueue");
	}

	public boolean jobsOutstanding() {
		int noOfJobs = jobQueueRunning.size() + jobQueuePending.size()
				+ jobQueueFailed.size() + jobQueueDeposited.size();
		return (noOfJobs > 0);
	}

	public boolean jobsRunning() {
		int noOfJobs = jobQueueRunning.size() + jobQueuePending.size();
		return (noOfJobs > 0);
	}
}
