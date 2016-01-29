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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import nz.govt.natlib.ndha.common.FileUtils;
import nz.govt.natlib.ndha.common.PropertiesUtil;
import nz.govt.natlib.ndha.common.XMLHandler;
import nz.govt.natlib.ndha.common.XMLHandler.XMLObject;
import nz.govt.natlib.ndha.common.exceptions.XmlException;
import nz.govt.natlib.ndha.common.exlibris.IDeposit;
import nz.govt.natlib.ndha.common.exlibris.ILogin;
import nz.govt.natlib.ndha.common.mets.MetsWriter;
import nz.govt.natlib.ndha.manualdeposit.exceptions.InvalidApplicationDataException;
import nz.govt.natlib.ndha.manualdeposit.exceptions.InvalidCMSSystemException;
import nz.govt.natlib.ndha.manualdeposit.exceptions.InvalidDepositSystemException;
import nz.govt.natlib.ndha.manualdeposit.exceptions.InvalidLoginSystemException;
import nz.govt.natlib.ndha.manualdeposit.exceptions.InvalidMetsWriterException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasypt.util.text.BasicTextEncryptor;

public final class ApplicationData {

	private final static Log LOG = LogFactory.getLog(ApplicationData.class);
	private final static String STANDARD_OBJECT_TYPE = "ApplicationData";
	private final static String PROP_METS_COPY_LOC = "MetsCopyLocation";
	private final static String PROP_METS_FTP_SERVER = "MetsFTPServer";
	private final static String PROP_METS_FTP_USER = "MetsFTPUser";
	private final static String PROP_METS_FTP_PASSWD = "MetsFTPPassword";
	private final static String PROP_QUEUE_LOC = "QueueLocation";
	private final static String PROP_BULK_LOC = "BulkQueueLocation";
	private final static String PROP_MAX_JOBS = "MaximumJobsRunning";
	private final static String PROP_QUEUE_REFRESH = "QueueRefreshInterval";
	private final static String PROP_METS_WRITER_CLASS = "MetsWriterClass";
	private final static String PROP_METADATA_USER_FILE = "MetaDataUserFile";
	private final static String PROP_SERVER_SOURCE_DIR = "SourceDirOnServer";
	private final static String PROP_DEPOSIT_WSDL_URL = "depositWsdlUrl";
	private final static String PROP_PRODUCER_WSDL_URL = "producerWsdlUrl";
	private final static String PROP_REPOSITORY_WSDL_URL = "repositoryWsdlUrl";
	private final static String PROP_PDS_URL = "pdsURL";
	private final static String PROP_CMS_2_SEARCH_URL = "cms2SearchURL";
	private final static String PROP_DPS_SEARCH_URL = "DPSSearchURL";
	private final static String PROP_CONTENT_AGGREGATOR_URL = "ContentAggregatorURL";
	private final static String PROP_CMS_1_SEARCH_URL = "cms1SearchURL";
	private final static String PROP_DEPOSIT_SET_ID = "DepositSetID";
	private final static String PROP_CMS_1_SEARCH_Z39 = "UseZ39SearchForCMS1";
	private final static String PROP_USER_INSTITUTION = "DepositUserInstitution";
	private final static String PROP_SETTINGS_PATH = "SettingsPath";
	private final static String PROP_LOGIN_CLASS = "LoginClass";
	private final static String PROP_DEPOSIT_CLASS = "DepositClass";
	private final static String PROP_SIP_STATUS_REFRESH_TIME = "SipStatusRefreshRate";
	private final static String PROP_MAXIMUM_STRUCTURE_LENGTH = "MaximumStructureLength";
	private final static String PROP_MAXIMUM_PROVENANCE_EVENT_LENGTH = "MaximumProvenanceEventLength";
	private final static String PROP_SEARCH_STRATEGY_CLASS = "SearchStrategyClass";
	private final static String PROP_SIP_STATUS_FILE = "SipStatusFile";
	private final static String PROP_ENCRYPTION_SEED = "Z39TapuhiVoyager";
	private final static String PROP_SRU_SEARCH_SCHEMA = "SRUSearchSchema";
	private final static String PROP_CMS_1_SYSTEM_TEXT = "CMS1SystemText";
	private final static String PROP_CMS_1_LABEL = "CMS1Label";
	private final static String PROP_CMS_2_TEXT = "CMS2Text";
	private final static String PROP_CMS_2_LABEL = "CMS2Label";
	private final static String PROP_INCLUDE_FILE_DATES = "IncludeFileDates";
	private final static String PROP_BULK_QUERY_COUNT = "BulkQueryCount";
	private final static String Z39_STRATEGY = "nz.govt.natlib.ndha.common.ilsquery.Z3950SearchStrategyImpl";
	private final static String SRU_STRATEGY = "nz.govt.natlib.ndha.common.ilsquery.SruSearchStrategyImpl";

