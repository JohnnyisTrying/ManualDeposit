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
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Timer;

import nz.govt.natlib.ndha.common.ChecksumDigest;
import nz.govt.natlib.ndha.common.ChecksumDigest.FileStatus;
import nz.govt.natlib.ndha.common.FileUtils;
//import nz.govt.natlib.ndha.common.MD5Digest;
import nz.govt.natlib.ndha.common.XMLHandler;
//import nz.govt.natlib.ndha.common.MD5Digest.FileStatus;
import nz.govt.natlib.ndha.common.XMLHandler.XMLObject;
import nz.govt.natlib.ndha.common.exlibris.IDeposit;
import nz.govt.natlib.ndha.common.exlibris.ResultOfDeposit;
import nz.govt.natlib.ndha.common.exlibris.SIPStatus;
import nz.govt.natlib.ndha.common.exlibris.SIPStatusCollection;
import nz.govt.natlib.ndha.common.mets.FSOCollection;
import nz.govt.natlib.ndha.common.mets.FileGroupCollection;
import nz.govt.natlib.ndha.common.mets.FileSystemObject;
import nz.govt.natlib.ndha.common.mets.MetsWriter;
import nz.govt.natlib.ndha.common.mets.StructMapCollection;
import nz.govt.natlib.ndha.manualdeposit.AppProperties;
import nz.govt.natlib.ndha.manualdeposit.exceptions.JobQueueException;
import nz.govt.natlib.ndha.manualdeposit.metadata.EDataType;
import nz.govt.natlib.ndha.manualdeposit.metadata.IMetaDataTypeExtended;
import nz.govt.natlib.ndha.manualdeposit.metadata.MetaDataFields;
import nz.govt.natlib.ndha.manualdeposit.metadata.UserGroupData;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class UploadJob implements Runnable {

	public enum JobState {
		Batching(false), Requested(false), Pending(false), Running(false), Deposited(
				false), AwaitingCleanup(false), Complete(false), Cancelled(true), Failed(
				true);

		JobState(boolean isFailure) {
			this.isFailure = isFailure;
		}

		private boolean isFailure;

		public boolean isFailure() {
			return isFailure;
		}
	}

	public class JobDetail {
		private final static String ENTITY_NAME_ATTR = "EntityName";
		private final static String FILE_GROUP_ATTR = "FileGroupFile";

		private String entityName;
		private FileGroupCollection fileGroups;
		private StructMapCollection structures;
		private MetaDataFields theMetaData;

		public JobDetail(String entityName, FileGroupCollection fileGroups,
				StructMapCollection structures, MetaDataFields theMetaData) {
			this.entityName = entityName;
			this.fileGroups = fileGroups;
			this.structures = structures;
			this.theMetaData = theMetaData;
		}

		public JobDetail(String xmlFileName) throws FileNotFoundException {
			XMLHandler handler = new XMLHandler(XML_ROOT_NAME, xmlFileName);
			List<String> keys = handler.getObjectNames();
			// There can be only one
			if (keys.size() > 1) {
				LOG.debug("Invalid job queue item - more than one object");
				throw new FileNotFoundException("Invalid job queue item");
			}
			XMLObject object = handler.getObject(keys.get(0));
			LOG.debug("XMLObject created - getting details");
			entityName = object.getAttribute(ENTITY_NAME_ATTR);
			String xmlFile = object.getAttribute(META_DATA_ATTR);
			theMetaData = MetaDataFields.create(xmlFile);
			xmlFile = object.getAttribute(FILE_GROUP_ATTR);
			fileGroups = FileGroupCollection.create(entityName, xmlFile, true);
			xmlFile = object.getAttribute(STRUCT_MAP_LOCATION_ATTR);
			List<FileGroupCollection> fileGroupCollection = new ArrayList<FileGroupCollection>();
			fileGroupCollection.add(fileGroups);
			structures = StructMapCollection.create(xmlFile,
					fileGroupCollection);
		}

		private String getEntityFileName() {
			return FileUtils.specialCharToUnderscore(entityName);
		}

		public String getXMLFileName() {
			return getDetailDirectory() + "/" + getEntityFileName() + ".xml";
		}

		public String getEntityName() {
			return entityName;
		}

		public FileGroupCollection getFileGroups() {
			return fileGroups;
		}

		public StructMapCollection getStructures() {
			return structures;
		}

		public MetaDataFields getTheMetaData() {
			return theMetaData;
		}

		/**
		 * Recreate the _theMetaData object and its contents, in case another
		 * thread modifies its contents. This is needed only when the job is
		 * submitted from the GUI, since the parent GUI cleans up all values in
		 * the meta data before this job finishes the METS creation. Therefore,
		 * this variable _theMetaData is not thread-safe.
		 */
		public void reloadFromFile() {
			try {
				theMetaData = new MetaDataFields(getMetaDataFileName());
			} catch (FileNotFoundException ex) {
				LOG.error(ex.getMessage(), ex);
			}
		}

		public String getDetailDirectory() {
			return theJobDirectory + "/" + getEntityFileName();
		}

		private String getMetaDataFileName() {
			return getDetailDirectory() + "/" + "metaData.xml";
		}

		public void saveJobDetail() {
			FileUtils.ensureDirectoryExists(theJobDirectory);
			String detailDirectory = getDetailDirectory();
			FileUtils.ensureDirectoryExists(detailDirectory);
			XMLHandler handler;
			try {
				String mainXmlFileName = getXMLFileName();
				File xmlFile = new File(mainXmlFileName);
				if (xmlFile.exists()) {
					xmlFile.delete();
				}
				handler = new XMLHandler(XML_ROOT_NAME, mainXmlFileName);
				XMLObject object = handler.createXMLObject("EntityDescription",
						entityName);
				object.addAttribute(ENTITY_NAME_ATTR, entityName);
				String metaDataFile = getMetaDataFileName();
				object.addAttribute(META_DATA_ATTR, metaDataFile);
				theMetaData.storeAsXML(getMetaDataFileName());
				String xmlFileName = detailDirectory + "/"
						+ getEntityFileName() + "FileGroups.xml";
				object.addAttribute(FILE_GROUP_ATTR, xmlFileName);
				fileGroups.storeAsXML(xmlFileName);
				String structureFileName = detailDirectory + "/Structures.xml";
				object
						.addAttribute(STRUCT_MAP_LOCATION_ATTR,
								structureFileName);
				structures.storeAsXML(structureFileName, true);
				handler.addObject(object);
				handler.writeXMLFile();
			} catch (Exception ex) {
				ex.printStackTrace();
				LOG.error(ex.getMessage(), ex);
			}
		}
	}

	private final static Log LOG = LogFactory.getLog(UploadJob.class);

	private final static String BATCH_NAME_ATTR = "BatchName";
	private final static String SAVE_PATH_ATTR = "SavePath";
	private final static String SYSTEM_USER_ID_ATTR = "SystemUser";
	private final static String IE_ROOT_ATTR = "IERoot";
	private final static String PRODUCER_ID_ATTR = "ProducerID";
	private final static String MATERIAL_FLOW_ID_ATTR = "MaterialFlowID";
	private final static String CREATOR_ATTR = "Creator";
	private final static String TOTAL_SIZE_ATTR = "TotalSizeAttr";
	private final static String SIZE_COPIED_ATTR = "SizeCopiedAttr";
	private final static String STATUS_ATTR = "Status";
	private final static String JOB_STATE_ATTR = "JobStatus";
	private final static String JOB_QUEUE_PATH_ATTR = "JobQueuePath";
	private final static String META_DATA_ATTR = "MetaDataPath";
	private final static String SIP_ID_ATTR = "SIP_ID";
	private final static String DEPOSIT_DATE_ATTR = "DepositDate";
	private final static String BASE_DIRECTORY_ATTR = "BaseDirectory";
	private final static String CLEANUP_DATE_ATTR = "CleaunupDate";
	private final static String TEMPORARY_CLEANUP_DIRECTORY_ATTR = "TemporaryCleanupDirectory";
	private final static String COPY_FINISHED_ATTR = "CopyFinished";
	private final static String STRUCT_MAP_LOCATION_ATTR = "StructMapLocation";
	private final static String DELETE_DIRECTORIES_ATTR = "DeleteDirectories";
	private final static String XML_ROOT_NAME = "Entity";
	private final static String JOB_DETAIL_ATTR = "JobDetail";
	private final static String CHECK_FOR_INCLUDE_NAME = "CheckForIncludeName";

	private static AppProperties appProperties;
	private final Lock _lock = new ReentrantLock();
	final private String theBatchName;
	final private String theSavePath;
	final private String theSystemUserID;
	final private String theIeRoot;
	final private String theCreator;
	private Map<String, JobDetail> allJobDetail = new HashMap<String, JobDetail>();
	private long totalSize;
	private long sizeCopied;
	private String jobDetailStatus;
	private String theSipID = null;
	private Date theDepositDateTime;
	private SIPStatus theSipStatus = SIPStatusCollection.unknown();
	private JobState theJobState = JobState.Batching;
	private String theJobQueuePath;
	private String theXmlFileName;
	private String theJobDirectory;
	private String theBaseDirectory;
	private String theFixityType = "";
	private String theTemporaryCleanupDirectory = "";
	private boolean canDeleteDirectories = false;
	private boolean isSelected = false;
	private MetsWriter theWriter;
	private boolean canCancelJob = false;
	private String theProducerID;
	private String theMaterialFlowID;
	private final DateFormat theDateFormat = new SimpleDateFormat(
			"dd/MMM/yyyy HH:mm:ss");
	private final DateFormat theDateFormatFileName = new SimpleDateFormat(
			"ddMMMyyyyHHmmss");
//	private MD5Digest theDigest;
	private ChecksumDigest theDigest;
	private boolean isCopyFinished = false;
	private FSOCollection theFiles;
	private Date theCleanupDate;
	private UserGroupData theUserGroup;
	private String theErrorText = "";
	private boolean shouldCheckForIncludeName = false;

	public boolean lock() {
		return _lock.tryLock();
	}

	public void unlock() {
		_lock.unlock();
	}

	public static UploadJob create(String xmlFileName,
			AppProperties appProperties) throws FileNotFoundException {
		return new UploadJob(xmlFileName, appProperties);
	}

	public UploadJob(String xmlFileName, AppProperties applicationProperties)
			throws FileNotFoundException {
		LOG.debug("Creating new UploadJob - xmlFileName " + xmlFileName);
		if (appProperties == null) {
			LOG.debug("AppProperties is null");
		} else {
			LOG.debug("AppProperties is not null, job queue path "
					+ appProperties.getApplicationData().getJobQueuePath());
		}
		appProperties = applicationProperties;
		try {
			theUserGroup = appProperties.getUserData().getUser(
					appProperties.getLoggedOnUser()).getUserGroupData();
		} catch (Exception ex) {
			// TODO fix this so it returns an error
		}
		XMLHandler handler = new XMLHandler(XML_ROOT_NAME, xmlFileName);
		List<String> keys = handler.getObjectNames();
		// There can be only one
		if (keys.size() > 1) {
			LOG.debug("Invalid job queue item - more than one object");
			throw new FileNotFoundException("Invalid job queue item");
		}
		XMLObject object = handler.getObject(keys.get(0));
		LOG.debug("XMLObject created - getting details");
		theBatchName = object.getAttribute(BATCH_NAME_ATTR);
		LOG.debug("_entityName " + theBatchName);
		theSavePath = object.getAttribute(SAVE_PATH_ATTR);
		LOG.debug("_savePath " + theSavePath);
		theSystemUserID = object.getAttribute(SYSTEM_USER_ID_ATTR);
		LOG.debug("_systemUserID " + theSystemUserID);
		theIeRoot = object.getAttribute(IE_ROOT_ATTR);
		LOG.debug("_ieRoot " + theIeRoot);
		theProducerID = object.getAttribute(PRODUCER_ID_ATTR);
		LOG.debug("_producerID " + theProducerID);
		theMaterialFlowID = object.getAttribute(MATERIAL_FLOW_ID_ATTR);
		LOG.debug("_materialFlowID " + theMaterialFlowID);
		theCreator = object.getAttribute(CREATOR_ATTR);
		LOG.debug("_creator " + theCreator);
		if (object.getAttribute(TOTAL_SIZE_ATTR) != null) {
			try {
				totalSize = Integer.parseInt(object
						.getAttribute(TOTAL_SIZE_ATTR));
			} catch (Exception ex) {
				ex.printStackTrace();
				LOG.error(ex.getMessage(), ex);
			}
		}
		LOG.debug("_totalSize " + totalSize);
		if (object.getAttribute(SIZE_COPIED_ATTR) != null) {
			try {
				sizeCopied = Integer.parseInt(object
						.getAttribute(SIZE_COPIED_ATTR));

			} catch (Exception ex) {
				ex.printStackTrace();
				LOG.error(ex.getMessage(), ex);
			}
		}
		LOG.debug("_sizeCopied " + sizeCopied);
		jobDetailStatus = object.getAttribute(STATUS_ATTR);
		LOG.debug("_status " + jobDetailStatus);
		if (object.getAttribute(JOB_STATE_ATTR) != null) {
			try {
				theJobState = JobState.valueOf(object
						.getAttribute(JOB_STATE_ATTR));
			} catch (Exception ex) {
				ex.printStackTrace();
				LOG.error(ex.getMessage(), ex);
			}
		}
		LOG.debug("_jobState " + theJobState);
		theJobQueuePath = object.getAttribute(JOB_QUEUE_PATH_ATTR);
		LOG.debug("_jobQueuePath " + theJobQueuePath);
		allJobDetail = new HashMap<String, JobDetail>();
		for (XMLObject childObject : object.getChildObjects()) {
			JobDetail detail = new JobDetail(childObject.getObjectValue());
			String entityName = detail.getEntityName();
			allJobDetail.put(entityName, detail);
		}
		theSipID = object.getAttribute(SIP_ID_ATTR);
		String depositDateTime = object.getAttribute(DEPOSIT_DATE_ATTR);
		if (depositDateTime == null) {
			theDepositDateTime = new Date();
		} else {
			try {
				theDepositDateTime = theDateFormat.parse(depositDateTime);
			} catch (Exception ex) {
				theDepositDateTime = new Date();
			}
		}
		theBaseDirectory = object.getAttribute(BASE_DIRECTORY_ATTR);
		theTemporaryCleanupDirectory = object
				.getAttribute(TEMPORARY_CLEANUP_DIRECTORY_ATTR);
		isCopyFinished = Boolean.parseBoolean(object
				.getAttribute(COPY_FINISHED_ATTR));
		canDeleteDirectories = Boolean.parseBoolean(object
				.getAttribute(DELETE_DIRECTORIES_ATTR));
		shouldCheckForIncludeName = Boolean.parseBoolean(object
				.getAttribute(CHECK_FOR_INCLUDE_NAME));
		String cleanupDate = object.getAttribute(CLEANUP_DATE_ATTR);
		if (depositDateTime == null) {
			theCleanupDate = new Date();
		} else {
			try {
				theCleanupDate = theDateFormat.parse(cleanupDate);
			} catch (Exception ex) {
				theCleanupDate = new Date();
			}
		}

		commonStartup(true);
		if (!theSystemUserID.equals(appProperties.getLoggedOnUser())
				&& ((theJobState == JobState.Requested)
						|| (theJobState == JobState.Pending) || (theJobState == JobState.Running))) {
			theJobState = JobState.Failed;
			jobDetailStatus = "Job could not be loaded as it belongs to "
					+ theSystemUserID;
		}
	}

	public String getBatchName() {
		return theBatchName;
	}

	public boolean isCreatingCopy() {
		return !isCopyFinished;
	}

	public ArrayList<JobDetail> getJobDetail() {
		ArrayList<JobDetail> detail = new ArrayList<JobDetail>();
		for (String key : allJobDetail.keySet()) {
			JobDetail jobDetail = allJobDetail.get(key);
			detail.add(jobDetail);
		}
		return detail;
	}

	private void handleException(Exception ex) {
		theJobState = JobState.Failed;
		theErrorText = "";
		if (ex != null) {
			ex.printStackTrace();
			if (ex.getMessage() != null) {
				theErrorText += ex.getMessage() + "\n";
			}
			StringWriter writer = new StringWriter();
			ex.printStackTrace(new PrintWriter(writer));
			theErrorText += writer.toString();
		}
		jobDetailStatus = "Job failed - " + theErrorText;
		ex.printStackTrace();
		LOG.error(ex.getMessage(), ex);
	}

	// @SuppressWarnings("unchecked")
	private void commonStartup(boolean createCopy) throws FileNotFoundException {
		try {
			LOG.debug("Common Startup");
			// Following if statement commented out - to fix issue #53 of only first file 
			// being copied to cleanup folder.
			// 20.Aug.2013 by Ben.
//			if (!isCopyFinished && createCopy) {
//				createCopy(false);
//			}
			theXmlFileName = theJobQueuePath + "/" + getEntityNamePadded()
					+ ".xml";
			theJobDirectory = theJobQueuePath + "/" + getEntityNamePadded();
			if (!isFTP()) {
				File savePathFile = new File(theSavePath);
				if (!savePathFile.exists()) {
					throw new FileNotFoundException(
							"Output directory not found");
				}
			}
			String metsRootDirectory = cleanUpForID(theSystemUserID + "/"
					+ getEntityNamePadded());
			theWriter = appProperties.getApplicationData().getMetsWriter();
			boolean includeEntityNameInTitle;
			String entityName;
			if (shouldCheckForIncludeName) {
				if (allJobDetail.size() > 1) {
					includeEntityNameInTitle = true;
				} else if (allJobDetail.isEmpty()) {
					includeEntityNameInTitle = true;
				} else {
					includeEntityNameInTitle = false;
				}
			} else {
				includeEntityNameInTitle = false;
			}
			if (allJobDetail.size() > 1) {
				entityName = "";
			} else if (allJobDetail.isEmpty()) {
				entityName = "";
			} else {
				String key = (String) allJobDetail.keySet().toArray()[0];
				entityName = allJobDetail.get(key).getEntityName();
			}
			theWriter.setup(entityName, theSavePath, appProperties
					.getApplicationData().getMetsFTPServer(), appProperties
					.getApplicationData().getMetsFTPUser(), appProperties
					.getApplicationData().getMetsFTPPassword(), theIeRoot,
					theSystemUserID, metsRootDirectory,
					includeEntityNameInTitle, appProperties
							.getApplicationData().isIncludeFileDates(), theFixityType);
		} catch (Exception ex) {
			handleException(ex);
		}
		checkDirectoryDoesntExist(theWriter.getEntityDirectory());
	}

	public static UploadJob create(String batchName, String entityName,
			String creator, FileGroupCollection entities,
			StructMapCollection structures, MetaDataFields theMetaData,
			String producerID, String materialFlowID,
			AppProperties appProperties, String baseDirectory,
			boolean deleteRootDir, boolean createCopy,
			boolean checkForIncludeName, String fixityType) throws FileNotFoundException,
			JobQueueException {
		return new UploadJob(batchName, entityName, creator, entities,
				structures, theMetaData, producerID, materialFlowID,
				appProperties, baseDirectory, deleteRootDir, createCopy,
				checkForIncludeName, fixityType);

	}

	public static UploadJob create(String batchName, String entityName,
			String creator, FileGroupCollection entities,
			StructMapCollection structures, MetaDataFields theMetaData,
			String producerID, String materialFlowID,
			AppProperties appProperties, String baseDirectory,
			boolean deleteRootDir, String fixityType) throws FileNotFoundException,
			JobQueueException {
		return new UploadJob(batchName, entityName, creator, entities,
				structures, theMetaData, producerID, materialFlowID,
				appProperties, baseDirectory, deleteRootDir, true, true, fixityType);

	}

	private void checkDigestStatus() {
		printDebugInfo("Check digest status");
		if (theDigest == null) {
			return;
		}
		if (theDigest.getFinished()) {
			isCopyFinished = true;
			boolean allSucceeded = true;
			StringBuilder errorMessage = new StringBuilder();
			errorMessage
					.append("The following files were not copied successfully:");
			for (ChecksumDigest.FileStatus status : theDigest.getFileStatii()) {
				if (!status.getFileCopyIsIdentical()) {
					allSucceeded = false;
					errorMessage.append("\n");
					errorMessage.append(status.getFSO().getFullPath());
				}
			}
			if (allSucceeded) {
				for (ChecksumDigest.FileStatus status : theDigest.getFileStatii()) {
					FileSystemObject fso = status.getFSO();
					fso.setFile(fso.getDestinationFile());
				}
			} else {
				for (FileStatus status : theDigest.getFileStatii()) {
					File parent = status.getFSO().getDestinationFile()
							.getParentFile();
					LOG.debug("Deleting file "
							+ status.getFSO().getDestinationFile()
									.getAbsolutePath());
					status.getFSO().getDestinationFile().delete();
					FileUtils.deleteEmptyDirectoryRecursive(parent);
				}
			}

			if (theJobState == JobState.Requested) {
				theJobState = JobState.Pending;
			}
			saveJob(false);
			if (allSucceeded) {
				for (ChecksumDigest.FileStatus status : theDigest.getFileStatii()) {
					if (status.getFileCopyIsIdentical()) {
						FileSystemObject fso = status.getFSO();
						fso.setFile(fso.getDestinationFile());
					} else {
						allSucceeded = false;
						errorMessage.append("\n");
						errorMessage.append(status.getFSO().getFullPath());
					}
				}
			} else {
				jobDetailStatus = errorMessage.toString();
			}
		} else {
			Action checkDigestStatusAction = new AbstractAction() {
				private static final long serialVersionUID = 5562669711772031634L;

				public void actionPerformed(ActionEvent e) {
					Timer t = (Timer) e.getSource();
					t.stop();
					checkDigestStatus();
				}
			};
			new Timer(500, checkDigestStatusAction).start();
		}
	}

	public void prepareJobToRun() {
		theJobState = JobState.Requested;
		saveJob(false);
		for (JobDetail detail : getJobDetail()) {
			detail.reloadFromFile();
		}
		createCopy(false);
	}

	private void createCopy(boolean inReverse) {
		isCopyFinished = false;
		if (theTemporaryCleanupDirectory == null
				|| theTemporaryCleanupDirectory.equals("")) {
			isCopyFinished = true;
			return;
		}
		
		// If cleanup is off, then exit method and do not copy files to cleanup dir
		// Added by Ben. 9/9/2013
		if(theUserGroup.getCleanupType() == UserGroupData.ECleanupType.None){
			isCopyFinished = true;
			return;
		}
		
		theFiles = FSOCollection.create();
		for (String key : allJobDetail.keySet()) {
			JobDetail detail = allJobDetail.get(key);
			for (FileSystemObject fso : detail.getFileGroups().getAllFiles()) {
				if (!fso.getIsDirectory() || canDeleteDirectories) {
					theFiles.add(fso);
				}
			}
		}
		for (FileSystemObject fso : theFiles.getAllFiles()) {
			if (fso.getOriginalFilePath() == null) {
				fso.setOriginalFilePath();
			}
			fso.setBaseDirectory(theTemporaryCleanupDirectory);
			String destinationFileName = theTemporaryCleanupDirectory
					+ fso.getRelativePath(theIeRoot, false);
			try {
				fso.setDestinationFileName(destinationFileName);
			} catch (Exception ex) {
				LOG.error("Error setting destination file name", ex);
			}
		}
		try {
			boolean deleteAfterCopying = (theUserGroup.getCleanupType() != UserGroupData.ECleanupType.None);
			theDigest = ChecksumDigest.create(theFiles,
					theTemporaryCleanupDirectory, false, deleteAfterCopying,
					appProperties.getApplicationData().getPersonalSettings()
							.getNoOfRetries(), canDeleteDirectories, theFixityType);
			if (inReverse) {
				theDigest.setReverseMovement(true);
			}
			if (theDigest.lock()) {
				Thread t = new Thread(theDigest);
				t.start();
				theDigest.unlock();
			} else {
				try {
					Thread.sleep(100);
				} catch (Exception ex) {
				}
			}
			checkDigestStatus();
		} catch (Exception ex) {
			LOG.debug("Error creating copy", ex);
			return;
		}
	}

	public void addJob(String entityName, FileGroupCollection entity,
			StructMapCollection structures, MetaDataFields theMetaData)
			throws JobQueueException {
		if (allJobDetail.containsKey(entityName)) {
			throw new JobQueueException("Entity " + entityName
					+ " already exists in batch");
		}
		if ((theMetaData.getCMSDescription() == null)
				|| (theMetaData.getCMSDescription().equals(""))) {
			try {
				theMetaData.setCMSDescription(entityName);
			} catch (Exception ex) {
				LOG.error(ex.getMessage(), ex);
			}
		}
		// System.out.println("Add job: " + entityName + ", CMS ID: " +
		// theMetaData.getAt(0).getDataFieldValue());
		JobDetail detail = new JobDetail(entityName, entity, structures,
				theMetaData);
		allJobDetail.put(entityName, detail);
	}

	public UploadJob(String batchName, String entityName, String creator,
			FileGroupCollection entity, StructMapCollection structures,
			MetaDataFields theMetaData, String producerID,
			String materialFlowID, AppProperties applicationProperties,
			String baseDirectory, boolean deleteDirectories,
			boolean createCopy, boolean checkForIncludeName, String fixityType)
			throws FileNotFoundException, JobQueueException {
		theFixityType = fixityType;
		theBatchName = batchName;
		appProperties = applicationProperties;
		isCopyFinished = false;
		canDeleteDirectories = deleteDirectories;
		try {
			theUserGroup = appProperties.getUserData().getUser(
					appProperties.getLoggedOnUser()).getUserGroupData();
		} catch (Exception ex) {
			// TODO fix this so it returns an error
		}
		theDepositDateTime = new Date();
		theBaseDirectory = baseDirectory;
		shouldCheckForIncludeName = checkForIncludeName;
		String interimDirectory = theUserGroup.getInterimFileLocation();
		if (interimDirectory != null && !interimDirectory.equals("")) {
			FileUtils.ensureDirectoryExists(interimDirectory);
			try {
				theTemporaryCleanupDirectory = new File(interimDirectory + "/"
						+ FileUtils.specialCharToUnderscore(theBatchName)
						+ theDateFormatFileName.format(theDepositDateTime))
						.getCanonicalPath();
			} catch (Exception ex) {
				theTemporaryCleanupDirectory = new File(interimDirectory + "/"
						+ FileUtils.specialCharToUnderscore(theBatchName)
						+ theDateFormatFileName.format(theDepositDateTime))
						.getAbsolutePath();
			}
		}

		String ieRoot = entity.getEntityRoot();
		File realPathFile = new File(ieRoot);
		try {
			ieRoot = realPathFile.getCanonicalPath();
		} catch (Exception ex) {
			ieRoot = realPathFile.getAbsolutePath();
		}
		theIeRoot = ieRoot;
		theJobQueuePath = appProperties.getApplicationData().getJobQueuePath();
		theSavePath = appProperties.getApplicationData().getMetsSavePath();
		theSystemUserID = appProperties.getLoggedOnUser();
		theCreator = creator;
		theProducerID = producerID;
		theMaterialFlowID = materialFlowID;
		totalSize = 0;
		sizeCopied = 0;
		jobDetailStatus = "Waiting...";
		addJob(entityName, entity, structures, theMetaData);
		commonStartup(createCopy);
	}

	private void reloadFromFile() {
		for (String key : allJobDetail.keySet()) {
			JobDetail detail = allJobDetail.get(key);
			detail.reloadFromFile();
		}
	}

	public Date getDepositDateTime() {
		return theDepositDateTime;
	}

	private boolean isFTP() {
		return (appProperties.getApplicationData().getMetsFTPServer() != null);
	}

	private String cleanUpForID(String theName) {
		return theName.replace(" ", "_").replace(":", "_");
	}

	private String getEntityNamePadded() {
		String entityNamePadded = FileUtils.specialCharToUnderscore(theBatchName)
				+ "_" + theDateFormatFileName.format(theDepositDateTime);
		return entityNamePadded;
	}

	public void run() {
		Date startRunDate = new Date();
		theJobState = JobState.Running;
		Date startWaitCopyDate = new Date();
		long totalSetupTime = 0;
		long totalWriteTime = 0;
		long totalDepositTime = 0;
		while (!isCopyFinished) {
			try {
				Thread.sleep(100);
			} catch (Exception ex) {
			}
			if (canCancelJob) {
				jobDetailStatus = "Cancelling...";
				theJobState = JobState.Cancelled;
				return;
			}
		}
		Date endWaitCopyDate = new Date();
		try {
			String metsRootDirectory = cleanUpForID(theSystemUserID + "/"
					+ getEntityNamePadded());
			boolean includeEntityNameInTitle;
			if (shouldCheckForIncludeName) {
				includeEntityNameInTitle = (allJobDetail.size() >= 1);
			} else {
				includeEntityNameInTitle = false;
			}
			for (String key : allJobDetail.keySet()) {
				JobDetail detail = allJobDetail.get(key);
				jobDetailStatus = "Processing " + detail.getEntityName();
				Date startDate = new Date();
				theWriter = appProperties.getApplicationData().getMetsWriter();
				theWriter
						.setup(detail.getEntityName(), theSavePath,
								appProperties.getApplicationData()
										.getMetsFTPServer(), appProperties
										.getApplicationData().getMetsFTPUser(),
								appProperties.getApplicationData()
										.getMetsFTPPassword(), theIeRoot,
								theSystemUserID, metsRootDirectory,
								includeEntityNameInTitle, appProperties
										.getApplicationData()
										.isIncludeFileDates(), theFixityType);
				for (int i = 0; i < detail.getTheMetaData().size(); i++) {
					IMetaDataTypeExtended meta = detail.getTheMetaData().getAt(
							i);
					if (meta.getDataType() == EDataType.ProvenanceNote) {
						
						theWriter.addProvenanceNote(
								meta.getProvenanceNoteEventType(), 
								meta.getProvenanceEventIdentifierType(), 
								meta.getProvenanceEventIdentifierValue(),
								meta.getProvenanceEventOutcome(),
								meta.getProvenanceEventOutcomeDetail(), 
								meta.getProvenanceEventDescription(), Calendar.getInstance());
						
					} else if ((meta.getDataFieldValue() != null)
							&& (meta.getWillBeUploaded())) {
						theWriter.addDescriptiveMetaDataField(meta);
					}
				}
				theWriter.setCreator(System.getProperty(theCreator));
				theWriter.setEntities(detail.getFileGroups());
				theWriter.setStructure(detail.getStructures());
				Date endDate = new Date();
				totalSetupTime = endDate.getTime() - startDate.getTime();
				startDate = new Date();
				Thread t = new Thread(theWriter);
				t.start();
				while (!theWriter.isFinished() && !theWriter.isFailed()) {
					if (canCancelJob) {
						t.interrupt();
						jobDetailStatus = "Cancelling...";
						theJobState = JobState.Cancelled;
						return;
					} else {
						jobDetailStatus = theWriter.getStatus();
						Thread.sleep(100);
					}
				}
				endDate = new Date();
				totalWriteTime = endDate.getTime() - startDate.getTime();
				if (theWriter.isFailed()) {
					theJobState = JobState.Failed;
				}
				if (canCancelJob) {
					jobDetailStatus = "Cancelled";
					theJobState = JobState.Cancelled;
					break;
				} else if (theJobState.isFailure) {
					jobDetailStatus = "Job failed - "
							+ theWriter.getFailureMessage();
					break;
				} else {
					jobDetailStatus = theWriter.getStatus();
				}
			}
			if (!theJobState.isFailure) {
				LOG.info("Depositing job");
				Date startDate = new Date();
				IDeposit deposit = appProperties.getApplicationData()
						.getDeposit();
				ResultOfDeposit result = deposit.deposit(metsRootDirectory,
						theSystemUserID, appProperties
								.getLoggedOnUserPassword(), appProperties
								.getApplicationData()
								.getDepositUserInstitution(), theProducerID,
						theMaterialFlowID, appProperties.getApplicationData()
								.getDepositSetID());
				Date endDate = new Date();
				totalDepositTime = endDate.getTime() - startDate.getTime();
				LOG.info("Job deposited");
				if (result.isSuccess()) {
					theJobState = JobState.Deposited;
					theSipID = result.getSipID();
					jobDetailStatus = "Deposited.  SIP ID " + theSipID
							+ " added.";
				} else {
					theJobState = JobState.Failed;
					jobDetailStatus = result.getResultMessage();
				}
				// Swallow any exception - as we don't need the SIP status yet
				try {
					theSipStatus = appProperties.getApplicationData()
							.getDeposit().getSipStatus(theSipID);
				} catch (Exception ex) {
				}
			}
			saveJob(false);
		} catch (Exception ex) {
			handleException(ex);
		}
		Date endRunDate = new Date();
		long totalTime = endRunDate.getTime() - startRunDate.getTime();
		long totalWaitTime = endWaitCopyDate.getTime()
				- startWaitCopyDate.getTime();
		System.out
				.println(String
						.format(
								"Total time: %d, wait time: %d, setup mets time: %d, write mets time: %d, deposit time: %d",
								totalTime, totalWaitTime, totalSetupTime,
								totalWriteTime, totalDepositTime));
	}

	public void resubmitJob() {
		jobDetailStatus = "Waiting...";
		theJobState = JobState.Pending;
		tidyUpJob();
		saveJob(false);
	}

	private void printDebugInfo(String message) {
		String finalMessage = message + ", copy finished? "
				+ String.format("%b ", isCopyFinished);
		if (theDigest != null) {
			finalMessage += "Digest not null, digest finished: "
					+ String.format("%b", theDigest.getFinished());
		}
		LOG.debug(finalMessage);
		// System.out.println(finalMessage);
	}

	public void cancelJob() {
		printDebugInfo("Cancel job");
		// This is a little bit complicated because of the various threads we
		// have running
		jobDetailStatus = "Cancelling job, please wait...";
		// Setting this will tell the Run procedure that it needs to finish
		// ***IMPORTANT***
		// It will also tell the CheckDigestStatus thread that it needs to
		// finish
		// ***IMPORTANT***
		canCancelJob = true;
		// Digest is probably not still running, but need to make sure
		// Wait until the digest is finished & the Run thread notices that we
		// are cancelling
		while (!isCopyFinished || (theJobState == JobState.Running)) {
			printDebugInfo("Sleeping...");
			try {
				Thread.sleep(100);
			} catch (Exception ex) {
			}
			if (theDigest != null) {
				if (theDigest.getFinished() && !isCopyFinished) {
					isCopyFinished = true;
				}
			}
		}
		// ***IMPORTANT***
		// Now that everything has finished with the cancelJob variable we need
		// to unset it
		// The CreateCopy procedure will kick off another CheckDigestStatus
		// thread and it will need to run
		// ***IMPORTANT***
		canCancelJob = false;

		printDebugInfo("Before create copy");
		createCopy(true);
		printDebugInfo("After create copy");
		while (!isCopyFinished) {
			if (theDigest.getFinished()) {
				checkDigestStatus();
			}
			try {
				Thread.sleep(100);
			} catch (Exception ex) {
			}
			printDebugInfo("After Sleep");
		}
		tidyUpJob();
		deleteJob();
	}

	// Only deletes the final destination files on ftp server and not the files in the cleanup directory.
	public void tidyUpJob() {
		jobDetailStatus = "Tidying up files...";
		theWriter.tidyUpFiles();
	}

	public void checkForCleanup() {
		Date currentDateTime = new Date();
		if (theJobState == JobState.AwaitingCleanup
				&& currentDateTime.after(theCleanupDate)) {
			theJobState = JobState.Complete;
			deleteJob();
		}
	}

	public void checkSipStatus() {
		try {
			if (theSipID != null) {
				SIPStatus newStatus = appProperties.getApplicationData()
						.getDeposit().getSipStatus(theSipID);
				if (!theSipStatus.equals(newStatus)) {
					theSipStatus = newStatus;
					if (theSipStatus.isSuccessState()) {
						theJobState = JobState.AwaitingCleanup;
						Calendar cal = new GregorianCalendar();
						if (theUserGroup.getCleanupType() == UserGroupData.ECleanupType.Delayed) {
							
							cal.add(Calendar.DATE, theUserGroup
									.getCleanupDelay());
							theCleanupDate = cal.getTime();
						} else {
							//The completed jobs got deleted immediately before user could record the sip id
							//so adding one day's delay to the cleanup date.					
							cal.add(Calendar.DATE, 1);
							theCleanupDate = cal.getTime();							
						}
					}
					if (theSipStatus.isNeedsTidying()) {
						
						/**The job will be deleted from indigoftp directory on ftp server ONLY if it is declined.
						* It will not remove it if the job moves to permanent.
						* For removing it completely in either cases remove the 'if' condition below.
						*/
						if (theSipStatus.getStatus().equals("Decline")){
							tidyUpJob();
						}
						
						
					}
					saveJob(false);
				}
			}
		} catch (Exception ex) {
			jobDetailStatus = String
					.format(
							"SIP ID %s.  Could not retrieve SIP status.  Error message is %s",
							theSipID, ex.getMessage());
		}
	}

	public void deleteJob() {
		jobDetailStatus = "Deleting job...";
		canCancelJob = true;
		theDigest = null;
		FileUtils.deleteFileOrDirectoryRecursive(theTemporaryCleanupDirectory);
		FileUtils.deleteFileOrDirectoryRecursive(theXmlFileName);
		FileUtils.deleteFileOrDirectoryRecursive(theJobDirectory);
	}

	public void saveJob(boolean withReload) {
		FileUtils.ensureDirectoryExists(theJobDirectory);
		XMLHandler handler;
		try {
			File xmlFile = new File(theXmlFileName);
			if (xmlFile.exists()) {
				xmlFile.delete();
			}
			// Added createCopy(false) to shift copying files to cleanup folder to after last job has been added.
			// 20.Aug.2013 by Ben.
			if (!isCopyFinished) {
				createCopy(false);
			}
			handler = new XMLHandler(XML_ROOT_NAME, theXmlFileName);
			XMLObject object = handler.createXMLObject("EntityDescription",
					theBatchName);
			object.addAttribute(BATCH_NAME_ATTR, theBatchName);
			object.addAttribute(SAVE_PATH_ATTR, theSavePath);
			object.addAttribute(SYSTEM_USER_ID_ATTR, theSystemUserID);
			object.addAttribute(IE_ROOT_ATTR, theIeRoot);
			object.addAttribute(PRODUCER_ID_ATTR, theProducerID);
			object.addAttribute(MATERIAL_FLOW_ID_ATTR, theMaterialFlowID);
			object.addAttribute(CREATOR_ATTR, theCreator);
			object
					.addAttribute(TOTAL_SIZE_ATTR, String.format("%d",
							totalSize));
			object.addAttribute(SIZE_COPIED_ATTR, String.format("%d",
					sizeCopied));
			object.addAttribute(STATUS_ATTR, jobDetailStatus);
			object.addAttribute(JOB_STATE_ATTR, theJobState.name());
			object.addAttribute(JOB_QUEUE_PATH_ATTR, theJobQueuePath);
			object.addAttribute(SIP_ID_ATTR, theSipID);
			object.addAttribute(DEPOSIT_DATE_ATTR, theDateFormat
					.format(theDepositDateTime));
			object.addAttribute(BASE_DIRECTORY_ATTR, theBaseDirectory);
			object.addAttribute(TEMPORARY_CLEANUP_DIRECTORY_ATTR,
					theTemporaryCleanupDirectory);
			object.addAttribute(COPY_FINISHED_ATTR, String.format("%b",
					isCopyFinished));
			object.addAttribute(DELETE_DIRECTORIES_ATTR, String.format("%b",
					canDeleteDirectories));
			object.addAttribute(CHECK_FOR_INCLUDE_NAME, String.format("%b",
					shouldCheckForIncludeName));
			if (theCleanupDate != null) {
				object.addAttribute(CLEANUP_DATE_ATTR, theDateFormat
						.format(theCleanupDate));
			}
			for (String key : allJobDetail.keySet()) {
				JobDetail jobDetail = allJobDetail.get(key);
				jobDetail.saveJobDetail();
				XMLObject child = handler.createXMLObject("JobDetail", key,
						jobDetail.getXMLFileName());
				child.addAttribute(JOB_DETAIL_ATTR, jobDetail.getXMLFileName());
				object.addChild(key, child);
			}
			handler.addObject(object);
			handler.writeXMLFile();
		} catch (Exception ex) {
			ex.printStackTrace();
			LOG.error(ex.getMessage(), ex);
		}
		if (withReload) {
			reloadFromFile();
		}
	}

	private void checkDirectoryDoesntExist(String theDirectory)
			throws FileNotFoundException {
		File directoryFile = new File(theDirectory);
		if (directoryFile.exists()) {
			throw new FileNotFoundException("Cannot create directory ("
					+ theDirectory + ") as it already exists");
		}
	}

	private String getSipDepositDetails() {
		return String.format(" (SIP ID %s deposited %s)", theSipID,
				theDateFormat.format(theDepositDateTime));
	}

	public String getStatus() {
		if (theSipStatus.isUnknown()) {
			String status = jobDetailStatus;
			if (theSipID != null) {
				status += getSipDepositDetails();
			}
			return status;
		} else {
			return theSipStatus + getSipDepositDetails();
		}
	}

	public JobState getJobState() {
		return theJobState;
	}

	public String getErrorText() {
		return theErrorText;
	}

	public boolean getCancelled() {
		return canCancelJob;
	}

	public boolean getIsSelected() {
		return isSelected;
	}

	public void setIsSelected(boolean value) {
		isSelected = value;
	}

	public String getSIPID() {
		return theSipID;
	}

	public void setSIPStatus(String value) {
		theSipID = value;
	}

	public SIPStatus getSIPStatus() {
		return theSipStatus;
	}

	public boolean getSIPSucceeded() {
		return (theSipStatus.isSuccessState());
	}
}
