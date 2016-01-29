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

package nz.govt.natlib.ndha.manualdeposit.customui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import nz.govt.natlib.ndha.manualdeposit.bulkupload.BulkUploadItem;
import nz.govt.natlib.ndha.manualdeposit.bulkupload.BulkUploadTableModel;
import nz.govt.natlib.ndha.manualdeposit.jobmanagement.JobQueueTableModel;
import nz.govt.natlib.ndha.manualdeposit.jobmanagement.UploadJob;
import nz.govt.natlib.ndha.manualdeposit.metadata.IMetaDataTypeExtended;
import nz.govt.natlib.ndha.manualdeposit.metadata.MetaDataTableModel;

public class TableRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 4883789964799441289L;

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex) {
		DefaultTableCellRenderer renderer = (DefaultTableCellRenderer) super
				.getTableCellRendererComponent(table, value, isSelected,
						hasFocus, rowIndex, vColIndex);

		String theText = "";
		String toolTipText = "";
		if (value != null) {
			theText = value.toString();
		}
		if (table.getModel() instanceof MetaDataTableModel) {
			MetaDataTableModel model = (MetaDataTableModel) table.getModel();
			IMetaDataTypeExtended metaData = model.getRow(rowIndex);
			if (!table.isCellEditable(rowIndex, vColIndex)) {
				renderer.setForeground(Color.black);
				renderer.setBackground(Color.lightGray);
			} else if ((metaData != null) && (!metaData.getWillBeUploaded())
					&& (vColIndex == 1)) {
				renderer.setForeground(Color.black);
				renderer.setBackground(new Color(255, 255, 200));
			} else {
				renderer.setForeground(Color.black);
				if (isSelected) {
					renderer.setBackground(new Color(230, 230, 230));
				} else {
					renderer.setBackground(Color.white);
				}
			}
			if (metaData != null) {
				if (metaData.getIsCompulsory() && (vColIndex == 0)) {
					theText += "*";
				}
			}
		} else if (table.getModel() instanceof JobQueueTableModel) {
			JobQueueTableModel model = (JobQueueTableModel) table.getModel();
			Object row = model.getRow(rowIndex);
			if (row instanceof UploadJob) {
				UploadJob job = (UploadJob) row;
				if (job.getJobDetail().size() > 1) {
					StringBuilder toolTip = new StringBuilder();
					for (UploadJob.JobDetail detail : job.getJobDetail()) {
						if (!toolTip.toString().equals("")) {
							toolTip.append(", ");
						}
						toolTip.append(detail.getEntityName());
					}
					toolTipText = toolTip.toString();
				}
				if (job.getJobState().isFailure()) {
					if (isSelected) {
						renderer.setForeground(Color.red);
						renderer.setBackground(Color.white);
					} else {
						renderer.setForeground(Color.white);
						renderer.setBackground(Color.red);
					}
				} else {
					renderer.setForeground(Color.black);
					renderer.setBackground(Color.white);
				}
			}
		} else if (table.getModel() instanceof BulkUploadTableModel) {
			BulkUploadTableModel model = (BulkUploadTableModel) table
					.getModel();
			Object row = model.getRow(rowIndex);
			if (row instanceof BulkUploadItem) {
				BulkUploadItem job = (BulkUploadItem) row;
				if (job.getJobState().isError()) {
					if (isSelected) {
						renderer.setForeground(Color.white);
						renderer.setBackground(Color.red);
					} else {
						renderer.setForeground(Color.red);
						renderer.setBackground(Color.white);
					}
				} else if (job.getJobState().isComplete()) {
					if (isSelected) {
						renderer.setForeground(Color.white);
						renderer.setBackground(new Color(50, 86, 150));
					} else {
						renderer.setForeground(Color.blue);
						renderer.setBackground(Color.white);
					}
				} else {
					if (isSelected) {
						renderer.setForeground(Color.white);
						renderer.setBackground(new Color(10, 36, 106));
					} else {
						renderer.setForeground(Color.black);
						renderer.setBackground(Color.white);
					}
				}
			}
		}
		super.setText(theText);
		if (toolTipText.equals("")) {
			super.setToolTipText(theText);
		} else {
			super.setToolTipText(toolTipText);
		}
		return renderer;
	}

	// The following methods override the defaults for performance reasons
	public void validate() {
	}

	public void revalidate() {
	}

	protected void firePropertyChange(String propertyName, Object oldValue,
			Object newValue) {
	}

	public void firePropertyChange(String propertyName, boolean oldValue,
			boolean newValue) {
	}
}