	private String metsSavePath = "";
	private String metsFTPServer = "";
	private String metsFTPUser = "";
	private String metsFTPPassword = "";
	private String jobQueuePath = "";
	private String bulkUploadQueuePath = "";
	private String settingsPath = "./";
	private String settingsStructTemplatePath = null;
	private String settingsMetaDataTemplatePath = null;
	private int jobQueueRefreshInterval = 1000;
	private int sipStatusRefreshInterval = 600000;
	private int maximumJobsRunning = 1;
	private int maximumStructureLength = 100;
	private int maximumProvenanceEventLength = 100;
	private String metsWriterClass = "nz.govt.natlib.ndha.common.mets.MetsWriterImpl";
	private String metaDataUserFile = "MetaDataUsers.xml";
	private String sourceDirOnServer = "";
	private String depositWsdlUrl = "";
	private String producerWsdlUrl = "";
	private String repositoryWsdlUrl = "";
	private String pdsUrl = "";
	private String dpsSearchUrl = "";
	private String contentAggregatorUrl = "";
	private String cms2SearchUrl = "";
	private String cms1SearchUrl = "";
	private String depositSetID = "";
	private boolean useZ39SearchForCMS1 = true;
	private boolean includeFileDates = true;
	private String depositUserInstitution = "";
	private String depositClass = "nz.govt.natlib.ndha.common.exlibris.DepositDummy";
	private IDeposit deposit = null;
	private boolean depositSetup = false;
	private String loginClass = "nz.govt.natlib.ndha.common.exlibris.Login";
	private ILogin login = null;
	private String searchStrategyClass = "nz.govt.natlib.ndha.common.ilsquery.SruSearchStrategyImpl";
	private String sipStatusFile = null;
	private String sruSearchSchema = "dps";
	private BasicTextEncryptor encryptor;
	private PersonalSettings personalSettings;
	private String cms1SystemText = "ilsdb";
	private String cms1Label = "Voyager";
	private String cms2Text = "tapuhi";
	private String cms2Label = "Tapuhi";
	private int bulkQueryCount = 5;
	private boolean dataLoaded = false;
	private static final ApplicationData INSTANCE = new ApplicationData();

	private ApplicationData() {
	}

	public static ApplicationData createNew() {
		return INSTANCE;
	}

	public static ApplicationData getInstance(final String xmlFileName)
			throws IOException {
		INSTANCE.loadData(xmlFileName);
		return INSTANCE;
	}

	public static ApplicationData getInstance()
			throws InvalidApplicationDataException {
		if (!INSTANCE.dataLoaded) {
			throw new InvalidApplicationDataException(
					"Application data not loaded");
		}
		return INSTANCE;
	}

	public String getMetsSavePath() {
		return metsSavePath;
	}

	public void setMetsSavePath(final String value) {
		metsSavePath = value;
	}

	public String getMetsFTPServer() {
		return metsFTPServer;
	}

	public void setMetsFTPServer(final String value) {
		metsFTPServer = value;
	}

	public String getMetsFTPUser() {
		return metsFTPUser;
	}

	public void setMetsFTPUser(final String value) {
		metsFTPUser = value;
	}

	public String getMetsFTPPassword() {
		return metsFTPPassword;
	}

