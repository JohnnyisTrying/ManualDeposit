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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import nz.govt.natlib.ndha.common.exlibris.SIPStatus;
import nz.govt.natlib.ndha.common.exlibris.SIPStatusCollection;
import nz.govt.natlib.ndha.manualdeposit.AppProperties;
import nz.govt.natlib.ndha.manualdeposit.exceptions.CharacterTranslationException;
import nz.govt.natlib.ndha.manualdeposit.exceptions.MetaDataException;
import nz.govt.natlib.ndha.manualdeposit.metadata.UserGroupData.ECharacterPosition;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MetaDataConfiguratorPresenter {

	private final static Log LOG = LogFactory
			.getLog(MetaDataConfiguratorPresenter.class);
	private final IMetaDataConfigurator configuratorFrame;
	private JList theUserList;
	private JList theDataList;
	private JComboBox theCmbCharacterPosition;
	private JTextField theTxtTranslateFrom;
	private JTextField theTxtTranslateTo;
	private JList theLstCharacterTranslations;
	private JPanel theDataLookupFrame;
	private JList theDataLookupList;
	private JComboBox theCmsFieldCombo;
	private JList theCmsMappingsList;
	private JComboBox theCmbDataType;
	private JList theDnxTypeList;
	private JList tehDnxDetailList;
	private JList theDcTypeList;
	private JList theDcXsiList;
	private JList theDcTermsTypeList;
	private JTabbedPane theDnxDcPane;
	private MetaDataFields metaData;
	private String chosenFileName;
	private File applicationConfigFile;
	private File userConfigFile;
	private File userGroupConfigFile;
	private File metaDataConfigFile;
	private File sipStatusConfigFile;
	private boolean formIsDirty = false;
	private AppProperties theAppProperties;
	private ApplicationData applicationData;
	private UserData userData;
	private UserGroupData userGroupData;
	private SIPStatusCollection sipStatusCollection;
	private JFileChooser chooser = new JFileChooser();
	private JList theSipList;
	private JLabel theLblCleanupDirectory;
	private JLabel theLblDelay;
	private JTextField theTxtCleanupDelay;
	private JComboBox theCmbCleanupType;
	private JComboBox theCmbUserGroupDesc;
	private String errorHeader = "An error occurred";

	public MetaDataConfiguratorPresenter(IMetaDataConfigurator theFrame) {
		configuratorFrame = theFrame;
	}

	private FileFilter xmlFilter = new FileFilter() {
		public boolean accept(File file) {
			return file.isDirectory()
					|| file.getName().toLowerCase().endsWith(".xml");
		}

		public String getDescription() {
			return "XML Files";
		}
	};

	private FileFilter propertiesFilter = new FileFilter() {
		public boolean accept(File file) {
			return file.isDirectory()
					|| file.getName().toLowerCase().endsWith(".properties");
		}

		public String getDescription() {
			return "properties Files";
		}
	};

	private FileFilter directoryFilter = new FileFilter() {
		public boolean accept(File file) {
			return file.isDirectory();
		}

		public String getDescription() {
			return "directories";
		}
	};

	public void setup(){
		try {
			theAppProperties = new AppProperties();
		} catch (IOException ex) {
		}
		
		if (theAppProperties.getApplicationData() != null) {
			configuratorFrame.setupScreen(theAppProperties.getApplicationData()
					.getSettingsPath());
		} else {
			configuratorFrame.setupScreen(null);
		}
		configuratorFrame.showView();
		chooser.setFileFilter(xmlFilter);
		chooser.setAcceptAllFileFilterUsed(false);
	}

	public void addUserHandlers(JList userList) {
		theUserList = userList;
		DefaultListModel model = new DefaultListModel();
		theUserList.setModel(model);
		theUserList
				.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
					public void valueChanged(
							javax.swing.event.ListSelectionEvent evt) {
						lstUsersValueChanged(evt);
					}
				});
	}

	public void addUserGroupHandlers(JLabel lblCleanupDirectory,
			JLabel lblDelay, JTextField txtCleanupDelay,
			JComboBox cmbCleanupType, JList lstCharacterTranslations,
			JComboBox cmbCharacterPosition, JTextField txtTranslateFrom,
			JTextField txtTranslateTo, JComboBox cmbUserGroupDesc) {
		theLblCleanupDirectory = lblCleanupDirectory;
		theLblDelay = lblDelay;
		theTxtCleanupDelay = txtCleanupDelay;
		theCmbCleanupType = cmbCleanupType;
		theLstCharacterTranslations = lstCharacterTranslations;
		theCmbCharacterPosition = cmbCharacterPosition;
		theTxtTranslateFrom = txtTranslateFrom;
		theTxtTranslateTo = txtTranslateTo;
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		for (UserGroupData.ECleanupType cleanupType : UserGroupData.ECleanupType
				.values()) {
			model.addElement(cleanupType);
		}
		theCmbCleanupType.setModel(model);
		theCmbCleanupType
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						cmbCleanupTypeActionPerformed(evt);
					}
				});
		theCmbUserGroupDesc = cmbUserGroupDesc;
		model = new DefaultComboBoxModel();
		for (UserGroupData.UserGroupDesc userGroupDesc : UserGroupData.UserGroupDesc.values()) {
			model.addElement(userGroupDesc);
		}
		theCmbUserGroupDesc.setModel(model);
		theCmbCharacterPosition = cmbCharacterPosition;
		model = new DefaultComboBoxModel();
		for (UserGroupData.ECharacterPosition charPosition : UserGroupData.ECharacterPosition
				.values()) {
			model.addElement(charPosition);
		}
		theCmbCharacterPosition.setModel(model);
		DefaultListModel translationModel = new DefaultListModel();
		theLstCharacterTranslations.setModel(translationModel);
		loadTranslations();
		theLstCharacterTranslations
				.addListSelectionListener(new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent evt) {
						characterTranslationValueChanged(evt);
					}
				});
		theTxtTranslateFrom
				.addCaretListener(new javax.swing.event.CaretListener() {
					public void caretUpdate(javax.swing.event.CaretEvent evt) {
						userGroupCaretUpdate(evt);
					}
				});
		theTxtTranslateTo
				.addCaretListener(new javax.swing.event.CaretListener() {
					public void caretUpdate(javax.swing.event.CaretEvent evt) {
						userGroupCaretUpdate(evt);
					}
				});
		checkCleanupItems();
	}

	private void checkCleanupItems() {
		UserGroupData.ECleanupType selectedItem = (UserGroupData.ECleanupType) theCmbCleanupType
				.getSelectedItem();
		switch (selectedItem) {
		case Delayed:
			theLblCleanupDirectory.setVisible(true);
			theLblDelay.setVisible(true);
			theTxtCleanupDelay.setVisible(true);
			break;
		case Immediate:
			theLblCleanupDirectory.setVisible(true);
			theLblDelay.setVisible(false);
			theTxtCleanupDelay.setVisible(false);
			break;
		default:
			theLblCleanupDirectory.setVisible(false);
			theLblDelay.setVisible(false);
			theTxtCleanupDelay.setVisible(false);
			break;
		}
	}

	private void cmbCleanupTypeActionPerformed(java.awt.event.ActionEvent evt) {
		checkCleanupItems();
	}

	private void lstUsersValueChanged(javax.swing.event.ListSelectionEvent evt) {
		int selectedIndex = theUserList.getSelectedIndex();
		if (selectedIndex >= 0) {
			DefaultListModel model = (DefaultListModel) theUserList.getModel();
			configuratorFrame.showUser((IndigoUser) model.get(selectedIndex));
		}
	}

	public void addMainHandlers(JTabbedPane mainPane) {
	}

	public void addSipStatusHandlers(JList sipList) {
		theSipList = sipList;
		DefaultListModel model = new DefaultListModel();
		theSipList.setModel(model);
		theSipList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent evt) {
				sipListValueChanged(evt);
			}
		});
	}

	private void sipListValueChanged(ListSelectionEvent evt) {
		if (theSipList.getSelectedValue() != null) {
			showSipData();
			setDirty(false);
			configuratorFrame.checkButtons();
		}
	}

	private void showSipData() {
		SIPStatus data = (SIPStatus) theSipList.getSelectedValue();
		configuratorFrame.showSipStatus(data);
	}

	public void addMetaDataHandlers(JList dataList, JComboBox dataTypeCombo,
			JPanel dataLookupFrame, JList dataTypeListItems,
			JList cmsMappingsList, JComboBox cmsFieldCombo, JList dnxTypeList,
			JList dnxDetailList, JList dcTypeList, JList dcXsiList, JList dcTermsTypeList,
			JTabbedPane dnxDcPane) {
		theDataList = dataList;
		theDataList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent evt) {
				dataListValueChanged(evt);
			}
		});
		theCmbDataType = dataTypeCombo;
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		for (EDataType dataType : EDataType.values()) {
			model.addElement(dataType);
		}
		theCmbDataType.setModel(model);
		theCmbDataType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				cmbDataTypeActionPerformed(evt);
			}
		});
		theDataLookupFrame = dataLookupFrame;
		theDataLookupList = dataTypeListItems;
		DefaultListModel listModel = new DefaultListModel();
		theDataLookupList.setModel(listModel);
		theDataLookupList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent evt) {
				dataLookupListValueChanged(evt);
			}
		});
		theCmsMappingsList = cmsMappingsList;
		listModel = new DefaultListModel();
		theCmsMappingsList.setModel(listModel);
		theCmsMappingsList
				.addListSelectionListener(new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent evt) {
						cmsMappingListValueChanged(evt);
					}
				});
		theDnxTypeList = dnxTypeList;
		theDnxTypeList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent evt) {
				dnxTypeListValueChanged(evt);
			}
		});
		theCmsFieldCombo = cmsFieldCombo;
		tehDnxDetailList = dnxDetailList;
		tehDnxDetailList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent evt) {
				dnxDetailListValueChanged(evt);
			}
		});
		theDcTypeList = dcTypeList;
		theDcTypeList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent evt) {
				dcTypeListValueChanged(evt);
			}
		});
		theDcXsiList = dcXsiList;
		theDcXsiList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent evt) {
				dcXsiListValueChanged(evt);
			}
		});
		theDcTermsTypeList = dcTermsTypeList;
		theDcTermsTypeList
				.addListSelectionListener(new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent evt) {
						dcTermsTypeListValueChanged(evt);
					}
				});
		theDnxDcPane = dnxDcPane;
		theDnxDcPane.addChangeListener(new javax.swing.event.ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent evt) {
				tabDnxDcStateChanged(evt);
			}
		});
		try {
			loadDNXValues();
		} catch (Exception ex) {
			configuratorFrame.showError("Error loading DNX values", ex
					.getMessage());
		}
		loadDCValues();
		loadDCXsiValues();
		loadDCTermsValues();
		loadCMSFields();
	}

	private void loadFrameData() {
		MetaDataTypeImpl data = (MetaDataTypeImpl) theDataList
				.getSelectedValue();
		String dcOther = "";
		if (data.isDNX()) {
			theDnxDcPane.setSelectedIndex(0);
			if (data.getDnxType() == null || data.getDnxType().equals("")) {
				theDnxTypeList.clearSelection();
			} else {
				theDnxTypeList.setSelectedValue(data.getDnxType(), true);
			}
			if (data.getDnxSubType() == null || data.getDnxSubType().equals("")) {
				tehDnxDetailList.clearSelection();
			} else {
				tehDnxDetailList.setSelectedValue(data.getDnxSubType(), true);
			}
			theDcTypeList.clearSelection();
			theDcXsiList.clearSelection();
			theDcTermsTypeList.clearSelection();
		} else if (data.isDCTerms()) {
			theDnxDcPane.setSelectedIndex(3);
			theDnxTypeList.clearSelection();
			tehDnxDetailList.clearSelection();
			theDcTypeList.clearSelection();
			theDcXsiList.clearSelection();
			theDcTermsTypeList.setSelectedValue(data.getDcTermsType(), true);
		} else {
			theDnxDcPane.setSelectedIndex(1);
			theDnxTypeList.clearSelection();
			tehDnxDetailList.clearSelection();
			theDcTypeList.clearSelection();
			theDcXsiList.clearSelection();
			theDcTermsTypeList.clearSelection();
			if (data.getDcType() != null && !data.getDcType().equals("")) {
				theDcTypeList.setSelectedValue(data.getDcType(), true);
			}
			if (theDcTypeList.getSelectedValue() == null) {
				dcOther = data.getDcType();
			}
			theDcXsiList.setSelectedValue(data.getDcXSIAttr(), true);
		}
		configuratorFrame.loadData(data, dcOther);
	}

	private void dataListValueChanged(ListSelectionEvent evt) {
		if (theDataList.getSelectedValue() != null) {
			loadFrameData();
			setDirty(false);
			configuratorFrame.checkButtons();
		}
	}

	private void loadTranslationData() {
		UserGroupData.CharacterTranslations translation = (UserGroupData.CharacterTranslations) theLstCharacterTranslations
				.getSelectedValue();
		theCmbCharacterPosition.setSelectedItem(translation.getPosition());
		theTxtTranslateFrom.setText(translation.getCharacterToTranslate());
		theTxtTranslateTo.setText(translation.getTranslateTo());
	}

	private void characterTranslationValueChanged(ListSelectionEvent evt) {
		if (theLstCharacterTranslations.getSelectedValue() != null) {
			loadTranslationData();
			setDirty(false);
			configuratorFrame.checkButtons();
		}
	}

	private void userGroupCaretUpdate(javax.swing.event.CaretEvent evt) {
		setDirty(true);
		configuratorFrame.checkButtons();
	}

	private void loadTranslations() {
		DefaultListModel translationModel = (DefaultListModel) theLstCharacterTranslations
				.getModel();
		translationModel.clear();
		if (userGroupData != null) {
			for (UserGroupData.CharacterTranslations translation : userGroupData
					.getCharacterTranslations()) {
				translationModel.addElement(translation);
			}
		}
		theTxtTranslateFrom.setText("");
		theTxtTranslateTo.setText("");
		theCmbCharacterPosition.setSelectedIndex(0);
	}

	public void updateCharacterTranslation() {
		String translateFrom = theTxtTranslateFrom.getText();
		String translateTo = theTxtTranslateTo.getText();
		ECharacterPosition position = (ECharacterPosition) theCmbCharacterPosition
				.getSelectedItem();
		if (theLstCharacterTranslations.getSelectedValue() == null) { // New one
			try {
				userGroupData.addCharacterTranslation(translateFrom,
						translateTo, position);
			} catch (Exception ex) {
				configuratorFrame.showError("Error adding translation", ex
						.getMessage());
			}
		} else {
			UserGroupData.CharacterTranslations translation = (UserGroupData.CharacterTranslations) theLstCharacterTranslations
					.getSelectedValue();
			try {
				translation.setCharacterToTranslate(translateFrom);
				translation.setTranslateTo(translateTo);
			} catch (CharacterTranslationException ex) {
				configuratorFrame
						.showError("Error setting translation characters", ex
								.getMessage());
			}
			translation.setPosition(position);
		}
		setDirty(true);
		loadTranslations();
		configuratorFrame.checkButtons();
	}

	public void deleteCharacterTranslation() {
		if (configuratorFrame.confirm("Delete translation?")) {
			UserGroupData.CharacterTranslations translation = (UserGroupData.CharacterTranslations) theLstCharacterTranslations
					.getSelectedValue();
			try {
				userGroupData.deleteCharacterTranslation(translation);
			} catch (CharacterTranslationException ex) {
				configuratorFrame
						.showError("Error setting translation characters", ex
								.getMessage());
			}
		}
	}

	private boolean isDNX() {
		return theDnxDcPane.getSelectedIndex() == 0;
	}

	private boolean isDCTerms() {
		return theDnxDcPane.getSelectedIndex() == 3;
	}

	private void dnxTypeListValueChanged(ListSelectionEvent evt) {
		try {
			loadDNXDetailValues();
		} catch (Exception ex) {
			configuratorFrame.showError("Error loading DNX values", ex
					.getMessage());
		}
		setDirty(true);
		configuratorFrame.checkButtons();
	}

	private void dnxDetailListValueChanged(ListSelectionEvent evt) {
		if (tehDnxDetailList.getSelectedValue() != null) {
			MetaDataTypeImpl data = (MetaDataTypeImpl) theDataList
					.getSelectedValue();
			data.setDnxSubType((String) tehDnxDetailList.getSelectedValue());
		}
		setDirty(true);
		configuratorFrame.checkButtons();
	}

	private void dcTypeListValueChanged(ListSelectionEvent evt) {
		if (theDcTypeList.getSelectedValue() != null) {
			MetaDataTypeImpl data = (MetaDataTypeImpl) theDataList
					.getSelectedValue();
			data.setDcType((String) theDcTypeList.getSelectedValue());
		}
		setDirty(true);
		configuratorFrame.checkButtons();
	}
	
	private void dcXsiListValueChanged(ListSelectionEvent evt) {
		if (theDcXsiList.getSelectedValue() != null) {
			MetaDataTypeImpl data = (MetaDataTypeImpl) theDataList
					.getSelectedValue();
			data.setDcXSIAttr((String) theDcXsiList.getSelectedValue());
		}
		setDirty(true);
		configuratorFrame.checkButtons();
	}

	private void dcTermsTypeListValueChanged(ListSelectionEvent evt) {
		if (theDcTermsTypeList.getSelectedValue() != null) {
			MetaDataTypeImpl data = (MetaDataTypeImpl) theDataList
					.getSelectedValue();
			data.setDcTermsType((String) theDcTermsTypeList.getSelectedValue());
		}
		setDirty(true);
		configuratorFrame.checkButtons();
	}

	private void cmsMappingListValueChanged(ListSelectionEvent evt) {
		configuratorFrame.editCmsMappingValue((String) theCmsMappingsList
				.getSelectedValue());
	}

	private void dataLookupListValueChanged(ListSelectionEvent evt) {
		configuratorFrame
				.editLookupValue((MetaDataListValues) theDataLookupList
						.getSelectedValue());
		loadCmsMappingData();
	}

	private void cmbDataTypeActionPerformed(ActionEvent evt) {
		// I'm going to make an assumption that the controls have been set up
		// correctly
		// If they haven't then you're going to get an error.
		EDataType dataType = (EDataType) theCmbDataType.getSelectedItem();
		MetaDataTypeImpl data = (MetaDataTypeImpl) theDataList
				.getSelectedValue();
		data.setDataType(dataType);
		if (dataType.equals(EDataType.MultiSelect)) {
			MetaDataTypeImpl meta = (MetaDataTypeImpl) theDataList
					.getSelectedValue();
			DefaultListModel model = new DefaultListModel();
			if (meta.getListItems() != null) {
				for (MetaDataListValues value : meta.getListItems()) {
					model.addElement(value);
				}
			}
			theDataLookupList.setModel(model);
			theDataLookupFrame.setVisible(true);
		} else {
			theDataLookupFrame.setVisible(false);
		}
	}

	private void tabDnxDcStateChanged(javax.swing.event.ChangeEvent evt) {
		MetaDataTypeImpl data = (MetaDataTypeImpl) theDataList
				.getSelectedValue();
		// Adjusted by Ben 2.12.2013
		data.setIsDNXState(isDNX());
		data.setIsDCTermsState(isDCTerms());
	}

	private void loadMetaData() {
		DefaultListModel model = new DefaultListModel();
		for (IMetaDataTypeExtended meta : metaData) {
			model.addElement(meta);
		}
		theDataList.setModel(model);
		configuratorFrame.checkButtons();
	}

	private boolean chooseDirectory(String initialDirectory, String title) {
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		boolean result = chooseFile(initialDirectory, title, true);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		return result;
	}

	private boolean chooseFile(String initialFile, String title) {
		return chooseFile(initialFile, title, true);
	}

	private boolean chooseFile(String initialFile, String title, boolean isLoad) {
		chooser.setDialogTitle(title);
		if (!initialFile.equals("")) {
			File initDir = new File(initialFile);
			while (!initDir.exists()) {
				initDir = initDir.getParentFile();
			}
			if (initDir.exists()) {
				chooser.setSelectedFile(initDir);
			}
		}
		int result;
		if (isLoad) {
			result = chooser.showOpenDialog(configuratorFrame.getComponent());
		} else {
			result = chooser.showSaveDialog(configuratorFrame.getComponent());
		}
		if (result == JFileChooser.APPROVE_OPTION) {
			chosenFileName = chooser.getSelectedFile().getPath();
			LOG.debug("Opening: " + chosenFileName);
			// _Frame.setConfigurationFileName(_chosenFileName);
			return true;
		} else {
			return false;
		}
	}

	private ApplicationData loadAppData() throws Exception {
		return ApplicationData.getInstance(applicationConfigFile
				.getAbsolutePath());
	}

	public String loadApplicationFile(String initialFile, boolean justDoIt)
			throws Exception {
		String retVal = "";
		applicationData = null;
		if (justDoIt) {
			applicationConfigFile = new File(initialFile);
			retVal = applicationConfigFile.getAbsolutePath();
			applicationData = loadAppData();
		} else {
			if (chooseFile(initialFile,
					"Select application configuration file to modify")) {
				applicationConfigFile = chooser.getSelectedFile();
				retVal = applicationConfigFile.getAbsolutePath();
				applicationData = loadAppData();
			}
		}
		if (applicationData != null) {
			configuratorFrame.showApplicationData(applicationData);
		}
		setDirty(false);
		return retVal;
	}

	public void saveSipStatusFile() throws Exception {
		if (sipStatusConfigFile == null) {
			if (!chooseFile("", "Please specify file to save as", false)) {
				return;
			}
			sipStatusConfigFile = chooser.getSelectedFile();
		}
		sipStatusCollection.storeAsXML(sipStatusConfigFile.getAbsolutePath());
	}

	private void loadSipData() throws Exception {
		if (theSipList == null) {
			return;
		}
		sipStatusCollection = SIPStatusCollection.create(sipStatusConfigFile
				.getAbsolutePath());
		DefaultListModel model = (DefaultListModel) theSipList.getModel();
		model.clear();
		for (SIPStatus sipStatus : sipStatusCollection) {
			model.addElement(sipStatus);
		}
	}

	public String loadSipStatusFile(String initialFile, boolean justDoIt)
			throws Exception {
		String retVal = "";
		sipStatusCollection = null;
		if (justDoIt) {
			sipStatusConfigFile = new File(initialFile);
			retVal = sipStatusConfigFile.getAbsolutePath();
			loadSipData();
		} else {
			if (chooseFile(initialFile,
					"Select SIP Status configuration file to modify")) {
				sipStatusConfigFile = chooser.getSelectedFile();
				retVal = sipStatusConfigFile.getAbsolutePath();
				loadSipData();
			}
		}
		setDirty(false);
		return retVal;
	}

	public void deleteSipStatus() {
		if (theSipList == null) {
			return;
		}
		DefaultListModel model = (DefaultListModel) theSipList.getModel();
		int selectedIndex = theSipList.getSelectedIndex();
		SIPStatus status = (SIPStatus) model.get(selectedIndex);
		sipStatusCollection.deleteStatus(status);
		model.remove(selectedIndex);
		int newSize = model.size();
		if (newSize > 0) {
			if (selectedIndex >= (newSize - 1)) {
				selectedIndex = newSize - 1;
			}
			theSipList.setSelectedIndex(selectedIndex);
		}
	}

	public void addSipStatus() {
		if (theSipList == null) {
			return;
		}
		DefaultListModel model = (DefaultListModel) theSipList.getModel();
		SIPStatus status = SIPStatus.create("New SIP Status", "", false, false,
				false);
		model.addElement(status);
		theSipList.setSelectedIndex(model.getSize() - 1);
		if (sipStatusCollection == null) {
			sipStatusCollection = SIPStatusCollection.create();
		}
		sipStatusCollection.add(status);
		configuratorFrame.showSipStatus(status);
	}

	public void updateSipStatus(SIPStatus status) {
		theSipList.repaint();
	}

	public void saveApplicationFile(ApplicationData appData) throws Exception {
		applicationData = appData;
		if (applicationConfigFile == null) {
			if (!chooseFile("", "Please specify file to save as", false)) {
				return;
			}
			applicationConfigFile = chooser.getSelectedFile();
		}
		applicationData.storeAsXML(applicationConfigFile.getAbsolutePath());
	}

	public String getUserConfigPath() {
		String retVal = "No file selected";
		if (userConfigFile != null) {
			retVal = userConfigFile.getAbsolutePath();
		}
		return retVal;
	}

	public void deleteUser() {
		if (theUserList == null) {
			return;
		}
		DefaultListModel model = (DefaultListModel) theUserList.getModel();
		int selectedIndex = theUserList.getSelectedIndex();
		IndigoUser user = (IndigoUser) model.get(selectedIndex);
		userData.deleteUser(user);
		model.remove(selectedIndex);
		int newSize = model.size();
		if (newSize > 0) {
			if (selectedIndex >= (newSize - 1)) {
				selectedIndex = newSize - 1;
			}
			theUserList.setSelectedIndex(selectedIndex);
		}
	}

	public void addUser() throws Exception {
		if (theUserList == null) {
			return;
		}
		DefaultListModel model = (DefaultListModel) theUserList.getModel();
		IndigoUser user = IndigoUser.create("New User", "");
		model.addElement(user);
		theUserList.setSelectedIndex(model.getSize() - 1);
		if (userData == null) {
			userData = UserData.create("");
		}
		userData.addUser(user);
		configuratorFrame.showUser(user);
	}

	public void updateUser(IndigoUser user) {
		theUserList.repaint();
	}

	private void loadUsers() throws Exception {
		if (theUserList == null) {
			return;
		}
		userData = UserData.create(userConfigFile.getAbsolutePath(), false);
		DefaultListModel model = (DefaultListModel) theUserList.getModel();
		model.clear();
		for (IndigoUser user : userData) {
			model.addElement(user);
		}
	}

	public String loadUserFile(String initialFile, boolean justDoIt)
			throws Exception {
		String retVal = "";
		if (justDoIt) {
			userConfigFile = new File(initialFile);
			retVal = userConfigFile.getAbsolutePath();
			loadUsers();
		} else {
			if (chooseFile(initialFile,
					"Select user configuration file to modify")) {
				userConfigFile = chooser.getSelectedFile();
				retVal = userConfigFile.getAbsolutePath();
				loadUsers();
			}
		}
		setDirty(false);
		theDataList.setEnabled(true);
		return retVal;
	}

	public void saveUsersFile() throws Exception {
		if (userConfigFile == null) {
			if (!chooseFile("", "Please specify file to save as", false)) {
				return;
			}
			userConfigFile = chooser.getSelectedFile();
		}
		userData.storeAsXML(userConfigFile.getAbsolutePath());
	}

	private void loadGroup() throws Exception {
		userGroupData = UserGroupData.create(userGroupConfigFile
				.getAbsolutePath(), false, false);
		loadTranslations();
		configuratorFrame.loadUserGroupData(userGroupData);
	}

	public void saveUserGroupData(UserGroupData groupData) throws Exception {
		userGroupData = groupData;
		if (userGroupConfigFile == null) {
			if (!chooseFile("", "Please specify file to save as", false)) {
				return;
			}
			userGroupConfigFile = chooser.getSelectedFile();
		}
		userGroupData.storeAsXML(userGroupConfigFile.getAbsolutePath());
	}

	public String loadUserGroupFile(String initialFile, boolean justDoIt)
			throws Exception {
		String retVal = "";
		if (justDoIt) {
			userGroupConfigFile = new File(initialFile);
			retVal = userConfigFile.getAbsolutePath();
			loadGroup();
		} else {
			if (chooseFile(initialFile,
					"Select user group configuration file to modify")) {
				userGroupConfigFile = chooser.getSelectedFile();
				retVal = userGroupConfigFile.getAbsolutePath();
				loadGroup();
			}
		}
		setDirty(false);
		return retVal;
	}

	public String getInterimCleanupDirectory(String initialDirectory) {
		String retVal = "";
		chooser.setFileFilter(directoryFilter);
		if (chooseDirectory(initialDirectory,
				"Select interim cleanup directory")) {
			retVal = chooser.getSelectedFile().getAbsolutePath();
		}
		chooser.setFileFilter(xmlFilter);
		return retVal;
	}

	public String getFileTypesPropertiesFile(String initialFile) {
		String retVal = "";
		chooser.setFileFilter(propertiesFilter);
		if (chooseFile(initialFile, "Select structure map descriptions file")) {
			retVal = chooser.getSelectedFile().getAbsolutePath();
		}
		chooser.setFileFilter(xmlFilter);
		return retVal;
	}

	public String getMetaDataFile(String initialFile) {
		String retVal = "";
		if (chooseFile(initialFile, "Select group metadata configuration file")) {
			retVal = chooser.getSelectedFile().getAbsolutePath();
		}
		return retVal;
	}

	public String getSharedTemplateLocation(String initialDirectory) {
		String retVal = "";
		chooser.setFileFilter(directoryFilter);
		if (chooseDirectory(initialDirectory, "Select shared template location")) {
			retVal = chooser.getSelectedFile().getAbsolutePath();
		}
		chooser.setFileFilter(xmlFilter);
		return retVal;
	}

	private void loadConfigFile() {
		try {
			metaData = new MetaDataFields(metaDataConfigFile.getPath());
			loadMetaData();
		} catch (Exception ex) {
			LOG.error("Error opening file " + metaDataConfigFile.getPath(), ex);
		}
	}

	public String loadConfigurationFile(String initialFile, boolean justDoIt) {
		String retVal = "";
		if (justDoIt) {
			metaDataConfigFile = new File(initialFile);
			retVal = metaDataConfigFile.getAbsolutePath();
			loadConfigFile();
		} else {
			if (chooseFile(initialFile,
					"Select metadata configuration file to modify")) {
				metaDataConfigFile = chooser.getSelectedFile();
				retVal = metaDataConfigFile.getAbsolutePath();
				loadConfigFile();
			}
		}
		setDirty(false);
		theDataList.setEnabled(true);
		return retVal;
	}

	private void loadDNXValues() throws Exception {
		DefaultListModel model = new DefaultListModel();
		ArrayList<String> parents = theAppProperties.getApplicationData()
				.getDeposit(false).getDNXValues(null);
		for (String parent : parents) {
			model.addElement(parent);
		}
		theDnxTypeList.setModel(model);
	}

	private void loadDNXDetailValues() throws Exception {
		DefaultListModel model = new DefaultListModel();
		if (theDnxTypeList.getSelectedValue() != null) {
			MetaDataTypeImpl data = (MetaDataTypeImpl) theDataList
					.getSelectedValue();
			data.setDnxType((String) theDnxTypeList.getSelectedValue());
			String selectedValue = (String) theDnxTypeList.getSelectedValue();
			ArrayList<String> children = theAppProperties.getApplicationData()
					.getDeposit(false).getDNXValues(selectedValue);
			for (String child : children) {
				model.addElement(child);
			}
		}
		tehDnxDetailList.setModel(model);
	}

	private void loadDCValues() {
		DefaultListModel model = new DefaultListModel();
		model.addElement("title");
		model.addElement("creator");
		model.addElement("subject");
		model.addElement("description");
		model.addElement("publisher");
		model.addElement("contributor");
		model.addElement("date");
		model.addElement("type");
		model.addElement("format");
		model.addElement("identifier");
		model.addElement("source");
		model.addElement("language");
		model.addElement("relation");
		model.addElement("coverage");
		model.addElement("rights");
		theDcTypeList.setModel(model);
	}
	
	private void loadDCXsiValues() {
		DefaultListModel model = new DefaultListModel();
		model.addElement("dcterms:URI");
		model.addElement("dcterms:ISBN");
		model.addElement("dcterms:ISMN");
		model.addElement("dcterms:ISSN");
		theDcXsiList.setModel(model);
	}

	private void loadDCTermsValues() {
		DefaultListModel model = new DefaultListModel();
		model.addElement("bibliographicCitation");
		model.addElement("issued");
		model.addElement("available");
		model.addElement("isbn");
		model.addElement("issn");
		model.addElement("ismn");
		model.addElement("url");
		model.addElement("accrualPeriodicity");
		model.addElement("created");
		model.addElement("alternative");
		theDcTermsTypeList.setModel(model);
	}

	private void loadCMSFields() {
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		model.addElement("Id");
		model.addElement("Title");
		model.addElement("Publisher");
		model.addElement("Author");
		model.addElement("Reference");
		model.addElement("Deposit Type");
		model.addElement("Description");
		model.addElement("Coverage");
		model.addElement("Rights");
		model.addElement("Relation");
		theCmsFieldCombo.setModel(model);
	}

	public void saveConfigurationFile() {
		try {
			
			if((theDcTypeList.getSelectedIndex() != -1) && (theDnxTypeList.getSelectedIndex() != -1)){
				throw new Exception("More than one metadata field mapped.");
			}
			else if((theDcTypeList.getSelectedIndex() != -1) && (theDcTermsTypeList.getSelectedIndex() != -1)){
				throw new Exception("More than one metadata field mapped.");
			}
			else if((theDnxTypeList.getSelectedIndex() != -1) && (theDcTermsTypeList.getSelectedIndex() != -1)){
				throw new Exception("More than one metadata field mapped.");
			}
			else if((theDnxTypeList.getSelectedIndex() != -1) && (theDcXsiList.getSelectedIndex() != -1)){
				throw new Exception("More than one metadata field mapped.");
			}
			else if((theDcTermsTypeList.getSelectedIndex() != -1) && (theDcXsiList.getSelectedIndex() != -1)){
				throw new Exception("More than one metadata field mapped.");
			}
			
			
			MetaDataTypeImpl data = (MetaDataTypeImpl) theDataList.getSelectedValue();
			
			if(theDnxTypeList.getSelectedIndex() == -1){
				data.setIsDNX(false);
			}
			else{ 
				if(isDNX()){
					data.setIsDNX(true);
				}
				else {
					throw new Exception("Wrong tab selected.");
				}
			}
			
			if(theDcTermsTypeList.getSelectedIndex() == -1){
				data.setIsDCTerms(false);
			}
			else{ 
				if(isDCTerms()){
					data.setIsDCTerms(true);
				}
				else {
					throw new Exception("Wrong tab selected.");
				}
				
			}
			
			metaData.storeAsXML(metaDataConfigFile.getPath());
			setDirty(false);
		} catch (Exception ex) {
			LOG.error("Error saving config file "
					+ metaDataConfigFile.getPath(), ex);
			configuratorFrame.showError("Error saving config file", ex
					.getMessage());
		}
	}

	public void updateData(String dataFieldName, String dataFieldDescription,
			String defaultValue, int maximumLength, boolean isCompulsory,
			boolean isVisible, boolean savedWithTemplate,
			boolean allowsMultipleRows, boolean isSetBySystem,
			boolean willBeUploaded, boolean isPopulatedFromCms, String dcExtra, boolean isCustomizable) {
		setDirty(true);
		if (theDataList.getSelectedValue() != null) {
			MetaDataTypeImpl data = (MetaDataTypeImpl) theDataList
					.getSelectedValue();
			data.setDataFieldName(dataFieldName);
			data.setDataFieldDescription(dataFieldDescription);
			data.setDefaultValue(defaultValue);
			try {
				data.setMaximumLength(maximumLength);
			} catch (Exception ex) {
			}
			data.setIsCompulsory(isCompulsory);
			data.setIsCustomizable(isCustomizable);
			data.setIsVisible(isVisible);
			data.setSavedWithTemplate(savedWithTemplate);
			data.setAllowsMultipleRows(allowsMultipleRows);
			data.setIsSetBySystem(isSetBySystem);
			data.setWillBeUploaded(willBeUploaded);
			data.setIsPopulatedFromCMS(isPopulatedFromCms);
			data.setCMSFieldName((String) theCmsFieldCombo.getSelectedItem());
			data.setDataType((EDataType) theCmbDataType.getSelectedItem());
			data.setIsDNX(isDNX());
			data.setIsDCTerms(isDCTerms());
			if (isDNX()) {
				data.setDnxType((String) theDnxTypeList.getSelectedValue());
				data
						.setDnxSubType((String) tehDnxDetailList
								.getSelectedValue());
				data.setDcType("");
				data.setDcTermsType("");
			} else if (isDCTerms()) {
				data.setDnxType("");
				data.setDnxSubType("");
				data.setDcType("");
				data.setDcTermsType((String) theDcTermsTypeList
						.getSelectedValue());
			} else {
				data.setDnxType("");
				data.setDnxSubType("");
				if (!dcExtra.equals("")) {
					data.setDcType(dcExtra);
				} else {
					data.setDcType((String) theDcTypeList.getSelectedValue());
				}
				if (theDcXsiList.getSelectedValue() != null){
					data.setDcXSIAttr((String) theDcXsiList.getSelectedValue());
				}
			}
			this.updateDataLookupValues();
		}
	}

	public boolean getIsDirty() {
		return formIsDirty;
	}

	public boolean canSave(String title, String dcOther, boolean willBeUploaded) {
		boolean retVal = false;
		if (title != null && !title.equals("")) {
			if (willBeUploaded) {
				if (isDNX()) {
					if ((theDnxTypeList.getSelectedValue() != null)
							&& (tehDnxDetailList.getSelectedValue() != null)) {
						retVal = true;
					}
				} else if (isDCTerms()) {
					if (theDcTermsTypeList.getSelectedValue() != null) {
						retVal = true;
					}
				} else {
					if (!dcOther.equals("")
							|| theDcTypeList.getSelectedValue() != null
							|| theDcXsiList.getSelectedValue() != null) {
						retVal = true;
					}
				}
			} else {
				retVal = true;
			}
		}
		return retVal;
	}

	public boolean canMoveItem(boolean up) {
		boolean retVal = false;
		if (theDataList.getSelectedValue() != null) {
			retVal = (up && theDataList.getSelectedIndex() > 0)
					|| (!up && theDataList.getSelectedIndex() < theDataList
							.getModel().getSize() - 1);
		}
		return retVal;
	}

	public void moveItem(boolean up) {
		if (canMoveItem(up)) {
			int currentIndex = theDataList.getSelectedIndex();
			int otherIndex;
			if (up) {
				otherIndex = currentIndex - 1;
			} else {
				otherIndex = currentIndex + 1;
			}
			MetaDataTypeImpl dataFrom = (MetaDataTypeImpl) theDataList
					.getSelectedValue();
			DefaultListModel model = (DefaultListModel) theDataList.getModel();
			MetaDataTypeImpl dataTo = (MetaDataTypeImpl) model.get(otherIndex);
			int orderFrom = dataFrom.getSortOrder();
			dataFrom.setSortOrder(dataTo.getSortOrder());
			dataTo.setSortOrder(orderFrom);
			metaData.reSort();
			loadMetaData();
			theDataList.setSelectedIndex(otherIndex);
			saveConfigurationFile();
		}
	}

	public boolean canMoveLookupItem(boolean up) {
		boolean retVal = false;
		if (theDataLookupList.getSelectedValue() != null) {
			retVal = (up && theDataLookupList.getSelectedIndex() > 0)
					|| (!up && theDataLookupList.getSelectedIndex() < theDataLookupList
							.getModel().getSize() - 1);
		}
		return retVal;
	}

	public void moveLookupItem(boolean up) {
		if (canMoveLookupItem(up)) {
			int currentIndex = theDataLookupList.getSelectedIndex();
			int otherIndex;
			if (up) {
				otherIndex = currentIndex - 1;
			} else {
				otherIndex = currentIndex + 1;
			}
			DefaultListModel model = (DefaultListModel) theDataLookupList
					.getModel();
			MetaDataListValues currentItem = (MetaDataListValues) model
					.get(currentIndex);
			currentItem.setSortOrder(otherIndex);
			MetaDataListValues itemToSwapWith = (MetaDataListValues) model
					.get(otherIndex);
			itemToSwapWith.setSortOrder(currentIndex);
			model.set(currentIndex, itemToSwapWith);
			model.set(otherIndex, currentItem);
			theDataLookupList.setSelectedIndex(otherIndex);
			setDirty(true);
			updateDataLookupValues();
			configuratorFrame.checkButtons();
		}
	}

	private void updateDataLookupValues() {
		setDirty(true);
		MetaDataTypeImpl data = (MetaDataTypeImpl) theDataList
				.getSelectedValue();
		if (data.getDataType().equals(EDataType.MultiSelect)) {
			data.setListItems(getDataLookupItems());
		} else {
			data.setListItems(null);
		}
	}

	public ArrayList<MetaDataListValues> getDataLookupItems() {
		DefaultListModel model = (DefaultListModel) theDataLookupList
				.getModel();
		ArrayList<MetaDataListValues> values = new ArrayList<MetaDataListValues>();
		for (int i = 0; i < model.size(); i++) {
			values.add((MetaDataListValues) model.get(i));
		}
		Collections.sort(values, new MetaDataListValuesComparator());
		for (int i = 0; i < values.size(); i++) {
			values.get(i).setSortOrder(i);
		}
		return values;
	}

	private int maxDataLookupSortOrder() {
		ArrayList<MetaDataListValues> lookupItems = getDataLookupItems();
		if (lookupItems == null || lookupItems.size() == 0) {
			return 0;
		} else {
			return lookupItems.get(lookupItems.size() - 1).getSortOrder();
		}
	}

	public void addDataLookupItem(String value, String display) {
		int sortOrder = maxDataLookupSortOrder();
		MetaDataListValues newValue = MetaDataListValues.create(value, display,
				sortOrder);
		DefaultListModel model = (DefaultListModel) theDataLookupList
				.getModel();
		model.addElement(newValue);
		updateDataLookupValues();
	}

	public void addCmsMappingItem(String value) {
		if (theDataLookupList.getSelectedValue() == null) {
			configuratorFrame.showError(errorHeader, "No meta data selected");
			return;
		}
		MetaDataListValues currentValue = (MetaDataListValues) theDataLookupList
				.getSelectedValue();
		currentValue.addCmsMapping(value);
		DefaultListModel model = (DefaultListModel) theCmsMappingsList
				.getModel();
		model.addElement(value);
	}

	public void loadCmsMappingData() {
		MetaDataListValues currentValue = (MetaDataListValues) theDataLookupList
				.getSelectedValue();
		DefaultListModel model = (DefaultListModel) theCmsMappingsList
				.getModel();
		model.clear();
		if (currentValue != null) {
			for (String cmsMap : currentValue.getCmsMappings()) {
				model.addElement(cmsMap);
			}
		}
	}

	public void saveDataLookupItem(String value, String display) {
		if (theDataLookupList.getSelectedIndex() > -1) {
			DefaultListModel model = (DefaultListModel) theDataLookupList
					.getModel();
			MetaDataListValues existingValue = (MetaDataListValues) model
					.get(theDataLookupList.getSelectedIndex());
			existingValue.setValue(value);
			existingValue.setDisplay(display);
			theDataLookupList.setSelectedIndex(-1);
			updateDataLookupValues();
		} else {
			configuratorFrame.showError(errorHeader,
					"Invalid selection saving meta data");
			return;
		}
	}

	public void addCMSValueItem(String value) {
		DefaultListModel model = (DefaultListModel) theCmsMappingsList
				.getModel();
		model.addElement(value);
	}

	public void saveCMSValueItem(String value) throws MetaDataException {
		if (theCmsMappingsList.getSelectedIndex() > -1) {
			DefaultListModel model = (DefaultListModel) theCmsMappingsList
					.getModel();
			model.set(theCmsMappingsList.getSelectedIndex(), value);
			theCmsMappingsList.setSelectedIndex(-1);
		} else {
			throw new MetaDataException(
					"Invalid selection saving CMS field data");
		}
	}

	public void deleteLookupItem() {
		MetaDataListValues value = (MetaDataListValues) theDataLookupList
				.getSelectedValue();
		if (configuratorFrame.confirm("Delete list item " + value.getDisplay()
				+ "?")) {
			setDirty(true);
			DefaultListModel model = (DefaultListModel) theDataLookupList
					.getModel();
			model.remove(theDataLookupList.getSelectedIndex());
			updateDataLookupValues();
		}
	}

	public void updateCmsLookupValue(String newValue) {
		if (theCmsMappingsList.getSelectedIndex() > -1) {
			DefaultListModel model = (DefaultListModel) theCmsMappingsList
					.getModel();
			model.set(theCmsMappingsList.getSelectedIndex(), newValue);
			theCmsMappingsList.setSelectedIndex(-1);
			updateDataLookupValues();
		} else {
			configuratorFrame.showError(errorHeader,
					"Invalid selection saving CMS lookup data");
			return;
		}
	}

	public void deleteCmsMappingItem() {
		MetaDataListValues value = (MetaDataListValues) theDataLookupList
				.getSelectedValue();
		String mappingValue = (String) theCmsMappingsList.getSelectedValue();
		if (configuratorFrame.confirm("Delete CMS mapping item " + mappingValue
				+ "?")) {
			setDirty(true);
			value.getCmsMappings()
					.remove(theCmsMappingsList.getSelectedIndex());
			loadCmsMappingData();
		}
	}

	public void deleteItem() {
		MetaDataTypeImpl data = (MetaDataTypeImpl) theDataList
				.getSelectedValue();
		if (configuratorFrame.confirm("Delete item " + data.toString()
				+ "?\nThis can not be undone.")) {
			metaData.deleteMetaData(data);
			saveConfigurationFile();
			loadMetaData();
		}
	}

	public void addNewItem() {
		MetaDataTypeImpl data = new MetaDataTypeImpl();
		data.setDataType(EDataType.Text);
		data.setDataFieldName("New item");
		data.setIsVisible(true);
		metaData.addMetaData(data);
		DefaultListModel model = (DefaultListModel) theDataList.getModel();
		model.addElement(data);
		theDataList.setSelectedIndex(model.size() - 1);
		setDirty(true);
	}

	private void setDirty(boolean dirty) {
		formIsDirty = dirty;
		theDataList.setEnabled(!formIsDirty);

	}

	public boolean canDeleteLookupData() {
		return (theDataLookupList.getSelectedValue() != null);
	}

	public boolean canSaveLookupData(String theData) {
		return ((theData != null) && (!theData.equals("")));
	}
}
