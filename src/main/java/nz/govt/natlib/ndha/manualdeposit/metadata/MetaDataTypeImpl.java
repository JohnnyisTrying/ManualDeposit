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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nz.govt.natlib.ndha.common.XMLHandler;
import nz.govt.natlib.ndha.common.XMLHandler.XMLObject;
import nz.govt.natlib.ndha.common.exceptions.MetsException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class MetaDataTypeImpl implements IMetaDataTypeExtended {
	private static final String IS_CMS_ID_FIELD = "IsCMSID";
	private static final String IS_CMS_SYSTEM_FIELD = "IsCMSSystem";
	private static final String IS_CMS_DESCRIPTION_FIELD = "IsCmsDescription";
	private static final String DATA_FIELD_NAME = "DataFieldName";
	private static final String SORT_ORDER = "SortOrder";
	private static final String DATA_FIELD_DESCRIPTION = "DataFieldDescription";
	private static final String DATA_FIELD_VALUE = "DataFieldValue";
	private static final String DATA_MAXIMUM_LENGTH = "MaximumLength";
	private static final String DATA_TYPE = "DataType";
	private static final String IS_COMPULSORY = "IsCompulsory";
	private static final String IS_CUSTOMIZABLE = "IsCustomizable";
	private static final String SAVED_WITH_TEMPLATE = "SavedWithTemplate";
	private static final String ALLOWS_MULTIPLE_ROWS = "AllowsMultipleRows";
	private static final String IS_SET_BY_SYSTEM = "IsSetBySystem";
	private static final String WILL_BE_UPLOADED = "WillBeUploaded";
	private static final String POPULATED_FROM_CMS = "PopulatedFromCMS";
	private static final String CMS_FIELD_NAME = "CMSFieldName";
	private static final String IS_DNX = "IsDNX";
	private static final String IS_DCTERMS = "IsDCTerms";
	private static final String DNX_TYPE = "DnxType";
	private static final String DNX_SUB_TYPE = "DnxSubType";
	private static final String DC_TYPE = "DcType";
	private static final String DC_XSI_ATTR = "DCxsiType";
	private static final String DCTERMS_TYPE = "DcTermsType";
	private static final String PROVENANCE_NOTE_EVENT_TYPE = "ProvenanceNoteEventType";
	private static final String PROVENANCE_EVENT_IDENTIFIER_VALUE = "ProvenanceEventIdentifierValue";
	private static final String PROVENANCE_EVENT_IDENTIFIER_TYPE = "ProvenanceEventIdentifierType";
	private static final String PROVENANCE_EVENT_OUTCOME_VALUE = "ProvenanceEventOutcome";
	private static final String PROVENANCE_EVENT_OUTCOME_DETAIL = "ProvenanceEventOutcomeDetail";
	private static final String PROVENANCE_EVENT_DESCRIPTION = "ProvenanceEventDescription";
	
	private static final String DEFAULT_VALUE = "DefaultValue";
	private static final String IS_VISIBLE = "IsVisible";

	// If you add any properties, be sure & change the duplicate function
	private final static Log LOG = LogFactory.getLog(MetaDataTypeImpl.class);
	private static String standardObjectType = "MetaData";
	private boolean isCMSIDField = false;
	private boolean isCMSSystemField = false;
	private boolean isCMSDescriptionField = false;
	private String dataFieldName = "";
	private int sortOrder = 0;
	private String dataFieldDescription = "";
	private String dataFieldValue = "";
	private int maximumLength = 100;
	private EDataType dataType;

	private List<MetaDataListValues> listItems = new ArrayList<MetaDataListValues>();
	private boolean isCompulsory = false;
	private boolean isCustomizable = false;
	private boolean savedWithTemplate = false;
	private boolean allowsMultipleRows = false;
	private boolean isSetBySystem = false;
	private boolean willBeUploaded = false;
	private boolean isPopulatedFromCms = false;
	private String cmsFieldName;
	private boolean isDNXType = false;
	private boolean isDCTermsType = false;
	private boolean isDNXState = false;
	private boolean isDCTermsState = false;
	private String dnxType;
	private String dnxSubType;
	private String dcType;
	private String dcTermsType;
	private String defaultValue;
	private boolean isVisible = true;
	private String dcXSIAttr;
	private String provenanceNoteEventType;
	private String provenanceEventIdentifierValue;
	private String provenanceEventIdentifierType;
	private boolean provenanceEventOutcome;
	private String provenanceEventOutcomeDetail;
	private String ProvenanceEventDescription;

	
	public void duplicate(IMetaDataTypeExtended dupTo) {
		dupTo.setIsCMSIDField(isCMSIDField);
		dupTo.setIsCMSSystemField(isCMSSystemField);
		dupTo.setIsCMSDescriptionField(isCMSDescriptionField);
		dupTo.setCMSFieldName(cmsFieldName);
		dupTo.setDataFieldName(dataFieldName);
		dupTo.setSortOrder(sortOrder);
		dupTo.setDataFieldDescription(dataFieldDescription);
		dupTo.setDataType(dataType);
		try {
			dupTo.setMaximumLength(maximumLength);
		} catch (Exception ex) {
			LOG.error("Error setting maximum length", ex);
		}
		try {
			dupTo.setDataFieldValue(dataFieldValue);
		} catch (Exception ex) {
			LOG.error("Error setting Data Field Value ", ex);
		}
		dupTo.setDefaultValue(defaultValue);
		dupTo.setListItems(listItems);
		dupTo.setIsCompulsory(isCompulsory);
		dupTo.setIsCustomizable(isCustomizable);
		dupTo.setSavedWithTemplate(savedWithTemplate);
		dupTo.setAllowsMultipleRows(allowsMultipleRows);
		dupTo.setIsSetBySystem(isSetBySystem);
		dupTo.setWillBeUploaded(willBeUploaded);
		dupTo.setIsPopulatedFromCMS(isPopulatedFromCms);
		dupTo.setIsDNX(isDNXType);
		dupTo.setIsDCTerms(isDCTermsType);
		dupTo.setIsDNXState(isDNXType);
		dupTo.setIsDCTermsState(isDNXType);
		dupTo.setDnxType(dnxType);
		dupTo.setDnxSubType(dnxSubType);
		dupTo.setDcType(dcType);
		dupTo.setDcXSIAttr(dcXSIAttr);
		dupTo.setDcTermsType(dcTermsType);
		dupTo.setIsVisible(isVisible);
		dupTo.setProvenanceEventDescription(ProvenanceEventDescription);
		dupTo.setProvenanceEventIdentifierType(provenanceEventIdentifierType);
		dupTo.setProvenanceEventIdentifierValue(provenanceEventIdentifierValue);
		dupTo.setProvenanceEventOutcome(provenanceEventOutcome);
		dupTo.setProvenanceEventOutcomeDetail(provenanceEventOutcomeDetail);
		dupTo.setProvenanceNoteEventType(provenanceNoteEventType);
	}

	public static String getStandardObjectType() {
		return standardObjectType;
	}

	public boolean getIsCMSIDField() {
		return isCMSIDField;
	}

	public void setIsCMSIDField(boolean value) {
		isCMSIDField = value;
	}

	public boolean getIsCMSSystemField() {
		return isCMSSystemField;
	}

	public void setIsCMSSystemField(boolean value) {
		isCMSSystemField = value;
	}

	public boolean getIsCMSDescriptionField() {
		return isCMSDescriptionField;
	}

	public void setIsCMSDescriptionField(boolean value) {
		isCMSDescriptionField = value;
	}

	public EDataType getDataType() {
		return dataType;
	}

	public void setDataType(EDataType value) {
		dataType = value;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int value) {
		sortOrder = value;
	}

	public String getDataFieldName() {
		return dataFieldName;
	}

	public void setDataFieldName(String value) {
		if (value == null) {
			dataFieldName = "";
		} else {
			dataFieldName = value;
		}
	}

	public String getDataFieldDescription() {
		return dataFieldDescription;
	}

	public void setDataFieldDescription(String value) {
		dataFieldDescription = value;
	}

	public String getDataFieldValue() {
		return dataFieldValue;
	}

	public void setDataFieldValue(String value) throws MetsException {
		if (dataType != EDataType.ProvenanceNote) {
			if (value.length() > maximumLength) {
				throw new MetsException("Maximum length exceeded");
			}
		}
		dataFieldValue = value;
	}

	public int getMaximumLength() {
		return maximumLength;
	}

	public void setMaximumLength(int value) throws MetsException {
		if (dataType != EDataType.ProvenanceNote) {
			if (dataFieldValue.length() > value) {
				throw new MetsException("Maximum length exceeded");
			}
		}
		maximumLength = value;
	}
	
	public String getProvenanceEventDescription() {
		return ProvenanceEventDescription;
	}

	public  void setProvenanceEventDescription(String provenanceEventDescription) {
		ProvenanceEventDescription = provenanceEventDescription;
	}


	public List<MetaDataListValues> getListItems() {
		return listItems;
	}

	public void setListItems(List<MetaDataListValues> items) {
		listItems = new ArrayList<MetaDataListValues>();
		if (items != null) {
			for (MetaDataListValues value : items) {
				listItems.add(value);
			}
		}
	}

	public boolean getIsCompulsory() {
		return isCompulsory;
	}

	public void setIsCompulsory(boolean value) {
		isCompulsory = value;
	}
	
	public boolean getIsCustomizable() {
		return isCustomizable;
	}

	public void setIsCustomizable(boolean value) {
		isCustomizable = value;
	}

	public boolean getSavedWithTemplate() {
		return savedWithTemplate;
	}

	public void setSavedWithTemplate(boolean value) {
		savedWithTemplate = value;
	}

	public boolean getAllowsMultipleRows() {
		return allowsMultipleRows;
	}

	public void setAllowsMultipleRows(boolean value) {
		allowsMultipleRows = value;
	}

	public boolean getIsSetBySystem() {
		return isSetBySystem;
	}

	public void setIsSetBySystem(boolean value) {
		isSetBySystem = value;
	}

	public boolean getWillBeUploaded() {
		return willBeUploaded;
	}

	public void setWillBeUploaded(boolean value) {
		willBeUploaded = value;
	}

	public boolean getIsPopulatedFromCMS() {
		return isPopulatedFromCms;
	}

	public void setIsPopulatedFromCMS(boolean value) {
		isPopulatedFromCms = value;
	}

	public String getCMSFieldName() {
		return cmsFieldName;
	}

	public void setCMSFieldName(String value) {
		cmsFieldName = value;
	}

	public String getDefaultValue() {
		if (defaultValue == null) {
			return "";
		} else {
			return defaultValue;
		}
	}

	public void setDefaultValue(String value) {
		defaultValue = value;
	}

	public boolean getIsVisible() {
		return isVisible;
	}

	public void setIsVisible(boolean value) {
		isVisible = value;
	}

	public boolean getIsSet() {
		return (dataFieldValue != null && dataFieldValue.length() != 0);
	}

	public boolean isDNX() {
		return isDNXType;
	}

	public void setIsDNX(boolean value) {
		isDNXType = value;
	}

	public boolean isDCTerms() {
		return isDCTermsType;
	}
	
	public void setIsDCTerms(boolean value) {
		isDCTermsType = value;
	}
	
	public boolean isDNXState() {
		return isDNXState;
	}
	
	public void setIsDNXState(boolean value) {
		isDNXState = value;
	}

	public boolean isDCTermsState() {
		return isDCTermsState;
	}

	public void setIsDCTermsState(boolean value) {
		isDCTermsState = value;
	}

	public String getDnxType() {
		return dnxType;
	}

	public void setDnxType(String value) {
		dnxType = value;
	}

	public String getDnxSubType() {
		return dnxSubType;
	}

	public void setDnxSubType(String value) {
		dnxSubType = value;
	}

	public String getDcType() {
		return dcType;
	}

	public void setDcType(String value) {
		dcType = value;
	}

	public String getDcTermsType() {
		return dcTermsType;
	}

	public void setDcTermsType(String value) {
		dcTermsType = value;
	}

	public String toString() {
		if ((dataFieldDescription != null)
				&& (!dataFieldDescription.equals(""))) {
			return dataFieldDescription;
		} else if ((dataFieldName != null) && (!dataFieldName.equals(""))) {
			return dataFieldName;
		} else {
			return "No description available";
		}
	}

	public static MetaDataTypeImpl create() {
		return new MetaDataTypeImpl();
	}

	public MetaDataTypeImpl() {

	}

	public static MetaDataTypeImpl create(XMLObject object) {
		return new MetaDataTypeImpl(object);
	}

	public MetaDataTypeImpl(XMLObject object) {
		setFromXMLObject(object);
	}

	public String getProvenanceNoteEventType() {
		return provenanceNoteEventType;
	}

	public void setProvenanceNoteEventType(String provenanceNoteEventType) {
		this.provenanceNoteEventType = provenanceNoteEventType;
	}

	public String getProvenanceEventIdentifierValue() {
		return provenanceEventIdentifierValue;
	}

	public void setProvenanceEventIdentifierValue(
			String provenanceEventIdentifierValue) {
		this.provenanceEventIdentifierValue = provenanceEventIdentifierValue;
	}

	public String getProvenanceEventIdentifierType() {
		return provenanceEventIdentifierType;
	}

	public void setProvenanceEventIdentifierType(
			String provenanceEventIdentifierType) {
		this.provenanceEventIdentifierType = provenanceEventIdentifierType;
	}

	public boolean getProvenanceEventOutcome() {
		return provenanceEventOutcome;
	}

	public void setProvenanceEventOutcome(boolean provenanceEventOutcome) {
		this.provenanceEventOutcome = provenanceEventOutcome;
	}

	public String getProvenanceEventOutcomeDetail() {
		return provenanceEventOutcomeDetail;
	}

	public void setProvenanceEventOutcomeDetail(
			String provenanceEventOutcomeDetail) {
		this.provenanceEventOutcomeDetail = provenanceEventOutcomeDetail;
	}

	public boolean isEquivalentTo(IMetaDataTypeExtended meta,
			boolean includeSortOrder) {
		boolean nameTheSame;
		if ((meta.getDataFieldName() == null && getDataFieldName() != null)
				|| (meta.getDataFieldName() != null && getDataFieldName() == null)) {
			nameTheSame = false;
		} else if (meta.getDataFieldName() == null
				&& getDataFieldName() == null) {
			nameTheSame = true;
		} else {
			nameTheSame = (meta.getDataFieldName().equals(getDataFieldName()));
		}
		boolean sortOrderTheSame = ((!includeSortOrder) || (meta.getSortOrder() == getSortOrder()));
		if (nameTheSame && sortOrderTheSame) {
			return true;
		} else {
			return false;
		}
	}

	public String getDisplayValue() {
		String retVal = dataFieldValue;
		if ((dataFieldValue != null) && (!dataFieldValue.equals(""))
				&& (dataType.equals(EDataType.MultiSelect))) {
			for (MetaDataListValues item : listItems) {
				if (item.getValue().trim().equals(dataFieldValue.trim())) {
					retVal = item.toString();
					break;
				}
			}
		} else {
			return dataFieldValue;
		}
		return retVal;
	}

	public XMLObject getXMLObject(XMLHandler handler) {
		XMLObject object = handler.createXMLObject(standardObjectType, String
				.format("%d", sortOrder));
		object.setObjectType(standardObjectType);
		object.setObjectName(String.format("%d", sortOrder));
		object.addAttribute(IS_CMS_ID_FIELD, String.format("%b", isCMSIDField));
		object.addAttribute(IS_CMS_SYSTEM_FIELD, String.format("%b",
				isCMSSystemField));
		object.addAttribute(IS_CMS_DESCRIPTION_FIELD, String.format("%b",
				isCMSDescriptionField));
		object.addAttribute(DATA_FIELD_NAME, dataFieldName);
		object.addAttribute(SORT_ORDER, String.format("%d", sortOrder));
		object.addAttribute(DATA_FIELD_DESCRIPTION, dataFieldDescription);
		object.addAttribute(DATA_FIELD_VALUE, dataFieldValue);
		object.addAttribute(DATA_MAXIMUM_LENGTH, String.format("%d",
				maximumLength));
		object.addAttribute(DATA_TYPE, dataType.name());
		if (listItems != null) {
			for (int i = 0; i < listItems.size(); i++) {
				MetaDataListValues value = listItems.get(i);
				object.addChild(value.getValue(), value.getXMLObject(handler));
			}
		}
		object.addAttribute(IS_COMPULSORY, String.format("%b", isCompulsory));
		object.addAttribute(IS_CUSTOMIZABLE, String.format("%b", isCustomizable));
		object.addAttribute(SAVED_WITH_TEMPLATE, String.format("%b",
				savedWithTemplate));
		object.addAttribute(ALLOWS_MULTIPLE_ROWS, String.format("%b",
				allowsMultipleRows));
		object.addAttribute(IS_SET_BY_SYSTEM, String
				.format("%b", isSetBySystem));
		object.addAttribute(WILL_BE_UPLOADED, String.format("%b",
				willBeUploaded));
		object.addAttribute(POPULATED_FROM_CMS, String.format("%b",
				isPopulatedFromCms));
		object.addAttribute(CMS_FIELD_NAME, cmsFieldName);

		object.addAttribute(IS_DNX, String.format("%b", isDNXType));
		object.addAttribute(IS_DCTERMS, String.format("%b", isDCTermsType));
		object.addAttribute(DNX_TYPE, dnxType);
		object.addAttribute(DNX_SUB_TYPE, dnxSubType);
		object.addAttribute(DC_TYPE, dcType);
		object.addAttribute(DC_XSI_ATTR, dcXSIAttr);
		object.addAttribute(DCTERMS_TYPE, dcTermsType);
		object.addAttribute(DEFAULT_VALUE, defaultValue);
		object.addAttribute(IS_VISIBLE, String.format("%b", isVisible));

		object
				.addAttribute(PROVENANCE_NOTE_EVENT_TYPE,
						provenanceNoteEventType);
		object.addAttribute(PROVENANCE_EVENT_IDENTIFIER_VALUE,
				provenanceEventIdentifierValue);
		object.addAttribute(PROVENANCE_EVENT_IDENTIFIER_TYPE,
				provenanceEventIdentifierType);
		object.addAttribute(PROVENANCE_EVENT_OUTCOME_VALUE, String.format("%b",
				provenanceEventOutcome));
		object.addAttribute(PROVENANCE_EVENT_OUTCOME_DETAIL,
				provenanceEventOutcomeDetail);
		object.addAttribute(PROVENANCE_EVENT_DESCRIPTION,
				ProvenanceEventDescription);


		return object;
	}

	public void setFromXMLObject(XMLObject object) {
		object.setObjectType(standardObjectType);
		if (object.getAttribute(IS_CMS_ID_FIELD) != null) {
			try {
				isCMSIDField = Boolean.parseBoolean(object
						.getAttribute(IS_CMS_ID_FIELD));
			} catch (Exception ex) {
				ex.printStackTrace();
				LOG.error(ex.getMessage(), ex);
			}
		}
		if (object.getAttribute(IS_CMS_SYSTEM_FIELD) != null) {
			try {
				isCMSSystemField = Boolean.parseBoolean(object
						.getAttribute(IS_CMS_SYSTEM_FIELD));
			} catch (Exception ex) {
				ex.printStackTrace();
				LOG.error(ex.getMessage(), ex);
			}
		}
		if (object.getAttribute(IS_CMS_DESCRIPTION_FIELD) != null) {
			try {
				isCMSDescriptionField = Boolean.parseBoolean(object
						.getAttribute(IS_CMS_DESCRIPTION_FIELD));
			} catch (Exception ex) {
				ex.printStackTrace();
				LOG.error(ex.getMessage(), ex);
			}
		}
		if (object.getAttribute(DATA_FIELD_NAME) != null) {
			dataFieldName = object.getAttribute(DATA_FIELD_NAME);
		}
		if (object.getAttribute(SORT_ORDER) != null) {
			try {
				sortOrder = Integer.parseInt(object.getAttribute(SORT_ORDER));
			} catch (Exception ex) {
				ex.printStackTrace();
				LOG.error(ex.getMessage(), ex);
			}
		}
		if (object.getAttribute(DATA_FIELD_DESCRIPTION) != null) {
			dataFieldDescription = object.getAttribute(DATA_FIELD_DESCRIPTION);
		}
		if (object.getAttribute(DATA_MAXIMUM_LENGTH) != null) {
			try {
				maximumLength = Integer.parseInt(object
						.getAttribute(DATA_MAXIMUM_LENGTH));
			} catch (Exception ex) {
				ex.printStackTrace();
				LOG.error(ex.getMessage(), ex);
			}
		}
		if (object.getAttribute(DATA_FIELD_VALUE) != null) {
			dataFieldValue = object.getAttribute(DATA_FIELD_VALUE);
			if (dataFieldValue.length() > maximumLength) {
				dataFieldValue = dataFieldValue.substring(0, maximumLength);
			}
		}
		if (object.getAttribute(DATA_TYPE) != null) {
			dataType = EDataType.valueOf(object.getAttribute(DATA_TYPE));
		}
		listItems = new ArrayList<MetaDataListValues>();
		for (XMLObject child : object.getChildObjects()) {
			if (child.getObjectType()
					.equals(MetaDataListValues.getObjectType())) {
				listItems.add(MetaDataListValues.create(child));
			}
		}
		Collections.sort(listItems, new MetaDataListValuesComparator());
		if (object.getAttribute(IS_COMPULSORY) != null) {
			try {
				isCompulsory = Boolean.parseBoolean(object
						.getAttribute(IS_COMPULSORY));
			} catch (Exception ex) {
				ex.printStackTrace();
				LOG.error(ex.getMessage(), ex);
			}
		}
		if (object.getAttribute(IS_CUSTOMIZABLE) != null) {
			try {
				isCustomizable = Boolean.parseBoolean(object
						.getAttribute(IS_CUSTOMIZABLE));
			} catch (Exception ex) {
				ex.printStackTrace();
				LOG.error(ex.getMessage(), ex);
			}
		}
		if (object.getAttribute(SAVED_WITH_TEMPLATE) != null) {
			try {
				savedWithTemplate = Boolean.parseBoolean(object
						.getAttribute(SAVED_WITH_TEMPLATE));
			} catch (Exception ex) {
				ex.printStackTrace();
				LOG.error(ex.getMessage(), ex);
			}
		}
		if (object.getAttribute(ALLOWS_MULTIPLE_ROWS) != null) {
			try {
				allowsMultipleRows = Boolean.parseBoolean(object
						.getAttribute(ALLOWS_MULTIPLE_ROWS));
			} catch (Exception ex) {
				ex.printStackTrace();
				LOG.error(ex.getMessage(), ex);
			}
		}
		if (object.getAttribute(IS_SET_BY_SYSTEM) != null) {
			try {
				isSetBySystem = Boolean.parseBoolean(object
						.getAttribute(IS_SET_BY_SYSTEM));
			} catch (Exception ex) {
				ex.printStackTrace();
				LOG.error(ex.getMessage(), ex);
			}
		}
		if (object.getAttribute(WILL_BE_UPLOADED) != null) {
			try {
				willBeUploaded = Boolean.parseBoolean(object
						.getAttribute(WILL_BE_UPLOADED));
			} catch (Exception ex) {
				ex.printStackTrace();
				LOG.error(ex.getMessage(), ex);
			}
		}
		if (object.getAttribute(POPULATED_FROM_CMS) != null) {
			try {
				isPopulatedFromCms = Boolean.parseBoolean(object
						.getAttribute(POPULATED_FROM_CMS));
			} catch (Exception ex) {
				ex.printStackTrace();
				LOG.error(ex.getMessage(), ex);
			}
		}
		if (object.getAttribute(CMS_FIELD_NAME) != null) {
			cmsFieldName = object.getAttribute(CMS_FIELD_NAME);
		}
		if (object.getAttribute(IS_DNX) != null) {
			try {
				isDNXType = Boolean.parseBoolean(object.getAttribute(IS_DNX));
			} catch (Exception ex) {
				ex.printStackTrace();
				LOG.error(ex.getMessage(), ex);
			}
		}
		if (object.getAttribute(IS_DCTERMS) != null) {
			try {
				isDCTermsType = Boolean.parseBoolean(object
						.getAttribute(IS_DCTERMS));
			} catch (Exception ex) {
				ex.printStackTrace();
				LOG.error(ex.getMessage(), ex);
			}
		}
		if (object.getAttribute(DNX_TYPE) != null) {
			dnxType = object.getAttribute(DNX_TYPE);
		}
		if (object.getAttribute(DNX_SUB_TYPE) != null) {
			dnxSubType = object.getAttribute(DNX_SUB_TYPE);
		}
		if (object.getAttribute(DC_TYPE) != null) {
			dcType = object.getAttribute(DC_TYPE);
		}
		if (object.getAttribute(DC_XSI_ATTR) != null) {
			dcXSIAttr = object.getAttribute(DC_XSI_ATTR);
		}
		if (object.getAttribute(DCTERMS_TYPE) != null) {
			dcTermsType = object.getAttribute(DCTERMS_TYPE);
		}
		if (object.getAttribute(DEFAULT_VALUE) != null) {
			defaultValue = object.getAttribute(DEFAULT_VALUE);
		}
		if (object.getAttribute(IS_VISIBLE) != null) {
			try {
				isVisible = Boolean.parseBoolean(object
						.getAttribute(IS_VISIBLE));
			} catch (Exception ex) {
				ex.printStackTrace();
				LOG.error(ex.getMessage(), ex);
			}
		} else {
			isVisible = true;
		}

		if (object.getAttribute(PROVENANCE_NOTE_EVENT_TYPE) != null) {
			provenanceNoteEventType = object
					.getAttribute(PROVENANCE_NOTE_EVENT_TYPE);
		}

		if (object.getAttribute(PROVENANCE_EVENT_IDENTIFIER_VALUE) != null) {
			provenanceEventIdentifierValue = object
					.getAttribute(PROVENANCE_EVENT_IDENTIFIER_VALUE);
		}

		if (object.getAttribute(PROVENANCE_EVENT_IDENTIFIER_TYPE) != null) {
			provenanceEventIdentifierType = object
					.getAttribute(PROVENANCE_EVENT_IDENTIFIER_TYPE);
		}

		if (object.getAttribute(PROVENANCE_EVENT_OUTCOME_VALUE) != null) {
			provenanceEventOutcome = Boolean.parseBoolean(object
					.getAttribute(PROVENANCE_EVENT_OUTCOME_VALUE));
		}

		if (object.getAttribute(PROVENANCE_EVENT_OUTCOME_DETAIL) != null) {
			provenanceEventOutcomeDetail = object
					.getAttribute(PROVENANCE_EVENT_OUTCOME_DETAIL);
		}
		
		if (object.getAttribute(PROVENANCE_EVENT_DESCRIPTION) != null) {
			ProvenanceEventDescription = object
					.getAttribute(PROVENANCE_EVENT_DESCRIPTION);
		}

	}

	@Override
	public String getDcXSIAttr() {
		return dcXSIAttr;
	}

	@Override
	public void setDcXSIAttr(String value) {
		dcXSIAttr = value;
		
	}
}
