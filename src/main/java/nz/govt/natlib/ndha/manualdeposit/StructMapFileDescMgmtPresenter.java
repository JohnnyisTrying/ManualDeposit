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
 * or the file "LICENSE.text" included with the software.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 */

package nz.govt.natlib.ndha.manualdeposit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import nz.govt.natlib.ndha.common.mets.FileType;
import nz.govt.natlib.ndha.common.mets.FileTypes;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

final class StructMapFileDescMgmtPresenter {

	private final static Log LOG = LogFactory
			.getLog(StructMapFileDescMgmtPresenter.class);
	private final JList jlstDescription;
	
	
	// Layer 1
	private final JTextField textfldDescription;
	private final JTextField textfldFilePrefix;
	private final JCheckBox checkAllowMultiples;
	private final JCheckBox checkMandatory;
	private final JComboBox cmboPosition;
	private final JCheckBox checkExtraLayers;
	private final JTextField textfldDescriptionMain;
	
	// Extra Layers
	private final HashMap<String, JComponent> extraLayers;
	
	// Layer 2
//	private final JTextField textfldDescriptionL2;
//	private final JTextField textfldFilePrefixL2;
//	private final JCheckBox checkAllowMultiplesL2;
	
	// Layer 3
//	private final JTextField textfldDescriptionL3;
//	private final JTextField textfldFilePrefixL3;
//	private final JCheckBox checkAllowMultiplesL3;
	
	// Layer 4
//	private final JTextField textfldDescriptionL4;
//	private final JTextField textfldFilePrefixL4;
//	private final JCheckBox checkAllowMultiplesL4;
	
	private final JButton bttnAddNew;
	private final JButton bttnDelete;
	private final JButton bttnSave;
	private final JButton bttnCancel;
	private final JButton bttnClose;
	private final JButton bttnMoveUp;
	private final JButton bttnMoveDown;
	private final JButton bttnGenMainDesc;

	private boolean isDirtyForm;
	private int newItemCount = 0;
	private static final String NEW_ITEM_NAME = "New Item";

	private FileTypes fileTypes;
	private final String theXmlFileName;
	private final IStructMapFileDescManagement theStructForm;
	private boolean isSystemChange = false;

	public static StructMapFileDescMgmtPresenter create(
			final JList lstDescription, final JTextField textfldDescription,
			final JTextField textfldFilePrefix,
			final JCheckBox checkAllowMultiples, final JCheckBox checkMandatory,
			final JComboBox cmbPosition, final JTextField textfldDescriptionMain, final JCheckBox checkExtraLayers, 
			final JTextField textfldDescriptionL2, final JTextField textfldFilePrefixL2, final JCheckBox checkAllowMultiplesL2, 
			final JTextField textfldDescriptionL3, final JTextField textfldFilePrefixL3, final JCheckBox checkAllowMultiplesL3, 
			final JTextField textfldDescriptionL4, final JTextField textfldFilePrefixL4, final JCheckBox checkAllowMultiplesL4, 
			final JButton btnAddNew,
			final JButton btnDelete, final JButton btnSave,
			final JButton btnCancel, final JButton btnClose,
			final JButton btnMoveUp, final JButton btnMoveDown, final JButton btnGenMainDesc,
			final String xmlFileName, final IStructMapFileDescManagement theForm) {
		return new StructMapFileDescMgmtPresenter(lstDescription,
				textfldDescription, textfldFilePrefix, checkAllowMultiples,
				checkMandatory, cmbPosition, textfldDescriptionMain, checkExtraLayers, 
				textfldDescriptionL2, textfldFilePrefixL2, checkAllowMultiplesL2, textfldDescriptionL3, 
				textfldFilePrefixL3, checkAllowMultiplesL3, textfldDescriptionL4, textfldFilePrefixL4, 
				checkAllowMultiplesL4, btnAddNew, btnDelete, btnSave,
				btnCancel, btnClose, btnMoveUp, btnMoveDown, btnGenMainDesc, xmlFileName,
				theForm);
	}

