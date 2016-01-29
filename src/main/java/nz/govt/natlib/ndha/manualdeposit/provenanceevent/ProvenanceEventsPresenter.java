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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import nz.govt.natlib.ndha.common.mets.FileGroupCollection;
import nz.govt.natlib.ndha.common.mets.FileSystemObject;
import nz.govt.natlib.ndha.manualdeposit.AppProperties;
import nz.govt.natlib.ndha.manualdeposit.metadata.EDataType;
import nz.govt.natlib.ndha.manualdeposit.metadata.IMetaDataTypeExtended;

public final class ProvenanceEventsPresenter {

	private final List<ProvenanceEvent> theProvenanceNotes = new ArrayList<ProvenanceEvent>();
	private final Map<String, List<ProvenanceEvent>> theEntityChildProvenanceNotes = new HashMap<String, List<ProvenanceEvent>>();
	private ProvenanceEventsEditorView theView;
	private int theSelectedNoteIndex = -1;
	private int thePreviousEntityIndex = -1;
	private int thePreviousNoteIndex = -1;
	private int theSelectedEntityNameIndex = -1;
	private ProvenanceNotesObserver theOnCloseObserver;
	private JList<Object> theProvenanceNotesList;
	private JList<Object> theEntityNamesList;
	private DefaultListModel<Object> theProvenanceNotesListModel;
	private DefaultListModel<Object> theEntityNamesListModel;
	private ArrayList<FileGroupCollection> theEntities;
	private FileSystemObject theFSO;
	private boolean isSystemChange = false;
	private boolean entityChanged = false;
	private boolean listEditingLock = false;
	private final static int NOTE_LIMIT = 20;

// New attributes as per Rosetta 2.0 requirements.
	public final static String DEFAULT_IDENTIFIER_TYPE = "Indigo";
	public final static String DEFAULT_IDENTIFIER_VALUE = "Indigo_1";
	public final static String DEFAULT_EVENT_DESCRIPTION = "Provenance Note from Indigo";
	private final AppProperties applicationProperties;

	public ProvenanceEventsPresenter(final AppProperties appProperties) {
		applicationProperties = appProperties;
	}

	public ProvenanceEventsPresenter(final ProvenanceEventsEditorView view,
			final AppProperties appProperties, ArrayList<FileGroupCollection> entities, FileSystemObject fso) {
		applicationProperties = appProperties;
		theEntities = entities;
		theFSO = fso;
		setView(view);
	}

	public void setView(final ProvenanceEventsEditorView view) {
		this.theView = view;
		final nz.govt.natlib.ndha.common.mets.ProvenanceEvent.EventType[] eventTypeValues = nz.govt.natlib.ndha.common.mets.ProvenanceEvent.EventType
				.values();
		final List<String> eventTypeStrings = new ArrayList<String>(
				eventTypeValues.length);
		for (nz.govt.natlib.ndha.common.mets.ProvenanceEvent.EventType eventType : eventTypeValues)
			eventTypeStrings.add(eventType.toString());

		view.setEventTypes(eventTypeStrings);
		view.setEditableControlsEnabled(false);
		view.setCopyProvenanceNoteEnabled(false);
	}

	public void setHandlers(final JList<Object> provenanceNotesList, final JList<Object> entityNamesList) {
		theProvenanceNotesList = provenanceNotesList;
		theProvenanceNotesListModel = new DefaultListModel<Object>();
		theProvenanceNotesList.setModel(theProvenanceNotesListModel);
		theProvenanceNotesListModel.clear();
		theProvenanceNotesList
				.addListSelectionListener(new ListSelectionListener() {
					public void valueChanged(final ListSelectionEvent evt) {
						dataListValueChanged(evt);
					}
				});
		
		theEntityNamesList = entityNamesList;
		theEntityNamesListModel = new DefaultListModel<Object>();
		theEntityNamesList.setModel(theEntityNamesListModel);
		theEntityNamesListModel.clear();
		theEntityNamesList
				.addListSelectionListener(new ListSelectionListener() {
					public void valueChanged(final ListSelectionEvent evt) {
						dataListEntityNameChanged(evt);
					}
				});
	}

	private void dataListValueChanged(final ListSelectionEvent evt) { // NOPMD
		// OK to swallow this one as it is an existing note
		// if any issues haven't been picked up already we are in trouble
		try {
			if(!listEditingLock){
				selectItem(theProvenanceNotesList.getSelectedIndex());
			}
		} catch (Exception ex) {
			listEditingLock = false;
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null ,ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		} // NOPMD
	}
	