	public void setMetsFTPPassword(final String value) {
		metsFTPPassword = value;
	}

	public String getJobQueuePath() {
		return jobQueuePath;
	}

	public void setJobQueuePath(final String value) {
		jobQueuePath = value;
	}

	public String getBulkUploadQueuePath() {
		return bulkUploadQueuePath;
	}

	public void setBulkUploadQueuePath(final String value) {
		bulkUploadQueuePath = value;
	}

	public int getJobQueueRefreshInterval() {
		return jobQueueRefreshInterval;
	}

	public void setJobQueueRefreshInterval(final int value) {
		jobQueueRefreshInterval = value;
	}

	public int getSipStatusRefreshInterval() {
		return sipStatusRefreshInterval;
	}

	public void setSipStatusRefreshInterval(final int value) {
		sipStatusRefreshInterval = value;
	}

	public int getMaximumStructureLength() {
		return maximumStructureLength;
	}

	public void setMaximumStructureLength(final int value) {
		maximumStructureLength = value;
	}

	public int getMaximumProvenanceEventLength() {
		return maximumProvenanceEventLength;
	}

	public void setMaximumProvenanceEventLength(final int value) {
		maximumProvenanceEventLength = value;
	}

	public int getMaximumJobsRunning() {
		return maximumJobsRunning;
	}

	public void setMaximumJobsRunning(final int value) {
		maximumJobsRunning = value;
	}

	public String getMetsWriterClass() {
		return metsWriterClass;
	}

	public void setMetsWriterClass(final String value) {
		metsWriterClass = value;
	}

	public String getSipStatusFile() {
		return sipStatusFile;
	}

	public void setSipStatusFile(final String value) {
		sipStatusFile = value;
	}

	public MetsWriter getMetsWriter() throws InvalidMetsWriterException {
		final Class<?> cls;
		final MetsWriter metsWriter;
		try {
			cls = Class.forName(metsWriterClass);
		} catch (Exception ex) {
			throw new InvalidMetsWriterException("Class not found "
					+ metsWriterClass, ex);
		}
		try {
			metsWriter = (MetsWriter) cls.newInstance();
		} catch (Exception ex) {
			throw new InvalidMetsWriterException("Invalid class", ex);
		}
		return metsWriter;
	}

	public String getMetaDataUserFile() {
		return metaDataUserFile;
	}

	public void setMetaDataUserFile(final String value) {
		metaDataUserFile = value;
	}

	public String getSourceDirOnServer() {
		return sourceDirOnServer;
	}

	public void setSourceDirOnServer(final String value) {
		sourceDirOnServer = value;
	}

	public String getDepositWsdlUrl() {
		return depositWsdlUrl;
	}

	public void setDepositWsdlUrl(final String value) {
		depositWsdlUrl = value;
	}

	public String getProducerWsdlUrl() {
		return producerWsdlUrl;
	}

	public void setProducerWsdlUrl(final String value) {
		producerWsdlUrl = value;
	}

	public String getRepositoryWsdlUrl() {
		return repositoryWsdlUrl;
	}

	public void setRepositoryWsdlUrl(final String value) {
		repositoryWsdlUrl = value;
	}

	public String getPdsUrl() {
		return pdsUrl;
	}

	public void setPdsUrl(final String value) {
		pdsUrl = value;
	}

	public String getDPSSearchUrl() {
		return dpsSearchUrl;
	}

	public void setDPSSearchUrl(final String value) {
		dpsSearchUrl = value;
	}

	public String getContentAggregatorUrl() {
		return contentAggregatorUrl;
	}

	public void setContentAggregatorUrl(final String value) {
		contentAggregatorUrl = value;
	}

	public String getCMS1SearchUrl() {
		return cms1SearchUrl;
	}

	public void setCMS1SearchUrl(final String value) {
		cms1SearchUrl = value;
	}

	public String getCMS2SearchUrl() {
		return cms2SearchUrl;
	}

	public void setCMS2SearchUrl(final String value) {
		cms2SearchUrl = value;
	}

	public String getDepositSetID() {
		return depositSetID;
	}

