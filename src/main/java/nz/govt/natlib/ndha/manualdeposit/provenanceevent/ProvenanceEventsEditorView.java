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

import java.util.List;

import nz.govt.natlib.ndha.manualdeposit.IBaseForm;

public interface ProvenanceEventsEditorView extends IBaseForm {

	public abstract String getEventIdentifierValue();

	public abstract void setEventIdentifierValue(String eventIdentifierValue);

	public abstract boolean getEventOutcomeValue();

	public abstract void setEventOutcomeValue(boolean eventDetail);

	public abstract String getEventOutcomeDetailValue();

	public abstract void setEventOutcomeDetailValue(String eventOutcomeDetail);

	public abstract String getEventIdentifierType();

	public abstract void setEventIdentifierType(String eventIdentifierType);

	public abstract String getSelectedEventType();

	public abstract void setEventTypes(List<String> values);

	public abstract void setSelectedEventType(String item);

	public abstract void setAddProvenanceNoteEnabled(boolean enabled);
	
	public abstract void setCopyProvenanceNoteEnabled(boolean enabled);

	public abstract void setEditableControlsEnabled(boolean enabled);
	
	public abstract void resetEventControls();

	public abstract void setWarningMessage(String message);

	public abstract void setSelectNotesItemIndex(int index);

	public abstract void setVisible(boolean isVisible);

	public abstract void setPresenter(ProvenanceEventsPresenter presenter);

	public abstract ProvenanceEventsPresenter getPresenter();
	
	public abstract void setProvenanceEventDescription(String provenanceEventDescription);
	
	public String getProvenanceEventDescription();

}
