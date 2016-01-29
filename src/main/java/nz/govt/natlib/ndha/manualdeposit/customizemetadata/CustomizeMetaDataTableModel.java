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
package nz.govt.natlib.ndha.manualdeposit.customizemetadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.table.DefaultTableModel;

import nz.govt.natlib.ndha.manualdeposit.metadata.IMetaDataTypeExtended;
import nz.govt.natlib.ndha.manualdeposit.provenanceevent.ProvenanceEvent;

public class CustomizeMetaDataTableModel extends DefaultTableModel {

	private static final long serialVersionUID = -6836674917556404711L;

	protected String[] columnNames = {"Entity Name"};
	protected Object[][] dataArray;
	// Persistent storage of custom meta data
	private Map<String, Map<String, String>> theCustomMetaData = null;
	private Map<String, List<IMetaDataTypeExtended>> theChildProvenanceNotes = new HashMap<String, List<IMetaDataTypeExtended>>();
	

	public CustomizeMetaDataTableModel(Object[][] rowData, String[] columnNames) {
		dataArray = rowData;
		this.columnNames = columnNames;
	}
	
	public CustomizeMetaDataTableModel(Object[][] rowData) {
		dataArray = rowData;
	}
	
	public CustomizeMetaDataTableModel() {}
	
	public String getColumnName(int column) {
		return columnNames[column];
	}

	public boolean isCellEditable(int row, int column) {
		
		switch (column) {
		//Only 1st Column (Entity Name) is not editable.
        case 0: return false;
        default:
            return true;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Class getColumnClass(int column) {
		return String.class;
	}

	public Object getValueAt(int row, int column) {
		return dataArray[row][column];
	}
	
	public void setValueAt(Object value, int row, int column) {
		dataArray[row][column] = value;
		fireTableCellUpdated(row, column);
	}

	public int getRowCount() {
		if (dataArray == null) {
			return 0;
		} else {
			return dataArray.length;
		}
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public void clearTable() {
		dataArray = new Object[0][0];
	}
	
	public void setMetaData(Map<String, Map<String, String>> newCustomMetaData){
		theCustomMetaData = newCustomMetaData;
	}
	
	public Map<String, Map<String, String>> getCustomMetaData(){
		return theCustomMetaData;
	}
	
	public Map<String, String> getCustomMetaDataForEntity(Object key){
		return theCustomMetaData.get(key);
	}
	
	public Map<String, List<IMetaDataTypeExtended>> getChildProvenanceEvents(){
		return theChildProvenanceNotes;
	}
	
	public void replaceChildProvenanceEvents(final Map<String, List<ProvenanceEvent>> events) {
		theChildProvenanceNotes.clear();
		
		Set<String> entityNames = events.keySet();
		for(String entity: entityNames){
			List<IMetaDataTypeExtended> childEvents = new ArrayList<IMetaDataTypeExtended>();
			for (ProvenanceEvent event : events.get(entity)) {
				IMetaDataTypeExtended metaData = event.toMetadataType(event.toString());
				childEvents.add(metaData);
			}
			theChildProvenanceNotes.put(entity, childEvents);
		}
	}
}
