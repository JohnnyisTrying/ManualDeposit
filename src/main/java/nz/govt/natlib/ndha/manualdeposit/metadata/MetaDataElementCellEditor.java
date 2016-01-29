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

import java.awt.Component;
import java.awt.Font;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import nz.govt.natlib.ndha.common.guiutilities.DecimalNumberField;
import nz.govt.natlib.ndha.common.guiutilities.MaxLengthTextField;
import nz.govt.natlib.ndha.common.guiutilities.WholeNumberField;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.JXDatePicker;

public class MetaDataElementCellEditor extends AbstractCellEditor implements
		TableCellEditor {
	private static final long serialVersionUID = -2101195499757478804L;
	private final static Log LOG = LogFactory
			.getLog(MetaDataElementCellEditor.class);
	// These are the components that will handle the editing of the cell value
	private JComponent component;
	private final JComponent componentText = new MaxLengthTextField("", 100);
	private final JComponent componentCheck = new JCheckBox();
	private final JComponent componentCombo = new JComboBox();
	private final JComponent componentWholeNumber = new WholeNumberField(0, 1);
	private final JComponent componentDecimal = new DecimalNumberField(0, 1, 5);

	// BillR 23/06/2010 - If version 0.9.0 of swingx is used then JXDatePicker
	// takes a long (millisecond), if version 1.6 is used then JXDatePicker
	// takes a Date object.
	// private final JXDatePicker cal = new
	// JXDatePicker(System.currentTimeMillis());
	private final JXDatePicker cal = new JXDatePicker(new Date(System
			.currentTimeMillis()));

	private final JComponent componentDatePicker = cal;
	private static final String DATE_FORMAT = "dd/MM/yyyy";
	private Font theStandardFont;

	public MetaDataElementCellEditor(final Font standardFont) {
		super();
		theStandardFont = standardFont;
	}

	private Date parseDate(final String dateAsString) {
		if (dateAsString != null && !dateAsString.equals("")) {
			final SimpleDateFormat f = new SimpleDateFormat(DATE_FORMAT,
					Locale.ENGLISH);
			try {
				return f.parse(dateAsString);
			} catch (java.text.ParseException pe) {
				LOG.error("Error: Wrong date format.", pe);
			}
		}
		return new Date();
	}

	public void setFont(final Font standardFont) {
		theStandardFont = standardFont;
	}

	// This method is called when a cell value is edited by the user.
	public Component getTableCellEditorComponent(final JTable table,
			final Object value, final boolean isSelected, final int rowIndex,
			final int vColIndex) {
		// 'value' is value contained in the cell located at (rowIndex,
		// vColIndex)

		final MetaDataTableModel model = (MetaDataTableModel) table.getModel();
		final IMetaDataTypeExtended type = model.getRow(rowIndex);
		final int maximumLength = type.getMaximumLength();
		switch (type.getDataType()) {
		case Date:
			cal.setFormats(DATE_FORMAT);
			component = componentDatePicker;
			((JXDatePicker) component).setDate(parseDate((String) value));
			break;
		case MultiSelect:
			component = componentCombo;
			final JComboBox box = (JComboBox) component;
			box.removeAllItems();
			box.addItem(new MetaDataListValues("", "", 0));
			int itemNo = 1;
			for (MetaDataListValues listValue : type.getListItems()) {
				box.addItem(listValue);
				if (listValue.getValue().equals(value)) {
					box.setSelectedIndex(itemNo);
				}
				itemNo++;
			}
			((JComboBox) component).setSelectedItem((String) value);
			break;
		case Boolean:
			component = componentCheck;
			final boolean boolValue = Boolean.parseBoolean((String) value);
			((JCheckBox) component).setSelected(boolValue);
			break;
		case Integer:
			component = componentWholeNumber;
			((WholeNumberField) component).setText((String) value);
			break;
		case RealNumber:
			component = componentDecimal;
			((DecimalNumberField) component).setText((String) value);
			break;
		case Text:
		default:
			component = componentText;
			((MaxLengthTextField) component).setMaximumLength(maximumLength);
			if (value == null) {
				((MaxLengthTextField) component).setText("");
			} else {
				((MaxLengthTextField) component).setText((String) value);
			}
		}
		if (theStandardFont != null) {
			component.setFont(theStandardFont);
		}
		return component;
	}

	// This method is called when editing is completed.
	// It must return the new value to be stored in the cell.
	public Object getCellEditorValue() {
		if (component.equals(componentCheck)) {
			final JCheckBox check = (JCheckBox) component;
			if (check.getSelectedObjects() == null) {
				return "false";
			} else {
				return "true";
			}
		} else if (component.equals(componentCombo)) {
			return ((JComboBox) component).getSelectedItem();
		} else if (component.equals(componentDatePicker)) {
			final JXDatePicker date = (JXDatePicker) component;
			final Format formatter = new SimpleDateFormat(DATE_FORMAT,
					Locale.ENGLISH);
			if (date.getDate() == null) {
				return "";
			} else {
				return formatter.format(date.getDate());
			}
		} else if (component.equals(componentWholeNumber)) {
			return ((WholeNumberField) component).getText();
		} else if (component.equals(componentDecimal)) {
			return ((DecimalNumberField) component).getText();
		} else {
			return ((JTextField) component).getText();
		}
	}
}