	StructMapFileDescMgmtPresenter(final JList lstDescription,
			final JTextField textfldDescription,
			final JTextField textfldFilePrefix,
			final JCheckBox checkAllowMultiples, final JCheckBox checkMandatory,
			final JComboBox cmbPosition, final JTextField textfldDescriptionMain, final JCheckBox checkExtraLayers, 
			final JTextField textfldDescriptionL2, final JTextField textfldFilePrefixL2, final JCheckBox checkAllowMultiplesL2, 
			final JTextField textfldDescriptionL3, final JTextField textfldFilePrefixL3, final JCheckBox checkAllowMultiplesL3, 
			final JTextField textfldDescriptionL4, final JTextField textfldFilePrefixL4, final JCheckBox checkAllowMultiplesL4, 
			final JButton btnAddNew,
			final JButton btnDelete, final JButton btnSave,
			final JButton btnCancel, final JButton btnClose,
			final JButton btnMoveUp, final JButton btnMoveDown, final JButton btnGenMainDesc,
			final String xmlFileName, final IStructMapFileDescManagement theForm) {
		jlstDescription = lstDescription;
		this.textfldDescription = textfldDescription;
		this.textfldFilePrefix = textfldFilePrefix;
		this.checkAllowMultiples = checkAllowMultiples;
		this.checkMandatory = checkMandatory;
		cmboPosition = cmbPosition;
		this.checkExtraLayers = checkExtraLayers;
		this.textfldDescriptionMain = textfldDescriptionMain;
		
		// Extra Layers
		extraLayers = new HashMap<String, JComponent>();
		extraLayers.put(textfldDescriptionL2.getName(), textfldDescriptionL2);
		extraLayers.put(textfldFilePrefixL2.getName(), textfldFilePrefixL2);
		extraLayers.put(checkAllowMultiplesL2.getName(), checkAllowMultiplesL2);
		extraLayers.put(textfldDescriptionL3.getName(), textfldDescriptionL3);
		extraLayers.put(textfldFilePrefixL3.getName(), textfldFilePrefixL3);
		extraLayers.put(checkAllowMultiplesL3.getName(), checkAllowMultiplesL3);
		extraLayers.put(textfldDescriptionL4.getName(), textfldDescriptionL4);
		extraLayers.put(textfldFilePrefixL4.getName(), textfldFilePrefixL4);
		extraLayers.put(checkAllowMultiplesL4.getName(), checkAllowMultiplesL4);
		
		
		bttnAddNew = btnAddNew;
		bttnDelete = btnDelete;
		bttnSave = btnSave;
		bttnCancel = btnCancel;
		bttnClose = btnClose;
		bttnMoveUp = btnMoveUp;
		bttnMoveDown = btnMoveDown;
		bttnGenMainDesc = btnGenMainDesc;
		theXmlFileName = xmlFileName;
		theStructForm = theForm;
		loadData();

		// Restrict the values of this combo box to that of possible "Position"
		// enums
		cmboPosition.setModel(new DefaultComboBoxModel(FileType.Position
				.values()));
		// Restrict the contents of the description and file prefix to not allow
		// special characters
		textfldDescription.setDocument(new SpecialCharFilterDocument());
		textfldFilePrefix.setDocument(new SpecialCharFilterDocument());

		loadListData();
		addEventHandlers();
		checkButtons();
	}

	private void loadData() {
		loadData(0);
	}

	private void loadData(final int selectedindex) {
		try {
			fileTypes = FileTypes.create(theXmlFileName);
		} catch (Exception ex) {
			theStructForm.showMessage(
					"Error loading structure map file descriptions", ex
							.getMessage());
		}
		loadListData(selectedindex);
		setDirty(false);
	}

	private void loadListData() {
		loadListData(0);
	}

	private void loadListData(final int selectedItem) {
		// Jlist doesn't accept ArrayList as a parameter so I can't pass it
		jlstDescription.setListData(new Vector<FileType>(fileTypes.getList())); // NOPMD
		jlstDescription.setSelectedIndex(selectedItem);
		showComponentValues(selectedItem);
	}