	public void setDepositSetID(final String value) {
		depositSetID = value;
	}

	public String getCMSSystemText(final MetaDataFields.ECMSSystem cmsSystem)
			throws InvalidCMSSystemException {
		String result = "";
		switch (cmsSystem) {
		case CMS2:
			result = cms2Text;
			break;
		case CMS1:
			result = cms1SystemText;
			break;
		case NoSystem:
			result = "No CMS Reference";
			break;
		case StaffMediated:
			result = "Staff Mediated";
			break;
		default:
			throw new InvalidCMSSystemException("Unknown");
		}
		return result;
	}

	public String getCMS1SystemText() {
		return cms1SystemText;
	}

	public void setCMS1SystemText(final String value) {
		cms1SystemText = value;
	}
	
	public String getCMS1Label() {
		return cms1Label;
	}

	public void setCMS1Label(final String value) {
		cms1Label = value;
	}

	public String getCMS2Text() {
		return cms2Text;
	}

	public void setCMS2Text(final String value) {
		cms2Text = value;
	}
	
	public String getCMS2Label() {
		return cms2Label;
	}

	public void setCMS2Label(final String value) {
		cms2Label = value;
	}

	public boolean isUseZ39SearchForCMS1() {
		return useZ39SearchForCMS1;
	}

	public void setUseZ39SearchForCMS1(final boolean value) {
		useZ39SearchForCMS1 = value;
	}

	public boolean isIncludeFileDates() {
		return includeFileDates;
	}

	public void setIncludeFileDates(final boolean value) {
		includeFileDates = value;
	}

	public String getDepositUserInstitution() {
		return depositUserInstitution;
	}

	public void setDepositUserInstitution(final String value) {
		depositUserInstitution = value;
	}

	public String getSettingsPath() {
		return settingsPath;
	}

	public void setSettingsPath(final String value)
			throws FileNotFoundException {
		settingsPath = value;
		personalSettings = PersonalSettings.create(settingsPath);
	}

	public String getStructTemplatePath() {
		return settingsStructTemplatePath;
	}

	public String getMetaDataTemplatePath() {
		return settingsMetaDataTemplatePath;
	}

	public void setDepositClass(final String value) {
		depositClass = value;
	}

	public String getDepositClass() {
		return depositClass;
	}

	public void setSruSearchSchema(final String value) {
		sruSearchSchema = value;
	}

	public String getSruSearchSchema() {
		return sruSearchSchema;
	}

	public IDeposit getDeposit() throws InvalidDepositSystemException {
		return getDeposit(true);
	}

	public IDeposit getDeposit(final boolean includeSetup)
			throws InvalidDepositSystemException {
		if (deposit == null) {
			final Class<?> cls;
			try {
				cls = Class.forName(depositClass);
			} catch (Exception ex) {
				throw new InvalidDepositSystemException("Class not found "
						+ depositClass, ex);
			}
			try {
				deposit = (IDeposit) cls.newInstance();
			} catch (Exception ex) {
				throw new InvalidDepositSystemException("Invalid class", ex);
			}
		}
		if (!depositSetup && includeSetup) {
			if (includeSetup) {
				depositSetup = true;
				try {
					deposit.setup(depositWsdlUrl, producerWsdlUrl,
							repositoryWsdlUrl, pdsUrl, sipStatusFile);
				} catch (Exception ex) {
					LOG.error("Deposit setup failed", ex);
					throw new InvalidDepositSystemException(
							"Deposit setup failed", ex);
				}
			}
		}
		return deposit;
	}

	public void setLoginClass(final String value) {
		loginClass = value;
	}

	public String getLoginClass() {
		return loginClass;
	}

	public ILogin getLogin() throws InvalidLoginSystemException {
		if (login == null) {
			final Class<?> cls;
			try {
				cls = Class.forName(loginClass);
			} catch (Exception ex) {
				throw new InvalidLoginSystemException("Class not found "
						+ loginClass, ex);
			}
			try {
				login = (ILogin) cls.newInstance();
			} catch (Exception ex) {
				throw new InvalidLoginSystemException("Invalid class", ex);
			}
		}
		return login;
	}