	private void dataListEntityNameChanged(final ListSelectionEvent evt) { // NOPMD
		// OK to swallow this one as it is an existing note
		// if any issues haven't been picked up already we are in trouble
		try {
			//Check size of list in map for selected entity
			if(!listEditingLock){
				if(!evt.getValueIsAdjusting()){
					listEditingLock = true;
					Object eventSource = evt.getSource();
					if(eventSource instanceof JList){
						@SuppressWarnings("unchecked")
						JList<Object> list = (javax.swing.JList<Object>) evt.getSource();
						int selected = list.getSelectedIndex();
						thePreviousEntityIndex = selected == evt.getFirstIndex() ? evt.getLastIndex() : evt.getFirstIndex();
						thePreviousNoteIndex = new Integer(theProvenanceNotesList.getSelectedIndex());
						theSelectedNoteIndex = 0;
						entityChanged = true;
						setSelectedNote(false);
						entityChanged = false;
						listEditingLock = false;
					}
				}
			}
		} catch (Exception ex) {
			listEditingLock = false;
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null ,ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		} // NOPMD
	}

	public int getProvenanceNotesCount() {
		return this.theProvenanceNotes.size();
	}

	public void addNewProvenanceNote() throws Exception {
		final ProvenanceEvent event = new ProvenanceEvent();
		event.setEventIdentifierType(DEFAULT_IDENTIFIER_TYPE);
		event.setEventIdentifierValue(DEFAULT_IDENTIFIER_VALUE);
		event.setEventDescription(DEFAULT_EVENT_DESCRIPTION);
		event.setMaximumLength(applicationProperties.getApplicationData()
				.getMaximumProvenanceEventLength());
		
		// Parent selected in entity list
		if(theEntityNamesList.getSelectedIndex() < 1){
			if(theProvenanceNotes.size() >= NOTE_LIMIT){
				JOptionPane.showMessageDialog(null ,"Note limit of "+NOTE_LIMIT+" has been reached for the parent", "Unable to add new note", JOptionPane.ERROR_MESSAGE);
				return;
			}
			copyCurrentValuesToCurrentNote();
			theProvenanceNotes.add(event);
			generateNoteLabels();
			theSelectedNoteIndex = theProvenanceNotes.size() - 1;
			setSelectedNote(false);
		}
		// Child entity selected
		else{
			copyCurrentValuesToCurrentNote();
			// Get the list of prov events in the Entity Map
			List<ProvenanceEvent> entityNotes = theEntityChildProvenanceNotes.get(theEntityNamesList.getSelectedValue());
			if(entityNotes == null){
				entityNotes = new ArrayList<ProvenanceEvent>();
			}
			else if(entityNotes.size() >= NOTE_LIMIT){
				JOptionPane.showMessageDialog(null ,"Note limit of "+NOTE_LIMIT+" has been reached for this entity", "Unable to add new note", JOptionPane.ERROR_MESSAGE);
				return;
			}
			entityNotes.add(event);
			theEntityChildProvenanceNotes.put((String)theEntityNamesList.getSelectedValue(), entityNotes);
			generateNoteLabels();
			theSelectedNoteIndex = entityNotes.size() - 1;
			setSelectedNote(false);
		}
		
	}
	
	
	/**
	 * New method for copying an existing note to other entities. Can specify entity to copy note to.
	 * Added by Ben.  8/10/2013.
	 * @param entityName
	 * @throws Exception
	 */
	public void addNewProvenanceNote(String entityName) throws Exception {
		final ProvenanceEvent event = new ProvenanceEvent();
		event.setEventIdentifierType(DEFAULT_IDENTIFIER_TYPE);
		event.setEventIdentifierValue(DEFAULT_IDENTIFIER_VALUE);
		event.setEventDescription(DEFAULT_EVENT_DESCRIPTION);
		event.setMaximumLength(applicationProperties.getApplicationData().getMaximumProvenanceEventLength());

		setEventValuesFromView(event);
		// Get the list of prov events in the Entity Map
		List<ProvenanceEvent> entityNotes = theEntityChildProvenanceNotes.get(entityName);
		// If there are no notes under this entity then create a blank list first.
		if(entityNotes == null){
			entityNotes = new ArrayList<ProvenanceEvent>();
		}
		else if(entityNotes.size() >= NOTE_LIMIT){
			JOptionPane.showMessageDialog(null ,"Note limit of "+NOTE_LIMIT+" has been reached for this entity", "Unable to add new note", JOptionPane.ERROR_MESSAGE);
			return;
		}
		entityNotes.add(event);
		theEntityChildProvenanceNotes.put(entityName, entityNotes);
		
	}
	
	
	/**
	 * New method for checking if note limit has been reached for specified entity.
	 * Added by Ben. 8/10/2013.
	 * @param entityName
	 * @return true if limit reached
	 * @throws Exception
	 */
	public boolean noteLimitReached(String entityName) throws Exception {

		// Get the list of prov events in the Entity Map
		List<ProvenanceEvent> entityNotes = theEntityChildProvenanceNotes.get(entityName);

		if(entityNotes == null){
			return false;
		}
		else if(entityNotes.size() < NOTE_LIMIT){
			return false;
		}
		
		return true;
	}

