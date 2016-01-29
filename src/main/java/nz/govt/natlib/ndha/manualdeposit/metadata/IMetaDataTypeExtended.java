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

import java.util.List;

import nz.govt.natlib.ndha.common.mets.IMetaDataType;

public interface IMetaDataTypeExtended extends IMetaDataType {

	public boolean getIsCMSIDField();

	public void setIsCMSIDField(boolean value);

	public boolean getIsCMSSystemField();

	public void setIsCMSSystemField(boolean value);

	public boolean getIsCMSDescriptionField();

	public void setIsCMSDescriptionField(boolean value);

	public int getSortOrder();

	public void setSortOrder(int value);

	public boolean getWillBeUploaded();

	public void setWillBeUploaded(boolean value);

	public boolean getIsCompulsory();

	public void setIsCompulsory(boolean value);
	
	public boolean getIsCustomizable();

	public void setIsCustomizable(boolean value);

	public boolean getAllowsMultipleRows();

	public void setAllowsMultipleRows(boolean value);

	public EDataType getDataType();

	public void setDataType(EDataType value);

	public boolean getIsSetBySystem();

	public void setIsSetBySystem(boolean value);

	public List<MetaDataListValues> getListItems();

	public void setListItems(List<MetaDataListValues> value);

	public boolean getSavedWithTemplate();

	public void setSavedWithTemplate(boolean value);

	public String getDefaultValue();

	public void setDefaultValue(String value);

	public boolean getIsVisible();

	public void setIsVisible(boolean value);

	public boolean getIsPopulatedFromCMS();

	public void setIsPopulatedFromCMS(boolean value);

	public String getCMSFieldName();

	public void setCMSFieldName(String value);

	public boolean getProvenanceEventOutcome();

	public void setProvenanceEventOutcome(boolean provenanceEventOutcome);

	public String getProvenanceEventOutcomeDetail();
	
	public String getProvenanceEventDescription();
	
	public void setProvenanceEventDescription(String provenanceEventDescription);

	public void setProvenanceEventOutcomeDetail(
			String provenanceEventOutcomeDetail);

	public String getProvenanceEventIdentifierType();

	public void setProvenanceEventIdentifierType(
			String provenanceEventIdentifierType);

	public String getProvenanceEventIdentifierValue();

	public void setProvenanceEventIdentifierValue(
			String provenanceEventIdentifierValue);

	public String getProvenanceNoteEventType();

	public void setProvenanceNoteEventType(String provenanceNoteEventType);

	public boolean isEquivalentTo(IMetaDataTypeExtended meta,
			boolean includeSortOrder);

	public boolean getIsSet();

	public void duplicate(IMetaDataTypeExtended dupTo) throws Exception;

	public String getDisplayValue();
	
	public boolean isDNXState();
	public void setIsDNXState(final boolean value);

	public boolean isDCTermsState();
	public void setIsDCTermsState(final boolean value);
}