	public void setSearchStrategyClass(final String value) {
		searchStrategyClass = value;
	}

	public String getSearchStrategyClass() {
		return searchStrategyClass;
	}

	public PersonalSettings getPersonalSettings() {
		return personalSettings;
	}

	public void setBulkQueryCount(final int value) {
		bulkQueryCount = value;
	}

	public int getBulkQueryCount() {
		return bulkQueryCount;
	}

	public void loadData(final String fileName) throws IOException {
		String xmlFileName = fileName;
		if (xmlFileName == null) {
			return;
		}
		File xmlFile = new File(xmlFileName);
		LOG.debug(xmlFile.getCanonicalFile());
		if (!xmlFile.exists()) { // Search for it
			final ClassLoader contextClassLoader = PropertiesUtil
					.getContextClassLoaderInternal();
			final Enumeration<URL> urls = PropertiesUtil.getResources(
					contextClassLoader, xmlFileName);
			if ((urls == null) || (!urls.hasMoreElements())) {
				LOG.error("Application Data file not found - " + xmlFileName);
				throw new FileNotFoundException(xmlFileName + " not found");
			}
			final URL xmlFileNameURL = (URL) urls.nextElement();
			xmlFile = new File(xmlFileNameURL.getPath());
			xmlFileName = xmlFile.getPath();
		}
		loadFromXML(xmlFileName);
		dataLoaded = true;
	}

	public void storeAsXML(final String xmlFileName) throws IOException,
			XmlException {
		final XMLHandler handler = new XMLHandler(MetaDataTypeImpl
				.getStandardObjectType(), xmlFileName);
		try {
			final List<XMLObject> objects = new ArrayList<XMLObject>();
			final XMLObject documentation = XMLObject.create("Documentation");
			final String documentationDetail = "To edit this file, use Indigo.exe /MetaData";
			documentation.setObjectValue(documentationDetail);
			objects.add(documentation);
			XMLObject object = getXMLObject(handler);
			objects.add(object);
			handler.setObjects(objects);
		} catch (Exception ex) {
			LOG.error(ex.getMessage(), ex);
		}
		handler.writeXMLFile();
	}

	public void loadFromXML(final String xmlFileName) throws IOException {
		final XMLHandler handler = new XMLHandler(MetaDataTypeImpl
				.getStandardObjectType(), xmlFileName);
		final XMLObject object = handler.getObject(STANDARD_OBJECT_TYPE);
		if (object != null) {
			setFromXMLObject(object);
		}
	}

	private BasicTextEncryptor getEncryptor() {
		if (encryptor == null) {
			encryptor = new BasicTextEncryptor();
			encryptor.setPassword(PROP_ENCRYPTION_SEED);
		}
		return encryptor;
	}

