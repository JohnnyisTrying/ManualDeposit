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

package nz.govt.natlib.ndha.manualdeposit.jobmanagement;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

public class JobQueueTableModel extends DefaultTableModel {
	private static final long serialVersionUID = 385804932891575035L;
	public static final int ENTITY_NAME = 0;
	public static final int ENTITY_STATUS = 1;

	protected String[] theColumnNames;
	protected Vector<UploadJob> dataVector;

	public JobQueueTableModel(String[] columnNames) {
		theColumnNames = new String[columnNames.length];
		for (int i = 0; i < columnNames.length; i++) {
			theColumnNames[i] = columnNames[i];
		}
		dataVector = new Vector<UploadJob>();
	}

	public String getColumnName(int column) {
		return theColumnNames[column];
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
			UploadJob record = dataVector.get(row);
			switch (column) {
			case ENTITY_NAME:
				retVal = record.getBatchName();
				break;
			case ENTITY_STATUS:
				retVal = record.getStatus();
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

	public int getColumnCount() {
		return theColumnNames.length;
	}

	public void addRow(UploadJob job) {
		dataVector.add(job);
		fireTableRowsInserted(dataVector.size() - 1, dataVector.size() - 1);
	}

	public UploadJob getRow(int rowNum) {
		if (rowNum < dataVector.size()) {
			return dataVector.get(rowNum);
		} else {
			return null;
		}
	}

	public void insertJob(UploadJob jobToInsert,
			UploadJob jobToInsertBeforeOrAfter, boolean before) {
		Vector<UploadJob> dataVectorTemp = new Vector<UploadJob>();
		boolean inserted = false;
		for (UploadJob job : dataVector) {
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

	public void removeJob(UploadJob job) {
		dataVector.remove(job);
	}

	public void clearTable() {
		dataVector.clear();
	}
}