	public void saveIfRequired() {
		if ((isDirtyForm)
				&& (theStructForm
						.confirm("Data has changed. Do you want to save them permanently before closing this window?"))) {
			saveFileTypes();
		}
	}

	private void addEventHandlers() {
		
		ActionListener actionDataChangedListener = new ActionListener() {
			public void actionPerformed(final ActionEvent evt) {
				dataChanged();
			}
		};
		
		CaretListener caretDataChangedListener = new CaretListener() {
			public void caretUpdate(final CaretEvent evt) {
				dataChanged();
			}
		};
		
		ItemListener itemDataChangedListener = new ItemListener() {
			public void itemStateChanged(final ItemEvent evt) {
				dataChanged();
			}
		};		
		
		jlstDescription.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(final ListSelectionEvent evt) {
				descriptionListValueChanged(evt);
			}
		});
		bttnMoveUp.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent evt) {
				moveItem(true);
			}
		});
		bttnMoveDown.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent evt) {
				moveItem(false);
			}
		});
		bttnAddNew.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent evt) {
				addItem();
			}
		});
		bttnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent evt) {
				deleteItem();
			}
		});
		bttnSave.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent evt) {
				saveFileTypes();
			}
		});
		bttnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent evt) {
				final int index = jlstDescription.getSelectedIndex();
				loadData(index);
			}
		});
		bttnClose.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent evt) {
				saveIfRequired();
				theStructForm.closeForm();
			}
		});
		bttnGenMainDesc.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent evt) {
				generateMainDescription();
			}
		});
		
		textfldDescription.addCaretListener(caretDataChangedListener);
		textfldDescriptionMain.addCaretListener(caretDataChangedListener);
		textfldFilePrefix.addCaretListener(caretDataChangedListener);
		checkAllowMultiples.addActionListener(actionDataChangedListener);
		checkExtraLayers.addActionListener(actionDataChangedListener);
		checkMandatory.addActionListener(actionDataChangedListener);		
		cmboPosition.addItemListener(itemDataChangedListener);
		
		
		for(Entry<String, JComponent> element : extraLayers.entrySet()){
			// If name starts with "Allow" then it is a checkbox
			if(element.getKey().startsWith("Allow")){
				JCheckBox field = (JCheckBox) element.getValue();
				field.addActionListener(actionDataChangedListener);
			}
			else{
				JTextField field = (JTextField) element.getValue();
				field.addCaretListener(caretDataChangedListener);
			}
		}
		
	}

	// Generate Main Description text from extra layer descriptions if they exist
	private void generateMainDescription() {
		String mainDescription = textfldDescription.getText();
		JTextField layerDescription = (JTextField) extraLayers.get("DescriptionL2");
		if(!layerDescription.getText().equals("")){
			mainDescription = mainDescription + " -> " + layerDescription.getText();
			layerDescription = (JTextField) extraLayers.get("DescriptionL3");
			if(!layerDescription.getText().equals("")){
				mainDescription = mainDescription + " -> " + layerDescription.getText();
				layerDescription = (JTextField) extraLayers.get("DescriptionL4");
				if(!layerDescription.getText().equals("")){
					mainDescription = mainDescription + " -> " + layerDescription.getText();
				}
			}
		}	
		textfldDescriptionMain.setText(mainDescription);
	}

	public boolean isDirty() {
		return isDirtyForm;
	}

	private void setDirty(final boolean flag) {
		isDirtyForm = flag;
		checkButtons();
	}

	private void saveFileTypes() {
		try {
			fileTypes.storeAsXML(theXmlFileName);
		} catch (Exception ex) {
			theStructForm.showMessage("Error saving structure map file descriptions", ex.getMessage());
			LOG.error("Error storing file types as XML", ex);
			return;
		}
		setDirty(false);
	}

	private void descriptionListValueChanged(final ListSelectionEvent evt) {
		LOG.debug("descriptionListValueChanged");
		showComponentValues(((JList) evt.getSource()).getSelectedIndex());
	}

	private void moveItem(final boolean moveItUp) {
		int index = jlstDescription.getSelectedIndex();
		final FileType type = fileTypes.get(index);
		int currentSortOrder = type.getSortOrder();
		if (moveItUp) {
			index--;
			currentSortOrder--;
		} else {
			index++;
			currentSortOrder++;
		}
		fileTypes.reorder(type, currentSortOrder);
		setDirty(true);
		loadListData(index);
	}

	private void showComponentValues(final int index) {
		LOG.debug("showComponentValues at index " + index);

		/*
		 * When you add a new item, this event gets fired with -1 as the index.
		 * So don't do anything.
		 */
		if (index < 0)
			return;

		final FileType type = fileTypes.get(index);
		isSystemChange = true;
		textfldDescription.setText(type.getDescription());
		textfldDescriptionMain.setText(type.getMainDescription());
		textfldFilePrefix.setText(type.getFilePrefix());
		checkMandatory.setSelected(type.isMandatory());
		checkAllowMultiples.setSelected(type.isAllowMultiples());
		cmboPosition.setSelectedItem(type.getPosition());
		checkExtraLayers.setSelected(type.chkExtraLayers());
		setExtraLayerValues(type.getExtraLayers());
		isSystemChange = false;
		checkButtons();
	}

	// Find matches for extra layers attributes and update values
	private void setExtraLayerValues(HashMap<String, String> extraLayers){
		for(Entry<String, JComponent> attr : this.extraLayers.entrySet()){
			String value = extraLayers.get(attr.getKey());
			// If JComponent is a checkbox
			if(attr.getKey().startsWith("Allow")){
				JCheckBox field = (JCheckBox) attr.getValue();
				if(value == null)
					field.setSelected(false);
				else
					field.setSelected(Boolean.parseBoolean(value));
			}
			// Else it is a text field
			else{
				JTextField field = (JTextField) attr.getValue();
				if(value == null)
					field.setText("");
				else
					field.setText(value);
			}
		}
	}

	private void checkButtons() {
		bttnSave.setEnabled(isDirtyForm);
		bttnCancel.setEnabled(isDirtyForm);
		bttnClose.setEnabled(!isDirtyForm);
		final int noOfItems = jlstDescription.getModel().getSize();
		final int selectedItem = jlstDescription.getSelectedIndex();
		bttnDelete.setEnabled(selectedItem > -1);
		bttnMoveDown.setEnabled(selectedItem < noOfItems - 1);
		bttnMoveUp.setEnabled((noOfItems > 0) && (selectedItem > 0));
	}

	/**
	 * A class to prevent special characters to be entered into a text field.
	 */
	private static class SpecialCharFilterDocument extends PlainDocument {
		private static final long serialVersionUID = -214402086953909772L;
		private final static String SPECIAL_CHARS = "[\\\\/:*?\"\\[<>\\]|~`'!]";
		private final Pattern pattern = Pattern.compile(SPECIAL_CHARS);

		@Override
		public void insertString(final int offs, final String str,
				final AttributeSet a) throws BadLocationException {
			// Only insert the text if it matches the regular expression
			if (str != null && !pattern.matcher(str).matches()) {
				super.insertString(offs, str, a);
			}
		}
	}

	private void dataChanged() {
		if (!isSystemChange) {
			final FileType type = fileTypes.get(jlstDescription
					.getSelectedIndex());
			if (valuesChanged(type)) {
				type.setAllowMultiples(checkAllowMultiples.isSelected());
				type.setChkExtraLayers(checkExtraLayers.isSelected());
				type.setDescription(textfldDescription.getText());
				type.setMainDescription(textfldDescriptionMain.getText());
				type.setFilePrefix(textfldFilePrefix.getText());
				type.setMandatory(checkMandatory.isSelected());
				type.setPosition((FileType.Position) cmboPosition.getSelectedItem());
				if(checkExtraLayers.isSelected()){
					type.setExtraLayers(convertExtraLayersToStrings());
				}
				setDirty(true);
			}
		}
	}
	
	// Helper method to convert extraLayers HashMap<String, JComponent> to a HashMap<String, String> 
	private HashMap<String, String> convertExtraLayersToStrings(){
		HashMap<String, String> extraLayersToStrings = new HashMap<String, String>();
		for(Entry<String, JComponent> attr : extraLayers.entrySet()){
			// If JComponent is a checkbox
			if(attr.getKey().startsWith("Allow")){
				JCheckBox field = (JCheckBox) attr.getValue();
				extraLayersToStrings.put(attr.getKey(), String.valueOf(field.isSelected()));
			}
			// Else it is a text field
			else{
				JTextField field = (JTextField) attr.getValue();
				extraLayersToStrings.put(attr.getKey(), field.getText());
			}
		}
		return extraLayersToStrings;
	}

	private void addItem() {
		final FileType newType = new FileType(NEW_ITEM_NAME + " "
				+ newItemCount, // _description
				"", // _mainDescription
				"", // _filePrefix
				false, // _mandatory
				true, // _allowMultiples
				0, // _sortOrder
				FileType.Position.Beginning, // _position
				false //_extraLayers
		);
		fileTypes.add(newType);
		// Jlist doesn't accept ArrayList as a parameter so I can't pass it
		jlstDescription.setListData(new Vector<FileType>(fileTypes.getList())); // NOPMD
		jlstDescription.setSelectedValue(newType, true);
		newItemCount++;
		setDirty(true);
		checkButtons();
	}

	private void deleteItem() {
		if (jlstDescription.isSelectionEmpty()) {
			return;
		}
		final int currentIndex = jlstDescription.getSelectedIndex();
		final FileType type = fileTypes.get(currentIndex);
		fileTypes.remove(type);
		// Jlist doesn't accept ArrayList as a parameter so I can't pass it
		jlstDescription.setListData(new Vector<FileType>(fileTypes.getList())); // NOPMD

		int newIndex;
		if (currentIndex >= fileTypes.size()) {
			newIndex = currentIndex - 1;
		} else {
			newIndex = currentIndex;
		}
		jlstDescription.setSelectedIndex(newIndex);
		setDirty(true);
		checkButtons();
	}

	private boolean valuesChanged(final FileType type) {
		if (!type.getDescription().equals(textfldDescription.getText()))
			return true;
		if (!type.getMainDescription().equals(textfldDescriptionMain.getText()))
			return true;
		if (!type.getFilePrefix().equals(textfldFilePrefix.getText()))
			return true;
		if (type.isMandatory() != checkMandatory.isSelected())
			return true;
		if (type.isAllowMultiples() != checkAllowMultiples.isSelected())
			return true;
		if (type.chkExtraLayers() != checkExtraLayers.isSelected())
			return true;
		if (type.getPosition() != (FileType.Position) cmboPosition.getSelectedItem())
			return true;
		if(checkExtraLayers.isSelected()){
			if (valuesChangedExtraLayers(type.getExtraLayers()))
				return true;
		}		
		return false;
	}
	
	// Find values not matching for extra layers attributes
	private boolean valuesChangedExtraLayers(HashMap<String, String> extraLayers){
		for(Entry<String, JComponent> attr : this.extraLayers.entrySet()){
			String value = extraLayers.get(attr.getKey());
			// If JComponent is a checkbox
			if(attr.getKey().startsWith("Allow")){
				JCheckBox field = (JCheckBox) attr.getValue();
				if(value == null){
					if(field.isSelected())
						return true;
				}
				else{
					if(Boolean.parseBoolean(value) != field.isSelected())
						return true;
				}
			}
			// Else it is a text field
			else{
				JTextField field = (JTextField) attr.getValue();
				if(value == null){
					if(!field.getText().equals(""))
						return true;
				}
				else{
					if(!value.equals(field.getText()))
						return true;
				}
			}
		}
		return false;
	}

}
