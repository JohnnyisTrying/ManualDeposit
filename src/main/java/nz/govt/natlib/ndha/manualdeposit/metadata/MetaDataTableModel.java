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

import java.awt.Toolkit;
import java.util.HashMap;
import java.util.Map;

import javax.swing.table.DefaultTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class MetaDataTableModel extends DefaultTableModel {

	private static final long serialVersionUID = -1698732751244945391L;
	private final static Log LOG = LogFactory.getLog(MetaDataTableModel.class);
	public static final int PROPERTY_NAME = 0;
	public static final int PROPERTY_VALUE = 1;
	private MetaDataFields theMetaData = null;
	private static final String[] COLUMN_NAMES = { "Data Element", "Data Value" };
	private final UserGroupData theUserGroupData;
	private final Map<String, String> permanentFields = new HashMap<String, String>();

	private MetaDataFields filteredMetaData() {
		final MetaDataFields retVal = new MetaDataFields();
		if (theMetaData != null) {
			for (IMetaDataTypeExtended data : theMetaData) {
				if (data.getIsVisible()) {
					retVal.addMetaData(data);
				}
			}
		}
		return retVal;
	}

	public static MetaDataTableModel create(final UserGroupData userGroupData,
			final MetaDataFields.ECMSSystem systemType) throws Exception {
		return new MetaDataTableModel(userGroupData, systemType);
	}

	public MetaDataTableModel(final UserGroupData userGroupData,
			final MetaDataFields.ECMSSystem systemType) throws Exception {
		super(COLUMN_NAMES, 0);
		theUserGroupData = userGroupData;
		clearTableData();
		setMetaDataType(systemType);
	}

	public void setMetaDataType(final MetaDataFields.ECMSSystem systemType)
			throws Exception {
		MetaDataFields newMetaData = null;
		final ApplicationData appData = ApplicationData.getInstance();
		switch (systemType) {
		case CMS2:
			newMetaData = theUserGroupData.getCMS2MetaData();
			newMetaData.setCMSSystem(appData
					.getCMSSystemText(MetaDataFields.ECMSSystem.CMS2));
			break;
		case CMS1:
			newMetaData = theUserGroupData.getCMS1MetaData();
			newMetaData.setCMSSystem(appData
					.getCMSSystemText(MetaDataFields.ECMSSystem.CMS1));
			break;
		case StaffMediated:
			newMetaData = theUserGroupData.getStaffMediatedMetaData();
			newMetaData.setCMSSystem(appData
					.getCMSSystemText(MetaDataFields.ECMSSystem.StaffMediated));
			break;
		case NoSystem:
			newMetaData = theUserGroupData.getNoCMSMetaData();
			newMetaData.setCMSSystem(appData
					.getCMSSystemText(MetaDataFields.ECMSSystem.NoSystem));
			break;
		default:
			break;
		}
		if (!newMetaData.equals(theMetaData)) {
			theMetaData = newMetaData;
			clearTableData();
		}
	}

	public MetaDataFields getMetaData() {
		return theMetaData;
	}

	public String getColumnName(final int column) {
		return COLUMN_NAMES[column];
	}

	public void addPermanentField(final String fieldName,
			final String fieldValue) throws Exception {
		this.setPropertyValue(fieldName, fieldValue);
		permanentFields.put(fieldName, fieldValue);
	}

	public boolean isCellEditable(final int row, final int column) {
		LOG.debug("Checking row " + row + ", col " + column);
		boolean isEditable = false;
		if (column == PROPERTY_VALUE) {
			final IMetaDataTypeExtended record = filteredMetaData().getAt(row);
			isEditable = (!record.getIsSetBySystem());
			LOG.debug("Correct column, Editable?: " + isEditable + ", Field: "
					+ record.getDataFieldName() + ", value: "
					+ record.getDataFieldValue());
		}
		return isEditable;
	}

	@SuppressWarnings("unchecked")
	public Class getColumnClass(final int column) {
		return String.class;
	}

	public Object getValueAt(final int row, final int column) {
		final IMetaDataTypeExtended record = filteredMetaData().getAt(row);
		switch (column) {
		case PROPERTY_NAME:
			return record.getDataFieldDescription();
		case PROPERTY_VALUE:
			return record.getDisplayValue();
		default:
			return new Object();
		}
	}

	public void setValueAt(final Object value, final int row, final int column) {
		final IMetaDataTypeExtended record = filteredMetaData().getAt(row);
		switch (column) {
		case PROPERTY_NAME:
			record.setDataFieldName((String) value);
			break;
		case PROPERTY_VALUE:
			if (value instanceof MetaDataListValues) {
				try {
					final MetaDataListValues listValue = (MetaDataListValues) value;
					record.setDataFieldValue(listValue.getValue());
				} catch (Exception ex) {
				}
			} else {
				String stringValue;
				if (value == null) {
					stringValue = "";
				} else {
					stringValue = (String) value;
				}
				if (stringValue.length() > record.getMaximumLength()) {
					Toolkit.getDefaultToolkit().beep();
				} else {
					try {
						record.setDataFieldValue((String) value);
					} catch (Exception ex) {
					}
				}
			}
			break;
		default:
			LOG.debug("invalid index");
		}
		fireTableCellUpdated(row, column);
	}

	public int getRowCount() {
		return filteredMetaData().size();
	}

	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	public boolean hasEmptyRow() {
		if (filteredMetaData().size() == 0)
			return false;
		final IMetaDataTypeExtended record = filteredMetaData().getAt(
				filteredMetaData().size() - 1);
		return ((record.getDataFieldName() == null || record.getDataFieldName()
				.trim().equals("")) && (record.getDataFieldValue() == null || record
				.getDataFieldValue().trim().equals("")));
	}

	public IMetaDataTypeExtended getRow(final int rowNum) {
		if (rowNum < filteredMetaData().size()) {
			return filteredMetaData().getAt(rowNum);
		} else {
			return null;
		}
	}

	public void addRow(final IMetaDataTypeExtended property) {
		theMetaData.addMetaData(property);
		fireTableRowsInserted(theMetaData.size() - 1, theMetaData.size() - 1);
	}

	public void setCMSID(final String value) throws Exception {
		theMetaData.setCMSID(value);
	}

	public void setCMSSystem(final String value) throws Exception {
		theMetaData.setCMSSystem(value);
	}

	public void setCMSDescription(final String value) throws Exception {
		theMetaData.setCMSDescription(value);
	}

	public void setPropertyValue(final String propertyName,
			final String propertyValue) throws Exception {
		final IMetaDataTypeExtended property = theMetaData
				.getMetaDataType(propertyName);
		if (property != null) {
			property.setDataFieldValue(propertyValue);
		}
	}

	public void clearTableData() {
		final Map<String, IMetaDataTypeExtended> duplicateAllowed = new HashMap<String, IMetaDataTypeExtended>();
		if (theMetaData != null) {
			final String cmsSystem = theMetaData.getCMSSystem();
			for (int i = theMetaData.size() - 1; i >= 0; i--) { // May be
																// removing
																// items
				IMetaDataTypeExtended current = theMetaData.getAt(i);
				try {
					current.setDataFieldValue(current.getDefaultValue());
				} catch (Exception ex) {
				}
				if (current.getDataType() == EDataType.ProvenanceNote) {
					theMetaData.deleteMetaData(current);
				} else if (current.getAllowsMultipleRows()) {
					if (duplicateAllowed
							.containsKey(current.getDataFieldName())) {
						theMetaData.deleteMetaData(current);
					} else {
						duplicateAllowed.put(current.getDataFieldName(),
								current);
					}
				}
			}
			try {
				theMetaData.setCMSSystem(cmsSystem);
			} catch (Exception ex) {

			}
			theMetaData.reSort();
		}
		for (String key : permanentFields.keySet()) {
			try {
				setPropertyValue(key, permanentFields.get(key));
			} catch (Exception ex) {
			}
		}
	}

}