	private void generateNoteLabels() {
		if(theEntityNamesList.getSelectedIndex() < 1){			
			if (theProvenanceNotesListModel != null) {
				theProvenanceNotesListModel.clear();
				for (int i = 1; i < theProvenanceNotes.size() + 1; i++) {
					theProvenanceNotesListModel.addElement("Note " + i);
				}
			}
			
			if (theProvenanceNotes.isEmpty()) {
				theSelectedNoteIndex = -1;
			}
		}
		else{
			List<ProvenanceEvent> entityNotes = theEntityChildProvenanceNotes.get(theEntityNamesList.getSelectedValue());
			if(entityNotes != null){
				
				if (theProvenanceNotesListModel != null) {
					theProvenanceNotesListModel.clear();
					for (int i = 1; i < entityNotes.size() + 1; i++) {
						theProvenanceNotesListModel.addElement("Note " + i);
					}
				}
				
				if (entityNotes.isEmpty()) {
					theSelectedNoteIndex = -1;
				} 
			}else{
				if (theProvenanceNotesListModel != null) {
					theProvenanceNotesListModel.clear();
				}
				theSelectedNoteIndex = -1;
			}
		}
	}
	
	private void generateEntityNameListLabels() {
		//Generate entity list only if theFSo object is not null and if there are more than one entities. 
		if (theFSO != null){
			if (theEntityNamesListModel != null) {
				theEntityNamesListModel.clear();
				theEntityNamesListModel.addElement(theFSO.getFileNameNoSuffix()+" (Parent)");
				if (theEntities.size() > 1){
					for (int i = 0; i < theEntities.size(); i++) {
						theEntityNamesListModel.addElement(theEntities.get(i).getEntityName());
					}
				}
			}
			if (theEntityChildProvenanceNotes.isEmpty()) {
				theSelectedEntityNameIndex = -1;
			} else {
				theSelectedEntityNameIndex = 0;
			}
		}
	}

	public void selectItem(final int selectedIndex) throws Exception {
		
		if (selectedIndex < 1) {
			theView.setCopyProvenanceNoteEnabled(false);
//			theSelectedNoteIndex = -1;
//			return;
		}
		
		
		
		if (selectedIndex < 0) {
			theView.setEditableControlsEnabled(false);
			theSelectedNoteIndex = -1;
			return;
		}

		if (!isSystemChange) {
			copyCurrentValuesToCurrentNote();
		}

		if (theSelectedNoteIndex < 0) {
			theView.resetEventControls();
			theView.setEditableControlsEnabled(false);
			return;
		}
		
		theSelectedNoteIndex = selectedIndex;

		
		if(theEntityNamesList.getSelectedIndex() < 1){
			final ProvenanceEvent event = theProvenanceNotes.get(selectedIndex);
			setViewValuesFromEvent(event);
		}
		else{
			List<ProvenanceEvent> entityNotes = theEntityChildProvenanceNotes.get(theEntityNamesList.getSelectedValue());
			if(entityNotes != null){
				if(!entityNotes.isEmpty()){
					final ProvenanceEvent event = entityNotes.get(selectedIndex);
					setViewValuesFromEvent(event);
				}
			}
		}
		
	}

	public List<ProvenanceEvent> getProvenanceNotes() {
		return theProvenanceNotes;
	}
	
	public Map<String, List<ProvenanceEvent>> getChildProvenanceNotes() {
		return theEntityChildProvenanceNotes;
	}

