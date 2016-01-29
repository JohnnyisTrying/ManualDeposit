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
package nz.govt.natlib.ndha.manualdeposit.bulkupload;

import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class BulkUploadTableModel extends DefaultTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6836674917556404711L;

	public static final int ENTITY_NAME = 0;
	public static final int CMS_ID = 1;
	public static final int CMS_SYSTEM = 2;
	public static final int NO_OF_FILES = 3;
	public static final int ENTITY_STATUS = 4;

	protected String[] columnNames = { "Item Name", "CMS ID", "CMS System",
			"No of files", "Status" };
	protected Vector<BulkUploadItem> dataVector;

	public BulkUploadTableModel() {
		dataVector = new Vector<BulkUploadItem>();
	}

	public String getColumnName(int column) {
		return columnNames[column];
	}

	public boolean isCellEditable(int row, int column) {
		boolean isEditable = false;
		return isEditable;
	}

	@SuppressWarnings("unchecked")
	public Class getColumnClass(int column) {
		return String.class;
	}

	public Object getValueAt(int row, int column) {
		Object retVal = new Object();
		if (dataVector != null) {
			BulkUploadItem record = dataVector.get(row);
			switch (column) {
			case ENTITY_NAME:
				retVal = record.getEntityName();
				break;
			case CMS_ID:
				retVal = record.getCMSID();
				break;
			case CMS_SYSTEM:
				retVal = record.getSearchTypeLabel();
				break;
			case NO_OF_FILES:
				retVal = record.getEntity().getAllFiles().size();
				break;
			case ENTITY_STATUS:
				retVal = record.getJobStatus();
				break;
			default:
				break;
			}
		}
		return retVal;
	}

	public void setValueAt(Object value, int row, int column) {
		// Non-editable - do nothing
	}

	public int getRowCount() {
		if (dataVector == null) {
			return 0;
		} else {
			return dataVector.size();
		}
	}

	public int getSelectedErrorCount(JTable table) {
		int noOfSelectedRowsInError = 0;
		int[] selectedRows = table.getSelectedRows();
		for (int index : selectedRows) {
			BulkUploadItem item = dataVector.elementAt(index);
			if (item.getJobState().isError()) {
				noOfSelectedRowsInError++;
			}
		}
		return noOfSelectedRowsInError;
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public void addRow(BulkUploadItem job) {
		dataVector.add(job);
		fireTableRowsInserted(dataVector.size() - 1, dataVector.size() - 1);
	}

	public BulkUploadItem getRow(int rowNum) {
		if (rowNum < dataVector.size()) {
			return dataVector.get(rowNum);
		} else {
			return null;
		}
	}

	public void insertJob(BulkUploadItem jobToInsert,
			BulkUploadItem jobToInsertBeforeOrAfter, boolean before) {
		Vector<BulkUploadItem> dataVectorTemp = new Vector<BulkUploadItem>();
		boolean inserted = false;
		for (BulkUploadItem job : dataVector) {
			if ((job.equals(jobToInsertBeforeOrAfter)) && (before)) {
				dataVectorTemp.add(jobToInsert);
				inserted = true;
			}
			dataVectorTemp.add(job);
			if ((job.equals(jobToInsertBeforeOrAfter)) && (!before)) {
				dataVectorTemp.add(jobToInsert);
				inserted = true;
			}
		}
		if (!inserted) {
			dataVectorTemp.add(jobToInsert);
		}
		fireTableRowsInserted(dataVector.size() - 1, dataVector.size() - 1);
		dataVector = dataVectorTemp;
	}

	public void removeJob(BulkUploadItem job) {
		dataVector.remove(job);
	}

	public void clearTable() {
		dataVector.clear();
	}
}