	public XMLObject getXMLObject(final XMLHandler handler) {
		final XMLObject object = handler.createXMLObject(STANDARD_OBJECT_TYPE,
				STANDARD_OBJECT_TYPE);
		object.setObjectType(STANDARD_OBJECT_TYPE);
		object.setObjectName(STANDARD_OBJECT_TYPE);
		object.addAttribute(PROP_METS_COPY_LOC, metsSavePath);
		object.addAttribute(PROP_METS_FTP_SERVER, metsFTPServer);
		object.addAttribute(PROP_METS_FTP_USER, metsFTPUser);
		String password = getEncryptor().encrypt(metsFTPPassword);
		object.addAttribute(PROP_METS_FTP_PASSWD, password);
		object.addAttribute(PROP_QUEUE_LOC, jobQueuePath);
		object.addAttribute(PROP_BULK_LOC, bulkUploadQueuePath);
		object.addAttribute(PROP_MAX_JOBS, String.format("%d",
				maximumJobsRunning));
		object.addAttribute(PROP_QUEUE_REFRESH, String.format("%d",
				jobQueueRefreshInterval));
		object.addAttribute(PROP_SIP_STATUS_REFRESH_TIME, String.format("%d",
				sipStatusRefreshInterval));
		object.addAttribute(PROP_MAXIMUM_STRUCTURE_LENGTH, String.format("%d",
				maximumStructureLength));
		object.addAttribute(PROP_MAXIMUM_PROVENANCE_EVENT_LENGTH, String
				.format("%d", maximumProvenanceEventLength));
		object.addAttribute(PROP_METS_WRITER_CLASS, metsWriterClass);
		object.addAttribute(PROP_METADATA_USER_FILE, metaDataUserFile);
		object.addAttribute(PROP_SERVER_SOURCE_DIR, sourceDirOnServer);
		object.addAttribute(PROP_DEPOSIT_WSDL_URL, depositWsdlUrl);
		object.addAttribute(PROP_PRODUCER_WSDL_URL, producerWsdlUrl);
		object.addAttribute(PROP_REPOSITORY_WSDL_URL, repositoryWsdlUrl);
		object.addAttribute(PROP_DPS_SEARCH_URL, dpsSearchUrl);
		object.addAttribute(PROP_CONTENT_AGGREGATOR_URL, contentAggregatorUrl);
		object.addAttribute(PROP_CMS_1_SEARCH_URL, cms1SearchUrl);
		object.addAttribute(PROP_CMS_2_SEARCH_URL, cms2SearchUrl);
		object.addAttribute(PROP_PDS_URL, pdsUrl);
		object.addAttribute(PROP_DEPOSIT_SET_ID, depositSetID);
		object.addAttribute(PROP_CMS_1_SYSTEM_TEXT, cms1SystemText);
		object.addAttribute(PROP_CMS_1_LABEL, cms1Label);
		object.addAttribute(PROP_CMS_2_TEXT, cms2Text);
		object.addAttribute(PROP_CMS_2_LABEL, cms2Label);
		object.addAttribute(PROP_CMS_1_SEARCH_Z39, String.format("%b",
				useZ39SearchForCMS1));
		object.addAttribute(PROP_INCLUDE_FILE_DATES, String.format("%b",
				includeFileDates));
		object.addAttribute(PROP_BULK_QUERY_COUNT, String.format("%d",
				bulkQueryCount));
		object.addAttribute(PROP_USER_INSTITUTION, depositUserInstitution);
		object.addAttribute(PROP_SETTINGS_PATH, settingsPath);
		object.addAttribute(PROP_LOGIN_CLASS, loginClass);
		object.addAttribute(PROP_DEPOSIT_CLASS, depositClass);
		object.addAttribute(PROP_SEARCH_STRATEGY_CLASS, searchStrategyClass);
		object.addAttribute(PROP_SIP_STATUS_FILE, sipStatusFile);
		object.addAttribute(PROP_SRU_SEARCH_SCHEMA, sruSearchSchema);
		return object;
	}