	private void copyCurrentValuesToCurrentNote() throws Exception {
		String selectedEntityValue = (String) theEntityNamesList.getSelectedValue();
		int selectedEntity = theEntityNamesList.getSelectedIndex();
		int selectedNote = theSelectedNoteIndex;
		
		if(entityChanged){
			selectedEntity = thePreviousEntityIndex;
			selectedNote = thePreviousNoteIndex;
			if(selectedEntity > 0)
				selectedEntityValue = (String) theEntityNamesList.getModel().getElementAt(selectedEntity);
		}
		
		// If Parent is selected in Entity List
		if(selectedEntity < 1){
				
			if (!theProvenanceNotes.isEmpty() && selectedNote >= 0) {
				
				final ProvenanceEvent event = theProvenanceNotes.get(selectedNote);
				setEventValuesFromView(event);
				
			}
		}
		else{
			List<ProvenanceEvent> entityNotes = theEntityChildProvenanceNotes.get(selectedEntityValue);
			if(entityNotes != null){
				if(!entityNotes.isEmpty() && selectedNote >= 0){
					final ProvenanceEvent event = entityNotes.get(selectedNote);
					setEventValuesFromView(event);
				}
			}
		}
		
		if(entityChanged){
			generateNoteLabels();
			entityChanged = false;
		}
		
	}
	
	
	/**
	 * New method for generating a JList of available entities that a note can be copied to.
	 * Called from ProvenanceEventsEditor.java.
	 * Added by Ben. 8/10/2013.
	 * @param copyEditor
	 */
	public void copyExistingProvenanceNote(CopyEventsEditor copyEditor){
		JList<Object> theCopyList = new JList<Object>();
		DefaultListModel<Object> theCopyListModel = new DefaultListModel<Object>();
		
		// Check that user isn't trying to copy a parent note.
		if(theEntityNamesList.getSelectedIndex() >= 1){
			// Check that user hasn't clicked button when no notes under this entity.
			if(theProvenanceNotesList.getSelectedIndex() != -1){
				String selectedEntity = (String) theEntityNamesList.getSelectedValue();
				
				if (theEntities.size() > 1){
					for (int i = 0; i < theEntities.size(); i++) {
						// The entity that current note belongs to is excluded from this list.
						if(!selectedEntity.equals(theEntities.get(i).getEntityName())){
							theCopyListModel.addElement(theEntities.get(i).getEntityName());
						}
					}
				}
				theCopyList.setModel(theCopyListModel);
				copyEditor.setJList(theCopyList);
				copyEditor.showView();
			}
			else{
				JOptionPane.showMessageDialog(null ,"There is no note to copy", "Warning", JOptionPane.ERROR_MESSAGE);
			}
			
		}
		else{
			JOptionPane.showMessageDialog(null ,"Cannot copy a note that is under the parent", "Warning", JOptionPane.ERROR_MESSAGE);
		}
		
	}
	
	
	private void setEventValuesFromView(ProvenanceEvent event) throws Exception {
		event.setEventOutcome(theView.getEventOutcomeValue());
		event.setEventOutcomeDetail(theView.getEventOutcomeDetailValue());
		event.setEventIdentifierType(theView.getEventIdentifierType());
		event.setEventIdentifierValue(theView.getEventIdentifierValue());
		event.setEventType(theView.getSelectedEventType());
		event.setMaximumLength(applicationProperties.getApplicationData().getMaximumProvenanceEventLength());
			
		if ((event.getEventDescription() == null)||(event.getEventDescription() == "")){
			event.setEventDescription(DEFAULT_EVENT_DESCRIPTION);
		}
	}
	
	private void setViewValuesFromEvent(ProvenanceEvent event) throws Exception {
		theView.setEventOutcomeValue(event.getEventOutcome());
		theView.setEventOutcomeDetailValue(event.getEventOutcomeDetail());
		theView.setEventIdentifierType(event.getEventIdentifierType());
		theView.setEventIdentifierValue(event.getEventIdentifierValue());
		theView.setSelectedEventType(event.getEventType());
		theView.setProvenanceEventDescription(event.getEventDescription());
		theView.setEditableControlsEnabled(true);
		if(theEntityNamesList.getSelectedIndex() >= 1){
			theView.setCopyProvenanceNoteEnabled(true);
		}
		
	}

	public void removeSelectedItem() {
		if(!listEditingLock){
			listEditingLock = true;
			
			// Parent selected in entity list
			if(theEntityNamesList.getSelectedIndex() < 1){
				theProvenanceNotes.remove(theSelectedNoteIndex);
				generateNoteLabels();
				if (theSelectedNoteIndex >= theProvenanceNotes.size()) {
					theSelectedNoteIndex = theProvenanceNotes.size()-1;
				}
			}
			// Child entity selected
			else{
				List<ProvenanceEvent> entityNotes = theEntityChildProvenanceNotes.get(theEntityNamesList.getSelectedValue());
				if(entityNotes != null){
					entityNotes.remove(theSelectedNoteIndex);
					generateNoteLabels();
					if (theSelectedNoteIndex >= entityNotes.size()) {
						theSelectedNoteIndex = entityNotes.size()-1;
					}
				}
			}
			
			if(theSelectedNoteIndex < 0){
				theView.setEditableControlsEnabled(false);
				theView.setCopyProvenanceNoteEnabled(false);
			}
			else{
				isSystemChange = true;
				// OK to swallow this one as it is an existing note
				// if any issues haven't been picked up already we are in trouble
				try {
					selectItem(theSelectedNoteIndex);
					setSelectedNote();
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null ,ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					listEditingLock = false;
				}
				isSystemChange = false;
			}
			clearWarningMessage();
			listEditingLock = false;
		}
	}

