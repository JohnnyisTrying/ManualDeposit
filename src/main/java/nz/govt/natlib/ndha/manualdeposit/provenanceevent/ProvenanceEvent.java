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

package nz.govt.natlib.ndha.manualdeposit.provenanceevent;

import nz.govt.natlib.ndha.manualdeposit.exceptions.ProvenanceEventException;
import nz.govt.natlib.ndha.manualdeposit.metadata.EDataType;
import nz.govt.natlib.ndha.manualdeposit.metadata.IMetaDataTypeExtended;
import nz.govt.natlib.ndha.manualdeposit.metadata.MetaDataTypeImpl;

public class ProvenanceEvent {
	public static final String DEFAULT_EVENT_TYPE = nz.govt.natlib.ndha.common.mets.ProvenanceEvent.EventType.PROCESSING
			.toString();
	public static final String XML_OBJECT_TYPE = "ProvenanceEvent";

	private String eventType;
	private String eventIdentifierType;
	private String eventIdentifierValue;
	private boolean eventOutcome;
	private String eventOutcomeDetail;
	private int theMaximumLength = 100;
	private String eventDescription;

	public ProvenanceEvent(String eventIdentifierType,
			String eventIdentifierValue, String eventType,
			boolean eventOutcome, String eventOutcomeDetail, int maximumLength, String eventDescription) {
		this.eventIdentifierType = eventIdentifierType;
		this.eventIdentifierValue = eventIdentifierValue;
		this.eventType = eventType;
		this.eventOutcome = eventOutcome;
		this.eventOutcomeDetail = eventOutcomeDetail;
		this.eventDescription = eventDescription;
		theMaximumLength = maximumLength;
	}

	public ProvenanceEvent() {
		eventType = DEFAULT_EVENT_TYPE;
	}

	public ProvenanceEvent(IMetaDataTypeExtended metaDataType) {
		this.eventIdentifierType = metaDataType
				.getProvenanceEventIdentifierType();
		this.eventIdentifierValue = metaDataType
				.getProvenanceEventIdentifierValue();
		this.eventType = metaDataType.getProvenanceNoteEventType();
		this.eventOutcome = metaDataType.getProvenanceEventOutcome();
		this.eventOutcomeDetail = metaDataType
				.getProvenanceEventOutcomeDetail();
		theMaximumLength = metaDataType.getMaximumLength();
		this.eventDescription = metaDataType.getProvenanceEventDescription();
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getEventIdentifierType() {
		return eventIdentifierType;
	}

	public void setEventIdentifierType(String eventIdentifierType) {
		this.eventIdentifierType = eventIdentifierType;
	}

	public String getEventIdentifierValue() {
		return eventIdentifierValue;
	}

	public void setEventIdentifierValue(String eventIdentifierValue) {
		this.eventIdentifierValue = eventIdentifierValue;
	}
	
	public void setEventDescription(String eventDescription){
		this.eventDescription = eventDescription;
	}

	public String getEventOutcomeString() {
		if (eventOutcome) {
			return "SUCCESS";
		} else {
			return "FAILURE";
		}
	}

	public boolean getEventOutcome() {
		return eventOutcome;
	}

	public void setEventOutcome(boolean eventOutcome) {
		this.eventOutcome = eventOutcome;
	}

	public String getEventOutcomeDetail() {
		return eventOutcomeDetail;
	}

	public void setEventOutcomeDetail(String value) throws ProvenanceEventException
			{
		if (value.length() > theMaximumLength) {
			throw new ProvenanceEventException("You can only enter "+ theMaximumLength
					+" characters in the event outcome detail field.\nPlease use Indigo config to change this limit");
		}else{
		this.eventOutcomeDetail = value;
		}
	}

	public int getMaximumLength() {
		return theMaximumLength;
	}

	public void setMaximumLength(int value) throws ProvenanceEventException {
		if (this.eventOutcomeDetail != null
				&& this.eventOutcomeDetail.length() > value) {
			throw new ProvenanceEventException("Maximum length exceeded");
		}
		theMaximumLength = value;
	}
	
	public String getEventDescription() {
		return eventDescription;
	}

	public IMetaDataTypeExtended toMetadataType(String fieldDescription) {
		MetaDataTypeImpl metaDataType = new MetaDataTypeImpl();

		metaDataType.setDataFieldDescription("Provenance Event");
		metaDataType.setDataFieldName(XML_OBJECT_TYPE);
		metaDataType.setDataType(EDataType.ProvenanceNote);
		try {
			metaDataType.setMaximumLength(theMaximumLength);
			metaDataType.setDataFieldValue(toString());
		} catch (Exception ex) {
		}
		metaDataType.setAllowsMultipleRows(false);
		metaDataType.setIsDNX(true);

		metaDataType.setProvenanceEventOutcome(eventOutcome);
		metaDataType.setProvenanceEventOutcomeDetail(eventOutcomeDetail);
		metaDataType.setProvenanceEventIdentifierType(eventIdentifierType);
		metaDataType.setProvenanceEventIdentifierValue(eventIdentifierValue);
		metaDataType.setProvenanceNoteEventType(eventType);
		metaDataType.setProvenanceEventDescription(eventDescription);

		metaDataType.setSavedWithTemplate(true);
		metaDataType.setIsDNX(true);
		metaDataType.setIsSetBySystem(true);
		return metaDataType;
	}

	public String toString() {
		StringBuilder description = new StringBuilder();

		description.append(getEventOutcomeString());
		description.append("; ");

		description.append(eventOutcomeDetail);
		description.append("; ");

		description.append(eventType);
		description.append("; ");

		description.append(eventIdentifierType);
		description.append("; ");

		description.append(eventIdentifierValue);
		description.append("; ");
		
		description.append(eventDescription);
		description.append("; ");

		return description.toString();
	}

}