	public void setFromXMLObject(final XMLObject object)
			throws FileNotFoundException {
		object.setObjectType(STANDARD_OBJECT_TYPE);
		if (object.getAttribute(PROP_METS_COPY_LOC) != null) {
			metsSavePath = object.getAttribute(PROP_METS_COPY_LOC);
		}
		if (object.getAttribute(PROP_METS_FTP_SERVER) != null) {
			metsFTPServer = object.getAttribute(PROP_METS_FTP_SERVER);
		}
		if (object.getAttribute(PROP_METS_FTP_USER) != null) {
			metsFTPUser = object.getAttribute(PROP_METS_FTP_USER);
		}
		if (object.getAttribute(PROP_METS_FTP_PASSWD) != null) {
			final String password = object.getAttribute(PROP_METS_FTP_PASSWD);
			if (password != null && !password.equals("")) {
				try {
					metsFTPPassword = getEncryptor().decrypt(password);
				} catch (Exception ex) {
					LOG.error("Password decryption error", ex);
					// Password was not encrypted
					// Don't want to throw an exception as there is a lot of
					// other processing to do
					// This needs to be handled by the receiving application
				}
			}
		}
		if (object.getAttribute(PROP_QUEUE_LOC) != null) {
			jobQueuePath = object.getAttribute(PROP_QUEUE_LOC);
		}
		if (object.getAttribute(PROP_BULK_LOC) != null) {
			bulkUploadQueuePath = object.getAttribute(PROP_BULK_LOC);
		}
		if (object.getAttribute(PROP_MAX_JOBS) != null) {
			try {
				maximumJobsRunning = Integer.parseInt(object
						.getAttribute(PROP_MAX_JOBS));
			} catch (Exception ex) {
				LOG.error(ex.getMessage(), ex);
			}
		}
		if (object.getAttribute(PROP_QUEUE_REFRESH) != null) {
			try {
				jobQueueRefreshInterval = Integer.parseInt(object
						.getAttribute(PROP_QUEUE_REFRESH));
			} catch (Exception ex) {
				LOG.error(ex.getMessage(), ex);
			}
		}
		if (object.getAttribute(PROP_SIP_STATUS_REFRESH_TIME) != null) {
			try {
				sipStatusRefreshInterval = Integer.parseInt(object
						.getAttribute(PROP_SIP_STATUS_REFRESH_TIME));
			} catch (Exception ex) {
				LOG.error(ex.getMessage(), ex);
			}
		}
		if (object.getAttribute(PROP_MAXIMUM_STRUCTURE_LENGTH) != null) {
			try {
				maximumStructureLength = Integer.parseInt(object
						.getAttribute(PROP_MAXIMUM_STRUCTURE_LENGTH));
			} catch (Exception ex) {
				LOG.error(ex.getMessage(), ex);
			}
		}
		if (object.getAttribute(PROP_MAXIMUM_PROVENANCE_EVENT_LENGTH) != null) {
			try {
				maximumProvenanceEventLength = Integer.parseInt(object
						.getAttribute(PROP_MAXIMUM_PROVENANCE_EVENT_LENGTH));
			} catch (Exception ex) {
				LOG.error(ex.getMessage(), ex);
			}
		}
		if (object.getAttribute(PROP_METS_WRITER_CLASS) != null) {
			metsWriterClass = object.getAttribute(PROP_METS_WRITER_CLASS);
		}
		if (object.getAttribute(PROP_METADATA_USER_FILE) != null) {
			metaDataUserFile = object.getAttribute(PROP_METADATA_USER_FILE);
		}
		if (object.getAttribute(PROP_SERVER_SOURCE_DIR) != null) {
			sourceDirOnServer = object.getAttribute(PROP_SERVER_SOURCE_DIR);
		}
		if (object.getAttribute(PROP_DEPOSIT_WSDL_URL) != null) {
			depositWsdlUrl = object.getAttribute(PROP_DEPOSIT_WSDL_URL);
		}
		if (object.getAttribute(PROP_PRODUCER_WSDL_URL) != null) {
			producerWsdlUrl = object.getAttribute(PROP_PRODUCER_WSDL_URL);
		}
		if (object.getAttribute(PROP_REPOSITORY_WSDL_URL) != null) {
			repositoryWsdlUrl = object.getAttribute(PROP_REPOSITORY_WSDL_URL);
		}
		if (object.getAttribute(PROP_DPS_SEARCH_URL) != null) {
			dpsSearchUrl = object.getAttribute(PROP_DPS_SEARCH_URL);
		}
		if (object.getAttribute(PROP_CONTENT_AGGREGATOR_URL) != null) {
			contentAggregatorUrl = object
					.getAttribute(PROP_CONTENT_AGGREGATOR_URL);
		}
		if (object.getAttribute(PROP_CMS_1_SEARCH_URL) != null) {
			cms1SearchUrl = object.getAttribute(PROP_CMS_1_SEARCH_URL);
		}
		if (object.getAttribute(PROP_CMS_2_SEARCH_URL) != null) {
			cms2SearchUrl = object.getAttribute(PROP_CMS_2_SEARCH_URL);
		}
		if (object.getAttribute(PROP_PDS_URL) != null) {
			pdsUrl = object.getAttribute(PROP_PDS_URL);
		}
		if (object.getAttribute(PROP_DEPOSIT_SET_ID) != null) {
			depositSetID = object.getAttribute(PROP_DEPOSIT_SET_ID);
		}
		if (object.getAttribute(PROP_CMS_1_SYSTEM_TEXT) != null) {
			cms1SystemText = object.getAttribute(PROP_CMS_1_SYSTEM_TEXT);
		}
		if (object.getAttribute(PROP_CMS_1_LABEL) != null) {
			cms1Label = object.getAttribute(PROP_CMS_1_LABEL);
		}
		if (object.getAttribute(PROP_CMS_2_TEXT) != null) {
			cms2Text = object.getAttribute(PROP_CMS_2_TEXT);
		}
		if (object.getAttribute(PROP_CMS_2_LABEL) != null) {
			cms2Label = object.getAttribute(PROP_CMS_2_LABEL);
		}
		if (object.getAttribute(PROP_CMS_1_SEARCH_Z39) != null) {
			try {
				useZ39SearchForCMS1 = Boolean.parseBoolean(object
						.getAttribute(PROP_CMS_1_SEARCH_Z39));
			} catch (Exception ex) {
				LOG.error(ex.getMessage(), ex);
			}
		}
		if (object.getAttribute(PROP_INCLUDE_FILE_DATES) != null) {
			try {
				includeFileDates = Boolean.parseBoolean(object
						.getAttribute(PROP_INCLUDE_FILE_DATES));
			} catch (Exception ex) {
				LOG.error(ex.getMessage(), ex);
			}
		}
		if (object.getAttribute(PROP_BULK_QUERY_COUNT) != null) {
			try {
				bulkQueryCount = Integer.parseInt(object
						.getAttribute(PROP_BULK_QUERY_COUNT));
			} catch (Exception ex) {
				LOG.error(ex.getMessage(), ex);
			}
		}
		object.addAttribute(PROP_BULK_QUERY_COUNT, String.format("%d",
				bulkQueryCount));
		if (object.getAttribute(PROP_USER_INSTITUTION) != null) {
			depositUserInstitution = object.getAttribute(PROP_USER_INSTITUTION);
		}
		if (object.getAttribute(PROP_SETTINGS_PATH) != null) {
			settingsPath = object.getAttribute(PROP_SETTINGS_PATH);
			personalSettings = PersonalSettings.create(settingsPath);
			final File settingsFile = new File(settingsPath);
			if ((!settingsFile.exists()) || (!settingsFile.isDirectory())) {
				FileUtils.ensureDirectoryExists(settingsFile);
			}
			settingsStructTemplatePath = settingsPath
					+ "/Templates/StructureMap";
			FileUtils.ensureDirectoryExists(settingsStructTemplatePath);
			settingsMetaDataTemplatePath = settingsPath + "/Templates/MetaData";
			FileUtils.ensureDirectoryExists(settingsMetaDataTemplatePath);
		}
		if (object.getAttribute(PROP_LOGIN_CLASS) != null) {
			loginClass = object.getAttribute(PROP_LOGIN_CLASS);
		}
		if (object.getAttribute(PROP_DEPOSIT_CLASS) != null) {
			depositClass = object.getAttribute(PROP_DEPOSIT_CLASS);
		}
		if (object.getAttribute(PROP_SEARCH_STRATEGY_CLASS) != null) {
			searchStrategyClass = object
					.getAttribute(PROP_SEARCH_STRATEGY_CLASS);
			if (searchStrategyClass.equalsIgnoreCase(Z39_STRATEGY)) {
				searchStrategyClass = Z39_STRATEGY;
			} else if (searchStrategyClass.equalsIgnoreCase(SRU_STRATEGY)) {
				searchStrategyClass = SRU_STRATEGY;
			}
		}
		if (object.getAttribute(PROP_SIP_STATUS_FILE) != null) {
			sipStatusFile = object.getAttribute(PROP_SIP_STATUS_FILE);
		}
		if (object.getAttribute(PROP_SRU_SEARCH_SCHEMA) != null) {
			sruSearchSchema = object.getAttribute(PROP_SRU_SEARCH_SCHEMA);
		}
	}
}