	public boolean lengthOK(final String noteDetail) {
		return (noteDetail.length() < applicationProperties
				.getApplicationData().getMaximumProvenanceEventLength());
	}

	public int getMaxNoteLength() {
		return applicationProperties.getApplicationData()
				.getMaximumProvenanceEventLength();
	}
	
	public void saveForm() throws Exception {
		copyCurrentValuesToCurrentNote();		
	}

	public void closeForm() throws Exception {
		copyCurrentValuesToCurrentNote();
		theView.setVisible(false);

		if (theOnCloseObserver != null)
			theOnCloseObserver.onCloseOfProvenanceNotesDialog(this);
	}

	public void setOnCloseObserver(final ProvenanceNotesObserver onCloseObserver) {
		this.theOnCloseObserver = onCloseObserver;
	}

	public void openForm(final List<IMetaDataTypeExtended> metaDataFields, Map<String, List<IMetaDataTypeExtended>> childMetaDataFields) {
		repopulatedNotesFrom(metaDataFields, childMetaDataFields);
		clearWarningMessage();
		theView.showView();
	}

	private void setSelectedNote(final boolean setFirst) {
		if (setFirst) {
			if (theProvenanceNotes.isEmpty()) {
				theSelectedNoteIndex = -1;
				return;
			} else {
				theSelectedNoteIndex = 0;
			}
		}
		// OK to swallow this one as it is an existing note
		// if any issues haven't been picked up already we are in trouble
		try {
			selectItem(theSelectedNoteIndex);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null ,ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			listEditingLock = false;
		}
		
		if (theProvenanceNotesList != null) {
			theProvenanceNotesList.setSelectedIndex(theSelectedNoteIndex);
		}
	}
	
	private void setSelectedNote() {		
		if (theProvenanceNotesList != null) {
			theProvenanceNotesList.setSelectedIndex(theSelectedNoteIndex);
		}		
	}
	
	private void setSelectedEntity(final boolean setFirst) {
		if (setFirst) {
			theSelectedEntityNameIndex = 0;
		}
		if (theEntityNamesList != null) {
			theEntityNamesList.setSelectedIndex(theSelectedEntityNameIndex);
		}		
	}

	private void repopulatedNotesFrom(
			final List<IMetaDataTypeExtended> metaDataFields, final Map<String, List<IMetaDataTypeExtended>> childMetaDataFields) {
		theProvenanceNotes.clear();
		theEntityChildProvenanceNotes.clear();

		ProvenanceEvent event = null;

		for (IMetaDataTypeExtended metaDataType : metaDataFields) {
			if (metaDataType.getDataType() == EDataType.ProvenanceNote) {
				event = new ProvenanceEvent(metaDataType);
				theProvenanceNotes.add(event);
			}
		}
		
		// Add child provenance notes
		event = null;
		
		if(childMetaDataFields != null){
			Set<String> entityNames = childMetaDataFields.keySet();
		
			for (String entity : entityNames) {
				List<ProvenanceEvent> childNotes = new ArrayList<ProvenanceEvent>();
				for(IMetaDataTypeExtended metaDataType: childMetaDataFields.get(entity)){
					if (metaDataType.getDataType() == EDataType.ProvenanceNote) {
						event = new ProvenanceEvent(metaDataType);
						childNotes.add(event);
					}
				}
				theEntityChildProvenanceNotes.put(entity, childNotes);
			}
		}
		
		if(!listEditingLock){
			listEditingLock = true;
			generateNoteLabels();
			generateEntityNameListLabels();
			isSystemChange = true;
			theSelectedNoteIndex = 0;
			theSelectedEntityNameIndex = 0;
			setSelectedEntity(true);
			setSelectedNote(true);		
			isSystemChange = false;
			listEditingLock = false;
		}
	}

	private void clearWarningMessage() {
		theView.setWarningMessage("");
	}

}
